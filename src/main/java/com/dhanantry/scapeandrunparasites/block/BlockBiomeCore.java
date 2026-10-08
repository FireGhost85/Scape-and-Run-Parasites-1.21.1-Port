package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNodeCore;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;

/** Biome heart of a node; {@code active} is the age of the heart. */
public class BlockBiomeCore extends BlockBase {
    public static final IntegerProperty ACTIVE = IntegerProperty.create("active", 0, 3);

    public BlockBiomeCore(SRPMaterial material, float hardness, boolean tickRandom) {
        super(prop(material.props(hardness, 1.0f).sound(SRPSoundTypes.HEART).pushReaction(PushReaction.BLOCK), tickRandom));
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
            ParasiteEventWorld.removeHeartInWorld(level, pos);
        }
        return flag;
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (level.isClientSide) {
            return;
        }
        ParasiteEventWorld.removeHeartInWorld(level, pos);
        super.onBlockExploded(state, level, pos, explosion);
        ParasiteEventWorld.checkNodeStatus(level);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!level.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            return;
        }
        int meta = state.getValue(ACTIVE);
        int age;
        if (meta > 0 && (age = ParasiteEventWorld.getHeartAgePostion(level, pos)) > meta) {
            level.setBlock(pos, this.defaultBlockState().setValue(ACTIVE, age & 3), 3);
            int type = ParasiteEventWorld.canBiomeStillExistType(level, pos, true);
            switch (age) {
                case 2: {
                    WorldGenParasiteNodeCore tree2 = new WorldGenParasiteNodeCore(false, 2, type);
                    tree2.generate(level, RandomSource.create(), pos.above());
                    break;
                }
                case 3: {
                    WorldGenParasiteNodeCore tree3 = new WorldGenParasiteNodeCore(false, 3, type);
                    tree3.generate(level, RandomSource.create(), pos.above());
                    break;
                }
                default:
                    break;
            }
        }
    }
}
