package me.ghoul.qoh.interfaces;

import net.minecraft.world.entity.Entity;

public interface ILeashHolder {
    void setLeashTargetEntity(Entity entity);

    void invalidateTargetEntity();

    Entity getLeashTargetEntity();
}
