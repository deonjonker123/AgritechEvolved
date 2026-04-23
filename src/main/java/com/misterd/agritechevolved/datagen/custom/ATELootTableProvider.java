package com.misterd.agritechevolved.datagen.custom;

import com.misterd.agritechevolved.block.ATEBlocks;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
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
        dropSelf(ATEBlocks.ADVANCED_PLANTER.get());
        dropSelf(ATEBlocks.COMPOSTER.get());
        dropSelf(ATEBlocks.BIOMASS_BURNER.get());
        dropSelf(ATEBlocks.CAPACITOR_TIER_1.get());
        dropSelf(ATEBlocks.CAPACITOR_TIER_2.get());
        dropSelf(ATEBlocks.CAPACITOR_TIER_3.get());
        dropSelf(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get());
        add(ATEBlocks.INFUSED_FARMLAND.get(), createInfusedFarmlandTable());
        dropSelf(ATEBlocks.MULCH.get());
        dropSelf(ATEBlocks.ENERGY_CONDUIT.get());
    }

    private LootTable.Builder createInfusedFarmlandTable() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ATEBlocks.INFUSED_FARMLAND.get())
                                .when(MatchTool.toolMatches(
                                        ItemPredicate.Builder.item().withSubPredicate(
                                                ItemSubPredicates.ENCHANTMENTS,
                                                ItemEnchantmentsPredicate.enchantments(List.of(
                                                        new EnchantmentPredicate(registries.lookupOrThrow(Registries.ENCHANTMENT)
                                                                .getOrThrow(Enchantments.SILK_TOUCH),
                                                                MinMaxBounds.Ints.atLeast(1)
                                                        )
                                                ))
                                        )
                                ))
                                .otherwise(LootItem.lootTableItem(ATEBlocks.MULCH.get()))
                        )
                        .when(ExplosionCondition.survivesExplosion())
                );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ATEBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
