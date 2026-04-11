package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.blockentity.custom.ComposterBlockEntity;
import com.misterd.agritechevolved.config.CompostableConfig;
import com.misterd.agritechevolved.gui.ATEMenuTypes;
import com.misterd.agritechevolved.util.RegistryHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ComposterMenu extends AbstractContainerMenu {

    // -------------------------------------------------------------------------
    // Slot layout
    // -------------------------------------------------------------------------

    private static final int INPUT_SLOTS_START  = 0;
    private static final int INPUT_SLOTS_COUNT  = 12;
    private static final int OUTPUT_SLOTS_START = 12;
    private static final int OUTPUT_SLOTS_COUNT = 3;
    private static final int MODULE_SLOT        = 15;

    // Player inventory layout
    private static final int PLAYER_INV_ROWS    = 3;
    private static final int PLAYER_INV_COLS    = 9;
    private static final int HOTBAR_SLOTS       = 9;
    private static final int PLAYER_SLOTS       = PLAYER_INV_ROWS * PLAYER_INV_COLS + HOTBAR_SLOTS; // 36

    // TE slot indices in the combined slot list (player slots come first)
    private static final int TE_FIRST_SLOT      = PLAYER_SLOTS;                                     // 36
    private static final int TE_INPUT_END       = TE_FIRST_SLOT + INPUT_SLOTS_COUNT;                // 48
    private static final int TE_OUTPUT_END      = TE_INPUT_END  + OUTPUT_SLOTS_COUNT;               // 51
    private static final int TE_MODULE_SLOT     = TE_OUTPUT_END;                                    // 51
    private static final int TE_LAST_SLOT       = TE_MODULE_SLOT + 1;                               // 52

    // Module IDs
    private static final String SM_MK1 = "agritechevolved:sm_mk1";
    private static final String SM_MK2 = "agritechevolved:sm_mk2";
    private static final String SM_MK3 = "agritechevolved:sm_mk3";

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    public final ComposterBlockEntity blockEntity;
    private final Level level;
    private int lastEnergyStored = 0;
    private int lastProgress     = 0;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ComposterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ComposterMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.COMPOSTER_MENU.get(), containerId);
        this.blockEntity = (ComposterBlockEntity) blockEntity;
        this.level       = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addBlockEntitySlots();
        addDataSlots();
    }

    // -------------------------------------------------------------------------
    // Slot setup
    // -------------------------------------------------------------------------

    private void addBlockEntitySlots() {
        // Input slots (3 rows x 4 cols)
        int idx = INPUT_SLOTS_START;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                addSlot(new CompostableInputSlot(blockEntity.inventory, idx++, 8 + col * 18, 15 + row * 18));
            }
        }

        // Output slots (vertical column)
        for (int i = 0; i < OUTPUT_SLOTS_COUNT; i++) {
            addSlot(new OutputOnlySlot(blockEntity.inventory, OUTPUT_SLOTS_START + i, 98, 15 + i * 18));
        }

        // Module slot
        addSlot(new ModuleSlot(blockEntity.inventory, MODULE_SLOT, 134, 15));
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < PLAYER_INV_ROWS; row++) {
            for (int col = 0; col < PLAYER_INV_COLS; col++) {
                addSlot(new Slot(inv, col + row * PLAYER_INV_COLS + HOTBAR_SLOTS, 8 + col * 18, 81 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            addSlot(new Slot(inv, i, 8 + i * 18, 140));
        }
    }

    // -------------------------------------------------------------------------
    // Data slots (server → client sync)
    // -------------------------------------------------------------------------

    private void addDataSlots() {
        addDataSlot(new DataSlot() {
            @Override public int get()           { return blockEntity.getEnergyStored(); }
            @Override public void set(int value) { lastEnergyStored = value; }
        });
        addDataSlot(new DataSlot() {
            @Override public int get()           { return blockEntity.getProgress(); }
            @Override public void set(int value) { lastProgress = value; }
        });
    }

    // -------------------------------------------------------------------------
    // Data accessors (client-safe)
    // -------------------------------------------------------------------------

    public int getEnergyStored()          { return level.isClientSide ? lastEnergyStored : blockEntity.getEnergyStored(); }
    public int getMaxEnergyStored()       { return blockEntity.getMaxEnergyStored(); }
    public int getProgress()              { return level.isClientSide ? lastProgress : blockEntity.getProgress(); }
    public int getMaxProgress()           { return blockEntity.getMaxProgress(); }
    public int getOrganicItemsCollected() { return blockEntity.getOrganicItemsCollected(); }
    public int getRequiredOrganicItems()  { return blockEntity.getRequiredOrganicItems(); }

    // -------------------------------------------------------------------------
    // Shift-click
    // -------------------------------------------------------------------------

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot source = slots.get(index);
        if (source == null || !source.hasItem()) return ItemStack.EMPTY;

        ItemStack stack     = source.getItem();
        ItemStack stackCopy = stack.copy();

        if (index < PLAYER_SLOTS) {
            // Player → block entity
            String id = RegistryHelper.getItemId(stack);
            boolean moved;
            if (CompostableConfig.isCompostable(id)) {
                moved = moveItemStackTo(stack, TE_FIRST_SLOT, TE_INPUT_END, false);
            } else if (id.equals(SM_MK1) || id.equals(SM_MK2) || id.equals(SM_MK3)) {
                moved = moveItemStackTo(stack, TE_MODULE_SLOT, TE_LAST_SLOT, false);
            } else {
                moved = moveItemStackTo(stack, TE_FIRST_SLOT, TE_LAST_SLOT, false);
            }
            if (!moved) return ItemStack.EMPTY;
        } else {
            // Block entity → player inventory
            if (index >= TE_LAST_SLOT) return ItemStack.EMPTY;
            if (!moveItemStackTo(stack, 0, PLAYER_SLOTS, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            source.set(ItemStack.EMPTY);
        } else {
            source.setChanged();
        }

        source.onTake(player, stack);
        return stackCopy;
    }

    // -------------------------------------------------------------------------
    // Validity
    // -------------------------------------------------------------------------

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ATEBlocks.COMPOSTER.get());
    }

    // -------------------------------------------------------------------------
    // Custom slot types
    // -------------------------------------------------------------------------

    private static class CompostableInputSlot extends SlotItemHandler {
        CompostableInputSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return !stack.isEmpty() && CompostableConfig.isCompostable(RegistryHelper.getItemId(stack));
        }
    }

    private static class OutputOnlySlot extends SlotItemHandler {
        OutputOnlySlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) { return false; }
    }

    private static class ModuleSlot extends SlotItemHandler {
        ModuleSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (stack.isEmpty()) return false;
            String id = RegistryHelper.getItemId(stack);
            return id.equals(SM_MK1) || id.equals(SM_MK2) || id.equals(SM_MK3);
        }

        @Override
        public int getMaxStackSize() { return 1; }
    }
}