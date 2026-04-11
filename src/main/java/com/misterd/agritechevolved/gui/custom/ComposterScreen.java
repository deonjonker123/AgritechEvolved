package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.compat.jei.ATJeiPlugin;
import com.misterd.agritechevolved.compat.jei.CompostRecipeCategory;
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

public class ComposterScreen extends AbstractContainerScreen<ComposterMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("agritechevolved", "textures/gui/composter_gui.png");

    // Progress bar widget (GUI-relative)
    private static final int PROGRESS_BAR_X   = 85;
    private static final int PROGRESS_BAR_Y   = 15;
    private static final int PROGRESS_BAR_W   = 6;
    private static final int PROGRESS_BAR_H   = 52;
    private static final int PROGRESS_BAR_TEX_X = 186;

    // Energy bar widget (GUI-relative)
    private static final int ENERGY_BAR_X     = 158;
    private static final int ENERGY_BAR_Y     = 15;
    private static final int ENERGY_BAR_W     = 10;
    private static final int ENERGY_BAR_H     = 52;
    private static final int ENERGY_BAR_TEX_X = 176;

    // -------------------------------------------------------------------------
    // Constructor / init
    // -------------------------------------------------------------------------

    public ComposterScreen(ComposterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth       = 176;
        this.imageHeight      = 166;
        this.inventoryLabelY  = imageHeight - 96;
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX  = (imageWidth - font.width(title)) / 2;
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

        // Progress bar (fills bottom-up)
        int progress    = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0) {
            int filled = (int) (PROGRESS_BAR_H * (float) progress / maxProgress);
            if (filled > 0) {
                guiGraphics.blit(GUI_TEXTURE,
                        x + PROGRESS_BAR_X, y + PROGRESS_BAR_Y + PROGRESS_BAR_H - filled,
                        PROGRESS_BAR_TEX_X, PROGRESS_BAR_H - filled,
                        PROGRESS_BAR_W, filled);
            }
        }

        // Energy bar (fills bottom-up)
        int energy    = menu.getEnergyStored();
        int maxEnergy = menu.getMaxEnergyStored();
        if (maxEnergy > 0) {
            int filled = (int) (ENERGY_BAR_H * (float) energy / maxEnergy);
            if (filled > 0) {
                guiGraphics.blit(GUI_TEXTURE,
                        x + ENERGY_BAR_X, y + ENERGY_BAR_Y + ENERGY_BAR_H - filled,
                        ENERGY_BAR_TEX_X, ENERGY_BAR_H - filled,
                        ENERGY_BAR_W, filled);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Tooltips
    // -------------------------------------------------------------------------

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth)   / 2;
        int y = (height - imageHeight) / 2;

        // Progress bar tooltip
        if (isHovering(PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BAR_W, PROGRESS_BAR_H, mouseX, mouseY)) {
            int progress      = menu.getProgress();
            int maxProgress   = menu.getMaxProgress();
            int organicItems  = menu.getOrganicItemsCollected();
            int requiredItems = menu.getRequiredOrganicItems();
            float pct = maxProgress > 0 ? (float) progress / maxProgress * 100.0F : 0.0F;

            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("tooltip.agritechevolved.composting_progress")
                            .withStyle(ChatFormatting.GREEN),
                    Component.literal(String.format("%.1f%%", pct))
                            .withStyle(ChatFormatting.GREEN),
                    Component.literal(String.format("Organic Items: %d/%d", organicItems, requiredItems))
                            .withStyle(ChatFormatting.GRAY),
                    Component.translatable("tooltip.agritechevolved.view_recipes")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
            ), mouseX, mouseY);
        }

        // Energy bar tooltip
        if (isHovering(ENERGY_BAR_X, ENERGY_BAR_Y, ENERGY_BAR_W, ENERGY_BAR_H, mouseX, mouseY)) {
            int energy    = menu.getEnergyStored();
            int maxEnergy = menu.getMaxEnergyStored();
            NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
            String energyText = fmt.format(energy) + " / " + fmt.format(maxEnergy) + " RF";
            float pct = maxEnergy > 0 ? (float) energy / maxEnergy * 100.0F : 0.0F;

            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("tooltip.agritechevolved.stored_energy")
                            .withStyle(ChatFormatting.YELLOW),
                    Component.literal(energyText)
                            .withStyle(ChatFormatting.GREEN),
                    Component.literal(String.format("%.1f%%", pct))
                            .withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }
    }

    // -------------------------------------------------------------------------
    // Mouse interaction
    // -------------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovering(PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BAR_W, PROGRESS_BAR_H,
                (int) mouseX, (int) mouseY)) {
            IJeiRuntime runtime = ATJeiPlugin.getJeiRuntime();
            if (runtime != null && minecraft != null && minecraft.player != null) {
                runtime.getRecipesGui().showTypes(List.of(CompostRecipeCategory.COMPOST_RECIPE_TYPE));
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /** Returns true if the screen-space point falls within the widget rectangle (GUI-relative coords). */
    private boolean isHovering(int wx, int wy, int ww, int wh, int mouseX, int mouseY) {
        int x = (width - imageWidth)   / 2;
        int y = (height - imageHeight) / 2;
        return mouseX >= x + wx && mouseX <= x + wx + ww
                && mouseY >= y + wy && mouseY <= y + wy + wh;
    }
}