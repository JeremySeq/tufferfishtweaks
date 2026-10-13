package kozy1kove.tufferfishtweaks;

import kozy1kove.tufferfishtweaks.block.ModBlocks;
import kozy1kove.tufferfishtweaks.enchanting.EnchantingTableOverride;
import kozy1kove.tufferfishtweaks.enchanting.ModMenuTypes;
import kozy1kove.tufferfishtweaks.enchanting.ModRecipes;
import kozy1kove.tufferfishtweaks.item.Moditems;
import kozy1kove.tufferfishtweaks.network.ModNetworking;
import kozy1kove.tufferfishtweaks.worldgen.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TufferfishTweaks implements ModInitializer {
	public static final String MOD_ID = "tufferfishtweaks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("The Tweaks are Tuff fr");
		ModNetworking.init();
		Moditems.registerModItems();
		ModBlocks.registerModBlocks();
		ModRecipes.init();
		ModMenuTypes.init();
		EnchantingTableOverride.init();

		ModWorldGeneration.generateModWorldGen();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
