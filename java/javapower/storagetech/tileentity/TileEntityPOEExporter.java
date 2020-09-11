package javapower.storagetech.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.node.NetworkNodePOEExporter;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityPOEExporter extends NetworkNodeTile<NetworkNodePOEExporter>
{
	@ObjectHolder(StorageTech.MODID+":poeexporter")
	public static final TileEntityType<TileEntityPOEExporter> CURRENT_TILE = null;
	
	public TileEntityPOEExporter()
	{
		super(CURRENT_TILE);

	}

	@Override
    @Nonnull
    public NetworkNodePOEExporter createNode(World world, BlockPos pos)
    {
        return new NetworkNodePOEExporter(world, pos);
    }

}
