package javapower.storagetech.item;

import javapower.storagetech.api.IItemEnergyStoragePart;
import javapower.storagetech.core.StorageTech;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemEnergyStoragePart extends Item implements IItemEnergyStoragePart
{
	int size;
	
	public ItemEnergyStoragePart(String subname, int _size)
	{
		super(STItems.DEFAULT_PROPERTIES);
		setRegistryName(StorageTech.MODID, subname+"_energy_storage_part");
		size = _size;
	}

	@Override
	public int getSize(ItemStack stack)
	{
		return size;
	}

}
