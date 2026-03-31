package me.ghoul.qoh;

import me.ghoul.qoh.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

public class CommonClass {

    public static void init() {
        if (Services.PLATFORM.isModLoaded("qoh")) {
            Constants.LOG.info("Peter the horse is here");
        }
    }
}
