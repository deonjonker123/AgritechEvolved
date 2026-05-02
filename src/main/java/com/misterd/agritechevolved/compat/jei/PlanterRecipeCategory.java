package com.misterd.agritechevolved.compat.jei;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.config.PlantablesConfig;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PlanterRecipeCategory implements IRecipeCategory<PlanterRecipe> {
    public static final Identifier UID = Identifier.fromNamespaceAndPath("agritechevolved", "planter");
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("agritechevolved", "textures/gui/jei/jei_planters_gui.png");
    public static final IRecipeType<PlanterRecipe> PLANTER_RECIPE_TYPE = IRecipeType.create(UID, PlanterRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public PlanterRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 134, 72);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ATEBlocks.OAK_PLANTER.get()));
    }

    @Override
    public IRecipeType<PlanterRecipe> getRecipeType() {
        return PLANTER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.agritechevolved.planter.tooltip");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 134;
    }

    @Override
    public int getHeight() {
        return 72;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PlanterRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder seedSlot = builder.addSlot(RecipeIngredientRole.INPUT, 10, 10);
        recipe.getSeedIngredient().items().map(h -> new ItemStack(h.value())).forEach(seedSlot::add);

        IRecipeSlotBuilder soilSlot = builder.addSlot(RecipeIngredientRole.INPUT, 10, 46);
        recipe.getSoilIngredient().items().map(h -> new ItemStack(h.value())).forEach(soilSlot::add);

        List<PlantablesConfig.DropInfo> dropInfos = recipe.getDropInfos();
        int outputIndex = 0;
        for (ItemStack output : recipe.getOutputs()) {
            if (outputIndex >= 12) break;
            int x = 52 + outputIndex % 4 * 18;
            int y = 10 + outputIndex / 4 * 18;

            final PlantablesConfig.DropInfo info = outputIndex < dropInfos.size() ? dropInfos.get(outputIndex) : null;

            var slot = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).add(output);

            if (info != null) {
                slot.addRichTooltipCallback((slotView, tooltip) -> {
                    String countStr = info.minCount == info.maxCount
                            ? String.valueOf(info.minCount)
                            : info.minCount + "–" + info.maxCount;
                    tooltip.add(Component.literal("Count: " + countStr).withStyle(ChatFormatting.GRAY));

                    if (info.chance < 1.0F) {
                        tooltip.add(Component.literal(String.format("%.0f%% chance", info.chance * 100)).withStyle(ChatFormatting.GOLD));
                    }
                });
            }

            outputIndex++;
        }
    }

    @Override
    public void draw(PlanterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }
}