package me.ghoul.qoh.neoforge;

import me.ghoul.qoh.CommonClass;
import me.ghoul.qoh.Constants;
import me.ghoul.qoh.ModConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class Main {

    public Main(IEventBus eventBus) {
        CommonClass.init();
//        NeoForge.EVENT_BUS.register(this);

        ModLoadingContext.get()
                .registerExtensionPoint(
                        IConfigScreenFactory.class,
                        () ->
                                (client, parent) ->
                                        ModConfig.HANDLER.generateGui().generateScreen(parent));
    }
}
