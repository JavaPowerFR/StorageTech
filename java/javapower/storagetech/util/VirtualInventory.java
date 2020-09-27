package javapower.storagetech.util;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class VirtualInventory implements IInventory
{
	Supplier<ItemStack> slot;
	
	
	public VirtualInventory(Supplier<ItemStack> _slot)
	{
		slot = _slot;
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return slot.get();
	}
	@Override
	public void clear() {}

	@Override
	public int getSizeInventory() {return 1;}

	@Override
	public boolean isEmpty() {return slot.get().isEmpty();}

	@Override
	public ItemStack decrStackSize(int index, int count) {return null;}

	@Override
	public ItemStack removeStackFromSlot(int index) {return null;}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {return true;}

	@Override
	public void markDirty(){}
	
}
