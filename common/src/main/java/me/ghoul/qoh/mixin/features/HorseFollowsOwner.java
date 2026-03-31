package me.ghoul.qoh.mixin.features;

import me.ghoul.qoh.goals.HorseFollowsOwnerGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Horse.class)
public abstract class HorseFollowsOwner extends AbstractHorse {
    private static final boolean ENABLED = true;


    protected HorseFollowsOwner(EntityType<? extends AbstractHorse> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        if (!ENABLED) {
            return;
        }

        this.goalSelector.addGoal(0, new HorseFollowsOwnerGoal(this, 1.2F, 10.0F, 2.0F));
    }
}
