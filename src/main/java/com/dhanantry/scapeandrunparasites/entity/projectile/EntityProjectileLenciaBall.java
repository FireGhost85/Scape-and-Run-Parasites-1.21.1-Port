package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
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
import net.neoforged.neoforge.event.EventHooks;

public class EntityProjectileLenciaBall
extends EntitySRPProjectile {
    public EntityProjectileLenciaBall(EntityType<? extends EntityProjectileLenciaBall> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityProjectileLenciaBall(EntityType<? extends EntityProjectileLenciaBall> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
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
                SRPEntityUtil.hitEntity(result).hurt(damagesource, (float)SRPAttributes.LENCIA_ATTACK_DAMAGE);
                this.attackEntityAsMobMinimum(SRPEntityUtil.hitEntity(result), (EntityParasiteBase)this.getOwner());
            }
            boolean flag = EventHooks.canEntityGrief((Level)this.level(), (Entity)this) && SRPConfigMobs.lenciaGriefing;
            ParasiteEventEntity.createExplosion(this.level(), (Entity)this.getOwner(), this.getX(), this.getY(), this.getZ(), 10.0f, flag);
            this.discard();
        }
    }
}

