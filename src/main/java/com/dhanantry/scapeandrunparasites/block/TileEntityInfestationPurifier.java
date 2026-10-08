package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.dhanantry.scapeandrunparasites.network.PureParticlesPayload;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Block entity of the infestation purifier: a breadth first walk over the connected SRP blocks (26 neighbours plus a 13x13x13
 * cube around each block) that turns up to 131072 blocks into their vanilla counterparts, 32 per tick, puts infest remain on
 * top and reports the total to the player that started it.
 */
public class TileEntityInfestationPurifier extends BlockEntity {
    private static final int MAX_TOTAL = 131072;
    private static final int NODES_PER_TICK = 32;
    private static final int SPREAD_RADIUS = 6;
    private static final ResourceLocation INFEST_REMAIN_RL = ResourceLocation.fromNamespaceAndPath("srparasites", "infestremain");
    private static Block cachedInfestRemain = null;
    private final ArrayDeque<BlockPos> queue = new ArrayDeque<>();
    private final HashSet<Long> visited = new HashSet<>();
    private final HashSet<Long> inQueue = new HashSet<>();
    private boolean running = false;
    private int total = 0;
    private UUID starter;

    public TileEntityInfestationPurifier(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.INFESTATION_PURIFIER.get(), pos, state);
    }

    public void startAt(BlockPos start, UUID starterUuid) {
        this.queue.clear();
        this.visited.clear();
        this.inQueue.clear();
        this.running = true;
        this.total = 0;
        this.starter = starterUuid;
        this.offer(start);
        this.setChanged();
    }

    private void offer(BlockPos p) {
        long k = p.asLong();
        if (this.visited.contains(k) || this.inQueue.contains(k)) {
            return;
        }
        this.queue.add(p);
        this.inQueue.add(k);
    }

    public void update() {
        if (this.level == null || this.level.isClientSide || !this.running) {
            return;
        }
        if (cachedInfestRemain == null) {
            cachedInfestRemain = BuiltInRegistries.BLOCK.getOptional(INFEST_REMAIN_RL).orElse(null);
        }
        Level level = this.level;
        int processed = 0;
        BlockPos cur;
        while (processed < NODES_PER_TICK && !this.queue.isEmpty() && this.total < MAX_TOTAL && (cur = this.queue.poll()) != null) {
            long curKey = cur.asLong();
            this.inQueue.remove(curKey);
            BlockState st = level.getBlockState(cur);
            if (!this.visited.add(curKey) || !PurifyMappings.isSrp(st)) {
                continue;
            }
            BlockState vanilla = PurifyMappings.mapToVanillaState(st);
            if (vanilla != null) {
                BlockState finalState = BlockInfestationPurifier.tryCopyCommonProps(st, vanilla);
                if (level.setBlock(cur, finalState, 3)) {
                    ++this.total;
                    BlockPos up = cur.above();
                    if (level.isEmptyBlock(up) && cachedInfestRemain != null) {
                        level.setBlock(up, cachedInfestRemain.defaultBlockState(), 3);
                    }
                    if (level instanceof ServerLevel server && level.isDay() && level.canSeeSky(cur.above())) {
                        double x = cur.getX() + 0.5;
                        double y = cur.getY() + 1.0;
                        double z = cur.getZ() + 0.5;
                        PacketDistributor.sendToPlayersNear(server, null, x, y, z, 64.0, new PureParticlesPayload(x, y, z, 8 + level.random.nextInt(4), 0));
                    }
                }
            }
            for (BlockPos p : BlockPos.betweenClosed(cur.offset(-1, -1, -1), cur.offset(1, 1, 1))) {
                if (p.equals(cur) || !PurifyMappings.isSrp(level.getBlockState(p))) {
                    continue;
                }
                this.offer(p.immutable());
            }
            for (BlockPos p : BlockPos.betweenClosed(cur.offset(-SPREAD_RADIUS, -SPREAD_RADIUS, -SPREAD_RADIUS), cur.offset(SPREAD_RADIUS, SPREAD_RADIUS, SPREAD_RADIUS))) {
                if (p.equals(cur) || !PurifyMappings.isSrp(level.getBlockState(p))) {
                    continue;
                }
                this.offer(p.immutable());
            }
            ++processed;
        }
        if (this.queue.isEmpty() || this.total >= MAX_TOTAL) {
            this.running = false;
            this.notifyFinished();
        }
    }

    private void notifyFinished() {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        if (this.starter != null) {
            Player p = this.level.getPlayerByUUID(this.starter);
            if (p != null) {
                p.displayClientMessage(Component.translatable("message.srparasites.purifier_purified", this.total), true);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putBoolean("running", this.running);
        nbt.putInt("total", this.total);
        if (this.starter != null) {
            nbt.putUUID("starter", this.starter);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.running = nbt.getBoolean("running");
        this.total = nbt.getInt("total");
        this.starter = nbt.hasUUID("starter") ? nbt.getUUID("starter") : null;
    }
}
