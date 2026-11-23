package com.misterd.agritechevolved.compat.jei;

import com.misterd.agritechevolved.item.ATEItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class CompostRecipe {

    private final Ingredient input;
    private final List<ItemStack> outputs;
    private final float chance;

    public CompostRecipe(Ingredient input, List<ItemStack> outputs, float chance) {
        this.input = input;
        this.outputs = outputs;
        this.chance = chance;
    }

    public Ingredient getInput() { return input; }
    public List<ItemStack> getOutputs() { return outputs; }
    public float getChance() { return chance; }

    public static CompostRecipe create(Item item, float chance) {
        return new CompostRecipe(
                Ingredient.of(item),
                List.of(new ItemStack(ATEItems.BIOMASS.get(), 1)),
                chance
        );
    }
}