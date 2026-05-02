package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.compat.jei.ATJeiPlugin;
import com.misterd.agritechevolved.compat.jei.PlanterRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdvancedPlanterScreen extends AbstractContainerScreen<AdvancedPlanterMenu> {

    private static final Identifier GUI_TEXTURE =
            Identifier.fromNamespaceAndPath("agritechevolved", "textures/gui/advanced_planter_gui.png");

    private static final int GUI_W = 212;
    private static final int GUI_H = 170;

    private static final int GROWTH_BAR_X = 40;
    private static final int GROWTH_BAR_Y = 19;
    private static final int GROWTH_BAR_W = 6;
    private static final int GROWTH_BAR_H = 52;
    private static final int GROWTH_BAR_TEX_X = 222;

    private static final int ENERGY_BAR_X = 194;
    private static final int ENERGY_BAR_Y = 19;
    private static final int ENERGY_BAR_W = 10;
    private static final int ENERGY_BAR_H = 52;
    private static final int ENERGY_BAR_TEX_X = 212;

    public AdvancedPlanterScreen(AdvancedPlanterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, GUI_W, GUI_H);
        this.inventoryLabelX = 26;
        this.inventoryLabelY = GUI_H - 93;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE,
                this.leftPos, this.topPos, 0.0F, 0.0F,
                this.imageWidth, this.imageHeight, 256, 256);

        // Growth bar (bottom-up)
        float progress = menu.getGrowthProgress();
        if (progress > 0.0F) {
            int filled = (int) (GROWTH_BAR_H * progress);
            graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE,
                    this.leftPos + GROWTH_BAR_X, this.topPos + GROWTH_BAR_Y + GROWTH_BAR_H - filled,
                    (float) GROWTH_BAR_TEX_X, (float) (GROWTH_BAR_H - filled),
                    GROWTH_BAR_W, filled, 256, 256);
        }

        // Energy bar (bottom-up)
        int energy = menu.getEnergyStored(), maxEnergy = menu.getMaxEnergyStored();
        if (maxEnergy > 0) {
            int filled = (int) (ENERGY_BAR_H * ((float) energy / maxEnergy));
            if (filled > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE,
                        this.leftPos + ENERGY_BAR_X, this.topPos + ENERGY_BAR_Y + ENERGY_BAR_H - filled,
                        (float) ENERGY_BAR_TEX_X, (float) (ENERGY_BAR_H - filled),
                        ENERGY_BAR_W, filled, 256, 256);
            }
        }

        super.extractContents(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (isOver(GROWTH_BAR_X, GROWTH_BAR_Y, GROWTH_BAR_W, GROWTH_BAR_H + 1, mouseX, mouseY)) {
            float progress = menu.getGrowthProgress();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.translatable("tooltip.agritechevolved.growth_progress"),
                    Component.literal(String.format("%.1f%%", progress * 100.0F)).withStyle(ChatFormatting.GREEN),
                    Component.translatable("tooltip.agritechevolved.view_recipes")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
            ), mouseX, mouseY);
            return;
        }

        if (isOver(ENERGY_BAR_X, ENERGY_BAR_Y, ENERGY_BAR_W, ENERGY_BAR_H + 1, mouseX, mouseY)) {
            int energy = menu.getEnergyStored(), maxEnergy = menu.getMaxEnergyStored();
            NumberFormat fmt = NumberFormat.getInstance(Locale.US);
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.translatable("tooltip.agritechevolved.stored_energy"),
                    Component.literal(fmt.format(energy) + " / " + fmt.format(maxEnergy) + " RF")
                            .withStyle(ChatFormatting.YELLOW)
            ), mouseX, mouseY);
            return;
        }

        super.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0
                && isOver(GROWTH_BAR_X, GROWTH_BAR_Y, GROWTH_BAR_W, GROWTH_BAR_H + 1,
                (int) event.x(), (int) event.y())) {
            IJeiRuntime runtime = ATJeiPlugin.getJeiRuntime();
            if (runtime != null && minecraft != null && minecraft.player != null) {
                runtime.getRecipesGui().showTypes(List.of(PlanterRecipeCategory.PLANTER_RECIPE_TYPE));
            }
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    private boolean isOver(int wx, int wy, int ww, int wh, int mx, int my) {
        return mx >= this.leftPos + wx && mx <= this.leftPos + wx + ww
                && my >= this.topPos + wy && my <= this.topPos + wy + wh;
    }
}