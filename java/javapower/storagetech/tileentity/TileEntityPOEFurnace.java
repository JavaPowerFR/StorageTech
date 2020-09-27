package javapower.storagetech.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.node.NetworkNodePOEFurnace;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityPOEFurnace extends NetworkNodeTile<NetworkNodePOEFurnace>
{
	@ObjectHolder(StorageTech.MODID+":poefurnace")
	public static final TileEntityType<TileEntityPOEFurnace> CURRENT_TILE = null;
	
	public static final TileDataParameter<Integer, TileEntityPOEFurnace> FURNACE1_PROCESSING = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getProcessing(0));
	public static final TileDataParameter<Integer, TileEntityPOEFurnace> FURNACE2_PROCESSING = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getProcessing(1));
	//public static final TileDataParameter<Integer, TileEntityPOEFurnace> FURNACE3_PROCESSING = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getProcessing(2));
	//public static final TileDataParameter<Integer, TileEntityPOEFurnace> FURNACE4_PROCESSING = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getProcessing(3));
	
	public static final TileDataParameter<Integer, TileEntityPOEFurnace> ENERGY_STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getEnergyBuffer().energy);
	public static final TileDataParameter<Integer, TileEntityPOEFurnace> ENERGY_CAPACITY = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getEnergyBuffer().capacity);
	
    public TileEntityPOEFurnace()
    {
        super(CURRENT_TILE);
        
        dataManager.addWatchedParameter(FURNACE1_PROCESSING);
        dataManager.addWatchedParameter(FURNACE2_PROCESSING);
        //dataManager.addWatchedParameter(FURNACE3_PROCESSING);
        //dataManager.addWatchedParameter(FURNACE4_PROCESSING);
        dataManager.addWatchedParameter(ENERGY_STORED);
        dataManager.addWatchedParameter(ENERGY_CAPACITY);
    }

    @Override
    @Nonnull
    public NetworkNodePOEFurnace createNode(World world, BlockPos pos)
    {
        return new NetworkNodePOEFurnace(world, pos);
    }
    
}