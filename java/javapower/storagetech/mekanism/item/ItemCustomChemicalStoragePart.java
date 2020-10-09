package javapower.storagetech.mekanism.item;

import com.refinedmods.refinedstorage.apiimpl.API;

import javapower.storagetech.api.ICustomStoragePart;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.item.STItems;
import javapower.storagetech.util.EPartType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemCustomChemicalStoragePart extends Item implements ICustomStoragePart
{
	public ItemCustomChemicalStoragePart()
	{
		super(STItems.DEFAULT_PROPERTIES);
		setRegistryName(StorageTech.MODID,"custom_chemical_storage_part");
	}
	
	@Override
	public ITextComponent getDisplayName(ItemStack stack)
	{
		CompoundNBT nbt = stack.getTag();
		if(nbt != null && nbt.contains("partvalue"))
		{
			return new TranslationTextComponent(
					"item.storagetech.custom_chemical_storage_part.advanced",
					API.instance().getQuantityFormatter().formatWithUnits(nbt.getInt("partvalue")));
		}
		return super.getDisplayName(stack);
	}
	
	public static ItemStack createItem(int quant)
	{
		ItemStack item = new ItemStack(MKItems.item_custom_chemical_storage_part, 1);
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("partvalue", quant);
		item.setTag(nbt);
		
		return item;
	}
	
	@Override
	public EPartType getType()
	{
		return EPartType.CHEMICAL;
	}

	@Override
	public int getSize(ItemStack stack)
	{
		CompoundNBT nbt = stack.getTag();
		if(nbt != null && nbt.contains("partvalue"))
			return nbt.getInt("partvalue");
		return 0;
	}

	@Override
	public ItemStack createDisk(ItemStack stack)
	{
		ItemStack stackDisk = new ItemStack(MKItems.item_custom_chemical_disk,1);
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("s", getSize(stack));
		stackDisk.setTag(nbt);
		return stackDisk;
	}
}
