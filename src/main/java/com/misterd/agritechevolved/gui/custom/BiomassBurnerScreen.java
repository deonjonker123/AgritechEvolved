package com.misterd.agritechevolved.gui.custom;

import com.mojang.blaze3d.systems.RenderSystem;
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

public class BiomassBurnerScreen extends AbstractContainerScreen<BiomassBurnerMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("agritechevolved", "textures/gui/burner_gui.png");

    // Progress bar (horizontal, fills left-to-right)
    private static final int PROGRESS_BAR_X     = 62;
    private static final int PROGRESS_BAR_Y     = 59;
    private static final int PROGRESS_BAR_W     = 52;
    private static final int PROGRESS_BAR_H     = 6;
    private static final int PROGRESS_BAR_TEX_X = 0;
    private static final int PROGRESS_BAR_TEX_Y = 166;

    // Energy bar (vertical, fills bottom-up)
    private static final int ENERGY_BAR_X     = 158;
    private static final int ENERGY_BAR_Y     = 15;
    private static final int ENERGY_BAR_W     = 10;
    private static final int ENERGY_BAR_H     = 52;
    private static final int ENERGY_BAR_TEX_X = 176;

    // Fuel slot hover region
    private static final int FUEL_SLOT_X = 79;
    private static final int FUEL_SLOT_Y = 31;
    private static final int FUEL_SLOT_W = 18;
    private static final int FUEL_SLOT_H = 18;

    // -------------------------------------------------------------------------
    // Constructor / init
    // -------------------------------------------------------------------------

    public BiomassBurnerScreen(BiomassBurnerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth       = 176;
        this.imageHeight      = 166;
        this.inventoryLabelY  = imageHeight - 94;
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

        // Progress bar (horizontal, left-to-right)
        int progress    = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0) {
            int filled = (int) (PROGRESS_BAR_W * (double) progress / maxProgress);
            if (filled > 0) {
                guiGraphics.blit(GUI_TEXTURE,
                        x + PROGRESS_BAR_X, y + PROGRESS_BAR_Y,
                        PROGRESS_BAR_TEX_X, PROGRESS_BAR_TEX_Y,
                        filled, PROGRESS_BAR_H);
            }
        }

        // Energy bar (vertical, bottom-up)
        int energy    = menu.getEnergyStored();
        int maxEnergy = menu.getMaxEnergyStored();
        if (maxEnergy > 0) {
            int filled = (int) (ENERGY_BAR_H * (double) energy / maxEnergy);
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

        // Energy bar tooltip
        if (isHovering(ENERGY_BAR_X, ENERGY_BAR_Y, ENERGY_BAR_W, ENERGY_BAR_H, mouseX, mouseY)) {
            int energy    = menu.getEnergyStored();
            int maxEnergy = menu.getMaxEnergyStored();
            NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
            double pct = maxEnergy > 0 ? (double) energy * 100.0 / maxEnergy : 0.0;

            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("tooltip.agritechevolved.burner.energy",
                                    fmt.format(energy), fmt.format(maxEnergy))
                            .withStyle(ChatFormatting.YELLOW),
                    Component.translatable("tooltip.agritechevolved.burner.energy_percentage",
                                    String.format("%.1f", pct))
                            .withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Progress bar tooltip
        if (isHovering(PROGRESS_BAR_X, PROGRESS_BAR_Y - 1, PROGRESS_BAR_W, PROGRESS_BAR_H + 1, mouseX, mouseY)) {
            int progress    = menu.getProgress();
            int maxProgress = menu.getMaxProgress();

            List<Component> tooltip;
            if (maxProgress > 0) {
                double pct            = (double) progress * 100.0 / maxProgress;
                double remainingSecs  = (double) (maxProgress - progress) / 20.0;
                tooltip = List.of(
                        Component.translatable("tooltip.agritechevolved.burner.burning_progress")
                                .withStyle(ChatFormatting.GOLD),
                        Component.translatable("tooltip.agritechevolved.burner.progress_percentage",
                                        String.format("%.1f", pct))
                                .withStyle(ChatFormatting.YELLOW),
                        Component.translatable("tooltip.agritechevolved.burner.time_remaining",
                                        String.format("%.1f", remainingSecs))
                                .withStyle(ChatFormatting.GRAY)
                );
            } else {
                tooltip = List.of(
                        Component.translatable("tooltip.agritechevolved.burner.no_fuel")
                                .withStyle(ChatFormatting.RED),
                        Component.translatable("tooltip.agritechevolved.burner.insert_fuel")
                                .withStyle(ChatFormatting.GRAY)
                );
            }
            guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }

        // Fuel slot empty tooltip
        if (isHovering(FUEL_SLOT_X, FUEL_SLOT_Y, FUEL_SLOT_W, FUEL_SLOT_H, mouseX, mouseY)
                && menu.blockEntity.inventory.getStackInSlot(0).isEmpty()) {
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("tooltip.agritechevolved.burner.fuel_slot")
                            .withStyle(ChatFormatting.GOLD),
                    Component.translatable("tooltip.agritechevolved.burner.accepts")
                            .withStyle(ChatFormatting.GRAY),
                    Component.translatable("tooltip.agritechevolved.burner.accepts_biomass")
                            .withStyle(ChatFormatting.GREEN),
                    Component.translatable("tooltip.agritechevolved.burner.accepts_compacted_biomass")
                            .withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
        }
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