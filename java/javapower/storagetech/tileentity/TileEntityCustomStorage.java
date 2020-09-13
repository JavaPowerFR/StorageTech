package javapower.storagetech.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IAccessType;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IPrioritizable;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.RSSerializers;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.node.NetworkNodeCustomStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityCustomStorage extends NetworkNodeTile<NetworkNodeCustomStorage>
{
	@ObjectHolder(StorageTech.MODID+":customstorageblock")
	public static final TileEntityType<TileEntityCustomStorage> CURRENT_TILE = null;
	
    public static final TileDataParameter<Integer, TileEntityCustomStorage> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer, TileEntityCustomStorage> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileEntityCustomStorage> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<AccessType, TileEntityCustomStorage> ACCESS_TYPE = IAccessType.createParameter();
    public static final TileDataParameter<Long, TileEntityCustomStorage> STORED = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getStorage() != null ? (long) t.getNode().getStorage().getStored() : 0);
    public static final TileDataParameter<Long, TileEntityCustomStorage> CAPACITY = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getCapacity());
    
    public TileEntityCustomStorage()
    {
        super(CURRENT_TILE);
        
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(CAPACITY);
        dataManager.addWatchedParameter(ACCESS_TYPE);
    }

    @Override
    @Nonnull
    public NetworkNodeCustomStorage createNode(World world, BlockPos pos)
    {
        return new NetworkNodeCustomStorage(world, pos);
    }

	public void fillItemStackDrop(ItemStack stack, boolean forDrop)
	{
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("capacity", (int)getNode().getCapacity());
		if(forDrop)
			nbt.putUniqueId(NetworkNodeCustomStorage.NBT_ID, getNode().getStorageId());
		stack.setTag(nbt);
	}
}