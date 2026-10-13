
package kozy1kove.tufferfishtweaks.enchanting;

import java.util.List;
import java.util.Optional;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;

import org.jspecify.annotations.NonNull;

/**
 * Replacement enchanting table menu.
 * Much copied from {@link net.minecraft.world.inventory.EnchantmentMenu}.
 * <p>
 * Slots: 0 block, 1 item, 2 lapis, 3 enchantable target, 4 preview (display only), then 36 player inventory slots.
 * <p>
 * Custom recipes take priority. If no custom recipe matches,
 * the menu falls back to vanilla enchanting.
 */
public class CustomEnchantMenu extends AbstractContainerMenu {

    private static final Identifier EMPTY_SLOT_LAPIS_LAZULI =
            Identifier.withDefaultNamespace("container/slot/lapis_lazuli");

    public static final int SLOT_BLOCK = 0;
    public static final int SLOT_ITEM = 1;
    public static final int SLOT_LAPIS = 2;
    public static final int SLOT_TARGET = 3;
    public static final int SLOT_PREVIEW = 4;

    private static final int INPUT_END = SLOT_TARGET + 1;
    private static final int INVENTORY_START = SLOT_PREVIEW + 1;
    private static final int INVENTORY_END =
            INVENTORY_START + Inventory.INVENTORY_SIZE;

    // slot layout
    public static final int BLOCK_X = 72;
    public static final int BLOCK_Y = 18;
    public static final int ITEM_X = 100;
    public static final int ITEM_Y = 18;
    public static final int TARGET_X = 86;
    public static final int TARGET_Y = 40;
    public static final int LAPIS_X = 142;
    public static final int LAPIS_Y = 29;
    public static final int PREVIEW_X = 16;
    public static final int PREVIEW_Y = 67;
    public static final int INVENTORY_X = 8;
    public static final int INVENTORY_Y = 96;

    public static final int IMAGE_WIDTH = 176;
    public static final int IMAGE_HEIGHT = INVENTORY_Y + 82;

    private static final boolean BLOCK_SLOT_ONLY_BLOCKS = true;

    private final ContainerLevelAccess access;
    private final Inventory playerInventory;

    private final Container inputs = new SimpleContainer(4) {
        @Override
        public void setChanged() {
            super.setChanged();
            CustomEnchantMenu.this.slotsChanged(this);
        }
    };

    private final Container preview = new SimpleContainer(1);

    private final DataSlot lapisCost = DataSlot.standalone();
    private final DataSlot xpCost = DataSlot.standalone();

    private record VanillaOffer(int index, int cost, List<EnchantmentInstance> enchantments) {
        int lapisRequired() {
            return this.index() + 1;
        }
    }

