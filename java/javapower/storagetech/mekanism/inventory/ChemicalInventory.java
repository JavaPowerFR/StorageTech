package javapower.storagetech.mekanism.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.inventory.listener.InventoryListener;

import javapower.storagetech.mekanism.api.MekanismUtils;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.nbt.CompoundNBT;

public class ChemicalInventory
{
	private static final String NBT_SLOT = "Slot_%d";
	private static final String NBT_SLOT_TYPE = "SlotType_%d";

    private final List<InventoryListener<ChemicalInventory>> listeners = new ArrayList<>();

    private final ChemicalStack<?>[] chemicals;
    private final int maxAmount;
    private boolean empty = true;

    public ChemicalInventory(int size, int maxAmount)
    {
        this.chemicals = new ChemicalStack[size];

        for (int i = 0; i < size; ++i)
        	chemicals[i] = GasStack.EMPTY;

        this.maxAmount = maxAmount;
    }

    public ChemicalInventory(int size)
    {
        this(size, Integer.MAX_VALUE);
    }

    public ChemicalInventory addListener(InventoryListener<ChemicalInventory> listener)
    {
        listeners.add(listener);

        return this;
    }

    public int getSlots()
    {
        return chemicals.length;
    }

    public int getMaxAmount()
    {
        return maxAmount;
    }

    public ChemicalStack<?>[] getChemicals()
    {
        return chemicals;
    }

    @Nonnull
    public ChemicalStack<?> getChemical(int slot)
    {
        return chemicals[slot];
    }

    public void setChemical(int slot, @Nonnull ChemicalStack<?> stack)
    {
        if (stack.getAmount() > maxAmount)
            throw new IllegalArgumentException("Chemical size is invalid (given: " + stack.getAmount() + ", max size: " + maxAmount + ")");

        chemicals[slot] = stack;

        onChanged(slot);
    }

    public void onChanged(int slot)
    {
        listeners.forEach(l -> l.onChanged(this, slot, false));
        updateEmptyState();
    }

    public CompoundNBT writeToNbt()
    {
        CompoundNBT tag = new CompoundNBT();

        for (int i = 0; i < getSlots(); ++i)
        {
            ChemicalStack<?> stack = getChemical(i);

            if (!stack.isEmpty())
            {
            	tag.putByte(String.format(NBT_SLOT_TYPE, i), MekanismUtils.getChemicalTypeId(stack.getType()));
                    		
                tag.put(String.format(NBT_SLOT, i), stack.write(new CompoundNBT()));
            }
        }

        return tag;
    }

    public void readFromNbt(CompoundNBT tag)
    {
        for (int i = 0; i < getSlots(); ++i)
        {
        	String key_type = String.format(NBT_SLOT_TYPE, i);
        	String key = String.format(NBT_SLOT, i);
        	
            if (tag.contains(key_type) && tag.contains(key))
            	chemicals[i] = MekanismUtils.buildChemicalStackById(tag.getByte(key_type), tag.getCompound(key));
        }

        updateEmptyState();
    }

    private void updateEmptyState()
    {
        this.empty = true;

        for (ChemicalStack<?> chemical : chemicals)
        {
            if (!chemical.isEmpty())
            {
                this.empty = false;
                return;
            }
        }
    }

    public boolean isEmpty()
    {
        return empty;
    }
}
