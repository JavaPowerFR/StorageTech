package javapower.storagetech.container;

import com.refinedmods.refinedstorage.container.BaseContainer;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityPOEDrive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerPOEDrive extends BaseContainer
{
	@ObjectHolder(StorageTech.MODID+":poedrive")
    public static final ContainerType<ContainerPOEDrive> CURRENT_CONTAINER = null;
	
	public TileEntityPOEDrive tileNode;
	
    public ContainerPOEDrive(TileEntityPOEDrive _tileNode, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, _tileNode, player, windowId);
        tileNode = _tileNode;
        int x = 80;
        int y = 38;

        for (int i = 0; i < 8; ++i)
        {
            addSlot(new SlotItemHandler(tileNode.getNode().getEnergyCells(), i, x + ((i % 2) * 18), y + Math.floorDiv(i, 2) * 18));
        }

        addPlayerInventory(8, 125);
        
        transferManager.addBiTransfer(player.inventory, tileNode.getNode().getEnergyCells());
    }
}