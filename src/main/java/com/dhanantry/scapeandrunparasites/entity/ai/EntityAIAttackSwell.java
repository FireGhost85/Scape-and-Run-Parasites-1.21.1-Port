package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EntityAIAttackSwell
extends Goal {
    EntityParasiteBase swellingParasite;
    LivingEntity creeperAttackTarget;
    double distance;

    public EntityAIAttackSwell(EntityParasiteBase in) {
        this.swellingParasite = in;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.distance = 9.0;
    }

    public EntityAIAttackSwell(EntityParasiteBase in, double dis) {
        this(in);
        this.distance = dis;
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
    }

    public boolean canUse() {
        LivingEntity entitylivingbase = this.swellingParasite.getTarget();
        return this.swellingParasite.getSelfeState() > 0 || entitylivingbase != null && this.swellingParasite.distanceToSqr((Entity)entitylivingbase) < this.distance;
    }

    public void start() {
        this.swellingParasite.getNavigation().stop();
        this.creeperAttackTarget = this.swellingParasite.getTarget();
    }

    public void stop() {
        this.creeperAttackTarget = null;
    }

    public void tick() {
        if (this.creeperAttackTarget == null) {
            this.swellingParasite.setSelfeState(-1);
        } else if (this.swellingParasite.distanceToSqr((Entity)this.creeperAttackTarget) > 49.0) {
            this.swellingParasite.setSelfeState(-1);
        } else if (!this.swellingParasite.getSensing().hasLineOfSight((Entity)this.creeperAttackTarget)) {
            this.swellingParasite.setSelfeState(-1);
        } else {
            this.swellingParasite.setSelfeState(1);
        }
    }
}

