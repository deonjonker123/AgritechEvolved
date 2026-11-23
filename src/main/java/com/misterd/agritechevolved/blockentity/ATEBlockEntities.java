package com.misterd.agritechevolved.blockentity;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.blockentity.custom.BiomassBurnerBlockEntity;
import com.misterd.agritechevolved.blockentity.custom.CapacitorBlockEntity;
import com.misterd.agritechevolved.blockentity.custom.ComposterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ATEBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AgritechEvolved.MODID);

    public static final RegistryObject<BlockEntityType<ComposterBlockEntity>> COMPOSTER_BE =
            BLOCK_ENTITIES.register("composter_be", () -> BlockEntityType.Builder.of(
                    ComposterBlockEntity::new, ATEBlocks.COMPOSTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CapacitorBlockEntity>> CAPACITOR_BE =
            BLOCK_ENTITIES.register("capacitor_be", () -> BlockEntityType.Builder.of(
                    CapacitorBlockEntity::new,
                    ATEBlocks.CAPACITOR_TIER_1.get(),
                    ATEBlocks.CAPACITOR_TIER_2.get(),
                    ATEBlocks.CAPACITOR_TIER_3.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<BiomassBurnerBlockEntity>> BURNER_BE =
            BLOCK_ENTITIES.register("burner_be", () -> BlockEntityType.Builder.of(
                    BiomassBurnerBlockEntity::new, ATEBlocks.BIOMASS_BURNER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}