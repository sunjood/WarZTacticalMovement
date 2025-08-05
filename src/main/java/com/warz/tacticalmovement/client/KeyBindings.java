package com.warz.tacticalmovement.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.warz.tacticalmovement.WarZTacticalMovement;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = WarZTacticalMovement.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {
    
    public static final String CATEGORY = "key.categories." + WarZTacticalMovement.MODID;
    
    // 探头按键 - 直接初始化
    public static final KeyMapping PEEK_LEFT = new KeyMapping(
        "key." + WarZTacticalMovement.MODID + ".peek_left",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_Q,
        CATEGORY
    );
    
    public static final KeyMapping PEEK_RIGHT = new KeyMapping(
        "key." + WarZTacticalMovement.MODID + ".peek_right",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_E,
        CATEGORY
    );
    
    public static final KeyMapping PEEK_TOGGLE = new KeyMapping(
        "key." + WarZTacticalMovement.MODID + ".peek_toggle",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        CATEGORY
    );
    
    // 战术动作按键
    public static final KeyMapping TACTICAL_STANCE = new KeyMapping(
        "key." + WarZTacticalMovement.MODID + ".tactical_stance",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_LEFT_ALT,
        CATEGORY
    );
    
    public static final KeyMapping QUICK_PEEK = new KeyMapping(
        "key." + WarZTacticalMovement.MODID + ".quick_peek",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_T,
        CATEGORY
    );
    
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(PEEK_LEFT);
        event.register(PEEK_RIGHT);
        event.register(PEEK_TOGGLE);
        event.register(TACTICAL_STANCE);
        event.register(QUICK_PEEK);
    }
}