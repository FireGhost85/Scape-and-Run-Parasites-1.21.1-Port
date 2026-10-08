package com.dhanantry.scapeandrunparasites.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Glass block with an infestation stage (1.12 {@code BlockGlass} with {@code ignoreSimilarity = true}: all faces render). */
public class BlockAshenGlass extends Block implements IStagedBlock {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 5);

    public static final MapCodec<BlockAshenGlass> CODEC = MapCodec.unit(BlockAshenGlass::new);

    public BlockAshenGlass() {
        super(BlockBehaviour.Properties.of().strength(0.3f).sound(SoundType.GLASS).noOcclusion()
                .isValidSpawn((s, l, p, t) -> false).isRedstoneConductor((s, l, p) -> false)
                .isSuffocating((s, l, p) -> false).isViewBlocking((s, l, p) -> false));
        registerDefaultState(stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public IntegerProperty getStageProperty() {
        return STAGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }
}
