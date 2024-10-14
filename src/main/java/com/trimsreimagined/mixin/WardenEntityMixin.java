package com.trimsreimagined.mixin;

import com.trimsreimagined.utils.PlayerMixinMethodAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WardenEntity.class)
public abstract class WardenEntityMixin extends Entity {

    public WardenEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private static final int wardenDetectDistance = 16;

    /**
     * Method reduces warden attack distance based on number of silence trims worn, down to 8 blocks instead of 16
     * @param entity attacking entity of warden
     * @param cir callback to return early
     */
    @Inject(method = "isValidTarget", at = @At("HEAD"), cancellable = true)
    public void checkPlayerDistance(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof PlayerEntity) {
            Integer silenceTrimCount = ((PlayerMixinMethodAccess) entity).trimsreimagined$getTrimCountForTrimType("silence");
            if(silenceTrimCount > 0) {
                BlockPos playerBlockPos = ((PlayerEntity) entity).getBlockPos();
                BlockPos wardenBlockPos = this.getBlockPos();
                double playerDistance = Math.abs(Math.sqrt(
                        Math.pow((playerBlockPos.getX() - wardenBlockPos.getX()), 2)
                                + Math.pow((playerBlockPos.getY() - wardenBlockPos.getY()), 2)
                                + Math.pow((playerBlockPos.getZ() - wardenBlockPos.getZ()), 2)
                ));
                if(playerDistance > (wardenDetectDistance - (silenceTrimCount * 2))) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
