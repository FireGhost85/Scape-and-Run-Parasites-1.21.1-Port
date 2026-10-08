package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class SoundEaterSoundHelper {
    public static void broadcastSound(Level world, BlockPos pos, double radius, int lifeTicks) {
        if (world == null || world.isClientSide) {
            return;
        }
        AABB box = new AABB(pos).expandTowards(radius, radius, radius);
        List<? extends EntityInfHuman> list = world.getEntitiesOfClass(EntityInfHuman.class, box);
        for (EntityInfHuman human : list) {
            if (human.getSkin() != 111) continue;
            human.notifyHeardSound(pos, lifeTicks);
        }
    }
}

