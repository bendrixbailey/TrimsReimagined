package com.trimsreimagined.mixin;

import com.trimsreimagined.utils.PlayerMixinMethodAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity{

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow protected abstract boolean shouldDropLoot();

    @Shadow protected abstract void dropLoot(DamageSource damageSource, boolean causedByPlayer);

    @Inject( method = "drop", at = @At("TAIL"))
    public void recalculateLootDropsForTrims(ServerWorld world, DamageSource damageSource, CallbackInfo ci) {
        Entity attacker = damageSource.getAttacker();

        if(this.shouldDropLoot() && world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            if(attacker instanceof PlayerEntity) {
                Integer vexTrimCount = ((PlayerMixinMethodAccess) damageSource.getAttacker()).trimsreimagined$getTrimCountForTrimType("vex");
                if(vexTrimCount.equals(getRandomNumberInRange(1, 6))) {
                    this.dropLoot(damageSource, true);
                }
            }
        }
    }

    @Inject( method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    public void canTargetPlayer(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if(target instanceof PlayerEntity) {
            //For Wild Trim
            if ((Object) this instanceof CreeperEntity) {
                Integer wildTrimCount = ((PlayerMixinMethodAccess) target).trimsreimagined$getTrimCountForTrimType("wild");
                if (wildTrimCount.equals(4)) {
                    cir.setReturnValue(false);
                }
            }
            //For Dune Trim
            if ((Object) this instanceof ZombieEntity ) {
                Integer duneTrimCount = ((PlayerMixinMethodAccess) target).trimsreimagined$getTrimCountForTrimType("dune");
                if (duneTrimCount.equals(4)) {
                    cir.setReturnValue(false);
                }
            }
            if ((Object) this instanceof SkeletonEntity ) {
                Integer duneTrimCount = ((PlayerMixinMethodAccess) target).trimsreimagined$getTrimCountForTrimType("dune");
                if (duneTrimCount.equals(4)) {
                    cir.setReturnValue(false);
                }
            }
            //For Ward Trim
            if ((Object) this instanceof WardenEntity ) {
                Integer wardTrimCount = ((PlayerMixinMethodAccess) target).trimsreimagined$getTrimCountForTrimType("ward");
                if(target.isInSneakingPose() && wardTrimCount.equals(4)) {
                    cir.setReturnValue(false);
                }
            }
            //For Silence Trim
            if ((Object) this instanceof WardenEntity ) {
                Integer silenceTrimCount = ((PlayerMixinMethodAccess) target).trimsreimagined$getTrimCountForTrimType("silence");
                if(silenceTrimCount.equals(4)) {
                    cir.setReturnValue(false);
                }
            }
            //For Tide Trim
            if ((Object) this instanceof GuardianEntity) {
                Integer tideTrimCount = ((PlayerMixinMethodAccess) target).trimsreimagined$getTrimCountForTrimType("tide");
                if(tideTrimCount.equals(4)) {
                    cir.setReturnValue(false);
                }
            }
            if ((Object) this instanceof ElderGuardianEntity) {
                Integer tideTrimCount = ((PlayerMixinMethodAccess) target).trimsreimagined$getTrimCountForTrimType("tide");
                if(tideTrimCount.equals(4)) {
                    cir.setReturnValue(false);
                }
            }
            //For Snout Trim
            if ((Object) this instanceof PiglinEntity) {
                Integer snoutTrimCount = ((PlayerMixinMethodAccess) target).trimsreimagined$getTrimCountForTrimType("snout");
                if(snoutTrimCount > 0) {
                    cir.setReturnValue(false);
                }
            }
            if ((Object) this instanceof PiglinBruteEntity) {
                Integer snoutTrimCount = ((PlayerMixinMethodAccess) target).trimsreimagined$getTrimCountForTrimType("snout");
                if(snoutTrimCount.equals(4)) {
                    cir.setReturnValue(false);
                }
            }
            //For Rib trim
            if ((Object) this instanceof BlazeEntity) {
                Integer ribTrimCount = ((PlayerMixinMethodAccess) target).trimsreimagined$getTrimCountForTrimType("rib");
                if(ribTrimCount.equals(4)) {
                    cir.setReturnValue(false);
                }
            }
            if ((Object) this instanceof WitherSkeletonEntity) {
                Integer ribTrimCount = ((PlayerMixinMethodAccess) target).trimsreimagined$getTrimCountForTrimType("rib");
                if(ribTrimCount.equals(4)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @ModifyVariable(method = "takeKnockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public double decreaseKnockbackForTrims(double strength) {
        double adjustedKnockback = strength;
        if((Object) this instanceof PlayerEntity player){
            Integer flowTrimCount = ((PlayerMixinMethodAccess)player).trimsreimagined$getTrimCountForTrimType("flow");
            adjustedKnockback = strength * (1 - (flowTrimCount * 0.25f));
        }
        return adjustedKnockback;
    }

    @Inject(method = "applyDamage", at = @At("TAIL"))
    private void summonLightningIfTrimBonus(DamageSource source, float amount, CallbackInfo ci) {
        if(source.getAttacker() != null) {
            if(source.getAttacker() instanceof PlayerEntity player) {
                if(((PlayerMixinMethodAccess)player).trimsreimagined$getTrimCountForTrimType("bolt").equals(4) && getRandomNumberInRange(0, 4).equals(3)) {
                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, this.getWorld());
                    lightning.setPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
                    this.getWorld().spawnEntity(lightning);
                }
            }
        }
    }



    @Unique
    public Integer getRandomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}
