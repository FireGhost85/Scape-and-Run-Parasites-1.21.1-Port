package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Parasite barrier: indestructible, unmovable; right click cycles the radius (1 to 10 chunks) for creative players and
 * operators. The field logic is in {@link TileEntityParasiteBarrier}.
 */
public class BlockParasiteBarrier extends BlockBase implements EntityBlock {
    public BlockParasiteBarrier() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(-1.0f, 6000000.0f).sound(SoundType.STONE)
                .pushReaction(PushReaction.BLOCK).randomTicks());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityParasiteBarrier(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == SRPBlockEntities.PARASITE_BARRIER.get() ? (lvl, pos, st, be) -> ((TileEntityParasiteBarrier) be).update() : null;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof TileEntityParasiteBarrier b) {
            b.initCenterFromPos(pos);
            b.scrubMobsAroundField();
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player pl, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!canConfigure(pl)) {
            pl.sendSystemMessage(Component.translatable("msg.srparasites.barrier.no_permission").withStyle(ChatFormatting.RED));
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof TileEntityParasiteBarrier b) {
            int r = b.getRadiusChunks();
            r = r % 10 + 1;
            b.setRadiusChunks(r);
            pl.sendSystemMessage(Component.translatable("msg.srparasites.barrier.radius_set", r).withStyle(ChatFormatting.GREEN));
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean canConfigure(Player pl) {
        return pl.getAbilities().instabuild || pl.hasPermissions(2);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
    }
}
