package javapower.storagetech.util;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.tileentity.TileEntityStructureConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SCElement
{
	BlockLocalPos localPos;
	BlockPos worldPos;
	
	boolean drop;
	ItemStack stack;
	BlockState blockState;
	
	public boolean isLoaded = false;
	
	public SCElement(BlockLocalPos _localPos, boolean _drop, ItemStack _stack)
	{
		localPos = _localPos;
		stack = _stack;
		drop = _drop;
		
		if(!drop && stack != null)
		{
			Item itemIn = stack.getItem();
			if(itemIn != null && itemIn instanceof BlockItem)
			{
				blockState = ((BlockItem)itemIn).getBlock().getDefaultState();
			}
		}
	}
	
	public SCElement(TileEntityStructureConstructor tesc, BlockLocalPos _localPos, boolean _drop, ItemStack _stack)
	{
		localPos = _localPos;
		load(tesc);
		stack = _stack;
		drop = _drop;
		
		if(!drop && stack != null)
		{
			Item itemIn = stack.getItem();
			if(itemIn != null && itemIn instanceof BlockItem)
			{
				blockState = ((BlockItem)itemIn).getBlock().getDefaultState();
			}
		}
	}
	
	public void load(TileEntityStructureConstructor tesc)
	{
		worldPos = localPos.getBlockPos(tesc.getPos(), tesc.getBlockState().get(STBlocks.blockStructureConstructor.getDirection().getProperty()));
		isLoaded = true;
	}
	
	public boolean worldProcess(World world, INetwork network)
	{
		if(worldPos == null || network == null)
			return false;
		
		if(drop)
		{
			ItemStack stackDrop = network.extractItem(stack, stack.getCount(), Action.SIMULATE);
			if(stackDrop != null)
			{
				if(stack.isItemEqual(stackDrop) && stack.getCount() == stackDrop.getCount())
				{
					world.addEntity(
							new ItemEntity(
									world,
									worldPos.getX() + 0.5D,
									worldPos.getY() + 0.5D,
									worldPos.getZ() + 0.5D,
									network.extractItem(stack, stack.getCount(), Action.PERFORM)
									));
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		else
		{
			if(blockState != null)
			{
				ItemStack stackExtracted = network.extractItem(stack, 1, Action.SIMULATE);
				if(stackExtracted != null && !stackExtracted.isEmpty() && stackExtracted.getCount() == 1 && world.isAirBlock(worldPos))
				{
					network.extractItem(stack, 1, Action.PERFORM);
					world.setBlockState(worldPos, blockState);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void setLocalPos(TileEntityStructureConstructor tesc, BlockLocalPos _localPos)
	{
		localPos = _localPos;
		worldPos = localPos.getBlockPos(tesc.getPos(), tesc.getBlockState().get(STBlocks.blockStructureConstructor.getDirection().getProperty()));
	}
	
	public void setElements(boolean isDrop, ItemStack _stack)
	{
		drop = isDrop;
		stack = _stack;
		blockState = null;
		
		if(!drop && stack != null)
		{
			Item itemIn = stack.getItem();
			if(itemIn != null && itemIn instanceof BlockItem)
			{
				blockState = ((BlockItem)itemIn).getBlock().getDefaultState();
			}
		}
	}
	
	public BlockState getBlockState()
	{
		return blockState;
	}
	
	public BlockLocalPos getLocalPos()
	{
		return localPos;
	}
	
	public BlockPos getWorldPos()
	{
		return worldPos;
	}
	
	public ItemStack getStack()
	{
		return stack;
	}
	
	public void writeToNBT(CompoundNBT nbt)
	{
		if(localPos == null || stack == null)
			return;
		
		localPos.writeToNBT(nbt);
		nbt.putBoolean("Drop", drop);
		stack.write(nbt);
		
	}
	
	public static SCElement getFromNBT(CompoundNBT nbt)
	{
		if(BlockLocalPos.NBTcontainTag(nbt) && nbt.contains("id"))
		{
			return new SCElement(BlockLocalPos.getFromNBT(nbt) , nbt.getBoolean("Drop"), ItemStack.read(nbt));
		}
		return null;
	}
	
	public static SCElement getFromNBT(TileEntityStructureConstructor tesc, CompoundNBT nbt)
	{
		if(BlockLocalPos.NBTcontainTag(nbt) && nbt.contains("id"))
		{
			return new SCElement(tesc, BlockLocalPos.getFromNBT(nbt) , nbt.getBoolean("Drop"), ItemStack.read(nbt));
		}
		return null;
	}
	
	public static class Client
	{
		public boolean isClientDirty = false;
		public BlockLocalPos localPos;
		public boolean drop;
		public ItemStack stack;
		
		public Client(BlockLocalPos _localPos, boolean _drop, ItemStack _stack)
		{
			localPos = _localPos;	
			stack = _stack;
			drop = _drop;
		}
		
		public void writeToNBT(CompoundNBT nbt)
		{
			if(localPos == null || stack == null)
				return;
			
			localPos.writeToNBT(nbt);
			nbt.putBoolean("Drop", drop);
			stack.write(nbt);
			
		}
		
		public static Client getFromNBT(CompoundNBT nbt)
		{
			if(BlockLocalPos.NBTcontainTag(nbt) && nbt.contains("id"))
			{
				return new Client(BlockLocalPos.getFromNBT(nbt) , nbt.getBoolean("Drop"), ItemStack.read(nbt));
			}
			return null;
		}
	}
}
