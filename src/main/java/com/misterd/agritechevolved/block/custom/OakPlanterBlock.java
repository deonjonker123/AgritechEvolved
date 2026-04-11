package com.misterd.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class OakPlanterBlock extends PlanterBlock {
    public static final MapCodec<OakPlanterBlock> CODEC = simpleCodec(OakPlanterBlock::new);

    public OakPlanterBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
