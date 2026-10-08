package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class EntityAIWanderStatus
extends Goal {
    protected final EntityParasiteBase entity;
    protected double x;
    protected double y;
    protected double z;
    protected final double speed;
    protected int executionChance;
    protected boolean mustUpdate;
    protected float probability;
    protected boolean avoidW;

    public EntityAIWanderStatus(EntityParasiteBase creatureIn, double speedIn, float prob, boolean avoidWater) {
        this(creatureIn, speedIn, 120, prob, avoidWater);
    }

    public EntityAIWanderStatus(EntityParasiteBase creatureIn, double speedIn, int chance, float prob, boolean aW) {
        this.entity = creatureIn;
        this.speed = speedIn;
        this.executionChance = chance;
        this.probability = prob;
        this.avoidW = aW;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        Vec3 vec3d;
        if (this.entity.getParasiteStatus() != 0) {
            return false;
        }
        if (!this.mustUpdate && this.executionChance > 0) {
            if (this.entity.getNoActionTime() >= 100) {
                return false;
            }
            if (this.entity.getRandom().nextInt(this.executionChance) != 0) {
                return false;
            }
        }
        if ((vec3d = this.getPosition()) == null) {
            return false;
        }
        this.x = vec3d.x;
        this.y = vec3d.y;
        this.z = vec3d.z;
        this.mustUpdate = false;
        if (!this.entity.checkPositionWander(this.x, this.y, this.y)) {
            return false;
        }
        return this.entity.getNavigation().getPath() == null || this.executionChance == 0;
    }

    @Nullable
    protected Vec3 getPosition() {
        if (this.avoidW) {
            if (this.entity.isInWater()) {
                Vec3 vec3d = LandRandomPos.getPos((PathfinderMob)this.entity, (int)15, (int)7);
                return vec3d == null ? DefaultRandomPos.getPos((PathfinderMob)this.entity, (int)10, (int)7) : vec3d;
            }
            if (this.entity.getParasiteFollowing() == null) {
                return this.entity.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos((PathfinderMob)this.entity, (int)10, (int)7) : DefaultRandomPos.getPos((PathfinderMob)this.entity, (int)10, (int)7);
            }
            RandomSource ran = RandomSource.create();
            if (ran.nextInt(3) == 0) {
                return this.entity.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos((PathfinderMob)this.entity, (int)3, (int)2) : DefaultRandomPos.getPos((PathfinderMob)this.entity, (int)3, (int)2);
            }
            return null;
        }
        if (this.entity.getParasiteFollowing() == null) {
            return DefaultRandomPos.getPos((PathfinderMob)this.entity, (int)10, (int)7);
        }
        RandomSource ran = RandomSource.create();
        if (ran.nextInt(3) == 0) {
            return DefaultRandomPos.getPos((PathfinderMob)this.entity, (int)0, (int)0);
        }
        return null;
    }

    public boolean canContinueToUse() {
        return !this.entity.getNavigation().isDone() && this.entity.getParasiteStatus() == 0;
    }

    public void start() {
        this.entity.getNavigation().moveTo(this.x, this.y, this.z, this.speed);
    }

    public void makeUpdate() {
        this.mustUpdate = true;
    }

    public void setExecutionValues(int newProb, float newChance) {
        this.executionChance = newProb;
        this.probability = newChance;
    }
}

