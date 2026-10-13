package kozy1kove.tufferfishtweaks.enchanting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Data-driven enchanting recipe.
 * <p>
 * {
 *   "type": "tufferfishtweaks:enchanting",
 *   "block": "minecraft:redstone_block",
 *   "item": "minecraft:blaze_powder",
 *   "enchantment": "minecraft:sharpness"
 * }
 */
public record EnchantingRecipe(Ingredient block, Ingredient item, Holder<Enchantment> enchantment) implements Recipe<EnchantingRecipeInput> {

    public static final MapCodec<EnchantingRecipe> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("block").forGetter(EnchantingRecipe::block),
                    Ingredient.CODEC.fieldOf("item").forGetter(EnchantingRecipe::item),
                    Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantingRecipe::enchantment)
            ).apply(instance, EnchantingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantingRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC,
                    EnchantingRecipe::block,
                    Ingredient.CONTENTS_STREAM_CODEC,
                    EnchantingRecipe::item,
                    ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT),
                    EnchantingRecipe::enchantment,
                    EnchantingRecipe::new
            );

    public static DataComponentType<ItemEnchantments> enchantComponent(ItemStack stack) {
        return stack.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
    }

    public boolean canApplyTo(ItemStack target) {
        if (target.isEmpty()) {
            return false;
        }

        if (EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, target) > 0) {
            return false;
        }

        boolean isBook = target.is(Items.BOOK) || target.is(Items.ENCHANTED_BOOK);
        if (!isBook && !this.enchantment.value().canEnchant(target)) {
            return false;
        }

        ItemEnchantments existing = target.getOrDefault(
                enchantComponent(target),
                ItemEnchantments.EMPTY
        );

        if (existing.getLevel(this.enchantment) >= this.enchantment.value().getMaxLevel()) {
            return false;
        }

        for (Holder<Enchantment> other : existing.keySet()) {
            if (!other.equals(this.enchantment)
                    && !Enchantment.areCompatible(this.enchantment, other)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean matches(EnchantingRecipeInput input, Level level) {
        return this.block.test(input.block())
                && this.item.test(input.item())
                && this.canApplyTo(input.target());
    }

    /**
     * Applies the level selected by the enchanting menu's vanilla logic.
     */
    public ItemStack assemble(EnchantingRecipeInput input, int enchantLevel) {
        ItemStack target = input.target();
        ItemStack result = target.is(Items.BOOK) ? target.transmuteCopy(Items.ENCHANTED_BOOK) : target.copy();

        DataComponentType<ItemEnchantments> type = enchantComponent(result);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(
                result.getOrDefault(type, ItemEnchantments.EMPTY)
        );

        mutable.set(this.enchantment, enchantLevel);
        result.set(type, mutable.toImmutable());

        return result;
    }

    @Override
    public @NonNull ItemStack assemble(EnchantingRecipeInput input) {
        return input.target().copy();
    }

    @Override
    public @NonNull RecipeSerializer<? extends Recipe<EnchantingRecipeInput>> getSerializer() {
        return ModRecipes.ENCHANTING_SERIALIZER;
    }

    @Override
    public @NonNull RecipeType<? extends Recipe<EnchantingRecipeInput>> getType() {
        return ModRecipes.ENCHANTING_TYPE;
    }

    @Override
    public @Nullable RecipeBookCategory recipeBookCategory() {
        return null;
    }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull String group() {
        return "enchanting";
    }
}