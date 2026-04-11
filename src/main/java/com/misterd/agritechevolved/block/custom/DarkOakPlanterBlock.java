package com.misterd.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class DarkOakPlanterBlock extends PlanterBlock {
    public static final MapCodec<DarkOakPlanterBlock> CODEC = simpleCodec(DarkOakPlanterBlock::new);

    public DarkOakPlanterBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
