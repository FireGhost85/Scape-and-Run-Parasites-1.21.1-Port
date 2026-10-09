package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Thornshade: a berry bush that lives on SRP blocks (anywhere else it is dead). Stages 0 to 2 grow on random ticks (1 in 5),
 * the berry stage gives 1 to 2 berries when used and falls back to the no-berry stage, which regrows. The {@code _snow}
 * stages are used while snow lies next to the bush. The block has no item and drops nothing (the berry plants it, as in
 * 1.12, where its drop was the item of a block without an item). Bonemeal does not work on it.
 */
public class BlockThornshade extends BushBlock implements BonemealableBlock {
    public static final MapCodec<BlockThornshade> CODEC = simpleCodec(BlockThornshade::new);
    public static final EnumProperty<EnumThornshadeStage> STAGE = EnumProperty.create("stage", EnumThornshadeStage.class);
    private static final VoxelShape AABB_SMALL = Shapes.box(0.2, 0.0, 0.2, 0.8, 0.5, 0.8);
    private static final VoxelShape AABB_MEDIUM = Shapes.box(0.1, 0.0, 0.1, 0.9, 0.8, 0.9);
    private static final VoxelShape AABB_FULL = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.9, 1.0);

    public BlockThornshade(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, EnumThornshadeStage.STAGE0_TS));
    }

    public BlockThornshade() {
        this(SRPMaterial.PLANTS.props(0.2f).sound(SoundType.GRASS).noCollission().randomTicks());
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean onSrpSoil = this.isSrpSoil(level.getBlockState(pos.below()));
        boolean snowy = this.isSnowy(level, pos);
        EnumThornshadeStage stage = onSrpSoil ? (snowy ? EnumThornshadeStage.STAGE0_TS_SNOW : EnumThornshadeStage.STAGE0_TS) : getDeadStateStage(snowy);
        return this.defaultBlockState().setValue(STAGE, stage);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(STAGE)) {
            case STAGE0_TS, STAGE0_TS_SNOW -> AABB_SMALL;
            case STAGE1_TS, STAGE1_TS_SNOW, STAGE2_TS_NOBERRY, STAGE2_TS_NOBERRY_SNOW, DEAD_TS, DEAD_TS_SNOW -> AABB_MEDIUM;
            default -> AABB_FULL;
        };
    }

    /** Soil rule of the original: anything solid below except this block, slabs, stairs, fences, fence gates and walls. */
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        Block block = state.getBlock();
        if (state.isAir() || block == this) {
            return false;
        }
        return !(block instanceof SlabBlock) && !(block instanceof StairBlock) && !(block instanceof FenceBlock)
                && !(block instanceof FenceGateBlock) && !(block instanceof WallBlock);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!this.mayPlaceOn(level.getBlockState(pos.below()), level, pos.below())) {
            if (!level.isClientSide) {
                Block.popResource(level, pos, new ItemStack(SRPItems.itemThornshadeBerry.get()));
            }
            level.removeBlock(pos, false);
        }
    }

    private boolean isSrpSoil(BlockState state) {
        return ScapeAndRunParasites.MODID.equals(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace());
    }

    private boolean canRemainOnCurrentSoil(Level level, BlockPos pos) {
        return this.mayPlaceOn(level.getBlockState(pos.below()), level, pos.below());
    }

    /**
     * Snow lies next to the bush, or it is snowing on it. The 1.12 condition ({@code isRainingAt && temperature < 0.15}) can
     * hardly be true in 1.21, where {@code isRainingAt} excludes cold biomes: see PORTING_NOTES.
     */
    private boolean isSnowy(Level level, BlockPos pos) {
        BlockPos above = pos.above();
        boolean snowfall = level.canSeeSky(above) && level.isRainingAt(above) && level.getBiome(pos).value().coldEnoughToSnow(pos);
        if (snowfall) {
            return true;
        }
        for (Direction face : Direction.values()) {
            if (BlockBase.isSnow(level.getBlockState(pos.relative(face)))) {
                return true;
            }
        }
        return false;
    }

    private static EnumThornshadeStage applySnow(EnumThornshadeStage stage, boolean snowy) {
        return switch (stage) {
            case STAGE0_TS, STAGE0_TS_SNOW -> snowy ? EnumThornshadeStage.STAGE0_TS_SNOW : EnumThornshadeStage.STAGE0_TS;
            case STAGE1_TS, STAGE1_TS_SNOW -> snowy ? EnumThornshadeStage.STAGE1_TS_SNOW : EnumThornshadeStage.STAGE1_TS;
            case STAGE2_TS, STAGE2_TS_SNOW -> snowy ? EnumThornshadeStage.STAGE2_TS_SNOW : EnumThornshadeStage.STAGE2_TS;
            case STAGE2_TS_NOBERRY, STAGE2_TS_NOBERRY_SNOW -> snowy ? EnumThornshadeStage.STAGE2_TS_NOBERRY_SNOW : EnumThornshadeStage.STAGE2_TS_NOBERRY;
            case DEAD_TS, DEAD_TS_SNOW -> snowy ? EnumThornshadeStage.DEAD_TS_SNOW : EnumThornshadeStage.DEAD_TS;
        };
    }

    private static EnumThornshadeStage getDeadStateStage(boolean snowy) {
        return snowy ? EnumThornshadeStage.DEAD_TS_SNOW : EnumThornshadeStage.DEAD_TS;
    }

    private BlockState getDeadState(Level level, BlockPos pos) {
        return this.defaultBlockState().setValue(STAGE, getDeadStateStage(this.isSnowy(level, pos)));
    }

    private static boolean isDeadStage(EnumThornshadeStage stage) {
        return stage == EnumThornshadeStage.DEAD_TS || stage == EnumThornshadeStage.DEAD_TS_SNOW;
    }

    private static boolean isBerryStage(EnumThornshadeStage stage) {
        return stage == EnumThornshadeStage.STAGE2_TS || stage == EnumThornshadeStage.STAGE2_TS_SNOW;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        EnumThornshadeStage stage = state.getValue(STAGE);
        if (!this.canRemainOnCurrentSoil(level, pos)) {
            level.setBlock(pos, this.getDeadState(level, pos), 2);
            return;
        }
        if (!this.isSrpSoil(level.getBlockState(pos.below())) && !isDeadStage(stage)) {
            level.setBlock(pos, this.getDeadState(level, pos), 2);
            return;
        }
        boolean snowy = this.isSnowy(level, pos);
        EnumThornshadeStage adjusted = applySnow(stage, snowy);
        if (adjusted != stage) {
            level.setBlock(pos, state.setValue(STAGE, adjusted), 2);
            stage = adjusted;
        }
        if (isDeadStage(stage)) {
            return;
        }
        if (rand.nextInt(5) != 0) {
            return;
        }
        this.advance(level, pos, state, stage, snowy);
    }

    /** Next growth stage: 0 to 1, 1 to berries, no-berry back to berries. */
    private void advance(Level level, BlockPos pos, BlockState state, EnumThornshadeStage stage, boolean snowy) {
        switch (stage) {
            case STAGE0_TS, STAGE0_TS_SNOW -> level.setBlock(pos, state.setValue(STAGE, snowy ? EnumThornshadeStage.STAGE1_TS_SNOW : EnumThornshadeStage.STAGE1_TS), 2);
            case STAGE1_TS, STAGE1_TS_SNOW -> level.setBlock(pos, state.setValue(STAGE, snowy ? EnumThornshadeStage.STAGE2_TS_SNOW : EnumThornshadeStage.STAGE2_TS), 2);
            case STAGE2_TS_NOBERRY, STAGE2_TS_NOBERRY_SNOW -> level.setBlock(pos, state.setValue(STAGE, snowy ? EnumThornshadeStage.STAGE2_TS_SNOW : EnumThornshadeStage.STAGE2_TS), 2);
            default -> {
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        EnumThornshadeStage stage = state.getValue(STAGE);
        if (!isBerryStage(stage)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        int count = 1 + level.random.nextInt(2);
        Block.popResource(level, pos, new ItemStack(SRPItems.itemThornshadeBerry.get(), count));
        boolean snowy = this.isSnowy(level, pos);
        EnumThornshadeStage next = snowy ? EnumThornshadeStage.STAGE2_TS_NOBERRY_SNOW : EnumThornshadeStage.STAGE2_TS_NOBERRY;
        level.setBlock(pos, state.setValue(STAGE, next), 2);
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        EnumThornshadeStage stage = state.getValue(STAGE);
        return !isDeadStage(stage) && !isBerryStage(stage);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource rand, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource rand, BlockPos pos, BlockState state) {
        this.advance(level, pos, state, state.getValue(STAGE), this.isSnowy(level, pos));
    }

    public enum EnumThornshadeStage implements StringRepresentable {
        STAGE0_TS("stage0_ts"),
        STAGE0_TS_SNOW("stage0_ts_snow"),
        STAGE1_TS("stage1_ts"),
        STAGE1_TS_SNOW("stage1_ts_snow"),
        STAGE2_TS("stage2_ts"),
        STAGE2_TS_SNOW("stage2_ts_snow"),
        STAGE2_TS_NOBERRY("stage2_ts_noberry"),
        STAGE2_TS_NOBERRY_SNOW("stage2_ts_noberry_snow"),
        DEAD_TS("dead_ts"),
        DEAD_TS_SNOW("dead_ts_snow");

        private final String name;

        EnumThornshadeStage(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
