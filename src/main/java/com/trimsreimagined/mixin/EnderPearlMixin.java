package com.trimsreimagined.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.trimsreimagined.utils.PlayerMixinMethodAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlMixin {
    @ModifyArg(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
    public float decreaseFallDamageIfWearingTrims(float amount, @Local Entity entity) {
        if(entity instanceof PlayerEntity) {
            Integer eyeTrimCount = ((PlayerMixinMethodAccess)entity).trimsreimagined$getTrimCountForTrimType("eye");
            if (eyeTrimCount > 0) {
                return amount - (eyeTrimCount + 1); //if all 4 pieces are worn, remove all fall damage
            }
        }
        return amount;
    }
}
