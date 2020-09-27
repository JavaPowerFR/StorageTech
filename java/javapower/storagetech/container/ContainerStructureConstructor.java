package javapower.storagetech.container;

import com.refinedmods.refinedstorage.container.BaseContainer;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityStructureConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerStructureConstructor extends BaseContainer
{
	@ObjectHolder(StorageTech.MODID+":structureconstructor")
    public static final ContainerType<ContainerStructureConstructor> CURRENT_CONTAINER = null;
	
	public TileEntityStructureConstructor tileNode;
	
    public ContainerStructureConstructor(TileEntityStructureConstructor _tileNode, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, _tileNode, player, windowId);
        tileNode = _tileNode;
        
        for (int i = 0; i < 4; ++i)
        {
            addSlot(new SlotItemHandler(tileNode.getNode().getUpgrades(), i, 227, 6 + (i * 18)));
        }
        addPlayerInventory(28, 140);
        
        transferManager.addBiTransfer(player.inventory, tileNode.getNode().getUpgrades());
    }
}