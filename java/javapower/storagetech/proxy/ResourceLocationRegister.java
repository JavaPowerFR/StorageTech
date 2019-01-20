package javapower.storagetech.proxy;

import javapower.storagetech.core.StorageTech;
import net.minecraft.util.ResourceLocation;

public class ResourceLocationRegister
{
	public static ResourceLocation gui_diskwb, gui_fdiskwb, overlay, textrue_gui_jei_recipe;
	public static void register()
	{
		gui_diskwb = resource("textures/guis/gui_diskmaker.png");
		gui_fdiskwb = resource("textures/guis/gui_diskmakerfluid.png");
		overlay = resource("textures/guis/overlay.png");
		textrue_gui_jei_recipe = resource("textures/guis/jei.png");
	}
	
	private static ResourceLocation resource(String target)
	{
		return new ResourceLocation(StorageTech.MODID, target);
	}

}
