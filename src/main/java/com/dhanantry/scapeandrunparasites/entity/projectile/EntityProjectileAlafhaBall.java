package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.EntityOrbBoom;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class EntityProjectileAlafhaBall
extends EntitySRPProjectile {
    public EntityProjectileAlafhaBall(EntityType<? extends EntityProjectileAlafhaBall> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityProjectileAlafhaBall(EntityType<? extends EntityProjectileAlafhaBall> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, worldIn, shooter, accelX, accelY, accelZ);
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.POOF;
    }

    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            DamageSource damagesource = this.getOwner() == null ? this.damageSources().thrown((Entity)this, (Entity)this) : this.damageSources().thrown((Entity)this, (Entity)this.getOwner());
            for (LivingEntity entitylivingbase : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.0, 3.0, 3.0))) {
                if (entitylivingbase instanceof EntityParasiteBase) continue;
                SRPPotions.applyStackPotion(SRPPotions.DLER_E, entitylivingbase, 300, 0);
                entitylivingbase.hurt(damagesource, SRPAttributes.ALAFHA_ATTACK_DAMAGE);
                this.attackEntityAsMobMinimum((Entity)entitylivingbase, (EntityParasiteBase)this.getOwner());
            }
            this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0f, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
            this.spawnLingeringCloud();
            this.discard();
        }
    }

    private void spawnLingeringCloud() {
        if (this.getOwner() != null && this.getOwner() instanceof EntityHeblu) {
            EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY(), this.getZ());
            entityareaeffectcloud.setRadius(5.0f, 0.9f);
            entityareaeffectcloud.setDuration(60);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.setOwner((EntityParasiteBase)this.getOwner());
            entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 300, 0, false, true));
            this.level().addFreshEntity((Entity)entityareaeffectcloud);
            this.level().broadcastEntityEvent((Entity)entityareaeffectcloud, (byte)77);
            EntityOrbBoom orb = new EntityOrbBoom(SRPEntities.ORBBOOM.get(), this.level(), (EntityPMalleable)this.getOwner(), 15, 1);
            orb.copyPosition(entityareaeffectcloud);
            this.level().addFreshEntity((Entity)orb);
            return;
        }
        EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY(), this.getZ());
        entityareaeffectcloud.setRadius(2.0f, 0.5f);
        entityareaeffectcloud.setRadiusOnUse(-0.5f);
        entityareaeffectcloud.setWaitTime(30);
        entityareaeffectcloud.setDuration(60);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.DLER_E, 360, 0, false, false));
        this.level().addFreshEntity((Entity)entityareaeffectcloud);
    }
}

