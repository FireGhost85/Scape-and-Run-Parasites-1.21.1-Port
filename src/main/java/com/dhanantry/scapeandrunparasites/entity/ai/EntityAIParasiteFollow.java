package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;

public class EntityAIParasiteFollow
extends Goal {
    private final EntityParasiteBase tameable;
    private LivingEntity owner;
    Level world;
    private final double followSpeed;
    private final PathNavigation petPathfinder;
    private int timeToRecalcPath;
    double minBlockDistance;
    double maxBlockDistance;
    private float oldWaterCost;
    private boolean canSendOrder;

    public EntityAIParasiteFollow(EntityParasiteBase tameableIn, double followSpeedIn, double mindistance, double maxdistance, boolean send) {
        this.tameable = tameableIn;
        this.world = tameableIn.level();
        this.followSpeed = followSpeedIn;
        this.petPathfinder = tameableIn.getNavigation();
        this.minBlockDistance = mindistance * mindistance;
        this.maxBlockDistance = maxdistance * maxdistance;
        this.canSendOrder = send;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        EntityParasiteBase entitylivingbase = this.tameable.getParasiteFollowing();
        if (entitylivingbase == null) {
            return false;
        }
        if (this.tameable.distanceToSqr((Entity)entitylivingbase) < this.minBlockDistance) {
            return false;
        }
        this.owner = entitylivingbase;
        return true;
    }

    public boolean canContinueToUse() {
        if (!this.owner.isAlive()) {
            this.tameable.setParasiteToFollow(null);
            return false;
        }
        return !this.petPathfinder.isDone() && this.tameable.distanceToSqr((Entity)this.owner) > this.maxBlockDistance;
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tameable.getPathfindingMalus(PathType.WATER);
        this.tameable.setPathfindingMalus(PathType.WATER, 0.0f);
    }

    public void stop() {
        this.petPathfinder.stop();
        this.tameable.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    public void tick() {
        this.tameable.getLookControl().setLookAt((Entity)this.owner, 10.0f, (float)this.tameable.getMaxHeadXRot());
        if (this.tameable.getTarget() != null && this.canSendOrder && this.tameable.getParasiteFollowing() != null && this.tameable.getParasiteFollowing().getTarget() == null && !(this.tameable.getTarget() instanceof EntityParasiteBase)) {
            this.tameable.getParasiteFollowing().setTarget(this.tameable.getTarget());
        }
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.petPathfinder.moveTo((Entity)this.owner, this.followSpeed)) {
                this.tameable.setParasiteToFollow(null);
            }
        }
    }
}

