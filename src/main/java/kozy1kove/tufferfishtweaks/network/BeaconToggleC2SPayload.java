package kozy1kove.tufferfishtweaks.network;

import kozy1kove.tufferfishtweaks.TufferfishTweaks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;


/**
 * Beacon toggles from screen on client to server.
 */
public record BeaconToggleC2SPayload(BlockPos beaconPos, int toggle) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BeaconToggleC2SPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(TufferfishTweaks.MOD_ID, "beacon_toggle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BeaconToggleC2SPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BeaconToggleC2SPayload::beaconPos,
            net.minecraft.network.codec.ByteBufCodecs.VAR_INT, BeaconToggleC2SPayload::toggle,
            BeaconToggleC2SPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}