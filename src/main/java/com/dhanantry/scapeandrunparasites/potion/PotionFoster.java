package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import net.minecraft.world.entity.LivingEntity;

public class PotionFoster extends SRPEffectBase {
    public PotionFoster(String name, boolean isBadEffectIn, int liquidColorIn) {
        super(name, isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            return true;
        }
        if (entity instanceof EntityPMalleable malleable) {
            malleable.increaseAllResistances();
        }
        return true;
    }
}
