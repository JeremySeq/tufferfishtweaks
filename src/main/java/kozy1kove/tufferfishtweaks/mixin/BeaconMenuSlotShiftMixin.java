package kozy1kove.tufferfishtweaks.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Removes the beacon menu's payment slot and shifts the player inventory to match the new screen.
 */
@Mixin(BeaconMenu.class)
public abstract class BeaconMenuSlotShiftMixin {

    @Unique
    private static final int Y_SHIFT = -22;

    @Inject(
            method = "<init>(ILnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
            at = @At("TAIL")
    )
    private void tufferfishtweaks$shiftSlots(
            int containerId, Container inventory, ContainerData beaconData, ContainerLevelAccess access, CallbackInfo ci
    ) {
        AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;

        if (!self.slots.isEmpty()) {
            self.slots.removeFirst();
        }

        for (int i = 0; i < self.slots.size(); i++) {
            Slot slot = self.slots.get(i);
            SlotAccessor accessor = (SlotAccessor) slot;
            accessor.setY(slot.y + Y_SHIFT);
            accessor.setIndex(i);
        }
    }
}