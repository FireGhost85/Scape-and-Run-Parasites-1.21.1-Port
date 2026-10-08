package com.dhanantry.scapeandrunparasites.entity.tile;

import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Block entity of the trophy blocks; holds the entity the renderer draws. */
public class TileEntityTrophy extends BlockEntity {
    public transient Entity cachedRenderEntity;

    public TileEntityTrophy(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.TROPHY.get(), pos, state);
    }
}
