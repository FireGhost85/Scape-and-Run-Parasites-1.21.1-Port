package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

public class EntityProjectileDragonE
extends EntitySRPProjectile {
    private int ticksInAir;

    public EntityProjectileDragonE(EntityType<? extends EntityProjectileDragonE> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityProjectileDragonE(EntityType<? extends EntityProjectileDragonE> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, worldIn, shooter, accelX, accelY, accelZ);
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }

    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(4.0);
            List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity mob : moblist) {
                if (mob instanceof EntityParasiteBase && !(mob instanceof EntityNak)) continue;
                SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 200, 1);
                DamageSource damagesource = this.getOwner() == null ? this.damageSources().thrown((Entity)this, (Entity)this) : this.damageSources().thrown((Entity)this, (Entity)this.getOwner());
                mob.hurt(damagesource, (float)SRPAttributes.INFDRAGONE_RANGED_ATTACK_DAMAGE);
                this.attackEntityAsMobMinimum((Entity)mob, (EntityParasiteBase)this.getOwner());
            }
            EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY(), this.getZ());
            entityareaeffectcloud.setRadius(3.5f, 0.5f);
            entityareaeffectcloud.setRadiusOnUse(-0.5f);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(100);
            entityareaeffectcloud.setParticle(ParticleTypes.DRAGON_BREATH);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 0));
            entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
            this.level().addFreshEntity((Entity)entityareaeffectcloud);
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            for (int i = 0; i <= 4; ++i) {
                double d0 = this.getRandom().nextGaussian() * 0.02;
                double d1 = this.getRandom().nextGaussian() * 0.02;
                double d2 = this.getRandom().nextGaussian() * 0.02;
                this.level().addParticle(this.getTrailParticle(), this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY() + 0.5 + (double)(this.getRandom().nextFloat() * this.getBbHeight()), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), d0, d1, d2);
            }
        }
    }

}

