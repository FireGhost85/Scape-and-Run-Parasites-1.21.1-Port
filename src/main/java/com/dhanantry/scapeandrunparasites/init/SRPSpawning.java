package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanColony;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSpawn;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSwim;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityBanoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityCanraAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityHullAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityNoglaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityRanracAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityTerla;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCruxA;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHeed;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHostII;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerCow;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHorse;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerPig;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiGolem;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityAta;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityButhol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityRathol;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityDorpa;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfBear;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfCow;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHorse;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPig;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityBano;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityCanra;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityIki;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityRanrac;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityShyco;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityWymo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAlafha;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAnged;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityEsor;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityFlog;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityGanro;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOmboo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityElvia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityJinjo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityLencia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityPheon;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityVesta;
import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.SubscribeEvent;

public class SRPSpawning {
    private static List<Biome.SpawnListEntry> PHASEMINUSONE;
    private static List<Biome.SpawnListEntry> PHASEZERO;
    private static List<Biome.SpawnListEntry> PHASEONE;
    private static List<Biome.SpawnListEntry> PHASETWO;
    private static List<Biome.SpawnListEntry> PHASETHREE;
    private static List<Biome.SpawnListEntry> PHASEFOUR;
    private static List<Biome.SpawnListEntry> PHASEFIVE;
    private static List<Biome.SpawnListEntry> PHASESIX;
    private static List<Biome.SpawnListEntry> PHASESEVEN;
    private static List<Biome.SpawnListEntry> PHASEEIGHT;
    private static List<Biome.SpawnListEntry> PHASENINE;
    private static List<Biome.SpawnListEntry> PHASETEN;
    private static List<Biome.SpawnListEntry> LEVELONE;
    private static List<Biome.SpawnListEntry> LEVELTWO;
    private static List<Biome.SpawnListEntry> LEVELTHREE;
    private static List<Biome.SpawnListEntry> LEVELFOUR;
    public static boolean totalParasites;
    private static final Logger SRP_LOG;

