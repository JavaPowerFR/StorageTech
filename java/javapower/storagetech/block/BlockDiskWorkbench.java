package javapower.storagetech.block;

import java.util.List;

import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.render.Styles;

import javapower.storagetech.container.ContainerDiskWorkbench;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityDiskWorkbench;
import javapower.storagetech.util.Tools;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockDiskWorkbench extends BBaseContainer
{
	public BlockDiskWorkbench()
	{
		super(STBlocks.DEFAULT_BLOCK_PROPERTIES, "diskworkbench");
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn)
	{
		return new TileEntityDiskWorkbench();
	}
	
	@Override
	public Item getItem()
	{
		if(thisItem == null)
		{
			thisItem = new BlockItem(this, new Item.Properties().group(StorageTech.creativeTab))
			{
				@Override
				public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
				{
					if(stack.hasTag())
					{
						CompoundNBT nbt = stack.getTag();
						if(nbt.contains("memory"))
						tooltip.add(new TranslationTextComponent(I18n.format("storagetech.tooltip.memory")+": "+Tools.longFormatToString(nbt.getLong("memory"))+" VIB"));
						if(nbt.contains("prosses"))
							tooltip.add(new TranslationTextComponent(I18n.format("storagetech.tooltip.inprocess")+": "+nbt.getBoolean("prosses")).setStyle(Styles.GRAY));
					}
					super.addInformation(stack, worldIn, tooltip, flagIn);
				}
				
				@Override
				protected boolean placeBlock(BlockItemUseContext context, BlockState newState)
				{
					World world = context.getWorld();
					BlockPos pos = context.getPos();
					ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
					
					//if (!world.setBlockState(pos, newState, 11)) return false;
					if(!super.placeBlock(context, newState))
						return false;
					
			        if (newState.getBlock() == this.getBlock())
			        {
			            setTileEntityNBT(world, context.getPlayer(), pos, stack);
			            
			            if(stack.hasTag())
			            {
			            	TileEntity tile = world.getTileEntity(pos);
			            	if(tile != null && tile instanceof TileEntityDiskWorkbench)
			            	{
			            		((TileEntityDiskWorkbench)tile).readFromNBT(stack.getTag());
			            	}
			            }
			            
			            this.getBlock().onBlockPlacedBy(world, pos, newState, context.getPlayer(), stack);
			        }
			        
					return true;
				}
			};
			thisItem.setRegistryName(StorageTech.MODID,name);
		}
		return thisItem;
	}
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		super.onBlockHarvested(world, pos, state, player);
		if(world.isRemote)
			return;
		
		if(player.isCreative())
			return;
		
        ItemStack itemblock = new ItemStack(getItem(), 1);
        CompoundNBT nbt = new CompoundNBT();
       
        TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityDiskWorkbench)
		{
			TileEntityDiskWorkbench te_dwb = (TileEntityDiskWorkbench) te;
			te_dwb.write(nbt);
			nbt.remove("x");
			nbt.remove("y");
			nbt.remove("z");
			itemblock.setTag(nbt);
		}
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemblock);
        
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder)
	{
		return NonNullList.create();
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
					new PositionalTileContainerProvider<TileEntityDiskWorkbench>
						(
								((TileEntityDiskWorkbench) worldIn.getTileEntity(pos)).getDisplayName(),
								(tile, windowId, inventory, p) -> new ContainerDiskWorkbench(windowId, tile, inventory),
								pos
						),
						pos
			);
		}
		
		return ActionResultType.SUCCESS;
	}

}
