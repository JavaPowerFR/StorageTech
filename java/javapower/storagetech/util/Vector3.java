package javapower.storagetech.util;

import net.minecraft.nbt.NBTTagCompound;

public class Vector3
{
	public int x,y,z;
	
	public Vector3(int _x, int _y, int _z)
	{
		x = _x;
		y = _y;
		z = _z;
	}
	
	public Vector3()
	{
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vector3 copy()
	{
		return new Vector3(x, y, z);
	}
	
	public void SaveToNBT(NBTTagCompound nbt, String name)
	{
		if(this != null)
		{
			nbt.setInteger(name+"X", x);
			nbt.setInteger(name+"Y", y);
			nbt.setInteger(name+"Z", z);
		}
	}
	
	public void ReadFromNBT(NBTTagCompound nbt, String name)
	{
		if(nbt.hasKey(name+"X")&&nbt.hasKey(name+"Y")&&nbt.hasKey(name+"Z"))
		{
			x = nbt.getInteger(name+"X");
			y = nbt.getInteger(name+"Y");
			z = nbt.getInteger(name+"Z");
		}
	}
	
	public String toString()
	{
		return "[X:"+x+" Y:"+y+" Z:"+z+"]";
	}
	
	public boolean ZeroPoint()
	{
		return x == 0 && y == 0 && z == 0;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Vector3)
		{
			return ((Vector3)obj).x == x && ((Vector3)obj).y == y && ((Vector3)obj).z == z;
		}
		return super.equals(obj);
	}
	
	public boolean equals(Vector3 vec)
	{
		return vec.x == x && vec.y == y && vec.z == z;
	}
	
	public boolean equals(int _x, int _y, int _z)
	{
		return _x == x && _y == y && _z == z;
	}
}
