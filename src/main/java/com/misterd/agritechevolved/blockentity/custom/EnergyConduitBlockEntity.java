package com.misterd.agritechevolved.blockentity.custom;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.custom.EnergyConduitBlock;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.network.ConduitNetworkManager;
import com.misterd.agritechevolved.network.EnergyConduitNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class EnergyConduitBlockEntity extends BlockEntity {

    private final IEnergyStorage energyCapability = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!isActive()) return 0;
            Level level = getLevel();
            if (level == null || level.isClientSide()) return 0;
            int capped = Math.min(maxReceive, getTransferRate());
            EnergyConduitNetwork network = ConduitNetworkManager.get(level).getEnergyNetwork(getBlockPos());
            if (network == null) return 0;
            return network.distribute(level, capped, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return 0;
        }

        @Override
        public int getMaxEnergyStored() {
            return 0;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return isActive();
        }
    };

    public EnergyConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ATEBlockEntities.ENERGY_CONDUIT_BE.get(), pos, state);
    }

    public IEnergyStorage getEnergyCapability() {
        return energyCapability;
    }

    public int getTransferRate() {
        return Config.getConduitTransferRate();
    }

    private boolean isActive() {
        if (!getBlockState().getValue(EnergyConduitBlock.REDSTONE_MODE)) return true;
        Level level = getLevel();
        if (level == null) return false;
        return level.hasNeighborSignal(getBlockPos());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

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

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) level.invalidateCapabilities(getBlockPos());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) {
            level.invalidateCapabilities(getBlockPos());
            ConduitNetworkManager.get(level).onEnergyConduitAdded(level, worldPosition);
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ATEBlockEntities.ENERGY_CONDUIT_BE.get(),
                (be, side) -> be.getEnergyCapability());
    }
}
