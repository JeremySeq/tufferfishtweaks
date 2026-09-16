package kozy1kove.tufferfishtweaks.worldgen;

import kozy1kove.tufferfishtweaks.TufferfishTweaks;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> CHORIUM_ORE_PLACED_KEY = registerKey("chorium_ore_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        //register(context, CHORIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.CHORIUM_ORE_KEY),
                //OrePlacements.commonOrePlacement(6,
                        //HeightRangePlacement.triangle(VerticalAnchor.absolute(-32), VerticalAnchor.absolute(100))));
        register(context, CHORIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.CHORIUM_ORE_KEY),
                OrePlacements.rareOrePlacement(1,
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(13), VerticalAnchor.absolute(100))));


    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        Object TutorialMod;
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(TufferfishTweaks.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
