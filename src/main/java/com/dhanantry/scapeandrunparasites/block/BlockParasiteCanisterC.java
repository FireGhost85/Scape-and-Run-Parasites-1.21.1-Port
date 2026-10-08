package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityCanister;
import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Active parasite canister: a 40 slot container that eats its contents (see {@link TileEntityCanister}). */
public class BlockParasiteCanisterC extends BaseEntityBlock {
    public static final MapCodec<BlockParasiteCanisterC> CODEC = simpleCodec(BlockParasiteCanisterC::new);
    protected static final VoxelShape TALL_GRASS_AABB = net.minecraft.world.level.block.Block.box(1.6, 0.0, 1.6, 14.4, 8.8, 14.4);

    public BlockParasiteCanisterC(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockParasiteCanisterC(SRPMaterial material, float hardness, boolean tickRandom) {
        this(BlockBase.prop(material.props(hardness).noOcclusion(), tickRandom));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return TALL_GRASS_AABB;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityCanister(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, SRPBlockEntities.CANISTER.get(), (lvl, pos, st, be) -> be.update());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof TileEntityCanister canister) {
            player.openMenu(canister);
            player.awardStat(Stats.OPEN_CHEST);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof TileEntityCanister canister) {
                Containers.dropContents(level, pos, canister);
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
        return level.getBlockEntity(pos) instanceof TileEntityCanister canister ? AbstractContainerMenu.getRedstoneSignalFromContainer(canister) : 0;
    }
}
