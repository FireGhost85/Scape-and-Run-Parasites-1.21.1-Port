package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class EntityAIBlockInfest
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityParasiteBase parentEntity;
    private int ticks = 0;
    private int stage;

    public EntityAIBlockInfest(EntityParasiteBase ghast, int stage) {
        this.parentEntity = ghast;
        this.stage = stage;
    }

    public boolean canUse() {
        return true;
    }

    public void start() {
    }

    public void stop() {
    }

    public void tick() {
        ++this.ticks;
        if (this.ticks > 200) {
            this.ticks = 0;
            Level world = this.parentEntity.level();
            Block block = world.getBlockState(this.parentEntity.blockPosition()).getBlock();
            BlockParasiteSpreading.canInfestBlock(world, this.parentEntity.blockPosition(), RandomSource.create(), this.stage, true);
        }
    }
}

