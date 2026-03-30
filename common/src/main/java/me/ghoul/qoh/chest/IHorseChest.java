package me.ghoul.qoh.chest;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;

public interface IHorseChest {
    boolean hasChest();
    void setHasChest(boolean value);

    Container getInventory();
    void initChestInventory();
}
