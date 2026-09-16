package kozy1kove.tufferfishtweaks.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// for some reason doesnt work for last item in stack???

@Mixin(Consumable.class)
public class FoodHealMixin {
    @Inject(method = "onConsume", at = @At("HEAD"))
    private void healOnEat(Level level, LivingEntity user, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        FoodProperties foodProperties = stack.get(DataComponents.FOOD);
        if (foodProperties != null) {
            float nutritionHalf = foodProperties.nutrition() * 0.5f;    // hunger bit of heal
            float saturationHalf = foodProperties.saturation() * 0.5f; // saturation bit of heal
            float healAmount = (nutritionHalf + saturationHalf) / 2.0f; // total stuff divided by 2
            user.heal(healAmount);
        }
    }
}
