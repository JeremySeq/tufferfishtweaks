package kozy1kove.tufferfishtweaks.client;

import com.google.common.collect.Lists;
import kozy1kove.tufferfishtweaks.CustomBeaconData;
import kozy1kove.tufferfishtweaks.TufferfishTweaks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Replacement for {@link net.minecraft.client.gui.screens.inventory.BeaconScreen}
 */
@Environment(EnvType.CLIENT)
public class TieredBeaconScreen extends AbstractContainerScreen<BeaconMenu> {

    private static final Identifier BEACON_LOCATION = Identifier.fromNamespaceAndPath(TufferfishTweaks.MOD_ID, "textures/gui/beacon.png");
    private static final Identifier BUTTON_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/beacon/button_disabled");
    private static final Identifier BUTTON_SELECTED_SPRITE = Identifier.withDefaultNamespace("container/beacon/button_selected");
    private static final Identifier BUTTON_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("container/beacon/button_highlighted");
    private static final Identifier BUTTON_SPRITE = Identifier.withDefaultNamespace("container/beacon/button");
    private static final Identifier CONFIRM_SPRITE = Identifier.withDefaultNamespace("container/beacon/confirm");
    private static final Identifier CANCEL_SPRITE = Identifier.withDefaultNamespace("container/beacon/cancel");

    private static final ItemStack COW_HEAD;
    static {
        COW_HEAD = new ItemStack(Items.PLAYER_HEAD);
        COW_HEAD.set(DataComponents.PROFILE, ResolvableProfile.createUnresolved("MHF_Cow"));
    }

    private static final List<Predicate<CustomBeaconData>> EFFECT_GETTERS = new ArrayList<>();
    static {
        EFFECT_GETTERS.add(CustomBeaconData::tufferfishtweaks$isTier1Enabled);
        EFFECT_GETTERS.add(CustomBeaconData::tufferfishtweaks$isTier2Enabled);
        EFFECT_GETTERS.add(CustomBeaconData::tufferfishtweaks$isTier3Enabled);
        EFFECT_GETTERS.add(CustomBeaconData::tufferfishtweaks$isPassiveSpawnBlockEnabled);
        EFFECT_GETTERS.add(CustomBeaconData::tufferfishtweaks$isHostileSpawnBlockEnabled);
    }

    private static final ItemStack[] EFFECT_ITEMS = new ItemStack[]{
            new ItemStack(Items.GLASS_BOTTLE),
            new ItemStack(Items.WHEAT),
            new ItemStack(Items.GOLDEN_PICKAXE),
            COW_HEAD,
            new ItemStack(Items.ZOMBIE_HEAD),
    };

    private static final String[] EFFECT_DESCRIPTIONS = new String[]{
            "Increase potion duration",
            "Increase crop growth",
            "Haste 1",
            "Block passive mob spawns",
            "Block hostile mob spawns"
    };

    private final List<BeaconButton> beaconButtons = Lists.newArrayList();

    private @Nullable BlockPos beaconPos;

