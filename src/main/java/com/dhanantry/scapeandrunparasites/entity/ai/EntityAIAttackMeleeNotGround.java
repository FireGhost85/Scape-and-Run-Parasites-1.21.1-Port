package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.Mot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EntityAIAttackMeleeNotGround
extends Goal {
    protected EntityParasiteBase attacker;
    protected int attackTick;
    private double attack;
    private double track;
    private int cooldown;
    private int currentCD;
    private double speed;
    private boolean needAir;
    private int off;

    public EntityAIAttackMeleeNotGround(EntityParasiteBase creature, double attackD, double tracking, double spee, boolean air) {
        this.attacker = creature;
        this.attack = attackD * attackD;
        this.track = tracking * tracking;
        this.cooldown = 0;
        this.currentCD = 80;
        this.speed = spee;
        this.needAir = air;
        this.off = 4;
    }

    public EntityAIAttackMeleeNotGround(EntityParasiteBase creature, double attackD, double tracking, double spee, boolean air, int cd, int offs) {
        this(creature, attackD, tracking, spee, air);
        this.currentCD = cd;
        this.off = offs;
    }

    public boolean canUse() {
        return this.attacker.getTarget() != null;
    }

    public void tick() {
        LivingEntity entitylivingbase = this.attacker.getTarget();
        if (entitylivingbase == null) {
            return;
        }
        if (!entitylivingbase.isAlive()) {
            this.attacker.setTarget(null);
        } else {
            if (this.needAir ? this.attacker.isInWater() : !this.attacker.isInWater()) {
                return;
            }
            ++this.cooldown;
            if (this.cooldown < this.currentCD) {
                return;
            }
            this.attacker.getLookControl().setLookAt((Entity)entitylivingbase, 30.0f, 30.0f);
            double d0 = this.attacker.distanceToSqr(entitylivingbase.getX(), entitylivingbase.getBoundingBox().minY, entitylivingbase.getZ());
            if (this.attacker.distanceToSqr((Entity)entitylivingbase) * 0.85 < this.track && this.attacker.hasLineOfSight((Entity)entitylivingbase)) {
                double dd0 = entitylivingbase.getX() - this.attacker.getX();
                double dd1 = entitylivingbase.getZ() - this.attacker.getZ();
                int yy = entitylivingbase.getY() >= this.attacker.getY() + (double)this.off ? 1 : -1;
                float f = (float)Math.sqrt((double)(dd0 * dd0 + dd1 * dd1));
                Mot.addX(this.attacker, dd0 / (double)f * this.speed + this.attacker.getDeltaMovement().x * this.speed);
                Mot.addZ(this.attacker, dd1 / (double)f * this.speed + this.attacker.getDeltaMovement().z * this.speed);
                Mot.setY(this.attacker, yy == 1 ? 0.52 * (double)yy : 0.2 * (double)yy);
                this.attackTick = Math.max(this.attackTick - 1, 0);
                this.checkAndPerformAttack(entitylivingbase, d0);
                if (!this.needAir && entitylivingbase.getY() >= this.attacker.getY() + 1.0) {
                    Mot.addY(this.attacker, -(0.2));
                }
                if (this.cooldown > 140) {
                    this.cooldown = 0;
                }
            }
        }
    }

    protected void checkAndPerformAttack(LivingEntity target, double distance) {
        if (distance <= this.attack && this.attackTick <= 0) {
            this.attackTick = 20;
            EntityCutomAttack thisA = (EntityCutomAttack)(this.attacker);
            thisA.attackEntityAsMobAOE((Entity)target);
        }
    }
}

