package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.compat.jei.JeiCompat;
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

public class ComposterScreen extends AbstractContainerScreen<ComposterMenu> {

    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("agritechevolved", "textures/gui/composter_gui.png");
    private static final int PROGRESS_BAR_X = 85;
    private static final int PROGRESS_BAR_Y = 18;
    private static final int PROGRESS_BAR_W = 6;
    private static final int PROGRESS_BAR_H = 54;
    private static final int PROGRESS_BAR_TEX_X = 176;
    private static final int ENERGY_BAR_X = 163;
    private static final int ENERGY_BAR_Y = 18;
    private static final int ENERGY_BAR_W = 6;
    private static final int ENERGY_BAR_H = 54;
    private static final int ENERGY_BAR_TEX_X = 182;

    public ComposterScreen(ComposterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 172;
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
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0) {
            int filled = (int) (PROGRESS_BAR_H * (float) progress / maxProgress);
            if (filled > 0) {
                guiGraphics.blit(GUI_TEXTURE, x + PROGRESS_BAR_X, y + PROGRESS_BAR_Y + PROGRESS_BAR_H - filled, PROGRESS_BAR_TEX_X, PROGRESS_BAR_H - filled, PROGRESS_BAR_W, filled);
            }
        }
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
        if (isHovering(PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BAR_W, PROGRESS_BAR_H, mouseX, mouseY)) {
            int progress = menu.getProgress();
            int maxProgress = menu.getMaxProgress();
            int organicItems = menu.getOrganicItemsCollected();
            int requiredItems = menu.getRequiredOrganicItems();
            float pct = maxProgress > 0 ? (float) progress / maxProgress * 100.0F : 0.0F;
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("tooltip.agritechevolved.composting_info").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC),
                    Component.translatable("tooltip.agritechevolved.composting_progress").withStyle(ChatFormatting.GREEN),
                    Component.literal(String.format("%.1f%%", pct)).withStyle(ChatFormatting.GREEN),
                    Component.literal(String.format("Organic Items: %d/%d", organicItems, requiredItems)).withStyle(ChatFormatting.GRAY),
                    Component.translatable("tooltip.agritechevolved.view_recipes").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
            ), mouseX, mouseY);
            return;
        }
        if (isHovering(ENERGY_BAR_X, ENERGY_BAR_Y, ENERGY_BAR_W, ENERGY_BAR_H, mouseX, mouseY)) {
            int energy = menu.getEnergyStored();
            int maxEnergy = menu.getMaxEnergyStored();
            NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
            String energyText = fmt.format(energy) + " / " + fmt.format(maxEnergy) + " RF";
            float pct = maxEnergy > 0 ? (float) energy / maxEnergy * 100.0F : 0.0F;
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("tooltip.agritechevolved.stored_energy").withStyle(ChatFormatting.YELLOW),
                    Component.literal(energyText).withStyle(ChatFormatting.GREEN),
                    Component.literal(String.format("%.1f%%", pct)).withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
            return;
        }
        if (hoveredSlot != null && hoveredSlot == menu.slots.get(51) && !hoveredSlot.hasItem()) {
            guiGraphics.renderTooltip(font, Component.translatable("tooltip.agritechevolved.slot.upgrade"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && JeiCompat.isLoaded() && isHovering(PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BAR_W, PROGRESS_BAR_H, (int) mouseX, (int) mouseY)) {
            JeiCompat.showCompostRecipes();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isHovering(int wx, int wy, int ww, int wh, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        return mouseX >= x + wx && mouseX <= x + wx + ww && mouseY >= y + wy && mouseY <= y + wy + wh;
    }
}