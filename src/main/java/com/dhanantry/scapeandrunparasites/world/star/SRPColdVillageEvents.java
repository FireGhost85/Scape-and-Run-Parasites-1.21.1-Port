package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * The cold (deadhead) villages of the cold star world. 1.12 called the generator when the chunk of a village of the grid was
 * populated; here the chunk that was just generated is checked and the village is built from the server tick (the chunks around are
 * generated on demand while it is built, as the 1.12 population did).
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPColdVillageEvents {
    private static final Queue<ChunkPos> PENDING = new ArrayDeque<>();

    private SRPColdVillageEvents() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!event.isNewChunk() || !(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD || SRPWorldEntitySpawner.starType != 1) {
            return;
        }
        ChunkPos pos = event.getChunk().getPos();
        if (SRPColdVillageGenerator.isVillageChunk(level, pos.x, pos.z)) {
            synchronized (PENDING) {
                PENDING.add(pos);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) {
            return;
        }
        ChunkPos pos;
        synchronized (PENDING) {
            pos = PENDING.poll();
        }
        if (pos == null) {
            return;
        }
        try {
            SRPColdVillageGenerator.generateVillage(level, pos.x, pos.z);
        } catch (Throwable t) {
            ScapeAndRunParasites.LOGGER.error("Could not generate the cold village of chunk {}", pos, t);
        }
    }
}
