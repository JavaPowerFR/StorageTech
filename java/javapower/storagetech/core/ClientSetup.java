package javapower.storagetech.core;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import javapower.storagetech.container.ContainerDiskWorkbench;
import javapower.storagetech.container.ContainerFluidDiskWorkbench;
import javapower.storagetech.screen.ScreenContainerDiskWorkbench;
import javapower.storagetech.screen.ScreenContainerFluidDiskWorkbench;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientSetup
{
	
	public static DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
	public static DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
	
	public ClientSetup()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
		MinecraftForge.EVENT_BUS.register(ClientDiskOverlay.class);

	}
	
	@OnlyIn(Dist.CLIENT)
	public void setupClient(final FMLClientSetupEvent event)
	{
		ResourceLocationRegister.register();
		ClientConfig.loadConfig();
		
		ScreenManager.registerFactory(ContainerDiskWorkbench.CURRENT_CONTAINER, ScreenContainerDiskWorkbench::new);
		ScreenManager.registerFactory(ContainerFluidDiskWorkbench.CURRENT_CONTAINER, ScreenContainerFluidDiskWorkbench::new);
		//ScreenManager.registerFactory(CustomStorageContainer.CURRENT_CONTAINER, CustomStorageBlockScreen::new);
		
	}

}
