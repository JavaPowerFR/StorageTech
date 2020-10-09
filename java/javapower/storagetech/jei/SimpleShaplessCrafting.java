package javapower.storagetech.jei;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SimpleShaplessCrafting implements ICraftingRecipe
{
	private NonNullList<Ingredient> ing;
	private ItemStack result;
	private ResourceLocation id;
	private IRecipeSerializer<?> serializer;
	
	public SimpleShaplessCrafting(NonNullList<Ingredient> _ing, ItemStack _result, ResourceLocation _id, IRecipeSerializer<?> _serializer)
	{
		ing = _ing;
		result = _result;
		id = _id;
		serializer = _serializer;
	}
	
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
		return serializer;
	}
	
	@Override
	public ItemStack getRecipeOutput()
	{
		return result;
	}
	
	@Override
	public ResourceLocation getId()
	{
		return id;
	}
	
	@Override
	public ItemStack getCraftingResult(CraftingInventory inv)
	{
		return result;
	}
	
	@Override
	public boolean canFit(int width, int height)
	{
		return width*height >= ing.size();
	}
}
