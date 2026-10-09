package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** The numbers shown by the "Current Progress" page of the compendium (SRPProgressSnapshot of 1.10.9). */
public final class SRPProgressSnapshot {
    public String dimension;
    public int phase;
    public int udl;
    public boolean phaseUnlocked;
    public boolean udUnlocked;
    public double phasePassiveGain;
    public double phaseReinforcementChance;
    public double phaseCothSpawnChance;
    public double phaseCropStuntChance;
    public int phaseSleepPenalty;
    public double beckonStageIGrowPenalty;
    public double beckonStageIIGrowPenalty;
    public double beckonStageIIIGrowPenalty;
    public int udDislodgmentLevel;
    public int udMergeLevel;
    public int udCollectiveConsciousnessLevel;
    public int udScentLevel;
    public int udVectorlessLevel;
    public int udNestsLevel;
    public int udVariantsLevel;
    public int udColoniesLevel;
    public int udHivesLevel;
    public int udNodesLevel;
    public double udMobChance;

    private SRPProgressSnapshot() {
    }

    public static SRPProgressSnapshot collect(Level world, Player player) {
        SRPProgressSnapshot out = new SRPProgressSnapshot();
        String dim = DimKeys.of(world);
        SRPSaveData data = SRPSaveData.get(world);
        out.dimension = dim;
        out.phase = data == null ? 0 : (int)data.getEvolutionPhase(dim);
        out.udl = data == null ? 0 : data.getDeveLevel();
        IBestiaryProgress prog = player == null ? null : BestiaryCapability.get(player);
        out.phaseUnlocked = prog != null && prog.hasUnlockedProgressPhase();
        out.udUnlocked = prog != null && prog.hasUnlockedProgressUD();
        out.phasePassiveGain = SRPProgressSnapshot.getPhasePassiveGain(out.phase);
        out.phaseReinforcementChance = SRPProgressSnapshot.getPhaseReinforcementChance(out.phase);
        out.phaseCothSpawnChance = SRPProgressSnapshot.getPhaseCothSpawnChance(out.phase);
        out.phaseCropStuntChance = SRPProgressSnapshot.getPhaseCropStuntChance(out.phase);
        out.phaseSleepPenalty = SRPProgressSnapshot.getPhaseSleepPenalty(out.phase);
        out.beckonStageIGrowPenalty = SRPProgressSnapshot.getBeckonStageIGrowPenalty(out.phase);
        out.beckonStageIIGrowPenalty = SRPProgressSnapshot.getBeckonStageIIGrowPenalty(out.phase);
        out.beckonStageIIIGrowPenalty = SRPProgressSnapshot.getBeckonStageIIIGrowPenalty(out.phase);
        out.udDislodgmentLevel = SRPConfigSystems.deveDisloUse;
        out.udMergeLevel = SRPConfigSystems.deveMergeUse;
        out.udCollectiveConsciousnessLevel = SRPConfigSystems.deveOnemindUse;
        out.udScentLevel = SRPConfigSystems.deveScentUse;
        out.udVectorlessLevel = SRPConfigSystems.deveOriginlessUse;
        out.udNestsLevel = SRPConfigSystems.deveNestsUse;
        out.udVariantsLevel = SRPConfigSystems.deveAlwaysVariantUse;
        out.udColoniesLevel = SRPConfigSystems.deveColoniesUse;
        out.udHivesLevel = SRPConfigSystems.deveHivesUse;
        out.udNodesLevel = SRPConfigSystems.deveNodesUse;
        out.udMobChance = SRPConfigSystems.deveMobChance;
        return out;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("dimension", this.dimension == null ? "" : this.dimension);
        tag.putInt("phase", this.phase);
        tag.putInt("udl", this.udl);
        tag.putBoolean("phaseUnlocked", this.phaseUnlocked);
        tag.putBoolean("udUnlocked", this.udUnlocked);
        tag.putDouble("phasePassiveGain", this.phasePassiveGain);
        tag.putDouble("phaseReinforcementChance", this.phaseReinforcementChance);
        tag.putDouble("phaseCothSpawnChance", this.phaseCothSpawnChance);
        tag.putDouble("phaseCropStuntChance", this.phaseCropStuntChance);
        tag.putInt("phaseSleepPenalty", this.phaseSleepPenalty);
        tag.putDouble("beckonStageIGrowPenalty", this.beckonStageIGrowPenalty);
        tag.putDouble("beckonStageIIGrowPenalty", this.beckonStageIIGrowPenalty);
        tag.putDouble("beckonStageIIIGrowPenalty", this.beckonStageIIIGrowPenalty);
        tag.putInt("udDislodgmentLevel", this.udDislodgmentLevel);
        tag.putInt("udMergeLevel", this.udMergeLevel);
        tag.putInt("udCollectiveConsciousnessLevel", this.udCollectiveConsciousnessLevel);
        tag.putInt("udScentLevel", this.udScentLevel);
        tag.putInt("udVectorlessLevel", this.udVectorlessLevel);
        tag.putInt("udNestsLevel", this.udNestsLevel);
        tag.putInt("udVariantsLevel", this.udVariantsLevel);
        tag.putInt("udColoniesLevel", this.udColoniesLevel);
        tag.putInt("udHivesLevel", this.udHivesLevel);
        tag.putInt("udNodesLevel", this.udNodesLevel);
        tag.putDouble("udMobChance", this.udMobChance);
        return tag;
    }

