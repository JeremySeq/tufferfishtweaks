package kozy1kove.tufferfishtweaks.worldgen.gen;

import kozy1kove.tufferfishtweaks.worldgen.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;

public class ModWorldGeneration {
    public static void  generateModWorldGen() {
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Decoration.UNDERGROUND_ORES,
                ModPlacedFeatures.CHORIUM_ORE_PLACED_KEY);
    }
}
