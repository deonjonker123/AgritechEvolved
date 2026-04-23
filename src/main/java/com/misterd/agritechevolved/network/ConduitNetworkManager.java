package com.misterd.agritechevolved.network;

import com.misterd.agritechevolved.block.custom.EnergyConduitBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nullable;
import java.util.*;

public class ConduitNetworkManager extends SavedData {

    private static final String NAME = "conduit_networks";

    private final Map<BlockPos, EnergyConduitNetwork> energyNetworkMap = new HashMap<>();

    public static ConduitNetworkManager get(Level level) {
        if (!(level instanceof ServerLevel serverLevel))
            throw new IllegalStateException("ConduitNetworkManager accessed on client side");
        return serverLevel.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(ConduitNetworkManager::new, ConduitNetworkManager::load), NAME);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) { return tag; }

    private static ConduitNetworkManager load(CompoundTag tag, HolderLookup.Provider registries) {
        return new ConduitNetworkManager();
    }

    @Nullable
    public EnergyConduitNetwork getEnergyNetwork(BlockPos pos) {
        return energyNetworkMap.get(pos);
    }

    public void onEnergyConduitAdded(Level level, BlockPos pos) {
        if (energyNetworkMap.containsKey(pos)) return;

        Set<BlockPos> toEvict = new HashSet<>();
        for (Direction dir : Direction.values()) {
            EnergyConduitNetwork adj = energyNetworkMap.get(pos.relative(dir));
            if (adj != null) toEvict.addAll(adj.getMembers());
        }
        toEvict.forEach(energyNetworkMap::remove);

        buildEnergyNetwork(level, pos);
    }

    public void onEnergyConduitRemoved(Level level, BlockPos pos) {
        EnergyConduitNetwork old = energyNetworkMap.remove(pos);
        if (old == null) return;

        Set<BlockPos> survivors = new HashSet<>(old.getMembers());
        survivors.remove(pos);
        survivors.forEach(energyNetworkMap::remove);

        for (BlockPos member : survivors) {
            if (!energyNetworkMap.containsKey(member) && level.getBlockState(member).getBlock() instanceof EnergyConduitBlock) {buildEnergyNetwork(level, member);
            }
        }
    }

    private void buildEnergyNetwork(Level level, BlockPos start) {
        Set<BlockPos> members = new LinkedHashSet<>();
        Map<BlockPos, Direction> endpoints = new LinkedHashMap<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (members.contains(pos)) continue;
            if (!(level.getBlockState(pos).getBlock() instanceof EnergyConduitBlock)) continue;
            members.add(pos);

            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = pos.relative(dir);
                BlockState neighborState = level.getBlockState(neighborPos);
                if (neighborState.getBlock() instanceof EnergyConduitBlock) {
                    if (!members.contains(neighborPos)) queue.add(neighborPos);
                } else {
                    Direction accessFace = dir.getOpposite();
                    if (!endpoints.containsKey(neighborPos) && level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, accessFace) != null) {endpoints.put(neighborPos, accessFace);
                    }
                }
            }
        }

        EnergyConduitNetwork network = new EnergyConduitNetwork(members, endpoints);
        for (BlockPos member : members) energyNetworkMap.put(member, network);
    }
}
