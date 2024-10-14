package com.trimsreimagined.mixin;


import com.mojang.authlib.GameProfile;
import com.trimsreimagined.TrimsReimagined;
import com.trimsreimagined.effect.ModEffects;
import com.trimsreimagined.utils.PlayerMixinMethodAccess;
import com.trimsreimagined.utils.TrimUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerMixinMethodAccess {

    @Unique
    Map<String, Integer> trimCountMapHelmet = new HashMap<>();
    @Unique
    Map<String, Integer> trimCountMapChestplate = new HashMap<>();
    @Unique
    Map<String, Integer> trimCountMapLeggings = new HashMap<>();
    @Unique
    Map<String, Integer> trimCountMapBoots = new HashMap<>();

    @Unique
    private boolean effectsEnabled;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);

    }

    public void trimsreimagined$toggleTrimEffects(){
        TrimsReimagined.LOGGER.info("Trim effect toggled!");
        this.effectsEnabled = !this.effectsEnabled;
        if(!this.effectsEnabled) { //clear effects if theyre on the player (only clears effects that sometimes persist)
            resetPlayerStatusEffects();
        }
    }

    public boolean trimsreimagined$getTrimEffectStatus(){
        return this.effectsEnabled;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void setupPlayerDataStructures(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        for (String trim : TrimUtils.getListOfVanillaTrims()){  // Initialize as if player was wearing no trims
            trimCountMapHelmet.put(trim, 0);
            trimCountMapChestplate.put(trim, 0);
            trimCountMapLeggings.put(trim, 0);
            trimCountMapBoots.put(trim, 0);
        }
        this.effectsEnabled = true;
    }

    @Shadow @Final
    PlayerInventory inventory;

    @Shadow public abstract void attack(Entity target);

    @Shadow protected abstract Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type);

    @Shadow public abstract float getAbsorptionAmount();

    @Shadow public abstract Text getName();

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void updateTrimCounts(CallbackInfo ci) {
        ItemStack helmetItem = this.inventory.getArmorStack(3);
        ItemStack chestplateItem = this.inventory.getArmorStack(2);
        ItemStack leggingsItem = this.inventory.getArmorStack(1);
        ItemStack bootsItem = this.inventory.getArmorStack(0);

        clearTrimWornCount(trimCountMapHelmet);
        clearTrimWornCount(trimCountMapChestplate);
        clearTrimWornCount(trimCountMapLeggings);
        clearTrimWornCount(trimCountMapBoots);

        String biomePlayerIsIn = this.getWorld().getBiome(this.getBlockPos()).getIdAsString();

        if (helmetItem.getItem() instanceof ArmorItem) {
            if (helmetItem.getComponents().get(DataComponentTypes.TRIM) != null) {
                String trimTypeString = Objects.requireNonNull(helmetItem.getComponents().get(DataComponentTypes.TRIM)).getPattern().value().assetId().toString();
                if (trimTypeString.split(":").length > 1) {
                    String trimName = trimTypeString.split(":")[1];
                    updateTrimWornCount(trimName, trimCountMapHelmet);
                }
            }
        }
        if (chestplateItem.getItem() instanceof ArmorItem) {
            if (chestplateItem.getComponents().get(DataComponentTypes.TRIM) != null) {
                String trimTypeString = Objects.requireNonNull(chestplateItem.getComponents().get(DataComponentTypes.TRIM)).getPattern().value().assetId().toString();
                if (trimTypeString.split(":").length > 1) {
                    String trimName = trimTypeString.split(":")[1];
                    updateTrimWornCount(trimName, trimCountMapChestplate);
                }
            }
        }
        if (leggingsItem.getItem() instanceof ArmorItem) {
            if (leggingsItem.getComponents().get(DataComponentTypes.TRIM) != null) {
                String trimTypeString = Objects.requireNonNull(leggingsItem.getComponents().get(DataComponentTypes.TRIM)).getPattern().value().assetId().toString();
                if (trimTypeString.split(":").length > 1) {
                    String trimName = trimTypeString.split(":")[1];
                    updateTrimWornCount(trimName, trimCountMapLeggings);
                }
            }
        }
        if (bootsItem.getItem() instanceof ArmorItem) {
            if (bootsItem.getComponents().get(DataComponentTypes.TRIM) != null) {
                String trimTypeString = Objects.requireNonNull(bootsItem.getComponents().get(DataComponentTypes.TRIM)).getPattern().value().assetId().toString();
                if (trimTypeString.split(":").length > 1) {
                    String trimName = trimTypeString.split(":")[1];
                    updateTrimWornCount(trimName, trimCountMapBoots);
                }
            }
        }

        // Calls to apply status effects
        updateSentryTrimEffects();
        updateCoastTrimEffects();
        updateWildTrimEffects(biomePlayerIsIn);
        updateDuneTrimEffects(biomePlayerIsIn);
        updateSpireTrimEffects();
        updateBoltTrimEffects();
        updateShaperTrimEffects();
        updateRaiserTrimEffects();
        updateTideTrimEffects();
        updateHostTrimEffects();

        //apply visual effect
        applyTrimSetBonusEffect();
    }


    /**
     * Mixin to adjust damage reductions to the player based on enemy attacking
     * @param source
     * @param amount
     * @return
     */
    @ModifyArg(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
    private float scaleDamageForTrimsModifyVariable(DamageSource source, float amount) {
        float damageDealtToPlayer = amount;
        if(source.getAttacker() != null) {  //If the attacker is an entity
            Entity attacker = source.getAttacker();
            //Call damage reduction trim methods here
            damageDealtToPlayer = updateSentryTrimEffectsGetCalculatedDamage(attacker, damageDealtToPlayer);
            damageDealtToPlayer = updateVexTrimEffectsBlockVexDamage(attacker, damageDealtToPlayer);
        }
        if(source.isIn(DamageTypeTags.IS_FIRE)) {
            damageDealtToPlayer = updateRibTrimEffectsReduceFireDamage(damageDealtToPlayer);
        }
        return damageDealtToPlayer;
    }

    /**
     * Mixin to adjust attack speed based on trims worn
     * @param ci
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"))
    private void speedUpAttackSpeedIfTrimBonus(CallbackInfo ci) {
        if(trimsreimagined$getTrimCountForTrimType("flow").equals(4)) {
            this.lastAttackedTicks++;
        }
    }

    /**
     * Mixin to adjust block interaction range based on trims worn and item in main hand
     * @param cir
     */
    @Inject(method = "getBlockInteractionRange", at = @At(value = "TAIL"), cancellable = true)
    public void modifyBlockReachForTrims(CallbackInfoReturnable<Double> cir) {
        ItemStack mainHandItemStack  = this.getMainHandStack();
        Integer shaperTrimCount = this.trimsreimagined$getTrimCountForTrimType("shaper");
        Integer wayfinderTrimCount = this.trimsreimagined$getTrimCountForTrimType("wayfinder");
        if(mainHandItemStack.getItem() instanceof AxeItem) {
            if (shaperTrimCount.equals(1) || shaperTrimCount.equals(2)) {
                cir.setReturnValue(this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE) + 1);
            }
            if (shaperTrimCount.equals(3) || shaperTrimCount.equals(4)) {
                cir.setReturnValue(this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE) + 2);
            }
        }
        if(mainHandItemStack.getItem() instanceof HoeItem) {
            if (wayfinderTrimCount.equals(1) || wayfinderTrimCount.equals(2)) {
                cir.setReturnValue(this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE) + 1);
            }
            if (wayfinderTrimCount.equals(3) || wayfinderTrimCount.equals(4)) {
                cir.setReturnValue(this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE) + 2);
            }
        }
    }

    /**
     * Below methods are helpers for armor trim accessing/effects
     */


    @Unique
    public void updateTrimWornCount(String trimName, Map<String, Integer> armorPieceType) {
        if(this.effectsEnabled) {
            for (String trim : TrimUtils.getListOfVanillaTrims()) {  // Initialize as if player was wearing no trims
                armorPieceType.put(trim, 0);
            }
            armorPieceType.put(trimName, 1);
        }
        else {
            for (String trim : TrimUtils.getListOfVanillaTrims()) {  // Initialize as if player was wearing no trims
                armorPieceType.put(trim, 0);
            }
        }
    }

    @Unique
    public void clearTrimWornCount(Map<String, Integer> armorPieceType) {
        for (String trim : TrimUtils.getListOfVanillaTrims()){  // Initialize as if player was wearing no trims
            armorPieceType.put(trim, 0);
        }
    }

    /**
     * This method returns the number of armor pieces that have a specific trim type on them
     * @param trimType type of vanilla trim
     * @return count of armor pieces with trim equipped
     */
    public Integer trimsreimagined$getTrimCountForTrimType(String trimType) {
        if (TrimUtils.getListOfVanillaTrims().contains(trimType)) {
            return trimCountMapHelmet.get(trimType)
                    + trimCountMapChestplate.get(trimType)
                    + trimCountMapLeggings.get(trimType)
                    + trimCountMapBoots.get(trimType);
        }
        return 0;
    }

    /**
     * Applies a status effect to the player that has the name of the trim set bonus. Status effect is non-functional,
     * simply a way to display to the player the bonus is active.
     */
    @Unique
    public void applyTrimSetBonusEffect() {
        for (String trim : TrimUtils.getListOfVanillaTrims()) {
            if(trimsreimagined$getTrimCountForTrimType(trim).equals(4)) {
                StatusEffect effectToGive = ModEffects.getStatusEffectFromTrimName(trim);
                if(effectToGive != null){
                    this.addStatusEffect(new StatusEffectInstance(
                            Registries.STATUS_EFFECT.getEntry(effectToGive),
                            100,
                            0,
                            false,
                            false,
                            true
                    ));
                }
            }
        }
    }

    /**
     * This method is needed because toggling the armor effects sometimes locks previously boosted effects(ex. haste 2)
     * and applies it permanently. This collects what effects the player has, and reapplies them so they dont lose
     * important ones, like hero of the village, bad omen, etc.
     */
    @Unique
    public void resetPlayerStatusEffects() {
        Collection<StatusEffectInstance> statusEffects = this.getStatusEffects();
        this.clearStatusEffects();
        for (StatusEffectInstance instance : statusEffects) {
            if(instance.equals(StatusEffects.HERO_OF_THE_VILLAGE)) {
                this.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.HERO_OF_THE_VILLAGE,
                        instance.getDuration(),
                        instance.getAmplifier(),
                        false,
                        false,
                        true
                ));
            }
            if(instance.equals(StatusEffects.HASTE) && instance.getAmplifier() == 2) {
                this.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.HERO_OF_THE_VILLAGE,
                        instance.getDuration(),
                        instance.getAmplifier(),
                        false,
                        false,
                        true
                ));
            }
            if(instance.equals(StatusEffects.BAD_OMEN)) {
                this.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.BAD_OMEN,
                        instance.getDuration(),
                        instance.getAmplifier(),
                        false,
                        false,
                        true
                ));
            }
            if(instance.equals(StatusEffects.MINING_FATIGUE)) {
                this.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.MINING_FATIGUE,
                        instance.getDuration(),
                        instance.getAmplifier(),
                        false,
                        false,
                        true
                ));
            }
            if(instance.equals(StatusEffects.INVISIBILITY)) {
                this.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.INVISIBILITY,
                        instance.getDuration(),
                        instance.getAmplifier(),
                        false,
                        false,
                        true
                ));
            }
            if(instance.equals(StatusEffects.NAUSEA)) {
                this.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NAUSEA,
                        instance.getDuration(),
                        instance.getAmplifier(),
                        false,
                        false,
                        true
                ));
            }
            if(instance.equals(StatusEffects.DARKNESS)) {
                this.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.DARKNESS,
                        instance.getDuration(),
                        instance.getAmplifier(),
                        false,
                        false,
                        true
                ));
            }
        }
    }

    /**
     * Methods below handle the logic to apply the damage modifications to the player based on trim type.
     */

    /**
     * If the sentry armor trim exists on the armor set, apply damage reductions against pillager mobs.
     * Returns the damage reduction from the armor set bonus.
     * @param attacker  attacker entity
     * @return damage reduction amount, in %
     */
    @Unique
    private float updateSentryTrimEffectsGetCalculatedDamage(Entity attacker, float damageDealtToPlayer) {
        Integer trimCountForSentry = this.trimsreimagined$getTrimCountForTrimType("sentry");
        float damageReductionAmount = 0;
        if(trimCountForSentry > 0 && (attacker instanceof PatrolEntity || attacker instanceof VexEntity)) {
            damageReductionAmount = 1 - ((float) (trimCountForSentry * 10) / 100);
            if(trimCountForSentry.equals(4)){
                damageReductionAmount = (float) 1 /2;
            }
            return damageReductionAmount * damageDealtToPlayer;
        }
        return damageDealtToPlayer;
    }

    /**
     * Helper for determining if entity is a vex attacking the player
     * @param attacker
     * @param damageDealtToPlayer
     * @return
     */
    @Unique
    private float updateVexTrimEffectsBlockVexDamage(Entity attacker, float damageDealtToPlayer) {
        if(trimsreimagined$getTrimCountForTrimType("vex").equals(4)){
            if(attacker instanceof VexEntity){
                return 0;
            }else {
                return damageDealtToPlayer;
            }
        }else {
            return damageDealtToPlayer;
        }
    }

    /**
     * Helper for calculating reduced fire damage amount
     * @param damageDealtToPlayer
     * @return
     */
    @Unique
    private float updateRibTrimEffectsReduceFireDamage(float damageDealtToPlayer) {
        Integer ribTrimCount = trimsreimagined$getTrimCountForTrimType("rib");
        float damageReduction = 10 * (ribTrimCount * 2); //20%, 40%, 60%, 80% damage reduction
        return (damageReduction/100) * damageDealtToPlayer;
    }

    /**
     * Methods below handle status effects on the player based on trim type.
     */

    @Unique
    private void updateSentryTrimEffects() {
        if(this.trimsreimagined$getTrimCountForTrimType("sentry").equals(4)) {
            if(this.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
                this.addStatusEffect( new StatusEffectInstance(
                        StatusEffects.HERO_OF_THE_VILLAGE,
                        100,
                        0,
                        false,
                        false,
                        true
                ));
            }
        }
    }

    @Unique
    private void updateWildTrimEffects(String biomePlayerIn) {
        Integer trimCountForWild = this.trimsreimagined$getTrimCountForTrimType("wild");
        if(trimCountForWild > 0) {
            for(String allowedBiomeString : TrimUtils.wildAllowedBiomes) {
                if(biomePlayerIn.contains(allowedBiomeString)) {
                    this.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.SPEED,
                            100,
                            trimCountForWild - 1,
                            false,
                            false,
                            true
                    ));
                }
            }
        }
    }

    @Unique
    private void updateCoastTrimEffects() {
        Integer trimCountForCoast = this.trimsreimagined$getTrimCountForTrimType("coast");
        if(trimCountForCoast > 0) {
            this.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.WATER_BREATHING,
                    100,
                    trimCountForCoast - 1,
                    false,
                    false,
                    true));
            if (trimCountForCoast.equals(4)) {
                this.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.DOLPHINS_GRACE,
                        100,
                        0,
                        false,
                        false,
                        true
                ));
            }
        }
    }

    @Unique
    private void updateDuneTrimEffects(String biomePlayerIn) {
        Integer trimCountForDune = this.trimsreimagined$getTrimCountForTrimType("dune");
        if(trimCountForDune > 0) {
            for(String allowedBiomeString : TrimUtils.duneAllowedBiomes) {
                if(biomePlayerIn.contains(allowedBiomeString)) {
                    this.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.SPEED,
                            100,
                            trimCountForDune - 1,
                            false,
                            false,
                            true
                    ));
                }
            }
        }
    }

    @Unique
    private void updateHostTrimEffects() {
        Integer hostTrimCount = this.trimsreimagined$getTrimCountForTrimType("host");
        if(hostTrimCount > 0) {
            this.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.ABSORPTION,
                    100,
                    hostTrimCount * 2,
                    false,
                    false,
                    true
            ));
            this.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SLOWNESS,
                    100,
                    hostTrimCount - 1,
                    false,
                    false,
                    true
            ));
            if(hostTrimCount.equals(4)) {
                this.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.RESISTANCE,
                        100,
                        1,
                        false,
                        false,
                        true
                ));
            }
        }
    }

    @Unique
    private void updateRaiserTrimEffects() {
        ItemStack mainHandItem = this.getMainHandStack();
        Integer raiserTrimCount = this.trimsreimagined$getTrimCountForTrimType("raiser");
        if(raiserTrimCount > 0 && mainHandItem.getItem() instanceof PickaxeItem) {
            this.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.HASTE,
                    200,
                    0,
                    false,
                    false,
                    true
            ));
            if(raiserTrimCount.equals(4)) {
                if(this.hasStatusEffect(StatusEffects.HASTE)) {
                    if(Objects.requireNonNull(this.getStatusEffect(StatusEffects.HASTE)).getAmplifier() == 1) {
                        this.addStatusEffect(new StatusEffectInstance(
                                StatusEffects.HASTE,
                                200,
                                2,
                                false,
                                false,
                                true
                        ));
                    }
                }
            }
        }
    }

    @Unique
    private void updateShaperTrimEffects() {
        ItemStack mainHandItemStack  = this.getMainHandStack();
        Integer shaperTrimCount = this.trimsreimagined$getTrimCountForTrimType("shaper");
        if(shaperTrimCount.equals(4)) {
            if(mainHandItemStack.getItem() instanceof AxeItem) {
                this.addStatusEffect(new StatusEffectInstance(
                        Registries.STATUS_EFFECT.getEntry(ModEffects.SHAPER),
                        100,
                        0,
                        false,
                        false,
                        true
                ));
            }
        }

    }

    @Unique
    private void updateTideTrimEffects() {
        Integer tideTrimCount = this.trimsreimagined$getTrimCountForTrimType("tide");
        if(tideTrimCount > 0) {
            if(this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
                StatusEffectInstance appliedMiningFatigue = this.getStatusEffect(StatusEffects.MINING_FATIGUE);
                Integer fatigueLevel = appliedMiningFatigue.getAmplifier();
                if(fatigueLevel.equals(2)) { //this is the level that an elder guardian gives the player
                    if(tideTrimCount.equals(1)) {
                        this.removeStatusEffect(StatusEffects.MINING_FATIGUE);
                        this.addStatusEffect(new StatusEffectInstance(
                                StatusEffects.MINING_FATIGUE,
                                appliedMiningFatigue.getDuration(),
                                1,
                                false,
                                false,
                                true
                        ));
                    }
                    if(tideTrimCount > 1) {
                        this.removeStatusEffect(StatusEffects.MINING_FATIGUE);
                        this.addStatusEffect(new StatusEffectInstance(
                                StatusEffects.MINING_FATIGUE,
                                appliedMiningFatigue.getDuration(),
                                0,
                                false,
                                false,
                                true
                        ));
                    }
                }
            }
        }
    }

    @Unique
    private void updateSpireTrimEffects() {
        if(this.trimsreimagined$getTrimCountForTrimType("spire").equals(4)) {
            this.addStatusEffect( new StatusEffectInstance(
                    StatusEffects.SLOW_FALLING,
                    100,
                    0,
                    false,
                    false,
                    true
            ));
        }
    }

    @Unique
    private void updateBoltTrimEffects() {
        if(this.getWorld().isRaining() || this.getWorld().isThundering()){
            Integer boltTrimCount = this.trimsreimagined$getTrimCountForTrimType("bolt");
            if(boltTrimCount > 0) {
                this.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SPEED,
                        100,
                        boltTrimCount - 1,
                        false,
                        false,
                        true
                ));
            }
        }
    }
}
