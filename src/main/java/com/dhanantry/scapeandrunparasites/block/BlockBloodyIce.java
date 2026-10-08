package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/** Translucent ice that can shatter under a hard landing of a player. */
public class BlockBloodyIce extends BlockParasiteSpreading {
    public BlockBloodyIce(SRPMaterial material, float hardness, boolean infested) {
        super(material.props(hardness).friction(0.98f).sound(SoundType.GLASS).noOcclusion(), infested);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacent, Direction dir) {
        return adjacent.is(this);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(level, state, pos, entity, fallDistance);
        if (level.isClientSide) {
            return;
        }
        if (!(entity instanceof Player)) {
            return;
        }
        if (!SRPConfigWorld.bloodyIceBreakOnHardLanding) {
            return;
        }
        if ((double) fallDistance < SRPConfigWorld.bloodyIceBreakFallDistance) {
            return;
        }
        int diameter = SRPConfigWorld.bloodyIceBreakDiameter;
        if (diameter < 1) {
            diameter = 1;
        }
        int radius = (diameter - 1) / 2;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                BlockPos p = pos.offset(dx, 0, dz);
                BlockState st = level.getBlockState(p);
                if (!st.is(this)) {
                    continue;
                }
                level.levelEvent(2001, p, Block.getId(st));
                level.destroyBlock(p, true);
            }
        }
    }
}
