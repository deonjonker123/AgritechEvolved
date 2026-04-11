package com.misterd.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class CherryPlanterBlock extends PlanterBlock {
    public static final MapCodec<CherryPlanterBlock> CODEC = simpleCodec(CherryPlanterBlock::new);

    public CherryPlanterBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
