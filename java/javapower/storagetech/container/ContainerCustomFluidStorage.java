package javapower.storagetech.container;

import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityCustomFluidStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerCustomFluidStorage extends BaseContainer
{
	@ObjectHolder(StorageTech.MODID+":customfluidstorageblock")
    public static final ContainerType<ContainerCustomFluidStorage> CURRENT_CONTAINER = null;
	
    public ContainerCustomFluidStorage(TileEntityCustomFluidStorage storage, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, storage, player, windowId);

        for (int i = 0; i < 9; ++i)
            addSlot(new FluidFilterSlot(storage.getNode().getFilters(), i, 8 + (18 * i), 20));

        addPlayerInventory(8, 141);
        
        transferManager.addFluidFilterTransfer(player.inventory, storage.getNode().getFilters());
    }
}