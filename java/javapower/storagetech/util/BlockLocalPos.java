package javapower.storagetech.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class BlockLocalPos
{
	private int i, j, k;
	
	public BlockLocalPos()
	{
		
	}
	
	public BlockLocalPos(int _i, int _j, int _k)
	{
		i = _i;
		j = _j;
		k = _k;
	}
	
	public int getI()
	{
		return i;
	}
	
	public int getJ()
	{
		return j;
	}
	
	public int getK()
	{
		return k;
	}
	
	public void setI(int _i)
	{
		i = _i;
	}
	
	public void setJ(int _j)
	{
		j = _j;
	}
	
	public void setK(int _k)
	{
		k = _k;
	}
	
	public BlockPos getBlockPos(BlockPos from_target)
	{
		return new BlockPos(from_target.getX() + i, from_target.getY() + j, from_target.getZ() + k);
	}
	
	public BlockPos getBlockPos(BlockPos from_target, Direction dir_target)
	{
		if(dir_target == Direction.NORTH)
		{
			return new BlockPos(from_target.getX() + i, from_target.getY() + j, from_target.getZ() - k);
		}
		else if(dir_target == Direction.SOUTH)
		{
			return new BlockPos(from_target.getX() - i, from_target.getY() + j, from_target.getZ() + k);
		}
		else if(dir_target == Direction.WEST)
		{
			return new BlockPos(from_target.getX() - k, from_target.getY() + j, from_target.getZ() - i);
		}
		else if(dir_target == Direction.EAST)
		{
			return new BlockPos(from_target.getX() + k, from_target.getY() + j, from_target.getZ() + i);
		}
		else if(dir_target == Direction.UP)
		{
			return new BlockPos(from_target.getX() - j, from_target.getY() + i, from_target.getZ() + k);
		}
		else if(dir_target == Direction.DOWN)
		{
			return new BlockPos(from_target.getX() + j, from_target.getY() - i, from_target.getZ() + k);
		}
		else 
		{
			return new BlockPos(from_target.getX() + i, from_target.getY() + j, from_target.getZ() + k);
		}
	}
	
	public void writeToNBT(CompoundNBT nbt)
	{
		nbt.putInt("I", i);
		nbt.putInt("J", j);
		nbt.putInt("K", k);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
			return true;
		
		if(obj instanceof BlockLocalPos)
			return i == ((BlockLocalPos)obj).getI() && j == ((BlockLocalPos)obj).getJ() && k == ((BlockLocalPos)obj).getK();
		
		return false;
	}
	
	public static BlockLocalPos getFromNBT(CompoundNBT nbt)
	{
		if(NBTcontainTag(nbt))
			return new BlockLocalPos(nbt.getInt("I"), nbt.getInt("J"), nbt.getInt("K"));
		
		return null;
	}
	
	public static boolean NBTcontainTag(CompoundNBT nbt)
	{
		return nbt.contains("I") && nbt.contains("J") && nbt.contains("K");
	}
}
