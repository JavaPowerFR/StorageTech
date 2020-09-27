package javapower.storagetech.mekanism.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class GasDisk
{
	public final UUID id;
	public final long capacity;
	
	protected long amount;
	protected List<GasStack> gass;
	
	public GasDisk(UUID _id, long _capacity)
	{
		id = _id;
		capacity = _capacity;
		gass = new ArrayList<GasStack>();
	}
	
	public void writeToNBT(CompoundNBT nbt)
	{
		nbt.putUniqueId("Id", id);
		nbt.putLong("Capacity", capacity);
		nbt.putLong("Amount", amount);
		
		ListNBT list_gass = new ListNBT();
		for(GasStack stack : gass)
		{
			if(stack != null && !stack.isEmpty())
			{
				CompoundNBT nbtTags = new CompoundNBT();
				stack.write(nbtTags);
				list_gass.add(nbtTags);
			}
		}
		nbt.put("Stacks", list_gass);
		
	}
	
	public static GasDisk readFromNBT(CompoundNBT nbt)
	{
		if(nbt.hasUniqueId("Id"))
		{
			GasDisk disk = new GasDisk(nbt.getUniqueId("Id"), nbt.getInt("Capacity"));
			if(nbt.contains("Stacks"))
			{
				ListNBT disksTag = nbt.getList("Stacks", Constants.NBT.TAG_COMPOUND);
				disk.amount = 0;
				for (int i = 0; i < disksTag.size(); ++i)
				{
					GasStack stack = GasStack.readFromNBT(disksTag.getCompound(i));
					if(stack != null)
					{
						disk.gass.add(stack);
						disk.amount += stack.getAmount();
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
	
	public int getGasSize()
	{
		return gass.size();
	}
	
	public GasStack getChemicalInSlot(int index)
	{
		return gass.get(index);
	}
	
	public void setChemicalInSlot(int index, GasStack stack)
	{
		gass.set(index, stack);
	}
	
	public GasStack extractChemical(GasStack stack, Action action)
	{
		if(stack != null && !stack.isEmpty() && gass.size() > 0)
		{
			for(int index = 0; index < gass.size(); ++index)
			{
				GasStack gas = gass.get(index);
				if(gas != null && gas.isTypeEqual(stack.getType()))
				{
					long amntExtracted = Math.min(gas.getAmount(), stack.getAmount());
					if(amntExtracted > 0)
					{
						GasStack gasExtracted = gas.copy();
						gasExtracted.setAmount(amntExtracted);
						if(action == Action.EXECUTE)
						{
							if(amntExtracted >= gas.getAmount())
								gass.remove(index);
							else
								gas.shrink(amntExtracted);
							
							amount -= amntExtracted;
						}
						return gasExtracted;
					}
				}
			}
		}
		
		return GasStack.EMPTY;
	}
	
	public GasStack insertChemical(GasStack stack, Action action)
	{
		if(stack != null && !stack.isEmpty())
		{
			long space = getSpace();
			if(space >= stack.getAmount())
			{
				if(action == Action.EXECUTE)
				{
					boolean sucsess = false;
					for(int index = 0; index < gass.size(); ++index)
					{
						GasStack gas = gass.get(index);
						if(gas != null)
						{
							if(gas.isTypeEqual(stack.getType()))
							{
								gas.grow(stack.getAmount());
								sucsess = true;
								break;
							}
						}
					}
					
					if(!sucsess)
					{
						gass.add(stack.copy());
					}
					
					amount += stack.getAmount();
				}
				
				return GasStack.EMPTY;
			}
			else if(space > 0)
			{
				if(action == Action.EXECUTE)
				{
					boolean sucsess = false;
					for(int index = 0; index < gass.size(); ++index)
					{
						GasStack gas = gass.get(index);
						if(gas != null)
						{
							if(gas.isTypeEqual(stack.getType()))
							{
								gas.grow(space);
								sucsess = true;
								break;
							}
						}
					}
					
					if(!sucsess)
					{
						GasStack gas = stack.copy();
						gas.setAmount(space);
						gass.add(gas);
					}
					
					amount += space;
				}
				
				GasStack gasreturn = stack.copy();
				gasreturn.setAmount(stack.getAmount() - space);
				return gasreturn;
			}
		}
		return stack;
	}
	
	public List<GasStack> getGass()
	{
		return gass;
	}
	
}
