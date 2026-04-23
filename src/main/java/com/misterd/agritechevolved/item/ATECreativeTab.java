package com.misterd.agritechevolved.item;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.block.ATEBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ATECreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AgritechEvolved.MODID);

    public static final Supplier<CreativeModeTab> AGRITECH_EVOLVED = CREATIVE_MODE_TAB.register("agritechevolved_creativetab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ATEBlocks.ADVANCED_PLANTER.get()))
                    .title(Component.translatable("creativetab.agritechevolved"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ATEBlocks.ACACIA_PLANTER);
                        output.accept(ATEBlocks.BAMBOO_PLANTER);
                        output.accept(ATEBlocks.BIRCH_PLANTER);
                        output.accept(ATEBlocks.CHERRY_PLANTER);
                        output.accept(ATEBlocks.CRIMSON_PLANTER);
                        output.accept(ATEBlocks.DARK_OAK_PLANTER);
                        output.accept(ATEBlocks.JUNGLE_PLANTER);
                        output.accept(ATEBlocks.MANGROVE_PLANTER);
                        output.accept(ATEBlocks.OAK_PLANTER);
                        output.accept(ATEBlocks.SPRUCE_PLANTER);
                        output.accept(ATEBlocks.WARPED_PLANTER);

                        output.accept(ATEBlocks.ADVANCED_PLANTER);

                        output.accept(ATEBlocks.COMPOSTER);

                        output.accept(ATEBlocks.BIOMASS_BURNER);
                        output.accept(ATEBlocks.CAPACITOR_TIER_1);
                        output.accept(ATEBlocks.CAPACITOR_TIER_2);
                        output.accept(ATEBlocks.CAPACITOR_TIER_3);
                        output.accept(ATEBlocks.ENERGY_CONDUIT);

                        output.accept(ATEBlocks.MULCH);
                        output.accept(ATEBlocks.INFUSED_FARMLAND);
                        output.accept(ATEBlocks.COMPACTED_BIOMASS_BLOCK);

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
