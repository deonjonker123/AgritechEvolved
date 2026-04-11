package com.misterd.agritechevolved.block.custom;

import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.blockentity.custom.AdvancedPlanterBlockEntity;
import com.misterd.agritechevolved.config.PlantablesConfig;
import com.misterd.agritechevolved.gui.custom.AdvancedPlanterMenu;
import com.misterd.agritechevolved.item.ATEItems;
import com.misterd.agritechevolved.item.custom.ClocheItem;
import com.misterd.agritechevolved.util.ATETags;
import com.misterd.agritechevolved.util.RegistryHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class AdvancedPlanterBlock extends BaseEntityBlock {

    public static final MapCodec<AdvancedPlanterBlock> CODEC = simpleCodec(AdvancedPlanterBlock::new);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty CLOCHED = BooleanProperty.create("cloched");
    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(1,  0,  1,  3, 11,  3),
            Block.box(13, 0,  1, 15, 11,  3),
            Block.box(1,  0, 13,  3, 11, 15),
            Block.box(13, 0, 13, 15, 11, 15),
            Block.box(2,  2,  2, 14, 10,  3),
            Block.box(2,  2, 13, 14, 10, 14),
            Block.box(2,  2,  3,  3, 10, 13),
            Block.box(13, 2,  3, 14, 10, 13),
            Block.box(3,  2,  3, 13,  3, 13)
    );

    private static final Map<String, String> TILLABLE_BLOCKS;
    private static final Map<String, String> ESSENCE_TO_FARMLAND;

    static {
        TILLABLE_BLOCKS = new HashMap<>();
        TILLABLE_BLOCKS.put("minecraft:dirt",        "minecraft:farmland");
        TILLABLE_BLOCKS.put("minecraft:grass_block", "minecraft:farmland");
        TILLABLE_BLOCKS.put("minecraft:mycelium",    "minecraft:farmland");
        TILLABLE_BLOCKS.put("minecraft:podzol",      "minecraft:farmland");
        TILLABLE_BLOCKS.put("minecraft:coarse_dirt", "minecraft:farmland");
        TILLABLE_BLOCKS.put("minecraft:rooted_dirt", "minecraft:farmland");

        ESSENCE_TO_FARMLAND = new HashMap<>();
        ESSENCE_TO_FARMLAND.put("mysticalagriculture:inferium_essence",   "mysticalagriculture:inferium_farmland");
        ESSENCE_TO_FARMLAND.put("mysticalagriculture:prudentium_essence", "mysticalagriculture:prudentium_farmland");
        ESSENCE_TO_FARMLAND.put("mysticalagriculture:tertium_essence",    "mysticalagriculture:tertium_farmland");
        ESSENCE_TO_FARMLAND.put("mysticalagriculture:imperium_essence",   "mysticalagriculture:imperium_farmland");
        ESSENCE_TO_FARMLAND.put("mysticalagriculture:supremium_essence",  "mysticalagriculture:supremium_farmland");
    }

    public AdvancedPlanterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POWERED, false)
                .setValue(CLOCHED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, CLOCHED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedPlanterBlockEntity(pos, state);
    }

    // -------------------------------------------------------------------------
    // Placement / removal
    // -------------------------------------------------------------------------

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide()) {
            level.invalidateCapabilities(pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            // Drop the cloche as an item if one is installed
            if (state.getValue(CLOCHED)) {
                level.addFreshEntity(new ItemEntity(
                        level,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        new ItemStack(ATEItems.CLOCHE.get())
                ));
            }
            if (level.getBlockEntity(pos) instanceof AdvancedPlanterBlockEntity planter) {
                planter.drops();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    // -------------------------------------------------------------------------
    // Interaction
    // -------------------------------------------------------------------------

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof AdvancedPlanterBlockEntity planter)) {
            return ItemInteractionResult.FAIL;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        // Sneak + empty hand + cloched → remove cloche
        if (player.isCrouching() && heldItem.isEmpty() && state.getValue(CLOCHED)) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(CLOCHED, false), 3);
                level.addFreshEntity(new ItemEntity(
                        level,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        new ItemStack(ATEItems.CLOCHE.get())
                ));
                level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.5F, 1.2F);
            }
            return ItemInteractionResult.SUCCESS;
        }

        // Sneak-click always opens the GUI
        if (player.isCrouching()) {
            if (!level.isClientSide()) openMenu(level, pos, player, planter);
            return ItemInteractionResult.SUCCESS;
        }

        String heldItemId = RegistryHelper.getItemId(heldItem);

        // --- Cloche ---
        if (heldItem.getItem() instanceof ClocheItem) {
            if (state.getValue(CLOCHED)) return ItemInteractionResult.FAIL;
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(CLOCHED, true), 3);
                if (!player.getAbilities().instabuild) heldItem.shrink(1);
                level.playSound(null, pos, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.SUCCESS;
        }

        // --- Seed / Sapling ---
        if (PlantablesConfig.isValidSeed(heldItemId) || PlantablesConfig.isValidSapling(heldItemId)) {
            return tryPlaceSeedOrSapling(heldItem, heldItemId, level, pos, state, player, planter);
        }

        // --- Soil ---
        if (PlantablesConfig.isValidSoil(heldItemId)) {
            return tryPlaceSoil(heldItem, heldItemId, level, pos, state, player, planter);
        }

        // --- Hoe (till soil in slot 1) ---
        if (heldItem.getItem() instanceof HoeItem) {
            return tryTillSoil(heldItem, level, pos, player, hand, planter);
        }

        // --- Fertilizer ---
        if (PlantablesConfig.isValidFertilizer(heldItemId)) {
            return tryPlaceFertilizer(heldItem, level, pos, state, player, planter);
        }

        // --- Module ---
        if (heldItem.is(ATETags.Items.ATE_MODULES)) {
            return tryPlaceModule(heldItem, level, pos, state, player, planter);
        }

        // --- Mystical Agriculture essence (upgrade farmland) ---
        if (tryUpgradeFarmland(stack, heldItemId, level, pos, player, planter)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        // Fallback: open GUI
        if (!level.isClientSide()) openMenu(level, pos, player, planter);
        return ItemInteractionResult.SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Interaction helpers
    // -------------------------------------------------------------------------

    private ItemInteractionResult tryPlaceSeedOrSapling(ItemStack heldItem, String heldItemId, Level level,
                                                        BlockPos pos, BlockState state, Player player,
                                                        AdvancedPlanterBlockEntity planter) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
        if (!planter.inventory.getStackInSlot(0).isEmpty()) return ItemInteractionResult.SUCCESS;

        ItemStack soilStack = planter.inventory.getStackInSlot(1);
        if (!soilStack.isEmpty()) {
            String soilId = RegistryHelper.getItemId(soilStack);
            boolean valid = PlantablesConfig.isValidSeed(heldItemId)
                    ? PlantablesConfig.isSoilValidForSeed(soilId, heldItemId)
                    : PlantablesConfig.isSoilValidForSapling(soilId, heldItemId);
            if (!valid) {
                player.displayClientMessage(
                        Component.translatable("message.agritechevolved.invalid_plant_soil_combination"), true);
                return ItemInteractionResult.SUCCESS;
            }
        }

        placeInSlot(planter, 0, heldItem, level, pos, state);
        level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
        return ItemInteractionResult.SUCCESS;
    }

    private ItemInteractionResult tryPlaceSoil(ItemStack heldItem, String heldItemId, Level level,
                                               BlockPos pos, BlockState state, Player player,
                                               AdvancedPlanterBlockEntity planter) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
        if (!planter.inventory.getStackInSlot(1).isEmpty()) return ItemInteractionResult.SUCCESS;

        ItemStack plantStack = planter.inventory.getStackInSlot(0);
        if (!plantStack.isEmpty()) {
            String plantId = RegistryHelper.getItemId(plantStack);
            boolean valid = false;
            if (PlantablesConfig.isValidSeed(plantId))         valid = PlantablesConfig.isSoilValidForSeed(heldItemId, plantId);
            else if (PlantablesConfig.isValidSapling(plantId)) valid = PlantablesConfig.isSoilValidForSapling(heldItemId, plantId);
            if (!valid) {
                player.displayClientMessage(
                        Component.translatable("message.agritechevolved.invalid_plant_soil_combination"), true);
                return ItemInteractionResult.SUCCESS;
            }
        }

        placeInSlot(planter, 1, heldItem, level, pos, state);
        level.playSound(null, pos, SoundEvents.GRAVEL_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
        return ItemInteractionResult.SUCCESS;
    }

    private ItemInteractionResult tryTillSoil(ItemStack heldItem, Level level, BlockPos pos,
                                              Player player, InteractionHand hand,
                                              AdvancedPlanterBlockEntity planter) {
        ItemStack soilStack = planter.inventory.getStackInSlot(1);
        if (soilStack.isEmpty() || !(soilStack.getItem() instanceof BlockItem blockItem)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        Map<String, String> tillable = new HashMap<>(TILLABLE_BLOCKS);
        if (ModList.get().isLoaded("farmersdelight")) {
            tillable.put("farmersdelight:rich_soil", "farmersdelight:rich_soil_farmland");
        }

        String soilId   = RegistryHelper.getBlockId(blockItem.getBlock());
        String resultId = tillable.get(soilId);
        if (resultId == null) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        Block resultBlock = RegistryHelper.getBlock(resultId);
        if (resultBlock == null) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        planter.inventory.setStackInSlot(1, new ItemStack(resultBlock));
        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!player.getAbilities().instabuild) {
            heldItem.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    private ItemInteractionResult tryPlaceFertilizer(ItemStack heldItem, Level level, BlockPos pos,
                                                     BlockState state, Player player,
                                                     AdvancedPlanterBlockEntity planter) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
        if (!planter.inventory.getStackInSlot(4).isEmpty()) return ItemInteractionResult.SUCCESS;

        placeInSlot(planter, 4, heldItem, level, pos, state);
        level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        return ItemInteractionResult.SUCCESS;
    }

    private ItemInteractionResult tryPlaceModule(ItemStack heldItem, Level level, BlockPos pos,
                                                 BlockState state, Player player,
                                                 AdvancedPlanterBlockEntity planter) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        for (int slot = 2; slot <= 3; slot++) {
            if (planter.inventory.getStackInSlot(slot).isEmpty()) {
                placeInSlot(planter, slot, heldItem, level, pos, state);
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.2F);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    /** @return true if the essence was consumed and the farmland was upgraded */
    private boolean tryUpgradeFarmland(ItemStack stack, String heldItemId, Level level, BlockPos pos,
                                       Player player, AdvancedPlanterBlockEntity planter) {
        Map<String, String> essenceMap = new HashMap<>(ESSENCE_TO_FARMLAND);
        if (ModList.get().isLoaded("mysticalagradditions")) {
            essenceMap.put("mysticalagradditions:insanium_essence", "mysticalagradditions:insanium_farmland");
        }

        String targetFarmlandId = essenceMap.get(heldItemId);
        if (targetFarmlandId == null) return false;

        ItemStack soilStack = planter.inventory.getStackInSlot(1);
        if (soilStack.isEmpty() || !(soilStack.getItem() instanceof BlockItem soilBlockItem)) return false;

        String soilId = RegistryHelper.getBlockId(soilBlockItem.getBlock());
        boolean isFarmland = soilId.equals("minecraft:farmland")
                || (soilId.startsWith("mysticalagriculture:") && soilId.endsWith("_farmland"))
                || (soilId.startsWith("mysticalagradditions:") && soilId.endsWith("_farmland"));
        if (!isFarmland) return false;

        if (soilId.equals(targetFarmlandId)) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.translatable("message.agritechevolved.same_farmland"), true);
            }
            return true;
        }

        Block resultBlock = RegistryHelper.getBlock(targetFarmlandId);
        if (resultBlock == null) return false;

        planter.inventory.setStackInSlot(1, new ItemStack(resultBlock));
        if (!player.getAbilities().instabuild) stack.shrink(1);
        level.playSound(player, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    // -------------------------------------------------------------------------
    // Utilities
    // -------------------------------------------------------------------------

    private void placeInSlot(AdvancedPlanterBlockEntity planter, int slot, ItemStack heldItem,
                             Level level, BlockPos pos, BlockState state) {
        planter.inventory.setStackInSlot(slot, heldItem.copyWithCount(1));
        heldItem.shrink(1);
        level.sendBlockUpdated(pos, state, state, 2);
        planter.setChanged();
    }

    private void openMenu(Level level, BlockPos pos, Player player, AdvancedPlanterBlockEntity planter) {
        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new AdvancedPlanterMenu(id, inv, planter),
                Component.translatable("gui.agritechevolved.advanced_planter")), pos);
    }

    // -------------------------------------------------------------------------
    // Ticker
    // -------------------------------------------------------------------------

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return type == ATEBlockEntities.ADVANCED_PLANTER_BLOCK_BE.get()
                ? (lvl, pos, blockState, be) ->
                AdvancedPlanterBlockEntity.tick(lvl, pos, blockState, (AdvancedPlanterBlockEntity) be)
                : null;
    }
}