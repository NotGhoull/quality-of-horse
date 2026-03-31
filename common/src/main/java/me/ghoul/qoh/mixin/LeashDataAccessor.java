package me.ghoul.qoh.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Leashable.LeashData.class)
public interface LeashDataAccessor {

    @Invoker("<init>")
    static Leashable.LeashData create(Entity leashHolder) {
        throw new AssertionError();
    }

}
