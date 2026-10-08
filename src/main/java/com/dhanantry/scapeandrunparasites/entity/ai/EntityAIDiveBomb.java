package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.EnumSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EntityAIDiveBomb
extends Goal {
    private final Mob host;
    private final int cooldownTicks;
    private final int hoverTicksMax;
    private final double diveSpeed;
    private final float explosionPower;
    private static final double ASCEND_HEIGHT = 20.0;
    private static final double ASCEND_STEP_MAX = 0.6;
    private static final int MAX_ASCEND_TICKS = 120;
    private Phase phase = Phase.IDLE;
    private long nextAllowedTick = 0L;
    private int hoverTicks = 0;
    private int diveTicks = 0;
    private int ascendTicks = 0;
    private double ascendStartY = 0.0;
    private double ascendTargetY = 0.0;
    private double hoverY = 0.0;
    private Vec3 lockedTargetPos = Vec3.ZERO;

    public EntityAIDiveBomb(Mob host, int cooldownTicks, int hoverTicks, double diveSpeed, float explosionPower) {
        this.host = host;
        this.cooldownTicks = cooldownTicks;
        this.hoverTicksMax = hoverTicks;
        this.diveSpeed = diveSpeed;
        this.explosionPower = explosionPower;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    public boolean canUse() {
        if (this.host.level().isClientSide) {
            return false;
        }
        if (this.host.level().getGameTime() < this.nextAllowedTick) {
            return false;
        }
        if (!(this.host instanceof EntityNogla)) {
            return false;
        }
        if (!((EntityNogla)this.host).isRicardoVariant()) {
            return false;
        }
        LivingEntity tgt = this.host.getTarget();
        return tgt != null && tgt.isAlive();
    }

    public boolean canContinueToUse() {
        if (!(this.host instanceof EntityNogla)) {
            return false;
        }
        if (!((EntityNogla)this.host).isRicardoVariant()) {
            return false;
        }
        return this.phase != Phase.IDLE;
    }

    public void start() {
        this.phase = Phase.ASCEND;
        this.hoverTicks = 0;
        this.diveTicks = 0;
        this.ascendTicks = 0;
        this.ascendStartY = this.host.getY();
        this.ascendTargetY = this.ascendStartY + 20.0;
        this.host.getNavigation().stop();
        this.host.fallDistance = 0.0f;
        this.host.setNoGravity(true);
        Mot.setZ(this.host, 0.0);
        Mot.setY(this.host, 0.0);
        Mot.setX(this.host, 0.0);
    }

    public void tick() {
        if (this.host.level().isClientSide) {
            return;
        }
        switch (this.phase) {
            case ASCEND: {
                this.host.setNoGravity(true);
                ++this.ascendTicks;
                double remaining = this.ascendTargetY - this.host.getY();
                if (remaining <= 0.05 || this.ascendTicks >= 120) {
                    this.hoverY = this.host.getY();
                    this.enterHover();
                    break;
                }
                Vec3 start = new Vec3(this.host.getX(), this.host.getY(), this.host.getZ());
                double step = Math.min(0.6, remaining);
                Vec3 end = new Vec3(this.host.getX(), this.host.getY() + step, this.host.getZ());
                HitResult up = SRPEntityUtil.rayTraceBlocks(this.host.level(), start, end);
                if (up != null && up.getType() == HitResult.Type.BLOCK) {
                    double yAtHit = up.getLocation().y - 0.05;
                    this.host.teleportTo(this.host.getX(), yAtHit, this.host.getZ());
                    this.hoverY = this.host.getY();
                    this.enterHover();
                    break;
                }
                this.host.teleportTo(this.host.getX(), this.host.getY() + step, this.host.getZ());
                Mot.setZ(this.host, 0.0);
                Mot.setY(this.host, 0.0);
                Mot.setX(this.host, 0.0);
                this.host.fallDistance = 0.0f;
                LivingEntity tA = this.host.getTarget();
                if (tA == null) break;
                this.faceTowards(tA.getX(), tA.getY() + (double)tA.getEyeHeight() * 0.5, tA.getZ());
                break;
            }
            case HOVER: {
                this.host.setNoGravity(true);
                double dy = this.hoverY - this.host.getY();
                if (Math.abs(dy) > 0.05) {
                    double nudge = Math.copySign(Math.min(0.2, Math.abs(dy)), dy);
                    this.host.teleportTo(this.host.getX(), this.host.getY() + nudge, this.host.getZ());
                }
                Mot.setZ(this.host, 0.0);
                Mot.setY(this.host, 0.0);
                Mot.setX(this.host, 0.0);
                this.host.fallDistance = 0.0f;
                LivingEntity tH = this.host.getTarget();
                if (tH == null || !tH.isAlive()) {
                    this.finish(false);
                    return;
                }
                this.faceTowards(tH.getX(), tH.getY() + (double)tH.getEyeHeight() * 0.5, tH.getZ());
                ++this.hoverTicks;
                int need = 20;
                if (this.hoverTicks < need) break;
                this.lockedTargetPos = new Vec3(tH.getX(), tH.getY() + (double)tH.getEyeHeight() * 0.5, tH.getZ());
                this.phase = Phase.DIVE;
                this.host.setNoGravity(true);
                this.diveTicks = 0;
                this.applyDiveVector(this.lockedTargetPos, 1.6);
                break;
            }
            case DIVE: {
                this.host.getNavigation().stop();
                this.host.setNoGravity(true);
                this.host.fallDistance = 0.0f;
                ++this.diveTicks;
                Vec3 start = new Vec3(this.host.getX(), this.host.getY(), this.host.getZ());
                Vec3 next = new Vec3(this.host.getX() + this.host.getDeltaMovement().x, this.host.getY() + this.host.getDeltaMovement().y, this.host.getZ() + this.host.getDeltaMovement().z);
                HitResult r = SRPEntityUtil.rayTraceBlocks(this.host.level(), start, next);
                if (r != null && r.getType() == HitResult.Type.BLOCK) {
                    this.explodeAndFinish();
                    return;
                }
                LivingEntity tD = this.host.getTarget();
                if (tD != null && tD.isAlive()) {
                    if (this.host.getBoundingBox().intersects(tD.getBoundingBox())) {
                        this.explodeAndFinish();
                        return;
                    }
                    if (this.host.distanceToSqr((Entity)tD) < 2.25) {
                        this.explodeAndFinish();
                        return;
                    }
                    Vec3 aim = new Vec3(tD.getX(), tD.getY() + (double)tD.getEyeHeight() * 0.5, tD.getZ());
                    double spd = this.computeDiveSpeed();
                    this.applyDiveVector(aim, spd);
                } else {
                    double spd = this.computeDiveSpeed();
                    this.applyDiveVector(this.lockedTargetPos, spd);
                }
                if (this.diveTicks <= 80) break;
                this.explodeAndFinish();
                break;
            }
        }
    }

    private void enterHover() {
        this.phase = Phase.HOVER;
        this.hoverTicks = 0;
        Mot.setZ(this.host, 0.0);
        Mot.setY(this.host, 0.0);
        Mot.setX(this.host, 0.0);
        this.host.hurtMarked = true;
        this.host.fallDistance = 0.0f;
    }

    private double computeDiveSpeed() {
        double base = this.diveSpeed <= 0.0 ? 2.8 : this.diveSpeed;
        double accel = 0.35 * (double)this.diveTicks;
        double cap = Math.max(base, 4.5);
        return Math.min(base + accel, cap);
    }

    private void applyDiveVector(Vec3 target, double speed) {
        Vec3 from = new Vec3(this.host.getX(), this.host.getY(), this.host.getZ());
        Vec3 dir = target.subtract(from).normalize();
        Mot.setX(this.host, dir.x * speed);
        Mot.setY(this.host, dir.y * speed);
        Mot.setZ(this.host, dir.z * speed);
        this.host.hurtMarked = true;
        this.faceTowards(this.host.getX() + this.host.getDeltaMovement().x, this.host.getY() + this.host.getDeltaMovement().y, this.host.getZ() + this.host.getDeltaMovement().z);
    }

    private void explodeAndFinish() {
        this.host.level().explode((Entity)this.host, this.host.getX(), this.host.getY(), this.host.getZ(), this.explosionPower, false, false ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
        this.finish(true);
    }

    private void finish(boolean setCd) {
        this.host.setNoGravity(false);
        Mot.setZ(this.host, 0.0);
        Mot.setY(this.host, 0.0);
        Mot.setX(this.host, 0.0);
        this.host.hurtMarked = true;
        this.host.fallDistance = 0.0f;
        this.phase = Phase.IDLE;
        if (setCd) {
            this.nextAllowedTick = this.host.level().getGameTime() + (long)this.cooldownTicks;
        }
    }

    public void stop() {
        this.finish(true);
    }

    private void faceTowards(double x, double y, double z) {
        float yaw;
        double dx = x - this.host.getX();
        double dz = z - this.host.getZ();
        this.host.setYRot(yaw = (float)(Mth.atan2((double)dz, (double)dx) * 57.29577951308232) - 90.0f);
        this.host.yHeadRot = yaw;
        this.host.yBodyRot = yaw;
    }

    private static enum Phase {
        IDLE,
        ASCEND,
        HOVER,
        DIVE;

    }
}

