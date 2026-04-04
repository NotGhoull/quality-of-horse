package me.ghoul.qoh.mixin.features.boatdragging;

import me.ghoul.qoh.interfaces.ILeashHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class PlayerLeashMixin implements ILeashHolder {
    @Unique Entity target;

    @Override
    public void setLeashTargetEntity(Entity entity) {
        target = entity;
    }

    @Override
    public Entity getLeashTargetEntity() {
        return target;
    }

    @Override
    public void invalidateTargetEntity() {
        target = null;
    }
}
