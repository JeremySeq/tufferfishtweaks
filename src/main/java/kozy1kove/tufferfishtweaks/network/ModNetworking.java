package kozy1kove.tufferfishtweaks.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class ModNetworking {

    private static volatile boolean typesRegistered = false;

    private ModNetworking() {
    }

    public static synchronized void registerPayloadTypes() {
        if (typesRegistered) {
            return;
        }
        PayloadTypeRegistry.serverboundPlay().register(BeaconToggleC2SPayload.TYPE, BeaconToggleC2SPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(BeaconOpenS2CPayload.TYPE, BeaconOpenS2CPayload.CODEC);
        typesRegistered = true;
    }

    public static void init() {
        registerPayloadTypes();

        ServerPlayNetworking.registerGlobalReceiver(BeaconToggleC2SPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            player.server.execute(() -> BeaconOpenS2CPayload.handle(player, payload.beaconPos(), payload.toggle()));
        });
    }
}