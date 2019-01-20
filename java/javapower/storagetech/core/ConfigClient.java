package javapower.storagetech.core;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigClient
{
	public static Configuration conf;
	
	public static boolean overlayEnable = true;
	public static boolean showHelp = true;
	
	public static void init(FMLPreInitializationEvent event)
	{
		conf = new Configuration(new File(event.getModConfigurationDirectory()+"/client_"+StorageTech.MODID+".cfg"));
		conf.load();
		
		//DiskMaxSize = conf.getInt("diskMaxSize", "general", Integer.MAX_VALUE - 65, 1, Integer.MAX_VALUE - 65, "");
		//DiskFluidMaxSize = conf.getInt("fluidDiskMaxSize", "general", Integer.MAX_VALUE - 64001, 1, Integer.MAX_VALUE - 64001, "");
		overlayEnable = conf.getBoolean("overlayEnable", "general", true, "");
		showHelp = conf.getBoolean("showHelp", "general", true, "");
		
		onChangment();
		conf.save();
	}
	
	public static void onChangment()
	{
		
	}
	
	public static boolean getShowHelp()
	{
		showHelp = conf.getBoolean("showHelp", "general", true, "");
		return showHelp;
	}
}
