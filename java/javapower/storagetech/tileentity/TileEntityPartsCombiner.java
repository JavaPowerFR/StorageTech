package javapower.storagetech.tileentity;

import javapower.storagetech.core.CommonConfig;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.inventory.InventoryPartsCombiner;
import javapower.storagetech.util.DiskUtils;
import javapower.storagetech.util.EnergyBuffer;
import javapower.storagetech.util.PartValue;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityPartsCombiner extends TileEntityBase implements ITickableTileEntity
{
	@ObjectHolder(StorageTech.MODID+":partscombiner")
	public static final TileEntityType<TileEntityPartsCombiner> CURRENT_TILE = null;
	
	private static final int processTime = 220;
	private static final int energyPerTick = 4;
	private int processTick = 0;
	
	private int processUpdate = 0;
	
	InventoryPartsCombiner inventory = new InventoryPartsCombiner(() -> markDirty(), (stack) -> DiskUtils.getValue(stack) != null);
	
	EnergyBuffer energy = new EnergyBuffer(CommonConfig.Value_EnergyBuffer, CommonConfig.Value_EnergyBuffer/2, 0).setEvent(() ->
	{
		
		markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), Constants.BlockFlags.BLOCK_UPDATE);
		
	});
	
	public TileEntityPartsCombiner()
	{
		super(CURRENT_TILE);
	}
	
	@Override
	public void tick()
	{
		if(processTick < processTime)
		{
			if(processIsValid())
			{
				if(energy.canExtractLevel(energyPerTick*inventory.getUpgradesStep()))
				{
					energy.energy -= energyPerTick*inventory.getUpgradesStep();
					
					processTick += inventory.getUpgradesStep();
					markDirty();
				}
			}
			else
			{
				if(processTick != 0)
				{
					processTick = 0;
					markDirty();
				}
			}
		}
		else
		{
			tryToFinish();
		}
		
		int newValue = processTick/10;
		if(newValue != processUpdate)
		{
			processUpdate = newValue;
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), Constants.BlockFlags.BLOCK_UPDATE);
		}
	}

	private void tryToFinish()
	{
		ItemStack leftStack = inventory.getInventory().getStackInSlot(0);
		ItemStack rightStack = inventory.getInventory().getStackInSlot(1);
		
		PartValue left = DiskUtils.getValue(leftStack);
		PartValue right = DiskUtils.getValue(rightStack);
		
		if(left == null || right == null)
			return;
		
		if(left.getType() != right.getType())
			return;
		
		long value = left.getValue(leftStack) + right.getValue(rightStack);
		if(value >= 0 && value < Integer.MAX_VALUE)
		{
			ItemStack output = inventory.getInventory().getStackInSlot(2);
			
			if(output.isEmpty())
			{
				inventory.getInventory().setStackInSlot(2, left.createPart((int) value));
				
				leftStack.shrink(1);
				rightStack.shrink(1);
				
				markDirty();
				processTick = 0;
			}
			else
			{
				if(output.getCount() < 64)
				{
					PartValue outputValue = DiskUtils.getValue(output);
					if(outputValue.getValue(output) == value && outputValue.getType() == left.getType())
					{
						output.grow(1);
						
						leftStack.shrink(1);
						rightStack.shrink(1);
						
						markDirty();
						processTick = 0;
					}
				}
			}
		}
	}

	private boolean processIsValid()
	{
		ItemStack leftStack = inventory.getInventory().getStackInSlot(0);
		ItemStack rightStack = inventory.getInventory().getStackInSlot(1);
		
		PartValue left = DiskUtils.getValue(leftStack);
		PartValue right = DiskUtils.getValue(rightStack);
		
		if(left == null || right == null)
			return false;
		
		if(left.getType() != right.getType())
			return false;
		
		ItemStack outStack = inventory.getInventory().getStackInSlot(2);
		PartValue out = DiskUtils.getValue(outStack);
		if(out != null && out.getType() != left.getType())
			return false;
		
		long value = (long)left.getValue(leftStack) + (long)right.getValue(rightStack);
		
		if(out != null && out.getValue(outStack) != value)
			return false;
		
		return value >= 0 && value < Integer.MAX_VALUE;
	}

	@Override
	public void readFromNBT(CompoundNBT tag)
	{
		inventory.read(tag);
		energy.ReadFromNBT(tag);
		
		if(tag.contains("processTick"))
			processTick = tag.getInt("processTick");
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag)
	{
		inventory.write(tag);
		energy.WriteToNBT(tag);
		
		tag.putInt("processTick", processTick);
		
		return tag;
	}

	@Override
	protected void readFromServer(CompoundNBT tag)
	{
		energy.ReadFromNBT(tag);
		
		if(tag.contains("processTick"))
			processTick = tag.getInt("processTick");
	}

	@Override
	protected CompoundNBT writeToClient(CompoundNBT tag)
	{
		energy.WriteToNBT(tag);
		
		tag.putInt("processTick", processTick);
		
		return tag;
	}
	
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == CapabilityEnergy.ENERGY)
		{
			return energy.getEnergyCapability().cast();
		}
		else if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return inventory.getItemsCapability().cast();
		}
		
		return super.getCapability(cap, side);
	}
	
	public InventoryPartsCombiner getInventory()
	{
		return inventory;
	}
	
	public EnergyBuffer getEnergy()
	{
		return energy;
	}
	
	public int getProcessUpdate()
	{
		return processUpdate;
	}

}
