package com.misterd.agritechevolved.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;

public class DurabilityShapelessRecipeSerializer implements RecipeSerializer<DurabilityShapelessRecipe> {

    @Override
    public DurabilityShapelessRecipe fromJson(ResourceLocation id, JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");
        CraftingBookCategory category = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", null), CraftingBookCategory.MISC);
        ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "ingredients")) {
            Ingredient ingredient = Ingredient.fromJson(element, false);
            if (!ingredient.isEmpty()) ingredients.add(ingredient);
        }
        if (ingredients.isEmpty()) throw new JsonParseException("No ingredients for durability shapeless recipe " + id);
        Ingredient tool = Ingredient.fromJson(GsonHelper.getNonNull(json, "tool"), false);
        int durabilityPerItem = GsonHelper.getAsInt(json, "durability_per_item");
        return new DurabilityShapelessRecipe(id, group, category, result, ingredients, tool, durabilityPerItem);
    }

    @Override
    public DurabilityShapelessRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        String group = buffer.readUtf();
        CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
        ItemStack result = buffer.readItem();
        int ingredientCount = buffer.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
        for (int i = 0; i < ingredientCount; i++) {
            ingredients.set(i, Ingredient.fromNetwork(buffer));
        }
        Ingredient tool = Ingredient.fromNetwork(buffer);
        int durabilityPerItem = buffer.readVarInt();
        return new DurabilityShapelessRecipe(id, group, category, result, ingredients, tool, durabilityPerItem);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, DurabilityShapelessRecipe recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeEnum(recipe.category());
        buffer.writeItem(recipe.getResult());
        buffer.writeVarInt(recipe.getIngredients().size());
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buffer);
        }
        recipe.getToolIngredient().toNetwork(buffer);
        buffer.writeVarInt(recipe.getDurabilityPerItem());
    }
}