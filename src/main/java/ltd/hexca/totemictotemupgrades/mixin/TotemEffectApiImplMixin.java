package ltd.hexca.totemictotemupgrades.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import ltd.hexca.totemictotemupgrades.Totemictotemupgrades;

@Mixin(targets = "pokefenn.totemic.apiimpl.totem.TotemEffectApiImpl")
public abstract class TotemEffectApiImplMixin {
    @ModifyConstant(method = "getDefaultRange", constant = @Constant(intValue = 5))
    private int totemictotemupgrades$modifyBaseRange(int baseRange) {
        return Totemictotemupgrades.TOTEM_BASE_RANGE;
    }
}
