package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * Parasite rubble. The 1.12 actual state (WEATHB/WEATHBC/WEATHFS swap to the snow variants when snow lies above, and the
 * stored snow variants swap back without snow) is kept in the {@code snowy} property that the blockstate file uses to
 * pick the model, so the stored variant (and therefore the drop) is never changed.
 */
public class BlockParasiteRubble extends BlockParasiteSpreading implements IVariantBlock<BlockParasiteRubble.EnumType> {
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);
    public static final BooleanProperty SNOWY = BooleanProperty.create("snowy");

    public BlockParasiteRubble(SRPMaterial material, float hardness, boolean infested) {
        super(material, hardness, infested);
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.BONE).setValue(SNOWY, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT, SNOWY);
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

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(SNOWY, isSnow(context.getLevel().getBlockState(context.getClickedPos().above())));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return dir == Direction.UP ? state.setValue(SNOWY, isSnow(neighbor)) : state;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        BlockClientHooks.spore(level, pos, rand);
    }

    public enum EnumType implements StringRepresentable {
        FLESH,
        BONE,
        STONE,
        STONEDEBRIS,
        WOOD,
        BRICKS,
        METAL,
        OBSIDIAN,
        FUNGUS,
        WEATHB,
        WEATHBS,
        WEATHBC,
        WEATHBCS,
        WEATHFS,
        WEATHFSS;

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
