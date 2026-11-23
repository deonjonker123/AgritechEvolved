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

public class TreeRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final Ingredient sapling;
    private final List<Ingredient> soils;
    private final List<DropEntry> drops;

    public TreeRecipe(ResourceLocation id, Ingredient sapling, List<Ingredient> soils, List<DropEntry> drops) {
        this.id = id;
        this.sapling = sapling;
        this.soils = soils;
        this.drops = drops;
    }

    public Ingredient getSapling() { return sapling; }
    public List<Ingredient> getSoils() { return soils; }
    public List<DropEntry> getDrops() { return drops; }

    public boolean matchesSapling(ItemStack stack) {
        return sapling.test(stack);
    }

    public boolean matchesSoil(ItemStack stack) {
        for (Ingredient soil : soils) {
            if (soil.test(stack)) return true;
        }
        return false;
    }

    @Override
    public boolean matches(Container input, Level level) {
        return sapling.test(input.getItem(0));
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
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public boolean isSpecial() {
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
    public RecipeSerializer<TreeRecipe> getSerializer() {
        return ATERecipeTypes.TREE_SERIALIZER.get();
    }

    @Override
    public RecipeType<TreeRecipe> getType() {
        return ATERecipeTypes.TREE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<TreeRecipe> {

        @Override
        public TreeRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient sapling = Ingredient.fromJson(GsonHelper.getNonNull(json, "sapling"), false);
            List<Ingredient> soils = PlanterRecipeIO.ingredientsFromJson(GsonHelper.getAsJsonArray(json, "soils"));
            List<DropEntry> drops = DropEntry.listFromJson(GsonHelper.getAsJsonArray(json, "drops"));
            return new TreeRecipe(id, sapling, soils, drops);
        }

        @Override
        public TreeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient sapling = Ingredient.fromNetwork(buf);
            List<Ingredient> soils = PlanterRecipeIO.ingredientsFromNetwork(buf);
            List<DropEntry> drops = PlanterRecipeIO.dropsFromNetwork(buf);
            return new TreeRecipe(id, sapling, soils, drops);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, TreeRecipe recipe) {
            recipe.sapling.toNetwork(buf);
            PlanterRecipeIO.ingredientsToNetwork(buf, recipe.soils);
            PlanterRecipeIO.dropsToNetwork(buf, recipe.drops);
        }
    }
}