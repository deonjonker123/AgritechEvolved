package com.misterd.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class PaleOakPlanterBlock extends PlanterBlock {
    public static final MapCodec<PaleOakPlanterBlock> CODEC = simpleCodec(PaleOakPlanterBlock::new);

    public PaleOakPlanterBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
