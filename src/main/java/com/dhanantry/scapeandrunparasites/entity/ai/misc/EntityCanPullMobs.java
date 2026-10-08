package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import net.minecraft.world.entity.LivingEntity;

public interface EntityCanPullMobs {
    public boolean hasTargetedEntity();

    public void setTargetedEntity(int var1);

    public LivingEntity getTargetedEntity();

    public boolean checkAttackTarget(LivingEntity var1);

    public void setPStatus(int var1);

    public void setPullingMobEffects(LivingEntity var1);

    public void resetPullSkill();

    public int getAcceleration();
}

