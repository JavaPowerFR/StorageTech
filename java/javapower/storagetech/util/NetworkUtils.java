package javapower.storagetech.util;

import javapower.storagetech.message.NetworkTileSync;
import javapower.storagetech.proxy.CommonProxy;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetworkUtils
{
	@SideOnly(Side.CLIENT)
	public static void sendToServerPlayerAsOpenGUI(TileEntity te, IGUITileSync gui)
	{
		NBTTagCompound tag_inf = new NBTTagCompound();
		tag_inf.setString("te", gui.tileEntityLink().getName());
		tag_inf.setInteger("lk", 1);
		CommonProxy.network_TileSynchroniser.sendToServer(new NetworkTileSync(new BlockPosDim(te.getPos(), te.getWorld().provider.getDimension()), new NBTTagCompound(), tag_inf));
	}
	
	@SideOnly(Side.CLIENT)
	public static void sendToServerPlayerStopOpenGUI(BlockPosDim posdim)
	{
		NBTTagCompound tag_inf = new NBTTagCompound();
		tag_inf.setInteger("lk", 3);
		CommonProxy.network_TileSynchroniser.sendToServer(new NetworkTileSync(posdim, new NBTTagCompound(), tag_inf));
	}
	
	public static void sendToPlayerTheData(TileEntity te, NBTTagCompound nbt, EntityPlayerMP player)
	{
		CommonProxy.network_TileSynchroniser.sendTo(new NetworkTileSync(new BlockPosDim(te.getPos(), te.getWorld().provider.getDimension()), nbt, new NBTTagCompound()), player);
	}
	
	@SideOnly(Side.CLIENT)
	public static void sendToServerTheData(TileEntity te, IGUITileSync gui, NBTTagCompound nbt)
	{
		NBTTagCompound tag_inf = new NBTTagCompound();
		tag_inf.setString("te", gui.tileEntityLink().getName());
		tag_inf.setInteger("lk", 2);
		CommonProxy.network_TileSynchroniser.sendToServer(new NetworkTileSync(new BlockPosDim(te.getPos(), te.getWorld().provider.getDimension()), nbt, tag_inf));
	}
}
