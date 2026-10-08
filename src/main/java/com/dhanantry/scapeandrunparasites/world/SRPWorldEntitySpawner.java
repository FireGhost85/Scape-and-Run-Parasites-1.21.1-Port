package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.SRPMain;
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
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
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
import net.neoforged.neoforge.event.entity.living.MobDespawnEvent;

public class SRPWorldEntitySpawner {
    private static final Set<ChunkPos> eligibleChunksForSpawning = Sets.newHashSet();
    private static int lock = 0;
    public static boolean triggerSPAWNING = false;
    public static int choiceNUMBER = 1;
    public static final int STAR_NORMAL = 0;
    public static final int STAR_COLD = 1;
    public static final int STAR_WARM = 2;
    public static int starType = 0;

    private static void debugSpawn(String msg) {
        if (SRPConfigSystems.debugSpawner) {
            SRPMain.logger.info("[SRP Spawner] " + msg);
        }
    }

    private static String posToString(BlockPos pos) {
        return "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
    }

    @Nullable
    private static String getSpawnFailureReason(Mob.SpawnPlacementType spawnPlacementTypeIn, Level worldIn, BlockPos pos) {
        if (!worldIn.getWorldBorder().contains(pos)) {
            return "outside world border";
        }
        BlockState iblockstate = worldIn.getBlockState(pos);
        if (spawnPlacementTypeIn == Mob.SpawnPlacementType.IN_WATER) {
            if (iblockstate.getMaterial() != Material.water) {
                return "spawn block is not water";
            }
            if (worldIn.getBlockState(pos.below()).getFluidState().is(FluidTags.WATER) == false) {
                return "block below is not water";
            }
            if (worldIn.getBlockState(pos.above()).getFluidState().is(FluidTags.WATER) == false) {
                return "block above is not water";
            }
            return null;
        }
        if (spawnPlacementTypeIn == Mob.SpawnPlacementType.IN_AIR) {
            if (iblockstate.getBlock() != Blocks.AIR) {
                return "spawn block is not air";
            }
            if (worldIn.getBlockState(pos.above()).isNormalCube()) {
                return "block above is solid";
            }
            return null;
        }
        BlockPos blockpos = pos.below();
        BlockState state = worldIn.getBlockState(blockpos);
        if (!state.getBlock().canCreatureSpawn(state, (BlockGetter)worldIn, blockpos, spawnPlacementTypeIn)) {
            return "block below does not allow creature spawn: " + state.getBlock().builtInRegistryHolder().key().location();
        }
        Block block = state.getBlock();
        if (block == Blocks.BEDROCK || block == Blocks.BARRIER) {
            return "block below is forbidden: " + block.builtInRegistryHolder().key().location();
        }
        if (!SRPWorldEntitySpawner.isValidEmptySpawnBlock(iblockstate)) {
            return "spawn block is not empty/valid";
        }
        if (!SRPWorldEntitySpawner.isValidEmptySpawnBlock(worldIn.getBlockState(pos.above()))) {
            return "block above is not empty/valid";
        }
        return null;
    }

    public static int findChunksForSpawning(ServerLevel worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
        if (!SRPConfigWorld.originActivated) {
            return SRPWorldEntitySpawner.findChunksForSpawningVanilla(worldServerIn, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate);
        }
        return SRPWorldEntitySpawner.findChunksForSpawningOrigin(worldServerIn, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate);
    }

