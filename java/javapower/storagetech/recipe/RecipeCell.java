package javapower.storagetech.recipe;

import javapower.storagetech.api.IItemEnergyStoragePart;
import javapower.storagetech.item.ItemEnergyCell;
import javapower.storagetech.item.ItemEnergyInterface;
import javapower.storagetech.item.STItems;
import javapower.storagetech.setup.CommonSetup;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeCell extends SpecialRecipe
{

	public RecipeCell(ResourceLocation idIn)
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
			
			if(stack.getItem().equals(STItems.item_energy_storage_housing))
			{
				if((matcher & 1) == 0) matcher |= 1; else matcher |= 8;
			}
			else if(stack.getItem() instanceof IItemEnergyStoragePart)
			{
				if((matcher & 2) == 0) matcher |= 2; else matcher |= 8;
			}
			else if(stack.getItem() instanceof ItemEnergyInterface)
			{
				if((matcher & 4) == 0) matcher |= 4; else matcher |= 8;
			}
			else if(!stack.isEmpty())
				 matcher |= 8;
		
		}
		
		return matcher == 7;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv)
	{
		int matcher = 0;
		
		long iocap_ef = 0;
		int cap = 0;
		
		for(int i = 0; i < inv.getSizeInventory(); ++i)
		{
			ItemStack stack = inv.getStackInSlot(i);
			
			if(stack.getItem().equals(STItems.item_energy_storage_housing))
			{
				if((matcher & 1) == 0) matcher |= 1; else matcher |= 8;
			}
			else if(stack.getItem() instanceof IItemEnergyStoragePart)
			{
				if((matcher & 2) == 0) matcher |= 2; else matcher |= 8;
				cap = ((IItemEnergyStoragePart)stack.getItem()).getSize(stack);
			}
			else if(stack.getItem() instanceof ItemEnergyInterface)
			{
				if((matcher & 4) == 0) matcher |= 4; else matcher |= 8;
				iocap_ef = ((ItemEnergyInterface)stack.getItem()).getPercentages();
			}
			else if(!stack.isEmpty())
				 matcher |= 8;
		
		}
		
		if(matcher == 7)
		{
			return ItemEnergyCell.createItem(cap, (int)((cap*iocap_ef)/100));
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
