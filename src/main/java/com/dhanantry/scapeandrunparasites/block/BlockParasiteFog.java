package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Parasite fog: an air block (1.12 {@code Material.AIR}) with three stages ({@code air} property). A random tick promotes
 * stage 0 to 1; stage 2 turns every fog block within 2 blocks into stage 2 and then vanishes. It cannot be targeted, drops
 * nothing and can be bottled with a glass bottle.
 */
public class BlockParasiteFog extends Block {
    public static final MapCodec<BlockParasiteFog> CODEC = simpleCodec(BlockParasiteFog::new);
    public static final IntegerProperty STAGE = IntegerProperty.create("air", 0, 2);

    public BlockParasiteFog(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    public BlockParasiteFog() {
        this(BlockBehaviour.Properties.of().replaceable().noCollission().noLootTable().air().noOcclusion().randomTicks());
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        if (rand.nextFloat() < 0.1f) {
            double x = pos.getX() + 0.5 + rand.nextDouble() * 0.9;
            double y = pos.getY() + 0.05 + rand.nextDouble() * 0.9;
            double z = pos.getZ() + 0.5 + rand.nextDouble() * 0.9;
            BlockClientHooks.particle(SRPEnumParticle.COOLER_FOG, x, y, z, 0.0, 0.0, 0.0, 0, 0, 0);
        }
        if (rand.nextFloat() < 0.005f) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SRPSounds.FOG.get(), SoundSource.AMBIENT, 1.3f, 1.0f, false);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        super.tick(state, level, pos, rand);
        int meta = state.getValue(STAGE);
        if (meta == 0) {
            level.setBlockAndUpdate(pos, SRPBlocks.ParasiteFog.get().defaultBlockState().setValue(STAGE, 1));
        }
        if (meta == 2) {
            int range = 2;
            for (int x = -range; x <= range; ++x) {
                for (int z = -range; z <= range; ++z) {
                    for (int y = -range; y <= range; ++y) {
                        BlockPos other = pos.offset(x, y, z);
                        BlockState otherState = level.getBlockState(other);
                        if (!otherState.is(SRPBlocks.ParasiteFog.get()) || otherState.getValue(STAGE) == 2) {
                            continue;
                        }
                        level.setBlockAndUpdate(other, SRPBlocks.ParasiteFog.get().defaultBlockState().setValue(STAGE, 2));
                    }
                }
            }
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty() || !stack.is(Items.GLASS_BOTTLE)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            ItemStack fogBottle = new ItemStack(SRPItems.FOG_BOTTLE.get());
            if (!player.getInventory().add(fogBottle)) {
                player.drop(fogBottle, false);
            }
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, direction);
    }
}
