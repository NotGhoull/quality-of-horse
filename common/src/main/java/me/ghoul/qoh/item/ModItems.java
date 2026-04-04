package me.ghoul.qoh.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.ghoul.qoh.Constants;
import me.ghoul.qoh.components.ModComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class ModItems {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Constants.MOD_ID, Registries.CREATIVE_MODE_TAB);
    protected static final RegistrySupplier<CreativeModeTab> QOH_TAB = TABS.register(
            "qoh_tab",
            () -> CreativeTabRegistry.create(
                    Component.translatable("qoh.itemgroup.main"),
                    () -> new ItemStack(ModItems.OWNER_TAG)
            )
    );

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Constants.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> OWNER_TAG = ITEMS.register("owner_tag",
            () -> new OwnerTagItem(new Item.Properties()
                    .component(ModComponents.HORSE_UUID_COMPONENT.get(), "<null>")
                    .arch$tab(QOH_TAB)
                    .stacksTo(1)
            )
    );

    public static void init() {
        TABS.register();
        ITEMS.register();
    }
}
