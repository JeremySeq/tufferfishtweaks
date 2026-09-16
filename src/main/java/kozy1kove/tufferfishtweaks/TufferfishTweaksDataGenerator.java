package kozy1kove.tufferfishtweaks;

import kozy1kove.tufferfishtweaks.datagen.*;
import kozy1kove.tufferfishtweaks.worldgen.ModConfiguredFeatures;
import kozy1kove.tufferfishtweaks.worldgen.ModPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class TufferfishTweaksDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();

		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModBlockTagsProvider::new);
		pack.addProvider(ModBlockLootTableProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModWorldGenProvider::new);


	}
	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {

		registryBuilder.add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap);
		registryBuilder.add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
	}

}
