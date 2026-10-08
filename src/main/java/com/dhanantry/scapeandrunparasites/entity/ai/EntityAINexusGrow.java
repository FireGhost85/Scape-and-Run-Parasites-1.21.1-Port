package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPRooter;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeemSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeemSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeemSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIV;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EntityAINexusGrow
extends Goal {
    private final EntityPStationaryArchitect parent;
    private byte venkrolCurrentStage;
    private double tickss;
    private byte type;
    private boolean canGrow;
    private int dimLock;

    public EntityAINexusGrow(EntityPStationaryArchitect venkrol, int CURRENTstage) {
        this.parent = venkrol;
        this.tickss = 20.0;
        this.type = 1;
        this.venkrolCurrentStage = (byte)CURRENTstage;
        this.canGrow = true;
        this.dimLock = -1;
        String[] here = new String[2];
        for (int i = 0; i < SRPConfigSystems.maximumStageList.length; ++i) {
            here = SRPConfigSystems.maximumStageList[i].split(";");
            if (here[0].isEmpty() || !DimKeys.normalize(here[0]).equals(DimKeys.of(this.parent.level()))) continue;
            this.dimLock = Integer.parseInt(here[1]);
        }
    }

    public EntityAINexusGrow(EntityPStationaryArchitect venkrol, int CURRENTstage, int in) {
        this(venkrol, CURRENTstage);
        this.type = (byte)in;
    }

    public boolean canUse() {
        if (this.parent.canChangeVariant) {
            return true;
        }
        this.tickss -= 1.0;
        if (this.tickss < 0.0) {
            this.tickss = 20.0;
            this.checkPhase();
            this.spawnLeem();
            return this.canGrow;
        }
        return false;
    }

    public void stop() {
    }

    private void spawnLeem() {
        if (SRPConfigSystems.evolutionNests < this.parent.getPhaseCreated() && SRPConfigSystems.deveNestsUse < this.parent.getLevelCreated()) {
            return;
        }
        if (this.parent.level().random.nextDouble() >= 0.02) {
            return;
        }
        if ((this.venkrolCurrentStage == 3 || this.venkrolCurrentStage == 2) && this.type != 3) {
            List serverList = SRPEntityUtil.allEntities(this.parent.level());
            int count = 0;
            for (int x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityPRooter) || ++count <= SRPConfig.nexusLeemCap && !(this.parent.distanceToSqr((Entity)serverList.get(x)) < (double)(SRPConfig.nexusLeemDis * SRPConfig.nexusLeemDis))) continue;
                return;
            }
            if (ParasiteSummon.SummonM((LivingEntity)this.parent, new String[]{"srparasites:rooter_si;1;0"}, 12, 20, null) && SRPConfigSystems.rsSounds) {
                if (SRPConfigSystems.disloGrowlNoise) {
                    if (SRPSaveData.get(this.parent.level()).getCurrentCode(DimKeys.of(this.parent.level()), 15) == 0) {
                        this.parent.playSound(SRPSounds.LEEMSI.get(), 4.0f, 1.0f);
                    }
                } else {
                    this.parent.playSound(SRPSounds.LEEMSI.get(), 4.0f, 1.0f);
                }
            }
        }
    }

    public void tick() {
        if (this.venkrolCurrentStage == 3 && this.parent.level().getBiome(this.parent.blockPosition()).value() instanceof BiomeParasiteBase) {
            this.tickss = 20000.0;
        } else {
            int j = this.parent.getActualT();
            this.parent.setActualT(++j);
            if (j > this.parent.neededTime) {
                this.parent.setConvert(false);
                switch (this.type) {
                    case 1: {
                        this.upgradeV();
                        break;
                    }
                    case 2: {
                        this.upgradeD();
                        break;
                    }
                    case 3: {
                        this.upgradeL();
                    }
                }
            }
        }
    }

    private void checkPhase() {
        if (this.parent.level().isClientSide) {
            return;
        }
        if (this.dimLock >= this.venkrolCurrentStage + 1) {
            this.canGrow = false;
            return;
        }
        if (SRPConfigSystems.useEvolution) {
            RandomSource rand = RandomSource.create();
            block0 : switch (SRPSaveData.get(this.parent.level()).getEvolutionPhase(DimKeys.of(this.parent.level()))) {
                case -1: 
                case 0: {
                    this.canGrow = false;
                    break;
                }
                case 1: {
                    switch (this.venkrolCurrentStage) {
                        case 1: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyOne);
                            break;
                        }
                        case 2: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyOne);
                            break;
                        }
                        case 3: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyOne);
                        }
                    }
                    break;
                }
                case 2: {
                    switch (this.venkrolCurrentStage) {
                        case 1: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyTwo);
                            break;
                        }
                        case 2: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyTwo);
                            break;
                        }
                        case 3: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyTwo);
                        }
                    }
                    break;
                }
                case 3: {
                    switch (this.venkrolCurrentStage) {
                        case 1: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyThree);
                            break;
                        }
                        case 2: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyThree);
                            break;
                        }
                        case 3: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyThree);
                        }
                    }
                    break;
                }
                case 4: {
                    switch (this.venkrolCurrentStage) {
                        case 1: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyFour);
                            break;
                        }
                        case 2: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyFour);
                            break;
                        }
                        case 3: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyFour);
                        }
                    }
                    break;
                }
                case 5: {
                    switch (this.venkrolCurrentStage) {
                        case 1: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyFive);
                            break;
                        }
                        case 2: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyFive);
                            break;
                        }
                        case 3: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyFive);
                        }
                    }
                    break;
                }
                case 6: {
                    switch (this.venkrolCurrentStage) {
                        case 1: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltySix);
                            break;
                        }
                        case 2: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltySix);
                            break;
                        }
                        case 3: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltySix);
                        }
                    }
                    break;
                }
                case 7: {
                    switch (this.venkrolCurrentStage) {
                        case 1: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltySeven);
                            break;
                        }
                        case 2: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltySeven);
                            break;
                        }
                        case 3: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltySeven);
                        }
                    }
                    break;
                }
                case 8: {
                    switch (this.venkrolCurrentStage) {
                        case 1: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyEight);
                            break;
                        }
                        case 2: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyEight);
                            break;
                        }
                        case 3: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyEight);
                        }
                    }
                    break;
                }
                case 9: {
                    switch (this.venkrolCurrentStage) {
                        case 1: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyNine);
                            break;
                        }
                        case 2: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyNine);
                            break;
                        }
                        case 3: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyNine);
                        }
                    }
                    break;
                }
                case 10: {
                    switch (this.venkrolCurrentStage) {
                        case 1: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyTen);
                            break block0;
                        }
                        case 2: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyTen);
                            break block0;
                        }
                        case 3: {
                            this.canGrow = !(rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyTen);
                        }
                    }
                }
            }
        }
    }

    private void upgradeV() {
        if (!ParasiteEventEntity.canSpawnNext) {
            return;
        }
        if (this.venkrolCurrentStage == 1) {
            EntityVenkrolSII out = new EntityVenkrolSII(SRPEntities.BECKON_SII.get(), this.parent.level());
            out.cannotDespawn(this.parent.removeWhenFarAway(0.0));
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
                if (SRPConfigSystems.disloWalkNoise && SRPSaveData.get(this.parent.level()).getCurrentCode(DimKeys.of(this.parent.level()), 16) >= 1) {
                    return;
                }
                this.parent.playSound(SRPSounds.VENKROLSII.get(), 10.0f, 1.0f);
                if (SRPConfig.nexusStructures && this.parent.level().random.nextDouble() < 0.5) {
                    this.parent.generateStructure();
                }
            }
        } else if (this.venkrolCurrentStage == 2) {
            EntityVenkrolSIII out = new EntityVenkrolSIII(SRPEntities.BECKON_SIII.get(), this.parent.level());
            out.cannotDespawn(this.parent.removeWhenFarAway(0.0));
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
                if (SRPConfigSystems.disloWalkNoise && SRPSaveData.get(this.parent.level()).getCurrentCode(DimKeys.of(this.parent.level()), 16) >= 1) {
                    return;
                }
                this.parent.playSound(SRPSounds.VENKROLSIII.get(), 9.0f, 1.0f);
            }
        } else if (this.venkrolCurrentStage == 3) {
            SRPSaveData dataS;
            if (SRPConfigSystems.useEvolution && (dataS = SRPSaveData.get(this.parent.level())).getEvolutionPhase(DimKeys.of(this.parent.level())) < SRPConfigSystems.evolutionNodeUnlock && dataS.getDeveLevel() < SRPConfigSystems.deveNodesUse) {
                this.tickss = 20000.0;
                return;
            }
            if (ParasiteEventWorld.canBiomeStillExist(this.parent.level(), this.parent.blockPosition(), false) >= 1) {
                this.tickss = 20000.0;
                return;
            }
            EntityVenkrolSIV out = new EntityVenkrolSIV(SRPEntities.BECKON_SIV.get(), this.parent.level());
            out.cannotDespawn(this.parent.removeWhenFarAway(0.0));
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
                if (SRPConfigSystems.disloWalkNoise && SRPSaveData.get(this.parent.level()).getCurrentCode(DimKeys.of(this.parent.level()), 16) >= 1) {
                    return;
                }
                this.parent.playSound(SRPSounds.VENKROLSIV.get(), 10.0f, 1.0f);
            }
        }
    }

    private void upgradeD() {
        if (!ParasiteEventEntity.canSpawnNext) {
            return;
        }
        if (this.venkrolCurrentStage == 1) {
            EntityDodSII out = new EntityDodSII(SRPEntities.DISPATCHER_SII.get(), this.parent.level());
            out.cannotDespawn(this.parent.removeWhenFarAway(0.0));
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
                if (SRPConfigSystems.disloWalkNoise && SRPSaveData.get(this.parent.level()).getCurrentCode(DimKeys.of(this.parent.level()), 16) >= 1) {
                    return;
                }
                this.parent.playSound(SRPSounds.DODSII.get(), 10.0f, 1.0f);
                if (SRPConfig.nexusStructures && this.parent.level().random.nextDouble() < 0.3) {
                    this.parent.generateStructure();
                }
            }
        } else if (this.venkrolCurrentStage == 2) {
            EntityDodSIII out = new EntityDodSIII(SRPEntities.DISPATCHER_SIII.get(), this.parent.level());
            out.cannotDespawn(this.parent.removeWhenFarAway(0.0));
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
                if (SRPConfigSystems.disloWalkNoise && SRPSaveData.get(this.parent.level()).getCurrentCode(DimKeys.of(this.parent.level()), 16) >= 1) {
                    return;
                }
                this.parent.playSound(SRPSounds.DODSIII.get(), 9.0f, 1.0f);
            }
        } else if (this.venkrolCurrentStage == 3) {
            SRPSaveData dataS;
            if (SRPConfigSystems.useEvolution && (dataS = SRPSaveData.get(this.parent.level())).getEvolutionPhase(DimKeys.of(this.parent.level())) < SRPConfigSystems.evolutionColonyUnlock && dataS.getDeveLevel() < SRPConfigSystems.deveColoniesUse) {
                this.tickss = 20000.0;
                return;
            }
            if (ParasiteEventWorld.rangeOfColony(this.parent.level(), this.parent.blockPosition(), true) != null) {
                this.tickss = 20000.0;
                return;
            }
            EntityDodSIV out = new EntityDodSIV(SRPEntities.DISPATCHER_SIV.get(), this.parent.level());
            out.cannotDespawn(this.parent.removeWhenFarAway(0.0));
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
                if (SRPConfigSystems.disloWalkNoise && SRPSaveData.get(this.parent.level()).getCurrentCode(DimKeys.of(this.parent.level()), 16) >= 1) {
                    return;
                }
                this.parent.playSound(SRPSounds.DODSIV.get(), 10.0f, 1.0f);
            }
        }
    }

    private void upgradeL() {
        if (!ParasiteEventEntity.canSpawnNext) {
            return;
        }
        if (this.venkrolCurrentStage == 1) {
            EntityLeemSII out = new EntityLeemSII(SRPEntities.ROOTER_SII.get(), this.parent.level());
            out.cannotDespawn(this.parent.removeWhenFarAway(0.0));
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
                if (SRPConfigSystems.disloWalkNoise && SRPSaveData.get(this.parent.level()).getCurrentCode(DimKeys.of(this.parent.level()), 16) >= 1) {
                    return;
                }
                this.parent.playSound(SRPSounds.LEEMSII.get(), 10.0f, 1.0f);
                if (SRPConfig.nexusStructures && this.parent.level().random.nextDouble() < 0.3) {
                    this.parent.generateStructure();
                }
            }
        } else if (this.venkrolCurrentStage == 2) {
            EntityLeemSIII out = new EntityLeemSIII(SRPEntities.ROOTER_SIII.get(), this.parent.level());
            out.cannotDespawn(this.parent.removeWhenFarAway(0.0));
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
                if (SRPConfigSystems.disloWalkNoise && SRPSaveData.get(this.parent.level()).getCurrentCode(DimKeys.of(this.parent.level()), 16) >= 1) {
                    return;
                }
                this.parent.playSound(SRPSounds.LEEMSIII.get(), 9.0f, 1.0f);
            }
        } else if (this.venkrolCurrentStage == 3) {
            SRPSaveData dataS;
            if (SRPConfigSystems.useEvolution && (dataS = SRPSaveData.get(this.parent.level())).getEvolutionPhase(DimKeys.of(this.parent.level())) < SRPConfigSystems.evolutionHives && dataS.getDeveLevel() < SRPConfigSystems.deveHivesUse) {
                this.tickss = 20000.0;
                return;
            }
            EntityLeemSIV out = new EntityLeemSIV(SRPEntities.ROOTER_SIV.get(), this.parent.level());
            out.cannotDespawn(this.parent.removeWhenFarAway(0.0));
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
                if (SRPConfigSystems.disloWalkNoise && SRPSaveData.get(this.parent.level()).getCurrentCode(DimKeys.of(this.parent.level()), 16) >= 1) {
                    return;
                }
                this.parent.playSound(SRPSounds.LEEMSIV.get(), 10.0f, 1.0f);
            }
        }
    }
}

