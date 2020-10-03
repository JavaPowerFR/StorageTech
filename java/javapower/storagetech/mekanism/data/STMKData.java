package javapower.storagetech.mekanism.data;

import java.util.ArrayList;
import java.util.List;

import javapower.storagetech.data.STData;
import javapower.storagetech.mekanism.api.IChemicalStorageNode;
import javapower.storagetech.mekanism.api.IChemicalViewNode;
import javapower.storagetech.mekanism.api.MekanismUtils;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.nbt.CompoundNBT;

public class STMKData
{
	private List<IChemicalStorageNode> chemicalStorages;
	private List<IChemicalViewNode> chemicalViews;
	
	//private TreeMap<Chemical<?>, Long> chemicalView;
	private List<ChemicalStack<?>> chemicalView;
	
	STData parent;
	
	public STMKData(STData stData)
	{
		parent = stData;
		
		chemicalStorages = new ArrayList<>();
		chemicalViews = new ArrayList<>();
		
		//chemicalView = new TreeMap<>();
		chemicalView = new ArrayList<>();
	}

	public void writeToNbt(CompoundNBT nbt)
	{
		
	}
	
	public void readFromNbt(CompoundNBT nbt)
	{
		
	}
	
	public void putChemicalStorageListener(IChemicalStorageNode chemicalStorageNode)
	{
		if(!chemicalStorages.contains(chemicalStorageNode))
		{
			chemicalStorages.add(chemicalStorageNode);
			updateVewListener(chemicalStorageNode, null, 0);
		}
	}

	public void removeChemicalStorageListener(IChemicalStorageNode chemicalStorageNode)
	{
		if(chemicalStorages.contains(chemicalStorageNode))
		{
			chemicalStorages.remove(chemicalStorageNode);
			updateVewListener(chemicalStorageNode, null, 0);
		}
	}
	
	public void putChemicalViewListener(IChemicalViewNode chemicalViewNode)
	{
		if(!chemicalViews.contains(chemicalViewNode))
		{
			chemicalViews.add(chemicalViewNode);
			chemicalViewNode.updateChemical(chemicalView);
		}
	}

	public void removeChemicalViewListener(IChemicalViewNode chemicalViewNode)
	{
		if(chemicalViews.contains(chemicalViewNode))
		{
			chemicalViews.remove(chemicalViewNode);
			chemicalViewNode.updateChemical(null);
		}
	}
	
	public List<IChemicalStorageNode> getChemicalStorages()
	{
		return chemicalStorages;
	}
    
	public ChemicalStack<?> extractChemical(ChemicalStack<?> stack, Action action)
	{
		long amntextracted = 0;
		for(IChemicalStorageNode node : chemicalStorages)
		{
			ChemicalStack<?> stackExt = stack.copy();
			stackExt.shrink(amntextracted);
			ChemicalStack<?> result = node.extractChemical(stackExt, action);
			if(result.getType().equals(stack.getType()) && result.getAmount() > 0)
			{
				amntextracted += result.getAmount();
				
				if(action == Action.EXECUTE)
				{
					updateVewListener(node, stack.getType(), -amntextracted);
					parent.markForSaving();
				}
				
				if(amntextracted >= stack.getAmount())
					return stack.getType().getStack(amntextracted);
			}
		}
		
		if(amntextracted > 0)
			return stack.getType().getStack(amntextracted);
		
		return MekanismUtils.getEmpty(stack);
	}
	
	public ChemicalStack<?> insertChemical(ChemicalStack<?> stack, Action action)
	{
		ChemicalStack<?> result = stack;
		for(IChemicalStorageNode node : chemicalStorages)
		{
			result = node.insertChemical(result.copy(), action);
			if(result.isEmpty())
			{
				if(action == Action.EXECUTE)
				{
					updateVewListener(node, stack.getType(), stack.getAmount());
					parent.markForSaving();
				}
				
				return MekanismUtils.getEmpty(stack);
			}
		}
		return result;
	}
	
	private void updateVewListener(IChemicalStorageNode node, Chemical<?> type, long deltaNetwork)
	{
		if(type != null)
		{
			//long old = getChemicalValue(type);
			long value = growChemical(type, deltaNetwork);
			//chemicalView.put(type, old + deltaNetwork);
			
			for(IChemicalViewNode viewNode : chemicalViews)
				viewNode.updateChemicalStack(type, value);
		}
		else
		{
			chemicalView.clear();
			
			for(IChemicalStorageNode inode : chemicalStorages)
				for(ChemicalDisk disk : inode.getDisks())
					for(ChemicalStack<?> chemicalStack : disk.getChemicals())
					{
						growChemical(chemicalStack.getType(), chemicalStack.getAmount());
						//long old = getChemicalValue(chemicalStack.getType());
						//chemicalView.put(chemicalStack.getType(), old + chemicalStack.getAmount());
					}
			
			for(IChemicalViewNode viewNode : chemicalViews)
				viewNode.updateChemical(chemicalView);
		}
	}
	
	private long growChemical(Chemical<?> ch, long deltaNetwork)
	{
		for(ChemicalStack<?> stack : chemicalView)
		{
			if(stack.getType() == ch)
			{
				stack.grow(deltaNetwork);
				return stack.getAmount();
			}
		}
		chemicalView.add(ch.getStack(deltaNetwork));
		return deltaNetwork;
	}

	/*public long getChemicalValue(Chemical<?> ch)
	{
		for(Entry<Chemical<?>, Long> entry : chemicalView.entrySet())
		{
			if(entry.getKey().equals(ch))
				return entry.getValue();
		}
		
		return 0;
	}*/
}
