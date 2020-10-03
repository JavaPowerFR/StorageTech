package javapower.storagetech.mekanism.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class VirtualSlot extends Slot
{
	public VirtualSlot(int index, int xPosition, int yPosition)
	{
		super(null, index, xPosition, yPosition);
	}
	
	@Override
	public ItemStack getStack()
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public void putStack(ItemStack stack)
	{
		
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 64;
	}
	
	@Override
	public ItemStack decrStackSize(int amount)
	{
		return getStack();
	}
	
	@Override
	public void onSlotChanged()
	{
		
	}
	
	@Override
	public boolean isSameInventory(Slot other)
	{
		return other instanceof VirtualSlot;
	}
	
	@Override
	public boolean canTakeStack(PlayerEntity playerIn)
	{
		return false;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false;
	}

}
