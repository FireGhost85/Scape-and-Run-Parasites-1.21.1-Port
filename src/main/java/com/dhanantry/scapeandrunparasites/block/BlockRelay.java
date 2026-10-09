package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * One of the three relay blocks (base, middle, roof) and the controller dummy. Stacking base, middle and roof turns the
 * base into a relay controller. Right click prints the debug line about hostile mobs of the original.
 */
public class BlockRelay extends Block {
    public BlockRelay() {
        super(SRPMaterial.IRON.props(2.0f, 10.0f));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            this.tryFormRelay(level, pos, placer);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            this.tryFormRelay(level, pos, null);
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level instanceof ServerLevel server) {
            if (this.isStructureComplete(level, pos)) {
                int hostile = 0;
                int parasiteHostile = 0;
                for (Entity e : server.getAllEntities()) {
                    if (!(e instanceof Mob mob) || mob.getType().getCategory() != MobCategory.MONSTER) {
                        continue;
                    }
                    ++hostile;
                    ResourceLocation rl = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
                    if ("srparasites".equals(rl.getNamespace())) {
                        ++parasiteHostile;
                    }
                }
                double percentage = hostile == 0 ? 0.0 : parasiteHostile * 100.0 / hostile;
                player.sendSystemMessage(Component.literal(String.format("\u00a7a[DEBUG] Hostile mobs: %d | Parasite hostiles: %d (%.1f%%)", hostile, parasiteHostile, percentage)));
            } else {
                player.sendSystemMessage(Component.literal("\u00a7c[DEBUG] Structure incomplete."));
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void tryFormRelay(Level level, BlockPos pos, @Nullable LivingEntity placer) {
        BlockPos[] candidateMiddles = new BlockPos[]{pos, pos.above(), pos.below()};
        for (BlockPos mid : candidateMiddles) {
            BlockPos bottom = mid.below();
            BlockPos top = mid.above();
            if (!level.getBlockState(bottom).is(SRPBlocks.RelayBase.get()) || !level.getBlockState(mid).is(SRPBlocks.RelayMiddle.get())
                    || !level.getBlockState(top).is(SRPBlocks.RelayRoof.get())) {
                continue;
            }
            BlockState controllerState = SRPBlocks.RELAY_CONTROLLER.get().defaultBlockState().setValue(BlockRelayController.FACING, net.minecraft.core.Direction.NORTH);
            level.destroyBlock(mid, false);
            level.destroyBlock(top, false);
            level.setBlock(bottom, controllerState, 2);
            ((BlockRelayController) SRPBlocks.RELAY_CONTROLLER.get()).setPlacedBy(level, bottom, controllerState, placer, ItemStack.EMPTY);
            return;
        }
    }

    private boolean isStructureComplete(Level level, BlockPos pos) {
        BlockPos bottom = this.findBottom(level, pos);
        boolean isBase = level.getBlockState(bottom).is(SRPBlocks.RelayBase.get());
        boolean isMiddle = level.getBlockState(bottom.above()).is(SRPBlocks.RelayMiddle.get());
        boolean isRoof = level.getBlockState(bottom.above(2)).is(SRPBlocks.RelayRoof.get());
        return isBase && isMiddle && isRoof;
    }

    private BlockPos findBottom(Level level, BlockPos pos) {
        while (level.getBlockState(pos.below()).getBlock() instanceof BlockRelay) {
            pos = pos.below();
        }
        return pos;
    }
}
