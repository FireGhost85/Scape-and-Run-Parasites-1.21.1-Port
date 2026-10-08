package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Warp diffuser: removes every connected SRP block (in a cube of radius 256) over several ticks. */
public class BlockEpitomeInfestationWarpDiffuser extends BlockBase {
    private static final int RADIUS = 256;
    private static final int MAX_BLOCKS_PER_TICK = 8192;
    private static final int MAX_BLOCKS_TOTAL = 2000000;
    private static final Map<String, DiffusionJob> JOBS = new HashMap<>();

    public BlockEpitomeInfestationWarpDiffuser() {
        super(prop(SRPMaterial.ROCK.props(15.0f, 120.0f).sound(SRPSoundTypes.FLESH).lightLevel(s -> (int) (15 * 0.4f)), false));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        String key = getJobKey(level, pos);
        if (JOBS.containsKey(key)) {
            player.displayClientMessage(Component.translatable("message.srparasites.diffuser.already_running"), true);
            return InteractionResult.SUCCESS;
        }
        DiffusionJob job = new DiffusionJob(pos);
        JOBS.put(key, job);
        level.scheduleTick(pos, this, 1);
        player.displayClientMessage(Component.translatable("message.srparasites.diffuser.started"), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        String key = getJobKey(level, pos);
        DiffusionJob job = JOBS.get(key);
        if (job == null) {
            return;
        }
        this.processJob(level, job);
        if (job.finished || job.queue.isEmpty() || job.removed >= MAX_BLOCKS_TOTAL) {
            JOBS.remove(key);
            ParasiteEventWorld.checkNodeStatus(level);
            ParasiteEventWorld.checkColonyStatus(level);
            if (job.removed > 0) {
                level.levelEvent(2001, pos, Block.getId(state));
            }
            return;
        }
        level.scheduleTick(pos, this, 1);
    }

    private int processJob(Level world, DiffusionJob job) {
        int removedThisTick = 0;
        while (!job.queue.isEmpty() && removedThisTick < MAX_BLOCKS_PER_TICK && job.removed < MAX_BLOCKS_TOTAL) {
            BlockPos current = BlockPos.of(job.queue.poll());
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    for (int z = -1; z <= 1; ++z) {
                        if (x == 0 && y == 0 && z == 0) {
                            continue;
                        }
                        BlockPos next = current.offset(x, y, z);
                        long nextLong = next.asLong();
                        if (job.visited.contains(nextLong)) {
                            continue;
                        }
                        job.visited.add(nextLong);
                        if (!this.isWithinRadius(job.origin, next) || !world.isLoaded(next) || !this.isRemovableSRPBlock(world.getBlockState(next))) {
                            continue;
                        }
                        world.setBlock(next, Blocks.AIR.defaultBlockState(), 3);
                        job.queue.add(nextLong);
                        job.removed++;
                        if (++removedThisTick < MAX_BLOCKS_PER_TICK && job.removed < MAX_BLOCKS_TOTAL) {
                            continue;
                        }
                        return removedThisTick;
                    }
                }
            }
        }
        job.finished = job.queue.isEmpty();
        return removedThisTick;
    }

    private boolean isWithinRadius(BlockPos origin, BlockPos pos) {
        return Math.abs(pos.getX() - origin.getX()) <= RADIUS && Math.abs(pos.getY() - origin.getY()) <= RADIUS && Math.abs(pos.getZ() - origin.getZ()) <= RADIUS;
    }

    private boolean isRemovableSRPBlock(BlockState state) {
        if (state.getBlock() == this) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return id != null && "srparasites".equals(id.getNamespace());
    }

    private static String getJobKey(Level world, BlockPos pos) {
        return world.dimension().location() + ":" + pos.asLong();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.srparasites.epitome_infestation_warp_diffuser.desc"));
        tooltip.add(Component.translatable("tooltip.srparasites.epitome_infestation_warp_diffuser.desc2"));
    }

    private static class DiffusionJob {
        private final BlockPos origin;
        private final Queue<Long> queue = new ArrayDeque<>();
        private final Set<Long> visited = new HashSet<>();
        private int removed;
        private boolean finished;

        private DiffusionJob(BlockPos origin) {
            this.origin = origin;
            this.queue.add(origin.asLong());
            this.visited.add(origin.asLong());
            this.removed = 0;
            this.finished = false;
        }
    }
}
