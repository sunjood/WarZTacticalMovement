package com.warz.tacticalmovement.common.capability;

import com.warz.tacticalmovement.config.TacticalConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class TacticalCapability implements ITacticalCapability {
    
    private PeekDirection peekDirection = PeekDirection.NONE;
    private float peekProgress = 0.0f;
    private float targetPeekProgress = 0.0f;
    private TacticalStance tacticalStance = TacticalStance.NORMAL;
    
    private float headTiltAngle = 0.0f;
    private float bodyLeanAngle = 0.0f;
    
    private boolean collisionAdjusted = false;
    private float collisionReduction = 0.0f;
    
    private long lastUpdateTime = 0;
    private boolean taczAiming = false;
    
    // 动画插值相关
    private float prevPeekProgress = 0.0f;
    private float prevHeadTiltAngle = 0.0f;
    private float prevBodyLeanAngle = 0.0f;
    
    @Override
    public PeekDirection getPeekDirection() {
        return peekDirection;
    }
    
    @Override
    public synchronized void setPeekDirection(PeekDirection direction) {
        this.peekDirection = direction;
        updateTargetProgress();
    }
    
    @Override
    public float getPeekProgress() {
        return peekProgress;
    }
    
    @Override
    public synchronized void setPeekProgress(float progress) {
        this.prevPeekProgress = this.peekProgress;
        this.peekProgress = Mth.clamp(progress, 0.0f, 1.0f);
    }
    
    @Override
    public float getTargetPeekProgress() {
        return targetPeekProgress;
    }
    
    @Override
    public void setTargetPeekProgress(float progress) {
        this.targetPeekProgress = Mth.clamp(progress, 0.0f, 1.0f);
    }
    
    @Override
    public boolean isPeeking() {
        return peekDirection != PeekDirection.NONE && peekProgress > 0.01f;
    }
    
    @Override
    public TacticalStance getTacticalStance() {
        return tacticalStance;
    }
    
    @Override
    public synchronized void setTacticalStance(TacticalStance stance) {
        this.tacticalStance = stance;
    }
    
    @Override
    public float getHeadTiltAngle() {
        return headTiltAngle;
    }
    
    @Override
    public void setHeadTiltAngle(float angle) {
        this.prevHeadTiltAngle = this.headTiltAngle;
        this.headTiltAngle = angle;
    }
    
    @Override
    public float getBodyLeanAngle() {
        return bodyLeanAngle;
    }
    
    @Override
    public void setBodyLeanAngle(float angle) {
        this.prevBodyLeanAngle = this.bodyLeanAngle;
        this.bodyLeanAngle = angle;
    }
    
    @Override
    public boolean isCollisionAdjusted() {
        return collisionAdjusted;
    }
    
    @Override
    public void setCollisionAdjusted(boolean adjusted) {
        this.collisionAdjusted = adjusted;
    }
    
    @Override
    public float getCollisionReduction() {
        return collisionReduction;
    }
    
    @Override
    public void setCollisionReduction(float reduction) {
        this.collisionReduction = reduction;
    }
    
    @Override
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    @Override
    public void setLastUpdateTime(long time) {
        this.lastUpdateTime = time;
    }
    
    @Override
    public boolean isTaczAiming() {
        return taczAiming;
    }
    
    @Override
    public synchronized void setTaczAiming(boolean aiming) {
        this.taczAiming = aiming;
    }
    
    @Override
    public synchronized void tick() {
        // 更新探头进度
        float peekSpeed = TacticalConfig.PEEK_SPEED.get().floatValue();
        float diff = targetPeekProgress - peekProgress;
        
        if (Math.abs(diff) > 0.001f) {
            setPeekProgress(peekProgress + diff * peekSpeed);
        }
        
        // 更新角度
        updateAngles();
        
        // 更新碰撞箱
        updateCollision();
        
        lastUpdateTime = System.currentTimeMillis();
    }
    
    @Override
    public void updateAnimations(float partialTicks) {
        if (!TacticalConfig.SMOOTH_INTERPOLATION.get()) {
            return;
        }
        
        // 平滑插值动画
        float interpolatedProgress = Mth.lerp(partialTicks, prevPeekProgress, peekProgress);
        float interpolatedHeadTilt = Mth.lerp(partialTicks, prevHeadTiltAngle, headTiltAngle);
        float interpolatedBodyLean = Mth.lerp(partialTicks, prevBodyLeanAngle, bodyLeanAngle);
        
        // 这些插值后的值可以用于渲染
    }
    
    @Override
    public synchronized void reset() {
        peekDirection = PeekDirection.NONE;
        peekProgress = 0.0f;
        targetPeekProgress = 0.0f;
        tacticalStance = TacticalStance.NORMAL;
        headTiltAngle = 0.0f;
        bodyLeanAngle = 0.0f;
        collisionAdjusted = false;
        collisionReduction = 0.0f;
        taczAiming = false;
    }
    
    private void updateTargetProgress() {
        if (peekDirection == PeekDirection.NONE) {
            targetPeekProgress = 0.0f;
        } else {
            // 检查TACZ兼容性
            if (TacticalConfig.TACZ_COMPATIBILITY.get() && 
                TacticalConfig.DISABLE_PEEK_WHILE_AIMING.get() && 
                taczAiming) {
                targetPeekProgress = 0.0f;
            } else {
                targetPeekProgress = 1.0f;
            }
        }
    }
    
    private void updateAngles() {
        if (!TacticalConfig.ENABLE_THIRD_PERSON_ANIMATION.get()) {
            return;
        }
        
        float maxHeadTilt = TacticalConfig.HEAD_TILT_ANGLE.get().floatValue();
        float maxBodyLean = TacticalConfig.BODY_LEAN_ANGLE.get().floatValue();
        
        float direction = peekDirection == PeekDirection.LEFT ? -1.0f : 
                         peekDirection == PeekDirection.RIGHT ? 1.0f : 0.0f;
        
        setHeadTiltAngle(direction * maxHeadTilt * peekProgress);
        setBodyLeanAngle(direction * maxBodyLean * peekProgress);
    }
    
    private void updateCollision() {
        if (!TacticalConfig.ENABLE_PEEK_COLLISION.get()) {
            return;
        }
        
        boolean shouldAdjust = isPeeking();
        setCollisionAdjusted(shouldAdjust);
        
        if (shouldAdjust) {
            float reduction = TacticalConfig.PEEK_COLLISION_REDUCTION.get().floatValue();
            setCollisionReduction(reduction * peekProgress);
        } else {
            setCollisionReduction(0.0f);
        }
    }
    
    @Override
    public synchronized CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("peekDirection", peekDirection.name());
        tag.putFloat("peekProgress", peekProgress);
        tag.putFloat("targetPeekProgress", targetPeekProgress);
        tag.putString("tacticalStance", tacticalStance.name());
        tag.putFloat("headTiltAngle", headTiltAngle);
        tag.putFloat("bodyLeanAngle", bodyLeanAngle);
        tag.putBoolean("collisionAdjusted", collisionAdjusted);
        tag.putFloat("collisionReduction", collisionReduction);
        tag.putLong("lastUpdateTime", lastUpdateTime);
        tag.putBoolean("taczAiming", taczAiming);
        return tag;
    }
    
    @Override
    public synchronized void deserializeNBT(CompoundTag tag) {
        try {
            peekDirection = PeekDirection.valueOf(tag.getString("peekDirection"));
            peekProgress = tag.getFloat("peekProgress");
            targetPeekProgress = tag.getFloat("targetPeekProgress");
            tacticalStance = TacticalStance.valueOf(tag.getString("tacticalStance"));
            headTiltAngle = tag.getFloat("headTiltAngle");
            bodyLeanAngle = tag.getFloat("bodyLeanAngle");
            collisionAdjusted = tag.getBoolean("collisionAdjusted");
            collisionReduction = tag.getFloat("collisionReduction");
            lastUpdateTime = tag.getLong("lastUpdateTime");
            taczAiming = tag.getBoolean("taczAiming");
        } catch (Exception e) {
            // 如果反序列化失败，重置为默认值
            reset();
        }
    }
}