package kozy1kove.tufferfishtweaks.mixin;

import kozy1kove.tufferfishtweaks.network.BeaconOpenS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconMenuOpenNotifyMixin {

    @Inject(method = "createMenu", at = @At("RETURN"))
    private void tufferfishtweaks$notifyPos(int containerId, Inventory inventory, Player player, CallbackInfoReturnable<AbstractContainerMenu> cir) {
        if (cir.getReturnValue() != null && player instanceof ServerPlayer serverPlayer) {
            BeaconBlockEntity self = (BeaconBlockEntity) (Object) this;
            ServerPlayNetworking.send(serverPlayer, new BeaconOpenS2CPayload(self.getBlockPos()));
        }
    }
}
