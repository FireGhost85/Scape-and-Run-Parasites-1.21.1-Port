package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Parasite bush with five variants; it also hangs from SRP ceilings. {@code node} and {@code end} are kept up to date
 * by {@link #updateShape} and {@link #setPlacedBy} (1.12 computed them in {@code getActualState}).
 */
public class BlockParasiteBush extends BushBlock implements IVariantBlock<BlockParasiteBush.EnumType> {
    public static final MapCodec<BlockParasiteBush> CODEC = simpleCodec(BlockParasiteBush::new);
    public static final BooleanProperty NODE = BooleanProperty.create("node");
    public static final BooleanProperty END = BooleanProperty.create("end");
    protected static final VoxelShape TALL_GRASS_AABB = Block.box(0.09999999403953552 * 16.0, 0.0, 0.09999999403953552 * 16.0, 0.8999999761581421 * 16.0, 0.800000011920929 * 16.0, 0.8999999761581421 * 16.0);
    protected static final VoxelShape REED_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);

    public BlockParasiteBush(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.TENDRIL).setValue(NODE, Boolean.FALSE).setValue(END, Boolean.FALSE));
    }

    /** The block hardness is 0.2 whatever the constructor argument ({@code getBlockHardness}); the resistance follows the argument. */
    public BlockParasiteBush(float hardness) {
        this(SRPMaterial.VINE.props(hardness).sound(SoundType.GRASS).noCollission().destroyTime(0.2f));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(END, NODE, VARIANT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        EnumType variant = state.getValue(VARIANT);
        if (variant == EnumType.TENDRIL || variant == EnumType.BINE) {
            return REED_AABB;
        }
        return TALL_GRASS_AABB;
    }

    private static boolean isBush(Block b) {
        return b instanceof BlockParasiteBush;
    }

    /** 1.12 {@code canBlockStay}. */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        EnumType selfVariant = state.getValue(VARIANT);
        if ((selfVariant == EnumType.BINE || selfVariant == EnumType.TENDRIL) && this.hasSRPCeilingSupport(level, pos)) {
            return true;
        }
        BlockPos down = pos.below();
        BlockState below = level.getBlockState(down);
        String variantName = BushHelper.variantName(below);
        if (below.getBlock() instanceof BlockParasiteBush
                && ("tendril".equals(variantName) || "bine".equals(variantName) || "vine".equals(variantName) || "spine".equals(variantName))) {
            BlockPos base = BushHelper.columnBase(level, down, BlockParasiteBush::isBush);
            return BushHelper.isSrpSupport(level, base, Direction.UP);
        }
        return BushHelper.isSrpSupport(level, down, Direction.UP);
    }

    private boolean hasSRPCeilingSupport(LevelReader level, BlockPos pos) {
        BlockPos p = pos.above();
        for (int guard = 0; level.getBlockState(p).getBlock() instanceof BlockParasiteBush && guard < 64; ++guard) {
            p = p.above();
        }
        return BushHelper.isSrpSupport(level, p, Direction.DOWN);
    }

    /** 1.12 {@code canPlaceBlockOnSide}: on top of a support, or under an SRP ceiling. */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        if (side == Direction.UP) {
            BlockState below = level.getBlockState(pos.below());
            if (below.getBlock() instanceof BlockParasiteBush) {
                EnumType v = below.getValue(VARIANT);
                if (v == EnumType.TENDRIL || v == EnumType.BINE) {
                    BlockPos base = BushHelper.columnBase(level, pos.below(), BlockParasiteBush::isBush);
                    return BushHelper.isSrpSupport(level, base, Direction.UP) ? this.defaultBlockState() : null;
                }
                return null;
            }
            return BushHelper.isSrpSupport(level, pos.below(), Direction.UP) ? this.defaultBlockState() : null;
        }
        if (side == Direction.DOWN) {
            return this.hasSRPCeilingSupport(level, pos) ? this.defaultBlockState() : null;
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockState actual = this.withActual(state, level, pos);
        if (actual != state) {
            level.setBlock(pos, actual, 3);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return this.withActual(state, level, pos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (BushHelper.isRawMeat(held)) {
            if (!level.isClientSide) {
                popResource(level, pos.above(), this.variantStack(state));
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                level.levelEvent(2005, pos, 0);
                player.getCooldowns().addCooldown(held.getItem(), 10);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        if (!SRPConfigWorld.bushClimbingEnabled) {
            return false;
        }
        if (entity instanceof Player) {
            EnumType type = state.getValue(VARIANT);
            return type == EnumType.TENDRIL || type == EnumType.BINE;
        }
        return false;
    }

    /** Swords break the bush four times faster. */
    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty() && held.getItem() instanceof SwordItem) {
            return super.getDestroyProgress(state, player, level, pos) * 4.0f;
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public EnumType[] getVariants() {
        return EnumType.values();
    }

    @Override
    public EnumProperty<EnumType> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return this.variantStack(state);
    }

    /** 1.12 {@code getActualState}: sets {@code node} and {@code end} from the surroundings. */
    public BlockState withActual(BlockState state, BlockGetter world, BlockPos pos) {
        boolean node = false;
        boolean end = false;
        if (this.isVariant(state, "tooh")) {
            node = BushHelper.isSpecialGround(world, pos.below());
        }
        if (this.isVariant(state, "tendril")) {
            boolean bottom = world.getBlockState(pos.below()).isAir();
            boolean top = !bottom && world.getBlockState(pos.above()).isAir();
            end = top;
            node = bottom;
        }
        if (this.isVariant(state, "bine")) {
            boolean top = world.getBlockState(pos.above()).isAir();
            boolean bottom = !top && world.getBlockState(pos.below()).isAir();
            node = top;
            end = bottom;
        }
        return state.setValue(NODE, node).setValue(END, end);
    }

    private boolean isVariant(BlockState state, String name) {
        return state.getValue(VARIANT).getSerializedName().equals(name);
    }

    public enum EnumType implements StringRepresentable {
        TENDRIL,
        BINE,
        POP,
        EYE,
        TOOH;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }

        @Override
        public String toString() {
            return this.getSerializedName();
        }
    }
}
