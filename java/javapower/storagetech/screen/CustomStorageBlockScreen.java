package javapower.storagetech.screen;

import com.raoulvdberge.refinedstorage.screen.StorageScreen;

import javapower.storagetech.container.CustomStorageContainer;
import javapower.storagetech.tileentity.CustomStorageTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class CustomStorageBlockScreen extends StorageScreen<CustomStorageContainer>
{
    public CustomStorageBlockScreen(CustomStorageContainer container, PlayerInventory inventory, ITextComponent title)
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