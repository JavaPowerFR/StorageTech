package javapower.storagetech.tileentity;

import java.util.Arrays;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.RSSerializers;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import com.refinedmods.refinedstorage.util.WorldUtils;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.node.NetworkNodePOEDrive;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityPOEDrive extends NetworkNodeTile<NetworkNodePOEDrive>
{
	@ObjectHolder(StorageTech.MODID+":poedrive")
	public static final TileEntityType<TileEntityPOEDrive> CURRENT_TILE = null;
	
	public static final TileDataParameter<Long, TileEntityPOEDrive> STORED = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getEnergyStored());
	public static final TileDataParameter<Long, TileEntityPOEDrive> CAPACITY = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getCapacity());
	public static final TileDataParameter<Long, TileEntityPOEDrive> IO_CAPACITY = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getIOCapacity());
    
	
	public static final ModelProperty<DiskState[]> DISK_STATE_PROPERTY = new ModelProperty<>();
	private final DiskState[] diskState = new DiskState[8];
    
    public long getEnergyStored()
    {
        return getNode().getEnergyStored();
    }
    
    public long getEnergyCapacity()
    {
    	return getNode().getCapacity();
    }
    public long getEnergyIOCapacity()
    {
    	return getNode().getIOCapacity();
    }
    
    public TileEntityPOEDrive()
    {
        super(CURRENT_TILE);
        
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(CAPACITY);
        dataManager.addWatchedParameter(IO_CAPACITY);
        
        Arrays.fill(diskState, DiskState.NONE);
    }

    @Override
    @Nonnull
    public NetworkNodePOEDrive createNode(World world, BlockPos pos)
    {
        return new NetworkNodePOEDrive(world, pos);
    }
    
    @Override
    public void readUpdate(CompoundNBT tag)
    {
    	super.readUpdate(tag);
    	
    	ListNBT list = tag.getList("diskstate", Constants.NBT.TAG_INT);

        for (int i = 0; i < list.size(); ++i) {
            diskState[i] = DiskState.values()[list.getInt(i)];
        }

        requestModelDataUpdate();

        WorldUtils.updateBlock(world, pos);
    }
    
    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag)
    {
    	super.writeUpdate(tag);
    	
    	ListNBT list = new ListNBT();
        for (DiskState state : getNode().getDiskState())
        {
            list.add(IntNBT.valueOf(state.ordinal()));
        }
        tag.put("diskstate", list);
    	return tag;
    }
    
    @Nonnull
    @Override
    public IModelData getModelData()
    {
        return new ModelDataMap.Builder().withInitial(DISK_STATE_PROPERTY, diskState).build();
    }
}