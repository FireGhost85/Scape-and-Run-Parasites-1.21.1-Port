package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/** Points to the nearest infection origin. */
public class CompassOrigin extends ItemCompass {
    public static BlockPos orig;

    public CompassOrigin(String name) {
        super(name, 3);
    }

    @Override
    public BlockPos getOrigin(Entity entity, Level level) {
        return SRPWorldData.get(level).nearestInfectionPosition(true, entity.blockPosition());
    }

    @Override
    public BlockPos getOri() {
        return orig;
    }
}
