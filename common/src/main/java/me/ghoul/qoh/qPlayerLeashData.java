package me.ghoul.qoh;

import net.minecraft.world.entity.Entity;

public interface qPlayerLeashData {
    void setLeashTargetEntity(Entity entity);

    void invalidateTargetEntity();

    Entity getLeashTargetEntity();
}