    public static void init() {
        if (!SRPConfig.allowMobs) {
            return;
        }
        if (!SRPConfigSystems.useEvolution || SRPConfigSystems.useEvolution && !SRPConfigSystems.phaseCustomSpawner) {
            for (Biome biome : Biome.REGISTRY) {
                SRPSpawning.addSpawn(0, EntityShyco.class, 1, 1, biome, SRPConfigMobs.shycoSpawnRate, SRPConfigMobs.shycoEnabled);
                SRPSpawning.addSpawn(0, EntityShycoAdapted.class, 1, 1, biome, SRPConfigMobs.shycoASpawnRate, SRPConfigMobs.shycoEnabled);
                SRPSpawning.addSpawn(0, EntityEmana.class, 2, 3, biome, SRPConfigMobs.emanaSpawnRate, SRPConfigMobs.emanaEnabled);
                SRPSpawning.addSpawn(0, EntityEmanaAdapted.class, 2, 3, biome, SRPConfigMobs.emanaASpawnRate, SRPConfigMobs.emanaEnabled);
                SRPSpawning.addSpawn(0, EntityHull.class, 4, 6, biome, SRPConfigMobs.hullSpawnRate, SRPConfigMobs.hullEnabled);
                SRPSpawning.addSpawn(0, EntityHullAdapted.class, 4, 6, biome, SRPConfigMobs.hullASpawnRate, SRPConfigMobs.hullEnabled);
                SRPSpawning.addSpawn(0, EntityCanra.class, 1, 1, biome, SRPConfigMobs.canraSpawnRate, SRPConfigMobs.canraEnabled);
                SRPSpawning.addSpawn(0, EntityCanraAdapted.class, 1, 1, biome, SRPConfigMobs.canraASpawnRate, SRPConfigMobs.canraEnabled);
                SRPSpawning.addSpawn(0, EntityNogla.class, 1, 2, biome, SRPConfigMobs.noglaSpawnRate, SRPConfigMobs.noglaEnabled);
                SRPSpawning.addSpawn(0, EntityNoglaAdapted.class, 1, 2, biome, SRPConfigMobs.noglaASpawnRate, SRPConfigMobs.noglaEnabled);
                SRPSpawning.addSpawn(0, EntityBano.class, 1, 1, biome, SRPConfigMobs.zetmoSpawnRate, SRPConfigMobs.zetmoEnabled);
                SRPSpawning.addSpawn(0, EntityBanoAdapted.class, 1, 1, biome, SRPConfigMobs.zetmoASpawnRate, SRPConfigMobs.zetmoEnabled);
                SRPSpawning.addSpawn(0, EntityRanrac.class, 1, 1, biome, SRPConfigMobs.arachnidaSpawnRate, SRPConfigMobs.arachnidaEnabled);
                SRPSpawning.addSpawn(0, EntityRanracAdapted.class, 1, 1, biome, SRPConfigMobs.arachnidaASpawnRate, SRPConfigMobs.arachnidaEnabled);
                SRPSpawning.addSpawn(0, EntityWymo.class, 1, 1, biome, SRPConfigMobs.wymoSpawnRate, SRPConfigMobs.wymoEnabled);
                SRPSpawning.addSpawn(0, EntityIki.class, 1, 1, biome, SRPConfigMobs.ikiSpawnRate, SRPConfigMobs.ikiEnabled);
                SRPSpawning.addSpawn(0, EntityRathol.class, 2, 2, biome, SRPConfigMobs.ratholSpawnRate, SRPConfigMobs.ratholEnabled);
                SRPSpawning.addSpawn(0, EntityButhol.class, 1, 2, biome, SRPConfigMobs.butholSpawnRate, SRPConfigMobs.butholEnabled);
                SRPSpawning.addSpawn(0, EntityMudo.class, 3, 6, biome, SRPConfigMobs.mudoSpawnRate, SRPConfigMobs.mudoEnabled);
                SRPSpawning.addSpawn(0, EntityLodo.class, 2, 5, biome, SRPConfigMobs.lodoSpawnRate, SRPConfigMobs.lodoEnabled);
                SRPSpawning.addSpawn(0, EntityKol.class, 1, 1, biome, SRPConfigMobs.kolSpawnRate, SRPConfigMobs.kolEnabled);
                SRPSpawning.addSpawn(0, EntityNuuh.class, 1, 1, biome, SRPConfigMobs.nuuhSpawnRate, SRPConfigMobs.nuuhEnabled);
                SRPSpawning.addSpawn(0, EntityAta.class, 1, 1, biome, SRPConfigMobs.ataSpawnRate, SRPConfigMobs.ataEnabled);
                SRPSpawning.addSpawn(0, EntityInfBear.class, 1, 1, biome, SRPConfigMobs.infbearSpawnRate, SRPConfigMobs.infbearEnabled);
                SRPSpawning.addSpawn(0, EntityDorpa.class, 1, 1, biome, SRPConfigMobs.dorpaSpawnRate, SRPConfigMobs.dorpaEnabled);
                SRPSpawning.addSpawn(0, EntityInfEnderman.class, 1, 1, biome, SRPConfigMobs.infendermanSpawnRate, SRPConfigMobs.infendermanEnabled);
                SRPSpawning.addSpawn(0, EntityInfHuman.class, 3, 5, biome, SRPConfigMobs.infhumanSpawnRate, SRPConfigMobs.infhumanEnabled);
                SRPSpawning.addSpawn(0, EntityInfCow.class, 1, 3, biome, SRPConfigMobs.infcowSpawnRate, SRPConfigMobs.infcowEnabled);
                SRPSpawning.addSpawn(0, EntityInfSheep.class, 1, 3, biome, SRPConfigMobs.infsheepSpawnRate, SRPConfigMobs.infsheepEnabled);
                SRPSpawning.addSpawn(0, EntityInfWolf.class, 3, 6, biome, SRPConfigMobs.infwolfSpawnRate, SRPConfigMobs.infwolfEnabled);
                SRPSpawning.addSpawn(0, EntityInfPig.class, 3, 6, biome, SRPConfigMobs.infpigSpawnRate, SRPConfigMobs.infpigEnabled);
                SRPSpawning.addSpawn(0, EntityInfVillager.class, 3, 6, biome, SRPConfigMobs.infvillagerSpawnRate, SRPConfigMobs.infvillagerEnabled);
                SRPSpawning.addSpawn(0, EntityInfHorse.class, 3, 6, biome, SRPConfigMobs.infhorseSpawnRate, SRPConfigMobs.infhorseEnabled);
                SRPSpawning.addSpawn(0, EntityInfPlayer.class, 3, 6, biome, SRPConfigMobs.infadventurerSpawnRate, SRPConfigMobs.infadventurerEnabled);
                SRPSpawning.addSpawn(0, EntityInfDragonE.class, 3, 6, biome, SRPConfigMobs.infdragoneSpawnRate, SRPConfigMobs.infdragoneEnabled);
                SRPSpawning.addSpawn(0, EntityFerEnderman.class, 1, 1, biome, SRPConfigMobs.ferendermanSpawnRate, SRPConfigMobs.ferendermanEnabled);
                SRPSpawning.addSpawn(0, EntityFerHuman.class, 3, 5, biome, SRPConfigMobs.ferhumanSpawnRate, SRPConfigMobs.ferhumanEnabled);
                SRPSpawning.addSpawn(0, EntityFerCow.class, 1, 3, biome, SRPConfigMobs.fercowSpawnRate, SRPConfigMobs.fercowEnabled);
                SRPSpawning.addSpawn(0, EntityFerSheep.class, 1, 3, biome, SRPConfigMobs.fersheepSpawnRate, SRPConfigMobs.fersheepEnabled);
                SRPSpawning.addSpawn(0, EntityFerWolf.class, 3, 6, biome, SRPConfigMobs.ferwolfSpawnRate, SRPConfigMobs.ferwolfEnabled);
                SRPSpawning.addSpawn(0, EntityFerPig.class, 3, 6, biome, SRPConfigMobs.ferpigSpawnRate, SRPConfigMobs.ferpigEnabled);
                SRPSpawning.addSpawn(0, EntityFerVillager.class, 3, 6, biome, SRPConfigMobs.fervillagerSpawnRate, SRPConfigMobs.fervillagerEnabled);
                SRPSpawning.addSpawn(0, EntityFerHorse.class, 3, 6, biome, SRPConfigMobs.ferhorseSpawnRate, SRPConfigMobs.ferhorseEnabled);
                SRPSpawning.addSpawn(0, EntityHiGolem.class, 1, 1, biome, SRPConfigMobs.higolemSpawnRate, SRPConfigMobs.higolemEnabled);
                SRPSpawning.addSpawn(0, EntityHost.class, 1, 1, biome, SRPConfigMobs.hostSpawnRate, SRPConfigMobs.hostEnabled);
                SRPSpawning.addSpawn(0, EntityHostII.class, 1, 1, biome, SRPConfigMobs.herdSpawnRate, SRPConfigMobs.herdEnabled);
                SRPSpawning.addSpawn(0, EntityHeed.class, 1, 1, biome, SRPConfigMobs.heedSpawnRate, SRPConfigMobs.heedEnabled);
                SRPSpawning.addSpawn(0, EntityCruxA.class, 1, 1, biome, SRPConfigMobs.cruxaSpawnRate, SRPConfigMobs.cruxaEnabled);
                SRPSpawning.addSpawn(0, EntityInhooS.class, 2, 5, biome, SRPConfigMobs.inhooSSpawnRate, SRPConfigMobs.inhooSEnabled);
                SRPSpawning.addSpawn(0, EntityInhooM.class, 2, 5, biome, SRPConfigMobs.inhooMSpawnRate, SRPConfigMobs.inhooMEnabled);
                SRPSpawning.addSpawn(0, EntityAlafha.class, 1, 1, biome, SRPConfigMobs.alafhaSpawnRate, SRPConfigMobs.alafhaEnabled);
                SRPSpawning.addSpawn(0, EntityGanro.class, 1, 1, biome, SRPConfigMobs.ganroSpawnRate, SRPConfigMobs.ganroEnabled);
                SRPSpawning.addSpawn(0, EntityAnged.class, 2, 2, biome, SRPConfigMobs.angedSpawnRate, SRPConfigMobs.angedEnabled);
                SRPSpawning.addSpawn(0, EntityEsor.class, 1, 1, biome, SRPConfigMobs.esorSpawnRate, SRPConfigMobs.esorEnabled);
                SRPSpawning.addSpawn(0, EntityOmboo.class, 2, 2, biome, SRPConfigMobs.ombooSpawnRate, SRPConfigMobs.ombooEnabled);
                SRPSpawning.addSpawn(0, EntityFlog.class, 3, 6, biome, SRPConfigMobs.flogSpawnRate, SRPConfigMobs.flogEnabled);
                SRPSpawning.addSpawn(0, EntityJinjo.class, 3, 6, biome, SRPConfigMobs.jinjoSpawnRate, SRPConfigMobs.jinjoEnabled);
                SRPSpawning.addSpawn(0, EntityElvia.class, 3, 6, biome, SRPConfigMobs.elviaSpawnRate, SRPConfigMobs.elviaEnabled);
                SRPSpawning.addSpawn(0, EntityLencia.class, 3, 6, biome, SRPConfigMobs.lenciaSpawnRate, SRPConfigMobs.lenciaEnabled);
                SRPSpawning.addSpawn(0, EntityPheon.class, 3, 6, biome, SRPConfigMobs.pheonSpawnRate, SRPConfigMobs.pheonEnabled);
                SRPSpawning.addSpawn(0, EntityVesta.class, 3, 6, biome, SRPConfigMobs.vestaSpawnRate, SRPConfigMobs.vestaEnabled);
                SRPSpawning.addSpawn(0, EntityHeblu.class, 1, 1, biome, SRPConfigMobs.hebluSpawnRate, SRPConfigMobs.hebluEnabled);
                SRPSpawning.addSpawn(0, EntityOronco.class, 1, 1, biome, SRPConfigMobs.oroncoSpawnRate, SRPConfigMobs.oroncoEnabled);
                SRPSpawning.addSpawn(0, EntityTerla.class, 1, 1, biome, SRPConfigMobs.terlaSpawnRate, SRPConfigMobs.terlaEnabled);
                SRPSpawning.addSpawn(1, EntityVenkrol.class, 1, 1, biome, SRPConfigMobs.venkrolSpawnrate, SRPConfigSystems.rsEnabled);
            }
        } else {
            String[] here = new String[4];
            PHASEMINUSONE = new ArrayList<Biome.SpawnListEntry>();
            PHASEZERO = new ArrayList<Biome.SpawnListEntry>();
            PHASEONE = new ArrayList<Biome.SpawnListEntry>();
            PHASETWO = new ArrayList<Biome.SpawnListEntry>();
            PHASETHREE = new ArrayList<Biome.SpawnListEntry>();
            PHASEFOUR = new ArrayList<Biome.SpawnListEntry>();
            PHASEFIVE = new ArrayList<Biome.SpawnListEntry>();
            PHASESIX = new ArrayList<Biome.SpawnListEntry>();
            PHASESEVEN = new ArrayList<Biome.SpawnListEntry>();
            PHASEEIGHT = new ArrayList<Biome.SpawnListEntry>();
            PHASENINE = new ArrayList<Biome.SpawnListEntry>();
            PHASETEN = new ArrayList<Biome.SpawnListEntry>();
            SRPSpawning.listInit(SRPConfigSystems.phaseSpawnEntryMinusne, PHASEMINUSONE);
            SRPSpawning.listInit(SRPConfigSystems.phaseSpawnEntryZero, PHASEZERO);
            SRPSpawning.listInit(SRPConfigSystems.phaseSpawnEntryOne, PHASEONE);
            SRPSpawning.listInit(SRPConfigSystems.phaseSpawnEntryTwo, PHASETWO);
            SRPSpawning.listInit(SRPConfigSystems.phaseSpawnEntryThree, PHASETHREE);
            SRPSpawning.listInit(SRPConfigSystems.phaseSpawnEntryFour, PHASEFOUR);
            SRPSpawning.listInit(SRPConfigSystems.phaseSpawnEntryFive, PHASEFIVE);
            SRPSpawning.listInit(SRPConfigSystems.phaseSpawnEntrySix, PHASESIX);
            SRPSpawning.listInit(SRPConfigSystems.phaseSpawnEntrySeven, PHASESEVEN);
            SRPSpawning.listInit(SRPConfigSystems.phaseSpawnEntryEight, PHASEEIGHT);
            SRPSpawning.listInit(SRPConfigSystems.phaseSpawnEntryNine, PHASENINE);
            SRPSpawning.listInit(SRPConfigSystems.phaseSpawnEntryTen, PHASETEN);
            LEVELONE = new ArrayList<Biome.SpawnListEntry>();
            LEVELTWO = new ArrayList<Biome.SpawnListEntry>();
            LEVELTHREE = new ArrayList<Biome.SpawnListEntry>();
            LEVELFOUR = new ArrayList<Biome.SpawnListEntry>();
            SRPSpawning.listInit(SRPConfigSystems.deveSpawnEntryUDOne, LEVELONE);
            SRPSpawning.listInit(SRPConfigSystems.deveSpawnEntryUDTwo, LEVELTWO);
            SRPSpawning.listInit(SRPConfigSystems.deveSpawnEntryUDThree, LEVELTHREE);
            SRPSpawning.listInit(SRPConfigSystems.deveSpawnEntryUDFour, LEVELFOUR);
        }
    }

