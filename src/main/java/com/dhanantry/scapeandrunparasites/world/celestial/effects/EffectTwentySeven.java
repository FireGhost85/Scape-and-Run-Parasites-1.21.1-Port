package com.dhanantry.scapeandrunparasites.world.celestial.effects;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.world.celestial.ICelestialEventEffect;
import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EffectTwentySeven
implements ICelestialEventEffect {
    private static final int DURATION = 12000;

    @Override
    public void onNightStart(Level world, int dim, int phase, long nightIndex) {
        for (EntityParasiteBase parasite : world.getEntities(EntityParasiteBase.class, (e -> e.isAlive()))) {
            parasite.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 12000, 1, false, false));
        }
    }

    @Override
    public void onParasiteSpawn(EntityParasiteBase parasite, @Nullable LivingEntity spawner) {
        parasite.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 12000, 1, false, false));
    }
}

