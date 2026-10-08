package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class EffectDodSmokeTrail extends SRPEffectBase {
    public EffectDodSmokeTrail() {
        super("dod_smoke_trail", false, 0x404040);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity.level() instanceof ServerLevel ws)) {
            return true;
        }
        MobEffectInstance instance = entity.getEffect(SRPPotions.DOD_SMOKE_TRAIL_E);
        int remaining = instance == null ? 0 : instance.getDuration();
        if (entity.onGround() && remaining <= 10) {
            entity.removeEffect(SRPPotions.DOD_SMOKE_TRAIL_E);
            return true;
        }
        double x = entity.getX();
        double y = entity.getY() + (double) entity.getEyeHeight();
        double z = entity.getZ();
        ws.sendParticles(ParticleTypes.SMOKE, x, y, z, 6, 0.15, 0.15, 0.15, 0.02);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
