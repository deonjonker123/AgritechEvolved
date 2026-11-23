package com.misterd.agritechevolved.recipe;

import com.misterd.agritechevolved.AgritechEvolved;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ATERecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, AgritechEvolved.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AgritechEvolved.MODID);

    public static final RegistryObject<RecipeType<CropRecipe>> CROP_TYPE =
            RECIPE_TYPES.register("crop", () -> new RecipeType<>() {
                @Override
                public String toString() { return AgritechEvolved.MODID + ":crop"; }
            });

    public static final RegistryObject<RecipeType<TreeRecipe>> TREE_TYPE =
            RECIPE_TYPES.register("tree", () -> new RecipeType<>() {
                @Override
                public String toString() { return AgritechEvolved.MODID + ":tree"; }
            });

    public static final RegistryObject<RecipeSerializer<CropRecipe>> CROP_SERIALIZER =
            RECIPE_SERIALIZERS.register("crop", CropRecipe.Serializer::new);

    public static final RegistryObject<RecipeSerializer<TreeRecipe>> TREE_SERIALIZER =
            RECIPE_SERIALIZERS.register("tree", TreeRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }
}