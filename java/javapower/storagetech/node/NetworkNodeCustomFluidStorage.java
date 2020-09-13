package javapower.storagetech.node;

import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
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
import com.refinedmods.refinedstorage.apiimpl.storage.cache.FluidStorageCache;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.tile.FluidStorageTile;
import com.refinedmods.refinedstorage.tile.config.IAccessType;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IPrioritizable;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.util.AccessTypeUtils;

import javapower.storagetech.block.BlockCustomFluidStorage;
import javapower.storagetech.core.StorageTech;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;

public class NetworkNodeCustomFluidStorage extends NetworkNode implements IStorageScreen, IStorageProvider, IComparable, IWhitelistBlacklist, IPrioritizable, IAccessType, IStorageDiskContainerContext
{
    private static final Logger LOGGER = LogManager.getLogger(NetworkNodeCustomFluidStorage.class);

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_FILTERS = "Filters";
    public static final String NBT_ID = "Id";

    private final FluidInventory filters = new FluidInventory(9).addListener(new NetworkNodeFluidInventoryListener(this));
    
    public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockCustomFluidStorage.raw_name);

    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;

    private UUID storageId = UUID.randomUUID();
    private IStorageDisk<FluidStack> storage;
    
    public int storageCapacity = 1000;

    public NetworkNodeCustomFluidStorage(World world, BlockPos pos)
    {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage()
    {
        return RS.SERVER_CONFIG.getFluidStorageBlock().getFourThousandNinetySixKUsage();
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause)
    {
        super.onConnectedStateChange(network, state, cause);

        LOGGER.debug("Connectivity state of fluid storage block at {} changed to {} due to {}", pos, state, cause);

        network.getNodeGraph().runActionWhenPossible(FluidStorageCache.INVALIDATE.apply(InvalidateCause.CONNECTED_STATE_CHANGED));
    }

    @Override
    public void addItemStorages(List<IStorage<ItemStack>> storages)
    {
        // NO OP
    }

    @Override
    public void addFluidStorages(List<IStorage<FluidStack>> storages)
    {
        if (storage == null)
        {
            loadStorage();
        }
        storages.add(storage);
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
        tag.putInt("capacity", storageCapacity);
        
        return tag;
    }

    @Override
    public void read(CompoundNBT tag)
    {
        super.read(tag);
        
        if(tag.contains("capacity"))
        	storageCapacity = tag.getInt("capacity");

        if (tag.hasUniqueId(NBT_ID)) {
            storageId = tag.getUniqueId(NBT_ID);

            loadStorage();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadStorage()
    {
        IStorageDisk disk = API.instance().getStorageDiskManager((ServerWorld) world).get(storageId);

        if (disk == null)
        {
            API.instance().getStorageDiskManager((ServerWorld) world).set(storageId, disk = API.instance().createDefaultFluidDisk((ServerWorld) world, getStorageCapacity()));
            API.instance().getStorageDiskManager((ServerWorld) world).markForSaving();
        }

        this.storage = new CustomFluidStorageWrapperStorageDisk(this, disk);
    }
    
    private int getStorageCapacity()
    {
		return storageCapacity;
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

    public IStorageDisk<FluidStack> getStorage()
    {
        return storage;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag)
    {
        super.writeConfiguration(tag);

        tag.put(NBT_FILTERS, filters.writeToNbt());
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

        if (tag.contains(NBT_FILTERS)) {
            filters.readFromNbt(tag.getCompound(NBT_FILTERS));
        }

        if (tag.contains(NBT_PRIORITY)) {
            priority = tag.getInt(NBT_PRIORITY);
        }

        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
        }

        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE);
        }

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

    public FluidInventory getFilters()
    {
        return filters;
    }

    @Override
    public ITextComponent getTitle()
    {
    	return new TranslationTextComponent("block.storagetech.customfluidstorageblock");
    }

    @Override
    public long getStored()
    {
        return FluidStorageTile.STORED.getValue();
    }

    @Override
    public long getCapacity()
    {
        return storageCapacity;
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
            network.getFluidStorageCache().invalidate(InvalidateCause.DEVICE_CONFIGURATION_CHANGED);

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

        if (network != null)
            network.getFluidStorageCache().sort();
    }
}