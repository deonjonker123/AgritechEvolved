package com.misterd.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class WarpedPlanterBlock extends PlanterBlock {
    public static final MapCodec<WarpedPlanterBlock> CODEC = simpleCodec(WarpedPlanterBlock::new);

    public WarpedPlanterBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
