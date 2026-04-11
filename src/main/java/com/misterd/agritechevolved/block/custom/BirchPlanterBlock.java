package com.misterd.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class BirchPlanterBlock extends PlanterBlock {
    public static final MapCodec<BirchPlanterBlock> CODEC = simpleCodec(BirchPlanterBlock::new);

    public BirchPlanterBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
