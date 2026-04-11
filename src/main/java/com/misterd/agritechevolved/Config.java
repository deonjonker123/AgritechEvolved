package com.misterd.agritechevolved;

import com.misterd.agritechevolved.config.CompostableConfig;
import com.misterd.agritechevolved.config.PlantablesConfig;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;

public class Config {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec SPEC;

    // -------------------------------------------------------------------------
    // Compatibility
    // -------------------------------------------------------------------------
    public static ModConfigSpec.BooleanValue ENABLE_MYSTICAL_AGRICULTURE;
    public static ModConfigSpec.BooleanValue ENABLE_MYSTICAL_AGRADDITIONS;
    public static ModConfigSpec.BooleanValue ENABLE_FARMERS_DELIGHT;
    public static ModConfigSpec.BooleanValue ENABLE_ARS_NOUVEAU;
    public static ModConfigSpec.BooleanValue ENABLE_ARS_ELEMENTAL;
    public static ModConfigSpec.BooleanValue ENABLE_SILENT_GEAR;
    public static ModConfigSpec.BooleanValue ENABLE_JUST_DIRE_THINGS;
    public static ModConfigSpec.BooleanValue ENABLE_IMMERSIVE_ENGINEERING;
    public static ModConfigSpec.BooleanValue ENABLE_EVILCRAFT;
    public static ModConfigSpec.BooleanValue ENABLE_FORBIDDEN_ARCANUS;
    public static ModConfigSpec.BooleanValue ENABLE_INTEGRATED_DYNAMICS;
    public static ModConfigSpec.BooleanValue ENABLE_OCCULTISM;
    public static ModConfigSpec.BooleanValue ENABLE_PAMS_CROPS;
    public static ModConfigSpec.BooleanValue ENABLE_PAMS_TREES;
    public static ModConfigSpec.BooleanValue ENABLE_CROPTOPIA;
    public static ModConfigSpec.BooleanValue ENABLE_COBBLEMON;
    public static ModConfigSpec.BooleanValue ENABLE_ACTUALLY_ADDITIONS;

    // -------------------------------------------------------------------------
    // Modules
    // -------------------------------------------------------------------------
    public static ModConfigSpec.DoubleValue SPEED_MODULE_MK1_MULTIPLIER;
    public static ModConfigSpec.DoubleValue SPEED_MODULE_MK1_POWER_MULTIPLIER;
    public static ModConfigSpec.DoubleValue SPEED_MODULE_MK2_MULTIPLIER;
    public static ModConfigSpec.DoubleValue SPEED_MODULE_MK2_POWER_MULTIPLIER;
    public static ModConfigSpec.DoubleValue SPEED_MODULE_MK3_MULTIPLIER;
    public static ModConfigSpec.DoubleValue SPEED_MODULE_MK3_POWER_MULTIPLIER;
    public static ModConfigSpec.DoubleValue YIELD_MODULE_MK1_MULTIPLIER;
    public static ModConfigSpec.DoubleValue YIELD_MODULE_MK1_SPEED_PENALTY;
    public static ModConfigSpec.DoubleValue YIELD_MODULE_MK2_MULTIPLIER;
    public static ModConfigSpec.DoubleValue YIELD_MODULE_MK2_SPEED_PENALTY;
    public static ModConfigSpec.DoubleValue YIELD_MODULE_MK3_MULTIPLIER;
    public static ModConfigSpec.DoubleValue YIELD_MODULE_MK3_SPEED_PENALTY;

    // -------------------------------------------------------------------------
    // Fertilizers
    // -------------------------------------------------------------------------
    public static ModConfigSpec.DoubleValue FERTILIZER_BONE_MEAL_SPEED_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_BONE_MEAL_YIELD_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_CRUDE_BIOMASS_SPEED_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_CRUDE_BIOMASS_YIELD_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_BIOMASS_SPEED_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_BIOMASS_YIELD_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_COMPACTED_BIOMASS_SPEED_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_COMPACTED_BIOMASS_YIELD_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_FERTILIZED_ESSENCE_SPEED_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_FERTILIZED_ESSENCE_YIELD_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_MYSTICAL_FERTILIZER_SPEED_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_MYSTICAL_FERTILIZER_YIELD_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_IMMERSIVE_FERTILIZER_SPEED_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_IMMERSIVE_FERTILIZER_YIELD_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_ARCANE_BONE_MEAL_SPEED_MULTIPLIER;
    public static ModConfigSpec.DoubleValue FERTILIZER_ARCANE_BONE_MEAL_YIELD_MULTIPLIER;

    // -------------------------------------------------------------------------
    // Machines — Planter
    // -------------------------------------------------------------------------
    public static ModConfigSpec.IntValue    PLANTER_BASE_POWER_CONSUMPTION;
    public static ModConfigSpec.IntValue    PLANTER_BASE_PROCESSING_TIME;
    public static ModConfigSpec.IntValue    PLANTER_ENERGY_BUFFER;

