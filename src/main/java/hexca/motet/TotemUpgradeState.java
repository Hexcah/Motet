package hexca.motet;

public final class TotemUpgradeState {
    private static final ThreadLocal<Boolean> RANGE_UPGRADE_ACTIVE = ThreadLocal.withInitial(() -> false);

    private TotemUpgradeState() {
    }

    public static void setRangeUpgradeActive(boolean active) {
        RANGE_UPGRADE_ACTIVE.set(active);
    }

    public static boolean consumeRangeUpgrade() {
        boolean active = RANGE_UPGRADE_ACTIVE.get();
        RANGE_UPGRADE_ACTIVE.set(false);
        return active;
    }
}
