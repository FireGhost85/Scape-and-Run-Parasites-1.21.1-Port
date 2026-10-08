package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.mojang.serialization.MapCodec;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Short hanging deadhead grass. The 1.12 {@code texture} property (position hash picking one of five textures) is now a
 * random model list in the blockstate file, which Minecraft picks by position as well.
 */
public class BlockDeadheadGrassShort extends BushBlock {
    public static final MapCodec<BlockDeadheadGrassShort> CODEC = simpleCodec(BlockDeadheadGrassShort::new);
    /** The original returned {@code Block.FULL_BLOCK_AABB} as its bounding box. */
    protected static final VoxelShape SHAPE = Shapes.block();

    public BlockDeadheadGrassShort(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockDeadheadGrassShort() {
        this(SRPMaterial.VINE.props(0.0f).sound(SoundType.GRASS).noCollission().replaceable());
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return isValidDeadheadSupport(level, pos.above());
    }

    /** The block above must be a deadhead trunk, deadhead leaves or a full deadhead block (by registry name). */
    static boolean isValidDeadheadSupport(BlockGetter world, BlockPos supportPos) {
        BlockState supportState = world.getBlockState(supportPos);
        Block supportBlock = supportState.getBlock();
        if (supportBlock == SRPBlocks.ParasiteTrunk.get()) {
            return supportState.getValue(BlockParasiteTrunk.VARIANT) == BlockParasiteTrunk.EnumType.DEADHEAD;
        }
        if (supportBlock instanceof BlockDeadheadLeaves) {
            return true;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(supportBlock);
        if (id == null || !supportState.isCollisionShapeFullBlock(world, supportPos)) {
            return false;
        }
        String path = id.getPath().toLowerCase(Locale.ROOT);
        return path.contains("deadhead") || path.contains("dead_head");
    }
}
