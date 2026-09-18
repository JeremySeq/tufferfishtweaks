package kozy1kove.tufferfishtweaks.mixin;

import kozy1kove.tufferfishtweaks.client.BeaconEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PotionContents.class)
public abstract class PotionDurationMixin {

    @Unique
    private static final double DURATION_MULTIPLIER = 1.5;

    @Redirect(
            method = "lambda$applyToLivingEntity$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
            )
    )
    private static boolean tufferfishtweaks$extendDuration(LivingEntity entity, MobEffectInstance effect) {
        if (entity.level().isClientSide() || effect.getDuration() <= 0) {
            return entity.addEffect(effect);
        }

        boolean inRange = BeaconEffects.hasActiveEffect(entity.level(), entity.blockPosition(), 0);
        if (!inRange) {
            return entity.addEffect(effect);
        }

        int newDuration = (int) Math.round(effect.getDuration() * DURATION_MULTIPLIER);
        MobEffectInstance extended = new MobEffectInstance(effect.getEffect(), newDuration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon());
        return entity.addEffect(extended);
    }
}
