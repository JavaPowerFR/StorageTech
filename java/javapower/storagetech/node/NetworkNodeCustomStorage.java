package javapower.storagetech.node;

import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.IStorage;
import com.refinedmods.refinedstorage.api.storage.IStorageProvider;
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.IStorageScreen;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.ItemStorageCache;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.tile.StorageTile;
import com.refinedmods.refinedstorage.tile.config.IAccessType;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IPrioritizable;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.util.AccessTypeUtils;
import com.refinedmods.refinedstorage.util.StackUtils;

import javapower.storagetech.block.BlockCustomStorage;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.CustomStorageTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;

public class NetworkNodeCustomStorage extends NetworkNode implements IStorageScreen, IStorageProvider, IComparable, IWhitelistBlacklist, IPrioritizable, IAccessType, IStorageDiskContainerContext {

    private static final Logger LOGGER = LogManager.getLogger(NetworkNodeCustomStorage.class);

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    public static final String NBT_ID = "Id";

    private BaseItemHandler filters = new BaseItemHandler(9).addListener(new NetworkNodeInventoryListener(this));
    
    public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockCustomStorage.raw_name);

    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;
    
    private UUID storageId = UUID.randomUUID();
    private IStorageDisk<ItemStack> storage;
    
    private CustomStorageTile ten;

    public NetworkNodeCustomStorage(World world, BlockPos pos, CustomStorageTile _ten)
    {
        super(world, pos);
        ten = _ten;
    }

    @Override
    public int getEnergyUsage()
    {
    	return RS.SERVER_CONFIG.getStorageBlock().getSixtyFourKUsage();
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause)
    {
        super.onConnectedStateChange(network, state, cause);

        LOGGER.debug("Connectivity state of item storage block at {} changed to {} due to {}", pos, state, cause);

        network.getNodeGraph().runActionWhenPossible(ItemStorageCache.INVALIDATE.apply(InvalidateCause.CONNECTED_STATE_CHANGED));
    }

    @Override
    public void addItemStorages(List<IStorage<ItemStack>> storages)
    {
        if (storage == null)
            loadStorage();

        storages.add(storage);
    }

    @Override
    public void addFluidStorages(List<IStorage<FluidStack>> storages)
    {
        // NO OP
    }

    @Override
    public ResourceLocation getId()
    {
        return NETWORK_NODE_ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag)
    {
        super.write(tag);

        tag.putUniqueId(NBT_ID, storageId);

        return tag;
    }

    @Override
    public void read(CompoundNBT tag)
    {
        super.read(tag);

        if (tag.hasUniqueId(NBT_ID))
        {
            storageId = tag.getUniqueId(NBT_ID);

            loadStorage();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadStorage()
    {
		IStorageDisk disk = API.instance().getStorageDiskManager((ServerWorld) world).get(storageId);

        if (disk == null)
        {
        	INetworkNode node = API.instance().getNetworkNodeManager((ServerWorld)world).getNode(pos);
        	if(node instanceof NetworkNodeCustomStorage)
        	{
	            API.instance().getStorageDiskManager((ServerWorld) world).set(storageId, disk = API.instance().createDefaultItemDisk((ServerWorld) world, ((NetworkNodeCustomStorage)node).getStorageCapacity()));
	            API.instance().getStorageDiskManager((ServerWorld) world).markForSaving();
        	}
        }

        this.storage = new CustomItemStorageWrapperStorageDisk(this, disk);
    }

    private int getStorageCapacity()
    {
    	return ten.storageCapacity;
	}

	public void setStorageId(UUID id)
    {
        this.storageId = id;

        markDirty();
    }

    public UUID getStorageId()
    {
        return storageId;
    }

    public IStorageDisk<ItemStack> getStorage()
    {
        return storage;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag)
    {
        super.writeConfiguration(tag);

        StackUtils.writeItems(filters, 0, tag);

        tag.putInt(NBT_PRIORITY, priority);
        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);

        AccessTypeUtils.writeAccessType(tag, accessType);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag)
    {
        super.readConfiguration(tag);

        StackUtils.readItems(filters, 0, tag);

        if (tag.contains(NBT_PRIORITY))
            priority = tag.getInt(NBT_PRIORITY);

        if (tag.contains(NBT_COMPARE))
            compare = tag.getInt(NBT_COMPARE);

        if (tag.contains(NBT_MODE))
            mode = tag.getInt(NBT_MODE);

        accessType = AccessTypeUtils.readAccessType(tag);
    }

    @Override
    public int getCompare()
    {
        return compare;
    }

    @Override
    public void setCompare(int compare)
    {
        this.compare = compare;

        markDirty();
    }

    @Override
    public int getWhitelistBlacklistMode()
    {
        return mode;
    }

    @Override
    public void setWhitelistBlacklistMode(int mode)
    {
        this.mode = mode;

        markDirty();
    }

    public BaseItemHandler getFilters()
    {
        return filters;
    }

    @Override
    public ITextComponent getTitle()
    {
        return new TranslationTextComponent("block.storagetech.customstorageblock");
    }

    /*@Override
    public TileDataParameter<Integer, ?> getTypeParameter()
    {
        return null;
    }

    @Override
    public TileDataParameter<Integer, ?> getRedstoneModeParameter()
    {
        return StorageTile.REDSTONE_MODE;
    }

    @Override
    public TileDataParameter<Integer, ?> getCompareParameter()
    {
        return StorageTile.COMPARE;
    }

    @Override
    public TileDataParameter<Integer, ?> getWhitelistBlacklistParameter()
    {
        return StorageTile.WHITELIST_BLACKLIST;
    }

    @Override
    public TileDataParameter<Integer, ?> getPriorityParameter()
    {
        return StorageTile.PRIORITY;
    }

    @Override
    public TileDataParameter<AccessType, ?> getAccessTypeParameter()
    {
        return StorageTile.ACCESS_TYPE;
    }*/

    @Override
    public long getStored()
    {
        return StorageTile.STORED.getValue();
    }

    @Override
    public long getCapacity()
    {
        return getStorageCapacity();
    }

    @Override
    public AccessType getAccessType()
    {
        return accessType;
    }

    @Override
    public void setAccessType(AccessType value)
    {
        this.accessType = value;

        if (network != null)
        {
            network.getItemStorageCache().invalidate(InvalidateCause.DEVICE_CONFIGURATION_CHANGED);
        }

        markDirty();
    }

    @Override
    public int getPriority()
    {
        return priority;
    }

    @Override
    public void setPriority(int priority)
    {
        this.priority = priority;

        markDirty();

        if (network != null) {
            network.getItemStorageCache().sort();
        }
    }
}