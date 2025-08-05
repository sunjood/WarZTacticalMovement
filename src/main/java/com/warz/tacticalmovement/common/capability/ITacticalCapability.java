package com.warz.tacticalmovement.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface ITacticalCapability extends INBTSerializable<CompoundTag> {
    
    enum PeekDirection {
        NONE,
        LEFT,
        RIGHT
    }
    
    enum TacticalStance {
        NORMAL,
        TACTICAL,
        PRONE
    }
    
    // 探头相关方法
    PeekDirection getPeekDirection();
    void setPeekDirection(PeekDirection direction);
    
    float getPeekProgress();
    void setPeekProgress(float progress);
    
    float getTargetPeekProgress();
    void setTargetPeekProgress(float progress);
    
    boolean isPeeking();
    
    // 战术姿态相关方法
    TacticalStance getTacticalStance();
    void setTacticalStance(TacticalStance stance);
    
    // 动画相关方法
    float getHeadTiltAngle();
    void setHeadTiltAngle(float angle);
    
    float getBodyLeanAngle();
    void setBodyLeanAngle(float angle);
    
    // 碰撞箱相关方法
    boolean isCollisionAdjusted();
    void setCollisionAdjusted(boolean adjusted);
    
    float getCollisionReduction();
    void setCollisionReduction(float reduction);
    
    // 时间相关方法
    long getLastUpdateTime();
    void setLastUpdateTime(long time);
    
    // TACZ兼容性
    boolean isTaczAiming();
    void setTaczAiming(boolean aiming);
    
    // 更新方法
    void tick();
    void updateAnimations(float partialTicks);
    
    // 重置方法
    void reset();
}