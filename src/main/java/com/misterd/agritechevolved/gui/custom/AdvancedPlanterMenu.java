package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.blockentity.custom.AdvancedPlanterBlockEntity;
import com.misterd.agritechevolved.gui.ATEMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

import static com.misterd.agritechevolved.blockentity.custom.AdvancedPlanterBlockEntity.*;

public class AdvancedPlanterMenu extends AbstractContainerMenu {

    private static final int PLAYER_INV_ROWS = 3;
    private static final int PLAYER_INV_COLS = 9;
    private static final int HOTBAR_SLOTS = 9;
    private static final int PLAYER_SLOTS = PLAYER_INV_ROWS * PLAYER_INV_COLS + HOTBAR_SLOTS;

    public final AdvancedPlanterBlockEntity blockEntity;
    private final Level level;
    private int lastEnergyStored = 0;
    private int lastGrowthProgress = 0;

    public AdvancedPlanterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public AdvancedPlanterMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.ADVANCED_PLANTER_BLOCK_MENU.get(), containerId);
        this.blockEntity = (AdvancedPlanterBlockEntity) blockEntity;
        this.level = inv.player.level();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addBlockEntitySlots();
        addDataSlots();
    }

    private void addBlockEntitySlots() {
        addSlot(new InputSlot(blockEntity, SLOT_PLANT, 8, 19, true));
        addSlot(new InputSlot(blockEntity, SLOT_SOIL, 8, 55, true));
        addSlot(new InputSlot(blockEntity, SLOT_MODULE_1, 134, 19, true));
        addSlot(new InputSlot(blockEntity, SLOT_MODULE_2, 134, 37, true));
        addSlot(new InputSlot(blockEntity, SLOT_FERTILIZER, 134, 55, false));
        int outputIndex = SLOT_OUTPUT_MIN;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                addSlot(new OutputSlot(blockEntity, outputIndex++, 44 + col * 18, 19 + row * 18));
            }
        }
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < PLAYER_INV_ROWS; row++) {
            for (int col = 0; col < PLAYER_INV_COLS; col++) {
                addSlot(new Slot(inv, col + row * PLAYER_INV_COLS + HOTBAR_SLOTS, 8 + col * 18, 88 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            addSlot(new Slot(inv, i, 8 + i * 18, 146));
        }
    }

    private void addDataSlots() {
        addDataSlot(new DataSlot() {
            @Override public int get() { return blockEntity.getEnergyStored() & 0xFFFF; }
            @Override public void set(int value) { lastEnergyStored = (lastEnergyStored & 0xFFFF0000) | (value & 0xFFFF); }
        });
        addDataSlot(new DataSlot() {
            @Override public int get() { return (blockEntity.getEnergyStored() >> 16) & 0xFFFF; }
            @Override public void set(int value) { lastEnergyStored = (lastEnergyStored & 0xFFFF) | ((value & 0xFFFF) << 16); }
        });
        addDataSlot(new DataSlot() {
            @Override public int get() { return Math.round(blockEntity.getGrowthProgress() * 1000.0F); }
            @Override public void set(int value) { lastGrowthProgress = value; }
        });
    }

    public int getEnergyStored() { return level.isClientSide ? lastEnergyStored : blockEntity.getEnergyStored(); }
    public int getMaxEnergyStored() { return blockEntity.getMaxEnergyStored(); }
    public float getGrowthProgress() { return level.isClientSide ? lastGrowthProgress / 1000.0F : blockEntity.getGrowthProgress(); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();

        if (index < PLAYER_SLOTS) {
            boolean moved = tryInsertSingle(sourceStack, SLOT_PLANT)
                    || tryInsertSingle(sourceStack, SLOT_SOIL)
                    || tryInsertModules(sourceStack)
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

    private boolean tryInsertModules(ItemStack stack) {
        if (!AdvancedPlanterBlockEntity.isModule(stack)) return false;
        boolean moved = false;
        for (int slot = SLOT_MODULE_1; slot <= SLOT_MODULE_2 && !stack.isEmpty(); slot++) {
            if (tryInsertSingle(stack, slot)) moved = true;
        }
        return moved;
    }

    private boolean tryInsertFertilizer(ItemStack stack) {
        if (!AdvancedPlanterBlockEntity.isFertilizer(stack)) return false;
        ItemStack remainder = blockEntity.inventory.insertItem(SLOT_FERTILIZER, stack.copy(), false);
        int inserted = stack.getCount() - remainder.getCount();
        if (inserted <= 0) return false;
        stack.shrink(inserted);
        return true;
    }

    private static boolean canPlace(AdvancedPlanterBlockEntity be, int planterSlot, ItemStack stack) {
        if (stack.isEmpty()) return false;
        return switch (planterSlot) {
            case SLOT_PLANT -> {
                if (!be.isValidPlant(stack)) yield false;
                ItemStack soil = be.inventory.getStackInSlot(SLOT_SOIL);
                yield soil.isEmpty() || be.isValidPlantSoilCombination(stack, soil);
            }
            case SLOT_SOIL -> {
                if (!be.isValidSoilForAnyRecipe(stack)) yield false;
                ItemStack plant = be.inventory.getStackInSlot(SLOT_PLANT);
                yield plant.isEmpty() || be.isValidPlantSoilCombination(plant, stack);
            }
            case SLOT_MODULE_1, SLOT_MODULE_2 -> AdvancedPlanterBlockEntity.isModule(stack);
            case SLOT_FERTILIZER -> AdvancedPlanterBlockEntity.isFertilizer(stack);
            default -> false;
        };
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ATEBlocks.ADVANCED_PLANTER.get());
    }

    private static class InputSlot extends SlotItemHandler {
        private final AdvancedPlanterBlockEntity be;
        private final int planterSlot;
        private final boolean single;

        InputSlot(AdvancedPlanterBlockEntity be, int planterSlot, int x, int y, boolean single) {
            super(be.inventory, planterSlot, x, y);
            this.be = be;
            this.planterSlot = planterSlot;
            this.single = single;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return canPlace(be, planterSlot, stack);
        }

        @Override
        public int getMaxStackSize() {
            return single ? 1 : super.getMaxStackSize();
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return single ? 1 : super.getMaxStackSize(stack);
        }
    }

    private static class OutputSlot extends SlotItemHandler {
        OutputSlot(AdvancedPlanterBlockEntity be, int index, int x, int y) {
            super(be.inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}