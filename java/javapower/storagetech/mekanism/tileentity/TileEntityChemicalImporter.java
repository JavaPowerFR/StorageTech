package javapower.storagetech.mekanism.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.node.NetworkNodeChemicalImporter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityChemicalImporter extends NetworkNodeTile<NetworkNodeChemicalImporter>
{
	@ObjectHolder(StorageTech.MODID+":chemicalimporter")
	public static final TileEntityType<TileEntityChemicalImporter> CURRENT_TILE = null;
	
	public static final TileDataParameter<Integer, TileEntityChemicalImporter> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
	public static final TileDataParameter<Integer, TileEntityChemicalImporter> CHEMICAL_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getChemicalType(), (t, v) -> t.getNode().setChemicalType(v));
	
	public TileEntityChemicalImporter()
	{
		super(CURRENT_TILE);
		
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(CHEMICAL_TYPE);
	}

	@Override
    @Nonnull
    public NetworkNodeChemicalImporter createNode(World world, BlockPos pos)
    {
        return new NetworkNodeChemicalImporter(world, pos);
    }

}