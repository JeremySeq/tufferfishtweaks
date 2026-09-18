package kozy1kove.tufferfishtweaks.mixin.client;

import kozy1kove.tufferfishtweaks.client.TieredBeaconScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replace vanilla {@link net.minecraft.client.gui.screens.inventory.BeaconScreen} with {@link TieredBeaconScreen}
 */
@Mixin(MenuScreens.class)
public class ScreenManagerMixin {

    @Unique
    private static boolean isReplaced = false;

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, @NonNull U> factory, CallbackInfo ci) {
        if (type == MenuType.BEACON && !isReplaced) {
            isReplaced = true;
            MenuScreens.register(MenuType.BEACON, TieredBeaconScreen::new);
            ci.cancel();
        }
    }
}