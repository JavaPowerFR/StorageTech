package javapower.storagetech.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.node.NetworkNodePOEImporter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityPOEImporter extends NetworkNodeTile<NetworkNodePOEImporter>
{
	@ObjectHolder(StorageTech.MODID+":poeimporter")
	public static final TileEntityType<TileEntityPOEImporter> CURRENT_TILE = null;
	
	public static final TileDataParameter<Boolean, TileEntityPOEImporter> ENERGY_RESTRICTION = new TileDataParameter<Boolean, TileEntityPOEImporter>(DataSerializers.BOOLEAN, false, t -> t.getNode().getEnergyRestrictionMode() ,(t,v) -> t.getNode().setEnergyRestrictionMode(v));
	public static final TileDataParameter<Integer, TileEntityPOEImporter> ENERGY_RESTRICTION_VALUE = new TileDataParameter<Integer, TileEntityPOEImporter>(DataSerializers.VARINT, 0, t -> t.getNode().getEnergyRestrictionValue() ,(t,v) -> t.getNode().setEnergyRestrictionMode(v));
	
	public TileEntityPOEImporter()
	{
		super(CURRENT_TILE);
		
		dataManager.addWatchedParameter(ENERGY_RESTRICTION);
		dataManager.addWatchedParameter(ENERGY_RESTRICTION_VALUE);
	}

	@Override
    @Nonnull
    public NetworkNodePOEImporter createNode(World world, BlockPos pos)
    {
        return new NetworkNodePOEImporter(world, pos);
    }

}
