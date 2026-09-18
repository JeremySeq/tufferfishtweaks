package kozy1kove.tufferfishtweaks.mixin;

import kozy1kove.tufferfishtweaks.CustomBeaconData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin implements CustomBeaconData {

    @Unique private boolean tier1Enabled = false;
    @Unique private boolean tier2Enabled = false;
    @Unique private boolean tier3Enabled = false;
    @Unique private boolean hostileSpawnBlockEnabled = false;
    @Unique private boolean passiveSpawnBlockEnabled = false;

    @Override public boolean tufferfishtweaks$isTier1Enabled() {
        return this.tier1Enabled;
    }

    @Override public void tufferfishtweaks$setTier1Enabled(boolean enabled) {
        this.tier1Enabled = enabled;
    }

    @Override public boolean tufferfishtweaks$isTier2Enabled() {
        return this.tier2Enabled;
    }

    @Override public void tufferfishtweaks$setTier2Enabled(boolean enabled) {
        this.tier2Enabled = enabled;
    }

    @Override public boolean tufferfishtweaks$isTier3Enabled() {
        return this.tier3Enabled;
    }

    @Override public void tufferfishtweaks$setTier3Enabled(boolean enabled) {
        this.tier3Enabled = enabled;
    }

    @Override public boolean tufferfishtweaks$isHostileSpawnBlockEnabled() {
        return this.hostileSpawnBlockEnabled;
    }

    @Override public void tufferfishtweaks$setHostileSpawnBlockEnabled(boolean enabled) {
        this.hostileSpawnBlockEnabled = enabled;
    }

    @Override public boolean tufferfishtweaks$isPassiveSpawnBlockEnabled() {
        return this.passiveSpawnBlockEnabled;
    }

    @Override public void tufferfishtweaks$setPassiveSpawnBlockEnabled(boolean enabled) {
        this.passiveSpawnBlockEnabled = enabled;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private static void applyHaste(Level level, BlockPos pos, BlockState selfState, BeaconBlockEntity entity, CallbackInfo ci) {
        if (level.isClientSide()) return;
        applyHaste(level, pos, (CustomBeaconData) entity, ((BeaconBlockEntityAccessor) entity).getLevels());
    }

    @Unique
    private static void applyHaste(Level level, BlockPos pos, CustomBeaconData data, int levels) {
        if (!data.tufferfishtweaks$isTier3Enabled() || levels < 3) {
            return;
        }

        if (level.getGameTime() % 80L != 0L) {
            return;
        }

        double range = levels * 10.0 + 10.0;
        int durationTicks = (9 + levels * 2) * 20;

        AABB bb = new AABB(pos).inflate(range).expandTowards(0.0, level.getHeight(), 0.0);
        for (Player player : level.getEntitiesOfClass(Player.class, bb)) {
            player.addEffect(new MobEffectInstance(MobEffects.HASTE, durationTicks, 0, true, true));
        }
    }

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    private void tufferfishtweaks$save(ValueOutput output, CallbackInfo ci) {
        output.putBoolean(TAG_TIER1, this.tier1Enabled);
        output.putBoolean(TAG_TIER2, this.tier2Enabled);
        output.putBoolean(TAG_TIER3, this.tier3Enabled);
        output.putBoolean(TAG_HOSTILE, this.hostileSpawnBlockEnabled);
        output.putBoolean(TAG_PASSIVE, this.passiveSpawnBlockEnabled);
    }

    @Inject(method = "loadAdditional", at = @At("HEAD"))
    private void tufferfishtweaks$load(ValueInput input, CallbackInfo ci) {
        this.tier1Enabled = input.getBooleanOr(TAG_TIER1, false);
        this.tier2Enabled = input.getBooleanOr(TAG_TIER2, false);
        this.tier3Enabled = input.getBooleanOr(TAG_TIER3, false);
        this.hostileSpawnBlockEnabled = input.getBooleanOr(TAG_HOSTILE, false);
        this.passiveSpawnBlockEnabled = input.getBooleanOr(TAG_PASSIVE, false);
    }
}