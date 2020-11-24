package javapower.storagetech.core;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class ClientConfig
{
	private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
	private ForgeConfigSpec spec;
	
	public static BooleanValue overlayEnable;
	public static boolean Value_overlayEnable;
	
	public ClientConfig()
	{
		//config
		builder.push("general");
		
		overlayEnable = builder.define("overlayEnable", true);
		
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
		Value_overlayEnable = overlayEnable.get();
	}
}
