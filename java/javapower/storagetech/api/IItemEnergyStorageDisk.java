package javapower.storagetech.api;

import java.util.UUID;

import net.minecraft.item.ItemStack;

public interface IItemEnergyStorageDisk
{
	public UUID getId(ItemStack stack);
	//public ItemStack[] getParts(ItemStack stack);
}
