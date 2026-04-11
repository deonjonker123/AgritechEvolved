package com.misterd.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class BambooPlanterBlock extends PlanterBlock {
    public static final MapCodec<BambooPlanterBlock> CODEC = simpleCodec(BambooPlanterBlock::new);

    public BambooPlanterBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
