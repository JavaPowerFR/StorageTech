package javapower.storagetech.mekanism.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javapower.storagetech.mekanism.api.MekanismUtils;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class ChemicalDisk
{
	public final UUID id;
	public final long capacity;
	
	protected long amount;
	protected List<ChemicalStack<?>> chemicals;
	
	public ChemicalDisk(UUID _id, long _capacity)
	{
		id = _id;
		capacity = _capacity;
		
		chemicals = new ArrayList<ChemicalStack<?>>();
	}
	
	public void writeToNBT(CompoundNBT nbt)
	{
		nbt.putUniqueId("Id", id);
		nbt.putLong("Capacity", capacity);
		nbt.putLong("Amount", amount);
		
		ListNBT list_chemical = new ListNBT();
		for(ChemicalStack<?> stack : chemicals)
		{
			if(stack != null && !stack.isEmpty())
			{
				CompoundNBT nbtTags = new CompoundNBT();
				stack.write(nbtTags);
				nbtTags.putByte("Chemical", MekanismUtils.getChemicalTypeId(stack.getType()));
				list_chemical.add(nbtTags);
			}
		}
		nbt.put("Stacks", list_chemical);
	}
	
	public static ChemicalDisk readFromNBT(CompoundNBT nbt)
	{
		if(nbt.hasUniqueId("Id"))
		{
			ChemicalDisk disk = new ChemicalDisk(nbt.getUniqueId("Id"), nbt.getInt("Capacity"));
			if(nbt.contains("Stacks"))
			{
				ListNBT disksTag = nbt.getList("Stacks", Constants.NBT.TAG_COMPOUND);
				disk.amount = 0;
				for (int i = 0; i < disksTag.size(); ++i)
				{
					CompoundNBT nbtTags = disksTag.getCompound(i);
					if(nbtTags.contains("Chemical"))
					{
						ChemicalStack<?> stack = MekanismUtils.buildChemicalStackById(nbtTags.getByte("Chemical"), nbtTags);
						
						if(stack != null && !stack.isEmpty())
						{
							disk.chemicals.add(stack);
							disk.amount += stack.getAmount();
						}
					}
				}
								
			}
			
			return disk;
		}
		return null;
	}
	
	public long getCapacity()
	{
		return capacity;
	}
	
	public long getAmount()
	{
		return amount;
	}
	
	public long getSpace()
    {
    	return capacity - amount;
    }
	
	public int getChemicalSize()
	{
		return chemicals.size();
	}
	
	public ChemicalStack<?> getChemicalInSlot(int index)
	{
		return chemicals.get(index);
	}
	
	public void setChemicalInSlot(int index, ChemicalStack<?> stack)
	{
		chemicals.set(index, stack);
	}
	
	public ChemicalStack<?> extractChemical(ChemicalStack<?> stack, Action action)
	{
		if(stack != null && !stack.isEmpty() && chemicals.size() > 0)
		{
			for(int index = 0; index < chemicals.size(); ++index)
			{
				ChemicalStack<?> chemical = chemicals.get(index);
				if(chemical != null && chemical.getType().equals(stack.getType()))
				{
					long amntExtracted = Math.min(chemical.getAmount(), stack.getAmount());
					if(amntExtracted > 0)
					{
						ChemicalStack<?> chemicalExtracted = chemical.copy();
						chemicalExtracted.setAmount(amntExtracted);
						if(action == Action.EXECUTE)
						{
							if(amntExtracted >= chemical.getAmount())
								chemicals.remove(index);
							else
								chemical.shrink(amntExtracted);
							
							amount -= amntExtracted;
						}
						return chemicalExtracted;
					}
				}
			}
		}
		
		return MekanismUtils.getEmpty(stack);
	}
	
	public ChemicalStack<?> insertChemical(ChemicalStack<?> stack, Action action)
	{
		if(stack != null && !stack.isEmpty())
		{
			long space = getSpace();
			if(space >= stack.getAmount())
			{
				if(action == Action.EXECUTE)
				{
					boolean sucsess = false;
					for(int index = 0; index < chemicals.size(); ++index)
					{
						ChemicalStack<?> chemical = chemicals.get(index);
						if(chemical != null)
						{
							if(chemical.getType().equals(stack.getType()))
							{
								chemical.grow(stack.getAmount());
								sucsess = true;
								break;
							}
						}
					}
					
					if(!sucsess)
					{
						chemicals.add(stack.copy());
					}
					
					amount += stack.getAmount();
				}
				
				return MekanismUtils.getEmpty(stack);
			}
			else if(space > 0)
			{
				if(action == Action.EXECUTE)
				{
					boolean sucsess = false;
					for(int index = 0; index < chemicals.size(); ++index)
					{
						ChemicalStack<?> chemical = chemicals.get(index);
						if(chemical != null)
						{
							if(chemical.getType().equals(stack.getType()))
							{
								chemical.grow(space);
								sucsess = true;
								break;
							}
						}
					}
					
					if(!sucsess)
					{
						ChemicalStack<?> chemical = stack.copy();
						chemical.setAmount(space);
						chemicals.add(chemical);
					}
					
					amount += space;
				}
				
				ChemicalStack<?> chemicalreturn = stack.copy();
				chemicalreturn.setAmount(stack.getAmount() - space);
				return chemicalreturn;
			}
		}
		return stack;
	}
	
	public List<ChemicalStack<?>> getChemicals()
	{
		return chemicals;
	}
	
}