    // Cloche
    public static ModConfigSpec.DoubleValue CLOCHE_SPEED_MULTIPLIER;
    public static ModConfigSpec.DoubleValue CLOCHE_YIELD_MULTIPLIER;

    // Machines — Composter
    public static ModConfigSpec.IntValue COMPOSTER_BASE_POWER_CONSUMPTION;
    public static ModConfigSpec.IntValue COMPOSTER_BASE_PROCESSING_TIME;
    public static ModConfigSpec.IntValue COMPOSTER_ENERGY_BUFFER;
    public static ModConfigSpec.IntValue COMPOSTER_ITEMS_PER_BIOMASS;

    // Machines — Burner
    public static ModConfigSpec.IntValue BURNER_ENERGY_BUFFER;
    public static ModConfigSpec.IntValue BURNER_BIOMASS_RF_VALUE;
    public static ModConfigSpec.IntValue BURNER_BIOMASS_BURN_DURATION;
    public static ModConfigSpec.IntValue BURNER_COMPACTED_BIOMASS_RF_VALUE;
    public static ModConfigSpec.IntValue BURNER_COMPACTED_BIOMASS_BURN_DURATION;
    public static ModConfigSpec.IntValue BURNER_COMPACTED_BIOMASS_BLOCK_RF_VALUE;
    public static ModConfigSpec.IntValue BURNER_COMPACTED_BIOMASS_BLOCK_BURN_DURATION;
    public static ModConfigSpec.IntValue BURNER_CRUDE_BIOMASS_RF_VALUE;
    public static ModConfigSpec.IntValue BURNER_CRUDE_BIOMASS_BURN_DURATION;

    // Machines — Capacitors
    public static ModConfigSpec.IntValue CAPACITOR_T1_BUFFER;
    public static ModConfigSpec.IntValue CAPACITOR_T1_TRANSFER_RATE;
    public static ModConfigSpec.IntValue CAPACITOR_T2_BUFFER;
    public static ModConfigSpec.IntValue CAPACITOR_T2_TRANSFER_RATE;
    public static ModConfigSpec.IntValue CAPACITOR_T3_BUFFER;
    public static ModConfigSpec.IntValue CAPACITOR_T3_TRANSFER_RATE;

    // -------------------------------------------------------------------------
    // Runtime booleans (populated on config load)
    // -------------------------------------------------------------------------
    public static boolean enableMysticalAgriculture;
    public static boolean enableMysticalAgradditions;
    public static boolean enableFarmersDelight;
    public static boolean enableArsNouveau;
    public static boolean enableArsElemental;
    public static boolean enableSilentGear;
    public static boolean enableJustDireThings;
    public static boolean enableImmersiveEngineering;
    public static boolean enableEvilCraft;
    public static boolean enableForbiddenArcanus;
    public static boolean enableIntegratedDynamics;
    public static boolean enableOccultism;
    public static boolean enablePamsCrops;
    public static boolean enablePamsTrees;
    public static boolean enableCroptopia;
    public static boolean enableCobblemon;
    public static boolean enableActuallyAdditions;

    // =========================================================================
    // Registration
    // =========================================================================

    public static void register(ModContainer container) {
        compatibilityConfig();
        moduleConfig();
        fertilizerConfig();
        machineConfig();
        COMMON_CONFIG = COMMON_BUILDER.build();
        SPEC = COMMON_CONFIG;
        container.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
    }

    // =========================================================================
    // Config builders
    // =========================================================================

