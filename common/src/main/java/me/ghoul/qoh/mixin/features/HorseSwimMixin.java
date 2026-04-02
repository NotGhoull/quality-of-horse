package me.ghoul.qoh.mixin.features;

import me.ghoul.qoh.Constants;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public abstract class HorseSwimMixin extends LivingEntity {

    protected HorseSwimMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Unique private int qoh$swimTickCount = 0;

    @Inject(method = "tickRidden", at = @At(value = "TAIL"))
    private void handleSwimming(Player player, Vec3 movement, CallbackInfo ci) {
        if (!Constants.CONFIG.CanHorsesSwim) {
            return;
        }

        double fluidHeight;
        if (this.isInLava()) {
            fluidHeight = getFluidHeight(FluidTags.LAVA);
        } else {
            fluidHeight = getFluidHeight(FluidTags.WATER);
        }

        if (fluidHeight > 0.4 || qoh$swimTickCount > 0) {
            qoh$swimTickCount++;

            if (isInWater()) {
                setDeltaMovement(
                        getDeltaMovement().add(0., (fluidHeight > 0.8 ? 0.04 : 0.01), 0.0));
            } else if (isInLava()) {
                setDeltaMovement(
                        getDeltaMovement().add(0., (fluidHeight > 0.8 ? 0.11 : 0.0275), 0.0));
            } else {
                qoh$swimTickCount = 0;
            }

            if (qoh$swimTickCount == 30) {
                qoh$swimTickCount = 0;
            }
        }
    }

    @Override
    public boolean dismountsUnderwater() {
        return false;
    }
}
