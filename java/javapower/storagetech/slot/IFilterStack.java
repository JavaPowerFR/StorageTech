package javapower.storagetech.slot;

import net.minecraft.item.ItemStack;

public interface IFilterStack
{
	public boolean canPutThisStack(ItemStack stack);
}
