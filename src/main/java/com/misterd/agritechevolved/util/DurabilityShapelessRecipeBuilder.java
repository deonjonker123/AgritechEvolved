package com.misterd.agritechevolved.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.misterd.agritechevolved.recipe.ATERecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class DurabilityShapelessRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final int count;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private Ingredient toolIngredient = Ingredient.EMPTY;
    private int durabilityPerItem = 1;
    @Nullable
    private String group;
    private CraftingBookCategory category = CraftingBookCategory.MISC;

    public DurabilityShapelessRecipeBuilder(ItemLike result, int count) {
        this.result = result.asItem();
        this.count = count;
    }

    public static DurabilityShapelessRecipeBuilder shapeless(ItemLike result) {
        return new DurabilityShapelessRecipeBuilder(result, 1);
    }

    public static DurabilityShapelessRecipeBuilder shapeless(ItemLike result, int count) {
        return new DurabilityShapelessRecipeBuilder(result, count);
    }

    public DurabilityShapelessRecipeBuilder requires(ItemLike item) {
        return requires(item, 1);
    }

    public DurabilityShapelessRecipeBuilder requires(ItemLike item, int count) {
        for (int i = 0; i < count; i++) {
            ingredients.add(Ingredient.of(item));
        }
        return this;
    }

    public DurabilityShapelessRecipeBuilder requires(Ingredient ingredient) {
        ingredients.add(ingredient);
        return this;
    }

    public DurabilityShapelessRecipeBuilder requires(TagKey<Item> tag) {
        ingredients.add(Ingredient.of(tag));
        return this;
    }

    public DurabilityShapelessRecipeBuilder tool(ItemLike tool) {
        this.toolIngredient = Ingredient.of(tool);
        return this;
    }

    public DurabilityShapelessRecipeBuilder tool(Ingredient tool) {
        this.toolIngredient = tool;
        return this;
    }

    public DurabilityShapelessRecipeBuilder durabilityPerItem(int durability) {
        this.durabilityPerItem = durability;
        return this;
    }

    public DurabilityShapelessRecipeBuilder category(CraftingBookCategory category) {
        this.category = category;
        return this;
    }

    @Override
    public DurabilityShapelessRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        advancement.addCriterion(name, criterion);
        return this;
    }

    @Override
    public DurabilityShapelessRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> output, ResourceLocation id) {
        ensureValid(id);
        advancement.parent(ROOT_RECIPE_ADVANCEMENT)
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR);

        String categoryPath = switch (category) {
            case BUILDING -> "building_blocks";
            case REDSTONE -> "redstone";
            case EQUIPMENT -> "equipment";
            case MISC -> "misc";
        };

        output.accept(new Result(id, result, count, group == null ? "" : group, category, ingredients, toolIngredient, durabilityPerItem, advancement, id.withPrefix("recipes/" + categoryPath + "/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
        if (toolIngredient.isEmpty()) {
            throw new IllegalStateException("No tool specified for recipe " + id);
        }
    }

    private record Result(ResourceLocation id, Item result, int count, String group, CraftingBookCategory category,
                          NonNullList<Ingredient> ingredients, Ingredient tool, int durabilityPerItem,
                          Advancement.Builder advancement, ResourceLocation advancementId) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!group.isEmpty()) json.addProperty("group", group);
            json.addProperty("category", category.getSerializedName());
            JsonArray ingredientArray = new JsonArray();
            for (Ingredient ingredient : ingredients) {
                ingredientArray.add(ingredient.toJson());
            }
            json.add("ingredients", ingredientArray);
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(result).toString());
            if (count > 1) resultJson.addProperty("count", count);
            json.add("result", resultJson);
            json.add("tool", tool.toJson());
            json.addProperty("durability_per_item", durabilityPerItem);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ATERecipe.DURABILITY_SHAPELESS_SERIALIZER.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return advancementId;
        }
    }
}