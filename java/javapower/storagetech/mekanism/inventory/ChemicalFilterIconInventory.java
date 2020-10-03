package javapower.storagetech.mekanism.inventory;

import javapower.storagetech.mekanism.item.ItemChemicalFilter;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ChemicalFilterIconInventory extends ChemicalInventory
{
	public ChemicalFilterIconInventory(ItemStack stack)
	{
        super(1, Integer.MAX_VALUE);

        this.addListener((handler, slot, reading) ->
        {
            if (!stack.hasTag())
                stack.setTag(new CompoundNBT());

            ItemChemicalFilter.setGasIcon(stack, getChemical(slot));
        });

        ChemicalStack<?> icon = ItemChemicalFilter.getGasIcon(stack);
        if (!icon.isEmpty())
            setChemical(0, icon);
    }
}
