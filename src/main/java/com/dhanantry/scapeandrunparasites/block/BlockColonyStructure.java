package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyB1;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyB2;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyB3;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyB4;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBS1;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBS2;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBS3;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

/** Colony structure block: grows a colony building on random ticks once a colony exists nearby. */
public class BlockColonyStructure extends BlockBase {
    public static final IntegerProperty ACTIVE = IntegerProperty.create("active", 0, 3);

    public BlockColonyStructure(SRPMaterial material, float hardness, boolean tick, float resistance) {
        super(prop(material.props(hardness, resistance).sound(SRPSoundTypes.FLESH).pushReaction(PushReaction.BLOCK), tick));
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    /** The old activation only ever replaced the block with air (its debug branches were unreachable). */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        return InteractionResult.PASS;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!level.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            return;
        }
        if (!SRPConfigWorld.coloniesActivated) {
            return;
        }
        SRPWorldData data = SRPWorldData.get(level);
        if (data.nearestColonyPosition(pos, false) == null) {
            return;
        }
        int meta = state.getValue(ACTIVE);
        block0:
        switch (meta) {
            case 1: {
                switch (rand.nextInt(3)) {
                    case 1: {
                        WorldGenParasiteColonyB3 b3 = new WorldGenParasiteColonyB3(false, 2);
                        b3.generate(level, RandomSource.create(), pos);
                        break block0;
                    }
                    case 2: {
                        WorldGenParasiteColonyB2 b2 = new WorldGenParasiteColonyB2(false, 2);
                        b2.generate(level, RandomSource.create(), pos);
                        break block0;
                    }
                    case 3: {
                        WorldGenParasiteColonyB4 b4 = new WorldGenParasiteColonyB4(false, 2);
                        b4.generate(level, RandomSource.create(), pos);
                        break block0;
                    }
                    default:
                        break;
                }
                WorldGenParasiteColonyB1 b1 = new WorldGenParasiteColonyB1(false, 2);
                b1.generate(level, RandomSource.create(), pos);
                break;
            }
            case 2: {
                switch (rand.nextInt(3)) {
                    case 1: {
                        WorldGenParasiteColonyBS1 bs1 = new WorldGenParasiteColonyBS1(false, 2);
                        bs1.generate(level, RandomSource.create(), pos);
                        break block0;
                    }
                    case 2: {
                        WorldGenParasiteColonyBS3 bs3 = new WorldGenParasiteColonyBS3(false, 2);
                        bs3.generate(level, RandomSource.create(), pos);
                        break block0;
                    }
                    default:
                        break;
                }
                WorldGenParasiteColonyBS2 bs2 = new WorldGenParasiteColonyBS2(false, 2);
                bs2.generate(level, RandomSource.create(), pos);
                break;
            }
            default:
                break;
        }
        level.setBlock(pos, this.defaultBlockState().setValue(ACTIVE, 3), 3);
    }
}
