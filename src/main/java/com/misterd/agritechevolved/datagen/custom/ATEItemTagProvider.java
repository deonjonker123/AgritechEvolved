package com.misterd.agritechevolved.datagen.custom;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.item.ATEItems;
import com.misterd.agritechevolved.util.ATETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ATEItemTagProvider extends ItemTagsProvider {
    public ATEItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, AgritechEvolved.MODID);
    }

    protected void addTags(HolderLookup.Provider provider) {
        tag(ATETags.Items.BIOMASS)
                .add(ATEItems.CRUDE_BIOMASS.get())
                .add(ATEItems.BIOMASS.get())
                .add(ATEItems.COMPACTED_BIOMASS.get());

        tag(ATETags.Items.ATE_MODULES)
                .add(ATEItems.SM_MK1.get())
                .add(ATEItems.SM_MK2.get())
                .add(ATEItems.SM_MK3.get())
                .add(ATEItems.YM_MK1.get())
                .add(ATEItems.YM_MK2.get())
                .add(ATEItems.YM_MK3.get());

        tag(ATETags.Items.BASIC_PLANTER_ITEMS)
                .add(ATEBlocks.ACACIA_PLANTER.asItem())
                .add(ATEBlocks.BAMBOO_PLANTER.asItem())
                .add(ATEBlocks.BIRCH_PLANTER.asItem())
                .add(ATEBlocks.CHERRY_PLANTER.asItem())
                .add(ATEBlocks.CRIMSON_PLANTER.asItem())
                .add(ATEBlocks.DARK_OAK_PLANTER.asItem())
                .add(ATEBlocks.JUNGLE_PLANTER.asItem())
                .add(ATEBlocks.MANGROVE_PLANTER.asItem())
                .add(ATEBlocks.OAK_PLANTER.asItem())
                .add(ATEBlocks.SPRUCE_PLANTER.asItem())
                .add(ATEBlocks.WARPED_PLANTER.asItem())
                .add(ATEBlocks.PALE_OAK_PLANTER.asItem());

        tag(ATETags.Items.DIRT_LIKE_BLOCK_ITEMS)
                .add(Blocks.DIRT.asItem())
                .add(Blocks.PODZOL.asItem())
                .add(Blocks.MYCELIUM.asItem())
                .add(Blocks.COARSE_DIRT.asItem())
                .add(Blocks.ROOTED_DIRT.asItem())
                .add(Blocks.GRASS_BLOCK.asItem());

        tag(ATETags.Items.FARMLAND_SOILS)
                .add(Blocks.FARMLAND.asItem())
                .add(ATEBlocks.INFUSED_FARMLAND.asItem());

        tag(ATETags.Items.DIRT_SOILS)
                .add(Blocks.GRASS_BLOCK.asItem())
                .add(Blocks.DIRT.asItem())
                .add(Blocks.COARSE_DIRT.asItem())
                .add(Blocks.ROOTED_DIRT.asItem())
                .add(Blocks.MYCELIUM.asItem())
                .add(Blocks.PODZOL.asItem())
                .add(Blocks.MUD.asItem())
                .add(Blocks.MUDDY_MANGROVE_ROOTS.asItem())
                .add(ATEBlocks.MULCH.asItem());

        tag(ATETags.Items.TREE_SOILS)
                .add(Blocks.FARMLAND.asItem())
                .add(Blocks.GRASS_BLOCK.asItem())
                .add(Blocks.DIRT.asItem())
                .add(Blocks.COARSE_DIRT.asItem())
                .add(Blocks.ROOTED_DIRT.asItem())
                .add(Blocks.MYCELIUM.asItem())
                .add(Blocks.PODZOL.asItem())
                .add(Blocks.MUD.asItem())
                .add(Blocks.MUDDY_MANGROVE_ROOTS.asItem())
                .add(Blocks.MOSS_BLOCK.asItem())
                .add(Blocks.PALE_MOSS_BLOCK.asItem())
                .add(ATEBlocks.MULCH.asItem())
                .add(ATEBlocks.INFUSED_FARMLAND.asItem());

        tag(ATETags.Items.SAND_SOILS)
                .add(Blocks.SAND.asItem())
                .add(Blocks.RED_SAND.asItem());

        tag(ATETags.Items.SOUL_SAND_SOILS)
                .add(Blocks.SOUL_SAND.asItem())
                .add(Blocks.SOUL_SOIL.asItem());

        tag(ATETags.Items.MOSS_SOILS)
                .add(Blocks.MOSS_BLOCK.asItem())
                .add(Blocks.PALE_MOSS_BLOCK.asItem());

        tag(ATETags.Items.WATER_SOILS)
                .add(Items.WATER_BUCKET);

        tag(ATETags.Items.MUSHROOM_SOILS)
                .add(Blocks.MYCELIUM.asItem())
                .add(Blocks.PODZOL.asItem());

        tag(ATETags.Items.NETHER_SOILS)
                .add(Blocks.CRIMSON_NYLIUM.asItem())
                .add(Blocks.WARPED_NYLIUM.asItem());

        tag(ATETags.Items.JUNGLE_SOILS)
                .add(Blocks.JUNGLE_LOG.asItem())
                .add(Blocks.JUNGLE_WOOD.asItem())
                .add(Blocks.STRIPPED_JUNGLE_LOG.asItem())
                .add(Blocks.STRIPPED_JUNGLE_WOOD.asItem());

        tag(ATETags.Items.STONE_SOILS)
                .add(Blocks.STONE.asItem())
                .add(Blocks.END_STONE.asItem());

        tag(ATETags.Items.END_SOILS)
                .add(Blocks.END_STONE.asItem())
                .add(Blocks.END_STONE_BRICKS.asItem());
    }
}
