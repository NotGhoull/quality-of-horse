package me.ghoul.qoh.goals;

import java.util.EnumSet;

import me.ghoul.qoh.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class HorseFollowsOwnerGoal extends Goal {

    private final AbstractHorse horse;
    private final PathNavigation navigation;
    private final double speed;
    private final float startDistance;
    private final float stopDistance;
    private final RandomSource random = RandomSource.create();

    private LivingEntity owner;
    private int recalcPathCooldown;

    public HorseFollowsOwnerGoal(AbstractHorse horse, double speed, float startDistance, float stopDistance) {
        this.horse = horse;
        this.navigation = horse.getNavigation();
        this.speed = speed;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));

        if (!(navigation instanceof GroundPathNavigation) && !(navigation instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported navigation type for horse follow goal");
        }
    }

    @Override
    public boolean canUse() {
        owner = horse.getOwner();
        return owner != null && isNotRiddenOrLeashed() && horse.distanceToSqr(owner) > startDistance * startDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return owner != null && isNotRiddenOrLeashed() &&
                navigation.isInProgress() &&
                horse.distanceToSqr(owner) > stopDistance * stopDistance;
    }

    @Override
    public void start() {
        recalcPathCooldown = 0;
    }

    @Override
    public void stop() {
        owner = null;
        navigation.stop();
    }

    @Override
    public void tick() {
        if (owner == null) return;

        horse.getLookControl().setLookAt(owner, 10.0F, horse.getMaxHeadXRot());

        if (--recalcPathCooldown <= 0) {
            recalcPathCooldown = adjustedTickDelay(10);

            if (shouldTeleportToOwner()) {
                teleportNearOwner(owner.blockPosition());
            } else {
                navigation.moveTo(owner, speed);
            }
        }
    }

    private boolean isNotRiddenOrLeashed() {
        return !horse.isLeashed() && horse.getVehicle() == null;
    }

    private boolean shouldTeleportToOwner() {
        return isNotRiddenOrLeashed() &&
                horse.distanceToSqr(horse.getOwner()) >= Constants.CONFIG.TeleportWhenFurtherAwayThan;
    }

    private void teleportNearOwner(BlockPos ownerPos) {
        for (int attempt = 0; attempt < 10; attempt++) {
            int xOffset = random.nextIntBetweenInclusive(-3, 3);
            int zOffset = random.nextIntBetweenInclusive(-3, 3);
            if (Math.abs(xOffset) >= 2 || Math.abs(zOffset) >= 2) {
                int yOffset = random.nextIntBetweenInclusive(-1, 1);
                if (tryTeleportTo(ownerPos.getX() + xOffset, ownerPos.getY() + yOffset, ownerPos.getZ() + zOffset)) {
                    return;
                }
            }
        }
    }

    private boolean tryTeleportTo(int x, int y, int z) {
        BlockPos targetPos = new BlockPos(x, y, z);
        if (!canTeleportTo(targetPos)) return false;

        horse.moveTo(x + 0.5, y, z + 0.5, horse.getYRot(), horse.getXRot());
        navigation.stop();
        return true;
    }

    private boolean canTeleportTo(BlockPos pos) {
        if (WalkNodeEvaluator.getPathTypeStatic(horse, pos) != PathType.WALKABLE) return false;

        BlockState below = horse.level().getBlockState(pos.below());
        if (below.getBlock() instanceof LeavesBlock) return false;

        return horse.level().noCollision(horse, horse.getBoundingBox().move(pos.subtract(horse.blockPosition())));
    }
}