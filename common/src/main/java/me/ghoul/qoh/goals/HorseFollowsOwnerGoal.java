package me.ghoul.qoh.goals;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;

public class HorseFollowsOwnerGoal extends Goal {
    private final AbstractHorse horse;

    private LivingEntity owner;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;

    protected final RandomSource random;

    public HorseFollowsOwnerGoal(
            AbstractHorse horse, double pSpeedModifier, float pStartDistance, float pStopDistance) {
        this.horse = horse;
        this.random = RandomSource.create();
        this.speedModifier = pSpeedModifier;
        this.navigation = horse.getNavigation();
        this.startDistance = pStartDistance;
        this.stopDistance = pStopDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        if (!(horse.getNavigation() instanceof GroundPathNavigation)
                && !(horse.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity entity = this.horse.getOwner();
        if (entity == null) {
            return false;
        } else if (this.unableToMoveToOwner()) {
            return false;
        } else if (this.horse.distanceToSqr(entity)
                < (double) (this.startDistance * this.startDistance)) {
            return false;
        } else {
            this.owner = entity;
            return true;
        }
    }

    public final boolean unableToMoveToOwner() {
        return !this.horse.isLeashed() && this.horse.getVehicle() != null;
    }

    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else {
            return !unableToMoveToOwner()
                    && !(this.horse.distanceToSqr(this.owner)
                            <= (double) (this.stopDistance * this.stopDistance));
        }
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.horse.getPathfindingMalus(PathType.WATER);
        this.horse.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.horse.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    public void tick() {
        boolean flag = shouldTryTeleportToOwner();
        if (!flag) {
            this.horse
                    .getLookControl()
                    .setLookAt(this.owner, 10.0F, (float) this.horse.getMaxHeadXRot());
        }

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (flag) {
                tryTeleportToOwner();
            } else {
                this.navigation.moveTo(this.owner, this.speedModifier);
            }
        }
    }

    public boolean shouldTryTeleportToOwner() {
        if (this.horse.isLeashed() || this.horse.getVehicle() != null) {
            return false;
        } else return !(this.horse.distanceToSqr(this.owner) < 200.0D);
    }

    public void tryTeleportToOwner() {
        LivingEntity entity = this.horse.getOwner();
        if (entity != null) {
            // Get the position of the entity
            BlockPos entityPos = entity.blockPosition();
            Level level = entity.level();

            // Find the closest solid block beneath the entity (up to a max search depth)
            BlockPos targetPos = null;
            for (int y = entityPos.getY(); y >= level.getMinBuildHeight(); y--) {
                BlockPos checkPos = new BlockPos(entityPos.getX(), y, entityPos.getZ());
                if (!level.getBlockState(checkPos).isAir()) {
                    if (level.getBlockState(checkPos)
                            .getCollisionShape(level, checkPos)
                            .isEmpty()) {
                        continue; // Skip blocks that don't have a collision shape
                    }
                    targetPos = checkPos.above(); // Found the closest non-air block
                    break;
                }
            }

            // If we found a valid block position beneath, teleport the horse there
            if (targetPos != null) {
                this.horse.teleportTo(entityPos.getX(), targetPos.getY(), entityPos.getZ());
            }
        }
    }
}
