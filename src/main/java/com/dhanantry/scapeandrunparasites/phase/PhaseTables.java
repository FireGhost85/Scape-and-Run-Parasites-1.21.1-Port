package com.dhanantry.scapeandrunparasites.phase;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;

/** Static tables copied verbatim from the 1.12.2 command classes (data only). */
public final class PhaseTables {
    private PhaseTables() {}

    public static byte[] getDisloPhase(int id) {
        switch (id) {
            case 1: {
                return SRPConfigSystems.disloPhaseOne;
            }
            case 2: {
                return SRPConfigSystems.disloPhaseTwo;
            }
            case 3: {
                return SRPConfigSystems.disloPhaseThree;
            }
            case 4: {
                return SRPConfigSystems.disloPhaseFour;
            }
            case 5: {
                return SRPConfigSystems.disloPhaseFive;
            }
            case 6: {
                return SRPConfigSystems.disloPhaseSix;
            }
            case 7: {
                return SRPConfigSystems.disloPhaseSeven;
            }
            case 8: {
                return SRPConfigSystems.disloPhaseEight;
            }
            case 9: {
                return SRPConfigSystems.disloPhaseNine;
            }
            case 10: {
                return SRPConfigSystems.disloPhaseTen;
            }
        }
        return null;
    }

    public static int getDisloPointPrice(byte in) {
        switch (in) {
            case 0: {
                return SRPConfigSystems.disloCOTHIgnoreAmpPrice;
            }
            case 1: {
                return SRPConfigSystems.disloCOTHTiersPrice;
            }
            case 2: {
                return SRPConfigSystems.disloSummonByDeathPrice;
            }
            case 3: {
                return SRPConfigSystems.disloPotiEffPrice;
            }
            case 4: {
                return SRPConfigSystems.dislostatsPrice;
            }
            case 5: {
                return SRPConfigSystems.disloDeathRaidPrice;
            }
            case 6: {
                return SRPConfigSystems.disloItemDuraPrice;
            }
            case 7: {
                return SRPConfigSystems.disloHealingDeathPrice;
            }
            case 8: {
                return SRPConfigSystems.disloDamageDeathPrice;
            }
            case 9: {
                return SRPConfigSystems.disloFoodDeathPrice;
            }
            case 10: {
                return SRPConfigSystems.disloDeathHighVerionsPrice;
            }
            case 11: {
                return SRPConfigSystems.disloParasiteNoPotionPrice;
            }
            case 12: {
                return SRPConfigSystems.disloHealthDrainingPrice;
            }
            case 13: {
                return SRPConfigSystems.disloFoodDrainingPrice;
            }
            case 14: {
                return SRPConfigSystems.disloNextPhaseLPrice;
            }
            case 15: {
                return SRPConfigSystems.disloGrowlNoisePrice;
            }
            case 16: {
                return SRPConfigSystems.disloWalkNoisePrice;
            }
            case 17: {
                return SRPConfigSystems.disloShieldFoodPrice;
            }
            case 18: {
                return SRPConfigSystems.disloLootXpCancPrice;
            }
            case 19: {
                return SRPConfigSystems.disloKillcountIncPrice;
            }
            case 20: {
                return SRPConfigSystems.disloGiveBodiesPrice;
            }
            case 21: {
                return SRPConfigSystems.disloBurningDeathPrice;
            }
            case 22: {
                return SRPConfigSystems.disloSameVersionDyeingPrice;
            }
            case 23: {
                return SRPConfigSystems.disloColonyNoLimitPrice;
            }
            case 24: {
                return SRPConfigSystems.disloNexusGrowthPrice;
            }
            case 25: {
                return SRPConfigSystems.disloParasiteBlockPrice;
            }
        }
        return 0;
    }

