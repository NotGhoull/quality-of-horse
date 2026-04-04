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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import java.util.Objects;
import java.util.UUID;

public class OwnerTagItem extends Item {
    private static final Gson GSON = new Gson();
    public OwnerTagItem(Properties properties) { super(properties); }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if (livingEntity instanceof Horse horse) {
            return handleHorseInteraction(itemStack, player, horse);
        }

        if (livingEntity instanceof Player targetPlayer) {
            handlePlayerInteraction(itemStack, player, targetPlayer);
            return InteractionResult.CONSUME;
        }

        // Eventually we'd do logic for transferring ownership here, but for now we just pass
        return InteractionResult.PASS;
    }

    private void handlePlayerInteraction(ItemStack itemStack, Player sourcePlayer, Player targetPlayer) {
        if (sourcePlayer.level().isClientSide()) {
            sourcePlayer.sendSystemMessage(Component.literal("Skipping because this is a client instance"));
            return;
        }

        var strUuid = itemStack.get(ModComponents.HORSE_UUID_COMPONENT.get());
        if (strUuid == null) {
            sourcePlayer.sendSystemMessage(Component.literal("This item is not bound to a horse!"));
            return;
        }

        UUID horseUUID = UUID.fromString(strUuid);

        ServerLevel level = (ServerLevel) sourcePlayer.level();
        Horse horse = (Horse) level.getEntity(horseUUID);
        if (horse == null) {
            sourcePlayer.sendSystemMessage(Component.literal("The horse bound to this item could not be found!"));
            return;
        }

        // TODO: Draw particles from player 1 to player 2 and the horse to make it more clear that something is happening
        horse.setOwnerUUID(targetPlayer.getUUID());
        targetPlayer.sendSystemMessage(Component.literal("[QoH] {} has given you {}!".formatted(sourcePlayer.getName().getString(), horse.getName().getString())));
        itemStack.consume(1, sourcePlayer);
    }

    private InteractionResult handleHorseInteraction(ItemStack itemStack, Player player, Horse horse) {
        if (!isHorseInteractable(horse, player)) {
            player.displayClientMessage(Component.translatable("message.qoh.not_tamed_or_owner"), true);
            // We return success so the player doesn't get on the horse
            return InteractionResult.SUCCESS;
        }

        if (player.level().isClientSide()) {
            return InteractionResult.sidedSuccess(true);
        }

        if (!bindHorseToItem(itemStack, horse)) {
            player.displayClientMessage(Component.translatable("message.qoh.binding_error"), false);
            return InteractionResult.FAIL;
        }

        player.displayClientMessage(Component.translatable("message.qoh.bound_success", horse.getName()), true);
        return InteractionResult.SUCCESS;

    }

    private String serializeComponent(Component component) {
        return GSON.toJson(ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, component).getOrThrow());
    }

    private Component deserializeComponent(String json) {
        return ComponentSerialization.CODEC.decode(JsonOps.INSTANCE, GSON.fromJson(json, JsonElement.class)).getOrThrow().getFirst();
    }

    private boolean isHorseInteractable(Horse horse, Player player) {
        // Because the client doesn't have access to the horse's owner UUID, we assume that it's okay if they're tamed
        if (player.level().isClientSide()) {
            return horse.isTamed();
        }

        return horse.isTamed() && Objects.equals(horse.getOwner(), player);
    }

    private boolean bindHorseToItem(ItemStack itemStack, Horse horse) {
        String horseUUID = horse.getStringUUID();
        String horseNameJson = serializeComponent(horse.getName());
        String ownerNameJson;
        try {
            //noinspection DataFlowIssue - We handle it in the try
            ownerNameJson = serializeComponent(horse.getOwner().getName());
        } catch (NullPointerException npe) {
            Constants.LOG.error("Horse {} has no owner, cannot bind to item!", horseUUID);
            return false;
        }

        itemStack.set(ModComponents.HORSE_UUID_COMPONENT.get(), horseUUID);
        itemStack.set(ModComponents.HORSE_NAME_COMPONENT.get(), horseNameJson);
        itemStack.set(ModComponents.HORSE_OWNER_NAME_COMPONENT.get(), ownerNameJson);
        itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return true;
    }


    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        var horseName = itemStack.getOrDefault(ModComponents.HORSE_NAME_COMPONENT.get(), "No name");
        var ownerName = itemStack.getOrDefault(ModComponents.HORSE_OWNER_NAME_COMPONENT.get(), "No owner");
        var horseUUID = itemStack.getOrDefault(ModComponents.HORSE_UUID_COMPONENT.get(), "<null>");

        if ("<null>".equals(horseUUID)) {
            list.add(Component.translatable("lore.qoh.not_bound").withStyle(Style.EMPTY
                    .withBold(true)
                    .withItalic(true)
                    .withColor(ChatFormatting.RED))
            );
            return;
        }

        var horseNameComponent = deserializeComponent(horseName);
        var ownerNameComponent = deserializeComponent(ownerName);

        list.add(Component.translatable("lore.qoh.bound_notice").withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GRAY)));

        list.add(Component.translatable("lore.qoh.bound_to", " ┣",
                        horseNameComponent.copy()
                                .withStyle(
                                        Style.EMPTY
                                                .withBold(true)
                                                .withColor(ChatFormatting.WHITE)))
                        .withStyle(
                                Style.EMPTY
                                        .withColor(ChatFormatting.GREEN)));

        list.add(Component.translatable(
                "lore.qoh.original_owner", " ┗",
                        ownerNameComponent.copy()
                                .withStyle(Style.EMPTY
                                        .withBold(true)
                                        .withColor(ChatFormatting.WHITE)))
                .withStyle(Style.EMPTY
                        .withColor(ChatFormatting.YELLOW)));

        list.add(Component.empty());

        list.add(Component.translatable("lore.qoh.transfer_tip")
                .withStyle(Style.EMPTY
                        .withItalic(true)
                        .withColor(ChatFormatting.DARK_GRAY)));

        if (tooltipFlag.isAdvanced()) {
            list.add(Component.literal("UUID: " + horseUUID).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        }
    }
}
