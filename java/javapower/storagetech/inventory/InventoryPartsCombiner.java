package javapower.storagetech.inventory;

import java.util.function.Function;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.StackUtils;

import javapower.storagetech.eventio.IEventVoid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryPartsCombiner
{
	private ItemStackHandler inventory = new ItemStackHandler(3)
	{

		public boolean isItemValid(int slot, ItemStack stack)
		{
			if(slot == 2)
				return false;
			
			return checker.apply(stack);
		};
		
		protected void onContentsChanged(int slot)
		{
			markDirty.event();
		};

	};
	
	private ItemStackHandler upgrades = new ItemStackHandler(4)
	{
		public boolean isItemValid(int slot, ItemStack stack)
		{
			return stack.getItem().equals(RSItems.UPGRADE_ITEMS.get(UpgradeItem.Type.SPEED).get());
		};
		
		protected void onContentsChanged(int slot)
		{
			upgradesCount = 0;
			
			for(int id = 0; id < 4; ++id)
			{
				if(!upgrades.getStackInSlot(id).isEmpty())
					++upgradesCount;
			}
			
			markDirty.event();
			
		};
		
		public int getSlotLimit(int slot)
		{
			return 1;
		};

	};
			
	private final LazyOptional<IItemHandler> itemsCapability = LazyOptional.of(() -> inventory);
	
	IEventVoid markDirty;
	Function<ItemStack, Boolean> checker;
	
	int upgradesCount = 0;
	
	public InventoryPartsCombiner(IEventVoid _markDirty, Function<ItemStack, Boolean> _checker)
	{
		markDirty = _markDirty;
		checker = _checker;
	}
	
	public void read(CompoundNBT tag)
	{
		if(tag.contains("upgradesCount"))
			upgradesCount = tag.getInt("upgradesCount");
		
		StackUtils.readItems(inventory, 0, tag);
		StackUtils.readItems(upgrades, 1, tag);
		
	}
	
	public void write(CompoundNBT tag)
	{
		StackUtils.writeItems(inventory, 0, tag);
		StackUtils.writeItems(upgrades, 1, tag);
		
		tag.putInt("upgradesCount", upgradesCount);
	}
	
	public LazyOptional<IItemHandler> getItemsCapability()
	{
		return itemsCapability;
	}
	
	public ItemStackHandler getInventory()
	{
		return inventory;
	}
	
	public ItemStackHandler getUpgrades()
	{
		return upgrades;
	}
	
	public int getUpgradesCount()
	{
		return upgradesCount;
	}

	public int getUpgradesStep()
	{
		return (int) Math.pow(3, upgradesCount);
	}

}
