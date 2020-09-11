package javapower.storagetech.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.node.NetworkNodePOEImporter;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityPOEImporter extends NetworkNodeTile<NetworkNodePOEImporter>
{
	@ObjectHolder(StorageTech.MODID+":poeimporter")
	public static final TileEntityType<TileEntityPOEImporter> CURRENT_TILE = null;
	
	public TileEntityPOEImporter()
	{
		super(CURRENT_TILE);

	}

	@Override
    @Nonnull
    public NetworkNodePOEImporter createNode(World world, BlockPos pos)
    {
        return new NetworkNodePOEImporter(world, pos);
    }

}
