package com.misterd.agritechevolved.datamap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataMapLoader extends SimplePreparableReloadListener<Void> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final ICondition.IContext context;

    public DataMapLoader(ICondition.IContext context) {
        this.context = context;
    }

    @Override
    protected Void prepare(ResourceManager manager, ProfilerFiller profiler) {
        return null;
    }

    @Override
    protected void apply(Void nothing, ResourceManager manager, ProfilerFiller profiler) {
        Map<Item, FertilizerData> fertilizers = load(manager, ATEDataMaps.FERTILIZERS_ID, FertilizerData.CODEC);
        Map<Item, SoilModifierData> soilModifiers = load(manager, ATEDataMaps.SOIL_MODIFIERS_ID, SoilModifierData.CODEC);
        ATEDataMaps.setFertilizers(fertilizers);
        ATEDataMaps.setSoilModifiers(soilModifiers);
        LOGGER.info("Loaded {} fertilizers and {} soil modifiers", fertilizers.size(), soilModifiers.size());
    }

    private <T> Map<Item, T> load(ResourceManager manager, ResourceLocation id, Codec<T> codec) {
        ResourceLocation file = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "data_maps/item/" + id.getPath() + ".json");
        Map<Item, T> result = new LinkedHashMap<>();
        for (Resource resource : manager.getResourceStack(file)) {
            try (Reader reader = resource.openAsReader()) {
                JsonObject root = GsonHelper.parse(reader);
                if (GsonHelper.getAsBoolean(root, "replace", false)) result.clear();
                JsonObject values = GsonHelper.getAsJsonObject(root, "values", new JsonObject());
                for (Map.Entry<String, JsonElement> entry : values.entrySet()) {
                    readEntry(file, entry.getKey(), entry.getValue(), codec, result);
                }
                if (root.has("remove")) {
                    for (JsonElement element : GsonHelper.getAsJsonArray(root, "remove")) {
                        Item item = lookup(element.getAsString());
                        if (item != null) result.remove(item);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load data map {} from pack {}", file, resource.sourcePackId(), e);
            }
        }
        return result;
    }

    private <T> void readEntry(ResourceLocation file, String key, JsonElement element, Codec<T> codec, Map<Item, T> result) {
        if (key.startsWith("#")) {
            LOGGER.warn("Tag keys are not supported in data map {}: {}", file, key);
            return;
        }
        JsonElement valueJson = element;
        if (element.isJsonObject() && element.getAsJsonObject().has("value")) {
            JsonObject obj = element.getAsJsonObject();
            if (!CraftingHelper.processConditions(obj, "conditions", context)) return;
            valueJson = obj.get("value");
        }
        Item item = lookup(key);
        if (item == null) return;
        codec.parse(JsonOps.INSTANCE, valueJson)
                .resultOrPartial(error -> LOGGER.error("Invalid value for {} in data map {}: {}", key, file, error))
                .ifPresent(value -> result.put(item, value));
    }

    @Nullable
    private static Item lookup(String key) {
        ResourceLocation id = ResourceLocation.tryParse(key);
        if (id == null || !ForgeRegistries.ITEMS.containsKey(id)) return null;
        return ForgeRegistries.ITEMS.getValue(id);
    }
}