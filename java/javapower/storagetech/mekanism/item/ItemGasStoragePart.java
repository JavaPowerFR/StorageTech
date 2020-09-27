package javapower.storagetech.mekanism.item;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.item.STItems;
import javapower.storagetech.mekanism.api.GasStorageType;
import net.minecraft.item.Item;

public class ItemGasStoragePart extends Item
{
	GasStorageType type;
	
	public ItemGasStoragePart(GasStorageType _type)
	{
		super(STItems.DEFAULT_PROPERTIES);
		type = _type;
		setRegistryName(StorageTech.MODID, type.getName()+"_gas_storage_part");
	}
	
	public GasStorageType getType()
	{
		return type;
	}

}
