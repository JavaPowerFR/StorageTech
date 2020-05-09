package javapower.storagetech.item;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class STItems
{
	public static final Item item_diskcustom = new ItemDiskCustom();
	public static final Item item_fluiddiskcustom = new ItemFluidDiskCustom();
	public static final Item item_memory = new ItemMemoryItem();
	public static final Item item_memory_fluid = new ItemMemoryFluid();
	
	public static void registerItems(IForgeRegistry<Item> registry)
	{
		registry.register(item_diskcustom);
		registry.register(item_fluiddiskcustom);
		registry.register(item_memory);
		registry.register(item_memory_fluid);
		
	}

}
