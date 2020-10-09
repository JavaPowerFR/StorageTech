package javapower.storagetech.jei;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import javapower.storagetech.core.ResourceLocationRegister;
import javapower.storagetech.core.StorageTech;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class PartsCombiner
{
	public static class Category implements IRecipeCategory<Wrapper>
	{
		
		public static final ResourceLocation UID = new ResourceLocation(StorageTech.MODID,"partscombiner");
		private String localizedName;
		private IDrawableStatic background;
		private IDrawableAnimated process_draw;
		
		public Category(IGuiHelper guiHelper)
		{
			localizedName = I18n.format("jei."+StorageTech.MODID+".category.partscombiner");
			
			background = guiHelper.createDrawable(ResourceLocationRegister.textrue_gui_jei_recipe, 0, 0, 112, 32);
			process_draw = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(ResourceLocationRegister.textrue_gui_jei_recipe, 0, 32, 22, 16), 50, StartDirection.LEFT, false);
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
		public void draw(Wrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY)
		{
			process_draw.draw(matrixStack, 60, 8);
		}

		@Override
		public void setIngredients(Wrapper recipe, IIngredients ingredients)
		{
			ingredients.setInputLists(VanillaTypes.ITEM, recipe.inputs);
			
			ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
		}

		@Override
		public void setRecipe(IRecipeLayout recipeLayout, Wrapper recipe, IIngredients ingredients)
		{
			IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
			itemStacks.init(0, true, 1, 7);
			itemStacks.init(1, true, 35, 7);
			
			itemStacks.init(2, false, 89, 7);
			
			itemStacks.set(ingredients);
		}
		
	}
	
	public static class Wrapper
	{
		public List<List<ItemStack>> inputs;
		
		private ItemStack output;
		
		public Wrapper(List<ItemStack> _inputsA, List<ItemStack> _inputsB, ItemStack _output)
		{
			inputs = new ArrayList<List<ItemStack>>();
			inputs.add(_inputsA);
			inputs.add(_inputsB);
			
			output = _output;
		}
		
		public void getIngredients(IIngredients ing)
		{
			ing.setInputLists(VanillaTypes.ITEM, inputs);
			
			ing.setOutput(VanillaTypes.ITEM, output);
		}
	}
}
