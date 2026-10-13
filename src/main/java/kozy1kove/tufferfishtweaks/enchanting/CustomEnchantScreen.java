package kozy1kove.tufferfishtweaks.client;

import java.util.List;
import kozy1kove.tufferfishtweaks.enchanting.CustomEnchantMenu;
import kozy1kove.tufferfishtweaks.enchanting.EnchantingRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jspecify.annotations.Nullable;


/**
 * Replacement Enchantment Screen
 * Much copied from {@link net.minecraft.client.gui.screens.inventory.EnchantmentScreen}.
 */
@Environment(EnvType.CLIENT)
public class CustomEnchantScreen extends AbstractContainerScreen<CustomEnchantMenu> {

    private static final Identifier ENCHANTING_BOOK_LOCATION =
            Identifier.withDefaultNamespace("textures/entity/enchantment/enchanting_table_book.png");

    private static final int BOOK_X0 = 20;
    private static final int BOOK_Y0 = 22;
    private static final int BOOK_X1 = BOOK_X0 + 38;
    private static final int BOOK_Y1 = BOOK_Y0 + 31;

    private static final int BUTTON_X = 12;
    private static final int BUTTON_Y = 62;
    private static final int BUTTON_W = 152;
    private static final int BUTTON_H = 26;

    private static final int TEXT_DARK = 0xFF404040;
    private static final int TEXT_WHITE = 0xFFFFFFFF;
    private static final int TEXT_GRAY = 0xFFA0A0A0;

    private Button enchantButton;

    private final RandomSource random = RandomSource.create();
    private BookModel bookModel;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;

