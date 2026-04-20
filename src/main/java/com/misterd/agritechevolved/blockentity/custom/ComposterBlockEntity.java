package com.misterd.agritechevolved.blockentity.custom;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.custom.ComposterBlock;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.config.CompostableConfig;
import com.misterd.agritechevolved.gui.custom.ComposterMenu;
import com.misterd.agritechevolved.item.ATEItems;
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

public class ComposterBlockEntity extends BlockEntity implements MenuProvider {

    private static final int INPUT_SLOTS_START  = 0;
    private static final int INPUT_SLOTS_COUNT  = 12;
    private static final int OUTPUT_SLOTS_START = 12;
    private static final int OUTPUT_SLOTS_COUNT = 3;
    private static final int MODULE_SLOT        = 15;
    private static final int TOTAL_SLOTS        = 16;

    private static final String SM_MK1 = "agritechevolved:sm_mk1";
    private static final String SM_MK2 = "agritechevolved:sm_mk2";
    private static final String SM_MK3 = "agritechevolved:sm_mk3";

    private int progress     = 0;
    private int energyStored = 0;

    public final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return slot == MODULE_SLOT ? 1 : 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot >= INPUT_SLOTS_START && slot < INPUT_SLOTS_START + INPUT_SLOTS_COUNT) {
                return isCompostable(stack);
            }
            if (slot >= OUTPUT_SLOTS_START && slot < OUTPUT_SLOTS_START + OUTPUT_SLOTS_COUNT) {
                return false;
            }
            return slot == MODULE_SLOT && isSpeedModule(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            ComposterBlockEntity.this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public ComposterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.COMPOSTER_BE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ComposterBlockEntity be) {
        if (level.isClientSide()) return;

        boolean changed = false;
        int requiredEnergy = Config.getComposterBasePowerConsumption();
        boolean hasPower   = be.energyStored >= requiredEnergy;

        int baseTime = Config.getComposterBaseProcessingTime();
        if (!hasPower) baseTime *= 3;

        int actualTime = (int) Math.max(1, baseTime / be.getModuleSpeedModifier());

        if (be.canProcess()) {
            if (be.progress == 0) be.progress = 1;
            else be.progress++;
            changed = true;

            if (be.progress >= actualTime) {
                be.processItems();
                if (hasPower) {
                    int adjusted = (int) Math.ceil(requiredEnergy * be.getModulePowerModifier());
                    be.energyStored -= adjusted;
                }
                be.progress = 0;
            }
        } else if (be.progress > 0) {
            be.progress = 0;
            changed = true;
        }

        if (changed) {
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            boolean shouldBePowered  = be.progress > 0;
            boolean currentlyPowered = state.getValue(ComposterBlock.POWERED);
            if (shouldBePowered != currentlyPowered) {
                level.setBlock(pos, state.setValue(ComposterBlock.POWERED, shouldBePowered), 3);
            }
        }
    }

    private boolean canProcess() {
        return countAvailableOrganicItems() >= Config.getComposterItemsPerBiomass()
                && hasSpaceForBiomass();
    }

    private int countAvailableOrganicItems() {
        int count = 0;
        for (int i = INPUT_SLOTS_START; i < INPUT_SLOTS_START + INPUT_SLOTS_COUNT; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && isCompostable(stack)) count += stack.getCount();
        }
        return count;
    }

    private boolean hasSpaceForBiomass() {
        ItemStack biomass = new ItemStack(ATEItems.BIOMASS.get());
        for (int i = OUTPUT_SLOTS_START; i < OUTPUT_SLOTS_START + OUTPUT_SLOTS_COUNT; i++) {
            ItemStack output = inventory.getStackInSlot(i);
            if (output.isEmpty()) return true;
            if (ItemStack.isSameItemSameComponents(output, biomass) && output.getCount() < output.getMaxStackSize()) return true;
        }
        return false;
    }

    private void processItems() {
        int toConsume = Config.getComposterItemsPerBiomass();
        for (int i = INPUT_SLOTS_START; i < INPUT_SLOTS_START + INPUT_SLOTS_COUNT && toConsume > 0; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && isCompostable(stack)) {
                int taken = Math.min(toConsume, stack.getCount());
                stack.shrink(taken);
                toConsume -= taken;
                if (stack.isEmpty()) inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
        addBiomassToOutput();
    }

    private void addBiomassToOutput() {
        ItemStack biomass = new ItemStack(ATEItems.BIOMASS.get(), 1);
        for (int i = OUTPUT_SLOTS_START; i < OUTPUT_SLOTS_START + OUTPUT_SLOTS_COUNT; i++) {
            ItemStack output = inventory.getStackInSlot(i);
            if (output.isEmpty()) {
                inventory.setStackInSlot(i, biomass);
                return;
            }
            if (ItemStack.isSameItemSameComponents(output, biomass) && output.getCount() < output.getMaxStackSize()) {
                output.grow(1);
                return;
            }
        }
    }

    private boolean isCompostable(ItemStack stack) {
        return !stack.isEmpty() && CompostableConfig.isCompostable(RegistryHelper.getItemId(stack));
    }

    private boolean isSpeedModule(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String id = RegistryHelper.getItemId(stack);
        return id.equals(SM_MK1) || id.equals(SM_MK2) || id.equals(SM_MK3);
    }

    private double getModuleSpeedModifier() {
        ItemStack module = inventory.getStackInSlot(MODULE_SLOT);
        if (module.isEmpty()) return 1.0;
        return switch (RegistryHelper.getItemId(module)) {
            case SM_MK1 -> Config.getSpeedModuleMk1Multiplier();
            case SM_MK2 -> Config.getSpeedModuleMk2Multiplier();
            case SM_MK3 -> Config.getSpeedModuleMk3Multiplier();
            default     -> 1.0;
        };
    }

    private double getModulePowerModifier() {
        ItemStack module = inventory.getStackInSlot(MODULE_SLOT);
        if (module.isEmpty()) return 1.0;
        return switch (RegistryHelper.getItemId(module)) {
            case SM_MK1 -> Config.getSpeedModuleMk1PowerMultiplier();
            case SM_MK2 -> Config.getSpeedModuleMk2PowerMultiplier();
            case SM_MK3 -> Config.getSpeedModuleMk3PowerMultiplier();
            default     -> 1.0;
        };
    }

    public int  getEnergyStored()    { return energyStored; }
    public int  getMaxEnergyStored() { return Config.getComposterEnergyBuffer(); }
    public boolean canExtractEnergy() { return false; }
    public boolean canReceiveEnergy() { return true; }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = Math.min(maxReceive, getMaxEnergyStored() - energyStored);
        if (!simulate) {
            energyStored += received;
            setChanged();
        }
        return received;
    }

    public int extractEnergy(int maxExtract, boolean simulate) { return 0; }

    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return new IEnergyStorage() {
            @Override public int  receiveEnergy(int max, boolean sim) { return ComposterBlockEntity.this.receiveEnergy(max, sim); }
            @Override public int  extractEnergy(int max, boolean sim) { return 0; }
            @Override public int  getEnergyStored()                   { return ComposterBlockEntity.this.getEnergyStored(); }
            @Override public int  getMaxEnergyStored()                { return ComposterBlockEntity.this.getMaxEnergyStored(); }
            @Override public boolean canExtract()                     { return false; }
            @Override public boolean canReceive()                     { return true; }
        };
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        return new IItemHandler() {
            @Override public int getSlots() { return TOTAL_SLOTS; }

            @Override public @NotNull ItemStack getStackInSlot(int slot) {
                return inventory.getStackInSlot(slot);
            }

            @Override public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                boolean isInputSlot  = slot >= INPUT_SLOTS_START && slot < INPUT_SLOTS_START + INPUT_SLOTS_COUNT;
                boolean isModuleSlot = slot == MODULE_SLOT;
                return (isInputSlot || isModuleSlot) ? inventory.insertItem(slot, stack, simulate) : stack;
            }

            @Override public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                boolean isOutputSlot = slot >= OUTPUT_SLOTS_START && slot < OUTPUT_SLOTS_START + OUTPUT_SLOTS_COUNT;
                return isOutputSlot ? inventory.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
            }

            @Override public int     getSlotLimit(int slot)                        { return inventory.getSlotLimit(slot); }
            @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) { return inventory.isItemValid(slot, stack); }
        };
    }

    public int getProgress() { return progress; }

    public int getMaxProgress() {
        int baseTime   = Config.getComposterBaseProcessingTime();
        boolean hasPower = energyStored >= Config.getComposterBasePowerConsumption();
        if (!hasPower) baseTime *= 4;
        return (int) Math.max(1, baseTime / getModuleSpeedModifier());
    }

    public int getOrganicItemsCollected()  { return countAvailableOrganicItems(); }
    public int getRequiredOrganicItems()   { return Config.getComposterItemsPerBiomass(); }

    public void drops() {
        SimpleContainer container = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) container.setItem(i, inventory.getStackInSlot(i));
        Containers.dropContents(level, worldPosition, container);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putInt("energyStored", energyStored);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        progress     = tag.getInt("progress");
        energyStored = tag.getInt("energyStored");
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
    public Component getDisplayName() {
        return Component.translatable("gui.agritechevolved.composter");
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ComposterMenu(containerId, playerInventory, this);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(ItemHandler.BLOCK, ATEBlockEntities.COMPOSTER_BE.get(),
                (be, dir) -> be instanceof ComposterBlockEntity c ? c.getItemHandler(dir) : null);
        event.registerBlockEntity(EnergyStorage.BLOCK, ATEBlockEntities.COMPOSTER_BE.get(),
                (be, dir) -> be instanceof ComposterBlockEntity c ? c.getEnergyStorage(dir) : null);
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
}