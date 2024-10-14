package com.trimsreimagined.effect;

import com.trimsreimagined.TrimsReimagined;
import com.trimsreimagined.effect.custom.ShaperEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static StatusEffect SHAPER;
    public static StatusEffect SENTRY;
    public static StatusEffect VEX;
    public static StatusEffect WILD;
    public static StatusEffect COAST;
    public static StatusEffect DUNE;
    public static StatusEffect WAYFINDER;
    public static StatusEffect RAISER;
    public static StatusEffect HOST;
    public static StatusEffect WARD;
    public static StatusEffect SILENCE;
    public static StatusEffect TIDE;
    public static StatusEffect SNOUT;
    public static StatusEffect RIB;
    public static StatusEffect EYE;
    public static StatusEffect SPIRE;
    public static StatusEffect BOLT;
    public static StatusEffect FLOW;



    public static StatusEffect registerStatusEffect(String name) {
        return Registry.register(Registries.STATUS_EFFECT, Identifier.of(TrimsReimagined.MOD_ID, name),
                new ShaperEffect(StatusEffectCategory.BENEFICIAL));
    }

    public static void registerEffects() {
        SHAPER = registerStatusEffect("shaper");
        SENTRY = registerStatusEffect("sentry");
        VEX = registerStatusEffect("vex");
        WILD = registerStatusEffect("wild");
        COAST = registerStatusEffect("coast");
        DUNE = registerStatusEffect("dune");
        WAYFINDER = registerStatusEffect("wayfinder");
        RAISER = registerStatusEffect("raiser");
        HOST = registerStatusEffect("host");
        WARD = registerStatusEffect("ward");
        SILENCE = registerStatusEffect("silence");
        TIDE = registerStatusEffect("tide");
        SNOUT = registerStatusEffect("snout");
        RIB = registerStatusEffect("rib");
        EYE = registerStatusEffect("eye");
        SPIRE = registerStatusEffect("spire");
        BOLT = registerStatusEffect("bolt");
        FLOW = registerStatusEffect("flow");
    }

    public static StatusEffect getStatusEffectFromTrimName(String trimName) {
        return switch (trimName) {
            case "shaper" -> SHAPER;
            case "sentry" -> SENTRY;
            case "vex" -> VEX;
            case "wild" -> WILD;
            case "coast" -> COAST;
            case "dune" -> DUNE;
            case "wayfinder" -> WAYFINDER;
            case "raiser" -> RAISER;
            case "host" -> HOST;
            case "ward" -> WARD;
            case "silence" -> SILENCE;
            case "tide" -> TIDE;
            case "snout" -> SNOUT;
            case "rib" -> RIB;
            case "eye" -> EYE;
            case "spire" -> SPIRE;
            case "bolt" -> BOLT;
            case "flow" -> FLOW;
            default -> null;
        };
    }

}
