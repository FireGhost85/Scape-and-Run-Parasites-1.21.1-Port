package com.dhanantry.scapeandrunparasites.world.celestial;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface ICelestialEventEffect {
    default void onNightStart(Level world, String dim, int phase, long nightIndex) {
    }

    default void onNightEnd(Level world, String dim, int phase, long nightIndex) {
    }

    default void onParasiteSpawn(EntityParasiteBase parasite, @Nullable LivingEntity spawner) {
    }
}
