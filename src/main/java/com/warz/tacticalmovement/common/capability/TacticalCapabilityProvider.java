package com.warz.tacticalmovement.common.capability;

import com.warz.tacticalmovement.WarZTacticalMovement;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TacticalCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    
    public static final ResourceLocation TACTICAL_CAPABILITY_ID = 
        new ResourceLocation(WarZTacticalMovement.MODID, "tactical_capability");
    
    public static final Capability<ITacticalCapability> TACTICAL_CAPABILITY = 
        CapabilityManager.get(new CapabilityToken<ITacticalCapability>(){});
    
    private ITacticalCapability tacticalCapability = null;
    private final LazyOptional<ITacticalCapability> optional = LazyOptional.of(this::createTacticalCapability);
    
    private ITacticalCapability createTacticalCapability() {
        if (this.tacticalCapability == null) {
            this.tacticalCapability = new TacticalCapability();
        }
        return this.tacticalCapability;
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == TACTICAL_CAPABILITY) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ITacticalCapability capability = createTacticalCapability();
        if (capability != null) {
            tag = capability.serializeNBT();
        }
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ITacticalCapability capability = createTacticalCapability();
        if (capability != null) {
            capability.deserializeNBT(nbt);
        }
    }
    
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ITacticalCapability.class);
    }
    
    public void invalidate() {
        optional.invalidate();
    }
}