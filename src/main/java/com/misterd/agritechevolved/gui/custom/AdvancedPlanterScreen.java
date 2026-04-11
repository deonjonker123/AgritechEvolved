package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.compat.jei.ATJeiPlugin;
import com.misterd.agritechevolved.compat.jei.PlanterRecipeCategory;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdvancedPlanterScreen extends AbstractContainerScreen<AdvancedPlanterMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("agritechevolved", "textures/gui/advanced_planter_gui.png");

    // -------------------------------------------------------------------------
    // GUI widget regions (relative to GUI origin)
    // -------------------------------------------------------------------------

    private static final int GROWTH_BAR_X      = 40;
    private static final int GROWTH_BAR_Y      = 16;
    private static final int GROWTH_BAR_W      = 6;
    private static final int GROWTH_BAR_H      = 52;
    private static final int GROWTH_BAR_TEX_X  = 222;

    private static final int ENERGY_BAR_X      = 194;
    private static final int ENERGY_BAR_Y      = 16;
    private static final int ENERGY_BAR_W      = 10;
    private static final int ENERGY_BAR_H      = 52;
    private static final int ENERGY_BAR_TEX_X  = 212;

    // -------------------------------------------------------------------------
    // Constructor / init
    // -------------------------------------------------------------------------

    public AdvancedPlanterScreen(AdvancedPlanterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth       = 212;
        this.imageHeight      = 170;
        this.inventoryLabelX  = 26;
        this.inventoryLabelY  = imageHeight - 96;
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
        titleLabelY -= 2;
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth)   / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        renderGrowthBar(guiGraphics, x, y);
        renderEnergyBar(guiGraphics, x, y);
    }

    private void renderGrowthBar(GuiGraphics guiGraphics, int x, int y) {
        float progress = menu.getGrowthProgress();
        if (progress <= 0.0F) return;

        int filledH = (int)(GROWTH_BAR_H * progress);
        int barY    = y + GROWTH_BAR_Y + GROWTH_BAR_H - filledH;
        guiGraphics.blit(GUI_TEXTURE, x + GROWTH_BAR_X, barY,
                GROWTH_BAR_TEX_X, GROWTH_BAR_H - filledH, GROWTH_BAR_W, filledH);
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        int energy    = menu.getEnergyStored();
        int maxEnergy = menu.getMaxEnergyStored();
        if (maxEnergy <= 0) return;

        int filledH = (int)(ENERGY_BAR_H * ((float) energy / maxEnergy));
        int barY    = y + ENERGY_BAR_Y + ENERGY_BAR_H - filledH;
        guiGraphics.blit(GUI_TEXTURE, x + ENERGY_BAR_X, barY,
                ENERGY_BAR_TEX_X, ENERGY_BAR_H - filledH, ENERGY_BAR_W, filledH);
    }

    // -------------------------------------------------------------------------
    // Tooltips
    // -------------------------------------------------------------------------

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth)   / 2;
        int y = (height - imageHeight) / 2;

        if (isHoveringWidget(mouseX, mouseY, x + GROWTH_BAR_X, y + GROWTH_BAR_Y, GROWTH_BAR_W, GROWTH_BAR_H + 1)) {
            float progress = menu.getGrowthProgress();
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("tooltip.agritechevolved.growth_progress"),
                    Component.literal(String.format("%.1f%%", progress * 100.0F)).withStyle(ChatFormatting.GREEN),
                    Component.translatable("tooltip.agritechevolved.view_recipes")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
            ), mouseX, mouseY);
        }

        if (isHoveringWidget(mouseX, mouseY, x + ENERGY_BAR_X, y + ENERGY_BAR_Y, ENERGY_BAR_W, ENERGY_BAR_H + 1)) {
            int energy    = menu.getEnergyStored();
            int maxEnergy = menu.getMaxEnergyStored();
            NumberFormat fmt = NumberFormat.getInstance(Locale.US);
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("tooltip.agritechevolved.stored_energy"),
                    Component.literal(fmt.format(energy) + " / " + fmt.format(maxEnergy) + " RF")
                            .withStyle(ChatFormatting.YELLOW)
            ), mouseX, mouseY);
        }
    }

    // -------------------------------------------------------------------------
    // Mouse interaction
    // -------------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int x = (width - imageWidth)   / 2;
            int y = (height - imageHeight) / 2;

            if (isHoveringWidget((int) mouseX, (int) mouseY, x + GROWTH_BAR_X, y + GROWTH_BAR_Y, GROWTH_BAR_W, GROWTH_BAR_H + 1)) {
                IJeiRuntime runtime = ATJeiPlugin.getJeiRuntime();
                if (runtime != null && minecraft != null && minecraft.player != null) {
                    runtime.getRecipesGui().showTypes(List.of(PlanterRecipeCategory.PLANTER_RECIPE_TYPE));
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Returns true if the given screen-space point falls within the widget rectangle. */
    private static boolean isHoveringWidget(int mouseX, int mouseY, int wx, int wy, int ww, int wh) {
        return mouseX >= wx && mouseX <= wx + ww && mouseY >= wy && mouseY <= wy + wh;
    }
}
