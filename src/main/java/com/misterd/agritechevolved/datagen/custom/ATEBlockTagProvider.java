package com.misterd.agritechevolved.datagen.custom;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.util.ATETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ATEBlockTagProvider extends BlockTagsProvider {
    public ATEBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AgritechEvolved.MODID, existingFileHelper);
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
                .add(ATEBlocks.WARPED_PLANTER.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ATEBlocks.ADVANCED_PLANTER.get())
                .add(ATEBlocks.BIOMASS_BURNER.get())
                .add(ATEBlocks.COMPOSTER.get())
                .add(ATEBlocks.CAPACITOR_TIER_1.get())
                .add(ATEBlocks.CAPACITOR_TIER_2.get())
                .add(ATEBlocks.CAPACITOR_TIER_3.get())

                .add(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get());

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
                .add(ATEBlocks.WARPED_PLANTER.get());

        tag(ATETags.Blocks.DIRT_LIKE_BLOCKS)
                .add(Blocks.DIRT)
                .add(Blocks.PODZOL)
                .add(Blocks.MYCELIUM)
                .add(Blocks.COARSE_DIRT)
                .add(Blocks.ROOTED_DIRT)
                .add(Blocks.GRASS_BLOCK);
    }
}
