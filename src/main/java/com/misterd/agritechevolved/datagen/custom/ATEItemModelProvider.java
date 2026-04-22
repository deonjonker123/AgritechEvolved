package com.misterd.agritechevolved.datagen.custom;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.item.ATEItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public class ATEItemModelProvider extends ModelProvider {
    public ATEItemModelProvider(PackOutput output) {
        super(output, AgritechEvolved.MODID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ATEItems.SM_MK1.get(),            ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ATEItems.SM_MK2.get(),            ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ATEItems.SM_MK3.get(),            ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ATEItems.YM_MK1.get(),            ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ATEItems.YM_MK2.get(),            ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ATEItems.YM_MK3.get(),            ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ATEItems.CRUDE_BIOMASS.get(),     ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ATEItems.BIOMASS.get(),           ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ATEItems.COMPACTED_BIOMASS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ATEItems.CLOCHE.get(),            ModelTemplates.FLAT_ITEM);
    }
}