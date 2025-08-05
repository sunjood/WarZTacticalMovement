package com.warz.tacticalmovement.client;

import com.warz.tacticalmovement.common.capability.ITacticalCapability;
import com.warz.tacticalmovement.common.capability.TacticalCapabilityProvider;
import com.warz.tacticalmovement.config.TacticalConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class CameraEventHandler {
    
    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        
        if (player == null) {
            return;
        }
        
        // 获取玩家的战术能力
        player.getCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY).ifPresent(cap -> {
            float peekProgress = cap.getPeekProgress();
            ITacticalCapability.PeekDirection peekDirection = cap.getPeekDirection();
            
            if (peekProgress > 0.01f && peekDirection != ITacticalCapability.PeekDirection.NONE) {
                // 计算身体倾斜角度（roll角度）
                float maxRollAngle = TacticalConfig.PEEK_ANGLE.get().floatValue();
                float rollAngle = 0.0f;
                
                switch (peekDirection) {
                    case LEFT:
                        rollAngle = -maxRollAngle * peekProgress; // 向左倾斜为负值
                        break;
                    case RIGHT:
                        rollAngle = maxRollAngle * peekProgress; // 向右倾斜为正值
                        break;
                    default:
                        break;
                }
                
                // 应用身体倾斜效果（roll角度）
                if (Math.abs(rollAngle) > 0.01f) {
                    // 设置roll角度来实现身体倾斜
                    float currentRoll = (float) event.getRoll();
                    event.setRoll(currentRoll + rollAngle);
                    
                    // 调试输出
                    System.out.println("[WarZ Tactical] Body lean applied - Direction: " + peekDirection + ", Progress: " + peekProgress + ", Roll: " + rollAngle);
                }
            }
        });
    }
    

}