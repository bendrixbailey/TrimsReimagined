package com.trimsreimagined.effect.custom;

import com.trimsreimagined.utils.PlayerMixinMethodAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class VexEffect extends StatusEffect {
    public VexEffect(StatusEffectCategory category) {
        super(category, 0x56261c);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier){
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity instanceof PlayerEntity player) {
            if(((PlayerMixinMethodAccess)player).trimsreimagined$getTrimCountForTrimType("vex").equals(4)) {
                super.applyUpdateEffect(entity, amplifier);
                return true;
            }
        }
        return false;
    }
}
