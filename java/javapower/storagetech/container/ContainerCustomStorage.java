package javapower.storagetech.container;

import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityCustomStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerCustomStorage extends BaseContainer
{
	@ObjectHolder(StorageTech.MODID+":customstorageblock")
    public static final ContainerType<ContainerCustomStorage> CURRENT_CONTAINER = null;
	
    public ContainerCustomStorage(TileEntityCustomStorage storage, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, storage, player, windowId);

        for (int i = 0; i < 9; ++i)
        {
            addSlot(new FilterSlot(storage.getNode().getFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 141);

        transferManager.addItemFilterTransfer(player.inventory, storage.getNode().getFilters());
    }
}