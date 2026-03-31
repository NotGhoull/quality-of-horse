package me.ghoul.qoh.mixin.client;

import me.ghoul.qoh.client.renderer.HorseChestLayer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.world.entity.animal.horse.Horse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseRenderer.class)
public abstract class HorseRendererMixin extends AbstractHorseRenderer<Horse, HorseModel<Horse>> {
    public HorseRendererMixin(EntityRendererProvider.Context pContext, HorseModel<Horse> pModel, float pScale) {
        super(pContext, pModel, pScale);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addChestLayer(CallbackInfo ci) {
        this.addLayer(new HorseChestLayer<>(this));
    }
}


