package javapower.storagetech.block;

import javapower.storagetech.container.ContainerFluidDiskWorkbench;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.gui.GuiContainerFluidDiskWorkbench;
import javapower.storagetech.item.ItemMemory;
import javapower.storagetech.tileentity.TileEntityFluidDiskWorkbench;
import javapower.storagetech.util.IGuiRegister;
import javapower.storagetech.util.TileNamed;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFluidDiskWorkbench extends BBaseContainer implements IGuiRegister
{

	public BlockFluidDiskWorkbench()
	{
		super(Material.IRON, "fluiddiskworkbench");
	}

	@Override
	public Block getBlock()
	{
		return this;
	}

	@Override
	public TileNamed[] getTilesEntity()
	{
		return new TileNamed[]{new TileNamed(TileEntityFluidDiskWorkbench.class, "fluiddiskworkbench")};
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityFluidDiskWorkbench();
	}
	
	// ---------- Render ----------
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!playerIn.isSneaking())
			playerIn.openGui(StorageTech.MODID, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	
	public void getDropsBlock(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityFluidDiskWorkbench)
		{
			TileEntityFluidDiskWorkbench te_dwb = (TileEntityFluidDiskWorkbench) te;
			for(ItemStack is : te_dwb.block_inv_content)
				drops.add(is);
			
			if(te_dwb.memory > 0)
			{
				drops.add(ItemMemory.createItem(te_dwb.memory, true));
			}
		}
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		if(!worldIn.isRemote)
		{
			NonNullList<ItemStack> items = NonNullList.create();
			getDropsBlock(items, worldIn, pos);
			for(ItemStack item : items)
			{
				InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), item);
			}
			
		}
		super.breakBlock(worldIn, pos, state);
	}
	
	// ---------- user interface ----------

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(TileEntity tile, EntityPlayer player)
	{
		return new GuiContainerFluidDiskWorkbench((TileEntityFluidDiskWorkbench) tile, player);
	}

	@Override
	public Container getContainer(TileEntity tile, EntityPlayer player)
	{
		return new ContainerFluidDiskWorkbench((TileEntityFluidDiskWorkbench) tile, player);
	}

}
