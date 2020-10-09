package javapower.storagetech.block;

import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;

import javapower.storagetech.container.ContainerPartsCombiner;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityPartsCombiner;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockPartsCombiner extends BBaseContainer
{
	public BlockPartsCombiner()
	{
		super(STBlocks.DEFAULT_BLOCK_PROPERTIES, "partscombiner");
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn)
	{
		return new TileEntityPartsCombiner();
	}
	
	@Override
	public Item getItem()
	{
		if(thisItem == null)
		{
			thisItem = new BlockItem(this, new Item.Properties().group(StorageTech.creativeTab));
			thisItem.setRegistryName(StorageTech.MODID,name);
		}
		return thisItem;
	}
	
	// ---------- Render ----------
	
	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult p_225533_6_)
	{
		
		if(!worldIn.isRemote)
		{
			NetworkHooks.openGui
			(
					(ServerPlayerEntity) player,
					new PositionalTileContainerProvider<TileEntityPartsCombiner>
						(
								new TranslationTextComponent("gui.storagetech.partscombiner"),
								(tile, windowId, inventory, p) -> new ContainerPartsCombiner(windowId, tile, inventory),
								pos
						),
						pos
			);
		}
		
		return ActionResultType.SUCCESS;
	}
}
