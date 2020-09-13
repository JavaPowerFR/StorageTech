package javapower.storagetech.screen;

import javapower.storagetech.container.ContainerPOEDrive;
import javapower.storagetech.tileentity.TileEntityPOEDrive;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenPOEDrive extends ScreenNodeEnergyStorage<ContainerPOEDrive>
{
    public ScreenPOEDrive(ContainerPOEDrive container, PlayerInventory inventory, ITextComponent title)
    {
        super(
            container,
            inventory,
            title,
            "guis/poe_drive.png",
            TileEntityPOEDrive.REDSTONE_MODE,
            TileEntityPOEDrive.STORED::getValue,
            TileEntityPOEDrive.CAPACITY::getValue,
            TileEntityPOEDrive.IO_CAPACITY::getValue
        );
    }
}