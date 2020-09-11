package javapower.storagetech.util;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DiskUtils
{
	private static PartValue[] item_part = {}, fluid_part = {};
	
	public static void updateValidParts(List<PartValue> item_part2, List<PartValue> fluid_part2)
	{
		item_part = new PartValue[item_part2.size()];
		fluid_part = new PartValue[fluid_part2.size()];
		
		item_part = item_part2.toArray(item_part);
		fluid_part = fluid_part2.toArray(fluid_part);	
	}
	
	public static long getMemoryFromItemPart(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			for(PartValue partv : item_part)
			{
				if(partv.getItem() == itemstack.getItem())
					return partv.getValue();
			}
		}
		
		return 0;
	}
	
	public static long getMemoryFromFluidPart(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			for(PartValue partv : fluid_part)
			{
				if(partv.getItem() == itemstack.getItem())
					return partv.getValue();
			}
		}
		
		return 0;
	}
	
	public static boolean validItemPart(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			Item item = itemstack.getItem();
			if(item != null)
			{
				String path = item.getRegistryName().getPath();
				if(path.contains("_storage_part") && !path.contains("_fluid"))
					return true;
			}
		}
		return false;
	}
	
	public static boolean validFluidPart(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			Item item = itemstack.getItem();
			if(item != null)
			{
				String path = item.getRegistryName().getPath();
				if(path.contains("_fluid_storage_part"))
					return true;
			}
		}
		return false;
	}
	
	public static PartValue[] getFluidParts()
	{
		return fluid_part;
	}
	
	public static PartValue[] getItemParts()
	{
		return item_part;
	}
}
