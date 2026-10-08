package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class PotionBleed extends SRPEffectBase {
    public PotionBleed(String name, boolean isBadEffectIn, int liquidColorIn) {
        super(name, isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity.level() instanceof ServerLevel ws)) {
            return true;
        }
        ws.sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(), entity.getZ(), 1, 0.15, 0.3, 0.15, 0.0);
        float damage = entity.getMaxHealth() * SRPConfigSystems.bleedingDamage;
        if (entity.getX() != entity.xo || entity.getZ() != entity.zo) {
            damage *= (float) (amplifier + 1);
        }
        damage = Math.min(damage, SRPConfigSystems.bleedingDamageCap);
        entity.hurt(ws.damageSources().magic(), damage);
        return true;
    }
}
