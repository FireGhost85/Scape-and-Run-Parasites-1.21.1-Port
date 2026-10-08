package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public class EntityAIAttackMeleeStatus
extends Goal {
    Level world;
    protected EntityParasiteBase attacker;
    protected int attackTick;
    double speedTowardsTarget;
    boolean longMemory;
    Path path;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
    private double blockDistance;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;

    public EntityAIAttackMeleeStatus(EntityParasiteBase creature, double speedIn, boolean useLongMemory, double runningD) {
        this.attacker = creature;
        this.world = creature.level();
        this.speedTowardsTarget = speedIn;
        this.longMemory = useLongMemory;
        this.blockDistance = runningD * runningD;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        LivingEntity entitylivingbase = this.attacker.getTarget();
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isAlive() || !this.attacker.shouldWorkTask()) {
            return false;
        }
        if (this.canPenalize) {
            if (--this.delayCounter <= 0) {
                this.path = this.attacker.getNavigation().createPath((Entity)entitylivingbase, 0);
                this.delayCounter = 4 + this.attacker.getRandom().nextInt(7);
                return this.path != null;
            }
            return true;
        }
        this.path = this.attacker.getNavigation().createPath((Entity)entitylivingbase, 0);
        if (this.path != null) {
            return true;
        }
        return this.getAttackReachSqr(entitylivingbase) >= this.attacker.distanceToSqr(entitylivingbase.getX(), entitylivingbase.getBoundingBox().minY, entitylivingbase.getZ());
    }

    public boolean canContinueToUse() {
        LivingEntity entitylivingbase = this.attacker.getTarget();
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isAlive() || !this.attacker.shouldWorkTask()) {
            return false;
        }
        if (!this.longMemory) {
            return !this.attacker.getNavigation().isDone();
        }
        if (!this.attacker.isWithinRestriction(entitylivingbase.blockPosition())) {
            return false;
        }
        return !(entitylivingbase instanceof Player) || !((Player)entitylivingbase).isSpectator() && !((Player)entitylivingbase).isCreative();
    }

    public void start() {
        LivingEntity entitylivingbase = this.attacker.getTarget();
        if (this.attacker.getParasiteStatus() <= 2 && this.attacker.getLLeap() < 1) {
            if ((this.attacker.distanceToSqr((Entity)entitylivingbase) > this.blockDistance || this.attacker.getAttackCooldownAni() == 0) && this.attacker.getGeneMod(3)) {
                this.attacker.getNavigation().moveTo(this.path, this.speedTowardsTarget);
                this.attacker.setParasiteStatus(2);
                this.attacker.setAttackCooldownAni(0);
            } else {
                this.attacker.getNavigation().moveTo(this.path, 1.0);
                this.attacker.setParasiteStatus(1);
            }
        }
        this.delayCounter = 0;
    }

    public void stop() {
        LivingEntity entitylivingbase = this.attacker.getTarget();
        if (entitylivingbase instanceof Player && (((Player)entitylivingbase).isSpectator() || ((Player)entitylivingbase).isCreative())) {
            this.attacker.setTarget(null);
        }
        this.attacker.getNavigation().stop();
    }

    public void tick() {
        LivingEntity entitylivingbase = this.attacker.getTarget();
        if (entitylivingbase == null) {
            return;
        }
        this.attacker.getLookControl().setLookAt((Entity)entitylivingbase, 30.0f, 30.0f);
        double d0 = this.attacker.distanceToSqr(entitylivingbase.getX(), entitylivingbase.getBoundingBox().minY, entitylivingbase.getZ());
        --this.delayCounter;
        if ((this.longMemory || this.attacker.getSensing().hasLineOfSight((Entity)entitylivingbase)) && this.delayCounter <= 0 && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0 || entitylivingbase.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0 || this.attacker.getRandom().nextFloat() < 0.05f)) {
            this.targetX = entitylivingbase.getX();
            this.targetY = entitylivingbase.getBoundingBox().minY;
            this.targetZ = entitylivingbase.getZ();
            this.delayCounter = 4 + this.attacker.getRandom().nextInt(7);
            if (this.canPenalize) {
                Node finalPathPoint;
                this.delayCounter += this.failedPathFindingPenalty;
                this.failedPathFindingPenalty = this.attacker.getNavigation().getPath() != null ? ((finalPathPoint = this.attacker.getNavigation().getPath().getEndNode()) != null && entitylivingbase.distanceToSqr((double)finalPathPoint.x, (double)finalPathPoint.y, (double)finalPathPoint.z) < 1.0 ? 0 : (this.failedPathFindingPenalty += 10)) : (this.failedPathFindingPenalty += 10);
            }
            if (d0 > 1024.0) {
                this.delayCounter += 10;
            } else if (d0 > 256.0) {
                this.delayCounter += 5;
            }
            if (this.attacker.getParasiteStatus() <= 2 && this.attacker.getLLeap() < 1) {
                if ((this.attacker.distanceToSqr((Entity)entitylivingbase) > this.blockDistance || this.attacker.getAttackCooldownAni() == 0) && this.attacker.getGeneMod(3)) {
                    if (!this.attacker.getNavigation().moveTo((Entity)entitylivingbase, this.speedTowardsTarget)) {
                        this.delayCounter += 15;
                    }
                    this.attacker.setParasiteStatus(2);
                    this.attacker.setAttackCooldownAni(0);
                } else {
                    if (!this.attacker.getNavigation().moveTo((Entity)entitylivingbase, 1.0)) {
                        this.delayCounter += 15;
                    }
                    this.attacker.setParasiteStatus(1);
                }
            }
        }
        --this.attackTick;
        if (this.attackTick > 0 && this.attacker.hasEffect(SRPPotions.PIVOT_E)) {
            this.attackTick -= this.attacker.getEffect(SRPPotions.PIVOT_E).getAmplifier() * 2;
        }
        this.checkAndPerformAttack(entitylivingbase, d0);
    }

    protected void checkAndPerformAttack(LivingEntity target, double distance) {
        double d0 = this.getAttackReachSqr(target);
        if (distance <= d0 && this.attackTick <= 0 && this.attacker.hasLineOfSight((Entity)target)) {
            this.attackTick = this.attacker.getAttackSpeed();
            this.attacker.doHurtTarget((Entity)target);
        }
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return this.attacker.getBbWidth() * 2.0f * this.attacker.getBbWidth() * 2.0f + attackTarget.getBbWidth();
    }

    protected boolean isTargetParasite(LivingEntity target) {
        if (SRPConfigSystems.useOneMind && target instanceof EntityParasiteBase) {
            if (((EntityParasiteBase)target).getTarget() instanceof EntityParasiteBase) {
                this.attacker.setTarget(null);
                return true;
            }
            if (SRPPotions.applySense((LivingEntity)this.attacker, 200, this.attacker.distanceToSqr((Entity)target), SRPConfigSystems.oneMinRangeCap)) {
                this.attacker.setTarget(((EntityParasiteBase)target).getTarget());
            } else {
                this.attacker.setTarget(null);
            }
            return true;
        }
        return false;
    }
}

