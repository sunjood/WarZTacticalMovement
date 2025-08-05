package com.warz.tacticalmovement.common.network.packets;

import com.warz.tacticalmovement.common.capability.ITacticalCapability;
import com.warz.tacticalmovement.common.capability.TacticalCapabilityProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PeekStatePacket {
    private final ITacticalCapability.PeekDirection peekDirection;
    private final boolean isTaczAiming;
    
    public PeekStatePacket(ITacticalCapability.PeekDirection peekDirection, boolean isTaczAiming) {
        this.peekDirection = peekDirection;
        this.isTaczAiming = isTaczAiming;
    }
    
    public static void encode(PeekStatePacket packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.peekDirection);
        buf.writeBoolean(packet.isTaczAiming);
    }
    
    public static PeekStatePacket decode(FriendlyByteBuf buf) {
        ITacticalCapability.PeekDirection peekDirection = buf.readEnum(ITacticalCapability.PeekDirection.class);
        boolean isTaczAiming = buf.readBoolean();
        return new PeekStatePacket(peekDirection, isTaczAiming);
    }
    
    public static void handle(PeekStatePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                player.getCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY).ifPresent(cap -> {
                    cap.setPeekDirection(packet.peekDirection);
                    cap.setTaczAiming(packet.isTaczAiming);
                    
                    // 同步给其他玩家
                    TacticalSyncPacket syncPacket = new TacticalSyncPacket(
                        player.getId(),
                        packet.peekDirection,
                        cap.getPeekProgress(),
                        cap.getHeadTiltAngle(),
                        cap.getBodyLeanAngle()
                    );
                    
                    // 发送给追踪该玩家的所有客户端
                    com.warz.tacticalmovement.common.network.NetworkHandler.sendToPlayersTrackingEntity(syncPacket, player);
                });
            }
        });
        context.setPacketHandled(true);
    }
}