    private static void listInit(String[] list, List<Biome.SpawnListEntry> in) {
        String[] here = new String[4];
        for (int i = 0; i < list.length; ++i) {
            if (list[i] == null) continue;
            here = list[i].split(";");
            int min = Integer.parseInt(here[1]);
            int max = Integer.parseInt(here[2]);
            int weight = Integer.parseInt(here[3]);
            in.add(new Biome.SpawnListEntry(EntityList.getClass((ResourceLocation)ResourceLocation.parse(here[0])), weight, min, max));
        }
    }

    public static void removeInit() {
        if (SRPConfigSystems.useEvolution) {
            return;
        }
        for (Biome biome : Biome.REGISTRY) {
            boolean rem = true;
            boolean gg = true;
            while (gg) {
                String eee;
                int i;
                for (i = 0; i < biome.getSpawnableList(MobCategory.MONSTER).size(); ++i) {
                    eee = ((Biome.SpawnListEntry)biome.getSpawnableList((MobCategory)MobCategory.MONSTER).get((int)i)).entityClass.toString();
                    if (eee.contains("scapeandrunparasites") && rem) {
                        gg = true;
                        biome.getSpawnableList(MobCategory.MONSTER).remove(i);
                        continue;
                    }
                    gg = false;
                }
                for (i = 0; i < biome.getSpawnableList(MobCategory.MONSTER).size(); ++i) {
                    eee = ((Biome.SpawnListEntry)biome.getSpawnableList((MobCategory)MobCategory.MONSTER).get((int)i)).entityClass.toString();
                    if (!eee.contains("scapeandrunparasites") || !rem) continue;
                    gg = true;
                    biome.getSpawnableList(MobCategory.MONSTER).remove(i);
                }
            }
        }
    }

