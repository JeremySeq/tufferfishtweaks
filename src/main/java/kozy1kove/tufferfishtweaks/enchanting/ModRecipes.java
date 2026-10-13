package kozy1kove.tufferfishtweaks.enchanting;

import kozy1kove.tufferfishtweaks.TufferfishTweaks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipes {

    public static final RecipeSerializer<EnchantingRecipe> ENCHANTING_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(TufferfishTweaks.MOD_ID, "enchanting"),
            new RecipeSerializer<>(EnchantingRecipe.CODEC, EnchantingRecipe.STREAM_CODEC)
    );

    public static final RecipeType<EnchantingRecipe> ENCHANTING_TYPE = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(TufferfishTweaks.MOD_ID, "enchanting"),
            new RecipeType<EnchantingRecipe>() { }
    );

    private ModRecipes() {
    }

    public static void init() {
    }
}
