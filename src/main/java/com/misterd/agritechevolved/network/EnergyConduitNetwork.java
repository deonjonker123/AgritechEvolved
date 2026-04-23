package com.misterd.agritechevolved.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class EnergyConduitNetwork {

    private final Set<BlockPos> members;
    private final Map<BlockPos, Direction> endpoints;

    public EnergyConduitNetwork(Set<BlockPos> members, Map<BlockPos, Direction> endpoints) {
        this.members = Collections.unmodifiableSet(members);
        this.endpoints = Collections.unmodifiableMap(endpoints);
    }

    public Set<BlockPos> getMembers() {
        return members;
    }

    public int distribute(Level level, int amount, boolean simulate) {
        if (endpoints.isEmpty()) return 0;

        int distributed = 0;
        int remaining = amount;

        for (Map.Entry<BlockPos, Direction> entry : endpoints.entrySet()) {
            if (remaining <= 0) break;

            IEnergyStorage dest = level.getCapability(Capabilities.EnergyStorage.BLOCK, entry.getKey(), entry.getValue());
            if (dest == null || !dest.canReceive()) continue;

            int sent = dest.receiveEnergy(remaining, simulate);
            distributed += sent;
            remaining -= sent;
        }

        return distributed;
    }
}