    public static List<Biome.SpawnListEntry> getSpawns(Level world, int id, int phase, SRPSaveData data) {
        if (phase == -2) {
            return null;
        }
        if (data != null) {
            int[] vaal;
            if (world.random.nextDouble() < SRPConfigSystems.deveMobChance && data.getDeveLevel() > 0) {
                List<Biome.SpawnListEntry> spawnList = LEVELONE;
                switch (data.getDeveLevel()) {
                    case 1: {
                        spawnList = LEVELONE;
                        break;
                    }
                    case 2: {
                        spawnList = LEVELTWO;
                        break;
                    }
                    case 3: {
                        spawnList = LEVELTHREE;
                        break;
                    }
                    case 4: {
                        spawnList = LEVELFOUR;
                    }
                }
                if (!spawnList.isEmpty()) {
                    return spawnList;
                }
            }
            if ((vaal = data.getDisloValues(id))[5] > 0) {
                return null;
            }
            if (vaal[14] > 0) {
                phase += vaal[14];
            }
            switch (phase) {
                case -1: {
                    return data.getEIVArea(id) > 0 ? PHASEMINUSONE : null;
                }
                case 0: {
                    return PHASEZERO;
                }
                case 1: {
                    return PHASEONE;
                }
                case 2: {
                    return PHASETWO;
                }
                case 3: {
                    return PHASETHREE;
                }
                case 4: {
                    return PHASEFOUR;
                }
                case 5: {
                    return PHASEFIVE;
                }
                case 6: {
                    return PHASESIX;
                }
                case 7: {
                    return PHASESEVEN;
                }
                case 8: {
                    return PHASEEIGHT;
                }
                case 9: {
                    return PHASENINE;
                }
                case 10: {
                    return PHASETEN;
                }
            }
            return null;
        }
        return null;
    }

