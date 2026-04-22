package com.misterd.agritechevolved.datagen.custom;

import com.misterd.agritechevolved.block.ATEBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import java.util.Set;

public class ATELootTableProvider extends BlockLootSubProvider {
    public ATELootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    protected void generate() {
        dropSelf(ATEBlocks.OAK_PLANTER.get());
        dropSelf(ATEBlocks.ACACIA_PLANTER.get());
        dropSelf(ATEBlocks.BAMBOO_PLANTER.get());
        dropSelf(ATEBlocks.BIRCH_PLANTER.get());
        dropSelf(ATEBlocks.CHERRY_PLANTER.get());
        dropSelf(ATEBlocks.CRIMSON_PLANTER.get());
        dropSelf(ATEBlocks.DARK_OAK_PLANTER.get());
        dropSelf(ATEBlocks.JUNGLE_PLANTER.get());
        dropSelf(ATEBlocks.MANGROVE_PLANTER.get());
        dropSelf(ATEBlocks.SPRUCE_PLANTER.get());
        dropSelf(ATEBlocks.WARPED_PLANTER.get());
        dropSelf(ATEBlocks.PALE_OAK_PLANTER.get());
        dropSelf(ATEBlocks.ADVANCED_PLANTER.get());
        dropSelf(ATEBlocks.COMPOSTER.get());
        dropSelf(ATEBlocks.BIOMASS_BURNER.get());
        dropSelf(ATEBlocks.CAPACITOR_TIER_1.get());
        dropSelf(ATEBlocks.CAPACITOR_TIER_2.get());
        dropSelf(ATEBlocks.CAPACITOR_TIER_3.get());
        dropSelf(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get());
        add(ATEBlocks.INFUSED_FARMLAND.get(),block -> createInfusedFarmlandTable(block, ATEBlocks.MULCH.get().asItem()));
        dropSelf(ATEBlocks.MULCH.get());
    }

    protected LootTable.Builder createInfusedFarmlandTable(Block pBlock, Item pDropItem) {
        return createSilkTouchDispatchTable(
                pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(pDropItem))
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ATEBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
