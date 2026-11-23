package com.misterd.agritechevolved.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public record DropEntry(Item item, int min, int max, float chance) {

    public static DropEntry fromJson(JsonObject json) {
        Item item = ShapedRecipe.itemFromJson(json);
        int min = GsonHelper.getAsInt(json, "min");
        int max = GsonHelper.getAsInt(json, "max");
        float chance = GsonHelper.getAsFloat(json, "chance");
        return new DropEntry(item, min, max, chance);
    }

    public static List<DropEntry> listFromJson(JsonArray array) {
        List<DropEntry> drops = new ArrayList<>();
        for (JsonElement element : array) {
            drops.add(fromJson(GsonHelper.convertToJsonObject(element, "drop")));
        }
        return List.copyOf(drops);
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item);
        buf.writeVarInt(min);
        buf.writeVarInt(max);
        buf.writeFloat(chance);
    }

    public static DropEntry fromNetwork(FriendlyByteBuf buf) {
        Item item = buf.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
        int min = buf.readVarInt();
        int max = buf.readVarInt();
        float chance = buf.readFloat();
        return new DropEntry(item, min, max, chance);
    }
}