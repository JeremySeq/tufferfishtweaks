package kozy1kove.tufferfishtweaks.mixin;

import kozy1kove.tufferfishtweaks.CustomBeaconData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Used for beacon tier 2: increase crop growth speed
 */
@Mixin(BeaconBlockEntity.class)
public abstract class BeaconExtraRandomTickMixin {

    @Unique
    private static final float CHANCE = 0.25f; // chance of extra random tick per tick

    @Unique
    private static final int RANGE = 15;

    @Inject(method = "tick", at = @At("TAIL"))
    private static void tufferfishtweaks$extraRandomTicks(Level level, BlockPos pos, BlockState selfState, BeaconBlockEntity entity, CallbackInfo ci) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        CustomBeaconData data = (CustomBeaconData) entity;
        if (!data.tufferfishtweaks$isTier2Enabled()) {
            return;
        }

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -RANGE; dx <= RANGE; dx++) {
            for (int dz = -RANGE; dz <= RANGE; dz++) {
                for (int dy = -4; dy <= 4; dy++) {
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    BlockState state = level.getBlockState(cursor);
                    if (state.getBlock() instanceof CropBlock && state.isRandomlyTicking()) {
                        if (serverLevel.getRandom().nextFloat() < CHANCE) {
                            state.randomTick(serverLevel, cursor, serverLevel.getRandom());
                        }
                    }
                }
            }
        }
    }
}