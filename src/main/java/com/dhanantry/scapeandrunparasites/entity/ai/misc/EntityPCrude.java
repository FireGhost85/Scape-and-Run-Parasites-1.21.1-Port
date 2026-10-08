package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class EntityPCrude
extends EntityParasiteBase {
    public EntityPCrude(EntityType<? extends EntityPCrude> type, Level worldIn) {
        super(type, worldIn);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, false, false, null, 1.0, 1.0f));
        this.setScentHPMultiplier(1.5f);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide && this.getRandom().nextInt(25) == 0) {
            for (int i = 0; i <= 1; ++i) {
                this.spawnParticlesGoreBox(SRPEnumParticle.GSPLASH, 0, -1, -1, 0.1, 0.0);
            }
        }
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance >= 60.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }
}

