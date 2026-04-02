package me.ghoul.qoh;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class ModConfig {
    public static ConfigClassHandler<ModConfig> HANDLER = ConfigClassHandler.<ModConfig>createBuilder(ModConfig.class)
            .id(ResourceLocation.fromNamespaceAndPath("qoh", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("qoh.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();



    // Swimming
    @AutoGen(category = "General", group = "misc")
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @CustomDescription(value = "If enabled, horses can swim in water instead of sinking. (Note, you will not get kicked off of the horse when its underwater)")
    @SerialEntry(comment = "If horses can swim when being ridden")
    public boolean CanHorsesSwim = true;

    // Horse two player ride
    @AutoGen(category = "General", group = "misc")
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @CustomDescription("If enabled, two players can ride the same horse.")
    @SerialEntry(comment = "If two players can ride at the same time")
    public boolean TwoPlayerRideEnabled = true;

    // Horse can have chest settings
    @SerialEntry(comment = "Whether horses can have chests")
    @MasterTickBox({"inventoryColumns"})
    @AutoGen(category = "General", group = "chest_settings")
    @CustomDescription("If enabled, right-clicking a horse with a chest will let you put a chest on it (like a donkey or mule), right-click with shears to remove the chest")
    @CustomImage(value = "textures/haschest.png", width = 32, height = 32)
    public boolean HorsesCanHaveChests = true;

    @SerialEntry(comment = "How many inventory columns the chest has")
    @IntSlider(min = 0, max = 10, step = 1)
    @AutoGen(category = "General", group = "chest_settings")
    @CustomDescription("Set the number of inventory columns the horse's chest will contain.")
    public int inventoryColumns = 5;


    @AutoGen(category = "General", group = "follow_settings")
    @MasterTickBox({"HorseStartsFollowingAt", "HorseStopsFollowingAt", "HorseFollowSpeedMult"})
    @CustomDescription("If enabled, horses will automatically follow their owner whenever possible. (This will allow horses to teleport)")
    @SerialEntry(comment = "If the horse follows their owner or not")
    public boolean HorseFollowsOwner = true;

    @AutoGen(category = "General", group = "follow_settings")
    @FloatSlider(min = 0, max = 100, step = 1F, format = "%.1f blocks")
    @SerialEntry(comment = "How far away does the player need to be for the horse to start following them? Double that distance will cause the horse to teleport like a wolf or cat would")
    @CustomDescription("How far away the player needs to be for the horse to start following them")
    public float HorseStartsFollowingAt = 10F;

    @AutoGen(category = "General", group = "follow_settings")
    @FloatSlider(min = 0, max = 20, step = 1F, format = "%.1f blocks")
    @CustomDescription("The distance a tamed horse will stop following you")
    @SerialEntry(comment = "The distance a tamed horse will stop following you")
    public float HorseStopsFollowingAt = 2F;

    @AutoGen(category = "General", group = "follow_settings")
    @FloatSlider(min = 0, max = 20, step = 0.2F, format = "%.1f times faster")
    @CustomDescription("How much faster the horse will move when following the player. Setting this to 1 changes nothing.")
    @SerialEntry(comment = "How much faster the horse will move when following the player")
    public float HorseFollowSpeedMult = 1.2F;
}
