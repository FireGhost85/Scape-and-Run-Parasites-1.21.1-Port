package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.Mot;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EntityAIEvade
extends Goal {
    private final EntityParasiteBase parent;
    private int eCooldown;
    private int cooldown;
    private int eDuration;
    private int tracker;
    private boolean inC;
    private double blockDistance;
    private boolean useDis;
    private int disValue;
    private boolean ignoreGene;

    public EntityAIEvade(EntityParasiteBase in, int cooldown, int duration, double distance) {
        this.parent = in;
        this.tracker = 0;
        this.eCooldown = cooldown;
        this.cooldown = cooldown + 1;
        this.eDuration = duration;
        this.blockDistance = distance * distance;
        this.inC = false;
    }

    public EntityAIEvade(EntityParasiteBase in, int cooldown, int duration, double distance, boolean use, int diss, boolean ignoreGen) {
        this(in, cooldown, duration, distance);
        this.useDis = use;
        this.disValue = diss;
        this.ignoreGene = ignoreGen;
    }

    public boolean canUse() {
        if (this.ignoreGene) {
            return (this.parent.getTarget() != null || this.inC) && this.parent.getParasiteStatus() > 0 && this.parent.getParasiteStatus() < 3 && this.parent.onGround() || this.inC;
        }
        return this.parent.getTarget() != null && this.parent.getParasiteStatus() > 0 && this.parent.getParasiteStatus() < 3 && this.parent.onGround() && this.parent.getGeneMod(5) || this.inC;
    }

    public void stop() {
        this.cooldown = 0;
        this.tracker = 0;
        this.inC = false;
        this.parent.xxa = 0.0f;
    }

    public void tick() {
        LivingEntity target = this.parent.getTarget();
        if ((target != null || this.inC) && !this.parent.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            if (this.inC && !this.useDis) {
                ++this.tracker;
                if (this.tracker >= this.eDuration) {
                    this.parent.xxa = 0.0f;
                    this.tracker = 0;
                    this.cooldown = 0;
                    this.inC = false;
                }
            } else {
                boolean flag = this.useDis ? this.parent.distanceToSqr((Entity)target) > (double)(this.disValue * this.disValue) : this.parent.distanceToSqr((Entity)target) < 225.0;
                if (this.parent.distanceToSqr((Entity)target) > this.blockDistance && this.parent.hasLineOfSight((Entity)target) && flag) {
                    ++this.cooldown;
                }
                if (this.cooldown >= this.eCooldown) {
                    this.parent.lookAt((Entity)target);
                    if (this.useDis) {
                        this.jumpTowardsTarget((Entity)target);
                        this.parent.getNavigation().stop();
                        return;
                    }
                    if (this.parent.xxa == 0.0f) {
                        int i = this.parent.getRandom().nextInt(2) == 0 ? 1 : -1;
                        this.parent.xxa = i;
                        this.inC = true;
                        double dd0 = target.getX() - this.parent.getX();
                        double dd1 = target.getZ() - this.parent.getZ();
                        float f = (float)Math.sqrt((double)(dd0 * dd0 + dd1 * dd1));
                        Mot.addX(this.parent, dd0 / (double)f * 1.77 * (double)0.8f + this.parent.getDeltaMovement().x * (double)0.2f);
                        Mot.addZ(this.parent, dd1 / (double)f * 1.77 * (double)0.8f + this.parent.getDeltaMovement().z * (double)0.2f);
                        Mot.setY(this.parent, 0.2 + (double)this.parent.getBbHeight() * 0.1);
                        this.parent.getNavigation().stop();
                    }
                }
            }
        }
    }

    private void jumpTowardsTarget(Entity target) {
        double dx = target.getX() - this.parent.getX();
        double dz = target.getZ() - this.parent.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance < (double)this.eDuration) {
            return;
        }
        double angle = Math.toRadians(45.0);
        double gravity = 0.8;
        double velocity = Math.sqrt(distance * gravity / Math.sin(2.0 * angle));
        velocity = Math.min(velocity, this.blockDistance);
        double horizontalVelocity = velocity * 1.9 * Math.cos(angle);
        double verticalVelocity = velocity * 0.6 * Math.sin(angle);
        double norm = distance;
        Mot.setX(this.parent, horizontalVelocity * (dx / norm));
        Mot.setZ(this.parent, horizontalVelocity * (dz / norm));
        Mot.setY(this.parent, verticalVelocity);
    }
}

