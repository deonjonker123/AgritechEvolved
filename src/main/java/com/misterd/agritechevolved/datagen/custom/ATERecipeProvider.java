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
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.ACACIA_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_acacia_log", has(Items.ACACIA_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.BAMBOO_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.BAMBOO_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_bamboo_block", has(Items.BAMBOO_BLOCK))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.BIRCH_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.BIRCH_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_birch_log", has(Items.BIRCH_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.CHERRY_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.CHERRY_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_cherry_log", has(Items.CHERRY_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.CRIMSON_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.CRIMSON_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_crimson_stem", has(Items.CRIMSON_STEM))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.DARK_OAK_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.DARK_OAK_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_dark_oak_log", has(Items.DARK_OAK_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.JUNGLE_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.JUNGLE_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_jungle_log", has(Items.JUNGLE_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.MANGROVE_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.MANGROVE_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_mangrove_log", has(Items.MANGROVE_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.OAK_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.OAK_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_oak_log", has(Items.OAK_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.OAK_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', ItemTags.PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_oak_log", has(Items.OAK_LOG))
                .save(output, "agritechevolved:zzz_basic_planter_from_any_wood");

        shaped(RecipeCategory.MISC, ATEBlocks.SPRUCE_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.SPRUCE_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_spruce_log", has(Items.SPRUCE_LOG))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.WARPED_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.WARPED_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_warped_stem", has(Items.WARPED_STEM))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.PALE_OAK_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.PALE_OAK_PLANKS)
                .define('H', Items.HOPPER)
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
                .define('C', Items.COPPER_BLOCK)
                .unlockedBy("has_copper_block", has(Items.COPPER_BLOCK))
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
                .define('C', Items.COPPER_BLOCK)
                .unlockedBy("has_wheat_seeds", has(Items.WHEAT_SEEDS))
                .save(output);

        shaped(RecipeCategory.MISC, ATEItems.YM_MK2.get())
                .pattern(" G ")
                .pattern("CYC")
                .pattern(" G ")
                .define('G', Items.GOLD_BLOCK)
                .define('C', Items.COPPER_BLOCK)
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
                .define('C', Items.COPPER_BLOCK)
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

        shaped(RecipeCategory.MISC, ATEBlocks.TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.BLACK_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.BLACK_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.BLUE_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.BLUE_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.BROWN_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.BROWN_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.CYAN_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.CYAN_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.GRAY_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.GRAY_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.GREEN_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.GREEN_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.LIGHT_BLUE_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.LIGHT_BLUE_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.LIGHT_GRAY_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.LIGHT_GRAY_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.LIME_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.LIME_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.MAGENTA_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.MAGENTA_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.ORANGE_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.ORANGE_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.PINK_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.PINK_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.PURPLE_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.PURPLE_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.RED_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.RED_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.WHITE_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.WHITE_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ATEBlocks.YELLOW_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.YELLOW_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(output);

        shapeless(RecipeCategory.MISC, ATEBlocks.BLACK_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.BLACK_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:black_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.BLUE_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.BLUE_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:blue_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.BROWN_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.BROWN_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:brown_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.CYAN_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.CYAN_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:cyan_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.GRAY_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.GRAY_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:gray_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.GREEN_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.GREEN_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:green_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.LIGHT_BLUE_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.LIGHT_BLUE_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:light_blue_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.LIGHT_GRAY_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.LIGHT_GRAY_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:light_gray_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.LIME_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.LIME_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:lime_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.MAGENTA_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.MAGENTA_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:magenta_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.ORANGE_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.ORANGE_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:orange_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.PINK_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.PINK_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:pink_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.PURPLE_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.PURPLE_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:purple_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.RED_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.RED_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:red_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.WHITE_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.WHITE_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:white_terracotta_planter_from_dye");

        shapeless(RecipeCategory.MISC, ATEBlocks.YELLOW_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.YELLOW_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(output, "agritechevolved:yellow_terracotta_planter_from_dye");

        saveTillingRecipe("dirt_to_farmland", Items.DIRT, Items.FARMLAND);
        saveTillingRecipe("rooted_dirt_to_farmland", Items.ROOTED_DIRT, Items.FARMLAND);
        saveTillingRecipe("coarse_dirt_to_farmland", Items.COARSE_DIRT, Items.FARMLAND);
        saveTillingRecipe("grass_to_farmland", Items.GRASS_BLOCK, Items.FARMLAND);
        saveTillingRecipe("mulch_to_infused_farmland", ATEBlocks.MULCH.get().asItem(), ATEBlocks.INFUSED_FARMLAND.get().asItem());
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