package me.ghoul.qoh.alchemy;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.apache.commons.codec.binary.Hex;

public class TamingEffect extends MobEffect {
    // Hex to int
    private static final int COLOR = Integer.parseInt("781800", 16);

    protected TamingEffect() {
        super(MobEffectCategory.BENEFICIAL, COLOR);
    }
}