    public CustomEnchantScreen(CustomEnchantMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, CustomEnchantMenu.IMAGE_WIDTH, CustomEnchantMenu.IMAGE_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();

        this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));

        this.enchantButton = Button.builder(Component.empty(), _ ->
                {
                    assert this.minecraft.gameMode != null;
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                })
                .bounds(this.leftPos + BUTTON_X, this.topPos + BUTTON_Y, BUTTON_W, BUTTON_H)
                .build();
        this.enchantButton.active = false;
        this.addRenderableWidget(this.enchantButton);
    }

    @Override
    public void containerTick() {
        super.containerTick();

        assert this.minecraft.player != null;
        this.minecraft.player.experienceDisplayStartTick = this.minecraft.player.tickCount;

        this.tickBook();
        if (this.enchantButton != null) {
            this.enchantButton.active = this.canEnchant();
        }
    }

    private boolean hasOption() {
        return !this.menu.getPreview().isEmpty();
    }

    private boolean hasLapis() {
        return this.menu.getLapisStack().getCount() >= this.menu.getLapisCost();
    }

    private boolean hasLevels() {
        Player player = this.minecraft.player;
        return player != null && (player.hasInfiniteMaterials() || player.experienceLevel >= this.menu.getXpCost());
    }

    private boolean canEnchant() {
        return this.hasOption() && this.hasLapis() && this.hasLevels();
    }

    /**
     * gets the enchantment name added
     */
    private @Nullable Component optionName() {
        ItemStack preview = this.menu.getPreview();
        if (preview.isEmpty()) {
            return null;
        }

        ItemStack target = this.menu.getTarget();
        ItemEnchantments after = preview.getOrDefault(EnchantingRecipe.enchantComponent(preview), ItemEnchantments.EMPTY);
        ItemEnchantments before = target.getOrDefault(EnchantingRecipe.enchantComponent(target), ItemEnchantments.EMPTY);

        for (Holder<Enchantment> holder : after.keySet()) {
            int level = after.getLevel(holder);
            if (level != before.getLevel(holder)) {
                return Enchantment.getFullname(holder, level);
            }
        }
        return null;
    }

    private void tickBook() {
        ItemStack current = this.menu.getTarget();
        if (!ItemStack.matches(current, this.last)) {
            this.last = current;
            do {
                this.flipT += (float) (this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.flip <= this.flipT + 1.0f && this.flip >= this.flipT - 1.0f);
        }

        this.oFlip = this.flip;
        this.oOpen = this.open;

        boolean shouldBeOpen = this.hasOption();
        this.open = shouldBeOpen ? this.open + 0.2f : this.open - 0.2f;
        this.open = Mth.clamp(this.open, 0.0f, 1.0f);

        float diff = (this.flipT - this.flip) * 0.4f;
        diff = Mth.clamp(diff, -0.2f, 0.2f);
        this.flipA += (diff - this.flipA) * 0.9f;
        this.flip += this.flipA;
    }

    private void extractBook(GuiGraphicsExtractor graphics, int left, int top) {
        float partial = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float openLerp = Mth.lerp(partial, this.oOpen, this.open);
        float flipLerp = Mth.lerp(partial, this.oFlip, this.flip);
        graphics.book(this.bookModel, ENCHANTING_BOOK_LOCATION, 40.0f, openLerp, flipLerp, left + BOOK_X0, top + BOOK_Y0, left + BOOK_X1, top + BOOK_Y1);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        int x = this.leftPos;
        int y = this.topPos;
        int w = this.imageWidth;
        int h = this.imageHeight;

        graphics.fill(x, y, x + w, y + h, 0xFFC6C6C6);
        graphics.fill(x, y, x + w, y + 2, 0xFFFFFFFF);
        graphics.fill(x, y, x + 2, y + h, 0xFFFFFFFF);
        graphics.fill(x, y + h - 2, x + w, y + h, 0xFF555555);
        graphics.fill(x + w - 2, y, x + w, y + h, 0xFF555555);
        graphics.outline(x - 1, y - 1, w + 2, h + 2, 0xFF000000);

        for (int i = 0; i < this.menu.slots.size(); i++) {
            if (i == CustomEnchantMenu.SLOT_PREVIEW) {
                continue;
            }
            Slot slot = this.menu.slots.get(i);
            drawSlotBox(graphics, x + slot.x - 1, y + slot.y - 1);
        }

        this.extractBook(graphics, x, y);
    }

    private static void drawSlotBox(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, 0xFF8B8B8B); // inside
        graphics.fill(x, y, x + 17, y + 1, 0xFF373737); // top shadow
        graphics.fill(x, y, x + 1, y + 17, 0xFF373737); // left shadow
        graphics.fill(x + 1, y + 17, x + 18, y + 18, 0xFFFFFFFF); // bottom highlight
        graphics.fill(x + 17, y + 1, x + 18, y + 18, 0xFFFFFFFF); // right highlight
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, 8, 6, TEXT_DARK, false);

        int topRowCenterY = CustomEnchantMenu.BLOCK_Y + 4;
        int plusBetweenTop = (CustomEnchantMenu.BLOCK_X + 16 + CustomEnchantMenu.ITEM_X) / 2;
        this.centered(graphics, Component.literal("+"), plusBetweenTop, topRowCenterY, TEXT_DARK, false);

        if (!this.hasOption()) {
            this.centered(graphics, Component.literal("Insert ingredients"), BUTTON_X + BUTTON_W / 2, BUTTON_Y + 9, TEXT_GRAY, true);
            return;
        }

        Component name = this.optionName();
        if (name != null) {
            this.centered(graphics, name, 99, BUTTON_Y + 4, TEXT_WHITE, true);
        }

        int lapisCost = this.menu.getLapisCost();
        int xpCost = this.menu.getXpCost();

        MutableComponent cost = Component.literal(lapisCost + " Lapis").withStyle(this.hasLapis() ? ChatFormatting.GREEN : ChatFormatting.RED);
        if (xpCost > 0) {
            cost.append(Component.literal("   "));
            cost.append(Component.literal(xpCost + (xpCost == 1 ? " Level" : " Levels")).withStyle(this.hasLevels() ? ChatFormatting.GREEN : ChatFormatting.RED));
        }
        this.centered(graphics, cost, 99, BUTTON_Y + 14, TEXT_WHITE, true);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float ignored) {
        float a = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        super.extractRenderState(graphics, mouseX, mouseY, a);

        for (int i = CustomEnchantMenu.SLOT_BLOCK; i <= CustomEnchantMenu.SLOT_TARGET; i++) {
            Slot slot = this.menu.slots.get(i);
            if (slot.hasItem() || !this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                continue;
            }

            String hint = switch (i) {
                case CustomEnchantMenu.SLOT_BLOCK -> "Block";
                case CustomEnchantMenu.SLOT_ITEM -> "Item";
                case CustomEnchantMenu.SLOT_LAPIS -> "Lapis Lazuli";
                default -> "Item to enchant";
            };
            graphics.setComponentTooltipForNextFrame(this.font, List.of(Component.literal(hint)), mouseX, mouseY);
            break;
        }
    }

    private void centered(GuiGraphicsExtractor graphics, Component text, int centerX, int y, int color, boolean shadow) {
        graphics.text(this.font, text, centerX - this.font.width(text) / 2, y, color, shadow);
    }
}