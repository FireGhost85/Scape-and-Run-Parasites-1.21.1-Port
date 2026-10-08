package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.util.SRPDebugRules;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Connection point of the block classes to the biome system: the parasite biomes are identified by their registry key
 * (they were the two registered {@code BiomeParasiteBase} subclasses), the biome of a column is rewritten in the chunk and
 * the changed chunks are re-sent to the clients at the end of the level tick (replaces {@code BiomeUpdateQueue}).
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPBlockLinks {
    public static final ResourceKey<Biome> BIOME_SHROUDED = biomeKey("biomeparasite_shrouded");
    public static final ResourceKey<Biome> BIOME_HARLEQUIN = biomeKey("biomeparasite_harlequin");

    private static final Map<ServerLevel, Map<ChunkAccess, Boolean>> DIRTY = new IdentityHashMap<>();

    private SRPBlockLinks() {
    }

    private static ResourceKey<Biome> biomeKey(String path) {
        return ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, path));
    }

    /** True when the biome at the position is one of the parasite biomes ({@code instanceof BiomeParasiteBase}). */
    public static boolean isParasiteBiome(LevelReader level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        return biome.is(BIOME_SHROUDED) || biome.is(BIOME_HARLEQUIN);
    }

    /** The parasite biome at the position, or null when the biome there is not a parasite biome. */
    @Nullable
    public static BiomeParasiteBase parasiteBiomeAt(LevelReader level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        if (biome.is(BIOME_SHROUDED)) {
            return BiomeParasiteBase.get(BIOME_SHROUDED);
        }
        if (biome.is(BIOME_HARLEQUIN)) {
            return BiomeParasiteBase.get(BIOME_HARLEQUIN);
        }
        return null;
    }

    /** The registry name of the biome at the position ({@code getBiome(pos).getRegistryName().toString()}). */
    public static String biomeName(LevelReader level, BlockPos pos) {
        return level.getBiome(pos).unwrapKey().map(k -> k.location().toString()).orElse("");
    }

    /** {@code BlockParasiteSpreading.positionToParasiteBiome}: SRPReference.getBiomeFromInt(type) is harlequin for 3, shrouded otherwise. */
    public static void setParasiteBiome(Level level, BlockPos pos, int type) {
        ResourceKey<Biome> target;
        if (level.getGameRules().getBoolean(SRPDebugRules.FORCE_HARLEQUIN)) {
            target = BIOME_HARLEQUIN;
        } else {
            target = type == 3 ? BIOME_HARLEQUIN : BIOME_SHROUDED;
        }
        if (level.getBiome(pos).is(target)) {
            return;
        }
        setColumnBiome(level, pos, level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(target));
    }

    /** Resets the column at {@code target} to the biome the world generator puts at {@code natural} (BlockBiomePurifier.killBiome). */
    public static void restoreNaturalBiome(Level level, BlockPos target, BlockPos natural) {
        if (!(level instanceof ServerLevel server)) {
            return;
        }
        ServerChunkCache cache = server.getChunkSource();
        Holder<Biome> original = cache.getGenerator().getBiomeSource().getNoiseBiome(QuartPos.fromBlock(natural.getX()), QuartPos.fromBlock(natural.getY()),
                QuartPos.fromBlock(natural.getZ()), cache.randomState().sampler());
        setColumnBiome(level, target, original);
    }

    /** Writes the biome into the 4x4 cell of every chunk section of the column and queues the chunk for the clients. */
    @SuppressWarnings("unchecked")
    public static void setColumnBiome(Level level, BlockPos pos, Holder<Biome> biome) {
        if (!(level instanceof ServerLevel server)) {
            return;
        }
        ChunkAccess chunk = level.getChunk(pos);
        int qx = (pos.getX() & 15) >> 2;
        int qz = (pos.getZ() & 15) >> 2;
        for (LevelChunkSection section : chunk.getSections()) {
            PalettedContainer<Holder<Biome>> container = (PalettedContainer<Holder<Biome>>) section.getBiomes();
            for (int qy = 0; qy < 4; ++qy) {
                container.getAndSet(qx, qy, qz, biome);
            }
        }
        chunk.setUnsaved(true);
        DIRTY.computeIfAbsent(server, l -> new IdentityHashMap<>()).put(chunk, Boolean.TRUE);
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel server)) {
            return;
        }
        Map<ChunkAccess, Boolean> dirty = DIRTY.remove(server);
        if (dirty == null || dirty.isEmpty()) {
            return;
        }
        List<ChunkAccess> chunks = new ArrayList<>(dirty.keySet());
        server.getChunkSource().chunkMap.resendBiomesForChunks(chunks);
    }
}
