package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.client.BarrierClient;
import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

/**
 * Block entity of the parasite barrier: a square field of chunks (radius 1 to 10 around the barrier's chunk) that mod
 * entities cannot enter. They lose targets inside it, drop paths through it and are pushed out of it.
 */
public class TileEntityParasiteBarrier extends BlockEntity {
    private int centerChunkX;
    private int centerChunkZ;
    private int radiusChunks = 1;
    private final Map<Integer, Integer> insideTicks = new HashMap<>();

    public TileEntityParasiteBarrier(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.PARASITE_BARRIER.get(), pos, state);
    }

    public void initCenterFromPos(BlockPos pos) {
        this.centerChunkX = pos.getX() >> 4;
        this.centerChunkZ = pos.getZ() >> 4;
        this.notifySync();
    }

    public int getRadiusChunks() {
        return this.radiusChunks;
    }

    public void setRadiusChunks(int r) {
        r = Math.max(1, Math.min(10, r));
        if (r != this.radiusChunks) {
            this.radiusChunks = r;
            this.notifySync();
            this.scrubMobsAroundField();
        }
    }

    /** {minX, maxX, minZ, maxZ} of the field in blocks. */
    public int[] currentWorldBounds() {
        int r = this.radiusChunks;
        int minChunkX = this.centerChunkX - r;
        int maxChunkX = this.centerChunkX + r;
        int minChunkZ = this.centerChunkZ - r;
        int maxChunkZ = this.centerChunkZ + r;
        return new int[]{minChunkX * 16, (maxChunkX + 1) * 16 - 1, minChunkZ * 16, (maxChunkZ + 1) * 16 - 1};
    }

    private boolean isInside(int[] bb, Entity e) {
        double x = e.getX();
        double z = e.getZ();
        return x >= bb[0] && x <= bb[1] && z >= bb[2] && z <= bb[3];
    }

    private boolean isSRP(Entity e) {
        String n = e.getClass().getName();
        return n.startsWith("com.dhanantry.scapeandrunparasites") || n.contains(".srparasites.");
    }

    private AABB searchBox(int[] bb) {
        return new AABB(bb[0] - 48, this.level.getMinBuildHeight(), bb[2] - 48, bb[1] + 48, this.level.getMaxBuildHeight(), bb[3] + 48);
    }

    /** Server and client tick; the client only draws the border particles. */
    public void update() {
        if (this.level == null) {
            return;
        }
        if (this.level.isClientSide) {
            BarrierClient.spawnBorderParticles(this.level, this.currentWorldBounds());
            return;
        }
        if (this.level.getGameTime() % 2L != 0L) {
            return;
        }
        int[] bb = this.currentWorldBounds();
        AABB search = this.searchBox(bb);
        for (Mob attacker : this.level.getEntitiesOfClass(Mob.class, search)) {
            this.clearCrossFactionAggroIfTargetInside(attacker, bb);
        }
        List<Entity> list = this.level.getEntitiesOfClass(Entity.class, search, this::isSRP);
        if (list.isEmpty()) {
            this.insideTicks.clear();
            return;
        }
        for (Entity e : list) {
            this.clearAggroIfTargetInside(e, bb);
            this.clearPathIfTouchesInside(e, bb);
            if (this.isInside(bb, e)) {
                this.pushOrTeleportOutside(bb, e);
                if (this.isInside(bb, e)) {
                    int t = this.insideTicks.getOrDefault(e.getId(), 0) + 2;
                    if (t >= 100) {
                        this.pushOrTeleportOutside(bb, e);
                        this.pushOrTeleportOutside(bb, e);
                        t = 0;
                    }
                    this.insideTicks.put(e.getId(), t);
                    continue;
                }
                this.insideTicks.remove(e.getId());
                continue;
            }
            this.insideTicks.remove(e.getId());
        }
    }

    public void scrubMobsAroundField() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        int[] bb = this.currentWorldBounds();
        for (Entity e : this.level.getEntitiesOfClass(Entity.class, this.searchBox(bb), this::isSRP)) {
            this.clearAggroIfTargetInside(e, bb);
            this.clearPathIfTouchesInside(e, bb);
        }
    }

    private void clearCrossFactionAggroIfTargetInside(Mob attacker, int[] bb) {
        LivingEntity tgt = attacker.getTarget();
        if (tgt == null) {
            tgt = attacker.getLastHurtByMob();
        }
        if (tgt == null) {
            return;
        }
        boolean targetInside = tgt.getX() >= bb[0] && tgt.getX() <= bb[1] && tgt.getZ() >= bb[2] && tgt.getZ() <= bb[3];
        if (!targetInside) {
            return;
        }
        if (this.isSRP(attacker) || this.isSRP(tgt)) {
            attacker.setTarget(null);
            attacker.setLastHurtByMob(null);
            attacker.getNavigation().stop();
        }
    }

    private void clearAggroIfTargetInside(Entity e, int[] bb) {
        if (!(e instanceof Mob el)) {
            return;
        }
        LivingEntity tgt = el.getTarget();
        if (tgt != null && this.isInside(bb, tgt)) {
            el.setTarget(null);
            el.setLastHurtByMob(null);
            el.getNavigation().stop();
            return;
        }
        LivingEntity rev = el.getLastHurtByMob();
        if (rev != null && this.isInside(bb, rev)) {
            el.setLastHurtByMob(null);
            el.getNavigation().stop();
        }
    }

    private void clearPathIfTouchesInside(Entity e, int[] bb) {
        if (!(e instanceof Mob el)) {
            return;
        }
        Path path = el.getNavigation().getPath();
        if (path == null) {
            return;
        }
        int n = path.getNodeCount();
        for (int i = 0; i < n; ++i) {
            Node p = path.getNode(i);
            if (p == null || p.x < bb[0] || p.x > bb[1] || p.z < bb[2] || p.z > bb[3]) {
                continue;
            }
            el.getNavigation().stop();
            if (el.getTarget() != null && this.isInside(bb, el.getTarget())) {
                el.setTarget(null);
                el.setLastHurtByMob(null);
            }
            return;
        }
    }

    private void pushOrTeleportOutside(int[] bb, Entity e) {
        double x = e.getX();
        double z = e.getZ();
        double dxMin = Math.abs(x - bb[0]);
        double dxMax = Math.abs(bb[1] - x);
        double dzMin = Math.abs(z - bb[2]);
        double dzMax = Math.abs(bb[3] - z);
        int edge = 0;
        double best = dxMin;
        if (dxMax < best) {
            best = dxMax;
            edge = 1;
        }
        if (dzMin < best) {
            best = dzMin;
            edge = 2;
        }
        if (dzMax < best) {
            best = dzMax;
            edge = 3;
        }
        double out = 0.6;
        double tx = x;
        double tz = z;
        if (edge == 0) {
            tx = bb[0] - out;
        } else if (edge == 1) {
            tx = bb[1] + out;
        } else {
            tz = edge == 2 ? bb[2] - out : bb[3] + out;
        }
        BlockPos top = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(Mth.floor(tx), 0, Mth.floor(tz)));
        double ty = top.getY() + 0.1;
        e.setPos(tx, ty, tz);
        e.setDeltaMovement(e.getDeltaMovement().x * 0.1, Math.min(e.getDeltaMovement().y, 0.0), e.getDeltaMovement().z * 0.1);
        e.fallDistance = 0.0f;
    }

    private void notifySync() {
        if (this.level == null) {
            return;
        }
        BlockState st = this.level.getBlockState(this.worldPosition);
        this.level.sendBlockUpdated(this.worldPosition, st, st, 3);
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putInt("centerChunkX", this.centerChunkX);
        nbt.putInt("centerChunkZ", this.centerChunkZ);
        nbt.putInt("radiusChunks", this.radiusChunks);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.centerChunkX = nbt.getInt("centerChunkX");
        this.centerChunkZ = nbt.getInt("centerChunkZ");
        int r = nbt.getInt("radiusChunks");
        this.radiusChunks = Math.max(1, Math.min(10, r == 0 ? 1 : r));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
