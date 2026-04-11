package com.misterd.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class CrimsonPlanterBlock extends PlanterBlock {
    public static final MapCodec<CrimsonPlanterBlock> CODEC = simpleCodec(CrimsonPlanterBlock::new);

    public CrimsonPlanterBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
