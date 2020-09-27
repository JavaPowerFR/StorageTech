package javapower.storagetech.slot;

import java.util.function.Supplier;

import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotItemStack extends Slot
{
	Supplier<ItemStack> stack;
	
	public SlotItemStack(Supplier<ItemStack> _stack, int xPosition, int yPosition)
	{
		super(null, 0, xPosition, yPosition);
		stack = _stack;
	}
	
	@Override
	public ItemStack getStack()
	{
		return stack.get();
	}
	
	@Override
	public void putStack(ItemStack stack)
	{
		
	}
	
	@Override
	public void onSlotChanged()
	{
		
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return stack.get().getMaxStackSize();
	}
	
	@Override
	public ItemStack decrStackSize(int amount)
	{
		return stack.get();
	}
	
	@Override
	public boolean isSameInventory(Slot other)
	{
		return true;
	}

}
