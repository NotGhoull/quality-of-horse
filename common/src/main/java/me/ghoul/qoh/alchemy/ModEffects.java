package me.ghoul.qoh.alchemy;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.ghoul.qoh.Constants;
import me.ghoul.qoh.attributes.ModAttributes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.alchemy.Potion;

import java.util.function.Supplier;

public class ModEffects {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Constants.MOD_ID, Registries.MOB_EFFECT);
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Constants.MOD_ID, Registries.POTION);

    /// Don't use this directly, use the supplier below
    private static final Holder<MobEffect> HORSE_TAMING_REGISTRY_EFFECT = EFFECTS.register(
            "horse_taming",
            () -> new TamingEffect().addAttributeModifier(ModAttributes.HORSE_TAMING, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "effect.taming"), 30, AttributeModifier.Operation.ADD_VALUE));

    public static Supplier<Holder<MobEffect>> HORSE_TAMING_EFFECT = () ->
            EFFECTS.getRegistrar().getHolder(HORSE_TAMING_REGISTRY_EFFECT.unwrapKey().get());


    public static final Holder<Potion> HORSE_TAMING_POTION = POTIONS.register("horse_taming", () -> new Potion("horse_taming", new MobEffectInstance(HORSE_TAMING_EFFECT.get(), 3600, 0)));
    public static final Holder<Potion> HORSE_TAMING_STRONG_POTION = POTIONS.register("horse_taming_strong", () -> new Potion("horse_taming", new MobEffectInstance(HORSE_TAMING_EFFECT.get(), 4800, 1, false, true, true)));



    public static void init() {
        EFFECTS.register();
        POTIONS.register();
//        ModPotions.init();
    }
}
