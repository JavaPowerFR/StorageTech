package javapower.storagetech.jei;

import java.util.ArrayList;
import java.util.List;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.RSItems;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.item.STItems;
import javapower.storagetech.screen.ScreenContainerDiskWorkbench;
import javapower.storagetech.screen.ScreenContainerFluidDiskWorkbench;
import javapower.storagetech.setup.CommonSetup;
import javapower.storagetech.util.DiskUtils;
import javapower.storagetech.util.PartValue;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

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
	
	@Override
	public void registerRecipes(IRecipeRegistration registry)
	{
		List<DiskWorkbench.Wrapper> recipes_diskWorkbench = new ArrayList<DiskWorkbench.Wrapper>();
		
		recipes_diskWorkbench.add(new DiskWorkbench.Wrapper(new ItemStack(RSItems.STORAGE_HOUSING), new ItemStack(STItems.item_diskcustom)));
		recipes_diskWorkbench.add(new DiskWorkbench.Wrapper(new ItemStack(RSBlocks.MACHINE_CASING), new ItemStack(STBlocks.blockCustomStorage)));
		
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
		recipes_fluidDiskWorkbench.add(new FluidDiskWorkbench.Wrapper(new ItemStack(RSBlocks.MACHINE_CASING), new ItemStack(STBlocks.blockCustomFluidStorage)));
		
		registry.addRecipes(recipes_fluidDiskWorkbench, FluidDiskWorkbench.Category.UID);
		
		// ----------------
		
		List<FluidDiskWorkbenchMemory.Wrapper> recipes_fluidDiskWorkbenchMemory = new ArrayList<FluidDiskWorkbenchMemory.Wrapper>();
		
		for(PartValue item : DiskUtils.getFluidParts())
			recipes_fluidDiskWorkbenchMemory.add(new FluidDiskWorkbenchMemory.Wrapper(new ItemStack(item.getItem()), item.getValue()));
		
		recipes_fluidDiskWorkbenchMemory.add(new FluidDiskWorkbenchMemory.Wrapper(new ItemStack(STItems.item_memory_fluid), -1));
		
		registry.addRecipes(recipes_fluidDiskWorkbenchMemory, FluidDiskWorkbenchMemory.Category.UID);
		
		// ----------------
		
		List<ICraftingRecipe> recipes_energy_cell_builder = new ArrayList<ICraftingRecipe>();
		NonNullList<Ingredient> ing = NonNullList.create();
		
		ing.add(Ingredient.fromItems(STItems.item_energy_storage_housing));
		ing.add(Ingredient.fromItems(
				STItems.item_10p_energy_io_interface,
				STItems.item_20p_energy_io_interface,
				STItems.item_40p_energy_io_interface,
				STItems.item_80p_energy_io_interface
				));
		ing.add(Ingredient.fromItems(
				STItems.item_100k_energy_storage_part,
				STItems.item_400k_energy_storage_part,
				STItems.item_1M6_energy_storage_part,
				STItems.item_6M4_energy_storage_part,
				STItems.item_25M6_energy_storage_part,
				STItems.item_102M4_energy_storage_part
				));
		
		recipes_energy_cell_builder.add(new ICraftingRecipe() {
			
			@Override
			public NonNullList<Ingredient> getIngredients()
			{
				return ing;
			}
			
			@Override
			public boolean matches(CraftingInventory inv, World worldIn)
			{
				return true;
			}
			
			@Override
			public IRecipeSerializer<?> getSerializer()
			{
				return CommonSetup.CRAFTING_STORAGETECH_CELL;
			}
			
			@Override
			public ItemStack getRecipeOutput()
			{
				return new ItemStack(STItems.item_energy_storage_cell);
			}
			
			@Override
			public ResourceLocation getId()
			{
				return CommonSetup.CRAFTING_STORAGETECH_CELL.getRegistryName();
			}
			
			@Override
			public ItemStack getCraftingResult(CraftingInventory inv)
			{
				return new ItemStack(STItems.item_energy_storage_cell);
			}
			
			@Override
			public boolean canFit(int width, int height)
			{
				return width*height >= 3;
			}
		});
		
		registry.addRecipes(recipes_energy_cell_builder, VanillaRecipeCategoryUid.CRAFTING);
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
