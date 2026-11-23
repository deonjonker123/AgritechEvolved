package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.block.custom.PlanterBlock;
import com.misterd.agritechevolved.blockentity.custom.PlanterBlockEntity;
import com.misterd.agritechevolved.gui.ATEMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BasicPlanterMenu extends AbstractContainerMenu {
    private static final int PLAYER_SLOTS = 36;
    private static final int SEED_SLOT = 0;
    private static final int SOIL_SLOT = 1;
    private static final int FERTILIZER_SLOT = 2;
    private static final int OUTPUT_FIRST = 3;

    public final PlanterBlockEntity blockEntity;
    private final Level level;

    public BasicPlanterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BasicPlanterMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.PLANTER_BLOCK_MENU.get(), containerId);
        this.blockEntity = (PlanterBlockEntity) blockEntity;
        this.level = inv.player.level();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addSlot(new SingleItemSlot(this.blockEntity, SEED_SLOT, 8, 18));
        addSlot(new SingleItemSlot(this.blockEntity, SOIL_SLOT, 8, 54));
        addSlot(new FertilizerSlot(this.blockEntity.inventory, FERTILIZER_SLOT, 152, 18));
        int slotIndex = OUTPUT_FIRST;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                addSlot(new OutputSlot(this.blockEntity.inventory, slotIndex++, 62 + col * 18, 18 + row * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();

        if (index < PLAYER_SLOTS) {
            boolean moved = tryInsertSingle(sourceStack, SEED_SLOT)
                    || tryInsertSingle(sourceStack, SOIL_SLOT)
                    || tryInsertFertilizer(sourceStack);
            if (moved) {
                if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
                else sourceSlot.setChanged();
            }
            return ItemStack.EMPTY;
        }

        ItemStack copyOfSourceStack = sourceStack.copy();
        if (!moveItemStackTo(sourceStack, 0, PLAYER_SLOTS, true)) return ItemStack.EMPTY;
        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    private boolean tryInsertSingle(ItemStack stack, int planterSlot) {
        if (!blockEntity.inventory.getStackInSlot(planterSlot).isEmpty()) return false;
        if (!canPlace(blockEntity, planterSlot, stack)) return false;
        blockEntity.inventory.setStackInSlot(planterSlot, stack.copyWithCount(1));
        stack.shrink(1);
        return true;
    }

    private boolean tryInsertFertilizer(ItemStack stack) {
        if (!PlanterBlockEntity.isFertilizer(stack)) return false;
        ItemStack remainder = blockEntity.inventory.insertItem(FERTILIZER_SLOT, stack.copy(), false);
        int inserted = stack.getCount() - remainder.getCount();
        if (inserted <= 0) return false;
        stack.shrink(inserted);
        return true;
    }

    private static boolean canPlace(PlanterBlockEntity be, int planterSlot, ItemStack stack) {
        if (stack.isEmpty()) return false;
        return switch (planterSlot) {
            case SEED_SLOT -> {
                if (!be.isValidPlant(stack)) yield false;
                ItemStack soil = be.inventory.getStackInSlot(SOIL_SLOT);
                yield soil.isEmpty() || be.isValidPlantSoilCombination(stack, soil);
            }
            case SOIL_SLOT -> {
                if (!be.isValidSoilForAnyRecipe(stack)) yield false;
                ItemStack plant = be.inventory.getStackInSlot(SEED_SLOT);
                yield plant.isEmpty() || be.isValidPlantSoilCombination(plant, stack);
            }
            case FERTILIZER_SLOT -> PlanterBlockEntity.isFertilizer(stack);
            default -> false;
        };
    }

    @Override
    public boolean stillValid(Player player) {
        Block block = this.blockEntity.getBlockState().getBlock();
        return block instanceof PlanterBlock && stillValid(ContainerLevelAccess.create(this.level, this.blockEntity.getBlockPos()), player, block);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 88 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 146));
        }
    }

    private static class SingleItemSlot extends SlotItemHandler {
        private final PlanterBlockEntity be;
        private final int planterSlot;

        SingleItemSlot(PlanterBlockEntity be, int planterSlot, int x, int y) {
            super(be.inventory, planterSlot, x, y);
            this.be = be;
            this.planterSlot = planterSlot;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return canPlace(be, planterSlot, stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return 1;
        }
    }

    private static class FertilizerSlot extends SlotItemHandler {
        FertilizerSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return PlanterBlockEntity.isFertilizer(stack);
        }
    }

    private static class OutputSlot extends SlotItemHandler {
        OutputSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}