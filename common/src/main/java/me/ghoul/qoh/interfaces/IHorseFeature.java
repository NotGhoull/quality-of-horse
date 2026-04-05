package me.ghoul.qoh.interfaces;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHorseFeature {
    default boolean getIsLeadArtificial() {
        return false;
    };

    boolean qoh$hasChest();
    default boolean isLinkedToBoat() {
        return false;
    }

    default void qoh$boatHandleLeashTooFarBehavior() { }

    default boolean qoh$tryTwoPlayerRide(Player player, ItemStack stack) {
        return false;
    }

    default InteractionResult qoh$tryChestInteraction(Player player, ItemStack stack) {
        return InteractionResult.PASS;
    }

    default InteractionResult qoh$tryCreateLead(Player player, ItemStack itemStack) {
        return InteractionResult.PASS;
    }
}
