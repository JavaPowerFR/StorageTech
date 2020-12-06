package javapower.storagetech.setup;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry;
import com.refinedmods.refinedstorage.render.model.FullbrightBakedModel;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.container.ContainerPOEDrive;
import javapower.storagetech.container.ContainerPOEExporter;
import javapower.storagetech.container.ContainerPOEFurnace;
import javapower.storagetech.container.ContainerPOEImporter;
import javapower.storagetech.container.ContainerPartsCombiner;
import javapower.storagetech.container.ContainerStructureConstructor;
import javapower.storagetech.core.ClientConfig;
import javapower.storagetech.core.ResourceLocationRegister;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.render.BakedModelPOEDrive;
import javapower.storagetech.render.ClientDiskOverlay;
import javapower.storagetech.render.TESRStructureConstructor;
import javapower.storagetech.screen.ScreenPOEDrive;
import javapower.storagetech.screen.ScreenPOEExporter;
import javapower.storagetech.screen.ScreenPOEFurnace;
import javapower.storagetech.screen.ScreenPOEImporter;
import javapower.storagetech.screen.ScreenPartsCombiner;
import javapower.storagetech.screen.ScreenStructureConstructor;
import javapower.storagetech.tileentity.TileEntityStructureConstructor;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientSetup
{
	public static boolean ShowInfo = false;
	
	public static DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
	public static DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
	
	private final BakedModelOverrideRegistry bakedModelOverrideRegistry = new BakedModelOverrideRegistry();
	
	public ClientSetup()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
		MinecraftForge.EVENT_BUS.register(ClientDiskOverlay.class);
		
		bakedModelOverrideRegistry.add(new ResourceLocation(StorageTech.MODID, "poedrive"), (base, registry) -> new FullbrightBakedModel(
				new BakedModelPOEDrive(
						base,
						registry.get(new ResourceLocation(StorageTech.MODID + ":block/batterys/battery")),
		                registry.get(new ResourceLocation(StorageTech.MODID + ":block/batterys/battery_near_capacity")),
		                registry.get(new ResourceLocation(StorageTech.MODID + ":block/batterys/battery_full")),
		                registry.get(new ResourceLocation(StorageTech.MODID + ":block/batterys/battery_disconnected"))
						),
				false,
				new ResourceLocation(StorageTech.MODID + ":block/batterys/leds")));
		
		ModelLoader.addSpecialModel(new ResourceLocation(StorageTech.MODID + ":block/batterys/battery"));
		ModelLoader.addSpecialModel(new ResourceLocation(StorageTech.MODID + ":block/batterys/battery_near_capacity"));
		ModelLoader.addSpecialModel(new ResourceLocation(StorageTech.MODID + ":block/batterys/battery_full"));
		ModelLoader.addSpecialModel(new ResourceLocation(StorageTech.MODID + ":block/batterys/battery_disconnected"));
		
		if(StorageTech.MOD_MEKANISM_IS_LOADED)
    		javapower.storagetech.mekanism.setup.ClientSetup.setup(bakedModelOverrideRegistry);
		
	}
	
	@OnlyIn(Dist.CLIENT)
	public void setupClient(final FMLClientSetupEvent event)
	{
		ResourceLocationRegister.register();
		ClientConfig.loadConfig();
		
		ClientRegistry.bindTileEntityRenderer(TileEntityStructureConstructor.CURRENT_TILE, TESRStructureConstructor::new);
		
		ScreenManager.registerFactory(ContainerPartsCombiner.CURRENT_CONTAINER, ScreenPartsCombiner::new);
		
		ScreenManager.registerFactory(ContainerPOEDrive.CURRENT_CONTAINER, ScreenPOEDrive::new);
		ScreenManager.registerFactory(ContainerPOEImporter.CURRENT_CONTAINER, ScreenPOEImporter::new);
		ScreenManager.registerFactory(ContainerPOEExporter.CURRENT_CONTAINER, ScreenPOEExporter::new);
		ScreenManager.registerFactory(ContainerPOEFurnace.CURRENT_CONTAINER, ScreenPOEFurnace::new);
		ScreenManager.registerFactory(ContainerStructureConstructor.CURRENT_CONTAINER, ScreenStructureConstructor::new);
		//ScreenManager.registerFactory(ContainerAdvancedExporter.CURRENT_CONTAINER, ScreenAdvancedExporter::new);
		
		RenderTypeLookup.setRenderLayer(STBlocks.blockPOEImporter, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(STBlocks.blockPOEExporter, RenderType.getCutout());
		
		if(StorageTech.MOD_MEKANISM_IS_LOADED)
    		javapower.storagetech.mekanism.setup.ClientSetup.setupClient(event);
	}
	
	@SubscribeEvent
    public void onModelBake(ModelBakeEvent event)
	{
		for (ResourceLocation id : event.getModelRegistry().keySet())
		{
            BakedModelOverrideRegistry.BakedModelOverrideFactory factory = this.bakedModelOverrideRegistry.get(new ResourceLocation(id.getNamespace(), id.getPath()));

            if (factory != null)
            {
                event.getModelRegistry().put(id, factory.create(event.getModelRegistry().get(id), event.getModelRegistry()));
            }
        }
	}

}
