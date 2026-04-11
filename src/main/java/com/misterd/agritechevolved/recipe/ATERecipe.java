package com.misterd.agritechevolved.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ATERecipe {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, "agritechevolved");

    public static final Supplier<RecipeSerializer<DurabilityShapelessRecipe>> DURABILITY_SHAPELESS_SERIALIZER =
            RECIPE_SERIALIZERS.register("durability_shapeless", DurabilityShapelessRecipeSerializer::new);
}
