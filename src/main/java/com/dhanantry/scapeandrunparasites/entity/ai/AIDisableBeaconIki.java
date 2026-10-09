package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityIki;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class AIDisableBeaconIki
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private static final double SCAN_RADIUS_D = 64.0;
    private static final int PICKUP_RADIUS = 6;
    private static final int RESCAN_TICKS = 100;
    private static final double REACH_SQ = 4.0;
    private static final int STALL_TICKS = 40;
    private static final int PHASE_BUDGET = 200;
    private final EntityIki host;
    private BlockState carried = null;
    private BeaconBlockEntity targetBeacon = null;
    private BlockPos beaconPos = null;
    private BlockPos placePos = null;
    private int timeout = 0;
    private int cooldown = 0;
    private Phase phase = Phase.SEARCH;
    private BlockPos pickupPos = null;
    private int progressTicks = 0;
    private double lastDistSq = Double.MAX_VALUE;

    public AIDisableBeaconIki(EntityIki host) {
        this.host = host;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        if (this.host.getTarget() != null) {
            return false;
        }
        return !this.host.level().isClientSide && EventHooks.canEntityGrief((Level)this.host.level(), (Entity)this.host) && this.host.isAlive();
    }

    public boolean canContinueToUse() {
        if (this.host.getTarget() != null) {
            return false;
        }
        return this.canUse();
    }

    public void stop() {
        this.targetBeacon = null;
        this.beaconPos = null;
        this.placePos = null;
        this.pickupPos = null;
        this.carried = null;
        this.timeout = 0;
        this.progressTicks = 0;
        this.lastDistSq = Double.MAX_VALUE;
        this.phase = Phase.SEARCH;
    }

    public void tick() {
        if (this.host.level().isClientSide) {
            return;
        }
        if (this.cooldown > 0) {
            --this.cooldown;
        }
        switch (this.phase) {
            case SEARCH: {
                if (this.cooldown > 0) break;
                this.targetBeacon = this.findNearestEmittingBeacon();
                if (this.targetBeacon != null) {
                    this.beaconPos = this.targetBeacon.getBlockPos();
                    this.placePos = null;
                    this.pickupPos = null;
                    this.phase = this.carried == null ? Phase.PICK_TARGET_BLOCK : Phase.MOVE_TO_BEAM;
                    this.timeout = 200;
                    break;
                }
                this.cooldown = 100;
                break;
            }
            case PICK_TARGET_BLOCK: {
                this.pickupPos = this.findNearbyPickupBlock();
                this.progressTicks = 0;
                this.lastDistSq = Double.MAX_VALUE;
                if (this.pickupPos != null) {
                    this.phase = Phase.MOVE_TO_BLOCK;
                    this.timeout = 200;
                    break;
                }
                this.cooldown = 100;
                this.phase = Phase.COOLDOWN;
                break;
            }
            case MOVE_TO_BLOCK: {
                if (this.pickupPos == null) {
                    this.phase = Phase.PICK_TARGET_BLOCK;
                    break;
                }
                if (!this.isPickupStillValid(this.pickupPos)) {
                    this.phase = Phase.PICK_TARGET_BLOCK;
                    break;
                }
                this.flyTo(this.pickupPos, 1.1);
                double d2 = this.host.distanceToSqr((double)this.pickupPos.getX() + 0.5, (double)this.pickupPos.getY() + 0.5, (double)this.pickupPos.getZ() + 0.5);
                if (d2 + 0.01 < this.lastDistSq) {
                    this.lastDistSq = d2;
                    this.progressTicks = 0;
                } else {
                    ++this.progressTicks;
                }
                if (d2 <= 4.0) {
                    this.phase = Phase.PICKUP;
                    break;
                }
                if (--this.timeout > 0 && this.progressTicks < 40) break;
                this.phase = Phase.PICK_TARGET_BLOCK;
                break;
            }
            case PICKUP: {
                if (this.pickupPos != null && this.isPickupStillValid(this.pickupPos)) {
                    this.carried = this.host.level().getBlockState(this.pickupPos);
                    this.host.level().removeBlock(this.pickupPos, false);
                }
                this.pickupPos = null;
                this.progressTicks = 0;
                this.lastDistSq = Double.MAX_VALUE;
                this.phase = Phase.MOVE_TO_BEAM;
                this.timeout = 200;
                break;
            }
            case MOVE_TO_BEAM: {
                BlockPos alt;
                if (!this.isBeaconEmitting(this.beaconPos)) {
                    this.phase = Phase.SEARCH;
                    break;
                }
                if (this.placePos == null) {
                    this.placePos = this.pickBeamPlacement(this.beaconPos);
                    if (this.placePos == null) {
                        this.cooldown = 100;
                        this.phase = Phase.COOLDOWN;
                        break;
                    }
                    this.progressTicks = 0;
                    this.lastDistSq = Double.MAX_VALUE;
                }
                this.flyTo(this.placePos, 1.1);
                double d2 = this.host.distanceToSqr((double)this.placePos.getX() + 0.5, (double)this.placePos.getY() + 0.5, (double)this.placePos.getZ() + 0.5);
                if (d2 + 0.01 < this.lastDistSq) {
                    this.lastDistSq = d2;
                    this.progressTicks = 0;
                } else {
                    ++this.progressTicks;
                }
                if (this.progressTicks == 20 && (alt = this.pickBeamPlacement(this.beaconPos)) != null && !alt.equals(this.placePos)) {
                    this.placePos = alt;
                    this.progressTicks = 0;
                    this.lastDistSq = Double.MAX_VALUE;
                }
                if (d2 <= 4.0) {
                    this.phase = Phase.PLACE;
                    break;
                }
                if (--this.timeout > 0 && this.progressTicks < 40) break;
                this.cooldown = 100;
                this.phase = Phase.COOLDOWN;
                break;
            }
            case PLACE: {
                if (this.carried != null && this.placePos != null && this.canPlaceAt(this.placePos)) {
                    this.host.level().setBlock(this.placePos, this.carried, 3);
                }
                this.carried = null;
                this.cooldown = 100;
                this.phase = Phase.COOLDOWN;
                break;
            }
            case COOLDOWN: {
                if (this.cooldown > 0) break;
                this.phase = Phase.SEARCH;
            }
        }
    }

    private boolean canPlaceAt(BlockPos pos) {
        if (!this.host.level().hasChunkAt(pos)) {
            return false;
        }
        BlockState st = this.host.level().getBlockState(pos);
        return st.canBeReplaced() || st.isAir();
    }

    private boolean isBeaconStillActive() {
        if (this.targetBeacon == null) {
            return false;
        }
        BlockEntity te = this.host.level().getBlockEntity(this.targetBeacon.getBlockPos());
        if (!(te instanceof BeaconBlockEntity)) {
            return false;
        }
        return ((BeaconBlockEntity)te).levels > 0;
    }

    private void flyTo(BlockPos pos, double speed) {
        this.host.getMoveControl().setWantedPosition((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, speed);
        this.host.getLookControl().setLookAt((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 30.0f, 30.0f);
    }

    /** 1.12 world.loadedTileEntityList, restricted to the loaded chunks around the host (the scan radius is 64 blocks). */
    private List<BeaconBlockEntity> loadedBeacons() {
        List<BeaconBlockEntity> out = new ArrayList<>();
        Level level = this.host.level();
        int cx = this.host.blockPosition().getX() >> 4;
        int cz = this.host.blockPosition().getZ() >> 4;
        for (int dx = -5; dx <= 5; ++dx) {
            for (int dz = -5; dz <= 5; ++dz) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(cx + dx, cz + dz);
                if (chunk == null) continue;
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof BeaconBlockEntity beacon) {
                        out.add(beacon);
                    }
                }
            }
        }
        return out;
    }

    private BeaconBlockEntity findNearestEmittingBeacon() {
        double bestD2 = 4096.0;
        BeaconBlockEntity best = null;
        for (BeaconBlockEntity te : this.loadedBeacons()) {
            BlockPos p;
            double d2;
            if (!(te instanceof BeaconBlockEntity) || (d2 = this.host.distanceToSqr((double)(p = te.getBlockPos()).getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5)) > bestD2 || !this.isBeaconEmitting(p)) continue;
            best = (BeaconBlockEntity)te;
            bestD2 = d2;
        }
        return best;
    }

    private boolean isPickupStillValid(BlockPos p) {
        if (!this.host.level().hasChunkAt(p)) {
            return false;
        }
        if (!this.host.level().isEmptyBlock(p.above())) {
            return false;
        }
        BlockState st = this.host.level().getBlockState(p);
        if (this.host.level().getBlockEntity(p) != null) {
            return false;
        }
        return this.isPickupWhitelisted(st.getBlock());
    }

    private boolean isBeaconEmitting(BlockPos beacon) {
        if (beacon == null) {
            return false;
        }
        BlockEntity te = this.host.level().getBlockEntity(beacon);
        if (!(te instanceof BeaconBlockEntity)) {
            return false;
        }
        if (((BeaconBlockEntity)te).levels <= 0) {
            return false;
        }
        int top = this.host.level().getHeight();
        for (int y = beacon.getY() + 1; y < top; ++y) {
            BlockPos q = BlockPos.containing(beacon.getX(), y, beacon.getZ());
            Block b = this.host.level().getBlockState(q).getBlock();
            if (b == Blocks.AIR || b == Blocks.GLASS || b == Blocks.WHITE_STAINED_GLASS || b == Blocks.WHITE_STAINED_GLASS_PANE) continue;
            return false;
        }
        return true;
    }

    private BlockPos pickBeamPlacement(BlockPos beacon) {
        BlockPos p = this.pickBeamPlacementStrict(beacon);
        if (p == null) {
            p = this.pickBeamPlacementLoose(beacon);
        }
        return p;
    }

    private BlockPos pickBeamPlacementStrict(BlockPos beacon) {
        BlockPos c;
        int y;
        int bx = beacon.getX();
        int by = beacon.getY();
        int bz = beacon.getZ();
        int top = this.host.level().getHeight();
        for (y = Math.min(top - 1, by + 64); y >= by + 1; --y) {
            c = BlockPos.containing(bx, y, bz);
            if (!this.canPlaceAt(c) || !this.hasAttachSupport(c) || !this.hasLineOfSightTo(c)) continue;
            return c;
        }
        for (y = by + 1; y < Math.min(top, by + 64); ++y) {
            c = BlockPos.containing(bx, y, bz);
            if (!this.canPlaceAt(c) || !this.hasAttachSupport(c) || !this.hasLineOfSightTo(c)) continue;
            return c;
        }
        return null;
    }

    private boolean hasAttachSupport(BlockPos pos) {
        for (Direction f : Direction.values()) {
            BlockState ns;
            BlockPos n = pos.relative(f);
            if (!this.host.level().hasChunkAt(n) || (ns = this.host.level().getBlockState(n)).getBlock() == Blocks.AIR || !ns.isFaceSturdy((BlockGetter)this.host.level(), n, f.getOpposite())) continue;
            return true;
        }
        return false;
    }

    private BlockPos pickBeamPlacementLoose(BlockPos beacon) {
        BlockPos c;
        int y;
        int bx = beacon.getX();
        int by = beacon.getY();
        int bz = beacon.getZ();
        int top = this.host.level().getHeight();
        for (y = Math.min(top - 1, by + 64); y >= by + 1; --y) {
            c = BlockPos.containing(bx, y, bz);
            if (!this.canPlaceAt(c) || !this.hasAttachSupport(c)) continue;
            return c;
        }
        for (y = by + 1; y < Math.min(top, by + 64); ++y) {
            c = BlockPos.containing(bx, y, bz);
            if (!this.canPlaceAt(c) || !this.hasAttachSupport(c)) continue;
            return c;
        }
        return null;
    }

    private boolean hasLineOfSightTo(BlockPos pos) {
        Vec3 end;
        double ez;
        double ex = this.host.getX();
        double ey = this.host.getY() + (double)this.host.getEyeHeight();
        Vec3 start = new Vec3(ex, ey, ez = this.host.getZ());
        HitResult hit = SRPEntityUtil.rayTraceBlocks(this.host.level(), start, end = new Vec3((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5));
        return hit == null || hit.getType() == HitResult.Type.MISS;
    }

    private BlockPos findNearbyPickupBlock() {
        BlockPos origin = this.host.blockPosition();
        int r = 6;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -r; dx <= r; ++dx) {
                for (int dz = -r; dz <= r; ++dz) {
                    BlockState st;
                    Block b;
                    BlockPos p = origin.offset(dx, dy, dz);
                    if (!this.host.level().hasChunkAt(p) || !this.isPickupWhitelisted(b = (st = this.host.level().getBlockState(p)).getBlock()) || this.host.level().getBlockEntity(p) != null || !this.host.level().isEmptyBlock(p.above()) || b == Blocks.BEACON) continue;
                    return p;
                }
            }
        }
        return null;
    }

    private boolean isPickupWhitelisted(Block b) {
        return b == Blocks.DIRT || b == Blocks.GRASS_BLOCK || b == Blocks.SAND || b == Blocks.GRAVEL || b == Blocks.COBBLESTONE || b == Blocks.NETHERRACK;
    }

    private BeaconBlockEntity findNearestActiveBeacon() {
        double bestD2 = 4096.0;
        BeaconBlockEntity best = null;
        for (BeaconBlockEntity te : this.loadedBeacons()) {
            BeaconBlockEntity b;
            BlockPos p;
            double d2;
            if (!(te instanceof BeaconBlockEntity) || (d2 = this.host.distanceToSqr((double)(p = te.getBlockPos()).getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5)) > bestD2 || (b = (BeaconBlockEntity)te).levels <= 0) continue;
            best = b;
            bestD2 = d2;
        }
        return best;
    }

    private static enum Phase {
        SEARCH,
        PICK_TARGET_BLOCK,
        MOVE_TO_BLOCK,
        PICKUP,
        MOVE_TO_BEAM,
        PLACE,
        COOLDOWN;

    }
}

