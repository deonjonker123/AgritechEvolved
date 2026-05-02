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

public class CompostableConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Set<String> compostableItems = new HashSet<>();

    public static void loadConfig() {
        LOGGER.info("CompostableConfig.loadConfig() invoked.");
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("agritechevolved/compostable.json");
        if (!Files.exists(configPath)) {
            createDefaultConfig(configPath);
        }

        try {
            String jsonString = Files.readString(configPath);
            CompostableConfigData configData = GSON.fromJson(jsonString, CompostableConfigData.class);
            processConfig(configData);
        } catch (JsonSyntaxException | IOException e) {
            LOGGER.error("Failed to load compostable config file: {}", e.getMessage());
            LOGGER.info("Loading default compostable configuration instead");
            processConfig(getDefaultConfig());
        }

        CompostableOverrideConfig.loadOverrides(compostableItems);
    }

    private static void createDefaultConfig(Path configPath) {
        try {
            Files.createDirectories(configPath.getParent());
            CompostableConfigData defaultConfig = getDefaultConfig();
            Files.writeString(configPath, GSON.toJson(defaultConfig));
            LOGGER.info("Created default compostable config at {}", configPath);
        } catch (IOException e) {
            LOGGER.error("Failed to create default compostable config file: {}", e.getMessage());
        }
    }

    private static CompostableConfigData getDefaultConfig() {
        LOGGER.info("Generating default compostable config.");
        CompostableConfigData config = new CompostableConfigData();
        config.compostableItems = new ArrayList<>();

        addVanillaCompostables(config.compostableItems);

        if (Config.enableMysticalAgriculture) {
            LOGGER.info("Adding Mystical Agriculture compostables");
            addMysticalAgricultureCompostables(config.compostableItems);
        }
        if (Config.enableMysticalAgradditions) {
            LOGGER.info("Adding Mystical Agradditions compostables");
            addMysticalAgradditionsCompostables(config.compostableItems);
        }
        if (Config.enableFarmersDelight) {
            LOGGER.info("Adding Farmer's Delight compostables");
            addFarmersDelightCompostables(config.compostableItems);
        }
        if (Config.enableArsNouveau) {
            LOGGER.info("Adding Ars Nouveau compostables");
            addArsNouveauCompostables(config.compostableItems);
        }
        if (Config.enableArsElemental) {
            LOGGER.info("Adding Ars Elemental compostables");
            addArsElementalCompostables(config.compostableItems);
        }
        if (Config.enableSilentGear) {
            LOGGER.info("Adding Silent Gear compostables");
            addSilentGearCompostables(config.compostableItems);
        }
        if (Config.enableImmersiveEngineering) {
            LOGGER.info("Adding Immersive Engineering compostables");
            addImmersiveEngineeringCompostables(config.compostableItems);
        }
        if (Config.enableForbiddenArcanus) {
            LOGGER.info("Adding Forbidden Arcanus compostables");
            addForbiddenArcanusCompostables(config.compostableItems);
        }
        if (Config.enableEvilCraft) {
            LOGGER.info("Adding EvilCraft compostables");
            addEvilCraftCompostables(config.compostableItems);
        }
        if (Config.enableIntegratedDynamics) {
            LOGGER.info("Adding Integrated Dynamics compostables");
            addIntegratedDynamicsCompostables(config.compostableItems);
        }
        if (Config.enableOccultism) {
            LOGGER.info("Adding Occultism compostables");
            addOccultismCompostables(config.compostableItems);
        }
        if (Config.enablePamsCrops) {
            LOGGER.info("Adding Pam's HarvestCraft - Crops compostables");
            addPamsCropsCompostables(config.compostableItems);
        }
        if (Config.enablePamsTrees) {
            LOGGER.info("Adding Pam's HarvestCraft - Trees compostables");
            addPamsTreesCompostables(config.compostableItems);
        }
        if (Config.enableCroptopia) {
            LOGGER.info("Adding Croptopia compostables");
            addCroptopiaCompostables(config.compostableItems);
        }
        if (Config.enableCobblemon) {
            LOGGER.info("Adding Cobblemon compostables");
            addCobblemonCompostables(config.compostableItems);
        }
        if (Config.enableActuallyAdditions) {
            LOGGER.info("Adding Actually Additions compostables");
            addActuallyAdditionsCompostables(config.compostableItems);
        }

        return config;
    }

    // -------------------------------------------------------------------------
    // Compostable item lists
    // -------------------------------------------------------------------------

    private static void addVanillaCompostables(List<String> items) {
        // Seeds
        items.addAll(Arrays.asList(
                "minecraft:wheat_seeds", "minecraft:beetroot_seeds",
                "minecraft:melon_seeds", "minecraft:pumpkin_seeds",
                "minecraft:carrot", "minecraft:potato",
                "minecraft:torchflower_seeds", "minecraft:pitcher_pod",
                "minecraft:cocoa_beans", "minecraft:nether_wart",
                "minecraft:chorus_flower", "minecraft:chorus_fruit"
        ));
        // Saplings
        items.addAll(Arrays.asList(
                "minecraft:oak_sapling", "minecraft:birch_sapling",
                "minecraft:spruce_sapling", "minecraft:jungle_sapling",
                "minecraft:acacia_sapling", "minecraft:dark_oak_sapling",
                "minecraft:cherry_sapling", "minecraft:mangrove_propagule",
                "minecraft:azalea", "minecraft:flowering_azalea",
                "minecraft:crimson_fungus", "minecraft:warped_fungus","minecraft:pale_oak_sapling"
        ));
        // Crops and food
        items.addAll(Arrays.asList(
                "minecraft:wheat", "minecraft:beetroot", "minecraft:melon_slice",
                "minecraft:pumpkin", "minecraft:sugar_cane", "minecraft:bamboo",
                "minecraft:kelp", "minecraft:cactus", "minecraft:apple",
                "minecraft:sweet_berries", "minecraft:glow_berries",
                "minecraft:poisonous_potato", "minecraft:melon"
        ));
        // Leaves and natural blocks
        items.addAll(Arrays.asList(
                "minecraft:oak_leaves", "minecraft:birch_leaves",
                "minecraft:spruce_leaves", "minecraft:jungle_leaves",
                "minecraft:acacia_leaves", "minecraft:dark_oak_leaves",
                "minecraft:cherry_leaves", "minecraft:azalea_leaves",
                "minecraft:flowering_azalea_leaves", "minecraft:mangrove_leaves",
                "minecraft:muddy_mangrove_roots", "minecraft:mangrove_roots",
                "minecraft:moss_block", "minecraft:vine", "minecraft:lily_pad",
                "minecraft:moss_carpet", "minecraft:pale_moss_carpet",
                "minecraft:spore_blossom", "minecraft:pink_petals",
                "minecraft:sculk_vein", "minecraft:glow_lichen", "minecraft:sea_pickle",
                "minecraft:twisting_vines", "minecraft:weeping_vines", "minecraft:leaf_litter",
                "minecraft:big_dripleaf", "minecraft:small_dripleaf"
        ));
        // Grass and foliage
        items.addAll(Arrays.asList(
                "minecraft:short_grass", "minecraft:fern",
                "minecraft:tall_grass", "minecraft:large_fern",
                "minecraft:dead_bush", "minecraft:stick"
        ));
        // Dirt variants
        items.addAll(Arrays.asList(
                "minecraft:dirt", "minecraft:grass_block", "minecraft:coarse_dirt",
                "minecraft:podzol", "minecraft:mycelium"
        ));
        // Flowers
        items.addAll(Arrays.asList(
                "minecraft:dandelion", "minecraft:poppy", "minecraft:blue_orchid",
                "minecraft:allium", "minecraft:azure_bluet", "minecraft:red_tulip",
                "minecraft:orange_tulip", "minecraft:white_tulip", "minecraft:pink_tulip",
                "minecraft:oxeye_daisy", "minecraft:cornflower",
                "minecraft:lily_of_the_valley", "minecraft:sunflower",
                "minecraft:lilac", "minecraft:rose_bush", "minecraft:peony",
                "minecraft:wither_rose", "minecraft:torchflower", "minecraft:pitcher_plant",
                "minecraft:closed_eyeblossom", "minecraft:open_eyeblossom",
                "minecraft:cactus_flower", "minecraft:wildflowers"
        ));
        // Mushrooms
        items.addAll(Arrays.asList(
                "minecraft:red_mushroom", "minecraft:brown_mushroom"
        ));
        // Animal drops
        items.addAll(Arrays.asList(
                "minecraft:rotten_flesh", "minecraft:bone", "minecraft:spider_eye",
                "minecraft:leather", "minecraft:feather", "minecraft:string"
        ));
    }

    private static void addMysticalAgricultureCompostables(List<String> items) {
        // Tier 1
        items.addAll(Arrays.asList(
                "mysticalagriculture:air_seeds", "mysticalagriculture:earth_seeds",
                "mysticalagriculture:water_seeds", "mysticalagriculture:fire_seeds",
                "mysticalagriculture:inferium_seeds", "mysticalagriculture:stone_seeds",
                "mysticalagriculture:dirt_seeds", "mysticalagriculture:wood_seeds",
                "mysticalagriculture:ice_seeds", "mysticalagriculture:deepslate_seeds",
                "mysticalagriculture:inferium_essence"
        ));
        // Tier 2
        items.addAll(Arrays.asList(
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
                "mysticalagriculture:grains_of_infinity_seeds",
                "mysticalagriculture:mystical_flower_seeds",
                "mysticalagriculture:marble_seeds", "mysticalagriculture:limestone_seeds",
                "mysticalagriculture:basalt_seeds", "mysticalagriculture:menril_seeds",
                "mysticalagriculture:prudentium_essence"
        ));
        // Tier 3
        items.addAll(Arrays.asList(
                "mysticalagriculture:iron_seeds", "mysticalagriculture:copper_seeds",
                "mysticalagriculture:nether_quartz_seeds",
                "mysticalagriculture:glowstone_seeds",
                "mysticalagriculture:redstone_seeds", "mysticalagriculture:obsidian_seeds",
                "mysticalagriculture:prismarine_seeds", "mysticalagriculture:zombie_seeds",
                "mysticalagriculture:skeleton_seeds", "mysticalagriculture:creeper_seeds",
                "mysticalagriculture:spider_seeds", "mysticalagriculture:rabbit_seeds",
                "mysticalagriculture:tin_seeds", "mysticalagriculture:bronze_seeds",
                "mysticalagriculture:zinc_seeds", "mysticalagriculture:brass_seeds",
                "mysticalagriculture:silver_seeds", "mysticalagriculture:lead_seeds",
                "mysticalagriculture:graphite_seeds", "mysticalagriculture:blizz_seeds",
                "mysticalagriculture:blitz_seeds", "mysticalagriculture:basalz_seeds",
                "mysticalagriculture:amethyst_bronze_seeds",
                "mysticalagriculture:slimesteel_seeds",
                "mysticalagriculture:pig_iron_seeds",
                "mysticalagriculture:copper_alloy_seeds",
                "mysticalagriculture:redstone_alloy_seeds",
                "mysticalagriculture:conductive_alloy_seeds",
                "mysticalagriculture:steeleaf_seeds", "mysticalagriculture:ironwood_seeds",
                "mysticalagriculture:sky_stone_seeds",
                "mysticalagriculture:certus_quartz_seeds",
                "mysticalagriculture:quartz_enriched_iron_seeds",
                "mysticalagriculture:manasteel_seeds",
                "mysticalagriculture:aquamarine_seeds",
                "mysticalagriculture:phantom_seeds", "mysticalagriculture:sculk_seeds",
                "mysticalagriculture:tertium_essence"
        ));
        // Tier 4
        items.addAll(Arrays.asList(
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
                "mysticalagriculture:flux_infused_ingot_seeds",
                "mysticalagriculture:hop_graphite_seeds",
                "mysticalagriculture:cobalt_seeds", "mysticalagriculture:rose_gold_seeds",
                "mysticalagriculture:soularium_seeds",
                "mysticalagriculture:dark_steel_seeds",
                "mysticalagriculture:pulsating_alloy_seeds",
                "mysticalagriculture:energetic_alloy_seeds",
                "mysticalagriculture:elementium_seeds", "mysticalagriculture:osmium_seeds",
                "mysticalagriculture:fluorite_seeds",
                "mysticalagriculture:refined_glowstone_seeds",
                "mysticalagriculture:refined_obsidian_seeds",
                "mysticalagriculture:knightmetal_seeds",
                "mysticalagriculture:fiery_ingot_seeds",
                "mysticalagriculture:compressed_iron_seeds",
                "mysticalagriculture:starmetal_seeds", "mysticalagriculture:fluix_seeds",
                "mysticalagriculture:energized_steel_seeds",
                "mysticalagriculture:blazing_crystal_seeds",
                "mysticalagriculture:imperium_essence"
        ));
        // Tier 5
        items.addAll(Arrays.asList(
                "mysticalagriculture:diamond_seeds", "mysticalagriculture:emerald_seeds",
                "mysticalagriculture:netherite_seeds",
                "mysticalagriculture:wither_skeleton_seeds",
                "mysticalagriculture:platinum_seeds", "mysticalagriculture:iridium_seeds",
                "mysticalagriculture:enderium_seeds",
                "mysticalagriculture:flux_infused_gem_seeds",
                "mysticalagriculture:manyullyn_seeds",
                "mysticalagriculture:queens_slime_seeds",
                "mysticalagriculture:hepatizon_seeds",
                "mysticalagriculture:vibrant_alloy_seeds",
                "mysticalagriculture:end_steel_seeds",
                "mysticalagriculture:terrasteel_seeds",
                "mysticalagriculture:rock_crystal_seeds",
                "mysticalagriculture:draconium_seeds",
                "mysticalagriculture:yellorium_seeds", "mysticalagriculture:cyanite_seeds",
                "mysticalagriculture:niotic_crystal_seeds",
                "mysticalagriculture:spirited_crystal_seeds",
                "mysticalagriculture:uraninite_seeds",
                "mysticalagriculture:supremium_essence"
        ));
    }

    private static void addMysticalAgradditionsCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "mysticalagradditions:insanium_seeds",
                "mysticalagradditions:insanium_essence",
                "mysticalagradditions:insanium_farmland"
        ));
    }

    private static void addFarmersDelightCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "farmersdelight:cabbage_seeds", "farmersdelight:tomato_seeds",
                "farmersdelight:onion", "farmersdelight:cabbage",
                "farmersdelight:tomato", "farmersdelight:rice",
                "farmersdelight:rice_panicle", "farmersdelight:organic_compost"
        ));
    }

    private static void addArsNouveauCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "ars_nouveau:magebloom", "ars_nouveau:magebloom_crop",
                "ars_nouveau:sourceberry_bush",
                "ars_nouveau:blue_archwood_sapling",
                "ars_nouveau:red_archwood_sapling",
                "ars_nouveau:purple_archwood_sapling",
                "ars_nouveau:green_archwood_sapling",
                "ars_nouveau:frostaya_pod",
                "ars_nouveau:bombegranate_pod",
                "ars_nouveau:bastion_pod",
                "ars_nouveau:mendosteen_pod"
        ));
    }

    private static void addArsElementalCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "ars_elemental:yellow_archwood_sapling",
                "ars_elemental:flashpine_pod"
        ));
    }

    private static void addSilentGearCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "silentgear:flax_seeds", "silentgear:flax_flowers",
                "silentgear:flax_fiber", "silentgear:fluffy_seeds",
                "silentgear:fluffy_puff", "silentgear:netherwood_sapling"
        ));
    }

    private static void addImmersiveEngineeringCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "immersiveengineering:seed", "immersiveengineering:hemp_fiber"
        ));
    }

    private static void addForbiddenArcanusCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "forbidden_arcanus:aurum_sapling",
                "forbidden_arcanus:growing_edelwood"
        ));
    }

    private static void addEvilCraftCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "evilcraft:undead_sapling"
        ));
    }

    private static void addIntegratedDynamicsCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "integrateddynamics:menril_sapling",
                "integrateddynamics:menril_log",
                "integrateddynamics:menril_berries"
        ));
    }

    private static void addOccultismCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "occultism:otherworld_sapling",
                "occultism:datura_seeds",
                "occultism:datura"
        ));
    }

    private static void addPamsCropsCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "pamhc2crops:agaveseeditem", "pamhc2crops:agaveitem",
                "pamhc2crops:alfalfaseeditem", "pamhc2crops:alfalfaitem",
                "pamhc2crops:aloeseeditem", "pamhc2crops:aloeitem",
                "pamhc2crops:amaranthseeditem", "pamhc2crops:amaranthitem",
                "pamhc2crops:arrowrootseeditem", "pamhc2crops:arrowrootitem",
                "pamhc2crops:artichokeseeditem", "pamhc2crops:artichokeitem",
                "pamhc2crops:asparagusseeditem", "pamhc2crops:asparagusitem",
                "pamhc2crops:barleyseeditem", "pamhc2crops:barleyitem",
                "pamhc2crops:barrelcactusseeditem", "pamhc2crops:barrelcactusitem",
                "pamhc2crops:beanseeditem", "pamhc2crops:beanitem",
                "pamhc2crops:bellpepperseeditem", "pamhc2crops:bellpepperitem",
                "pamhc2crops:blackberryseeditem", "pamhc2crops:blackberryitem",
                "pamhc2crops:blueberryseeditem", "pamhc2crops:blueberryitem",
                "pamhc2crops:bokchoyseeditem", "pamhc2crops:bokchoyitem",
                "pamhc2crops:broccoliseeditem", "pamhc2crops:broccoliitem",
                "pamhc2crops:brusselsproutseeditem", "pamhc2crops:brusselsproutitem",
                "pamhc2crops:cabbageseeditem", "pamhc2crops:cabbageitem",
                "pamhc2crops:cactusfruitseeditem", "pamhc2crops:cactusfruititem",
                "pamhc2crops:calabashseeditem", "pamhc2crops:calabashitem",
                "pamhc2crops:candleberryseeditem", "pamhc2crops:candleberryitem",
                "pamhc2crops:canolaseeditem", "pamhc2crops:canolaitem",
                "pamhc2crops:cantaloupeseeditem", "pamhc2crops:cantaloupeitem",
                "pamhc2crops:cassavaseeditem", "pamhc2crops:cassavaitem",
                "pamhc2crops:cattailseeditem", "pamhc2crops:cattailitem",
                "pamhc2crops:cauliflowerseeditem", "pamhc2crops:caulifloweritem",
                "pamhc2crops:celeryseeditem", "pamhc2crops:celeryitem",
                "pamhc2crops:chiaseeditem", "pamhc2crops:chiaitem",
                "pamhc2crops:chickpeaseeditem", "pamhc2crops:chickpeaitem",
                "pamhc2crops:chilipepperseeditem", "pamhc2crops:chilipepperitem",
                "pamhc2crops:cloudberryseeditem", "pamhc2crops:cloudberryitem",
                "pamhc2crops:coffeebeanseeditem", "pamhc2crops:coffeebeanitem",
                "pamhc2crops:cornseeditem", "pamhc2crops:cornitem",
                "pamhc2crops:cottonseeditem", "pamhc2crops:cottonitem",
                "pamhc2crops:cranberryseeditem", "pamhc2crops:cranberryitem",
                "pamhc2crops:cucumberseeditem", "pamhc2crops:cucumberitem",
                "pamhc2crops:eggplantseeditem", "pamhc2crops:eggplantitem",
                "pamhc2crops:elderberryseeditem", "pamhc2crops:elderberryitem",
                "pamhc2crops:flaxseeditem", "pamhc2crops:flaxitem",
                "pamhc2crops:garlicseeditem", "pamhc2crops:garlicitem",
                "pamhc2crops:gingerseeditem", "pamhc2crops:gingeritem",
                "pamhc2crops:grapeseeditem", "pamhc2crops:grapeitem",
                "pamhc2crops:greengrapeseeditem", "pamhc2crops:greengrapeitem",
                "pamhc2crops:guaranaseeditem", "pamhc2crops:guaranaitem",
                "pamhc2crops:huckleberryseeditem", "pamhc2crops:huckleberryitem",
                "pamhc2crops:jicamaseeditem", "pamhc2crops:jicamaitem",
                "pamhc2crops:juniperberryseeditem", "pamhc2crops:juniperberryitem",
                "pamhc2crops:juteseeditem", "pamhc2crops:juteitem",
                "pamhc2crops:kaleseeditem", "pamhc2crops:kaleitem",
                "pamhc2crops:kenafseeditem", "pamhc2crops:kenafitem",
                "pamhc2crops:kiwiseeditem", "pamhc2crops:kiwiitem",
                "pamhc2crops:kohlrabiseeditem", "pamhc2crops:kohlrabiitem",
                "pamhc2crops:leekseeditem", "pamhc2crops:leekitem",
                "pamhc2crops:lentilseeditem", "pamhc2crops:lentilitem",
                "pamhc2crops:lettuceseeditem", "pamhc2crops:lettuceitem",
                "pamhc2crops:lotusseeditem", "pamhc2crops:lotusitem",
                "pamhc2crops:milletseeditem", "pamhc2crops:milletitem",
                "pamhc2crops:mulberryseeditem", "pamhc2crops:mulberryitem",
                "pamhc2crops:mustardseedsseeditem", "pamhc2crops:mustardseedsitem",
                "pamhc2crops:nettlesseeditem", "pamhc2crops:nettlesitem",
                "pamhc2crops:nopalesseeditem", "pamhc2crops:nopalesitem",
                "pamhc2crops:oatsseeditem", "pamhc2crops:oatsitem",
                "pamhc2crops:okraseeditem", "pamhc2crops:okraitem",
                "pamhc2crops:onionseeditem", "pamhc2crops:onionitem",
                "pamhc2crops:papyrusseeditem", "pamhc2crops:papyrusitem",
                "pamhc2crops:parsnipseeditem", "pamhc2crops:parsnipitem",
                "pamhc2crops:peanutseeditem", "pamhc2crops:peanutitem",
                "pamhc2crops:peasseeditem", "pamhc2crops:peasitem",
                "pamhc2crops:pineappleseeditem", "pamhc2crops:pineappleitem",
                "pamhc2crops:quinoaseeditem", "pamhc2crops:quinoaitem",
                "pamhc2crops:radishseeditem", "pamhc2crops:radishitem",
                "pamhc2crops:raspberryseeditem", "pamhc2crops:raspberryitem",
                "pamhc2crops:rhubarbseeditem", "pamhc2crops:rhubarbitem",
                "pamhc2crops:riceseeditem", "pamhc2crops:riceitem",
                "pamhc2crops:rutabagaseeditem", "pamhc2crops:rutabagaitem",
                "pamhc2crops:ryeseeditem", "pamhc2crops:ryeitem",
                "pamhc2crops:scallionseeditem", "pamhc2crops:scallionitem",
                "pamhc2crops:sesameseedsseeditem", "pamhc2crops:sesameseedsitem",
                "pamhc2crops:sisalseeditem", "pamhc2crops:sisalitem",
                "pamhc2crops:sorghumseeditem", "pamhc2crops:sorghumitem",
                "pamhc2crops:soybeanseeditem", "pamhc2crops:soybeanitem",
                "pamhc2crops:spiceleafseeditem", "pamhc2crops:spiceleafitem",
                "pamhc2crops:spinachseeditem", "pamhc2crops:spinachitem",
                "pamhc2crops:strawberryseeditem", "pamhc2crops:strawberryitem",
                "pamhc2crops:sweetpotatoseeditem", "pamhc2crops:sweetpotatoitem",
                "pamhc2crops:taroseeditem", "pamhc2crops:taroitem",
                "pamhc2crops:tealeafseeditem", "pamhc2crops:tealeafitem",
                "pamhc2crops:tomatilloseeditem", "pamhc2crops:tomatilloitem",
                "pamhc2crops:tomatoseeditem", "pamhc2crops:tomatoitem",
                "pamhc2crops:truffleseeditem", "pamhc2crops:truffleitem",
                "pamhc2crops:turnipseeditem", "pamhc2crops:turnipitem",
                "pamhc2crops:waterchestnutseeditem", "pamhc2crops:waterchestnutitem",
                "pamhc2crops:whitemushroomseeditem", "pamhc2crops:whitemushroomitem",
                "pamhc2crops:wintersquashseeditem", "pamhc2crops:wintersquashitem",
                "pamhc2crops:wolfberryseeditem", "pamhc2crops:wolfberryitem",
                "pamhc2crops:yuccaseeditem", "pamhc2crops:yuccaitem",
                "pamhc2crops:zucchiniseeditem", "pamhc2crops:zucchiniitem",
                "pamhc2crops:sunchokeseeditem", "pamhc2crops:sunchokeitem"
        ));
    }

    private static void addPamsTreesCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "pamhc2trees:acorn_sapling", "pamhc2trees:acornitem",
                "pamhc2trees:almond_sapling", "pamhc2trees:almonditem",
                "pamhc2trees:apple_sapling",
                "pamhc2trees:apricot_sapling", "pamhc2trees:apricotitem",
                "pamhc2trees:avocado_sapling", "pamhc2trees:avocadoitem",
                "pamhc2trees:banana_sapling", "pamhc2trees:bananaitem",
                "pamhc2trees:breadfruit_sapling", "pamhc2trees:breadfruititem",
                "pamhc2trees:candlenut_sapling", "pamhc2trees:candlenutitem",
                "pamhc2trees:cherry_sapling", "pamhc2trees:cherryitem",
                "pamhc2trees:chestnut_sapling", "pamhc2trees:chestnutitem",
                "pamhc2trees:cinnamon_sapling", "pamhc2trees:cinnamonitem",
                "pamhc2trees:coconut_sapling", "pamhc2trees:coconutitem",
                "pamhc2trees:date_sapling", "pamhc2trees:dateitem",
                "pamhc2trees:dragonfruit_sapling", "pamhc2trees:dragonfruititem",
                "pamhc2trees:durian_sapling", "pamhc2trees:durianitem",
                "pamhc2trees:fig_sapling", "pamhc2trees:figitem",
                "pamhc2trees:gooseberry_sapling", "pamhc2trees:gooseberryitem",
                "pamhc2trees:grapefruit_sapling", "pamhc2trees:grapefruititem",
                "pamhc2trees:guava_sapling", "pamhc2trees:guavaitem",
                "pamhc2trees:hazelnut_sapling", "pamhc2trees:hazelnutitem",
                "pamhc2trees:jackfruit_sapling", "pamhc2trees:jackfruititem",
                "pamhc2trees:lemon_sapling", "pamhc2trees:lemonitem",
                "pamhc2trees:lime_sapling", "pamhc2trees:limeitem",
                "pamhc2trees:lychee_sapling", "pamhc2trees:lycheeitem",
                "pamhc2trees:mango_sapling", "pamhc2trees:mangoitem",
                "pamhc2trees:maple_sapling", "pamhc2trees:maplesyrupitem",
                "pamhc2trees:nutmeg_sapling", "pamhc2trees:nutmegitem",
                "pamhc2trees:olive_sapling", "pamhc2trees:oliveitem",
                "pamhc2trees:orange_sapling", "pamhc2trees:orangeitem",
                "pamhc2trees:papaya_sapling", "pamhc2trees:papayaitem",
                "pamhc2trees:paperbark_sapling",
                "pamhc2trees:passionfruit_sapling", "pamhc2trees:passionfruititem",
                "pamhc2trees:pawpaw_sapling", "pamhc2trees:pawpawitem",
                "pamhc2trees:peach_sapling", "pamhc2trees:peachitem",
                "pamhc2trees:pear_sapling", "pamhc2trees:pearitem",
                "pamhc2trees:pecan_sapling", "pamhc2trees:pecanitem",
                "pamhc2trees:peppercorn_sapling", "pamhc2trees:peppercornitem",
                "pamhc2trees:persimmon_sapling", "pamhc2trees:persimmonitem",
                "pamhc2trees:pinenut_sapling", "pamhc2trees:pinenutitem",
                "pamhc2trees:pistachio_sapling", "pamhc2trees:pistachioitem",
                "pamhc2trees:plum_sapling", "pamhc2trees:plumitem",
                "pamhc2trees:pomegranate_sapling", "pamhc2trees:pomegranateitem",
                "pamhc2trees:rambutan_sapling", "pamhc2trees:rambutanitem",
                "pamhc2trees:soursop_sapling", "pamhc2trees:soursopitem",
                "pamhc2trees:spiderweb_sapling",
                "pamhc2trees:starfruit_sapling", "pamhc2trees:starfruititem",
                "pamhc2trees:tamarind_sapling", "pamhc2trees:tamarinditem",
                "pamhc2trees:walnut_sapling", "pamhc2trees:walnutitem",
                "pamhc2trees:cashew_sapling", "pamhc2trees:cashewitem",
                "pamhc2trees:vanillabean_sapling", "pamhc2trees:vanillabeanitem"
        ));
    }

    private static void addCroptopiaCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                // Seeds
                "croptopia:artichoke_seed", "croptopia:asparagus_seed",
                "croptopia:barley_seed", "croptopia:basil_seed",
                "croptopia:bellpepper_seed", "croptopia:blackbean_seed",
                "croptopia:blackberry_seed", "croptopia:blueberry_seed",
                "croptopia:broccoli_seed", "croptopia:cabbage_seed",
                "croptopia:cantaloupe_seed", "croptopia:cauliflower_seed",
                "croptopia:celery_seed", "croptopia:chile_pepper_seed",
                "croptopia:coffee_seed", "croptopia:corn_seed",
                "croptopia:cranberry_seed", "croptopia:cucumber_seed",
                "croptopia:currant_seed", "croptopia:eggplant_seed",
                "croptopia:elderberry_seed", "croptopia:garlic_seed",
                "croptopia:ginger_seed", "croptopia:grape_seed",
                "croptopia:greenbean_seed", "croptopia:greenonion_seed",
                "croptopia:honeydew_seed", "croptopia:hops_seed",
                "croptopia:kale_seed", "croptopia:kiwi_seed",
                "croptopia:leek_seed", "croptopia:lettuce_seed",
                "croptopia:mustard_seed", "croptopia:oat_seed",
                "croptopia:olive_seed", "croptopia:onion_seed",
                "croptopia:peanut_seed", "croptopia:pepper_seed",
                "croptopia:pineapple_seed", "croptopia:radish_seed",
                "croptopia:raspberry_seed", "croptopia:rhubarb_seed",
                "croptopia:rice_seed", "croptopia:rutabaga_seed",
                "croptopia:saguaro_seed", "croptopia:soybean_seed",
                "croptopia:spinach_seed", "croptopia:squash_seed",
                "croptopia:strawberry_seed", "croptopia:sweetpotato_seed",
                "croptopia:tea_seed", "croptopia:tomatillo_seed",
                "croptopia:tomato_seed", "croptopia:turmeric_seed",
                "croptopia:turnip_seed", "croptopia:vanilla_seeds",
                "croptopia:yam_seed", "croptopia:zucchini_seed",
                // Crops
                "croptopia:artichoke", "croptopia:asparagus",
                "croptopia:barley", "croptopia:basil",
                "croptopia:bellpepper", "croptopia:blackbean",
                "croptopia:blackberry", "croptopia:blueberry",
                "croptopia:broccoli", "croptopia:cabbage",
                "croptopia:cantaloupe", "croptopia:cauliflower",
                "croptopia:celery", "croptopia:chile_pepper",
                "croptopia:coffee", "croptopia:corn",
                "croptopia:cranberry", "croptopia:cucumber",
                "croptopia:currant", "croptopia:eggplant",
                "croptopia:elderberry", "croptopia:garlic",
                "croptopia:ginger", "croptopia:grape",
                "croptopia:greenbean", "croptopia:greenonion",
                "croptopia:honeydew", "croptopia:hops",
                "croptopia:kale", "croptopia:kiwi",
                "croptopia:leek", "croptopia:lettuce",
                "croptopia:mustard", "croptopia:oat",
                "croptopia:olive", "croptopia:onion",
                "croptopia:peanut", "croptopia:pepper",
                "croptopia:pineapple", "croptopia:radish",
                "croptopia:raspberry", "croptopia:rhubarb",
                "croptopia:rice", "croptopia:rutabaga",
                "croptopia:saguaro", "croptopia:soybean",
                "croptopia:spinach", "croptopia:squash",
                "croptopia:strawberry", "croptopia:sweetpotato",
                "croptopia:tea", "croptopia:tomatillo",
                "croptopia:tomato", "croptopia:turmeric",
                "croptopia:turnip", "croptopia:vanilla",
                "croptopia:yam", "croptopia:zucchini",
                // Tree saplings and fruits
                "croptopia:almond_sapling", "croptopia:almond",
                "croptopia:apple_sapling",
                "croptopia:apricot_sapling", "croptopia:apricot",
                "croptopia:avocado_sapling", "croptopia:avocado",
                "croptopia:banana_sapling", "croptopia:banana",
                "croptopia:cashew_sapling", "croptopia:cashew",
                "croptopia:cherry_sapling", "croptopia:cherry",
                "croptopia:cinnamon_sapling", "croptopia:cinnamon_log",
                "croptopia:coconut_sapling", "croptopia:coconut",
                "croptopia:date_sapling", "croptopia:date",
                "croptopia:dragonfruit_sapling", "croptopia:dragonfruit",
                "croptopia:fig_sapling", "croptopia:fig",
                "croptopia:grapefruit_sapling", "croptopia:grapefruit",
                "croptopia:kumquat_sapling", "croptopia:kumquat",
                "croptopia:lemon_sapling", "croptopia:lemon",
                "croptopia:lime_sapling", "croptopia:lime",
                "croptopia:mango_sapling", "croptopia:mango",
                "croptopia:nectarine_sapling", "croptopia:nectarine",
                "croptopia:nutmeg_sapling", "croptopia:nutmeg",
                "croptopia:orange_sapling", "croptopia:orange",
                "croptopia:peach_sapling", "croptopia:peach",
                "croptopia:pear_sapling", "croptopia:pear",
                "croptopia:pecan_sapling", "croptopia:pecan",
                "croptopia:persimmon_sapling", "croptopia:persimmon",
                "croptopia:plum_sapling", "croptopia:plum",
                "croptopia:starfruit_sapling", "croptopia:starfruit",
                "croptopia:walnut_sapling", "croptopia:walnut"
        ));
    }

    private static void addCobblemonCompostables(List<String> items) {
        // Berries
        items.addAll(Arrays.asList(
                "cobblemon:aguav_berry", "cobblemon:apicot_berry",
                "cobblemon:aspear_berry", "cobblemon:babiri_berry",
                "cobblemon:belue_berry", "cobblemon:bluk_berry",
                "cobblemon:charti_berry", "cobblemon:cheri_berry",
                "cobblemon:chesto_berry", "cobblemon:chilan_berry",
                "cobblemon:chople_berry", "cobblemon:coba_berry",
                "cobblemon:colbur_berry", "cobblemon:cornn_berry",
                "cobblemon:custap_berry", "cobblemon:durin_berry",
                "cobblemon:eggant_berry", "cobblemon:enigma_berry",
                "cobblemon:figy_berry", "cobblemon:ganlon_berry",
                "cobblemon:grepa_berry", "cobblemon:haban_berry",
                "cobblemon:hondew_berry", "cobblemon:hopo_berry",
                "cobblemon:iapapa_berry", "cobblemon:jaboca_berry",
                "cobblemon:kasib_berry", "cobblemon:kebia_berry",
                "cobblemon:kee_berry", "cobblemon:kelpsy_berry",
                "cobblemon:lansat_berry", "cobblemon:leppa_berry",
                "cobblemon:liechi_berry", "cobblemon:lum_berry",
                "cobblemon:mago_berry", "cobblemon:magost_berry",
                "cobblemon:maranga_berry", "cobblemon:micle_berry",
                "cobblemon:nanab_berry", "cobblemon:nomel_berry",
                "cobblemon:occa_berry", "cobblemon:oran_berry",
                "cobblemon:pamtre_berry", "cobblemon:passho_berry",
                "cobblemon:payapa_berry", "cobblemon:pecha_berry",
                "cobblemon:persim_berry", "cobblemon:petaya_berry",
                "cobblemon:pinap_berry", "cobblemon:pomeg_berry",
                "cobblemon:qualot_berry", "cobblemon:rabuta_berry",
                "cobblemon:rawst_berry", "cobblemon:razz_berry",
                "cobblemon:rindo_berry", "cobblemon:roseli_berry",
                "cobblemon:rowap_berry", "cobblemon:salac_berry",
                "cobblemon:shuca_berry", "cobblemon:sitrus_berry",
                "cobblemon:spelon_berry", "cobblemon:starf_berry",
                "cobblemon:tamato_berry", "cobblemon:tanga_berry",
                "cobblemon:touga_berry", "cobblemon:wacan_berry",
                "cobblemon:watmel_berry", "cobblemon:wepear_berry",
                "cobblemon:wiki_berry", "cobblemon:yache_berry"
        ));
        // Apricorn saplings and fruits
        items.addAll(Arrays.asList(
                "cobblemon:red_apricorn_seed", "cobblemon:red_apricorn",
                "cobblemon:yellow_apricorn_seed", "cobblemon:yellow_apricorn",
                "cobblemon:green_apricorn_seed", "cobblemon:green_apricorn",
                "cobblemon:blue_apricorn_seed", "cobblemon:blue_apricorn",
                "cobblemon:pink_apricorn_seed", "cobblemon:pink_apricorn",
                "cobblemon:black_apricorn_seed", "cobblemon:black_apricorn",
                "cobblemon:white_apricorn_seed", "cobblemon:white_apricorn",
                "cobblemon:saccharine_sapling"
        ));
        // Plants and herbs
        items.addAll(Arrays.asList(
                "cobblemon:revival_herb", "cobblemon:pep_up_flower",
                "cobblemon:vivichoke_seeds", "cobblemon:vivichoke",
                "cobblemon:big_root", "cobblemon:energy_root",
                "cobblemon:galarica_nuts", "cobblemon:hearty_grains",
                "cobblemon:medicinal_leek",
                "cobblemon:red_mint_seeds", "cobblemon:red_mint_leaf",
                "cobblemon:blue_mint_seeds", "cobblemon:blue_mint_leaf",
                "cobblemon:cyan_mint_seeds", "cobblemon:cyan_mint_leaf",
                "cobblemon:pink_mint_seeds", "cobblemon:pink_mint_leaf",
                "cobblemon:green_mint_seeds", "cobblemon:green_mint_leaf",
                "cobblemon:white_mint_seeds", "cobblemon:white_mint_leaf"
        ));
    }

    private static void addActuallyAdditionsCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "actuallyadditions:rice_seeds", "actuallyadditions:rice",
                "actuallyadditions:canola_seeds", "actuallyadditions:canola",
                "actuallyadditions:flax_seeds",
                "actuallyadditions:coffee_beans"
        ));
    }

    // -------------------------------------------------------------------------
    // Config processing
    // -------------------------------------------------------------------------

    private static void processConfig(CompostableConfigData configData) {
        compostableItems.clear();
        if (configData.compostableItems != null) {
            compostableItems.addAll(configData.compostableItems);
        }
        LOGGER.info("Loaded {} compostable items from config", compostableItems.size());
    }

    public static boolean isCompostable(String itemId) {
        return compostableItems.contains(itemId);
    }

    public static Set<String> getCompostableItems() {
        return new HashSet<>(compostableItems);
    }

    // -------------------------------------------------------------------------
    // Data class
    // -------------------------------------------------------------------------

    public static class CompostableConfigData {
        public List<String> compostableItems;
    }
}