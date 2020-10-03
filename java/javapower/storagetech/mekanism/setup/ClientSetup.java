package javapower.storagetech.mekanism.setup;

import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry;
import com.refinedmods.refinedstorage.render.model.FullbrightBakedModel;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.block.MKBlocks;
import javapower.storagetech.mekanism.container.ContainerChemicalDrive;
import javapower.storagetech.mekanism.container.ContainerChemicalExporter;
import javapower.storagetech.mekanism.container.ContainerChemicalFilter;
import javapower.storagetech.mekanism.container.ContainerChemicalGrid;
import javapower.storagetech.mekanism.container.ContainerChemicalImporter;
import javapower.storagetech.mekanism.render.BakedModelChemicalDrive;
import javapower.storagetech.mekanism.screen.ScreenChemicalDrive;
import javapower.storagetech.mekanism.screen.ScreenChemicalExporter;
import javapower.storagetech.mekanism.screen.ScreenChemicalFilter;
import javapower.storagetech.mekanism.screen.ScreenChemicalGrid;
import javapower.storagetech.mekanism.screen.ScreenChemicalImporter;
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
		ScreenManager.registerFactory(ContainerChemicalDrive.CURRENT_CONTAINER, ScreenChemicalDrive::new);
		ScreenManager.registerFactory(ContainerChemicalImporter.CURRENT_CONTAINER, ScreenChemicalImporter::new);
		ScreenManager.registerFactory(ContainerChemicalExporter.CURRENT_CONTAINER, ScreenChemicalExporter::new);
		ScreenManager.registerFactory(ContainerChemicalGrid.CURRENT_CONTAINER, ScreenChemicalGrid::new);
		ScreenManager.registerFactory(ContainerChemicalFilter.CURRENT_CONTAINER, ScreenChemicalFilter::new);
		
		RenderTypeLookup.setRenderLayer(MKBlocks.blockChemicalImporter, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(MKBlocks.blockChemicalExporter, RenderType.getCutout());
		
	}

	public static void setup(BakedModelOverrideRegistry bakedModelOverrideRegistry)
	{
		bakedModelOverrideRegistry.add(new ResourceLocation(StorageTech.MODID, "chemicaldrive"), (base, registry) -> new FullbrightBakedModel(
				new BakedModelChemicalDrive(
						base,
						registry.get(new ResourceLocation(StorageTech.MODID + ":block/chemicaldisks/chemicaldisk")),
		                registry.get(new ResourceLocation(StorageTech.MODID + ":block/chemicaldisks/chemicaldisk_near_capacity")),
		                registry.get(new ResourceLocation(StorageTech.MODID + ":block/chemicaldisks/chemicaldisk_full")),
		                registry.get(new ResourceLocation(StorageTech.MODID + ":block/chemicaldisks/chemicaldisk_disconnected"))
						),
				false,
				new ResourceLocation(StorageTech.MODID + ":block/chemicaldisks/leds")));
		
		ModelLoader.addSpecialModel(new ResourceLocation(StorageTech.MODID + ":block/chemicaldisks/chemicaldisk"));
		ModelLoader.addSpecialModel(new ResourceLocation(StorageTech.MODID + ":block/chemicaldisks/chemicaldisk_near_capacity"));
		ModelLoader.addSpecialModel(new ResourceLocation(StorageTech.MODID + ":block/chemicaldisks/chemicaldisk_full"));
		ModelLoader.addSpecialModel(new ResourceLocation(StorageTech.MODID + ":block/chemicaldisks/chemicaldisk_disconnected"));
	}
}
