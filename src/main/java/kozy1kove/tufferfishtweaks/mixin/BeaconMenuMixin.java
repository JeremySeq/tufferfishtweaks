package kozy1kove.tufferfishtweaks.mixin;

import net.minecraft.world.inventory.BeaconMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeaconMenu.class)
public class BeaconMenuMixin {

    // disable payment requirement
    @Inject(at = @At("HEAD"), method = "hasPayment", cancellable = true)
    private void hasPayment(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    // disable server side payment check
    @Redirect(method = "updateEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/BeaconMenu$PaymentSlot;hasItem()Z"))
    private boolean tufferfishtweaks$skipPaymentCheck(BeaconMenu.PaymentSlot instance) {
        return true;
    }
}
