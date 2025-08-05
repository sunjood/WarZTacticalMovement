package com.warz.tacticalmovement.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TacticalConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    // 探头设置
    public static final ForgeConfigSpec.DoubleValue PEEK_ANGLE;
    public static final ForgeConfigSpec.DoubleValue PEEK_SPEED;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PEEK_COLLISION;
    public static final ForgeConfigSpec.DoubleValue PEEK_COLLISION_REDUCTION;
    
    // 第三人称动画设置
    public static final ForgeConfigSpec.BooleanValue ENABLE_THIRD_PERSON_ANIMATION;
    public static final ForgeConfigSpec.DoubleValue HEAD_TILT_ANGLE;
    public static final ForgeConfigSpec.DoubleValue BODY_LEAN_ANGLE;
    public static final ForgeConfigSpec.DoubleValue ANIMATION_SPEED;
    
    // TACZ兼容性设置
    public static final ForgeConfigSpec.BooleanValue TACZ_COMPATIBILITY;
    public static final ForgeConfigSpec.BooleanValue DISABLE_PEEK_WHILE_AIMING;
    
    // 性能设置
    public static final ForgeConfigSpec.IntValue UPDATE_FREQUENCY;
    public static final ForgeConfigSpec.BooleanValue SMOOTH_INTERPOLATION;
    
    static {
        BUILDER.push("Peek Settings");
        
        PEEK_ANGLE = BUILDER
            .comment("Maximum peek angle in degrees")
            .defineInRange("peekAngle", 25.0, 5.0, 45.0);
            
        PEEK_SPEED = BUILDER
            .comment("Speed of peek animation")
            .defineInRange("peekSpeed", 0.15, 0.05, 0.5);
            
        ENABLE_PEEK_COLLISION = BUILDER
            .comment("Enable collision box adjustment during peek")
            .define("enablePeekCollision", true);
            
        PEEK_COLLISION_REDUCTION = BUILDER
            .comment("Collision box width reduction factor during peek")
            .defineInRange("peekCollisionReduction", 0.3, 0.1, 0.8);
            
        BUILDER.pop();
        
        BUILDER.push("Third Person Animation");
        
        ENABLE_THIRD_PERSON_ANIMATION = BUILDER
            .comment("Enable third person peek animations")
            .define("enableThirdPersonAnimation", true);
            
        HEAD_TILT_ANGLE = BUILDER
            .comment("Head tilt angle during peek in degrees")
            .defineInRange("headTiltAngle", 15.0, 5.0, 30.0);
            
        BODY_LEAN_ANGLE = BUILDER
            .comment("Body lean angle during peek in degrees")
            .defineInRange("bodyLeanAngle", 10.0, 0.0, 20.0);
            
        ANIMATION_SPEED = BUILDER
            .comment("Animation transition speed")
            .defineInRange("animationSpeed", 0.2, 0.1, 0.5);
            
        BUILDER.pop();
        
        BUILDER.push("TACZ Compatibility");
        
        TACZ_COMPATIBILITY = BUILDER
            .comment("Enable TACZ mod compatibility")
            .define("taczCompatibility", true);
            
        DISABLE_PEEK_WHILE_AIMING = BUILDER
            .comment("Disable peek while aiming with TACZ weapons")
            .define("disablePeekWhileAiming", false);
            
        BUILDER.pop();
        
        BUILDER.push("Performance");
        
        UPDATE_FREQUENCY = BUILDER
            .comment("Update frequency in ticks (lower = more frequent updates)")
            .defineInRange("updateFrequency", 1, 1, 5);
            
        SMOOTH_INTERPOLATION = BUILDER
            .comment("Enable smooth interpolation for animations")
            .define("smoothInterpolation", true);
            
        BUILDER.pop();
        
        SPEC = BUILDER.build();
    }
}