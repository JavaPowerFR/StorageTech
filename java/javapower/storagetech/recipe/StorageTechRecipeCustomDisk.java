package javapower.storagetech.recipe;

import com.refinedmods.refinedstorage.RSItems;

import javapower.storagetech.api.ICustomStoragePart;
import javapower.storagetech.setup.CommonSetup;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class StorageTechRecipeCustomDisk extends SpecialRecipe
{

	public StorageTechRecipeCustomDisk(ResourceLocation idIn)
	{
		super(idIn);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn)
	{
		int matcher = 0;
		
		for(int i = 0; i < 9; ++i)
		{
			ItemStack stack = inv.getStackInSlot(i);
			
			if(stack.getItem().equals(RSItems.STORAGE_HOUSING))
			{
				if((matcher & 1) == 0) matcher |= 1; else matcher |= 8;
			}
			else if(stack.getItem() instanceof ICustomStoragePart)
			{
				if((matcher & 2) == 0) matcher |= 2; else matcher |= 8;
			}
			else if(!stack.isEmpty())
				 matcher |= 8;
		
		}
		
		return matcher == 3;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv)
	{
		int matcher = 0;
		
		ICustomStoragePart part = null;
		ItemStack partStack = ItemStack.EMPTY;
		
		for(int i = 0; i < inv.getSizeInventory(); ++i)
		{
			ItemStack stack = inv.getStackInSlot(i);
			
			if(stack.getItem().equals(RSItems.STORAGE_HOUSING))
			{
				if((matcher & 1) == 0) matcher |= 1; else matcher |= 8;
			}
			else if(stack.getItem() instanceof ICustomStoragePart)
			{
				if((matcher & 2) == 0) matcher |= 2; else matcher |= 8;
				part = (ICustomStoragePart) stack.getItem();
				partStack = stack;
			}
			else if(!stack.isEmpty())
				 matcher |= 8;
		
		}
		
		if(matcher == 3 && part != null)
		{
			return part.createDisk(partStack);
		}
		
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return width*height >= 3;
	}

	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		return CommonSetup.CRAFTING_STORAGETECH_CELL;
	}

}
