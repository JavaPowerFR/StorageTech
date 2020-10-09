package javapower.storagetech.item;

import com.refinedmods.refinedstorage.apiimpl.API;

import javapower.storagetech.api.IItemEnergyStoragePart;
import javapower.storagetech.core.StorageTech;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemCustomEnergyStoragePart extends Item implements IItemEnergyStoragePart
{
	public ItemCustomEnergyStoragePart()
	{
		super(STItems.DEFAULT_PROPERTIES);
		setRegistryName(StorageTech.MODID,"custom_energy_storage_part");
	}
	
	@Override
	public ITextComponent getDisplayName(ItemStack stack)
	{
		CompoundNBT nbt = stack.getTag();
		if(nbt != null && nbt.contains("partvalue"))
		{
			return new TranslationTextComponent(
					"item.storagetech.custom_energy_storage_part.advanced",
					API.instance().getQuantityFormatter().formatWithUnits(nbt.getInt("partvalue")));
		}
		return super.getDisplayName(stack);
	}
	
	public static ItemStack createItem(int quant)
	{
		ItemStack item = new ItemStack(STItems.item_custom_energy_storage_part, 1);
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("partvalue", quant);
		item.setTag(nbt);
		
		return item;
	}

	@Override
	public int getSize(ItemStack stack)
	{
		CompoundNBT nbt = stack.getTag();
		if(nbt != null && nbt.contains("partvalue"))
			return nbt.getInt("partvalue");
		return 0;
	}
}
