package javapower.storagetech.mekanism.container;

import com.refinedmods.refinedstorage.container.BaseContainer;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.grid.FilterTab;
import javapower.storagetech.mekanism.packet.PacketChemicalInitializeContainer;
import javapower.storagetech.mekanism.screen.ScreenChemicalGrid;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalGrid;
import javapower.storagetech.packet.IGenericMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
	
	int hashTmpSlot = 0;
	
    public ContainerChemicalGrid(TileEntityChemicalGrid _tileNode, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, _tileNode, player, windowId);
        tileNode = _tileNode;
        
        if(player instanceof ServerPlayerEntity)
        	tileNode.getNode().getGrid().addPlayerListener((ServerPlayerEntity) player);
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
	
	@Override
	public void detectAndSendChanges()
	{
		int tmpslot = 0;
		for(int id = 0; id < 4; ++id)
			tmpslot |= !tileNode.getNode().getFilter().getStackInSlot(id).isEmpty() ? 1 << id : 0;
		
		if(getPlayer() instanceof ServerPlayerEntity && tmpslot != hashTmpSlot)
		{
			hashTmpSlot = tmpslot;
			CompoundNBT nbt = new CompoundNBT();
			nbt.putByte("updateFilters", (byte) 0);
			IGenericMessage.sendToScreen((ServerPlayerEntity) getPlayer(), nbt);
		}
		super.detectAndSendChanges();
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

	public void tryToInteractWidthHeldStack(mekanism.api.chemical.Chemical<?> chemicalClicked, boolean putInGrid, boolean shift)
	{
		ExtendChemicalGrid.tryToInteractWidthHeldStack(chemicalClicked, putInGrid, shift, (ServerPlayerEntity) getPlayer(), tileNode);
	}

	public FilterTab[] getFilter()
	{
		FilterTab[] filters = new FilterTab[4];
		for(int id = 0; id < 4; ++id)
			filters[id] = FilterTab.getFilterTab(tileNode.getNode().getFilter().getStackInSlot(id));
		return filters;
	}
}