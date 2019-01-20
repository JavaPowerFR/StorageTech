package javapower.storagetech.core;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config
{
	public static Configuration conf;
	
	public static int DiskMaxSize = Integer.MAX_VALUE - 65;
    public static int DiskFluidMaxSize = Integer.MAX_VALUE - 64001;
    
    public static boolean EnableCostDisk = true;
    public static int EnergyCostPerSize = 500;
    public static int TimeCostPerSize = 1;
    public static int ProssesAdvancementSize = 10000;
    public static int ProssesAdvancementSizeFluid = 20000;
    
	public static void init(FMLPreInitializationEvent event)
	{
		conf = new Configuration(event.getSuggestedConfigurationFile());
		conf.load();
		
		DiskMaxSize = conf.getInt("diskMaxSize", "general", Integer.MAX_VALUE - 65, 1, Integer.MAX_VALUE - 65, "");
		DiskFluidMaxSize = conf.getInt("fluidDiskMaxSize", "general", Integer.MAX_VALUE - 64001, 1, Integer.MAX_VALUE - 64001, "");
		EnableCostDisk = conf.getBoolean("enableCostDisk", "general", true, "");
		EnergyCostPerSize = conf.getInt("energyCost", "general", 5, 1, 1000000, "");
		TimeCostPerSize = conf.getInt("timeCost", "general", 2, 1, 1000, "");
		ProssesAdvancementSize = conf.getInt("prossesAdvancementSize", "general", 10000, 1, 1000000000, "");
		ProssesAdvancementSizeFluid = conf.getInt("prossesAdvancementSizeFluid", "general", 20000, 1, 1000000000, "");
		onChangment();
		conf.save();
	}
	
	public static void onChangment()
	{
		
	}
}
