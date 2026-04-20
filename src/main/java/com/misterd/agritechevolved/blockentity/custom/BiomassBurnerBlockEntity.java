package com.misterd.agritechevolved.blockentity.custom;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.custom.BiomassBurnerBlock;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.gui.custom.BiomassBurnerMenu;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BiomassBurnerBlockEntity extends BlockEntity implements MenuProvider {

    private static final String BIOMASS                 = "agritechevolved:biomass";
    private static final String CRUDE_BIOMASS           = "agritechevolved:crude_biomass";
    private static final String COMPACTED_BIOMASS       = "agritechevolved:compacted_biomass";
    private static final String COMPACTED_BIOMASS_BLOCK = "agritechevolved:compacted_biomass_block";

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) { return 64; }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return isFuel(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            BiomassBurnerBlockEntity.this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final GeneratorEnergyStorage energyStorage =
            new GeneratorEnergyStorage(Config.getBurnerEnergyBuffer());

    private int     progress        = 0;
    private int     maxProgress     = 0;
    private int     currentBurnValue = 0;
    private boolean isBurning        = false;

    public BiomassBurnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.BURNER_BE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BiomassBurnerBlockEntity be) {
        if (level == null || level.isClientSide()) return;

        boolean changed = false;

        if (!be.isBurning && be.canStartBurning()) {
            be.startBurning();
            changed = true;
        }

        if (be.isBurning && be.energyStorage.getEnergyStored() < be.energyStorage.getMaxEnergyStored()) {
            be.progress++;
            if (be.currentBurnValue > 0 && be.energyStorage.generateEnergy(be.currentBurnValue) > 0) {
                changed = true;
            }
            if (be.progress >= be.maxProgress) {
                be.completeBurning();
                changed = true;
            }
        }

        if (be.distributeEnergy()) changed = true;

        if (changed) {
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }

        boolean shouldBurn   = be.isBurning();
        boolean currentlyBurn = state.getValue(BiomassBurnerBlock.BURNING);
        if (shouldBurn != currentlyBurn) {
            level.setBlock(pos, state.setValue(BiomassBurnerBlock.BURNING, shouldBurn), 3);
        }
    }

    private boolean canStartBurning() {
        ItemStack fuel = inventory.getStackInSlot(0);
        return !fuel.isEmpty()
                && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()
                && isFuel(fuel);
    }

    private void startBurning() {
        ItemStack fuel = inventory.getStackInSlot(0);
        if (fuel.isEmpty()) return;

        String id = RegistryHelper.getItemId(fuel);
        int baseRF, burnDuration, baseDuration;

        switch (id) {
            case BIOMASS -> {
                baseRF       = Config.getBurnerBiomassRfValue();
                burnDuration = Config.getBurnerBiomassBurnDuration();
                baseDuration = 100;
            }
            case COMPACTED_BIOMASS -> {
                baseRF       = Config.getBurnerCompactedBiomassRfValue();
                burnDuration = Config.getBurnerCompactedBiomassBurnDuration();
                baseDuration = 180;
            }
            case COMPACTED_BIOMASS_BLOCK -> {
                baseRF       = Config.getBurnerCompactedBiomassBlockRfValue();
                burnDuration = Config.getBurnerCompactedBiomassBlockBurnDuration();
                baseDuration = 180;
            }
            case CRUDE_BIOMASS -> {
                baseRF       = Config.getBurnerCrudeBiomassRfValue();
                burnDuration = Config.getBurnerCrudeBiomassBurnDuration();
                baseDuration = 50;
            }
            default -> { return; }
        }

        int totalRF = (int) ((float) baseRF * ((float) burnDuration / baseDuration));
        if (totalRF <= 0 || burnDuration <= 0) return;

        maxProgress      = burnDuration;
        currentBurnValue = totalRF / maxProgress;
        progress         = 0;
        isBurning        = true;
        fuel.shrink(1);
        inventory.setStackInSlot(0, fuel);
    }

    private void completeBurning() {
        progress         = 0;
        maxProgress      = 0;
        currentBurnValue = 0;
        isBurning        = false;
    }

    private boolean distributeEnergy() {
        if (energyStorage.getEnergyStored() <= 0) return false;

        boolean distributed = false;
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            if (level.getBlockEntity(neighborPos) == null) continue;

            IEnergyStorage neighbor = level.getCapability(
                    EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
            if (neighbor == null || !neighbor.canReceive()) continue;

            int toTransfer = energyStorage.extractEnergy(1000, true);
            if (toTransfer <= 0) continue;

            int transferred = neighbor.receiveEnergy(toTransfer, false);
            if (transferred > 0) {
                energyStorage.extractEnergy(transferred, false);
                distributed = true;
            }
        }
        return distributed;
    }

    private static boolean isFuel(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String id = RegistryHelper.getItemId(stack);
        return id.equals(BIOMASS) || id.equals(CRUDE_BIOMASS)
                || id.equals(COMPACTED_BIOMASS) || id.equals(COMPACTED_BIOMASS_BLOCK);
    }

    public int     getEnergyStored()    { return energyStorage.getEnergyStored(); }
    public int     getMaxEnergyStored() { return energyStorage.getMaxEnergyStored(); }
    public boolean canExtractEnergy()   { return true; }
    public boolean canReceiveEnergy()   { return false; }
    public int     receiveEnergy(int maxReceive, boolean simulate) { return 0; }
    public int     extractEnergy(int maxExtract, boolean simulate) {
        return energyStorage.extractEnergy(maxExtract, simulate);
    }

    public IEnergyStorage getEnergyStorage()                        { return energyStorage; }
    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return new IEnergyStorage() {
            @Override public int  receiveEnergy(int max, boolean sim) { return 0; }
            @Override public int  extractEnergy(int max, boolean sim) { return BiomassBurnerBlockEntity.this.extractEnergy(max, sim); }
            @Override public int  getEnergyStored()                   { return BiomassBurnerBlockEntity.this.getEnergyStored(); }
            @Override public int  getMaxEnergyStored()                { return BiomassBurnerBlockEntity.this.getMaxEnergyStored(); }
            @Override public boolean canExtract()                     { return true; }
            @Override public boolean canReceive()                     { return false; }
        };
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        return new IItemHandler() {
            @Override public int getSlots() { return 1; }

            @Override public @NotNull ItemStack getStackInSlot(int slot) {
                return inventory.getStackInSlot(slot);
            }

            @Override public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (stack.isEmpty()) return stack;
                String id = RegistryHelper.getItemId(stack);
                return (id.equals(BIOMASS) || id.equals(COMPACTED_BIOMASS))
                        ? inventory.insertItem(slot, stack, simulate)
                        : stack;
            }

            @Override public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return inventory.extractItem(slot, amount, simulate);
            }

            @Override public int     getSlotLimit(int slot)                        { return inventory.getSlotLimit(slot); }
            @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) { return isFuel(stack); }
        };
    }

    public int     getProgress()    { return progress; }
    public int     getMaxProgress() { return maxProgress; }
    public boolean isBurning()      { return isBurning; }
    public void    setProgress(int progress) { this.progress = progress; }

    public void drops() {
        SimpleContainer container = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) container.setItem(i, inventory.getStackInSlot(i));
        Containers.dropContents(level, worldPosition, container);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory",       inventory.serializeNBT(registries));
        tag.put("energy",          energyStorage.serializeNBT(registries));
        tag.putInt("progress",     progress);
        tag.putInt("maxProgress",  maxProgress);
        tag.putInt("currentBurnValue", currentBurnValue);
        tag.putBoolean("isBurning", isBurning);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        if (tag.contains("energy"))    energyStorage.deserializeNBT(registries, tag.get("energy"));
        progress         = tag.getInt("progress");
        maxProgress      = tag.getInt("maxProgress");
        currentBurnValue = tag.getInt("currentBurnValue");
        isBurning        = tag.getBoolean("isBurning");
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

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(ItemHandler.BLOCK, ATEBlockEntities.BURNER_BE.get(),
                (be, dir) -> be instanceof BiomassBurnerBlockEntity b ? b.getItemHandler(dir) : null);
        event.registerBlockEntity(EnergyStorage.BLOCK, ATEBlockEntities.BURNER_BE.get(),
                (be, dir) -> be instanceof BiomassBurnerBlockEntity b ? b.getEnergyStorage(dir) : null);
    }

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

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.agritechevolved.biomass_burner");
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BiomassBurnerMenu(containerId, playerInventory, this);
    }

    private static class GeneratorEnergyStorage extends net.neoforged.neoforge.energy.EnergyStorage {

        GeneratorEnergyStorage(int capacity) {
            super(capacity, 0, Integer.MAX_VALUE);
        }

        @Override public int     receiveEnergy(int maxReceive, boolean simulate) { return 0; }
        @Override public boolean canReceive()                                     { return false; }
        @Override public boolean canExtract()                                     { return true; }

        public int generateEnergy(int amount) {
            int generated = Math.min(amount, capacity - energy);
            energy += generated;
            return generated;
        }
    }
}