package com.warz.tacticalmovement.common.network.packets;

import com.warz.tacticalmovement.common.capability.ITacticalCapability;
import com.warz.tacticalmovement.common.capability.TacticalCapabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TacticalSyncPacket {
    private final int playerId;
    private final ITacticalCapability.PeekDirection peekDirection;
    private final float peekProgress;
    private final float headTiltAngle;
    private final float bodyLeanAngle;
    
    public TacticalSyncPacket(int playerId, ITacticalCapability.PeekDirection peekDirection, 
                             float peekProgress, float headTiltAngle, float bodyLeanAngle) {
        this.playerId = playerId;
        this.peekDirection = peekDirection;
        this.peekProgress = peekProgress;
        this.headTiltAngle = headTiltAngle;
        this.bodyLeanAngle = bodyLeanAngle;
    }
    
    public static void encode(TacticalSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.playerId);
        buf.writeEnum(packet.peekDirection);
        buf.writeFloat(packet.peekProgress);
        buf.writeFloat(packet.headTiltAngle);
        buf.writeFloat(packet.bodyLeanAngle);
    }
    
    public static TacticalSyncPacket decode(FriendlyByteBuf buf) {
        int playerId = buf.readInt();
        ITacticalCapability.PeekDirection peekDirection = buf.readEnum(ITacticalCapability.PeekDirection.class);
        float peekProgress = buf.readFloat();
        float headTiltAngle = buf.readFloat();
        float bodyLeanAngle = buf.readFloat();
        return new TacticalSyncPacket(playerId, peekDirection, peekProgress, headTiltAngle, bodyLeanAngle);
    }
    
    public static void handle(TacticalSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            handleClientSide(packet);
        });
        context.setPacketHandled(true);
    }
    
    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(TacticalSyncPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Entity entity = mc.level.getEntity(packet.playerId);
            if (entity instanceof Player player) {
                player.getCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY).ifPresent(cap -> {
                    cap.setPeekDirection(packet.peekDirection);
                    cap.setPeekProgress(packet.peekProgress);
                    cap.setHeadTiltAngle(packet.headTiltAngle);
                    cap.setBodyLeanAngle(packet.bodyLeanAngle);
                });
            }
        }
    }
}