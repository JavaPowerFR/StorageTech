package javapower.storagetech.node;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskListener;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.util.StackUtils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class CustomFluidStorageWrapperStorageDisk implements IStorageDisk<FluidStack>
{
    private final NetworkNodeCustomFluidStorage storage;
    private final IStorageDisk<FluidStack> parent;

    public CustomFluidStorageWrapperStorageDisk(NetworkNodeCustomFluidStorage storage, IStorageDisk<FluidStack> parent)
    {
        this.storage = storage;
        this.parent = parent;
        this.setSettings(null, storage);
    }

    @Override
    public int getPriority()
    {
        return storage.getPriority();
    }

    @Override
    public AccessType getAccessType()
    {
        return parent.getAccessType();
    }

    @Override
    public Collection<FluidStack> getStacks()
    {
        return parent.getStacks();
    }

    @Override
    @Nonnull
    public FluidStack insert(@Nonnull FluidStack stack, int size, Action action)
    {
        if (!IWhitelistBlacklist.acceptsFluid(storage.getFilters(), storage.getWhitelistBlacklistMode(), storage.getCompare(), stack))
        {
            return StackUtils.copy(stack, size);
        }
        
        return parent.insert(stack, size, action);
    }

    @Override
    @Nonnull
    public FluidStack extract(@Nonnull FluidStack stack, int size, int flags, Action action)
    {
        return parent.extract(stack, size, flags, action);
    }

    @Override
    public int getStored()
    {
        return parent.getStored();
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable FluidStack remainder)
    {
        return parent.getCacheDelta(storedPreInsertion, size, remainder);
    }

    @Override
    public int getCapacity()
    {
        return parent.getCapacity();
    }

    @Override
    public void setSettings(@Nullable IStorageDiskListener listener, IStorageDiskContainerContext context)
    {
        parent.setSettings(listener, context);
    }

    @Override
    public CompoundNBT writeToNbt()
    {
        return parent.writeToNbt();
    }

    @Override
    public ResourceLocation getFactoryId()
    {
        return parent.getFactoryId();
    }
}