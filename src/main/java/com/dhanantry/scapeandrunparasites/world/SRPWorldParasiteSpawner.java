package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSpawning;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;

public class SRPWorldParasiteSpawner {
    private static final Set<ChunkPos> eligibleChunksForSpawning = Sets.newHashSet();
    private static int lock = 0;
    private static int originC = 0;
    public static boolean triggerSPAWNING = false;
    public static int choiceNUMBER = 1;

    public static int findChunksForSpawning(ServerLevel worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
        if (!SRPConfigWorld.originActivated) {
            return SRPWorldParasiteSpawner.findChunksForSpawningVanilla(worldServerIn, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate);
        }
        return SRPWorldParasiteSpawner.findChunksForSpawningOrigin(worldServerIn, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate);
    }

    public static int findChunksForSpawningVanilla(ServerLevel worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
        if (!spawnHostileMobs && !spawnPeacefulMobs) {
            return 0;
        }
        if (!SRPSpawning.totalParasites) {
            if (++lock > 40) {
                SRPSpawning.totalParasites = true;
                lock = 0;
            }
            return 0;
        }
        eligibleChunksForSpawning.clear();
        for (Player entityplayer : worldServerIn.players()) {
            if (entityplayer.isSpectator()) continue;
            int j = Mth.floor((double)(entityplayer.getX() / 16.0));
            int k = Mth.floor((double)(entityplayer.getZ() / 16.0));
            for (int i1 = -8; i1 <= 8; ++i1) {
                for (int j1 = -8; j1 <= 8; ++j1) {
                    PlayerChunkMapEntry playerchunkmapentry;
                    boolean flag = i1 == -8 || i1 == 8 || j1 == -8 || j1 == 8;
                    ChunkPos chunkpos = new ChunkPos(i1 + j, j1 + k);
                    if (eligibleChunksForSpawning.contains(chunkpos) || flag || !worldServerIn.getWorldBorder().contains(chunkpos) || (playerchunkmapentry = worldServerIn.getPlayerChunkMap().getEntry(chunkpos.chunkXPos, chunkpos.chunkZPos)) == null || !playerchunkmapentry.isSentToPlayers()) continue;
                    eligibleChunksForSpawning.add(chunkpos);
                }
            }
        }
        int j4 = 0;
        BlockPos blockpos1 = worldServerIn.getSpawnPoint();
        ArrayList shuffled = Lists.newArrayList(eligibleChunksForSpawning);
        Collections.shuffle(shuffled);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        block5: for (ChunkPos chunkpos1 : shuffled) {
            BlockPos blockpos = SRPWorldParasiteSpawner.getRandomChunkPosition((Level)worldServerIn, chunkpos1.chunkXPos, chunkpos1.chunkZPos);
            int k1 = blockpos.getX();
            int l1 = blockpos.getY();
            int i2 = blockpos.getZ();
            BlockState iblockstate = worldServerIn.getBlockState(blockpos);
            if (iblockstate.isNormalCube()) continue;
            int j2 = 0;
            block6: for (int k2 = 0; k2 < 3; ++k2) {
                int l2 = k1;
                int i3 = l1;
                int j3 = i2;
                Biome.SpawnListEntry biome$spawnlistentry = null;
                SpawnGroupData ientitylivingdata = null;
                int l3 = Mth.ceiling_double_int((double)(Math.random() * 4.0));
                for (int i4 = 0; i4 < l3; ++i4) {
                    EntityParasiteBase entityliving;
                    Player closest;
                    blockpos$mutableblockpos.set(l2 += worldServerIn.random.nextInt(6) - worldServerIn.random.nextInt(6), i3 += worldServerIn.random.nextInt(1) - worldServerIn.random.nextInt(1), j3 += worldServerIn.random.nextInt(6) - worldServerIn.random.nextInt(6));
                    float f = (float)l2 + 0.5f;
                    float f1 = (float)j3 + 0.5f;
                    if (worldServerIn.isAnyPlayerWithinRangeAt((double)f, (double)i3, (double)f1, 24.0) || !(blockpos1.distToLowCornerSqr((double)f, (double)i3, (double)f1) >= 576.0)) continue;
                    if (biome$spawnlistentry == null && (biome$spawnlistentry = SRPWorldParasiteSpawner.getSpawnListEntryForTypeAt(worldServerIn, (BlockPos)blockpos$mutableblockpos)) == null) continue block6;
                    Mob.SpawnPlacementType ground = EntitySpawnPlacementRegistry.getPlacementForEntity((Class)biome$spawnlistentry.entityClass);
                    if (ground == Mob.SpawnPlacementType.IN_AIR && (closest = SRPWorldParasiteSpawner.getClosestPlayer(f, i3, f1, 24.0, (Level)worldServerIn)) != null) {
                        double base = closest.getY();
                        double randomOff = worldServerIn.random.nextInt(21) - 10;
                        base = Math.max(base, (double)worldServerIn.getWorldInfo().getTerrainType().getMinimumSpawnHeight((Level)worldServerIn) / 2.0);
                        base = Math.min(base, (double)SRPConfigWorld.spawnerSKYLimitUp);
                        blockpos$mutableblockpos.setPos((double)l2, base + randomOff, (double)j3);
                    }
                    if (!SRPWorldParasiteSpawner.canCreatureTypeSpawnAtLocation(ground, (Level)worldServerIn, (BlockPos)blockpos$mutableblockpos)) continue;
                    try {
                        entityliving = (EntityParasiteBase)biome$spawnlistentry.newInstance((Level)worldServerIn);
                        entityliving.canSpawnSpawn = true;
                    }
                    catch (Exception exception) {
                        return j4;
                    }
                    entityliving.moveTo(f, i3, f1, worldServerIn.random.nextFloat() * 360.0f, 0.0f);
                    Event.Result canSpawn = EventHooks.canEntitySpawn((Mob)entityliving, (Level)worldServerIn, (float)f, (float)i3, (float)f1, (boolean)false);
                    if (canSpawn == Event.Result.ALLOW || canSpawn == Event.Result.DEFAULT && entityliving.getCanSpawnHere()) {
                        if (!EventHooks.doSpecialSpawn((Mob)entityliving, (Level)worldServerIn, (float)f, (float)i3, (float)f1)) {
                            ientitylivingdata = entityliving.finalizeSpawn((ServerLevel) entityliving.level(), worldServerIn.getCurrentDifficultyAt(entityliving.blockPosition()), MobSpawnType.MOB_SUMMONED, ientitylivingdata);
                        }
                        if (entityliving.checkSpawnObstruction()) {
                            ++j2;
                            worldServerIn.addFreshEntity((Entity)entityliving);
                        } else {
                            entityliving.discard();
                        }
                        if (j2 >= EventHooks.getMaxSpawnPackSize((Mob)entityliving)) continue block5;
                    }
                    j4 += j2;
                }
            }
        }
        return j4;
    }

