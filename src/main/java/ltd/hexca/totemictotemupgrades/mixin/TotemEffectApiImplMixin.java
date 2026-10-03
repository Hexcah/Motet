package ltd.hexca.totemictotemupgrades.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ltd.hexca.totemictotemupgrades.TotemictotemupgradesConfig;

@Mixin(targets = "pokefenn.totemic.apiimpl.totem.TotemEffectApiImpl")
public abstract class TotemEffectApiImplMixin {
    @Inject(method = "getDefaultRange", at = @At("RETURN"), cancellable = true)
    private void totemictotemupgrades$modifyBaseRange(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValueI() + TotemictotemupgradesConfig.TOTEM_BASE_RANGE.get() - 5);
    }
}
