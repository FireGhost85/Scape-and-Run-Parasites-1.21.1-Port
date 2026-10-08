package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class EntityProjectileAngedball
extends EntitySRPProjectile {
    public EntityProjectileAngedball(EntityType<? extends EntityProjectileAngedball> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityProjectileAngedball(EntityType<? extends EntityProjectileAngedball> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, worldIn, shooter, accelX, accelY, accelZ);
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.ITEM_SLIME;
    }

    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            if (SRPEntityUtil.hitEntity(result) != null && SRPEntityUtil.hitEntity(result) instanceof LivingEntity) {
                if (SRPEntityUtil.hitEntity(result) instanceof EntityParasiteBase && !(SRPEntityUtil.hitEntity(result) instanceof EntityNak)) {
                    this.discard();
                    return;
                }
                DamageSource damagesource = this.getOwner() == null ? this.damageSources().thrown((Entity)this, (Entity)this) : this.damageSources().thrown((Entity)this, (Entity)this.getOwner());
                SRPEntityUtil.hitEntity(result).hurt(damagesource, (float)SRPAttributes.ANGED_RANGED_ATTACK_DAMAGE);
                this.attackEntityAsMobMinimum(SRPEntityUtil.hitEntity(result), (EntityParasiteBase)this.getOwner());
            }
            EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY(), this.getZ());
            entityareaeffectcloud.setRadius(2.5f, 0.5f);
            entityareaeffectcloud.setRadiusOnUse(-0.5f);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(100);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 0, false, false));
            entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.CORRO_E, 100, 0, false, false));
            this.level().addFreshEntity((Entity)entityareaeffectcloud);
            this.discard();
        }
    }
}

