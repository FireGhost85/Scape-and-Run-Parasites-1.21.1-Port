package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.phase.PhaseConfig;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyCore;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNodeCore;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ParasiteEventWorld {
    public static int disloCool;

    public static boolean blockException(Level worldIn, BlockPos pos, Block block, BlockState state, String[] list, boolean invert, float maxHardness) {
        float bHard = state.getDestroySpeed(worldIn, pos);
        if (block instanceof HalfTransparentBlock && LegacyMaterial.of(state) == LegacyMaterial.ice) {
            return false;
        }
        if (bHard > maxHardness || bHard < 0.0f) {
            return true;
        }
        if (ParasiteEventEntity.checkName(block.builtInRegistryHolder().key().location().toString(), list, invert)) {
            return true;
        }
        return block instanceof HalfTransparentBlock || block instanceof BaseEntityBlock || block instanceof DropExperienceBlock || block instanceof HorizontalDirectionalBlock || block instanceof TntBlock || block.defaultBlockState().getCollisionShape(worldIn, pos).isEmpty() || block instanceof net.minecraft.world.level.block.BushBlock || !state.isCollisionShapeFullBlock(worldIn, pos);
    }

    public static int canBiomeStillExist(Level worldIn, BlockPos pos, boolean spread) {
        if (!SRPConfigWorld.nodesActivated || !SRPConfigWorld.biomeRegster) {
            return -1;
        }
        return SRPWorldData.get(worldIn).nearestHeartAge(pos, spread, 0);
    }

    public static int canBiomeStillExistType(Level worldIn, BlockPos pos, boolean spread) {
        if (!SRPConfigWorld.nodesActivated || !SRPConfigWorld.biomeRegster) {
            return -1;
        }
        return SRPWorldData.get(worldIn).nearestHeartType(pos, spread, 0);
    }

    public static int placeHeartInWorld(Level worldIn, BlockPos pos, int type) {
        type = 0;
        if (!SRPConfigWorld.nodesActivated) {
            return 3;
        }
        if (!SRPConfigWorld.biomeRegster) {
            return 4;
        }
        if (!ParasiteEventWorld.chechBlackListNodes(worldIn)) {
            return 2;
        }
        BlockPos origin = worldIn.getSharedSpawnPos();
        if (ParasiteEventWorld.getDistanceSQ(origin.getX(), origin.getY(), origin.getZ(), pos.getX(), pos.getY(), pos.getZ()) < (double)(SRPConfigWorld.minimumDistanceFromSpawnPoint * SRPConfigWorld.minimumDistanceFromSpawnPoint)) {
            return 5;
        }
        SRPWorldData data = SRPWorldData.get(worldIn);
        SRPSaveData dataS = SRPSaveData.get(worldIn);
        if (SRPConfigSystems.useEvolution) {
            if (dataS.getEvolutionPhase(DimKeys.of(worldIn)) < SRPConfigSystems.evolutionNodeUnlock && dataS.getDeveLevel() < SRPConfigSystems.deveNodesUse) {
                return 6;
            }
        } else if (!SRPConfigWorld.venkrolNode) {
            return 10;
        }
        if ((pos = ParasiteEventEntity.getFloor(worldIn, pos, 100)) == null) {
            return 7;
        }
        int key = data.setNode(pos.getX(), pos.getY(), pos.getZ(), type);
        if (key == 1) {
            WorldGenParasiteNodeCore gen = new WorldGenParasiteNodeCore(false, 1, type);
            gen.generate(worldIn, worldIn.random, pos);
            BlockParasiteSpreading.SpreadBiome(worldIn, pos, 1, type);
            ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigWorld.nodeWarning, 100);
            return 1;
        }
        return key;
    }

    public static double getDistanceSQ(double rootx, double rooty, double rootz, double standingx, double standingy, double standingz) {
        double d0 = rootx - standingx;
        double d1 = rooty - standingy;
        double d2 = rootz - standingz;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    private static boolean chechBlackListNodes(Level worldIn) {
        for (String i : SRPConfigWorld.blackListedDimensionsNodes) {
            if (!DimKeys.normalize(i).equals(DimKeys.of(worldIn))) continue;
            return true;
        }
        return false;
    }

    public static boolean removeHeartInWorld(Level worldIn, BlockPos pos) {
        if (!SRPConfigWorld.nodesActivated || !SRPConfigWorld.biomeRegster) {
            return false;
        }
        SRPWorldData data = SRPWorldData.get(worldIn);
        if (data.removeNode(pos.getX(), pos.getY(), pos.getZ())) {
            ParasiteEventWorld.setDisloWorldPhase(worldIn, SRPAttributes.EVENTPARANODEC, SRPConfigSystems.chanceEventParaNodeC, 0, null);
            return true;
        }
        return false;
    }

    public static int getHeartAgePostion(Level worldIn, BlockPos pos) {
        SRPWorldData data = SRPWorldData.get(worldIn);
        return data.getHeartPocition(pos, 0);
    }

    public static int nodesPoints(Level worldIn) {
        SRPWorldData data = SRPWorldData.get(worldIn);
        return data.totalNodePoints(0);
    }

    private static double getDistanceSq(BlockPos pos, Entity entityIn) {
        double d0 = (double)pos.getX() - entityIn.getX();
        double d1 = (double)pos.getY() - entityIn.getY();
        double d2 = (double)pos.getZ() - entityIn.getZ();
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public static void checkNodeStatus(Level worldIn) {
        SRPWorldData data = SRPWorldData.get(worldIn);
        data.checkHeartExistance(worldIn);
    }

    public static int placeColonyInWorld(Level worldIn, BlockPos pos) {
        if (!SRPConfigWorld.coloniesActivated) {
            return 3;
        }
        if (!ParasiteEventWorld.chechBlackListColonies(worldIn)) {
            return 2;
        }
        SRPWorldData data = SRPWorldData.get(worldIn);
        SRPSaveData dataS = SRPSaveData.get(worldIn);
        if (SRPConfigSystems.useEvolution) {
            if (dataS.getEvolutionPhase(DimKeys.of(worldIn)) < SRPConfigSystems.evolutionColonyUnlock && dataS.getDeveLevel() < SRPConfigSystems.deveColoniesUse) {
                return 4;
            }
        } else if (!SRPConfigWorld.dodColony) {
            return 6;
        }
        int newX = ParasiteEventWorld.findNumberMultipleOf(pos.getX(), 26);
        int newZ = ParasiteEventWorld.findNumberMultipleOf(pos.getZ(), 26);
        BlockPos newPos = BlockPos.containing(newX, pos.getY(), newZ);
        if ((newPos = ParasiteEventEntity.getFloor(worldIn, newPos, 100)) == null) {
            return 5;
        }
        int key = data.setColony(newPos.getX(), newPos.getY(), newPos.getZ());
        if (key == 1) {
            WorldGenParasiteColonyCore gen = new WorldGenParasiteColonyCore(false, 1);
            gen.generate(worldIn, worldIn.random, newPos);
            ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigWorld.colonyWarning, 101);
            return 1;
        }
        return key;
    }

    private static boolean chechBlackListColonies(Level worldIn) {
        for (String i : SRPConfigWorld.blackListedDimensionsColonies) {
            if (!DimKeys.normalize(i).equals(DimKeys.of(worldIn))) continue;
            return true;
        }
        return false;
    }

    private static int findNumberMultipleOf(int n, int x) {
        if (x > n) {
            // empty if block
        }
        boolean neg = false;
        if (n < 0) {
            n *= -1;
            neg = true;
        }
        n += x / 2;
        n -= n % x;
        if (neg) {
            n *= -1;
        }
        return n;
    }

    public static boolean removeColonyInWorld(Level worldIn, BlockPos pos) {
        if (!SRPConfigWorld.coloniesActivated) {
            return false;
        }
        SRPWorldData data = SRPWorldData.get(worldIn);
        if (data.removeColony(pos.getX(), pos.getY(), pos.getZ())) {
            data.resetGlobalAdaptation();
            ParasiteEventWorld.setDisloWorldPhase(worldIn, SRPAttributes.EVENTPARACOLONYC, SRPConfigSystems.chanceEventParaColonyC, 0, null);
            return true;
        }
        return false;
    }

    public static BlockPos rangeOfColony(Level worldIn, BlockPos pos, boolean effect) {
        return SRPWorldData.get(worldIn).nearestColonyPosition(pos, effect);
    }

    public static int numberofColonies(Level worldIn) {
        return SRPWorldData.get(worldIn).colonunumber();
    }

    public static int spreadOfColony(Level worldIn, BlockPos pos) {
        return SRPWorldData.get(worldIn).getColonyDistanceSpreadByPosition(pos, false);
    }

    public static void checkColonyStatus(Level worldIn) {
        SRPWorldData data = SRPWorldData.get(worldIn);
        data.checkColonyExistance(worldIn);
    }

    public static int placeOriginInWorld(Level worldIn, BlockPos pos, int health, int radius) {
        if (!SRPConfigWorld.originActivated) {
            return 3;
        }
        SRPWorldData data = SRPWorldData.get(worldIn);
        int key = data.setOrigin(worldIn, pos.getX(), pos.getY(), pos.getZ(), health *= PhaseConfig.getVectorHealthBonus(SRPSaveData.get(worldIn).getEvolutionPhase(DimKeys.of(worldIn))), radius);
        Player nearestPlayer = worldIn.getNearestPlayer((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), -1.0, false);
        if (nearestPlayer != null) {
            double horizontalDistance = Math.sqrt(Math.pow((double)pos.getX() - nearestPlayer.getX(), 2.0) + Math.pow((double)pos.getZ() - nearestPlayer.getZ(), 2.0));
            ScapeAndRunParasites.LOGGER.debug("[EIV DEBUG] placeOriginInWorld called. pos={} health={} radius={} resultKey={} nearestPlayer={} playerPos={} horizontalDistance={} trueDistance={} totalOrigins={}", pos, health, radius, key, nearestPlayer.getName().getString(), nearestPlayer.blockPosition(), String.format("%.2f", horizontalDistance), String.format("%.2f", Math.sqrt(nearestPlayer.distanceToSqr((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()))), data.getorigins("x").size());
        } else {
            ScapeAndRunParasites.LOGGER.debug("[EIV DEBUG] placeOriginInWorld called. pos={} health={} radius={} resultKey={} no nearest player found. totalOrigins={}", pos, health, radius, key, data.getorigins("x").size());
        }
        if (key == 1) {
            if (SRPConfigWorld.originNewMess.length() > 0) {
                ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigWorld.originNewMess, 400);
            } else {
                ParasiteEventEntity.alertAllPlayerDim(worldIn, "", 400);
            }
        } else if (key == 2) {
            if (SRPConfigWorld.originNewOutbreakMess.length() > 0) {
                ParasiteEventEntity.alertAllPlayerSer(worldIn, SRPConfigWorld.originNewOutbreakMess, 401);
            } else {
                ParasiteEventEntity.alertAllPlayerSer(worldIn, "", 401);
            }
        }
        return key;
    }

    public static boolean removeOriginInWorld(Level worldIn, BlockPos pos) {
        if (!SRPConfigWorld.originActivated) {
            return false;
        }
        SRPWorldData data = SRPWorldData.get(worldIn);
        pos = data.nearestInfectionPosition(false, pos);
        if (pos == null) {
            return false;
        }
        if (data.removeOrigin(pos.getX(), pos.getY(), pos.getZ(), worldIn)) {
            if (DimKeys.of(worldIn).equals(DimKeys.normalize("-1"))) {
                if (SRPConfigWorld.originGoneOB.length() > 0) {
                    ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigWorld.originGoneOB, 402);
                }
            } else if (SRPConfigWorld.originGone.length() > 0) {
                ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigWorld.originGone, 402);
            }
            return true;
        }
        return false;
    }

    public static boolean setOriginInHealth(Level worldIn, BlockPos pos, int amount, boolean plus) {
        if (pos == null || amount == 0) {
            return false;
        }
        SRPWorldData data = SRPWorldData.get(worldIn);
        return data.setOriginHealth(worldIn, pos, amount, plus);
    }

    public static void setDisloWorldPhase(Level world, ArrayList<Byte> disloEvent, double chance, int cothCheck, BlockPos pos) {
        String dim;
        byte phase;
        byte[] disloEve;
        if (world.isClientSide) {
            return;
        }
        if (disloCool > 0) {
            return;
        }
        SRPSaveData data = SRPSaveData.get(world);
        if (data.getEvolutionPhase(DimKeys.of(world)) < SRPConfigSystems.evolutionDislodgment && data.getDeveLevel() < SRPConfigSystems.deveDisloUse) {
            return;
        }
        if (world.random.nextDouble() > chance) {
            return;
        }
        if (disloEvent.size() == 0) {
            return;
        }
        if (cothCheck > 1 && pos != null) {
            int coth = 0;
            List<? extends LivingEntity> moblist = world.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(5.0, 3.0, 5.0));
            for (LivingEntity mob : moblist) {
                if (!mob.hasEffect(SRPPotions.COTH_E)) continue;
                ++coth;
            }
            if (coth < cothCheck) {
                return;
            }
        }
        if ((disloEve = PhaseConfig.getDisloPhase(phase = data.getEvolutionPhase(dim = DimKeys.of(world)))) == null) {
            return;
        }
        ArrayList<Byte> halo = new ArrayList<Byte>();
        for (int i = 0; i < disloEve.length; ++i) {
            for (int k = 0; k < disloEvent.size(); ++k) {
                if (disloEve[i] != disloEvent.get(k)) continue;
                halo.add(disloEvent.get(k));
            }
        }
        if (halo.size() == 0) {
            return;
        }
        boolean looop = false;
        for (int gggg = 10; gggg > 0 && !looop; --gggg) {
            byte dislo = (Byte)halo.get(world.random.nextInt(halo.size()));
            int cost = (int)((double)PhaseConfig.getDisloPointPrice(dislo) * PhaseConfig.getDisloPhaseCost(phase));
            int duration = (int)((double)PhaseConfig.getDisloDuration(dislo) * PhaseConfig.getDisloPhaseDuration(phase));
            int value = (int)((double)PhaseConfig.getDisloValue(dislo) * PhaseConfig.getDisloPhaseValue(phase));
            looop = data.setCurrentCode(DimKeys.of(world), dislo, value, duration, world, true, cost);
        }
        disloCool = SRPConfigSystems.disloGlobalCooldown;
    }
}

