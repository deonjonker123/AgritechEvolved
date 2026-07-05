package com.misterd.agritechevolved.datagen.custom;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.item.ATEItems;
import com.misterd.agritechevolved.util.ATETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ATEItemTagProvider extends ItemTagsProvider {
    public ATEItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, AgritechEvolved.MODID);
    }

    private static ResourceKey<Item> key(Item item) {
        return item.builtInRegistryHolder().key();
    }

    protected void addTags(HolderLookup.Provider provider) {
        tag(ATETags.Items.BIOMASS)
                .add(key(ATEItems.CRUDE_BIOMASS.get()))
                .add(key(ATEItems.BIOMASS.get()))
                .add(key(ATEItems.COMPACTED_BIOMASS.get()));

        tag(ATETags.Items.ATE_MODULES)
                .add(key(ATEItems.SM_MK1.get()))
                .add(key(ATEItems.SM_MK2.get()))
                .add(key(ATEItems.SM_MK3.get()))
                .add(key(ATEItems.YM_MK1.get()))
                .add(key(ATEItems.YM_MK2.get()))
                .add(key(ATEItems.YM_MK3.get()))
                .add(key(ATEItems.RM_MK1.get()))
                .add(key(ATEItems.RM_MK2.get()))
                .add(key(ATEItems.RM_MK3.get()));

        tag(ATETags.Items.BASIC_PLANTER_ITEMS)
                .add(key(ATEBlocks.ACACIA_PLANTER.asItem()))
                .add(key(ATEBlocks.BAMBOO_PLANTER.asItem()))
                .add(key(ATEBlocks.BIRCH_PLANTER.asItem()))
                .add(key(ATEBlocks.CHERRY_PLANTER.asItem()))
                .add(key(ATEBlocks.CRIMSON_PLANTER.asItem()))
                .add(key(ATEBlocks.DARK_OAK_PLANTER.asItem()))
                .add(key(ATEBlocks.JUNGLE_PLANTER.asItem()))
                .add(key(ATEBlocks.MANGROVE_PLANTER.asItem()))
                .add(key(ATEBlocks.OAK_PLANTER.asItem()))
                .add(key(ATEBlocks.SPRUCE_PLANTER.asItem()))
                .add(key(ATEBlocks.WARPED_PLANTER.asItem()))
                .add(key(ATEBlocks.PALE_OAK_PLANTER.asItem()));

        tag(ATETags.Items.DIRT_LIKE_BLOCK_ITEMS)
                .add(key(Blocks.DIRT.asItem()))
                .add(key(Blocks.PODZOL.asItem()))
                .add(key(Blocks.MYCELIUM.asItem()))
                .add(key(Blocks.COARSE_DIRT.asItem()))
                .add(key(Blocks.ROOTED_DIRT.asItem()))
                .add(key(Blocks.GRASS_BLOCK.asItem()));
    }
}