package javapower.storagetech.mekanism.container;

import java.util.ArrayList;
import java.util.List;

import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.tile.BaseTile;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.api.MekanismUtils;
import javapower.storagetech.mekanism.inventory.ChemicalFilterSlot;
import javapower.storagetech.mekanism.packet.PacketChemicalFilterSlotUpdateMessage;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
/**
 * 
 * Allow GasStack implementation 
 *
 */
public abstract class BaseContainer2 extends BaseContainer
{

	public BaseContainer2(ContainerType<?> type, BaseTile tile, PlayerEntity player, int windowId)
	{
		super(type, tile, player, windowId);
	}

	private final List<ChemicalFilterSlot> chemicalSlots = new ArrayList<ChemicalFilterSlot>();
	private final List<ChemicalStack<?>> chemicals = new ArrayList<ChemicalStack<?>>();
	
	public List<ChemicalFilterSlot> getChemicalSlots()
    {
		return chemicalSlots;
	}
    
    @Override
    public ItemStack slotClick(int id, int dragType, ClickType clickType, PlayerEntity player)
    {
    	Slot slot = id >= 0 ? getSlot(id) : null;

        int disabledSlotNumber = getDisabledSlotNumber();

        // Prevent swapping disabled held item with the number keys (dragType is the slot we're swapping with)
        if (disabledSlotNumber != -1 && clickType == ClickType.SWAP && dragType == disabledSlotNumber)
            return ItemStack.EMPTY;
        
        if(slot instanceof ChemicalFilterSlot)
        {
        	if(((ChemicalFilterSlot)slot).isSizeAllowed())
        	{
        		if (clickType == ClickType.QUICK_MOVE)
				{
                    ((ChemicalFilterSlot) slot).onContainerClicked(ItemStack.EMPTY);
                }
				else if (!player.inventory.getItemStack().isEmpty())
				{
                    ((ChemicalFilterSlot) slot).onContainerClicked(player.inventory.getItemStack());
                }
        	}
        	else if (player.inventory.getItemStack().isEmpty())
			{
                ((ChemicalFilterSlot) slot).onContainerClicked(ItemStack.EMPTY);
            }
			else
			{
                ((ChemicalFilterSlot) slot).onContainerClicked(player.inventory.getItemStack());
            }

            return player.inventory.getItemStack();
        }
        
    	return super.slotClick(id, dragType, clickType, player);
    }
    
    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot)
    {
    	if(slot instanceof ChemicalFilterSlot)
    		return false;
    	
    	return super.canMergeSlot(stack, slot);
    }
    
    @Override
    protected Slot addSlot(Slot slot)
    {
    	if(slot instanceof ChemicalFilterSlot)
    	{
    		chemicals.add(GasStack.EMPTY);
    		chemicalSlots.add((ChemicalFilterSlot) slot);
    	}
    	
    	return super.addSlot(slot);
    }
    
    @Override
    public void detectAndSendChanges()
    {
    	super.detectAndSendChanges();
    	
    	if (this.getPlayer() instanceof ServerPlayerEntity)
    	{
    		for (int i = 0; i < this.chemicalSlots.size(); ++i)
    		{
    			ChemicalFilterSlot slot = this.chemicalSlots.get(i);
    			
    			ChemicalStack<?> cached = this.chemicals.get(i);
    			ChemicalStack<?> actual = slot.getChemicalInventory().getChemical(slot.getSlotIndex());
    			
    			if(!MekanismUtils.isEqual(cached, actual, IComparer.COMPARE_QUANTITY))
    			{
    				this.chemicals.set(i, actual.copy());
    				StorageTech.sendTo((ServerPlayerEntity) getPlayer(), new PacketChemicalFilterSlotUpdateMessage(slot.slotNumber, actual));
    			}
    		}
    	}
    }
}
