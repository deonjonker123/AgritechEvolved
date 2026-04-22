package com.misterd.agritechevolved.datagen.custom;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.item.ATEItems;
import com.misterd.agritechevolved.util.ATETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
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
    }
}