    private static void compatibilityConfig() {
        COMMON_BUILDER.comment("Mod Compatibility Settings").push("compatibility");
        ENABLE_MYSTICAL_AGRICULTURE  = COMMON_BUILDER.comment("Enable Mystical Agriculture compatibility").define("enable_mystical_agriculture", true);
        ENABLE_MYSTICAL_AGRADDITIONS = COMMON_BUILDER.comment("Enable Mystical Agradditions compatibility").define("enable_mystical_agradditions", true);
        ENABLE_FARMERS_DELIGHT       = COMMON_BUILDER.comment("Enable Farmer's Delight compatibility").define("enable_farmers_delight", true);
        ENABLE_ARS_NOUVEAU           = COMMON_BUILDER.comment("Enable Ars Nouveau compatibility").define("enable_ars_nouveau", true);
        ENABLE_ARS_ELEMENTAL         = COMMON_BUILDER.comment("Enable Ars Elemental compatibility").define("enable_ars_elemental", true);
        ENABLE_SILENT_GEAR           = COMMON_BUILDER.comment("Enable Silent Gear compatibility").define("enable_silent_gear", true);
        ENABLE_JUST_DIRE_THINGS      = COMMON_BUILDER.comment("Enable Just Dire Things compatibility").define("enable_just_dire_things", true);
        ENABLE_IMMERSIVE_ENGINEERING = COMMON_BUILDER.comment("Enable Immersive Engineering compatibility").define("enable_immersive_engineering", true);
        ENABLE_EVILCRAFT             = COMMON_BUILDER.comment("Enable EvilCraft compatibility").define("enable_evilcraft", true);
        ENABLE_FORBIDDEN_ARCANUS     = COMMON_BUILDER.comment("Enable Forbidden and Arcanus compatibility").define("enable_forbidden_arcanus", true);
        ENABLE_INTEGRATED_DYNAMICS   = COMMON_BUILDER.comment("Enable Integrated Dynamics compatibility").define("enable_integrated_dynamics", true);
        ENABLE_OCCULTISM             = COMMON_BUILDER.comment("Enable Occultism compatibility").define("enable_occultism", true);
        ENABLE_PAMS_CROPS            = COMMON_BUILDER.comment("Enable Pam's HarvestCraft - Crops compatibility").define("enable_pams_crops", true);
        ENABLE_PAMS_TREES            = COMMON_BUILDER.comment("Enable Pam's HarvestCraft - Trees compatibility").define("enable_pams_trees", true);
        ENABLE_CROPTOPIA             = COMMON_BUILDER.comment("Enable Croptopia compatibility").define("enable_croptopia", true);
        ENABLE_COBBLEMON             = COMMON_BUILDER.comment("Enable Cobblemon compatibility").define("enable_cobblemon", true);
        ENABLE_ACTUALLY_ADDITIONS    = COMMON_BUILDER.comment("Enable Actually Additions compatibility").define("enable_actually_additions", true);
        COMMON_BUILDER.pop();
    }

    private static void moduleConfig() {
        COMMON_BUILDER.comment("Module Effectiveness Settings").push("modules");

        COMMON_BUILDER.comment("Speed Module Configuration").push("speed_modules");
        SPEED_MODULE_MK1_MULTIPLIER       = COMMON_BUILDER.comment("Speed multiplier for SM-MK1 module").defineInRange("mk1_speed_multiplier", 1.1D, 0.1D, 10.0D);
        SPEED_MODULE_MK1_POWER_MULTIPLIER = COMMON_BUILDER.comment("Power consumption multiplier for SM-MK1 module").defineInRange("mk1_power_multiplier", 1.1D, 0.1D, 10.0D);
        SPEED_MODULE_MK2_MULTIPLIER       = COMMON_BUILDER.comment("Speed multiplier for SM-MK2 module").defineInRange("mk2_speed_multiplier", 1.25D, 0.1D, 10.0D);
        SPEED_MODULE_MK2_POWER_MULTIPLIER = COMMON_BUILDER.comment("Power consumption multiplier for SM-MK2 module").defineInRange("mk2_power_multiplier", 1.25D, 0.1D, 10.0D);
        SPEED_MODULE_MK3_MULTIPLIER       = COMMON_BUILDER.comment("Speed multiplier for SM-MK3 module").defineInRange("mk3_speed_multiplier", 1.5D, 0.1D, 10.0D);
        SPEED_MODULE_MK3_POWER_MULTIPLIER = COMMON_BUILDER.comment("Power consumption multiplier for SM-MK3 module").defineInRange("mk3_power_multiplier", 1.5D, 0.1D, 10.0D);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Yield Module Configuration").push("yield_modules");
        YIELD_MODULE_MK1_MULTIPLIER    = COMMON_BUILDER.comment("Yield multiplier for YM-MK1 module").defineInRange("mk1_yield_multiplier", 1.1D, 0.1D, 10.0D);
        YIELD_MODULE_MK1_SPEED_PENALTY = COMMON_BUILDER.comment("Speed penalty for YM-MK1 module (multiplier)").defineInRange("mk1_speed_penalty", 0.95D, 0.1D, 1.0D);
        YIELD_MODULE_MK2_MULTIPLIER    = COMMON_BUILDER.comment("Yield multiplier for YM-MK2 module").defineInRange("mk2_yield_multiplier", 1.25D, 0.1D, 10.0D);
        YIELD_MODULE_MK2_SPEED_PENALTY = COMMON_BUILDER.comment("Speed penalty for YM-MK2 module (multiplier)").defineInRange("mk2_speed_penalty", 0.85D, 0.1D, 1.0D);
        YIELD_MODULE_MK3_MULTIPLIER    = COMMON_BUILDER.comment("Yield multiplier for YM-MK3 module").defineInRange("mk3_yield_multiplier", 1.5D, 0.1D, 10.0D);
        YIELD_MODULE_MK3_SPEED_PENALTY = COMMON_BUILDER.comment("Speed penalty for YM-MK3 module (multiplier)").defineInRange("mk3_speed_penalty", 0.75D, 0.1D, 1.0D);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.pop();
    }

