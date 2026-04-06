package me.ghoul.qoh.mixin.features;

import me.ghoul.qoh.Constants;
import me.ghoul.qoh.interfaces.IHorseFeature;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Horse.class)
public abstract class HorseFreeLeadMixin extends AbstractHorse implements IHorseFeature  {

    boolean leadWasCreated = false;

    @Override
    public boolean handleLeashAtDistance(Entity entity, float f) {
        return super.handleLeashAtDistance(entity, f);
    }

    protected HorseFreeLeadMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public InteractionResult qoh$tryCreateLead(Player player, ItemStack stack) {
        if (this.isTamed() && player.isCrouching() && stack.isEmpty()) {
            if (!player.level().isClientSide()) {
                if (getLeashData() != null) {
                    player.displayClientMessage(
                            Component.translatable(
                                    "message.qoh.already_leashed"),
                            true);
                    return InteractionResult.SUCCESS;
                }
                setLeashedTo(player, true);
                leadWasCreated = true;
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.sidedSuccess(true);
        }

        return InteractionResult.PASS;
    }
}
