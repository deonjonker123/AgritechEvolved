package com.misterd.agritechevolved.client.ber;

import com.misterd.agritechevolved.block.custom.AdvancedPlanterBlock;
import com.misterd.agritechevolved.blockentity.custom.AdvancedPlanterBlockEntity;
import com.misterd.agritechevolved.util.RegistryHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class AdvancedPlanterBlockEntityRenderer
        implements BlockEntityRenderer<AdvancedPlanterBlockEntity, AdvancedPlanterBlockEntityRenderer.RenderState> {

    public AdvancedPlanterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    public static class RenderState extends BlockEntityRenderState {
        public boolean cloched = false;
        public ItemStack soilStack = ItemStack.EMPTY;
        public ItemStack plantStack = ItemStack.EMPTY;
        public float growthProgress = 0f;
        public int growthStage = 0;
        public boolean soilIsWater = false;
        public long posSeed = 0L;
        public double distanceSq = 0.0;
        public int[] soilTints = new int[0];
        public int[] plantTints = new int[0];
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(AdvancedPlanterBlockEntity be, RenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);

        state.cloched = be.getBlockState().getValue(AdvancedPlanterBlock.CLOCHED);
        state.soilStack = be.getStack(1).copy();
        state.plantStack  = be.getStack(0).copy();
        state.growthProgress = be.getGrowthProgress();
        state.growthStage = be.getGrowthStage();
        state.posSeed = be.getBlockPos().asLong();
        state.distanceSq = cameraPos.distanceToSqr(Vec3.atCenterOf(be.getBlockPos()));
        state.soilIsWater = !state.soilStack.isEmpty() && RegistryHelper.getItemId(state.soilStack).equals("minecraft:water_bucket");

        var level = (BlockAndTintGetter) be.getLevel();
        var pos = be.getBlockPos();
        state.soilTints = PlanterBlockEntityRenderer.sampleTints(state.soilStack,  level, pos);
        state.plantTints = PlanterBlockEntityRenderer.sampleTints(state.plantStack, level, pos);
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        PlanterBlockEntityRenderer.submitShared(
                state.cloched,
                state.distanceSq,
                state.soilStack,
                state.soilIsWater,
                state.soilTints,
                state.plantStack,
                state.plantTints,
                state.growthProgress,
                state.growthStage,
                state.posSeed,
                state.lightCoords,
                poseStack,
                collector);
    }
}