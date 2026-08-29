package com.misterd.agritechevolved.datagen.custom;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.util.ATETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ATEBlockTagProvider extends BlockTagsProvider {
    public ATEBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, AgritechEvolved.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ATEBlocks.ACACIA_PLANTER.get())
                .add(ATEBlocks.BAMBOO_PLANTER.get())
                .add(ATEBlocks.BIRCH_PLANTER.get())
                .add(ATEBlocks.CHERRY_PLANTER.get())
                .add(ATEBlocks.CRIMSON_PLANTER.get())
                .add(ATEBlocks.DARK_OAK_PLANTER.get())
                .add(ATEBlocks.JUNGLE_PLANTER.get())
                .add(ATEBlocks.MANGROVE_PLANTER.get())
                .add(ATEBlocks.OAK_PLANTER.get())
                .add(ATEBlocks.SPRUCE_PLANTER.get())
                .add(ATEBlocks.WARPED_PLANTER.get())
                .add(ATEBlocks.PALE_OAK_PLANTER.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ATEBlocks.ADVANCED_PLANTER.get())
                .add(ATEBlocks.BIOMASS_BURNER.get())
                .add(ATEBlocks.COMPOSTER.get())
                .add(ATEBlocks.CAPACITOR_TIER_1.get())
                .add(ATEBlocks.CAPACITOR_TIER_2.get())
                .add(ATEBlocks.CAPACITOR_TIER_3.get())
                .add(ATEBlocks.SILO.get())
                .add(ATEBlocks.FERT_SPREADER.get())

                .add(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get())

                .add(ATEBlocks.TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.BLACK_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.BLUE_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.BROWN_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.CYAN_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.GRAY_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.GREEN_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.LIGHT_BLUE_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.LIGHT_GRAY_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.LIME_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.MAGENTA_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.ORANGE_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.PINK_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.PURPLE_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.RED_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.WHITE_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.YELLOW_TERRACOTTA_PLANTER.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ATEBlocks.INFUSED_FARMLAND.get())
                .add(ATEBlocks.MULCH.get());

        tag(ATETags.Blocks.BASIC_PLANTERS)
                .add(ATEBlocks.ACACIA_PLANTER.get())
                .add(ATEBlocks.BAMBOO_PLANTER.get())
                .add(ATEBlocks.BIRCH_PLANTER.get())
                .add(ATEBlocks.CHERRY_PLANTER.get())
                .add(ATEBlocks.CRIMSON_PLANTER.get())
                .add(ATEBlocks.DARK_OAK_PLANTER.get())
                .add(ATEBlocks.JUNGLE_PLANTER.get())
                .add(ATEBlocks.MANGROVE_PLANTER.get())
                .add(ATEBlocks.OAK_PLANTER.get())
                .add(ATEBlocks.SPRUCE_PLANTER.get())
                .add(ATEBlocks.WARPED_PLANTER.get())
                .add(ATEBlocks.PALE_OAK_PLANTER.get())
                .add(ATEBlocks.TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.BLACK_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.BLUE_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.BROWN_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.CYAN_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.GRAY_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.GREEN_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.LIGHT_BLUE_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.LIGHT_GRAY_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.LIME_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.MAGENTA_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.ORANGE_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.PINK_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.PURPLE_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.RED_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.WHITE_TERRACOTTA_PLANTER.get())
                .add(ATEBlocks.YELLOW_TERRACOTTA_PLANTER.get());
    }
}
