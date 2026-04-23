package com.misterd.agritechevolved.blockentity;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.blockentity.custom.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ATEBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, AgritechEvolved.MODID);

    public static final Supplier<BlockEntityType<PlanterBlockEntity>> PLANTER_BLOCK_BE =
            BLOCK_ENTITIES.register("planter_block_be", () -> BlockEntityType.Builder.of(
                    PlanterBlockEntity::new,
                    ATEBlocks.ACACIA_PLANTER.get(),
                    ATEBlocks.BAMBOO_PLANTER.get(),
                    ATEBlocks.BIRCH_PLANTER.get(),
                    ATEBlocks.CHERRY_PLANTER.get(),
                    ATEBlocks.CRIMSON_PLANTER.get(),
                    ATEBlocks.DARK_OAK_PLANTER.get(),
                    ATEBlocks.JUNGLE_PLANTER.get(),
                    ATEBlocks.MANGROVE_PLANTER.get(),
                    ATEBlocks.OAK_PLANTER.get(),
                    ATEBlocks.SPRUCE_PLANTER.get(),
                    ATEBlocks.WARPED_PLANTER.get()
            ).build(null));

    public static final Supplier<BlockEntityType<AdvancedPlanterBlockEntity>> ADVANCED_PLANTER_BLOCK_BE
            = BLOCK_ENTITIES.register("advanced_planter_block_be", () ->  BlockEntityType.Builder.of(
                    AdvancedPlanterBlockEntity::new, ATEBlocks.ADVANCED_PLANTER.get()).build(null));

    public static final Supplier<BlockEntityType<ComposterBlockEntity>> COMPOSTER_BE
            = BLOCK_ENTITIES.register("composter_be", () ->  BlockEntityType.Builder.of(
            ComposterBlockEntity::new, ATEBlocks.COMPOSTER.get()).build(null));

    public static final Supplier<BlockEntityType<CapacitorBlockEntity>> CAPACITOR_BE  =
            BLOCK_ENTITIES.register("capacitor_be", () -> BlockEntityType.Builder.of(
                    CapacitorBlockEntity::new,
                    ATEBlocks.CAPACITOR_TIER_1.get(),
                    ATEBlocks.CAPACITOR_TIER_2.get(),
                    ATEBlocks.CAPACITOR_TIER_3.get()
            ).build(null));

    public static final Supplier<BlockEntityType<BiomassBurnerBlockEntity>> BURNER_BE
            = BLOCK_ENTITIES.register("burner_be", () ->  BlockEntityType.Builder.of(
            BiomassBurnerBlockEntity::new, ATEBlocks.BIOMASS_BURNER.get()).build(null));

    public static final Supplier<BlockEntityType<EnergyConduitBlockEntity>> ENERGY_CONDUIT_BE =
            BLOCK_ENTITIES.register("energy_conduit_be", () -> BlockEntityType.Builder.of(
                    EnergyConduitBlockEntity::new, ATEBlocks.ENERGY_CONDUIT.get()
            ).build(null));


    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PLANTER_BLOCK_BE.get(),
                (blockEntity, direction) -> {
                    if (!(blockEntity instanceof PlanterBlockEntity planter) || direction == null) {
                        return null;
                    }

                    if (direction.getAxis().isHorizontal()) {
                        return planter.getCapabilityHandler();
                    }

                    if (direction == Direction.DOWN) {
                        return planter.getExtractHandler();
                    }

                    return null;
                });
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        eventBus.addListener(ATEBlockEntities::registerCapabilities);
    }
}
