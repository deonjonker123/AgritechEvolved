package com.misterd.agritechevolved.datagen.custom;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.item.ATEItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ATEItemModelProvider extends ItemModelProvider {
    public ATEItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AgritechEvolved.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ATEItems.SM_MK1.get());
        basicItem(ATEItems.SM_MK2.get());
        basicItem(ATEItems.SM_MK3.get());
        basicItem(ATEItems.YM_MK1.get());
        basicItem(ATEItems.YM_MK2.get());
        basicItem(ATEItems.YM_MK3.get());
        basicItem(ATEItems.CRUDE_BIOMASS.get());
        basicItem(ATEItems.BIOMASS.get());
        basicItem(ATEItems.COMPACTED_BIOMASS.get());
    }
}