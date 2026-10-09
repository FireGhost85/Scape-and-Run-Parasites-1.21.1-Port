package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.util.Mot;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class EntityAICircleGroup
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final PathfinderMob mob;
    private final double speed;
    private final int minGroup;
    private final double minRadius;
    private final double maxRadius;
    private final int scanRadius;
    private final Predicate<? super Entity> sameGroup;
    private float speedMul = 1.0f;
    private double effSpeed = 1.0;
    private float lapTicksBase = 100.0f;
    private double smCenterX;
    private double smCenterZ;
    private double smRadius;
    private double smTargetX;
    private double smTargetY;
    private double smTargetZ;
    private float smYaw;
    private int tickAge = 0;
    private float seedF;
    private float wobbleA = 0.8f;
    private float wobbleF = 0.06f;
    private float wanderA = 0.6f;
    private float wanderF = 0.09f;
    private float angJitterAmp = (float)Math.toRadians(0.6);
    private float angJitterFreq = 0.07f;
    private double centerX;
    private double centerZ;
    private double radius;
    private int dirSign = 0;
    private static final String NBT_RING_DIR = "SRP_RingDir";
    private float myAngle;
    private float angularVel;
    private int recalcCenterTicker = 0;
    private static final int RECALC_CENTER_EVERY = 10;
    private int recalcWaypointTicker = 0;
    private static final int RECALC_WAYPOINT_EVERY = 8;
    private final List<PathfinderMob> groupSnapshot = new ArrayList<PathfinderMob>();

    public EntityAICircleGroup(PathfinderMob mob, double speed, int minGroup, double minRadius, double maxRadius, int scanRadius, Predicate<? super Entity> sameGroup) {
        this.mob = mob;
        this.speed = speed;
        this.minGroup = minGroup;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.scanRadius = scanRadius;
        this.sameGroup = sameGroup == null ? e -> true : sameGroup;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        if (this.mob.isPassenger() || this.mob.getTarget() != null || this.mob.isInWater()) {
            return false;
        }
        this.snapshotGroup();
        if (this.groupSnapshot.size() < this.minGroup) {
            return false;
        }
        this.estimateCenterAndRadius();
        this.assignInitialAngle();
        return true;
    }

    public boolean canContinueToUse() {
        if (this.mob.isRemoved()) {
            return false;
        }
        if (this.mob.getTarget() != null) {
            return false;
        }
        this.snapshotGroup();
        int n = this.groupSnapshot.size();
        return n >= Math.max(2, this.minGroup - 2);
    }

    public void start() {
        this.tickAge = 0;
        this.seedF = (float)(this.mob.getId() % 997) * 0.73f;
        int s = this.mob.getId() * 1103515245 + 12345;
        float u01 = (float)((s ^ s >>> 16) & Integer.MAX_VALUE) / 2.14748365E9f;
        this.speedMul = 0.65f + 0.35f * u01;
        this.effSpeed = this.speed * (double)this.speedMul;
        this.smCenterX = this.centerX;
        this.smCenterZ = this.centerZ;
        this.smRadius = this.radius;
        this.smTargetX = this.mob.getX();
        this.smTargetY = this.mob.getY();
        this.smTargetZ = this.mob.getZ();
        this.smYaw = this.mob.getYRot();
        this.recalcCenterTicker = 0;
        this.recalcWaypointTicker = 0;
    }

    public void stop() {
        this.mob.getNavigation().stop();
    }

    public void tick() {
        ++this.tickAge;
        if (++this.recalcCenterTicker >= 10) {
            this.recalcCenterTicker = 0;
            this.snapshotGroup();
            this.estimateCenterAndRadius();
        }
        this.smCenterX += (this.centerX - this.smCenterX) * 0.15;
        this.smCenterZ += (this.centerZ - this.smCenterZ) * 0.15;
        this.smRadius += (this.radius - this.smRadius) * 0.2;
        this.dirSign = this.getGroupDirSign();
        float wMax = (float)(Math.PI * 2 / (double)this.lapTicksBase);
        this.angularVel = (float)this.dirSign * wMax * this.speedMul;
        float jitter = this.angJitterAmp * (0.5f * Mth.sin((float)(((float)this.tickAge + this.seedF) * this.angJitterFreq)) + 0.5f * Mth.cos((float)(((float)this.tickAge * 0.73f + this.seedF) * this.angJitterFreq * 0.7f)));
        this.myAngle = this.normalizeAngle(this.myAngle + (this.angularVel + jitter));
        double rEff = this.smRadius + (double)(this.wobbleA * Mth.sin((float)(((float)this.tickAge + this.seedF) * this.wobbleF)));
        double nx = Mth.cos((float)this.myAngle);
        double nz = Mth.sin((float)this.myAngle);
        double tnx = -Mth.sin((float)this.myAngle) * (float)this.dirSign;
        double tnz = Mth.cos((float)this.myAngle) * (float)this.dirSign;
        double side = this.wanderA * Mth.sin((float)(((float)this.tickAge + this.seedF * 3.0f) * this.wanderF));
        double rawX = this.smCenterX + nx * rEff + tnx * side;
        double rawZ = this.smCenterZ + nz * rEff + tnz * side;
        double rawY = this.findGroundY(this.mob.level(), rawX, rawZ, this.mob.getY());
        this.smTargetX += (rawX - this.smTargetX) * 0.35;
        this.smTargetZ += (rawZ - this.smTargetZ) * 0.35;
        double dy = rawY - this.smTargetY;
        if (dy > 0.4) {
            dy = 0.4;
        }
        if (dy < -0.4) {
            dy = -0.4;
        }
        this.smTargetY += dy;
        double dx = this.smTargetX - this.mob.getX();
        double dz = this.smTargetZ - this.mob.getZ();
        double distSq = dx * dx + dz * dz;
        if (distSq > 4.0 || ++this.recalcWaypointTicker >= 8) {
            this.recalcWaypointTicker = 0;
            this.mob.getNavigation().moveTo(this.smTargetX, this.smTargetY, this.smTargetZ, this.effSpeed);
        }
        float targetYaw = (float)(Mth.atan2((double)tnz, (double)tnx) * 57.29577951308232) - 90.0f;
        this.mob.setYRot(this.smYaw = this.approachAngle(this.smYaw, targetYaw, 20.0f));
        this.mob.yHeadRot = this.smYaw;
        this.mob.yBodyRot = this.smYaw;
        this.mob.getMoveControl().setWantedPosition(this.smTargetX, this.smTargetY, this.smTargetZ, this.effSpeed);
        double tangentPush = 0.03;
        Mot.addX(this.mob, tnx * 0.03);
        Mot.addZ(this.mob, tnz * 0.03);
        this.mob.getLookControl().setLookAt(this.smTargetX, this.smTargetY + (double)this.mob.getEyeHeight(), this.smTargetZ, 30.0f, 30.0f);
        this.pushApartSlightly(tnx, tnz);
    }

    private void snapshotGroup() {
        this.groupSnapshot.clear();
        AABB box = new AABB(this.mob.getX() - (double)this.scanRadius, this.mob.getY() - 8.0, this.mob.getZ() - (double)this.scanRadius, this.mob.getX() + (double)this.scanRadius, this.mob.getY() + 8.0, this.mob.getZ() + (double)this.scanRadius);
        this.groupSnapshot.add(this.mob);
        List<? extends Entity> nearby = this.mob.level().getEntities((Entity)this.mob, box);
        for (Entity e : nearby) {
            if (!(e instanceof PathfinderMob) || !this.sameGroup.test(e)) continue;
            this.groupSnapshot.add((PathfinderMob)e);
        }
        this.groupSnapshot.sort(Comparator.comparingInt(Entity::getId));
    }

    private void estimateCenterAndRadius() {
        if (this.groupSnapshot.isEmpty()) {
            this.centerX = this.mob.getX();
            this.centerZ = this.mob.getZ();
            this.radius = Mth.clamp((double)3.0, (double)this.minRadius, (double)this.maxRadius);
            return;
        }
        double sx = 0.0;
        double sz = 0.0;
        for (PathfinderMob e : this.groupSnapshot) {
            sx += e.getX();
            sz += e.getZ();
        }
        this.centerX = sx / (double)this.groupSnapshot.size();
        this.centerZ = sz / (double)this.groupSnapshot.size();
        double r = 0.0;
        for (PathfinderMob e : this.groupSnapshot) {
            double dx = e.getX() - this.centerX;
            double dz = e.getZ() - this.centerZ;
            r += Math.sqrt(dx * dx + dz * dz);
        }
        if ((r /= (double)this.groupSnapshot.size()) < this.minRadius * 0.6) {
            r = Math.max(this.minRadius, Math.min(this.maxRadius, 1.2 * Math.sqrt(this.groupSnapshot.size())));
        }
        this.radius = Mth.clamp((double)r, (double)this.minRadius, (double)this.maxRadius);
    }

    private void assignInitialAngle() {
        int idx = 0;
        int n = this.groupSnapshot.size();
        for (int i = 0; i < n; ++i) {
            if (this.groupSnapshot.get(i) != this.mob) continue;
            idx = i;
            break;
        }
        float baseAngle = (float)((double)idx * (Math.PI * 2 / (double)Math.max(1, n)));
        this.myAngle = this.normalizeAngle(baseAngle);
    }

    private int getGroupDirSign() {
        return (this.mob.getId() & 1) == 0 ? 1 : -1;
    }

    private float normalizeAngle(float a) {
        while ((double)a < -Math.PI) {
            a = (float)((double)a + Math.PI * 2);
        }
        while ((double)a > Math.PI) {
            a = (float)((double)a - Math.PI * 2);
        }
        return a;
    }

    private float approachAngle(float current, float target, float maxStep) {
        float delta;
        for (delta = target - current; delta < -180.0f; delta += 360.0f) {
        }
        while (delta > 180.0f) {
            delta -= 360.0f;
        }
        if (delta > maxStep) {
            delta = maxStep;
        }
        if (delta < -maxStep) {
            delta = -maxStep;
        }
        return current + delta;
    }

    private double findGroundY(Level world, double x, double z, double fallbackY) {
        BlockPos top = world.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, BlockPos.containing(Mth.floor((double)x), 0, Mth.floor((double)z)));
        int y = top.getY();
        if (Math.abs((double)y - fallbackY) > 6.0) {
            return fallbackY;
        }
        return (double)y + 0.2;
    }

    private void pushApartSlightly(double tnx, double tnz) {
        AABB bb = this.mob.getBoundingBox().inflate(0.6, 0.2, 0.6);
        List<? extends Entity> crowd = this.mob.level().getEntities((Entity)this.mob, bb);
        for (Entity e : crowd) {
            if (!(e instanceof LivingEntity)) continue;
            double dx = this.mob.getX() - e.getX();
            double dz = this.mob.getZ() - e.getZ();
            double d2 = dx * dx + dz * dz + 0.001;
            double strength = Math.min(0.035, 0.02 / d2);
            double px = (dx * 0.5 + tnx * 0.5) * strength;
            double pz = (dz * 0.5 + tnz * 0.5) * strength;
            Mot.addX(this.mob, px);
            Mot.addZ(this.mob, pz);
        }
    }
}

