package javapower.storagetech.mekanism.container;

import com.refinedmods.refinedstorage.container.BaseContainer;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.tileentity.TileEntityGasDrive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerGasDrive extends BaseContainer
{
	@ObjectHolder(StorageTech.MODID+":gasdrive")
    public static final ContainerType<ContainerGasDrive> CURRENT_CONTAINER = null;
	
	public TileEntityGasDrive tileNode;
	
    public ContainerGasDrive(TileEntityGasDrive _tileNode, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, _tileNode, player, windowId);
        tileNode = _tileNode;
        int x = 80;
        int y = 38;

        for (int i = 0; i < 8; ++i)
        {
            addSlot(new SlotItemHandler(tileNode.getNode().getDisksHandler(), i, x + ((i % 2) * 18), y + Math.floorDiv(i, 2) * 18));
        }

        addPlayerInventory(8, 125);
        
        transferManager.addBiTransfer(player.inventory, tileNode.getNode().getDisksHandler());
    }
}