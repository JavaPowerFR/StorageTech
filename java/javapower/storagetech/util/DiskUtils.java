package javapower.storagetech.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DiskUtils
{
	public static long getMemoryFromItemPart(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			Item item = itemstack.getItem();
			if(item != null)
			{
				int quant = itemstack.getCount();
				String path = item.getRegistryName().getPath();
				if(path.contains("_storage_part"))
				{
					if(path.contains("64k"))
					{
						return 64000l*quant;
					}
					else if(path.contains("16k"))
					{
						return 16000l*quant;
					}
					else if(path.contains("4k"))
					{
						return 4000l*quant;
					}
					else if(path.contains("1k"))
					{
						return 1000l*quant;
					}
					
					
					
				}
			}
		}
		return 0;
	}
	
	public static long getMemoryFromFluidPart(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			Item item = itemstack.getItem();
			if(item != null)
			{
				int quant = itemstack.getCount();
				String path = item.getRegistryName().getPath();
				if(path.contains("_fluid_storage_part"))
				{
					if(path.contains("64k"))
					{
						return 64000l*quant;
					}
					else if(path.contains("256k"))
					{
						return 256000l*quant;
					}
					else if(path.contains("1024k"))
					{
						return 1024000l*quant;
					}
					else if(path.contains("4096k"))
					{
						return 4096000l*quant;
					}
				}
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
				/*else if(item.equals(STItems.item_memory) && itemstack.getItemDamage() == 0 && itemstack.getTagCompound() != null)
				{
					return true;
				}*/
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
				/*else if(item.equals(STItems.item_memory) && itemstack.getItemDamage() == 1 && itemstack.getTagCompound() != null)
				{
					return true;
				}*/
			}
		}
		return false;
	}
}
