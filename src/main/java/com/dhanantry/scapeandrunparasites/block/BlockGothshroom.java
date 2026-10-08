package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Gothshroom: a mushroom that clings to a solid face of an SRP block. {@code facing} is the direction of the support block
 * for the four sides and the outward direction for floor (up) and ceiling (down), exactly as the 1.12 block stored it. Using
 * another gothshroom on it makes a group, which drops two items (loot table).
 */
public class BlockGothshroom extends Block {
    public static final MapCodec<BlockGothshroom> CODEC = simpleCodec(BlockGothshroom::new);
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final BooleanProperty GROUP = BooleanProperty.create("group");
    private static final VoxelShape AABB_FLOOR = Shapes.box(0.25, 0.0, 0.25, 0.75, 0.65, 0.75);
    private static final VoxelShape AABB_CEILING = Shapes.box(0.25, 0.35, 0.25, 0.75, 1.0, 0.75);
    private static final VoxelShape AABB_NORTH = Shapes.box(0.25, 0.25, 0.0, 0.75, 0.75, 0.6);
    private static final VoxelShape AABB_SOUTH = Shapes.box(0.25, 0.25, 0.4, 0.75, 0.75, 1.0);
    private static final VoxelShape AABB_WEST = Shapes.box(0.0, 0.25, 0.25, 0.6, 0.75, 0.75);
    private static final VoxelShape AABB_EAST = Shapes.box(0.4, 0.25, 0.25, 1.0, 0.75, 0.75);

    public BlockGothshroom(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(GROUP, Boolean.FALSE));
    }

    public BlockGothshroom() {
        this(SRPMaterial.PLANTS.props(0.0f, 0.0f).sound(SoundType.GRASS).noOcclusion().pushReaction(PushReaction.DESTROY));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, GROUP);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> AABB_CEILING;
            case NORTH -> AABB_NORTH;
            case SOUTH -> AABB_SOUTH;
            case WEST -> AABB_WEST;
            case EAST -> AABB_EAST;
            default -> AABB_FLOOR;
        };
    }

    private static boolean isSRP(Block b) {
        return ScapeAndRunParasites.MODID.equals(BuiltInRegistries.BLOCK.getKey(b).getNamespace());
    }

    private static boolean canAttachTo(LevelReader level, BlockPos pos, Direction attachSide) {
        BlockPos supportPos = pos.relative(attachSide.getOpposite());
        BlockState support = level.getBlockState(supportPos);
        return isSRP(support.getBlock()) && support.isFaceSturdy(level, supportPos, attachSide);
    }

    private static Direction attachFromOutward(Direction outward) {
        return outward.getAxis().isVertical() ? outward : outward.getOpposite();
    }

    private static Direction outwardFromAttach(Direction attach) {
        return attach.getAxis().isVertical() ? attach : attach.getOpposite();
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canAttachTo(level, pos, attachFromOutward(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clicked = context.getClickedFace();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return canAttachTo(level, pos, clicked) ? this.defaultBlockState().setValue(FACING, outwardFromAttach(clicked)) : null;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        this.checkAndDrop(level, pos, state);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        this.checkAndDrop(level, pos, state);
    }

    private void checkAndDrop(Level level, BlockPos pos, BlockState state) {
        if (!canAttachTo(level, pos, attachFromOutward(state.getValue(FACING)))) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (held.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (held.getItem() == this.asItem() && !state.getValue(GROUP)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(GROUP, Boolean.TRUE), 2);
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.6f, 1.0f);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
