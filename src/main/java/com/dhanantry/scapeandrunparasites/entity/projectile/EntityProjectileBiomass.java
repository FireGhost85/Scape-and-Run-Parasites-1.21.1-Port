package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EntityProjectileBiomass
extends Fireball {
    private LivingEntity target;
    private String parasite;
    private int point;
    private int kin;

    public EntityProjectileBiomass(EntityType<? extends EntityProjectileBiomass> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityProjectileBiomass(EntityType<? extends EntityProjectileBiomass> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, shooter, new Vec3(accelX, accelY, accelZ), worldIn);
    }

    public void setParasite(String in, int points, int type) {
        this.parasite = in;
        this.point = points;
        this.target = ((EntityParasiteBase)this.getOwner()).getTarget();
        this.kin = type;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.POOF;
    }

    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            if (this.getOwner() != null && this.getOwner().isAlive() && this.getOwner() instanceof EntityParasiteBase) {
                EntityBiomass entityout = new EntityBiomass(SRPEntities.BIOMASS.get(), this.level(), (EntityParasiteBase)this.getOwner(), this.target);
                entityout.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                entityout.setFuse(80);
                entityout.setParasite(this.parasite, this.point);
                entityout.setSkin(this.kin);
                EntityCanSummon father = (EntityCanSummon)this.getOwner();
                father.setActualParasites(this.point);
                father.addID(entityout.getId(), this.point);
                this.level().addFreshEntity((Entity)entityout);
            }
            this.discard();
        }
    }

    public void tick() {
        super.tick();
        if (this.tickCount > 140) {
            this.discard();
        }
    }
}

