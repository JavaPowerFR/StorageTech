package javapower.storagetech.api;

import javapower.storagetech.util.EPartType;
import net.minecraft.item.ItemStack;

public interface ICustomStoragePart
{
	public EPartType getType();
	public int getSize(ItemStack stack);
	public ItemStack createDisk(ItemStack stack);
	
}
