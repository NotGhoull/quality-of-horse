// This file handles dispatching the horse interact events to the features through qHorse.
package me.ghoul.qoh.mixin;

import me.ghoul.qoh.interfaces.IHorseFeature;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Horse.class)
public abstract class HorseMultiEventDispatcherMixin extends AbstractHorse {

    protected HorseMultiEventDispatcherMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }
    
    @Override
    public void leashTooFarBehaviour() {
        Entity leashHolder = this.getOwner();

        if (leashHolder == null) {
            return;
        }
        double dx = leashHolder.getX() - this.getX();
        double dy = leashHolder.getY() - this.getY();
        double dz = leashHolder.getZ() - this.getZ();
        double distanceSquared = dx * dx + dy * dy + dz * dz;

        double maxDistance = 25.0;

        if (distanceSquared > maxDistance * maxDistance) {
            this.setPos(leashHolder.getX(), leashHolder.getY(), leashHolder.getZ());
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isVehicle() && !this.isBaby()) {
            if (!stack.isEmpty()) {
                if (this.isFood(stack)) {
                    cir.setReturnValue(this.fedFood(player, stack));
                    return;
                }

                if (!this.isTamed()) {
                    this.makeMad();
                    cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide));
                    return;
                }

                IHorseFeature horse = (IHorseFeature) this;
                InteractionResult chestResult = horse.qoh$tryChestInteraction(player, stack);
                if (chestResult != InteractionResult.PASS) {
                    cir.setReturnValue(chestResult);
                    return;
                }
            } else {
                if (player.isCrouching()) {
                    IHorseFeature horse = (IHorseFeature) this;
                    InteractionResult leadResult = horse.qoh$tryCreateLead(player, stack);
                    if (leadResult != InteractionResult.PASS) {
                        cir.setReturnValue(leadResult);
                        return;
                    }
                }
            }

            IHorseFeature horse = (IHorseFeature) this;
            if (horse.qoh$tryTwoPlayerRide(player, stack)) {
                cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide));
            }
        }

    }
}
