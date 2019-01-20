package javapower.storagetech.util;

import javax.annotation.Nonnull;

import com.raoulvdberge.refinedstorage.RSItems;

import javapower.storagetech.item.STItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DiskUtils
{
	public static long getMemoryFromItemDisk(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			Item item = itemstack.getItem();
			if(item != null)
			{
				if(item.equals(RSItems.STORAGE_PART))
				{
					int dam = itemstack.getItemDamage();
					int quant = itemstack.getCount();
					long memadd = (long) (Math.pow(2, 2*dam)*quant*1000);
					return memadd;
				}
				else if(item.equals(Item.getByNameOrId("rebornstorage:storagepart")))
				{
					int dam = itemstack.getItemDamage();
					if(dam >= 0 && dam <= 3)
					{
						int quant = itemstack.getCount();
						long memadd = (long) (Math.pow(2, 2*(dam+4))*quant*1000);
						return memadd;
					}
				}
				else if(item.equals(STItems.item_memory) && itemstack.getItemDamage() == 0 && itemstack.getTagCompound() != null)
				{
					return itemstack.getTagCompound().getLong("memory");
				}
			}
		}
		return 0;
	}
	
	public static long getMemoryFromFluidDisk(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			Item item = itemstack.getItem();
			if(item != null)
			{
				if(item.equals(RSItems.FLUID_STORAGE_PART))
				{
					int dam = itemstack.getItemDamage();
					int quant = itemstack.getCount();
					long memadd = (long) (Math.pow(2, (dam*2)+6)*quant*1000);
					return memadd;
				}
				else if(item.equals(Item.getByNameOrId("rebornstorage:storagepart")))
				{
					int dam = itemstack.getItemDamage();
					if(dam >= 4 && dam <= 7)
					{
						int quant = itemstack.getCount();
						int exp = dam == 4 ? 14 : dam == 5 ? 15 : dam == 6 ? 17 : dam == 7 ? 19 : 0;
						long memadd = (long) (Math.pow(2, exp)*quant*1000);
						return memadd;
					}
				}
				else if(item.equals(STItems.item_memory) && itemstack.getItemDamage() == 1 && itemstack.getTagCompound() != null)
				{
					return itemstack.getTagCompound().getLong("memory");
				}
			}
		}
		return 0;
	}
	
	public static boolean validItemDisk(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			Item item = itemstack.getItem();
			if(item != null)
			{
				if(item.equals(RSItems.STORAGE_PART))
				{
					return true;
				}
				else if(item.equals(Item.getByNameOrId("rebornstorage:storagepart")))
				{
					int dam = itemstack.getItemDamage();
					if(dam >= 0 && dam <= 3)
					{
						return true;
					}
				}
				else if(item.equals(STItems.item_memory) && itemstack.getItemDamage() == 0 && itemstack.getTagCompound() != null)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean validFluidDisk(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			Item item = itemstack.getItem();
			if(item != null)
			{
				if(item.equals(RSItems.FLUID_STORAGE_PART))
				{
					return true;
				}
				else if(item.equals(Item.getByNameOrId("rebornstorage:storagepart")))
				{
					int dam = itemstack.getItemDamage();
					if(dam >= 4 && dam <= 7)
					{
						return true;
					}
				}
				else if(item.equals(STItems.item_memory) && itemstack.getItemDamage() == 1 && itemstack.getTagCompound() != null)
				{
					return true;
				}
			}
		}
		return false;
	}
}
