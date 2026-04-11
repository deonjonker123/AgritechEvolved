package com.misterd.agritechevolved.blockentity.custom;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.block.custom.CapacitorTier1Block;
import com.misterd.agritechevolved.block.custom.CapacitorTier2Block;
import com.misterd.agritechevolved.block.custom.CapacitorTier3Block;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.component.ATEDataComponents;
import com.misterd.agritechevolved.gui.custom.CapacitorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class CapacitorBlockEntity extends BlockEntity implements MenuProvider {

    private EnergyStorage energyStorage;
    private int tier         = 1;
    private int transferRate = 512;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public CapacitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.CAPACITOR_BE.get(), pos, blockState);
        initializeCapacitor(blockState);
    }

    // -------------------------------------------------------------------------
    // Capacitor initialisation
    // -------------------------------------------------------------------------

    private void initializeCapacitor(BlockState state) {
        if (state.is(ATEBlocks.CAPACITOR_TIER_1.get())) {
            tier         = 1;
            transferRate = Config.getCapacitorT1TransferRate();
            energyStorage = makeTrackedStorage(Config.getCapacitorT1Buffer());
        } else if (state.is(ATEBlocks.CAPACITOR_TIER_2.get())) {
            tier         = 2;
            transferRate = Config.getCapacitorT2TransferRate();
            energyStorage = makeTrackedStorage(Config.getCapacitorT2Buffer());
        } else if (state.is(ATEBlocks.CAPACITOR_TIER_3.get())) {
            tier         = 3;
            transferRate = Config.getCapacitorT3TransferRate();
            energyStorage = makeTrackedStorage(Config.getCapacitorT3Buffer());
        } else {
            tier         = 1;
            transferRate = Config.getCapacitorT1TransferRate();
            energyStorage = new EnergyStorage(Config.getCapacitorT1Buffer());
        }
    }

    /** Creates an EnergyStorage that notifies the level on every real receive/extract. */
    private EnergyStorage makeTrackedStorage(int capacity) {
        return new EnergyStorage(capacity) {
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
        };
    }

    private void notifyChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    // -------------------------------------------------------------------------
    // Tick
    // -------------------------------------------------------------------------

    public static void tick(Level level, BlockPos pos, BlockState state, CapacitorBlockEntity be) {
        if (level == null || level.isClientSide()) return;

        boolean changed = false;

        if (be.energyStorage.getEnergyStored() > 0) {
            for (Direction dir : Direction.values()) {
                if (dir == Direction.DOWN) continue;
                if (be.energyStorage.getEnergyStored() <= 0) break;

                BlockPos neighborPos = pos.relative(dir);
                if (level.getBlockEntity(neighborPos) == null) continue;

                IEnergyStorage neighbor = level.getCapability(
                        Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
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

        // Sync the has_energy blockstate property
        boolean hasEnergy = be.getEnergyStored() > 0;
        BooleanProperty prop = null;
        if      (state.is(ATEBlocks.CAPACITOR_TIER_1.get())) prop = CapacitorTier1Block.HAS_ENERGY;
        else if (state.is(ATEBlocks.CAPACITOR_TIER_2.get())) prop = CapacitorTier2Block.HAS_ENERGY;
        else if (state.is(ATEBlocks.CAPACITOR_TIER_3.get())) prop = CapacitorTier3Block.HAS_ENERGY;

        if (prop != null && state.getValue(prop) != hasEnergy) {
            level.setBlock(pos, state.setValue(prop, hasEnergy), 3);
        }
    }

    // -------------------------------------------------------------------------
    // Energy API
    // -------------------------------------------------------------------------

    public int getEnergyStored()    { return energyStorage != null ? energyStorage.getEnergyStored()    : 0; }
    public int getMaxEnergyStored() { return energyStorage != null ? energyStorage.getMaxEnergyStored() : 0; }
    public boolean canExtractEnergy() { return true; }
    public boolean canReceiveEnergy() { return true; }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        return energyStorage != null ? energyStorage.receiveEnergy(maxReceive, simulate) : 0;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        return energyStorage != null ? energyStorage.extractEnergy(maxExtract, simulate) : 0;
    }

    public IEnergyStorage getEnergyStorage()                 { return energyStorage; }
    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        if (side == Direction.DOWN) return null;
        return new IEnergyStorage() {
            @Override public int  receiveEnergy(int max, boolean sim) { return CapacitorBlockEntity.this.receiveEnergy(max, sim); }
            @Override public int  extractEnergy(int max, boolean sim) { return CapacitorBlockEntity.this.extractEnergy(max, sim); }
            @Override public int  getEnergyStored()                   { return CapacitorBlockEntity.this.getEnergyStored(); }
            @Override public int  getMaxEnergyStored()                { return CapacitorBlockEntity.this.getMaxEnergyStored(); }
            @Override public boolean canExtract()                     { return true; }
            @Override public boolean canReceive()                     { return true; }
        };
    }

    // -------------------------------------------------------------------------
    // Force-set energy (used on block placement to restore data-component energy)
    // -------------------------------------------------------------------------

    public void forceSetEnergy(int energy) {
        if (energyStorage == null) return;
        int clamped = Math.min(energy, energyStorage.getMaxEnergyStored());
        try {
            Field f = EnergyStorage.class.getDeclaredField("energy");
            f.setAccessible(true);
            f.setInt(energyStorage, clamped);
        } catch (Exception e) {
            // Fallback: pump energy in via the normal API
            int current;
            while ((current = energyStorage.getEnergyStored()) < clamped
                    && energyStorage.receiveEnergy(clamped - current, false) > 0) {
                // loop until full or no progress
            }
        }
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public int    getTier()        { return tier; }
    public int    getTransferRate() { return transferRate; }
    public String getTierName() {
        return switch (tier) {
            case 1  -> "Tier 1";
            case 2  -> "Tier 2";
            case 3  -> "Tier 3";
            default -> "Unknown";
        };
    }

    // -------------------------------------------------------------------------
    // NBT serialization
    // -------------------------------------------------------------------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (energyStorage != null) tag.put("energy", energyStorage.serializeNBT(registries));
        tag.putInt("tier", tier);
        tag.putInt("transferRate", transferRate);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (getBlockState() != null) initializeCapacitor(getBlockState());
        if (tag.contains("energy") && energyStorage != null) energyStorage.deserializeNBT(registries, tag.get("energy"));
        if (tag.contains("tier"))         tier         = tag.getInt("tier");
        if (tag.contains("transferRate")) transferRate = tag.getInt("transferRate");
    }

    // -------------------------------------------------------------------------
    // Sync packets
    // -------------------------------------------------------------------------

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // -------------------------------------------------------------------------
    // Capability registration
    // -------------------------------------------------------------------------

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ATEBlockEntities.CAPACITOR_BE.get(),
                (be, dir) -> be instanceof CapacitorBlockEntity c ? c.getEnergyStorage(dir) : null);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) level.invalidateCapabilities(getBlockPos());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) level.invalidateCapabilities(getBlockPos());
    }

    // -------------------------------------------------------------------------
    // MenuProvider
    // -------------------------------------------------------------------------

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.agritechevolved.capacitor_tier" + tier);
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CapacitorMenu(containerId, playerInventory, this);
    }
}