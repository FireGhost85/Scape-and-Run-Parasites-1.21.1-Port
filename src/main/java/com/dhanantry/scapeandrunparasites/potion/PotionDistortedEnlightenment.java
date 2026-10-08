package com.dhanantry.scapeandrunparasites.potion;

import net.minecraft.world.entity.LivingEntity;

/** The glowing flag is cleared in {@link DistortedEnlightenmentEvents} when the effect is removed or expires (1.12 removeAttributesModifiersFromEntity). */
public class PotionDistortedEnlightenment extends SRPEffectBase {
    public PotionDistortedEnlightenment(String name, boolean isBadEffectIn, int liquidColorIn) {
        super(name, isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);
        if (!entity.level().isClientSide) {
            entity.setGlowingTag(true);
        }
        return true;
    }
}
