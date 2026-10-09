package com.dhanantry.scapeandrunparasites.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** Leaves of the deadhead tree: decay only after {@link #beginLeavesDecay}, kept alive by logs and deadhead trunks within 7 blocks. */
public class BlockDeadheadLeaves extends Block implements ILeavesDecay {
    public static final BooleanProperty DECAYABLE = BooleanProperty.create("decayable");
    public static final BooleanProperty CHECK_DECAY = BooleanProperty.create("check_decay");
    public static final BooleanProperty SNOWY = BooleanProperty.create("snowy");
    public static final MapCodec<BlockDeadheadLeaves> CODEC = MapCodec.unit(BlockDeadheadLeaves::new);

    private int[] surroundings;

    public BlockDeadheadLeaves() {
        super(SRPMaterial.LEAVES.props(0.2f).sound(SoundType.GRASS).randomTicks().noOcclusion()
                .isValidSpawn((s, l, p, t) -> true).isSuffocating((s, l, p) -> false).isViewBlocking((s, l, p) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(DECAYABLE, Boolean.TRUE).setValue(CHECK_DECAY, Boolean.FALSE).setValue(SNOWY, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DECAYABLE, CHECK_DECAY, SNOWY);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(SNOWY, BlockBase.isSnow(context.getLevel().getBlockState(context.getClickedPos().above())));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return dir == Direction.UP ? state.setValue(SNOWY, BlockBase.isSnow(neighbor)) : state;
    }

    private static boolean canSustainLeaves(BlockState state) {
        return state.is(BlockTags.LOGS) || BlockParasiteTrunk.canSustainLeaves(state);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!state.getValue(CHECK_DECAY) || !state.getValue(DECAYABLE)) {
            return;
        }
        int decayDistance = 7;
        int checkRadius = decayDistance + 1;
        int arraySize = 32;
        int arrayArea = arraySize * arraySize;
        int arrayCenter = arraySize / 2;
        if (this.surroundings == null) {
            this.surroundings = new int[arraySize * arraySize * arraySize];
        }
        if (!level.hasChunksAt(pos.offset(-checkRadius, -checkRadius, -checkRadius), pos.offset(checkRadius, checkRadius, checkRadius))) {
            return;
        }
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = -decayDistance; x <= decayDistance; ++x) {
            for (int y = -decayDistance; y <= decayDistance; ++y) {
                for (int z = -decayDistance; z <= decayDistance; ++z) {
                    mutablePos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    BlockState checkState = level.getBlockState(mutablePos);
                    int index = (x + arrayCenter) * arrayArea + (y + arrayCenter) * arraySize + z + arrayCenter;
                    this.surroundings[index] = canSustainLeaves(checkState) ? 0 : (checkState.is(BlockTags.LEAVES) ? -2 : -1);
                }
            }
        }
        for (int distance = 1; distance <= decayDistance; ++distance) {
            for (int x = -decayDistance; x <= decayDistance; ++x) {
                for (int y = -decayDistance; y <= decayDistance; ++y) {
                    for (int z = -decayDistance; z <= decayDistance; ++z) {
                        int index = (x + arrayCenter) * arrayArea + (y + arrayCenter) * arraySize + z + arrayCenter;
                        if (this.surroundings[index] != distance - 1) {
                            continue;
                        }
                        if (this.surroundings[index - arrayArea] == -2) {
                            this.surroundings[index - arrayArea] = distance;
                        }
                        if (this.surroundings[index + arrayArea] == -2) {
                            this.surroundings[index + arrayArea] = distance;
                        }
                        if (this.surroundings[index - arraySize] == -2) {
                            this.surroundings[index - arraySize] = distance;
                        }
                        if (this.surroundings[index + arraySize] == -2) {
                            this.surroundings[index + arraySize] = distance;
                        }
                        if (this.surroundings[index - 1] == -2) {
                            this.surroundings[index - 1] = distance;
                        }
                        if (this.surroundings[index + 1] != -2) {
                            continue;
                        }
                        this.surroundings[index + 1] = distance;
                    }
                }
            }
        }
        int centerIndex = arrayCenter * arrayArea + arrayCenter * arraySize + arrayCenter;
        if (this.surroundings[centerIndex] >= 0) {
            level.setBlock(pos, state.setValue(CHECK_DECAY, Boolean.FALSE), 4);
        } else {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public void beginLeavesDecay(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(DECAYABLE) && !state.getValue(CHECK_DECAY)) {
            level.setBlock(pos, state.setValue(CHECK_DECAY, Boolean.TRUE), 4);
        }
    }
}
