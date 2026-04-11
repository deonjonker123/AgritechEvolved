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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CapacitorScreen extends AbstractContainerScreen<CapacitorMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("agritechevolved", "textures/gui/capacitor_gui.png");

    // Energy bar region (relative to GUI origin)
    private static final int BAR_X = 8;
    private static final int BAR_Y = 15;
    private static final int BAR_W = 160;
    private static final int BAR_H = 34;

    // -------------------------------------------------------------------------
    // Constructor / init
    // -------------------------------------------------------------------------

    public CapacitorScreen(CapacitorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight      = 151;
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

        int energy    = menu.getEnergyStored();
        int maxEnergy = menu.getMaxEnergyStored();
        if (maxEnergy > 0) {
            int fillWidth = (int) (BAR_W * (double) energy / maxEnergy);
            if (fillWidth > 0) {
                guiGraphics.blit(GUI_TEXTURE, x + BAR_X, y + BAR_Y, 0, 151, fillWidth, BAR_H);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title,               titleLabelX,    titleLabelY,    0x404040, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);

        int energy    = menu.getEnergyStored();
        int maxEnergy = menu.getMaxEnergyStored();
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);

        // Energy amount centred in the bar
        Component energyText = Component.literal(fmt.format(energy) + " RF");
        int barCenterX = BAR_X + BAR_W / 2;
        int barCenterY = BAR_Y + BAR_H / 2 - font.lineHeight;
        guiGraphics.drawString(font, energyText, barCenterX - font.width(energyText) / 2, barCenterY, 0xFFFFFF, true);

        // Percentage below
        if (maxEnergy > 0) {
            double pct = (double) energy * 100.0 / maxEnergy;
            Component pctText = Component.literal(String.format("%.1f%%", pct));
            guiGraphics.drawString(font, pctText, barCenterX - font.width(pctText) / 2, barCenterY + 10, 0xCCCCCC, true);
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

        // Energy bar tooltip
        if (isHovering(BAR_X, BAR_Y, BAR_W, BAR_H, mouseX, mouseY)) {
            int energy      = menu.getEnergyStored();
            int maxEnergy   = menu.getMaxEnergyStored();
            int transferRate = menu.getTransferRate();
            String tierName = menu.getTierName();
            NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
            double pct = maxEnergy > 0 ? (double) energy * 100.0 / maxEnergy : 0.0;

            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("tooltip.agritechevolved.capacitor.title", tierName)
                            .withStyle(ChatFormatting.GOLD),
                    Component.translatable("tooltip.agritechevolved.capacitor.energy_storage")
                            .withStyle(ChatFormatting.YELLOW),
                    Component.translatable("tooltip.agritechevolved.capacitor.energy",
                                    fmt.format(energy), fmt.format(maxEnergy))
                            .withStyle(ChatFormatting.WHITE),
                    Component.translatable("tooltip.agritechevolved.capacitor.energy_percentage",
                                    String.format("%.1f", pct))
                            .withStyle(ChatFormatting.GRAY),
                    Component.translatable("tooltip.agritechevolved.capacitor.transfer_rate",
                                    fmt.format(transferRate))
                            .withStyle(ChatFormatting.AQUA),
                    Component.translatable("tooltip.agritechevolved.capacitor.connects_all_sides")
                            .withStyle(ChatFormatting.GREEN),
                    Component.translatable("tooltip.agritechevolved.capacitor.except_bottom")
                            .withStyle(ChatFormatting.DARK_GREEN)
            ), mouseX, mouseY);
        }

        // Title bar tooltip
        if (isHovering(BAR_X, 6, BAR_W, 8, mouseX, mouseY)) {
            String tierName  = menu.getTierName();
            int transferRate = menu.getTransferRate();
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("tooltip.agritechevolved.capacitor.title", tierName)
                            .withStyle(ChatFormatting.GOLD),
                    Component.translatable("tooltip.agritechevolved.capacitor.description")
                            .withStyle(ChatFormatting.YELLOW),
                    Component.translatable("tooltip.agritechevolved.capacitor.max_transfer",
                                    NumberFormat.getNumberInstance(Locale.US).format(transferRate))
                            .withStyle(ChatFormatting.AQUA)
            ), mouseX, mouseY);
        }

        // Info region tooltip
        if (isHovering(BAR_X, 50, BAR_W, 15, mouseX, mouseY)) {
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("tooltip.agritechevolved.capacitor.information")
                            .withStyle(ChatFormatting.GOLD),
                    Component.translatable("tooltip.agritechevolved.capacitor.info_stores")
                            .withStyle(ChatFormatting.GRAY),
                    Component.translatable("tooltip.agritechevolved.capacitor.info_balances")
                            .withStyle(ChatFormatting.GRAY),
                    Component.translatable("tooltip.agritechevolved.capacitor.info_automatic")
                            .withStyle(ChatFormatting.GRAY)
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