    public static int findChunksForSpawningOrigin(ServerLevel worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
        if (!spawnHostileMobs && !spawnPeacefulMobs) {
            return 0;
        }
        if (!SRPSpawning.totalParasites) {
            if (++lock > 7) {
                SRPSpawning.totalParasites = true;
                lock = 0;
            }
            return 0;
        }
        eligibleChunksForSpawning.clear();
        SRPWorldData worldData = SRPWorldData.get((Level)worldServerIn);
        SRPSaveData saveData225 = SRPSaveData.get((Level)worldServerIn, 225);
        SRPSaveData saveData72 = SRPSaveData.get((Level)worldServerIn, 72);
        boolean originsExist = worldData != null && !worldData.getorigins("x").isEmpty();
        boolean originlessAllowed = saveData225 != null && saveData225.getDeveLevel() >= SRPConfigSystems.deveOriginlessUse;
        for (Player entityplayer : worldServerIn.players()) {
            if (entityplayer.isSpectator()) continue;
            int j = Mth.floor((double)(entityplayer.getX() / 16.0));
            int k = Mth.floor((double)(entityplayer.getZ() / 16.0));
            for (int i1 = -8; i1 <= 8; ++i1) {
                for (int j1 = -8; j1 <= 8; ++j1) {
                    PlayerChunkMapEntry playerchunkmapentry;
                    boolean flag = i1 == -8 || i1 == 8 || j1 == -8 || j1 == 8;
                    ChunkPos chunkpos = new ChunkPos(i1 + j, j1 + k);
                    if (eligibleChunksForSpawning.contains(chunkpos) || flag || !worldServerIn.getWorldBorder().contains(chunkpos) || (playerchunkmapentry = worldServerIn.getPlayerChunkMap().getEntry(chunkpos.chunkXPos, chunkpos.chunkZPos)) == null || !playerchunkmapentry.isSentToPlayers()) continue;
                    eligibleChunksForSpawning.add(chunkpos);
                }
            }
        }
        SRPWorldParasiteSpawner.filterEligibleChunksForOrigin(worldData, originsExist, originlessAllowed);
        if (eligibleChunksForSpawning.isEmpty()) {
            return 0;
        }
        int j4 = 0;
        BlockPos blockpos1 = worldServerIn.getSpawnPoint();
        ArrayList shuffled = Lists.newArrayList(eligibleChunksForSpawning);
        Collections.shuffle(shuffled);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        block5: for (ChunkPos chunkpos1 : shuffled) {
            BlockPos blockpos = SRPWorldParasiteSpawner.getRandomChunkPosition((Level)worldServerIn, chunkpos1.chunkXPos, chunkpos1.chunkZPos);
            int k1 = blockpos.getX();
            int l1 = blockpos.getY();
            int i2 = blockpos.getZ();
            BlockState iblockstate = worldServerIn.getBlockState(blockpos);
            if (iblockstate.isNormalCube()) continue;
            int j2 = 0;
            block6: for (int k2 = 0; k2 < 3; ++k2) {
                int l2 = k1;
                int i3 = l1;
                int j3 = i2;
                Biome.SpawnListEntry biome$spawnlistentry = null;
                SpawnGroupData ientitylivingdata = null;
                int l3 = Mth.ceiling_double_int((double)(Math.random() * 4.0));
                for (int i4 = 0; i4 < l3; ++i4) {
                    EntityParasiteBase entityliving;
                    blockpos$mutableblockpos.set(l2 += worldServerIn.random.nextInt(6) - worldServerIn.random.nextInt(6), i3 += worldServerIn.random.nextInt(1) - worldServerIn.random.nextInt(1), j3 += worldServerIn.random.nextInt(6) - worldServerIn.random.nextInt(6));
                    float f = (float)l2 + 0.5f;
                    float f1 = (float)j3 + 0.5f;
                    if (worldServerIn.isAnyPlayerWithinRangeAt((double)f, (double)i3, (double)f1, 24.0) || !(blockpos1.distToLowCornerSqr((double)f, (double)i3, (double)f1) >= 576.0)) continue;
                    if (biome$spawnlistentry == null && (biome$spawnlistentry = SRPWorldParasiteSpawner.getSpawnListEntryForTypeAtOrigin(worldServerIn, (BlockPos)blockpos$mutableblockpos, saveData72, worldData, originsExist, originlessAllowed)) == null) continue block6;
                    Mob.SpawnPlacementType ground = EntitySpawnPlacementRegistry.getPlacementForEntity((Class)biome$spawnlistentry.entityClass);
                    if (ground == Mob.SpawnPlacementType.IN_AIR) {
                        if (worldServerIn.random.nextDouble() <= 0.7) continue;
                        Player closest = SRPWorldParasiteSpawner.getClosestPlayer(f, i3, f1, 24.0, (Level)worldServerIn);
                        if (closest != null) {
                            double base = closest.getY();
                            double randomOff = worldServerIn.random.nextInt(21) - 10;
                            base = Math.max(base, (double)worldServerIn.getWorldInfo().getTerrainType().getMinimumSpawnHeight((Level)worldServerIn) / 2.0);
                            base = Math.min(base, (double)SRPConfigWorld.spawnerSKYLimitUp);
                            blockpos$mutableblockpos.setPos((double)l2, base + randomOff, (double)j3);
                        }
                    }
                    if (!SRPWorldParasiteSpawner.canCreatureTypeSpawnAtLocation(ground, (Level)worldServerIn, (BlockPos)blockpos$mutableblockpos)) continue;
                    try {
                        entityliving = (EntityParasiteBase)biome$spawnlistentry.newInstance((Level)worldServerIn);
                        entityliving.canSpawnSpawn = true;
                    }
                    catch (Exception exception) {
                        return j4;
                    }
                    entityliving.moveTo(f, i3, f1, worldServerIn.random.nextFloat() * 360.0f, 0.0f);
                    entityliving.moveTo(f, blockpos$mutableblockpos.getY(), f1, worldServerIn.random.nextFloat() * 360.0f, 0.0f);
                    Event.Result canSpawn = EventHooks.canEntitySpawn((Mob)entityliving, (Level)worldServerIn, (float)f, (float)i3, (float)f1, (boolean)false);
                    if (canSpawn == Event.Result.ALLOW || canSpawn == Event.Result.DEFAULT && entityliving.getCanSpawnHere()) {
                        if (!EventHooks.doSpecialSpawn((Mob)entityliving, (Level)worldServerIn, (float)f, (float)i3, (float)f1)) {
                            ientitylivingdata = entityliving.finalizeSpawn((ServerLevel) entityliving.level(), worldServerIn.getCurrentDifficultyAt(entityliving.blockPosition()), MobSpawnType.MOB_SUMMONED, ientitylivingdata);
                        }
                        if (entityliving.checkSpawnObstruction()) {
                            ++j2;
                            worldServerIn.addFreshEntity((Entity)entityliving);
                        } else {
                            entityliving.discard();
                        }
                        if (j2 >= EventHooks.getMaxSpawnPackSize((Mob)entityliving)) continue block5;
                    }
                    j4 += j2;
                }
            }
        }
        return j4;
    }

