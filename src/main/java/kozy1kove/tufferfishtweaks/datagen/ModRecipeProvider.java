package kozy1kove.tufferfishtweaks.datagen;

import kozy1kove.tufferfishtweaks.block.ModBlocks;
import kozy1kove.tufferfishtweaks.item.Moditems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                List<ItemLike> CHORIUM_SMELTABLES = List.of(ModBlocks.CHORIUM_ORE);
                List<ItemLike> RAW_CHORIUM_SMELTABLES = List.of(Moditems.RAW_CHORIUM);

                oreSmelting(CHORIUM_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.BLOCKS, Moditems.RAW_CHORIUM, 0.3f, 200, "chorium");
                oreBlasting(CHORIUM_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.BLOCKS, Moditems.RAW_CHORIUM, 0.3f, 100, "chorium");
                oreSmelting(RAW_CHORIUM_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, Moditems.CHORIUM, 0.3f, 200, "chorium");
                oreBlasting(RAW_CHORIUM_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, Moditems.CHORIUM, 0.3f, 100, "chorium");


                shapeless(RecipeCategory.MISC, Moditems.CHORISTEEL_INGOT, 1)
                        .requires(Moditems.CHORIUM, 4)
                        .requires(Items.IRON_INGOT, 4)
                        .unlockedBy(getHasName(Moditems.RAW_CHORIUM), has(Moditems.RAW_CHORIUM))
                        .save(output);
                shapeless(RecipeCategory.MISC, Moditems.CHORINITE_INGOT, 1)
                        .requires(Moditems.CHORIUM, 4)
                        .requires(Items.GOLD_INGOT, 4)
                        .unlockedBy(getHasName(Moditems.RAW_CHORIUM), has(Moditems.RAW_CHORIUM))
                        .save(output);
                shapeless(RecipeCategory.MISC, Moditems.NETHERSTEEL_INGOT, 1)
                        .requires(Items.NETHERITE_SCRAP, 4)
                        .requires(Items.IRON_INGOT, 4)
                        .unlockedBy(getHasName(Items.ANCIENT_DEBRIS), has(Items.ANCIENT_DEBRIS))
                        .save(output);






            }
        };
    }

    @Override
    public String getName() {
        return "TufferfishTweaks Recipes";
    }
}
