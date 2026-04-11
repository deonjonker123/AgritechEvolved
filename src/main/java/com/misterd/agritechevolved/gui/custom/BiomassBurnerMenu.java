package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.blockentity.custom.BiomassBurnerBlockEntity;
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

public class BiomassBurnerMenu extends AbstractContainerMenu {

    // -------------------------------------------------------------------------
    // Slot layout
    // -------------------------------------------------------------------------

    private static final int FUEL_SLOT      = 0;
    private static final int PLAYER_INV_ROWS = 3;
    private static final int PLAYER_INV_COLS = 9;
    private static final int HOTBAR_SLOTS    = 9;
    private static final int PLAYER_SLOTS    = PLAYER_INV_ROWS * PLAYER_INV_COLS + HOTBAR_SLOTS; // 36

    private static final int TE_FIRST_SLOT  = PLAYER_SLOTS;      // 36
    private static final int TE_LAST_SLOT   = TE_FIRST_SLOT + 1; // 37

    // Fuel IDs — mirrors BiomassBurnerBlockEntity constants
    private static final String BIOMASS                 = "agritechevolved:biomass";
    private static final String CRUDE_BIOMASS           = "agritechevolved:crude_biomass";
    private static final String COMPACTED_BIOMASS       = "agritechevolved:compacted_biomass";
    private static final String COMPACTED_BIOMASS_BLOCK = "agritechevolved:compacted_biomass_block";

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    public final BiomassBurnerBlockEntity blockEntity;
    private final Level level;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public BiomassBurnerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BiomassBurnerMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.BURNER_MENU.get(), containerId);
        this.blockEntity = (BiomassBurnerBlockEntity) blockEntity;
        this.level       = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addSlot(new BiomassInputSlot(this.blockEntity.inventory, FUEL_SLOT, 80, 32));
        addDataSlots();
    }

    // -------------------------------------------------------------------------
    // Data slots (server → client sync)
    // -------------------------------------------------------------------------

    private void addDataSlots() {
        // Energy stored
        addDataSlot(new DataSlot() {
            @Override public int get()           { return blockEntity.getEnergyStored(); }
            @Override public void set(int value) { }
        });
        // Progress
        addDataSlot(new DataSlot() {
            @Override public int get()           { return blockEntity.getProgress(); }
            @Override public void set(int value) { blockEntity.setProgress(value); }
        });
        // Max progress
        addDataSlot(new DataSlot() {
            @Override public int get()           { return blockEntity.getMaxProgress(); }
            @Override public void set(int value) { }
        });
    }

    // -------------------------------------------------------------------------
    // Data accessors
    // -------------------------------------------------------------------------

    public int getEnergyStored()    { return blockEntity.getEnergyStored(); }
    public int getMaxEnergyStored() { return blockEntity.getMaxEnergyStored(); }
    public int getProgress()        { return blockEntity.getProgress(); }
    public int getMaxProgress()     { return blockEntity.getMaxProgress(); }

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
            // Player → fuel slot (all fuel types accepted)
            if (!moveItemStackTo(stack, TE_FIRST_SLOT, TE_LAST_SLOT, false)) return ItemStack.EMPTY;
        } else {
            // Fuel slot → player inventory
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
                player, ATEBlocks.BIOMASS_BURNER.get());
    }

    // -------------------------------------------------------------------------
    // Player inventory / hotbar
    // -------------------------------------------------------------------------

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
    // Custom slot
    // -------------------------------------------------------------------------

    private static class BiomassInputSlot extends SlotItemHandler {
        BiomassInputSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (stack.isEmpty()) return false;
            String id = RegistryHelper.getItemId(stack);
            return id.equals(BIOMASS) || id.equals(CRUDE_BIOMASS)
                    || id.equals(COMPACTED_BIOMASS) || id.equals(COMPACTED_BIOMASS_BLOCK);
        }
    }
}