    @Nullable
    private static Player getClosestPlayer(double x, double y, double z, double distance, Level world) {
        double d0 = -1.0;
        Player entityplayer = null;
        List<? extends Player> list = world.players();
        for (Player entityPlayer : list) {
            double d1;
            if (!EntitySelector.NO_SPECTATORS.apply(entityPlayer) || !((d1 = entityPlayer.distanceToSqr(x, y, z)) > distance * distance) || d0 != -1.0 && !(d1 < d0)) continue;
            d0 = d1;
            entityplayer = entityPlayer;
        }
        return entityplayer;
    }

    private static BlockPos getRandomChunkPosition(Level worldIn, int x, int z) {
        Chunk chunk = worldIn.getChunkFromChunkCoords(x, z);
        int i = x * 16 + worldIn.random.nextInt(16);
        int j = z * 16 + worldIn.random.nextInt(16);
        int k = Mth.roundUp((int)(chunk.getHeight(BlockPos.containing(i, 0, j)) + 1), (int)16);
        int l = worldIn.random.nextInt(Math.max(1, k > 0 ? k : chunk.getTopFilledSegment() + 16 - 1));
        return BlockPos.containing(i, l, j);
    }

    public static boolean canCreatureTypeSpawnAtLocation(Mob.SpawnPlacementType spawnPlacementTypeIn, Level worldIn, BlockPos pos) {
        if (!worldIn.getWorldBorder().contains(pos)) {
            return false;
        }
        return SRPWorldParasiteSpawner.canCreatureTypeSpawnBody(spawnPlacementTypeIn, worldIn, pos);
    }