    private static void fertilizerConfig() {
        COMMON_BUILDER.comment("Fertilizer Configuration").push("fertilizers");
        FERTILIZER_BONE_MEAL_SPEED_MULTIPLIER             = COMMON_BUILDER.comment("Speed multiplier for Bone Meal").defineInRange("bone_meal_speed_multiplier", 1.2D, 0.1D, 10.0D);
        FERTILIZER_BONE_MEAL_YIELD_MULTIPLIER             = COMMON_BUILDER.comment("Yield multiplier for Bone Meal").defineInRange("bone_meal_yield_multiplier", 1.2D, 0.1D, 10.0D);
        FERTILIZER_CRUDE_BIOMASS_SPEED_MULTIPLIER               = COMMON_BUILDER.comment("Speed multiplier for Crude Biomass fertilizer").defineInRange("biomass_speed_multiplier", 1.25D, 0.1D, 10.0D);
        FERTILIZER_CRUDE_BIOMASS_YIELD_MULTIPLIER               = COMMON_BUILDER.comment("Yield multiplier for Crude Biomass fertilizer").defineInRange("biomass_yield_multiplier", 1.25D, 0.1D, 10.0D);
        FERTILIZER_BIOMASS_SPEED_MULTIPLIER               = COMMON_BUILDER.comment("Speed multiplier for Biomass fertilizer").defineInRange("biomass_speed_multiplier", 1.3D, 0.1D, 10.0D);
        FERTILIZER_BIOMASS_YIELD_MULTIPLIER               = COMMON_BUILDER.comment("Yield multiplier for Biomass fertilizer").defineInRange("biomass_yield_multiplier", 1.3D, 0.1D, 10.0D);
        FERTILIZER_COMPACTED_BIOMASS_SPEED_MULTIPLIER     = COMMON_BUILDER.comment("Speed multiplier for Compacted Biomass fertilizer").defineInRange("compacted_biomass_speed_multiplier", 1.8D, 0.1D, 10.0D);
        FERTILIZER_COMPACTED_BIOMASS_YIELD_MULTIPLIER     = COMMON_BUILDER.comment("Yield multiplier for Compacted Biomass fertilizer").defineInRange("compacted_biomass_yield_multiplier", 1.8D, 0.1D, 10.0D);
        FERTILIZER_FERTILIZED_ESSENCE_SPEED_MULTIPLIER    = COMMON_BUILDER.comment("Speed multiplier for Fertilized Essence").defineInRange("fertilized_essence_speed_multiplier", 1.3D, 0.1D, 10.0D);
        FERTILIZER_FERTILIZED_ESSENCE_YIELD_MULTIPLIER    = COMMON_BUILDER.comment("Yield multiplier for Fertilized Essence").defineInRange("fertilized_essence_yield_multiplier", 1.3D, 0.1D, 10.0D);
        FERTILIZER_MYSTICAL_FERTILIZER_SPEED_MULTIPLIER   = COMMON_BUILDER.comment("Speed multiplier for Mystical Fertilizer").defineInRange("mystical_fertilizer_speed_multiplier", 1.6D, 0.1D, 10.0D);
        FERTILIZER_MYSTICAL_FERTILIZER_YIELD_MULTIPLIER   = COMMON_BUILDER.comment("Yield multiplier for Mystical Fertilizer").defineInRange("mystical_fertilizer_yield_multiplier", 1.6D, 0.1D, 10.0D);
        FERTILIZER_IMMERSIVE_FERTILIZER_SPEED_MULTIPLIER  = COMMON_BUILDER.comment("Speed multiplier for Immersive Engineering Fertilizer").defineInRange("immersive_fertilizer_speed_multiplier", 1.4D, 0.1D, 10.0D);
        FERTILIZER_IMMERSIVE_FERTILIZER_YIELD_MULTIPLIER  = COMMON_BUILDER.comment("Yield multiplier for Immersive Engineering Fertilizer").defineInRange("immersive_fertilizer_yield_multiplier", 1.4D, 0.1D, 10.0D);
        FERTILIZER_ARCANE_BONE_MEAL_SPEED_MULTIPLIER      = COMMON_BUILDER.comment("Speed multiplier for Arcane Bone Meal").defineInRange("arcane_bone_meal_speed_multiplier", 1.5D, 0.1D, 10.0D);
        FERTILIZER_ARCANE_BONE_MEAL_YIELD_MULTIPLIER      = COMMON_BUILDER.comment("Yield multiplier for Arcane Bone Meal").defineInRange("arcane_bone_meal_yield_multiplier", 1.5D, 0.1D, 10.0D);
        COMMON_BUILDER.pop();
    }

