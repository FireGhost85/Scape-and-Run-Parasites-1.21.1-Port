package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityNade;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class EntityProjectileNade
extends EntitySRPProjectile {
    private int duration;
    private int fuse;

    public EntityProjectileNade(EntityType<? extends EntityProjectileNade> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityProjectileNade(EntityType<? extends EntityProjectileNade> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ, int fuse, int duration) {
        super(type, worldIn, shooter, accelX, accelY, accelZ);
        this.fuse = fuse;
        this.duration = duration;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.ITEM_SLIME;
    }

    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            if (this.getOwner() == null) {
                return;
            }
            if (!this.getOwner().isAlive()) {
                return;
            }
            EntityNade nade = new EntityNade(SRPEntities.NADE.get(), this.level(), this.fuse, this.duration);
            if (this.getOwner() instanceof EntityParasiteBase) {
                nade.setFatherS((EntityParasiteBase)this.getOwner());
            }
            nade.copyPosition((Entity)this);
            this.level().addFreshEntity((Entity)nade);
            this.discard();
        }
    }
}

