package com.misterd.agritechevolved.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class CropRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient seed;
    private final List<Ingredient> soils;
    private final List<DropEntry> drops;

    public CropRecipe(Ingredient seed, List<Ingredient> soils, List<DropEntry> drops) {
        this.seed = seed;
        this.soils = soils;
        this.drops = drops;
    }

    public Ingredient getSeed() { return seed; }
    public List<Ingredient> getSoils() { return soils; }
    public List<DropEntry> getDrops() { return drops; }

    public boolean matchesSeed(ItemStack stack) {
        return seed.test(stack);
    }

    public boolean matchesSoil(ItemStack stack) {
        for (Ingredient soil : soils) {
            if (soil.test(stack)) return true;
        }
        return false;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return seed.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, net.minecraft.core.HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<CropRecipe> getSerializer() {
        return ATERecipeTypes.CROP_SERIALIZER.get();
    }

    @Override
    public RecipeType<CropRecipe> getType() {
        return ATERecipeTypes.CROP_TYPE.get();
    }

    public static final MapCodec<CropRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("seed").forGetter(CropRecipe::getSeed),
            Ingredient.CODEC.listOf().fieldOf("soils").forGetter(CropRecipe::getSoils),
            DropEntry.CODEC.listOf().fieldOf("drops").forGetter(CropRecipe::getDrops)
    ).apply(instance, CropRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CropRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, CropRecipe::getSeed,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), CropRecipe::getSoils,
            DropEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), CropRecipe::getDrops,
            CropRecipe::new
    );
}