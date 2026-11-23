package com.misterd.agritechevolved.network;

import com.misterd.agritechevolved.AgritechEvolved;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ATENetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private ATENetwork() {}

    public static void register() {
        CHANNEL.messageBuilder(SyncDataMapsPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncDataMapsPacket::encode)
                .decoder(SyncDataMapsPacket::decode)
                .consumerMainThread(SyncDataMapsPacket::handle)
                .add();
    }
}