    public static void initBiome() {
        SRPSpawning.setBiomeSpawn(SRPConfigWorld.biomeOneSpawnEntry, SRPBiomes.biomeShrouded);
        SRPSpawning.setBiomeSpawn(SRPConfigWorld.biomeThreeSpawnEntry, SRPBiomes.biomeHarlequin);
    }

    private static void setBiomeSpawn(String[] list, Biome biome) {
        String[] here = new String[5];
        for (int i = 0; i < list.length; ++i) {
            if (list[i] == null) continue;
            here = list[i].split(";");
            int min = Integer.parseInt(here[1]);
            int max = Integer.parseInt(here[2]);
            int weight = Integer.parseInt(here[3]);
            int type = Integer.parseInt(here[4]);
            Class mob = EntityList.getClass((ResourceLocation)ResourceLocation.parse(here[0]));
            SRPSpawning.addSpawn(type, mob, min, max, biome, weight, true);
        }
    }

    public static void addSpawn(int type, Class<? extends Mob> entity, int groupMin, int groupMax, Biome biome, int weight, boolean addSpawn) {
        if (!addSpawn || weight <= 0) {
            return;
        }
        switch (type) {
            case 0: {
                biome.getSpawnableList(MobCategory.MONSTER).add(new Biome.SpawnListEntry(entity, weight, groupMin, groupMax));
                return;
            }
            case 1: {
                biome.getSpawnableList(MobCategory.CREATURE).add(new Biome.SpawnListEntry(entity, weight, groupMin, groupMax));
                return;
            }
            case 2: {
                biome.getSpawnableList(MobCategory.WATER_CREATURE).add(new Biome.SpawnListEntry(entity, weight, groupMin, groupMax));
                return;
            }
        }
    }