    private static void machineConfig() {
        COMMON_BUILDER.comment("Machine Settings").push("machines");
        planterConfig();
        composterConfig();
        burnerConfig();
        capacitorConfig();
        COMMON_BUILDER.pop();
    }

    private static void planterConfig() {
        COMMON_BUILDER.comment("Advanced Planter Configuration").push("advanced_planter");
        PLANTER_BASE_POWER_CONSUMPTION = COMMON_BUILDER.comment("Base power consumption for Advanced Planter (RF/t)").defineInRange("base_power_consumption", 128, 1, 100000);
        PLANTER_BASE_PROCESSING_TIME   = COMMON_BUILDER.comment("Base processing time for Planters (ticks)").defineInRange("base_processing_time", 1200, 1, 72000);
        PLANTER_ENERGY_BUFFER          = COMMON_BUILDER.comment("Energy buffer capacity for Advanced Planter (RF)").defineInRange("energy_buffer", 100000, 1000, 10000000);
        CLOCHE_SPEED_MULTIPLIER        = COMMON_BUILDER.comment("Speed multiplier applied when a cloche is attached to a planter").defineInRange("cloche_speed_multiplier", 1.15D, 0.1D, 10.0D);
        CLOCHE_YIELD_MULTIPLIER        = COMMON_BUILDER.comment("Yield multiplier applied when a cloche is attached to a planter").defineInRange("cloche_yield_multiplier", 1.10D, 0.1D, 10.0D);
        COMMON_BUILDER.pop();
    }

    private static void composterConfig() {
        COMMON_BUILDER.comment("Composter Configuration").push("composter");
        COMPOSTER_BASE_POWER_CONSUMPTION = COMMON_BUILDER.comment("Base power consumption for Composter (RF/t)").defineInRange("base_power_consumption", 128, 1, 100000);
        COMPOSTER_BASE_PROCESSING_TIME   = COMMON_BUILDER.comment("Base processing time for Composter (ticks)").defineInRange("base_processing_time", 600, 1, 72000);
        COMPOSTER_ENERGY_BUFFER          = COMMON_BUILDER.comment("Energy buffer capacity for Composter (RF)").defineInRange("energy_buffer", 100000, 1000, 10000000);
        COMPOSTER_ITEMS_PER_BIOMASS      = COMMON_BUILDER.comment("Number of organic items required per biomass").defineInRange("items_per_biomass", 32, 1, 256);
        COMMON_BUILDER.pop();
    }

    private static void burnerConfig() {
        COMMON_BUILDER.comment("Burner Configuration").push("burner");
        BURNER_ENERGY_BUFFER                      = COMMON_BUILDER.comment("Energy buffer capacity for Burner (RF)").defineInRange("energy_buffer", 100000, 1000, 10000000);
        BURNER_BIOMASS_RF_VALUE                   = COMMON_BUILDER.comment("RF generated per biomass item").defineInRange("biomass_rf_value", 2500, 100, 100000);
        BURNER_BIOMASS_BURN_DURATION              = COMMON_BUILDER.comment("Burn duration for biomass in ticks (20 ticks = 1 second)").defineInRange("biomass_burn_duration", 100, 20, 72000);
        BURNER_COMPACTED_BIOMASS_RF_VALUE         = COMMON_BUILDER.comment("RF generated per compacted biomass item").defineInRange("compacted_biomass_rf_value", 22500, 1000, 1000000);
        BURNER_COMPACTED_BIOMASS_BURN_DURATION    = COMMON_BUILDER.comment("Burn duration for compacted biomass in ticks (20 ticks = 1 second)").defineInRange("compacted_biomass_burn_duration", 180, 20, 72000);
        BURNER_COMPACTED_BIOMASS_BLOCK_RF_VALUE   = COMMON_BUILDER.comment("RF generated per compacted biomass block").defineInRange("compacted_biomass_block_rf_value", 225000, 1000, 1000000);
        BURNER_COMPACTED_BIOMASS_BLOCK_BURN_DURATION = COMMON_BUILDER.comment("Burn duration for compacted biomass block in ticks (20 ticks = 1 second)").defineInRange("compacted_biomass_block_burn_duration", 1800, 20, 72000);
        BURNER_CRUDE_BIOMASS_RF_VALUE             = COMMON_BUILDER.comment("RF generated per crude biomass item").defineInRange("crude_biomass_rf_value", 250, 50, 50000);
        BURNER_CRUDE_BIOMASS_BURN_DURATION        = COMMON_BUILDER.comment("Burn duration for crude biomass in ticks (20 ticks = 1 second)").defineInRange("crude_biomass_burn_duration", 50, 20, 72000);
        COMMON_BUILDER.pop();
    }

