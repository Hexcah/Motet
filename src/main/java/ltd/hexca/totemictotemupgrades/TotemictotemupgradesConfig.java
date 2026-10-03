package ltd.hexca.totemictotemupgrades;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class TotemictotemupgradesConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue TOTEM_BASE_RANGE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        TOTEM_BASE_RANGE = builder
                .comment("Base range of Totemic effects before music and maximum pole size bonuses.")
                .defineInRange("TOTEM_BASE_RANGE", 5, 0, Integer.MAX_VALUE);

        SPEC = builder.build();
    }

    private TotemictotemupgradesConfig() {
    }
}
