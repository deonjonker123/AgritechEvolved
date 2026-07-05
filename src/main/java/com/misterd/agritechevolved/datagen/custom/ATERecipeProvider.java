package com.misterd.agritechevolved.datagen.custom;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.item.ATEItems;
import com.misterd.agritechevolved.recipe.CropRecipe;
import com.misterd.agritechevolved.recipe.DropEntry;
import com.misterd.agritechevolved.recipe.DurabilityShapelessRecipe;
import com.misterd.agritechevolved.recipe.TreeRecipe;
import com.misterd.agritechevolved.util.ATETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.WeatheringCopper;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ATERecipeProvider extends RecipeProvider {

    public ATERecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new ATERecipeProvider(provider, recipeOutput);
        }

        @Override
        public String getName() {
            return "Agritech: Evolved Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, ATEBlocks.ACACIA_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', Items.ACACIA_PLANKS)
                .define('L', Items.ACACIA_LOG)
                .define('H', Items.HOPPER)
                .define('D', Items.ACACIA_SLAB)
                .unlockedBy("has_acacia_log", has(Items.ACACIA_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.BAMBOO_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', Items.BAMBOO_PLANKS)
                .define('L', Items.BAMBOO_BLOCK)
                .define('H', Items.HOPPER)
                .define('D', Items.BAMBOO_SLAB)
                .unlockedBy("has_bamboo_block", has(Items.BAMBOO_BLOCK))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.BIRCH_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', Items.BIRCH_PLANKS)
                .define('L', Items.BIRCH_LOG)
                .define('H', Items.HOPPER)
                .define('D', Items.BIRCH_SLAB)
                .unlockedBy("has_birch_log", has(Items.BIRCH_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.CHERRY_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', Items.CHERRY_PLANKS)
                .define('L', Items.CHERRY_LOG)
                .define('H', Items.HOPPER)
                .define('D', Items.CHERRY_SLAB)
                .unlockedBy("has_cherry_log", has(Items.CHERRY_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.CRIMSON_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', Items.CRIMSON_PLANKS)
                .define('L', Items.CRIMSON_STEM)
                .define('H', Items.HOPPER)
                .define('D', Items.CRIMSON_SLAB)
                .unlockedBy("has_crimson_stem", has(Items.CRIMSON_STEM))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.DARK_OAK_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', Items.DARK_OAK_PLANKS)
                .define('L', Items.DARK_OAK_LOG)
                .define('H', Items.HOPPER)
                .define('D', Items.DARK_OAK_SLAB)
                .unlockedBy("has_dark_oak_log", has(Items.DARK_OAK_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.JUNGLE_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', Items.JUNGLE_PLANKS)
                .define('L', Items.JUNGLE_LOG)
                .define('H', Items.HOPPER)
                .define('D', Items.JUNGLE_SLAB)
                .unlockedBy("has_jungle_log", has(Items.JUNGLE_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.MANGROVE_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', Items.MANGROVE_PLANKS)
                .define('L', Items.MANGROVE_LOG)
                .define('H', Items.HOPPER)
                .define('D', Items.MANGROVE_SLAB)
                .unlockedBy("has_mangrove_log", has(Items.MANGROVE_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.OAK_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', Items.OAK_PLANKS)
                .define('L', Items.OAK_LOG)
                .define('H', Items.HOPPER)
                .define('D', Items.OAK_SLAB)
                .unlockedBy("has_oak_log", has(Items.OAK_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.OAK_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', ItemTags.PLANKS)
                .define('L', ItemTags.LOGS)
                .define('H', Items.HOPPER)
                .define('D', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_oak_log", has(Items.OAK_LOG))
                .save(output, "agritechevolved:zzz_basic_planter_from_any_wood");

        shaped(RecipeCategory.MISC, ATEBlocks.SPRUCE_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', Items.SPRUCE_PLANKS)
                .define('L', Items.SPRUCE_LOG)
                .define('H', Items.HOPPER)
                .define('D', Items.SPRUCE_SLAB)
                .unlockedBy("has_spruce_log", has(Items.SPRUCE_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.WARPED_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', Items.WARPED_PLANKS)
                .define('L', Items.WARPED_STEM)
                .define('H', Items.HOPPER)
                .define('D', Items.WARPED_SLAB)
                .unlockedBy("has_warped_stem", has(Items.WARPED_STEM))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.PALE_OAK_PLANTER.get())
                .pattern("P P")
                .pattern("PDP")
                .pattern("LHL")
                .define('P', Items.PALE_OAK_PLANKS)
                .define('L', Items.PALE_OAK_LOG)
                .define('H', Items.HOPPER)
                .define('D', Items.PALE_OAK_SLAB)
                .unlockedBy("has_pale_oak_log", has(Items.PALE_OAK_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.CLOCHE.get(), 4)
                .pattern("III")
                .pattern("IPI")
                .pattern("III")
                .define('P', Tags.Items.GLASS_BLOCKS)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.ADVANCED_PLANTER.get())
                .pattern("F F")
                .pattern("IAI")
                .pattern("RIR")
                .define('F', Items.IRON_INGOT)
                .define('I', Items.IRON_BLOCK)
                .define('A', ATETags.Items.BASIC_PLANTER_ITEMS)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_basic_planter", has(ATEBlocks.OAK_PLANTER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.COMPOSTER.get())
                .pattern("I I")
                .pattern("ICI")
                .pattern("IRI")
                .define('C', Items.COMPOSTER)
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_composter", has(Items.COMPOSTER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.BIOMASS_BURNER.get())
                .pattern("III")
                .pattern("IFI")
                .pattern("IRI")
                .define('I', Items.IRON_INGOT)
                .define('F', Items.FURNACE)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_furnace", has(Items.FURNACE))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.SILO.get())
                .pattern("III")
                .pattern("IFI")
                .pattern("IRI")
                .define('I', Items.IRON_INGOT)
                .define('F', Tags.Items.CHESTS)
                .define('R', Items.OBSERVER)
                .unlockedBy("has_observer", has(Items.OBSERVER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.FERT_SPREADER.get())
                .pattern("III")
                .pattern("IFI")
                .pattern("IRI")
                .define('I', Items.IRON_INGOT)
                .define('F', Tags.Items.CHESTS)
                .define('R', Items.DISPENSER)
                .unlockedBy("has_dispenser", has(Items.DISPENSER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.CAPACITOR_TIER_1.get())
                .pattern("RRR")
                .pattern("ICI")
                .pattern("RRR")
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_BLOCK.weathering().pick(WeatheringCopper.WeatherState.UNAFFECTED))
                .unlockedBy("has_copper_block", has(Items.COPPER_BLOCK.weathering().pick(WeatheringCopper.WeatherState.UNAFFECTED)))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.CAPACITOR_TIER_2.get())
                .pattern("RRR")
                .pattern("GCG")
                .pattern("RRR")
                .define('R', Items.REDSTONE_BLOCK)
                .define('G', Items.GOLD_INGOT)
                .define('C', ATEBlocks.CAPACITOR_TIER_1.get())
                .unlockedBy("has_capacitor_tier1", has(ATEBlocks.CAPACITOR_TIER_1.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.CAPACITOR_TIER_3.get())
                .pattern("DDD")
                .pattern("ECE")
                .pattern("DDD")
                .define('D', Items.DIAMOND)
                .define('E', Items.EMERALD)
                .define('C', ATEBlocks.CAPACITOR_TIER_2.get())
                .unlockedBy("has_capacitor_tier2", has(ATEBlocks.CAPACITOR_TIER_2.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.SM_MK1.get())
                .pattern(" R ")
                .pattern("IGI")
                .pattern(" R ")
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_INGOT)
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.SM_MK2.get())
                .pattern(" D ")
                .pattern("GSG")
                .pattern(" D ")
                .define('D', Items.DIAMOND)
                .define('G', Items.GOLD_BLOCK)
                .define('S', ATEItems.SM_MK1.get())
                .unlockedBy("has_sm_mk1", has(ATEItems.SM_MK1.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.SM_MK3.get())
                .pattern("ENE")
                .pattern("DSD")
                .pattern("ENE")
                .define('N', Items.NETHERITE_INGOT)
                .define('D', Items.DIAMOND_BLOCK)
                .define('E', Items.EMERALD_BLOCK)
                .define('S', ATEItems.SM_MK2.get())
                .unlockedBy("has_sm_mk2", has(ATEItems.SM_MK2.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.YM_MK1.get())
                .pattern(" W ")
                .pattern("SCS")
                .pattern(" W ")
                .define('W', Items.WHEAT)
                .define('S', Items.WHEAT_SEEDS)
                .define('C', Items.COPPER_BLOCK.weathering().pick(WeatheringCopper.WeatherState.UNAFFECTED))
                .unlockedBy("has_farmland", has(Items.FARMLAND))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.YM_MK2.get())
                .pattern(" G ")
                .pattern("CYC")
                .pattern(" G ")
                .define('G', Items.GOLD_BLOCK)
                .define('C', Items.COPPER_BLOCK.weathering().pick(WeatheringCopper.WeatherState.UNAFFECTED))
                .define('Y', ATEItems.YM_MK1.get())
                .unlockedBy("has_ym_mk1", has(ATEItems.YM_MK1.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.YM_MK3.get())
                .pattern("ENE")
                .pattern("GYG")
                .pattern("ENE")
                .define('E', Items.ENCHANTED_GOLDEN_APPLE)
                .define('G', Items.GOLD_BLOCK)
                .define('N', Items.NETHERITE_INGOT)
                .define('Y', ATEItems.YM_MK2.get())
                .unlockedBy("has_ym_mk2", has(ATEItems.YM_MK2.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.RM_MK1.get())
                .pattern(" W ")
                .pattern("SCS")
                .pattern(" W ")
                .define('W', Items.REDSTONE_TORCH)
                .define('S', Items.REDSTONE)
                .define('C', Items.COPPER_BLOCK.weathering().pick(WeatheringCopper.WeatherState.UNAFFECTED))
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.RM_MK2.get())
                .pattern(" G ")
                .pattern("CYC")
                .pattern(" G ")
                .define('G', Items.REDSTONE_BLOCK)
                .define('C', Items.IRON_BLOCK)
                .define('Y', ATEItems.YM_MK1.get())
                .unlockedBy("has_rm_mk1", has(ATEItems.RM_MK1.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.RM_MK3.get())
                .pattern("ENE")
                .pattern("GYG")
                .pattern("ENE")
                .define('E', Items.COMPARATOR)
                .define('G', Items.REDSTONE_BLOCK)
                .define('N', Items.NETHERITE_INGOT)
                .define('Y', ATEItems.YM_MK2.get())
                .unlockedBy("has_rm_mk2", has(ATEItems.RM_MK2.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.COMPACTED_BIOMASS.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ATEItems.BIOMASS.get())
                .unlockedBy("has_biomass", has(ATEItems.BIOMASS.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, ATEItems.BIOMASS.get(), 9)
                .requires(ATEItems.COMPACTED_BIOMASS.get())
                .unlockedBy("has_compacted_biomass", has(ATEItems.COMPACTED_BIOMASS.get()))
                .save(output, "biomass_from_compacted");

        shaped(RecipeCategory.BUILDING_BLOCKS, ATEBlocks.COMPACTED_BIOMASS_BLOCK.get())
                .pattern("CCC")
                .pattern("CCC")
                .pattern("CCC")
                .define('C', ATEItems.COMPACTED_BIOMASS.get())
                .unlockedBy("has_compacted_biomass", has(ATEItems.COMPACTED_BIOMASS.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, ATEItems.COMPACTED_BIOMASS.get(), 9)
                .requires(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get())
                .unlockedBy("has_compacted_biomass_block", has(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get()))
                .save(output, "compacted_biomass_from_block");

        shaped(RecipeCategory.BUILDING_BLOCKS, ATEBlocks.MULCH.get(), 2)
                .pattern("BBB")
                .pattern("BFB")
                .pattern("BBB")
                .define('B', ATEItems.COMPACTED_BIOMASS.get())
                .define('F', Items.FARMLAND)
                .unlockedBy("has_farmland", has(Items.FARMLAND))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.CRUDE_BIOMASS)
                .pattern("LLL")
                .pattern("DDD")
                .pattern("LLL")
                .define('L', ItemTags.LEAVES)
                .define('D', ATETags.Items.DIRT_LIKE_BLOCK_ITEMS)
                .unlockedBy("has_leaves", has(ItemTags.LEAVES))
                .save(output);

        saveTillingRecipe("dirt_to_farmland", Items.DIRT, Items.FARMLAND);
        saveTillingRecipe("rooted_dirt_to_farmland", Items.ROOTED_DIRT, Items.FARMLAND);
        saveTillingRecipe("coarse_dirt_to_farmland", Items.COARSE_DIRT, Items.FARMLAND);
        saveTillingRecipe("grass_to_farmland", Items.GRASS_BLOCK, Items.FARMLAND);
        saveTillingRecipe("mulch_to_infused_farmland", ATEBlocks.MULCH.get().asItem(), ATEBlocks.INFUSED_FARMLAND.get().asItem());

        generateCropRecipes();
        generateTreeRecipes();
    }

    private void generateCropRecipes() {
        Ingredient farmlandSoils = tagIngredient(ATETags.Items.FARMLAND_SOILS);
        Ingredient dirtSoils = tagIngredient(ATETags.Items.DIRT_SOILS);
        Ingredient sandSoils = tagIngredient(ATETags.Items.SAND_SOILS);
        Ingredient mossSoils = tagIngredient(ATETags.Items.MOSS_SOILS);
        Ingredient waterSoils = tagIngredient(ATETags.Items.WATER_SOILS);
        Ingredient soulSandSoils = tagIngredient(ATETags.Items.SOUL_SAND_SOILS);
        Ingredient mushroomSoils = tagIngredient(ATETags.Items.MUSHROOM_SOILS);
        Ingredient jungleSoils = tagIngredient(ATETags.Items.JUNGLE_SOILS);
        Ingredient stoneSoils = tagIngredient(ATETags.Items.STONE_SOILS);
        Ingredient endSoils = tagIngredient(ATETags.Items.END_SOILS);

        saveCropRecipe("wheat", Items.WHEAT_SEEDS, List.of(farmlandSoils),
                drop(Items.WHEAT, 1, 1),
                drop(Items.WHEAT_SEEDS, 1, 2, 0.5f));

        saveCropRecipe("beetroot", Items.BEETROOT_SEEDS, List.of(farmlandSoils),
                drop(Items.BEETROOT, 1, 1),
                drop(Items.BEETROOT_SEEDS, 1, 2, 0.5f));

        saveCropRecipe("carrot", Items.CARROT, List.of(farmlandSoils),
                drop(Items.CARROT, 2, 5));

        saveCropRecipe("potato", Items.POTATO, List.of(farmlandSoils),
                drop(Items.POTATO, 2, 5),
                drop(Items.POISONOUS_POTATO, 1, 1, 0.02f));

        saveCropRecipe("melon", Items.MELON_SEEDS, List.of(farmlandSoils),
                drop(Items.MELON_SLICE, 3, 7));

        saveCropRecipe("pumpkin", Items.PUMPKIN_SEEDS, List.of(farmlandSoils),
                drop(Items.PUMPKIN, 1, 1));

        saveCropRecipe("pitcher_pod", Items.PITCHER_POD, List.of(farmlandSoils),
                drop(Items.PITCHER_PLANT, 1, 1));

        saveCropRecipe("torchflower", Items.TORCHFLOWER_SEEDS, List.of(farmlandSoils),
                drop(Items.TORCHFLOWER, 1, 1));

        saveCropRecipe("sweet_berries", Items.SWEET_BERRIES, List.of(dirtSoils),
                drop(Items.SWEET_BERRIES, 2, 4));

        saveCropRecipe("firefly_bush", Items.FIREFLY_BUSH, List.of(dirtSoils),
                drop(Items.FIREFLY_BUSH, 1, 2));

        saveCropRecipe("bamboo", Items.BAMBOO, List.of(dirtSoils),
                drop(Items.BAMBOO, 2, 4));

        saveCropRecipe("sugar_cane", Items.SUGAR_CANE, List.of(dirtSoils, sandSoils),
                drop(Items.SUGAR_CANE, 1, 3));

        saveCropRecipe("cactus", Items.CACTUS, List.of(sandSoils),
                drop(Items.CACTUS, 1, 3),
                drop(Items.CACTUS_FLOWER, 1, 1, 0.25f));

        saveCropRecipe("nether_wart", Items.NETHER_WART, List.of(soulSandSoils),
                drop(Items.NETHER_WART, 1, 3));

        saveCropRecipe("kelp", Items.KELP, List.of(waterSoils),
                drop(Items.KELP, 1, 2));

        saveCropRecipe("lily_pad", Items.LILY_PAD, List.of(waterSoils),
                drop(Items.LILY_PAD, 1, 1));

        saveCropRecipe("sea_pickle", Items.SEA_PICKLE, List.of(waterSoils),
                drop(Items.SEA_PICKLE, 1, 1));

        saveCropRecipe("glow_berries", Items.GLOW_BERRIES, List.of(mossSoils),
                drop(Items.GLOW_BERRIES, 2, 4));

        saveCropRecipe("spore_blossom", Items.SPORE_BLOSSOM, List.of(mossSoils),
                drop(Items.SPORE_BLOSSOM, 1, 1));

        saveCropRecipe("chorus_flower", Items.CHORUS_FLOWER, List.of(endSoils),
                drop(Items.CHORUS_FRUIT, 1, 3),
                drop(Items.CHORUS_FLOWER, 1, 1, 0.02f));

        saveCropRecipe("moss_block", Items.MOSS_BLOCK, List.of(stoneSoils),
                drop(Items.MOSS_BLOCK, 1, 2));

        saveCropRecipe("pale_moss_block", Items.PALE_MOSS_BLOCK, List.of(stoneSoils),
                drop(Items.PALE_MOSS_BLOCK, 1, 2));

        saveCropRecipe("brown_mushroom", Items.BROWN_MUSHROOM, List.of(mushroomSoils),
                drop(Items.BROWN_MUSHROOM, 1, 1));

        saveCropRecipe("red_mushroom", Items.RED_MUSHROOM, List.of(mushroomSoils),
                drop(Items.RED_MUSHROOM, 1, 1));

        saveCropRecipe("cocoa_beans", Items.COCOA_BEANS, List.of(jungleSoils),
                drop(Items.COCOA_BEANS, 1, 3));

        for (Item flower : new Item[]{
                Items.ALLIUM, Items.AZURE_BLUET, Items.BLUE_ORCHID,
                Items.CORNFLOWER, Items.DANDELION, Items.LILY_OF_THE_VALLEY,
                Items.OXEYE_DAISY, Items.POPPY, Items.RED_TULIP,
                Items.ORANGE_TULIP, Items.WHITE_TULIP, Items.PINK_TULIP,
                Items.WITHER_ROSE, Items.LILAC, Items.PEONY,
                Items.ROSE_BUSH, Items.SUNFLOWER,
                Items.CLOSED_EYEBLOSSOM, Items.OPEN_EYEBLOSSOM
        }) {
            String name = BuiltInRegistries.ITEM.getKey(flower).getPath();
            saveCropRecipe(name, flower, List.of(dirtSoils), drop(flower, 1, 1));
        }
    }

    private void generateTreeRecipes() {
        Ingredient treeSoils = tagIngredient(ATETags.Items.TREE_SOILS);
        Ingredient netherSoils = tagIngredient(ATETags.Items.NETHER_SOILS);

        saveTreeRecipe("oak", Items.OAK_SAPLING, List.of(treeSoils),
                drop(Items.OAK_LOG, 2, 6),
                drop(Items.OAK_SAPLING, 1, 2, 0.5f),
                drop(Items.STICK, 1, 2, 0.5f),
                drop(Items.APPLE, 1, 1, 0.4f));

        saveTreeRecipe("birch", Items.BIRCH_SAPLING, List.of(treeSoils),
                drop(Items.BIRCH_LOG, 2, 6),
                drop(Items.BIRCH_SAPLING, 1, 2, 0.5f),
                drop(Items.STICK, 1, 2, 0.5f));

        saveTreeRecipe("spruce", Items.SPRUCE_SAPLING, List.of(treeSoils),
                drop(Items.SPRUCE_LOG, 4, 8),
                drop(Items.SPRUCE_SAPLING, 1, 2, 0.5f),
                drop(Items.STICK, 1, 2, 0.5f));

        saveTreeRecipe("jungle", Items.JUNGLE_SAPLING, List.of(treeSoils),
                drop(Items.JUNGLE_LOG, 2, 6),
                drop(Items.JUNGLE_SAPLING, 1, 2, 0.4f),
                drop(Items.STICK, 1, 2, 0.5f),
                drop(Items.COCOA_BEANS, 1, 2, 0.2f));

        saveTreeRecipe("acacia", Items.ACACIA_SAPLING, List.of(treeSoils),
                drop(Items.ACACIA_LOG, 2, 6),
                drop(Items.ACACIA_SAPLING, 1, 2, 0.5f),
                drop(Items.STICK, 1, 2, 0.5f));

        saveTreeRecipe("dark_oak", Items.DARK_OAK_SAPLING, List.of(treeSoils),
                drop(Items.DARK_OAK_LOG, 4, 8),
                drop(Items.DARK_OAK_SAPLING, 1, 2, 0.5f),
                drop(Items.STICK, 1, 2, 0.5f),
                drop(Items.APPLE, 1, 2, 0.3f));

        saveTreeRecipe("cherry", Items.CHERRY_SAPLING, List.of(treeSoils),
                drop(Items.CHERRY_LOG, 2, 6),
                drop(Items.CHERRY_SAPLING, 1, 2, 0.5f),
                drop(Items.STICK, 1, 2, 0.5f));

        saveTreeRecipe("pale_oak", Items.PALE_OAK_SAPLING, List.of(treeSoils),
                drop(Items.PALE_OAK_LOG, 4, 8),
                drop(Items.PALE_OAK_SAPLING, 1, 2, 0.5f),
                drop(Items.STICK, 1, 2, 0.5f),
                drop(Items.PALE_HANGING_MOSS, 1, 2, 0.3f));

        saveTreeRecipe("mangrove", Items.MANGROVE_PROPAGULE, List.of(treeSoils),
                drop(Items.MANGROVE_LOG, 2, 6),
                drop(Items.MANGROVE_PROPAGULE, 1, 2, 0.5f),
                drop(Items.STICK, 1, 2, 0.5f),
                drop(Items.MANGROVE_ROOTS, 1, 1, 0.3f));

        saveTreeRecipe("azalea", Items.AZALEA, List.of(treeSoils),
                drop(Items.OAK_LOG, 1, 4),
                drop(Items.AZALEA, 1, 1, 0.3f),
                drop(Items.FLOWERING_AZALEA, 1, 1, 0.1f),
                drop(Items.STICK, 1, 2, 0.5f));

        saveTreeRecipe("flowering_azalea", Items.FLOWERING_AZALEA, List.of(treeSoils),
                drop(Items.OAK_LOG, 1, 4),
                drop(Items.AZALEA, 1, 1, 0.2f),
                drop(Items.FLOWERING_AZALEA, 1, 1, 0.2f),
                drop(Items.STICK, 1, 2, 0.5f));

        saveTreeRecipe("crimson_fungus", Items.CRIMSON_FUNGUS, List.of(netherSoils),
                drop(Items.CRIMSON_STEM, 2, 6),
                drop(Items.NETHER_WART_BLOCK, 2, 6, 1f),
                drop(Items.WEEPING_VINES, 1, 2, 0.5f),
                drop(Items.SHROOMLIGHT, 1, 2, 0.5f));

        saveTreeRecipe("warped_fungus", Items.WARPED_FUNGUS, List.of(netherSoils),
                drop(Items.WARPED_STEM, 2, 6),
                drop(Items.WARPED_WART_BLOCK, 2, 6, 1f),
                drop(Items.TWISTING_VINES, 1, 2, 0.5f),
                drop(Items.SHROOMLIGHT, 1, 2, 0.5f));
    }

    private Ingredient tagIngredient(TagKey<Item> tag) {
        HolderSet<Item> holders = registries.lookupOrThrow(Registries.ITEM).getOrThrow(tag);
        return Ingredient.of(holders);
    }

    private DropEntry drop(Item item, int min, int max) {
        return new DropEntry(item, min, max, 1.0f);
    }

    private DropEntry drop(Item item, int min, int max, float chance) {
        return new DropEntry(item, min, max, chance);
    }

    private void saveCropRecipe(String name, Item seed, List<Ingredient> soils, DropEntry... drops) {
        String namespace = BuiltInRegistries.ITEM.getKey(seed).getNamespace();
        CropRecipe recipe = new CropRecipe(Ingredient.of(seed), soils, List.of(drops));
        ResourceKey<Recipe<?>> key = ResourceKey.create(
                Registries.RECIPE,
                Identifier.fromNamespaceAndPath("agritechevolved", "planter/crop/" + namespace + "/" + name)
        );
        output.accept(key, recipe, null);
    }

    private void saveTreeRecipe(String name, Item sapling, List<Ingredient> soils, DropEntry... drops) {
        String namespace = BuiltInRegistries.ITEM.getKey(sapling).getNamespace();
        TreeRecipe recipe = new TreeRecipe(Ingredient.of(sapling), soils, List.of(drops));
        ResourceKey<Recipe<?>> key = ResourceKey.create(
                Registries.RECIPE,
                Identifier.fromNamespaceAndPath("agritechevolved", "planter/tree/" + namespace + "/" + name)
        );
        output.accept(key, recipe, null);
    }

    private void saveTillingRecipe(String name, Item input, Item result) {
        HolderSet<Item> hoeTag = registries
                .lookupOrThrow(Registries.ITEM)
                .getOrThrow(ItemTags.HOES);

        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(input));

        DurabilityShapelessRecipe recipe = new DurabilityShapelessRecipe(
                CraftingBookCategory.MISC,
                new ItemStackTemplate(result),
                ingredients,
                Ingredient.of(hoeTag),
                1
        );

        ResourceKey<Recipe<?>> key = ResourceKey.create(
                Registries.RECIPE,
                Identifier.fromNamespaceAndPath("agritechevolved", name)
        );
        output.accept(key, recipe, null);
    }
}