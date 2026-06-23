package com.misterd.agritechevolved.blockentity.custom;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.custom.PlanterBlock;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.datamap.ATEDataMaps;
import com.misterd.agritechevolved.datamap.FertilizerData;
import com.misterd.agritechevolved.datamap.SoilModifierData;
import com.misterd.agritechevolved.gui.custom.BasicPlanterMenu;
import com.misterd.agritechevolved.recipe.ATERecipeTypes;
import com.misterd.agritechevolved.recipe.CropRecipe;
import com.misterd.agritechevolved.recipe.DropEntry;
import com.misterd.agritechevolved.recipe.TreeRecipe;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class PlanterBlockEntity extends BlockEntity implements MenuProvider {

    @Nullable private CropRecipe cachedCropRecipe = null;
    @Nullable private TreeRecipe cachedTreeRecipe = null;
    @Nullable private Item cachedSeedItem = null;
    private Set<Item> cachedValidSoils = null;
    private int soilCacheRevision = -1;
    private int cachedRevision = -1;

    public final ItemStackHandler inventory = new ItemStackHandler(15) {
        @Override
        public int getSlotLimit(int slot) {
            return (slot == 0 || slot == 1) ? 1 : super.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> isValidPlant(stack);
                case 1 -> isValidSoilForAnyRecipe(stack);
                case 2 -> isFertilizer(stack);
                default -> true;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 0) invalidateRecipeCache();
            PlanterBlockEntity.this.setChanged();
            Level lvl = PlanterBlockEntity.this.level;
            if (lvl != null && !lvl.isClientSide()) {
                lvl.sendBlockUpdated(
                        PlanterBlockEntity.this.getBlockPos(),
                        PlanterBlockEntity.this.getBlockState(),
                        PlanterBlockEntity.this.getBlockState(),
                        3
                );
            }
        }
    };

    private int growthProgress = 0;
    private int growthTicks = 0;
    private boolean readyToHarvest = false;
    private int lastGrowthStage = -1;

    public PlanterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.PLANTER_BLOCK_BE.get(), pos, blockState);
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
        return findTreeRecipe(inventory.getStackInSlot(0)).isPresent();
    }

    public static boolean isFertilizer(ItemStack stack) {
        return stack.getItem().builtInRegistryHolder().getData(ATEDataMaps.FERTILIZERS) != null;
    }

    private float getClocheGrowthModifier(BlockState state) {
        return state.getValue(PlanterBlock.CLOCHED) ? (float) Config.getClocheSpeedMultiplier() : 1.0F;
    }

    private float getClocheYieldModifier(BlockState state) {
        return state.getValue(PlanterBlock.CLOCHED) ? (float) Config.getClocheYieldMultiplier() : 1.0F;
    }

    private float getFertilizerGrowthModifier() {
        ItemStack stack = inventory.getStackInSlot(2);
        if (stack.isEmpty()) return 1.0F;
        FertilizerData data = stack.getItem().builtInRegistryHolder().getData(ATEDataMaps.FERTILIZERS);
        return data != null ? data.speedMultiplier() : 1.0F;
    }

    private float getFertilizerYieldModifier() {
        ItemStack stack = inventory.getStackInSlot(2);
        if (stack.isEmpty()) return 1.0F;
        FertilizerData data = stack.getItem().builtInRegistryHolder().getData(ATEDataMaps.FERTILIZERS);
        return data != null ? data.yieldMultiplier() : 1.0F;
    }

    public float getSoilGrowthModifier(ItemStack soilStack) {
        if (soilStack.isEmpty()) return 1.0F;
        SoilModifierData data = soilStack.getItem().builtInRegistryHolder().getData(ATEDataMaps.SOIL_MODIFIERS);
        return data != null ? data.growthModifier() : 1.0F;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PlanterBlockEntity be) {
        if (level.isClientSide()) return;
        ItemStack plantStack = be.inventory.getStackInSlot(0);
        ItemStack soilStack = be.inventory.getStackInSlot(1);
        if (plantStack.isEmpty() || soilStack.isEmpty()) { be.resetGrowth(); return; }
        if (!be.isValidPlantSoilCombination(plantStack, soilStack)) { be.resetGrowth(); return; }
        if (!be.readyToHarvest) {
            float soilMod = be.getSoilGrowthModifier(soilStack);
            float fertMod = be.getFertilizerGrowthModifier();
            float clocheMod = be.getClocheGrowthModifier(state);
            float totalMod = soilMod * fertMod * clocheMod;
            int baseTime = Config.getPlanterBaseProcessingTime();
            int adjustedTime = Math.max(1, Math.round((float) baseTime / totalMod));
            be.growthTicks++;
            if (be.growthTicks >= adjustedTime) {
                be.readyToHarvest = true;
                be.growthProgress = 100;
                be.lastGrowthStage = be.getGrowthStage();
                level.sendBlockUpdated(pos, state, state, 3);
                be.setChanged();
            } else {
                be.growthProgress = (int) ((float) be.growthTicks / adjustedTime * 100.0F);
                int stage = be.getGrowthStage();
                if (stage != be.lastGrowthStage) be.lastGrowthStage = stage;
                if (be.growthTicks % 20 == 0) {
                    level.sendBlockUpdated(pos, state, state, 3);
                    be.setChanged();
                }
            }
        }
        if (be.readyToHarvest && be.hasOutputSpace()) {
            be.harvestPlant(state);
            tryOutputItemsBelow(level, pos, be);
        }
    }

    private void resetGrowth() {
        this.growthProgress = 0;
        this.growthTicks = 0;
        this.readyToHarvest = false;
        this.lastGrowthStage = -1;
        this.setChanged();
    }

    public boolean isReadyToHarvest() { return readyToHarvest; }

    public void harvestPlant(BlockState state) {
        if (!readyToHarvest) return;
        float fertYield = getFertilizerYieldModifier();
        float clocheYield = getClocheYieldModifier(state);
        List<ItemStack> drops = applyYieldModifier(getHarvestDrops(inventory.getStackInSlot(0)), fertYield * clocheYield);
        for (ItemStack drop : drops) {
            int remaining = drop.getCount();
            for (int slot = 3; slot <= 14 && remaining > 0; slot++) {
                ItemStack existing = inventory.getStackInSlot(slot);
                if (!existing.isEmpty() && existing.is(drop.getItem())) {
                    int space = existing.getMaxStackSize() - existing.getCount();
                    if (space <= 0) continue;
                    int toAdd = Math.min(space, remaining);
                    existing.grow(toAdd);
                    remaining -= toAdd;
                }
            }
            for (int slot = 3; slot <= 14 && remaining > 0; slot++) {
                if (inventory.getStackInSlot(slot).isEmpty()) {
                    int toPlace = Math.min(remaining, drop.getMaxStackSize());
                    inventory.setStackInSlot(slot, new ItemStack(drop.getItem(), toPlace));
                    remaining -= toPlace;
                }
            }
            if (remaining > 0) break;
        }
        consumeFertilizer();
        resetGrowth();
    }

    private void consumeFertilizer() {
        ItemStack stack = inventory.getStackInSlot(2);
        if (stack.isEmpty()) return;
        stack.shrink(1);
        inventory.setStackInSlot(2, stack.isEmpty() ? ItemStack.EMPTY : stack);
        setChanged();
    }

    private List<ItemStack> applyYieldModifier(List<ItemStack> drops, float mod) {
        if (mod == 1.0F) return drops;
        List<ItemStack> out = new ArrayList<>();
        for (ItemStack drop : drops)
            out.add(drop.copyWithCount(Math.max(1, Math.round(drop.getCount() * mod))));
        return out;
    }

    public void applyManualFertilizer(float speedMultiplier) {
        if (readyToHarvest) return;
        ItemStack plantStack = inventory.getStackInSlot(0);
        ItemStack soilStack = inventory.getStackInSlot(1);
        if (plantStack.isEmpty() || soilStack.isEmpty()) return;
        float soilMod = getSoilGrowthModifier(soilStack);
        float clocheMod = getClocheGrowthModifier(getBlockState());
        int adjustedTime = Math.max(1, Math.round(Config.getPlanterBaseProcessingTime() / (soilMod * clocheMod)));
        int boost = Math.max(1, Math.round(adjustedTime * 0.25F * speedMultiplier));
        growthTicks = Math.min(adjustedTime, growthTicks + boost);
        growthProgress = (int) ((float) growthTicks / adjustedTime * 100.0F);
        if (growthTicks >= adjustedTime) { readyToHarvest = true; growthProgress = 100; }
        lastGrowthStage = getGrowthStage();
        setChanged();
    }

    private List<ItemStack> getHarvestDrops(ItemStack plantStack) {
        if (plantStack.isEmpty()) return List.of();
        Optional<CropRecipe> crop = findCropRecipe(plantStack);
        List<DropEntry> entries = crop.map(CropRecipe::getDrops).orElseGet(() -> findTreeRecipe(plantStack).map(TreeRecipe::getDrops).orElse(List.of()));
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

    private static void tryOutputItemsBelow(Level level, BlockPos pos, PlanterBlockEntity be) {
        BlockPos below = pos.below();
        if (level.getBlockEntity(below) == null) return;
        IItemHandler target = level.getCapability(Capabilities.ItemHandler.BLOCK, below, Direction.UP);
        if (target == null) return;
        boolean changed = false;
        for (int slot = 3; slot <= 14; slot++) {
            ItemStack extracted = be.inventory.extractItem(slot, 64, true);
            if (extracted.isEmpty()) continue;
            ItemStack remaining = ItemHandlerHelper.insertItemStacked(target, extracted, false);
            int inserted = extracted.getCount() - remaining.getCount();
            if (inserted > 0) { be.inventory.extractItem(slot, inserted, false); changed = true; }
        }
        if (changed) {
            be.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }
    }

    public boolean hasOutputSpace() {
        List<ItemStack> drops = getHarvestDrops(inventory.getStackInSlot(0));
        Map<Integer, Integer> simAmounts = new HashMap<>();
        Map<Integer, Item> simItems = new HashMap<>();
        Map<Integer, Integer> simCapacity = new HashMap<>();
        for (int slot = 3; slot <= 14; slot++) {
            ItemStack s = inventory.getStackInSlot(slot);
            simAmounts.put(slot, s.getCount());
            simItems.put(slot, s.isEmpty() ? null : s.getItem());
            simCapacity.put(slot, s.isEmpty() ? 64 : s.getMaxStackSize());
        }
        for (ItemStack drop : drops) {
            int remaining = drop.getCount();
            for (int slot = 3; slot <= 14 && remaining > 0; slot++) {
                Item here = simItems.get(slot);
                if (here != null && here == drop.getItem()) {
                    int space = simCapacity.get(slot) - simAmounts.get(slot);
                    int toAdd = Math.min(space, remaining);
                    simAmounts.merge(slot, toAdd, Integer::sum);
                    remaining -= toAdd;
                }
            }
            for (int slot = 3; slot <= 14 && remaining > 0; slot++) {
                if (simItems.get(slot) == null) {
                    simItems.put(slot, drop.getItem());
                    simAmounts.put(slot, remaining);
                    remaining = 0;
                }
            }
            if (remaining > 0) return false;
        }
        return true;
    }

    public IItemHandler getCapabilityHandler() {
        return new IItemHandler() {
            @Override public int getSlots() { return inventory.getSlots(); }
            @Override public ItemStack getStackInSlot(int slot) { return inventory.getStackInSlot(slot); }
            @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (slot != 2) return stack;
                if (!isFertilizer(stack)) return stack;
                return inventory.insertItem(slot, stack, simulate);
            }
            @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
            @Override public int getSlotLimit(int slot) { return inventory.getSlotLimit(slot); }
            @Override public boolean isItemValid(int slot, ItemStack stack) {
                if (slot != 2) return false;
                return isFertilizer(stack);
            }
        };
    }

    public IItemHandler getExtractHandler() {
        return new IItemHandler() {
            @Override public int getSlots() { return inventory.getSlots(); }
            @Override public ItemStack getStackInSlot(int slot) { return inventory.getStackInSlot(slot); }
            @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }
            @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot < 3) return ItemStack.EMPTY;
                return inventory.extractItem(slot, amount, simulate);
            }
            @Override public int getSlotLimit(int slot) { return inventory.getSlotLimit(slot); }
            @Override public boolean isItemValid(int slot, ItemStack stack) { return false; }
        };
    }

    public float getGrowthProgress() { return growthProgress / 100.0F; }

    public int getGrowthStage() {
        if (isTree()) return growthProgress > 50 ? 1 : 0;
        return Math.min(8, (int) (growthProgress / 12.5F));
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
        tag.putInt("growthProgress", growthProgress);
        tag.putInt("growthTicks", growthTicks);
        tag.putBoolean("readyToHarvest", readyToHarvest);
        tag.putInt("lastGrowthStage", lastGrowthStage);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        growthProgress = tag.getInt("growthProgress");
        growthTicks = tag.getInt("growthTicks");
        readyToHarvest = tag.getBoolean("readyToHarvest");
        lastGrowthStage = tag.getInt("lastGrowthStage");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.agritechevolved.basic_planter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new BasicPlanterMenu(i, inventory, this);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}