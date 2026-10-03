package hexca.motet.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hexca.motet.MotetConfig;
import hexca.motet.TotemUpgradeState;

@Mixin(targets = "pokefenn.totemic.apiimpl.totem.TotemEffectApiImpl")
public abstract class TotemEffectApiImplMixin {
    @Inject(method = "getDefaultRange", at = @At("RETURN"), cancellable = true)
    private void motet$modifyBaseRange(CallbackInfoReturnable<Integer> cir) {
        int range = cir.getReturnValueI() + MotetConfig.TOTEM_BASE_RANGE.get() - 5;
        if (TotemUpgradeState.consumeRangeUpgrade()) {
            range += MotetConfig.TOTEM_UPGRADE_1_RANGE.get();
        }
        cir.setReturnValue(range);
    }
}