    public static int findChunksForSpawningVanilla(ServerLevel worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
        if (!spawnHostileMobs && !spawnPeacefulMobs) {
            return 0;
        }
        if (!SRPSpawning.totalParasites) {
            if (++lock > 30) {
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
            BlockPos blockpos = SRPWorldEntitySpawner.getRandomChunkPosition((Level)worldServerIn, chunkpos1.chunkXPos, chunkpos1.chunkZPos);
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
                    boolean bypassForgeDenyForFloating;
                    EntityParasiteBase entityliving;
                    BlockPos beforeAdjust;
                    blockpos$mutableblockpos.set(l2 += worldServerIn.random.nextInt(6) - worldServerIn.random.nextInt(6), i3 += worldServerIn.random.nextInt(5) - worldServerIn.random.nextInt(5), j3 += worldServerIn.random.nextInt(6) - worldServerIn.random.nextInt(6));
                    float f = (float)l2 + 0.5f;
                    float f1 = (float)j3 + 0.5f;
                    if (worldServerIn.isAnyPlayerWithinRangeAt((double)f, (double)i3, (double)f1, 24.0) || !(blockpos1.distToLowCornerSqr((double)f, (double)i3, (double)f1) >= 576.0)) continue;
                    if (biome$spawnlistentry == null && (biome$spawnlistentry = SRPWorldEntitySpawner.getSpawnListEntryForTypeAt(worldServerIn, (BlockPos)blockpos$mutableblockpos)) == null) continue block6;
                    Mob.SpawnPlacementType ground = EntitySpawnPlacementRegistry.getPlacementForEntity((Class)biome$spawnlistentry.entityClass);
                    if (ground == Mob.SpawnPlacementType.IN_AIR) {
                        beforeAdjust = blockpos$mutableblockpos.toImmutable();
                        SRPWorldEntitySpawner.adjustAirSpawnPos((Level)worldServerIn, blockpos$mutableblockpos);
                        if (!beforeAdjust.equals(blockpos$mutableblockpos)) {
                            SRPWorldEntitySpawner.debugSpawn("VANILLA adjusted air spawn pos from " + SRPWorldEntitySpawner.posToString(beforeAdjust) + " to " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " for " + biome$spawnlistentry.entityClass.getSimpleName());
                        }
                    } else if (ground == Mob.SpawnPlacementType.IN_WATER) {
                        beforeAdjust = blockpos$mutableblockpos.toImmutable();
                        SRPWorldEntitySpawner.adjustWaterSpawnPos((Level)worldServerIn, blockpos$mutableblockpos);
                        if (!beforeAdjust.equals(blockpos$mutableblockpos)) {
                            SRPWorldEntitySpawner.debugSpawn("VANILLA adjusted water spawn pos from " + SRPWorldEntitySpawner.posToString(beforeAdjust) + " to " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " for " + biome$spawnlistentry.entityClass.getSimpleName());
                        }
                    } else if (ground == Mob.SpawnPlacementType.ON_GROUND) {
                        beforeAdjust = blockpos$mutableblockpos.toImmutable();
                        SRPWorldEntitySpawner.adjustGroundSpawnPos((Level)worldServerIn, blockpos$mutableblockpos);
                        if (!beforeAdjust.equals(blockpos$mutableblockpos)) {
                            SRPWorldEntitySpawner.debugSpawn("VANILLA adjusted ground spawn pos from " + SRPWorldEntitySpawner.posToString(beforeAdjust) + " to " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " for " + biome$spawnlistentry.entityClass.getSimpleName());
                        }
                    }
                    String failureReason = SRPWorldEntitySpawner.getSpawnFailureReason(ground, (Level)worldServerIn, (BlockPos)blockpos$mutableblockpos);
                    if (failureReason != null) {
                        SRPWorldEntitySpawner.debugSpawn("VANILLA rejected location for " + biome$spawnlistentry.entityClass.getSimpleName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " | reason=" + failureReason);
                        continue;
                    }
                    try {
                        entityliving = (EntityParasiteBase)biome$spawnlistentry.newInstance((Level)worldServerIn);
                        entityliving.canSpawnSpawn = true;
                    }
                    catch (Exception exception) {
                        SRPWorldEntitySpawner.debugSpawn("VANILLA failed to create entity instance for " + biome$spawnlistentry.entityClass.getName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " | reason=" + exception.getClass().getSimpleName() + ": " + exception.getMessage());
                        return j4;
                    }
                    float spawnX = (float)blockpos$mutableblockpos.getX() + 0.5f;
                    float spawnY = blockpos$mutableblockpos.getY();
                    float spawnZ = (float)blockpos$mutableblockpos.getZ() + 0.5f;
                    entityliving.moveTo(spawnX, spawnY, spawnZ, worldServerIn.random.nextFloat() * 360.0f, 0.0f);
                    Event.Result canSpawn = EventHooks.canEntitySpawn((Mob)entityliving, (Level)worldServerIn, (float)spawnX, (float)spawnY, (float)spawnZ, (boolean)false);
                    boolean canSpawnHere = entityliving.getCanSpawnHere();
                    boolean bl = bypassForgeDenyForFloating = canSpawn == MobDespawnEvent.Result.DENY && (ground == Mob.SpawnPlacementType.IN_WATER || ground == Mob.SpawnPlacementType.IN_AIR) && canSpawnHere;
                    if (canSpawn == MobDespawnEvent.Result.DENY || canSpawn == MobDespawnEvent.Result.DENY && !bypassForgeDenyForFloating) {
                        SRPWorldEntitySpawner.debugSpawn("VANILLA denied by Forge canEntitySpawn for " + entityliving.getName().getString() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                    } else if (canSpawn == Event.Result.ALLOW || bypassForgeDenyForFloating || canSpawn == Event.Result.DEFAULT && canSpawnHere) {
                        if (!EventHooks.doSpecialSpawn((Mob)entityliving, (Level)worldServerIn, (float)spawnX, (float)spawnY, (float)spawnZ)) {
                            ientitylivingdata = entityliving.finalizeSpawn((ServerLevel) entityliving.level(), worldServerIn.getCurrentDifficultyAt(entityliving.blockPosition()), MobSpawnType.MOB_SUMMONED, ientitylivingdata);
                        }
                        if (entityliving.checkSpawnObstruction()) {
                            worldServerIn.addFreshEntity((Entity)entityliving);
                            SRPWorldEntitySpawner.applyDebugGlow((Mob)entityliving);
                            SRPWorldEntitySpawner.debugSpawn("VANILLA spawned " + entityliving.getName().getString() + " at " + SRPWorldEntitySpawner.posToString(entityliving.blockPosition()) + " | packCount=" + ++j2 + " | glow=" + SRPConfigSystems.debugSpawner);
                        } else {
                            SRPWorldEntitySpawner.debugSpawn("VANILLA failed collision check for " + entityliving.getName().getString() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                            entityliving.discard();
                        }
                        if (j2 >= EventHooks.getMaxSpawnPackSize((Mob)entityliving)) {
                            SRPWorldEntitySpawner.debugSpawn("VANILLA reached max pack size for " + entityliving.getName().getString() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                            continue block5;
                        }
                    } else {
                        SRPWorldEntitySpawner.debugSpawn("VANILLA getCanSpawnHere returned false | selected=" + biome$spawnlistentry.entityClass.getName() + " | actualClass=" + entityliving.getClass().getName() + " | actualName=" + entityliving.getName().getString() + " | pos=" + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                    }
                    j4 += j2;
                }
            }
        }
        return j4;
    }

    private static void adjustGroundSpawnPos(Level worldIn, BlockPos.MutableBlockPos pos) {
        BlockPos adjusted = SRPWorldEntitySpawner.findGroundSpawnPos(worldIn, pos.toImmutable());
        pos.setPos((Vec3i)adjusted);
    }

    private static void adjustAirSpawnPos(Level worldIn, BlockPos.MutableBlockPos pos) {
        BlockPos adjusted = SRPWorldEntitySpawner.findAirSpawnPos(worldIn, pos.toImmutable());
        pos.setPos((Vec3i)adjusted);
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
        SRPWorldEntitySpawner.filterEligibleChunksForOrigin(worldData, originsExist, originlessAllowed);
        if (eligibleChunksForSpawning.isEmpty()) {
            return 0;
        }
        int j4 = 0;
        BlockPos blockpos1 = worldServerIn.getSpawnPoint();
        ArrayList shuffled = Lists.newArrayList(eligibleChunksForSpawning);
        Collections.shuffle(shuffled, worldServerIn.random);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        block5: for (ChunkPos chunkpos1 : shuffled) {
            BlockPos blockpos = SRPWorldEntitySpawner.getRandomChunkPosition((Level)worldServerIn, chunkpos1.chunkXPos, chunkpos1.chunkZPos);
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
                int l3 = 1 + worldServerIn.random.nextInt(4);
                for (int i4 = 0; i4 < l3; ++i4) {
                    EntityParasiteBase entityliving;
                    BlockPos beforeAdjust;
                    blockpos$mutableblockpos.set(l2 += worldServerIn.random.nextInt(6) - worldServerIn.random.nextInt(6), i3 += worldServerIn.random.nextInt(5) - worldServerIn.random.nextInt(5), j3 += worldServerIn.random.nextInt(6) - worldServerIn.random.nextInt(6));
                    float f = (float)l2 + 0.5f;
                    float f1 = (float)j3 + 0.5f;
                    if (worldServerIn.isAnyPlayerWithinRangeAt((double)f, (double)i3, (double)f1, 24.0) || !(blockpos1.distToLowCornerSqr((double)f, (double)i3, (double)f1) >= 576.0)) continue;
                    if (biome$spawnlistentry == null && (biome$spawnlistentry = SRPWorldEntitySpawner.getSpawnListEntryForTypeAtOrigin(worldServerIn, (BlockPos)blockpos$mutableblockpos, saveData72, worldData, originsExist, originlessAllowed)) == null) {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN no spawn entry available at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                        continue block6;
                    }
                    Mob.SpawnPlacementType ground = EntitySpawnPlacementRegistry.getPlacementForEntity((Class)biome$spawnlistentry.entityClass);
                    if (ground == Mob.SpawnPlacementType.IN_AIR) {
                        beforeAdjust = SRPConfigSystems.debugSpawner ? blockpos$mutableblockpos.toImmutable() : null;
                        SRPWorldEntitySpawner.adjustAirSpawnPos((Level)worldServerIn, blockpos$mutableblockpos);
                        if (beforeAdjust != null && !beforeAdjust.equals(blockpos$mutableblockpos)) {
                            SRPWorldEntitySpawner.debugSpawn("ORIGIN adjusted air spawn pos from " + SRPWorldEntitySpawner.posToString(beforeAdjust) + " to " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " for " + biome$spawnlistentry.entityClass.getSimpleName());
                        }
                    } else if (ground == Mob.SpawnPlacementType.IN_WATER) {
                        beforeAdjust = SRPConfigSystems.debugSpawner ? blockpos$mutableblockpos.toImmutable() : null;
                        SRPWorldEntitySpawner.adjustWaterSpawnPos((Level)worldServerIn, blockpos$mutableblockpos);
                        if (beforeAdjust != null && !beforeAdjust.equals(blockpos$mutableblockpos)) {
                            SRPWorldEntitySpawner.debugSpawn("ORIGIN adjusted water spawn pos from " + SRPWorldEntitySpawner.posToString(beforeAdjust) + " to " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " for " + biome$spawnlistentry.entityClass.getSimpleName());
                        }
                    } else if (ground == Mob.SpawnPlacementType.ON_GROUND) {
                        beforeAdjust = SRPConfigSystems.debugSpawner ? blockpos$mutableblockpos.toImmutable() : null;
                        SRPWorldEntitySpawner.adjustGroundSpawnPos((Level)worldServerIn, blockpos$mutableblockpos);
                        if (beforeAdjust != null && !beforeAdjust.equals(blockpos$mutableblockpos)) {
                            SRPWorldEntitySpawner.debugSpawn("ORIGIN adjusted ground spawn pos from " + SRPWorldEntitySpawner.posToString(beforeAdjust) + " to " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " for " + biome$spawnlistentry.entityClass.getSimpleName());
                        }
                    }
                    String failureReason = SRPWorldEntitySpawner.getSpawnFailureReason(ground, (Level)worldServerIn, (BlockPos)blockpos$mutableblockpos);
                    if (failureReason != null) {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN rejected location for " + biome$spawnlistentry.entityClass.getSimpleName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " | reason=" + failureReason);
                        continue;
                    }
                    boolean withinOrigin = SRPWorldEntitySpawner.isPosWithinOrigin((Level)worldServerIn, (BlockPos)blockpos$mutableblockpos, worldData, originsExist, originlessAllowed);
                    if (!withinOrigin) {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN adjusted position outside origin for " + biome$spawnlistentry.entityClass.getSimpleName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                        continue;
                    }
                    try {
                        entityliving = (EntityParasiteBase)biome$spawnlistentry.newInstance((Level)worldServerIn);
                        entityliving.canSpawnSpawn = true;
                    }
                    catch (Exception exception) {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN failed to create entity instance for " + biome$spawnlistentry.entityClass.getName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " | reason=" + exception.getClass().getSimpleName() + ": " + exception.getMessage());
                        return j4;
                    }
                    float spawnX = (float)blockpos$mutableblockpos.getX() + 0.5f;
                    float spawnY = blockpos$mutableblockpos.getY();
                    float spawnZ = (float)blockpos$mutableblockpos.getZ() + 0.5f;
                    entityliving.moveTo(spawnX, spawnY, spawnZ, worldServerIn.random.nextFloat() * 360.0f, 0.0f);
                    Event.Result canSpawn = EventHooks.canEntitySpawn((Mob)entityliving, (Level)worldServerIn, (float)spawnX, (float)spawnY, (float)spawnZ, (boolean)false);
                    boolean canSpawnHere = entityliving.getCanSpawnHere();
                    if (canSpawn == MobDespawnEvent.Result.DENY) {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN denied by Forge canEntitySpawn for " + entityliving.getName().getString() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                    } else if (canSpawn == Event.Result.ALLOW || canSpawn == Event.Result.DEFAULT && canSpawnHere) {
                        if (!EventHooks.doSpecialSpawn((Mob)entityliving, (Level)worldServerIn, (float)spawnX, (float)spawnY, (float)spawnZ)) {
                            ientitylivingdata = entityliving.finalizeSpawn((ServerLevel) entityliving.level(), worldServerIn.getCurrentDifficultyAt(entityliving.blockPosition()), MobSpawnType.MOB_SUMMONED, ientitylivingdata);
                        }
                        if (entityliving.checkSpawnObstruction()) {
                            worldServerIn.addFreshEntity((Entity)entityliving);
                            SRPWorldEntitySpawner.applyDebugGlow((Mob)entityliving);
                            SRPWorldEntitySpawner.debugSpawn("ORIGIN spawned " + entityliving.getName().getString() + " at " + SRPWorldEntitySpawner.posToString(entityliving.blockPosition()) + " | packCount=" + ++j2 + " | glow=" + SRPConfigSystems.debugSpawner);
                        } else {
                            SRPWorldEntitySpawner.debugSpawn("ORIGIN failed collision check for " + entityliving.getName().getString() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                            entityliving.discard();
                        }
                        if (j2 >= EventHooks.getMaxSpawnPackSize((Mob)entityliving)) {
                            SRPWorldEntitySpawner.debugSpawn("ORIGIN reached max pack size for " + entityliving.getName().getString() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                            continue block5;
                        }
                    } else if (!canSpawnHere) {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN getCanSpawnHere returned false | selected=" + biome$spawnlistentry.entityClass.getName() + " | actualClass=" + entityliving.getClass().getName() + " | actualName=" + entityliving.getName().getString() + " | pos=" + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                    } else {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN spawn failed for " + entityliving.getName().getString() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " | reason=unknown DEFAULT rejection");
                    }
                    j4 += j2;
                }
            }
        }
        return j4;
    }

    private static boolean isPosWithinOrigin(Level world, BlockPos pos) {
        SRPWorldData data = SRPWorldData.get(world);
        boolean originsExist = data != null && !data.getorigins("x").isEmpty();
        SRPSaveData saveData225 = SRPSaveData.get(world);
        boolean originlessAllowed = saveData225 != null && saveData225.getDeveLevel() >= SRPConfigSystems.deveOriginlessUse;
        return SRPWorldEntitySpawner.isPosWithinOrigin(world, pos, data, originsExist, originlessAllowed);
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
                if (!SRPWorldEntitySpawner.chunkIntersectsRadius2D(chunkPos.chunkXPos, chunkPos.chunkZPos, originsX.get(i), originsZ.get(i), originsA.get(i))) continue;
                inRange = true;
                break;
            }
            if (!inRange) {
                for (i = 0; i < colonyCount; ++i) {
                    if (!SRPWorldEntitySpawner.chunkIntersectsRadius2D(chunkPos.chunkXPos, chunkPos.chunkZPos, coloniesX.get(i), coloniesZ.get(i), colonyRadii[i])) continue;
                    inRange = true;
                    break;
                }
            }
            if (inRange) continue;
            chunkIterator.remove();
        }
    }

    private static BlockPos getRandomChunkPosition(Level worldIn, int x, int z) {
        int i = x * 16 + worldIn.random.nextInt(16);
        int j = z * 16 + worldIn.random.nextInt(16);
        int minY = 1;
        int maxY = Math.max(minY + 1, worldIn.getMaxBuildHeight() - 2);
        int l = minY + worldIn.random.nextInt(maxY - minY + 1);
        return BlockPos.containing(i, l, j);
    }

    @Nullable
    public static Biome.SpawnListEntry getSpawnListEntryForTypeAt(ServerLevel worldServerIn, BlockPos pos) {
        Biome.SpawnListEntry chosen;
        SRPSaveData dat = SRPSaveData.get((Level)worldServerIn, 72);
        int id = DimKeys.of(worldServerIn);
        if (dat == null) {
            SRPWorldEntitySpawner.debugSpawn("No spawn entry: save data unavailable in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos));
            return null;
        }
        if (SRPConfigWorld.originActivated) {
            SRPWorldData data = SRPWorldData.get((Level)worldServerIn);
            boolean originsExist = data != null && !data.getorigins("x").isEmpty();
            SRPSaveData saveData225 = SRPSaveData.get((Level)worldServerIn, 225);
            boolean originlessAllowed = saveData225 != null && saveData225.getDeveLevel() >= SRPConfigSystems.deveOriginlessUse;
            return SRPWorldEntitySpawner.getSpawnListEntryForTypeAtOrigin(worldServerIn, pos, dat, data, originsExist, originlessAllowed);
        }
        List<Biome.SpawnListEntry> list = SRPSpawning.getSpawns((Level)worldServerIn, id, dat.getEvolutionPhase(id), dat);
        Biome.SpawnListEntry spawnListEntry = chosen = list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem((Random)worldServerIn.random, list) : null;
        if (chosen == null) {
            SRPWorldEntitySpawner.debugSpawn("Spawn list empty/null in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos) + " | originActivated=false | phase=" + dat.getEvolutionPhase(id));
        } else {
            SRPWorldEntitySpawner.debugSpawn("Selected spawn entry " + chosen.entityClass.getSimpleName() + " in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos) + " | originActivated=false | phase=" + dat.getEvolutionPhase(id));
        }
        return chosen;
    }

    @Nullable
    private static Biome.SpawnListEntry getSpawnListEntryForTypeAtOrigin(ServerLevel worldServerIn, BlockPos pos, SRPSaveData dat, SRPWorldData data, boolean originsExist, boolean originlessAllowed) {
        Biome.SpawnListEntry chosen;
        int id = DimKeys.of(worldServerIn);
        if (!SRPWorldEntitySpawner.isPosWithinOrigin((Level)worldServerIn, pos, data, originsExist, originlessAllowed)) {
            SRPWorldEntitySpawner.debugSpawn("ORIGIN position rejected (not within origin) at " + SRPWorldEntitySpawner.posToString(pos));
            return null;
        }
        byte phase = dat.getEvolutionPhase(id);
        if (phase == -1) {
            Biome.SpawnListEntry chosen2;
            if (!originsExist) {
                return null;
            }
            List<Biome.SpawnListEntry> list = SRPSpawning.getSpawns((Level)worldServerIn, id, phase, dat);
            Biome.SpawnListEntry spawnListEntry = chosen2 = list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem((Random)worldServerIn.random, list) : null;
            if (chosen2 == null) {
                SRPWorldEntitySpawner.debugSpawn("Spawn list empty/null in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos) + " | originActivated=true | phase=" + phase + " | outbreak=true");
            } else {
                SRPWorldEntitySpawner.debugSpawn("Selected spawn entry " + chosen2.entityClass.getSimpleName() + " in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos) + " | originActivated=true | phase=" + phase + " | outbreak=true");
            }
            return chosen2;
        }
        byte phaseToUse = originsExist ? phase : (byte)0;
        List<Biome.SpawnListEntry> list = SRPSpawning.getSpawns((Level)worldServerIn, id, phaseToUse, dat);
        Biome.SpawnListEntry spawnListEntry = chosen = list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem((Random)worldServerIn.random, list) : null;
        if (chosen == null) {
            SRPWorldEntitySpawner.debugSpawn("Spawn list empty/null in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos) + " | originActivated=true | phase=" + phaseToUse + (originsExist ? " | originsPresent=true" : " | forcedPhase=0 | noOrigins=true"));
        } else {
            SRPWorldEntitySpawner.debugSpawn("Selected spawn entry " + chosen.entityClass.getSimpleName() + " in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos) + " | originActivated=true | phase=" + phaseToUse + (originsExist ? " | originsPresent=true" : " | forcedPhase=0 | noOrigins=true"));
        }
        return chosen;
    }

    public static boolean canCreatureTypeSpawnAtLocation(Mob.SpawnPlacementType spawnPlacementTypeIn, Level worldIn, BlockPos pos) {
        return SRPWorldEntitySpawner.getSpawnFailureReason(spawnPlacementTypeIn, worldIn, pos) == null;
    }

    public static boolean canCreatureTypeSpawnBody(Mob.SpawnPlacementType spawnPlacementTypeIn, Level worldIn, BlockPos pos) {
        return SRPWorldEntitySpawner.canCreatureTypeSpawnAtLocation(spawnPlacementTypeIn, worldIn, pos);
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

    private static BlockPos findGroundSpawnPos(Level worldIn, BlockPos start) {
        BlockPos best = null;
        int bestDist = Integer.MAX_VALUE;
        int minY = Math.max(1, start.getY() - 12);
        int maxY = Math.min(worldIn.getMaxBuildHeight() - 2, start.getY() + 12);
        for (int y = minY; y <= maxY; ++y) {
            int dist;
            BlockPos candidate = BlockPos.containing(start.getX(), y, start.getZ());
            BlockPos below = candidate.below();
            BlockState belowState = worldIn.getBlockState(below);
            BlockState atState = worldIn.getBlockState(candidate);
            BlockState aboveState = worldIn.getBlockState(candidate.above());
            Block belowBlock = belowState.getBlock();
            boolean validFloor = !belowState.getMaterial().isLiquid() && belowBlock != Blocks.AIR && belowBlock != Blocks.OAK_LEAVES && belowBlock != Blocks.ACACIA_LEAVES && belowBlock != Blocks.BEDROCK && belowBlock != Blocks.BARRIER && belowBlock.canCreatureSpawn(belowState, (BlockGetter)worldIn, below, Mob.SpawnPlacementType.ON_GROUND);
            boolean openAt = SRPWorldEntitySpawner.isValidEmptySpawnBlock(atState);
            boolean openAbove = SRPWorldEntitySpawner.isValidEmptySpawnBlock(aboveState);
            if (!validFloor || !openAt || !openAbove || (dist = Math.abs(y - start.getY())) >= bestDist) continue;
            bestDist = dist;
            best = candidate;
        }
        return best != null ? best : start;
    }

    private static void applyDebugGlow(Mob entityliving) {
        if (!SRPConfigSystems.debugSpawner || entityliving == null) {
            return;
        }
        entityliving.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1200, 0, false, false));
    }

    private static BlockPos findWaterSpawnPos(Level worldIn, BlockPos start) {
        int x = start.getX();
        int z = start.getZ();
        BlockPos top = worldIn.getTopSolidOrLiquidBlock(BlockPos.containing(x, 0, z));
        int topY = Math.max(1, top.getY() + 2);
        BlockPos best = null;
        int bestDist = Integer.MAX_VALUE;
        for (int y = 1; y <= topY; ++y) {
            int dist;
            boolean valid;
            BlockPos pos = BlockPos.containing(x, y, z);
            BlockState at = worldIn.getBlockState(pos);
            BlockState below = worldIn.getBlockState(pos.below());
            BlockState above = worldIn.getBlockState(pos.above());
            boolean bl = valid = at.getMaterial() == Material.water && below.getMaterial() == Material.water && above.getMaterial() == Material.water;
            if (!valid || (dist = Math.abs(y - start.getY())) >= bestDist) continue;
            bestDist = dist;
            best = pos;
        }
        return best != null ? best : start;
    }

    private static void adjustWaterSpawnPos(Level worldIn, BlockPos.MutableBlockPos pos) {
        BlockPos adjusted = SRPWorldEntitySpawner.findWaterSpawnPos(worldIn, pos.toImmutable());
        pos.setPos((Vec3i)adjusted);
    }

    private static BlockPos findAirSpawnPos(Level worldIn, BlockPos start) {
        int x = start.getX();
        int z = start.getZ();
        int topY = worldIn.getTopSolidOrLiquidBlock(BlockPos.containing(x, 0, z)).getY();
        int minY = Math.max(2, topY + 8);
        int maxY = Math.min(SRPConfigWorld.spawnerSKYLimitUp, worldIn.getMaxBuildHeight() - 2);
        if (maxY < minY) {
            return start;
        }
        ArrayList<BlockPos> validPositions = new ArrayList<BlockPos>();
        for (int y = minY; y <= maxY; ++y) {
            boolean valid;
            BlockPos pos = BlockPos.containing(x, y, z);
            BlockState at = worldIn.getBlockState(pos);
            boolean bl = valid = at.getBlock() == Blocks.AIR && worldIn.getBlockState(pos.above()).getBlock() == Blocks.AIR && worldIn.getBlockState(pos.below()).getBlock() == Blocks.AIR;
            if (!valid) continue;
            validPositions.add(pos);
        }
        if (validPositions.isEmpty()) {
            return start;
        }
        return (BlockPos)validPositions.get(worldIn.random.nextInt(validPositions.size()));
    }
}

