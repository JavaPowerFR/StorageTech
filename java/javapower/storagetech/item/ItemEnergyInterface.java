package javapower.storagetech.item;

import javapower.storagetech.core.StorageTech;
import net.minecraft.item.Item;

public class ItemEnergyInterface extends Item
{
	int percent;
	
	public ItemEnergyInterface(String subname, int _percent)
	{
		super(STItems.DEFAULT_PROPERTIES);
		setRegistryName(StorageTech.MODID, subname+"_energy_io_interface");
		percent = _percent;
	}
	
	public int getPercentages()
	{
		return percent;
	}
}
