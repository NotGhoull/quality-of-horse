package me.ghoul.qoh.mixin.features;

import me.ghoul.qoh.Constants;
import me.ghoul.qoh.mixin.accessor.AbstractHorseAccessor;
import me.ghoul.qoh.qHorse;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Horse.class)
public abstract class HorseTwoPlayerRideMixin extends AbstractHorse implements qHorse {


    protected HorseTwoPlayerRideMixin(EntityType<? extends AbstractHorse> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean qoh$tryTwoPlayerRide(Player player, ItemStack stack) {
        if (this.isTamed() && this.getPassengers().size() < 2 && Constants.CONFIG.TwoPlayerRideEnabled) {
            this.doPlayerRide(player);
            return true;
        }

        return false;
    }



    @Override
    protected boolean canAddPassenger(@NotNull Entity pPassenger) {
        if (this.getPassengers().size() <= 2) {
            return true;
        }

        return super.canAddPassenger(pPassenger);
    }

    @Override
    protected @NotNull Vec3 getPassengerAttachmentPoint(@NotNull Entity pEntity, @NotNull EntityDimensions pDimensions, float pPartialTick) {
        int index = this.getPassengers().indexOf(pEntity);
        var standAnimO = ((AbstractHorseAccessor) this).getStandAnim0();

        if (index == 0) {
            return super.getPassengerAttachmentPoint(pEntity, pDimensions, pPartialTick)
                .add(
                    new Vec3(0.0, 0.15 * (double)standAnimO * (double)pPartialTick, -0.7 * (double)standAnimO * (double)pPartialTick)
                        .yRot(-this.getYRot() * (float) (Math.PI / 180.0))
                );
        } else {
            return super.getPassengerAttachmentPoint(pEntity, pDimensions, pPartialTick)
                .add(
                    new Vec3(0, 0.15 * (double)standAnimO * (double)pPartialTick, (double)standAnimO * (double)pPartialTick - 0.5)
                        .yRot(-this.getYRot() * (float) (Math.PI / 180.0))
                );
        }
    }
}
