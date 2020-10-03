package javapower.storagetech.api;

import net.minecraft.item.ItemStack;

public interface IItemProgressBarOverlay
{
	public float getOverlayBarValue(ItemStack stack);
	public default int getOverlayBarColor(ItemStack stack, float value)
	{
		return value >= 0.75f ? value >= 1 ? 0xffff0000 : 0xffffd800 : 0xff00eded;
	}
}
