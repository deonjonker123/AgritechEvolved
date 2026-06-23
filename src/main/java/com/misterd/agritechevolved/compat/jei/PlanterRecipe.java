package com.misterd.agritechevolved.compat.jei;

import com.misterd.agritechevolved.recipe.CropRecipe;
import com.misterd.agritechevolved.recipe.DropEntry;
import com.misterd.agritechevolved.recipe.TreeRecipe;
import com.mojang.logging.LogUtils;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class PlanterRecipe implements IRecipeCategoryExtension {

    private final Ingredient plantableIngredient;
    private final Ingredient soilIngredient;
    private final List<ItemStack> outputs;
    private final List<DropEntry> dropEntries;
    private final PlantableType type;

    public PlanterRecipe(Ingredient plantableIngredient, Ingredient soilIngredient, List<ItemStack> outputs, List<DropEntry> dropEntries, PlantableType type) {
        this.plantableIngredient = plantableIngredient;
        this.soilIngredient = soilIngredient;
        this.outputs = outputs;
        this.dropEntries = dropEntries;
        this.type = type;
    }

    public Ingredient getPlantableIngredient() { return plantableIngredient; }
    public Ingredient getSeedIngredient() { return plantableIngredient; }
    public Ingredient getSaplingIngredient() { return plantableIngredient; }
    public Ingredient getSoilIngredient() { return soilIngredient; }
    public List<ItemStack> getOutputs() { return outputs; }
    public List<DropEntry> getDropEntries() { return dropEntries; }
    public PlantableType getType() { return type; }

    public static PlanterRecipe fromCropRecipe(CropRecipe recipe) {
        Ingredient plant = recipe.getSeed();
        List<Ingredient> soils = recipe.getSoils();
        Ingredient soil = soils.isEmpty() ? Ingredient.EMPTY : soils.get(0);
        List<ItemStack> outputs = recipe.getDrops().stream()
                .filter(d -> d.chance() > 0.0F)
                .map(d -> new ItemStack(d.item(), (d.min() + d.max()) / 2))
                .toList();
        return new PlanterRecipe(plant, soil, outputs, recipe.getDrops(), PlantableType.CROP);
    }

    public static PlanterRecipe fromTreeRecipe(TreeRecipe recipe) {
        Ingredient plant = recipe.getSapling();
        List<Ingredient> soils = recipe.getSoils();
        Ingredient soil = soils.isEmpty() ? Ingredient.EMPTY : soils.get(0);
        List<ItemStack> outputs = recipe.getDrops().stream()
                .filter(d -> d.chance() > 0.0F)
                .map(d -> new ItemStack(d.item(), (d.min() + d.max()) / 2))
                .toList();
        return new PlanterRecipe(plant, soil, outputs, recipe.getDrops(), PlantableType.TREE);
    }

    public enum PlantableType {
        CROP,
        TREE
    }
}