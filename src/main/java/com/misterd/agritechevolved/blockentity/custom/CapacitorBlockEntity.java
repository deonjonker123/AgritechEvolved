package com.misterd.agritechevolved.blockentity.custom;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.block.custom.CapacitorTier1Block;
import com.misterd.agritechevolved.block.custom.CapacitorTier2Block;
import com.misterd.agritechevolved.block.custom.CapacitorTier3Block;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.gui.custom.CapacitorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CapacitorBlockEntity extends BlockEntity implements MenuProvider {

    private TrackedEnergyStorage energyStorage;
    private int tier = 1;
    private int transferRate = 512;

    private LazyOptional<IEnergyStorage> energyOptional = LazyOptional.empty();

    public CapacitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.CAPACITOR_BE.get(), pos, blockState);
        initializeCapacitor(blockState);
    }

    private void initializeCapacitor(BlockState state) {
        if (state.is(ATEBlocks.CAPACITOR_TIER_2.get())) {
            tier = 2;
            transferRate = Config.getCapacitorT2TransferRate();
            energyStorage = new TrackedEnergyStorage(Config.getCapacitorT2Buffer());
        } else if (state.is(ATEBlocks.CAPACITOR_TIER_3.get())) {
            tier = 3;
            transferRate = Config.getCapacitorT3TransferRate();
            energyStorage = new TrackedEnergyStorage(Config.getCapacitorT3Buffer());
        } else {
            tier = 1;
            transferRate = Config.getCapacitorT1TransferRate();
            energyStorage = new TrackedEnergyStorage(Config.getCapacitorT1Buffer());
        }
    }

    private void notifyChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CapacitorBlockEntity be) {
        if (level == null || level.isClientSide()) return;

        boolean changed = false;

        if (be.energyStorage.getEnergyStored() > 0) {
            for (Direction dir : Direction.values()) {
                if (be.energyStorage.getEnergyStored() <= 0) break;

                BlockEntity neighborBe = level.getBlockEntity(pos.relative(dir));
                if (neighborBe == null) continue;

                IEnergyStorage neighbor = neighborBe.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).orElse(null);
                if (neighbor == null || !neighbor.canReceive()) continue;

                int toTransfer = be.energyStorage.extractEnergy(be.transferRate, true);
                if (toTransfer <= 0) continue;

                int transferred = neighbor.receiveEnergy(toTransfer, false);
                if (transferred > 0) {
                    be.energyStorage.extractEnergy(transferred, false);
                    changed = true;
                }
            }
        }

        if (changed) {
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }

        boolean hasEnergy = be.getEnergyStored() > 0;
        BooleanProperty prop = null;
        if (state.is(ATEBlocks.CAPACITOR_TIER_1.get())) prop = CapacitorTier1Block.HAS_ENERGY;
        else if (state.is(ATEBlocks.CAPACITOR_TIER_2.get())) prop = CapacitorTier2Block.HAS_ENERGY;
        else if (state.is(ATEBlocks.CAPACITOR_TIER_3.get())) prop = CapacitorTier3Block.HAS_ENERGY;

        if (prop != null && state.getValue(prop) != hasEnergy) {
            level.setBlock(pos, state.setValue(prop, hasEnergy), 3);
        }
    }

    public int getEnergyStored() { return energyStorage != null ? energyStorage.getEnergyStored() : 0; }
    public int getMaxEnergyStored() { return energyStorage != null ? energyStorage.getMaxEnergyStored() : 0; }
    public boolean canExtractEnergy() { return true; }
    public boolean canReceiveEnergy() { return true; }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        return energyStorage != null ? energyStorage.receiveEnergy(maxReceive, simulate) : 0;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        return energyStorage != null ? energyStorage.extractEnergy(maxExtract, simulate) : 0;
    }

    public IEnergyStorage getEnergyStorage() { return energyStorage; }
    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return new IEnergyStorage() {
            @Override public int receiveEnergy(int max, boolean sim) { return CapacitorBlockEntity.this.receiveEnergy(max, sim); }
            @Override public int extractEnergy(int max, boolean sim) { return CapacitorBlockEntity.this.extractEnergy(max, sim); }
            @Override public int getEnergyStored() { return CapacitorBlockEntity.this.getEnergyStored(); }
            @Override public int getMaxEnergyStored() { return CapacitorBlockEntity.this.getMaxEnergyStored(); }
            @Override public boolean canExtract() { return true; }
            @Override public boolean canReceive() { return true; }
        };
    }

    public void forceSetEnergy(int energy) {
        if (energyStorage == null) return;
        energyStorage.setEnergy(energy);
    }

    public int getTier() { return tier; }
    public int getTransferRate() { return transferRate; }
    public String getTierName() {
        return switch (tier) {
            case 1 -> "Tier 1";
            case 2 -> "Tier 2";
            case 3 -> "Tier 3";
            default -> "Unknown";
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (energyStorage != null) tag.put("energy", energyStorage.serializeNBT());
        tag.putInt("tier", tier);
        tag.putInt("transferRate", transferRate);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        initializeCapacitor(getBlockState());
        if (tag.contains("energy") && energyStorage != null) energyStorage.deserializeNBT(tag.get("energy"));
        if (tag.contains("tier")) tier = tag.getInt("tier");
        if (tag.contains("transferRate")) transferRate = tag.getInt("transferRate");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        energyOptional = LazyOptional.of(() -> getEnergyStorage(null));
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyOptional.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) return energyOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.agritechevolved.capacitor_tier" + tier);
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CapacitorMenu(containerId, playerInventory, this);
    }

    private class TrackedEnergyStorage extends EnergyStorage {

        TrackedEnergyStorage(int capacity) {
            super(capacity);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(Math.min(maxReceive, transferRate), simulate);
            if (!simulate && received > 0) notifyChanged();
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(Math.min(maxExtract, transferRate), simulate);
            if (!simulate && extracted > 0) notifyChanged();
            return extracted;
        }

        void setEnergy(int value) {
            energy = Math.max(0, Math.min(value, capacity));
        }
    }
}