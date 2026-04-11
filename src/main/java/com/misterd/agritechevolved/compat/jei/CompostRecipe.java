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

    public CompostRecipe(Ingredient input, List<ItemStack> outputs) {
        this.input   = input;
        this.outputs = outputs;
    }

    public Ingredient getInput()         { return input; }
    public List<ItemStack> getOutputs()  { return outputs; }

    public static CompostRecipe create(String itemId) {
        var item = RegistryHelper.getItem(itemId);
        if (item == null) {
            LogUtils.getLogger().error("Failed to create compost recipe: item not found for ID: {}", itemId);
            throw new IllegalArgumentException("Item not found for ID: " + itemId);
        }

        return new CompostRecipe(
                Ingredient.of(item),
                List.of(new ItemStack(ATEItems.BIOMASS.get(), 1))
        );
    }
}