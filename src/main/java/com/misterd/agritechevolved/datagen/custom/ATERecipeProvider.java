package com.misterd.agritechevolved.datagen.custom;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.item.ATEItems;
import com.misterd.agritechevolved.util.ATETags;
import com.misterd.agritechevolved.util.DurabilityShapelessRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ATERecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ATERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.ACACIA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.ACACIA_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_acaia_log", has(Items.ACACIA_LOG))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.BAMBOO_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.BAMBOO_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_bamboo_block", has(Items.BAMBOO_BLOCK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.BIRCH_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.BIRCH_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_birch_log", has(Items.BIRCH_LOG))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.CHERRY_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.CHERRY_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_cherry_log", has(Items.CHERRY_LOG))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.CRIMSON_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.CRIMSON_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_crimson_stem", has(Items.CRIMSON_STEM))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.DARK_OAK_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.DARK_OAK_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_dark_oak_log", has(Items.DARK_OAK_LOG))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.JUNGLE_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.JUNGLE_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_jungle_log", has(Items.JUNGLE_LOG))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.MANGROVE_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.MANGROVE_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_mangrove_log", has(Items.MANGROVE_LOG))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.OAK_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.OAK_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_oak_log", has(Items.OAK_LOG))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.OAK_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', ItemTags.PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_oak_log", has(Items.OAK_LOG))
                .save(recipeOutput, "agritechevolved:basic_planter_from_any_wood");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.SPRUCE_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.SPRUCE_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_spruce_log", has(Items.SPRUCE_LOG))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.WARPED_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.WARPED_PLANKS)
                .define('H', Items.HOPPER)
                .unlockedBy("has_warped_stem", has(Items.WARPED_STEM))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.CLOCHE.get(), 4)
                .pattern("III")
                .pattern("IPI")
                .pattern("III")
                .define('P', Tags.Items.GLASS_BLOCKS)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.ADVANCED_PLANTER.get())
                .pattern("F F")
                .pattern("IAI")
                .pattern("RIR")
                .define('F', Items.IRON_INGOT)
                .define('I', Items.IRON_BLOCK)
                .define('A', ATETags.Items.BASIC_PLANTER_ITEMS)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_basic_planter", has(ATEBlocks.OAK_PLANTER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.COMPOSTER.get())
                .pattern("I I")
                .pattern("ICI")
                .pattern("IRI")
                .define('C', Items.COMPOSTER)
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_composter", has(Items.COMPOSTER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.BIOMASS_BURNER.get())
                .pattern("III")
                .pattern("IFI")
                .pattern("IRI")
                .define('I', Items.IRON_INGOT)
                .define('F', Items.FURNACE)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_furnace", has(Items.FURNACE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.CAPACITOR_TIER_1.get())
                .pattern("RRR")
                .pattern("ICI")
                .pattern("RRR")
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_BLOCK)
                .unlockedBy("has_copper_block", has(Items.COPPER_BLOCK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.CAPACITOR_TIER_2.get())
                .pattern("RRR")
                .pattern("GCG")
                .pattern("RRR")
                .define('R', Items.REDSTONE_BLOCK)
                .define('G', Items.GOLD_INGOT)
                .define('C', ATEBlocks.CAPACITOR_TIER_1.get())
                .unlockedBy("has_capacitor_tier1", has(ATEBlocks.CAPACITOR_TIER_1.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.CAPACITOR_TIER_3.get())
                .pattern("DDD")
                .pattern("ECE")
                .pattern("DDD")
                .define('D', Items.DIAMOND)
                .define('E', Items.EMERALD)
                .define('C', ATEBlocks.CAPACITOR_TIER_2.get())
                .unlockedBy("has_capacitor_tier2", has(ATEBlocks.CAPACITOR_TIER_2.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.SM_MK1.get())
                .pattern(" R ")
                .pattern("IGI")
                .pattern(" R ")
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_INGOT)
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.SM_MK2.get())
                .pattern(" D ")
                .pattern("GSG")
                .pattern(" D ")
                .define('D', Items.DIAMOND)
                .define('G', Items.GOLD_BLOCK)
                .define('S', ATEItems.SM_MK1.get())
                .unlockedBy("has_sm_mk1", has(ATEItems.SM_MK1.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.SM_MK3.get())
                .pattern("ENE")
                .pattern("DSD")
                .pattern("ENE")
                .define('N', Items.NETHERITE_INGOT)
                .define('D', Items.DIAMOND_BLOCK)
                .define('E', Items.EMERALD_BLOCK)
                .define('S', ATEItems.SM_MK2.get())
                .unlockedBy("has_sm_mk2", has(ATEItems.SM_MK2.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.YM_MK1.get())
                .pattern(" W ")
                .pattern("SCS")
                .pattern(" W ")
                .define('W', Items.WHEAT)
                .define('S', Items.WHEAT_SEEDS)
                .define('C', Items.COPPER_BLOCK)
                .unlockedBy("has_farmland", has(Items.FARMLAND))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.YM_MK2.get())
                .pattern(" G ")
                .pattern("CYC")
                .pattern(" G ")
                .define('G', Items.GOLD_BLOCK)
                .define('C', Items.COPPER_BLOCK)
                .define('Y', ATEItems.YM_MK1.get())
                .unlockedBy("has_ym_mk1", has(ATEItems.YM_MK1.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.YM_MK3.get())
                .pattern("ENE")
                .pattern("GYG")
                .pattern("ENE")
                .define('E', Items.ENCHANTED_GOLDEN_APPLE)
                .define('G', Items.GOLD_BLOCK)
                .define('N', Items.NETHERITE_INGOT)
                .define('Y', ATEItems.YM_MK2.get())
                .unlockedBy("has_ym_mk2", has(ATEItems.YM_MK2.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.COMPACTED_BIOMASS.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ATEItems.BIOMASS.get())
                .unlockedBy("has_biomass", has(ATEItems.BIOMASS.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEItems.BIOMASS.get(), 9)
                .requires(ATEItems.COMPACTED_BIOMASS.get())
                .unlockedBy("has_compacted_biomass", has(ATEItems.COMPACTED_BIOMASS.get()))
                .save(recipeOutput, "biomass_from_compacted");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ATEBlocks.COMPACTED_BIOMASS_BLOCK.get())
                .pattern("CCC")
                .pattern("CCC")
                .pattern("CCC")
                .define('C', ATEItems.COMPACTED_BIOMASS.get())
                .unlockedBy("has_compacted_biomass", has(ATEItems.COMPACTED_BIOMASS.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEItems.COMPACTED_BIOMASS.get(), 9)
                .requires(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get())
                .unlockedBy("has_compacted_biomass_block", has(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get()))
                .save(recipeOutput, "compacted_biomass_from_block");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ATEBlocks.MULCH.get(), 2)
                .pattern("BBB")
                .pattern("BFB")
                .pattern("BBB")
                .define('B', ATEItems.COMPACTED_BIOMASS.get())
                .define('F', Items.FARMLAND)
                .unlockedBy("has_farmland", has(Items.FARMLAND))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.CRUDE_BIOMASS)
                .pattern("LLL")
                .pattern("DDD")
                .pattern("LLL")
                .define('L', ItemTags.LEAVES)
                .define('D', ATETags.Items.DIRT_LIKE_BLOCK_ITEMS)
                .unlockedBy("has_leaves", has(ItemTags.LEAVES))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.BLACK_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.BLACK_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.BLUE_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.BLUE_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.BROWN_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.BROWN_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.CYAN_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.CYAN_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.GRAY_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.GRAY_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.GREEN_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.GREEN_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.LIGHT_BLUE_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.LIGHT_BLUE_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.LIGHT_GRAY_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.LIGHT_GRAY_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.LIME_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.LIME_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.MAGENTA_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.MAGENTA_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.ORANGE_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.ORANGE_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.PINK_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.PINK_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.PURPLE_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.PURPLE_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.RED_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.RED_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.WHITE_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.WHITE_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.YELLOW_TERRACOTTA_PLANTER.get())
                .pattern("PHP")
                .pattern("PPP")
                .define('P', Items.YELLOW_TERRACOTTA)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.BLACK_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.BLACK_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:black_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.BLUE_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.BLUE_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:blue_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.BROWN_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.BROWN_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:brown_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.CYAN_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.CYAN_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:cyan_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.GRAY_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.GRAY_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:gray_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.GREEN_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.GREEN_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:green_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.LIGHT_BLUE_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.LIGHT_BLUE_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:light_blue_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.LIGHT_GRAY_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.LIGHT_GRAY_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:light_gray_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.LIME_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.LIME_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:lime_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.MAGENTA_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.MAGENTA_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:magenta_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.ORANGE_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.ORANGE_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:orange_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.PINK_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.PINK_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:pink_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.PURPLE_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.PURPLE_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:purple_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.RED_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.RED_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:red_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.WHITE_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.WHITE_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:white_terracotta_planter_from_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEBlocks.YELLOW_TERRACOTTA_PLANTER.get())
                .requires(ATEBlocks.TERRACOTTA_PLANTER.get())
                .requires(Items.YELLOW_DYE)
                .unlockedBy("has_terracotta_planter", has(ATEBlocks.TERRACOTTA_PLANTER.get()))
                .save(recipeOutput, "agritechevolved:yellow_terracotta_planter_from_dye");

        buildDurabilityRecipes(recipeOutput);
    }

    public void buildDurabilityRecipes(RecipeOutput recipeOutput) {
        Ingredient hoes = Ingredient.of(Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE);

        DurabilityShapelessRecipeBuilder.shapeless(Items.FARMLAND)
                .requires(ATETags.Items.DIRT_LIKE_BLOCK_ITEMS)
                .tool(hoes)
                .durabilityPerItem(1)
                .group("farmland_from_dirt_like_blocks")
                .unlockedBy("has_dirt", has(Items.DIRT))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("agritechevolved", "farmland_from_dirt_like"));

        DurabilityShapelessRecipeBuilder.shapeless(ATEBlocks.INFUSED_FARMLAND)
                .requires(ATEBlocks.MULCH)
                .tool(hoes)
                .durabilityPerItem(1)
                .group("infused_farmland")
                .unlockedBy("has_mulch", has(ATEBlocks.MULCH))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("agritechevolved", "infused_farmland_from_mulch"));
    }
}
