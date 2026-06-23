package com.misterd.agritechevolved.compat.jei;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.recipe.ATERecipeTypes;
import com.misterd.agritechevolved.recipe.CropRecipe;
import com.misterd.agritechevolved.recipe.TreeRecipe;
import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.ComposterBlock;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ATJeiPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_ID = ResourceLocation.fromNamespaceAndPath("agritechevolved", "jei_plugin");
    private static IJeiRuntime jeiRuntime;

    @Override
    public ResourceLocation getPluginUid() { return PLUGIN_ID; }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new PlanterRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new CompostRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(PlanterRecipeCategory.PLANTER_RECIPE_TYPE, generatePlanterRecipes());
        registration.addRecipes(CompostRecipeCategory.COMPOST_RECIPE_TYPE, generateCompostRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ATEBlocks.OAK_PLANTER.get()), PlanterRecipeCategory.PLANTER_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ATEBlocks.COMPOSTER.get()), CompostRecipeCategory.COMPOST_RECIPE_TYPE);
    }

    private List<PlanterRecipe> generatePlanterRecipes() {
        List<PlanterRecipe> recipes = new ArrayList<>();
        RecipeManager rm = Minecraft.getInstance().level.getRecipeManager();
        for (RecipeHolder<CropRecipe> holder : rm.getAllRecipesFor(ATERecipeTypes.CROP_TYPE.get())) {
            try {
                PlanterRecipe recipe = PlanterRecipe.fromCropRecipe(holder.value());
                if (!recipe.getOutputs().isEmpty()) recipes.add(recipe);
            } catch (Exception e) {
                LogUtils.getLogger().error("Error creating JEI recipe from CropRecipe: {}", e.getMessage(), e);
            }
        }
        for (RecipeHolder<TreeRecipe> holder : rm.getAllRecipesFor(ATERecipeTypes.TREE_TYPE.get())) {
            try {
                PlanterRecipe recipe = PlanterRecipe.fromTreeRecipe(holder.value());
                if (!recipe.getOutputs().isEmpty()) recipes.add(recipe);
            } catch (Exception e) {
                LogUtils.getLogger().error("Error creating JEI recipe from TreeRecipe: {}", e.getMessage(), e);
            }
        }
        LogUtils.getLogger().info("Generated {} total planter recipes for JEI", recipes.size());
        return recipes;
    }

    private List<CompostRecipe> generateCompostRecipes() {
        List<CompostRecipe> recipes = new ArrayList<>();
        for (var item : BuiltInRegistries.ITEM) {
            ItemStack stack = new ItemStack(item);
            float chance = ComposterBlock.getValue(stack);
            if (chance <= 0f) continue;
            String itemId = BuiltInRegistries.ITEM.getKey(item).toString();
            try {
                recipes.add(CompostRecipe.create(itemId, chance));
            } catch (Exception e) {
                LogUtils.getLogger().error("Failed compost recipe for {}: {}", itemId, e.getMessage());
            }
        }
        LogUtils.getLogger().info("Generated {} compost recipes for JEI", recipes.size());
        return recipes;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) { jeiRuntime = runtime; }

    public static IJeiRuntime getJeiRuntime() { return jeiRuntime; }
}