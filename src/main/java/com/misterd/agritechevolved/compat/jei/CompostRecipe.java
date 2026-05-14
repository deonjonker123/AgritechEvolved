package com.misterd.agritechevolved.compat.jei;

import com.misterd.agritechevolved.item.ATEItems;
import com.misterd.agritechevolved.util.RegistryHelper;
import com.mojang.logging.LogUtils;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class CompostRecipe implements IRecipeCategoryExtension {

    private final Ingredient input;
    private final List<ItemStack> outputs;
    private final boolean dense;

    public CompostRecipe(Ingredient input, List<ItemStack> outputs, boolean dense) {
        this.input = input;
        this.outputs = outputs;
        this.dense = dense;
    }

    public Ingredient getInput() { return input; }
    public List<ItemStack> getOutputs() { return outputs; }
    public boolean isDense() { return dense; }

    public static CompostRecipe create(String itemId) {
        return create(itemId, false);
    }

    public static CompostRecipe createDense(String itemId) {
        return create(itemId, true);
    }

    private static CompostRecipe create(String itemId, boolean dense) {
        var item = RegistryHelper.getItem(itemId);
        if (item == null) {
            LogUtils.getLogger().error("Failed to create compost recipe: item not found for ID: {}", itemId);
            throw new IllegalArgumentException("Item not found for ID: " + itemId);
        }
        return new CompostRecipe(
                Ingredient.of(item),
                List.of(new ItemStack(ATEItems.BIOMASS.get(), 1)),
                dense
        );
    }
}