    private static void capacitorConfig() {
        COMMON_BUILDER.comment("Capacitor Configuration").push("capacitors");
        COMMON_BUILDER.comment("Tier 1 Capacitor").push("tier_1");
        CAPACITOR_T1_BUFFER        = COMMON_BUILDER.comment("Energy buffer capacity for T1 Capacitor (RF)").defineInRange("buffer_capacity", 500000, 10000, 100000000);
        CAPACITOR_T1_TRANSFER_RATE = COMMON_BUILDER.comment("Energy transfer rate for T1 Capacitor (RF/t)").defineInRange("transfer_rate", 512, 1, 100000);
        COMMON_BUILDER.pop();
        COMMON_BUILDER.comment("Tier 2 Capacitor").push("tier_2");
        CAPACITOR_T2_BUFFER        = COMMON_BUILDER.comment("Energy buffer capacity for T2 Capacitor (RF)").defineInRange("buffer_capacity", 1000000, 10000, 100000000);
        CAPACITOR_T2_TRANSFER_RATE = COMMON_BUILDER.comment("Energy transfer rate for T2 Capacitor (RF/t)").defineInRange("transfer_rate", 2048, 1, 100000);
        COMMON_BUILDER.pop();
        COMMON_BUILDER.comment("Tier 3 Capacitor").push("tier_3");
        CAPACITOR_T3_BUFFER        = COMMON_BUILDER.comment("Energy buffer capacity for T3 Capacitor (RF)").defineInRange("buffer_capacity", 4000000, 10000, 100000000);
        CAPACITOR_T3_TRANSFER_RATE = COMMON_BUILDER.comment("Energy transfer rate for T3 Capacitor (RF/t)").defineInRange("transfer_rate", 8192, 1, 100000);
        COMMON_BUILDER.pop();
        COMMON_BUILDER.pop();
    }

    // =========================================================================
    // Getters — Modules
    // =========================================================================

    public static double getSpeedModuleMk1Multiplier()      { return SPEED_MODULE_MK1_MULTIPLIER.get(); }
    public static double getSpeedModuleMk1PowerMultiplier() { return SPEED_MODULE_MK1_POWER_MULTIPLIER.get(); }
    public static double getSpeedModuleMk2Multiplier()      { return SPEED_MODULE_MK2_MULTIPLIER.get(); }
    public static double getSpeedModuleMk2PowerMultiplier() { return SPEED_MODULE_MK2_POWER_MULTIPLIER.get(); }
    public static double getSpeedModuleMk3Multiplier()      { return SPEED_MODULE_MK3_MULTIPLIER.get(); }
    public static double getSpeedModuleMk3PowerMultiplier() { return SPEED_MODULE_MK3_POWER_MULTIPLIER.get(); }
    public static double getYieldModuleMk1Multiplier()      { return YIELD_MODULE_MK1_MULTIPLIER.get(); }
    public static double getYieldModuleMk1SpeedPenalty()    { return YIELD_MODULE_MK1_SPEED_PENALTY.get(); }
    public static double getYieldModuleMk2Multiplier()      { return YIELD_MODULE_MK2_MULTIPLIER.get(); }
    public static double getYieldModuleMk2SpeedPenalty()    { return YIELD_MODULE_MK2_SPEED_PENALTY.get(); }
    public static double getYieldModuleMk3Multiplier()      { return YIELD_MODULE_MK3_MULTIPLIER.get(); }
    public static double getYieldModuleMk3SpeedPenalty()    { return YIELD_MODULE_MK3_SPEED_PENALTY.get(); }

    // =========================================================================
    // Getters — Fertilizers
    // =========================================================================

