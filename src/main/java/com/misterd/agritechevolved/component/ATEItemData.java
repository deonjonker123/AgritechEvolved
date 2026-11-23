package com.misterd.agritechevolved.component;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public final class ATEItemData {
    public static final String STORED_ENERGY = "StoredEnergy";
    public static final String CAPACITOR_TIER = "CapacitorTier";

    private ATEItemData() {}

    public static int getStoredEnergy(ItemStack stack) {
        return getInt(stack, STORED_ENERGY);
    }

    public static void setStoredEnergy(ItemStack stack, int energy) {
        stack.getOrCreateTag().putInt(STORED_ENERGY, energy);
    }

    public static boolean hasStoredEnergy(ItemStack stack) {
        return has(stack, STORED_ENERGY);
    }

    public static int getCapacitorTier(ItemStack stack) {
        return getInt(stack, CAPACITOR_TIER);
    }

    public static void setCapacitorTier(ItemStack stack, int tier) {
        stack.getOrCreateTag().putInt(CAPACITOR_TIER, tier);
    }

    public static boolean hasCapacitorTier(ItemStack stack) {
        return has(stack, CAPACITOR_TIER);
    }

    private static boolean has(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(key, Tag.TAG_INT);
    }

    private static int getInt(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(key, Tag.TAG_INT) ? tag.getInt(key) : 0;
    }
}