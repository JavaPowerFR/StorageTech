package javapower.storagetech.mekanism.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.inventory.listener.InventoryListener;

import mekanism.api.chemical.gas.GasStack;
import net.minecraft.nbt.CompoundNBT;

public class GasInventory
{
	private static final String NBT_SLOT = "Slot_%d";

    private final List<InventoryListener<GasInventory>> listeners = new ArrayList<>();

    private final GasStack[] gass;
    private final int maxAmount;
    private boolean empty = true;

    public GasInventory(int size, int maxAmount)
    {
        this.gass = new GasStack[size];

        for (int i = 0; i < size; ++i)
            gass[i] = GasStack.EMPTY;

        this.maxAmount = maxAmount;
    }

    public GasInventory(int size)
    {
        this(size, Integer.MAX_VALUE);
    }

    public GasInventory addListener(InventoryListener<GasInventory> listener)
    {
        listeners.add(listener);

        return this;
    }

    public int getSlots()
    {
        return gass.length;
    }

    public int getMaxAmount()
    {
        return maxAmount;
    }

    public GasStack[] getGass()
    {
        return gass;
    }

    @Nonnull
    public GasStack getGas(int slot)
    {
        return gass[slot];
    }

    public void setGas(int slot, @Nonnull GasStack stack)
    {
        if (stack.getAmount() > maxAmount)
            throw new IllegalArgumentException("Gas size is invalid (given: " + stack.getAmount() + ", max size: " + maxAmount + ")");

        gass[slot] = stack;

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
            GasStack stack = getGas(i);

            if (!stack.isEmpty())
                tag.put(String.format(NBT_SLOT, i), stack.write(new CompoundNBT()));
        }

        return tag;
    }

    public void readFromNbt(CompoundNBT tag)
    {
        for (int i = 0; i < getSlots(); ++i)
        {
            String key = String.format(NBT_SLOT, i);

            if (tag.contains(key))
                gass[i] = GasStack.readFromNBT(tag.getCompound(key));
        }

        updateEmptyState();
    }

    private void updateEmptyState()
    {
        this.empty = true;

        for (GasStack gas : gass)
        {
            if (!gas.isEmpty())
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
