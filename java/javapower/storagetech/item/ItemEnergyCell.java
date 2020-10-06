package javapower.storagetech.item;

import java.util.List;
import java.util.UUID;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.Styles;

import javapower.storagetech.api.IItemEnergyStorageDisk;
import javapower.storagetech.api.STAPI;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.data.EnergyDisk;
import javapower.storagetech.data.StorageEnergyDiskSyncData;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemEnergyCell extends Item implements IItemEnergyStorageDisk
{
	public ItemEnergyCell()
	{
		super(STItems.DEFAULT_PROPERTIES);
		setRegistryName(StorageTech.MODID, "energy_storage_cell");
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		return 1;
	}
	
	@Override
	public ITextComponent getDisplayName(ItemStack stack)
	{
		UUID id = getId(stack);
		STAPI.STORAGE_DISK_SYNC.sendRequest(id);
		StorageEnergyDiskSyncData sedsy = STAPI.STORAGE_DISK_SYNC.getData(id);
		if(sedsy != null)
		{	
			return new StringTextComponent(I18n.format(
					"item.storagetech.energy_storage_cell.advanced",
					API.instance().getQuantityFormatter().formatWithUnits(sedsy.getCapacity()),
					API.instance().getQuantityFormatter().formatWithUnits(sedsy.getIoCapacity())
					));
		}
		else if(stack.hasTag() && stack.getTag().contains("s"))
		{
			return new StringTextComponent(I18n.format(
					"item.storagetech.energy_storage_cell.advanced",
					API.instance().getQuantityFormatter().formatWithUnits(stack.getTag().getInt("s")),
					API.instance().getQuantityFormatter().formatWithUnits(stack.getTag().getInt("i"))
					));
		}
		
		return super.getDisplayName(stack);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		if(stackIsValid(stack))
		{
			UUID id = getId(stack);
			STAPI.STORAGE_DISK_SYNC.sendRequest(id);
			StorageEnergyDiskSyncData sedsd = STAPI.STORAGE_DISK_SYNC.getData(id);
			if(sedsd == null)
				return;
			
			tooltip.add(new TranslationTextComponent(
					"misc.storagetech.storage.energy.stored_capacity",
					API.instance().getQuantityFormatter().format(sedsd.getStored()),
					API.instance().getQuantityFormatter().format(sedsd.getCapacity())
					).func_230530_a_(Styles.GRAY));
			tooltip.add(new TranslationTextComponent(
					"misc.storagetech.storage.energy.io_capacity",
					API.instance().getQuantityFormatter().format(sedsd.getIoCapacity())
					).func_230530_a_(Styles.GRAY));
			
			if (flagIn.isAdvanced())
			{
                tooltip.add(new StringTextComponent(id.toString()).func_230530_a_(Styles.GRAY));
            }
		}
	}
	
	public boolean stackIsValid(ItemStack stack)
	{
		if(stack != null)
		{
			return stack.hasTag() && stack.getTag().hasUniqueId("Id");
		}
		return false;
	}
	
	public static ItemStack createItem(int size, int iocap)
	{
		UUID id = UUID.randomUUID();
		
		ItemStack item = new ItemStack(STItems.item_energy_storage_cell, 1);
		
		CompoundNBT nbt = new CompoundNBT();
		nbt.putUniqueId("Id", id);
		nbt.putInt("s", size);
		nbt.putInt("i", iocap);
		
		item.setTag(nbt);
		
		return item;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if(!worldIn.isRemote && stack.hasTag() && stack.getTag().contains("s"))
		{
			STAPI.getGlobalNetworkManager((ServerWorld) worldIn).createEnergyDisk(stack.getTag().getUniqueId("Id"), stack.getTag().getInt("s"), stack.getTag().getInt("i"));
			stack.getTag().remove("s");
			stack.getTag().remove("i");
		}
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn)
	{
		super.onCreated(stack, worldIn, playerIn);
		if(!worldIn.isRemote && stack.hasTag() && stack.getTag().contains("s"))
		{
			STAPI.getGlobalNetworkManager((ServerWorld) worldIn).createEnergyDisk(stack.getTag().getUniqueId("Id"), stack.getTag().getInt("s"), stack.getTag().getInt("i"));
			stack.getTag().remove("s");
			stack.getTag().remove("i");
		}
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
        ItemStack cellStack = player.getHeldItem(hand);

        if (!world.isRemote && player.isCrouching() && cellStack.getItem() instanceof ItemEnergyCell)
        {
        	ItemEnergyCell cell = (ItemEnergyCell) cellStack.getItem();
        	UUID id = cell.getId(cellStack);
        	if(id != null)
        	{
	        	EnergyDisk disk = STAPI.getGlobalNetworkManager((ServerWorld) world).removeEnergyDisk(id);
	            if (disk.getEnergyStored() == 0)
	            {
	                ItemStack storagePart[] = cell.getParts(disk.capacity, disk.io_capacity);
	
	                for(ItemStack stack : storagePart)
	                if (!player.inventory.addItemStackToInventory(stack.copy()))
	                	InventoryHelper.spawnItemStack(world, player.getPosX(), player.getPosY(), player.getPosZ(), stack);
	
	                return new ActionResult<>(ActionResultType.SUCCESS, new ItemStack(STItems.item_energy_storage_housing));
	            }
        	}
        }

        return new ActionResult<>(ActionResultType.PASS, cellStack);
    }
	
	public ItemStack[] getParts(int capacity, int ioCapacity)
	{
		ItemStack part = ItemStack.EMPTY;
		ItemStack io_iterface = ItemStack.EMPTY;
		
		if(capacity == 100_000) part = new ItemStack(STItems.item_100k_energy_storage_part);
		else if(capacity == 400_000) part = new ItemStack(STItems.item_400k_energy_storage_part);
		else if(capacity == 1_600_000) part = new ItemStack(STItems.item_1M6_energy_storage_part);
		else if(capacity == 6_400_000) part = new ItemStack(STItems.item_6M4_energy_storage_part);
		else if(capacity == 25_600_000) part = new ItemStack(STItems.item_25M6_energy_storage_part);
		else if(capacity == 102_400_000) part = new ItemStack(STItems.item_102M4_energy_storage_part);
		
		if(ioCapacity == 10) io_iterface = new ItemStack(STItems.item_10p_energy_io_interface);
		else if(ioCapacity == 20) io_iterface = new ItemStack(STItems.item_20p_energy_io_interface);
		else if(ioCapacity == 40) io_iterface = new ItemStack(STItems.item_40p_energy_io_interface);
		else if(ioCapacity == 80) io_iterface = new ItemStack(STItems.item_80p_energy_io_interface);
		
		return new ItemStack[]{part, io_iterface};
	}

	@Override
	public UUID getId(ItemStack stack)
	{
		if(stack.hasTag())
			return stack.getTag().getUniqueId("Id");
		return null;
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
	{
		if(group != StorageTech.creativeTab)
			return;
		
		int[] values = new int[] {100_000, 400_000, 1_600_000, 6_400_000, 25_600_000, 102_400_000};
		int[] efficiencys = new int[] {10, 20, 40, 80};
		for(int v : values)
		{
			for(int e : efficiencys)
			{
				items.add(ItemEnergyCell.createItem(v, (int)((v*((long)e))/100)));
			}
		}
	}

}
