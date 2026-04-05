package me.ghoul.qoh.mixin.features.boatdragging;

import me.ghoul.qoh.Constants;
import me.ghoul.qoh.interfaces.ILeashHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Horse.class)
public abstract class HorsesDragBoats extends Animal implements Leashable {

    protected HorsesDragBoats(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void setLeashedTo(Entity pLeashHolder, boolean pBroadcastPacket) {
        if (!(pLeashHolder instanceof Player)) {
            super.setLeashedTo(pLeashHolder, pBroadcastPacket);
            return;
        }

        ILeashHolder data = (ILeashHolder) pLeashHolder;

        data.setLeashTargetEntity(this);

        super.setLeashedTo(pLeashHolder, pBroadcastPacket);
    }
}
