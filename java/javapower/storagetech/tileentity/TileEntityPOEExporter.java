package javapower.storagetech.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.node.NetworkNodePOEExporter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityPOEExporter extends NetworkNodeTile<NetworkNodePOEExporter>
{
	@ObjectHolder(StorageTech.MODID+":poeexporter")
	public static final TileEntityType<TileEntityPOEExporter> CURRENT_TILE = null;
	
	public static final TileDataParameter<Boolean, TileEntityPOEExporter> ENERGY_RESTRICTION = new TileDataParameter<Boolean, TileEntityPOEExporter>(DataSerializers.BOOLEAN, false, t -> t.getNode().getEnergyRestrictionMode() ,(t,v) -> t.getNode().setEnergyRestrictionMode(v));
	public static final TileDataParameter<Integer, TileEntityPOEExporter> ENERGY_RESTRICTION_VALUE = new TileDataParameter<Integer, TileEntityPOEExporter>(DataSerializers.VARINT, 0, t -> t.getNode().getEnergyRestrictionValue() ,(t,v) -> t.getNode().setEnergyRestrictionMode(v));
	
	
	public TileEntityPOEExporter()
	{
		super(CURRENT_TILE);
		
		dataManager.addWatchedParameter(ENERGY_RESTRICTION);
		dataManager.addWatchedParameter(ENERGY_RESTRICTION_VALUE);
		
	}

	@Override
    @Nonnull
    public NetworkNodePOEExporter createNode(World world, BlockPos pos)
    {
        return new NetworkNodePOEExporter(world, pos);
    }

}
