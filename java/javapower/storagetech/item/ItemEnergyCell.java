package javapower.storagetech.item;

import java.util.List;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.Styles;

import javapower.storagetech.api.IEnergyStorageCell;
import javapower.storagetech.core.StorageTech;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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

public class ItemEnergyCell extends Item implements IEnergyStorageCell
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
		return new StringTextComponent(I18n.format(
				"item.storagetech.energy_storage_cell.advanced",
				API.instance().getQuantityFormatter().formatWithUnits(getCapacity(stack)),
				API.instance().getQuantityFormatter().formatWithUnits(getIOCapacity(stack))
				));
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		if(stackIsValid(stack))
		{
			tooltip.add(new TranslationTextComponent(
					"misc.storagetech.storage.energy.stored_capacity",
					API.instance().getQuantityFormatter().format(getEnergyStored(stack)),
					API.instance().getQuantityFormatter().format(getCapacity(stack))
					).func_230530_a_(Styles.GRAY));
			tooltip.add(new TranslationTextComponent(
					"misc.storagetech.storage.energy.io_capacity",
					API.instance().getQuantityFormatter().format(getIOCapacity(stack))
					).func_230530_a_(Styles.GRAY));
		}
	}

	@Override
	public int getCapacity(ItemStack stack)
	{
		if(stack != null && stack.hasTag())
			return stack.getTag().getInt("cap");
		
		return 0;
	}

	@Override
	public int getIOCapacity(ItemStack stack)
	{
		if(stack != null && stack.hasTag())
			return stack.getTag().getInt("iocap");
		
		return 0;
	}

	@Override
	public int getEnergyStored(ItemStack stack)
	{
		if(stackIsValid(stack))
			return stack.getTag().getInt("energy");
		return 0;
	}

	@Override
	public int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate)
	{
		if(stackIsValid(stack))
		{
			int energy = stack.getTag().getInt("energy");
			int capacity = getCapacity(stack);
			int accepted = Math.min(Math.min(capacity - energy, maxReceive), getIOCapacity(stack));
			if(!simulate)
			{
				energy += accepted;
				stack.getTag().putInt("energy", energy);
			}
			return accepted;
		}
		return 0;
	}

	@Override
	public int extractEnergy(ItemStack stack, int maxExtract, boolean simulate)
	{
		if(stackIsValid(stack))
		{
			int energy = stack.getTag().getInt("energy");
			int energyExtracteble = Math.min(Math.min(energy, maxExtract), getIOCapacity(stack));
			if(!simulate)
			{
				energy -= energyExtracteble;
				stack.getTag().putInt("energy", energy);
			}
			
			return energyExtracteble;
		}
		return 0;
	}
	
	public boolean stackIsValid(ItemStack stack)
	{
		if(stack != null)
		{
			if(!stack.hasTag())
			{
				stack.setTag(new CompoundNBT());
			}
			if(!stack.getTag().contains("energy"))
				stack.getTag().putInt("energy", 0);
			return true;
		}
		return false;
	}
	
	public static ItemStack createItem(int size, int iocap)
	{
		ItemStack item = new ItemStack(STItems.item_energy_storage_cell, 1);
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("cap", size);
		nbt.putInt("iocap", iocap);
		nbt.putInt("energy", 0);
		item.setTag(nbt);
		
		return item;
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
        ItemStack cellStack = player.getHeldItem(hand);

        if (!world.isRemote && player.isCrouching() && cellStack.getItem() instanceof IEnergyStorageCell)
        {
            IEnergyStorageCell cell = (IEnergyStorageCell) cellStack.getItem();

            if (cell.getEnergyStored(cellStack) == 0)
            {
                ItemStack storagePart[] = cell.getParts(cellStack);

                for(ItemStack stack : storagePart)
                if (!player.inventory.addItemStackToInventory(stack.copy()))
                	InventoryHelper.spawnItemStack(world, player.getPosX(), player.getPosY(), player.getPosZ(), stack);

                return new ActionResult<>(ActionResultType.SUCCESS, new ItemStack(STItems.item_energy_storage_housing));
            }
        }

        return new ActionResult<>(ActionResultType.PASS, cellStack);
    }

	@Override
	public ItemStack[] getParts(ItemStack stack)
	{
		ItemStack part = ItemStack.EMPTY;
		ItemStack io_iterface = ItemStack.EMPTY;
		
		int capacity = stack.getTag().getInt("cap");
		int ioCapacity = stack.getTag().getInt("iocap");
		
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

}
