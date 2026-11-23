package com.misterd.agritechevolved.util;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.datamap.ATEDataMaps;
import com.misterd.agritechevolved.datamap.FertilizerData;
import com.misterd.agritechevolved.datamap.SoilModifierData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AgritechEvolved.MODID, value = Dist.CLIENT)
public class TooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        SoilModifierData soilData = ATEDataMaps.getSoilModifier(event.getItemStack().getItem());
        FertilizerData fertData = ATEDataMaps.getFertilizer(event.getItemStack().getItem());

        if (soilData == null && fertData == null) return;

        if (soilData != null) {
            event.getToolTip().add(
                    Component.translatable("tooltip.agritechevolved.soil_type")
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
            event.getToolTip().add(
                    Component.translatable("tooltip.agritechevolved.soil_growth_modifier",
                                    String.format("%.2fx", soilData.growthModifier()))
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
        }

        if (fertData != null) {
            event.getToolTip().add(
                    Component.translatable("tooltip.agritechevolved.fertilizer_type")
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
            event.getToolTip().add(
                    Component.translatable("tooltip.agritechevolved.fertilizer_speed",
                                    String.format("%.2fx", fertData.speedMultiplier()))
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
            event.getToolTip().add(
                    Component.translatable("tooltip.agritechevolved.fertilizer_yield",
                                    String.format("%.2fx", fertData.yieldMultiplier()))
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
        }
    }
}