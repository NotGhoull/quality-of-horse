package me.ghoul.qoh.mixin.features;

import me.ghoul.qoh.Constants;
import me.ghoul.qoh.ModConfig;
import me.ghoul.qoh.goals.HorseFollowsOwnerGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Horse.class)
public abstract class HorseFollowsOwner extends AbstractHorse {


    protected HorseFollowsOwner(EntityType<? extends AbstractHorse> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new HorseFollowsOwnerGoal(this, Constants.CONFIG.HorseFollowSpeedMult, Constants.CONFIG.HorseStartsFollowingAt, Constants.CONFIG.HorseStopsFollowingAt));
    }
}