    public static boolean canCreatureTypeSpawnBody(Mob.SpawnPlacementType spawnPlacementTypeIn, Level worldIn, BlockPos pos) {
        BlockState iblockstate = worldIn.getBlockState(pos);
        if (spawnPlacementTypeIn == Mob.SpawnPlacementType.IN_WATER) {
            return iblockstate.getMaterial() == Material.water && worldIn.getBlockState(pos.below()).getFluidState().is(FluidTags.WATER) && !worldIn.getBlockState(pos.above()).isNormalCube();
        }
        if (spawnPlacementTypeIn == Mob.SpawnPlacementType.IN_AIR) {
            return iblockstate.getBlock() == Blocks.AIR && worldIn.getBlockState(pos.below()).getBlock() == Blocks.AIR && worldIn.getBlockState(pos.above()).getBlock() == Blocks.AIR;
        }
        BlockPos blockpos = pos.below();
        BlockState state = worldIn.getBlockState(blockpos);
        if (!state.getBlock().canCreatureSpawn(state, (BlockGetter)worldIn, blockpos, spawnPlacementTypeIn)) {
            return false;
        }
        Block block = worldIn.getBlockState(blockpos).getBlock();
        boolean flag = block != Blocks.BEDROCK && block != Blocks.BARRIER;
        return flag && SRPWorldParasiteSpawner.isValidEmptySpawnBlock(iblockstate) && SRPWorldParasiteSpawner.isValidEmptySpawnBlock(worldIn.getBlockState(pos.above()));
    }

