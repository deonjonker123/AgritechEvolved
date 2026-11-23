package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.blockentity.custom.FertilizerSpreaderBlockEntity;
import com.misterd.agritechevolved.blockentity.custom.SiloBlockEntity;
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
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

import static com.misterd.agritechevolved.blockentity.custom.FertilizerSpreaderBlockEntity.MODULE_SLOT;
import static com.misterd.agritechevolved.blockentity.custom.FertilizerSpreaderBlockEntity.STORAGE_SLOTS;

public class FertilizerSpreaderMenu extends AbstractContainerMenu {

    private static final int PLAYER_SLOTS = 36;

    public final FertilizerSpreaderBlockEntity blockEntity;
    private final Level level;
    private int lastEnergyStored = 0;

    public FertilizerSpreaderMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public FertilizerSpreaderMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.FERTILIZER_SPREADER_MENU.get(), containerId);
        this.blockEntity = (FertilizerSpreaderBlockEntity) blockEntity;
        this.level = inv.player.level();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addBlockEntitySlots();
        addDataSlots();
    }

    private void addBlockEntitySlots() {
        int idx = 0;
        for (int row = 0; row < 7; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new StorageSlot(blockEntity, idx++, 8 + col * 18, 19 + row * 18));
        addSlot(new ModuleSlot(blockEntity, MODULE_SLOT, 176, 19));
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
    }

    public int getEnergyStored() { return level.isClientSide ? lastEnergyStored : blockEntity.getEnergyStored(); }
    public int getMaxEnergyStored() { return blockEntity.getMaxEnergyStored(); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();

        if (index < PLAYER_SLOTS) {
            boolean moved = false;
            if (SiloBlockEntity.isRangeModule(sourceStack)) {
                if (blockEntity.inventory.getStackInSlot(MODULE_SLOT).isEmpty()) {
                    blockEntity.inventory.setStackInSlot(MODULE_SLOT, sourceStack.copyWithCount(1));
                    sourceStack.shrink(1);
                    moved = true;
                }
            } else if (FertilizerSpreaderBlockEntity.isFertilizer(sourceStack)) {
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(new RangedWrapper(blockEntity.inventory, 0, STORAGE_SLOTS), sourceStack.copy(), false);
                int inserted = sourceStack.getCount() - remainder.getCount();
                if (inserted > 0) {
                    sourceStack.shrink(inserted);
                    moved = true;
                }
            }
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

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ATEBlocks.FERT_SPREADER.get());
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 160 + row * 18));
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inv, i, 8 + i * 18, 218));
    }

    private static class StorageSlot extends SlotItemHandler {
        StorageSlot(FertilizerSpreaderBlockEntity be, int index, int x, int y) {
            super(be.inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return FertilizerSpreaderBlockEntity.isFertilizer(stack);
        }
    }

    private static class ModuleSlot extends SlotItemHandler {
        ModuleSlot(FertilizerSpreaderBlockEntity be, int index, int x, int y) {
            super(be.inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return SiloBlockEntity.isRangeModule(stack);
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
}