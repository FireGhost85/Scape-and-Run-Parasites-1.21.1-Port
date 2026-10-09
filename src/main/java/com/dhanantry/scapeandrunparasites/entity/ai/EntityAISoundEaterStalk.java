package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class EntityAISoundEaterStalk
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityInfHuman mob;
    private final double hearingRangeSq;
    private int ticksSinceNoise = 0;
    private static final int QUIET_THRESHOLD_TICKS = 100;

    public EntityAISoundEaterStalk(EntityInfHuman mob, double hearingRange) {
        this.mob = mob;
        this.hearingRangeSq = hearingRange * hearingRange;
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
    }

    public boolean canUse() {
        return this.mob.getSkin() == 111;
    }

    public boolean canContinueToUse() {
        return this.mob.getSkin() == 111 && !this.mob.isRemoved();
    }

    public void start() {
        this.ticksSinceNoise = 0;
    }

    public void stop() {
        this.ticksSinceNoise = 0;
    }

    public void tick() {
        BlockPos soundPos;
        if (this.mob.level().isClientSide) {
            return;
        }
        this.mob.tickSoundMemory();
        Player nearest = this.mob.level().getNearestPlayer((Entity)this.mob, Math.sqrt(this.hearingRangeSq));
        if (nearest != null && (nearest.isSpectator() || nearest.getAbilities().instabuild)) {
            nearest = null;
        }
        boolean noisy = false;
        boolean canSee = false;
        if (nearest != null && nearest.isAlive() && this.mob.distanceToSqr((Entity)nearest) <= this.hearingRangeSq) {
            noisy = this.isTargetNoisy((LivingEntity)nearest);
            canSee = this.mob.hasLineOfSight((Entity)nearest);
        }
        if (nearest == null || !nearest.isAlive() || this.mob.distanceToSqr((Entity)nearest) > this.hearingRangeSq) {
            ++this.ticksSinceNoise;
            if (this.ticksSinceNoise >= 100) {
                this.forceDropAggro();
            }
        } else if (noisy) {
            this.ticksSinceNoise = 0;
            soundPos = BlockPos.containing(nearest.getX(), nearest.getY(), nearest.getZ());
            this.mob.notifyHeardSound(soundPos, 100);
            if (canSee) {
                if (this.mob.getTarget() != nearest) {
                    this.mob.setTarget((LivingEntity)nearest);
                }
            } else if (this.mob.getTarget() == nearest) {
                this.mob.setTarget(null);
                this.mob.getNavigation().stop();
            }
        } else if (!canSee) {
            if (this.mob.getTarget() instanceof Player) {
                this.mob.setTarget(null);
                this.mob.getNavigation().stop();
            }
            ++this.ticksSinceNoise;
            if (this.ticksSinceNoise >= 100) {
                this.forceDropAggro();
            }
        } else {
            this.ticksSinceNoise = 0;
        }
        if (this.mob.getTarget() == null && (soundPos = this.mob.getHeardSoundPos()) != null) {
            double sz;
            double sy;
            double sx = (double)soundPos.getX() + 0.5;
            if (this.mob.distanceToSqr(sx, sy = (double)soundPos.getY(), sz = (double)soundPos.getZ() + 0.5) < 2.0) {
                this.mob.clearHeardSound();
            } else {
                this.mob.getNavigation().moveTo(sx, sy, sz, 1.2);
            }
        }
    }

    private void forceDropAggro() {
        this.mob.setTarget(null);
        this.mob.setLastHurtByMob(null);
        this.mob.getNavigation().stop();
        this.ticksSinceNoise = 0;
    }

    private boolean isTargetNoisy(LivingEntity t) {
        if (!(t instanceof Player)) {
            return false;
        }
        Player p = (Player)t;
        if (p.isSpectator() || p.getAbilities().instabuild) {
            return false;
        }
        if (p.isShiftKeyDown()) {
            return false;
        }
        if (p.isSprinting()) {
            return true;
        }
        if (p.isUsingItem()) {
            return true;
        }
        if (p.swinging || p.swingTime > 0) {
            return true;
        }
        if (Math.abs(p.getDeltaMovement().y) > 0.08) {
            return true;
        }
        double dx = p.getDeltaMovement().x;
        double dz = p.getDeltaMovement().z;
        double speedSq = dx * dx + dz * dz;
        return speedSq > 0.003;
    }
}

