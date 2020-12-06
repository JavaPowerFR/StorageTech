package javapower.storagetech.core;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class CommonConfig
{
	private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
	private ForgeConfigSpec spec;
	
	/*public static IntValue DiskMaxSize;
	public static int Value_DiskMaxSize;
	public static IntValue DiskFluidMaxSize;
	public static int Value_DiskFluidMaxSize;
    
    public static BooleanValue EnableCostDisk;
    public static boolean Value_EnableCostDisk;
    
    public static IntValue EnergyCostPerSize;
    public static int Value_EnergyCostPerSize;
    public static IntValue TimeCostPerSize;
    public static int Value_TimeCostPerSize;
    public static IntValue ProssesAdvancementSize;
    public static int Value_ProssesAdvancementSize;
    public static IntValue ProssesAdvancementSizeFluid;
    public static int Value_ProssesAdvancementSizeFluid;*/
    public static IntValue EnergyBuffer;
    public static int Value_EnergyBuffer;
	
	public CommonConfig()
	{
		//config
		builder.push("general");
		
		/*builder.comment("The maximum size of custom disk (Item)");
		DiskMaxSize = builder.defineInRange("diskMaxSize", Integer.MAX_VALUE - 65, 1, Integer.MAX_VALUE - 65);
		builder.comment("The maximum size of custom disk (Fluid)");
		DiskFluidMaxSize = builder.defineInRange("fluidDiskMaxSize", Integer.MAX_VALUE - 64001, 1, Integer.MAX_VALUE - 64001);
		builder.comment("If is enable the workbench (fluid/item) required energy to create disk");
		EnableCostDisk = builder.define("enableCostDisk", true);
		builder.comment("The energy cost per disk","the total energy cost = energyCost * disk size");
		EnergyCostPerSize = builder.defineInRange("energyCost", 5, 1, 1000000);
		builder.comment("The time elapsed per disk","the total time elapsed = timeCost * disk size");
		TimeCostPerSize = builder.defineInRange("timeCost", 2, 1, 1000);
		builder.comment("tick per step (item workbench)");
		ProssesAdvancementSize = builder.defineInRange("prossesAdvancementSize", 10000, 1, 1000000000);
		builder.comment("tick per step (fluid workbench)");
		ProssesAdvancementSizeFluid = builder.defineInRange("prossesAdvancementSizeFluid", 20000, 1, 1000000000);*/
		builder.comment("The energy buffer of parts combiner");
		EnergyBuffer = builder.defineInRange("energyBuffer", 200000, 10000, Integer.MAX_VALUE-1);
		
		builder.pop();
		//end config
		spec = builder.build();
	}
	
	public ForgeConfigSpec getSpec()
	{
        return spec;
    }
	
	public static void loadConfig()
	{
		/*Value_DiskMaxSize = DiskMaxSize.get();
		Value_DiskFluidMaxSize = DiskFluidMaxSize.get();
		Value_EnableCostDisk = EnableCostDisk.get();
		Value_EnergyCostPerSize = EnergyCostPerSize.get()*8;
		Value_TimeCostPerSize = TimeCostPerSize.get();
		Value_ProssesAdvancementSize = ProssesAdvancementSize.get();
		Value_ProssesAdvancementSizeFluid = ProssesAdvancementSizeFluid.get();*/
		Value_EnergyBuffer = EnergyBuffer.get();
	}
}
