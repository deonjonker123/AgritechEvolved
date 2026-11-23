package com.misterd.agritechevolved.item;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.block.ATEBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ATECreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AgritechEvolved.MODID);

    public static final RegistryObject<CreativeModeTab> AGRITECH_EVOLVED = CREATIVE_MODE_TAB.register("agritechevolved_creativetab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ATEItems.BIOMASS.get()))
                    .title(Component.translatable("creativetab.agritechevolved"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ATEBlocks.COMPOSTER.get());

                        output.accept(ATEBlocks.BIOMASS_BURNER.get());
                        output.accept(ATEBlocks.CAPACITOR_TIER_1.get());
                        output.accept(ATEBlocks.CAPACITOR_TIER_2.get());
                        output.accept(ATEBlocks.CAPACITOR_TIER_3.get());

                        output.accept(ATEBlocks.MULCH.get());
                        output.accept(ATEBlocks.INFUSED_FARMLAND.get());
                        output.accept(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get());

                        output.accept(ATEItems.CRUDE_BIOMASS.get());
                        output.accept(ATEItems.BIOMASS.get());
                        output.accept(ATEItems.COMPACTED_BIOMASS.get());
                        output.accept(ATEItems.YM_MK1.get());
                        output.accept(ATEItems.YM_MK2.get());
                        output.accept(ATEItems.YM_MK3.get());
                        output.accept(ATEItems.SM_MK1.get());
                        output.accept(ATEItems.SM_MK2.get());
                        output.accept(ATEItems.SM_MK3.get());
                        output.accept(ATEItems.CLOCHE.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}