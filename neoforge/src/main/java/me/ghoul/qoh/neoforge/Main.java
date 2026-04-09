package me.ghoul.qoh.neoforge;

import me.ghoul.qoh.CommonClass;
import me.ghoul.qoh.Constants;
import me.ghoul.qoh.ModConfig;
import me.ghoul.qoh.attributes.ModAttributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class Main {

    public Main(IEventBus eventBus) {
        CommonClass.init();

        ModLoadingContext.get()
            .registerExtensionPoint(
                IConfigScreenFactory.class,
                () ->
                    (client, parent) ->
                        ModConfig.HANDLER.generateGui().generateScreen(parent));
    }

    @SubscribeEvent
    public static void modifyDefaultAttributes(EntityAttributeModificationEvent event) {
        event.add(
            EntityType.PLAYER,
            ModAttributes.HORSE_TAMING
        );
    }
}
