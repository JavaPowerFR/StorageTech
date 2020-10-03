package javapower.storagetech.mekanism.item;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.item.STItems;
import javapower.storagetech.mekanism.api.ChemicalStorageType;
import net.minecraft.item.Item;

public class ItemChemicalStoragePart extends Item
{
	ChemicalStorageType type;
	
	public ItemChemicalStoragePart(ChemicalStorageType _type)
	{
		super(STItems.DEFAULT_PROPERTIES);
		type = _type;
		setRegistryName(StorageTech.MODID, type.getName()+"_chemical_storage_part");
	}
	
	public ChemicalStorageType getType()
	{
		return type;
	}

}
