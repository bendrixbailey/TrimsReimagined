package com.trimsreimagined.mixin;

import com.trimsreimagined.utils.PlayerMixinMethodAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public abstract class EndermanMixin extends HostileEntity {
    protected EndermanMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * If the player has a full set of eye armor, dont aggo the enderman to the player
     * @param player player thats looking at enderman
     * @param cir callback to interrupt
     */
    @Inject(method = "isPlayerStaring", at = @At("HEAD"), cancellable = true)
    public void allowStaringIfTrimmedOut(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if(((PlayerMixinMethodAccess) player).trimsreimagined$getTrimCountForTrimType("eye").equals(4)) {
            cir.setReturnValue(false);
        }
    }

}
