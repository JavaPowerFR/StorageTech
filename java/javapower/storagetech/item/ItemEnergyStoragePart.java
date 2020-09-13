package javapower.storagetech.item;

import javapower.storagetech.core.StorageTech;
import net.minecraft.item.Item;

public class ItemEnergyStoragePart extends Item
{
	int size;
	
	public ItemEnergyStoragePart(String subname, int _size)
	{
		super(STItems.DEFAULT_PROPERTIES);
		setRegistryName(StorageTech.MODID, subname+"_energy_part");
		size = _size;
	}
	
	public int getSize()
	{
		return size;
	}

}
