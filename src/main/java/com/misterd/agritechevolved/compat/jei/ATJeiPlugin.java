package com.misterd.agritechevolved.compat.jei;

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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

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
                new CompostRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(PlanterRecipeCategory.PLANTER_RECIPE_TYPE, generatePlanterRecipes());
        registration.addRecipes(CompostRecipeCategory.COMPOST_RECIPE_TYPE, generateCompostRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(PlanterRecipeCategory.PLANTER_RECIPE_TYPE, ATEBlocks.OAK_PLANTER);
        registration.addCraftingStation(CompostRecipeCategory.COMPOST_RECIPE_TYPE, ATEBlocks.COMPOSTER);
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
        List<CompostRecipe> recipes = CompostableConfig.getCompostableItems().stream()
                .map(itemId -> {
                    try {
                        return CompostRecipe.create(itemId);
                    } catch (Exception e) {
                        LogUtils.getLogger().error("Failed to create compost recipe for {}: {}", itemId, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
        LogUtils.getLogger().info("Generated {} compost recipes for JEI", recipes.size());
        return recipes;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }

    public static IJeiRuntime getJeiRuntime() {
        return jeiRuntime;
    }
}