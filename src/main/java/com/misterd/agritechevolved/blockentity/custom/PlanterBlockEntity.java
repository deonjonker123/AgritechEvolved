package com.misterd.agritechevolved.blockentity.custom;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.custom.PlanterBlock;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.config.PlantablesConfig;
import com.misterd.agritechevolved.gui.custom.BasicPlanterMenu;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nullable;
import java.util.*;

public class PlanterBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(15) {
        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            return (index == 0 || index == 1) ? 1 : resource.toStack().getMaxStackSize();
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (resource.isEmpty()) return false;
            String itemId = RegistryHelper.getItemId(resource.toStack());
            return switch (index) {
                case 0 -> PlantablesConfig.isValidSeed(itemId) || PlantablesConfig.isValidSapling(itemId);
                case 1 -> PlantablesConfig.isValidSoil(itemId);
                case 2 -> PlantablesConfig.isValidFertilizer(itemId);
                default -> true;
            };
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
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

    public static void tick(Level level, BlockPos pos, BlockState state, PlanterBlockEntity be) {
        if (level.isClientSide()) return;

        ItemStack plantStack = be.getStack(0);
        ItemStack soilStack  = be.getStack(1);

        if (plantStack.isEmpty() || soilStack.isEmpty()) {
            be.resetGrowth();
            return;
        }

        String plantId = RegistryHelper.getItemId(plantStack);
        String soilId  = RegistryHelper.getItemId(soilStack);

        if (!be.isValidPlantSoilCombination(plantId, soilId)) {
            be.resetGrowth();
            return;
        }

        if (!be.readyToHarvest) {
            float soilMod = be.getSoilGrowthModifier(soilStack);
            float fertMod = be.getFertilizerGrowthModifier();
            float clocheMod = be.getClocheGrowthModifier(state);
            float totalMod = soilMod * fertMod * clocheMod;
            int baseTime = be.getBaseGrowthTime(plantStack);
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
                if (stage != be.lastGrowthStage) {
                    be.lastGrowthStage = stage;
                }
                if (be.growthTicks % 20 == 0) {
                    level.sendBlockUpdated(pos, state, state, 3);
                    be.setChanged();
                }
            }
        }

        if (be.readyToHarvest && be.hasOutputSpace()) {
            be.harvestPlant(state);
        }

