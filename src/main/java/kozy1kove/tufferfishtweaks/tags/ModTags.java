package kozy1kove.tufferfishtweaks.tags;

import kozy1kove.tufferfishtweaks.TufferfishTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.swing.text.html.HTML;

public class ModTags {
    public static class Blocks {

        private  static TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(TufferfishTweaks.MOD_ID, name));
        }
    }
    public static class Items {
        public static final TagKey<Item> TRANSFORMABLE_ITEMS = createTag("transformable_items");


        private  static TagKey<Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TufferfishTweaks.MOD_ID, name));
        }
    }
}
