package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenDeadheadTreeStructure;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTallFlower;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTree;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTreeThin;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.event.EventHooks;

/** Parasite sapling: grows a parasite tree or flower in two stages. */
public class BlockParasiteSapling extends BushBlock implements BonemealableBlock, IVariantBlock<BlockParasiteSapling.EnumType> {
    public static final MapCodec<BlockParasiteSapling> CODEC = simpleCodec(BlockParasiteSapling::new);
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 1);
    protected static final VoxelShape SAPLING_AABB = Block.box(0.09999999403953552 * 16.0, 0.0, 0.09999999403953552 * 16.0, 0.8999999761581421 * 16.0, 0.800000011920929 * 16.0, 0.8999999761581421 * 16.0);

    public BlockParasiteSapling(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.TREE).setValue(STAGE, 0));
    }

    public BlockParasiteSapling() {
        this(SRPMaterial.PLANTS.props(0.0f).sound(SoundType.STONE).noCollission().randomTicks());
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT, STAGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SAPLING_AABB;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!level.hasChunksAt(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            return;
        }
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && rand.nextInt(7) == 0) {
            this.grow(level, pos, state, rand);
        }
    }

    public void grow(Level level, BlockPos pos, BlockState state, RandomSource rand) {
        if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.cycle(STAGE), 4);
        } else {
            this.generateTree(level, pos, state, rand);
        }
    }

    public void generateTree(Level level, BlockPos pos, BlockState state, RandomSource rand) {
        if (EventHooks.fireBlockGrowFeature(level, rand, pos, null).isCanceled()) {
            return;
        }
        boolean generated;
        switch (state.getValue(VARIANT)) {
            case TREETHIN:
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
                generated = new WorldGenParasiteTreeThin(true).generate(level, rand, pos);
                break;
            case FLOWERTALL:
            case INFESTED:
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
                generated = new WorldGenParasiteTallFlower(true).generate(level, rand, pos);
                break;
            case DEADHEAD:
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
                generated = new WorldGenDeadheadTreeStructure(true).generate(level, rand, pos);
                break;
            case TREE:
            case CONSUMED:
            default:
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
                generated = new WorldGenParasiteTree(true).generate(level, rand, pos);
                break;
        }
        if (!generated) {
            level.setBlock(pos, state, 4);
        }
    }

    /** Growth by the infested bone meal: first stage, then the consumed, deadhead and infested trees only. */
    public boolean tryGrowWithInfestedBonemeal(Level world, BlockPos pos, BlockState state, RandomSource rand) {
        if (state.getValue(STAGE) == 0) {
            return world.setBlock(pos, state.setValue(STAGE, 1), 4);
        }
        boolean generated;
        switch (state.getValue(VARIANT)) {
            case CONSUMED:
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
                generated = new WorldGenParasiteTree(true).generate(world, rand, pos);
                break;
            case DEADHEAD:
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
                generated = new WorldGenDeadheadTreeStructure(true).generate(world, rand, pos);
                break;
            case INFESTED:
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
                generated = new WorldGenParasiteTallFlower(true).generate(world, rand, pos);
                break;
            default:
                return false;
        }
        if (!generated) {
            world.setBlock(pos, state, 4);
            return false;
        }
        return true;
    }

    /** 1.12 {@code canBlockStay}: the deadhead sapling also stands on grass, dirt and farmland. */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos downPos = pos.below();
        BlockState ground = level.getBlockState(downPos);
        if (state.getValue(VARIANT) == EnumType.DEADHEAD) {
            return this.checkBush(level, downPos, ground) || this.checkVanillaSaplingSoil(ground);
        }
        return this.checkBush(level, downPos, ground);
    }

    protected boolean checkBush(BlockGetter level, BlockPos pos, BlockState state) {
        Block b = state.getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
        if (id == null) {
            return false;
        }
        if (!"srparasites".equals(id.getNamespace())) {
            return false;
        }
        return state.isCollisionShapeFullBlock(level, pos) && state.canOcclude();
    }

    private boolean checkVanillaSaplingSoil(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.PODZOL) || state.is(Blocks.FARMLAND);
    }

    public boolean isTypeAt(Level level, BlockPos pos, EnumType type) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() == this && state.getValue(VARIANT) == type;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource rand, BlockPos pos, BlockState state) {
        return (double) level.random.nextFloat() < 0.45;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource rand, BlockPos pos, BlockState state) {
        this.grow(level, pos, state, rand);
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
        TREE,
        TREETHIN,
        FLOWERTALL,
        CONSUMED,
        DEADHEAD,
        INFESTED;

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
