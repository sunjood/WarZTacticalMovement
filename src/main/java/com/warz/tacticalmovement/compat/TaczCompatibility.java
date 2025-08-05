package com.warz.tacticalmovement.compat;

import com.warz.tacticalmovement.config.TacticalConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

/**
 * TACZ mod兼容性处理器
 * 提供与TACZ武器mod的集成功能
 */
public class TaczCompatibility {
    
    private static final String TACZ_MOD_ID = "tacz";
    private static boolean taczLoaded = false;
    private static boolean initialized = false;
    
    /**
     * 初始化TACZ兼容性
     */
    public static void init() {
        if (initialized) {
            return;
        }
        
        taczLoaded = ModList.get().isLoaded(TACZ_MOD_ID);
        initialized = true;
        
        if (taczLoaded && TacticalConfig.TACZ_COMPATIBILITY.get()) {
            initTaczIntegration();
        }
    }
    
    /**
     * 检查TACZ是否已加载
     */
    public static boolean isTaczLoaded() {
        return taczLoaded;
    }
    
    /**
     * 检查玩家是否在使用TACZ武器瞄准
     */
    public static boolean isPlayerAiming(Player player) {
        if (!taczLoaded || !TacticalConfig.TACZ_COMPATIBILITY.get()) {
            return false;
        }
        
        try {
            return checkTaczAiming(player);
        } catch (Exception e) {
            // 如果出现异常，返回false以避免崩溃
            return false;
        }
    }
    
    /**
     * 检查物品是否为TACZ武器
     */
    public static boolean isTaczWeapon(ItemStack itemStack) {
        if (!taczLoaded || itemStack.isEmpty()) {
            return false;
        }
        
        try {
            // 检查物品是否为TACZ武器
            // 这里需要根据TACZ的实际API进行实现
            String itemId = itemStack.getItem().toString();
            return itemId.contains("tacz:") || itemId.contains("gun");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取TACZ武器的瞄准状态
     */
    public static boolean getTaczAimingState(Player player) {
        if (!taczLoaded || !TacticalConfig.TACZ_COMPATIBILITY.get()) {
            return false;
        }
        
        try {
            return checkTaczAiming(player);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检查是否应该禁用探头（当使用TACZ武器瞄准时）
     */
    public static boolean shouldDisablePeek(Player player) {
        if (!taczLoaded || !TacticalConfig.TACZ_COMPATIBILITY.get()) {
            return false;
        }
        
        if (!TacticalConfig.DISABLE_PEEK_WHILE_AIMING.get()) {
            return false;
        }
        
        return isPlayerAiming(player);
    }
    
    /**
     * 获取TACZ武器的后坐力补偿
     */
    public static float getTaczRecoilCompensation(Player player) {
        if (!taczLoaded || !TacticalConfig.TACZ_COMPATIBILITY.get()) {
            return 0.0f;
        }
        
        try {
            // 根据TACZ武器类型返回不同的后坐力补偿值
            ItemStack heldItem = player.getMainHandItem();
            if (isTaczWeapon(heldItem)) {
                // 这里可以根据具体武器类型返回不同的补偿值
                return 0.1f; // 基础补偿值
            }
        } catch (Exception e) {
            // 忽略异常
        }
        
        return 0.0f;
    }
    
    /**
     * 初始化TACZ集成
     */
    private static void initTaczIntegration() {
        try {
            // 这里可以添加TACZ特定的初始化代码
            // 例如注册事件监听器、兼容性检查等
        } catch (Exception e) {
            // 如果初始化失败，禁用TACZ兼容性
            taczLoaded = false;
        }
    }
    
    /**
     * 实际检查TACZ瞄准状态的方法
     */
    private static boolean checkTaczAiming(Player player) {
        try {
            // 这里需要根据TACZ的实际API实现
            // 由于TACZ是外部mod，这里提供一个基础的检查方法
            
            // 检查玩家手中的物品是否为TACZ武器
            ItemStack heldItem = player.getMainHandItem();
            if (!isTaczWeapon(heldItem)) {
                return false;
            }
            
            // 暂时返回false，避免干扰正常的按键功能
            // 实际应该调用TACZ的API来检查瞄准状态
            // 例如：return TaczAPI.isPlayerAiming(player);
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取TACZ武器的探头兼容性设置
     */
    public static PeekCompatibilityMode getPeekCompatibilityMode(Player player) {
        if (!taczLoaded || !TacticalConfig.TACZ_COMPATIBILITY.get()) {
            return PeekCompatibilityMode.NORMAL;
        }
        
        if (isPlayerAiming(player)) {
            if (TacticalConfig.DISABLE_PEEK_WHILE_AIMING.get()) {
                return PeekCompatibilityMode.DISABLED;
            } else {
                return PeekCompatibilityMode.REDUCED;
            }
        }
        
        return PeekCompatibilityMode.NORMAL;
    }
    
    /**
     * 探头兼容性模式枚举
     */
    public enum PeekCompatibilityMode {
        NORMAL,    // 正常探头
        REDUCED,   // 减少探头角度
        DISABLED   // 禁用探头
    }
}