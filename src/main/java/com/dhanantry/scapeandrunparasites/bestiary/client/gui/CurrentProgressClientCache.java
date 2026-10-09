package com.dhanantry.scapeandrunparasites.bestiary.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import net.minecraft.nbt.CompoundTag;

public final class CurrentProgressClientCache {
    public static boolean hasData = false;
    public static String dimension = "";
    public static int phase = 0;
    public static int udl = 0;
    public static boolean phaseUnlocked = false;
    public static boolean udUnlocked = false;
    public static double phasePassiveGain = 0.0;
    public static double phaseReinforcementChance = 0.0;
    public static double phaseCothSpawnChance = 0.0;
    public static double phaseCropStuntChance = 0.0;
    public static int phaseSleepPenalty = 0;
    public static double beckonStageIGrowPenalty = 1.0;
    public static double beckonStageIIGrowPenalty = 1.0;
    public static double beckonStageIIIGrowPenalty = 1.0;
    public static int udDislodgmentLevel = 1;
    public static int udMergeLevel = 1;
    public static int udCollectiveConsciousnessLevel = 2;
    public static int udScentLevel = 2;
    public static int udVectorlessLevel = 2;
    public static int udNestsLevel = 3;
    public static int udVariantsLevel = 3;
    public static int udColoniesLevel = 4;
    public static int udHivesLevel = 4;
    public static int udNodesLevel = 4;
    public static double udMobChance = 0.5;

    private CurrentProgressClientCache() {
    }

    public static void read(CompoundTag tag) {
        if (tag == null) {
            return;
        }
        dimension = tag.getString("dimension");
        phase = tag.getInt("phase");
        udl = tag.getInt("udl");
        phaseUnlocked = tag.getBoolean("phaseUnlocked");
        udUnlocked = tag.getBoolean("udUnlocked");
        phasePassiveGain = tag.getDouble("phasePassiveGain");
        phaseReinforcementChance = tag.getDouble("phaseReinforcementChance");
        phaseCothSpawnChance = tag.getDouble("phaseCothSpawnChance");
        phaseCropStuntChance = tag.getDouble("phaseCropStuntChance");
        phaseSleepPenalty = tag.getInt("phaseSleepPenalty");
        beckonStageIGrowPenalty = tag.getDouble("beckonStageIGrowPenalty");
        beckonStageIIGrowPenalty = tag.getDouble("beckonStageIIGrowPenalty");
        beckonStageIIIGrowPenalty = tag.getDouble("beckonStageIIIGrowPenalty");
        udDislodgmentLevel = tag.getInt("udDislodgmentLevel");
        udMergeLevel = tag.getInt("udMergeLevel");
        udCollectiveConsciousnessLevel = tag.getInt("udCollectiveConsciousnessLevel");
        udScentLevel = tag.getInt("udScentLevel");
        udVectorlessLevel = tag.getInt("udVectorlessLevel");
        udNestsLevel = tag.getInt("udNestsLevel");
        udVariantsLevel = tag.getInt("udVariantsLevel");
        udColoniesLevel = tag.getInt("udColoniesLevel");
        udHivesLevel = tag.getInt("udHivesLevel");
        udNodesLevel = tag.getInt("udNodesLevel");
        udMobChance = tag.getDouble("udMobChance");
        hasData = true;
    }
}

