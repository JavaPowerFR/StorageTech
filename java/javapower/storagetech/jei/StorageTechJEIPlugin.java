package javapower.storagetech.jei;

import java.util.ArrayList;
import java.util.List;

import com.raoulvdberge.refinedstorage.RSItems;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.item.STItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class StorageTechJEIPlugin implements IModPlugin
{
	@Override
	public void register(IModRegistry registry)
	{
		//DiskWorkbench
		
		List<DiskWorkbench.IRecipePattern> diskiwb_recipes = new ArrayList<DiskWorkbench.IRecipePattern>();
		diskiwb_recipes.add(new DiskWorkbench.IRecipePattern()
		{
			@Override public ItemStack output() {return new ItemStack(STItems.item_diskcustom);}
			@Override public ItemStack input() {return new ItemStack(RSItems.STORAGE_HOUSING);}
		});
		
		registry.handleRecipes(DiskWorkbench.IRecipePattern.class, new DiskWorkbench.WrapperFactory(), DiskWorkbench.Category.UID);
		registry.addRecipes(diskiwb_recipes, DiskWorkbench.Category.UID);
		registry.addRecipeCatalyst(new ItemStack(STBlocks.block_diskWorkbench), DiskWorkbench.Category.UID);
		
		//FluidDiskWorkbench
		
		List<FluidDiskWorkbench.IRecipePattern> diskfwb_recipes = new ArrayList<FluidDiskWorkbench.IRecipePattern>();
		diskfwb_recipes.add(new FluidDiskWorkbench.IRecipePattern()
		{
			@Override public ItemStack output() {return new ItemStack(STItems.item_fluiddiskcustom);}
			@Override public ItemStack input() {return new ItemStack(RSItems.STORAGE_HOUSING);}
		});
		
		registry.handleRecipes(FluidDiskWorkbench.IRecipePattern.class, new FluidDiskWorkbench.WrapperFactory(), FluidDiskWorkbench.Category.UID);
		registry.addRecipes(diskfwb_recipes, FluidDiskWorkbench.Category.UID);
		registry.addRecipeCatalyst(new ItemStack(STBlocks.block_fluiddiskWorkbench), FluidDiskWorkbench.Category.UID);
		
		//DiskWorkbench
		
		List<DiskWorkbenchMemory.IRecipePattern> diskiwbm_recipes = new ArrayList<DiskWorkbenchMemory.IRecipePattern>();
		
		diskiwbm_recipes.add(new DiskWorkbenchMemory.IRecipePattern()
			{@Override public ItemStack input() {return new ItemStack(RSItems.STORAGE_PART, 1, 0);}});
		diskiwbm_recipes.add(new DiskWorkbenchMemory.IRecipePattern()
			{@Override public ItemStack input() {return new ItemStack(RSItems.STORAGE_PART, 1, 1);}});
		diskiwbm_recipes.add(new DiskWorkbenchMemory.IRecipePattern()
			{@Override public ItemStack input() {return new ItemStack(RSItems.STORAGE_PART, 1, 2);}});
		diskiwbm_recipes.add(new DiskWorkbenchMemory.IRecipePattern()
			{@Override public ItemStack input() {return new ItemStack(RSItems.STORAGE_PART, 1, 3);}});
		
		Item i = Item.getByNameOrId("rebornstorage:storagepart");
		if(i != null)
		{
			diskiwbm_recipes.add(new DiskWorkbenchMemory.IRecipePattern()
				{@Override public ItemStack input() {return new ItemStack(i, 1, 0);}});
			diskiwbm_recipes.add(new DiskWorkbenchMemory.IRecipePattern()
				{@Override public ItemStack input() {return new ItemStack(i, 1, 1);}});
			diskiwbm_recipes.add(new DiskWorkbenchMemory.IRecipePattern()
				{@Override public ItemStack input() {return new ItemStack(i, 1, 2);}});
			diskiwbm_recipes.add(new DiskWorkbenchMemory.IRecipePattern()
				{@Override public ItemStack input() {return new ItemStack(i, 1, 3);}});
		}
		
		diskiwbm_recipes.add(new DiskWorkbenchMemory.IRecipePattern()
		{@Override public ItemStack input() {return new ItemStack(STItems.item_memory, 1, 0);}});
		
		registry.handleRecipes(DiskWorkbenchMemory.IRecipePattern.class, new DiskWorkbenchMemory.WrapperFactory(), DiskWorkbenchMemory.Category.UID);
		registry.addRecipes(diskiwbm_recipes, DiskWorkbenchMemory.Category.UID);
		registry.addRecipeCatalyst(new ItemStack(STBlocks.block_diskWorkbench), DiskWorkbenchMemory.Category.UID);
		
		//FluidDiskWorkbench
		
		List<FluidDiskWorkbenchMemory.IRecipePattern> diskfwbm_recipes = new ArrayList<FluidDiskWorkbenchMemory.IRecipePattern>();
		
		diskfwbm_recipes.add(new FluidDiskWorkbenchMemory.IRecipePattern()
			{@Override public ItemStack input() {return new ItemStack(RSItems.FLUID_STORAGE_PART, 1, 0);}});
		diskfwbm_recipes.add(new FluidDiskWorkbenchMemory.IRecipePattern()
			{@Override public ItemStack input() {return new ItemStack(RSItems.FLUID_STORAGE_PART, 1, 1);}});
		diskfwbm_recipes.add(new FluidDiskWorkbenchMemory.IRecipePattern()
			{@Override public ItemStack input() {return new ItemStack(RSItems.FLUID_STORAGE_PART, 1, 2);}});
		diskfwbm_recipes.add(new FluidDiskWorkbenchMemory.IRecipePattern()
			{@Override public ItemStack input() {return new ItemStack(RSItems.FLUID_STORAGE_PART, 1, 3);}});
		
		if(i != null)
		{
			diskfwbm_recipes.add(new FluidDiskWorkbenchMemory.IRecipePattern()
				{@Override public ItemStack input() {return new ItemStack(i, 1, 4);}});
			diskfwbm_recipes.add(new FluidDiskWorkbenchMemory.IRecipePattern()
				{@Override public ItemStack input() {return new ItemStack(i, 1, 5);}});
			diskfwbm_recipes.add(new FluidDiskWorkbenchMemory.IRecipePattern()
				{@Override public ItemStack input() {return new ItemStack(i, 1, 6);}});
			diskfwbm_recipes.add(new FluidDiskWorkbenchMemory.IRecipePattern()
				{@Override public ItemStack input() {return new ItemStack(i, 1, 7);}});
		}
		
		diskfwbm_recipes.add(new FluidDiskWorkbenchMemory.IRecipePattern()
		{@Override public ItemStack input() {return new ItemStack(STItems.item_memory, 1, 1);}});
		
		registry.handleRecipes(FluidDiskWorkbenchMemory.IRecipePattern.class, new FluidDiskWorkbenchMemory.WrapperFactory(), FluidDiskWorkbenchMemory.Category.UID);
		registry.addRecipes(diskfwbm_recipes, FluidDiskWorkbenchMemory.Category.UID);
		registry.addRecipeCatalyst(new ItemStack(STBlocks.block_fluiddiskWorkbench), FluidDiskWorkbenchMemory.Category.UID);
				
				
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		registry.addRecipeCategories(new DiskWorkbench.Category(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeCategories(new FluidDiskWorkbench.Category(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeCategories(new DiskWorkbenchMemory.Category(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeCategories(new FluidDiskWorkbenchMemory.Category(registry.getJeiHelpers().getGuiHelper()));
		
	}
}
