package me.ghoul.qoh.mixin.features;

import me.ghoul.qoh.Constants;
import me.ghoul.qoh.attributes.ModAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RunAroundLikeCrazyGoal.class)
public abstract class HorseTamingStatMixin extends Goal {

    @Shadow
    @Final
    private AbstractHorse horse;

    @ModifyVariable(method = "tick",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ), name = "i")
    private int modifyTemper(int i) {
        Entity entity = this.horse.getFirstPassenger();
        if (entity instanceof Player player) {
            int tamingBonus = 0;
            try {
                Constants.LOG.info("Trying to get attribute from player");
                tamingBonus = (int) player.getAttributeValue(ModAttributes.HORSE_TAMING);
                Constants.LOG.info("Got attribute value: {}", tamingBonus);
            } catch (IllegalArgumentException e) {
                Constants.LOG.error("Failed to get attribute from player, defaulting to 0");
            }
            return i + tamingBonus;
        }
        return i;
    }


}
