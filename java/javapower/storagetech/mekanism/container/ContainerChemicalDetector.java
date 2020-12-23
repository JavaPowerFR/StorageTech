package javapower.storagetech.mekanism.container;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.inventory.ChemicalFilterSlot;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalDetector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerChemicalDetector extends BaseContainer2
{
	@ObjectHolder(StorageTech.MODID+":chemicaldetector")
    public static final ContainerType<ContainerChemicalDetector> CURRENT_CONTAINER = null;
	
    public ContainerChemicalDetector(TileEntityChemicalDetector detector, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, detector, player, windowId);
        
        addSlot(new ChemicalFilterSlot(detector.getNode().getChemicalFilters(), 0, 107, 20));
        addPlayerInventory(8, 55);
    }
    
}