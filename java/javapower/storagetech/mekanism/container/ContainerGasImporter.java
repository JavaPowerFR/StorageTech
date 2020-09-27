package javapower.storagetech.mekanism.container;

import java.util.ArrayList;
import java.util.List;

import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.container.BaseContainer;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.api.MekanismUtils;
import javapower.storagetech.mekanism.inventory.GasFilterSlot;
import javapower.storagetech.mekanism.packet.PacketGasFilterSlotUpdateMessage;
import javapower.storagetech.mekanism.tileentity.TileEntityGasImporter;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerGasImporter extends BaseContainer
{
	@ObjectHolder(StorageTech.MODID+":gasimporter")
    public static final ContainerType<ContainerGasImporter> CURRENT_CONTAINER = null;
	
	private final List<GasFilterSlot> gasSlots = new ArrayList<GasFilterSlot>();
	private final List<GasStack> gass = new ArrayList<GasStack>();
	
    public ContainerGasImporter(TileEntityGasImporter importer, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, importer, player, windowId);

        for (int i = 0; i < 4; ++i)
        {
            addSlot(new SlotItemHandler(importer.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i)
        {
            addSlot(new GasFilterSlot(importer.getNode().getGasFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, importer.getNode().getUpgrades());
        
        //transferManager.addBiTransfer(player.inventory, importer.getNode().getGasFilters());
    }
    
    public List<GasFilterSlot> getGasSlots()
    {
		return gasSlots;
	}
    
    @Override
    public ItemStack slotClick(int id, int dragType, ClickType clickType, PlayerEntity player)
    {
    	Slot slot = id >= 0 ? getSlot(id) : null;

        int disabledSlotNumber = getDisabledSlotNumber();

        // Prevent swapping disabled held item with the number keys (dragType is the slot we're swapping with)
        if (disabledSlotNumber != -1 && clickType == ClickType.SWAP && dragType == disabledSlotNumber)
            return ItemStack.EMPTY;
        
        if(slot instanceof GasFilterSlot)
        {
        	if(((GasFilterSlot)slot).isSizeAllowed())
        	{
        		if (clickType == ClickType.QUICK_MOVE)
				{
                    ((GasFilterSlot) slot).onContainerClicked(ItemStack.EMPTY);
                }
				else if (!player.inventory.getItemStack().isEmpty())
				{
                    ((GasFilterSlot) slot).onContainerClicked(player.inventory.getItemStack());
                }
        	}
        	else if (player.inventory.getItemStack().isEmpty())
			{
                ((GasFilterSlot) slot).onContainerClicked(ItemStack.EMPTY);
            }
			else
			{
                ((GasFilterSlot) slot).onContainerClicked(player.inventory.getItemStack());
            }

            return player.inventory.getItemStack();
        }
        
    	return super.slotClick(id, dragType, clickType, player);
    }
    
    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot)
    {
    	if(slot instanceof GasFilterSlot)
    		return false;
    	
    	return super.canMergeSlot(stack, slot);
    }
    
    @Override
    protected Slot addSlot(Slot slot)
    {
    	if(slot instanceof GasFilterSlot)
    	{
    		gass.add(GasStack.EMPTY);
    		gasSlots.add((GasFilterSlot) slot);
    	}
    	
    	return super.addSlot(slot);
    }
    
    @Override
    public void detectAndSendChanges()
    {
    	super.detectAndSendChanges();
    	
    	if (this.getPlayer() instanceof ServerPlayerEntity)
    	{
    		for (int i = 0; i < this.gasSlots.size(); ++i)
    		{
    			GasFilterSlot slot = this.gasSlots.get(i);
    			
    			GasStack cached = this.gass.get(i);
    			GasStack actual = slot.getGasInventory().getGas(slot.getSlotIndex());
    			
    			if(!MekanismUtils.isEqual(cached, actual, IComparer.COMPARE_QUANTITY))
    			{
    				this.gass.set(i, actual.copy());
    				StorageTech.sendTo((ServerPlayerEntity) getPlayer(), new PacketGasFilterSlotUpdateMessage(slot.slotNumber, actual));
    			}
    		}
    	}
    }
}