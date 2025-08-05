package com.warz.tacticalmovement;

import com.mojang.logging.LogUtils;
import com.warz.tacticalmovement.client.ClientEventHandler;
import com.warz.tacticalmovement.client.CameraEventHandler;
import com.warz.tacticalmovement.client.KeyBindings;
import com.warz.tacticalmovement.common.capability.TacticalCapabilityProvider;
import com.warz.tacticalmovement.common.network.NetworkHandler;
import com.warz.tacticalmovement.config.TacticalConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(WarZTacticalMovement.MODID)
public class WarZTacticalMovement {
    public static final String MODID = "warz_tactical_movement";
    private static final Logger LOGGER = LogUtils.getLogger();

    public WarZTacticalMovement() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册配置
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TacticalConfig.SPEC);
        
        // 注册事件监听器
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        
        // 客户端专用事件
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(this::clientSetup);
        });
        
        // 注册到Forge事件总线
        MinecraftForge.EVENT_BUS.register(this);
        
        LOGGER.info("WarZ Tactical Movement mod initialized!");
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 初始化网络处理器
            NetworkHandler.init();
            
            // 初始化TACZ兼容性
            com.warz.tacticalmovement.compat.TaczCompatibility.init();
            
            LOGGER.info("Common setup completed");
        });
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 注册客户端事件处理器
            MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
            MinecraftForge.EVENT_BUS.register(new CameraEventHandler());
            
            LOGGER.info("Client setup completed");
        });
    }
    
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        TacticalCapabilityProvider.registerCapabilities(event);
    }
    
    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            TacticalCapabilityProvider provider = new TacticalCapabilityProvider();
            event.addCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY_ID, provider);
            // 添加invalidate监听器以避免内存泄漏
            event.addListener(provider::invalidate);
        }
    }
}