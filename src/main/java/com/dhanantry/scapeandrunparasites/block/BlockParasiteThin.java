package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

/**
 * Thin parasite pillar: a fence-like block. The 1.12 "actual state" (the six connection flags) is stored in the state and
 * refreshed on placement and neighbour updates. Outline: one box per connection combination of the horizontal arms; collision:
 * the pillar (0.5 to 1.5) plus a 1.5 high arm per connection.
 */
public class BlockParasiteThin extends Block {
    public static final MapCodec<BlockParasiteThin> CODEC = simpleCodec(BlockParasiteThin::new);
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST,
            Direction.SOUTH, SOUTH, Direction.WEST, WEST, Direction.UP, UP, Direction.DOWN, DOWN);

    protected static final VoxelShape[] BOUNDING_BOXES = new VoxelShape[]{
            Shapes.box(0.375, 0.0, 0.375, 0.625, 1.0, 0.625), Shapes.box(0.375, 0.0, 0.375, 0.625, 1.0, 1.0),
            Shapes.box(0.0, 0.0, 0.375, 0.625, 1.0, 0.625), Shapes.box(0.0, 0.0, 0.375, 0.625, 1.0, 1.0),
            Shapes.box(0.375, 0.0, 0.0, 0.625, 1.0, 0.625), Shapes.box(0.375, 0.0, 0.0, 0.625, 1.0, 1.0),
            Shapes.box(0.0, 0.0, 0.0, 0.625, 1.0, 0.625), Shapes.box(0.0, 0.0, 0.0, 0.625, 1.0, 1.0),
            Shapes.box(0.375, 0.0, 0.375, 1.0, 1.0, 0.625), Shapes.box(0.375, 0.0, 0.375, 1.0, 1.0, 1.0),
            Shapes.box(0.0, 0.0, 0.375, 1.0, 1.0, 0.625), Shapes.box(0.0, 0.0, 0.375, 1.0, 1.0, 1.0),
            Shapes.box(0.375, 0.0, 0.0, 1.0, 1.0, 0.625), Shapes.box(0.375, 0.0, 0.0, 1.0, 1.0, 1.0),
            Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.625), Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)};
    public static final VoxelShape PILLAR_AABB = Shapes.box(0.375, 0.5, 0.375, 0.625, 1.5, 0.625);
    public static final VoxelShape SOUTH_AABB = Shapes.box(0.375, 0.0, 0.625, 0.625, 1.5, 1.0);
    public static final VoxelShape WEST_AABB = Shapes.box(0.0, 0.0, 0.375, 0.375, 1.5, 0.625);
    public static final VoxelShape NORTH_AABB = Shapes.box(0.375, 0.0, 0.0, 0.625, 1.5, 0.375);
    public static final VoxelShape EAST_AABB = Shapes.box(0.625, 0.0, 0.375, 1.0, 1.5, 0.625);

    public BlockParasiteThin(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE).setValue(DOWN, Boolean.FALSE));
    }

    public BlockParasiteThin(float hardness) {
        this(SRPMaterial.WOOD.props(hardness).sound(SoundType.WOOD).noOcclusion());
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = PILLAR_AABB;
        if (state.getValue(NORTH)) {
            shape = Shapes.or(shape, NORTH_AABB);
        }
        if (state.getValue(EAST)) {
            shape = Shapes.or(shape, EAST_AABB);
        }
        if (state.getValue(SOUTH)) {
            shape = Shapes.or(shape, SOUTH_AABB);
        }
        if (state.getValue(WEST)) {
            shape = Shapes.or(shape, WEST_AABB);
        }
        return shape;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BOUNDING_BOXES[getBoundingBoxIdx(state)];
    }

    private static int getBoundingBoxIdx(BlockState state) {
        int i = 0;
        if (state.getValue(NORTH)) {
            i |= 1 << Direction.NORTH.get2DDataValue();
        }
        if (state.getValue(EAST)) {
            i |= 1 << Direction.EAST.get2DDataValue();
        }
        if (state.getValue(SOUTH)) {
            i |= 1 << Direction.SOUTH.get2DDataValue();
        }
        if (state.getValue(WEST)) {
            i |= 1 << Direction.WEST.get2DDataValue();
        }
        return i;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    public boolean canConnectTo(BlockGetter level, BlockPos pos, Direction facing) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        boolean pole = state.is(BlockTags.WOODEN_FENCES);
        boolean thin = block == SRPBlocks.ParasiteThin.get();
        boolean solid = state.isFaceSturdy(level, pos, facing) && (facing == Direction.UP || state.isSolidRender(level, pos));
        return !isExcepBlockForAttachWithPiston(state) && solid || pole || thin;
    }

    /** 1.12 {@code isExceptBlockForAttachWithPiston} plus the barrier, melon, pumpkin and jack o'lantern of the original. */
    protected static boolean isExcepBlockForAttachWithPiston(BlockState state) {
        return state.is(BlockTags.LEAVES) || state.is(BlockTags.SHULKER_BOXES) || state.is(BlockTags.TRAPDOORS) || state.is(Blocks.BEACON)
                || state.is(BlockTags.CAULDRONS) || state.is(Tags.Blocks.GLASS_BLOCKS_COLORLESS) || state.is(Tags.Blocks.GLASS_BLOCKS_CHEAP)
                || state.is(Blocks.GLOWSTONE) || state.is(Blocks.ICE) || state.is(Blocks.SEA_LANTERN) || state.is(Blocks.PISTON)
                || state.is(Blocks.STICKY_PISTON) || state.is(Blocks.PISTON_HEAD) || state.is(Blocks.BARRIER) || state.is(Blocks.MELON)
                || state.is(Blocks.PUMPKIN) || state.is(Blocks.JACK_O_LANTERN);
    }

    private boolean canFenceConnectTo(BlockGetter level, BlockPos pos, Direction facing) {
        return this.canConnectTo(level, pos.relative(facing), facing.getOpposite());
    }

    public boolean canBeConnectedTo(BlockGetter level, BlockPos pos, Direction facing) {
        return this.canConnectTo(level, pos.relative(facing), facing.getOpposite());
    }

    /** The 1.12 {@code getActualState}: all six connection flags from the neighbours. */
    private BlockState withConnections(BlockState state, BlockGetter level, BlockPos pos) {
        for (Map.Entry<Direction, BooleanProperty> e : PROPERTY_BY_DIRECTION.entrySet()) {
            state = state.setValue(e.getValue(), this.canFenceConnectTo(level, pos, e.getKey()));
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.withConnections(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.canFenceConnectTo(level, pos, direction));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return stack.is(Items.LEAD) || stack.isEmpty() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return LeadItem.bindPlayerMobs(player, level, pos).consumesAction() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return switch (rot) {
            case CLOCKWISE_180 -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST))
                    .setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90 -> state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH))
                    .setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
            case CLOCKWISE_90 -> state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH))
                    .setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
            default -> state;
        };
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
            case FRONT_BACK -> state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
            default -> super.mirror(state, mirror);
        };
    }
}
