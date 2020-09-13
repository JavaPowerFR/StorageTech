package javapower.storagetech.screen;

import com.refinedmods.refinedstorage.screen.StorageScreen;

import javapower.storagetech.container.ContainerCustomFluidStorage;
import javapower.storagetech.tileentity.TileEntityCustomFluidStorage;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenCustomFluidStorageBlock extends StorageScreen<ContainerCustomFluidStorage>
{
    public ScreenCustomFluidStorageBlock(ContainerCustomFluidStorage container, PlayerInventory inventory, ITextComponent title)
    {
        super(
            container,
            inventory,
            title,
            "gui/storage.png",
            null,
            TileEntityCustomFluidStorage.REDSTONE_MODE,
            TileEntityCustomFluidStorage.COMPARE,
            TileEntityCustomFluidStorage.WHITELIST_BLACKLIST,
            TileEntityCustomFluidStorage.PRIORITY,
            TileEntityCustomFluidStorage.ACCESS_TYPE,
            TileEntityCustomFluidStorage.STORED::getValue,
            TileEntityCustomFluidStorage.CAPACITY::getValue
        );
    }
}