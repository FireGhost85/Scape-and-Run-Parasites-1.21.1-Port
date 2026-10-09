package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSpawning;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.EventHooks;

/**
 * The parasite spawner of the original (SRPEventHandlerBus.tickSpawn -> findChunksForSpawning): replaces the vanilla spawn
 * cycle for the parasites with the phase / origin driven spawn lists of {@link SRPSpawning}. Called every tick of a level by
 * {@link com.dhanantry.scapeandrunparasites.util.handlers.SRPEventHandlerBus}.
 */
public class SRPWorldParasiteSpawner {
    private static final Set<ChunkPos> eligibleChunksForSpawning = new HashSet<>();
    private static int lock = 0;
    public static boolean triggerSPAWNING = false;
    public static int choiceNUMBER = 1;

    public static int findChunksForSpawning(ServerLevel worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
        if (!SRPConfigWorld.originActivated) {
            return spawnCycle(worldServerIn, spawnHostileMobs, spawnPeacefulMobs, false);
        }
        return spawnCycle(worldServerIn, spawnHostileMobs, spawnPeacefulMobs, true);
    }

    /** {@code origin == false}: findChunksForSpawningVanilla, {@code true}: findChunksForSpawningOrigin of the original. */
    private static int spawnCycle(ServerLevel level, boolean hostile, boolean peaceful, boolean origin) {
        if (!hostile && !peaceful) {
            return 0;
        }
        if (!SRPSpawning.totalParasites) {
            if (++lock > (origin ? 7 : 40)) {
                SRPSpawning.totalParasites = true;
                lock = 0;
            }
            return 0;
        }
        eligibleChunksForSpawning.clear();
        SRPWorldData worldData = null;
        SRPSaveData saveData = SRPSaveData.get(level);
        boolean originsExist = false;
        boolean originlessAllowed = false;
        if (origin) {
            worldData = SRPWorldData.get(level);
            originsExist = worldData != null && !worldData.getorigins("x").isEmpty();
            originlessAllowed = saveData != null && saveData.getDeveLevel() >= SRPConfigSystems.deveOriginlessUse;
        }
        for (Player player : level.players()) {
            if (player.isSpectator()) continue;
            int j = Mth.floor(player.getX() / 16.0);
            int k = Mth.floor(player.getZ() / 16.0);
            for (int i1 = -8; i1 <= 8; ++i1) {
                for (int j1 = -8; j1 <= 8; ++j1) {
                    boolean edge = i1 == -8 || i1 == 8 || j1 == -8 || j1 == 8;
                    ChunkPos chunkpos = new ChunkPos(i1 + j, j1 + k);
                    if (eligibleChunksForSpawning.contains(chunkpos) || edge || !level.getWorldBorder().isWithinBounds(chunkpos)
                            || level.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z) == null) continue;
                    eligibleChunksForSpawning.add(chunkpos);
                }
            }
        }
        if (origin) {
            filterEligibleChunksForOrigin(worldData, originsExist, originlessAllowed);
            if (eligibleChunksForSpawning.isEmpty()) {
                return 0;
            }
        }
        int spawned = 0;
        BlockPos spawnPoint = level.getSharedSpawnPos();
        List<ChunkPos> shuffled = new ArrayList<>(eligibleChunksForSpawning);
        Collections.shuffle(shuffled);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        chunks:
        for (ChunkPos chunkpos1 : shuffled) {
            BlockPos blockpos = getRandomChunkPosition(level, chunkpos1.x, chunkpos1.z);
            int k1 = blockpos.getX();
            int l1 = blockpos.getY();
            int i2 = blockpos.getZ();
            BlockState state = level.getBlockState(blockpos);
            if (state.isRedstoneConductor(level, blockpos)) continue;
            int packCount = 0;
            groups:
            for (int k2 = 0; k2 < 3; ++k2) {
                int l2 = k1;
                int i3 = l1;
                int j3 = i2;
                SRPSpawning.SpawnEntry entry = null;
                SpawnGroupData groupData = null;
                int tries = Mth.ceil(Math.random() * 4.0);
                for (int i4 = 0; i4 < tries; ++i4) {
                    l2 += level.random.nextInt(6) - level.random.nextInt(6);
                    i3 += level.random.nextInt(1) - level.random.nextInt(1);
                    j3 += level.random.nextInt(6) - level.random.nextInt(6);
                    mutable.set(l2, i3, j3);
                    float f = (float) l2 + 0.5f;
                    float f1 = (float) j3 + 0.5f;
                    if (level.hasNearbyAlivePlayer(f, i3, f1, 24.0) || !(spawnPoint.distToCenterSqr(f, i3, f1) >= 576.0)) continue;
                    if (entry == null) {
                        entry = origin
                                ? getSpawnListEntryForTypeAtOrigin(level, mutable, saveData, worldData, originsExist, originlessAllowed)
                                : getSpawnListEntryForTypeAt(level, mutable);
                        if (entry == null) continue groups;
                    }
                    SRPSpawning.Placement ground = SRPSpawning.placementOf(entry.entityType);
                    if (ground == SRPSpawning.Placement.IN_AIR) {
                        if (origin && level.random.nextDouble() <= 0.7) continue;
                        Player closest = getClosestPlayer(f, i3, f1, 24.0, level);
                        if (closest != null) {
                            double base = closest.getY();
                            double randomOff = level.random.nextInt(21) - 10;
                            base = Math.max(base, (double) level.getMinBuildHeight() / 2.0);
                            base = Math.min(base, (double) SRPConfigWorld.spawnerSKYLimitUp);
                            mutable.set(l2, (int) (base + randomOff), j3);
                        }
                    }
                    if (!canCreatureTypeSpawnAtLocation(ground, level, mutable)) continue;
                    Entity created = entry.entityType.create(level);
                    if (!(created instanceof EntityParasiteBase parasite)) {
                        if (created != null) created.discard();
                        return spawned;
                    }
                    parasite.canSpawnSpawn = true;
                    parasite.moveTo(f, origin ? mutable.getY() : i3, f1, level.random.nextFloat() * 360.0f, 0.0f);
                    if (parasite.getCanSpawnHere() && EventHooks.checkSpawnPosition(parasite, level, MobSpawnType.NATURAL)) {
                        groupData = EventHooks.finalizeMobSpawn(parasite, level, level.getCurrentDifficultyAt(parasite.blockPosition()), MobSpawnType.NATURAL, groupData);
                        ++packCount;
                        level.addFreshEntity(parasite);
                        if (packCount >= EventHooks.getMaxSpawnClusterSize(parasite)) continue chunks;
                    } else {
                        parasite.discard();
                    }
                    spawned += packCount;
                }
            }
        }
        return spawned;
    }

    @Nullable
    private static Player getClosestPlayer(double x, double y, double z, double distance, Level world) {
        double d0 = -1.0;
        Player found = null;
        for (Player player : world.players()) {
            double d1;
            if (player.isSpectator() || !((d1 = player.distanceToSqr(x, y, z)) > distance * distance) || d0 != -1.0 && !(d1 < d0)) continue;
            d0 = d1;
            found = player;
        }
        return found;
    }

    private static BlockPos getRandomChunkPosition(Level worldIn, int x, int z) {
        int i = x * 16 + worldIn.random.nextInt(16);
        int j = z * 16 + worldIn.random.nextInt(16);
        int minY = worldIn.getMinBuildHeight();
        int height = worldIn.getHeight(Heightmap.Types.WORLD_SURFACE, i, j);
        int k = Mth.roundToward(height + 1, 16);
        int l = worldIn.random.nextInt(Math.max(1, k > 0 ? k - minY : worldIn.getMaxBuildHeight() - minY)) + minY;
        return new BlockPos(i, l, j);
    }

    public static boolean canCreatureTypeSpawnAtLocation(SRPSpawning.Placement placement, Level worldIn, BlockPos pos) {
        if (!worldIn.getWorldBorder().isWithinBounds(pos)) {
            return false;
        }
        return canCreatureTypeSpawnBody(placement, worldIn, pos);
    }

    public static boolean canCreatureTypeSpawnBody(SRPSpawning.Placement placement, Level worldIn, BlockPos pos) {
        BlockState state = worldIn.getBlockState(pos);
        if (placement == SRPSpawning.Placement.IN_WATER) {
            return LegacyMaterial.of(state) == LegacyMaterial.water && worldIn.getBlockState(pos.below()).getFluidState().is(FluidTags.WATER)
                    && !worldIn.getBlockState(pos.above()).isRedstoneConductor(worldIn, pos.above());
        }
        if (placement == SRPSpawning.Placement.IN_AIR) {
            return state.getBlock() == Blocks.AIR && worldIn.getBlockState(pos.below()).getBlock() == Blocks.AIR && worldIn.getBlockState(pos.above()).getBlock() == Blocks.AIR;
        }
        BlockPos below = pos.below();
        BlockState belowState = worldIn.getBlockState(below);
        // 1.12 canCreatureSpawn: a solid top face that is not bedrock / barrier
        if (!belowState.isFaceSturdy(worldIn, below, net.minecraft.core.Direction.UP)) {
            return false;
        }
        Block block = belowState.getBlock();
        boolean flag = block != Blocks.BEDROCK && block != Blocks.BARRIER;
        return flag && isValidEmptySpawnBlock(worldIn, pos, state) && isValidEmptySpawnBlock(worldIn, pos.above(), worldIn.getBlockState(pos.above()));
    }

    public static boolean isValidEmptySpawnBlock(Level level, BlockPos pos, BlockState state) {
        if (state.isRedstoneConductor(level, pos)) {
            return false;
        }
        if (state.isSignalSource()) {
            return false;
        }
        if (LegacyMaterial.of(state).isLiquid()) {
            return false;
        }
        return !(state.getBlock() instanceof BaseRailBlock);
    }

    private static void filterEligibleChunksForOrigin(SRPWorldData data, boolean originsExist, boolean originlessAllowed) {
        if (!originsExist || originlessAllowed || data == null || eligibleChunksForSpawning.isEmpty()) {
            return;
        }
        List<Integer> originsX = data.getorigins("x");
        List<Integer> originsZ = data.getorigins("z");
        List<Integer> originsA = data.getorigins("a");
        int originCount = Math.min(originsX.size(), Math.min(originsZ.size(), originsA.size()));
        List<Integer> coloniesX = data.getColonies("x");
        List<Integer> coloniesY = data.getColonies("y");
        List<Integer> coloniesZ = data.getColonies("z");
        int colonyCount = Math.min(coloniesX.size(), Math.min(coloniesY.size(), coloniesZ.size()));
        int[] colonyRadii = new int[colonyCount];
        for (int i = 0; i < colonyCount; ++i) {
            BlockPos colonyPos = new BlockPos(coloniesX.get(i), coloniesY.get(i), coloniesZ.get(i));
            colonyRadii[i] = Math.max(0, data.getColonyDistanceSpreadByPosition(colonyPos, false));
        }
        Iterator<ChunkPos> chunkIterator = eligibleChunksForSpawning.iterator();
        while (chunkIterator.hasNext()) {
            ChunkPos chunkPos = chunkIterator.next();
            boolean inRange = false;
            for (int i = 0; i < originCount; ++i) {
                if (!chunkIntersectsRadius2D(chunkPos.x, chunkPos.z, originsX.get(i), originsZ.get(i), originsA.get(i))) continue;
                inRange = true;
                break;
            }
            if (!inRange) {
                for (int i = 0; i < colonyCount; ++i) {
                    if (!chunkIntersectsRadius2D(chunkPos.x, chunkPos.z, coloniesX.get(i), coloniesZ.get(i), colonyRadii[i])) continue;
                    inRange = true;
                    break;
                }
            }
            if (inRange) continue;
            chunkIterator.remove();
        }
    }

    private static boolean chunkIntersectsRadius2D(int chunkX, int chunkZ, int centerX, int centerZ, int radius) {
        if (radius <= 0) {
            return false;
        }
        int minX = chunkX << 4;
        int maxX = minX + 15;
        int minZ = chunkZ << 4;
        int maxZ = minZ + 15;
        int closestX = Math.max(minX, Math.min(centerX, maxX));
        long dx = (long) centerX - (long) closestX;
        int closestZ = Math.max(minZ, Math.min(centerZ, maxZ));
        long dz = (long) centerZ - (long) closestZ;
        long radiusSq = (long) radius * (long) radius;
        return dx * dx + dz * dz <= radiusSq;
    }

    @Nullable
    public static SRPSpawning.SpawnEntry getSpawnListEntryForTypeAt(ServerLevel worldServerIn, BlockPos pos) {
        SRPSaveData dat = SRPSaveData.get(worldServerIn);
        String id = DimKeys.of(worldServerIn);
        if (dat == null) {
            return null;
        }
        if (SRPConfigWorld.originActivated) {
            SRPWorldData data = SRPWorldData.get(worldServerIn);
            boolean originsExist = data != null && !data.getorigins("x").isEmpty();
            SRPSaveData saveData225 = SRPSaveData.get(worldServerIn);
            boolean originlessAllowed = saveData225 != null && saveData225.getDeveLevel() >= SRPConfigSystems.deveOriginlessUse;
            return getSpawnListEntryForTypeAtOrigin(worldServerIn, pos, dat, data, originsExist, originlessAllowed);
        }
        return pick(worldServerIn, SRPSpawning.getSpawns(worldServerIn, id, dat.getEvolutionPhase(id), dat));
    }

    @Nullable
    private static SRPSpawning.SpawnEntry getSpawnListEntryForTypeAtOrigin(ServerLevel worldServerIn, BlockPos pos, SRPSaveData dat, SRPWorldData data, boolean originsExist, boolean originlessAllowed) {
        String id = DimKeys.of(worldServerIn);
        if (!isPosWithinOrigin(worldServerIn, pos, data, originsExist, originlessAllowed)) {
            return null;
        }
        int phase = dat.getEvolutionPhase(id);
        if (phase == -1) {
            if (!originsExist) {
                return null;
            }
            return pick(worldServerIn, SRPSpawning.getSpawns(worldServerIn, id, phase, dat));
        }
        int phaseToUse = originsExist ? phase : 0;
        return pick(worldServerIn, SRPSpawning.getSpawns(worldServerIn, id, phaseToUse, dat));
    }

    @Nullable
    private static SRPSpawning.SpawnEntry pick(ServerLevel level, @Nullable List<SRPSpawning.SpawnEntry> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        Optional<SRPSpawning.SpawnEntry> chosen = WeightedRandom.getRandomItem(level.random, list);
        return chosen.orElse(null);
    }

    private static boolean isPosWithinOrigin(Level world, BlockPos pos, SRPWorldData data, boolean originsExist, boolean originlessAllowed) {
        if (com.dhanantry.scapeandrunparasites.block.SRPBlockLinks.isParasiteBiome(world, pos)) {
            return true;
        }
        if (data == null) {
            return false;
        }
        int a = data.nearestInfectionValue(pos, false);
        if (data.nearestColonyPosition(pos, true) != null) {
            return true;
        }
        return a > 0 || originsExist && originlessAllowed || !originsExist && !data.getTriggerMet();
    }
}
