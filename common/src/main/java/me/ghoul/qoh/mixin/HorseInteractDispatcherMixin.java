// This file handles dispatching the horse interact events to the features through qHorse.
package me.ghoul.qoh.mixin;

import me.ghoul.qoh.Constants;
import me.ghoul.qoh.qHorse;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Horse.class)
public class HorseInteractDispatcherMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(
            Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);

        qHorse horse = (qHorse) this;

        Constants.LOG.info("Dispatching mob interact for horse");
        if (horse.dispatchMobInteract(player, stack)) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
