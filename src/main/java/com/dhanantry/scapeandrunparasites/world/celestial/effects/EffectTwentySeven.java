package com.dhanantry.scapeandrunparasites.world.celestial.effects;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.world.celestial.ICelestialEventEffect;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EffectTwentySeven implements ICelestialEventEffect {
    private static final int DURATION = 12000;

    @Override
    public void onNightStart(Level world, String dim, int phase, long nightIndex) {
        if (!(world instanceof ServerLevel server)) {
            return;
        }
        for (net.minecraft.world.entity.Entity e : server.getAllEntities()) {
            if (e instanceof EntityParasiteBase parasite && parasite.isAlive()) {
                parasite.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, DURATION, 1, false, false));
            }
        }
    }

    @Override
    public void onParasiteSpawn(EntityParasiteBase parasite, @Nullable LivingEntity spawner) {
        parasite.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, DURATION, 1, false, false));
    }
}
