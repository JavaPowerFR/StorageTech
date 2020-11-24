package javapower.storagetech.recipe;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class RecipeSerializerCell extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeCell>
{
    @Override
    public RecipeCell read(ResourceLocation recipeId, JsonObject json)
    {
        return new RecipeCell(recipeId);
    }

    @Nullable
    @Override
    public RecipeCell read(ResourceLocation recipeId, PacketBuffer buffer)
    {
        return new RecipeCell(recipeId);
    }

    @Override
    public void write(PacketBuffer buffer, RecipeCell recipe)
    {
    	
    }
}