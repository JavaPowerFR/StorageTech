package javapower.storagetech.container;

import com.refinedmods.refinedstorage.container.BaseContainer;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityPOEImporter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerPOEImporter extends BaseContainer
{
	@ObjectHolder(StorageTech.MODID+":poeimporter")
    public static final ContainerType<ContainerPOEImporter> CURRENT_CONTAINER = null;
	public TileEntityPOEImporter importer;
	
    public ContainerPOEImporter(TileEntityPOEImporter _importer, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, _importer, player, windowId);
        importer = _importer;
        
        for (int i = 0; i < 4; ++i)
            addSlot(new SlotItemHandler(importer.getNode().getUpgrades(), i, 53 + (i * 18), 39));
        
        addPlayerInventory(8, 108);
        
        transferManager.addBiTransfer(player.inventory, importer.getNode().getUpgrades());
        
    }
    
    
}