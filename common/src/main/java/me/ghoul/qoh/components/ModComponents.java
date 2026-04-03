package me.ghoul.qoh.components;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.ghoul.qoh.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

public class ModComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Constants.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<String>> TEST_COMPONENT =
            COMPONENTS.register("target_horse_uuid", () ->
                DataComponentType.<String>builder().persistent(Codec.STRING).build()
            );

    public static final RegistrySupplier<DataComponentType<String>> HORSE_NAME_COMPONENT =
            COMPONENTS.register("target_horse_name", () ->
                    DataComponentType.<String>builder().persistent(Codec.STRING).build()
            );

    public static final RegistrySupplier<DataComponentType<String>> HORSE_OWNER_NAME_COMPONENT =
            COMPONENTS.register("target_horse_owner_name", () ->
                    DataComponentType.<String>builder().persistent(Codec.STRING).build()
            );

    public static void init() {}

}
