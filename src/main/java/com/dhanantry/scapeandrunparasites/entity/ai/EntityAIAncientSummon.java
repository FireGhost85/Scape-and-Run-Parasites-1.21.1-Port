package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EntityAIAncientSummon
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityParasiteBase parentEntity;
    private int limit = 0;
    private int sCooldown;
    private int sLimit;
    private String[] sMobs;
    private int attackTimer = 0;
    private int attacking = 0;
    private double targetX;
    private double targetY;
    private double targetZ;

    public EntityAIAncientSummon(EntityParasiteBase ancient, int cooldown, int limit, String[] mobList) {
        this.parentEntity = ancient;
        this.sCooldown = cooldown;
        this.sLimit = limit;
        this.sMobs = mobList;
    }

    public boolean canUse() {
        return this.parentEntity.getTarget() != null || this.attacking >= 1;
    }

    public void start() {
    }

    public void stop() {
        this.attackTimer = 0;
        this.limit = 0;
    }

    public void tick() {
        if (this.attacking >= 1) {
            ++this.attacking;
            if (this.attacking == 2) {
                this.parentEntity.playSound(SRPSounds.ANCIENT_POD.get(), 5.0f, 1.0f);
            }
            if (this.attacking % 20 == 0 && this.attacking >= 40 && ParasiteSummon.SummonM(this.parentEntity, this.sMobs, 10, this.targetX, this.targetY, this.targetZ, this.parentEntity.getTarget(), true)) {
                ++this.limit;
            }
            if (this.limit >= this.sLimit) {
                this.attacking = 0;
                this.attackTimer = 0;
                this.limit = 0;
            }
        } else if (this.parentEntity.getTarget() == null) {
            this.attackTimer = 0;
        } else if (this.parentEntity.getTarget().isRemoved()) {
            this.attackTimer = 0;
        } else {
            LivingEntity entitylivingbase = this.parentEntity.getTarget();
            if (this.parentEntity.hasLineOfSight((Entity)entitylivingbase) && this.parentEntity.distanceToSqr((Entity)entitylivingbase) < 2500.0) {
                if (entitylivingbase.onGround() || this.parentEntity.level().random.nextBoolean()) {
                    ++this.attackTimer;
                }
                if (this.attackTimer >= this.sCooldown && this.attacking == 0) {
                    ++this.attacking;
                    this.targetX = entitylivingbase.getX();
                    this.targetY = this.parentEntity.getY() + 25.0;
                    this.targetZ = entitylivingbase.getZ();
                }
            } else {
                this.attackTimer = 0;
            }
        }
    }
}

