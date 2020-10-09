package javapower.storagetech.container;

import com.refinedmods.refinedstorage.container.slot.BaseSlot;
import com.refinedmods.refinedstorage.container.slot.OutputSlot;
import com.refinedmods.refinedstorage.container.transfer.TransferManager;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityPartsCombiner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerPartsCombiner extends Container
{
	@ObjectHolder(StorageTech.MODID+":partscombiner")
    public static final ContainerType<ContainerPartsCombiner> CURRENT_CONTAINER = null;
	
	protected final TransferManager transferManager = new TransferManager(this);
	
	public TileEntityPartsCombiner tile;
	PlayerInventory playerInventory;
	
	public ContainerPartsCombiner(int windowId, PlayerInventory _playerInventory)
	{
		this(windowId, new TileEntityPartsCombiner(), _playerInventory);
	}
	
	public ContainerPartsCombiner(int windowId, TileEntityPartsCombiner _tile, PlayerInventory _playerInventory)
	{
		super(CURRENT_CONTAINER, windowId);
		
		playerInventory = _playerInventory;
		tile = _tile;
		
		addSlot(new BaseSlot(tile.getInventory().getInventory(), 0, 39, 42));
		addSlot(new BaseSlot(tile.getInventory().getInventory(), 1, 73, 42));
		addSlot(new OutputSlot(tile.getInventory().getInventory(), 2, 127, 42));
		
		addSlot(new BaseSlot(tile.getInventory().getUpgrades(), 0, 187, 6));
		addSlot(new BaseSlot(tile.getInventory().getUpgrades(), 1, 187, 24));
		addSlot(new BaseSlot(tile.getInventory().getUpgrades(), 2, 187, 42));
		addSlot(new BaseSlot(tile.getInventory().getUpgrades(), 3, 187, 60));
		
		
		addPlayerInventory(8, 96);
		
		transferManager.addBiTransfer(playerInventory, tile.getInventory().getInventory());
		transferManager.addBiTransfer(playerInventory, tile.getInventory().getUpgrades());
	}
	
	private void addPlayerInventory(int x, int y)
	{
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlot(new Slot(playerInventory, k, x + k * 18, y + 58));
        }
	}
	
	@Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex)
	{
        return transferManager.transfer(slotIndex);
    }

	@Override
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return true;
	}

}
