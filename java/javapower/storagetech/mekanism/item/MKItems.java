package javapower.storagetech.mekanism.item;

import javapower.storagetech.mekanism.api.ChemicalStorageType;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class MKItems
{
	public static final Item item_chemical_storage_part_64k = new ItemChemicalStoragePart(ChemicalStorageType.SIXTY_FOUR_K);
	public static final Item item_chemical_storage_part_256k = new ItemChemicalStoragePart(ChemicalStorageType.TWO_HUNDRED_FIFTY_SIX_K);
	public static final Item item_chemical_storage_part_1024k = new ItemChemicalStoragePart(ChemicalStorageType.THOUSAND_TWENTY_FOUR_K);
	public static final Item item_chemical_storage_part_4096k = new ItemChemicalStoragePart(ChemicalStorageType.FOUR_THOUSAND_NINETY_SIX_K);
	
	public static final Item item_chemical_storage_disk_64k = new ItemChemicalStorageDisk(ChemicalStorageType.SIXTY_FOUR_K);
	public static final Item item_chemical_storage_disk_256k = new ItemChemicalStorageDisk(ChemicalStorageType.TWO_HUNDRED_FIFTY_SIX_K);
	public static final Item item_chemical_storage_disk_1024k = new ItemChemicalStorageDisk(ChemicalStorageType.THOUSAND_TWENTY_FOUR_K);
	public static final Item item_chemical_storage_disk_4096k = new ItemChemicalStorageDisk(ChemicalStorageType.FOUR_THOUSAND_NINETY_SIX_K);
	public static final Item item_chemical_storage_disk_creative = new ItemChemicalStorageDisk(ChemicalStorageType.CREATIVE);
	
	public static final ItemChemicalFilter item_chemical_filter = new ItemChemicalFilter();
	
	public static void registerItems(IForgeRegistry<Item> registry)
	{
		registry.register(item_chemical_storage_part_64k);
		registry.register(item_chemical_storage_part_256k);
		registry.register(item_chemical_storage_part_1024k);
		registry.register(item_chemical_storage_part_4096k);
		
		registry.register(item_chemical_storage_disk_64k);
		registry.register(item_chemical_storage_disk_256k);
		registry.register(item_chemical_storage_disk_1024k);
		registry.register(item_chemical_storage_disk_4096k);
		registry.register(item_chemical_storage_disk_creative);
		
		registry.register(item_chemical_filter);
	}
	
	public static Item getItemGasPart(ChemicalStorageType type)
	{
		if(type == ChemicalStorageType.SIXTY_FOUR_K)
			return item_chemical_storage_part_64k;
		if(type == ChemicalStorageType.TWO_HUNDRED_FIFTY_SIX_K)
			return item_chemical_storage_part_256k;
		if(type == ChemicalStorageType.THOUSAND_TWENTY_FOUR_K)
			return item_chemical_storage_part_1024k;
		if(type == ChemicalStorageType.FOUR_THOUSAND_NINETY_SIX_K)
			return item_chemical_storage_part_4096k;
		return null;
	}
}
