package com.misterd.agritechevolved.compat.jei;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.config.CompostableConfig;
import com.misterd.agritechevolved.config.PlantablesConfig;
import com.misterd.agritechevolved.util.RegistryHelper;
import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JeiPlugin
public class ATJeiPlugin implements IModPlugin {

    private static final Identifier PLUGIN_ID =
            Identifier.fromNamespaceAndPath("agritechevolved", "jei_plugin");

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

    private List<PlanterRecipe> generateCropRecipes() {
        List<PlanterRecipe> recipes = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : PlantablesConfig.getAllSeedToSoilMappings().entrySet()) {
            String seedId = entry.getKey();
            for (String soilId : entry.getValue()) {
                try {
                    if (!soilId.equals("minecraft:water_bucket") && RegistryHelper.getBlock(soilId) == null) {
                        LogUtils.getLogger().error("Invalid soil block in config: {} for seed {}", soilId, seedId);
                        continue;
                    }
                    PlanterRecipe recipe = PlanterRecipe.createCrop(seedId, soilId);
                    if (recipe != null && !recipe.getOutputs().isEmpty()) recipes.add(recipe);
                } catch (Exception e) {
                    LogUtils.getLogger().error("Error creating recipe for seed {} and soil {}: {}", seedId, soilId, e.getMessage(), e);
                }
            }
        }
        LogUtils.getLogger().info("Generated {} crop planter recipes for JEI", recipes.size());
        return recipes;
    }

    private List<PlanterRecipe> generateTreeRecipes() {
        List<PlanterRecipe> recipes = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : PlantablesConfig.getAllSaplingToSoilMappings().entrySet()) {
            String saplingId = entry.getKey();
            for (String soilId : entry.getValue()) {
                try {
                    if (RegistryHelper.getBlock(soilId) == null) {
                        LogUtils.getLogger().error("Invalid soil block in config: {} for sapling {}", soilId, saplingId);
                        continue;
                    }
                    PlanterRecipe recipe = PlanterRecipe.createTree(saplingId, soilId);
                    if (recipe != null && !recipe.getOutputs().isEmpty()) recipes.add(recipe);
                } catch (Exception e) {
                    LogUtils.getLogger().error("Error creating recipe for sapling {} and soil {}: {}", saplingId, soilId, e.getMessage());
                }
            }
        }
        LogUtils.getLogger().info("Generated {} tree planter recipes for JEI", recipes.size());
        return recipes;
    }

    private List<CompostRecipe> generateCompostRecipes() {
        List<CompostRecipe> recipes = new ArrayList<>();

        CompostableConfig.getCompostableItems().stream()
                .map(itemId -> {
                    try { return CompostRecipe.create(itemId); }
                    catch (Exception e) {
                        LogUtils.getLogger().error("Failed to create compost recipe for {}: {}", itemId, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(recipes::add);

        CompostableConfig.getDenseItems().stream()
                .map(itemId -> {
                    try { return CompostRecipe.createDense(itemId); }
                    catch (Exception e) {
                        LogUtils.getLogger().error("Failed to create dense compost recipe for {}: {}", itemId, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(recipes::add);

        LogUtils.getLogger().info("Generated {} compost recipes for JEI ({} dense)", recipes.size(),
                CompostableConfig.getDenseItems().size());
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

            if (Config.enableFarmersDelight) {
                addTillingModded(recipes, hoe, "farmersdelight:rich_soil", "farmersdelight:rich_soil_farmland");
            }
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