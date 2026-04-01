package me.ghoul.qoh.neoforge;


import me.ghoul.qoh.CommonClass;
import me.ghoul.qoh.Constants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Main {

    public Main(IEventBus eventBus) {
        CommonClass.init();
    }
}
