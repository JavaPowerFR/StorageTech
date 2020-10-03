package javapower.storagetech.mekanism.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.node.NetworkNodeChemicalExporter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityChemicalExporter extends NetworkNodeTile<NetworkNodeChemicalExporter>
{
	@ObjectHolder(StorageTech.MODID+":chemicalexporter")
	public static final TileEntityType<TileEntityChemicalExporter> CURRENT_TILE = null;
	
	public static final TileDataParameter<Integer, TileEntityChemicalExporter> CHEMICAL_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getChemicalType(), (t, v) -> t.getNode().setChemicalType(v));
	
	public TileEntityChemicalExporter()
	{
		super(CURRENT_TILE);
		dataManager.addWatchedParameter(CHEMICAL_TYPE);
	}

	@Override
    @Nonnull
    public NetworkNodeChemicalExporter createNode(World world, BlockPos pos)
    {
        return new NetworkNodeChemicalExporter(world, pos);
    }

}