    private static double getPhasePassiveGain(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.phaseKillCountPlusOne;
            }
            case 2: {
                return SRPConfigSystems.phaseKillCountPlusTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseKillCountPlusThree;
            }
            case 4: {
                return SRPConfigSystems.phaseKillCountPlusFour;
            }
            case 5: {
                return SRPConfigSystems.phaseKillCountPlusFive;
            }
            case 6: {
                return SRPConfigSystems.phaseKillCountPlusSix;
            }
            case 7: {
                return SRPConfigSystems.phaseKillCountPlusSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseKillCountPlusEight;
            }
            case 9: {
                return SRPConfigSystems.phaseKillCountPlusNine;
            }
            case 10: {
                return SRPConfigSystems.phaseKillCountPlusTen;
            }
        }
        return 0.0;
    }

    private static double getPhaseReinforcementChance(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.reinforcementSystemChanceOne;
            }
            case 2: {
                return SRPConfigSystems.reinforcementSystemChanceTwo;
            }
            case 3: {
                return SRPConfigSystems.reinforcementSystemChanceThree;
            }
            case 4: {
                return SRPConfigSystems.reinforcementSystemChanceFour;
            }
            case 5: {
                return SRPConfigSystems.reinforcementSystemChanceFive;
            }
            case 6: {
                return SRPConfigSystems.reinforcementSystemChanceSix;
            }
            case 7: {
                return SRPConfigSystems.reinforcementSystemChanceSeven;
            }
            case 8: {
                return SRPConfigSystems.reinforcementSystemChanceEight;
            }
            case 9: {
                return SRPConfigSystems.reinforcementSystemChanceNine;
            }
            case 10: {
                return SRPConfigSystems.reinforcementSystemChanceTen;
            }
        }
        return 0.0;
    }

    private static double getPhaseCothSpawnChance(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.mobSpawningCOTHChanceOne;
            }
            case 2: {
                return SRPConfigSystems.mobSpawningCOTHChanceTwo;
            }
            case 3: {
                return SRPConfigSystems.mobSpawningCOTHChanceThree;
            }
            case 4: {
                return SRPConfigSystems.mobSpawningCOTHChanceFour;
            }
            case 5: {
                return SRPConfigSystems.mobSpawningCOTHChanceFive;
            }
            case 6: {
                return SRPConfigSystems.mobSpawningCOTHChanceSix;
            }
            case 7: {
                return SRPConfigSystems.mobSpawningCOTHChanceSeven;
            }
            case 8: {
                return SRPConfigSystems.mobSpawningCOTHChanceEight;
            }
            case 9: {
                return SRPConfigSystems.mobSpawningCOTHChanceNine;
            }
            case 10: {
                return SRPConfigSystems.mobSpawningCOTHChanceTen;
            }
        }
        return 0.0;
    }

    private static double getPhaseCropStuntChance(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.cropGrowStunnedOne;
            }
            case 2: {
                return SRPConfigSystems.cropGrowStunnedTwo;
            }
            case 3: {
                return SRPConfigSystems.cropGrowStunnedThree;
            }
            case 4: {
                return SRPConfigSystems.cropGrowStunnedFour;
            }
            case 5: {
                return SRPConfigSystems.cropGrowStunnedFive;
            }
            case 6: {
                return SRPConfigSystems.cropGrowStunnedSix;
            }
            case 7: {
                return SRPConfigSystems.cropGrowStunnedSeven;
            }
            case 8: {
                return SRPConfigSystems.cropGrowStunnedEight;
            }
            case 9: {
                return SRPConfigSystems.cropGrowStunnedNine;
            }
            case 10: {
                return SRPConfigSystems.cropGrowStunnedTen;
            }
        }
        return 0.0;
    }

    private static int getPhaseSleepPenalty(int phase) {
        switch (phase) {
            case 0: {
                return SRPConfigSystems.sleepPenaltyZero;
            }
            case 1: {
                return SRPConfigSystems.sleepPenaltyOne;
            }
            case 2: {
                return SRPConfigSystems.sleepPenaltyTwo;
            }
            case 3: {
                return SRPConfigSystems.sleepPenaltyThree;
            }
            case 4: {
                return SRPConfigSystems.sleepPenaltyFour;
            }
            case 5: {
                return SRPConfigSystems.sleepPenaltyFive;
            }
            case 6: {
                return SRPConfigSystems.sleepPenaltySix;
            }
            case 7: {
                return SRPConfigSystems.sleepPenaltySeven;
            }
            case 8: {
                return SRPConfigSystems.sleepPenaltyEight;
            }
            case 9: {
                return SRPConfigSystems.sleepPenaltyNine;
            }
            case 10: {
                return SRPConfigSystems.sleepPenaltyTen;
            }
        }
        return 0;
    }

    private static double getBeckonStageIGrowPenalty(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.beckonStageIGrowPenaltyOne;
            }
            case 2: {
                return SRPConfigSystems.beckonStageIGrowPenaltyTwo;
            }
            case 3: {
                return SRPConfigSystems.beckonStageIGrowPenaltyThree;
            }
            case 4: {
                return SRPConfigSystems.beckonStageIGrowPenaltyFour;
            }
            case 5: {
                return SRPConfigSystems.beckonStageIGrowPenaltyFive;
            }
            case 6: {
                return SRPConfigSystems.beckonStageIGrowPenaltySix;
            }
            case 7: {
                return SRPConfigSystems.beckonStageIGrowPenaltySeven;
            }
            case 8: {
                return SRPConfigSystems.beckonStageIGrowPenaltyEight;
            }
            case 9: {
                return SRPConfigSystems.beckonStageIGrowPenaltyNine;
            }
            case 10: {
                return SRPConfigSystems.beckonStageIGrowPenaltyTen;
            }
        }
        return 1.0;
    }

    private static double getBeckonStageIIGrowPenalty(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyOne;
            }
            case 2: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyTwo;
            }
            case 3: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyThree;
            }
            case 4: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyFour;
            }
            case 5: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyFive;
            }
            case 6: {
                return SRPConfigSystems.beckonStageIIGrowPenaltySix;
            }
            case 7: {
                return SRPConfigSystems.beckonStageIIGrowPenaltySeven;
            }
            case 8: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyEight;
            }
            case 9: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyNine;
            }
            case 10: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyTen;
            }
        }
        return 1.0;
    }

    private static double getBeckonStageIIIGrowPenalty(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyOne;
            }
            case 2: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyTwo;
            }
            case 3: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyThree;
            }
            case 4: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyFour;
            }
            case 5: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyFive;
            }
            case 6: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltySix;
            }
            case 7: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltySeven;
            }
            case 8: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyEight;
            }
            case 9: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyNine;
            }
            case 10: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyTen;
            }
        }
        return 1.0;
    }
}
