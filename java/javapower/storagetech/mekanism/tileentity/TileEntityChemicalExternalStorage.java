package javapower.storagetech.mekanism.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.node.NetworkNodeChemicalExternalStorage;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityChemicalExternalStorage extends NetworkNodeTile<NetworkNodeChemicalExternalStorage>
{
	@ObjectHolder(StorageTech.MODID+":chemicalexternalstorage")
	public static final TileEntityType<TileEntityChemicalExternalStorage> CURRENT_TILE = null;
	
	public static final TileDataParameter<Integer, TileEntityChemicalExternalStorage> CHEMICAL_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getChemicalType(), (t, v) -> t.getNode().setChemicalType(v));
	
	public TileEntityChemicalExternalStorage()
	{
		super(CURRENT_TILE);
		dataManager.addWatchedParameter(CHEMICAL_TYPE);
	}

	@Override
    @Nonnull
    public NetworkNodeChemicalExternalStorage createNode(World world, BlockPos pos)
    {
        return new NetworkNodeChemicalExternalStorage(world, pos);
    }

}