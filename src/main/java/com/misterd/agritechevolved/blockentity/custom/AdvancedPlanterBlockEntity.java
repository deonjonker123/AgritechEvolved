package com.misterd.agritechevolved.blockentity.custom;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.custom.AdvancedPlanterBlock;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.datamap.ATEDataMaps;
import com.misterd.agritechevolved.datamap.FertilizerData;
import com.misterd.agritechevolved.datamap.SoilModifierData;
import com.misterd.agritechevolved.gui.custom.AdvancedPlanterMenu;
import com.misterd.agritechevolved.recipe.ATERecipeTypes;
import com.misterd.agritechevolved.recipe.CropRecipe;
import com.misterd.agritechevolved.recipe.DropEntry;
import com.misterd.agritechevolved.recipe.TreeRecipe;
import com.misterd.agritechevolved.util.ATETags;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
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

    private static final int SLOT_PLANT = 0;
    private static final int SLOT_SOIL = 1;
    private static final int SLOT_MODULE_1 = 2;
    private static final int SLOT_MODULE_2 = 3;
    private static final int SLOT_FERTILIZER = 4;
    private static final int SLOT_OUTPUT_MIN = 5;
    private static final int SLOT_OUTPUT_MAX = 16;
    private static final int TOTAL_SLOTS = 17;

    private static final String SM_MK1 = "agritechevolved:sm_mk1";
    private static final String SM_MK2 = "agritechevolved:sm_mk2";
    private static final String SM_MK3 = "agritechevolved:sm_mk3";
    private static final String YM_MK1 = "agritechevolved:ym_mk1";
    private static final String YM_MK2 = "agritechevolved:ym_mk2";
    private static final String YM_MK3 = "agritechevolved:ym_mk3";

    @Nullable private CropRecipe cachedCropRecipe = null;
    @Nullable private TreeRecipe cachedTreeRecipe = null;
    @Nullable private Item cachedSeedItem = null;
    private Set<Item> cachedValidSoils = null;
    private int soilCacheRevision = -1;
    private int cachedRevision = -1;

    public final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        public int getSlotLimit(int slot) {
            return slot == SLOT_PLANT || slot == SLOT_SOIL || slot == SLOT_MODULE_1 || slot == SLOT_MODULE_2 ? 1 : super.getSlotLimit(slot);
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return slot == SLOT_FERTILIZER ? 64 : super.getStackLimit(slot, stack);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case SLOT_PLANT -> {
                    if (!isValidPlant(stack)) yield false;
                    ItemStack soil = getStackInSlot(SLOT_SOIL);
                    if (soil.isEmpty()) yield true;
                    yield isValidPlantSoilCombination(stack, soil);
                }
                case SLOT_SOIL -> {
                    if (!isValidSoilForAnyRecipe(stack)) yield false;
                    ItemStack plant = getStackInSlot(SLOT_PLANT);
                    if (plant.isEmpty()) yield true;
                    yield isValidPlantSoilCombination(plant, stack);
                }
                case SLOT_MODULE_1, SLOT_MODULE_2 -> stack.is(ATETags.Items.ATE_MODULES);
                case SLOT_FERTILIZER -> isFertilizer(stack);
                default -> false;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == SLOT_PLANT) invalidateRecipeCache();
            AdvancedPlanterBlockEntity.this.setChanged();
            Level lvl = AdvancedPlanterBlockEntity.this.level;
            if (lvl != null && !lvl.isClientSide()) {
                BlockPos p = AdvancedPlanterBlockEntity.this.getBlockPos();
                lvl.sendBlockUpdated(p, AdvancedPlanterBlockEntity.this.getBlockState(), AdvancedPlanterBlockEntity.this.getBlockState(), 3);
            }
        }
    };

    private final OutputOnlyItemHandler outputHandler;

    private int growthProgress = 0;
    private int growthTicks = 0;
    private boolean readyToHarvest = false;
    private int energyStored = 0;
    private int lastGrowthStage = -1;
    private float currentTotalModifier = 1.0F;

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

    private void invalidateRecipeCache() {
        cachedCropRecipe = null;
        cachedTreeRecipe = null;
        cachedSeedItem = null;
        cachedRevision = -1;
    }

    @Nullable
    private RecipeManager getRecipes() {
        if (level == null) return null;
        return level.isClientSide() ? level.getRecipeManager() : level.getServer().getRecipeManager();
    }

    private void refreshRecipeCacheIfNeeded(ItemStack seed) {
        if (seed.isEmpty()) { invalidateRecipeCache(); return; }
        Item seedItem = seed.getItem();
        if (seedItem == cachedSeedItem && cachedRevision == AgritechEvolved.RECIPE_REVISION) return;
        invalidateRecipeCache();
        RecipeManager rm = getRecipes();
        if (rm == null) return;
        cachedSeedItem = seedItem;
        cachedRevision = AgritechEvolved.RECIPE_REVISION;
        SingleRecipeInput input = new SingleRecipeInput(seed);
        Optional<RecipeHolder<CropRecipe>> crop = rm.getRecipeFor(ATERecipeTypes.CROP_TYPE.get(), input, level);
        if (crop.isPresent()) { cachedCropRecipe = crop.get().value(); return; }
        Optional<RecipeHolder<TreeRecipe>> tree = rm.getRecipeFor(ATERecipeTypes.TREE_TYPE.get(), input, level);
        tree.ifPresent(h -> cachedTreeRecipe = h.value());
    }

    private Optional<CropRecipe> findCropRecipe(ItemStack seed) {
        if (seed.isEmpty()) return Optional.empty();
        refreshRecipeCacheIfNeeded(seed);
        return Optional.ofNullable(cachedCropRecipe);
    }

    private Optional<TreeRecipe> findTreeRecipe(ItemStack sapling) {
        if (sapling.isEmpty()) return Optional.empty();
        refreshRecipeCacheIfNeeded(sapling);
        return Optional.ofNullable(cachedTreeRecipe);
    }

    public boolean isValidPlant(ItemStack stack) {
        if (level == null) return false;
        return findCropRecipe(stack).isPresent() || findTreeRecipe(stack).isPresent();
    }

    private Set<Item> getValidSoils() {
        if (cachedValidSoils != null && soilCacheRevision == AgritechEvolved.RECIPE_REVISION) return cachedValidSoils;
        RecipeManager rm = getRecipes();
        if (rm == null) return Set.of();
        Set<Item> soils = new HashSet<>();
        for (RecipeHolder<?> holder : rm.getRecipes()) {
            if (holder.value().getType() == ATERecipeTypes.CROP_TYPE.get()) {
                for (Ingredient ing : ((CropRecipe) holder.value()).getSoils())
                    for (ItemStack s : ing.getItems()) soils.add(s.getItem());
            } else if (holder.value().getType() == ATERecipeTypes.TREE_TYPE.get()) {
                for (Ingredient ing : ((TreeRecipe) holder.value()).getSoils())
                    for (ItemStack s : ing.getItems()) soils.add(s.getItem());
            }
        }
        cachedValidSoils = soils;
        soilCacheRevision = AgritechEvolved.RECIPE_REVISION;
        return cachedValidSoils;
    }

    public boolean isValidSoilForAnyRecipe(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return getValidSoils().contains(stack.getItem());
    }

    public boolean isValidPlantSoilCombination(ItemStack plant, ItemStack soil) {
        Optional<CropRecipe> crop = findCropRecipe(plant);
        if (crop.isPresent()) return crop.get().matchesSoil(soil);
        Optional<TreeRecipe> tree = findTreeRecipe(plant);
        if (tree.isPresent()) return tree.get().matchesSoil(soil);
        return false;
    }

    public boolean isTree() {
        return findTreeRecipe(inventory.getStackInSlot(SLOT_PLANT)).isPresent();
    }

    public static boolean isFertilizer(ItemStack stack) {
        return stack.getItem().builtInRegistryHolder().getData(ATEDataMaps.FERTILIZERS) != null;
    }

    public int getEnergyStored() { return energyStored; }
    public int getMaxEnergyStored() { return Config.getPlanterEnergyBuffer(); }
    public boolean canExtractEnergy() { return false; }
    public boolean canReceiveEnergy() { return true; }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = Math.min(maxReceive, getMaxEnergyStored() - energyStored);
        if (!simulate) { energyStored += received; setChanged(); }
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
            @Override public int getEnergyStored() { return AdvancedPlanterBlockEntity.this.getEnergyStored(); }
            @Override public int getMaxEnergyStored() { return AdvancedPlanterBlockEntity.this.getMaxEnergyStored(); }
            @Override public boolean canExtract() { return false; }
            @Override public boolean canReceive() { return true; }
        };
    }

    public IItemHandler getOutputHandler() { return outputHandler; }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (side == Direction.DOWN) return outputHandler;
        return new IItemHandler() {
            @Override public int getSlots() { return 12; }
            @Override public ItemStack getStackInSlot(int slot) {
                return slot == 0 ? inventory.getStackInSlot(SLOT_FERTILIZER) : inventory.getStackInSlot(slot + SLOT_FERTILIZER);
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

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ATEBlockEntities.ADVANCED_PLANTER_BLOCK_BE.get(),
                (be, dir) -> be instanceof AdvancedPlanterBlockEntity p ? p.getItemHandler(dir) : null);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ATEBlockEntities.ADVANCED_PLANTER_BLOCK_BE.get(),
                (be, dir) -> be instanceof AdvancedPlanterBlockEntity p ? p.getEnergyStorage(dir) : null);
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

    public float getModuleSpeedModifier() {
        float speed = 1.0F, penalty = 1.0F;
        for (int slot = SLOT_MODULE_1; slot <= SLOT_MODULE_2; slot++) {
            ItemStack s = inventory.getStackInSlot(slot);
            if (s.isEmpty()) continue;
            String id = s.getItem().builtInRegistryHolder().getRegisteredName();
            speed *= switch (id) {
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
            ItemStack s = inventory.getStackInSlot(slot);
            if (s.isEmpty()) continue;
            String id = s.getItem().builtInRegistryHolder().getRegisteredName();
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
            ItemStack s = inventory.getStackInSlot(slot);
            if (s.isEmpty()) continue;
            String id = s.getItem().builtInRegistryHolder().getRegisteredName();
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

    private float getClocheGrowthModifier() {
        return getBlockState().getValue(AdvancedPlanterBlock.CLOCHED) ? (float) Config.getClocheSpeedMultiplier() : 1.0F;
    }

    private float getClocheYieldModifier() {
        return getBlockState().getValue(AdvancedPlanterBlock.CLOCHED) ? (float) Config.getClocheYieldMultiplier() : 1.0F;
    }

    private float getFertilizerGrowthModifier() {
        ItemStack stack = inventory.getStackInSlot(SLOT_FERTILIZER);
        if (stack.isEmpty()) return 1.0F;
        FertilizerData data = stack.getItem().builtInRegistryHolder().getData(ATEDataMaps.FERTILIZERS);
        return data != null ? data.speedMultiplier() : 1.0F;
    }

    private float getFertilizerYieldModifier() {
        ItemStack stack = inventory.getStackInSlot(SLOT_FERTILIZER);
        if (stack.isEmpty()) return 1.0F;
        FertilizerData data = stack.getItem().builtInRegistryHolder().getData(ATEDataMaps.FERTILIZERS);
        return data != null ? data.yieldMultiplier() : 1.0F;
    }

    public float getSoilGrowthModifier(ItemStack soil) {
        if (soil.isEmpty()) return 1.0F;
        SoilModifierData data = soil.getItem().builtInRegistryHolder().getData(ATEDataMaps.SOIL_MODIFIERS);
        return data != null ? data.growthModifier() : 1.0F;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AdvancedPlanterBlockEntity be) {
        if (level.isClientSide()) return;
        boolean powered = be.energyStored > 0;
        if (state.getValue(AdvancedPlanterBlock.POWERED) != powered) {
            level.setBlock(pos, state.setValue(AdvancedPlanterBlock.POWERED, powered), 3);
        }
        ItemStack plant = be.inventory.getStackInSlot(SLOT_PLANT);
        ItemStack soil = be.inventory.getStackInSlot(SLOT_SOIL);
        if (plant.isEmpty() || soil.isEmpty()) { be.resetGrowth(); return; }
        if (!be.isValidPlantSoilCombination(plant, soil)) { be.resetGrowth(); return; }
        if (!be.readyToHarvest) {
            if (!be.consumeEnergy()) return;
            float totalModifier = be.getSoilGrowthModifier(soil) * be.getModuleGrowthModifier() * be.getFertilizerGrowthModifier() * be.getClocheGrowthModifier();
            be.currentTotalModifier = totalModifier;
            int growthTime = Math.max(1, Math.round(Config.getAdvancedPlanterBaseProcessingTime() / totalModifier));
            be.growthTicks++;
            if (be.growthTicks >= growthTime) {
                be.readyToHarvest = true;
                be.growthProgress = 100;
                be.lastGrowthStage = be.getGrowthStage();
                level.sendBlockUpdated(pos, state, state, 3);
                be.setChanged();
            } else {
                be.growthProgress = (int)(be.growthTicks / (float) growthTime * 100);
                int stage = be.getGrowthStage();
                if (stage != be.lastGrowthStage) be.lastGrowthStage = stage;
                if (be.growthTicks % 20 == 0) { level.sendBlockUpdated(pos, state, state, 3); be.setChanged(); }
            }
        }
        if (be.readyToHarvest && be.hasOutputSpace()) be.harvestPlant();
        tryOutputItemsBelow(level, pos, be);
    }

    private void resetGrowth() {
        growthProgress = 0;
        growthTicks = 0;
        readyToHarvest = false;
        lastGrowthStage = -1;
        setChanged();
    }

    public float getGrowthProgress() { return growthProgress / 100.0F; }

    public int getGrowthStage() {
        return isTree() ? (growthProgress > 50 ? 1 : 0) : Math.min(8, (int)(growthProgress / 12.5F));
    }

    public boolean hasOutputSpace() {
        List<ItemStack> drops = getHarvestDrops(inventory.getStackInSlot(SLOT_PLANT));
        Map<Integer, ItemStack> sim = new HashMap<>();
        for (int slot = SLOT_OUTPUT_MIN; slot <= SLOT_OUTPUT_MAX; slot++) {
            ItemStack s = inventory.getStackInSlot(slot);
            sim.put(slot, s.isEmpty() ? ItemStack.EMPTY : s.copy());
        }
        for (ItemStack drop : drops) {
            int remaining = drop.getCount();
            for (int slot = SLOT_OUTPUT_MIN; slot <= SLOT_OUTPUT_MAX && remaining > 0; slot++) {
                ItemStack existing = sim.get(slot);
                if (!existing.isEmpty() && existing.is(drop.getItem()) && existing.getCount() < existing.getMaxStackSize()) {
                    int space = existing.getMaxStackSize() - existing.getCount();
                    int add = Math.min(space, remaining);
                    existing.grow(add);
                    remaining -= add;
                }
            }
            for (int slot = SLOT_OUTPUT_MIN; slot <= SLOT_OUTPUT_MAX && remaining > 0; slot++) {
                if (sim.get(slot).isEmpty()) { sim.put(slot, new ItemStack(drop.getItem(), remaining)); remaining = 0; }
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
                    int add = Math.min(space, remaining);
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
        if (plant.isEmpty()) return List.of();
        Optional<CropRecipe> crop = findCropRecipe(plant);
        List<DropEntry> entries = crop.map(CropRecipe::getDrops).orElseGet(() -> findTreeRecipe(plant).map(TreeRecipe::getDrops).orElse(List.of()));
        List<ItemStack> drops = new ArrayList<>();
        Random rng = new Random();
        for (DropEntry entry : entries) {
            if (rng.nextFloat() <= entry.chance()) {
                int count = entry.max() > entry.min() ? entry.min() + rng.nextInt(entry.max() - entry.min() + 1) : entry.min();
                drops.add(new ItemStack(entry.item(), count));
            }
        }
        return drops;
    }

    private List<ItemStack> applyYieldModifier(List<ItemStack> drops, float modifier) {
        if (modifier == 1.0F) return drops;
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack drop : drops)
            result.add(new ItemStack(drop.getItem(), Math.max(1, Math.round(drop.getCount() * modifier))));
        return result;
    }

    public void applyManualFertilizer(float speedMultiplier) {
        if (readyToHarvest) return;
        ItemStack plantStack = inventory.getStackInSlot(0);
        ItemStack soilStack = inventory.getStackInSlot(1);
        if (plantStack.isEmpty() || soilStack.isEmpty()) return;
        float soilMod = getSoilGrowthModifier(soilStack);
        float clocheMod = getClocheGrowthModifier();
        int adjustedTime = Math.max(1, Math.round(Config.getPlanterBaseProcessingTime() / (soilMod * clocheMod)));
        int boost = Math.max(1, Math.round(adjustedTime * 0.25F * speedMultiplier));
        growthTicks = Math.min(adjustedTime, growthTicks + boost);
        growthProgress = (int) ((float) growthTicks / adjustedTime * 100.0F);
        if (growthTicks >= adjustedTime) { readyToHarvest = true; growthProgress = 100; }
        lastGrowthStage = getGrowthStage();
        setChanged();
    }

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
            if (inserted > 0) { be.inventory.extractItem(slot, inserted, false); changed = true; }
        }
        if (changed) { be.setChanged(); level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3); }
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) container.setItem(i, inventory.getStackInSlot(i));
        Containers.dropContents(level, worldPosition, container);
    }

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
        growthProgress = tag.getInt("growthProgress");
        growthTicks = tag.getInt("growthTicks");
        readyToHarvest = tag.getBoolean("readyToHarvest");
        energyStored = tag.getInt("energyStored");
        lastGrowthStage = tag.getInt("lastGrowthStage");
        currentTotalModifier = tag.getFloat("currentTotalModifier");
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

    private static class OutputOnlyItemHandler implements IItemHandler {
        private final ItemStackHandler original;
        private final int firstOutputSlot;
        private final int lastOutputSlot;

        OutputOnlyItemHandler(ItemStackHandler original, int firstOutputSlot, int lastOutputSlot) {
            this.original = original;
            this.firstOutputSlot = firstOutputSlot;
            this.lastOutputSlot = lastOutputSlot;
        }

        @Override public int getSlots() { return original.getSlots(); }
        @Override @NotNull public ItemStack getStackInSlot(int slot) { return original.getStackInSlot(slot); }
        @Override @NotNull public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return stack; }
        @Override @NotNull public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return (slot >= firstOutputSlot && slot <= lastOutputSlot) ? original.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }
        @Override public int getSlotLimit(int slot) { return original.getSlotLimit(slot); }
        @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) { return false; }
    }
}