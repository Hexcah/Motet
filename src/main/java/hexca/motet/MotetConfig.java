package hexca.motet;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MotetConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue TOTEM_BASE_RANGE;
    public static final ModConfigSpec.IntValue TOTEM_UPGRADE_1_RANGE;
    public static final ModConfigSpec.IntValue TOTEM_UPGRADE_2_RANGE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        TOTEM_BASE_RANGE = builder
                .comment("Base range of Totemic effects before music and maximum pole size bonuses.")
                .defineInRange("TOTEM_BASE_RANGE", 5, 0, Integer.MAX_VALUE);

        TOTEM_UPGRADE_1_RANGE = builder
                .comment("Additional range provided by totem_upgrade_1")
                .defineInRange("TOTEM_UPGRADE_1_RANGE", 3, 0, Integer.MAX_VALUE);

        TOTEM_UPGRADE_2_RANGE = builder
                .comment("Additional range provided by totem_upgrade_2")
                .defineInRange("TOTEM_UPGRADE_2_RANGE", 6, 0, Integer.MAX_VALUE);

        SPEC = builder.build();
    }

    private MotetConfig() {
    }
}
