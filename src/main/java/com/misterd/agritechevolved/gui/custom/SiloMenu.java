package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.block.ATEBlocks;
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
import net.minecraftforge.items.SlotItemHandler;

import static com.misterd.agritechevolved.blockentity.custom.SiloBlockEntity.MODULE_SLOT;
import static com.misterd.agritechevolved.blockentity.custom.SiloBlockEntity.STORAGE_SLOTS;

public class SiloMenu extends AbstractContainerMenu {

    private static final int PLAYER_SLOTS = 36;

    public final SiloBlockEntity blockEntity;
    private final Level level;
    private int lastEnergyStored = 0;

    public SiloMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public SiloMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.SILO_MENU.get(), containerId);
        this.blockEntity = (SiloBlockEntity) blockEntity;
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
            if (SiloBlockEntity.isRangeModule(sourceStack) && blockEntity.inventory.getStackInSlot(MODULE_SLOT).isEmpty()) {
                blockEntity.inventory.setStackInSlot(MODULE_SLOT, sourceStack.copyWithCount(1));
                sourceStack.shrink(1);
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
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ATEBlocks.SILO.get());
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
        StorageSlot(SiloBlockEntity be, int index, int x, int y) {
            super(be.inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    private static class ModuleSlot extends SlotItemHandler {
        ModuleSlot(SiloBlockEntity be, int index, int x, int y) {
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