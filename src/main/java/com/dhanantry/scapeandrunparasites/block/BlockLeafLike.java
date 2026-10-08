package com.dhanantry.scapeandrunparasites.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Infested leaves: they age and finally decay unless an srparasites block touches them. */
public class BlockLeafLike extends Block {
    public static final IntegerProperty DECAY_AGE = IntegerProperty.create("decay_age", 0, 5);
    public static final MapCodec<BlockLeafLike> CODEC = MapCodec.unit(BlockLeafLike::new);

    public BlockLeafLike() {
        super(SRPMaterial.LEAVES.props(0.2f).sound(SoundType.GRASS).randomTicks().noOcclusion()
                .isSuffocating((s, l, p) -> false).isViewBlocking((s, l, p) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(DECAY_AGE, 0));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DECAY_AGE);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        int age = state.getValue(DECAY_AGE);
        if (this.touchingAnySRP(level, pos)) {
            if (age > 0 && rand.nextInt(6) == 0) {
                level.setBlock(pos, state.setValue(DECAY_AGE, age - 1), 2);
            }
            return;
        }
        if (rand.nextInt(12) == 0) {
            if (age < 5) {
                level.setBlock(pos, state.setValue(DECAY_AGE, age + 1), 2);
            } else {
                level.destroyBlock(pos, true);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        if (!level.isEmptyBlock(pos.below())) {
            return;
        }
        if (rand.nextInt(14) != 0) {
            return;
        }
        double x = pos.getX() + 0.2 + rand.nextDouble() * 0.6;
        double y = pos.getY() + 0.95;
        double z = pos.getZ() + 0.2 + rand.nextDouble() * 0.6;
        BlockClientHooks.infestedLeaf(level, x, y, z);
    }

    private boolean touchingAnySRP(Level level, BlockPos pos) {
        for (Direction f : Direction.values()) {
            BlockPos n = pos.relative(f);
            BlockState s = level.getBlockState(n);
            ResourceLocation key = BuiltInRegistries.BLOCK.getKey(s.getBlock());
            if (key == null || !"srparasites".equals(key.getNamespace())) {
                continue;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 60;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 30;
    }
}
