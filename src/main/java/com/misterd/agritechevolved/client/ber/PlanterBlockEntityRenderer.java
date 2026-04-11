package com.misterd.agritechevolved.client.ber;

import com.misterd.agritechevolved.block.custom.AdvancedPlanterBlock;
import com.misterd.agritechevolved.block.custom.PlanterBlock;
import com.misterd.agritechevolved.blockentity.custom.AdvancedPlanterBlockEntity;
import com.misterd.agritechevolved.blockentity.custom.PlanterBlockEntity;
import com.misterd.agritechevolved.config.PlantablesConfig;
import com.misterd.agritechevolved.util.RegistryHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

public class PlanterBlockEntityRenderer implements BlockEntityRenderer<BlockEntity> {

    private static final ModelResourceLocation CLOCHE_DOME_MODEL = new ModelResourceLocation(
            ResourceLocation.fromNamespaceAndPath("agritechtwo", "block/cloche_dome"), "standalone");
    private static final ResourceLocation WATER_STILL =
            ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");

    public PlanterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    // -------------------------------------------------------------------------
    // Render entry point
    // -------------------------------------------------------------------------

    @Override
    public void render(BlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        // Resolve the concrete planter type into a common view
        PlanterView planter = PlanterView.of(blockEntity);
        if (planter == null) return;

        renderClocheDome(blockEntity, poseStack, bufferSource, packedLight);
        renderSoil(planter.inventory(), poseStack, bufferSource, packedLight);
        renderPlant(planter.inventory(), planter.growthProgress(), planter.growthStage(),
                poseStack, bufferSource, packedLight);
    }

    // -------------------------------------------------------------------------
    // Cloche dome rendering
    // -------------------------------------------------------------------------

