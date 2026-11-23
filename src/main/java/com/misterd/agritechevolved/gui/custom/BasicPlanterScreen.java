package com.misterd.agritechevolved.gui.custom;

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
        this.imageHeight = 171;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
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
            int progressBarHeight = (int) (52.0F * growthProgress);
            int progressBarY = y + 18 + 52 - progressBarHeight;
            guiGraphics.blit(GUI_TEXTURE, x + 40, progressBarY, 176, 52 - progressBarHeight, 6, progressBarHeight);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
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
        }
    }
}