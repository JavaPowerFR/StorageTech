package javapower.storagetech.container;

import com.refinedmods.refinedstorage.container.BaseContainer;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityPOEExporter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerPOEExporter extends BaseContainer
{
	@ObjectHolder(StorageTech.MODID+":poeexporter")
    public static final ContainerType<ContainerPOEExporter> CURRENT_CONTAINER = null;
	public TileEntityPOEExporter exporter;
	
    public ContainerPOEExporter(TileEntityPOEExporter _exporter, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, _exporter, player, windowId);
        exporter = _exporter;

        for (int i = 0; i < 4; ++i)
            addSlot(new SlotItemHandler(exporter.getNode().getUpgrades(), i, 53 + (i * 18), 39));
        
        addPlayerInventory(8, 108);
        
        transferManager.addBiTransfer(player.inventory, exporter.getNode().getUpgrades());
        
    }
    
    
}