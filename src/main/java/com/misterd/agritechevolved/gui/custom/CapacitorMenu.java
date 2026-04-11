package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.blockentity.custom.CapacitorBlockEntity;
import com.misterd.agritechevolved.gui.ATEMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CapacitorMenu extends AbstractContainerMenu {

    public final CapacitorBlockEntity blockEntity;
    private final Level level;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public CapacitorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CapacitorMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.CAPACITOR_MENU.get(), containerId);
        this.blockEntity = (CapacitorBlockEntity) blockEntity;
        this.level       = inv.player.level();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addDataSlots();
    }

    // -------------------------------------------------------------------------
    // Data slots
    //
    // DataSlot is limited to 16-bit signed values (-32768..32767), so energy
    // values that exceed Short.MAX_VALUE must be split across two slots and
    // reassembled on the client. The low/high 16-bit split below is intentional.
    // -------------------------------------------------------------------------

    private void addDataSlots() {
        // Energy stored — low 16 bits
        addDataSlot(new DataSlot() {
            @Override public int get()           { return blockEntity.getEnergyStored() & 0xFFFF; }
            @Override public void set(int value) { }
        });
        // Energy stored — high 16 bits
        addDataSlot(new DataSlot() {
            @Override public int get()           { return (blockEntity.getEnergyStored() >> 16) & 0xFFFF; }
            @Override public void set(int value) { }
        });
        // Max energy — low 16 bits
        addDataSlot(new DataSlot() {
            @Override public int get()           { return blockEntity.getMaxEnergyStored() & 0xFFFF; }
            @Override public void set(int value) { }
        });
        // Max energy — high 16 bits
        addDataSlot(new DataSlot() {
            @Override public int get()           { return (blockEntity.getMaxEnergyStored() >> 16) & 0xFFFF; }
            @Override public void set(int value) { }
        });
        // Transfer rate
        addDataSlot(new DataSlot() {
            @Override public int get()           { return blockEntity.getTransferRate(); }
            @Override public void set(int value) { }
        });
        // Tier
        addDataSlot(new DataSlot() {
            @Override public int get()           { return blockEntity.getTier(); }
            @Override public void set(int value) { }
        });
    }

    // -------------------------------------------------------------------------
    // Data accessors
    // -------------------------------------------------------------------------

    public int    getEnergyStored()    { return blockEntity.getEnergyStored(); }
    public int    getMaxEnergyStored() { return blockEntity.getMaxEnergyStored(); }
    public int    getTransferRate()    { return blockEntity.getTransferRate(); }
    public int    getTier()            { return blockEntity.getTier(); }
    public String getTierName() {
        return switch (getTier()) {
            case 1  -> "Tier 1";
            case 2  -> "Tier 2";
            case 3  -> "Tier 3";
            default -> "Unknown";
        };
    }

    // -------------------------------------------------------------------------
    // Slot interaction
    // -------------------------------------------------------------------------

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        ContainerLevelAccess access = ContainerLevelAccess.create(level, blockEntity.getBlockPos());
        return stillValid(access, player, ATEBlocks.CAPACITOR_TIER_1.get())
                || stillValid(access, player, ATEBlocks.CAPACITOR_TIER_2.get())
                || stillValid(access, player, ATEBlocks.CAPACITOR_TIER_3.get());
    }

    // -------------------------------------------------------------------------
    // Player inventory / hotbar
    // -------------------------------------------------------------------------

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 66 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inv, i, 8 + i * 18, 125));
        }
    }
}