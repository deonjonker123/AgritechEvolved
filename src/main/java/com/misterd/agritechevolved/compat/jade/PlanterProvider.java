package com.misterd.agritechevolved.compat.jade;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.custom.AdvancedPlanterBlock;
import com.misterd.agritechevolved.block.custom.PlanterBlock;
import com.misterd.agritechevolved.blockentity.custom.AdvancedPlanterBlockEntity;
import com.misterd.agritechevolved.blockentity.custom.PlanterBlockEntity;
import com.misterd.agritechevolved.config.PlantablesConfig;
import com.misterd.agritechevolved.util.RegistryHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.text.NumberFormat;
import java.util.Locale;

public enum PlanterProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("agritechevolved", "planter_info");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains("hasCrop") || !data.getBoolean("hasCrop")) return;

        String cropName       = data.getString("cropName");
        int currentStage      = data.getInt("currentStage");
        int maxStage          = data.getInt("maxStage");
        float progressPercent = data.getFloat("progressPercent");
        String soilName       = data.getString("soilName");
        float growthModifier  = data.getFloat("growthModifier");

        if (progressPercent >= 100.0F) {
            tooltip.add(Component.translatable("jade.agritechevolved.crop_ready", cropName)
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        } else {
            tooltip.add(Component.translatable("jade.agritechevolved.crop_progress",
                            cropName, currentStage, maxStage, Math.round(progressPercent))
                    .withStyle(ChatFormatting.DARK_GREEN));
        }

        tooltip.add(Component.translatable("jade.agritechevolved.soil_info",
                        soilName, String.format("%.2fx", growthModifier))
                .withStyle(ChatFormatting.GRAY));

        if (data.getBoolean("hasFertilizer")) {
            tooltip.add(Component.translatable("jade.agritechevolved.fertilizer_info",
                            data.getString("fertilizerName"),
                            String.format("%.2fx", data.getFloat("fertilizerSpeedModifier")),
                            String.format("%.2fx", data.getFloat("fertilizerYieldModifier")))
                    .withStyle(ChatFormatting.YELLOW));
        }

        if (data.getBoolean("isCloched")) {
            tooltip.add(Component.translatable("jade.agritechevolved.cloche_installed",
                            String.format("%.2fx", data.getFloat("clocheSpeedModifier")),
                            String.format("%.2fx", data.getFloat("clocheYieldModifier")))
                    .withStyle(ChatFormatting.AQUA));
        }

        if (data.getBoolean("isAdvanced")) {
            int energy    = data.getInt("energyStored");
            int maxEnergy = data.getInt("maxEnergy");
            tooltip.add(Component.translatable("jade.agritechevolved.energy_info",
                            NumberFormat.getInstance(Locale.US).format(energy),
                            NumberFormat.getInstance(Locale.US).format(maxEnergy))
                    .withStyle(ChatFormatting.LIGHT_PURPLE));

            if (data.getBoolean("hasModules")) {
                tooltip.add(Component.translatable("jade.agritechevolved.module_info",
                                String.format("%.2fx", data.getFloat("moduleSpeedModifier")),
                                String.format("%.2fx", data.getFloat("moduleYieldModifier")))
                        .withStyle(ChatFormatting.GOLD));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (be instanceof AdvancedPlanterBlockEntity advanced) {
            appendAdvancedPlanterData(data, advanced, accessor.getBlockState());
        } else if (be instanceof PlanterBlockEntity basic) {
            appendBasicPlanterData(data, basic, accessor.getBlockState());
        }
    }

    private void appendBasicPlanterData(CompoundTag data, PlanterBlockEntity planter, BlockState state) {
        data.putBoolean("isAdvanced", false);

        ItemStack seedStack = planter.inventory.getStackInSlot(0);
        ItemStack soilStack = planter.inventory.getStackInSlot(1);
        if (seedStack.isEmpty() || soilStack.isEmpty()) {
            data.putBoolean("hasCrop", false);
            return;
        }

        appendCommonCropData(data, seedStack, soilStack, planter.getGrowthStage(), planter.getGrowthProgress(), planter.getSoilGrowthModifier(soilStack));

        ItemStack fertilizerStack = planter.inventory.getStackInSlot(2);
        appendFertilizerData(data, fertilizerStack);

        boolean cloched = state.getValue(PlanterBlock.CLOCHED);
        appendClocheData(data, cloched);
    }

    private void appendAdvancedPlanterData(CompoundTag data, AdvancedPlanterBlockEntity planter, BlockState state) {
        data.putBoolean("isAdvanced", true);

        ItemStack seedStack = planter.inventory.getStackInSlot(0);
        ItemStack soilStack = planter.inventory.getStackInSlot(1);
        if (seedStack.isEmpty() || soilStack.isEmpty()) {
            data.putBoolean("hasCrop", false);
            return;
        }

        appendCommonCropData(data, seedStack, soilStack, planter.getGrowthStage(), planter.getGrowthProgress(), planter.getSoilGrowthModifier(soilStack));

        ItemStack fertilizerStack = planter.inventory.getStackInSlot(4);
        appendFertilizerData(data, fertilizerStack);

        boolean cloched = state.getValue(AdvancedPlanterBlock.CLOCHED);
        appendClocheData(data, cloched);

        data.putInt("energyStored", planter.getEnergyStored());
        data.putInt("maxEnergy", planter.getMaxEnergyStored());

        ItemStack mod1 = planter.inventory.getStackInSlot(2);
        ItemStack mod2 = planter.inventory.getStackInSlot(3);
        boolean hasModules = !mod1.isEmpty() || !mod2.isEmpty();
        data.putBoolean("hasModules", hasModules);
        if (hasModules) {
            data.putFloat("moduleSpeedModifier", planter.getModuleSpeedModifier());
            data.putFloat("moduleYieldModifier", planter.getModuleYieldModifier());
        }
    }

    private void appendCommonCropData(CompoundTag data, ItemStack seedStack, ItemStack soilStack,
                                      int growthStage, float growthProgress, float soilModifier) {
        data.putBoolean("hasCrop", true);
        data.putString("cropName", seedStack.getDisplayName().getString());
        data.putInt("currentStage", growthStage);
        data.putInt("maxStage", getMaxStage(seedStack));
        data.putFloat("progressPercent", growthProgress * 100.0F);
        data.putString("soilName", soilStack.getDisplayName().getString());
        data.putFloat("growthModifier", soilModifier);
    }

    private void appendFertilizerData(CompoundTag data, ItemStack fertilizerStack) {
        if (fertilizerStack.isEmpty()) {
            data.putBoolean("hasFertilizer", false);
            return;
        }
        String id = RegistryHelper.getItemId(fertilizerStack);
        data.putBoolean("hasFertilizer", true);
        data.putString("fertilizerName", fertilizerStack.getDisplayName().getString());
        data.putFloat("fertilizerSpeedModifier", getFertilizerSpeedModifier(id));
        data.putFloat("fertilizerYieldModifier", getFertilizerYieldModifier(id));
    }

    private void appendClocheData(CompoundTag data, boolean cloched) {
        data.putBoolean("isCloched", cloched);
        if (cloched) {
            data.putFloat("clocheSpeedModifier", (float) Config.getClocheSpeedMultiplier());
            data.putFloat("clocheYieldModifier", (float) Config.getClocheYieldMultiplier());
        }
    }

    private float getFertilizerSpeedModifier(String fertilizerId) {
        PlantablesConfig.FertilizerInfo info = PlantablesConfig.getFertilizerInfo(fertilizerId);
        return info != null ? info.speedMultiplier : 1.0F;
    }

    private float getFertilizerYieldModifier(String fertilizerId) {
        PlantablesConfig.FertilizerInfo info = PlantablesConfig.getFertilizerInfo(fertilizerId);
        return info != null ? info.yieldMultiplier : 1.0F;
    }

    private int getMaxStage(ItemStack plantStack) {
        return PlantablesConfig.isValidSapling(RegistryHelper.getItemId(plantStack)) ? 1 : 8;
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}