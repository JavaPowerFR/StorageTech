package javapower.storagetech.mekanism.container;

import com.refinedmods.refinedstorage.container.BaseContainer;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.packet.PacketChemicalInitializeContainer;
import javapower.storagetech.mekanism.screen.ScreenChemicalGrid;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalGrid;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerChemicalGrid extends BaseContainer
{
	@ObjectHolder(StorageTech.MODID+":chemicalgrid")
    public static final ContainerType<ContainerChemicalGrid> CURRENT_CONTAINER = null;
	
	public TileEntityChemicalGrid tileNode;
	
	@OnlyIn(Dist.CLIENT)
	private ScreenChemicalGrid screenChemicalGrid;
	
    public ContainerChemicalGrid(TileEntityChemicalGrid _tileNode, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, _tileNode, player, windowId);
        tileNode = _tileNode;
        
        if(player instanceof ServerPlayerEntity)
        	tileNode.getNode().getGrid().addPlayerListener((ServerPlayerEntity) player);
        
        //addPlayerInventory(0, 0);
    }
    
    @Override
    public void onContainerClosed(PlayerEntity player)
    {
    	if(player instanceof ServerPlayerEntity)
    		tileNode.getNode().getGrid().removePlayerListener((ServerPlayerEntity) player);
    	
    	super.onContainerClosed(player);
    }

	public void initSlot(ScreenChemicalGrid _screenChemicalGrid)
	{
		screenChemicalGrid = _screenChemicalGrid;
		StorageTech.INSTANCE_CHANNEL.sendToServer(new PacketChemicalInitializeContainer(screenChemicalGrid.getYPlayerInventory(), screenChemicalGrid.getClient().getVisibleRows()));
		this.inventorySlots.clear();
		
		addPlayerInventory(8, screenChemicalGrid.getYPlayerInventory());
		addFilterSlots();
		
		int rows = screenChemicalGrid.getClient().getVisibleRows();
		
		int index = 0;
		
        for (int i = 0; i < rows; ++i)
        {
        	for(int ii = 0; ii < 9; ++ii)
    		{
    			addSlot(new VirtualSlot(index, 8 + ii*18, 19 + i*18));
    			++index;
    		}
        }
	}
	
	public void initSlotServer(int yPlayerInv, int rows)
	{
		tileNode.getNode().getGrid().sendToListeners();
		
		this.inventorySlots.clear();
		
		addPlayerInventory(8, yPlayerInv);
		addFilterSlots();
		
		int index = 0;
		
        for (int i = 0; i < rows; ++i)
        {
        	for(int ii = 0; ii < 9; ++ii)
    		{
    			addSlot(new VirtualSlot(index, 8 + ii*18, 19 + i*18));
    			++index;
    		}
        }
	}
	
	public Slot getSlot(int slotId)
	{
		if(slotId >= this.inventorySlots.size() || slotId < 0)
			return null;
		return this.inventorySlots.get(slotId);
	}
	
	@Override
	public ItemStack slotClick(int id, int dragType, ClickType clickType, PlayerEntity player)
	{
		if(player.world.isRemote)
			if(screenChemicalGrid != null)
				screenChemicalGrid.slotClick(id, dragType, clickType, player, id == -999 ? null : getSlot(id));
		
		return super.slotClick(id, dragType, clickType, player);
	}
	
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slot)
	{
		if(slot instanceof VirtualSlot)
			return false;
		
		return super.canMergeSlot(stack, slot);
	}

	private void addFilterSlots()
	{
        for (int i = 0; i < 4; ++i)
        {
            addSlot(new SlotItemHandler(tileNode.getNode().getFilter(), i, 204, 6 + (18 * i)));
        }

        transferManager.addBiTransfer(getPlayer().inventory, tileNode.getNode().getFilter());
    }

	public void tryToInteractWidthHeldStack(Chemical<?> chemicalClicked, boolean putInGrid, boolean shift)
	{
		int maxAmnt = shift ? 64000 : 1000;
		
		ItemStack mouseStack = getPlayer().inventory.getItemStack();
		if(mouseStack != null && !mouseStack.isEmpty())
		{
			if(putInGrid)
			{
				IGasHandler ghandler = mouseStack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY).orElse(null);
				if(ghandler != null)
				{
					GasStack stackExtracted = ghandler.extractChemical(maxAmnt, Action.SIMULATE);
					if(!stackExtracted.isEmpty())
					{
						GasStack result = (GasStack) tileNode.getNode().getStData().getMekanismData().insertChemical(stackExtracted, Action.SIMULATE);
						
						long amntExt = maxAmnt - result.getAmount();
						if(amntExt > 0)
						{
							tileNode.getNode().getStData().getMekanismData().insertChemical(ghandler.extractChemical((GasStack) stackExtracted.getType().getStack(amntExt), Action.EXECUTE), Action.EXECUTE);
							return;
						}
					}
				}
				
				IInfusionHandler ihandler = mouseStack.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY).orElse(null);
				if(ihandler != null)
				{
					InfusionStack stackExtracted = ihandler.extractChemical(maxAmnt, Action.SIMULATE);
					if(!stackExtracted.isEmpty())
					{
						InfusionStack result = (InfusionStack) tileNode.getNode().getStData().getMekanismData().insertChemical(stackExtracted, Action.SIMULATE);
						
						long amntExt = maxAmnt - result.getAmount();
						if(amntExt > 0)
						{
							tileNode.getNode().getStData().getMekanismData().insertChemical(ihandler.extractChemical((InfusionStack) stackExtracted.getType().getStack(amntExt), Action.EXECUTE), Action.EXECUTE);
							return;
						}
					}
				}
				
				ISlurryHandler shandler = mouseStack.getCapability(Capabilities.SLURRY_HANDLER_CAPABILITY).orElse(null);
				if(shandler != null)
				{
					SlurryStack stackExtracted = shandler.extractChemical(maxAmnt, Action.SIMULATE);
					if(!stackExtracted.isEmpty())
					{
						SlurryStack result = (SlurryStack) tileNode.getNode().getStData().getMekanismData().insertChemical(stackExtracted, Action.SIMULATE);
						
						long amntExt = maxAmnt - result.getAmount();
						if(amntExt > 0)
						{
							tileNode.getNode().getStData().getMekanismData().insertChemical(shandler.extractChemical((SlurryStack) stackExtracted.getType().getStack(amntExt), Action.EXECUTE), Action.EXECUTE);
							return;
						}
					}
				}
				
			}
			else
			{
				if(chemicalClicked != null)
				{
					if(chemicalClicked instanceof Gas)
					{
						IGasHandler handler = mouseStack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY).orElse(null);
						if(handler != null)
						{
							GasStack stackExtracted = (GasStack) tileNode.getNode().getStData().getMekanismData().extractChemical((GasStack) chemicalClicked.getStack(maxAmnt), Action.SIMULATE);
							GasStack result = (GasStack) handler.insertChemical(stackExtracted, Action.SIMULATE);
							long amntExt = maxAmnt - result.getAmount();
							if(amntExt > 0)
								handler.insertChemical((GasStack) tileNode.getNode().getStData().getMekanismData().extractChemical((GasStack) chemicalClicked.getStack(amntExt), Action.EXECUTE), Action.EXECUTE);
						}
					}
					else if(chemicalClicked instanceof InfuseType)
					{
						IInfusionHandler handler = mouseStack.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY).orElse(null);
						if(handler != null)
						{
							InfusionStack stackExtracted = (InfusionStack) tileNode.getNode().getStData().getMekanismData().extractChemical((InfusionStack) chemicalClicked.getStack(maxAmnt), Action.SIMULATE);
							InfusionStack result = (InfusionStack) handler.insertChemical(stackExtracted, Action.SIMULATE);
							long amntExt = maxAmnt - result.getAmount();
							if(amntExt > 0)
								handler.insertChemical((InfusionStack) tileNode.getNode().getStData().getMekanismData().extractChemical((InfusionStack) chemicalClicked.getStack(amntExt), Action.EXECUTE), Action.EXECUTE);
						}
					}
					else if(chemicalClicked instanceof Slurry)
					{
						ISlurryHandler handler = mouseStack.getCapability(Capabilities.SLURRY_HANDLER_CAPABILITY).orElse(null);
						if(handler != null)
						{
							SlurryStack stackExtracted = (SlurryStack) tileNode.getNode().getStData().getMekanismData().extractChemical((SlurryStack) chemicalClicked.getStack(maxAmnt), Action.SIMULATE);
							SlurryStack result = (SlurryStack) handler.insertChemical(stackExtracted, Action.SIMULATE);
							long amntExt = maxAmnt - result.getAmount();
							if(amntExt > 0)
								handler.insertChemical((SlurryStack) tileNode.getNode().getStData().getMekanismData().extractChemical((SlurryStack) chemicalClicked.getStack(amntExt), Action.EXECUTE), Action.EXECUTE);
						}
					}
				}
			}
		}
	}
}