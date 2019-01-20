package javapower.storagetech.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public interface IGUITileSync
{
	public Class<? extends TileEntity> tileEntityLink();
	public void reciveDataFromServer(NBTTagCompound nbt);
}
