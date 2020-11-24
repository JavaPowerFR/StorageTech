package javapower.storagetech.mekanism.item;

import java.util.List;
import java.util.UUID;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.Styles;

import javapower.storagetech.api.STAPI;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.item.STItems;
import javapower.storagetech.mekanism.api.ChemicalStorageType;
import javapower.storagetech.mekanism.api.IItemChemicalStorageDisk;
import javapower.storagetech.mekanism.api.STMKAPI;
import javapower.storagetech.mekanism.data.ChemicalDisk;
import javapower.storagetech.mekanism.data.StorageChemicalDiskSyncData;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemChemicalStorageDisk extends Item implements IItemChemicalStorageDisk
{
	ChemicalStorageType type;
	
	public ItemChemicalStorageDisk(ChemicalStorageType _type)
	{
		super(STItems.DEFAULT_PROPERTIES);
		type = _type;
		setRegistryName(StorageTech.MODID, type.getName()+"_chemical_storage_disk");
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		return 1;
	}
	
	public ChemicalStorageType getType()
	{
		return type;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		if(stackIsValid(stack))
		{
			UUID id = getId(stack);
			STMKAPI.STORAGE_DISK_SYNC.sendRequest(id);
			StorageChemicalDiskSyncData sgdsd = STMKAPI.STORAGE_DISK_SYNC.getData(id);
			if(sgdsd == null)
				return;
			if(sgdsd.getCapacity() == Long.MAX_VALUE-1)
			{
				tooltip.add(new TranslationTextComponent(
						"misc.refinedstorage.storage.stored",
						API.instance().getQuantityFormatter().format(sgdsd.getStored())
						).setStyle(Styles.GRAY));
			}
			tooltip.add(new TranslationTextComponent(
					"misc.refinedstorage.storage.stored_capacity",
					API.instance().getQuantityFormatter().format(sgdsd.getStored()),
					API.instance().getQuantityFormatter().format(sgdsd.getCapacity())
					).setStyle(Styles.GRAY));
			
			if (flagIn.isAdvanced())
			{
                tooltip.add(new StringTextComponent(id.toString()).setStyle(Styles.GRAY));
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
	
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
        ItemStack cellStack = player.getHeldItem(hand);

        if (!world.isRemote && player.isCrouching() && cellStack.getItem() instanceof ItemChemicalStorageDisk)
        {
        	ItemChemicalStorageDisk diskItem = (ItemChemicalStorageDisk) cellStack.getItem();
        	UUID id = diskItem.getId(cellStack);
        	
        	if(id != null)
        	{
	        	ChemicalDisk disk = STAPI.getGlobalNetworkManager((ServerWorld) world).getGlobalMekanisumManager().removeChemicalDisk(id);
	            if (disk.getAmount() == 0)
	            {
	                Item itemIn = MKItems.getItemGasPart(type);
	                if(itemIn != null)
	                {
						ItemStack stack = new ItemStack(itemIn );
		                if (!player.inventory.addItemStackToInventory(stack.copy()))
		                	InventoryHelper.spawnItemStack(world, player.getPosX(), player.getPosY(), player.getPosZ(), stack);
	                }
	                return new ActionResult<>(ActionResultType.SUCCESS, new ItemStack(RSItems.STORAGE_HOUSING.get()));
	            }
        	}
        }

        return new ActionResult<>(ActionResultType.PASS, cellStack);
    }
	
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if(!worldIn.isRemote)
		{
			if(!stack.hasTag())
				stack.setTag(new CompoundNBT());
			
			if(!stack.getTag().hasUniqueId("Id"))
			{
				UUID id = UUID.randomUUID();
				STAPI.getGlobalNetworkManager((ServerWorld) worldIn).getGlobalMekanisumManager().createEnergyDisk(id, type.getCapacity());
				stack.getTag().putUniqueId("Id", id);
			}
		}
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn)
	{
		super.onCreated(stack, worldIn, playerIn);
		if(!worldIn.isRemote)
		{
			if(!stack.hasTag())
				stack.setTag(new CompoundNBT());
			
			if(!stack.getTag().hasUniqueId("Id"))
			{
				UUID id = UUID.randomUUID();
				STAPI.getGlobalNetworkManager((ServerWorld) worldIn).getGlobalMekanisumManager().createEnergyDisk(id, type.getCapacity());
				stack.getTag().putUniqueId("Id", id);
			}
		}
	}

	@Override
	public UUID getId(ItemStack stack)
	{
		if(stack.hasTag())
			return stack.getTag().getUniqueId("Id");
		return null;
	}

}
