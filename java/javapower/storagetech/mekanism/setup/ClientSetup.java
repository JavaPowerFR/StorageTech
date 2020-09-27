package javapower.storagetech.mekanism.setup;

import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry;
import com.refinedmods.refinedstorage.render.model.FullbrightBakedModel;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.block.MKBlocks;
import javapower.storagetech.mekanism.container.ContainerGasDrive;
import javapower.storagetech.mekanism.container.ContainerGasExporter;
import javapower.storagetech.mekanism.container.ContainerGasImporter;
import javapower.storagetech.mekanism.render.BakedModelGasDrive;
import javapower.storagetech.mekanism.screen.ScreenGasDrive;
import javapower.storagetech.mekanism.screen.ScreenGasExporter;
import javapower.storagetech.mekanism.screen.ScreenGasImporter;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup
{
	@OnlyIn(Dist.CLIENT)
	public static void setupClient(final FMLClientSetupEvent event)
	{
		ScreenManager.registerFactory(ContainerGasDrive.CURRENT_CONTAINER, ScreenGasDrive::new);
		ScreenManager.registerFactory(ContainerGasImporter.CURRENT_CONTAINER, ScreenGasImporter::new);
		ScreenManager.registerFactory(ContainerGasExporter.CURRENT_CONTAINER, ScreenGasExporter::new);
		
		RenderTypeLookup.setRenderLayer(MKBlocks.blockGasImporter, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(MKBlocks.blockGasExporter, RenderType.getCutout());
		
	}

	public static void setup(BakedModelOverrideRegistry bakedModelOverrideRegistry)
	{
		bakedModelOverrideRegistry.add(new ResourceLocation(StorageTech.MODID, "gasdrive"), (base, registry) -> new FullbrightBakedModel(
				new BakedModelGasDrive(
						base,
						registry.get(new ResourceLocation(StorageTech.MODID + ":block/gasdisks/gasdisk")),
		                registry.get(new ResourceLocation(StorageTech.MODID + ":block/gasdisks/gasdisk_near_capacity")),
		                registry.get(new ResourceLocation(StorageTech.MODID + ":block/gasdisks/gasdisk_full")),
		                registry.get(new ResourceLocation(StorageTech.MODID + ":block/gasdisks/gasdisk_disconnected"))
						),
				false,
				new ResourceLocation(StorageTech.MODID + ":block/gasdisks/leds")));
		
		ModelLoader.addSpecialModel(new ResourceLocation(StorageTech.MODID + ":block/gasdisks/gasdisk"));
		ModelLoader.addSpecialModel(new ResourceLocation(StorageTech.MODID + ":block/gasdisks/gasdisk_near_capacity"));
		ModelLoader.addSpecialModel(new ResourceLocation(StorageTech.MODID + ":block/gasdisks/gasdisk_full"));
		ModelLoader.addSpecialModel(new ResourceLocation(StorageTech.MODID + ":block/gasdisks/gasdisk_disconnected"));
		
	}
}
