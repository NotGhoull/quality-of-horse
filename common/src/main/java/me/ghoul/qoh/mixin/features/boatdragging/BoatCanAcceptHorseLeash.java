package me.ghoul.qoh.mixin.features.boatdragging;

import me.ghoul.qoh.Constants;
import me.ghoul.qoh.mixin.accessor.LeashDataAccessor;
import me.ghoul.qoh.interfaces.ILeashHolder;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Boat.class)
public abstract class BoatCanAcceptHorseLeash extends Entity implements Leashable {

    public BoatCanAcceptHorseLeash(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void leashTooFarBehaviour() {
        // Do nothing, we won't drop the leash here
        // Possible config? LeadsBreakWhenTooFar

        // LeashData must exist here, since this is only called when the leash is active, so we can
        // safely access it
        // Config? BoatCanTpWhenTooFar
        assert leashData != null;
        Entity leashHolder = leashData.leashHolder;

        assert leashHolder != null;
        double dx = leashHolder.getX() - this.getX();
        double dy = leashHolder.getY() - this.getY();
        double dz = leashHolder.getZ() - this.getZ();
        double distanceSquared = dx * dx + dy * dy + dz * dz;

        double maxDistance = 25.0; // Config MaxDistanceBeforeTp

        if (distanceSquared > maxDistance * maxDistance) {
            this.setPos(leashHolder.getX(), leashHolder.getY(), leashHolder.getZ());
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    @Shadow
    public abstract void setLeashData(@Nullable Leashable.LeashData leashData);

    @Shadow @Nullable private Leashable.LeashData leashData;

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void interact(
            Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> cir) {
        // This is safe because we know pPlayer will implement it because of the mixin
        ILeashHolder data = (ILeashHolder) pPlayer;
        Entity owner = data.getLeashTargetEntity();

        if (owner != null) {
            Constants.LOG.info("Attempting to leash boat to horse");
            LeashData _leashData = LeashDataAccessor.create(owner);
            if (_leashData != null) {
                Horse horse = (Horse) owner;
                horse.setLeashData(null);
                leashData = null;
                data.invalidateTargetEntity();
                setLeashData(_leashData);

                ServerLevel serverLevel = (ServerLevel) level();
                // Unlink the player from the horse
                serverLevel
                        .getChunkSource()
                        .broadcastAndSend(this, new ClientboundSetEntityLinkPacket(owner, null));

                serverLevel
                        .getChunkSource()
                        .broadcastAndSend(this, new ClientboundSetEntityLinkPacket(this, owner));

                Constants.LOG.info("Boat leash data set to {}", _leashData);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
