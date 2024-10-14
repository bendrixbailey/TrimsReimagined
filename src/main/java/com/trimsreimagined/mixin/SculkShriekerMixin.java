package com.trimsreimagined.mixin;

import com.trimsreimagined.utils.PlayerMixinMethodAccess;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SculkShriekerBlockEntity.class)
public class SculkShriekerMixin {

    @Inject(method = "shriek(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    public void interruptShrikeBasedOnTrim(ServerWorld world, Entity entity, CallbackInfo ci) {
        if(entity instanceof PlayerEntity) {
            Integer wardTrimCount = ((PlayerMixinMethodAccess) entity).trimsreimagined$getTrimCountForTrimType("ward");
            if(getRandomNumberInRange(1, 6) < wardTrimCount) {
                ci.cancel();
            }
        }
    }

    @Unique
    public Integer getRandomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

}
