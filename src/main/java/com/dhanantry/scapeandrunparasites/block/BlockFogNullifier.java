package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Fog nullifier: clears the whole connected parasite fog next to it (up to 500000 blocks) when placed, used or updated by a
 * neighbour, and spends one use per clearing. The remaining uses travel on the item ({@code UsesRemaining} in the custom
 * data); a spent nullifier breaks and drops nothing. Hardness 2, resistance 10, pickaxe 0. The smoke particles of the original
 * were spawned with the client-only {@code World.spawnParticle} on the server and never appeared, so they are not ported.
 */
public class BlockFogNullifier extends BlockBase implements EntityBlock {
    private static final String TAG_USES = "UsesRemaining";

    public BlockFogNullifier() {
        super(SRPMaterial.ROCK.props(2.0f, 10.0f).noOcclusion());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityFogNullifier(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof TileEntityFogNullifier te) {
                int from = getUsesFromStack(stack);
                te.setUsesRemaining(from >= 0 ? from : Math.max(0, SRPConfigWorld.fogNullifierMaxUses));
                te.setChanged();
            }
            this.attemptConsumeUseAndClear(level, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            this.attemptConsumeUseAndClear(level, pos);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        return this.attemptConsumeUseAndClear(level, pos) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    private boolean attemptConsumeUseAndClear(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof TileEntityFogNullifier data)) {
            return false;
        }
        if (data.getUsesRemaining() <= 0) {
            return false;
        }
        int cleared = this.clearConnectedFog(level, pos);
        if (cleared > 0) {
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.9f, 1.0f);
            int left = data.getUsesRemaining() - 1;
            data.setUsesRemaining(left);
            data.setChanged();
            if (left <= 0) {
                level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.8f, 0.9f);
                level.destroyBlock(pos, false);
            }
            return true;
        }
        return false;
    }

    private int clearConnectedFog(Level level, BlockPos origin) {
        ArrayDeque<BlockPos> q = new ArrayDeque<>();
        HashSet<BlockPos> seen = new HashSet<>();
        for (Direction f : Direction.values()) {
            BlockPos n = origin.relative(f);
            if (!this.isParasiteFog(level, n)) {
                continue;
            }
            q.add(n);
            seen.add(n);
        }
        if (q.isEmpty()) {
            return 0;
        }
        int cleared = 0;
        int cap = 500000;
        while (!q.isEmpty() && cleared < cap) {
            BlockPos p = q.pollFirst();
            if (!this.isParasiteFog(level, p)) {
                continue;
            }
            level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            ++cleared;
            for (Direction f : Direction.values()) {
                BlockPos n = p.relative(f);
                if (seen.contains(n) || !this.isParasiteFog(level, n)) {
                    continue;
                }
                seen.add(n);
                q.addLast(n);
            }
        }
        return cleared;
    }

    private boolean isParasiteFog(Level level, BlockPos p) {
        return level.getBlockState(p).is(SRPBlocks.ParasiteFog.get());
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity te = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (te instanceof TileEntityFogNullifier nullifier && nullifier.getUsesRemaining() > 0) {
            ItemStack stack = new ItemStack(this);
            setUsesOnStack(stack, nullifier.getUsesRemaining());
            return List.of(stack);
        }
        return List.of();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack out = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof TileEntityFogNullifier te && te.getUsesRemaining() >= 0) {
            setUsesOnStack(out, te.getUsesRemaining());
        }
        return out;
    }

    private static int getUsesFromStack(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null && data.contains(TAG_USES)) {
            return data.copyTag().getInt(TAG_USES);
        }
        return -1;
    }

    private static void setUsesOnStack(ItemStack stack, int uses) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(TAG_USES, Math.max(0, uses)));
    }
}
