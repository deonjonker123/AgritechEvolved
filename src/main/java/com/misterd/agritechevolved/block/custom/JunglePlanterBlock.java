package com.misterd.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class JunglePlanterBlock extends PlanterBlock {
    public static final MapCodec<JunglePlanterBlock> CODEC = simpleCodec(JunglePlanterBlock::new);

    public JunglePlanterBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
