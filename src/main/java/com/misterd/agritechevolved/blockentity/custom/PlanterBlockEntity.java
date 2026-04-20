package com.misterd.agritechevolved.blockentity.custom;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.custom.PlanterBlock;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.config.PlantablesConfig;
import com.misterd.agritechevolved.gui.custom.BasicPlanterMenu;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class PlanterBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler inventory = new ItemStackHandler(15) {
        @Override
        public int getSlotLimit(int slot) {
            return slot != 0 && slot != 1 ? super.getSlotLimit(slot) : 1;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            String itemId = RegistryHelper.getItemId(stack);
            return switch (slot) {
                case 0 -> PlantablesConfig.isValidSeed(itemId) || PlantablesConfig.isValidSapling(itemId);
                case 1 -> PlantablesConfig.isValidSoil(itemId);
                case 2 -> PlantablesConfig.isValidFertilizer(itemId);
                default -> false;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            PlanterBlockEntity.this.setChanged();
            if (!PlanterBlockEntity.this.level.isClientSide()) {
                PlanterBlockEntity.this.level.sendBlockUpdated(
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

    public static void tick(Level level, BlockPos pos, BlockState state, PlanterBlockEntity blockEntity) {
        if (level.isClientSide()) return;

        ItemStack plantStack = blockEntity.inventory.getStackInSlot(0);
        ItemStack soilStack = blockEntity.inventory.getStackInSlot(1);

        if (plantStack.isEmpty() || soilStack.isEmpty()) {
            blockEntity.resetGrowth();
            return;
        }

        String plantId = RegistryHelper.getItemId(plantStack);
        String soilId = RegistryHelper.getItemId(soilStack);

        if (!blockEntity.isValidPlantSoilCombination(plantId, soilId)) {
            blockEntity.resetGrowth();
            return;
        }

        if (!blockEntity.readyToHarvest) {
            float soilModifier = blockEntity.getSoilGrowthModifier(soilStack);
            float fertilizerGrowthModifier = blockEntity.getFertilizerGrowthModifier();
            float clocheGrowthModifier = blockEntity.getClocheGrowthModifier(state);
            float totalModifier = soilModifier * fertilizerGrowthModifier * clocheGrowthModifier;
            int baseGrowthTime = blockEntity.getBaseGrowthTime(plantStack);
            int adjustedGrowthTime = Math.max(1, Math.round((float) baseGrowthTime / totalModifier));
            blockEntity.growthTicks++;

            if (blockEntity.growthTicks >= adjustedGrowthTime) {
                blockEntity.readyToHarvest = true;
                blockEntity.growthProgress = 100;
                blockEntity.lastGrowthStage = blockEntity.getGrowthStage();
                level.sendBlockUpdated(pos, state, state, 3);
                blockEntity.setChanged();
            } else {
                blockEntity.growthProgress = (int) ((float) blockEntity.growthTicks / adjustedGrowthTime * 100.0F);
                int currentGrowthStage = blockEntity.getGrowthStage();
                if (currentGrowthStage != blockEntity.lastGrowthStage) {
                    blockEntity.lastGrowthStage = currentGrowthStage;
                }
                if (blockEntity.growthTicks % 20 == 0) {
                    level.sendBlockUpdated(pos, state, state, 3);
                    blockEntity.setChanged();
                }
            }
        }

        if (blockEntity.readyToHarvest && blockEntity.hasOutputSpace()) {
            blockEntity.harvestPlant(state);
        }

        tryOutputItemsBelow(level, pos, blockEntity);
    }

    private float getClocheGrowthModifier(BlockState state) {
        return state.getValue(PlanterBlock.CLOCHED) ? (float) Config.getClocheSpeedMultiplier() : 1.0F;
    }

    private float getClocheYieldModifier(BlockState state) {
        return state.getValue(PlanterBlock.CLOCHED) ? (float) Config.getClocheYieldMultiplier() : 1.0F;
    }

    private float getFertilizerGrowthModifier() {
        ItemStack fertilizerStack = this.inventory.getStackInSlot(2);
        if (fertilizerStack.isEmpty()) return 1.0F;
        String fertilizerId = RegistryHelper.getItemId(fertilizerStack);
        PlantablesConfig.FertilizerInfo info = PlantablesConfig.getFertilizerInfo(fertilizerId);
        return info != null ? info.speedMultiplier : 1.0F;
    }

    private float getFertilizerYieldModifier() {
        ItemStack fertilizerStack = this.inventory.getStackInSlot(2);
        if (fertilizerStack.isEmpty()) return 1.0F;
        String fertilizerId = RegistryHelper.getItemId(fertilizerStack);
        PlantablesConfig.FertilizerInfo info = PlantablesConfig.getFertilizerInfo(fertilizerId);
        return info != null ? info.yieldMultiplier : 1.0F;
    }

    private boolean isValidPlantSoilCombination(String plantId, String soilId) {
        if (PlantablesConfig.isValidSeed(plantId)) {
            return PlantablesConfig.isSoilValidForSeed(soilId, plantId);
        } else if (PlantablesConfig.isValidSapling(plantId)) {
            return PlantablesConfig.isSoilValidForSapling(soilId, plantId);
        }
        return false;
    }

    private boolean isTree() {
        ItemStack plantStack = this.inventory.getStackInSlot(0);
        if (plantStack.isEmpty()) return false;
        return PlantablesConfig.isValidSapling(RegistryHelper.getItemId(plantStack));
    }

    private boolean isCrop() {
        ItemStack plantStack = this.inventory.getStackInSlot(0);
        if (plantStack.isEmpty()) return false;
        return PlantablesConfig.isValidSeed(RegistryHelper.getItemId(plantStack));
    }

    private int getBaseGrowthTime(ItemStack plantStack) {
        String itemId = RegistryHelper.getItemId(plantStack);
        if (PlantablesConfig.isValidSeed(itemId)) {
            return Config.getPlanterBaseProcessingTime();
        } else if (PlantablesConfig.isValidSapling(itemId)) {
            return PlantablesConfig.getBaseSaplingGrowthTime(itemId);
        }
        return Config.getPlanterBaseProcessingTime();
    }

    public float getSoilGrowthModifier(ItemStack soilStack) {
        if (soilStack.isEmpty()) return 1.0F;
        return PlantablesConfig.getSoilGrowthModifier(RegistryHelper.getItemId(soilStack));
    }

    private void resetGrowth() {
        this.growthProgress = 0;
        this.growthTicks = 0;
        this.readyToHarvest = false;
        this.lastGrowthStage = -1;
        this.setChanged();
    }

    public void harvestPlant(BlockState state) {
        if (!this.readyToHarvest) return;

        float fertilizerYieldModifier = this.getFertilizerYieldModifier();
        float clocheYieldModifier = this.getClocheYieldModifier(state);
        List<ItemStack> drops = applyYieldModifier(
                this.getHarvestDrops(this.inventory.getStackInSlot(0)),
                fertilizerYieldModifier * clocheYieldModifier
        );

        for (ItemStack drop : drops) {
            int remaining = drop.getCount();

            for (int slot = 3; slot <= 14; slot++) {
                ItemStack existing = this.inventory.getStackInSlot(slot);
                if (existing.isEmpty()) {
                    int toPlace = Math.min(remaining, drop.getMaxStackSize());
                    this.inventory.setStackInSlot(slot, new ItemStack(drop.getItem(), toPlace));
                    remaining -= toPlace;
                } else if (existing.is(drop.getItem()) && existing.getCount() < existing.getMaxStackSize()) {
                    int space = existing.getMaxStackSize() - existing.getCount();
                    int toAdd = Math.min(space, remaining);
                    existing.grow(toAdd);
                    remaining -= toAdd;
                }

                if (remaining <= 0) break;
            }

            if (remaining > 0) break;
        }

        this.consumeFertilizer();
        this.resetGrowth();
    }

    private void consumeFertilizer() {
        ItemStack fertilizerStack = this.inventory.getStackInSlot(2);
        if (fertilizerStack.isEmpty()) return;
        fertilizerStack.shrink(1);
        this.inventory.setStackInSlot(2, fertilizerStack.isEmpty() ? ItemStack.EMPTY : fertilizerStack);
        this.setChanged();
    }

    private List<ItemStack> applyYieldModifier(List<ItemStack> drops, float yieldModifier) {
        if (yieldModifier == 1.0F) return drops;
        List<ItemStack> modified = new ArrayList<>();
        for (ItemStack drop : drops) {
            int newCount = Math.max(1, Math.round(drop.getCount() * yieldModifier));
            modified.add(new ItemStack(drop.getItem(), newCount));
        }
        return modified;
    }

    private List<ItemStack> getHarvestDrops(ItemStack plantStack) {
        List<ItemStack> drops = new ArrayList<>();
        if (plantStack.isEmpty()) return drops;

        String plantId = RegistryHelper.getItemId(plantStack);
        List<PlantablesConfig.DropInfo> configDrops;

        if (PlantablesConfig.isValidSeed(plantId)) {
            configDrops = PlantablesConfig.getCropDrops(plantId);
        } else if (PlantablesConfig.isValidSapling(plantId)) {
            configDrops = PlantablesConfig.getTreeDrops(plantId);
        } else {
            return drops;
        }

        Random random = new Random();
        for (PlantablesConfig.DropInfo dropInfo : configDrops) {
            if (random.nextFloat() <= dropInfo.chance) {
                int count = dropInfo.maxCount > dropInfo.minCount
                        ? dropInfo.minCount + random.nextInt(dropInfo.maxCount - dropInfo.minCount + 1)
                        : dropInfo.minCount;
                Item item = RegistryHelper.getItem(dropInfo.item);
                if (item != null) {
                    drops.add(new ItemStack(item, count));
                }
            }
        }

        return drops;
    }

    private static void tryOutputItemsBelow(Level level, BlockPos pos, PlanterBlockEntity blockEntity) {
        BlockPos belowPos = pos.below();
        if (level.getBlockEntity(belowPos) == null) return;

        IItemHandler targetInventory = level.getCapability(Capabilities.ItemHandler.BLOCK, belowPos, Direction.UP);
        if (targetInventory == null) return;

        boolean changed = false;
        for (int slot = 3; slot <= 14; slot++) {
            if (blockEntity.inventory.getStackInSlot(slot).isEmpty()) continue;

            ItemStack extracted = blockEntity.inventory.extractItem(slot, 64, true);
            if (extracted.isEmpty()) continue;

            ItemStack remaining = ItemHandlerHelper.insertItemStacked(targetInventory, extracted, false);
            int inserted = extracted.getCount() - remaining.getCount();
            if (inserted > 0) {
                blockEntity.inventory.extractItem(slot, inserted, false);
                changed = true;
            }
        }

        if (changed) {
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }
    }

    public boolean hasOutputSpace() {
        List<ItemStack> potentialDrops = this.getHarvestDrops(this.inventory.getStackInSlot(0));
        Map<Integer, ItemStack> simulatedSlots = new HashMap<>();

        for (int slot = 3; slot <= 14; slot++) {
            ItemStack stack = this.inventory.getStackInSlot(slot);
            simulatedSlots.put(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }

        for (ItemStack drop : potentialDrops) {
            int remaining = drop.getCount();

            for (int slot = 3; slot <= 14; slot++) {
                ItemStack existing = simulatedSlots.get(slot);
                if (!existing.isEmpty() && existing.is(drop.getItem()) && existing.getCount() < existing.getMaxStackSize()) {
                    int space = existing.getMaxStackSize() - existing.getCount();
                    int toAdd = Math.min(space, remaining);
                    existing.grow(toAdd);
                    remaining -= toAdd;
                    if (remaining <= 0) break;
                }
            }

            if (remaining > 0) {
                for (int slot = 3; slot <= 14; slot++) {
                    if (simulatedSlots.get(slot).isEmpty()) {
                        simulatedSlots.put(slot, new ItemStack(drop.getItem(), remaining));
                        remaining = 0;
                        break;
                    }
                }
            }

            if (remaining > 0) return false;
        }

        return true;
    }

    public float getGrowthProgress() {
        return this.growthProgress / 100.0F;
    }

    public int getGrowthStage() {
        if (this.isTree()) {
            return this.growthProgress > 50 ? 1 : 0;
        }
        return Math.min(8, (int) (this.growthProgress / 12.5F));
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(this.inventory.getSlots());
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            inv.setItem(i, this.inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public IItemHandler getCapabilityHandler() {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return inventory.getSlots();
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return inventory.getStackInSlot(slot);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (slot != 2) return stack;

                String itemId = RegistryHelper.getItemId(stack);
                if (!PlantablesConfig.isValidFertilizer(itemId)) return stack;

                return inventory.insertItem(slot, stack, simulate);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                return inventory.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if (slot != 2) return false;
                return PlantablesConfig.isValidFertilizer(RegistryHelper.getItemId(stack));
            }
        };
    }

    public IItemHandler getExtractHandler() {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return inventory.getSlots();
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return inventory.getStackInSlot(slot);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return stack;
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot < 3) {
                    return ItemStack.EMPTY;
                }
                return inventory.extractItem(slot, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return inventory.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return false;
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", this.inventory.serializeNBT(registries));
        tag.putInt("growthProgress", this.growthProgress);
        tag.putInt("growthTicks", this.growthTicks);
        tag.putBoolean("readyToHarvest", this.readyToHarvest);
        tag.putInt("lastGrowthStage", this.lastGrowthStage);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        this.growthProgress = tag.getInt("growthProgress");
        this.growthTicks = tag.getInt("growthTicks");
        this.readyToHarvest = tag.getBoolean("readyToHarvest");
        this.lastGrowthStage = tag.getInt("lastGrowthStage");
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
        return this.saveWithoutMetadata(registries);
    }
}