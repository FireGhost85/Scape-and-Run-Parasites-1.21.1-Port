package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityKirin;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EntityAIKirinBlink
extends Goal {
    private final EntityKirin kirin;
    private final Level world;
    private BlockPos targetPos = null;
    private int chargeTicks = 0;
    private static final int CHARGE_TIME = 60;
    private static final int COOLDOWN_TICKS = 200;
    private int nextAllowedTick = 0;
    private static final double MIN_FAR_DIST_SQ = 256.0;
    private static final int RADIUS_MAX = 24;
    private static final int MAX_TRIES = 64;

    public EntityAIKirinBlink(EntityKirin kirin) {
        this.kirin = kirin;
        this.world = kirin.level();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        if (this.kirin.tickCount < this.nextAllowedTick || this.kirin.getParasiteStatus() >= 3) {
            return false;
        }
        LivingEntity target = this.kirin.getTarget();
        if (target == null) {
            return false;
        }
        if (!this.kirin.getSensing().hasLineOfSight((Entity)target)) {
            return false;
        }
        if (this.isIndoors(target)) {
            return false;
        }
        if (this.kirin.distanceToSqr((Entity)target) <= 256.0) {
            return false;
        }
        BlockPos pos = this.findBlinkSpotNear(target);
        if (pos == null) {
            return false;
        }
        this.targetPos = pos;
        return true;
    }

    public boolean canContinueToUse() {
        return this.chargeTicks > 0;
    }

    public boolean isInterruptable() {
        return false;
    }

    public void start() {
        this.chargeTicks = 60;
        this.kirin.getNavigation().stop();
        Mot.setX(this.kirin, 0.0);
        Mot.setY(this.kirin, 0.0);
        Mot.setZ(this.kirin, 0.0);
        this.kirin.setBlinkCharge(this.targetPos, this.chargeTicks);
        this.kirin.playSound(SRPSounds.OMBOO_DEATH.get(), 1.0f, 0.9f);
    }

    public void tick() {
        if (this.targetPos == null) {
            this.chargeTicks = 0;
            return;
        }
        this.kirin.getNavigation().stop();
        Mot.setZ(this.kirin, 0.0);
        Mot.setY(this.kirin, 0.0);
        Mot.setX(this.kirin, 0.0);
        if (!this.world.isClientSide && this.chargeTicks % 10 == 0) {
            this.kirin.playSound(SoundEvents.NOTE_BLOCK_HAT.value(), 0.9f, 1.25f);
        }
        --this.chargeTicks;
        this.kirin.setBlinkCharge(this.targetPos, Math.max(this.chargeTicks, 0));
        if (!this.world.isClientSide && this.chargeTicks <= 0) {
            this.kirin.randomTeleport((double)this.targetPos.getX() + 0.5, this.targetPos.getY(), (double)this.targetPos.getZ() + 0.5, true);
            this.kirin.playSound(SRPSounds.INFECTEDENDERMAN_PORTAL.get(), 1.0f, 1.0f);
            this.doBlinkLifeSteal();
            this.nextAllowedTick = this.kirin.tickCount + 200;
            this.kirin.clearBlinkCharge();
            this.stop();
        }
    }

    public void stop() {
        this.targetPos = null;
        this.chargeTicks = 0;
    }

    private boolean isIndoors(LivingEntity target) {
        BlockPos head = BlockPos.containing(target.getX(), target.getY() + (double)target.getEyeHeight(), target.getZ());
        for (int i = 0; i < 3; ++i) {
            if (!this.world.canSeeSky(head.above(i))) continue;
            return false;
        }
        return true;
    }

    private BlockPos findBlinkSpotNear(LivingEntity target) {
        BlockPos tpos = target.blockPosition();
        for (int i = 0; i < 64; ++i) {
            int[] ys;
            double r = 1.5 + this.kirin.getRandom().nextDouble() * 22.5;
            double a = this.kirin.getRandom().nextDouble() * Math.PI * 2.0;
            int x = Mth.floor((double)((double)tpos.getX() + 0.5 + r * Math.cos(a)));
            int z = Mth.floor((double)((double)tpos.getZ() + 0.5 + r * Math.sin(a)));
            for (int y : ys = new int[]{tpos.getY(), tpos.getY() + 1, tpos.getY() - 1, tpos.getY() + 2, tpos.getY() - 2, tpos.getY() + 3, tpos.getY() - 3, tpos.getY() + 4, tpos.getY() - 4, tpos.getY() + 6, tpos.getY() - 6, tpos.getY() + 8, tpos.getY() - 8}) {
                BlockPos p = BlockPos.containing(x, y, z);
                if (!this.isSpotValid(p) || !this.world.canSeeSky(p.above()) || !this.hasLineOfSight(target, p)) continue;
                return p;
            }
        }
        return null;
    }

    private void doBlinkLifeSteal() {
        if (this.world.isClientSide) {
            return;
        }
        double radius = 5.0;
        AABB box = new AABB(this.kirin.getX() - radius, this.kirin.getY() - radius, this.kirin.getZ() - radius, this.kirin.getX() + radius, this.kirin.getY() + radius, this.kirin.getZ() + radius);
        List nearby = this.world.getEntitiesOfClass(LivingEntity.class, box, e -> e != null && e != this.kirin && e.isAlive() && !this.isSRPEntity((LivingEntity)e));
        if (nearby.isEmpty()) {
            this.world.playSound(null, this.kirin.getX(), this.kirin.getY(), this.kirin.getZ(), SRPSounds.ALAFHA_HURT.get(), SoundSource.HOSTILE, 0.7f, 0.9f + this.kirin.getRandom().nextFloat() * 0.2f);
            return;
        }
        LivingEntity target = (LivingEntity)nearby.get(0);
        float currentHealth = target.getHealth();
        if (currentHealth <= 0.0f) {
            return;
        }
        float stolen = currentHealth * 0.5f;
        float newHealth = currentHealth - stolen;
        if (newHealth < 0.0f) {
            newHealth = 0.0f;
        }
        target.setHealth(newHealth);
        this.forceHurtAnim(target);
        this.world.playSound(null, target.getX(), target.getY(), target.getZ(), SRPSounds.CRUX_HURT.get(), SoundSource.HOSTILE, 1.0f, 0.8f + this.kirin.getRandom().nextFloat() * 0.4f);
        if (stolen > 0.0f) {
            this.kirin.heal(stolen);
        }
    }

    public void forceHurtAnim(LivingEntity target) {
        target.animateHurt(0.0f);
    }

    private boolean isSRPEntity(LivingEntity entity) {
        String name = entity.getClass().getName();
        return name.startsWith("com.dhanantry.scapeandrunparasites.entity");
    }

    private boolean isSpotValid(BlockPos p) {
        if (!this.world.hasChunkAt(p)) {
            return false;
        }
        AABB aabb = new AABB(p).deflate(0.05);
        if (!this.world.noCollision(this.kirin, aabb)) {
            return false;
        }
        BlockPos below = p.below();
        return this.world.getBlockState(below).isSolid();
    }

    private boolean hasLineOfSight(LivingEntity target, BlockPos to) {
        Vec3 dest;
        Vec3 from = new Vec3(this.kirin.getX(), this.kirin.getY() + (double)this.kirin.getEyeHeight(), this.kirin.getZ());
        HitResult r = SRPEntityUtil.rayTraceBlocks(this.world, from, dest = new Vec3((double)to.getX() + 0.5, (double)to.getY() + 0.5, (double)to.getZ() + 0.5));
        if (r != null && r.getType() == HitResult.Type.BLOCK) {
            return false;
        }
        return this.kirin.getSensing().hasLineOfSight((Entity)target);
    }

    public static boolean tryBlinkToNearbyLand(EntityKirin kirin, int horizontalRange, int verticalRange) {
        Level world = kirin.level();
        BlockPos origin = kirin.blockPosition();
        BlockPos best = null;
        double bestDistSq = Double.MAX_VALUE;
        for (int dx = -horizontalRange; dx <= horizontalRange; ++dx) {
            block1: for (int dz = -horizontalRange; dz <= horizontalRange; ++dz) {
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                for (int dy = verticalRange; dy >= -verticalRange; --dy) {
                    int y = origin.getY() + dy;
                    BlockPos p = BlockPos.containing(x, y, z);
                    if (!EntityAIKirinBlink.isRecoverySpotValid(world, p)) continue;
                    double d = kirin.distanceToSqr(Vec3.atCenterOf(p));
                    if (!(d < bestDistSq)) continue block1;
                    bestDistSq = d;
                    best = p;
                    continue block1;
                }
            }
        }
        if (best != null) {
            return kirin.randomTeleport((double)best.getX() + 0.5, best.getY(), (double)best.getZ() + 0.5, true);
        }
        return false;
    }

    private static boolean isRecoverySpotValid(Level world, BlockPos p) {
        return world.getBlockState(p.below()).isCollisionShapeFullBlock(world, p.below()) && world.isEmptyBlock(p) && world.isEmptyBlock(p.above()) && world.canSeeSky(p.above());
    }
}

