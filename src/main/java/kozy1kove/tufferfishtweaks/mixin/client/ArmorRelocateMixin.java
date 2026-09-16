package kozy1kove.tufferfishtweaks.mixin.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Hud.class)
public class ArmorRelocateMixin {

    @Redirect(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractArmor(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;IIII)V"))
    private static void relocateArmor(GuiGraphicsExtractor graphics, Player player, int yLineBase, int numHealthRows, int healthRowHeight, int xLeft) {
        int armor = player.getArmorValue();
        if (armor > 0) {
            int xRight = graphics.guiWidth() / 2 + 91;
            int yo = yLineBase; // ts used to be way higher it should be same as food now

            for (int i = 0; i < 10; i++) {
                int xo = xRight - i * 8 - 9;
                int armorIndex = 9 - i; // maybe remove this later depending on testing

                if (armorIndex * 2 + 1 < armor) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HudAccessorMixin.getArmorFullSprite(), xo, yo, 9, 9);
                }
                if (armorIndex * 2 + 1 == armor) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HudAccessorMixin.getArmorHalfSprite(), xo, yo, 9, 9);
                }
                if (armorIndex * 2 + 1 > armor) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HudAccessorMixin.getArmorEmptySprite(), xo, yo, 9, 9);
                }
            }
        }
    }
}
