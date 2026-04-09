package me.ghoul.qoh.attributes;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import me.ghoul.qoh.Constants;
import me.ghoul.qoh.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ModAttributes {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Constants.MOD_ID, Registries.ATTRIBUTE);

    public static Holder<Attribute> HORSE_TAMING = register("horse_taming", (new RangedAttribute("attribute.qoh.horse_taming", 0, -100, 100).setSyncable(true)));

    public static void init() {
        if (Platform.isNeoForge()) {
            ATTRIBUTES.register();
        }
    }


    private static Holder<Attribute> register(String name, Attribute attr) {
        if (Platform.isFabric()) {
            return registerFabric(name, attr);
        } else {
            return registerNeoForge(name, attr);
        }
    }

    private static Holder<Attribute> registerFabric(String name, Attribute attr) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), attr);
    }

    private static Holder<Attribute> registerNeoForge(String name, Attribute attr) {
        return ATTRIBUTES.register(name, () -> attr);
    }
}
