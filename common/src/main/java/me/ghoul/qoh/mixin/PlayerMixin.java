package me.ghoul.qoh.mixin;

import dev.architectury.platform.Platform;
import me.ghoul.qoh.attributes.ModAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void modifyAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        if (Platform.isFabric()) {
            cir.getReturnValue().add(ModAttributes.HORSE_TAMING);
        }
    }

}
