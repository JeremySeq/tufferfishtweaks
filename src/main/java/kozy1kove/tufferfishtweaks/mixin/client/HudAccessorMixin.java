package kozy1kove.tufferfishtweaks.mixin.client;

import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// ideally i want to flip around the armor bar to work right to left maybe

@Mixin(Hud.class)
public interface HudAccessorMixin {
    @Accessor("ARMOR_FULL_SPRITE")
    static Identifier getArmorFullSprite() { throw new AssertionError(); }

    @Accessor("ARMOR_HALF_SPRITE")
    static Identifier getArmorHalfSprite() { throw new AssertionError(); }

    @Accessor("ARMOR_EMPTY_SPRITE")
    static Identifier getArmorEmptySprite() { throw new AssertionError(); }
}