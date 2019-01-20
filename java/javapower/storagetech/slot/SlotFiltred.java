package javapower.storagetech.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotFiltred extends Slot
{
	IFilterStack filter;
	
	public SlotFiltred(IInventory inventoryIn, int index, int xPosition, int yPosition, IFilterStack _filter)
	{
		super(inventoryIn, index, xPosition, yPosition);
		filter = _filter;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return filter.canPutThisStack(stack);
	}

}
