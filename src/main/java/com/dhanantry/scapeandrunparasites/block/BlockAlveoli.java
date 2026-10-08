package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BlockAlveoli extends BlockBase {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty DEPLETED = BooleanProperty.create("depleted");

    public BlockAlveoli() {
        super(prop(SRPMaterial.CLAY.props(1.0f).sound(SRPSoundTypes.FLESH).noOcclusion(), true));
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, true).setValue(DEPLETED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, DEPLETED);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (held.is(Items.GLASS_BOTTLE)) {
            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                ItemStack fluid = new ItemStack(SRPItems.ALVEOLAR_FLUID.get());
                if (!player.getInventory().add(fluid)) {
                    player.drop(fluid, false);
                }
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                setDepleted(level, pos, state);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean near = isNearFollicle(context.getLevel(), context.getClickedPos(), 6);
        return defaultBlockState().setValue(ACTIVE, near).setValue(DEPLETED, false);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!state.getValue(DEPLETED)) {
            boolean near = isNearFollicle(level, pos, 6);
            if (state.getValue(ACTIVE) != near) {
                level.setBlock(pos, state.setValue(ACTIVE, near), 2);
            }
        }
    }

    private void setDepleted(Level level, BlockPos pos, BlockState state) {
        BlockState s = state.setValue(DEPLETED, true).setValue(ACTIVE, false);
        level.setBlock(pos, s, 3);
        level.scheduleTick(pos, this, 1200);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(DEPLETED)) {
            boolean near = isNearFollicle(level, pos, 6);
            level.setBlock(pos, state.setValue(DEPLETED, false).setValue(ACTIVE, near), 3);
        } else {
            boolean near = isNearFollicle(level, pos, 6);
            if (state.getValue(ACTIVE) != near) {
                level.setBlock(pos, state.setValue(ACTIVE, near), 3);
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (state.getValue(DEPLETED)) {
            return;
        }
        boolean near = isNearFollicle(level, pos, 6);
        if (state.getValue(ACTIVE) != near) {
            level.setBlock(pos, state.setValue(ACTIVE, near), 3);
        }
    }

    private boolean isNearFollicle(Level level, BlockPos pos, int r) {
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; ++dx) {
            for (int dy = -r; dy <= r; ++dy) {
                for (int dz = -r; dz <= r; ++dz) {
                    p.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    BlockState s = level.getBlockState(p);
                    if (BuiltInRegistries.BLOCK.getKey(s.getBlock()).getPath().contains("hair_follicle")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
