package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EntityAIAttackMeleeRangeSwitch
extends Goal {
    private EntityParasiteBase parent;
    private float distance;
    private boolean generation;

    public EntityAIAttackMeleeRangeSwitch(EntityParasiteBase in, float meleeDistance) {
        this.parent = in;
        this.distance = meleeDistance * meleeDistance;
        this.generation = false;
    }

    public EntityAIAttackMeleeRangeSwitch(EntityParasiteBase in, float meleeDistance, boolean checkGen) {
        this.parent = in;
        this.distance = meleeDistance * meleeDistance;
        this.generation = checkGen;
    }

    public boolean canUse() {
        if (this.parent.srpTicks >= 10) {
            return false;
        }
        if (this.generation) {
            return this.parent.getTarget() != null && this.parent.getGeneMod(5);
        }
        if (this.parent.getTarget() != null) {
            return true;
        }
        this.parent.setWorkTask(true);
        return false;
    }

    public void tick() {
        if (this.parent.getTarget() != null) {
            LivingEntity entitylivingbase = this.parent.getTarget();
            if (entitylivingbase.distanceToSqr((Entity)this.parent) < (double)this.distance && this.parent.hasLineOfSight((Entity)entitylivingbase)) {
                this.parent.setWorkTask(true);
            } else {
                this.parent.setWorkTask(false);
            }
        }
    }
}

