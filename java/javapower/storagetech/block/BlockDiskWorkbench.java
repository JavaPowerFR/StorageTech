package javapower.storagetech.block;

import javapower.storagetech.container.ContainerDiskWorkbench;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.gui.GuiContainerDiskWorkbench;
import javapower.storagetech.item.ItemMemory;
import javapower.storagetech.tileentity.TileEntityDiskWorkbench;
import javapower.storagetech.util.GuiUtils;
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

public class BlockDiskWorkbench extends BBaseContainer implements IGuiRegister
{

	public BlockDiskWorkbench()
	{
		super(Material.IRON, "diskworkbench");
	}

	@Override
	public Block getBlock()
	{
		return this;
	}

	@Override
	public TileNamed[] getTilesEntity()
	{
		return new TileNamed[]{new TileNamed(TileEntityDiskWorkbench.class, "diskworkbench")};
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityDiskWorkbench();
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
			GuiUtils.openGui(playerIn, StorageTech.MODID, 0, worldIn, pos);
			//playerIn.openGui(StorageTech.MODID, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	
	public void getDropsBlock(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityDiskWorkbench)
		{
			TileEntityDiskWorkbench te_dwb = (TileEntityDiskWorkbench) te;
			for(ItemStack is : te_dwb.block_inv_content)
				drops.add(is);
			
			if(te_dwb.memory > 0)
			{
				drops.add(ItemMemory.createItem(te_dwb.memory, false));
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
		return new GuiContainerDiskWorkbench((TileEntityDiskWorkbench) tile, player);
	}

	@Override
	public Container getContainer(TileEntity tile, EntityPlayer player)
	{
		// TODO Auto-generated method stub
		return new ContainerDiskWorkbench((TileEntityDiskWorkbench) tile, player);
	}

}