    public static boolean isValidEmptySpawnBlock(BlockState state) {
        if (state.isBlockNormalCube()) {
            return false;
        }
        if (state.canProvidePower()) {
            return false;
        }
        if (state.getMaterial().isLiquid()) {
            return false;
        }
        return !BlockRailBase.isRailBlock((BlockState)state);
    }

    private static void filterEligibleChunksForOrigin(SRPWorldData data, boolean originsExist, boolean originlessAllowed) {
        if (!originsExist || originlessAllowed || data == null || eligibleChunksForSpawning.isEmpty()) {
            return;
        }
        ArrayList<Integer> originsX = data.getorigins("x");
        ArrayList<Integer> originsZ = data.getorigins("z");
        ArrayList<Integer> originsA = data.getorigins("a");
        int originCount = Math.min(originsX.size(), Math.min(originsZ.size(), originsA.size()));
        ArrayList<Integer> coloniesX = data.getColonies("x");
        ArrayList<Integer> coloniesY = data.getColonies("y");
        ArrayList<Integer> coloniesZ = data.getColonies("z");
        int colonyCount = Math.min(coloniesX.size(), Math.min(coloniesY.size(), coloniesZ.size()));
        int[] colonyRadii = new int[colonyCount];
        for (int i = 0; i < colonyCount; ++i) {
            BlockPos colonyPos = BlockPos.containing(coloniesX.get(i).intValue(), coloniesY.get(i).intValue(), coloniesZ.get(i).intValue());
            colonyRadii[i] = Math.max(0, data.getColonyDistanceSpreadByPosition(colonyPos, false));
        }
        Iterator<ChunkPos> chunkIterator = eligibleChunksForSpawning.iterator();
        while (chunkIterator.hasNext()) {
            int i;
            ChunkPos chunkPos = chunkIterator.next();
            boolean inRange = false;
            for (i = 0; i < originCount; ++i) {
                if (!SRPWorldParasiteSpawner.chunkIntersectsRadius2D(chunkPos.chunkXPos, chunkPos.chunkZPos, originsX.get(i), originsZ.get(i), originsA.get(i))) continue;
                inRange = true;
                break;
            }
            if (!inRange) {
                for (i = 0; i < colonyCount; ++i) {
                    if (!SRPWorldParasiteSpawner.chunkIntersectsRadius2D(chunkPos.chunkXPos, chunkPos.chunkZPos, coloniesX.get(i), coloniesZ.get(i), colonyRadii[i])) continue;
                    inRange = true;
                    break;
                }
            }
            if (inRange) continue;
            chunkIterator.remove();
        }
    }

