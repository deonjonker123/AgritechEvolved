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

import java.util.List;

public class BasicPlanterScreen extends AbstractContainerScreen<BasicPlanterMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("agritechevolved", "textures/gui/basic_planter_gui.png");

    public BasicPlanterScreen(BasicPlanterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 172;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        float growthProgress = this.menu.blockEntity.getGrowthProgress();
        if (growthProgress > 0.0F) {
            int barHeight = (int) (54.0F * growthProgress);
            int barY = y + 17 + 54 - barHeight;
            guiGraphics.blit(GUI_TEXTURE, x + 40, barY, 176, 54 - barHeight, 6, barHeight);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        int guiX = (this.width - this.imageWidth) / 2;
        int guiY = (this.height - this.imageHeight) / 2;
        if (x >= guiX + 40 && x <= guiX + 46 && y >= guiY + 18 && y <= guiY + 71) {
            float progress = this.menu.blockEntity.getGrowthProgress();
            guiGraphics.renderComponentTooltip(this.font, List.of(
                    Component.translatable("tooltip.agritechevolved.growth_progress"),
                    Component.literal(String.format("%.1f%%", progress * 100.0F)).withStyle(ChatFormatting.GREEN),
                    Component.translatable("tooltip.agritechevolved.view_recipes").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
            ), x, y);
            return;
        }
        renderEmptySlotTooltip(guiGraphics, x, y, 36, "tooltip.agritechevolved.slot.plant");
        renderEmptySlotTooltip(guiGraphics, x, y, 37, "tooltip.agritechevolved.slot.soil");
        renderEmptySlotTooltip(guiGraphics, x, y, 38, "tooltip.agritechevolved.slot.fertilizer");
    }

    private void renderEmptySlotTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int menuSlot, String key) {
        if (this.hoveredSlot != null && this.hoveredSlot == this.menu.slots.get(menuSlot) && !this.hoveredSlot.hasItem()) {
            guiGraphics.renderTooltip(this.font, Component.translatable(key), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && JeiCompat.isLoaded()) {
            int guiX = (this.width - this.imageWidth) / 2;
            int guiY = (this.height - this.imageHeight) / 2;
            if (mouseX >= guiX + 40 && mouseX <= guiX + 46 && mouseY >= guiY + 18 && mouseY <= guiY + 71) {
                JeiCompat.showPlanterRecipes();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}