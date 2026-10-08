package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Inactive parasite canister: sac, cyst, lump and bag. The cyst has the low box and pops when nothing is below it. Drops:
 * the sac gives {@code itemlurecomponent6} with 5 percent (see notes), the others drop their own variant item.
 */
public class BlockParasiteCanister extends BlockBase implements IVariantBlock<BlockParasiteCanister.EnumType> {
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);
    protected static final VoxelShape TALL_GRASS_AABB = Block.box(1.6, 0.0, 1.6, 14.4, 8.8, 14.4);

    public BlockParasiteCanister(SRPMaterial material, float hardness, boolean tickRandom) {
        super(prop(material.props(hardness).noOcclusion(), tickRandom));
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.SAC));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(VARIANT) == EnumType.CYST) {
            return TALL_GRASS_AABB;
        }
        return super.getShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!level.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            return;
        }
        if (state.getValue(VARIANT) == EnumType.CYST && level.getBlockState(pos.below()).isAir()) {
            level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
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
        SAC,
        CYST,
        LUMP,
        BAG;

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
