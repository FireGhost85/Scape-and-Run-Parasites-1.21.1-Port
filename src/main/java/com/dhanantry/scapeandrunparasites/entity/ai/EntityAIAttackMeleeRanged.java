package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class EntityAIAttackMeleeRanged
extends Goal {
    private final EntityParasiteBase parentEntity;
    private double minDistance;
    private double maxDistance;
    private double prepDistance;
    private boolean pulling;
    private float str;

    public EntityAIAttackMeleeRanged(EntityParasiteBase in, double minD, double maxD, double prepD, boolean pull, float str) {
        this.parentEntity = in;
        this.minDistance = minD;
        this.maxDistance = maxD;
        this.prepDistance = prepD;
        this.pulling = pull;
        this.str = str;
    }

    public boolean canUse() {
        return this.parentEntity.getTarget() != null;
    }

    public void stop() {
        this.parentEntity.setParasiteStatus(0);
    }

    public void tick() {
        if (this.parentEntity.getTarget() == null) {
            this.stop();
        } else {
            LivingEntity entitylivingbase = this.parentEntity.getTarget();
            if (entitylivingbase.distanceToSqr((Entity)this.parentEntity) < this.prepDistance * this.prepDistance && this.parentEntity.hasLineOfSight((Entity)entitylivingbase)) {
                this.parentEntity.setParasiteStatus(15);
                if (entitylivingbase.distanceToSqr((Entity)this.parentEntity) < this.maxDistance * this.maxDistance && entitylivingbase.distanceToSqr((Entity)this.parentEntity) > this.minDistance * this.minDistance && this.parentEntity.tickCount % 20 == 0) {
                    Vec3 vec3d = this.parentEntity.getViewVector(1.0f);
                    float f = (float)Mth.atan2((double)(entitylivingbase.getZ() - this.parentEntity.getZ()), (double)(entitylivingbase.getX() - this.parentEntity.getX()));
                    EntityDamage entityevokerfangs = new EntityDamage(this.parentEntity.level(), entitylivingbase.getX(), entitylivingbase.getY(), entitylivingbase.getZ(), f, (LivingEntity)this.parentEntity, 1.0f, this.pulling, 1.0f);
                    this.parentEntity.level().addFreshEntity((Entity)entityevokerfangs);
                }
            } else {
                this.parentEntity.setParasiteStatus(0);
            }
        }
    }
}

