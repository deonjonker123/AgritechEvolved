package com.misterd.agritechevolved.util;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

public class RegistryHelper {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static String getItemId(Item item) {
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item);
        return registryName == null ? "minecraft:air" : registryName.toString();
    }

    public static String getItemId(ItemStack stack) {
        return getItemId(stack.getItem());
    }

    public static String getBlockId(Block block) {
        ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block);
        return registryName == null ? "minecraft:air" : registryName.toString();
    }

    public static Item getItem(String id) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(id);
        if (resourceLocation == null) {
            LOGGER.error("Invalid item ID in config: {}", id);
            return null;
        }
        return ForgeRegistries.ITEMS.containsKey(resourceLocation) ? ForgeRegistries.ITEMS.getValue(resourceLocation) : null;
    }

    public static Block getBlock(String id) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(id);
        if (resourceLocation == null) {
            LOGGER.error("Invalid block ID in config: {}", id);
            return null;
        }
        return ForgeRegistries.BLOCKS.containsKey(resourceLocation) ? ForgeRegistries.BLOCKS.getValue(resourceLocation) : null;
    }
}