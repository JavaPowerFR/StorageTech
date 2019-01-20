package javapower.storagetech.util;

import net.minecraft.tileentity.TileEntity;

public class TileNamed
{
	public Class<? extends TileEntity> TClass;
	public String TName;
	
	public TileNamed(Class<? extends TileEntity> exClass, String name)
	{
		TClass = exClass;
		TName = name;
	}
	
	public Class<? extends TileEntity> getTClass()
	{
		return TClass;
	}
}
