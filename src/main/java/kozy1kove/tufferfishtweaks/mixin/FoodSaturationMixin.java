package kozy1kove.tufferfishtweaks.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodSaturationMixin {

    @Shadow //shadow lets the mixin use an existing thing in the target class
    private int foodLevel;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void noHungerTick(ServerPlayer player, CallbackInfo ci) {
        this.foodLevel = 19;
        ci.cancel();
    }
}


