package me.ghoul.qoh.mixin;

import me.ghoul.qoh.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Horse.class)
public abstract class HorseMixin extends AbstractHorse  {
    @Unique
    private static final EntityDataAccessor<Boolean> DATA_HAS_CHEST = SynchedEntityData.defineId(HorseMixin.class, EntityDataSerializers.BOOLEAN);

    protected HorseMixin(EntityType<? extends AbstractHorse> type, Level level) {
        super(type, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo _ci) {
        builder.define(DATA_HAS_CHEST, false);
    }
    @Unique
    public boolean qoh$hasChest() { return this.entityData.get(DATA_HAS_CHEST); }
    @Unique
    public void qoh$setChest(boolean chested) { this.entityData.set(DATA_HAS_CHEST, chested); }

    @Unique
    public int getInventoryColumns() { return this.qoh$hasChest() ? 5 : 0; };

    @Override
    public @NotNull SlotAccess getSlot(int slot) {
        return slot == 499 ? new SlotAccess() {

            public @NotNull ItemStack get() {
                return HorseMixin.this.qoh$hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
            }

            @Override
            public boolean set(@NotNull ItemStack stack) {
                if (stack.isEmpty()) {
                    if (qoh$hasChest()) {
                        qoh$setChest(false);
                        createInventory();
                    }
                    return true;
                } else if (stack.is(Items.CHEST)) {
                    if (!qoh$hasChest()) {
                        qoh$setChest(true);
                        createInventory();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        } : super.getSlot(slot);
    }

    @Unique
    private void qoh$dropInventory() {
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

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Constants.LOG.info("Interacting with horse! Has chest: {}", this.qoh$hasChest());
        ItemStack stack = player.getItemInHand(hand);

        if (!this.qoh$hasChest() && stack.is(Items.CHEST) && this.isTamed()) {
            this.equipChest(player, stack);
            cir.setReturnValue(InteractionResult.SUCCESS);
//            cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide));
        } else if (this.qoh$hasChest() && stack.isEmpty() && this.isTamed() && player.isCrouching()) {
            this.qoh$dropInventory();
            cir.setReturnValue(InteractionResult.SUCCESS);
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
                    this.inventory.setItem(j + 1, (ItemStack) ItemStack.parse(this.registryAccess(), cTag).orElse(ItemStack.EMPTY));
                }
            }
        }

        this.syncSaddleToClients();
    }

    @Unique
    private void equipChest(Player player, ItemStack chestStack) {
        this.qoh$setChest(true);
        this.playChestEquipSound();
        chestStack.consume(1, player);
        this.createInventory();
    }

    @Unique
    private void playChestEquipSound() {
        this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }


}
