package javapower.storagetech.mekanism.item;

import javapower.storagetech.mekanism.api.GasStorageType;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class MKItems
{
	public static final Item item_gas_storage_part_64k = new ItemGasStoragePart(GasStorageType.SIXTY_FOUR_K);
	public static final Item item_gas_storage_part_256k = new ItemGasStoragePart(GasStorageType.TWO_HUNDRED_FIFTY_SIX_K);
	public static final Item item_gas_storage_part_1024k = new ItemGasStoragePart(GasStorageType.THOUSAND_TWENTY_FOUR_K);
	public static final Item item_gas_storage_part_4096k = new ItemGasStoragePart(GasStorageType.FOUR_THOUSAND_NINETY_SIX_K);
	
	public static final Item item_gas_storage_disk_64k = new ItemGasStorageDisk(GasStorageType.SIXTY_FOUR_K);
	public static final Item item_gas_storage_disk_256k = new ItemGasStorageDisk(GasStorageType.TWO_HUNDRED_FIFTY_SIX_K);
	public static final Item item_gas_storage_disk_1024k = new ItemGasStorageDisk(GasStorageType.THOUSAND_TWENTY_FOUR_K);
	public static final Item item_gas_storage_disk_4096k = new ItemGasStorageDisk(GasStorageType.FOUR_THOUSAND_NINETY_SIX_K);
	public static final Item item_gas_storage_disk_creative = new ItemGasStorageDisk(GasStorageType.CREATIVE);
	
	public static void registerItems(IForgeRegistry<Item> registry)
	{
		registry.register(item_gas_storage_part_64k);
		registry.register(item_gas_storage_part_256k);
		registry.register(item_gas_storage_part_1024k);
		registry.register(item_gas_storage_part_4096k);
		
		registry.register(item_gas_storage_disk_64k);
		registry.register(item_gas_storage_disk_256k);
		registry.register(item_gas_storage_disk_1024k);
		registry.register(item_gas_storage_disk_4096k);
		registry.register(item_gas_storage_disk_creative);
	}
	
	public static Item getItemGasPart(GasStorageType type)
	{
		if(type == GasStorageType.SIXTY_FOUR_K)
			return item_gas_storage_part_64k;
		if(type == GasStorageType.TWO_HUNDRED_FIFTY_SIX_K)
			return item_gas_storage_part_256k;
		if(type == GasStorageType.THOUSAND_TWENTY_FOUR_K)
			return item_gas_storage_part_1024k;
		if(type == GasStorageType.FOUR_THOUSAND_NINETY_SIX_K)
			return item_gas_storage_part_4096k;
		return null;
	}
}