    public CustomEnchantMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public CustomEnchantMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.CUSTOM_ENCHANT, containerId);

        this.access = access;
        this.playerInventory = inventory;

        this.addSlot(new Slot(this.inputs, SLOT_BLOCK, BLOCK_X, BLOCK_Y) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                return !BLOCK_SLOT_ONLY_BLOCKS || stack.getItem() instanceof BlockItem;
            }
        });

        this.addSlot(new Slot(this.inputs, SLOT_ITEM, ITEM_X, ITEM_Y));

        this.addSlot(new Slot(this.inputs, SLOT_LAPIS, LAPIS_X, LAPIS_Y) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                return stack.is(Items.LAPIS_LAZULI);
            }

            @Override
            public @NonNull Identifier getNoItemIcon() {
                return EMPTY_SLOT_LAPIS_LAZULI;
            }
        });

        this.addSlot(new Slot(this.inputs, SLOT_TARGET, TARGET_X, TARGET_Y) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        this.addSlot(new Slot(this.preview, 0, PREVIEW_X, PREVIEW_Y) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(@NonNull Player player) {
                return false;
            }
        });

        this.addStandardInventorySlots(inventory, INVENTORY_X, INVENTORY_Y);

        this.addDataSlot(this.lapisCost);
        this.addDataSlot(this.xpCost);
    }

    private EnchantingRecipeInput createInput() {
        return new EnchantingRecipeInput(
                this.inputs.getItem(SLOT_BLOCK),
                this.inputs.getItem(SLOT_ITEM),
                this.inputs.getItem(SLOT_LAPIS),
                this.inputs.getItem(SLOT_TARGET)
        );
    }

    private int getBookshelfCount(ServerLevel level, BlockPos pos) {
        int count = 0;

        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            if (EnchantingTableBlock.isValidBookShelf(level, pos, offset)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Generates the enchantments for one vanilla offer, same as vanilla EnchantmentMenu.
     */
    private List<EnchantmentInstance> getVanillaEnchantmentList(ServerLevel level, ItemStack stack, int offerIndex, int cost, int seed) {
        RandomSource random = RandomSource.create();
        random.setSeed(seed + offerIndex);

        Optional<HolderSet.Named<Enchantment>> enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE);

        if (enchantments.isEmpty()) {
            return List.of();
        }

        List<EnchantmentInstance> result = EnchantmentHelper.selectEnchantment(random, stack, cost, enchantments.get().stream());

        if (stack.is(Items.BOOK) && result.size() > 1) {
            result.remove(random.nextInt(result.size()));
        }

        return result;
    }

    private VanillaOffer getBestVanillaOffer(ServerLevel level, BlockPos pos, ItemStack stack, int seed) {
        if (stack.isEmpty() || !stack.isEnchantable()) {
            return null;
        }

        int bookshelfCount = this.getBookshelfCount(level, pos);
        int[] costs = new int[3];

        RandomSource random = RandomSource.create();
        random.setSeed(seed);

        for (int i = 0; i < 3; i++) {
            costs[i] = EnchantmentHelper.getEnchantmentCost(random, i, bookshelfCount, stack);

            if (costs[i] < i + 1) {
                costs[i] = 0;
            }
        }

        for (int i = 2; i >= 0; i--) {
            if (costs[i] == 0) {
                continue;
            }

            List<EnchantmentInstance> enchantments = this.getVanillaEnchantmentList(level, stack, i, costs[i], seed);

            if (!enchantments.isEmpty()) {
                return new VanillaOffer(i, costs[i], enchantments);
            }
        }

        return null;
    }

    private ItemStack createVanillaPreview(ItemStack target, VanillaOffer offer) {
        if (offer == null || target.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = target.copy();

        if (result.is(Items.BOOK)) {
            result = result.transmuteCopy(Items.ENCHANTED_BOOK);
        }

        for (EnchantmentInstance enchantment : offer.enchantments()) {
            result.enchant(enchantment.enchantment(), enchantment.level());
        }

        return result;
    }

    @Override
    public void slotsChanged(@NonNull Container container) {
        if (container != this.inputs) {
            return;
        }

        this.access.execute((level, pos) -> {
            if (!(level instanceof ServerLevel serverLevel)) {
                return;
            }

            EnchantingRecipeInput input = this.createInput();

            Optional<RecipeHolder<EnchantingRecipe>> match = serverLevel.recipeAccess().getRecipeFor(ModRecipes.ENCHANTING_TYPE, input, serverLevel);

            ItemStack target = this.inputs.getItem(SLOT_TARGET);

            VanillaOffer offer = this.getBestVanillaOffer(serverLevel, pos, target, this.playerInventory.player.getEnchantmentSeed());

            if (match.isPresent()) {
                EnchantingRecipe recipe = match.get().value();

                // Reject enchanted books and targets that already have this enchantment.
                if (target.isEmpty()
                        || target.is(Items.ENCHANTED_BOOK)
                        || EnchantmentHelper.getItemEnchantmentLevel(
                        recipe.enchantment(), target
                ) > 0) {
                    this.preview.setItem(0, ItemStack.EMPTY);
                } else {
                    int bookshelfCount = this.getBookshelfCount(serverLevel, pos);

                    int enchantLevel = Math.min(
                            1 + bookshelfCount / 3,
                            recipe.enchantment().value().getMaxLevel()
                    );

                    this.preview.setItem(0, recipe.assemble(input, enchantLevel));
                }
            } else {
                this.preview.setItem(0, this.createVanillaPreview(target, offer));
            }

            if (offer == null) {
                this.lapisCost.set(0);
                this.xpCost.set(0);
            } else {
                this.lapisCost.set(offer.lapisRequired());
                this.xpCost.set(offer.cost());
            }

            this.broadcastChanges();
        });
    }

    @Override
    public boolean clickMenuButton(@NonNull Player player, int id) {
        if (id != 0) {
            return false;
        }

        boolean[] success = {false};

        this.access.execute((level, pos) -> {
            if (!(level instanceof ServerLevel serverLevel)) {
                return;
            }

            EnchantingRecipeInput input = this.createInput();

            Optional<RecipeHolder<EnchantingRecipe>> match = serverLevel.recipeAccess().getRecipeFor(ModRecipes.ENCHANTING_TYPE, input, serverLevel);

            ItemStack target = this.inputs.getItem(SLOT_TARGET);
            int seed = player.getEnchantmentSeed();

            VanillaOffer offer = this.getBestVanillaOffer(serverLevel, pos, target, seed);

            if (offer == null && match.isEmpty()) {
                return;
            }

            int lapisRequired = offer != null ? offer.lapisRequired() : 0;
            int levelsRequired = offer != null ? offer.cost() : 0;
            int levelsSpent = offer != null ? offer.index() + 1 : 0;
            boolean infinite = player.hasInfiniteMaterials();

            if (!infinite) {
                if (this.inputs.getItem(SLOT_LAPIS).getCount() < lapisRequired) {
                    return;
                }

                if (player.experienceLevel < levelsRequired || player.experienceLevel < levelsSpent) return;
            }

            ItemStack result;

            if (match.isPresent()) {
                EnchantingRecipe recipe = match.get().value();

                if (target.isEmpty() || target.is(Items.ENCHANTED_BOOK) || EnchantmentHelper.getItemEnchantmentLevel(recipe.enchantment(), target) > 0) {
                    return;
                }

                int bookshelfCount = this.getBookshelfCount(serverLevel, pos);

                int enchantLevel = Math.min(1 + bookshelfCount / 3, recipe.enchantment().value().getMaxLevel());

                result = recipe.assemble(input, enchantLevel);
            } else {
                result = this.createVanillaPreview(target, offer);
            }

            if (result.isEmpty()) {
                return;
            }

            if (levelsSpent > 0) {
                player.onEnchantmentPerformed(target, levelsRequired);
            }

            if (match.isPresent()) {
                this.consume(SLOT_BLOCK, 1);
                this.consume(SLOT_ITEM, 1);
            }

            if (!infinite && lapisRequired > 0) {
                this.consume(SLOT_LAPIS, lapisRequired);
            }

            this.inputs.setItem(SLOT_TARGET, result);

            player.awardStat(Stats.ENCHANT_ITEM);

            if (player instanceof ServerPlayer serverPlayer && levelsRequired > 0) {
                CriteriaTriggers.ENCHANTED_ITEM.trigger(serverPlayer, result, levelsRequired);
            }

            serverLevel.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, serverLevel.getRandom().nextFloat() * 0.1F + 0.9F);

            this.inputs.setChanged();
            success[0] = true;
        });

        return success[0];
    }

    private void consume(int slot, int count) {
        ItemStack stack = this.inputs.getItem(slot);

        stack.shrink(count);

        if (stack.isEmpty()) {
            this.inputs.setItem(slot, ItemStack.EMPTY);
        }
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int slotIndex) {
        if (slotIndex == SLOT_PREVIEW) {
            return ItemStack.EMPTY;
        }

        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack clicked = stack.copy();

        if (slotIndex < INPUT_END) {
            if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean moved;

            if (stack.is(Items.LAPIS_LAZULI)) {
                moved = this.moveItemStackTo(stack, SLOT_LAPIS, SLOT_LAPIS + 1, false);
            } else {
                moved = stack.getItem() instanceof BlockItem && this.moveItemStackTo(stack, SLOT_BLOCK, SLOT_BLOCK + 1, false);

                if (!moved && !this.slots.get(SLOT_TARGET).hasItem()) {
                    moved = this.moveItemStackTo(stack, SLOT_TARGET, SLOT_TARGET + 1, false);
                }

                if (!moved) {
                    moved = this.moveItemStackTo(stack, SLOT_ITEM, SLOT_ITEM + 1, false);
                }
            }

            if (!moved) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == clicked.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return clicked;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return AbstractContainerMenu.stillValid(
                this.access, player, Blocks.ENCHANTING_TABLE
        );
    }

    @Override
    public void removed(@NonNull Player player) {
        super.removed(player);

        this.access.execute((_, _) -> this.clearContainer(player, this.inputs));
    }

    public ItemStack getPreview() {
        return this.preview.getItem(0);
    }

    public ItemStack getTarget() {
        return this.inputs.getItem(SLOT_TARGET);
    }

    public ItemStack getLapisStack() {
        return this.inputs.getItem(SLOT_LAPIS);
    }

    public int getLapisCost() {
        return this.lapisCost.get();
    }

    public int getXpCost() {
        return this.xpCost.get();
    }
}
