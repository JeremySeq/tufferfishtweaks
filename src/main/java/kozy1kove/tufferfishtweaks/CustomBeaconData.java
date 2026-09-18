package kozy1kove.tufferfishtweaks;

public interface CustomBeaconData {

    // nbt tag names, used for saving selections in world data
    String TAG_TIER1 = TufferfishTweaks.MOD_ID + ":tier1";
    String TAG_TIER2 = TufferfishTweaks.MOD_ID + ":tier2";
    String TAG_TIER3 = TufferfishTweaks.MOD_ID + ":tier3";
    String TAG_HOSTILE = TufferfishTweaks.MOD_ID + ":hostileSpawnBlock";
    String TAG_PASSIVE = TufferfishTweaks.MOD_ID + ":passiveSpawnBlock";

    // T1, increase potion duration
    boolean tufferfishtweaks$isTier1Enabled();
    void tufferfishtweaks$setTier1Enabled(boolean enabled);

    // T2, increase crop growth speed and decrease animal breeding time
    boolean tufferfishtweaks$isTier2Enabled();
    void tufferfishtweaks$setTier2Enabled(boolean enabled);

    // T3, haste 1
    boolean tufferfishtweaks$isTier3Enabled();
    void tufferfishtweaks$setTier3Enabled(boolean enabled);

    // T4, mob spawning toggles
    // enable passive spawns
    boolean tufferfishtweaks$isPassiveSpawnBlockEnabled();
    void tufferfishtweaks$setPassiveSpawnBlockEnabled(boolean enabled);
    // enable hostile spawns
    boolean tufferfishtweaks$isHostileSpawnBlockEnabled();
    void tufferfishtweaks$setHostileSpawnBlockEnabled(boolean enabled);
}