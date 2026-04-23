package com.misterd.agritechevolved.block.custom;

import com.misterd.agritechevolved.blockentity.custom.EnergyConduitBlockEntity;
import com.misterd.agritechevolved.network.ConduitNetworkManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nullable;
import java.util.Map;

public class EnergyConduitBlock extends BaseEntityBlock {

    public static final MapCodec<EnergyConduitBlock> CODEC = simpleCodec(EnergyConduitBlock::new);

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty REDSTONE_MODE = BooleanProperty.create("redstone_mode");

    private static final Map<Direction, BooleanProperty> DIR_TO_PROPERTY = Map.of(
            Direction.NORTH, NORTH,
            Direction.SOUTH, SOUTH,
            Direction.EAST, EAST,
            Direction.WEST, WEST,
            Direction.UP, UP,
            Direction.DOWN, DOWN
    );

    private static final VoxelShape CORE = Block.box(6, 6, 6, 10, 10, 10);
    private static final VoxelShape S_UP = Block.box(6.5, 10, 6.5, 9.5, 16, 9.5);
    private static final VoxelShape S_DN = Block.box(6.5, 0, 6.5, 9.5, 6, 9.5);
    private static final VoxelShape S_N = Block.box(6.5, 6.5, 0,  9.5, 9.5, 6.5);
    private static final VoxelShape S_S = Block.box(6.5, 6.5, 9.5, 9.5, 9.5, 15.5);
    private static final VoxelShape S_E = Block.box(10, 6.5, 6.5, 16, 9.5, 9.5);
    private static final VoxelShape S_W = Block.box(0, 6.5, 6.5, 6, 9.5, 9.5);

    private static final VoxelShape[] SHAPES = buildShapes();

    private static VoxelShape[] buildShapes() {
        VoxelShape[] s = new VoxelShape[64];
        for (int i = 0; i < 64; i++) {
            VoxelShape shape = CORE;
            if ((i & 1) != 0) shape = Shapes.or(shape, S_DN);
            if ((i & 2) != 0) shape = Shapes.or(shape, S_UP);
            if ((i & 4) != 0) shape = Shapes.or(shape, S_N);
            if ((i & 8) != 0) shape = Shapes.or(shape, S_S);
            if ((i & 16) != 0) shape = Shapes.or(shape, S_W);
            if ((i & 32) != 0) shape = Shapes.or(shape, S_E);
            s[i] = shape;
        }
        return s;
    }

    private static int shapeIndex(BlockState state) {
        int i = 0;
        if (state.getValue(DOWN)) i |= 1;
        if (state.getValue(UP)) i |= 2;
        if (state.getValue(NORTH)) i |= 4;
        if (state.getValue(SOUTH)) i |= 8;
        if (state.getValue(WEST)) i |= 16;
        if (state.getValue(EAST)) i |= 32;
        return i;
    }

    public EnergyConduitBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST,  false)
                .setValue(UP, false).setValue(DOWN,  false)
                .setValue(REDSTONE_MODE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, REDSTONE_MODE);
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
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[shapeIndex(state)];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = defaultBlockState();
        for (Direction dir : Direction.values()) {
            state = state.setValue(DIR_TO_PROPERTY.get(dir), canConnect(level, pos.relative(dir), dir));
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(DIR_TO_PROPERTY.get(direction), canConnect(level, neighborPos, direction));
    }

    private boolean canConnect(LevelAccessor level, BlockPos neighborPos, Direction dirToNeighbor) {
        if (level.getBlockState(neighborPos).getBlock() instanceof EnergyConduitBlock) return true;
        if (!(level instanceof Level realLevel)) return false;
        return realLevel.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dirToNeighbor.getOpposite()) != null;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (!level.isClientSide()) {
                ConduitNetworkManager.get(level).onEnergyConduitRemoved(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyConduitBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }

}
