package me.ghoul.qoh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

    public static final String MOD_ID = "qoh";
    public static final String MOD_NAME = "Quality of horse";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static ModConfig CONFIG;

    public static void SetConfig(ModConfig config) {
        CONFIG = config;
    }

    public static ModConfig GetConfig() {
        if (CONFIG == null) {
            return ModConfig.HANDLER.defaults();
        } else {
            return CONFIG;
        }
    }
}
