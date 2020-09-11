package javapower.storagetech.block;

import javapower.storagetech.core.StorageTech;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ObjectHolder;

public class BlockGenerator extends BBaseContainer
{

	protected BlockGenerator()
	{
		super(STBlocks.DEFAULT_BLOCK_PROPERTIES, "generator");
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn)
	{
		return new TileEntityGenerator();
	}
	
	// ---------- Render ----------
	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}
	
	
	public static class TileEntityGenerator extends TileEntity implements ITickableTileEntity
	{
		@ObjectHolder(StorageTech.MODID+":generator")
		public static final TileEntityType<TileEntityGenerator> CURRENT_TILE = null;
		
		public TileEntityGenerator()
		{
			super(CURRENT_TILE);
		}

		@Override
		public void tick()
		{
			if(world != null && !world.isRemote)
			for(Direction dir : Direction.values())
			{
				TileEntity tileSide = world.getTileEntity(pos.offset(dir));
				if(tileSide != null)
				{
					LazyOptional<IEnergyStorage> energycap = tileSide.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite());
					if(energycap != null)
					{
						energycap.ifPresent(storage ->
						{
								if(storage.canReceive())
								{
									storage.receiveEnergy(200, false);
								}
						});
					}
				}
			}
		}
		
	}

}
