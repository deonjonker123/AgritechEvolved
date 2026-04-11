package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.blockentity.custom.AdvancedPlanterBlockEntity;
import com.misterd.agritechevolved.config.PlantablesConfig;
import com.misterd.agritechevolved.gui.ATEMenuTypes;
import com.misterd.agritechevolved.util.ATETags;
import com.misterd.agritechevolved.util.RegistryHelper;
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
import net.neoforged.neoforge.items.SlotItemHandler;

public class AdvancedPlanterMenu extends AbstractContainerMenu {

    // -------------------------------------------------------------------------
    // Slot layout constants
    // -------------------------------------------------------------------------

    private static final int PLAYER_INV_ROWS    = 3;
    private static final int PLAYER_INV_COLS    = 9;
    private static final int HOTBAR_SLOTS       = 9;
    private static final int PLAYER_SLOTS       = PLAYER_INV_ROWS * PLAYER_INV_COLS + HOTBAR_SLOTS; // 36

    private static final int TE_FIRST_SLOT      = PLAYER_SLOTS;       // 36
    private static final int TE_SLOT_COUNT      = 17;
    private static final int TE_LAST_SLOT       = TE_FIRST_SLOT + TE_SLOT_COUNT; // 53

    // Block entity slot indices (mirrors AdvancedPlanterBlockEntity)
    private static final int SLOT_PLANT      = 0;
    private static final int SLOT_SOIL       = 1;
    private static final int SLOT_MODULE_1   = 2;
    private static final int SLOT_MODULE_2   = 3;
    private static final int SLOT_FERTILIZER = 4;
    private static final int SLOT_OUTPUT_MIN = 5;
    private static final int SLOT_OUTPUT_MAX = 16;

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    public final AdvancedPlanterBlockEntity blockEntity;
    private final Level level;

