package com.misterd.agritechevolved.block.custom;

import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.blockentity.custom.AdvancedPlanterBlockEntity;
import com.misterd.agritechevolved.datamap.ATEDataMaps;
import com.misterd.agritechevolved.datamap.FertilizerData;
import com.misterd.agritechevolved.gui.custom.AdvancedPlanterMenu;
import com.misterd.agritechevolved.item.ATEItems;
import com.misterd.agritechevolved.item.custom.ClocheItem;
import com.misterd.agritechevolved.util.RegistryHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedPlanterBlock extends BaseEntityBlock {

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty CLOCHED = BooleanProperty.create("cloched");
    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(1, 0, 1, 3, 11, 3),
            Block.box(13, 0, 1, 15, 11, 3),
            Block.box(1, 0, 13, 3, 11, 15),
            Block.box(13, 0, 13, 15, 11, 15),
            Block.box(2, 2, 2, 14, 10, 3),
            Block.box(2, 2, 13, 14, 10, 14),
            Block.box(2, 2, 3, 3, 10, 13),
            Block.box(13, 2, 3, 14, 10, 13),
            Block.box(3, 2, 3, 13, 3, 13)
    );

    private static final List<String> FARMLAND_TIERS = List.of(
            "minecraft:farmland",
            "mysticalagriculture:inferium_farmland",
            "mysticalagriculture:prudentium_farmland",
            "mysticalagriculture:tertium_farmland",
            "mysticalagriculture:imperium_farmland",
            "mysticalagriculture:supremium_farmland",
            "mysticalagradditions:insanium_farmland"
    );

    private static final Map<String, String> ESSENCE_TO_FARMLAND = Map.of(
            "mysticalagriculture:inferium_essence", "mysticalagriculture:inferium_farmland",
            "mysticalagriculture:prudentium_essence", "mysticalagriculture:prudentium_farmland",
            "mysticalagriculture:tertium_essence", "mysticalagriculture:tertium_farmland",
            "mysticalagriculture:imperium_essence", "mysticalagriculture:imperium_farmland",
            "mysticalagriculture:supremium_essence", "mysticalagriculture:supremium_farmland",
            "mysticalagradditions:insanium_essence", "mysticalagradditions:insanium_farmland"
    );

    public AdvancedPlanterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(CLOCHED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, CLOCHED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedPlanterBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (state.getValue(CLOCHED)) {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ATEItems.CLOCHE.get())));
            }
            if (level.getBlockEntity(pos) instanceof AdvancedPlanterBlockEntity planter) planter.drops();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof AdvancedPlanterBlockEntity planter)) return InteractionResult.FAIL;
        ItemStack heldItem = player.getItemInHand(hand);
        if (player.isCrouching() && heldItem.isEmpty() && state.getValue(CLOCHED)) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(CLOCHED, false), 3);
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, new ItemStack(ATEItems.CLOCHE.get())));
                level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.5F, 1.2F);
            }
            return InteractionResult.SUCCESS;
        }
        if (player.isCrouching()) {
            if (!level.isClientSide()) openMenu(pos, player, planter);
            return InteractionResult.SUCCESS;
        }
        if (heldItem.getItem() instanceof ClocheItem) {
            if (state.getValue(CLOCHED)) return InteractionResult.FAIL;
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(CLOCHED, true), 3);
                if (!player.getAbilities().instabuild) heldItem.shrink(1);
                level.playSound(null, pos, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }
        if (planter.isValidPlant(heldItem)) return tryPlaceSeedOrSapling(heldItem, level, pos, state, player, planter);
        if (planter.isValidSoilForAnyRecipe(heldItem)) return tryPlaceSoil(heldItem, level, pos, state, player, planter);
        if (heldItem.getItem() instanceof HoeItem) return tryTillSoil(heldItem, level, pos, player, hand, hitResult, planter);
        if (AdvancedPlanterBlockEntity.isFertilizer(heldItem)) return tryPlaceFertilizer(heldItem, level, pos, state, player, planter);
        if (AdvancedPlanterBlockEntity.isModule(heldItem)) return tryPlaceModule(heldItem, level, pos, state, player, planter);
        if (tryUpgradeFarmland(heldItem, level, pos, player, planter)) return InteractionResult.sidedSuccess(level.isClientSide());
        if (!level.isClientSide()) openMenu(pos, player, planter);
        return InteractionResult.SUCCESS;
    }

    private InteractionResult tryPlaceSeedOrSapling(ItemStack heldItem, Level level, BlockPos pos, BlockState state, Player player, AdvancedPlanterBlockEntity planter) {
        if (!planter.inventory.getStackInSlot(AdvancedPlanterBlockEntity.SLOT_PLANT).isEmpty()) {
            if (!level.isClientSide()) openMenu(pos, player, planter);
            return InteractionResult.SUCCESS;
        }
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        ItemStack soilStack = planter.inventory.getStackInSlot(AdvancedPlanterBlockEntity.SLOT_SOIL);
        if (!soilStack.isEmpty() && !planter.isValidPlantSoilCombination(heldItem, soilStack)) {
            player.displayClientMessage(Component.translatable("message.agritechevolved.invalid_plant_soil_combination"), true);
            return InteractionResult.SUCCESS;
        }
        placeInSlot(planter, AdvancedPlanterBlockEntity.SLOT_PLANT, heldItem, level, pos, state);
        level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    private InteractionResult tryPlaceSoil(ItemStack heldItem, Level level, BlockPos pos, BlockState state, Player player, AdvancedPlanterBlockEntity planter) {
        if (!planter.inventory.getStackInSlot(AdvancedPlanterBlockEntity.SLOT_SOIL).isEmpty()) {
            if (!level.isClientSide()) openMenu(pos, player, planter);
            return InteractionResult.SUCCESS;
        }
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        ItemStack plantStack = planter.inventory.getStackInSlot(AdvancedPlanterBlockEntity.SLOT_PLANT);
        if (!plantStack.isEmpty() && !planter.isValidPlantSoilCombination(plantStack, heldItem)) {
            player.displayClientMessage(Component.translatable("message.agritechevolved.invalid_plant_soil_combination"), true);
            return InteractionResult.SUCCESS;
        }
        placeInSlot(planter, AdvancedPlanterBlockEntity.SLOT_SOIL, heldItem, level, pos, state);
        level.playSound(null, pos, SoundEvents.GRAVEL_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
        return InteractionResult.SUCCESS;
    }

    private InteractionResult tryPlaceFertilizer(ItemStack heldItem, Level level, BlockPos pos, BlockState state, Player player, AdvancedPlanterBlockEntity planter) {
        if (planter.inventory.getStackInSlot(AdvancedPlanterBlockEntity.SLOT_PLANT).isEmpty()
                || planter.inventory.getStackInSlot(AdvancedPlanterBlockEntity.SLOT_SOIL).isEmpty()
                || planter.isReadyToHarvest()) {
            if (!level.isClientSide()) openMenu(pos, player, planter);
            return InteractionResult.SUCCESS;
        }
        if (!level.isClientSide()) {
            FertilizerData data = ATEDataMaps.getFertilizer(heldItem.getItem());
            if (data != null) {
                planter.applyManualFertilizer(data.speedMultiplier());
                if (!player.getAbilities().instabuild) heldItem.shrink(1);
                level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 6, 0.3, 0.2, 0.3, 0.0);
                }
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult tryPlaceModule(ItemStack heldItem, Level level, BlockPos pos, BlockState state, Player player, AdvancedPlanterBlockEntity planter) {
        for (int slot = AdvancedPlanterBlockEntity.SLOT_MODULE_1; slot <= AdvancedPlanterBlockEntity.SLOT_MODULE_2; slot++) {
            if (planter.inventory.getStackInSlot(slot).isEmpty()) {
                if (!level.isClientSide()) {
                    placeInSlot(planter, slot, heldItem, level, pos, state);
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.2F);
                }
                return InteractionResult.SUCCESS;
            }
        }
        if (!level.isClientSide()) openMenu(pos, player, planter);
        return InteractionResult.SUCCESS;
    }

    private InteractionResult tryTillSoil(ItemStack heldItem, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, AdvancedPlanterBlockEntity planter) {
        ItemStack soilStack = planter.inventory.getStackInSlot(AdvancedPlanterBlockEntity.SLOT_SOIL);
        if (soilStack.isEmpty() || !(soilStack.getItem() instanceof BlockItem blockItem)) return InteractionResult.PASS;
        BlockState soilState = blockItem.getBlock().defaultBlockState();
        BlockState result = soilState.getToolModifiedState(new UseOnContext(level, player, hand, heldItem, hitResult), ToolActions.HOE_TILL, false);
        if (result == null) return InteractionResult.PASS;
        planter.inventory.setStackInSlot(AdvancedPlanterBlockEntity.SLOT_SOIL, new ItemStack(result.getBlock()));
        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!player.getAbilities().instabuild) heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private boolean tryUpgradeFarmland(ItemStack heldItem, Level level, BlockPos pos, Player player, AdvancedPlanterBlockEntity planter) {
        Map<String, String> essenceMap = new HashMap<>(ESSENCE_TO_FARMLAND);
        if (ModList.get().isLoaded("mysticalagradditions")) {
            essenceMap.put("mysticalagradditions:insanium_essence", "mysticalagradditions:insanium_farmland");
        }
        String heldItemId = RegistryHelper.getItemId(heldItem);
        String targetFarmlandId = essenceMap.get(heldItemId);
        if (targetFarmlandId == null) return false;
        ItemStack soilStack = planter.inventory.getStackInSlot(AdvancedPlanterBlockEntity.SLOT_SOIL);
        if (soilStack.isEmpty() || !(soilStack.getItem() instanceof BlockItem soilBlockItem)) return false;
        String soilId = RegistryHelper.getBlockId(soilBlockItem.getBlock());
        boolean isFarmland = soilId.equals("minecraft:farmland")
                || (soilId.startsWith("mysticalagriculture:") && soilId.endsWith("_farmland"))
                || (soilId.startsWith("mysticalagradditions:") && soilId.endsWith("_farmland"));
        if (!isFarmland) return false;
        int currentTier = FARMLAND_TIERS.indexOf(soilId);
        int targetTier = FARMLAND_TIERS.indexOf(targetFarmlandId);
        if (targetTier <= currentTier) {
            if (!level.isClientSide()) player.displayClientMessage(Component.translatable("message.agritechevolved.same_ma_farmland").withStyle(ChatFormatting.GOLD), true);
            return true;
        }
        Block resultBlock = RegistryHelper.getBlock(targetFarmlandId);
        if (resultBlock == null) return false;
        planter.inventory.setStackInSlot(AdvancedPlanterBlockEntity.SLOT_SOIL, new ItemStack(resultBlock));
        if (!player.getAbilities().instabuild) heldItem.shrink(1);
        level.playSound(player, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    private void placeInSlot(AdvancedPlanterBlockEntity planter, int slot, ItemStack heldItem, Level level, BlockPos pos, BlockState state) {
        planter.inventory.setStackInSlot(slot, heldItem.copyWithCount(1));
        heldItem.shrink(1);
        level.sendBlockUpdated(pos, state, state, 2);
        planter.setChanged();
    }

    private void openMenu(BlockPos pos, Player player, AdvancedPlanterBlockEntity planter) {
        NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider((id, inv, p) -> new AdvancedPlanterMenu(id, inv, planter), Component.translatable("gui.agritechevolved.advanced_planter")), pos);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ATEBlockEntities.ADVANCED_PLANTER_BLOCK_BE.get()
                ? (lvl, pos, blockState, be) -> AdvancedPlanterBlockEntity.tick(lvl, pos, blockState, (AdvancedPlanterBlockEntity) be)
                : null;
    }
}