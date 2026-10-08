package com.dhanantry.scapeandrunparasites.potion;

import net.minecraft.world.entity.LivingEntity;

public class PotionOverheat extends SRPEffectBase {
    public PotionOverheat(String name, boolean isBadEffectIn, int liquidColorIn) {
        super(name, isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            return true;
        }
        if (entity.tickCount % 20 == 0) {
            entity.igniteForSeconds(2);
        }
        return true;
    }
}
