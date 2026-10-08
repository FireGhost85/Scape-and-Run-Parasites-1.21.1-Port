package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.util.Mot;
import java.util.ArrayList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class EntityProjectileEffects
extends EntitySRPProjectile {
    private ArrayList<MobEffectInstance> eff;

    public EntityProjectileEffects(EntityType<? extends EntityProjectileEffects> type, Level worldIn) {
        super(type, worldIn);
        this.eff = new ArrayList();
    }

    public EntityProjectileEffects(EntityType<? extends EntityProjectileEffects> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, worldIn, shooter, accelX, accelY, accelZ);
        this.eff = new ArrayList();
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.POOF;
    }

    public void addEffect(MobEffectInstance in) {
        this.eff.add(in);
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            Mot.addY(this, -(0.1));
        }
    }

    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY() - 1.0, this.getZ());
            entityareaeffectcloud.setRadius(2.3f, 0.5f);
            entityareaeffectcloud.setRadiusOnUse(-0.5f);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(100);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            for (MobEffectInstance in : this.eff) {
                entityareaeffectcloud.addEffect(in);
            }
            this.level().addFreshEntity((Entity)entityareaeffectcloud);
            this.discard();
        }
    }
}

