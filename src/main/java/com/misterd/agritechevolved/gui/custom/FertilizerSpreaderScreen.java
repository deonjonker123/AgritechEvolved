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

public class FertilizerSpreaderScreen extends AbstractContainerScreen<FertilizerSpreaderMenu> {

    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("agritechevolved", "textures/gui/auto_gui.png");
    private static final int ENERGY_BAR_X = 181;
    private static final int ENERGY_BAR_Y = 45;
    private static final int ENERGY_BAR_W = 6;
    private static final int ENERGY_BAR_H = 99;
    private static final int ENERGY_BAR_TEX_X = 200;
    private static final int MODULE_MENU_SLOT = 36 + 63;

    public FertilizerSpreaderScreen(FertilizerSpreaderMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 200;
        this.imageHeight = 242;
        this.inventoryLabelY = imageHeight - 94;
    }

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
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        int energy = menu.getEnergyStored();
        int maxEnergy = menu.getMaxEnergyStored();
        if (maxEnergy > 0) {
            int filled = (int) (ENERGY_BAR_H * (float) energy / maxEnergy);
            if (filled > 0) {
                guiGraphics.blit(GUI_TEXTURE, x + ENERGY_BAR_X, y + ENERGY_BAR_Y + ENERGY_BAR_H - filled, ENERGY_BAR_TEX_X, ENERGY_BAR_H - filled, ENERGY_BAR_W, filled);
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        if (isOver(ENERGY_BAR_X, ENERGY_BAR_Y, ENERGY_BAR_W, ENERGY_BAR_H, mouseX, mouseY)) {
            int energy = menu.getEnergyStored();
            int maxEnergy = menu.getMaxEnergyStored();
            NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
            float pct = maxEnergy > 0 ? (float) energy / maxEnergy * 100.0F : 0.0F;
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("tooltip.agritechevolved.stored_energy").withStyle(ChatFormatting.YELLOW),
                    Component.literal(fmt.format(energy) + " / " + fmt.format(maxEnergy) + " RF").withStyle(ChatFormatting.GREEN),
                    Component.literal(String.format("%.1f%%", pct)).withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
            return;
        }
        if (hoveredSlot != null && hoveredSlot == menu.slots.get(MODULE_MENU_SLOT) && !hoveredSlot.hasItem()) {
            guiGraphics.renderTooltip(font, Component.translatable("tooltip.agritechevolved.slot.range_module"), mouseX, mouseY);
        }
    }

    private boolean isOver(int wx, int wy, int ww, int wh, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        return mouseX >= x + wx && mouseX <= x + wx + ww && mouseY >= y + wy && mouseY <= y + wy + wh;
    }
}