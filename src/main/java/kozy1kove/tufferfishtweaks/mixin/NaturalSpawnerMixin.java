package kozy1kove.tufferfishtweaks.mixin;

import kozy1kove.tufferfishtweaks.client.BeaconEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {
    @Inject(method = "spawnCategoryForChunk", at = @At("HEAD"), cancellable = true)
    private static void disableSpawning(MobCategory category, ServerLevel level, LevelChunk chunk, NaturalSpawner.SpawnPredicate predicate, NaturalSpawner.AfterSpawnCallback callback, CallbackInfo ci) {
        boolean block;
        if (category.isFriendly()) {
            // block passive mobs
            block = BeaconEffects.hasActiveEffect(level, chunk.getPos().getMiddleBlockPosition(level.getMinY()), 3);
        } else {
            // block hostile mobs
            block = BeaconEffects.hasActiveEffect(level, chunk.getPos().getMiddleBlockPosition(level.getMinY()), 4);
        }

        if (block) {
            ci.cancel();
        }
    }
}