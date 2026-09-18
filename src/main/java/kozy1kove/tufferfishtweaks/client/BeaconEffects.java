package kozy1kove.tufferfishtweaks.client;

import kozy1kove.tufferfishtweaks.CustomBeaconData;
import kozy1kove.tufferfishtweaks.mixin.BeaconBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Map;

public final class BeaconEffects {
    private BeaconEffects() {

    }

    private static int requiredLevelFor(int effectId) {
        return switch (effectId) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
            case 3, 4 -> 4;
            default -> Integer.MAX_VALUE;
        };
    }

    public static boolean hasActiveEffect(Level level, BlockPos pos, int effectId) {
        int requiredLevel = requiredLevelFor(effectId);

        ChunkPos chunkPos = level.getChunk(pos).getPos();
        for (int x = chunkPos.x() - 5; x <= chunkPos.x() + 5; x++) {
            for (int z = chunkPos.z() - 5; z <= chunkPos.z() + 5; z++) {
                LevelChunk chunk = level.getChunk(x, z);
                for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                    if (!(entry.getValue().getBlockState().getBlock() instanceof BeaconBlock)) {
                        continue;
                    }
                    BeaconBlockEntity beaconBlockEntity = (BeaconBlockEntity) entry.getValue();

                    int levels = ((BeaconBlockEntityAccessor) beaconBlockEntity).getLevels();
                    if (levels < requiredLevel) {
                        continue;
                    }

                    boolean enabled = isEnabled(effectId, (CustomBeaconData) beaconBlockEntity);

                    if (enabled) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isEnabled(int effectId, CustomBeaconData beacon) {
        return switch (effectId) {
            case 0 -> beacon.tufferfishtweaks$isTier1Enabled();
            case 1 -> beacon.tufferfishtweaks$isTier2Enabled();
            case 2 -> beacon.tufferfishtweaks$isTier3Enabled();
            case 3 -> beacon.tufferfishtweaks$isPassiveSpawnBlockEnabled();
            case 4 -> beacon.tufferfishtweaks$isHostileSpawnBlockEnabled();
            default -> false;
        };
    }
}