package javapower.storagetech.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class DiskUtils
{
	private static List<PartValue> parts = new ArrayList<PartValue>();
	
	public static List<PartValue> getParts()
	{
		return parts;
	}
	
	//private static PartValue[] item_part = {}, fluid_part = {};
	
	public static void updateValidParts(List<PartValue> item_part2, List<PartValue> fluid_part2)
	{
		/*item_part = new PartValue[item_part2.size()];
		fluid_part = new PartValue[fluid_part2.size()];
		
		item_part = item_part2.toArray(item_part);
		fluid_part = fluid_part2.toArray(fluid_part);	*/
	}
	
	public static PartValue getValue(ItemStack stack)
	{
		if(stack.isEmpty())
			return null;
		
		for(PartValue v : parts)
		{
			if(stack.getItem().equals(v.getItem()))
					return v;
		}
		return null;
	}
	
	/*public static long getMemoryFromItemPart(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			for(PartValue partv : item_part)
			{
				if(partv.getItem() == itemstack.getItem())
					return partv.getValue(itemstack);
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
					return partv.getValue(itemstack);
			}
		}
		
		return 0;
	}
	
	public static boolean validItemPart(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			for(PartValue partv : item_part)
			{
				if(partv.getItem() == itemstack.getItem())
					return true;
			}
		}
		
		return false;
	}
	
	public static boolean validFluidPart(ItemStack itemstack)
	{
		if(!itemstack.isEmpty())
		{
			for(PartValue partv : fluid_part)
			{
				if(partv.getItem() == itemstack.getItem())
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
	}*/
}
