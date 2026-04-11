package com.misterd.agritechevolved.blockentity.custom;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.custom.AdvancedPlanterBlock;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.config.PlantablesConfig;
import com.misterd.agritechevolved.gui.custom.AdvancedPlanterMenu;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class AdvancedPlanterBlockEntity extends BlockEntity implements MenuProvider {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final int SLOT_PLANT      = 0;
    private static final int SLOT_SOIL       = 1;
    private static final int SLOT_MODULE_1   = 2;
    private static final int SLOT_MODULE_2   = 3;
    private static final int SLOT_FERTILIZER = 4;
    private static final int SLOT_OUTPUT_MIN = 5;
    private static final int SLOT_OUTPUT_MAX = 16;
    private static final int TOTAL_SLOTS     = 17;

    // Module IDs
    private static final String SM_MK1 = "agritechevolved:sm_mk1";
    private static final String SM_MK2 = "agritechevolved:sm_mk2";
    private static final String SM_MK3 = "agritechevolved:sm_mk3";
    private static final String YM_MK1 = "agritechevolved:ym_mk1";
    private static final String YM_MK2 = "agritechevolved:ym_mk2";
    private static final String YM_MK3 = "agritechevolved:ym_mk3";

    // Fertilizer IDs
    private static final String FERTILIZER_CRUDE_BIOMASS           = "agritechevolved:crude_biomass";
    private static final String FERTILIZER_BIOMASS           = "agritechevolved:biomass";
    private static final String FERTILIZER_COMPACTED_BIOMASS = "agritechevolved:compacted_biomass";
    private static final String FERTILIZER_BONE_MEAL         = "minecraft:bone_meal";
    private static final String FERTILIZER_IE                = "immersiveengineering:fertilizer";
    private static final String FERTILIZER_MA_ESSENCE        = "mysticalagriculture:fertilized_essence";
    private static final String FERTILIZER_MA_MYSTICAL       = "mysticalagriculture:mystical_fertilizer";
    private static final String FERTILIZER_ARCANE_BONE_MEAL  = "forbidden_arcanus:arcane_bone_meal";

    // -------------------------------------------------------------------------
    // Inventory
    // -------------------------------------------------------------------------

    public final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        public int getSlotLimit(int slot) {
            return slot == SLOT_PLANT || slot == SLOT_SOIL || slot == SLOT_MODULE_1 || slot == SLOT_MODULE_2
                    ? 1 : super.getSlotLimit(slot);
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return slot == SLOT_FERTILIZER ? 64 : super.getStackLimit(slot, stack);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            String id = RegistryHelper.getItemId(stack);
            return switch (slot) {
                case SLOT_PLANT -> {
                    if (!PlantablesConfig.isValidSeed(id) && !PlantablesConfig.isValidSapling(id)) yield false;
                    ItemStack soil = getStackInSlot(SLOT_SOIL);
                    if (soil.isEmpty()) yield true;
                    String soilId = RegistryHelper.getItemId(soil);
                    yield PlantablesConfig.isValidSeed(id)
                            ? PlantablesConfig.isSoilValidForSeed(soilId, id)
                            : PlantablesConfig.isSoilValidForSapling(soilId, id);
                }
                case SLOT_SOIL -> {
                    if (!PlantablesConfig.isValidSoil(id)) yield false;
                    ItemStack plant = getStackInSlot(SLOT_PLANT);
                    if (plant.isEmpty()) yield true;
                    String plantId = RegistryHelper.getItemId(plant);
                    yield PlantablesConfig.isValidSeed(plantId)
                            ? PlantablesConfig.isSoilValidForSeed(id, plantId)
                            : PlantablesConfig.isSoilValidForSapling(id, plantId);
                }
                case SLOT_MODULE_1, SLOT_MODULE_2 -> stack.is(ATETags.Items.ATE_MODULES);
                case SLOT_FERTILIZER             -> PlantablesConfig.isValidFertilizer(id);
                default                          -> false; // output slots (5-16) are output-only
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            AdvancedPlanterBlockEntity.this.setChanged();
            Level lvl = AdvancedPlanterBlockEntity.this.level;
            if (lvl != null && !lvl.isClientSide()) {
                BlockPos p = AdvancedPlanterBlockEntity.this.getBlockPos();
                lvl.sendBlockUpdated(p, AdvancedPlanterBlockEntity.this.getBlockState(),
                        AdvancedPlanterBlockEntity.this.getBlockState(), 3);
            }
        }
    };

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private final OutputOnlyItemHandler outputHandler;

    private int   growthProgress      = 0;
    private int   growthTicks         = 0;
    private boolean readyToHarvest    = false;
    private int   energyStored        = 0;
    private int   lastGrowthStage     = -1;
    private float currentTotalModifier = 1.0F;

    // -------------------------------------------------------------------------
    // Constructor / MenuProvider
    // -------------------------------------------------------------------------

    public AdvancedPlanterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.ADVANCED_PLANTER_BLOCK_BE.get(), pos, blockState);
        this.outputHandler = new OutputOnlyItemHandler(inventory, SLOT_OUTPUT_MIN, SLOT_OUTPUT_MAX);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.agritechevolved.advanced_planter");
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new AdvancedPlanterMenu(id, inv, this);
    }

    // -------------------------------------------------------------------------
    // Energy
    // -------------------------------------------------------------------------

    public int  getEnergyStored()    { return energyStored; }
    public int  getMaxEnergyStored() { return Config.getPlanterEnergyBuffer(); }
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

    private boolean consumeEnergy() {
        int required = Math.round(Config.getPlanterBasePowerConsumption() * getModulePowerModifier());
        if (energyStored < required) return false;
        energyStored -= required;
        setChanged();
        return true;
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return new IEnergyStorage() {
            @Override public int receiveEnergy(int max, boolean sim) { return AdvancedPlanterBlockEntity.this.receiveEnergy(max, sim); }
            @Override public int extractEnergy(int max, boolean sim) { return 0; }
            @Override public int  getEnergyStored()    { return AdvancedPlanterBlockEntity.this.getEnergyStored(); }
            @Override public int  getMaxEnergyStored() { return AdvancedPlanterBlockEntity.this.getMaxEnergyStored(); }
            @Override public boolean canExtract()       { return false; }
            @Override public boolean canReceive()       { return true; }
        };
    }

    // -------------------------------------------------------------------------
    // Item handler capability
    // -------------------------------------------------------------------------

    public IItemHandler getOutputHandler() { return outputHandler; }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (side == Direction.DOWN) return outputHandler;

        // Side handler: slot 0 → fertilizer (insert only), slots 1-11 → output slots 5-15 (extract only)
        return new IItemHandler() {
            @Override public int getSlots() { return 12; }

            @Override public ItemStack getStackInSlot(int slot) {
                return slot == 0
                        ? inventory.getStackInSlot(SLOT_FERTILIZER)
                        : inventory.getStackInSlot(slot + SLOT_FERTILIZER);
            }

            @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return slot == 0 ? inventory.insertItem(SLOT_FERTILIZER, stack, simulate) : stack;
            }

            @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return slot == 0 ? ItemStack.EMPTY : inventory.extractItem(slot + SLOT_FERTILIZER, amount, simulate);
            }

            @Override public int getSlotLimit(int slot) {
                return inventory.getSlotLimit(slot == 0 ? SLOT_FERTILIZER : slot + SLOT_FERTILIZER);
            }

            @Override public boolean isItemValid(int slot, ItemStack stack) {
                return slot == 0 && inventory.isItemValid(SLOT_FERTILIZER, stack);
            }
        };
    }

    // -------------------------------------------------------------------------
    // Capability registration
    // -------------------------------------------------------------------------

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ATEBlockEntities.ADVANCED_PLANTER_BLOCK_BE.get(),
                (be, dir) -> be instanceof AdvancedPlanterBlockEntity p ? p.getItemHandler(dir) : null);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ATEBlockEntities.ADVANCED_PLANTER_BLOCK_BE.get(),
                (be, dir) -> be instanceof AdvancedPlanterBlockEntity p ? p.getEnergyStorage(dir) : null);
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
    // Module modifiers
    // -------------------------------------------------------------------------

    public float getModuleSpeedModifier() {
        float speed = 1.0F, penalty = 1.0F;
        for (int slot = SLOT_MODULE_1; slot <= SLOT_MODULE_2; slot++) {
            String id = RegistryHelper.getItemId(inventory.getStackInSlot(slot));
            if (id.isEmpty()) continue;
            speed   *= switch (id) {
                case SM_MK1 -> (float) Config.getSpeedModuleMk1Multiplier();
                case SM_MK2 -> (float) Config.getSpeedModuleMk2Multiplier();
                case SM_MK3 -> (float) Config.getSpeedModuleMk3Multiplier();
                default -> 1.0F;
            };
            penalty *= switch (id) {
                case YM_MK1 -> (float) Config.getYieldModuleMk1SpeedPenalty();
                case YM_MK2 -> (float) Config.getYieldModuleMk2SpeedPenalty();
                case YM_MK3 -> (float) Config.getYieldModuleMk3SpeedPenalty();
                default -> 1.0F;
            };
        }
        return speed * penalty;
    }

    public float getModuleYieldModifier() {
        float yield = 1.0F;
        for (int slot = SLOT_MODULE_1; slot <= SLOT_MODULE_2; slot++) {
            String id = RegistryHelper.getItemId(inventory.getStackInSlot(slot));
            yield *= switch (id) {
                case YM_MK1 -> (float) Config.getYieldModuleMk1Multiplier();
                case YM_MK2 -> (float) Config.getYieldModuleMk2Multiplier();
                case YM_MK3 -> (float) Config.getYieldModuleMk3Multiplier();
                default -> 1.0F;
            };
        }
        return yield;
    }

    public float getModulePowerModifier() {
        float power = 1.0F;
        for (int slot = SLOT_MODULE_1; slot <= SLOT_MODULE_2; slot++) {
            String id = RegistryHelper.getItemId(inventory.getStackInSlot(slot));
            power *= switch (id) {
                case SM_MK1 -> (float) Config.getSpeedModuleMk1PowerMultiplier();
                case SM_MK2 -> (float) Config.getSpeedModuleMk2PowerMultiplier();
                case SM_MK3 -> (float) Config.getSpeedModuleMk3PowerMultiplier();
                default -> 1.0F;
            };
        }
        return power;
    }

    private float getModuleGrowthModifier() { return getModuleSpeedModifier(); }

    // -------------------------------------------------------------------------
    // Cloche modifiers
    // -------------------------------------------------------------------------

    private float getClocheGrowthModifier() {
        return getBlockState().getValue(AdvancedPlanterBlock.CLOCHED)
                ? (float) Config.getClocheSpeedMultiplier() : 1.0F;
    }

    private float getClocheYieldModifier() {
        return getBlockState().getValue(AdvancedPlanterBlock.CLOCHED)
                ? (float) Config.getClocheYieldMultiplier() : 1.0F;
    }

    // -------------------------------------------------------------------------
    // Fertilizer modifiers
    // -------------------------------------------------------------------------

    private float getFertilizerGrowthModifier() {
        return getFertilizerModifier(true);
    }

    private float getFertilizerYieldModifier() {
        return getFertilizerModifier(false);
    }

    private float getFertilizerModifier(boolean forSpeed) {
        ItemStack stack = inventory.getStackInSlot(SLOT_FERTILIZER);
        if (stack.isEmpty()) return 1.0F;
        String id = RegistryHelper.getItemId(stack);
        return switch (id) {
            case FERTILIZER_CRUDE_BIOMASS     -> (float)(forSpeed ? Config.getFertilizerCrudeBiomassSpeedMultiplier()      : Config.getFertilizerCrudeBiomassYieldMultiplier());
            case FERTILIZER_BIOMASS           -> (float)(forSpeed ? Config.getFertilizerBiomassSpeedMultiplier()           : Config.getFertilizerBiomassYieldMultiplier());
            case FERTILIZER_COMPACTED_BIOMASS -> (float)(forSpeed ? Config.getFertilizerCompactedBiomassSpeedMultiplier()  : Config.getFertilizerCompactedBiomassYieldMultiplier());
            case FERTILIZER_BONE_MEAL         -> (float)(forSpeed ? Config.getFertilizerBoneMealSpeedMultiplier()          : Config.getFertilizerBoneMealYieldMultiplier());
            case FERTILIZER_IE                -> (float)(forSpeed ? Config.getFertilizerImmersiveFertilizerSpeedMultiplier(): Config.getFertilizerImmersiveFertilizerYieldMultiplier());
            case FERTILIZER_MA_ESSENCE        -> (float)(forSpeed ? Config.getFertilizerFertilizedEssenceSpeedMultiplier() : Config.getFertilizerFertilizedEssenceYieldMultiplier());
            case FERTILIZER_MA_MYSTICAL       -> (float)(forSpeed ? Config.getFertilizerMysticalFertilizerSpeedMultiplier(): Config.getFertilizerMysticalFertilizerYieldMultiplier());
            case FERTILIZER_ARCANE_BONE_MEAL  -> (float)(forSpeed ? Config.getFertilizerArcaneBoneMealSpeedMultiplier()    : Config.getFertilizerArcaneBoneMealYieldMultiplier());
            default -> {
                PlantablesConfig.FertilizerInfo info = PlantablesConfig.getFertilizerInfo(id);
                yield info != null ? (forSpeed ? info.speedMultiplier : info.yieldMultiplier) : 1.0F;
            }
        };
    }

    // -------------------------------------------------------------------------
    // Tick
    // -------------------------------------------------------------------------

    public static void tick(Level level, BlockPos pos, BlockState state, AdvancedPlanterBlockEntity be) {
        if (level.isClientSide()) return;

        // Sync powered state
        boolean powered = be.energyStored > 0;
        if (state.getValue(AdvancedPlanterBlock.POWERED) != powered) {
            level.setBlock(pos, state.setValue(AdvancedPlanterBlock.POWERED, powered), 3);
        }

        ItemStack plant = be.inventory.getStackInSlot(SLOT_PLANT);
        ItemStack soil  = be.inventory.getStackInSlot(SLOT_SOIL);

        if (plant.isEmpty() || soil.isEmpty()) {
            be.resetGrowth();
            return;
        }

        String plantId = RegistryHelper.getItemId(plant);
        String soilId  = RegistryHelper.getItemId(soil);

        if (!be.isValidPlantSoilCombination(plantId, soilId)) {
            be.resetGrowth();
            return;
        }

        if (!be.readyToHarvest) {
            if (!be.consumeEnergy()) return;

            float totalModifier = be.getSoilGrowthModifier(soil)
                    * be.getModuleGrowthModifier()
                    * be.getFertilizerGrowthModifier()
                    * be.getClocheGrowthModifier();
            be.currentTotalModifier = totalModifier;

            int growthTime = Math.max(1, Math.round(be.getBaseGrowthTime(plant) / totalModifier));
            be.growthTicks++;

            if (be.growthTicks >= growthTime) {
                be.readyToHarvest  = true;
                be.growthProgress  = 100;
                be.lastGrowthStage = be.getGrowthStage();
                level.sendBlockUpdated(pos, state, state, 3);
                be.setChanged();
            } else {
                be.growthProgress = (int)(be.growthTicks / (float) growthTime * 100);
                int stage = be.getGrowthStage();
                if (stage != be.lastGrowthStage) be.lastGrowthStage = stage;
                if (be.growthTicks % 20 == 0) {
                    level.sendBlockUpdated(pos, state, state, 3);
                    be.setChanged();
                }
            }
        }

        if (be.readyToHarvest && be.hasOutputSpace()) {
            be.harvestPlant();
        }

        tryOutputItemsBelow(level, pos, be);
    }

    // -------------------------------------------------------------------------
    // Growth helpers
    // -------------------------------------------------------------------------

    private boolean isValidPlantSoilCombination(String plantId, String soilId) {
        if (PlantablesConfig.isValidSeed(plantId))    return PlantablesConfig.isSoilValidForSeed(soilId, plantId);
        if (PlantablesConfig.isValidSapling(plantId)) return PlantablesConfig.isSoilValidForSapling(soilId, plantId);
        return false;
    }

    private boolean isTree() {
        ItemStack plant = inventory.getStackInSlot(SLOT_PLANT);
        return !plant.isEmpty() && PlantablesConfig.isValidSapling(RegistryHelper.getItemId(plant));
    }

    private boolean isCrop() {
        ItemStack plant = inventory.getStackInSlot(SLOT_PLANT);
        return !plant.isEmpty() && PlantablesConfig.isValidSeed(RegistryHelper.getItemId(plant));
    }

    private int getBaseGrowthTime(ItemStack plant) {
        String id = RegistryHelper.getItemId(plant);
        if (PlantablesConfig.isValidSapling(id)) return PlantablesConfig.getBaseSaplingGrowthTime(id);
        return Config.getPlanterBaseProcessingTime();
    }

    public float getSoilGrowthModifier(ItemStack soil) {
        return soil.isEmpty() ? 1.0F : PlantablesConfig.getSoilGrowthModifier(RegistryHelper.getItemId(soil));
    }

    private void resetGrowth() {
        growthProgress  = 0;
        growthTicks     = 0;
        readyToHarvest  = false;
        lastGrowthStage = -1;
        setChanged();
    }

    public float getGrowthProgress() { return growthProgress / 100.0F; }

    public int getGrowthStage() {
        return isTree()
                ? (growthProgress > 50 ? 1 : 0)
                : Math.min(8, (int)(growthProgress / 12.5F));
    }

    // -------------------------------------------------------------------------
    // Harvest
    // -------------------------------------------------------------------------

    public boolean hasOutputSpace() {
        List<ItemStack> drops = getHarvestDrops(inventory.getStackInSlot(SLOT_PLANT));
        Map<Integer, ItemStack> sim = new HashMap<>();
        for (int slot = SLOT_OUTPUT_MIN; slot <= SLOT_OUTPUT_MAX; slot++) {
            ItemStack s = inventory.getStackInSlot(slot);
            sim.put(slot, s.isEmpty() ? ItemStack.EMPTY : s.copy());
        }

        for (ItemStack drop : drops) {
            int remaining = drop.getCount();

            // Try to stack into existing matching stacks
            for (int slot = SLOT_OUTPUT_MIN; slot <= SLOT_OUTPUT_MAX && remaining > 0; slot++) {
                ItemStack existing = sim.get(slot);
                if (!existing.isEmpty() && existing.is(drop.getItem()) && existing.getCount() < existing.getMaxStackSize()) {
                    int space = existing.getMaxStackSize() - existing.getCount();
                    int add   = Math.min(space, remaining);
                    existing.grow(add);
                    remaining -= add;
                }
            }

            // Try empty slots
            for (int slot = SLOT_OUTPUT_MIN; slot <= SLOT_OUTPUT_MAX && remaining > 0; slot++) {
                if (sim.get(slot).isEmpty()) {
                    sim.put(slot, new ItemStack(drop.getItem(), remaining));
                    remaining = 0;
                }
            }

            if (remaining > 0) return false;
        }
        return true;
    }

    public void harvestPlant() {
        if (!readyToHarvest) return;

        float yieldModifier = getFertilizerYieldModifier() * getModuleYieldModifier() * getClocheYieldModifier();
        List<ItemStack> drops = applyYieldModifier(getHarvestDrops(inventory.getStackInSlot(SLOT_PLANT)), yieldModifier);

        for (ItemStack drop : drops) {
            int remaining = drop.getCount();
            for (int slot = SLOT_OUTPUT_MIN; slot <= SLOT_OUTPUT_MAX && remaining > 0; slot++) {
                ItemStack existing = inventory.getStackInSlot(slot);
                if (existing.isEmpty()) {
                    int toPlace = Math.min(remaining, drop.getMaxStackSize());
                    inventory.setStackInSlot(slot, new ItemStack(drop.getItem(), toPlace));
                    remaining -= toPlace;
                } else if (existing.is(drop.getItem()) && existing.getCount() < existing.getMaxStackSize()) {
                    int space = existing.getMaxStackSize() - existing.getCount();
                    int add   = Math.min(space, remaining);
                    existing.grow(add);
                    remaining -= add;
                }
            }
            if (remaining > 0) break;
        }

        consumeFertilizerForGrowthCycle();
        resetGrowth();
    }

    private void consumeFertilizerForGrowthCycle() {
        ItemStack fertilizer = inventory.getStackInSlot(SLOT_FERTILIZER);
        if (fertilizer.isEmpty()) return;
        fertilizer.shrink(1);
        inventory.setStackInSlot(SLOT_FERTILIZER, fertilizer.isEmpty() ? ItemStack.EMPTY : fertilizer);
        setChanged();
    }

    private List<ItemStack> getHarvestDrops(ItemStack plant) {
        List<ItemStack> drops = new ArrayList<>();
        if (plant.isEmpty()) return drops;

        String plantId = RegistryHelper.getItemId(plant);
        List<PlantablesConfig.DropInfo> configDrops;
        if (PlantablesConfig.isValidSeed(plantId))        configDrops = PlantablesConfig.getCropDrops(plantId);
        else if (PlantablesConfig.isValidSapling(plantId)) configDrops = PlantablesConfig.getTreeDrops(plantId);
        else return drops;

        Random random = new Random();
        for (PlantablesConfig.DropInfo info : configDrops) {
            if (random.nextFloat() > info.chance) continue;
            int count = info.minCount == info.maxCount
                    ? info.minCount
                    : info.minCount + random.nextInt(info.maxCount - info.minCount + 1);
            Item item = RegistryHelper.getItem(info.item);
            if (item != null) drops.add(new ItemStack(item, count));
        }
        return drops;
    }

    private List<ItemStack> applyYieldModifier(List<ItemStack> drops, float modifier) {
        if (modifier == 1.0F) return drops;
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack drop : drops) {
            result.add(new ItemStack(drop.getItem(), Math.max(1, Math.round(drop.getCount() * modifier))));
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // Auto-output below
    // -------------------------------------------------------------------------

    private static void tryOutputItemsBelow(Level level, BlockPos pos, AdvancedPlanterBlockEntity be) {
        IItemHandler target = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.below(), Direction.UP);
        if (target == null) return;

        boolean changed = false;
        for (int slot = SLOT_OUTPUT_MIN; slot <= SLOT_OUTPUT_MAX; slot++) {
            if (be.inventory.getStackInSlot(slot).isEmpty()) continue;
            ItemStack extracted = be.inventory.extractItem(slot, 64, true);
            if (extracted.isEmpty()) continue;
            ItemStack remaining = ItemHandlerHelper.insertItemStacked(target, extracted, false);
            int inserted = extracted.getCount() - remaining.getCount();
            if (inserted > 0) {
                be.inventory.extractItem(slot, inserted, false);
                changed = true;
            }
        }

        if (changed) {
            be.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }
    }

    // -------------------------------------------------------------------------
    // Drops on break
    // -------------------------------------------------------------------------

    public void drops() {
        SimpleContainer container = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) container.setItem(i, inventory.getStackInSlot(i));
        Containers.dropContents(level, worldPosition, container);
    }

    // -------------------------------------------------------------------------
    // NBT serialization
    // -------------------------------------------------------------------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("growthProgress", growthProgress);
        tag.putInt("growthTicks", growthTicks);
        tag.putBoolean("readyToHarvest", readyToHarvest);
        tag.putInt("energyStored", energyStored);
        tag.putInt("lastGrowthStage", lastGrowthStage);
        tag.putFloat("currentTotalModifier", currentTotalModifier);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        growthProgress      = tag.getInt("growthProgress");
        growthTicks         = tag.getInt("growthTicks");
        readyToHarvest      = tag.getBoolean("readyToHarvest");
        energyStored        = tag.getInt("energyStored");
        lastGrowthStage     = tag.getInt("lastGrowthStage");
        currentTotalModifier = tag.getFloat("currentTotalModifier");
    }

    // -------------------------------------------------------------------------
    // Sync packets
    // -------------------------------------------------------------------------

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    // -------------------------------------------------------------------------
    // OutputOnlyItemHandler
    // -------------------------------------------------------------------------

    private static class OutputOnlyItemHandler implements IItemHandler {
        private final ItemStackHandler original;
        private final int firstOutputSlot;
        private final int lastOutputSlot;

        OutputOnlyItemHandler(ItemStackHandler original, int firstOutputSlot, int lastOutputSlot) {
            this.original        = original;
            this.firstOutputSlot = firstOutputSlot;
            this.lastOutputSlot  = lastOutputSlot;
        }

        @Override public int getSlots() { return original.getSlots(); }

        @Override @NotNull
        public ItemStack getStackInSlot(int slot) { return original.getStackInSlot(slot); }

        @Override @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return stack; }

        @Override @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return (slot >= firstOutputSlot && slot <= lastOutputSlot)
                    ? original.extractItem(slot, amount, simulate)
                    : ItemStack.EMPTY;
        }

        @Override public int     getSlotLimit(int slot)                   { return original.getSlotLimit(slot); }
        @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) { return false; }
    }
}