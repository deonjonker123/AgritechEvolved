package com.misterd.agritechevolved.compat.jei;

import mezz.jei.api.runtime.IJeiRuntime;

import java.util.List;

final class JeiRecipeOpener {

    private JeiRecipeOpener() {}

    static void showPlanterRecipes() {
        IJeiRuntime runtime = ATJeiPlugin.getJeiRuntime();
        if (runtime != null) runtime.getRecipesGui().showTypes(List.of(PlanterRecipeCategory.PLANTER_RECIPE_TYPE));
    }

    static void showCompostRecipes() {
        IJeiRuntime runtime = ATJeiPlugin.getJeiRuntime();
        if (runtime != null) runtime.getRecipesGui().showTypes(List.of(CompostRecipeCategory.COMPOST_RECIPE_TYPE));
    }
}