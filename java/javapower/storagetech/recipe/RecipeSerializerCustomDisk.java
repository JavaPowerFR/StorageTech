package javapower.storagetech.recipe;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class RecipeSerializerCustomDisk extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeCustomDisk>
{
    @Override
    public RecipeCustomDisk read(ResourceLocation recipeId, JsonObject json)
    {
        return new RecipeCustomDisk(recipeId);
    }

    @Nullable
    @Override
    public RecipeCustomDisk read(ResourceLocation recipeId, PacketBuffer buffer)
    {
        return new RecipeCustomDisk(recipeId);
    }

    @Override
    public void write(PacketBuffer buffer, RecipeCustomDisk recipe)
    {
    	
    }
}