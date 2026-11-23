package com.misterd.agritechevolved.block.custom;

import com.misterd.agritechevolved.block.ATEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;

public class MulchBlock extends Block {

    public MulchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof HoeItem) {
            return convertToInfusedFarmland(level, pos, player, hand, stack);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult convertToInfusedFarmland(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack hoe) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        level.setBlockAndUpdate(pos, ATEBlocks.INFUSED_FARMLAND.get().defaultBlockState());
        level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!player.getAbilities().instabuild) {
            hoe.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }
        return InteractionResult.CONSUME;
    }

    @Override
    @Nullable
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        return toolAction == ToolActions.HOE_TILL
                ? ATEBlocks.INFUSED_FARMLAND.get().defaultBlockState()
                : super.getToolModifiedState(state, context, toolAction, simulate);
    }
}