package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedBush;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedRemain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayDeque;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Greek fire: burns away a whole connected cluster of parasite bushes / remains / gore (4 uses). */
public class ItemGreekFire extends ItemBase {
    private static final int MAX_SPREAD = 12000;
    private static final int LINK_RADIUS = 8;
    private static final int PARTICLE_EVERY = 8;
    private static final int MAX_PARTICLE_SPAWNS = 24;

    public ItemGreekFire(String name, int maxStack, int id) {
        super(new Item.Properties().stacksTo(1).durability(4), id);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!this.isTargetBush(block)) {
            return InteractionResult.FAIL;
        }
        this.burnBushCluster(world, pos);
        world.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0f, 0.9f + world.random.nextFloat() * 0.2f);
        if (player != null && !player.isCreative()) {
            context.getItemInHand().hurtAndBreak(1, player, net.minecraft.world.entity.LivingEntity.getSlotForHand(context.getHand()));
        }
        return InteractionResult.SUCCESS;
    }

    private void burnBushCluster(Level world, BlockPos origin) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        LongOpenHashSet visited = new LongOpenHashSet();
        queue.add(origin);
        visited.add(packPos(origin.getX(), origin.getY(), origin.getZ()));
        int burned = 0;
        int particleBursts = 0;
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();
        while (!queue.isEmpty() && burned < MAX_SPREAD) {
            BlockPos current = queue.poll();
            BlockState state = world.getBlockState(current);
            Block block = state.getBlock();
            if (!this.isTargetBush(block)) continue;
            world.removeBlock(current, false);
            if (++burned % PARTICLE_EVERY == 0 && particleBursts < MAX_PARTICLE_SPAWNS) {
                this.spawnSmoke(world, current);
                ++particleBursts;
            }
            int cx = current.getX();
            int cy = current.getY();
            int cz = current.getZ();
            for (int dx = -LINK_RADIUS; dx <= LINK_RADIUS; ++dx) {
                for (int dy = -LINK_RADIUS; dy <= LINK_RADIUS; ++dy) {
                    for (int dz = -LINK_RADIUS; dz <= LINK_RADIUS; ++dz) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        int nx = cx + dx;
                        int ny = cy + dy;
                        int nz = cz + dz;
                        long key;
                        if (ny < world.getMinBuildHeight() || ny >= world.getMaxBuildHeight() || visited.contains(key = packPos(nx, ny, nz))) continue;
                        visited.add(key);
                        scanPos.set(nx, ny, nz);
                        if (!this.isTargetBush(world.getBlockState(scanPos).getBlock())) continue;
                        queue.add(new BlockPos(nx, ny, nz));
                    }
                }
            }
        }
        if (burned > 0 && particleBursts == 0) {
            this.spawnSmoke(world, origin);
        }
    }

    private void spawnSmoke(Level world, BlockPos pos) {
        if (!(world instanceof ServerLevel ws)) {
            return;
        }
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        ws.sendParticles(ParticleTypes.CLOUD, x, y, z, 4, 0.25, 0.25, 0.25, 0.01);
        ws.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 2, 0.2, 0.2, 0.2, 0.01);
    }

    private boolean isTargetBush(Block block) {
        return block instanceof BlockParasiteBush || block instanceof BlockInfestedBush || block instanceof BlockInfestedRemain || block instanceof BlockGore;
    }

    private static long packPos(int x, int y, int z) {
        return (long) (x & 0x3FFFFFF) << 38 | (long) (y & 0xFFF) << 26 | (long) (z & 0x3FFFFFF);
    }
}
