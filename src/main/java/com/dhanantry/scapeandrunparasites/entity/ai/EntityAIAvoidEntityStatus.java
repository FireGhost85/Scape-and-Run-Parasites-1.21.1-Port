package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class EntityAIAvoidEntityStatus<T extends Entity>
extends Goal {
    private final Predicate<Entity> canBeSeenSelector = new Predicate<Entity>(){

        public boolean test(@Nullable Entity p_apply_1_) {
            return p_apply_1_.isAlive() && EntityAIAvoidEntityStatus.this.entity.getSensing().hasLineOfSight(p_apply_1_) && !EntityAIAvoidEntityStatus.this.entity.isAlliedTo(p_apply_1_);
        }
    };
    protected EntityParasiteBase entity;
    private final double farSpeed;
    protected T closestLivingEntity;
    private final float avoidDistance;
    private Path path;
    private final PathNavigation navigation;
    private final Class<T> classToAvoid;
    private final Predicate<? super T> avoidTargetSelector;

    public EntityAIAvoidEntityStatus(EntityParasiteBase entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn) {
        this(entityIn, classToAvoidIn, (e -> true), avoidDistanceIn, farSpeedIn);
    }

    public EntityAIAvoidEntityStatus(EntityParasiteBase entityIn, Class<T> classToAvoidIn, Predicate<? super T> avoidTargetSelectorIn, float avoidDistanceIn, double farSpeedIn) {
        this.entity = entityIn;
        this.classToAvoid = classToAvoidIn;
        this.avoidTargetSelector = avoidTargetSelectorIn;
        this.avoidDistance = avoidDistanceIn;
        this.farSpeed = farSpeedIn;
        this.navigation = entityIn.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        List<T> list = this.entity.level().getEntitiesOfClass(this.classToAvoid, this.entity.getBoundingBox().inflate((double)this.avoidDistance, 3.0, (double)this.avoidDistance), e -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(e) && this.canBeSeenSelector.test(e) && this.avoidTargetSelector.test(e));
        if (list.isEmpty() || this.entity.shouldWorkTask()) {
            return false;
        }
        this.closestLivingEntity = list.get(0);
        Vec3 vec3d = DefaultRandomPos.getPosAway((PathfinderMob)this.entity, (int)16, (int)7, (Vec3)new Vec3(((Entity)this.closestLivingEntity).getX(), ((Entity)this.closestLivingEntity).getY(), ((Entity)this.closestLivingEntity).getZ()));
        if (vec3d == null) {
            return false;
        }
        if (this.closestLivingEntity.distanceToSqr(vec3d.x, vec3d.y, vec3d.z) < this.closestLivingEntity.distanceToSqr((Entity)this.entity)) {
            return false;
        }
        this.path = this.navigation.createPath(vec3d.x, vec3d.y, vec3d.z, 0);
        return this.path != null;
    }

    public boolean canContinueToUse() {
        return !this.navigation.isDone();
    }

    public void start() {
        this.navigation.moveTo(this.path, this.farSpeed);
    }

    public void stop() {
        this.closestLivingEntity = null;
    }

    public void tick() {
        this.entity.getNavigation().setSpeedModifier(this.farSpeed);
    }
}

