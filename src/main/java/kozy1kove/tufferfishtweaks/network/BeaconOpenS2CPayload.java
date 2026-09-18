package kozy1kove.tufferfishtweaks.network;

import io.netty.buffer.ByteBuf;
import kozy1kove.tufferfishtweaks.CustomBeaconData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jspecify.annotations.NonNull;

/**
 * Used to inform client screen of block entity position.
 */
public record BeaconOpenS2CPayload(BlockPos beaconPos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BeaconOpenS2CPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("tufferfishtweaks", "beacon_open"));

    public static final StreamCodec<ByteBuf, BeaconOpenS2CPayload> CODEC = BlockPos.STREAM_CODEC.map(BeaconOpenS2CPayload::new, BeaconOpenS2CPayload::beaconPos);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerPlayer player, BlockPos pos, int toggle) {
        Level level = player.level();

        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64 * 64) {
            return;
        }

        if (!(level.getBlockEntity(pos) instanceof BeaconBlockEntity beacon)) {
            return;
        }

        CustomBeaconData data = (CustomBeaconData) beacon;
        switch (toggle) {
            case 0 -> data.tufferfishtweaks$setTier1Enabled(!data.tufferfishtweaks$isTier1Enabled());
            case 1 -> data.tufferfishtweaks$setTier2Enabled(!data.tufferfishtweaks$isTier2Enabled());
            case 2 -> data.tufferfishtweaks$setTier3Enabled(!data.tufferfishtweaks$isTier3Enabled());
            case 3 -> data.tufferfishtweaks$setPassiveSpawnBlockEnabled(!data.tufferfishtweaks$isPassiveSpawnBlockEnabled());
            case 4 -> data.tufferfishtweaks$setHostileSpawnBlockEnabled(!data.tufferfishtweaks$isHostileSpawnBlockEnabled());
            default -> { return; }
        }

        beacon.setChanged();
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
    }
}