package javapower.storagetech.util;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;

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
	
	public static void openGui(EntityPlayer entityPlayer, Object mod, int modGuiId, World world, BlockPos pos)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        if (entityPlayer instanceof EntityPlayerMP && !(entityPlayer instanceof FakePlayer))
        {
        	FMLNetworkHandler.openGui(entityPlayer, mod, modGuiId, world, pos.getX(), pos.getY(), pos.getZ());
        }
        else if (entityPlayer instanceof FakePlayer && FMLCommonHandler.instance().getSide().equals(Side.CLIENT))
        {
        	NetworkUtils.sendToServerOpenGui(mc, entityPlayer, modGuiId, world.getTileEntity(pos));
        }
        else
        {
            FMLLog.log.debug("Invalid attempt to open a local GUI on a dedicated server. This is likely a bug. GUI ID: {},{}", mc.getModId(), modGuiId);
        }

    }
}
