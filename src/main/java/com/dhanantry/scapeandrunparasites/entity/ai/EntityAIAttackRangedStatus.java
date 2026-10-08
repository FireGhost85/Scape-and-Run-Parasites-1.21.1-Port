package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.Mot;
import java.util.EnumSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class EntityAIAttackRangedStatus
extends Goal {
    private final EntityParasiteBase entityHost;
    private final RangedAttackMob rangedAttackEntityHost;
    private LivingEntity attackTarget;
    private int rangedAttackTime = -1;
    private final double entityMoveSpeed;
    private int seeTime;
    private final int maxRangedAttackTime;
    private final float attackRadius;
    private final float maxAttackDistance;
    private boolean pullAway;

    public EntityAIAttackRangedStatus(RangedAttackMob attacker, double movespeed, int maxAttackTime, float maxAttackDistanceIn, boolean away) {
        if (!(attacker instanceof LivingEntity)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.rangedAttackEntityHost = attacker;
        this.entityHost = (EntityParasiteBase)attacker;
        this.entityMoveSpeed = movespeed;
        this.maxRangedAttackTime = maxAttackTime;
        this.attackRadius = maxAttackDistanceIn;
        this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        this.pullAway = away;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        if (!this.entityHost.onGround()) {
            return false;
        }
        LivingEntity entitylivingbase = this.entityHost.getTarget();
        if (entitylivingbase == null || this.entityHost.shouldWorkTask()) {
            return false;
        }
        this.attackTarget = entitylivingbase;
        return true;
    }

    public boolean canContinueToUse() {
        return (this.canUse() || !this.entityHost.getNavigation().isDone()) && !this.entityHost.shouldWorkTask();
    }

    public void stop() {
    }

    public void tick() {
        if (this.entityHost.srpTicks == 10) {
            if (this.entityHost.getGeneMod(3)) {
                this.entityHost.setParasiteStatus(2);
            } else {
                this.entityHost.setParasiteStatus(1);
            }
        }
        if (this.attackTarget == null) {
            this.stop();
        } else if (!this.attackTarget.isAlive()) {
            this.stop();
        } else {
            double follo;
            boolean flag2;
            double d0 = this.entityHost.distanceToSqr(this.attackTarget.getX(), this.attackTarget.getBoundingBox().minY, this.attackTarget.getZ());
            boolean flag = this.entityHost.getSensing().hasLineOfSight((Entity)this.attackTarget);
            boolean bl = flag2 = d0 <= (double)this.maxAttackDistance;
            this.seeTime = flag ? ++this.seeTime : 0;
            if (this.entityHost.srpTicks == 10 && d0 > (follo = this.entityHost.getAttribute(Attributes.FOLLOW_RANGE).getValue()) * follo) {
                this.entityHost.setTarget(null);
                return;
            }
            if (flag2 && this.seeTime >= 10) {
                this.entityHost.getNavigation().stop();
            } else {
                this.entityHost.getNavigation().moveTo((Entity)this.attackTarget, this.entityMoveSpeed);
            }
            if (d0 <= 25.0 && this.pullAway) {
                double deltaX = this.attackTarget.getX() - this.entityHost.getX();
                double deltaY = this.attackTarget.getY() - this.entityHost.getY();
                double deltaZ = this.attackTarget.getZ() - this.entityHost.getZ();
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                if (distance == 0.0) {
                    return;
                }
                double strength = 0.08;
                Mot.addX(this.entityHost, -((deltaX /= distance) * strength));
                Mot.addY(this.entityHost, -((deltaY /= distance) * strength));
                Mot.addZ(this.entityHost, -((deltaZ /= distance) * strength));
            }
            this.entityHost.getLookControl().setLookAt((Entity)this.attackTarget, 30.0f, 30.0f);
            if (this.entityHost.hasEffect(SRPPotions.RAGE_E)) {
                --this.rangedAttackTime;
            }
            if (--this.rangedAttackTime <= 0) {
                if (!flag) {
                    return;
                }
                if (flag && flag2) {
                    float f = (float)Math.sqrt((double)d0) / this.attackRadius;
                    float lvt_5_1_ = Mth.clamp((float)f, (float)0.1f, (float)1.0f);
                    this.rangedAttackEntityHost.performRangedAttack(this.attackTarget, lvt_5_1_);
                    this.entityHost.resetIdleTime();
                    this.rangedAttackTime = this.maxRangedAttackTime;
                }
            } else if (this.rangedAttackTime < 0) {
                // empty if block
            }
        }
    }
}

