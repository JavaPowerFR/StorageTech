package javapower.storagetech.mekanism.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.node.NetworkNodeGasImporter;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityGasImporter extends NetworkNodeTile<NetworkNodeGasImporter>
{
	@ObjectHolder(StorageTech.MODID+":gasimporter")
	public static final TileEntityType<TileEntityGasImporter> CURRENT_TILE = null;
	
	public static final TileDataParameter<Integer, TileEntityGasImporter> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
	
	public TileEntityGasImporter()
	{
		super(CURRENT_TILE);
		
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
	}

	@Override
    @Nonnull
    public NetworkNodeGasImporter createNode(World world, BlockPos pos)
    {
        return new NetworkNodeGasImporter(world, pos);
    }

}