package javapower.storagetech.mekanism.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.node.NetworkNodeGasExporter;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityGasExporter extends NetworkNodeTile<NetworkNodeGasExporter>
{
	@ObjectHolder(StorageTech.MODID+":gasexporter")
	public static final TileEntityType<TileEntityGasExporter> CURRENT_TILE = null;
	
	public TileEntityGasExporter()
	{
		super(CURRENT_TILE);
	}

	@Override
    @Nonnull
    public NetworkNodeGasExporter createNode(World world, BlockPos pos)
    {
        return new NetworkNodeGasExporter(world, pos);
    }

}