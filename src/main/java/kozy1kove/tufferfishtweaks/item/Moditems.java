package kozy1kove.tufferfishtweaks.item;

import kozy1kove.tufferfishtweaks.TufferfishTweaks;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.math.IEEE754rUtils;

import java.util.function.Function;

public class Moditems {
    public static final Item RAW_CHORIUM = registerItem("raw_chorium", Item::new);
    public static final Item CHORISTEEL_INGOT = registerItem("choristeel_ingot", Item::new);
    public static final Item CHORINITE_INGOT = registerItem("chorinite_ingot", Item::new);
    public static final Item NETHERSTEEL_INGOT = registerItem("nethersteel_ingot", Item::new);
    public static final Item CHORIUM = registerItem("chorium", Item::new);
    public static final Item LAPIS_BOOK = registerItem("lapis_book", Item::new);





    private static Item registerItem(String name, Function<Item.Properties, Item> function) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TufferfishTweaks.MOD_ID, name),
                function.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TufferfishTweaks.MOD_ID, name)))));
    }



    public static void  registerModItems() {
        TufferfishTweaks.LOGGER.info("registering Mod Items for" + TufferfishTweaks.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> {
            output.accept(RAW_CHORIUM);
            output.accept(CHORISTEEL_INGOT);
            output.accept(CHORINITE_INGOT);
            output.accept(NETHERSTEEL_INGOT);
            output.accept(CHORIUM);
            output.accept(LAPIS_BOOK);
        });
        //CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output -> {
         //   output.accept();
        //});
    }
}
