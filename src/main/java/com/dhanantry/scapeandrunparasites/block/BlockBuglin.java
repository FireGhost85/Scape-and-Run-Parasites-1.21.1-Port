package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Buglin tunnel block: spawns a buglin (Lodo) when ticked and releases one when broken. */
public class BlockBuglin extends BlockBase {
    protected static final VoxelShape TALL_GRASS_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    public BlockBuglin(SRPMaterial material, float hardness, boolean tickRandom, float resistance) {
        super(prop(material.props(hardness, resistance).sound(SRPSoundTypes.TUNNEL).noOcclusion().noLootTable(), tickRandom));
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!level.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            return;
        }
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (!SRPConfigMobs.lodoEnabled) {
            return;
        }
        AABB axisalignedbb = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(1.0, 1.0, 1.0);
        List<EntityLodo> moblist = level.getEntitiesOfClass(EntityLodo.class, axisalignedbb);
        if (!moblist.isEmpty()) {
            return;
        }
        axisalignedbb = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(16.0, 16.0, 16.0);
        List<EntityParasiteBase> parasites = level.getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        if (parasites.size() > 10) {
            return;
        }
        EntityLodo l222 = SRPEntities.BUGLIN.get().create(level);
        l222.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        l222.setFloorTimer();
        level.addFreshEntity(l222);
        level.broadcastEntityEvent(l222, (byte) 50);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return TALL_GRASS_AABB;
    }

    protected boolean canBlockStay(LevelReader level, BlockPos pos) {
        BlockPos down = pos.below();
        BlockState below = level.getBlockState(down);
        return below.isFaceSturdy(level, down, Direction.UP);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.canBlockStay(level, pos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && !this.canBlockStay(level, pos)) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, fromPos, movedByPiston);
        if (!level.isClientSide && !this.canBlockStay(level, pos)) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide && level.hasChunksAt(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                && level.getDifficulty() != Difficulty.PEACEFUL) {
            Entity ent = SRPEntities.BUGLIN.get().create(level);
            if (ent != null) {
                ent.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                level.addFreshEntity(ent);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
