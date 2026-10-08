package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/** Parasite trunk (ball, tree, plant, circle and deadhead variants) with an axis. */
public class BlockParasiteTrunk extends RotatedPillarBlock implements IVariantBlock<BlockParasiteTrunk.EnumType> {
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);

    public BlockParasiteTrunk(SRPMaterial material, float hardness, boolean tickRandom) {
        super(BlockBase.prop(material.props(hardness).sound(SoundType.WOOD), tickRandom));
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.TREE).setValue(AXIS, net.minecraft.core.Direction.Axis.Y));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT, AXIS);
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
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!level.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            return;
        }
        if (SRPBlockLinks.isParasiteBiome(level, pos)) {
            BlockParasiteSpreading.spreadBiomeBlockTrunk(level, pos, rand);
        }
    }

    /** Only the deadhead trunk keeps leaves alive (1.12 {@code canSustainLeaves}). */
    public static boolean canSustainLeaves(BlockState state) {
        return state.is(SRPBlocks.ParasiteTrunk.get()) && state.getValue(VARIANT) == EnumType.DEADHEAD;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            int range = 4;
            int loadedRange = range + 1;
            if (state.getValue(VARIANT) == EnumType.DEADHEAD
                    && level.hasChunksAt(pos.offset(-loadedRange, -loadedRange, -loadedRange), pos.offset(loadedRange, loadedRange, loadedRange))) {
                for (int x = -range; x <= range; ++x) {
                    for (int y = -range; y <= range; ++y) {
                        for (int z = -range; z <= range; ++z) {
                            BlockPos checkPos = pos.offset(x, y, z);
                            BlockState checkState = level.getBlockState(checkPos);
                            if (!checkState.is(BlockTags.LEAVES)) {
                                continue;
                            }
                            if (checkState.getBlock() instanceof ILeavesDecay decay) {
                                decay.beginLeavesDecay(checkState, level, checkPos);
                            } else if (checkState.getBlock() instanceof LeavesBlock) {
                                level.scheduleTick(checkPos, checkState.getBlock(), 1);
                            }
                        }
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public enum EnumType implements StringRepresentable {
        BALL,
        TREE,
        PLANT,
        CIRCLE,
        DEADHEAD;

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
