package com.trimsreimagined.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.trimsreimagined.utils.TrimUtils;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ArmorTrimTooltipMixin implements ComponentHolder {

    @Shadow public abstract ComponentMap getComponents();

    @Shadow public abstract Item getItem();

    @Unique
    private static final Formatting TITLE_FORMATTING = Formatting.GRAY;
    @Unique
    private static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;
    @Unique
    private static final Text EFFECTS_TEXT = Text.translatable("mixin.trimsreimagined.effecttitle").formatted(TITLE_FORMATTING);
    @Unique
    private static final Text SET_BONUS_TEXT = Text.translatable("mixin.trimsreimagined.setbonustitle").formatted(TITLE_FORMATTING);

    @Inject(method = "getTooltip", at = @At(value = "TAIL"))
    public void tossInTrimBuffInfo(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir, @Local List<Text> list) {
        if ((Object)this.getItem() instanceof ArmorItem armorItem) {
            if(list.size() > 3) {
                String possibleTrimString = list.get(2).getString();
                if(possibleTrimString.contains("Trim")) {
                    String trimName = possibleTrimString.split(" ")[1].strip().toLowerCase();
                    if(TrimUtils.getListOfVanillaTrims().contains(trimName)) {
                        list.add(4, Text.empty());
                        list.add(5, EFFECTS_TEXT);
                        String effectDescriptionPath = String.format("trimsreimagined.%s.effectdescription", trimName);
                        list.add(6, ScreenTexts.space().append(Text.translatable(effectDescriptionPath).formatted(DESCRIPTION_FORMATTING)));

                        list.add(7, SET_BONUS_TEXT);
                        String setBonusDescription = String.format("trimsreimagined.%s.setbonusdescription", trimName);
                        list.add(8, ScreenTexts.space().append(Text.translatable(setBonusDescription).formatted(DESCRIPTION_FORMATTING)));
                    }
                }
            }
        }
    }
}
