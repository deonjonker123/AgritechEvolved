package com.misterd.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class MangrovePlanterBlock extends PlanterBlock {
    public static final MapCodec<MangrovePlanterBlock> CODEC = simpleCodec(MangrovePlanterBlock::new);

    public MangrovePlanterBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
