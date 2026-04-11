package com.misterd.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class SprucePlanterBlock extends PlanterBlock {
    public static final MapCodec<SprucePlanterBlock> CODEC = simpleCodec(SprucePlanterBlock::new);

    public SprucePlanterBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
