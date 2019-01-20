package javapower.storagetech.container;

import com.raoulvdberge.refinedstorage.RSItems;

import javapower.storagetech.item.STItems;
import javapower.storagetech.slot.IFilterStack;
import javapower.storagetech.slot.SlotFiltred;
import javapower.storagetech.slot.SlotOutput;
import javapower.storagetech.tileentity.TileEntityDiskWorkbench;
import javapower.storagetech.util.DiskUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDiskWorkbench extends Container
{
	IInventory block_inv;
	InventoryPlayer playerInventory;
	
	public ContainerDiskWorkbench(TileEntityDiskWorkbench tile, EntityPlayer player)
	{
		playerInventory = player.inventory;
		block_inv = tile;
		
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 107 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 165));
        }
        
        this.addSlotToContainer(new SlotFiltred(block_inv, 0, 8, 16, new IFilterStack()
        {
			@Override
			public boolean canPutThisStack(ItemStack stack)
			{
				if(stack != null && !stack.isEmpty())
					return DiskUtils.validItemDisk(stack);
					//return stack.getItem().equals(RSItems.STORAGE_PART) || (stack.getItem().equals(STItems.item_memory) && stack.getItemDamage() == 0);
				
				return false;
			}
		}));
        
        this.addSlotToContainer(new SlotFiltred(block_inv, 1, 148, 16, new IFilterStack()
        {
			@Override
			public boolean canPutThisStack(ItemStack stack)
			{
				if(stack != null && !stack.isEmpty())
					return stack.isItemEqualIgnoreDurability(new ItemStack(RSItems.STORAGE_HOUSING));
				
				return false;
			}
		}));
        
        this.addSlotToContainer(new SlotOutput(block_inv, 2, 148, 66));
	}
	
	 public void addListener(IContainerListener listener)
	 {
	        super.addListener(listener);
	        listener.sendAllWindowProperties(this, this.block_inv);
	 }
	 
	 @Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		 ItemStack from = getSlot(index).getStack();
		 if(index >= 36 && index <= 38)
		 {
			 for(int id_slot = 0; id_slot <= 35; ++id_slot)
			 {
				 if(from.isEmpty())
					 break;
				 Slot go = getSlot(id_slot);
				 
				 if(go.getStack().isEmpty())
				 {
					 go.putStack(from.copy());
					 from.setCount(0);
				 }
				 else if(go.getStack().getItem().equals(from.getItem()))
				 {
					 int capacity = go.getStack().getMaxStackSize() - go.getStack().getCount();
					 if(capacity >= from.getCount())
					 {
						 go.getStack().setCount(go.getStack().getCount() + from.getCount());
						 from.setCount(0);
					 }
					 else
					 {
						 go.getStack().setCount(go.getStack().getCount() + capacity);
						 from.setCount(from.getCount() - capacity);
					 }
				 }
			 }
		 }
		 else if(from.getItem().equals(RSItems.STORAGE_HOUSING))
		 {
			 Slot go = getSlot(37);
			 int total = go.getStack().getCount() + from.getCount();
			 if(total <= go.getStack().getMaxStackSize())
			 {
				 if(go.getStack().isEmpty())
				 {
					 go.putStack(from.copy());
					 from.setCount(0);
				 }
				 else
				 {
					 go.getStack().setCount(go.getStack().getCount() + from.getCount());
					 from.setCount(0);
				 }
			 }
			 else
			 {
				 int putitemscount = go.getStack().getMaxStackSize() - go.getStack().getCount();
				 go.getStack().setCount(go.getStack().getCount() + putitemscount);
				 from.setCount(from.getCount() - putitemscount);
			 }
		 }
		 else if(DiskUtils.validItemDisk(from))
		 {
			 Slot go = getSlot(36);
			 if(go.getStack().isEmpty() || DiskUtils.validItemDisk(go.getStack()))
			 {
				 int total = go.getStack().getCount() + from.getCount();
				 if(total <= go.getStack().getMaxStackSize())
				 {
					 if(go.getStack().isEmpty())
					 {
						 go.putStack(from.copy());
						 from.setCount(0);
					 }
					 else
					 {
						 go.getStack().setCount(go.getStack().getCount() + from.getCount());
						 from.setCount(0);
					 }
				 }
				 else
				 {
					 int putitemscount = go.getStack().getMaxStackSize() - go.getStack().getCount();
					 go.getStack().setCount(go.getStack().getCount() + putitemscount);
					 from.setCount(from.getCount() - putitemscount);
				 }
			 }
		 }
		 
		 return ItemStack.EMPTY;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}
	
	@Override
	public void putStackInSlot(int slotID, ItemStack stack)
	{
		super.putStackInSlot(slotID, stack);
	}

}