    private void renderClocheDome(BlockEntity blockEntity, PoseStack poseStack,
                                  MultiBufferSource bufferSource, int packedLight) {
        BlockState state = blockEntity.getBlockState();
        boolean cloched = (state.hasProperty(PlanterBlock.CLOCHED) && state.getValue(PlanterBlock.CLOCHED))
                || (state.hasProperty(AdvancedPlanterBlock.CLOCHED) && state.getValue(AdvancedPlanterBlock.CLOCHED));
        if (!cloched) return;

        BakedModel domeModel = Minecraft.getInstance().getModelManager().getModel(CLOCHE_DOME_MODEL);
        poseStack.pushPose();
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(RenderType.cutoutMipped()),
                null, domeModel,
                1.0F, 1.0F, 1.0F,
                packedLight, OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY, RenderType.cutoutMipped());
        poseStack.popPose();
    }

    // -------------------------------------------------------------------------
    // Soil rendering (slot 1)
    // -------------------------------------------------------------------------

    private void renderSoil(ItemStackHandler inventory, PoseStack poseStack,
                            MultiBufferSource bufferSource, int packedLight) {
        ItemStack soilStack = inventory.getStackInSlot(1);
        if (soilStack.isEmpty()) return;

        String soilId = RegistryHelper.getItemId(soilStack);
        if (soilId.equals("minecraft:water_bucket")) {
            renderWater(poseStack, bufferSource, packedLight);
            return;
        }

        if (!(soilStack.getItem() instanceof BlockItem blockItem)) return;

        BlockState soilState = blockItem.getBlock().defaultBlockState();
        poseStack.pushPose();
        poseStack.translate(0.175, 0.4, 0.175);
        poseStack.scale(0.65F, 0.05F, 0.65F);
        blockRenderer().renderSingleBlock(soilState, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    // -------------------------------------------------------------------------
    // Water rendering (used when soil slot holds a water bucket)
    // -------------------------------------------------------------------------

    private void renderWater(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(WATER_STILL);

        float y = 0.41F, xMin = 0.175F, xMax = 0.825F, zMin = 0.175F, zMax = 0.825F;
        float u0 = sprite.getU0(), u1 = sprite.getU1();
        float v0 = sprite.getV0(), v1 = sprite.getV1();

        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());

        buffer.addVertex(matrix, xMin, y, zMin).setColor(0x3F, 0x76, 0xE4, 0xA0).setUv(u0, v0).setLight(packedLight).setNormal(0, 1, 0);
        buffer.addVertex(matrix, xMin, y, zMax).setColor(0x3F, 0x76, 0xE4, 0xA0).setUv(u0, v1).setLight(packedLight).setNormal(0, 1, 0);
        buffer.addVertex(matrix, xMax, y, zMax).setColor(0x3F, 0x76, 0xE4, 0xA0).setUv(u1, v1).setLight(packedLight).setNormal(0, 1, 0);
        buffer.addVertex(matrix, xMax, y, zMin).setColor(0x3F, 0x76, 0xE4, 0xA0).setUv(u1, v0).setLight(packedLight).setNormal(0, 1, 0);

        poseStack.popPose();
    }

    // -------------------------------------------------------------------------
    // Plant rendering (slot 0, requires soil in slot 1)
    // -------------------------------------------------------------------------

    private void renderPlant(ItemStackHandler inventory, float growthProgress, int growthStage,
                             PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (inventory.getStackInSlot(0).isEmpty() || inventory.getStackInSlot(1).isEmpty()) return;

        ItemStack plantStack = inventory.getStackInSlot(0);
        if (!(plantStack.getItem() instanceof BlockItem blockItem)) return;

        String plantId  = RegistryHelper.getItemId(plantStack);
        boolean isTree  = PlantablesConfig.isValidSapling(plantId);
        boolean isCrop  = PlantablesConfig.isValidSeed(plantId);
        if (!isTree && !isCrop) return;

        BlockState plantState = blockItem.getBlock().defaultBlockState();

        poseStack.pushPose();
        if (isTree) {
            float scale = 0.3F + growthProgress * 0.4F;
            poseStack.translate(0.5, 0.45, 0.5);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(-0.5, 0.0, -0.5);
        } else {
            plantState = getCropBlockState(plantStack, growthStage);
            float growthScale = 0.2F + Math.min(1.0F, growthProgress) * 0.5F;
            poseStack.translate(0.1725, 0.45, 0.1725);
            poseStack.scale(0.65F, growthScale, 0.65F);
        }
        blockRenderer().renderSingleBlock(plantState, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    // -------------------------------------------------------------------------
    // Crop age state resolution
    // -------------------------------------------------------------------------

    @Nullable
    private BlockState getCropBlockState(ItemStack stack, int age) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) return null;

        BlockState state = blockItem.getBlock().defaultBlockState();

        // Prefer the generic "age" IntegerProperty so modded crops are handled automatically
        for (Property<?> property : state.getProperties()) {
            if (property instanceof IntegerProperty intProp && property.getName().equals("age")) {
                int maxAge = intProp.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(7);
                return state.setValue(intProp, Math.min(age, maxAge));
            }
        }

        // Fallback to vanilla well-known age properties
        if (state.hasProperty(BlockStateProperties.AGE_7))  return state.setValue(BlockStateProperties.AGE_7,  Math.min(age, 7));
        if (state.hasProperty(BlockStateProperties.AGE_3))  return state.setValue(BlockStateProperties.AGE_3,  Math.min(age, 3));
        if (state.hasProperty(BlockStateProperties.AGE_5))  return state.setValue(BlockStateProperties.AGE_5,  Math.min(age, 5));
        if (state.hasProperty(BlockStateProperties.AGE_15)) return state.setValue(BlockStateProperties.AGE_15, Math.min(age, 15));
        if (state.hasProperty(BlockStateProperties.AGE_25)) return state.setValue(BlockStateProperties.AGE_25, Math.min(age, 25));

        return state;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static BlockRenderDispatcher blockRenderer() {
        return Minecraft.getInstance().getBlockRenderer();
    }

    // -------------------------------------------------------------------------
    // PlanterView — unifies Advanced and Basic planters
    // -------------------------------------------------------------------------

    private record PlanterView(ItemStackHandler inventory, float growthProgress, int growthStage) {

        @Nullable
        static PlanterView of(BlockEntity be) {
            if (be instanceof AdvancedPlanterBlockEntity p)
                return new PlanterView(p.inventory, p.getGrowthProgress(), p.getGrowthStage());
            if (be instanceof PlanterBlockEntity p)
                return new PlanterView(p.inventory, p.getGrowthProgress(), p.getGrowthStage());
            return null;
        }
    }
}