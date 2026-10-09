package com.dhanantry.scapeandrunparasites.world.biome;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.block.SRPBlockLinks;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenAbstractTree;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBush;
import java.util.ArrayDeque;
import java.util.Queue;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * The two parasite biomes (Shrouded, Harlequin). In 1.12 they were added to the cool biome list with a weight (config
 * {@code biomeOneWeight} / {@code biomeThreeWeight}, 0 = never) and decorated by their own BiomeDecorator.
 * 1.21 has no API to add a biome to the overworld source, so a newly generated overworld chunk may be turned into one of them:
 * regions of 6x6 chunks are picked by a hash of the seed and the region, the chance grows with the weights, and the biome of the
 * chunk is replaced. The decoration (parasite trees and bushes, 1 tree and 15 grass spots per chunk like the original biomes) is
 * queued and done once the 3x3 chunks around are loaded.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class ParasiteBiomeGeneration {
    private static final int REGION = 6;
    private static final Queue<Pending> QUEUE = new ArrayDeque<>();

    private record Pending(ServerLevel level, ChunkPos pos, ResourceKey<Biome> biome) {}

    private ParasiteBiomeGeneration() {}

    private static ResourceKey<Biome> pick(ServerLevel level, ChunkPos pos) {
        int w1 = Math.max(0, SRPConfigWorld.biomeOneWeight);
        int w3 = Math.max(0, SRPConfigWorld.biomeThreeWeight);
        int total = w1 + w3;
        if (!SRPConfigWorld.biomeRegster || total <= 0) {
            return null;
        }
        int rx = Math.floorDiv(pos.x, REGION);
        int rz = Math.floorDiv(pos.z, REGION);
        RandomSource r = RandomSource.create(level.getSeed() ^ ((long)rx * 341873128712L + (long)rz * 132897987541L));
        if (r.nextInt(total + 30) >= total) {
            return null;
        }
        return r.nextInt(total) < w1 ? SRPBlockLinks.BIOME_SHROUDED : SRPBlockLinks.BIOME_HARLEQUIN;
    }

    /** Replaces the biome of every cell of the chunk. */
    public static boolean applyBiome(ServerLevel level, ChunkAccess chunk, ResourceKey<Biome> key) {
        Holder<Biome> holder = level.registryAccess().registryOrThrow(Registries.BIOME).getHolder(key).orElse(null);
        if (holder == null) {
            return false;
        }
        for (LevelChunkSection section : chunk.getSections()) {
            @SuppressWarnings("unchecked")
            PalettedContainer<Holder<Biome>> biomes = (PalettedContainer<Holder<Biome>>) section.getBiomes();
            for (int x = 0; x < 4; ++x) {
                for (int y = 0; y < 4; ++y) {
                    for (int z = 0; z < 4; ++z) {
                        biomes.getAndSetUnchecked(x, y, z, holder);
                    }
                }
            }
        }
        chunk.setUnsaved(true);
        return true;
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!event.isNewChunk() || !(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) {
            return;
        }
        ChunkAccess chunk = event.getChunk();
        ResourceKey<Biome> key = pick(level, chunk.getPos());
        if (key == null) {
            return;
        }
        if (!applyBiome(level, chunk, key)) {
            return;
        }
        synchronized (QUEUE) {
            QUEUE.add(new Pending(level, chunk.getPos(), key));
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) {
            return;
        }
        Pending job = null;
        synchronized (QUEUE) {
            if (QUEUE.isEmpty()) {
                return;
            }
            Pending p = QUEUE.peek();
            if (p.level() != level) {
                return;
            }
            ChunkPos c = p.pos();
            if (level.hasChunksAt(c.getMinBlockX() - 16, level.getMinBuildHeight(), c.getMinBlockZ() - 16, c.getMaxBlockX() + 16, level.getMinBuildHeight() + 1, c.getMaxBlockZ() + 16)) {
                job = QUEUE.poll();
            } else if (QUEUE.size() > 256) {
                QUEUE.poll();
            }
        }
        if (job != null) {
            decorate(level, job.pos(), job.biome());
        }
    }

    private static boolean isTerrain(BlockState s) {
        if (s.isAir() || !s.getFluidState().isEmpty() || !s.blocksMotion()) {
            return false;
        }
        return s.is(BlockTags.DIRT) || s.is(BlockTags.BASE_STONE_OVERWORLD) || s.is(BlockTags.SAND) || s.is(Blocks.GRAVEL)
            || s.is(Blocks.SANDSTONE) || s.is(Blocks.RED_SANDSTONE) || s.is(Blocks.CLAY) || s.is(Blocks.TERRACOTTA) || s.is(Blocks.SNOW_BLOCK);
    }

    /**
     * genTerrainBlocks of the parasite biomes: the top block of the surface becomes the dirt of the biome, the filler below it
     * the stone of the biome, and a surface lying exactly at y 62 (just under sea level) is turned to gravel.
     */
    private static void genTerrainBlocks(ServerLevel level, ChunkPos chunk, BiomeParasiteBase biome) {
        BlockState top = BlockIds.parse(biome.getDirt());
        BlockState filler = BlockIds.parse(biome.getStone());
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int wx = chunk.getMinBlockX() + x;
                int wz = chunk.getMinBlockZ() + z;
                int y = level.getHeight(Heightmap.Types.OCEAN_FLOOR, wx, wz) - 1;
                pos.set(wx, y, wz);
                int steps = 0;
                while (!isTerrain(level.getBlockState(pos)) && steps++ < 40 && y > level.getMinBuildHeight()) {
                    pos.set(wx, --y, wz);
                }
                if (!isTerrain(level.getBlockState(pos))) {
                    continue;
                }
                if (y == 62) {
                    level.setBlock(pos, Blocks.GRAVEL.defaultBlockState(), 2);
                    continue;
                }
                level.setBlock(pos, top, 2);
                for (int d = 1; d <= 3; ++d) {
                    pos.set(wx, y - d, wz);
                    if (!isTerrain(level.getBlockState(pos))) {
                        break;
                    }
                    level.setBlock(pos, filler, 2);
                }
            }
        }
    }

    private static void decorate(ServerLevel level, ChunkPos chunk, ResourceKey<Biome> key) {
        BiomeParasiteBase biome = BiomeParasiteBase.get(key);
        RandomSource rand = RandomSource.create(level.getSeed() + chunk.x * 341873128712L + chunk.z * 132897987541L);
        BlockPos origin = chunk.getWorldPosition();
        genTerrainBlocks(level, chunk, biome);
        WorldGenAbstractTree tree = biome.genBigTreeChance(rand);
        BlockPos top = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, origin.offset(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8));
        tree.generate(level, rand, top);
        for (int i = 0; i < 15; ++i) {
            BlockPos pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, origin.offset(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8));
            if (rand.nextInt(10) == 0) {
                biome.getRandomWorldGenForGrass(rand).generate(level, rand, pos);
            } else {
                new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.TENDRIL, 4).generate(level, rand, pos);
            }
        }
    }
}
