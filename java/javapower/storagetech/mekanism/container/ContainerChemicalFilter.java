package javapower.storagetech.mekanism.container;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.inventory.ChemicalFilterIconInventory;
import javapower.storagetech.mekanism.inventory.ChemicalFilterInventory;
import javapower.storagetech.mekanism.inventory.ChemicalFilterSlot;
import javapower.storagetech.mekanism.inventory.ChemicalInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerChemicalFilter extends BaseContainer2
{
	
	@ObjectHolder(StorageTech.MODID+":chemicalfilter")
    public static final ContainerType<ContainerChemicalFilter> CURRENT_CONTAINER = null;
	
    private final ItemStack stack;

    public ContainerChemicalFilter(PlayerEntity player, ItemStack stack, int windowId)
    {
        super(CURRENT_CONTAINER, null, player, windowId);

        this.stack = stack;

        int y = 20;
        int x = 8;
        
        ChemicalInventory chemicalFilter = new ChemicalFilterInventory(stack);

        for (int i = 0; i < 27; ++i)
        {
            addSlot(new ChemicalFilterSlot(chemicalFilter, i, x, y));

            if ((i + 1) % 9 == 0)
            {
                x = 8;
                y += 18;
            }
            else
            {
                x += 18;
            }
        }
        
        addSlot(new ChemicalFilterSlot(new ChemicalFilterIconInventory(stack), 0, 8, 117));

        addPlayerInventory(8, 149);
    }

    public ItemStack getStack()
    {
        return stack;
    }

    @Override
    protected int getDisabledSlotNumber()
    {
        return getPlayer().inventory.currentItem;
    }
}