package com.misterd.agritechevolved.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public final class PlanterRecipeIO {

    private PlanterRecipeIO() {}

    public static List<Ingredient> ingredientsFromJson(JsonArray array) {
        List<Ingredient> ingredients = new ArrayList<>();
        for (JsonElement element : array) {
            ingredients.add(Ingredient.fromJson(element, false));
        }
        return List.copyOf(ingredients);
    }

    public static void ingredientsToNetwork(FriendlyByteBuf buf, List<Ingredient> ingredients) {
        buf.writeVarInt(ingredients.size());
        for (Ingredient ingredient : ingredients) {
            ingredient.toNetwork(buf);
        }
    }

    public static List<Ingredient> ingredientsFromNetwork(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Ingredient> ingredients = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ingredients.add(Ingredient.fromNetwork(buf));
        }
        return List.copyOf(ingredients);
    }

    public static void dropsToNetwork(FriendlyByteBuf buf, List<DropEntry> drops) {
        buf.writeVarInt(drops.size());
        for (DropEntry drop : drops) {
            drop.toNetwork(buf);
        }
    }

    public static List<DropEntry> dropsFromNetwork(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<DropEntry> drops = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            drops.add(DropEntry.fromNetwork(buf));
        }
        return List.copyOf(drops);
    }
}