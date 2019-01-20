package javapower.storagetech.util;

import net.minecraft.nbt.NBTTagCompound;

public class Vector2
{
	public int x,y;
	
	public Vector2(int _x, int _y)
	{
		x = _x;
		y = _y;
	}
	
	public Vector2()
	{
		x = 0;
		y = 0;
	}
	
	public Vector2 copy()
	{
		return new Vector2(x, y);
	}
	
	public Vector2 copyAndAdd(int _x, int _y)
	{
		return new Vector2(x+_x, y+_y);
	}
	
	public void SaveToNBT(NBTTagCompound nbt, String name)
	{
		nbt.setInteger(name+"X", x);
		nbt.setInteger(name+"Y", y);
	}
	
	public void ReadFromNBT(NBTTagCompound nbt, String name)
	{
		if(nbt.hasKey(name+"X")&&nbt.hasKey(name+"Y"))
		{
			x = nbt.getInteger(name+"X");
			y = nbt.getInteger(name+"Y");
		}
	}
	
	public String toString()
	{
		return "[X:"+x+" Y:"+y+"]";
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Vector2)
		{
			return ((Vector2)obj).x == x && ((Vector2)obj).y == y;
		}
		return super.equals(obj);
	}
	
	public boolean equals(Vector2 vec)
	{
		return vec.x == x && vec.y == y;
	}
	
	public boolean equals(int _x, int _y)
	{
		return _x == x && _y == y;
	}
}
