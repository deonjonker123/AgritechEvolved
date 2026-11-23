package com.misterd.agritechevolved.item;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.item.custom.ClocheItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ATEItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AgritechEvolved.MODID);

    public static final RegistryObject<Item> SM_MK1 = ITEMS.register("sm_mk1",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        int speedBoost = (int) Math.round((Config.getSpeedModuleMk1Multiplier() - 1.0D) * 100.0D);
                        int powerIncrease = (int) Math.round((Config.getSpeedModuleMk1PowerMultiplier() - 1.0D) * 100.0D);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.speed_boost", speedBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.power_increase", powerIncrease));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.shift_info"));
                    }

                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    public static final RegistryObject<Item> SM_MK2 = ITEMS.register("sm_mk2",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        int speedBoost = (int) Math.round((Config.getSpeedModuleMk2Multiplier() - 1.0D) * 100.0D);
                        int powerIncrease = (int) Math.round((Config.getSpeedModuleMk2PowerMultiplier() - 1.0D) * 100.0D);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.speed_boost", speedBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.power_increase", powerIncrease));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.shift_info"));
                    }

                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    public static final RegistryObject<Item> SM_MK3 = ITEMS.register("sm_mk3",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        int speedBoost = (int) Math.round((Config.getSpeedModuleMk3Multiplier() - 1.0D) * 100.0D);
                        int powerIncrease = (int) Math.round((Config.getSpeedModuleMk3PowerMultiplier() - 1.0D) * 100.0D);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.speed_boost", speedBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.power_increase", powerIncrease));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.shift_info"));
                    }

                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    public static final RegistryObject<Item> YM_MK1 = ITEMS.register("ym_mk1",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        int yieldBoost = (int) Math.round((Config.getYieldModuleMk1Multiplier() - 1.0D) * 100.0D);
                        int speedReduction = (int) Math.round((1.0D - Config.getYieldModuleMk1SpeedPenalty()) * 100.0D);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.yield_boost", yieldBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.speed_reduction", speedReduction));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.shift_info"));
                    }

                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    public static final RegistryObject<Item> YM_MK2 = ITEMS.register("ym_mk2",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        int yieldBoost = (int) Math.round((Config.getYieldModuleMk2Multiplier() - 1.0D) * 100.0D);
                        int speedReduction = (int) Math.round((1.0D - Config.getYieldModuleMk2SpeedPenalty()) * 100.0D);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.yield_boost", yieldBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.speed_reduction", speedReduction));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.shift_info"));
                    }

                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    public static final RegistryObject<Item> YM_MK3 = ITEMS.register("ym_mk3",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        int yieldBoost = (int) Math.round((Config.getYieldModuleMk3Multiplier() - 1.0D) * 100.0D);
                        int speedReduction = (int) Math.round((1.0D - Config.getYieldModuleMk3SpeedPenalty()) * 100.0D);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.yield_boost", yieldBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.speed_reduction", speedReduction));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.shift_info"));
                    }

                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    public static final RegistryObject<Item> CRUDE_BIOMASS = ITEMS.register("crude_biomass",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.crude_biomass"));
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                    int baseRF = Config.getBurnerCrudeBiomassRfValue();
                    int burnDuration = Config.getBurnerCrudeBiomassBurnDuration();
                    int baseDuration = 50;
                    int actualRF = (int) ((float) baseRF * ((float) burnDuration / (float) baseDuration));
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.crude_biomass.rf_generation", numberFormat.format(actualRF)).withStyle(ChatFormatting.GREEN));
                    if (Screen.hasShiftDown()) {
                        double burnSeconds = (double) burnDuration / 20.0D;
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.burn_duration", String.format("%.1f", burnSeconds)).withStyle(ChatFormatting.AQUA));
                        int rfPerSecond = (int) Math.round((double) actualRF / burnSeconds);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.rf_per_second", numberFormat.format(rfPerSecond)).withStyle(ChatFormatting.YELLOW));
                        if (burnDuration != baseDuration) {
                            float multiplier = (float) burnDuration / (float) baseDuration;
                            int multiplierPercent = (int) Math.round(((double) multiplier - 1.0D) * 100.0D);
                            if (multiplierPercent > 0) {
                                tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.duration_bonus", multiplierPercent).withStyle(ChatFormatting.GREEN));
                            } else if (multiplierPercent < 0) {
                                tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.duration_penalty", Math.abs(multiplierPercent)).withStyle(ChatFormatting.RED));
                            }
                        }
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.crude_fuel.shift_info"));
                    }
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.crude_biomass.inefficient").withStyle(ChatFormatting.RED));

                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    public static final RegistryObject<Item> BIOMASS = ITEMS.register("biomass",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.biomass"));
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                    int baseRF = Config.getBurnerBiomassRfValue();
                    int burnDuration = Config.getBurnerBiomassBurnDuration();
                    int baseDuration = 100;
                    int actualRF = (int) ((float) baseRF * ((float) burnDuration / (float) baseDuration));
                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.biomass.rf_generation", numberFormat.format(actualRF)).withStyle(ChatFormatting.GREEN));
                        double burnSeconds = (double) burnDuration / 20.0D;
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.burn_duration", String.format("%.1f", burnSeconds)).withStyle(ChatFormatting.AQUA));
                        int rfPerSecond = (int) Math.round((double) actualRF / burnSeconds);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.rf_per_second", numberFormat.format(rfPerSecond)).withStyle(ChatFormatting.YELLOW));
                        if (burnDuration != baseDuration) {
                            float multiplier = (float) burnDuration / (float) baseDuration;
                            int multiplierPercent = (int) Math.round(((double) multiplier - 1.0D) * 100.0D);
                            if (multiplierPercent > 0) {
                                tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.duration_bonus", multiplierPercent).withStyle(ChatFormatting.GREEN));
                            } else if (multiplierPercent < 0) {
                                tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.duration_penalty", Math.abs(multiplierPercent)).withStyle(ChatFormatting.RED));
                            }
                        }
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.shift_info"));
                    }

                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    public static final RegistryObject<Item> COMPACTED_BIOMASS = ITEMS.register("compacted_biomass",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.compacted"));
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                    int baseRF = Config.getBurnerCompactedBiomassRfValue();
                    int burnDuration = Config.getBurnerCompactedBiomassBurnDuration();
                    int baseDuration = 180;
                    int actualRF = (int) ((float) baseRF * ((float) burnDuration / (float) baseDuration));
                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.compacted_biomass.rf_generation", numberFormat.format(actualRF)).withStyle(ChatFormatting.GREEN));
                        double burnSeconds = (double) burnDuration / 20.0D;
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.burn_duration", String.format("%.1f", burnSeconds)).withStyle(ChatFormatting.AQUA));
                        int rfPerSecond = (int) Math.round((double) actualRF / burnSeconds);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.rf_per_second", numberFormat.format(rfPerSecond)).withStyle(ChatFormatting.YELLOW));
                        if (burnDuration != baseDuration) {
                            float multiplier = (float) burnDuration / (float) baseDuration;
                            int multiplierPercent = (int) Math.round(((double) multiplier - 1.0D) * 100.0D);
                            if (multiplierPercent > 0) {
                                tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.duration_bonus", multiplierPercent).withStyle(ChatFormatting.GREEN));
                            } else if (multiplierPercent < 0) {
                                tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.duration_penalty", Math.abs(multiplierPercent)).withStyle(ChatFormatting.RED));
                            }
                        }
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.shift_info"));
                    }

                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    public static final RegistryObject<ClocheItem> CLOCHE = ITEMS.register("cloche_dome",
            () -> new ClocheItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}