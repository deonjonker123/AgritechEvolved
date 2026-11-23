package com.misterd.agritechevolved.network;

import com.misterd.agritechevolved.datamap.ATEDataMaps;
import com.misterd.agritechevolved.datamap.FertilizerData;
import com.misterd.agritechevolved.datamap.SoilModifierData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.function.Supplier;

public record SyncDataMapsPacket(Map<Item, FertilizerData> fertilizers, Map<Item, SoilModifierData> soilModifiers) {

    public static void encode(SyncDataMapsPacket packet, FriendlyByteBuf buf) {
        buf.writeMap(packet.fertilizers,
                (b, item) -> b.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item),
                (b, data) -> {
                    b.writeFloat(data.speedMultiplier());
                    b.writeFloat(data.yieldMultiplier());
                });
        buf.writeMap(packet.soilModifiers,
                (b, item) -> b.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item),
                (b, data) -> b.writeFloat(data.growthModifier()));
    }

    public static SyncDataMapsPacket decode(FriendlyByteBuf buf) {
        Map<Item, FertilizerData> fertilizers = buf.readMap(
                b -> b.readRegistryIdUnsafe(ForgeRegistries.ITEMS),
                b -> new FertilizerData(b.readFloat(), b.readFloat()));
        Map<Item, SoilModifierData> soilModifiers = buf.readMap(
                b -> b.readRegistryIdUnsafe(ForgeRegistries.ITEMS),
                b -> new SoilModifierData(b.readFloat()));
        return new SyncDataMapsPacket(fertilizers, soilModifiers);
    }

    public static void handle(SyncDataMapsPacket packet, Supplier<NetworkEvent.Context> context) {
        ATEDataMaps.setFertilizers(packet.fertilizers);
        ATEDataMaps.setSoilModifiers(packet.soilModifiers);
    }
}