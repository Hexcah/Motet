package hexca.motet;

public final class TotemUpgradeState {
    private static final ThreadLocal<Integer> RANGE_UPGRADE = ThreadLocal.withInitial(() -> 0);

    private TotemUpgradeState() {
    }

    public static void setRangeUpgrade(int range) {
        RANGE_UPGRADE.set(range);
    }

    public static int consumeRangeUpgrade() {
        int range = RANGE_UPGRADE.get();
        RANGE_UPGRADE.set(0);
        return range;
    }
}
