package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

/** Dermoid cyst: a chest-like container (27 slots) that grows when opened. */
public class BlockDermoidCyst extends BlockBase implements EntityBlock {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    public BlockDermoidCyst() {
        super(prop(SRPMaterial.WOOD.props(2.5f).sound(SRPSoundTypes.FLESH), true));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new com.dhanantry.scapeandrunparasites.block.TileEntityDermoidCyst(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != SRPBlockEntities.DERMOID_CYST.get()) {
            return null;
        }
        return (lvl, pos, st, be) -> ((TileEntityDermoidCyst) be).update();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityDermoidCyst cyst) {
            player.openMenu(cyst);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide) {
                level.playSound(null, pos, SRPSounds.FLESH_GROWL.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityDermoidCyst cyst) {
                Containers.dropContents(level, pos, cyst);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityDermoidCyst cyst) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(cyst);
        }
        return 0;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity te = level.getBlockEntity(pos);
        return te != null && te.triggerEvent(id, param);
    }
}
