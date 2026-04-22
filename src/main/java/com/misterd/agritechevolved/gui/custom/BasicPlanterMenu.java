package com.misterd.agritechevolved.gui.custom;

import com.misterd.agritechevolved.block.custom.PlanterBlock;
import com.misterd.agritechevolved.blockentity.custom.PlanterBlockEntity;
import com.misterd.agritechevolved.config.PlantablesConfig;
import com.misterd.agritechevolved.gui.ATEMenuTypes;
import com.misterd.agritechevolved.util.RegistryHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class BasicPlanterMenu extends AbstractContainerMenu {

    public final PlanterBlockEntity blockEntity;
    private final Level level;

    private static final int PLAYER_SLOTS            = 36;
    private static final int TE_INVENTORY_FIRST_SLOT = 36;
    private static final int TE_INVENTORY_SLOT_COUNT = 15; // 0=plant,1=soil,2=fertilizer,3-14=output

    public BasicPlanterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BasicPlanterMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.PLANTER_BLOCK_MENU.get(), containerId);
        this.blockEntity = (PlanterBlockEntity) blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addSlot(new PlanterSlot(this.blockEntity, 0, 8,   18));  // plant
        addSlot(new PlanterSlot(this.blockEntity, 1, 8,   54));  // soil
        addSlot(new FertilizerSlot(this.blockEntity, 2, 152, 18)); // fertilizer

        int slotIndex = 3;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                addSlot(new PlanterSlot(this.blockEntity, slotIndex++, 62 + col * 18, 18 + row * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot source = slots.get(index);
        if (source == null || !source.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = source.getItem();
        ItemStack copy  = stack.copy();

        if (index < PLAYER_SLOTS) {
            String id = RegistryHelper.getItemId(stack);

            if (PlantablesConfig.isValidSeed(id) || PlantablesConfig.isValidSapling(id)) {
                if (blockEntity.getStack(0).isEmpty()) {
                    ItemStack soil = blockEntity.getStack(1);
                    if (!soil.isEmpty()) {
                        String soilId = RegistryHelper.getItemId(soil);
                        boolean valid = PlantablesConfig.isValidSeed(id)
                                ? PlantablesConfig.isSoilValidForSeed(soilId, id)
                                : PlantablesConfig.isSoilValidForSapling(soilId, id);
                        if (!valid) return ItemStack.EMPTY;
                    }
                    insertSingle(stack, 0);
                    return copy;
                }
            } else if (PlantablesConfig.isValidSoil(id) && blockEntity.getStack(1).isEmpty()) {
                ItemStack plant = blockEntity.getStack(0);
                if (!plant.isEmpty()) {
                    String plantId = RegistryHelper.getItemId(plant);
                    boolean valid = PlantablesConfig.isValidSeed(plantId)
                            ? PlantablesConfig.isSoilValidForSeed(id, plantId)
                            : PlantablesConfig.isSoilValidForSapling(id, plantId);
                    if (!valid) return ItemStack.EMPTY;
                }
                insertSingle(stack, 1);
                return copy;
            } else if (PlantablesConfig.isValidFertilizer(id)) {
                if (!moveItemStackTo(stack, TE_INVENTORY_FIRST_SLOT + 2, TE_INVENTORY_FIRST_SLOT + 3, false))
                    return ItemStack.EMPTY;
            } else {
                if (!moveItemStackTo(stack, TE_INVENTORY_FIRST_SLOT, TE_INVENTORY_FIRST_SLOT + TE_INVENTORY_SLOT_COUNT, false))
                    return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(stack, 0, PLAYER_SLOTS, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) source.set(ItemStack.EMPTY);
        else source.setChanged();

        source.onTake(player, stack);
        return copy;
    }

    private void insertSingle(ItemStack stack, int slot) {
        try (Transaction tx = Transaction.openRoot()) {
            blockEntity.inventory.insert(slot, ItemResource.of(stack), 1, tx);
            tx.commit();
        }
        stack.shrink(1);
    }

    @Override
    public boolean stillValid(Player player) {
        Block block = blockEntity.getBlockState().getBlock();
        return block instanceof PlanterBlock
                && stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, block);
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 88 + row * 18));
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inv, i, 8 + i * 18, 147));
    }

    // -------------------------------------------------------------------------
    // Custom slots
    // -------------------------------------------------------------------------

    private static class PlanterSlot extends Slot {
        private final PlanterBlockEntity be;
        private final int index;

        PlanterSlot(PlanterBlockEntity be, int index, int x, int y) {
            super(new SimpleContainer(be.inventory.size()), index, x, y);
            this.be    = be;
            this.index = index;
        }

        @Override public ItemStack getItem() { return be.getStack(index); }

        @Override
        public void set(ItemStack stack) {
            try (Transaction tx = Transaction.openRoot()) {
                ItemStack existing = be.getStack(index);
                if (!existing.isEmpty())
                    be.inventory.extract(index, ItemResource.of(existing), existing.getCount(), tx);
                if (!stack.isEmpty())
                    be.inventory.insert(index, ItemResource.of(stack), stack.getCount(), tx);
                tx.commit();
            }
            setChanged();
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return be.inventory.isValid(index, ItemResource.of(stack));
        }

        @Override
        public ItemStack remove(int amount) {
            ItemStack existing = getItem();
            if (existing.isEmpty()) return ItemStack.EMPTY;
            int toExtract = Math.min(amount, existing.getCount());
            try (Transaction tx = Transaction.openRoot()) {
                int extracted = be.inventory.extract(index, ItemResource.of(existing), toExtract, tx);
                tx.commit();
                return new ItemStack(existing.getItem(), extracted);
            }
        }
    }

    private static class FertilizerSlot extends PlanterSlot {
        FertilizerSlot(PlanterBlockEntity be, int index, int x, int y) {
            super(be, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return PlantablesConfig.isValidFertilizer(RegistryHelper.getItemId(stack));
        }
    }
}