package com.misterd.agritechevolved.compat.jei;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.ATEBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CompostRecipeCategory implements IRecipeCategory<CompostRecipe> {

    public static final Identifier UID =
            Identifier.fromNamespaceAndPath("agritechevolved", "composting");
    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("agritechevolved", "textures/gui/jei/jei_composter_gui.png");
    public static final RecipeType<CompostRecipe> COMPOST_RECIPE_TYPE =
            new RecipeType<>(UID, CompostRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public CompostRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 134, 72);
        this.icon       = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ATEBlocks.COMPOSTER.get()));
    }

    @Override public RecipeType<CompostRecipe> getRecipeType() { return COMPOST_RECIPE_TYPE; }
    @Override public Component getTitle()  { return Component.translatable("jei.agritechevolved.composting.tooltip"); }
    @Override public IDrawable getIcon()   { return icon; }
    @Override public int       getWidth()  { return 134; }
    @Override public int       getHeight() { return 72; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CompostRecipe recipe, IFocusGroup focuses) {
        int itemsRequired = Config.getComposterItemsPerBiomass();

        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 10, 10);
        recipe.getInput().items()
                .map(h -> new ItemStack(h.value(), itemsRequired))
                .forEach(inputSlot::add);

        List<ItemStack> outputs = recipe.getOutputs();
        for (int i = 0; i < outputs.size(); i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 106, 10 + i * 18).add(outputs.get(i));
        }
    }

    @Override
    public void draw(CompostRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }
}