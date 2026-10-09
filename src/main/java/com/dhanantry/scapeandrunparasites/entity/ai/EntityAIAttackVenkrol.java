package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.EnumSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public class EntityAIAttackVenkrol
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

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
    protected final int attackInterval = 20;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;

    public EntityAIAttackVenkrol(EntityParasiteBase creature, double speedIn, boolean useLongMemory) {
        this.attacker = creature;
        this.world = creature.level();
        this.speedTowardsTarget = speedIn;
        this.longMemory = useLongMemory;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        LivingEntity entitylivingbase = this.attacker.getTarget();
        if (!this.attacker.shouldWorkTask()) {
            return false;
        }
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isAlive()) {
            return false;
        }
        if (this.canPenalize) {
            if (--this.delayCounter <= 0) {
                this.delayCounter = 4 + this.attacker.getRandom().nextInt(7);
                return this.path != null;
            }
            return true;
        }
        if (this.path != null) {
            return true;
        }
        return this.getAttackReachSqr(entitylivingbase) >= this.attacker.distanceToSqr(entitylivingbase.getX(), entitylivingbase.getBoundingBox().minY, entitylivingbase.getZ());
    }

    public boolean canContinueToUse() {
        LivingEntity entitylivingbase = this.attacker.getTarget();
        if (!this.attacker.shouldWorkTask()) {
            return false;
        }
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isAlive()) {
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
        this.attacker.setParasiteStatus(1);
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
        double d0 = this.attacker.distanceToSqr(entitylivingbase.getX(), entitylivingbase.getBoundingBox().minY, entitylivingbase.getZ());
        --this.delayCounter;
        if ((this.longMemory || this.attacker.getSensing().hasLineOfSight((Entity)entitylivingbase)) && this.delayCounter <= 0 && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0 || entitylivingbase.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0 || this.attacker.getRandom().nextFloat() < 0.05f)) {
            this.targetX = entitylivingbase.getX();
            this.targetY = entitylivingbase.getBoundingBox().minY;
            this.targetZ = entitylivingbase.getZ();
            this.delayCounter = 4 + this.attacker.getRandom().nextInt(7);
            if (this.canPenalize) {
                this.delayCounter += this.failedPathFindingPenalty;
                this.failedPathFindingPenalty += 10;
            }
            if (d0 > 1024.0) {
                this.delayCounter += 10;
            } else if (d0 > 256.0) {
                this.delayCounter += 5;
            }
            this.attacker.setParasiteStatus(1);
        }
        this.attacker.setParasiteStatus(1);
        this.attackTick = Math.max(this.attackTick - 1, 0);
        this.checkAndPerformAttack(entitylivingbase, d0);
    }

    protected void checkAndPerformAttack(LivingEntity target, double distance) {
        double d0 = this.getAttackReachSqr(target);
        if (distance <= d0 && this.attackTick <= 0) {
            this.attackTick = 20;
            this.attacker.swing(InteractionHand.MAIN_HAND);
            this.attacker.doHurtTarget((Entity)target);
        }
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return this.attacker.getBbWidth() * 2.0f * this.attacker.getBbWidth() * 2.0f + attackTarget.getBbWidth();
    }
}

