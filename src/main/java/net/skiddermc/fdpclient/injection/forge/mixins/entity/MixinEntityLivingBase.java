/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.skiddermc.fdpclient.injection.forge.mixins.entity;

import net.skiddermc.fdpclient.FDPClient;
import net.skiddermc.fdpclient.event.JumpEvent;
import net.skiddermc.fdpclient.features.module.modules.client.Animations;
import net.skiddermc.fdpclient.features.module.modules.combat.KillAura;
import net.skiddermc.fdpclient.features.module.modules.movement.Jesus;
import net.skiddermc.fdpclient.features.module.modules.movement.NoJumpDelay;
import net.skiddermc.fdpclient.features.module.modules.movement.Sprint;
import net.skiddermc.fdpclient.features.module.modules.render.AntiBlind;
import net.skiddermc.fdpclient.utils.MovementUtils;
import net.skiddermc.fdpclient.utils.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {

    @Shadow
    protected boolean isJumping;
    @Shadow
    private int jumpTicks;

    @Shadow
    protected abstract float getJumpUpwardsMotion();

    @Shadow
    public abstract PotionEffect getActivePotionEffect(Potion potionIn);

    @Shadow
    public abstract boolean isPotionActive(Potion potionIn);

    @Shadow
    public void onLivingUpdate() {
    }

    @Shadow
    protected abstract void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos);

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract ItemStack getHeldItem();

    @Shadow
    protected abstract void updateAITick();

    /**
     * @author CCBlueX
     */
    @Overwrite
    protected void jump() {
        if (!this.equals(Minecraft.getMinecraft().thePlayer)) {
            return;
        }

        final JumpEvent jumpEvent = new JumpEvent(MovementUtils.INSTANCE.getJumpMotion());
        FDPClient.eventManager.callEvent(jumpEvent);
        if (jumpEvent.isCancelled())
            return;

        this.motionY = jumpEvent.getMotion();

        if (this.isSprinting()) {
            final Sprint sprint = FDPClient.moduleManager.getModule(Sprint.class);
            float fixedYaw = this.rotationYaw;
            final KillAura killAura = FDPClient.moduleManager.getModule(KillAura.class);
            if(killAura.getStrictStrafe() && RotationUtils.serverRotation != null && killAura.getState()) {
                fixedYaw = RotationUtils.serverRotation.getYaw();
            }
            if(sprint.getState() && sprint.getJumpDirectionsValue().get()) {
                fixedYaw += MovementUtils.INSTANCE.getMovingYaw() - this.rotationYaw;
            }
            this.motionX -= MathHelper.sin(fixedYaw / 180F * 3.1415927F) * 0.2F;
            this.motionZ += MathHelper.cos(fixedYaw / 180F * 3.1415927F) * 0.2F;
        }

        this.isAirBorne = true;
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void headLiving(CallbackInfo callbackInfo) {
        if (FDPClient.moduleManager.getModule(NoJumpDelay.class).getState())
            jumpTicks = 0;
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;isJumping:Z", ordinal = 1))
    private void onJumpSection(CallbackInfo callbackInfo) {
        final Jesus jesus = FDPClient.moduleManager.getModule(Jesus.class);

        if (jesus.getState() && !isJumping && !isSneaking() && isInWater() &&
                jesus.getModeValue().equals("Legit")) {
            this.updateAITick();
        }
    }

    @Inject(method = "getLook", at = @At("HEAD"), cancellable = true)
    private void getLook(CallbackInfoReturnable<Vec3> callbackInfoReturnable) {
        if (((EntityLivingBase) (Object) this) instanceof EntityPlayerSP)
            callbackInfoReturnable.setReturnValue(getVectorForRotation(this.rotationPitch, this.rotationYaw));
    }

    @Inject(method = "isPotionActive(Lnet/minecraft/potion/Potion;)Z", at = @At("HEAD"), cancellable = true)
    private void isPotionActive(Potion p_isPotionActive_1_, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final AntiBlind antiBlind = FDPClient.moduleManager.getModule(AntiBlind.class);

        if ((p_isPotionActive_1_ == Potion.confusion || p_isPotionActive_1_ == Potion.blindness) && antiBlind.getState() && antiBlind.getConfusionEffectValue().get())
            callbackInfoReturnable.setReturnValue(false);
    }

    /**
     * @author Liuli
     */
    @Overwrite
    private int getArmSwingAnimationEnd() {
        int speed = this.isPotionActive(Potion.digSpeed) ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);

        if (this.equals(Minecraft.getMinecraft().thePlayer)) {
            speed = (int) (speed * Animations.INSTANCE.getSwingSpeedValue().get());
        }

        return speed;
    }
}