    public static double getFertilizerBoneMealSpeedMultiplier()             { return FERTILIZER_BONE_MEAL_SPEED_MULTIPLIER.get(); }
    public static double getFertilizerBoneMealYieldMultiplier()             { return FERTILIZER_BONE_MEAL_YIELD_MULTIPLIER.get(); }
    public static double getFertilizerCrudeBiomassSpeedMultiplier()              { return FERTILIZER_CRUDE_BIOMASS_SPEED_MULTIPLIER.get(); }
    public static double getFertilizerCrudeBiomassYieldMultiplier()              { return FERTILIZER_CRUDE_BIOMASS_YIELD_MULTIPLIER.get(); }
    public static double getFertilizerBiomassSpeedMultiplier()              { return FERTILIZER_BIOMASS_SPEED_MULTIPLIER.get(); }
    public static double getFertilizerBiomassYieldMultiplier()              { return FERTILIZER_BIOMASS_YIELD_MULTIPLIER.get(); }
    public static double getFertilizerCompactedBiomassSpeedMultiplier()     { return FERTILIZER_COMPACTED_BIOMASS_SPEED_MULTIPLIER.get(); }
    public static double getFertilizerCompactedBiomassYieldMultiplier()     { return FERTILIZER_COMPACTED_BIOMASS_YIELD_MULTIPLIER.get(); }
    public static double getFertilizerFertilizedEssenceSpeedMultiplier()    { return FERTILIZER_FERTILIZED_ESSENCE_SPEED_MULTIPLIER.get(); }
    public static double getFertilizerFertilizedEssenceYieldMultiplier()    { return FERTILIZER_FERTILIZED_ESSENCE_YIELD_MULTIPLIER.get(); }
    public static double getFertilizerMysticalFertilizerSpeedMultiplier()   { return FERTILIZER_MYSTICAL_FERTILIZER_SPEED_MULTIPLIER.get(); }
    public static double getFertilizerMysticalFertilizerYieldMultiplier()   { return FERTILIZER_MYSTICAL_FERTILIZER_YIELD_MULTIPLIER.get(); }
    public static double getFertilizerImmersiveFertilizerSpeedMultiplier()  { return FERTILIZER_IMMERSIVE_FERTILIZER_SPEED_MULTIPLIER.get(); }
    public static double getFertilizerImmersiveFertilizerYieldMultiplier()  { return FERTILIZER_IMMERSIVE_FERTILIZER_YIELD_MULTIPLIER.get(); }
    public static double getFertilizerArcaneBoneMealSpeedMultiplier()       { return FERTILIZER_ARCANE_BONE_MEAL_SPEED_MULTIPLIER.get(); }
    public static double getFertilizerArcaneBoneMealYieldMultiplier()       { return FERTILIZER_ARCANE_BONE_MEAL_YIELD_MULTIPLIER.get(); }

    // =========================================================================
    // Getters — Cloche
    // =========================================================================

    public static double getClocheSpeedMultiplier() { return CLOCHE_SPEED_MULTIPLIER.get(); }
    public static double getClocheYieldMultiplier() { return CLOCHE_YIELD_MULTIPLIER.get(); }

    // =========================================================================
    // Getters — Planter
    // =========================================================================

    public static int getPlanterBasePowerConsumption() { return PLANTER_BASE_POWER_CONSUMPTION.get(); }
    public static int getPlanterBaseProcessingTime()   { return PLANTER_BASE_PROCESSING_TIME.get(); }
    public static int getPlanterEnergyBuffer()         { return PLANTER_ENERGY_BUFFER.get(); }

    // =========================================================================
    // Getters — Composter
    // =========================================================================

    public static int getComposterBasePowerConsumption() { return COMPOSTER_BASE_POWER_CONSUMPTION.get(); }
    public static int getComposterBaseProcessingTime()   { return COMPOSTER_BASE_PROCESSING_TIME.get(); }
    public static int getComposterEnergyBuffer()         { return COMPOSTER_ENERGY_BUFFER.get(); }
    public static int getComposterItemsPerBiomass()      { return COMPOSTER_ITEMS_PER_BIOMASS.get(); }

    // =========================================================================
    // Getters — Burner
    // =========================================================================

    public static int getBurnerEnergyBuffer()                    { return BURNER_ENERGY_BUFFER.get(); }
    public static int getBurnerBiomassRfValue()                  { return BURNER_BIOMASS_RF_VALUE.get(); }
    public static int getBurnerBiomassBurnDuration()             { return BURNER_BIOMASS_BURN_DURATION.get(); }
    public static int getBurnerCompactedBiomassRfValue()         { return BURNER_COMPACTED_BIOMASS_RF_VALUE.get(); }
    public static int getBurnerCompactedBiomassBurnDuration()    { return BURNER_COMPACTED_BIOMASS_BURN_DURATION.get(); }
    public static int getBurnerCompactedBiomassBlockRfValue()    { return BURNER_COMPACTED_BIOMASS_BLOCK_RF_VALUE.get(); }
    public static int getBurnerCompactedBiomassBlockBurnDuration() { return BURNER_COMPACTED_BIOMASS_BLOCK_BURN_DURATION.get(); }
    public static int getBurnerCrudeBiomassRfValue()             { return BURNER_CRUDE_BIOMASS_RF_VALUE.get(); }
    public static int getBurnerCrudeBiomassBurnDuration()        { return BURNER_CRUDE_BIOMASS_BURN_DURATION.get(); }

    // =========================================================================
    // Getters — Capacitors
    // =========================================================================

    public static int getCapacitorT1Buffer()       { return CAPACITOR_T1_BUFFER.get(); }
    public static int getCapacitorT1TransferRate() { return CAPACITOR_T1_TRANSFER_RATE.get(); }
    public static int getCapacitorT2Buffer()       { return CAPACITOR_T2_BUFFER.get(); }
    public static int getCapacitorT2TransferRate() { return CAPACITOR_T2_TRANSFER_RATE.get(); }
    public static int getCapacitorT3Buffer()       { return CAPACITOR_T3_BUFFER.get(); }
    public static int getCapacitorT3TransferRate() { return CAPACITOR_T3_TRANSFER_RATE.get(); }

