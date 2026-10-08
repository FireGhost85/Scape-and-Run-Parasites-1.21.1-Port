package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.tileentity.TileEntityNodeRelay;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Invisible full block that fills the multiblock of the relay controller. Using it opens the controller, breaking it dismantles
 * the relay (the controller within 8 blocks is used when the stored position is missing). Drops nothing, has no item.
 */
public class BlockNodeRelay extends BaseEntityBlock {
    public static final MapCodec<BlockNodeRelay> CODEC = simpleCodec(p -> new BlockNodeRelay());

    public BlockNodeRelay() {
        super(SRPMaterial.IRON.props(3.0f, 10.0f).noOcclusion().noLootTable());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityNodeRelay(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) {
        return false;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof TileEntityNodeRelay node && node.getControllerPos() != null) {
            // The scanner menu (gui id 0 of the original) is ported with the Relay (M6).
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void tryDismantle(Level level, BlockPos nodePos, @Nullable Player player) {
        if (level.isClientSide) {
            return;
        }
        if (!(level.getBlockEntity(nodePos) instanceof TileEntityNodeRelay node)) {
            return;
        }
        BlockPos ctrlPos = node.getControllerPos();
        TileEntityRelayController controller = null;
        if (ctrlPos != null && level.isLoaded(ctrlPos) && level.getBlockEntity(ctrlPos) instanceof TileEntityRelayController c) {
            controller = c;
        }
        if (controller == null) {
            controller = this.findNearbyController(level, nodePos, 8);
        }
        if (controller != null) {
            controller.dismantle();
        } else if (player != null) {
            player.displayClientMessage(Component.literal("Relay controller missing or unloaded."), true);
        }
    }

    @Nullable
    private TileEntityRelayController findNearbyController(Level level, BlockPos origin, int radius) {
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -radius; dy <= radius; ++dy) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    BlockPos p = origin.offset(dx, dy, dz);
                    if (level.isLoaded(p) && level.getBlockEntity(p) instanceof TileEntityRelayController c) {
                        return c;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        this.tryDismantle(level, pos, player);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            this.tryDismantle(level, pos, null);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }
}
