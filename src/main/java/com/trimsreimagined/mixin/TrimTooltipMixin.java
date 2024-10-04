package com.trimsreimagined.mixin;

import com.trimsreimagined.TrimsReimagined;
import com.trimsreimagined.utils.TrimUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(SmithingTemplateItem.class)
public abstract class TrimTooltipMixin {

    @Unique
    private static final Formatting TITLE_FORMATTING = Formatting.GRAY;
    @Unique
    private static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;
    @Unique
    private static final Text EFFECTS_TEXT = Text.translatable("mixin.trimsreimagined.effecttitle").formatted(TITLE_FORMATTING);
    @Unique
    private static final Text SET_BONUS_TEXT = Text.translatable("mixin.trimsreimagined.setbonustitle").formatted(TITLE_FORMATTING);

    @Shadow @Final private Text titleText;

    @Inject( method = "appendTooltip", at = @At("TAIL"))
    public void appendToolTip0(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci){
        //TODO refactor the below, very fragile.
        String trimType = this.titleText.getString().toLowerCase().split(" ")[0];
        if(TrimUtils.getListOfVanillaTrims().contains(trimType)){       //only show for vanilla trims
            tooltip.add(EFFECTS_TEXT);
            String effectDescriptionPath = String.format("trimsreimagined.%s.effectdescription", trimType);
            tooltip.add(ScreenTexts.space().append(Text.translatable(effectDescriptionPath).formatted(DESCRIPTION_FORMATTING)));

            tooltip.add(SET_BONUS_TEXT);
            String setBonusDescription = String.format("trimsreimagined.%s.setbonusdescription", trimType);
            tooltip.add(ScreenTexts.space().append(Text.translatable(setBonusDescription).formatted(DESCRIPTION_FORMATTING)));
        }
    }

}