    public static int getDisloDuration(byte in) {
        switch (in) {
            case 0: {
                return SRPConfigSystems.disloCOTHIgnoreAmpDuration;
            }
            case 1: {
                return SRPConfigSystems.disloCOTHTiersDuration;
            }
            case 2: {
                return SRPConfigSystems.disloSummonByDeathDuration;
            }
            case 3: {
                return SRPConfigSystems.disloPotiEffDuration;
            }
            case 4: {
                return SRPConfigSystems.dislostatsDuration;
            }
            case 5: {
                return SRPConfigSystems.disloDeathRaidDuration;
            }
            case 6: {
                return SRPConfigSystems.disloItemDuraDuration;
            }
            case 7: {
                return SRPConfigSystems.disloHealingDeathDuration;
            }
            case 8: {
                return SRPConfigSystems.disloDamageDeathDuration;
            }
            case 9: {
                return SRPConfigSystems.disloFoodDeathDuration;
            }
            case 10: {
                return SRPConfigSystems.disloDeathHighVerionsDuration;
            }
            case 11: {
                return SRPConfigSystems.disloParasiteNoPotionDuration;
            }
            case 12: {
                return SRPConfigSystems.disloHealthDrainingDuration;
            }
            case 13: {
                return SRPConfigSystems.disloFoodDrainingDuration;
            }
            case 14: {
                return SRPConfigSystems.disloNextPhaseLDuration;
            }
            case 15: {
                return SRPConfigSystems.disloGrowlNoiseDuration;
            }
            case 16: {
                return SRPConfigSystems.disloWalkNoiseDuration;
            }
            case 17: {
                return SRPConfigSystems.disloShieldFoodDuration;
            }
            case 18: {
                return SRPConfigSystems.disloLootXpCancDuration;
            }
            case 19: {
                return SRPConfigSystems.disloKillcountIncDuration;
            }
            case 20: {
                return SRPConfigSystems.disloGiveBodiesDuration;
            }
            case 21: {
                return SRPConfigSystems.disloBurningDeathDuration;
            }
            case 22: {
                return SRPConfigSystems.disloSameVersionDyeingDuration;
            }
            case 23: {
                return SRPConfigSystems.disloColonyNoLimitDuration;
            }
            case 24: {
                return SRPConfigSystems.disloNexusGrowthDuration;
            }
            case 25: {
                return SRPConfigSystems.disloParasiteBlockDuration;
            }
        }
        return 0;
    }

    public static int getDisloValue(byte in) {
        switch (in) {
            case 0: {
                return 1;
            }
            case 1: {
                return SRPConfigSystems.disloCOTHTiersValue;
            }
            case 2: {
                return SRPConfigSystems.disloSummonByDeathValue;
            }
            case 3: {
                return SRPConfigSystems.disloPotiEffValue;
            }
            case 4: {
                return SRPConfigSystems.dislostatsValue;
            }
            case 5: {
                return SRPConfigSystems.disloDeathRaidValue;
            }
            case 6: {
                return SRPConfigSystems.disloItemDuraValue;
            }
            case 7: {
                return SRPConfigSystems.disloHealingDeathValue;
            }
            case 8: {
                return SRPConfigSystems.disloDamageDeathValue;
            }
            case 9: {
                return SRPConfigSystems.disloFoodDeathValue;
            }
            case 10: {
                return SRPConfigSystems.disloDeathHighVerionsValue;
            }
            case 11: {
                return 1;
            }
            case 12: {
                return SRPConfigSystems.disloHealthDrainingValue;
            }
            case 13: {
                return SRPConfigSystems.disloFoodDrainingValue;
            }
            case 14: {
                return SRPConfigSystems.disloNextPhaseLValue;
            }
            case 15: {
                return 1;
            }
            case 16: {
                return 1;
            }
            case 17: {
                return 1;
            }
            case 18: {
                return 1;
            }
            case 19: {
                return SRPConfigSystems.disloKillcountIncValue;
            }
            case 20: {
                return 1;
            }
            case 21: {
                return 1;
            }
            case 22: {
                return SRPConfigSystems.disloSameVersionDyeingValue;
            }
            case 23: {
                return SRPConfigSystems.disloColonyNoLimitValue;
            }
            case 24: {
                return SRPConfigSystems.disloNexusGrowthValue;
            }
            case 25: {
                return SRPConfigSystems.disloParasiteBlockValue;
            }
        }
        return 0;
    }

