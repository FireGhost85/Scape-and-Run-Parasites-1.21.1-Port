package com.dhanantry.scapeandrunparasites.potion;

import net.minecraft.world.entity.LivingEntity;

/** Unused in 1.10.9 (THORNSHADE_THORNS_E is a plain SRPEffectBase). */
public class PotionThornshadeThorns extends SRPEffectBase {
    public PotionThornshadeThorns(String name, boolean isBadEffectIn, int liquidColorIn) {
        super(name, isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }
}
