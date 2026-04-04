package me.ghoul.qoh;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.ghoul.qoh.components.ModComponents;
import me.ghoul.qoh.item.OwnerTagItem;
import me.ghoul.qoh.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CommonClass {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Constants.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> QOH_TAB = TABS.register(
            "qoh_tab",
            () -> CreativeTabRegistry.create(
                    Component.translatable("qoh.itemgroup.main"),
                    () -> new ItemStack(Items.NAME_TAG)
            )
    );

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Constants.MOD_ID, Registries.ITEM);
    public static final RegistrySupplier<Item> OWNER_TAG = ITEMS.register("owner_tag", () -> new OwnerTagItem(new Item.Properties().component(ModComponents.HORSE_UUID_COMPONENT.get(), "<null>").arch$tab(QOH_TAB).stacksTo(1)));

    public static void init() {
        if (Services.PLATFORM.isModLoaded("yet_another_config_lib_v3")) {
            ModConfig.HANDLER.load();
            Constants.SetConfig(ModConfig.HANDLER.instance());
        } else {
            Constants.LOG.warn("YACL not found, using defaults");
            ModConfig.HANDLER.defaults();
            Constants.SetConfig(ModConfig.HANDLER.instance());
        }

        ModComponents.COMPONENTS.register();
        TABS.register();
        ITEMS.register();
//        ModComponents.init();

        Constants.LOG.info("Peter the horse is here");
    }
}
