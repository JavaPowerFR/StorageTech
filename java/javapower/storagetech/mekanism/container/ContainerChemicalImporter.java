package javapower.storagetech.mekanism.container;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.inventory.ChemicalFilterSlot;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalImporter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerChemicalImporter extends BaseContainer2
{
	@ObjectHolder(StorageTech.MODID+":chemicalimporter")
    public static final ContainerType<ContainerChemicalImporter> CURRENT_CONTAINER = null;
	
    public ContainerChemicalImporter(TileEntityChemicalImporter importer, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, importer, player, windowId);

        for (int i = 0; i < 4; ++i)
        {
            addSlot(new SlotItemHandler(importer.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i)
        {
            addSlot(new ChemicalFilterSlot(importer.getNode().getChemicalFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, importer.getNode().getUpgrades());
        
        //transferManager.addBiTransfer(player.inventory, importer.getNode().getGasFilters());
    }
    
}