package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/** Points to the nearest biome heart (node). */
public class CompassNode extends ItemCompass {
    public static BlockPos orig;

    public CompassNode(String name) {
        super(name, 1);
    }

    @Override
    public BlockPos getOrigin(Entity entity, Level level) {
        return SRPWorldData.get(level).nearestHeartAgePosition(entity.blockPosition(), 0);
    }

    @Override
    public BlockPos getOri() {
        return orig;
    }
}
