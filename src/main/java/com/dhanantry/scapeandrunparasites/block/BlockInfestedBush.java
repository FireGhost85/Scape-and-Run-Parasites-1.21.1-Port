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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.SoundType;

/**
 * Infested bush with six variants. {@code node} and {@code end} were computed in 1.12 {@code getActualState}; they are now
 * real blockstate properties kept up to date by {@link #updateShape} and {@link #setPlacedBy}.
 */
public class BlockInfestedBush extends BushBlock implements IVariantBlock<BlockInfestedBush.EnumType> {
    public static final MapCodec<BlockInfestedBush> CODEC = simpleCodec(BlockInfestedBush::new);
    protected static final VoxelShape TALL_GRASS_AABB = Block.box(0.09999999403953552 * 16.0, 0.0, 0.09999999403953552 * 16.0, 0.8999999761581421 * 16.0, 0.800000011920929 * 16.0, 0.8999999761581421 * 16.0);
    protected static final VoxelShape REED_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    public static final BooleanProperty NODE = BooleanProperty.create("node");
    public static final BooleanProperty END = BooleanProperty.create("end");
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);

    public BlockInfestedBush(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.INFECTED).setValue(NODE, Boolean.FALSE).setValue(END, Boolean.FALSE));
    }

    public BlockInfestedBush(float hardness) {
        this(SRPMaterial.VINE.props(hardness).sound(SoundType.GRASS).noCollission());
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
        if (variant == EnumType.SPINE || variant == EnumType.VINE) {
            return REED_AABB;
        }
        return TALL_GRASS_AABB;
    }

    private static boolean isBush(Block b) {
        return b instanceof BlockInfestedBush || b instanceof BlockParasiteBush;
    }

    /** 1.12 {@code canBlockStay}. */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos down = pos.below();
        BlockState below = level.getBlockState(down);
        Block belowBlock = below.getBlock();
        String variantName = BushHelper.variantName(below);
        if (isBush(belowBlock) && ("spine".equals(variantName) || "vine".equals(variantName))) {
            BlockPos base = BushHelper.columnBase(level, down, BlockInfestedBush::isBush);
            return BushHelper.isSrpSupport(level, base, Direction.UP);
        }
        return BushHelper.isSrpSupport(level, down, Direction.UP);
    }

    /** 1.12 {@code canPlaceBlockOnSide}: only on the top face. */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace() != Direction.UP) {
            return null;
        }
        return this.defaultBlockState();
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
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
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
            return type == EnumType.SPINE;
        }
        return false;
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

    private boolean isSameVariantAbove(BlockGetter world, BlockPos pos, String variantName) {
        BlockState above = world.getBlockState(pos.above());
        if (above.getBlock() != this) {
            return false;
        }
        return above.getValue(VARIANT).toString().equals(variantName);
    }

    /** 1.12 {@code getActualState}: sets {@code node} and {@code end} from the surroundings. */
    public BlockState withActual(BlockState state, BlockGetter world, BlockPos pos) {
        boolean node = false;
        boolean end = false;
        if (this.isVariant(state, "grass1")) {
            node = BushHelper.isSpecialGround(world, pos.below());
        } else if (this.isVariant(state, "spine")) {
            BlockPos scan = pos;
            BlockState s;
            BlockPos down;
            while ((s = world.getBlockState(down = scan.below())).getBlock() == this && this.isVariant(s, "spine")) {
                scan = down;
            }
            node = BushHelper.isSpecialGround(world, scan.below());
            end = !this.isSameVariantAbove(world, pos, "spine");
        }
        return state.setValue(NODE, node).setValue(END, end);
    }

    private boolean isVariant(BlockState state, String name) {
        return state.getValue(VARIANT).getSerializedName().equals(name);
    }

    public enum EnumType implements StringRepresentable {
        INFECTED,
        GRASS1,
        FLOWER1,
        SPINE,
        VINE,
        ARC;

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