        tryOutputItemsBelow(level, pos, be);
    }

    private float getClocheGrowthModifier(BlockState state) {
        return state.getValue(PlanterBlock.CLOCHED) ? (float) Config.getClocheSpeedMultiplier() : 1.0F;
    }

    private float getClocheYieldModifier(BlockState state) {
        return state.getValue(PlanterBlock.CLOCHED) ? (float) Config.getClocheYieldMultiplier() : 1.0F;
    }

    private float getFertilizerGrowthModifier() {
        ItemStack stack = getStack(2);
        if (stack.isEmpty()) return 1.0F;
        PlantablesConfig.FertilizerInfo info = PlantablesConfig.getFertilizerInfo(RegistryHelper.getItemId(stack));
        return info != null ? info.speedMultiplier : 1.0F;
    }

    private float getFertilizerYieldModifier() {
        ItemStack stack = getStack(2);
        if (stack.isEmpty()) return 1.0F;
        PlantablesConfig.FertilizerInfo info = PlantablesConfig.getFertilizerInfo(RegistryHelper.getItemId(stack));
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
        ItemStack s = getStack(0);
        return !s.isEmpty() && PlantablesConfig.isValidSapling(RegistryHelper.getItemId(s));
    }

    private int getBaseGrowthTime(ItemStack plantStack) {
        String id = RegistryHelper.getItemId(plantStack);
        if (PlantablesConfig.isValidSeed(id)) return Config.getPlanterBaseProcessingTime();
        if (PlantablesConfig.isValidSapling(id)) return PlantablesConfig.getBaseSaplingGrowthTime(id);
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

    public boolean isReadyToHarvest() {
        return readyToHarvest;
    }

    public void harvestPlant(BlockState state) {
        if (!readyToHarvest) return;

        float fertYield = getFertilizerYieldModifier();
        float clocheYield = getClocheYieldModifier(state);
        List<ItemStack> drops = applyYieldModifier(getHarvestDrops(getStack(0)), fertYield * clocheYield);

        for (ItemStack drop : drops) {
            int remaining = drop.getCount();
            ItemResource res = ItemResource.of(drop);

            for (int slot = 3; slot <= 14 && remaining > 0; slot++) {
                ItemStack existing = getStack(slot);
                if (!existing.isEmpty() && existing.is(drop.getItem())) {
                    int space = existing.getMaxStackSize() - existing.getCount();
                    if (space <= 0) continue;
                    int toAdd = Math.min(space, remaining);
                    try (Transaction tx = Transaction.openRoot()) {
                        int inserted = inventory.insert(slot, res, toAdd, tx);
                        tx.commit();
                        remaining -= inserted;
                    }
                }
            }

            for (int slot = 3; slot <= 14 && remaining > 0; slot++) {
                if (getStack(slot).isEmpty()) {
                    int toPlace = Math.min(remaining, drop.getMaxStackSize());
                    try (Transaction tx = Transaction.openRoot()) {
                        int inserted = inventory.insert(slot, res, toPlace, tx);
                        tx.commit();
                        remaining -= inserted;
                    }
                }
            }

            if (remaining > 0) break;
        }

        consumeFertilizer();
        resetGrowth();
    }

    public void applyManualFertilizer(float speedMultiplier) {
        if (readyToHarvest) return;
        ItemStack plantStack = getStack(0);
        ItemStack soilStack = getStack(1);
        if (plantStack.isEmpty() || soilStack.isEmpty()) return;

        float soilMod = getSoilGrowthModifier(soilStack);
        float clocheMod = getClocheGrowthModifier(getBlockState());
        int adjustedTime = Math.max(1, Math.round(getBaseGrowthTime(plantStack) / (soilMod * clocheMod)));

        int boost = Math.max(1, Math.round(adjustedTime * 0.25F * speedMultiplier));
        growthTicks = Math.min(adjustedTime, growthTicks + boost);
        growthProgress = (int) ((float) growthTicks / adjustedTime * 100.0F);

        if (growthTicks >= adjustedTime) {
            readyToHarvest = true;
            growthProgress = 100;
        }

        lastGrowthStage = getGrowthStage();
        setChanged();
    }

    private void consumeFertilizer() {
        ItemStack stack = getStack(2);
        if (stack.isEmpty()) return;
        try (Transaction tx = Transaction.openRoot()) {
            inventory.extract(2, ItemResource.of(stack), 1, tx);
            tx.commit();
        }
        setChanged();
    }

    private List<ItemStack> applyYieldModifier(List<ItemStack> drops, float mod) {
        if (mod == 1.0F) return drops;
        List<ItemStack> out = new ArrayList<>();
        for (ItemStack drop : drops) {
            out.add(new ItemStack(drop.getItem(), Math.max(1, Math.round(drop.getCount() * mod))));
        }
        return out;
    }

    private List<ItemStack> getHarvestDrops(ItemStack plantStack) {
        List<ItemStack> drops = new ArrayList<>();
        if (plantStack.isEmpty()) return drops;

        String plantId = RegistryHelper.getItemId(plantStack);
        List<PlantablesConfig.DropInfo> configDrops;

        if (PlantablesConfig.isValidSeed(plantId)) configDrops = PlantablesConfig.getCropDrops(plantId);
        else if (PlantablesConfig.isValidSapling(plantId)) configDrops = PlantablesConfig.getTreeDrops(plantId);
        else return drops;

        Random rng = new Random();
        for (PlantablesConfig.DropInfo info : configDrops) {
            if (rng.nextFloat() <= info.chance) {
                int count = info.maxCount > info.minCount
                        ? info.minCount + rng.nextInt(info.maxCount - info.minCount + 1)
                        : info.minCount;
                Item item = RegistryHelper.getItem(info.item);
                if (item != null) drops.add(new ItemStack(item, count));
            }
        }
        return drops;
    }

    private static void tryOutputItemsBelow(Level level, BlockPos pos, PlanterBlockEntity be) {
        BlockPos below = pos.below();
        if (level.getBlockEntity(below) == null) return;

        ResourceHandler<ItemResource> target = level.getCapability(Capabilities.Item.BLOCK, below, Direction.UP);
        if (target == null) return;

        boolean changed = false;
        for (int slot = 3; slot <= 14; slot++) {
            ItemResource res = be.inventory.getResource(slot);
            if (res.isEmpty()) continue;
            int available = be.inventory.getAmountAsInt(slot);
            if (available <= 0) continue;

            try (Transaction tx = Transaction.openRoot()) {
                int insertable = target.insert(res, available, tx);
                if (insertable <= 0) continue;

                int extracted = be.inventory.extract(slot, res, insertable, tx);
                if (extracted != insertable) continue;

                tx.commit();
                changed = true;
            }
        }

        if (changed) {
            be.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }
    }

    public boolean hasOutputSpace() {
        List<ItemStack> drops = getHarvestDrops(getStack(0));

        Map<Integer, Integer> simAmounts = new HashMap<>();
        Map<Integer, Item> simItems = new HashMap<>();
        Map<Integer, Integer> simCapacity = new HashMap<>();

        for (int slot = 3; slot <= 14; slot++) {
            ItemStack s = getStack(slot);
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

    public ResourceHandler<ItemResource> getInsertHandler() {
        return new ResourceHandler<>() {
            @Override
            public int size() {
                return inventory.size();
            }

            @Override
            public ItemResource getResource(int index) {
                return inventory.getResource(index);
            }

            @Override
            public long getAmountAsLong(int index) {
                return inventory.getAmountAsLong(index);
            }

            @Override
            public long getCapacityAsLong(int index, ItemResource resource) {
                return inventory.getCapacityAsLong(index, resource);
            }

            @Override
            public boolean isValid(int index, ItemResource resource) {
                if (resource.isEmpty()) return false;
                String itemId = RegistryHelper.getItemId(resource.toStack());
                return switch (index) {
                    case 0 -> PlantablesConfig.isValidSeed(itemId) || PlantablesConfig.isValidSapling(itemId);
                    case 1 -> PlantablesConfig.isValidSoil(itemId);
                    case 2 -> PlantablesConfig.isValidFertilizer(itemId);
                    default -> true;
                };
            }

            @Override
            public int insert(int index, ItemResource resource, int amount, TransactionContext tx) {
                if (index != 2) return 0;
                if (!PlantablesConfig.isValidFertilizer(RegistryHelper.getItemId(resource.toStack()))) return 0;
                return inventory.insert(index, resource, amount, tx);
            }

            @Override
            public int extract(int index, ItemResource resource, int amount, TransactionContext tx) {
                return 0;
            }
        };
    }

    public ResourceHandler<ItemResource> getExtractHandler() {
        return new ResourceHandler<>() {
            @Override
            public int size() {
                return inventory.size();
            }

            @Override
            public ItemResource getResource(int index) {
                return inventory.getResource(index);
            }

            @Override
            public long getAmountAsLong(int index) {
                return inventory.getAmountAsLong(index);
            }

            @Override
            public long getCapacityAsLong(int index, ItemResource resource) {
                return inventory.getCapacityAsLong(index, resource);
            }

            @Override
            public boolean isValid(int index, ItemResource resource) {
                return false;
            }

            @Override
            public int insert(int index, ItemResource resource, int amount, TransactionContext tx) {
                return 0;
            }

            @Override
            public int extract(int index, ItemResource resource, int amount, TransactionContext tx) {
                if (index < 3) return 0;
                return inventory.extract(index, resource, amount, tx);
            }
        };
    }

    public ItemStack getStack(int slot) {
        ItemResource res = inventory.getResource(slot);
        if (res.isEmpty()) return ItemStack.EMPTY;
        return res.toStack(inventory.getAmountAsInt(slot));
    }

    public float getGrowthProgress() {
        return growthProgress / 100.0F;
    }

    public int getGrowthStage() {
        if (isTree()) return growthProgress > 50 ? 1 : 0;
        return Math.min(8, (int) (growthProgress / 12.5F));
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (state.getValue(PlanterBlock.CLOCHED)) {
            level.addFreshEntity(new ItemEntity(level,pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ATEItems.CLOCHE.get())));
        }
        drops();
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.size());
        for (int i = 0; i < inventory.size(); i++) {
            inv.setItem(i, getStack(i));
        }
        Containers.dropContents(level, worldPosition, inv);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        inventory.serialize(output);
        output.putInt("growthProgress", growthProgress);
        output.putInt("growthTicks", growthTicks);
        output.putBoolean("readyToHarvest", readyToHarvest);
        output.putInt("lastGrowthStage", lastGrowthStage);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inventory.deserialize(input);
        growthProgress = input.getIntOr("growthProgress", 0);
        growthTicks = input.getIntOr("growthTicks", 0);
        readyToHarvest = input.getBooleanOr("readyToHarvest", false);
        lastGrowthStage = input.getIntOr("lastGrowthStage", -1);
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