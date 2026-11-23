package com.misterd.agritechevolved.compat.jei;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.recipe.ATERecipeTypes;
import com.misterd.agritechevolved.recipe.CropRecipe;
import com.misterd.agritechevolved.recipe.TreeRecipe;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
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
        if (Minecraft.getInstance().level == null) return recipes;
        RecipeManager rm = Minecraft.getInstance().level.getRecipeManager();
        for (CropRecipe cropRecipe : rm.getAllRecipesFor(ATERecipeTypes.CROP_TYPE.get())) {
            try {
                PlanterRecipe recipe = PlanterRecipe.fromCropRecipe(cropRecipe);
                if (!recipe.getOutputs().isEmpty()) recipes.add(recipe);
            } catch (Exception e) {
                LogUtils.getLogger().error("Error creating JEI recipe from CropRecipe {}: {}", cropRecipe.getId(), e.getMessage(), e);
            }
        }
        for (TreeRecipe treeRecipe : rm.getAllRecipesFor(ATERecipeTypes.TREE_TYPE.get())) {
            try {
                PlanterRecipe recipe = PlanterRecipe.fromTreeRecipe(treeRecipe);
                if (!recipe.getOutputs().isEmpty()) recipes.add(recipe);
            } catch (Exception e) {
                LogUtils.getLogger().error("Error creating JEI recipe from TreeRecipe {}: {}", treeRecipe.getId(), e.getMessage(), e);
            }
        }
        LogUtils.getLogger().info("Generated {} total planter recipes for JEI", recipes.size());
        return recipes;
    }

    private List<CompostRecipe> generateCompostRecipes() {
        List<CompostRecipe> recipes = new ArrayList<>();
        for (Object2FloatMap.Entry<ItemLike> entry : ComposterBlock.COMPOSTABLES.object2FloatEntrySet()) {
            float chance = entry.getFloatValue();
            if (chance <= 0f) continue;
            Item item = entry.getKey().asItem();
            recipes.add(CompostRecipe.create(item, chance));
        }
        LogUtils.getLogger().info("Generated {} compost recipes for JEI", recipes.size());
        return recipes;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) { jeiRuntime = runtime; }

    public static IJeiRuntime getJeiRuntime() { return jeiRuntime; }
}