package me.ghoul.qoh.mixin.features;

import me.ghoul.qoh.Constants;
import me.ghoul.qoh.interfaces.IHorseFeature;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Horse.class)
public abstract class HorseCanHaveChestMixin extends AbstractHorse implements IHorseFeature {
    @Unique private static final EntityDataAccessor<Boolean> DATA_HAS_CHEST =
            SynchedEntityData.defineId(HorseCanHaveChestMixin.class, EntityDataSerializers.BOOLEAN);

    protected HorseCanHaveChestMixin(EntityType<? extends AbstractHorse> type, Level level) {
        super(type, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo _ci) {
        builder.define(DATA_HAS_CHEST, false);
    }

    @Unique public boolean qoh$hasChest() {
        return this.entityData.get(DATA_HAS_CHEST);
    }

    @Unique public void qoh$setChest(boolean chested) {
        this.entityData.set(DATA_HAS_CHEST, chested);
    }

    @Unique public int getInventoryColumns() {
        return this.qoh$hasChest() ? Constants.CONFIG.inventoryColumns : 0;
    }
    ;

    @Override
    public @NotNull SlotAccess getSlot(int slot) {
        return slot == 499
                ? new SlotAccess() {

                    public @NotNull ItemStack get() {
                        return HorseCanHaveChestMixin.this.qoh$hasChest()
                                ? new ItemStack(Items.CHEST)
                                : ItemStack.EMPTY;
                    }

                    @Override
                    public boolean set(@NotNull ItemStack stack) {
                        if (stack.isEmpty()) {
                            if (HorseCanHaveChestMixin.this.qoh$hasChest()) {
                                qoh$setChest(false);
                                createInventory();
                            }
                            return true;
                        } else if (stack.is(Items.CHEST)) {
                            if (!HorseCanHaveChestMixin.this.qoh$hasChest()) {
                                qoh$setChest(true);
                                createInventory();
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                : super.getSlot(slot);
    }

    @Override
    public boolean qoh$tryChestInteraction(Player player, ItemStack stack) {
        if (!Constants.CONFIG.HorsesCanHaveChests) {
            return false;
        }

        if (!this.qoh$hasChest() && stack.is(Items.CHEST) && this.isTamed()) {
            this.qoh$equipChest(player, stack);
            return true;
        } else if (this.qoh$hasChest() && stack.is(Items.SHEARS) && this.isTamed()) {
            this.qoh$dropInventory();
            return true;
            // This should be moved to a different feature, but for now it's here
        } else if (this.isTamed() && player.isCrouching() && stack.isEmpty()) {
            if (!player.level().isClientSide()) {
                // TODO: Move this
                if (getLeashData() != null) {
                    // TODO: Translatable message
                    player.displayClientMessage(
                            Component.literal(
                                    "[QoH] This horse already has a leash, unleash it first!"),
                            true);
                    return true;
                }
                setLeashedTo(player, true);
                return true;
            }
        }
        return false;
    }

    @Unique private void qoh$dropInventory() {
        if (this.qoh$hasChest()) {
            for (int i = 1; i < this.inventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    this.spawnAtLocation(itemstack);
                    this.inventory.setItem(i, ItemStack.EMPTY);
                }
            }
            ItemStack chest = new ItemStack(Items.CHEST);
            this.spawnAtLocation(chest);

            qoh$setChest(false);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveChest(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("HasChest", this.qoh$hasChest());

        if (this.qoh$hasChest()) {
            ListTag listTag = new ListTag();

            for (int i = 1; i < this.inventory.getContainerSize(); ++i) {
                ItemStack item = this.inventory.getItem(i);
                if (!item.isEmpty()) {
                    CompoundTag cTag = new CompoundTag();
                    cTag.putByte("Slot", (byte) (i - 1));
                    listTag.add(item.save(this.registryAccess(), cTag));
                }
            }

            tag.put("Items", listTag);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadChest(CompoundTag tag, CallbackInfo ci) {
        this.qoh$setChest(tag.getBoolean("HasChest"));
        this.createInventory();

        if (this.qoh$hasChest()) {
            ListTag listTag = tag.getList("Items", 10);

            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag cTag = listTag.getCompound(i);
                int j = cTag.getByte("Slot") & 255;
                if (j < this.inventory.getContainerSize() - 1) {
                    this.inventory.setItem(
                            j + 1,
                            (ItemStack)
                                    ItemStack.parse(this.registryAccess(), cTag)
                                            .orElse(ItemStack.EMPTY));
                }
            }
        }

        this.syncSaddleToClients();
    }

    @Unique private void qoh$equipChest(Player player, ItemStack chestStack) {
        this.qoh$setChest(true);
        this.qoh$playChestEquipSound();
        chestStack.consume(1, player);
        this.createInventory();
    }

    @Unique private void qoh$playChestEquipSound() {
        this.playSound(
                SoundEvents.DONKEY_CHEST,
                1.0F,
                (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }
}
