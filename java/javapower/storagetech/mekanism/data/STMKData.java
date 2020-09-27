package javapower.storagetech.mekanism.data;

import java.util.ArrayList;
import java.util.List;

import javapower.storagetech.data.STData;
import javapower.storagetech.mekanism.api.IGasStorageNode;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.nbt.CompoundNBT;

public class STMKData
{
	private List<IGasStorageNode> gasStorages;
	STData parent;
	
	public STMKData(STData stData)
	{
		parent = stData;
		gasStorages = new ArrayList<>();
	}

	public void writeToNbt(CompoundNBT nbt)
	{
		
	}
	
	public void readFromNbt(CompoundNBT nbt)
	{
		
	}
	
	public void putGasStorageListener(IGasStorageNode gasStorageNode)
	{
		if(!gasStorages.contains(gasStorageNode))
			gasStorages.add(gasStorageNode);
	}

	public void removeGasStorageListener(IGasStorageNode gasStorageNode)
	{
		if(gasStorages.contains(gasStorageNode))
			gasStorages.remove(gasStorageNode);
	}
	
	public List<IGasStorageNode> getGasStorages()
	{
		return gasStorages;
	}
	
	public GasStack extractChemical(GasStack stack, Action action)
	{
		int amntextracted = 0;
		for(IGasStorageNode node : gasStorages)
		{
			GasStack stackExt = stack.copy();
			stackExt.shrink(amntextracted);
			GasStack result = node.extractChemical(stackExt, action);
			if(result.isTypeEqual(stack) && result.getAmount() > 0)
			{
				amntextracted += result.getAmount();
				
				if(action == Action.EXECUTE)
					parent.markForSaving();
				
				if(amntextracted >= stack.getAmount())
					return new GasStack(stack, amntextracted);
			}
		}
		
		if(amntextracted > 0)
			return new GasStack(stack, amntextracted);
		
		return GasStack.EMPTY;
	}
    
	public GasStack insertChemical(GasStack stack, Action action)
	{
		GasStack result = stack;
		for(IGasStorageNode node : gasStorages)
		{
			result = node.insertChemical(result.copy(), action);
			if(result.isEmpty())
			{
				if(action == Action.EXECUTE)
					parent.markForSaving();
				
				return GasStack.EMPTY;
			}
		}
		return result;
	}
}
