package javapower.storagetech.mekanism.inventory;

import javapower.storagetech.mekanism.item.ItemChemicalFilter;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class ChemicalFilterInventory extends ChemicalInventory
{

	public ChemicalFilterInventory(ItemStack stack)
	{
		super(27, Integer.MAX_VALUE);
		
		this.addListener((handler, slot, reading) ->
		{
			if (!stack.hasTag())
                stack.setTag(new CompoundNBT());
			
			stack.getTag().put(ItemChemicalFilter.NBT_CHEMICAL_FILTERS, writeToNbt());
			
		});
		
		if (stack.hasTag() && stack.getTag().contains(ItemChemicalFilter.NBT_CHEMICAL_FILTERS))
		{
            readFromNbt(stack.getTag().getCompound(ItemChemicalFilter.NBT_CHEMICAL_FILTERS));
        }
	}
	
	public NonNullList<ChemicalStack<?>> getFilteredGass()
	{
        NonNullList<ChemicalStack<?>> list = NonNullList.create();

        for (ChemicalStack<?> chemical : this.getChemicals())
        {
            if (chemical != null)
                list.add(chemical);
        }
        
        return list;
	}
	
}
