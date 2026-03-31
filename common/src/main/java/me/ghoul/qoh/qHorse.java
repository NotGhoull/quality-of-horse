package me.ghoul.qoh;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface qHorse {
    boolean qoh$hasChest();

    default boolean qoh$tryTwoPlayerRide(Player player, ItemStack stack) {
        return false;
    };

    default boolean qoh$tryChestInteraction(Player player, ItemStack stack) {
        return false;
    }

    default boolean dispatchMobInteract(Player player, ItemStack stack) {
        return qoh$tryChestInteraction(player, stack) || qoh$tryTwoPlayerRide(player, stack);
    }
}
