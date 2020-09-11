package javapower.storagetech.screen;

import javapower.storagetech.container.ContainerPOEDrive;
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
            container.tileNode::getEnergyStored,
            container.tileNode::getEnergyCapacity,
            container.tileNode::getEnergyIOCapacity
        );
    }
}