package com.trimsreimagined.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.trimsreimagined.TrimsReimagined;
import com.trimsreimagined.utils.PlayerMixinMethodAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ShulkerBulletEntity.class)
public abstract class ShulkerBulletMixin extends ProjectileEntity {
    public ShulkerBulletMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * If player is wearing spire trim armor, reduce duration of levitation effect applied
     * @param effect levitation effect instance
     * @param entity entity thats being attacked
     * @return new instance of levitation with reduced duration
     */
    @ModifyArg(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z"), index = 0)
    private StatusEffectInstance interceptLevitationApplicationToPlayer(StatusEffectInstance effect, @Local(argsOnly = true) EntityHitResult entity) {
        LivingEntity hitEntity = (LivingEntity) entity.getEntity();
        int duration = 200;
        if(hitEntity instanceof PlayerEntity playerEntity) {
            Integer spireCount = ((PlayerMixinMethodAccess)playerEntity).trimsreimagined$getTrimCountForTrimType("spire");
            TrimsReimagined.LOGGER.info("Spire count: " + spireCount);
            if(spireCount > 0) {
                 duration = 200 - (spireCount * 40);
            }
            if(spireCount.equals(4)){
                duration = 0;
            }
        }
        return new StatusEffectInstance(StatusEffects.LEVITATION, duration);
    }
}
