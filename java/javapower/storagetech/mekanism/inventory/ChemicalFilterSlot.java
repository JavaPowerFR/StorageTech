package javapower.storagetech.mekanism.inventory;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.container.slot.BaseSlot;

import javapower.storagetech.mekanism.api.MekanismUtils;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ChemicalFilterSlot extends BaseSlot
{
	public static final int FILTER_ALLOW_SIZE = 1;
    public static final int FILTER_ALLOW_ALTERNATIVES = 2;

    private final int flags;
    private final ChemicalInventory gasInventory;
    private final int index;

    public ChemicalFilterSlot(ChemicalInventory inventory, int inventoryIndex, int x, int y, int flags)
    {
        super(new ItemStackHandler(inventory.getSlots()), inventoryIndex, x, y);

        this.flags = flags;
        this.gasInventory = inventory;
        this.index = inventoryIndex;
    }

    public ChemicalFilterSlot(ChemicalInventory inventory, int inventoryIndex, int x, int y)
    {
        this(inventory, inventoryIndex, x, y, 0);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
        return false;
    }

    public void onContainerClicked(@Nonnull ItemStack stack)
    {
        gasInventory.setChemical(getSlotIndex(), MekanismUtils.getChemical(stack, true));
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn)
    {
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

    public ChemicalInventory getChemicalInventory()
    {
        return gasInventory;
    }
    
    public ChemicalStack<?> getChemicalInSlot()
    {
    	return gasInventory.getChemical(index);
    }
}
