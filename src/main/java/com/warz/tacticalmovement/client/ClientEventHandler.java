package com.warz.tacticalmovement.client;

import com.warz.tacticalmovement.common.capability.ITacticalCapability;
import com.warz.tacticalmovement.common.capability.TacticalCapabilityProvider;
import com.warz.tacticalmovement.common.network.NetworkHandler;
import com.warz.tacticalmovement.common.network.packets.PeekStatePacket;
import com.warz.tacticalmovement.config.TacticalConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.client.CameraType;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    
    private boolean wasLeftPeekPressed = false;
    private boolean wasRightPeekPressed = false;
    private boolean wasTacticalStancePressed = false;
    private boolean wasQuickPeekPressed = false;
    
    private ITacticalCapability.PeekDirection currentPeekDirection = ITacticalCapability.PeekDirection.NONE;
    private boolean isToggleMode = false;
    
    // 保存原始位置用于恢复
    private Vec3 originalPosition = null;
    private boolean isPositionOffset = false;
    
    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) {
            return;
        }
        
        handleKeyInput(mc.player);
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        
        try {
            // 更新玩家的战术能力
            mc.player.getCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY).ifPresent(cap -> {
                cap.tick();
                
                // 检查TACZ兼容性
                if (TacticalConfig.TACZ_COMPATIBILITY.get()) {
                    boolean isTaczAiming = checkTaczAiming(mc.player);
                    cap.setTaczAiming(isTaczAiming);
                }
            });
            
            // 处理按键输入
            handleKeyInput(mc.player);
            
            // 应用位置偏移
            applyPeekPositionOffset(mc.player);
        } catch (Exception e) {
            // 捕获异常以避免崩溃
            // 静默处理，避免与其他mod冲突
        }
    }
    
    private void handleKeyInput(LocalPlayer player) {
        boolean leftPeekPressed = KeyBindings.PEEK_LEFT.isDown();
        boolean rightPeekPressed = KeyBindings.PEEK_RIGHT.isDown();
        boolean tacticalStancePressed = KeyBindings.TACTICAL_STANCE.isDown();
        boolean quickPeekPressed = KeyBindings.QUICK_PEEK.isDown();
        boolean peekTogglePressed = KeyBindings.PEEK_TOGGLE.consumeClick();
        
        // 调试输出已移除以减少日志量
        
        // 处理切换模式
        if (peekTogglePressed) {
            isToggleMode = !isToggleMode;
        }
        
        ITacticalCapability.PeekDirection newPeekDirection = ITacticalCapability.PeekDirection.NONE;
        
        if (isToggleMode) {
            // 切换模式：按一次开始探头，再按一次停止
            if (leftPeekPressed && !wasLeftPeekPressed) {
                currentPeekDirection = currentPeekDirection == ITacticalCapability.PeekDirection.LEFT ? 
                    ITacticalCapability.PeekDirection.NONE : ITacticalCapability.PeekDirection.LEFT;
            }
            if (rightPeekPressed && !wasRightPeekPressed) {
                currentPeekDirection = currentPeekDirection == ITacticalCapability.PeekDirection.RIGHT ? 
                    ITacticalCapability.PeekDirection.NONE : ITacticalCapability.PeekDirection.RIGHT;
            }
            newPeekDirection = currentPeekDirection;
        } else {
            // 按住模式：按住时探头，松开时停止
            if (leftPeekPressed && !rightPeekPressed) {
                newPeekDirection = ITacticalCapability.PeekDirection.LEFT;
            } else if (rightPeekPressed && !leftPeekPressed) {
                newPeekDirection = ITacticalCapability.PeekDirection.RIGHT;
            }
        }
        
        // 快速探头（优先级最高）
        if (quickPeekPressed) {
            // 快速探头逻辑：根据鼠标移动方向决定探头方向
            // 这里简化为左探头，实际可以根据鼠标移动实现
            newPeekDirection = ITacticalCapability.PeekDirection.LEFT;
        }
        
        // 更新玩家能力并发送到服务器
        final ITacticalCapability.PeekDirection finalNewPeekDirection = newPeekDirection;
        player.getCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY).ifPresent(cap -> {
            ITacticalCapability.PeekDirection oldDirection = cap.getPeekDirection();
            
            if (oldDirection != finalNewPeekDirection) {
                cap.setPeekDirection(finalNewPeekDirection);
                
                // 发送到服务器
                boolean isTaczAiming = TacticalConfig.TACZ_COMPATIBILITY.get() && cap.isTaczAiming();
                NetworkHandler.sendToServer(new PeekStatePacket(finalNewPeekDirection, isTaczAiming));
            }
            
            // 处理战术姿态
            if (tacticalStancePressed && !wasTacticalStancePressed) {
                ITacticalCapability.TacticalStance currentStance = cap.getTacticalStance();
                ITacticalCapability.TacticalStance newStance = currentStance == ITacticalCapability.TacticalStance.NORMAL ? 
                    ITacticalCapability.TacticalStance.TACTICAL : ITacticalCapability.TacticalStance.NORMAL;
                cap.setTacticalStance(newStance);
            }
        });
        
        // 更新按键状态
        wasLeftPeekPressed = leftPeekPressed;
        wasRightPeekPressed = rightPeekPressed;
        wasTacticalStancePressed = tacticalStancePressed;
        wasQuickPeekPressed = quickPeekPressed;
    }
    
    private boolean checkTaczAiming(Player player) {
        // 使用TaczCompatibility类进行检查
        try {
            return com.warz.tacticalmovement.compat.TaczCompatibility.isPlayerAiming(player);
        } catch (Exception e) {
            // 如果出错，返回false
            return false;
        }
    }
    
    private void applyPeekPositionOffset(LocalPlayer player) {
        // 只在第一人称时应用位置偏移，第三人称使用TacticalPlayerRenderer的身体倾斜动画
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.getCameraType() != CameraType.FIRST_PERSON) {
            // 第三人称模式下，恢复位置偏移（如果有的话）
            if (isPositionOffset && originalPosition != null) {
                player.setPos(originalPosition);
                isPositionOffset = false;
                originalPosition = null;
            }
            return;
        }
        
        player.getCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY).ifPresent(cap -> {
            ITacticalCapability.PeekDirection peekDirection = cap.getPeekDirection();
            float peekProgress = cap.getPeekProgress();
            
            if (peekDirection != ITacticalCapability.PeekDirection.NONE && peekProgress > 0) {
                // 保存原始位置（只在开始peek时保存一次）
                if (!isPositionOffset) {
                    originalPosition = player.position();
                    isPositionOffset = true;
                }
                
                // 计算基础偏移量
                float maxOffset = 0.3f; // 减少最大偏移到0.3格，避免过度偏移
                float offsetAmount = maxOffset * peekProgress;
                
                // 根据探头方向确定偏移方向
                float offsetX = 0;
                if (peekDirection == ITacticalCapability.PeekDirection.LEFT) {
                    offsetX = -offsetAmount; // 左探身向左偏移
                } else if (peekDirection == ITacticalCapability.PeekDirection.RIGHT) {
                    offsetX = offsetAmount; // 右探身向右偏移
                }
                
                // 获取玩家的朝向向量和侧向量
                Vec3 viewVector = player.getViewVector(1.0F);
                Vec3 sideVector = new Vec3(-viewVector.z, 0, viewVector.x).normalize();
                
                // 计算目标偏移向量
                Vec3 offsetVector = sideVector.scale(offsetX);
                
                // 使用raycast检测偏移方向是否有障碍物
                Vec3 startPos = originalPosition.add(0, player.getEyeHeight(), 0); // 从眼部位置开始
                Vec3 endPos = startPos.add(offsetVector.scale(2.0)); // 检测偏移方向的2倍距离
                
                ClipContext clipContext = new ClipContext(
                    startPos, endPos,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    player
                );
                
                HitResult hitResult = player.level().clip(clipContext);
                
                // 如果检测到障碍物，减少偏移量或取消偏移
                double actualOffsetX = offsetVector.x;
                double actualOffsetZ = offsetVector.z;
                
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    // 计算到障碍物的距离
                    double distanceToObstacle = hitResult.getLocation().distanceTo(startPos);
                    double maxSafeDistance = 0.4; // 安全缓冲距离
                    
                    if (distanceToObstacle < maxSafeDistance) {
                        // 如果太接近障碍物，取消偏移
                        actualOffsetX = 0;
                        actualOffsetZ = 0;
                    } else {
                        // 根据距离调整偏移量
                        double safetyFactor = Math.min(1.0, (distanceToObstacle - 0.2) / maxSafeDistance);
                        actualOffsetX *= safetyFactor;
                        actualOffsetZ *= safetyFactor;
                    }
                }
                
                // 设置新位置（基于原始位置）
                Vec3 newPos = new Vec3(
                    originalPosition.x + actualOffsetX,
                    originalPosition.y,
                    originalPosition.z + actualOffsetZ
                );
                
                player.setPos(newPos);
                
                // 调试输出
                if (peekProgress > 0.1f && Math.abs(actualOffsetX) > 0.01) {
                    System.out.println("[WarZ Tactical] First Person Peek - Direction: " + peekDirection + 
                                     ", Progress: " + String.format("%.2f", peekProgress) + 
                                     ", Offset: (" + String.format("%.2f", actualOffsetX) + 
                                     ", " + String.format("%.2f", actualOffsetZ) + ")");
                }
            } else {
                // 恢复原始位置
                if (isPositionOffset && originalPosition != null) {
                    player.setPos(originalPosition);
                    isPositionOffset = false;
                    originalPosition = null;
                    System.out.println("[WarZ Tactical] Position restored to original");
                }
            }
        });
    }
}