    public TieredBeaconScreen(BeaconMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 230, 197);
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int xm, int ym) {

    }

    @Override
    protected void init() {
        super.init();
        this.beaconPos = OpenBeaconTracker.get();

        this.beaconButtons.clear();

        int y = 54;
        int buttonWidth = 22;

        int effectIndex = 0;
        for(int tier = 0; tier <= 2; ++tier) {

            BeaconPowerButton beaconPowerButton = new BeaconPowerButton(this.leftPos + 32 + tier * 39 - buttonWidth/2, this.topPos + y,
                    Component.literal(EFFECT_DESCRIPTIONS[effectIndex]), tier, effectIndex, EFFECT_GETTERS.get(effectIndex));
            beaconPowerButton.active = false;
            this.addBeaconButton(beaconPowerButton);

            effectIndex++;
        }

        for(int c = 0; c < 2; ++c) {
            BeaconPowerButton beaconPowerButton = new BeaconPowerButton(this.leftPos + 175 - buttonWidth/2 + (c == 0 ? -1 : 1) * (15), this.topPos + y,
                    Component.literal(EFFECT_DESCRIPTIONS[effectIndex]), 3, effectIndex, EFFECT_GETTERS.get(effectIndex));
            beaconPowerButton.active = false;
            this.addBeaconButton(beaconPowerButton);
            effectIndex++;
        }
    }

    private void addToggle(int x, int y, int width, int height, int toggleIndex, String labelText, Predicate<CustomBeaconData> getter) {
        boolean initial = getBeaconData().map(getter::test).orElse(true);

        Button button = Button.builder(label(labelText, initial), b -> {
                            if (this.beaconPos != null) {
                                ClientNetworking.sendToggle(this.beaconPos, toggleIndex);
                            }
                            boolean current = getBeaconData().map(getter::test).orElse(true);
                            b.setMessage(label(labelText, !current));
                        })
                .bounds(x, y, width, height)
                .build();

        this.addRenderableWidget(button);
    }

    private void toggleEffect(int effectIndex) {
        ClientNetworking.sendToggle(this.beaconPos, effectIndex);
    }

    private Optional<CustomBeaconData> getBeaconData() {
        if (this.beaconPos == null || this.minecraft.level == null) {
            return Optional.empty();
        }
        if (this.minecraft.level.getBlockEntity(this.beaconPos) instanceof BeaconBlockEntity beacon) {
            return Optional.of((CustomBeaconData) beacon);
        }
        return Optional.empty();
    }

    private static Component label(String text, boolean enabled) {
        return Component.literal(text + ": " + (enabled ? "ON" : "OFF"));
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, BEACON_LOCATION, xo, yo, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256, -1);
    }

    @Override
    public void removed() {
        super.removed();
        OpenBeaconTracker.clear();
    }

    public void containerTick() {
        super.containerTick();
        this.updateButtons();
    }

    private <T extends AbstractWidget & BeaconButton> void addBeaconButton(final T beaconButton) {
        this.addRenderableWidget(beaconButton);
        this.beaconButtons.add(beaconButton);
    }

    private void updateButtons() {
        int levels = ((BeaconMenu)this.menu).getLevels();
        this.beaconButtons.forEach((b) -> b.updateStatus(levels));
    }

    @Environment(value=EnvType.CLIENT)
    private static interface BeaconButton {
        public void updateStatus(int var1);
    }

    @Environment(EnvType.CLIENT)
    private abstract static class BeaconScreenButton extends AbstractButton implements BeaconButton {
        private boolean selected;

        protected BeaconScreenButton(final int x, final int y) {
            super(x, y, 22, 22, CommonComponents.EMPTY);
        }

        protected BeaconScreenButton(final int x, final int y, final Component component) {
            super(x, y, 22, 22, component);
        }

        public void extractContents(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
            Identifier sprite;
            if (!this.active) {
                sprite = BUTTON_DISABLED_SPRITE;
            } else if (this.selected) {
                sprite = BUTTON_SELECTED_SPRITE;
            } else if (this.isHovered()) {
                sprite = BUTTON_HIGHLIGHTED_SPRITE;
            } else {
                sprite = BUTTON_SPRITE;
            }

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.getX(), this.getY(), this.width, this.height);
            this.extractIcon(graphics);
        }

        protected abstract void extractIcon(final GuiGraphicsExtractor graphics);

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(final boolean selected) {
            this.selected = selected;
        }

        public void updateWidgetNarration(final @NonNull NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }
    }

    @Environment(EnvType.CLIENT)
    private class BeaconPowerButton extends BeaconScreenButton {
        protected final int tier;
        protected final int effectIndex;
        private final Predicate<CustomBeaconData> enabledGetter;

        public BeaconPowerButton(final int x, final int y, MutableComponent description, final int tier, int effectIndex, Predicate<CustomBeaconData> enabledGetter) {
            super(x, y);
            this.tier = tier;
            this.setTooltip(Tooltip.create(description, null));
            this.effectIndex = effectIndex;
            this.enabledGetter = enabledGetter;
        }

        public void onPress(final @NonNull InputWithModifiers input) {
            TieredBeaconScreen.this.toggleEffect(this.effectIndex);
            TieredBeaconScreen.this.updateButtons();
        }

        protected void extractIcon(final GuiGraphicsExtractor graphics) {
//            if (this.tier == 3) {
//                graphics.item(this.effectIndex-tier == 0 ? COW_HEAD : ZOMBIE_HEAD, this.getX() + 2, this.getY() + 2);
//            } else {
//                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX() + 2, this.getY() + 2, 18, 18);
//            }

            graphics.item(EFFECT_ITEMS[effectIndex], this.getX() + 3, this.getY() + 3);
        }

        public void updateStatus(final int levels) {
            this.active = this.tier < levels;

            boolean selected = getBeaconData()
                    .map(this.enabledGetter::test)
                    .orElse(false);

            this.setSelected(selected);
        }
    }
}