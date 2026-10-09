package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class EntityAIEvadeTP
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityParasiteBase parent;
    private int eCooldown;
    private int cooldown;
    private int blockDistance;
    private int dash;
    private int currentDash;
    private int maxDis;
    private double targetIniDis;
    private double targetIniDisSegment;

    public EntityAIEvadeTP(EntityParasiteBase in, int cooldown, int distance) {
        this.parent = in;
        this.eCooldown = cooldown;
        this.cooldown = cooldown + 1;
        this.blockDistance = distance * distance;
        this.dash = 3;
        this.maxDis = 225;
    }

    public EntityAIEvadeTP(EntityParasiteBase in, int cooldown, int distance, int jumps, int maxD) {
        this(in, cooldown, distance);
        this.dash = jumps;
        this.maxDis = maxD * maxD;
    }

    public boolean canUse() {
        return this.parent.getTarget() != null && this.parent.getParasiteStatus() > 0 && this.parent.getParasiteStatus() < 3 && this.parent.onGround();
    }

    public void stop() {
        this.cooldown = 0;
        this.parent.xxa = 0.0f;
    }

    public void tick() {
        LivingEntity target = this.parent.getTarget();
        if (target != null) {
            if (this.parent.distanceToSqr((Entity)target) > (double)this.blockDistance && this.parent.hasLineOfSight((Entity)target) && this.parent.distanceToSqr((Entity)target) < (double)this.maxDis && this.cooldown < this.eCooldown) {
                ++this.cooldown;
                if (this.cooldown >= this.eCooldown) {
                    this.targetIniDis = this.parent.distanceToSqr((Entity)target);
                    this.targetIniDisSegment = this.targetIniDis / (double)(this.dash + 1);
                }
            }
            if (this.cooldown >= this.eCooldown) {
                this.parent.particleStatus((byte)10);
                RandomSource rand = RandomSource.create();
                double x = target.getX();
                double y = target.getY();
                double z = target.getZ();
                this.parent.getNavigation().stop();
                this.stop();
            }
        }
    }

    protected boolean teleportRandomly() {
        double d0 = this.parent.getX() + (this.parent.level().random.nextDouble() - 0.5) * 64.0;
        double d1 = this.parent.getY() + (double)(this.parent.level().random.nextInt(64) - 32);
        double d2 = this.parent.getZ() + (this.parent.level().random.nextDouble() - 0.5) * 64.0;
        if (this.parent.getTarget() != null && Math.sqrt(this.parent.getTarget().distanceToSqr(d0, d1, d2)) < 10.0) {
            return false;
        }
        return this.teleportTo(d0, d1, d2);
    }

    private boolean teleportTo(double x, double y, double z) {
        boolean flag = this.parent.randomTeleport(x, y, z, true);
        if (flag) {
            this.parent.level().playSound((Player)null, this.parent.xo, this.parent.yo, this.parent.zo, SRPSounds.INFECTEDENDERMAN_PORTAL.get(), this.parent.getSoundSource(), 1.0f, 1.0f);
            this.parent.playSound(SRPSounds.INFECTEDENDERMAN_PORTAL.get(), 1.0f, 1.0f);
        }
        return flag;
    }
}