    static {
        SRP_LOG = LogManager.getLogger((String)"srparasites");
    }

    @Mod.EventBusSubscriber(modid="srparasites")
    public static class DimensionHandler {
        private static int mobClearCooldown = 0;

        @SubscribeEvent
        public static void onSpawn(LivingSpawnEvent.CheckSpawn event) {
            if (event.getEntity() instanceof EntityParasiteBase) {
                --mobClearCooldown;
                List<? extends Entity> serverList = SRPEntityUtil.allEntities(event.getEntity().level());
                int count = 0;
                int gnatCount = 0;
                int worker = 0;
                int waterParasites = 0;
                int airParasites = 0;
                for (Entity entity : serverList) {
                    if (!(entity instanceof EntityParasiteBase)) continue;
                    if (entity instanceof EntityCanHaveBodies) {
                        EntityCanHaveBodies bodies = (EntityCanHaveBodies)entity;
                        if (bodies.getBodyNumber() != 0) continue;
                        ++count;
                        continue;
                    }
                    ++count;
                    if (entity instanceof EntityAta) {
                        ++gnatCount;
                    }
                    if (entity instanceof EntityCanSwim) {
                        ++waterParasites;
                    }
                    if (entity instanceof EntityCanFly) {
                        ++airParasites;
                    }
                    if (!(entity instanceof EntityKol)) continue;
                    ++worker;
                }
                int players = event.getEntity().level().players().size() * SRPConfig.worldMobCapPlusPlayer;
                if (count > SRPConfig.worldSpawningMobCap + players) {
                    totalParasites = false;
                    event.setResult(MobDespawnEvent.Result.DENY);
                    if (count > (SRPConfig.worldSpawningMobCap + players) * 6 && mobClearCooldown <= 0 && SRPConfig.mobCleanerEnabled) {
                        SRP_LOG.warn("SOO MANY PARASITES!!! If you see this logging message, it means an unusual amount of parasites have spawned in the world. This is likely due to a bug or an issue with the mod or an addon you have installed. Excess parasites will be removed as a result, THIS IS INTENDED.");
                        mobClearCooldown = 50;
                        ArrayList<EntityParasiteBase> list = new ArrayList<EntityParasiteBase>();
                        for (Entity entity : serverList) {
                            if (!(entity instanceof EntityParasiteBase)) continue;
                            list.add((EntityParasiteBase)entity);
                        }
                        list.sort(Comparator.comparingDouble(e -> e.getBbWidth() + e.getBbHeight()).thenComparingInt(e -> e.tickCount));
                        for (Entity entity : serverList) {
                            if (!(entity instanceof EntityParasiteBase)) continue;
                            entity.discard();
                            if (--count >= (SRPConfig.worldSpawningMobCap + players) * 3) continue;
                            return;
                        }
                    }
                    return;
                }
                if (event.getEntity() instanceof EntityAta && gnatCount > SRPConfig.worldGnatCap) {
                    event.setResult(MobDespawnEvent.Result.DENY);
                    return;
                }
                if (event.getEntity() instanceof EntityKol && worker > 10) {
                    event.setResult(MobDespawnEvent.Result.DENY);
                    return;
                }
                if (waterParasites > SRPConfig.worldWaterCap && event.getEntity() instanceof EntityCanSwim) {
                    event.setResult(MobDespawnEvent.Result.DENY);
                    return;
                }
                if (airParasites > SRPConfig.worldAirCap && event.getEntity() instanceof EntityCanFly) {
                    event.setResult(MobDespawnEvent.Result.DENY);
                    return;
                }
                if (!SRPConfigSystems.useEvolution) {
                    boolean inv = false;
                    boolean flagI = false;
                    if (SRPConfig.blackListedDimensionsWhite) {
                        inv = true;
                    }
                    for (int i : SRPConfig.blackListedDimensions) {
                        if (SRPConfig.blackListedDimensionsWhite) {
                            if (i != DimKeys.of(event.getWorld())) continue;
                            flagI = true;
                            break;
                        }
                        if (i != DimKeys.of(event.getWorld())) continue;
                        event.setResult(MobDespawnEvent.Result.DENY);
                        return;
                    }
                    if (inv && !flagI) {
                        event.setResult(MobDespawnEvent.Result.DENY);
                        return;
                    }
                }
                if (SRPConfigSystems.useEvolution || SRPConfigWorld.coloniesActivated) {
                    EntityCanSpawn parasiteSus;
                    EntityParasiteBase parasite = (EntityParasiteBase)event.getEntity();
                    if (!parasite.canSpawnSpawn) {
                        event.setResult(MobDespawnEvent.Result.DENY);
                        return;
                    }
                    SRPWorldData data = SRPWorldData.get(parasite.level());
                    SRPSaveData sopa = SRPSaveData.get(parasite.level());
                    parasite.setCreatedPhase(sopa.getEvolutionPhase(DimKeys.of(parasite.level())), sopa.getDeveLevel());
                    if (SRPConfigSystems.useEvolution && !SRPConfigSystems.phaseCustomSpawner) {
                        if (sopa.getEvolutionPhase(DimKeys.of(parasite.level())) <= -1) {
                            event.setResult(MobDespawnEvent.Result.DENY);
                            return;
                        }
                        if (!DimensionHandler.canSpawninPhase(sopa.getEvolutionPhase(DimKeys.of(parasite.level())), sopa.getDeveLevel(), parasite)) {
                            event.setResult(MobDespawnEvent.Result.DENY);
                            return;
                        }
                    }
                    if (DimensionHandler.checkEvoLock(parasite.getParasiteIDRegister(), sopa) || DimensionHandler.checkColoLock(parasite.getParasiteIDRegister(), data, parasite)) {
                        event.setResult(MobDespawnEvent.Result.DENY);
                        return;
                    }
                    if (parasite instanceof EntityCanSpawn && sopa.getNumberIDDataSpawn((parasiteSus = (EntityCanSpawn)(parasite)).getIDSpawn()) < parasiteSus.canSpawnByIDData()) {
                        event.setResult(MobDespawnEvent.Result.DENY);
                        return;
                    }
                }
            } else if (event.getEntity() instanceof LivingEntity) {
                if (SRPConfigSystems.useEvolution && SRPSaveData.get(event.getWorld()).getEvolutionPhase(DimKeys.of(event.getWorld())) >= SRPConfigSystems.evolutionNoParasiteSpawnDenied) {
                    event.setResult(MobDespawnEvent.Result.DENY);
                }
                if (SRPConfigWorld.coloniesActivated && SRPWorldData.get(event.getWorld()).nearestColonyPosition(event.getEntity().blockPosition(), false) != null) {
                    event.setResult(MobDespawnEvent.Result.DENY);
                }
            }
        }

