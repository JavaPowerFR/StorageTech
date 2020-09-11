package javapower.storagetech.util;

import net.minecraft.entity.LivingEntity;

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
}