    public static double getDisloPhaseDuration(byte in) {
        switch (in) {
            case 1: {
                return SRPConfigSystems.phaseDisloDurationOne;
            }
            case 2: {
                return SRPConfigSystems.phaseDisloDurationTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseDisloDurationThree;
            }
            case 4: {
                return SRPConfigSystems.phaseDisloDurationFour;
            }
            case 5: {
                return SRPConfigSystems.phaseDisloDurationFive;
            }
            case 6: {
                return SRPConfigSystems.phaseDisloDurationSix;
            }
            case 7: {
                return SRPConfigSystems.phaseDisloDurationSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseDisloDurationEight;
            }
            case 9: {
                return SRPConfigSystems.phaseDisloDurationNine;
            }
            case 10: {
                return SRPConfigSystems.phaseDisloDurationTen;
            }
        }
        return 0.0;
    }

    public static double getDisloPhaseCost(byte in) {
        switch (in) {
            case 1: {
                return SRPConfigSystems.phaseDisloPointCostOne;
            }
            case 2: {
                return SRPConfigSystems.phaseDisloPointCostTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseDisloPointCostThree;
            }
            case 4: {
                return SRPConfigSystems.phaseDisloPointCostFour;
            }
            case 5: {
                return SRPConfigSystems.phaseDisloPointCostFive;
            }
            case 6: {
                return SRPConfigSystems.phaseDisloPointCostSix;
            }
            case 7: {
                return SRPConfigSystems.phaseDisloPointCostSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseDisloPointCostEight;
            }
            case 9: {
                return SRPConfigSystems.phaseDisloPointCostNine;
            }
            case 10: {
                return SRPConfigSystems.phaseDisloPointCostTen;
            }
        }
        return 0.0;
    }

    public static double getDisloPhaseValue(byte in) {
        switch (in) {
            case 1: {
                return SRPConfigSystems.phaseDisloMoreValueOne;
            }
            case 2: {
                return SRPConfigSystems.phaseDisloMoreValueTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseDisloMoreValueThree;
            }
            case 4: {
                return SRPConfigSystems.phaseDisloMoreValueFour;
            }
            case 5: {
                return SRPConfigSystems.phaseDisloMoreValueFive;
            }
            case 6: {
                return SRPConfigSystems.phaseDisloMoreValueSix;
            }
            case 7: {
                return SRPConfigSystems.phaseDisloMoreValueSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseDisloMoreValueEight;
            }
            case 9: {
                return SRPConfigSystems.phaseDisloMoreValueNine;
            }
            case 10: {
                return SRPConfigSystems.phaseDisloMoreValueTen;
            }
        }
        return 0.0;
    }

    public static int getVectorHealthBonus(int in) {
        switch (in) {
            case -1: {
                return SRPConfigSystems.phaseVectorMultBonusMinusOne;
            }
            case 0: {
                return SRPConfigSystems.phaseVectorMultBonusZero;
            }
            case 1: {
                return SRPConfigSystems.phaseVectorMultBonusOne;
            }
            case 2: {
                return SRPConfigSystems.phaseVectorMultBonusTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseVectorMultBonusThree;
            }
            case 4: {
                return SRPConfigSystems.phaseVectorMultBonusFour;
            }
            case 5: {
                return SRPConfigSystems.phaseVectorMultBonusFive;
            }
            case 6: {
                return SRPConfigSystems.phaseVectorMultBonusSix;
            }
            case 7: {
                return SRPConfigSystems.phaseVectorMultBonusSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseVectorMultBonusEight;
            }
            case 9: {
                return SRPConfigSystems.phaseVectorMultBonusNine;
            }
        }
        return SRPConfigSystems.phaseVectorMultBonusTen;
    }
}
