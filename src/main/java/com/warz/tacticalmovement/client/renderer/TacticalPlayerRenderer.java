package com.warz.tacticalmovement.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.warz.tacticalmovement.common.capability.ITacticalCapability;
import com.warz.tacticalmovement.common.capability.TacticalCapabilityProvider;
import com.warz.tacticalmovement.config.TacticalConfig;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TacticalPlayerRenderer extends PlayerRenderer {
    
    public TacticalPlayerRenderer(EntityRendererProvider.Context context, boolean useSlimModel) {
        super(context, useSlimModel);
    }
    
    @Override
    public void render(AbstractClientPlayer player, float entityYaw, float partialTicks, 
                      PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        
        // 获取战术能力
        player.getCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY).ifPresent(cap -> {
            if (TacticalConfig.ENABLE_THIRD_PERSON_ANIMATION.get() && cap.isPeeking()) {
                applyTacticalTransforms(poseStack, cap, partialTicks);
            }
        });
        
        super.render(player, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
    
    @Override
    protected void setupRotations(AbstractClientPlayer player, PoseStack poseStack, 
                                 float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(player, poseStack, ageInTicks, rotationYaw, partialTicks);
        
        // 应用探头旋转
        player.getCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY).ifPresent(cap -> {
            if (TacticalConfig.ENABLE_THIRD_PERSON_ANIMATION.get() && cap.isPeeking()) {
                applyPeekRotations(poseStack, cap, partialTicks);
            }
        });
    }
    
    private void applyTacticalTransforms(PoseStack poseStack, ITacticalCapability cap, float partialTicks) {
        float peekProgress = cap.getPeekProgress();
        float bodyLeanAngle = cap.getBodyLeanAngle();
        
        if (peekProgress > 0.01f) {
            // 应用身体倾斜
            poseStack.mulPose(Axis.ZP.rotationDegrees(bodyLeanAngle));
            
            // 根据探头方向调整位置
            ITacticalCapability.PeekDirection direction = cap.getPeekDirection();
            if (direction != ITacticalCapability.PeekDirection.NONE) {
                float offsetX = direction == ITacticalCapability.PeekDirection.LEFT ? -0.2f : 0.2f;
                poseStack.translate(offsetX * peekProgress, 0, 0);
            }
        }
    }
    
    private void applyPeekRotations(PoseStack poseStack, ITacticalCapability cap, float partialTicks) {
        float headTiltAngle = cap.getHeadTiltAngle();
        float peekProgress = cap.getPeekProgress();
        
        if (peekProgress > 0.01f && Math.abs(headTiltAngle) > 0.01f) {
            // 应用头部倾斜
            poseStack.translate(0, 1.5, 0); // 移动到头部位置
            poseStack.mulPose(Axis.ZP.rotationDegrees(headTiltAngle));
            poseStack.translate(0, -1.5, 0); // 移回原位置
        }
    }
    
    @Override
    protected void scale(AbstractClientPlayer player, PoseStack poseStack, float partialTicks) {
        super.scale(player, poseStack, partialTicks);
        
        // 根据战术姿态调整缩放
        player.getCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY).ifPresent(cap -> {
            ITacticalCapability.TacticalStance stance = cap.getTacticalStance();
            if (stance == ITacticalCapability.TacticalStance.TACTICAL) {
                // 战术姿态时稍微降低高度
                poseStack.scale(1.0f, 0.95f, 1.0f);
            } else if (stance == ITacticalCapability.TacticalStance.PRONE) {
                // 趴下姿态
                poseStack.scale(1.0f, 0.3f, 1.0f);
            }
        });
    }
    
    // 自定义模型动画
    public static void animatePlayerModel(PlayerModel<?> model, AbstractClientPlayer player, float partialTicks) {
        player.getCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY).ifPresent(cap -> {
            if (!TacticalConfig.ENABLE_THIRD_PERSON_ANIMATION.get()) {
                return;
            }
            
            float peekProgress = cap.getPeekProgress();
            float headTiltAngle = cap.getHeadTiltAngle();
            float bodyLeanAngle = cap.getBodyLeanAngle();
            
            if (cap.isPeeking()) {
                // 头部动画
                model.head.zRot += Math.toRadians(headTiltAngle);
                model.hat.zRot += Math.toRadians(headTiltAngle);
                
                // 身体动画
                model.body.zRot += Math.toRadians(bodyLeanAngle * 0.5f);
                
                // 手臂动画
                ITacticalCapability.PeekDirection direction = cap.getPeekDirection();
                if (direction == ITacticalCapability.PeekDirection.LEFT) {
                    model.leftArm.zRot += Math.toRadians(10 * peekProgress);
                    model.rightArm.zRot -= Math.toRadians(5 * peekProgress);
                } else if (direction == ITacticalCapability.PeekDirection.RIGHT) {
                    model.rightArm.zRot -= Math.toRadians(10 * peekProgress);
                    model.leftArm.zRot += Math.toRadians(5 * peekProgress);
                }
                
                // 腿部动画（轻微调整以保持平衡）
                model.leftLeg.zRot += Math.toRadians(bodyLeanAngle * 0.2f);
                model.rightLeg.zRot += Math.toRadians(bodyLeanAngle * 0.2f);
            }
            
            // 战术姿态动画
            ITacticalCapability.TacticalStance stance = cap.getTacticalStance();
            if (stance == ITacticalCapability.TacticalStance.TACTICAL) {
                // 战术姿态：稍微弯腰
                model.body.xRot += Math.toRadians(5);
                model.head.xRot -= Math.toRadians(2);
            }
        });
    }
}