        private static boolean canSpawninPhase(int evPhase, int levelUD, EntityParasiteBase parasite) {
            byte type = parasite.getParasiteType();
            switch (evPhase) {
                case -1: {
                    return false;
                }
                case 0: {
                    if (type < SRPConfigSystems.phaseMaxParasiteIDZero && type > SRPConfigSystems.phaseCancelParasiteIDZero) break;
                    return false;
                }
                case 1: {
                    if (type < SRPConfigSystems.phaseMaxParasiteIDOne && type > SRPConfigSystems.phaseCancelParasiteIDOne) break;
                    return false;
                }
                case 2: {
                    if (type < SRPConfigSystems.phaseMaxParasiteIDTwo && type > SRPConfigSystems.phaseCancelParasiteIDTwo) break;
                    return false;
                }
                case 3: {
                    if (type < SRPConfigSystems.phaseMaxParasiteIDThree && type > SRPConfigSystems.phaseCancelParasiteIDThree) break;
                    return false;
                }
                case 4: {
                    if (type < SRPConfigSystems.phaseMaxParasiteIDFour && type > SRPConfigSystems.phaseCancelParasiteIDFour) break;
                    return false;
                }
                case 5: {
                    if (type < SRPConfigSystems.phaseMaxParasiteIDFive && type > SRPConfigSystems.phaseCancelParasiteIDFive) break;
                    return false;
                }
                case 6: {
                    if (type < SRPConfigSystems.phaseMaxParasiteIDSix && type > SRPConfigSystems.phaseCancelParasiteIDSix) break;
                    return false;
                }
                case 7: {
                    if (type < SRPConfigSystems.phaseMaxParasiteIDSeven && type > SRPConfigSystems.phaseCancelParasiteIDSeven) break;
                    return false;
                }
                case 8: {
                    if (type < SRPConfigSystems.phaseMaxParasiteIDEight && type > SRPConfigSystems.phaseCancelParasiteIDEight) break;
                    return false;
                }
                case 9: {
                    if (type < SRPConfigSystems.phaseMaxParasiteIDNine && type > SRPConfigSystems.phaseCancelParasiteIDNine) break;
                    return false;
                }
                case 10: {
                    if (type < SRPConfigSystems.phaseMaxParasiteIDTen && type > SRPConfigSystems.phaseCancelParasiteIDTen) break;
                    return false;
                }
            }
            return true;
        }

