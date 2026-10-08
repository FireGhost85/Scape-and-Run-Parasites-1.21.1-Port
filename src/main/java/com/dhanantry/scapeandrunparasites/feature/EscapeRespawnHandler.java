package com.dhanantry.scapeandrunparasites.feature;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.feature.EscapeOnDeathHandler;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;


public class EscapeRespawnHandler {
    private static final String PERSIST_TAG = "PlayerPersisted";
    private static final String PENDING_TAG = "srp_escape_pending";

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (e.player.level().isClientSide) {
            return;
        }
        if (!SRPConfigWorld.escapeEnabled) {
            return;
        }
        ServerPlayer p = (ServerPlayer)e.player;
        boolean pending = p.getPersistentData().getCompound(PERSIST_TAG).getBoolean(PENDING_TAG);
        if (!pending) {
            return;
        }
        p.getPersistentData().getCompound(PERSIST_TAG).putBoolean(PENDING_TAG, false);
        EscapeOnDeathHandler.clearOffer(p);
        BlockPos origin = p.blockPosition();
        int min = Math.max(0, SRPConfigWorld.escapeMinDistance);
        int max = Math.max(min, SRPConfigWorld.escapeMaxDistance);
        BlockPos target = EscapeRespawnHandler.findSafeRandom((ServerLevel)p.level(), origin, min, max, 24);
        if (target != null) {
            p.playerNetServerHandler.setPlayerLocation((double)target.getX() + 0.5, (double)target.getY(), (double)target.getZ() + 0.5, p.getYRot(), p.getXRot());
        }
    }

    private static BlockPos findSafeRandom(ServerLevel world, BlockPos origin, int min, int max, int tries) {
        Random r = world.random;
        for (int i = 0; i < tries; ++i) {
            int dz;
            double ang = r.nextDouble() * Math.PI * 2.0;
            int dist = min + r.nextInt(Math.max(1, max - min + 1));
            int dx = origin.getX() + (int)Math.round(Math.cos(ang) * (double)dist);
            BlockPos top = world.getTopSolidOrLiquidBlock(BlockPos.containing(dx, 0, dz = origin.getZ() + (int)Math.round(Math.sin(ang) * (double)dist)));
            BlockPos solid = EscapeRespawnHandler.descendToSolid(world, top);
            if (solid == null || !EscapeRespawnHandler.isSafe(world, solid)) continue;
            return solid.above();
        }
        return null;
    }

    private static BlockPos descendToSolid(ServerLevel w, BlockPos start) {
        BlockPos pos = start;
        for (int i = 0; i < 16; ++i) {
            Material m = w.getBlockState(pos).getMaterialPlaceholder();
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
        Material m = w.getBlockState(solid).getMaterialPlaceholder();
        if (!m.isSolid() || m == Material.leaves) {
            return false;
        }
        BlockPos feet = solid.above();
        BlockPos head = feet.above();
        if (!w.isEmptyBlock(feet) || !w.isEmptyBlock(head)) {
            return false;
        }
        if (w.getBlockState(feet).getMaterialPlaceholder().isLiquid()) {
            return false;
        }
        return !w.getBlockState(head).getMaterialPlaceholder().isLiquid();
    }
}

