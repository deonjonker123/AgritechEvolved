package com.misterd.agritechevolved.util;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ATETags {
    public static class Items {
        public static final TagKey<Item> BIOMASS = createTag("biomass");
        public static final TagKey<Item> DIRT_LIKE_BLOCK_ITEMS = createTag("dirt_like_block_items");
        public static final TagKey<Item> BASIC_PLANTER_ITEMS = createTag("basic_planter_items");
        public static final TagKey<Item> ATE_MODULES = createTag("agritechevolved_modules");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(Identifier.fromNamespaceAndPath("agritechevolved", name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> BASIC_PLANTERS = createTag("basic_planters");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath("agritechevolved", name));
        }
    }
}
