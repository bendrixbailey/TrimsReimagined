package com.trimsreimagined.mixin;

import com.trimsreimagined.effect.ModEffects;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {

    @Shadow public abstract RegistryKey<LootTable> getLootTableKey();

    @Inject(method = "getDroppedStacks", at = @At("HEAD"), cancellable = true)
    public void interceptAxeUseAddSilkTouch(
            BlockState state,
            LootContextParameterSet.Builder builder,
            CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        if(builder.get(LootContextParameters.THIS_ENTITY) != null) {
            if(builder.get(LootContextParameters.THIS_ENTITY) instanceof PlayerEntity player) {
                if(builder.get(LootContextParameters.TOOL) != null) {
                    if(builder.get(LootContextParameters.TOOL).getItem() instanceof AxeItem){
                        if(player.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(ModEffects.SHAPER))) {
                            //Player broke the item with an axe and has the loggers intuition effect
                            RegistryEntry<Enchantment> silkTouch = builder.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.SILK_TOUCH).orElseThrow();
                            if(!builder.get(LootContextParameters.TOOL).getEnchantments().getEnchantments().contains(silkTouch)) {
                                ItemStack newAxe = builder.get(LootContextParameters.TOOL).copy();
                                newAxe.addEnchantment(silkTouch, 1);
                                builder.add(LootContextParameters.TOOL, newAxe);
                                LootContextParameterSet lootContextParameterSet = builder.add(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
                                ServerWorld serverWorld = lootContextParameterSet.getWorld();
                                LootTable lootTable = serverWorld.getServer().getReloadableRegistries().getLootTable(this.getLootTableKey());
                                cir.setReturnValue(lootTable.generateLoot(lootContextParameterSet));
                            }
                        }
                    }
                }
            }

        }
    }
}
