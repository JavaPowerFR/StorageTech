package javapower.storagetech.mekanism.container;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.inventory.ChemicalFilterSlot;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalExporter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerChemicalExporter extends BaseContainer2
{
	@ObjectHolder(StorageTech.MODID+":chemicalexporter")
    public static final ContainerType<ContainerChemicalExporter> CURRENT_CONTAINER = null;
	
    public ContainerChemicalExporter(TileEntityChemicalExporter exporter, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, exporter, player, windowId);

        for (int i = 0; i < 4; ++i)
        {
            addSlot(new SlotItemHandler(exporter.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i)
        {
            addSlot(new ChemicalFilterSlot(exporter.getNode().getChemicalFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, exporter.getNode().getUpgrades());
        
        //transferManager.addBiTransfer(player.inventory, exporter.getNode().getGasFilters());
    }
}