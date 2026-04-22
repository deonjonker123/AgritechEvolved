package com.misterd.agritechevolved.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.misterd.agritechevolved.Config;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PlantablesConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, CropInfo> crops = new HashMap<>();
    private static Map<String, TreeInfo> trees = new HashMap<>();
    private static Map<String, SoilInfo> soils = new HashMap<>();
    private static Map<String, FertilizerInfo> fertilizers = new HashMap<>();

    public static void loadConfig() {
        LOGGER.info("PlantablesConfig.loadConfig() invoked.");
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("agritechevolved/plantables.json");
        if (!Files.exists(configPath)) {
            createDefaultConfig(configPath);
        }

        try {
            String jsonString = Files.readString(configPath);
            PlantablesConfigData configData = GSON.fromJson(jsonString, PlantablesConfigData.class);
            processConfig(configData);
        } catch (JsonSyntaxException | IOException e) {
            LOGGER.error("Failed to load plantables config file: {}", e.getMessage());
            LOGGER.info("Loading default plantables configuration instead");
            processConfig(getDefaultConfig());
        }

        PlantablesOverrideConfig.loadOverrides(crops, trees, soils, soils, fertilizers);
    }

    private static void createDefaultConfig(Path configPath) {
        try {
            Files.createDirectories(configPath.getParent());
            PlantablesConfigData defaultConfig = getDefaultConfig();
            Files.writeString(configPath, GSON.toJson(defaultConfig));
        } catch (IOException e) {
            LOGGER.error("Failed to create default config file: {}", e.getMessage());
        }
    }

    private static PlantablesConfigData getDefaultConfig() {
        LOGGER.info("Generating default plantables config.");
        PlantablesConfigData config = new PlantablesConfigData();

        List<CropEntry> defaultCrops = new ArrayList<>();
        addVanillaCrops(defaultCrops);
        if (Config.enableMysticalAgriculture) {
            LOGGER.info("Adding Mystical Agriculture crops to AgriTech: Evolved config");
            addMysticalAgricultureCrops(defaultCrops);
        }
        if (Config.enableFarmersDelight) {
            LOGGER.info("Adding Farmer's Delight crops to AgriTech: Evolved config");
            addFarmersDelightCrops(defaultCrops);
        }
        if (Config.enableArsNouveau) {
            LOGGER.info("Adding Ars Nouveau crops to AgriTech: Evolved config");
            addArsNouveauCrops(defaultCrops);
        }
        if (Config.enableSilentGear) {
            LOGGER.info("Adding Silent Gear crops to AgriTech: Evolved config");
            addSilentGearCrops(defaultCrops);
        }
        if (Config.enableImmersiveEngineering) {
            LOGGER.info("Adding Immersive Engineering Hemp Fiber to AgriTech: Evolved config");
            addImmersiveEngineering(defaultCrops);
        }
        if (Config.enableOccultism) {
            LOGGER.info("Adding Occultism crops to AgriTech: Evolved config");
            addOccultimCrops(defaultCrops);
        }
        if (Config.enablePamsCrops) {
            LOGGER.info("Adding Pam's HarvestCraft - Crops to AgriTech: Evolved config");
            addPamsCrops(defaultCrops);
        }
        if (Config.enableCroptopia) {
            LOGGER.info("Adding Croptopia crops to AgriTech: Evolved config");
            addCroptopiaCrops(defaultCrops);
        }
        if (Config.enableActuallyAdditions) {
            LOGGER.info("Adding Actually Additions crops to AgriTech: Evolved config");
            addActuallyAdditionsCrops(defaultCrops);
        }
        if (Config.enableCobblemon) {
            LOGGER.info("Adding Cobblemon crops to AgriTech: Evolved config");
            addCobblemonCrops(defaultCrops);
        }
        config.allowedCrops = defaultCrops;

        List<TreeEntry> defaultTrees = new ArrayList<>();
        addVanillaTrees(defaultTrees);
        if (Config.enableArsElemental) {
            LOGGER.info("Adding Ars Nouveau Archwood trees to AgriTech: Evolved config");
            addArsElementalTrees(defaultTrees);
        }
        if (Config.enableArsNouveau) {
            LOGGER.info("Adding Ars Nouveau Archwood trees to AgriTech: Evolved config");
            addArsNouveauTrees(defaultTrees);
        }
        if (Config.enableEvilCraft) {
            LOGGER.info("Adding Evilcraft trees to AgriTech: Evolved config");
            addEvilCraftTrees(defaultTrees);
        }
        if (Config.enableForbiddenArcanus) {
            LOGGER.info("Adding Forbidden Arcanus trees to AgriTech: Evolved config");
            addForbiddenArcanusTrees(defaultTrees);
        }
        if (Config.enableIntegratedDynamics) {
            LOGGER.info("Adding Menril trees to AgriTech: Evolved config");
            addIntegratedDynamicsTrees(defaultTrees);
        }
        if (Config.enableSilentGear) {
            LOGGER.info("Adding Silent Gear trees to AgriTech: Evolved config");
            addSilentGearTrees(defaultTrees);
        }
        if (Config.enableOccultism) {
            LOGGER.info("Adding Occultism trees to AgriTech: Evolved config");
            addOccultismTrees(defaultTrees);
        }
        if (Config.enablePamsTrees) {
            LOGGER.info("Adding Pam's HarvestCraft - Trees to AgriTech: Evolved config");
            addPamsTrees(defaultTrees);
        }
        if (Config.enableCroptopia) {
            LOGGER.info("Adding Croptopia trees to AgriTech: Evolved config");
            addCroptopiaTrees(defaultTrees);
        }
        if (Config.enableCobblemon) {
            LOGGER.info("Adding Cobblemon trees to AgriTech: Evolved config");
            addCobblemonTrees(defaultTrees);
        }
        config.allowedTrees = defaultTrees;

        List<SoilEntry> defaultSoils = new ArrayList<>();
        addVanillaSoils(defaultSoils);
        if (Config.enableMysticalAgriculture) {
            LOGGER.info("Adding Mystical Agriculture soils to AgriTech: Evolved config");
            addMysticalAgricultureSoils(defaultSoils);
        }
        if (Config.enableFarmersDelight) {
            LOGGER.info("Adding Farmer's Delight soils to AgriTech: Evolved config");
            addFarmersDelightSoils(defaultSoils);
        }
        if (Config.enableJustDireThings) {
            LOGGER.info("Adding Just Dire Things soils to AgriTech: Evolved config");
            addJustDireThingsSoils(defaultSoils);
        }
        config.allowedSoils = defaultSoils;

        List<FertilizerEntry> defaultFertilizers = new ArrayList<>();
        addVanillaFertilizers(defaultFertilizers);
        if (Config.enableImmersiveEngineering) {
            LOGGER.info("Adding Immersive Engineering fertilizer to AgriTech: Evolved config");
            addImmersiveEngineeringFertilizers(defaultFertilizers);
        }
        if (Config.enableForbiddenArcanus) {
            LOGGER.info("Adding Forbidden Arcanus fertilizer to AgriTech: Evolved config");
            addForbiddenArcanusFertilizers(defaultFertilizers);
        }
        if (Config.enableMysticalAgriculture) {
            LOGGER.info("Adding Mystical Agriculture fertilizer to AgriTech: Evolved config");
            addMysticalAgricultureFertilizers(defaultFertilizers);
        }
        config.allowedFertilizers = defaultFertilizers;

        return config;
    }

    // Standard soils accepted by most farmland crops
    private static final List<String> STANDARD_FARMLAND_SOILS = List.of(
            "minecraft:farmland",
            "mysticalagriculture:inferium_farmland",
            "mysticalagriculture:prudentium_farmland",
            "mysticalagriculture:tertium_farmland",
            "mysticalagriculture:imperium_farmland",
            "mysticalagriculture:supremium_farmland",
            "mysticalagradditions:insanium_farmland",
            "justdirethings:goosoil_tier1",
            "justdirethings:goosoil_tier2",
            "justdirethings:goosoil_tier3",
            "justdirethings:goosoil_tier4",
            "farmersdelight:rich_soil_farmland",
            "agritechevolved:infused_farmland"
    );

    // Standard soils accepted by most ground-planted crops (flowers, berries, etc.)
    private static final List<String> STANDARD_GROUND_SOILS = List.of(
            "minecraft:farmland",
            "minecraft:dirt",
            "minecraft:grass_block",
            "minecraft:rooted_dirt",
            "minecraft:coarse_dirt",
            "minecraft:podzol",
            "minecraft:mycelium",
            "minecraft:mud",
            "minecraft:moss_block",
            "minecraft:muddy_mangrove_roots",
            "minecraft:water_bucket",
            "mysticalagriculture:inferium_farmland",
            "mysticalagriculture:prudentium_farmland",
            "mysticalagriculture:tertium_farmland",
            "mysticalagriculture:imperium_farmland",
            "mysticalagriculture:supremium_farmland",
            "mysticalagradditions:insanium_farmland",
            "justdirethings:goosoil_tier1",
            "justdirethings:goosoil_tier2",
            "justdirethings:goosoil_tier3",
            "justdirethings:goosoil_tier4",
            "farmersdelight:rich_soil_farmland",
            "agritechevolved:infused_farmland",
            "agritechevolved:mulch"
    );

    private static CropEntry makeCrop(String seed, List<String> validSoils, DropEntry... drops) {
        CropEntry crop = new CropEntry();
        crop.seed = seed;
        crop.validSoils = new ArrayList<>(validSoils);
        crop.drops = new ArrayList<>(List.of(drops));
        return crop;
    }

    private static DropEntry makeDrop(String item, int min, int max, float chance) {
        DropEntry drop = new DropEntry();
        drop.item = item;
        drop.count = new CountRange(min, max);
        drop.chance = chance;
        return drop;
    }

    private static DropEntry makeDrop(String item, int min, int max) {
        return makeDrop(item, min, max, 1.0F);
    }

    private static void addVanillaCrops(List<CropEntry> crops) {
        crops.add(makeCrop("minecraft:wheat_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("minecraft:wheat", 1, 1),
                makeDrop("minecraft:wheat_seeds", 1, 2, 0.5F)));

        crops.add(makeCrop("minecraft:beetroot_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("minecraft:beetroot", 1, 1),
                makeDrop("minecraft:beetroot_seeds", 1, 2, 0.5F)));

        crops.add(makeCrop("minecraft:carrot", STANDARD_FARMLAND_SOILS,
                makeDrop("minecraft:carrot", 2, 5)));

        crops.add(makeCrop("minecraft:potato", STANDARD_FARMLAND_SOILS,
                makeDrop("minecraft:potato", 2, 5),
                makeDrop("minecraft:poisonous_potato", 1, 1, 0.02F)));

        crops.add(makeCrop("minecraft:melon_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("minecraft:melon_slice", 3, 7)));

        crops.add(makeCrop("minecraft:pumpkin_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("minecraft:pumpkin", 1, 1)));

        crops.add(makeCrop("minecraft:sugar_cane",
                List.of("minecraft:dirt", "minecraft:grass_block", "minecraft:sand", "minecraft:red_sand", "snad:snad", "snad:red_snad", "agritechevolved:mulch"),
                makeDrop("minecraft:sugar_cane", 1, 3)));

        crops.add(makeCrop("minecraft:cactus",
                List.of("minecraft:sand", "minecraft:red_sand", "snad:snad", "snad:red_snad"),
                makeDrop("minecraft:cactus", 1, 3)));

        crops.add(makeCrop("minecraft:bamboo",
                List.of("minecraft:dirt", "minecraft:grass_block", "minecraft:rooted_dirt", "minecraft:coarse_dirt",
                        "minecraft:podzol", "minecraft:mycelium", "minecraft:mud", "minecraft:moss_block",
                        "minecraft:muddy_mangrove_roots",
                        "farmersdelight:rich_soil", "farmersdelight:organic_compost", "agritechevolved:mulch"),
                makeDrop("minecraft:bamboo", 2, 4)));

        crops.add(makeCrop("minecraft:sweet_berries",
                List.of("minecraft:farmland", "minecraft:dirt", "minecraft:grass_block", "minecraft:rooted_dirt",
                        "minecraft:coarse_dirt", "minecraft:podzol", "minecraft:mycelium", "minecraft:mud",
                        "minecraft:moss_block", "minecraft:muddy_mangrove_roots", "minecraft:pale_moss_block",
                        "mysticalagriculture:inferium_farmland", "mysticalagriculture:prudentium_farmland",
                        "mysticalagriculture:tertium_farmland", "mysticalagriculture:imperium_farmland",
                        "mysticalagriculture:supremium_farmland", "mysticalagradditions:insanium_farmland",

                        "justdirethings:goosoil_tier1", "justdirethings:goosoil_tier2",
                        "justdirethings:goosoil_tier3", "justdirethings:goosoil_tier4",
                        "farmersdelight:rich_soil_farmland", "farmersdelight:rich_soil", "farmersdelight:organic_compost"),
                makeDrop("minecraft:sweet_berries", 2, 4)));

        crops.add(makeCrop("minecraft:glow_berries",
                List.of("minecraft:moss_block", "minecraft:pale_moss_block"),
                makeDrop("minecraft:glow_berries", 2, 4)));

        crops.add(makeCrop("minecraft:nether_wart",
                List.of("minecraft:soul_sand", "snad:suol_snad"),
                makeDrop("minecraft:nether_wart", 1, 3)));

        crops.add(makeCrop("minecraft:chorus_flower",
                List.of("minecraft:end_stone"),
                makeDrop("minecraft:chorus_fruit", 1, 3),
                makeDrop("minecraft:chorus_flower", 1, 1, 0.02F)));

        crops.add(makeCrop("minecraft:kelp",
                List.of("minecraft:water_bucket"),
                makeDrop("minecraft:kelp", 1, 2)));

        crops.add(makeCrop("minecraft:lily_pad",
                List.of("minecraft:water_bucket"),
                makeDrop("minecraft:lily_pad", 1, 1)));

        crops.add(makeCrop("minecraft:spore_blossom",
                List.of("minecraft:moss_block"),
                makeDrop("minecraft:spore_blossom", 1, 1)));

        crops.add(makeCrop("minecraft:moss_block",
                List.of("minecraft:stone"),
                makeDrop("minecraft:moss_block", 1, 2),
                makeDrop("minecraft:moss_carpet", 1, 1, 0.1F),
                makeDrop("minecraft:wheat_seeds", 1, 1, 0.1F)));

        crops.add(makeCrop("minecraft:pale_moss_block",
                List.of("minecraft:stone"),
                makeDrop("minecraft:pale_moss_block", 1, 2),
                makeDrop("minecraft:pale_moss_carpet", 1, 1, 0.1F)));

        crops.add(makeCrop("minecraft:brown_mushroom",
                List.of("minecraft:mycelium", "minecraft:podzol",
                        "farmersdelight:rich_soil", "farmersdelight:organic_compost", "agritechevolved:mulch"),
                makeDrop("minecraft:brown_mushroom", 1, 1)));

        crops.add(makeCrop("minecraft:red_mushroom",
                List.of("minecraft:mycelium", "minecraft:podzol",
                        "farmersdelight:rich_soil", "farmersdelight:organic_compost", "agritechevolved:mulch"),
                makeDrop("minecraft:red_mushroom", 1, 1)));

        crops.add(makeCrop("minecraft:cocoa_beans",
                List.of("minecraft:jungle_log", "minecraft:jungle_wood",
                        "minecraft:stripped_jungle_log", "minecraft:stripped_jungle_wood"),
                makeDrop("minecraft:cocoa_beans", 1, 3)));

        crops.add(makeCrop("minecraft:pitcher_pod", STANDARD_FARMLAND_SOILS,
                makeDrop("minecraft:pitcher_plant", 1, 1)));

        crops.add(makeCrop("minecraft:torchflower_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("minecraft:torchflower", 1, 1)));

        for (String flower : new String[]{
                "minecraft:allium", "minecraft:azure_bluet", "minecraft:blue_orchid",
                "minecraft:cornflower", "minecraft:dandelion", "minecraft:lily_of_the_valley",
                "minecraft:oxeye_daisy", "minecraft:poppy", "minecraft:red_tulip",
                "minecraft:orange_tulip", "minecraft:white_tulip", "minecraft:pink_tulip",
                "minecraft:wither_rose", "minecraft:lilac", "minecraft:peony",
                "minecraft:rose_bush", "minecraft:sunflower"
        }) {
            crops.add(makeCrop(flower, STANDARD_GROUND_SOILS, makeDrop(flower, 1, 1)));
        }
    }

    private static final List<String> STANDARD_TREE_SOILS = List.of(
    "agritechevolved:infused_farmland", "agritechevolved:mulch",
            "minecraft:dirt", "minecraft:grass_block", "minecraft:podzol",
            "minecraft:coarse_dirt", "minecraft:mycelium", "minecraft:rooted_dirt",
            "minecraft:moss_block", "minecraft:pale_moss_block", "minecraft:farmland",
            "minecraft:mud", "minecraft:muddy_mangrove_roots"
    );

    private static TreeEntry makeTree(String sapling, List<String> validSoils, DropEntry... drops) {
        TreeEntry tree = new TreeEntry();
        tree.sapling = sapling;
        tree.validSoils = new ArrayList<>(validSoils);
        tree.drops = new ArrayList<>(List.of(drops));
        return tree;
    }

    private static void addMysticalAgricultureCrops(List<CropEntry> crops) {
        List<String> tier1Soils = List.of(
                "minecraft:farmland",
                "mysticalagriculture:inferium_farmland", "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland", "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland", "mysticalagradditions:insanium_farmland",

                "justdirethings:goosoil_tier1", "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3", "justdirethings:goosoil_tier4",
                "farmersdelight:rich_soil_farmland");

        for (String seed : new String[]{
                "mysticalagriculture:air_seeds", "mysticalagriculture:earth_seeds",
                "mysticalagriculture:water_seeds", "mysticalagriculture:fire_seeds",
                "mysticalagriculture:inferium_seeds", "mysticalagriculture:stone_seeds",
                "mysticalagriculture:dirt_seeds", "mysticalagriculture:wood_seeds",
                "mysticalagriculture:ice_seeds", "mysticalagriculture:deepslate_seeds"
        }) addMysticalAgricultureCrop(crops, seed, tier1Soils);

        List<String> tier2Soils = List.of(
                "mysticalagriculture:prudentium_farmland", "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland", "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "justdirethings:goosoil_tier1", "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3", "justdirethings:goosoil_tier4");

        for (String seed : new String[]{
                "mysticalagriculture:nature_seeds", "mysticalagriculture:dye_seeds",
                "mysticalagriculture:nether_seeds", "mysticalagriculture:coal_seeds",
                "mysticalagriculture:coral_seeds", "mysticalagriculture:honey_seeds",
                "mysticalagriculture:amethyst_seeds", "mysticalagriculture:pig_seeds",
                "mysticalagriculture:chicken_seeds", "mysticalagriculture:cow_seeds",
                "mysticalagriculture:sheep_seeds", "mysticalagriculture:squid_seeds",
                "mysticalagriculture:fish_seeds", "mysticalagriculture:slime_seeds",
                "mysticalagriculture:turtle_seeds", "mysticalagriculture:armadillo_seeds",
                "mysticalagriculture:rubber_seeds", "mysticalagriculture:silicon_seeds",
                "mysticalagriculture:sulfur_seeds", "mysticalagriculture:aluminum_seeds",
                "mysticalagriculture:saltpeter_seeds", "mysticalagriculture:apatite_seeds",
                "mysticalagriculture:grains_of_infinity_seeds", "mysticalagriculture:mystical_flower_seeds",
                "mysticalagriculture:marble_seeds", "mysticalagriculture:limestone_seeds",
                "mysticalagriculture:basalt_seeds", "mysticalagriculture:menril_seeds"
        }) addMysticalAgricultureCrop(crops, seed, tier2Soils);

        List<String> tier3Soils = List.of(
                "mysticalagriculture:tertium_farmland", "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland", "mysticalagradditions:insanium_farmland",

                "justdirethings:goosoil_tier2", "justdirethings:goosoil_tier3", "justdirethings:goosoil_tier4");

        for (String seed : new String[]{
                "mysticalagriculture:iron_seeds", "mysticalagriculture:copper_seeds",
                "mysticalagriculture:nether_quartz_seeds", "mysticalagriculture:glowstone_seeds",
                "mysticalagriculture:redstone_seeds", "mysticalagriculture:obsidian_seeds",
                "mysticalagriculture:prismarine_seeds", "mysticalagriculture:zombie_seeds",
                "mysticalagriculture:skeleton_seeds", "mysticalagriculture:creeper_seeds",
                "mysticalagriculture:spider_seeds", "mysticalagriculture:rabbit_seeds",
                "mysticalagriculture:tin_seeds", "mysticalagriculture:bronze_seeds",
                "mysticalagriculture:zinc_seeds", "mysticalagriculture:brass_seeds",
                "mysticalagriculture:silver_seeds", "mysticalagriculture:lead_seeds",
                "mysticalagriculture:graphite_seeds", "mysticalagriculture:blizz_seeds",
                "mysticalagriculture:blitz_seeds", "mysticalagriculture:basalz_seeds",
                "mysticalagriculture:amethyst_bronze_seeds", "mysticalagriculture:slimesteel_seeds",
                "mysticalagriculture:pig_iron_seeds", "mysticalagriculture:copper_alloy_seeds",
                "mysticalagriculture:redstone_alloy_seeds", "mysticalagriculture:conductive_alloy_seeds",
                "mysticalagriculture:steeleaf_seeds", "mysticalagriculture:ironwood_seeds",
                "mysticalagriculture:sky_stone_seeds", "mysticalagriculture:certus_quartz_seeds",
                "mysticalagriculture:quartz_enriched_iron_seeds", "mysticalagriculture:manasteel_seeds",
                "mysticalagriculture:aquamarine_seeds", "mysticalagriculture:phantom_seeds", "mysticalagriculture:sculk_seeds"
        }) addMysticalAgricultureCrop(crops, seed, tier3Soils);

        List<String> tier4Soils = List.of(
                "mysticalagriculture:imperium_farmland", "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "justdirethings:goosoil_tier2", "justdirethings:goosoil_tier3", "justdirethings:goosoil_tier4");

        for (String seed : new String[]{
                "mysticalagriculture:gold_seeds", "mysticalagriculture:lapis_lazuli_seeds",
                "mysticalagriculture:end_seeds", "mysticalagriculture:experience_seeds",
                "mysticalagriculture:breeze_seeds", "mysticalagriculture:blaze_seeds",
                "mysticalagriculture:ghast_seeds", "mysticalagriculture:enderman_seeds",
                "mysticalagriculture:steel_seeds", "mysticalagriculture:nickel_seeds",
                "mysticalagriculture:constantan_seeds", "mysticalagriculture:electrum_seeds",
                "mysticalagriculture:invar_seeds", "mysticalagriculture:uranium_seeds",
                "mysticalagriculture:ruby_seeds", "mysticalagriculture:sapphire_seeds",
                "mysticalagriculture:peridot_seeds", "mysticalagriculture:soulium_seeds",
                "mysticalagriculture:signalum_seeds", "mysticalagriculture:lumium_seeds",
                "mysticalagriculture:flux_infused_ingot_seeds", "mysticalagriculture:hop_graphite_seeds",
                "mysticalagriculture:cobalt_seeds", "mysticalagriculture:rose_gold_seeds",
                "mysticalagriculture:soularium_seeds", "mysticalagriculture:dark_steel_seeds",
                "mysticalagriculture:pulsating_alloy_seeds", "mysticalagriculture:energetic_alloy_seeds",
                "mysticalagriculture:elementium_seeds", "mysticalagriculture:osmium_seeds",
                "mysticalagriculture:fluorite_seeds", "mysticalagriculture:refined_glowstone_seeds",
                "mysticalagriculture:refined_obsidian_seeds", "mysticalagriculture:knightmetal_seeds",
                "mysticalagriculture:fiery_ingot_seeds", "mysticalagriculture:compressed_iron_seeds",
                "mysticalagriculture:starmetal_seeds", "mysticalagriculture:fluix_seeds",
                "mysticalagriculture:energized_steel_seeds", "mysticalagriculture:blazing_crystal_seeds"
        }) addMysticalAgricultureCrop(crops, seed, tier4Soils);

        List<String> tier5Soils = List.of(
                "mysticalagriculture:supremium_farmland", "mysticalagradditions:insanium_farmland",
                "justdirethings:goosoil_tier4");

        for (String seed : new String[]{
                "mysticalagriculture:diamond_seeds", "mysticalagriculture:emerald_seeds",
                "mysticalagriculture:netherite_seeds", "mysticalagriculture:wither_skeleton_seeds",
                "mysticalagriculture:platinum_seeds", "mysticalagriculture:iridium_seeds",
                "mysticalagriculture:enderium_seeds", "mysticalagriculture:flux_infused_gem_seeds",
                "mysticalagriculture:manyullyn_seeds", "mysticalagriculture:queens_slime_seeds",
                "mysticalagriculture:hepatizon_seeds", "mysticalagriculture:vibrant_alloy_seeds",
                "mysticalagriculture:end_steel_seeds", "mysticalagriculture:terrasteel_seeds",
                "mysticalagriculture:rock_crystal_seeds", "mysticalagriculture:draconium_seeds",
                "mysticalagriculture:yellorium_seeds", "mysticalagriculture:cyanite_seeds",
                "mysticalagriculture:niotic_crystal_seeds", "mysticalagriculture:spirited_crystal_seeds",
                "mysticalagriculture:uraninite_seeds"
        }) addMysticalAgricultureCrop(crops, seed, tier5Soils);

        if (Config.enableMysticalAgradditions) {
            addSpecialCruxCrop(crops, "mysticalagriculture:nether_star_seeds",       "mysticalagradditions:nether_star_crux");
            addSpecialCruxCrop(crops, "mysticalagriculture:dragon_egg_seeds",        "mysticalagradditions:dragon_egg_crux");
            addSpecialCruxCrop(crops, "mysticalagriculture:gaia_spirit_seeds",       "mysticalagradditions:gaia_spirit_crux");
            addSpecialCruxCrop(crops, "mysticalagriculture:awakened_draconium_seeds","mysticalagradditions:awakened_draconium_crux");
            addSpecialCruxCrop(crops, "mysticalagriculture:neutronium_seeds",        "mysticalagradditions:neutronium_crux");
            addSpecialCruxCrop(crops, "mysticalagriculture:nitro_crystal_seeds",     "mysticalagradditions:nitro_crystal_crux");
        }
    }

    private static void addSpecialCruxCrop(List<CropEntry> crops, String seedId, String cruxId) {
        String essence = seedId.replace("_seeds", "_essence");
        crops.add(makeCrop(seedId, List.of(cruxId), makeDrop(essence, 1, 1, 1.0F)));
    }

    private static void addMysticalAgricultureCrop(List<CropEntry> crops, String seedId, List<String> validSoils) {
        String essence = seedId.replace("_seeds", "_essence");
        crops.add(makeCrop(seedId, validSoils,
                makeDrop(essence, 1, 1, 1.0F),
                makeDrop(seedId, 1, 1, 0.2F)));
    }

    private static void addFarmersDelightCrops(List<CropEntry> crops) {
        crops.add(makeCrop("farmersdelight:cabbage_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("farmersdelight:cabbage", 1, 1, 1.0F),
                makeDrop("farmersdelight:cabbage_seeds", 1, 2, 1.0F)));

        crops.add(makeCrop("farmersdelight:tomato_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("farmersdelight:tomato", 1, 2, 1.0F)));

        crops.add(makeCrop("farmersdelight:onion", STANDARD_FARMLAND_SOILS,
                makeDrop("farmersdelight:onion", 1, 3, 1.0F)));

        crops.add(makeCrop("farmersdelight:rice", STANDARD_FARMLAND_SOILS,
                makeDrop("farmersdelight:rice_panicle", 1, 1, 1.0F)));
    }

    private static void addOccultimCrops(List<CropEntry> crops) {
        crops.add(makeCrop("occultism:datura_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("occultism:datura", 1, 1, 1.0F),
                makeDrop("occultism:datura_seeds", 1, 1, 0.5F)));
    }

    private static void addImmersiveEngineering(List<CropEntry> crops) {
        crops.add(makeCrop("immersiveengineering:seed", STANDARD_FARMLAND_SOILS,
                makeDrop("immersiveengineering:hemp_fiber", 4, 8, 1.0F),
                makeDrop("immersiveengineering:seed", 1, 1, 0.5F)));
    }

    private static void addArsNouveauCrops(List<CropEntry> crops) {
        crops.add(makeCrop("ars_nouveau:magebloom_crop", STANDARD_FARMLAND_SOILS,
                makeDrop("ars_nouveau:magebloom", 1, 1, 1.0F)));

        crops.add(makeCrop("ars_nouveau:sourceberry_bush",
                List.of("minecraft:farmland", "minecraft:dirt", "minecraft:grass_block",
                        "minecraft:rooted_dirt", "minecraft:coarse_dirt", "minecraft:podzol",
                        "minecraft:mycelium", "minecraft:mud", "minecraft:moss_block",
                        "minecraft:muddy_mangrove_roots",
                        "mysticalagriculture:inferium_farmland", "mysticalagriculture:prudentium_farmland",
                        "mysticalagriculture:tertium_farmland", "mysticalagriculture:imperium_farmland",
                        "mysticalagriculture:supremium_farmland", "mysticalagradditions:insanium_farmland",
                        "justdirethings:goosoil_tier1", "justdirethings:goosoil_tier2",
                        "justdirethings:goosoil_tier3", "justdirethings:goosoil_tier4",
                        "farmersdelight:rich_soil_farmland", "farmersdelight:organic_compost",
                        "farmersdelight:rich_soil", "agritechevolved:mulch"),
                makeDrop("ars_nouveau:sourceberry_bush", 2, 4, 1.0F)));
    }

    private static void addSilentGearCrops(List<CropEntry> crops) {
        crops.add(makeCrop("silentgear:fluffy_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("silentgear:fluffy_puff", 1, 4, 1.0F),
                makeDrop("silentgear:fluffy_seeds", 1, 1, 1.0F)));

        crops.add(makeCrop("silentgear:flax_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("silentgear:flax_fiber", 1, 4, 1.0F),
                makeDrop("silentgear:flax_seeds", 1, 1, 0.2F),
                makeDrop("silentgear:flax_flowers", 1, 1, 0.2F)));
    }

    private static void addPamsCrops(List<CropEntry> crops) {
        crops.add(makeCrop("pamhc2crops:agaveseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:agaveitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:alfalfaseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:alfalfaitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:aloeseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:aloeitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:amaranthseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:amaranthitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:arrowrootseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:arrowrootitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:artichokeseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:artichokeitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:asparagusseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:asparagusitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:barleyseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:barleyitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:barrelcactusseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:barrelcactusitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:beanseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:beanitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:bellpepperseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:bellpepperitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:blackberryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:blackberryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:blueberryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:blueberryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:bokchoyseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:bokchoyitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:broccoliseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:broccoliitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:brusselsproutseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:brusselsproutitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:cabbageseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:cabbageitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:cactusfruitseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:cactusfruititem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:calabashseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:calabashitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:candleberryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:candleberryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:canolaseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:canolaitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:cantaloupeseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:cantaloupeitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:cassavaseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:cassavaitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:cattailseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:cattailitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:cauliflowerseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:caulifloweritem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:celeryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:celeryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:chiaseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:chiaitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:chickpeaseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:chickpeaitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:chilipepperseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:chilipepperitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:cloudberryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:cloudberryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:coffeebeanseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:coffeebeanitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:cornseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:cornitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:cottonseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:cottonitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:cranberryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:cranberryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:cucumberseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:cucumberitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:eggplantseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:eggplantitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:elderberryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:elderberryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:flaxseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:flaxitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:garlicseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:garlicitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:gingerseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:gingeritem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:grapeseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:grapeitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:greengrapeseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:greengrapeitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:guaranaseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:guaranaitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:huckleberryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:huckleberryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:jicamaseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:jicamaitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:juniperberryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:juniperberryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:juteseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:juteitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:kaleseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:kaleitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:kenafseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:kenafitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:kiwiseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:kiwiitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:kohlrabiseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:kohlrabiitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:leekseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:leekitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:lentilseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:lentilitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:lettuceseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:lettuceitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:lotusseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:lotusitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:milletseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:milletitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:mulberryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:mulberryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:mustardseedsseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:mustardseedsitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:nettlesseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:nettlesitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:nopalesseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:nopalesitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:oatsseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:oatsitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:okraseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:okraitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:onionseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:onionitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:papyrusseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:papyrusitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:parsnipseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:parsnipitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:peanutseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:peanutitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:peasseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:peasitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:pineappleseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:pineappleitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:quinoaseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:quinoaitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:radishseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:radishitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:raspberryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:raspberryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:rhubarbseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:rhubarbitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:riceseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:riceitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:rutabagaseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:rutabagaitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:ryeseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:ryeitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:scallionseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:scallionitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:sesameseedsseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:sesameseedsitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:sisalseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:sisalitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:sorghumseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:sorghumitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:soybeanseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:soybeanitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:spiceleafseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:spiceleafitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:spinachseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:spinachitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:strawberryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:strawberryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:sweetpotatoseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:sweetpotatoitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:taroseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:taroitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:tealeafseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:tealeafitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:tomatilloseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:tomatilloitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:tomatoseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:tomatoitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:truffleseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:truffleitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:turnipseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:turnipitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:waterchestnutseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:waterchestnutitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:whitemushroomseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:whitemushroomitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:wintersquashseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:wintersquashitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:wolfberryseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:wolfberryitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:yuccaseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:yuccaitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:zucchiniseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:zucchiniitem", 2, 4, 1.0F)));
        crops.add(makeCrop("pamhc2crops:sunchokeseeditem", STANDARD_FARMLAND_SOILS,
                makeDrop("pamhc2crops:sunchokeitem", 2, 4, 1.0F)));
    }

    private static void addCroptopiaCrops(List<CropEntry> crops) {
        crops.add(makeCrop("croptopia:artichoke_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:artichoke", 2, 4, 1.0F),
                makeDrop("croptopia:artichoke_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:asparagus_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:asparagus", 2, 4, 1.0F),
                makeDrop("croptopia:asparagus_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:barley_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:barley", 2, 4, 1.0F),
                makeDrop("croptopia:barley_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:basil_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:basil", 2, 4, 1.0F),
                makeDrop("croptopia:basil_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:bellpepper_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:bellpepper", 2, 4, 1.0F),
                makeDrop("croptopia:bellpepper_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:blackbean_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:blackbean", 2, 4, 1.0F),
                makeDrop("croptopia:blackbean_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:blackberry_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:blackberry", 2, 4, 1.0F),
                makeDrop("croptopia:blackberry_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:blueberry_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:blueberry", 2, 4, 1.0F),
                makeDrop("croptopia:blueberry_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:broccoli_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:broccoli", 2, 4, 1.0F),
                makeDrop("croptopia:broccoli_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:cabbage_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:cabbage", 2, 4, 1.0F),
                makeDrop("croptopia:cabbage_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:cantaloupe_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:cantaloupe", 2, 4, 1.0F),
                makeDrop("croptopia:cantaloupe_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:cauliflower_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:cauliflower", 2, 4, 1.0F),
                makeDrop("croptopia:cauliflower_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:celery_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:celery", 2, 4, 1.0F),
                makeDrop("croptopia:celery_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:chile_pepper_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:chile_pepper", 2, 4, 1.0F),
                makeDrop("croptopia:chile_pepper_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:coffee_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:coffee", 2, 4, 1.0F),
                makeDrop("croptopia:coffee_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:corn_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:corn", 2, 4, 1.0F),
                makeDrop("croptopia:corn_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:cranberry_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:cranberry", 2, 4, 1.0F),
                makeDrop("croptopia:cranberry_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:cucumber_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:cucumber", 2, 4, 1.0F),
                makeDrop("croptopia:cucumber_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:currant_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:currant", 2, 4, 1.0F),
                makeDrop("croptopia:currant_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:eggplant_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:eggplant", 2, 4, 1.0F),
                makeDrop("croptopia:eggplant_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:elderberry_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:elderberry", 2, 4, 1.0F),
                makeDrop("croptopia:elderberry_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:garlic_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:garlic", 2, 4, 1.0F),
                makeDrop("croptopia:garlic_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:ginger_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:ginger", 2, 4, 1.0F),
                makeDrop("croptopia:ginger_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:grape_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:grape", 2, 4, 1.0F),
                makeDrop("croptopia:grape_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:greenbean_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:greenbean", 2, 4, 1.0F),
                makeDrop("croptopia:greenbean_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:greenonion_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:greenonion", 2, 4, 1.0F),
                makeDrop("croptopia:greenonion_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:honeydew_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:honeydew", 2, 4, 1.0F),
                makeDrop("croptopia:honeydew_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:hops_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:hops", 2, 4, 1.0F),
                makeDrop("croptopia:hops_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:kale_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:kale", 2, 4, 1.0F),
                makeDrop("croptopia:kale_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:kiwi_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:kiwi", 2, 4, 1.0F),
                makeDrop("croptopia:kiwi_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:leek_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:leek", 2, 4, 1.0F),
                makeDrop("croptopia:leek_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:lettuce_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:lettuce", 2, 4, 1.0F),
                makeDrop("croptopia:lettuce_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:mustard_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:mustard", 2, 4, 1.0F),
                makeDrop("croptopia:mustard_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:oat_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:oat", 2, 4, 1.0F),
                makeDrop("croptopia:oat_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:olive_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:olive", 2, 4, 1.0F),
                makeDrop("croptopia:olive_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:onion_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:onion", 2, 4, 1.0F),
                makeDrop("croptopia:onion_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:peanut_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:peanut", 2, 4, 1.0F),
                makeDrop("croptopia:peanut_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:pepper_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:pepper", 2, 4, 1.0F),
                makeDrop("croptopia:pepper_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:pineapple_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:pineapple", 2, 4, 1.0F),
                makeDrop("croptopia:pineapple_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:radish_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:radish", 2, 4, 1.0F),
                makeDrop("croptopia:radish_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:raspberry_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:raspberry", 2, 4, 1.0F),
                makeDrop("croptopia:raspberry_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:rhubarb_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:rhubarb", 2, 4, 1.0F),
                makeDrop("croptopia:rhubarb_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:rice_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:rice", 2, 4, 1.0F),
                makeDrop("croptopia:rice_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:rutabaga_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:rutabaga", 2, 4, 1.0F),
                makeDrop("croptopia:rutabaga_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:saguaro_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:saguaro", 2, 4, 1.0F),
                makeDrop("croptopia:saguaro_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:soybean_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:soybean", 2, 4, 1.0F),
                makeDrop("croptopia:soybean_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:spinach_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:spinach", 2, 4, 1.0F),
                makeDrop("croptopia:spinach_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:squash_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:squash", 2, 4, 1.0F),
                makeDrop("croptopia:squash_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:strawberry_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:strawberry", 2, 4, 1.0F),
                makeDrop("croptopia:strawberry_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:sweetpotato_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:sweetpotato", 2, 4, 1.0F),
                makeDrop("croptopia:sweetpotato_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:tea_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:tea", 2, 4, 1.0F),
                makeDrop("croptopia:tea_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:tomatillo_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:tomatillo", 2, 4, 1.0F),
                makeDrop("croptopia:tomatillo_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:tomato_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:tomato", 2, 4, 1.0F),
                makeDrop("croptopia:tomato_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:turmeric_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:turmeric", 2, 4, 1.0F),
                makeDrop("croptopia:turmeric_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:turnip_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:turnip", 2, 4, 1.0F),
                makeDrop("croptopia:turnip_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:vanilla_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:vanilla", 2, 4, 1.0F),
                makeDrop("croptopia:vanilla_seeds", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:yam_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:yam", 2, 4, 1.0F),
                makeDrop("croptopia:yam_seed", 1, 1, 0.2F)));
        crops.add(makeCrop("croptopia:zucchini_seed", STANDARD_FARMLAND_SOILS,
                makeDrop("croptopia:zucchini", 2, 4, 1.0F),
                makeDrop("croptopia:zucchini_seed", 1, 1, 0.2F)));
    }

    private static void addActuallyAdditionsCrops(List<CropEntry> crops) {
        crops.add(makeCrop("actuallyadditions:rice_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("actuallyadditions:rice", 1, 4, 1.0F),
                makeDrop("actuallyadditions:rice_seeds", 1, 1, 0.2F)));
        crops.add(makeCrop("actuallyadditions:canola_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("actuallyadditions:canola", 1, 4, 1.0F),
                makeDrop("actuallyadditions:canola_seeds", 1, 1, 0.2F)));
        crops.add(makeCrop("actuallyadditions:flax_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("minecraft:string", 2, 4, 1.0F),
                makeDrop("actuallyadditions:flax_seeds", 1, 1, 0.2F)));
        crops.add(makeCrop("actuallyadditions:coffee_beans", STANDARD_FARMLAND_SOILS,
                makeDrop("actuallyadditions:coffee_beans", 1, 4, 1.0F)));
    }

    private static void addCobblemonCrops(List<CropEntry> crops) {
        crops.add(makeCrop("cobblemon:revival_herb", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:revival_herb", 1, 2, 1.0F),
                makeDrop("cobblemon:pep_up_flower", 1, 1, 1.0F)));
        crops.add(makeCrop("cobblemon:vivichoke_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:vivichoke", 1, 1, 1.0F),
                makeDrop("cobblemon:vivichoke_seeds", 1, 1, 1.0F)));
        crops.add(makeCrop("cobblemon:big_root", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:big_root", 1, 1, 1.0F),
                makeDrop("cobblemon:energy_root", 1, 1, 0.5F)));
        crops.add(makeCrop("cobblemon:galarica_nuts", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:galarica_nuts", 1, 1, 1.0F)));
        crops.add(makeCrop("cobblemon:hearty_grains", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:hearty_grains", 1, 2, 1.0F)));

        crops.add(makeCrop("cobblemon:medicinal_leek",
                List.of("minecraft:water_bucket"),
                makeDrop("cobblemon:medicinal_leek", 1, 1, 1.0F)));

        crops.add(makeCrop("cobblemon:aguav_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:aguav_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:apicot_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:apicot_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:aspear_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:aspear_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:babiri_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:babiri_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:belue_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:belue_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:bluk_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:bluk_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:charti_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:charti_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:cheri_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:cheri_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:chesto_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:chesto_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:chilan_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:chilan_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:chople_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:chople_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:coba_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:coba_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:colbur_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:colbur_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:cornn_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:cornn_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:custap_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:custap_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:durin_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:durin_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:eggant_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:eggant_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:enigma_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:enigma_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:figy_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:figy_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:ganlon_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:ganlon_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:grepa_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:grepa_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:haban_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:haban_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:hondew_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:hondew_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:hopo_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:hopo_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:iapapa_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:iapapa_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:jaboca_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:jaboca_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:kasib_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:kasib_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:kebia_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:kebia_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:kee_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:kee_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:kelpsy_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:kelpsy_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:lansat_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:lansat_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:leppa_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:leppa_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:liechi_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:liechi_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:lum_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:lum_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:mago_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:mago_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:magost_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:magost_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:maranga_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:maranga_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:micle_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:micle_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:nanab_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:nanab_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:nomel_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:nomel_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:occa_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:occa_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:oran_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:oran_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:pamtre_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:pamtre_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:passho_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:passho_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:payapa_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:payapa_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:pecha_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:pecha_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:persim_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:persim_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:petaya_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:petaya_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:pinap_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:pinap_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:pomeg_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:pomeg_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:qualot_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:qualot_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:rabuta_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:rabuta_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:rawst_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:rawst_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:razz_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:razz_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:rindo_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:rindo_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:roseli_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:roseli_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:rowap_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:rowap_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:salac_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:salac_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:shuca_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:shuca_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:sitrus_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:sitrus_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:spelon_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:spelon_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:starf_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:starf_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:tamato_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:tamato_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:tanga_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:tanga_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:touga_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:touga_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:wacan_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:wacan_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:watmel_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:watmel_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:wepear_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:wepear_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:wiki_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:wiki_berry", 1, 3, 1.0F)));
        crops.add(makeCrop("cobblemon:yache_berry", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:yache_berry", 1, 3, 1.0F)));

        crops.add(makeCrop("cobblemon:red_mint_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:red_mint_leaf", 1, 4, 1.0F)));
        crops.add(makeCrop("cobblemon:blue_mint_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:blue_mint_leaf", 1, 4, 1.0F)));
        crops.add(makeCrop("cobblemon:cyan_mint_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:cyan_mint_leaf", 1, 4, 1.0F)));
        crops.add(makeCrop("cobblemon:pink_mint_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:pink_mint_leaf", 1, 4, 1.0F)));
        crops.add(makeCrop("cobblemon:green_mint_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:green_mint_leaf", 1, 4, 1.0F)));
        crops.add(makeCrop("cobblemon:white_mint_seeds", STANDARD_FARMLAND_SOILS,
                makeDrop("cobblemon:white_mint_leaf", 1, 4, 1.0F)));
    }

    private static void addVanillaTrees(List<TreeEntry> trees) {
        trees.add(makeTree("minecraft:oak_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 2, 6),
                makeDrop("minecraft:oak_sapling", 1, 2, 0.5F),
                makeDrop("minecraft:stick", 1, 2, 0.5F),
                makeDrop("minecraft:apple", 1, 1, 0.4F)));

        trees.add(makeTree("minecraft:birch_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:birch_log", 2, 6),
                makeDrop("minecraft:birch_sapling", 1, 2, 0.5F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));

        trees.add(makeTree("minecraft:spruce_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:spruce_log", 4, 8),
                makeDrop("minecraft:spruce_sapling", 1, 2, 0.5F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));

        trees.add(makeTree("minecraft:jungle_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 2, 6),
                makeDrop("minecraft:jungle_sapling", 1, 2, 0.4F),
                makeDrop("minecraft:stick", 1, 2, 0.5F),
                makeDrop("minecraft:cocoa_beans", 1, 2, 0.2F)));

        trees.add(makeTree("minecraft:acacia_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:acacia_log", 2, 6),
                makeDrop("minecraft:acacia_sapling", 1, 2, 0.5F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));

        trees.add(makeTree("minecraft:dark_oak_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:dark_oak_log", 4, 8),
                makeDrop("minecraft:dark_oak_sapling", 1, 2, 0.5F),
                makeDrop("minecraft:stick", 1, 2, 0.5F),
                makeDrop("minecraft:apple", 1, 2, 0.3F)));

        trees.add(makeTree("minecraft:mangrove_propagule",
                List.of("minecraft:mud", "minecraft:muddy_mangrove_roots", "minecraft:dirt",
                        "minecraft:coarse_dirt", "minecraft:grass_block", "minecraft:podzol",
                        "minecraft:mycelium"),
                makeDrop("minecraft:mangrove_log", 2, 6),
                makeDrop("minecraft:mangrove_propagule", 1, 2, 0.5F),
                makeDrop("minecraft:stick", 1, 2, 0.5F),
                makeDrop("minecraft:mangrove_roots", 1, 1, 0.3F)));

        trees.add(makeTree("minecraft:cherry_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:cherry_log", 2, 6),
                makeDrop("minecraft:cherry_sapling", 1, 2, 0.5F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));

        trees.add(makeTree("minecraft:pale_oak_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:pale_oak_log", 4, 8),
                makeDrop("minecraft:pale_oak_sapling", 1, 2, 0.5F),
                makeDrop("minecraft:stick", 1, 2, 0.5F),
                makeDrop("minecraft:pale_hanging_moss", 1, 2, 0.3F)));

        List<String> azaleaSoils = List.of("minecraft:dirt", "minecraft:grass_block", "minecraft:podzol",
                "minecraft:coarse_dirt", "minecraft:rooted_dirt", "minecraft:moss_block",
                "minecraft:mycelium", "agritechevolved:mulch");

        trees.add(makeTree("minecraft:azalea", azaleaSoils,
                makeDrop("minecraft:oak_log", 2, 6),
                makeDrop("minecraft:azalea", 1, 1, 0.5F),
                makeDrop("minecraft:stick", 1, 2, 0.5F),
                makeDrop("minecraft:moss_block", 1, 2, 0.2F)));

        trees.add(makeTree("minecraft:flowering_azalea", azaleaSoils,
                makeDrop("minecraft:oak_log", 2, 6),
                makeDrop("minecraft:flowering_azalea", 1, 1, 0.5F),
                makeDrop("minecraft:stick", 1, 2, 0.5F),
                makeDrop("minecraft:moss_block", 1, 1, 0.2F)));

        List<String> fungalSoils = List.of("minecraft:crimson_nylium", "minecraft:warped_nylium");

        trees.add(makeTree("minecraft:crimson_fungus", fungalSoils,
                makeDrop("minecraft:crimson_stem", 2, 6),
                makeDrop("minecraft:nether_wart_block", 4, 8),
                makeDrop("minecraft:weeping_vines", 1, 2),
                makeDrop("minecraft:shroomlight", 2, 4)));

        trees.add(makeTree("minecraft:warped_fungus", fungalSoils,
                makeDrop("minecraft:warped_stem", 2, 6),
                makeDrop("minecraft:warped_wart_block", 4, 8),
                makeDrop("minecraft:twisting_vines", 1, 2),
                makeDrop("minecraft:shroomlight", 2, 4)));
    }

    private static void addArsElementalTrees(List<TreeEntry> trees) {
        trees.add(makeTree("ars_elemental:yellow_archwood_sapling", STANDARD_TREE_SOILS,
                makeDrop("ars_elemental:yellow_archwood_log", 4, 8),
                makeDrop("ars_elemental:yellow_archwood_sapling", 1, 1, 0.3F),
                makeDrop("ars_elemental:flashpine_pod", 1, 1, 0.2F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
    }

    private static void addArsNouveauTrees(List<TreeEntry> trees) {
        record ArchwoodTree(String color, String pod) {}
        for (ArchwoodTree t : new ArchwoodTree[]{
                new ArchwoodTree("blue",   "frostaya"),
                new ArchwoodTree("red",    "bombegranate"),
                new ArchwoodTree("purple", "bastion"),
                new ArchwoodTree("green",  "mendosteen")
        }) {
            trees.add(makeTree("ars_nouveau:" + t.color() + "_archwood_sapling", STANDARD_TREE_SOILS,
                    makeDrop("ars_nouveau:" + t.color() + "_archwood_log", 4, 8),
                    makeDrop("ars_nouveau:" + t.color() + "_archwood_sapling", 1, 1, 0.3F),
                    makeDrop("ars_nouveau:" + t.pod() + "_pod", 1, 1, 0.2F),
                    makeDrop("minecraft:stick", 1, 2, 0.5F)));
        }
    }

    private static void addEvilCraftTrees(List<TreeEntry> trees) {
        trees.add(makeTree("evilcraft:undead_sapling", STANDARD_TREE_SOILS,
                makeDrop("evilcraft:undead_log", 2, 6),
                makeDrop("minecraft:dead_bush", 1, 2)));
    }

    private static void addForbiddenArcanusTrees(List<TreeEntry> trees) {
        trees.add(makeTree("forbidden_arcanus:aurum_sapling", STANDARD_TREE_SOILS,
                makeDrop("forbidden_arcanus:aurum_log", 2, 6),
                makeDrop("forbidden_arcanus:aurum_sapling", 1, 3),
                makeDrop("minecraft:stick", 1, 2, 0.5F),
                makeDrop("minecraft:gold_nugget", 1, 2, 0.05F)));

        trees.add(makeTree("forbidden_arcanus:growing_edelwood", STANDARD_TREE_SOILS,
                makeDrop("forbidden_arcanus:edelwood_log", 2, 6),
                makeDrop("forbidden_arcanus:carved_edelwood_log", 1, 1, 0.4F)));
    }

    private static void addIntegratedDynamicsTrees(List<TreeEntry> trees) {
        trees.add(makeTree("integrateddynamics:menril_sapling", STANDARD_TREE_SOILS,
                makeDrop("integrateddynamics:menril_log", 2, 6),
                makeDrop("integrateddynamics:menril_sapling", 1, 2),
                makeDrop("integrateddynamics:crystalized_menril_chunk", 1, 2, 0.5F),
                makeDrop("integrateddynamics:menril_berries", 2, 4, 0.5F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
    }

    private static void addOccultismTrees(List<TreeEntry> trees) {
        trees.add(makeTree("occultism:otherworld_sapling", STANDARD_TREE_SOILS,
                makeDrop("occultism:otherworld_log", 2, 6),
                makeDrop("occultism:otherworld_sapling", 1, 3),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
    }

    private static void addSilentGearTrees(List<TreeEntry> trees) {
        trees.add(makeTree("silentgear:netherwood_sapling", STANDARD_TREE_SOILS,
                makeDrop("silentgear:netherwood_log", 2, 6),
                makeDrop("silentgear:netherwood_sapling", 1, 1, 0.3F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
    }

    private static void addPamsTrees(List<TreeEntry> trees) {
        trees.add(makeTree("pamhc2trees:acorn_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:acorn_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:acornitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:almond_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:almond_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:almonditem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:apple_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:apple_sapling", 1, 1, 0.3F),
                makeDrop("minecraft:apple", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:apricot_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:apricot_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:apricotitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:avocado_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:avocado_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:avocadoitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:banana_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:banana_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:bananaitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:breadfruit_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:breadfruit_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:breadfruititem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:candlenut_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:candlenut_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:candlenutitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:cherry_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:cherry_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:cherryitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:chestnut_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:chestnut_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:chestnutitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:cinnamon_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:cinnamon_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:cinnamonitem", 1, 1, 0.5F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:coconut_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:coconut_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:coconutitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:date_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:date_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:dateitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:dragonfruit_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:dragonfruit_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:dragonfruititem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:durian_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:durian_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:durianitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:fig_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:fig_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:figitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:gooseberry_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:gooseberry_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:gooseberryitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:grapefruit_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:grapefruit_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:grapefruititem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:guava_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:guava_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:guavaitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:hazelnut_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:hazelnut_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:hazelnutitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:jackfruit_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:jackfruit_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:jackfruititem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:lemon_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:lemon_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:lemonitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:lime_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:lime_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:limeitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:lychee_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:lychee_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:lycheeitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:mango_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:mango_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:mangoitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:maple_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:spruce_log", 2, 4),
                makeDrop("pamhc2trees:maple_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:pammaple", 1, 2, 0.5F),
                makeDrop("pamhc2trees:maplesyrupitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:nutmeg_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:nutmeg_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:nutmegitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:olive_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:olive_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:oliveitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:orange_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:orange_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:orangeitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:papaya_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:papaya_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:papayaitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:paperbark_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 2, 6),
                makeDrop("minecraft:paper", 1, 2, 1.0F),
                makeDrop("pamhc2trees:paperbark_sapling", 1, 1, 0.3F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:passionfruit_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:passionfruit_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:passionfruititem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:pawpaw_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:pawpaw_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:pawpawitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:peach_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:peach_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:peachitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:pear_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:pear_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:pearitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:pecan_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:pecan_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:pecanitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:peppercorn_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:peppercorn_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:peppercornitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:persimmon_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:persimmon_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:persimmonitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:pinenut_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:spruce_log", 4, 8),
                makeDrop("pamhc2trees:pinenut_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:pinenutitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:pistachio_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:pistachio_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:pistachioitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:plum_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:plum_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:plumitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:pomegranate_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:pomegranate_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:pomegranateitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:rambutan_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:rambutan_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:rambutanitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:soursop_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:soursop_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:soursopitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:spiderweb_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:spiderweb_sapling", 1, 1, 0.3F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:starfruit_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:starfruit_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:starfruititem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:tamarind_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:tamarind_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:tamarinditem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:walnut_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 8),
                makeDrop("pamhc2trees:walnut_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:walnutitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:cashew_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:cashew_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:cashewitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("pamhc2trees:vanillabean_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 8),
                makeDrop("pamhc2trees:vanillabean_sapling", 1, 1, 0.3F),
                makeDrop("pamhc2trees:vanillabeanitem", 1, 1, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
    }

    private static void addCroptopiaTrees(List<TreeEntry> trees) {
        trees.add(makeTree("croptopia:almond_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:dark_oak_log", 4, 6),
                makeDrop("croptopia:almond_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:almond", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:apple_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 6),
                makeDrop("croptopia:apple_sapling", 1, 1, 0.3F),
                makeDrop("minecraft:apple", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:apricot_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 6),
                makeDrop("croptopia:apricot_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:apricot", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:avocado_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:spruce_log", 4, 6),
                makeDrop("croptopia:avocado_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:avocado", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:banana_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 6),
                makeDrop("croptopia:banana_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:banana", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:cashew_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:dark_oak_log", 4, 6),
                makeDrop("croptopia:cashew_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:cashew", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:cherry_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 6),
                makeDrop("croptopia:cherry_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:cherry", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:cinnamon_sapling", STANDARD_TREE_SOILS,
                makeDrop("croptopia:cinnamon_log", 4, 6),
                makeDrop("croptopia:cinnamon_sapling", 1, 1, 0.3F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:coconut_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 6),
                makeDrop("croptopia:coconut_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:coconut", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:date_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 6),
                makeDrop("croptopia:date_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:date", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:dragonfruit_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 6),
                makeDrop("croptopia:dragonfruit_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:dragonfruit", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:fig_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 6),
                makeDrop("croptopia:fig_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:fig", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:grapefruit_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 6),
                makeDrop("croptopia:grapefruit_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:grapefruit", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:kumquat_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 6),
                makeDrop("croptopia:kumquat_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:kumquat", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:lemon_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 6),
                makeDrop("croptopia:lemon_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:lemon", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:lime_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 6),
                makeDrop("croptopia:lime_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:lime", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:mango_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 6),
                makeDrop("croptopia:mango_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:mango", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:nectarine_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 6),
                makeDrop("croptopia:nectarine_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:nectarine", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:nutmeg_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:jungle_log", 4, 6),
                makeDrop("croptopia:nutmeg_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:nutmeg", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:orange_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 6),
                makeDrop("croptopia:orange_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:orange", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:peach_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 6),
                makeDrop("croptopia:peach_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:peach", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:pear_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 6),
                makeDrop("croptopia:pear_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:pear", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:pecan_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:dark_oak_log", 4, 6),
                makeDrop("croptopia:pecan_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:pecan", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:persimmon_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 6),
                makeDrop("croptopia:persimmon_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:persimmon", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:plum_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 6),
                makeDrop("croptopia:plum_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:plum", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:starfruit_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:oak_log", 4, 6),
                makeDrop("croptopia:starfruit_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:starfruit", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("croptopia:walnut_sapling", STANDARD_TREE_SOILS,
                makeDrop("minecraft:dark_oak_log", 4, 6),
                makeDrop("croptopia:walnut_sapling", 1, 1, 0.3F),
                makeDrop("croptopia:walnut", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
    }

    private static void addCobblemonTrees(List<TreeEntry> trees) {
        trees.add(makeTree("cobblemon:red_apricorn_seed", STANDARD_TREE_SOILS,
                makeDrop("cobblemon:apricorn_log", 4, 6),
                makeDrop("cobblemon:red_apricorn_seed", 1, 1, 0.3F),
                makeDrop("cobblemon:red_apricorn", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("cobblemon:yellow_apricorn_seed", STANDARD_TREE_SOILS,
                makeDrop("cobblemon:apricorn_log", 4, 6),
                makeDrop("cobblemon:yellow_apricorn_seed", 1, 1, 0.3F),
                makeDrop("cobblemon:yellow_apricorn", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("cobblemon:green_apricorn_seed", STANDARD_TREE_SOILS,
                makeDrop("cobblemon:apricorn_log", 4, 6),
                makeDrop("cobblemon:green_apricorn_seed", 1, 1, 0.3F),
                makeDrop("cobblemon:green_apricorn", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("cobblemon:blue_apricorn_seed", STANDARD_TREE_SOILS,
                makeDrop("cobblemon:apricorn_log", 4, 6),
                makeDrop("cobblemon:blue_apricorn_seed", 1, 1, 0.3F),
                makeDrop("cobblemon:blue_apricorn", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("cobblemon:pink_apricorn_seed", STANDARD_TREE_SOILS,
                makeDrop("cobblemon:apricorn_log", 4, 6),
                makeDrop("cobblemon:pink_apricorn_seed", 1, 1, 0.3F),
                makeDrop("cobblemon:pink_apricorn", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("cobblemon:black_apricorn_seed", STANDARD_TREE_SOILS,
                makeDrop("cobblemon:apricorn_log", 4, 6),
                makeDrop("cobblemon:black_apricorn_seed", 1, 1, 0.3F),
                makeDrop("cobblemon:black_apricorn", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("cobblemon:white_apricorn_seed", STANDARD_TREE_SOILS,
                makeDrop("cobblemon:apricorn_log", 4, 6),
                makeDrop("cobblemon:white_apricorn_seed", 1, 1, 0.3F),
                makeDrop("cobblemon:white_apricorn", 2, 4, 1.0F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
        trees.add(makeTree("cobblemon:saccharine_sapling", STANDARD_TREE_SOILS,
                makeDrop("cobblemon:saccharine_log", 4, 6),
                makeDrop("cobblemon:saccharine_sapling", 1, 1, 0.3F),
                makeDrop("minecraft:stick", 1, 2, 0.5F)));
    }

    private static void addVanillaSoils(List<SoilEntry> soils) {
        record S(String id, float mod) {}
        for (S s : new S[]{
                new S("minecraft:dirt",                   0.475F),
                new S("minecraft:coarse_dirt",            0.475F),
                new S("minecraft:podzol",                 0.475F),
                new S("minecraft:mycelium",               0.475F),
                new S("minecraft:mud",                    0.5F),
                new S("minecraft:muddy_mangrove_roots",   0.5F),
                new S("minecraft:rooted_dirt",            0.475F),
                new S("minecraft:moss_block",             0.475F),
                new S("minecraft:pale_moss_block",        0.475F),
                new S("minecraft:farmland",               0.5F),
                new S("minecraft:sand",                   0.5F),
                new S("minecraft:red_sand",               0.5F),
                new S("minecraft:grass_block",            0.475F),
                new S("minecraft:soul_sand",              0.5F),
                new S("minecraft:end_stone",              0.5F),
                new S("minecraft:jungle_log",             0.5F),
                new S("minecraft:jungle_wood",            0.5F),
                new S("minecraft:stripped_jungle_log",    0.5F),
                new S("minecraft:water_bucket",           0.5F),
                new S("minecraft:stripped_jungle_wood",   0.5F),
                new S("minecraft:crimson_nylium",         0.6F),
                new S("minecraft:warped_nylium",          0.6F),
                new S("minecraft:stone",                  0.6F),
                new S("snad:suol_snad",                  1.2F),
                new S("snad:snad",                  1.2F),
                new S("snad:red_snad",                  1.2F),
                new S("agritechevolved:infused_farmland", 1.5F),
                new S("agritechevolved:mulch", 1.0F)

        }) {
            SoilEntry e = new SoilEntry();
            e.soil = s.id();
            e.growthModifier = s.mod();
            soils.add(e);
        }
    }

    private static void addFarmersDelightSoils(List<SoilEntry> soils) {
        for (String id : new String[]{
                "farmersdelight:rich_soil",
                "farmersdelight:rich_soil_farmland",
                "farmersdelight:organic_compost"
        }) {
            SoilEntry e = new SoilEntry();
            e.soil = id;
            e.growthModifier = 0.525F;
            soils.add(e);
        }
    }

    private static void addMysticalAgricultureSoils(List<SoilEntry> soils) {
        record S(String id, float mod) {}
        for (S s : new S[]{
                new S("mysticalagriculture:inferium_farmland",  0.55F),
                new S("mysticalagriculture:prudentium_farmland",0.625F),
                new S("mysticalagriculture:tertium_farmland",   0.75F),
                new S("mysticalagriculture:imperium_farmland",  0.875F),
                new S("mysticalagriculture:supremium_farmland", 1.0F)
        }) {
            SoilEntry e = new SoilEntry();
            e.soil = s.id();
            e.growthModifier = s.mod();
            soils.add(e);
        }

        if (Config.enableMysticalAgradditions) {
            for (String id : new String[]{
                    "mysticalagradditions:insanium_farmland",
                    "mysticalagradditions:nether_star_crux",
                    "mysticalagradditions:dragon_egg_crux",
                    "mysticalagradditions:awakened_draconium_crux",
                    "mysticalagradditions:neutronium_crux",
                    "mysticalagradditions:nitro_crystal_crux",
                    "mysticalagradditions:gaia_spirit_crux"
            }) {
                SoilEntry e = new SoilEntry();
                e.soil = id;
                e.growthModifier = 1.75F;
                soils.add(e);
            }
        }
    }

    private static void addJustDireThingsSoils(List<SoilEntry> soils) {
        record S(String id, float mod) {}
        for (S s : new S[]{
                new S("justdirethings:goosoil_tier1", 0.575F),
                new S("justdirethings:goosoil_tier2", 0.75F),
                new S("justdirethings:goosoil_tier3", 1.0F),
                new S("justdirethings:goosoil_tier4", 1.5F)
        }) {
            SoilEntry e = new SoilEntry();
            e.soil = s.id();
            e.growthModifier = s.mod();
            soils.add(e);
        }
    }

    private static void addVanillaFertilizers(List<FertilizerEntry> fertilizers) {
        FertilizerEntry boneMeal = new FertilizerEntry();
        boneMeal.item = "minecraft:bone_meal";
        boneMeal.speedMultiplier = (float) Config.getFertilizerBoneMealSpeedMultiplier();
        boneMeal.yieldMultiplier = (float) Config.getFertilizerBoneMealYieldMultiplier();

        FertilizerEntry crudeBiomass = new FertilizerEntry();
        crudeBiomass.item = "agritechevolved:crude_biomass";
        crudeBiomass.speedMultiplier = (float) Config.getFertilizerCrudeBiomassSpeedMultiplier();
        crudeBiomass.yieldMultiplier = (float) Config.getFertilizerCrudeBiomassYieldMultiplier();

        FertilizerEntry biomass = new FertilizerEntry();
        biomass.item = "agritechevolved:biomass";
        biomass.speedMultiplier = (float) Config.getFertilizerBiomassSpeedMultiplier();
        biomass.yieldMultiplier = (float) Config.getFertilizerBiomassYieldMultiplier();

        FertilizerEntry compactedBiomass = new FertilizerEntry();
        compactedBiomass.item = "agritechevolved:compacted_biomass";
        compactedBiomass.speedMultiplier = (float) Config.getFertilizerCompactedBiomassSpeedMultiplier();
        compactedBiomass.yieldMultiplier = (float) Config.getFertilizerCompactedBiomassYieldMultiplier();

        fertilizers.add(boneMeal);
        fertilizers.add(crudeBiomass);
        fertilizers.add(biomass);
        fertilizers.add(compactedBiomass);
    }

    private static void addImmersiveEngineeringFertilizers(List<FertilizerEntry> fertilizers) {
        FertilizerEntry f = new FertilizerEntry();
        f.item = "immersiveengineering:fertilizer";
        f.speedMultiplier = (float) Config.getFertilizerFertilizedEssenceSpeedMultiplier();
        f.yieldMultiplier = (float) Config.getFertilizerFertilizedEssenceYieldMultiplier();
        fertilizers.add(f);
    }

    private static void addForbiddenArcanusFertilizers(List<FertilizerEntry> fertilizers) {
        FertilizerEntry f = new FertilizerEntry();
        f.item = "forbidden_arcanus:arcane_bone_meal";
        f.speedMultiplier = (float) Config.getFertilizerArcaneBoneMealSpeedMultiplier();
        f.yieldMultiplier = (float) Config.getFertilizerArcaneBoneMealYieldMultiplier();
        fertilizers.add(f);
    }

    private static void addMysticalAgricultureFertilizers(List<FertilizerEntry> fertilizers) {
        FertilizerEntry mystical = new FertilizerEntry();
        mystical.item = "mysticalagriculture:mystical_fertilizer";
        mystical.speedMultiplier = (float) Config.getFertilizerMysticalFertilizerSpeedMultiplier();
        mystical.yieldMultiplier = (float) Config.getFertilizerMysticalFertilizerYieldMultiplier();
        fertilizers.add(mystical);

        FertilizerEntry essence = new FertilizerEntry();
        essence.item = "mysticalagriculture:fertilized_essence";
        essence.speedMultiplier = (float) Config.getFertilizerFertilizedEssenceSpeedMultiplier();
        essence.yieldMultiplier = (float) Config.getFertilizerFertilizedEssenceYieldMultiplier();
        fertilizers.add(essence);
    }

    private static void processConfig(PlantablesConfigData configData) {
        crops.clear();
        trees.clear();
        soils.clear();
        fertilizers.clear();

        if (configData.allowedCrops != null) {
            for (CropEntry entry : configData.allowedCrops) {
                if (entry.seed != null && !entry.seed.isEmpty()) {
                    crops.put(entry.seed, createPlantInfo(entry.validSoils, entry.soil, entry.drops));
                }
            }
        }

        if (configData.allowedTrees != null) {
            for (TreeEntry entry : configData.allowedTrees) {
                if (entry.sapling != null && !entry.sapling.isEmpty()) {
                    trees.put(entry.sapling, createTreeInfo(entry.validSoils, entry.soil, entry.drops));
                }
            }
        }

        if (configData.allowedSoils != null) {
            for (SoilEntry entry : configData.allowedSoils) {
                if (entry.soil != null && !entry.soil.isEmpty()) {
                    soils.put(entry.soil, new SoilInfo(entry.growthModifier));
                }
            }
        }

        if (configData.allowedFertilizers != null) {
            for (FertilizerEntry entry : configData.allowedFertilizers) {
                if (entry.item != null && !entry.item.isEmpty()) {
                    fertilizers.put(entry.item, new FertilizerInfo(entry.speedMultiplier, entry.yieldMultiplier));
                }
            }
        }

        LOGGER.info("Loaded {} crops, {} trees, and {} soils from config", crops.size(), trees.size(), soils.size());
    }

    private static CropInfo createPlantInfo(List<String> validSoils, String soil, List<DropEntry> drops) {
        CropInfo info = new CropInfo();
        info.drops = new ArrayList<>();
        if (validSoils != null && !validSoils.isEmpty()) {
            info.validSoils.addAll(validSoils);
        } else if (soil != null && !soil.isEmpty()) {
            info.validSoils.add(soil);
        }
        if (drops != null) {
            for (DropEntry d : drops) {
                info.drops.add(new DropInfo(d.item,
                        d.count != null ? d.count.min : 1,
                        d.count != null ? d.count.max : 1,
                        d.chance));
            }
        }
        return info;
    }

    private static TreeInfo createTreeInfo(List<String> validSoils, String soil, List<DropEntry> drops) {
        TreeInfo info = new TreeInfo();
        info.drops = new ArrayList<>();
        if (validSoils != null && !validSoils.isEmpty()) {
            info.validSoils.addAll(validSoils);
        } else if (soil != null && !soil.isEmpty()) {
            info.validSoils.add(soil);
        }
        if (drops != null) {
            for (DropEntry d : drops) {
                info.drops.add(new DropInfo(d.item,
                        d.count != null ? d.count.min : 1,
                        d.count != null ? d.count.max : 1,
                        d.chance));
            }
        }
        return info;
    }

    public static boolean isValidSeed(String itemId) {
        return crops.containsKey(itemId);
    }

    public static boolean isValidSapling(String itemId) {
        return trees.containsKey(itemId);
    }

    public static boolean isValidSoil(String blockId) {
        return soils.containsKey(blockId);
    }

    public static boolean isValidFertilizer(String itemId) {
        return fertilizers.containsKey(itemId);
    }

    public static boolean isSoilValidForSeed(String soilId, String seedId) {
        CropInfo info = crops.get(seedId);
        return info != null && !info.validSoils.isEmpty() && info.validSoils.contains(soilId);
    }

    public static boolean isSoilValidForSapling(String soilId, String saplingId) {
        TreeInfo info = trees.get(saplingId);
        return info != null && !info.validSoils.isEmpty() && info.validSoils.contains(soilId);
    }

    public static List<DropInfo> getCropDrops(String seedId) {
        CropInfo info = crops.get(seedId);
        return info != null ? info.drops : Collections.emptyList();
    }

    public static List<DropInfo> getTreeDrops(String saplingId) {
        TreeInfo info = trees.get(saplingId);
        return info != null ? info.drops : Collections.emptyList();
    }

    public static float getSoilGrowthModifier(String blockId) {
        SoilInfo info = soils.get(blockId);
        return info != null ? info.growthModifier : 1.0F;
    }

    public static FertilizerInfo getFertilizerInfo(String itemId) {
        return fertilizers.get(itemId);
    }

    public static int getBaseSaplingGrowthTime(String saplingId) {
        return Config.getPlanterBaseProcessingTime();
    }

    public static Map<String, List<String>> getAllSeedToSoilMappings() {
        Map<String, List<String>> result = new HashMap<>();
        for (Map.Entry<String, CropInfo> entry : crops.entrySet()) {
            if (!entry.getValue().validSoils.isEmpty()) {
                result.put(entry.getKey(), new ArrayList<>(entry.getValue().validSoils));
            }
        }
        return result;
    }

    public static Map<String, List<String>> getAllSaplingToSoilMappings() {
        Map<String, List<String>> result = new HashMap<>();
        for (Map.Entry<String, TreeInfo> entry : trees.entrySet()) {
            if (!entry.getValue().validSoils.isEmpty()) {
                result.put(entry.getKey(), new ArrayList<>(entry.getValue().validSoils));
            }
        }
        return result;
    }

    public static class PlantablesConfigData {
        public List<CropEntry> allowedCrops;
        public List<TreeEntry> allowedTrees;
        public List<SoilEntry> allowedSoils;
        public List<FertilizerEntry> allowedFertilizers;
    }

    public static class CropEntry {
        public String seed;
        public String soil;
        public List<String> validSoils;
        public List<DropEntry> drops;
    }

    public static class TreeEntry {
        public String sapling;
        public String soil;
        public List<String> validSoils;
        public List<DropEntry> drops;
    }

    public static class SoilEntry {
        public String soil;
        public float growthModifier;
    }

    public static class FertilizerEntry {
        public String item;
        public float speedMultiplier = 1.2F;
        public float yieldMultiplier = 1.2F;
    }

    public static class DropEntry {
        public String item;
        public CountRange count;
        public float chance = 1.0F;
    }

    public static class CountRange {
        public int min;
        public int max;

        public CountRange() {
            this.min = 1;
            this.max = 1;
        }

        public CountRange(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }

    public static class CropInfo {
        public List<DropInfo> drops;
        public List<String> validSoils = new ArrayList<>();
    }

    public static class TreeInfo {
        public List<DropInfo> drops;
        public List<String> validSoils = new ArrayList<>();
    }

    public static class SoilInfo {
        public final float growthModifier;

        public SoilInfo(float growthModifier) {
            this.growthModifier = growthModifier;
        }
    }

    public static class FertilizerInfo {
        public final float speedMultiplier;
        public final float yieldMultiplier;

        public FertilizerInfo(float speedMultiplier, float yieldMultiplier) {
            this.speedMultiplier = speedMultiplier;
            this.yieldMultiplier = yieldMultiplier;
        }
    }

    public static class DropInfo {
        public final String item;
        public final int minCount;
        public final int maxCount;
        public final float chance;

        public DropInfo(String item, int minCount, int maxCount, float chance) {
            this.item = item;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.chance = chance;
        }
    }
}
