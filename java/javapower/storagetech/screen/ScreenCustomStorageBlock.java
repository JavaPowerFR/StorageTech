package javapower.storagetech.screen;

import com.refinedmods.refinedstorage.screen.StorageScreen;

import javapower.storagetech.container.ContainerCustomStorage;
import javapower.storagetech.tileentity.CustomStorageTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenCustomStorageBlock extends StorageScreen<ContainerCustomStorage>
{
    public ScreenCustomStorageBlock(ContainerCustomStorage container, PlayerInventory inventory, ITextComponent title)
    {
        super(
            container,
            inventory,
            title,
            "gui/storage.png",
            null,
            CustomStorageTile.REDSTONE_MODE,
            CustomStorageTile.COMPARE,
            CustomStorageTile.WHITELIST_BLACKLIST,
            CustomStorageTile.PRIORITY,
            CustomStorageTile.ACCESS_TYPE,
            CustomStorageTile.STORED::getValue,
            () -> (long) ((CustomStorageTile) container.getTile()).storageCapacity
        );
    }
}