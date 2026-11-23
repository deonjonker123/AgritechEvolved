package com.misterd.agritechevolved.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class CropRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final Ingredient seed;
    private final List<Ingredient> soils;
    private final List<DropEntry> drops;

    public CropRecipe(ResourceLocation id, Ingredient seed, List<Ingredient> soils, List<DropEntry> drops) {
        this.id = id;
        this.seed = seed;
        this.soils = soils;
        this.drops = drops;
    }

    public Ingredient getSeed() { return seed; }
    public List<Ingredient> getSoils() { return soils; }
    public List<DropEntry> getDrops() { return drops; }

    public boolean matchesSeed(ItemStack stack) {
        return seed.test(stack);
    }

    public boolean matchesSoil(ItemStack stack) {
        for (Ingredient soil : soils) {
            if (soil.test(stack)) return true;
        }
        return false;
    }

    @Override
    public boolean matches(Container input, Level level) {
        return seed.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(Container input, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public String group() {
        return "";
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<CropRecipe> getSerializer() {
        return ATERecipeTypes.CROP_SERIALIZER.get();
    }

    @Override
    public RecipeType<CropRecipe> getType() {
        return ATERecipeTypes.CROP_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CropRecipe> {

        @Override
        public CropRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient seed = Ingredient.fromJson(GsonHelper.getNonNull(json, "seed"), false);
            List<Ingredient> soils = PlanterRecipeIO.ingredientsFromJson(GsonHelper.getAsJsonArray(json, "soils"));
            List<DropEntry> drops = DropEntry.listFromJson(GsonHelper.getAsJsonArray(json, "drops"));
            return new CropRecipe(id, seed, soils, drops);
        }

        @Override
        public CropRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient seed = Ingredient.fromNetwork(buf);
            List<Ingredient> soils = PlanterRecipeIO.ingredientsFromNetwork(buf);
            List<DropEntry> drops = PlanterRecipeIO.dropsFromNetwork(buf);
            return new CropRecipe(id, seed, soils, drops);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CropRecipe recipe) {
            recipe.seed.toNetwork(buf);
            PlanterRecipeIO.ingredientsToNetwork(buf, recipe.soils);
            PlanterRecipeIO.dropsToNetwork(buf, recipe.drops);
        }
    }
}