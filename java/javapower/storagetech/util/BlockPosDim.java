package javapower.storagetech.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class BlockPosDim
{
	BlockPos pos;
	int dimID;
	
	public BlockPosDim(BlockPos _pos, int _dimID)
	{
		pos = _pos;
		dimID = _dimID;
	}
	
	public BlockPosDim()
	{
		pos = new BlockPos(0, 0, 0);
		dimID = 0;
	}
	
	public BlockPosDim(int x, int y, int z, int d)
	{
		pos = new BlockPos(x, y, z);
		dimID = d;
	}
	
	public BlockPosDim(String seed)
	{
		this();
		if(seed != null && seed.contains("-"))
		{
			String[] s = seed.split("-");
			if(s != null && s.length == 4)
			{
				int x = 0,y = 0,z = 0,d = 0;
				try
				{
					x = Integer.parseInt(s[0].substring(1));
				}
				catch (StringIndexOutOfBoundsException | NumberFormatException e)
				{
					e.printStackTrace();
				}
				
				try
				{
					y = Integer.parseInt(s[1].substring(1));
				}
				catch (StringIndexOutOfBoundsException | NumberFormatException e)
				{
					e.printStackTrace();
				}
				
				try
				{
					z = Integer.parseInt(s[2].substring(1));
				}
				catch (StringIndexOutOfBoundsException | NumberFormatException e)
				{
					e.printStackTrace();
				}
				
				try
				{
					d = Integer.parseInt(s[3].substring(1));
				}
				catch (StringIndexOutOfBoundsException | NumberFormatException e)
				{
					e.printStackTrace();
				}
				
				pos = new BlockPos(x, y, z);
				dimID = d;
			}
		}
	}
	
	public BlockPosDim(NBTTagCompound nbt, String name)
	{
		if(nbt != null && name != null && nbt.hasKey(name))
		{
			NBTTagCompound inbt = nbt.getCompoundTag(name);
			dimID = inbt.getInteger("D");
			pos = new BlockPos(inbt.getInteger("X"), inbt.getInteger("Y"), inbt.getInteger("Z"));
		}
		else
		{
			pos = new BlockPos(0, 0, 0);
		}
	}
	
	public void WriteToNBT(NBTTagCompound nbt, String name)
	{
		if(nbt != null && name != null)
		{
			NBTTagCompound inbt = new NBTTagCompound();
			
			inbt.setInteger("D", dimID);
			if(pos != null)
			{
				inbt.setInteger("X", pos.getX());
				inbt.setInteger("Y", pos.getY());
				inbt.setInteger("Z", pos.getZ());
			}
			
			nbt.setTag(name, inbt);
		}
	}
	
	public void ReadFromNBT(NBTTagCompound nbt, String name)
	{
		if(nbt != null && name != null && nbt.hasKey(name))
		{
			NBTTagCompound inbt = nbt.getCompoundTag(name);
			dimID = inbt.getInteger("D");
			pos = new BlockPos(inbt.getInteger("X"), inbt.getInteger("Y"), inbt.getInteger("Z"));
		}
	}
	
	public TileEntity GetTileEntity()
	{
		WorldServer w = DimensionManager.getWorld(dimID);
		if(w == null)
			return null;
		return w.getTileEntity(pos);
	}
	
	public IBlockState GetBlockState()
	{
		WorldServer w = DimensionManager.getWorld(dimID);
		if(w == null)
			return null;
		return w.getBlockState(pos);
	}
	
	public WorldServer GetWorld()
	{
		return DimensionManager.getWorld(dimID);
	}
	
	public BlockPos getPos()
	{
		return pos;
	}
	
	public int getDimID()
	{
		return dimID;
	}
	
	public void setDimID(int _dimID)
	{
		dimID = _dimID;
	}
	
	public void setPos(BlockPos _pos)
	{
		pos = _pos;
	}
	
	public void setPos(int x, int y, int z)
	{
		pos = new BlockPos(x, y, z);
	}
	
	public int getX()
	{
		return pos.getX();
	}
	
	public int getY()
	{
		return pos.getY();
	}
	
	public int getZ()
	{
		return pos.getZ();
	}
	
	@Override
	public String toString()
	{
		return "x"+pos.getX()+"-y"+pos.getY()+"-z"+pos.getZ()+"-d"+dimID;
	}
	
	public String toText()
	{
		return "x="+pos.getX()+",y="+pos.getY()+",z="+pos.getZ()+",d="+dimID;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
			return true;
		
		else if(obj instanceof BlockPosDim)
		{
			return dimID == ((BlockPosDim)obj).dimID &&
					((BlockPosDim)obj).pos.getX() == pos.getX() &&
					((BlockPosDim)obj).pos.getY() == pos.getY() &&
					((BlockPosDim)obj).pos.getZ() == pos.getZ();
		}
		return false;
	}
	
}
