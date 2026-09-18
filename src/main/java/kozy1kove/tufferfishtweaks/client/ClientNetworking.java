package kozy1kove.tufferfishtweaks.client;

import kozy1kove.tufferfishtweaks.network.BeaconOpenS2CPayload;
import kozy1kove.tufferfishtweaks.network.BeaconToggleC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;

public final class ClientNetworking {

    private ClientNetworking() {

    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(BeaconOpenS2CPayload.TYPE,
                (payload, context) -> context.client().execute(() -> OpenBeaconTracker.set(payload.beaconPos())));
    }

    public static void sendToggle(BlockPos beaconPos, int toggle) {
        ClientPlayNetworking.send(new BeaconToggleC2SPayload(beaconPos, toggle));
    }
}
