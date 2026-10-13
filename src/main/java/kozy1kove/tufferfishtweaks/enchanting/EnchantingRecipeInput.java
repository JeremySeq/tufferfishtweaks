package kozy1kove.tufferfishtweaks.enchanting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jspecify.annotations.NonNull;

public record EnchantingRecipeInput(ItemStack block, ItemStack item, ItemStack lapis, ItemStack target) implements RecipeInput {

    @Override
    public @NonNull ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> this.block;
            case 1 -> this.item;
            case 2 -> this.lapis;
            case 3 -> this.target;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 4;
    }
}
