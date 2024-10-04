package com.trimsreimagined.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.trimsreimagined.utils.PlayerMixinMethodAccess;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
    @ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getDifficulty()Lnet/minecraft/world/Difficulty;"))
    public Difficulty setDifficultyPeacefulToAvoidHungerDrain(Difficulty original, @Local(argsOnly = true) PlayerEntity player) {
        Integer wayfinderTrimCount = ((PlayerMixinMethodAccess) player).trimsreimagined$getTrimCountForTrimType("wayfinder");
        if(wayfinderTrimCount.equals(4)) {
            if(player.getMainHandStack().getItem() instanceof HoeItem) {
                return Difficulty.PEACEFUL;
            }
        }
        return original;
    }
}
