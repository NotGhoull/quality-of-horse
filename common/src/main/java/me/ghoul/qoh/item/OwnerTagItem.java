package me.ghoul.qoh.item;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.ghoul.qoh.Constants;
import me.ghoul.qoh.components.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OwnerTagItem extends Item {
    private static final Gson GSON = new Gson();
    public OwnerTagItem(Properties properties) { super(properties); }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if (livingEntity instanceof Horse horse) {
            if (!horse.isTamed() && !horse.getOwner().equals(player.getUUID())) {
                player.displayClientMessage(Component.literal("This horse is not tamed or you are not the owner!"), true);
                return InteractionResult.FAIL;
            }
            // Level upset because of ''Level' used without 'try'-with-resources statement'
            if (player.level().isClientSide()) {
                return InteractionResult.sidedSuccess(true);
            }

            Gson gson = new Gson();
            itemStack.set(ModComponents.TEST_COMPONENT.get(), horse.getStringUUID());
            itemStack.set(ModComponents.HORSE_NAME_COMPONENT.get(), serializeComponent(horse.getName()));
            itemStack.set(ModComponents.HORSE_OWNER_NAME_COMPONENT.get(),  serializeComponent(horse.getOwner().getName()));
            itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

            player.displayClientMessage(Component.literal("Bound to horse ").append(horse.getName()), true);
            return InteractionResult.SUCCESS;
        }

        // Eventually we'd do logic for transferring ownership here, but for now we just pass
        return InteractionResult.PASS;
    }

    private String serializeComponent(Component component) {
        return GSON.toJson(ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, component).getOrThrow());
    }

    private Component deserializeComponent(String json) {
        return ComponentSerialization.CODEC.decode(JsonOps.INSTANCE, GSON.fromJson(json, JsonElement.class)).getOrThrow().getFirst();
    }


    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        var horseName = itemStack.getOrDefault(ModComponents.HORSE_NAME_COMPONENT.get(), "No name");
        var ownerName = itemStack.getOrDefault(ModComponents.HORSE_OWNER_NAME_COMPONENT.get(), "No owner");
        var horseUUID = itemStack.getOrDefault(ModComponents.TEST_COMPONENT.get(), "<null>");
        Gson gson = new Gson();

        if ("<null>".equals(horseUUID)) {
            list.add(Component.literal("§cNot bound to any horse").withStyle(Style.EMPTY.withBold(true).withItalic(true)));
            return;
        }

        var horseNameComponent = deserializeComponent(horseName);
        var ownerNameComponent = deserializeComponent(ownerName);

        list.add(Component.literal("Bound horse").withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GRAY)));

        list.add(Component.literal(" ┣ Bound to horse: ")
                .withStyle(
                        Style.EMPTY
                                .withColor(ChatFormatting.GREEN))
                .append(horseNameComponent.copy()
                        .withStyle(
                                Style.EMPTY
                                        .withBold(true)
                                        .withColor(ChatFormatting.WHITE))));

        list.add(Component.literal(" ┗ Original Owner: ")
                .withStyle(Style.EMPTY
                        .withColor(ChatFormatting.YELLOW))
                .append(ownerNameComponent.copy()
                        .withStyle(Style.EMPTY
                                .withBold(true)
                                .withColor(ChatFormatting.WHITE))));

        list.add(Component.empty());

        list.add(Component.literal("To transfer ownership, right-click the horse as the owner, then right-click the new owner.")
                .withStyle(Style.EMPTY
                        .withItalic(true)
                        .withColor(ChatFormatting.DARK_GRAY)));

        if (tooltipFlag.isAdvanced()) {
            list.add(Component.literal("UUID: " + horseUUID).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        }
    }
}
