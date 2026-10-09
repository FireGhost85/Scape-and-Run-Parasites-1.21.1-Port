package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.Mot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EntityAIEvadeDash
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityParasiteBase parent;
    private int eCooldown;
    private int cooldown;
    private int eDuration;
    private int tracker;
    private boolean inC;
    private int blockDistance;
    private double dash;
    private int maxDis;

    public EntityAIEvadeDash(EntityParasiteBase in, int cooldown, int duration, int distance) {
        this.parent = in;
        this.tracker = 0;
        this.eCooldown = cooldown;
        this.cooldown = cooldown + 1;
        this.eDuration = duration;
        this.blockDistance = distance * distance;
        this.inC = false;
        this.dash = 3.0;
        this.maxDis = 225;
    }

    public EntityAIEvadeDash(EntityParasiteBase in, int cooldown, int duration, int distance, double dash, int maxD) {
        this(in, cooldown, duration, distance);
        this.dash = dash;
        this.maxDis = maxD * maxD;
    }

    public boolean canUse() {
        return (this.parent.getTarget() != null || this.inC) && this.parent.getParasiteStatus() > 0 && this.parent.getParasiteStatus() < 3 && this.parent.onGround();
    }

    public void stop() {
        this.cooldown = 0;
        this.tracker = 0;
        this.inC = false;
        this.parent.xxa = 0.0f;
    }

    public void tick() {
        LivingEntity target = this.parent.getTarget();
        if (target != null || this.inC) {
            if (this.inC) {
                ++this.tracker;
                if (this.tracker >= this.eDuration) {
                    this.parent.xxa = 0.0f;
                    this.tracker = 0;
                    this.cooldown = 0;
                    this.inC = false;
                }
            } else {
                if (this.parent.distanceToSqr((Entity)target) > (double)this.blockDistance && this.parent.hasLineOfSight((Entity)target) && this.parent.distanceToSqr((Entity)target) < (double)this.maxDis && this.cooldown < this.eCooldown) {
                    ++this.cooldown;
                }
                if (this.cooldown >= this.eCooldown) {
                    this.parent.particleStatus((byte)10);
                    double dd0 = target.getX() - this.parent.getX();
                    double dd1 = target.getZ() - this.parent.getZ();
                    float f = (float)Math.sqrt((double)(dd0 * dd0 + dd1 * dd1));
                    double i = 0.0;
                    double j = 0.0;
                    if (this.parent.level().random.nextBoolean()) {
                        i = this.dash;
                    } else {
                        j = this.dash;
                    }
                    Mot.addX(this.parent, dd0 / (double)f * this.dash * (double)0.8f + this.parent.getDeltaMovement().x * (double)0.2f + i);
                    Mot.addZ(this.parent, dd1 / (double)f * this.dash * (double)0.8f + this.parent.getDeltaMovement().z * (double)0.2f + j);
                    this.parent.getNavigation().stop();
                    this.stop();
                }
            }
        }
    }
}

