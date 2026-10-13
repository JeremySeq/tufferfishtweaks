package kozy1kove.tufferfishtweaks.client;

import kozy1kove.tufferfishtweaks.enchanting.ModMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class TufferfishTweaksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientNetworking.init();
        MenuScreens.register(ModMenuTypes.CUSTOM_ENCHANT, kozy1kove.tufferfishtweaks.client.CustomEnchantScreen::new);
    }
}