    private static boolean chunkIntersectsRadius2D(int chunkX, int chunkZ, int centerX, int centerZ, int radius) {
        long radiusSq;
        int closestZ;
        long dz;
        if (radius <= 0) {
            return false;
        }
        int minX = chunkX << 4;
        int maxX = minX + 15;
        int minZ = chunkZ << 4;
        int maxZ = minZ + 15;
        int closestX = Math.max(minX, Math.min(centerX, maxX));
        long dx = (long)centerX - (long)closestX;
        return dx * dx + (dz = (long)centerZ - (long)(closestZ = Math.max(minZ, Math.min(centerZ, maxZ)))) * dz <= (radiusSq = (long)radius * (long)radius);
    }

    @Nullable
    public static Biome.SpawnListEntry getSpawnListEntryForTypeAt(ServerLevel worldServerIn, BlockPos pos) {
        SRPSaveData dat = SRPSaveData.get((Level)worldServerIn, 72);
        int id = DimKeys.of(worldServerIn);
        if (dat == null) {
            return null;
        }
        if (SRPConfigWorld.originActivated) {
            SRPWorldData data = SRPWorldData.get((Level)worldServerIn);
            boolean originsExist = data != null && !data.getorigins("x").isEmpty();
            SRPSaveData saveData225 = SRPSaveData.get((Level)worldServerIn, 225);
            boolean originlessAllowed = saveData225 != null && saveData225.getDeveLevel() >= SRPConfigSystems.deveOriginlessUse;
            return SRPWorldParasiteSpawner.getSpawnListEntryForTypeAtOrigin(worldServerIn, pos, dat, data, originsExist, originlessAllowed);
        }
        List<Biome.SpawnListEntry> list = SRPSpawning.getSpawns((Level)worldServerIn, id, dat.getEvolutionPhase(id), dat);
        Biome.SpawnListEntry chosen = list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem((Random)worldServerIn.random, list) : null;
        return chosen;
    }

    @Nullable
    private static Biome.SpawnListEntry getSpawnListEntryForTypeAtOrigin(ServerLevel worldServerIn, BlockPos pos, SRPSaveData dat, SRPWorldData data, boolean originsExist, boolean originlessAllowed) {
        int id = DimKeys.of(worldServerIn);
        if (!SRPWorldParasiteSpawner.isPosWithinOrigin((Level)worldServerIn, pos, data, originsExist, originlessAllowed)) {
            return null;
        }
        byte phase = dat.getEvolutionPhase(id);
        if (phase == -1) {
            if (!originsExist) {
                return null;
            }
            List<Biome.SpawnListEntry> list = SRPSpawning.getSpawns((Level)worldServerIn, id, phase, dat);
            Biome.SpawnListEntry chosen = list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem((Random)worldServerIn.random, list) : null;
            return chosen;
        }
        byte phaseToUse = originsExist ? phase : (byte)0;
        List<Biome.SpawnListEntry> list = SRPSpawning.getSpawns((Level)worldServerIn, id, phaseToUse, dat);
        Biome.SpawnListEntry chosen = list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem((Random)worldServerIn.random, list) : null;
        return chosen;
    }

    private static boolean isPosWithinOrigin(Level world, BlockPos pos, SRPWorldData data, boolean originsExist, boolean originlessAllowed) {
        if (world.getBiome(pos).value() instanceof BiomeParasiteBase) {
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

    private static String posToString(BlockPos pos) {
        return "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
    }
}

