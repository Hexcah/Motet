package hexca.motet.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hexca.motet.Motet;
import hexca.motet.TotemUpgradeState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(targets = "pokefenn.totemic.block.totem.entity.TotemBaseBlockEntity")
public abstract class TotemBaseBlockEntityMixin {
    @Inject(method = "getPoleSize", at = @At("RETURN"))
    private void motet$detectRangeUpgrade(CallbackInfoReturnable<Integer> cir) {
        BlockEntity base = (BlockEntity) (Object) this;
        Level level = base.getLevel();
        BlockPos basePos = base.getBlockPos();
        TotemUpgradeState.setRangeUpgradeActive(level != null
                && level.getBlockState(basePos.below()).is(Motet.TOTEM_UPGRADE_1.get()));
    }
}
