package com.misterd.agritechevolved.compat.jei;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.recipe.ATERecipeTypes;
import com.misterd.agritechevolved.recipe.CropRecipe;
import com.misterd.agritechevolved.recipe.TreeRecipe;
import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.ComposterBlock;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ATJeiPlugin implements IModPlugin {

    private static final Identifier PLUGIN_ID =
            Identifier.fromNamespaceAndPath("agritechevolved", "jei_plugin");

    private static final float DENSE_THRESHOLD = 0.65f;

    private static IJeiRuntime jeiRuntime;

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new PlanterRecipeCategory(guiHelper),
                new CompostRecipeCategory(guiHelper),
                new FarmlandRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(PlanterRecipeCategory.PLANTER_RECIPE_TYPE, generatePlanterRecipes());
        registration.addRecipes(CompostRecipeCategory.COMPOST_RECIPE_TYPE, generateCompostRecipes());
        registration.addRecipes(FarmlandRecipeCategory.FARMLAND_RECIPE_TYPE, generateFarmlandRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(PlanterRecipeCategory.PLANTER_RECIPE_TYPE, ATEBlocks.OAK_PLANTER);
        registration.addCraftingStation(CompostRecipeCategory.COMPOST_RECIPE_TYPE, ATEBlocks.COMPOSTER);
        registration.addCraftingStation(FarmlandRecipeCategory.FARMLAND_RECIPE_TYPE, new ItemStack(Items.DIAMOND_HOE));
    }

    private List<PlanterRecipe> generatePlanterRecipes() {
        List<PlanterRecipe> recipes = new ArrayList<>();
        recipes.addAll(generateCropRecipes());
        recipes.addAll(generateTreeRecipes());
        LogUtils.getLogger().info("Generated {} total planter recipes for JEI", recipes.size());
        return recipes;
    }

    private RecipeManager getRecipeManager() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;
        if (mc.level.recipeAccess() instanceof RecipeManager rm) return rm;
        return null;
    }

    private List<PlanterRecipe> generateCropRecipes() {
        List<PlanterRecipe> recipes = new ArrayList<>();
        RecipeManager rm = getRecipeManager();
        if (rm == null) {
            LogUtils.getLogger().warn("RecipeManager unavailable during JEI crop recipe generation");
            return recipes;
        }
        for (RecipeHolder<?> holder : rm.getRecipes()) {
            if (holder.value().getType() != ATERecipeTypes.CROP_TYPE.get()) continue;
            try {
                recipes.add(PlanterRecipe.fromCrop((CropRecipe) holder.value()));
            } catch (Exception e) {
                LogUtils.getLogger().error("Error creating JEI crop recipe for {}: {}", holder.id(), e.getMessage());
            }
        }
        LogUtils.getLogger().info("Generated {} crop planter recipes for JEI", recipes.size());
        return recipes;
    }

    private List<PlanterRecipe> generateTreeRecipes() {
        List<PlanterRecipe> recipes = new ArrayList<>();
        RecipeManager rm = getRecipeManager();
        if (rm == null) {
            LogUtils.getLogger().warn("RecipeManager unavailable during JEI tree recipe generation");
            return recipes;
        }
        for (RecipeHolder<?> holder : rm.getRecipes()) {
            if (holder.value().getType() != ATERecipeTypes.TREE_TYPE.get()) continue;
            try {
                recipes.add(PlanterRecipe.fromTree((TreeRecipe) holder.value()));
            } catch (Exception e) {
                LogUtils.getLogger().error("Error creating JEI tree recipe for {}: {}", holder.id(), e.getMessage());
            }
        }
        LogUtils.getLogger().info("Generated {} tree planter recipes for JEI", recipes.size());
        return recipes;
    }

    private List<CompostRecipe> generateCompostRecipes() {
        List<CompostRecipe> recipes = new ArrayList<>();
        int denseCount = 0;

        for (var entry : ComposterBlock.COMPOSTABLES.object2FloatEntrySet()) {
            String itemId = BuiltInRegistries.ITEM.getKey(entry.getKey().asItem()).toString();
            float chance = entry.getFloatValue();
            try {
                CompostRecipe recipe = chance >= DENSE_THRESHOLD
                        ? CompostRecipe.createDense(itemId)
                        : CompostRecipe.create(itemId);
                if (recipe != null) {
                    recipes.add(recipe);
                    if (chance >= DENSE_THRESHOLD) denseCount++;
                }
            } catch (Exception e) {
                LogUtils.getLogger().error("Failed to create compost recipe for {}: {}", itemId, e.getMessage());
            }
        }

        LogUtils.getLogger().info("Generated {} compost recipes for JEI ({} dense)", recipes.size(), denseCount);
        return recipes;
    }

    private List<FarmlandRecipe> generateFarmlandRecipes() {
        List<FarmlandRecipe> recipes = new ArrayList<>();
        try {
            Ingredient hoe = Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(ItemTags.HOES));

            addTilling(recipes, hoe, Items.DIRT, Items.FARMLAND);
            addTilling(recipes, hoe, Items.ROOTED_DIRT, Items.FARMLAND);
            addTilling(recipes, hoe, Items.COARSE_DIRT, Items.FARMLAND);
            addTilling(recipes, hoe, Items.GRASS_BLOCK, Items.FARMLAND);
            addTilling(recipes, hoe, ATEBlocks.MULCH.get().asItem(), ATEBlocks.INFUSED_FARMLAND.get().asItem());
            addTillingModded(recipes, hoe, "farmersdelight:rich_soil", "farmersdelight:rich_soil_farmland");
        } catch (Exception e) {
            LogUtils.getLogger().error("Failed to generate farmland recipes for JEI: {}", e.getMessage());
        }
        LogUtils.getLogger().info("Generated {} farmland recipes for JEI", recipes.size());
        return recipes;
    }

    private void addTilling(List<FarmlandRecipe> recipes, Ingredient hoe, net.minecraft.world.item.Item input, net.minecraft.world.item.Item result) {
        recipes.add(new FarmlandRecipe(Ingredient.of(input), hoe, new ItemStack(result)));
    }

    private void addTillingModded(List<FarmlandRecipe> recipes, Ingredient hoe, String inputId, String resultId) {
        try {
            var inputOpt = BuiltInRegistries.ITEM.get(Identifier.parse(inputId));
            var resultOpt = BuiltInRegistries.ITEM.get(Identifier.parse(resultId));
            if (inputOpt.isEmpty() || inputOpt.get().value() == Items.AIR) return;
            if (resultOpt.isEmpty() || resultOpt.get().value() == Items.AIR) return;
            addTilling(recipes, hoe, inputOpt.get().value(), resultOpt.get().value());
        } catch (Exception e) {
            LogUtils.getLogger().error("Failed to add modded tilling recipe for {} -> {}: {}", inputId, resultId, e.getMessage());
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }

    public static IJeiRuntime getJeiRuntime() {
        return jeiRuntime;
    }
}