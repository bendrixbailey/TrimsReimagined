package com.trimsreimagined.mixin;

import com.trimsreimagined.utils.PlayerMixinMethodAccess;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PiglinBrain.class)
public abstract class PiglinBrainMixin {

    @Shadow
    private static void drop(PiglinEntity piglin, List<ItemStack> items, Vec3d pos) {
    }

    @Inject(method = "dropBarteredItem(Lnet/minecraft/entity/mob/PiglinEntity;Lnet/minecraft/entity/player/PlayerEntity;Ljava/util/List;)V", at = @At("TAIL"))
    private static void doExtraDropsForTrimBonus(PiglinEntity piglin, PlayerEntity player, List<ItemStack> items, CallbackInfo ci) {
        if(((PlayerMixinMethodAccess)player).trimsreimagined$getTrimCountForTrimType("snout").equals(4)) {
            drop(piglin, items, player.getPos());
        }
    }
}
