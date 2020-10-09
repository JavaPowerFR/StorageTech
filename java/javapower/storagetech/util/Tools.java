package javapower.storagetech.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

public class Tools
{
	public static String longFormatToString(long bytes)
	{
	    if (bytes < 1000) return ""+bytes;
	    int exp = (int) (Math.log(bytes) / Math.log(1000));
	    String pre = ""+ "kMGTPE".charAt(exp-1);
	    return String.format("%.1f %s", bytes / Math.pow(1000, exp), pre);
	}
    
    public static boolean isJavaPower(LivingEntity player)
    {
    	return player.getUniqueID().toString().equalsIgnoreCase("6d89ae8c-01f7-498c-bb36-3f76ff9dfdc9");
    }
    
    public static <T> boolean isOnArray(T[] list, T element)
    {
    	if(list == null)
    		return false;
    	
    	for(T e : list)
    		if(e.equals(element))
    			return true;
    		
    	return false;
    }
    
    @SafeVarargs
    public static <T> List<T> toList(T... args)
    {
    	List<T> list = new ArrayList<T>();
    	for(T a : args)
    		list.add(a);
    	return list;
    }
    
    public static List<ItemStack> ItemStackListFromItems(IItemProvider... args)
    {
    	List<ItemStack> list = new ArrayList<ItemStack>();
    	for(IItemProvider a : args)
    		list.add(new ItemStack(a));
    	return list;
    }
}
