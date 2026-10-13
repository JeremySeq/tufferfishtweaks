package kozy1kove.tufferfishtweaks.enchanting;

import kozy1kove.tufferfishtweaks.TufferfishTweaks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class ModMenuTypes {

    public static final MenuType<CustomEnchantMenu> CUSTOM_ENCHANT = register("enchanting", CustomEnchantMenu::new);

    private ModMenuTypes() {
    }

    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType.MenuSupplier<T> constructor) {
        return Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(TufferfishTweaks.MOD_ID, name), new MenuType<>(constructor, FeatureFlagSet.of()));
    }

    public static void init() {
    }
}
