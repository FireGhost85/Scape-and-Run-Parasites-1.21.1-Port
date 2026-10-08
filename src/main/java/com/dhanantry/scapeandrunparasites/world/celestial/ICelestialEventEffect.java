package com.dhanantry.scapeandrunparasites.world.celestial;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface ICelestialEventEffect {
    default public void onNightStart(Level world, int dim, int phase, long nightIndex) {
    }

    default public void onNightEnd(Level world, int dim, int phase, long nightIndex) {
    }

    default public void onParasiteSpawn(EntityParasiteBase parasite, @Nullable LivingEntity spawner) {
    }
}

