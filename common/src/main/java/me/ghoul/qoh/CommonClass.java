package me.ghoul.qoh;

import me.ghoul.qoh.platform.Services;

public class CommonClass {

    public static void init() {
        if (Services.PLATFORM.isModLoaded("yet_another_config_lib_v3")) {
            ModConfig.HANDLER.load();
            Constants.SetConfig(ModConfig.HANDLER.instance());
        } else {
            Constants.LOG.warn("YACL not found, using defaults");
            ModConfig.HANDLER.defaults();
            Constants.SetConfig(ModConfig.HANDLER.instance());
        }

        Constants.LOG.info("Peter the horse is here");
    }
}
