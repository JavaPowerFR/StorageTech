package javapower.storagetech.jei;

import java.util.ArrayList;
import java.util.List;

import com.refinedmods.refinedstorage.RSItems;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.item.STItems;
import javapower.storagetech.screen.ScreenPartsCombiner;
import javapower.storagetech.setup.CommonSetup;
import javapower.storagetech.util.DiskUtils;
import javapower.storagetech.util.EPartType;
import javapower.storagetech.util.PartValue;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
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
		
		registry.addRecipeCategories(new PartsCombiner.Category(guiHelper));
	}
	
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry)
	{
		registry.addRecipeClickArea(ScreenPartsCombiner.class, 97, 42, 22, 15, PartsCombiner.Category.UID);
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registry)
	{
		List<ItemStack> partsItem = new ArrayList<ItemStack>();
		List<ItemStack> partsFluid = new ArrayList<ItemStack>();
		List<ItemStack> partsEnergy = new ArrayList<ItemStack>();
		List<ItemStack> partsChemical = new ArrayList<ItemStack>();
		
		for(PartValue part : DiskUtils.getParts())
		{
			if(part.getType() == EPartType.ITEM)
				partsItem.add(new ItemStack(part.getItem()));
			if(part.getType() == EPartType.FLUID)
				partsFluid.add(new ItemStack(part.getItem()));
			if(part.getType() == EPartType.ENERGY)
				partsEnergy.add(new ItemStack(part.getItem()));
			if(part.getType() == EPartType.CHEMICAL)
				partsChemical.add(new ItemStack(part.getItem()));
			
		}
		List<PartsCombiner.Wrapper> recipesPartsCombiner = new ArrayList<PartsCombiner.Wrapper>();
		
		recipesPartsCombiner.add(new PartsCombiner.Wrapper(partsItem, partsItem, new ItemStack(STItems.item_custom_storage_part)));
		recipesPartsCombiner.add(new PartsCombiner.Wrapper(partsFluid, partsFluid, new ItemStack(STItems.item_custom_fluid_storage_part)));
		recipesPartsCombiner.add(new PartsCombiner.Wrapper(partsEnergy, partsEnergy, new ItemStack(STItems.item_custom_energy_storage_part)));
		
		/*if(StorageTech.MOD_MEKANISM_IS_LOADED)
			recipesPartsCombiner.add(new PartsCombiner.Wrapper(partsChemical, partsChemical, new ItemStack(javapower.storagetech.mekanism.item.MKItems.item_custom_chemical_storage_part)));*/
		
		registry.addRecipes(recipesPartsCombiner, PartsCombiner.Category.UID);
		
		// ----------------
		
		List<ICraftingRecipe> recipes_builder = new ArrayList<ICraftingRecipe>();
		
		//cell recipe
		NonNullList<Ingredient> ing_cell = NonNullList.create();
		
		ing_cell.add(Ingredient.fromItems(STItems.item_energy_storage_housing));
		
		ing_cell.add(Ingredient.fromItems(
				STItems.item_10p_energy_io_interface,
				STItems.item_20p_energy_io_interface,
				STItems.item_40p_energy_io_interface,
				STItems.item_80p_energy_io_interface
				));
		ing_cell.add(Ingredient.fromItems(
				STItems.item_100k_energy_storage_part,
				STItems.item_400k_energy_storage_part,
				STItems.item_1M6_energy_storage_part,
				STItems.item_6M4_energy_storage_part,
				STItems.item_25M6_energy_storage_part,
				STItems.item_102M4_energy_storage_part,
				STItems.item_custom_energy_storage_part
				));
		
		recipes_builder.add(new SimpleShaplessCrafting(ing_cell,
				new ItemStack(STItems.item_energy_storage_cell),
				CommonSetup.CRAFTING_STORAGETECH_CELL.getRegistryName(),
				CommonSetup.CRAFTING_STORAGETECH_CELL));
		
		//custom disk recipe
		NonNullList<Ingredient> ing_diskA = NonNullList.create();
		ing_diskA.add(Ingredient.fromItems(RSItems.STORAGE_HOUSING.get()));
		ing_diskA.add(Ingredient.fromItems(STItems.item_custom_storage_part));
		
		recipes_builder.add(new SimpleShaplessCrafting(ing_diskA,
				new ItemStack(STItems.item_diskcustom),
				CommonSetup.CRAFTING_CUSTOM_DISK.getRegistryName(),
				CommonSetup.CRAFTING_CUSTOM_DISK));
		
		NonNullList<Ingredient> ing_diskB = NonNullList.create();
		ing_diskB.add(Ingredient.fromItems(RSItems.STORAGE_HOUSING.get()));
		ing_diskB.add(Ingredient.fromItems(STItems.item_custom_fluid_storage_part));
		
		recipes_builder.add(new SimpleShaplessCrafting(ing_diskB,
				new ItemStack(STItems.item_fluiddiskcustom),
				CommonSetup.CRAFTING_CUSTOM_DISK.getRegistryName(),
				CommonSetup.CRAFTING_CUSTOM_DISK));
		
		/*if(StorageTech.MOD_MEKANISM_IS_LOADED)
		{
			NonNullList<Ingredient> ing_diskC = NonNullList.create();
			ing_diskC.add(Ingredient.fromItems(RSItems.STORAGE_HOUSING));
			ing_diskC.add(Ingredient.fromItems(javapower.storagetech.mekanism.item.MKItems.item_custom_chemical_storage_part));
			
			recipes_builder.add(new SimpleShaplessCrafting(ing_diskC,
					new ItemStack(javapower.storagetech.mekanism.item.MKItems.item_custom_chemical_disk),
					CommonSetup.CRAFTING_CUSTOM_DISK.getRegistryName(),
					CommonSetup.CRAFTING_CUSTOM_DISK));
		}*/
		
		registry.addRecipes(recipes_builder, VanillaRecipeCategoryUid.CRAFTING);
		// ----------------
		
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry)
	{
		registry.addRecipeCatalyst(new ItemStack(STBlocks.blockPartsCombiner), PartsCombiner.Category.UID);
	}

}
