package kozy1kove.tufferfishtweaks.enchanting;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.EnchantingTableBlock;

/**
 * Open CustomEnchantMenu when interacting with enchanting table
 */
public final class EnchantingTableOverride {

    private EnchantingTableOverride() {
    }

    public static void init() {
        UseBlockCallback.EVENT.register((player, level, _, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            if (!(level.getBlockState(pos).getBlock() instanceof EnchantingTableBlock)) {
                return InteractionResult.PASS;
            }
            if (player.isSpectator()) {
                return InteractionResult.PASS;
            }

            boolean holdingSomething = !player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty();
            if (player.isSecondaryUseActive() && holdingSomething) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide()) {
                Component name = level.getBlockEntity(pos) instanceof Nameable nameable ? nameable.getDisplayName() : Component.translatable("container.enchant");

                player.openMenu(new SimpleMenuProvider((containerId, inventory, p) -> new CustomEnchantMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), name));
            }

            return InteractionResult.SUCCESS;
        });
    }
}
