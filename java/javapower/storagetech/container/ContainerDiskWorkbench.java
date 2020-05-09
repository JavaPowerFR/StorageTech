package javapower.storagetech.container;

import com.raoulvdberge.refinedstorage.RSItems;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.slot.IFilterStack;
import javapower.storagetech.slot.SlotFiltred;
import javapower.storagetech.slot.SlotOutput;
import javapower.storagetech.tileentity.TileEntityDiskWorkbench;
import javapower.storagetech.util.DiskUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerDiskWorkbench extends Container
{
	@ObjectHolder(StorageTech.MODID+":diskworkbench")
    public static final ContainerType<ContainerDiskWorkbench> CURRENT_CONTAINER = null;
	
	public TileEntityDiskWorkbench tile;
	PlayerInventory playerInventory;
	
	public ContainerDiskWorkbench(int windowId, PlayerInventory _playerInventory)
	{
		this(windowId, new TileEntityDiskWorkbench(), _playerInventory);
	}
	
	public ContainerDiskWorkbench(int windowId, TileEntityDiskWorkbench _tile, PlayerInventory _playerInventory)
	{
		super(CURRENT_CONTAINER, windowId);
		
		playerInventory = _playerInventory;
		tile = _tile;
		
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 107 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 165));
        }
        
        this.addSlot(new SlotFiltred(tile, 0, 8, 16, new IFilterStack()
        {
			@Override
			public boolean canPutThisStack(ItemStack stack)
			{
				if(stack != null && !stack.isEmpty())
					return DiskUtils.validItemPart(stack);
				
				return false;
			}
		}));
        
        this.addSlot(new SlotFiltred(tile, 1, 148, 16, new IFilterStack()
        {
			@Override
			public boolean canPutThisStack(ItemStack stack)
			{
				if(stack != null && !stack.isEmpty())
					return stack.isItemEqualIgnoreDurability(new ItemStack(RSItems.STORAGE_HOUSING));
				
				return false;
			}
		}));
        
        this.addSlot(new SlotOutput(tile, 2, 148, 66));
        
        for(int ids = 0; ids < 4; ++ids)
        {
        	this.addSlot(new SlotFiltred(tile, 3+ids, -17, 16+18*ids, new IFilterStack()
	        {
				@Override
				public boolean canPutThisStack(ItemStack stack)
				{
					if(stack != null && !stack.isEmpty())
						return stack.isItemEqual(new ItemStack(RSItems.SPEED_UPGRADE,1));
					
					return false;
				}
			})
	        {
	        	@Override
	        	public int getSlotStackLimit()
	        	{
	        		return 1;
	        	}
	        });
        }
	}
	
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
	{
		 ItemStack from = getSlot(index).getStack();
		 if(index >= 36 && index <= 42)
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
		 else if(DiskUtils.validItemPart(from))
		 {
			 Slot go = getSlot(36);
			 if(go.getStack().isEmpty() || DiskUtils.validItemPart(go.getStack()))
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
	
	public void addListener(IContainerListener listener)
	{
		super.addListener(listener);
		//listener.sendAllContents(this, this.tile);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return true;
	}

}
