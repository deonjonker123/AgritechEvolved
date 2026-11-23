package com.misterd.agritechevolved.blockentity.custom;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.custom.SiloBlock;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.gui.custom.SiloMenu;
import com.misterd.agritechevolved.util.ATETags;
import com.misterd.agritechevolved.util.RegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SiloBlockEntity extends BlockEntity implements MenuProvider {

    public static final int STORAGE_SLOTS = 63;
    public static final int MODULE_SLOT = 63;
    public static final int TOTAL_SLOTS = 64;

    private static final String RM_MK1 = "agritechevolved:rm_mk1";
    private static final String RM_MK2 = "agritechevolved:rm_mk2";
    private static final String RM_MK3 = "agritechevolved:rm_mk3";

    private static final int MIN_RESCAN_INTERVAL_TICKS = 20;

    private int energyStored = 0;
    private int tickCounter = 0;
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
            if (slot == MODULE_SLOT) return isRangeModule(stack);
            return slot < STORAGE_SLOTS;
        }

        @Override
        protected void onContentsChanged(int slot) {
            SiloBlockEntity.this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final IItemHandler storage = new RangedWrapper(inventory, 0, STORAGE_SLOTS);

    private final IItemHandler externalItemHandler = new IItemHandler() {
        @Override public int getSlots() { return inventory.getSlots(); }
        @Override public @NotNull ItemStack getStackInSlot(int slot) { return inventory.getStackInSlot(slot); }
        @Override public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return stack; }
        @Override public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot >= STORAGE_SLOTS) return ItemStack.EMPTY;
            return inventory.extractItem(slot, amount, simulate);
        }
        @Override public int getSlotLimit(int slot) { return inventory.getSlotLimit(slot); }
        @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) { return false; }
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
        @Override public int getMaxEnergyStored() { return Config.getSiloEnergyBuffer(); }
        @Override public boolean canExtract() { return false; }
        @Override public boolean canReceive() { return true; }
    };

    public SiloBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.SILO_BE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SiloBlockEntity be) {
        if (level.isClientSide()) return;

        be.tickCounter++;
        if (be.tickCounter < Config.getSiloPullInterval()) return;
        be.tickCounter = 0;

        int required = Config.getSiloBasePowerConsumption();
        boolean hasPower = be.energyStored >= required;

        boolean changed = false;
        if (hasPower) {
            be.energyStored -= required;
            be.scanAge += Config.getSiloPullInterval();
            if (be.scanAge >= MIN_RESCAN_INTERVAL_TICKS) {
                be.rescanTargets(level, pos);
                be.scanAge = 0;
            }
            for (BlockPos targetPos : be.cachedTargets) {
                if (be.pullFromTarget(level, targetPos)) changed = true;
            }
        }

        if (changed) {
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }

        if (state.getValue(SiloBlock.POWERED) != hasPower) {
            level.setBlock(pos, state.setValue(SiloBlock.POWERED, hasPower), 3);
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

    private boolean pullFromTarget(Level level, BlockPos targetPos) {
        IItemHandler source = getExtractHandlerFor(level.getBlockEntity(targetPos));
        if (source == null) return false;

        boolean changed = false;
        for (int slot = 0; slot < source.getSlots(); slot++) {
            ItemStack simulated = source.extractItem(slot, 64, true);
            if (simulated.isEmpty()) continue;
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(storage, simulated, true);
            int canInsert = simulated.getCount() - remainder.getCount();
            if (canInsert <= 0) continue;
            ItemStack extracted = source.extractItem(slot, canInsert, false);
            if (extracted.isEmpty()) continue;
            ItemStack leftover = ItemHandlerHelper.insertItemStacked(storage, extracted, false);
            if (!leftover.isEmpty()) {
                ItemStack backInto = source.insertItem(slot, leftover, false);
                if (!backInto.isEmpty()) Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, backInto);
            }
            changed = true;
        }
        return changed;
    }

    @Nullable
    private IItemHandler getExtractHandlerFor(@Nullable BlockEntity be) {
        if (be instanceof AdvancedPlanterBlockEntity planter) return planter.getOutputHandler();
        if (be instanceof PlanterBlockEntity planter) return planter.getExtractHandler();
        return null;
    }

    public static boolean isRangeModule(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ATETags.Items.ATE_RANGE_MODULES);
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
        return Config.getSiloBaseRange() + getRangeBonus();
    }

    public int getEnergyStored() { return energyStored; }
    public int getMaxEnergyStored() { return Config.getSiloEnergyBuffer(); }

    public IItemHandler getExternalItemHandler(@Nullable Direction side) { return externalItemHandler; }
    public IEnergyStorage getEnergyHandler(@Nullable Direction side) { return energyHandler; }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ATEBlockEntities.SILO_BE.get(),
                (be, dir) -> be instanceof SiloBlockEntity s ? s.getExternalItemHandler(dir) : null);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ATEBlockEntities.SILO_BE.get(),
                (be, dir) -> be instanceof SiloBlockEntity s ? s.getEnergyHandler(dir) : null);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) inv.setItem(i, inventory.getStackInSlot(i));
        Containers.dropContents(level, worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("energyStored", energyStored);
        tag.putInt("tickCounter", tickCounter);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        energyStored = tag.getInt("energyStored");
        tickCounter = tag.getInt("tickCounter");
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.agritechevolved.silo");
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SiloMenu(containerId, playerInventory, this);
    }
}