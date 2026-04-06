package me.ghoul.qoh;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.resources.ResourceLocation;

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
    @CustomDescription("config.qoh.CanHorsesSwim.description")
    @CustomName("config.qoh.CanHorsesSwim.name")
    @SerialEntry(comment = "If horses can swim when being ridden")
    public boolean CanHorsesSwim = true;

    // Horse two player ride
    @AutoGen(category = "General", group = "misc")
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @CustomDescription("config.qoh.TwoPlayerRideEnabled.description")
    @CustomName("config.qoh.TwoPlayerRideEnabled.name")
    @SerialEntry(comment = "If two players can ride at the same time")
    public boolean TwoPlayerRideEnabled = true;

    // Horse can have chest settings
    @SerialEntry(comment = "Whether horses can have chests")
    @MasterTickBox({"inventoryColumns"})
    @AutoGen(category = "General", group = "chest_settings")
    @CustomDescription("config.qoh.HorsesCanHaveChests.description")
    @CustomName("config.qoh.HorsesCanHaveChests.name")
    public boolean HorsesCanHaveChests = true;

    @SerialEntry(comment = "How many inventory columns the chest has")
    @IntSlider(min = 0, max = 10, step = 1)
    @AutoGen(category = "General", group = "chest_settings")
    @CustomDescription("config.qoh.inventoryColumns.description")
    @CustomName("config.qoh.inventoryColumns.name")
    public int inventoryColumns = 5;


    @SerialEntry(comment = "If the horse follows their owner or not")
    @AutoGen(category = "General", group = "follow_settings")
    @MasterTickBox({"HorseStartsFollowingAt", "HorseStopsFollowingAt", "HorseFollowSpeedMult"})
    @CustomDescription("config.qoh.HorseFollowsOwner.description")
    @CustomName("config.qoh.HorseFollowsOwner.name")
    public boolean HorseFollowsOwner = true;

    @SerialEntry(comment = "How far away does the player need to be for the horse to start following them")
    @AutoGen(category = "General", group = "follow_settings")
    @FloatSlider(min = 0, max = 100, step = 1F, format = "%.1f blocks")
    @CustomDescription("config.qoh.HorseStartsFollowingAt.description")
    @CustomName("config.qoh.HorseStartsFollowingAt.name")
    public float HorseStartsFollowingAt = 14F;

    @SerialEntry(comment = "The distance a tamed horse will stop following you")
    @AutoGen(category = "General", group = "follow_settings")
    @FloatSlider(min = 0, max = 20, step = 1F, format = "%.1f blocks")
    @CustomDescription("config.qoh.HorseStopsFollowingAt.description")
    @CustomName("config.qoh.HorseStopsFollowingAt.name")
    public float HorseStopsFollowingAt = 5F;

    @SerialEntry(comment = "How much faster the horse will move when following the player")
    @AutoGen(category = "General", group = "follow_settings")
    @FloatSlider(min = 0, max = 20, step = 0.2F, format = "%.1f times faster")
    @CustomDescription("config.qoh.HorseFollowSpeedMult.description")
    @CustomName("config.qoh.HorseFollowSpeedMult.name")
    public float HorseFollowSpeedMult = 1.5F;

    @SerialEntry(comment = "How many blocks (squared) the horse needs to be away from the player to teleport")
    @AutoGen(category = "General", group = "follow_settings")
    @DoubleField(format = "%.1f blocks^2")
    @CustomDescription("config.qoh.TeleportWhenFurtherAwayThan.description")
    @CustomName("config.qoh.TeleportWhenFurtherAwayThan.name")
    public double TeleportWhenFurtherAwayThan = 400.F;
}
