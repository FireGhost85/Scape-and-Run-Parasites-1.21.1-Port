package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;

/** Colony heart / outpost core; {@code active} greater than zero means it is registered in the world data. */
public class BlockColonyCore extends BlockBase {
    public static final IntegerProperty ACTIVE = IntegerProperty.create("active", 0, 3);

    public BlockColonyCore(SRPMaterial material, float hardness, boolean tickRandom, float resistance) {
        super(prop(material.props(hardness, resistance).sound(SRPSoundTypes.FLESH).pushReaction(PushReaction.BLOCK), tickRandom));
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean flag = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (level.isClientSide) {
            return flag;
        }
        if (state.getValue(ACTIVE) > 0) {
            ParasiteEventWorld.removeColonyInWorld(level, pos);
        }
        return flag;
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (level.isClientSide) {
            return;
        }
        ParasiteEventWorld.removeColonyInWorld(level, pos);
        super.onBlockExploded(state, level, pos, explosion);
        ParasiteEventWorld.checkColonyStatus(level);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide && state.getValue(ACTIVE) > 0) {
            ParasiteEventWorld.removeColonyInWorld(level, pos);
            ParasiteEventWorld.checkColonyStatus(level);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
