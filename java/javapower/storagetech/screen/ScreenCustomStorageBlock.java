package javapower.storagetech.screen;

import com.refinedmods.refinedstorage.screen.StorageScreen;

import javapower.storagetech.container.ContainerCustomStorage;
import javapower.storagetech.tileentity.TileEntityCustomStorage;
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
            TileEntityCustomStorage.REDSTONE_MODE,
            TileEntityCustomStorage.COMPARE,
            TileEntityCustomStorage.WHITELIST_BLACKLIST,
            TileEntityCustomStorage.PRIORITY,
            TileEntityCustomStorage.ACCESS_TYPE,
            TileEntityCustomStorage.STORED::getValue,
            TileEntityCustomStorage.CAPACITY::getValue
        );
    }
}