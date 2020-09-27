package javapower.storagetech.mekanism.inventory;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.container.slot.BaseSlot;

import javapower.storagetech.mekanism.api.MekanismUtils;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class GasFilterSlot extends BaseSlot
{
	public static final int FILTER_ALLOW_SIZE = 1;
    public static final int FILTER_ALLOW_ALTERNATIVES = 2;

    private final int flags;
    private final GasInventory gasInventory;
    private final int index;

    public GasFilterSlot(GasInventory inventory, int inventoryIndex, int x, int y, int flags)
    {
        super(new ItemStackHandler(inventory.getSlots()), inventoryIndex, x, y);

        this.flags = flags;
        this.gasInventory = inventory;
        this.index = inventoryIndex;
    }

    public GasFilterSlot(GasInventory inventory, int inventoryIndex, int x, int y)
    {
        this(inventory, inventoryIndex, x, y, 0);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
    	//onContainerClicked(stack);
        return false;
    }

    public void onContainerClicked(@Nonnull ItemStack stack)
    {
        gasInventory.setGas(getSlotIndex(), MekanismUtils.getGas(stack, true));
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn)
    {
    	/*if(playerIn.inventory.getItemStack() == ItemStack.EMPTY)
    		onContainerClicked(ItemStack.EMPTY);*/
        return false;
    }

    public boolean isSizeAllowed()
    {
        return (flags & FILTER_ALLOW_SIZE) == FILTER_ALLOW_SIZE;
    }

    public boolean isAlternativesAllowed()
    {
        return (flags & FILTER_ALLOW_ALTERNATIVES) == FILTER_ALLOW_ALTERNATIVES;
    }

    public GasInventory getGasInventory()
    {
        return gasInventory;
    }
    
    public GasStack getGasInSlot()
    {
    	return gasInventory.getGas(index);
    }
}
