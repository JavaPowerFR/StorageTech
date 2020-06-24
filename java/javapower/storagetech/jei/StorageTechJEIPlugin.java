package javapower.storagetech.jei;

import java.util.ArrayList;
import java.util.List;

import com.refinedmods.refinedstorage.RSItems;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.item.STItems;
import javapower.storagetech.screen.ScreenContainerDiskWorkbench;
import javapower.storagetech.screen.ScreenContainerFluidDiskWorkbench;
import javapower.storagetech.util.DiskUtils;
import javapower.storagetech.util.PartValue;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class StorageTechJEIPlugin implements IModPlugin
{

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation(StorageTech.MODID, "jei");
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		
		registry.addRecipeCategories(new DiskWorkbench.Category(guiHelper));
		registry.addRecipeCategories(new DiskWorkbenchMemory.Category(guiHelper));
		
		registry.addRecipeCategories(new FluidDiskWorkbench.Category(guiHelper));
		registry.addRecipeCategories(new FluidDiskWorkbenchMemory.Category(guiHelper));
	}
	
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry)
	{
		registry.addRecipeClickArea(ScreenContainerDiskWorkbench.class, 8, 36, 15, 46, DiskWorkbenchMemory.Category.UID);
		registry.addRecipeClickArea(ScreenContainerDiskWorkbench.class, 148, 36, 15, 22, DiskWorkbench.Category.UID);
		registry.addRecipeClickArea(ScreenContainerFluidDiskWorkbench.class, 8, 36, 15, 46, FluidDiskWorkbenchMemory.Category.UID);
		registry.addRecipeClickArea(ScreenContainerFluidDiskWorkbench.class, 148, 36, 15, 22, FluidDiskWorkbench.Category.UID);
		
	}
	
	/*@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
	{
		registration.addRecipeTransferHandler(ContainerDiskWorkbench.class,DiskWorkbench.Category.UID,0,1,37,1);
		registration.addRecipeTransferHandler(ContainerDiskWorkbench.class,DiskWorkbenchMemory.Category.UID,0,1,36,1);
		
		registration.addRecipeTransferHandler(ContainerFluidDiskWorkbench.class,FluidDiskWorkbench.Category.UID,0,1,37,1);
		registration.addRecipeTransferHandler(ContainerFluidDiskWorkbench.class,FluidDiskWorkbenchMemory.Category.UID,0,1,36,1);
		
	}*/
	@Override
	public void registerRecipes(IRecipeRegistration registry)
	{
		
		List<DiskWorkbench.Wrapper> recipes_diskWorkbench = new ArrayList<DiskWorkbench.Wrapper>();
		
		recipes_diskWorkbench.add(new DiskWorkbench.Wrapper(new ItemStack(RSItems.STORAGE_HOUSING), new ItemStack(STItems.item_diskcustom)));
		
		registry.addRecipes(recipes_diskWorkbench, DiskWorkbench.Category.UID);
		
		// ----------------
		
		List<DiskWorkbenchMemory.Wrapper> recipes_diskWorkbenchMemory = new ArrayList<DiskWorkbenchMemory.Wrapper>();
		
		for(PartValue item : DiskUtils.getItemParts())
			recipes_diskWorkbenchMemory.add(new DiskWorkbenchMemory.Wrapper(new ItemStack(item.getItem()), item.getValue()));
		
		recipes_diskWorkbenchMemory.add(new DiskWorkbenchMemory.Wrapper(new ItemStack(STItems.item_memory), -1));
		
		registry.addRecipes(recipes_diskWorkbenchMemory, DiskWorkbenchMemory.Category.UID);
		
		// ----------------
		
		List<FluidDiskWorkbench.Wrapper> recipes_fluidDiskWorkbench = new ArrayList<FluidDiskWorkbench.Wrapper>();
		
		recipes_fluidDiskWorkbench.add(new FluidDiskWorkbench.Wrapper(new ItemStack(RSItems.STORAGE_HOUSING), new ItemStack(STItems.item_fluiddiskcustom)));
		
		registry.addRecipes(recipes_fluidDiskWorkbench, FluidDiskWorkbench.Category.UID);
		
		// ----------------
		
		List<FluidDiskWorkbenchMemory.Wrapper> recipes_fluidDiskWorkbenchMemory = new ArrayList<FluidDiskWorkbenchMemory.Wrapper>();
		
		for(PartValue item : DiskUtils.getFluidParts())
			recipes_fluidDiskWorkbenchMemory.add(new FluidDiskWorkbenchMemory.Wrapper(new ItemStack(item.getItem()), item.getValue()));
		
		recipes_fluidDiskWorkbenchMemory.add(new FluidDiskWorkbenchMemory.Wrapper(new ItemStack(STItems.item_memory_fluid), -1));
		
		registry.addRecipes(recipes_fluidDiskWorkbenchMemory, FluidDiskWorkbenchMemory.Category.UID);
		
		// ----------------
		
		
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry)
	{
		registry.addRecipeCatalyst(new ItemStack(STBlocks.blockDiskWorkbench), DiskWorkbench.Category.UID);
		registry.addRecipeCatalyst(new ItemStack(STBlocks.blockDiskWorkbench), DiskWorkbenchMemory.Category.UID);
		
		registry.addRecipeCatalyst(new ItemStack(STBlocks.blockFluidDiskWorkbench), FluidDiskWorkbench.Category.UID);
		registry.addRecipeCatalyst(new ItemStack(STBlocks.blockFluidDiskWorkbench), FluidDiskWorkbenchMemory.Category.UID);
		
	}

}
