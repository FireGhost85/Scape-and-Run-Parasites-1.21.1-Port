package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityNodeRelay;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

/**
 * Relay controller: the bottom block of the relay multiblock. When it is placed (or formed from base, middle and roof) it
 * fills the 38 positions around and above it with relay node blocks, or breaks itself with a message when something solid is in
 * the way. Breaking it dismantles the multiblock. The scanner screen is ported with the Relay (M6).
 */
public class BlockRelayController extends BaseEntityBlock {
    public static final MapCodec<BlockRelayController> CODEC = simpleCodec(p -> new BlockRelayController());
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    public BlockRelayController() {
        super(SRPMaterial.IRON.props(3.0f, 10.0f).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        // the tower is drawn by RenderRelayController
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, Direction.NORTH);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityRelayController(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, com.dhanantry.scapeandrunparasites.init.SRPBlockEntities.RELAY_CONTROLLER.get(), TileEntityRelayController::serverTick);
    }

    private static void spawnBreakSmoke(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel ws)) {
            return;
        }
        RandomSource r = level.random;
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                int puffs = dx == 0 && dz == 0 ? 10 : 6;
                for (int i = 0; i < puffs; ++i) {
                    double x = pos.getX() + 0.5 + dx + (r.nextDouble() - 0.5) * 0.9;
                    double y = pos.getY() + 0.05 + r.nextDouble() * 0.3;
                    double z = pos.getZ() + 0.5 + dz + (r.nextDouble() - 0.5) * 0.9;
                    double vx = (r.nextDouble() - 0.5) * 0.04;
                    double vy = 0.08 + r.nextDouble() * 0.06;
                    double vz = (r.nextDouble() - 0.5) * 0.04;
                    ws.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 1, vx, vy, vz, 0.0);
                }
            }
        }
        ws.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 10, 0.35, 0.45, 0.35, 0.0);
        ws.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 1.6, pos.getZ() + 0.5, 6, 0.25, 0.35, 0.25, 0.0);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            spawnBreakSmoke(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            net.minecraft.world.phys.BlockHitResult hit) {
        if (level.isClientSide) {
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof TileEntityRelayController ctrlTe) {
            player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inv, p) -> new com.dhanantry.scapeandrunparasites.container.ScannerContainer(id, inv, ctrlTe), net.minecraft.network.chat.Component.translatable("container.srparasites.scanner")), ctrlTe.getBlockPos());
        }
        return net.minecraft.world.InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) {
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof TileEntityRelayController ctrl)) {
            return;
        }
        List<BlockPos> nodes = this.computeNodePositions(pos);
        List<BlockPos> toClear = new ArrayList<>();
        for (BlockPos p : nodes) {
            if (level.isEmptyBlock(p)) {
                continue;
            }
            BlockState s = level.getBlockState(p);
            if (s.canBeReplaced()) {
                toClear.add(p);
                continue;
            }
            if (placer instanceof Player player) {
                player.displayClientMessage(Component.translatable("block.srparasites.relay_controller.no_space"), true);
            }
            level.destroyBlock(pos, true);
            return;
        }
        for (BlockPos p : toClear) {
            level.removeBlock(p, false);
        }
        for (BlockPos p : nodes) {
            level.setBlock(p, SRPBlocks.NODE_RELAY.get().defaultBlockState(), 2);
            if (level.getBlockEntity(p) instanceof TileEntityNodeRelay node) {
                node.setControllerPos(pos);
            }
        }
        ctrl.setChildPositions(nodes);
        ctrl.setFormed(true);
        ctrl.setChanged();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof TileEntityRelayController ctrl) {
            ctrl.dismantle();
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private List<BlockPos> computeNodePositions(BlockPos origin) {
        List<BlockPos> out = new ArrayList<>();
        List<BlockPos> rel = new ArrayList<>();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                rel.add(new BlockPos(dx, 0, dz));
            }
        }
        for (int y = 1; y <= 3; ++y) {
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dz = -1; dz <= 1; ++dz) {
                    rel.add(new BlockPos(dx, y, dz));
                }
            }
        }
        for (int y = 4; y <= 6; ++y) {
            rel.add(new BlockPos(0, y, 0));
        }
        for (BlockPos r : rel) {
            out.add(origin.offset(r.getX(), r.getY(), r.getZ()));
        }
        return out;
    }
}
