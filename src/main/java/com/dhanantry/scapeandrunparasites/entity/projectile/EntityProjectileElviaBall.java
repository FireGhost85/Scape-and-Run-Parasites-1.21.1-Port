package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class EntityProjectileElviaBall
extends EntitySRPProjectile {
    public EntityProjectileElviaBall(EntityType<? extends EntityProjectileElviaBall> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityProjectileElviaBall(EntityType<? extends EntityProjectileElviaBall> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, worldIn, shooter, accelX, accelY, accelZ);
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.POOF;
    }

    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            if (SRPEntityUtil.hitEntity(result) != null && SRPEntityUtil.hitEntity(result) instanceof LivingEntity) {
                if (SRPEntityUtil.hitEntity(result) instanceof EntityParasiteBase && !(SRPEntityUtil.hitEntity(result) instanceof EntityNak)) {
                    this.discard();
                    return;
                }
                DamageSource damagesource = this.getOwner() == null ? this.damageSources().thrown((Entity)this, (Entity)this) : this.damageSources().thrown((Entity)this, (Entity)this.getOwner());
                SRPEntityUtil.hitEntity(result).hurt(damagesource, (float)SRPAttributes.ELVIA_ATTACK_DAMAGE);
                this.attackEntityAsMobMinimum(SRPEntityUtil.hitEntity(result), (EntityParasiteBase)this.getOwner());
            }
            this.discard();
        }
    }
}

