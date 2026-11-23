package com.misterd.agritechevolved.recipe;

import com.misterd.agritechevolved.AgritechEvolved;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ATERecipe {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AgritechEvolved.MODID);

    public static final RegistryObject<RecipeSerializer<DurabilityShapelessRecipe>> DURABILITY_SHAPELESS_SERIALIZER =
            RECIPE_SERIALIZERS.register("durability_shapeless", DurabilityShapelessRecipeSerializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
        ATERecipeTypes.register(eventBus);
    }
}