package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * Infested cactus (1.12 {@code BlockParasiteCactus extends BlockCactus}): grows on sand, red sandstone and infested sand, and
 * pushes players away from it (cooldown 8 ticks).
 */
public class BlockParasiteCactus extends CactusBlock {
    private static final String NBT_PCACTUS_LAST_PUSH = "srp_pcactus_last_push";
    private static final double PUSH_H = 0.35;
    private static final double PUSH_Y = 0.08;
    private static final long PUSH_CD = 8L;

    public BlockParasiteCactus() {
        super(SRPMaterial.CACTUS.props(0.4f, 0.4f).sound(SoundType.WOOL).randomTicks()
                .isValidSpawn((state, level, pos, type) -> type.is(BlockBase.PARASITE_ENTITIES)));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockBase.parasiteBlockBreak(level, pos, true);
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState down = level.getBlockState(pos.below());
        if (down.is(Blocks.SAND) || down.is(Blocks.RED_SANDSTONE) || down.is(SRPBlocks.InfestedSand.get()) || down.is(this)) {
            for (Direction f : Direction.Plane.HORIZONTAL) {
                BlockState side = level.getBlockState(pos.relative(f));
                if (!side.isSolid() || side.is(this)) {
                    continue;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (level.isClientSide || !(entity instanceof Player player) || !player.isAlive()) {
            return;
        }
        long now = level.getGameTime();
        long last = player.getPersistentData().getLong(NBT_PCACTUS_LAST_PUSH);
        if (now - last < PUSH_CD) {
            return;
        }
        player.getPersistentData().putLong(NBT_PCACTUS_LAST_PUSH, now);
        double cx = pos.getX() + 0.5;
        double cz = pos.getZ() + 0.5;
        double dx = player.getX() - cx;
        double dz = player.getZ() - cz;
        double mag = Math.sqrt(dx * dx + dz * dz);
        if (mag < 1.0E-4) {
            dx = 0.0;
            dz = 1.0;
            mag = 1.0;
        }
        dx /= mag;
        dz /= mag;
        player.push(dx * PUSH_H, PUSH_Y, dz * PUSH_H);
        player.hurtMarked = true;
    }
}
