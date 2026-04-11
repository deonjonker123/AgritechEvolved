package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.block.custom.PlanterBlock;
import com.misterd.agritechevolved.blockentity.custom.PlanterBlockEntity;
import com.misterd.agritechevolved.config.PlantablesConfig;
import com.misterd.agritechevolved.gui.ATEMenuTypes;
import com.misterd.agritechevolved.util.RegistryHelper;
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
    public final PlanterBlockEntity blockEntity;
    private final Level level;

    private static final int HOTBAR_SLOT_COUNT             = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT    = 3;
    private static final int PLAYER_INVENTORY_COL_COUNT    = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT   = 27;
    private static final int VANILLA_SLOT_COUNT            = 36;
    private static final int VANILLA_FIRST_SLOT_INDEX      = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 36;
    private static final int TE_INVENTORY_SLOT_COUNT       = 14;

    public BasicPlanterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BasicPlanterMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.PLANTER_BLOCK_MENU.get(), containerId);
        this.blockEntity = (PlanterBlockEntity) blockEntity;
        this.level = inv.player.level();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addSlot(new SlotItemHandler(this.blockEntity.inventory, 0, 8, 18));
        addSlot(new SlotItemHandler(this.blockEntity.inventory, 1, 8, 54));
        addSlot(new FertilizerSlot(this.blockEntity.inventory, 2, 152, 18));
        int slotIndex = 3;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                addSlot(new SlotItemHandler(this.blockEntity.inventory, slotIndex++, 62 + col * 18, 18 + row * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < 36) {
            String sourceItemId = RegistryHelper.getItemId(sourceStack);

            if (PlantablesConfig.isValidSeed(sourceItemId) || PlantablesConfig.isValidSapling(sourceItemId)) {
                if (this.blockEntity.inventory.getStackInSlot(0).isEmpty()) {
                    ItemStack existingSoil = this.blockEntity.inventory.getStackInSlot(1);
                    if (!existingSoil.isEmpty()) {
                        String soilId = RegistryHelper.getItemId(existingSoil);
                        boolean valid = PlantablesConfig.isValidSeed(sourceItemId)
                                ? PlantablesConfig.isSoilValidForSeed(soilId, sourceItemId)
                                : PlantablesConfig.isSoilValidForSapling(soilId, sourceItemId);
                        if (!valid) return ItemStack.EMPTY;
                    }
                    this.blockEntity.inventory.setStackInSlot(0, sourceStack.copyWithCount(1));
                    sourceStack.shrink(1);
                    return copyOfSourceStack;
                }
            } else if (PlantablesConfig.isValidSoil(sourceItemId) && this.blockEntity.inventory.getStackInSlot(1).isEmpty()) {
                ItemStack existingPlant = this.blockEntity.inventory.getStackInSlot(0);
                if (!existingPlant.isEmpty()) {
                    String plantId = RegistryHelper.getItemId(existingPlant);
                    boolean valid = PlantablesConfig.isValidSeed(plantId)
                            ? PlantablesConfig.isSoilValidForSeed(sourceItemId, plantId)
                            : PlantablesConfig.isSoilValidForSapling(sourceItemId, plantId);
                    if (!valid) return ItemStack.EMPTY;
                }
                this.blockEntity.inventory.setStackInSlot(1, sourceStack.copyWithCount(1));
                sourceStack.shrink(1);
                return copyOfSourceStack;
            } else if (PlantablesConfig.isValidFertilizer(sourceItemId) && this.blockEntity.inventory.getStackInSlot(2).isEmpty()) {
                this.blockEntity.inventory.setStackInSlot(2, sourceStack.copyWithCount(1));
                sourceStack.shrink(1);
                return copyOfSourceStack;
            }

            if (!moveItemStackTo(sourceStack, 36, 50, false)) return ItemStack.EMPTY;

        } else {
            if (!moveItemStackTo(sourceStack, 0, 36, false)) return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        Block block = this.blockEntity.getBlockState().getBlock();
        return block instanceof PlanterBlock
                && stillValid(ContainerLevelAccess.create(this.level, this.blockEntity.getBlockPos()), player, block);
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
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 147));
        }
    }

    private static class FertilizerSlot extends SlotItemHandler {
        public FertilizerSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return PlantablesConfig.isValidFertilizer(RegistryHelper.getItemId(stack));
        }
    }
}