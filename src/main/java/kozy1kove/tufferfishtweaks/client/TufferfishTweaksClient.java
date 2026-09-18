package kozy1kove.tufferfishtweaks.client;

import net.fabricmc.api.ClientModInitializer;

public class TufferfishTweaksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientNetworking.init();
    }
}