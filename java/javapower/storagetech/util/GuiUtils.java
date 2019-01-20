package javapower.storagetech.util;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiUtils
{
	public static GuiContainer getGuiContainer(World world, BlockPos pos, EntityPlayer player)
	{
		Block block = world.getBlockState(pos).getBlock();
		if(block != null && block instanceof IGuiRegister)
				return ((IGuiRegister)block).getGui(world.getTileEntity(pos), player);
		
		return null;
	}
	
	public static Container getContainer(World world, BlockPos pos, EntityPlayer player)
	{
		Block block = world.getBlockState(pos).getBlock();
		if(block != null && block instanceof IGuiRegister)
				return ((IGuiRegister)block).getContainer(world.getTileEntity(pos), player);
		
		return null;
	}
}
