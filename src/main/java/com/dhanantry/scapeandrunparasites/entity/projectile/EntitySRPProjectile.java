package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class EntitySRPProjectile
extends Fireball {
    public EntitySRPProjectile(EntityType<? extends EntitySRPProjectile> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntitySRPProjectile(EntityType<? extends EntitySRPProjectile> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, shooter, new Vec3(accelX, accelY, accelZ), worldIn);
    }

    /** 1.12 isBurning(): the projectile does not set itself on fire. */
    @Override
    protected boolean shouldBurn() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    protected boolean attackEntityAsMobMinimum(Entity entityIn, EntityParasiteBase attacker) {
        if (entityIn == null || attacker == null) {
            return false;
        }
        if (!attacker.isAlive()) {
            return false;
        }
        if (entityIn instanceof LivingEntity && !(entityIn instanceof EntityParasiteBase)) {
            return attacker.attackEntityAsMobMinimum((LivingEntity)entityIn, attacker.getMiniDamage());
        }
        return false;
    }
}

