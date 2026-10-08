package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Residue: slows players walking on it and slowly grows residue plants on its free faces. */
public class BlockResidue extends BlockBase {
    public BlockResidue() {
        super(prop(SRPMaterial.ROCK.props(1.5f, 10.0f).sound(SRPSoundTypes.VOMIT), true));
    }

    private boolean tooManyNeighborPlants(Level level, BlockPos pos) {
        int count = 0;
        for (Direction f : Direction.values()) {
            if (!level.getBlockState(pos.relative(f)).is(SRPBlocks.ResiduePlants.get()) || ++count < 2) {
                continue;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (rand.nextInt(16) != 0) {
            return;
        }
        BlockResiduePlants plants = (BlockResiduePlants) SRPBlocks.ResiduePlants.get();
        for (int tries = 0; tries < 3; ++tries) {
            Direction face = Direction.values()[rand.nextInt(6)];
            BlockPos plantPos = pos.relative(face);
            if (!level.isEmptyBlock(plantPos) || this.tooManyNeighborPlants(level, plantPos) || !plants.canAttach(level, plantPos, face)) {
                continue;
            }
            level.setBlock(plantPos, plants.defaultBlockState().setValue(BlockResiduePlants.FACING, face), 2);
            break;
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof Player && entity.onGround()) {
            Vec3 m = entity.getDeltaMovement();
            entity.setDeltaMovement(m.x * 0.5, m.y, m.z * 0.5);
        }
        super.stepOn(level, pos, state, entity);
    }
}
