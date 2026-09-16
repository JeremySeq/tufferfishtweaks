package kozy1kove.tufferfishtweaks.datagen;

import kozy1kove.tufferfishtweaks.block.ModBlocks;
import kozy1kove.tufferfishtweaks.item.Moditems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class ModModelProvider extends FabricModelProvider
{
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createTrivialCube(ModBlocks.CHORIUM_ORE);

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(Moditems.RAW_CHORIUM, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(Moditems.CHORISTEEL_INGOT, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(Moditems.CHORINITE_INGOT, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(Moditems.NETHERSTEEL_INGOT, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(Moditems.CHORIUM, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(Moditems.LAPIS_BOOK, ModelTemplates.FLAT_ITEM);

    }
}