    // =========================================================================
    // Config lifecycle
    // =========================================================================

    public static void loadConfig() {
        CompostableConfig.loadConfig();
        PlantablesConfig.loadConfig();
        LOGGER.info("AgriTech: Evolved configs reloaded");
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        enableMysticalAgriculture  = ENABLE_MYSTICAL_AGRICULTURE.get()  && ModList.get().isLoaded("mysticalagriculture");
        enableMysticalAgradditions = ENABLE_MYSTICAL_AGRADDITIONS.get() && ModList.get().isLoaded("mysticalagradditions");
        enableFarmersDelight       = ENABLE_FARMERS_DELIGHT.get()       && ModList.get().isLoaded("farmersdelight");
        enableArsNouveau           = ENABLE_ARS_NOUVEAU.get()           && ModList.get().isLoaded("ars_nouveau");
        enableArsElemental         = ENABLE_ARS_ELEMENTAL.get()         && ModList.get().isLoaded("ars_elemental");
        enableSilentGear           = ENABLE_SILENT_GEAR.get()           && ModList.get().isLoaded("silentgear");
        enableJustDireThings       = ENABLE_JUST_DIRE_THINGS.get()      && ModList.get().isLoaded("justdirethings");
        enableImmersiveEngineering = ENABLE_IMMERSIVE_ENGINEERING.get() && ModList.get().isLoaded("immersiveengineering");
        enableEvilCraft            = ENABLE_EVILCRAFT.get()             && ModList.get().isLoaded("evilcraft");
        enableForbiddenArcanus     = ENABLE_FORBIDDEN_ARCANUS.get()     && ModList.get().isLoaded("forbidden_arcanus");
        enableIntegratedDynamics   = ENABLE_INTEGRATED_DYNAMICS.get()   && ModList.get().isLoaded("integrateddynamics");
        enableOccultism            = ENABLE_OCCULTISM.get()             && ModList.get().isLoaded("occultism");
        enablePamsCrops            = ENABLE_PAMS_CROPS.get()            && ModList.get().isLoaded("pamhc2crops");
        enablePamsTrees            = ENABLE_PAMS_TREES.get()            && ModList.get().isLoaded("pamhc2trees");
        enableCroptopia            = ENABLE_CROPTOPIA.get()             && ModList.get().isLoaded("croptopia");
        enableCobblemon            = ENABLE_COBBLEMON.get()             && ModList.get().isLoaded("cobblemon");
        enableActuallyAdditions    = ENABLE_ACTUALLY_ADDITIONS.get()    && ModList.get().isLoaded("actuallyadditions");
        LOGGER.info("AgriTech: Evolved configuration loaded");
        CompostableConfig.loadConfig();
        PlantablesConfig.loadConfig();
        logModCompatibility();
    }

    private static void logModCompatibility() {
        record Mod(boolean enabled, String modId, String label) {}
        LOGGER.info("Mod Compatibility Status:");
        for (Mod m : new Mod[]{
                new Mod(enableMysticalAgriculture,  "mysticalagriculture",  "Mystical Agriculture"),
                new Mod(enableMysticalAgradditions, "mysticalagradditions", "Mystical Agradditions"),
                new Mod(enableFarmersDelight,       "farmersdelight",       "Farmer's Delight"),
                new Mod(enableArsNouveau,           "ars_nouveau",          "Ars Nouveau"),
                new Mod(enableArsElemental,         "ars_elemental",        "Ars Elemental"),
                new Mod(enableSilentGear,           "silentgear",           "Silent Gear"),
                new Mod(enableJustDireThings,       "justdirethings",       "Just Dire Things"),
                new Mod(enableImmersiveEngineering, "immersiveengineering", "Immersive Engineering"),
                new Mod(enableEvilCraft,            "evilcraft",            "EvilCraft"),
                new Mod(enableForbiddenArcanus,     "forbidden_arcanus",    "Forbidden and Arcanus"),
                new Mod(enableIntegratedDynamics,   "integrateddynamics",   "Integrated Dynamics"),
                new Mod(enableOccultism,            "occultism",            "Occultism"),
                new Mod(enablePamsCrops,            "pamhc2crops",          "Pam's HarvestCraft - Crops"),
                new Mod(enablePamsTrees,            "pamhc2trees",          "Pam's HarvestCraft - Trees"),
                new Mod(enableCroptopia,            "croptopia",            "Croptopia"),
                new Mod(enableCobblemon,            "cobblemon",            "Cobblemon"),
                new Mod(enableActuallyAdditions,    "actuallyadditions",    "Actually Additions")
        }) {
            if (m.enabled() && ModList.get().isLoaded(m.modId())) {
                LOGGER.info("  - {}: ENABLED", m.label());
            }
        }
    }
}