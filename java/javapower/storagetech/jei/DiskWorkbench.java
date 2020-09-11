package javapower.storagetech.jei;

import javapower.storagetech.core.ResourceLocationRegister;
import javapower.storagetech.core.StorageTech;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DiskWorkbench
{
	public static class Category implements IRecipeCategory<Wrapper>
	{
		
		public static final ResourceLocation UID = new ResourceLocation(StorageTech.MODID,"diskiwb");
		private String localizedName;
		private IDrawableStatic background;
		
		public Category(IGuiHelper guiHelper)
		{
			localizedName = I18n.format(StorageTech.MODID+".jei.category.diskiwb");
			background = guiHelper.createDrawable(ResourceLocationRegister.textrue_gui_jei_recipe, 0, 0, 26, 72);
		}

		@Override
		public ResourceLocation getUid()
		{
			return UID;
		}

		@Override
		public Class<? extends Wrapper> getRecipeClass()
		{
			return Wrapper.class;
		}

		@Override
		public String getTitle()
		{
			return localizedName;
		}

		@Override
		public IDrawable getBackground()
		{
			return background;
		}

		@Override
		public IDrawable getIcon()
		{
			return null;
		}

		@Override
		public void setIngredients(Wrapper recipe, IIngredients ingredients)
		{
			ingredients.setInput(VanillaTypes.ITEM, recipe.input);
			ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
		}

		@Override
		public void setRecipe(IRecipeLayout recipeLayout, Wrapper recipe, IIngredients ingredients)
		{
			IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
			itemStacks.init(0, true, 4, 0);
			itemStacks.init(1, false, 4, 50);
			itemStacks.set(ingredients);
		}
		
	}
	
	public static class Wrapper
	{
		private ItemStack input;
		private ItemStack output;
		
		public Wrapper(ItemStack _input, ItemStack _output)
		{
			input = _input;
			output = _output;
		}
		
		public void getIngredients(IIngredients ing)
		{
			ing.setInput(VanillaTypes.ITEM, input);
			ing.setOutput(VanillaTypes.ITEM, output);
		}
	}
}