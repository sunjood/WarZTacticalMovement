package com.warz.tacticalmovement.common.util;

import com.warz.tacticalmovement.common.capability.ITacticalCapability;
import com.warz.tacticalmovement.common.capability.TacticalCapabilityProvider;
import com.warz.tacticalmovement.config.TacticalConfig;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CollisionManager {
    
    // 默认玩家尺寸
    private static final float DEFAULT_WIDTH = 0.6f;
    private static final float DEFAULT_HEIGHT = 1.8f;
    
    // 注意：EntityEvent.Size在Forge 1.20.1中已过时
    // 碰撞箱调整现在通过getAdjustedBoundingBox方法实现
    // 如果需要动态尺寸调整，可以考虑使用其他事件或直接修改实体属性
    
    /**
     * 获取调整后的碰撞箱
     */
    public static AABB getAdjustedBoundingBox(Player player, AABB originalBox) {
        if (!TacticalConfig.ENABLE_PEEK_COLLISION.get()) {
            return originalBox;
        }
        
        return player.getCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY)
            .map(cap -> {
                if (!cap.isCollisionAdjusted() || !cap.isPeeking()) {
                    return originalBox;
                }
                
                float reduction = cap.getCollisionReduction();
                ITacticalCapability.PeekDirection direction = cap.getPeekDirection();
                
                double width = originalBox.getXsize();
                double height = originalBox.getYsize();
                double depth = originalBox.getZsize();
                
                double centerX = originalBox.getCenter().x;
                double centerY = originalBox.getCenter().y;
                double centerZ = originalBox.getCenter().z;
                
                // 根据探头方向调整碰撞箱
                if (direction == ITacticalCapability.PeekDirection.LEFT) {
                    // 左探头：减少右侧宽度，向左偏移
                    double newWidth = width * (1.0f - reduction);
                    double offsetX = (width - newWidth) * 0.5;
                    return new AABB(
                        centerX - newWidth * 0.5 - offsetX,
                        centerY - height * 0.5,
                        centerZ - depth * 0.5,
                        centerX + newWidth * 0.5 - offsetX,
                        centerY + height * 0.5,
                        centerZ + depth * 0.5
                    );
                } else if (direction == ITacticalCapability.PeekDirection.RIGHT) {
                    // 右探头：减少左侧宽度，向右偏移
                    double newWidth = width * (1.0f - reduction);
                    double offsetX = (width - newWidth) * 0.5;
                    return new AABB(
                        centerX - newWidth * 0.5 + offsetX,
                        centerY - height * 0.5,
                        centerZ - depth * 0.5,
                        centerX + newWidth * 0.5 + offsetX,
                        centerY + height * 0.5,
                        centerZ + depth * 0.5
                    );
                }
                
                return originalBox;
            })
            .orElse(originalBox);
    }
    
    /**
     * 检查探头时的碰撞
     */
    public static boolean canPeekInDirection(Player player, ITacticalCapability.PeekDirection direction) {
        if (!TacticalConfig.ENABLE_PEEK_COLLISION.get()) {
            return true;
        }
        
        AABB currentBox = player.getBoundingBox();
        
        // 模拟探头后的碰撞箱
        double peekOffset = 0.3; // 探头偏移距离
        AABB peekBox;
        
        if (direction == ITacticalCapability.PeekDirection.LEFT) {
            peekBox = currentBox.move(-peekOffset, 0, 0);
        } else if (direction == ITacticalCapability.PeekDirection.RIGHT) {
            peekBox = currentBox.move(peekOffset, 0, 0);
        } else {
            return true;
        }
        
        // 检查新位置是否有碰撞
        return player.level().noCollision(player, peekBox);
    }
    
    /**
     * 获取安全的探头进度
     */
    public static float getSafePeekProgress(Player player, ITacticalCapability.PeekDirection direction, float targetProgress) {
        if (!TacticalConfig.ENABLE_PEEK_COLLISION.get()) {
            return targetProgress;
        }
        
        // 逐步检查探头进度，找到最大安全值
        float safeProgress = 0.0f;
        float step = 0.1f;
        
        for (float progress = step; progress <= targetProgress; progress += step) {
            if (canPeekAtProgress(player, direction, progress)) {
                safeProgress = progress;
            } else {
                break;
            }
        }
        
        return safeProgress;
    }
    
    private static boolean canPeekAtProgress(Player player, ITacticalCapability.PeekDirection direction, float progress) {
        AABB currentBox = player.getBoundingBox();
        double maxOffset = 0.3; // 最大探头偏移
        double offset = maxOffset * progress;
        
        AABB testBox;
        if (direction == ITacticalCapability.PeekDirection.LEFT) {
            testBox = currentBox.move(-offset, 0, 0);
        } else if (direction == ITacticalCapability.PeekDirection.RIGHT) {
            testBox = currentBox.move(offset, 0, 0);
        } else {
            return true;
        }
        
        return player.level().noCollision(player, testBox);
    }
}