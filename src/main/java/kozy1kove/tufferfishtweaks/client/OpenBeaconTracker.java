package kozy1kove.tufferfishtweaks.client;

import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;


/**
 * Stores position of beacon menu currently open on client.
 * @see kozy1kove.tufferfishtweaks.network.BeaconOpenS2CPayload
 */
public final class OpenBeaconTracker {

    private static @Nullable BlockPos currentPos;

    private OpenBeaconTracker() {

    }

    public static void set(BlockPos pos) {
        currentPos = pos;
    }

    public static void clear() {
        currentPos = null;
    }

    public static @Nullable BlockPos get() {
        return currentPos;
    }
}
