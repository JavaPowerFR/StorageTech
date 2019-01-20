package javapower.storagetech.jei;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.proxy.ResourceLocationRegister;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class DiskWorkbenchMemory
{
	public static class Category implements IRecipeCategory<Wrapper>
	{

		public static final String UID = StorageTech.MODID + ".diskiwbm";
		private final String localizedName;
		
		private final IDrawableStatic background;
		
		public Category(IGuiHelper guiHelper)
		{
			localizedName = I18n.format(StorageTech.MODID+".jei.category.diskiwbm");
			background = guiHelper.createDrawable(ResourceLocationRegister.textrue_gui_jei_recipe, 26, 0, 18, 67);
		}

		@Override
		public String getUid()
		{
			return UID;
		}

		@Override
		public String getTitle()
		{
			return localizedName;
		}

		@Override
		public String getModName()
		{
			return "Storage Tech";
		}

		@Override
		public IDrawable getBackground()
		{
			return background;
		}

		@Override
		public void setRecipe(IRecipeLayout recipeLayout, Wrapper recipeWrapper, IIngredients ingredients)
		{
			IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
			
			guiItemStacks.init(0, true, 0, 0);
			guiItemStacks.set(ingredients);

		}
		
	}
	
	public static class Wrapper implements IRecipeWrapper
	{
		private ItemStack input;
		
		public Wrapper(ItemStack _input)
		{
			input = _input;
		}
		
		@Override
		public void getIngredients(IIngredients ing)
		{
			ing.setInput(VanillaTypes.ITEM, input);
		}
	}
	
	public static class WrapperFactory implements IRecipeWrapperFactory<IRecipePattern>
	{
		@Override
		public IRecipeWrapper getRecipeWrapper(IRecipePattern pattern)
		{
			return new Wrapper(pattern.input());
		}
	}
	
	public static interface IRecipePattern
	{
		public ItemStack input();
	}
}
