package javapower.storagetech.item;

import javapower.storagetech.core.StorageTech;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class STItems
{
	public static final Item.Properties DEFAULT_PROPERTIES = new Item.Properties().group(StorageTech.creativeTab);
	
	public static final Item item_diskcustom = new ItemDiskCustom();
	public static final Item item_fluiddiskcustom = new ItemFluidDiskCustom();
	
	public static final Item item_memory = new ItemMemoryItem();
	public static final Item item_memory_fluid = new ItemMemoryFluid();
	
	public static final Item item_capacitive_redstone = new Item(DEFAULT_PROPERTIES).setRegistryName(StorageTech.MODID, "capacitive_redstone");
	
	public static final Item item_100k_energy_storage_part = new ItemEnergyStoragePart("100k", 100_000);
	public static final Item item_400k_energy_storage_part = new ItemEnergyStoragePart("400k", 400_000);
	public static final Item item_1M6_energy_storage_part = new ItemEnergyStoragePart("1m6", 1_600_000);
	public static final Item item_6M4_energy_storage_part = new ItemEnergyStoragePart("6m4", 6_400_000);
	public static final Item item_25M6_energy_storage_part = new ItemEnergyStoragePart("25m6", 25_600_000);
	public static final Item item_102M4_energy_storage_part = new ItemEnergyStoragePart("102m4", 102_400_000);
	
	public static final Item item_10p_energy_io_interface = new ItemEnergyInterface("10p", 10);
	public static final Item item_20p_energy_io_interface = new ItemEnergyInterface("20p", 20);
	public static final Item item_40p_energy_io_interface = new ItemEnergyInterface("40p", 40);
	public static final Item item_80p_energy_io_interface = new ItemEnergyInterface("80p", 80);
	
	public static final Item item_energy_storage_housing = new Item(DEFAULT_PROPERTIES).setRegistryName(StorageTech.MODID, "energy_storage_housing");
	
	public static final Item item_energy_storage_cell = new ItemEnergyCell();
	
	public static void registerItems(IForgeRegistry<Item> registry)
	{
		registry.register(item_diskcustom);
		registry.register(item_fluiddiskcustom);
		registry.register(item_memory);
		registry.register(item_memory_fluid);
		
		registry.register(item_capacitive_redstone);
		
		registry.register(item_100k_energy_storage_part);
		registry.register(item_400k_energy_storage_part);
		registry.register(item_1M6_energy_storage_part);
		registry.register(item_6M4_energy_storage_part);
		registry.register(item_25M6_energy_storage_part);
		registry.register(item_102M4_energy_storage_part);
		
		registry.register(item_10p_energy_io_interface);
		registry.register(item_20p_energy_io_interface);
		registry.register(item_40p_energy_io_interface);
		registry.register(item_80p_energy_io_interface);
		
		registry.register(item_energy_storage_housing);
		
		registry.register(item_energy_storage_cell);
		
	}

}
