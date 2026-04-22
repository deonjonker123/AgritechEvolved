package com.misterd.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class AcaciaPlanterBlock extends PlanterBlock {
    public static final MapCodec<AcaciaPlanterBlock> CODEC = simpleCodec(AcaciaPlanterBlock::new);

    public AcaciaPlanterBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
