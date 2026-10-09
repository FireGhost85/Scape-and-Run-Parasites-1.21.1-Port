package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.Mot;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

public class EntityAIWaterLeapAtTargetStatus
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private EntityParasiteBase leaper;
    private LivingEntity leapTarget;
    private float leapMotionY;
    private float targetY;
    private double jumpSpeed;
    private int jCooldown;
    private int jumpR;
    private int attackTimer = 0;
    private int attacking = 0;
    private double targetX;
    private double targetZ;

    public EntityAIWaterLeapAtTargetStatus(EntityParasiteBase leapingEntity, float leapMotionYIn, double speed, int distance, int cooldown, int jumpDamageRange) {
        this.leaper = leapingEntity;
        this.leapMotionY = leapMotionYIn;
        this.jumpSpeed = speed;
        this.jCooldown = cooldown;
        this.jumpR = jumpDamageRange;
    }

    public boolean canUse() {
        return this.leaper.isInWater() || this.leaper.isInLava() || this.attacking >= 1;
    }

    public void tick() {
        if (this.leaper.getTarget() == null || !this.leaper.shouldWorkTask()) {
            if (this.attackTimer > 0) {
                --this.attackTimer;
            }
        } else if (this.leaper.getTarget().isRemoved() || this.leaper.getParasiteStatus() > 2) {
            if (this.attackTimer > 0) {
                --this.attackTimer;
            }
        } else {
            LivingEntity entitylivingbase = this.leaper.getTarget();
            ++this.attackTimer;
            if (this.attackTimer >= this.jCooldown && this.attacking == 0) {
                ++this.attacking;
                this.targetX = entitylivingbase.getX();
                this.targetZ = entitylivingbase.getZ();
                this.targetY = (float)(entitylivingbase.getY() - this.leaper.getY()) * 0.07f;
                if (this.targetY <= 0.0f) {
                    this.targetY = 0.0f;
                }
            }
        }
        if (this.attacking >= 1) {
            ++this.attacking;
            if (this.attacking == 2 && this.leaper.onGround()) {
                this.leaper.setParasiteStatus(10);
                if (this.leaper.getNavigation().getPath() != null) {
                    this.leaper.getNavigation().stop();
                }
                double d0 = this.targetX - this.leaper.getX();
                double d1 = this.targetZ - this.leaper.getZ();
                double f = (float)Math.sqrt((double)(d0 * d0 + d1 * d1));
                Mot.setY(this.leaper, (double)this.leapMotionY + (double)this.targetY);
                Mot.addX(this.leaper, d0 / f * this.jumpSpeed * 0.9 + this.leaper.getDeltaMovement().x * 0.3);
                Mot.addZ(this.leaper, d1 / f * this.jumpSpeed * 0.9 + this.leaper.getDeltaMovement().z * 0.3);
            }
            if (this.attacking >= 3 && this.leaper.onGround()) {
                if (this.jumpR != 0) {
                    float damage = (float)this.leaper.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                    AABB axisalignedbb = new AABB(this.leaper.getX(), this.leaper.getY(), this.leaper.getZ(), this.leaper.getX() + 1.0, this.leaper.getY() + 1.0, this.leaper.getZ() + 1.0).inflate((double)this.jumpR, 2.0, (double)this.jumpR);
                    List<? extends LivingEntity> moblist = this.leaper.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                    for (LivingEntity mob : moblist) {
                        if (mob == this.leaper || mob instanceof EntityParasiteBase) continue;
                        mob.knockback(2.5, this.leaper.getX() - mob.getX(), this.leaper.getZ() - mob.getZ());
                        this.leaper.doHurtTarget((Entity)mob);
                    }
                }
                this.attacking = 0;
                this.attackTimer = 0;
                this.leaper.setParasiteStatus(2);
            }
        }
    }
}

