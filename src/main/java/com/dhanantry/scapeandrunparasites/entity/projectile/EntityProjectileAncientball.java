package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class EntityProjectileAncientball
extends EntitySRPProjectile {
    public EntityProjectileAncientball(EntityType<? extends EntityProjectileAncientball> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityProjectileAncientball(EntityType<? extends EntityProjectileAncientball> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, worldIn, shooter, accelX, accelY, accelZ);
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.ITEM_SLIME;
    }

    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            if (SRPEntityUtil.hitEntity(result) != null && SRPEntityUtil.hitEntity(result) instanceof LivingEntity) {
                LivingEntity target = (LivingEntity)SRPEntityUtil.hitEntity(result);
                if (target instanceof EntityParasiteBase && !(target instanceof EntityNak)) {
                    this.discard();
                    return;
                }
                float damageR = SRPAttributes.ORONCO_ATTACK_DAMAGE;
                DamageSource damagesource = this.getOwner() == null ? this.damageSources().thrown((Entity)this, (Entity)this) : this.damageSources().thrown((Entity)this, (Entity)this.getOwner());
                target.hurt(damagesource, damageR);
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
                this.attackEntityAsMobMinimum((Entity)target, (EntityParasiteBase)this.getOwner());
            }
            AreaEffectCloud entityareaeffectcloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            entityareaeffectcloud.setRadius(this.getBbWidth() * 4.0f);
            entityareaeffectcloud.setRadiusOnUse(-0.5f);
            entityareaeffectcloud.setWaitTime(5);
            entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration());
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.WITHER, 300, 0));
            entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
            this.level().addFreshEntity((Entity)entityareaeffectcloud);
            this.discard();
        }
    }
}

