package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Gore: a small bush on full blocks that infects living entities walking through it and rots away. */
public class BlockGore extends BushBlock implements IVariantBlock<BlockGore.EnumType> {
    public static final MapCodec<BlockGore> CODEC = simpleCodec(BlockGore::new);
    protected static final VoxelShape TALL_GRASS_AABB = Block.box(0.09999999403953552 * 16.0, 0.0, 0.09999999403953552 * 16.0, 0.8999999761581421 * 16.0, 0.800000011920929 * 16.0, 0.8999999761581421 * 16.0);
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);

    public BlockGore(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.SMALL));
    }

    public BlockGore() {
        this(SRPMaterial.VINE.props(0.4f).sound(SRPSoundTypes.FLESH).noCollission().randomTicks());
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return TALL_GRASS_AABB;
    }

    public boolean canBlockStay(LevelReader level, BlockPos pos, BlockState state) {
        return this.checkBush(level, pos.below(), level.getBlockState(pos.below()));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.canBlockStay(level, pos, state);
    }

    protected boolean checkBush(LevelReader level, BlockPos pos, BlockState state) {
        return state.isCollisionShapeFullBlock(level, pos);
    }

    /** The original tested {@code canBlockStay} at the position of the changed neighbour instead of its own position. */
    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!this.canBlockStay(level, fromPos, state)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide) {
            if (level.random.nextDouble() < 0.5 && entity.tickCount % 20 != 0) {
                return;
            }
            if (!(entity instanceof EntityParasiteBase) && entity instanceof LivingEntity target
                    && !target.hasEffect(SRPPotions.COTH_E) && !target.hasEffect(SRPPotions.EPEL_E)) {
                target.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        int air = 10;
        EnumType c = state.getValue(VARIANT);
        if (c == EnumType.BIG) {
            air = 45;
        }
        if (level.random.nextInt(air) == 0) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
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

    public enum EnumType implements StringRepresentable {
        FLAT,
        SMALL,
        BIG;

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
