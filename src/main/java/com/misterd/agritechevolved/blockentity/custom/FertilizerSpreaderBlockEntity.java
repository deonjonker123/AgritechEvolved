package com.misterd.agritechevolved.blockentity.custom;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.custom.FertilizerSpreaderBlock;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.datamap.ATEDataMaps;
import com.misterd.agritechevolved.gui.custom.FertilizerSpreaderMenu;
import com.misterd.agritechevolved.util.ATETags;
import com.misterd.agritechevolved.util.RegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FertilizerSpreaderBlockEntity extends BlockEntity implements MenuProvider {

    public static final int STORAGE_SLOTS = 63;
    public static final int MODULE_SLOT = 63;
    public static final int TOTAL_SLOTS = 64;

    private static final String RM_MK1 = "agritechevolved:rm_mk1";
    private static final String RM_MK2 = "agritechevolved:rm_mk2";
    private static final String RM_MK3 = "agritechevolved:rm_mk3";

    private static final int MIN_RESCAN_INTERVAL_TICKS = 20;

    private int energyStored = 0;
    private int tickCounter = 0;
    private int roundRobinIndex = 0;
    private int scanAge = MIN_RESCAN_INTERVAL_TICKS;
    private List<BlockPos> cachedTargets = new ArrayList<>();

    public final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return slot == MODULE_SLOT ? 1 : stack.getMaxStackSize();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (slot == MODULE_SLOT) return SiloBlockEntity.isRangeModule(stack);
            return slot < STORAGE_SLOTS && isFertilizer(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            FertilizerSpreaderBlockEntity.this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final IItemHandler externalItemHandler = new IItemHandler() {
        @Override public int getSlots() { return inventory.getSlots(); }
        @Override public @NotNull ItemStack getStackInSlot(int slot) { return inventory.getStackInSlot(slot); }
        @Override public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return inventory.insertItem(slot, stack, simulate);
        }
        @Override public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
        @Override public int getSlotLimit(int slot) { return inventory.getSlotLimit(slot); }
        @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) { return inventory.isItemValid(slot, stack); }
    };

    private final IEnergyStorage energyHandler = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = Math.min(maxReceive, getMaxEnergyStored() - energyStored);
            if (received <= 0) return 0;
            if (!simulate) { energyStored += received; setChanged(); }
            return received;
        }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return 0; }
        @Override public int getEnergyStored() { return energyStored; }
        @Override public int getMaxEnergyStored() { return Config.getFertilizerSpreaderEnergyBuffer(); }
        @Override public boolean canExtract() { return false; }
        @Override public boolean canReceive() { return true; }
    };

    private LazyOptional<IItemHandler> itemHandlerOptional = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> energyOptional = LazyOptional.empty();

    public FertilizerSpreaderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.FERTILIZER_SPREADER_BE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FertilizerSpreaderBlockEntity be) {
        if (level.isClientSide()) return;

        be.tickCounter++;
        if (be.tickCounter < Config.getFertilizerSpreaderPushInterval()) return;
        be.tickCounter = 0;

        int required = Config.getFertilizerSpreaderBasePowerConsumption();
        boolean hasPower = be.energyStored >= required;

        boolean changed = false;
        if (hasPower) {
            be.energyStored -= required;
            be.scanAge += Config.getFertilizerSpreaderPushInterval();
            if (be.scanAge >= MIN_RESCAN_INTERVAL_TICKS) {
                be.rescanTargets(level, pos);
                be.scanAge = 0;
            }
            changed = be.distributeFertilizer(level);
        }

        if (changed) {
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }

        if (state.getValue(FertilizerSpreaderBlock.POWERED) != hasPower) {
            level.setBlock(pos, state.setValue(FertilizerSpreaderBlock.POWERED, hasPower), 3);
        }
    }

    private void rescanTargets(Level level, BlockPos pos) {
        int range = getRange();
        List<BlockPos> targets = new ArrayList<>();
        int chunkRadius = (range >> 4) + 1;
        int centerChunkX = pos.getX() >> 4;
        int centerChunkZ = pos.getZ() >> 4;

        for (int cx = -chunkRadius; cx <= chunkRadius; cx++) {
            for (int cz = -chunkRadius; cz <= chunkRadius; cz++) {
                int chunkX = centerChunkX + cx;
                int chunkZ = centerChunkZ + cz;
                if (!level.hasChunk(chunkX, chunkZ)) continue;

                LevelChunk chunk = level.getChunk(chunkX, chunkZ);
                for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                    BlockPos bePos = entry.getKey();
                    if (Math.abs(bePos.getX() - pos.getX()) > range) continue;
                    if (Math.abs(bePos.getY() - pos.getY()) > range) continue;
                    if (Math.abs(bePos.getZ() - pos.getZ()) > range) continue;

                    BlockEntity be = entry.getValue();
                    if (be instanceof PlanterBlockEntity || be instanceof AdvancedPlanterBlockEntity) {
                        targets.add(bePos.immutable());
                    }
                }
            }
        }
        cachedTargets = targets;
    }

    private boolean distributeFertilizer(Level level) {
        if (cachedTargets.isEmpty()) return false;

        int amountToPush = Config.getFertilizerSpreaderPushAmount();
        int count = cachedTargets.size();
        boolean changed = false;

        for (int i = 0; i < count; i++) {
            int idx = (roundRobinIndex + i) % count;
            if (pushToTarget(level, cachedTargets.get(idx), amountToPush)) changed = true;
        }
        roundRobinIndex = (roundRobinIndex + 1) % count;
        return changed;
    }

    private boolean pushToTarget(Level level, BlockPos targetPos, int amountToPush) {
        IItemHandler sink = getInsertHandlerFor(level.getBlockEntity(targetPos));
        if (sink == null) return false;

        for (int storageSlot = 0; storageSlot < STORAGE_SLOTS; storageSlot++) {
            ItemStack available = inventory.extractItem(storageSlot, amountToPush, true);
            if (available.isEmpty()) continue;

            for (int targetSlot = 0; targetSlot < sink.getSlots(); targetSlot++) {
                if (!sink.isItemValid(targetSlot, available)) continue;
                ItemStack remainder = sink.insertItem(targetSlot, available, true);
                int inserted = available.getCount() - remainder.getCount();
                if (inserted <= 0) continue;
                ItemStack extracted = inventory.extractItem(storageSlot, inserted, false);
                ItemStack leftover = sink.insertItem(targetSlot, extracted, false);
                if (!leftover.isEmpty()) inventory.insertItem(storageSlot, leftover, false);
                return true;
            }
        }
        return false;
    }

    @Nullable
    private IItemHandler getInsertHandlerFor(@Nullable BlockEntity be) {
        if (be instanceof AdvancedPlanterBlockEntity planter) return planter.getItemHandler(null);
        if (be instanceof PlanterBlockEntity planter) return planter.getCapabilityHandler();
        return null;
    }

    public static boolean isFertilizer(ItemStack stack) {
        return !stack.isEmpty() && ATEDataMaps.getFertilizer(stack.getItem()) != null;
    }

    private int getRangeBonus() {
        ItemStack module = inventory.getStackInSlot(MODULE_SLOT);
        if (module.isEmpty()) return 0;
        return switch (RegistryHelper.getItemId(module)) {
            case RM_MK1 -> 16;
            case RM_MK2 -> 32;
            case RM_MK3 -> 64;
            default -> 0;
        };
    }

    public int getRange() {
        return Config.getFertilizerSpreaderBaseRange() + getRangeBonus();
    }

    public int getEnergyStored() { return energyStored; }
    public int getMaxEnergyStored() { return Config.getFertilizerSpreaderEnergyBuffer(); }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) inv.setItem(i, inventory.getStackInSlot(i));
        Containers.dropContents(level, worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("energyStored", energyStored);
        tag.putInt("tickCounter", tickCounter);
        tag.putInt("roundRobinIndex", roundRobinIndex);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        energyStored = tag.getInt("energyStored");
        tickCounter = tag.getInt("tickCounter");
        roundRobinIndex = tag.getInt("roundRobinIndex");
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        itemHandlerOptional = LazyOptional.of(() -> externalItemHandler);
        energyOptional = LazyOptional.of(() -> energyHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerOptional.invalidate();
        energyOptional.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemHandlerOptional.cast();
        if (cap == ForgeCapabilities.ENERGY) return energyOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.agritechevolved.fertilizer_spreader");
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FertilizerSpreaderMenu(containerId, playerInventory, this);
    }
}