    // Synced values (client-side mirrors)
    private int lastEnergyStored   = 0;
    private int lastGrowthProgress = 0;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public AdvancedPlanterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public AdvancedPlanterMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.ADVANCED_PLANTER_BLOCK_MENU.get(), containerId);
        this.blockEntity = (AdvancedPlanterBlockEntity) blockEntity;
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
        // Input slots
        addSlot(new SlotItemHandler(blockEntity.inventory, SLOT_PLANT,      8,   16));
        addSlot(new SlotItemHandler(blockEntity.inventory, SLOT_SOIL,       8,   52));
        addSlot(new SlotItemHandler(blockEntity.inventory, SLOT_MODULE_1,   152, 16));
        addSlot(new SlotItemHandler(blockEntity.inventory, SLOT_MODULE_2,   170, 16));
        addSlot(new SlotItemHandler(blockEntity.inventory, SLOT_FERTILIZER, 161, 52));

        // Output grid (3 rows × 4 cols)
        int outputIndex = SLOT_OUTPUT_MIN;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                addSlot(new SlotItemHandler(blockEntity.inventory, outputIndex++,
                        62 + col * 18, 16 + row * 18));
            }
        }
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < PLAYER_INV_ROWS; row++) {
            for (int col = 0; col < PLAYER_INV_COLS; col++) {
                addSlot(new Slot(inv, col + row * PLAYER_INV_COLS + HOTBAR_SLOTS,
                        26 + col * 18, 86 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            addSlot(new Slot(inv, i, 26 + i * 18, 144));
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
            @Override public int get()           { return Math.round(blockEntity.getGrowthProgress() * 1000.0F); }
            @Override public void set(int value) { lastGrowthProgress = value; }
        });
    }

    // -------------------------------------------------------------------------
    // Data accessors (client-safe)
    // -------------------------------------------------------------------------

    public int getEnergyStored() {
        return level.isClientSide ? lastEnergyStored : blockEntity.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return blockEntity.getMaxEnergyStored();
    }

    public float getGrowthProgress() {
        return level.isClientSide ? lastGrowthProgress / 1000.0F : blockEntity.getGrowthProgress();
    }

    // -------------------------------------------------------------------------
    // Shift-click
    // -------------------------------------------------------------------------

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot source = slots.get(index);
        if (source == null || !source.hasItem()) return ItemStack.EMPTY;

        ItemStack stack    = source.getItem();
        ItemStack stackCopy = stack.copy();

        if (index < PLAYER_SLOTS) {
            // Player → block entity: try to place into the appropriate input slot
            if (!tryMoveToBlockEntity(stack)) {
                // Fallback: shift into any TE slot
                if (!moveItemStackTo(stack, TE_FIRST_SLOT, TE_LAST_SLOT, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else {
            // Block entity → player inventory
            if (index >= TE_LAST_SLOT) {
                return ItemStack.EMPTY; // invalid
            }
            if (!moveItemStackTo(stack, 0, PLAYER_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            source.set(ItemStack.EMPTY);
        } else {
            source.setChanged();
        }
        source.onTake(player, stack);
        return stackCopy;
    }

    /**
     * Attempts to shift-click a stack from the player's inventory into the
     * correct block-entity slot. Returns true if the item was placed.
     */
    private boolean tryMoveToBlockEntity(ItemStack stack) {
        String id = RegistryHelper.getItemId(stack);

        // Seed or sapling → slot 0
        if (PlantablesConfig.isValidSeed(id) || PlantablesConfig.isValidSapling(id)) {
            if (!blockEntity.inventory.getStackInSlot(SLOT_PLANT).isEmpty()) return false;
            if (!isSeedSoilCompatible(id, blockEntity.inventory.getStackInSlot(SLOT_SOIL))) return false;
            placeSingle(stack, SLOT_PLANT);
            return true;
        }

        // Soil → slot 1
        if (PlantablesConfig.isValidSoil(id)) {
            if (!blockEntity.inventory.getStackInSlot(SLOT_SOIL).isEmpty()) return false;
            if (!isSoilPlantCompatible(id, blockEntity.inventory.getStackInSlot(SLOT_PLANT))) return false;
            placeSingle(stack, SLOT_SOIL);
            return true;
        }

        // Fertilizer → slot 4
        if (PlantablesConfig.isValidFertilizer(id)) {
            if (!blockEntity.inventory.getStackInSlot(SLOT_FERTILIZER).isEmpty()) return false;
            placeSingle(stack, SLOT_FERTILIZER);
            return true;
        }

        // Module → first empty module slot (2 or 3)
        if (stack.is(ATETags.Items.ATE_MODULES)) {
            for (int slot = SLOT_MODULE_1; slot <= SLOT_MODULE_2; slot++) {
                if (blockEntity.inventory.getStackInSlot(slot).isEmpty()) {
                    placeSingle(stack, slot);
                    return true;
                }
            }
        }

        return false;
    }

    // -------------------------------------------------------------------------
    // Compatibility helpers
    // -------------------------------------------------------------------------

    /** Returns true if the seed/sapling is compatible with the existing soil (or soil is absent). */
    private boolean isSeedSoilCompatible(String seedId, ItemStack soilStack) {
        if (soilStack.isEmpty()) return true;
        String soilId = RegistryHelper.getItemId(soilStack);
        if (PlantablesConfig.isValidSeed(seedId))    return PlantablesConfig.isSoilValidForSeed(soilId, seedId);
        if (PlantablesConfig.isValidSapling(seedId)) return PlantablesConfig.isSoilValidForSapling(soilId, seedId);
        return false;
    }

    /** Returns true if the soil is compatible with the existing plant (or plant slot is absent). */
    private boolean isSoilPlantCompatible(String soilId, ItemStack plantStack) {
        if (plantStack.isEmpty()) return true;
        String plantId = RegistryHelper.getItemId(plantStack);
        if (PlantablesConfig.isValidSeed(plantId))    return PlantablesConfig.isSoilValidForSeed(soilId, plantId);
        if (PlantablesConfig.isValidSapling(plantId)) return PlantablesConfig.isSoilValidForSapling(soilId, plantId);
        return false;
    }

    /** Places one item from {@code stack} into the given inventory slot. */
    private void placeSingle(ItemStack stack, int slot) {
        blockEntity.inventory.setStackInSlot(slot, stack.copyWithCount(1));
        stack.shrink(1);
    }

    // -------------------------------------------------------------------------
    // Validity check
    // -------------------------------------------------------------------------

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ATEBlocks.ADVANCED_PLANTER.get());
    }
}
