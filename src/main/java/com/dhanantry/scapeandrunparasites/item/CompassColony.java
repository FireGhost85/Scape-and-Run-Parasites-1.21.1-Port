package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/** Points to the nearest colony. */
public class CompassColony extends ItemCompass {
    public static BlockPos orig;

    public CompassColony(String name) {
        super(name, 2);
    }

    @Override
    public BlockPos getOrigin(Entity entity, Level level) {
        return SRPWorldData.get(level).nearestColonyPosition(entity.blockPosition(), true);
    }

    @Override
    public BlockPos getOri() {
        return orig;
    }
}
