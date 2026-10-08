package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.Mot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityAIAttackProjectile
extends Goal {
    private final EntityParasiteBase parentEntity;
    private int attackTimer;
    private int cooldown;
    private int shootingTimes;
    private int shootingUpdate;
    private int tickInterval;
    private int heighA;
    private boolean canShootH;

    public EntityAIAttackProjectile(EntityParasiteBase parent, int cooldown, int tickInter, int shootingTim) {
        this(parent, cooldown, tickInter, shootingTim, false);
    }

    public EntityAIAttackProjectile(EntityParasiteBase parent, int cooldown, int tickInter, int shootingTim, boolean homming) {
        this.parentEntity = parent;
        this.attackTimer = 0;
        this.cooldown = cooldown;
        this.shootingTimes = shootingTim;
        this.shootingUpdate = 0;
        this.tickInterval = tickInter;
        this.canShootH = homming;
    }

    public boolean canUse() {
        return this.parentEntity.getTarget() != null;
    }

    public void stop() {
        this.shootingUpdate = 0;
        this.attackTimer = 0;
    }

    public void tick() {
        if (this.parentEntity.getTarget() == null || this.parentEntity.getParasiteStatus() >= 3) {
            this.attackTimer = 0;
        } else if (this.parentEntity.getTarget().isRemoved()) {
            this.attackTimer = 0;
        } else {
            LivingEntity entitylivingbase = this.parentEntity.getTarget();
            if (this.parentEntity.getParasiteStatus() == 0) {
                this.parentEntity.setParasiteStatus(1);
            }
            if (entitylivingbase.distanceToSqr((Entity)this.parentEntity) < 4225.0 && this.parentEntity.hasLineOfSight((Entity)entitylivingbase)) {
                ++this.attackTimer;
                if (this.parentEntity.hasEffect(SRPPotions.RAGE_E)) {
                    ++this.attackTimer;
                }
                if (this.attackTimer == this.cooldown - 10) {
                    EntityCanShoot shooter = (EntityCanShoot)(this.parentEntity);
                    shooter.playProjSound();
                }
                if (this.attackTimer > this.cooldown) {
                    if (this.shootingTimes > this.shootingUpdate) {
                        if (this.attackTimer % this.tickInterval == 0) {
                            this.shoot(entitylivingbase, this.parentEntity.level());
                            ++this.shootingUpdate;
                        }
                    } else {
                        this.attackTimer = 0;
                        this.shootingUpdate = 0;
                    }
                }
            } else if (this.attackTimer > 0) {
                --this.attackTimer;
            }
        }
    }

    private void shoot(LivingEntity entitylivingbase, Level world) {
        this.heighA = !entitylivingbase.onGround() && this.canShootH ? ++this.heighA : 0;
        if (this.heighA <= 5) {
            double d1 = 4.0;
            Vec3 vec3d = this.parentEntity.getViewVector(1.0f);
            double d2 = entitylivingbase.getX() - (this.parentEntity.getX() + vec3d.x);
            double d3 = entitylivingbase.getBoundingBox().minY + (double)(entitylivingbase.getBbHeight() / 2.0f) - (0.5 + this.parentEntity.getY() + (double)(this.parentEntity.getBbHeight() / 2.0f));
            double d4 = entitylivingbase.getZ() - (this.parentEntity.getZ() + vec3d.z);
            EntityCanShoot shooter = (EntityCanShoot)(this.parentEntity);
            Entity entitylargefireball = shooter.getProj(d2, d3, d4);
            Mot.setPosX(entitylargefireball, this.parentEntity.getX() + vec3d.x);
            Mot.setPosY(entitylargefireball, this.parentEntity.getY() + (double)this.parentEntity.getEyeHeight() - 0.2);
            Mot.setPosZ(entitylargefireball, this.parentEntity.getZ() + vec3d.z);
            world.addFreshEntity(entitylargefireball);
            this.parentEntity.resetIdleTime();
        }
    }
}

