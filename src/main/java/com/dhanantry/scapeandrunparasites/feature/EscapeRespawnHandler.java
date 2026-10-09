package com.dhanantry.scapeandrunparasites.feature;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/** Moves a respawning player that asked for the escape to a safe spot {@code escapeMinDistance} to {@code escapeMaxDistance} blocks away. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class EscapeRespawnHandler {
    private static final String PERSIST_TAG = EscapeOnDeathHandler.PERSIST_TAG;
    private static final String PENDING_TAG = EscapeOnDeathHandler.PENDING_TAG;

    /** The 1.12 "PlayerPersisted" sub tag survived death; here the player entity is a new one, so it is copied. */
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone e) {
        if (!e.isWasDeath()) {
            return;
        }
        CompoundTag old = e.getOriginal().getPersistentData();
        if (old.contains(PERSIST_TAG, 10)) {
            e.getEntity().getPersistentData().put(PERSIST_TAG, old.getCompound(PERSIST_TAG).copy());
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (e.getEntity().level().isClientSide || !(e.getEntity() instanceof ServerPlayer p)) {
            return;
        }
        if (!SRPConfigWorld.escapeEnabled) {
            return;
        }
        CompoundTag persisted = p.getPersistentData().getCompound(PERSIST_TAG);
        if (!persisted.getBoolean(PENDING_TAG)) {
            return;
        }
        persisted.putBoolean(PENDING_TAG, false);
        p.getPersistentData().put(PERSIST_TAG, persisted);
        EscapeOnDeathHandler.clearOffer(p);
        BlockPos origin = p.blockPosition();
        int min = Math.max(0, SRPConfigWorld.escapeMinDistance);
        int max = Math.max(min, SRPConfigWorld.escapeMaxDistance);
        BlockPos target = findSafeRandom((ServerLevel)p.level(), origin, min, max, 24);
        if (target != null) {
            p.teleportTo((double)target.getX() + 0.5, (double)target.getY(), (double)target.getZ() + 0.5);
        }
    }

    private static BlockPos findSafeRandom(ServerLevel world, BlockPos origin, int min, int max, int tries) {
        RandomSource r = world.random;
        for (int i = 0; i < tries; ++i) {
            double ang = r.nextDouble() * Math.PI * 2.0;
            int dist = min + r.nextInt(Math.max(1, max - min + 1));
            int dx = origin.getX() + (int)Math.round(Math.cos(ang) * (double)dist);
            int dz = origin.getZ() + (int)Math.round(Math.sin(ang) * (double)dist);
            BlockPos top = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(dx, 0, dz));
            BlockPos solid = descendToSolid(world, top);
            if (solid == null || !isSafe(world, solid)) continue;
            return solid.above();
        }
        return null;
    }

    private static BlockPos descendToSolid(ServerLevel w, BlockPos start) {
        BlockPos pos = start;
        for (int i = 0; i < 16; ++i) {
            LegacyMaterial m = LegacyMaterial.of(w.getBlockState(pos));
            if (m.isSolid()) {
                return pos;
            }
            if ((pos = pos.below()).getY() <= 4) break;
        }
        return null;
    }

    private static boolean isSafe(ServerLevel w, BlockPos solid) {
        if (solid == null) {
            return false;
        }
        LegacyMaterial m = LegacyMaterial.of(w.getBlockState(solid));
        if (!m.isSolid() || m == LegacyMaterial.leaves) {
            return false;
        }
        BlockPos feet = solid.above();
        BlockPos head = feet.above();
        if (!w.isEmptyBlock(feet) || !w.isEmptyBlock(head)) {
            return false;
        }
        if (LegacyMaterial.of(w.getBlockState(feet)).isLiquid()) {
            return false;
        }
        return !LegacyMaterial.of(w.getBlockState(head)).isLiquid();
    }
}
