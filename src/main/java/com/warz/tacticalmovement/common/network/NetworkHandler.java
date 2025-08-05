package com.warz.tacticalmovement.common.network;

import com.warz.tacticalmovement.WarZTacticalMovement;
import com.warz.tacticalmovement.common.network.packets.PeekStatePacket;
import com.warz.tacticalmovement.common.network.packets.TacticalSyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(WarZTacticalMovement.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    
    private static int packetId = 0;
    
    public static void init() {
        // 注册数据包
        INSTANCE.messageBuilder(PeekStatePacket.class, nextId())
            .decoder(PeekStatePacket::decode)
            .encoder(PeekStatePacket::encode)
            .consumerMainThread(PeekStatePacket::handle)
            .add();
            
        INSTANCE.messageBuilder(TacticalSyncPacket.class, nextId())
            .decoder(TacticalSyncPacket::decode)
            .encoder(TacticalSyncPacket::encode)
            .consumerMainThread(TacticalSyncPacket::handle)
            .add();
    }
    
    private static int nextId() {
        return packetId++;
    }
    
    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
    
    public static void sendToPlayer(Object packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
    
    public static void sendToAllPlayers(Object packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }
    
    public static void sendToPlayersTrackingEntity(Object packet, net.minecraft.world.entity.Entity entity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
    }
}