        private static boolean checkEvoLock(int in, SRPSaveData data) {
            return data.checkParasiteID(in);
        }

        private static boolean checkColoLock(int in, SRPWorldData data, EntityParasiteBase parasite) {
            int points;
            if (parasite instanceof EntityCanColony) {
                if (!SRPConfigWorld.coloniesActivated) {
                    return true;
                }
                points = data.totalColonyPoints(0);
                if (points > 0) {
                    BlockPos origin = data.nearestColonyPosition(parasite.blockPosition(), false);
                    if (origin == null ? ((EntityCanColony)(parasite)).onlySpawnInside() : !((EntityCanColony)(parasite)).onlySpawnInside()) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
            points = data.totalColonyPoints(0);
            String[] here = new String[2];
            int id = 0;
            int req = 0;
            for (int i = 0; i < SRPConfigWorld.preeValues.length; ++i) {
                here = SRPConfigWorld.preeValues[i].split(";");
                id = Integer.parseInt(here[0]);
                if (id != in) continue;
                if (parasite.level().getBiome(parasite.blockPosition()).value() instanceof BiomeParasiteBase) {
                    return SRPConfigWorld.preeValuesBiome;
                }
                req = Integer.parseInt(here[1]);
                return points < req;
            }
            return false;
        }
    }
}

