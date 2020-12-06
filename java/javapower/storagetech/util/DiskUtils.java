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
}
