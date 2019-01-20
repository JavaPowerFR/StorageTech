package javapower.storagetech.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public interface ITileUpdate
{
	public void reciveDataFromClient(NBTTagCompound nbt, EntityPlayer player);
	public void onPlayerOpenGUISendData(NBTTagCompound nbt, EntityPlayer player);
	public NBTTagCompound updateData();
	public void addOrRemovePlayer(EntityPlayerMP player, boolean isAdded);
}
