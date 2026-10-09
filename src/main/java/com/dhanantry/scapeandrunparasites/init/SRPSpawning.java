package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.SRPBlockLinks;
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
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityAta;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SRPSpawning {
    private static List<SpawnEntry> PHASEMINUSONE;
    private static List<SpawnEntry> PHASEZERO;
    private static List<SpawnEntry> PHASEONE;
    private static List<SpawnEntry> PHASETWO;
    private static List<SpawnEntry> PHASETHREE;
    private static List<SpawnEntry> PHASEFOUR;
    private static List<SpawnEntry> PHASEFIVE;
    private static List<SpawnEntry> PHASESIX;
    private static List<SpawnEntry> PHASESEVEN;
    private static List<SpawnEntry> PHASEEIGHT;
    private static List<SpawnEntry> PHASENINE;
    private static List<SpawnEntry> PHASETEN;
    private static List<SpawnEntry> LEVELONE;
    private static List<SpawnEntry> LEVELTWO;
    private static List<SpawnEntry> LEVELTHREE;
    private static List<SpawnEntry> LEVELFOUR;
    public static boolean totalParasites;
    private static final Logger SRP_LOG = LogManager.getLogger("srparasites");

    /** 1.12 Biome.SpawnListEntry: an entity type with a weight and a group size. */
    public static final class SpawnEntry implements WeightedEntry {
        public final EntityType<?> entityType;
        public final int itemWeight;
        public final int minGroupCount;
        public final int maxGroupCount;

        public SpawnEntry(EntityType<?> entityType, int weight, int min, int max) {
            this.entityType = entityType;
            this.itemWeight = weight;
            this.minGroupCount = min;
            this.maxGroupCount = max;
        }

        @Override
        public Weight getWeight() {
            return Weight.of(Math.max(1, this.itemWeight));
        }
    }

    /** 1.12 EntityLiving.SpawnPlacementType as registered by CommonProxy (everything else is ON_GROUND). */
    public enum Placement { ON_GROUND, IN_AIR, IN_WATER }

    private static Map<EntityType<?>, Placement> placements;

    public static Placement placementOf(EntityType<?> type) {
        if (placements == null) {
            Map<EntityType<?>, Placement> map = new HashMap<>();
            map.put(SRPEntities.CARRIER_FLYING.get(), Placement.IN_AIR);
            map.put(SRPEntities.LICE.get(), Placement.IN_AIR);
            map.put(SRPEntities.SIM_DRAGONE.get(), Placement.IN_AIR);
            map.put(SRPEntities.HI_BLAZE.get(), Placement.IN_AIR);
            map.put(SRPEntities.PRI_YELLOWEYE.get(), Placement.IN_AIR);
            map.put(SRPEntities.ADA_YELLOWEYE.get(), Placement.IN_AIR);
            map.put(SRPEntities.PRI_VERMIN.get(), Placement.IN_AIR);
            map.put(SRPEntities.AIRSCREW.get(), Placement.IN_AIR);
            map.put(SRPEntities.OVERSEER.get(), Placement.IN_AIR);
            map.put(SRPEntities.BOMBER_HEAVY.get(), Placement.IN_AIR);
            map.put(SRPEntities.BOGLE.get(), Placement.IN_AIR);
            map.put(SRPEntities.WRAITH.get(), Placement.IN_AIR);
            map.put(SRPEntities.ARCHITECT.get(), Placement.IN_AIR);
            map.put(SRPEntities.BOMBER_LIGHT.get(), Placement.IN_AIR);
            map.put(SRPEntities.DRACONITE.get(), Placement.IN_AIR);
            map.put(SRPEntities.SIM_SQUID.get(), Placement.IN_WATER);
            map.put(SRPEntities.PRI_DEVOURER.get(), Placement.IN_WATER);
            map.put(SRPEntities.ADA_DEVOURER.get(), Placement.IN_WATER);
            placements = map;
        }
        return placements.getOrDefault(type, Placement.ON_GROUND);
    }

    /** The spawns the original added to every vanilla biome when the custom spawner is off (applied by SRPSpawnBiomeModifier). */
    public record BiomeSpawn(int category, Supplier<? extends EntityType<?>> type, int min, int max, int weight) {
    }

    private static List<BiomeSpawn> biomeSpawns;

    public static synchronized List<BiomeSpawn> biomeSpawns() {
        if (biomeSpawns == null) {
            biomeSpawns = new ArrayList<>();
            if (SRPConfig.allowMobs && (!SRPConfigSystems.useEvolution || !SRPConfigSystems.phaseCustomSpawner)) {
                buildBiomeSpawns();
            }
        }
        return biomeSpawns;
    }

    private static void addSpawn(int type, Supplier<? extends EntityType<?>> entity, int groupMin, int groupMax, int weight, boolean addSpawn) {
        if (!addSpawn || weight <= 0) {
            return;
        }
        biomeSpawns.add(new BiomeSpawn(type, entity, groupMin, groupMax, weight));
    }

    private static void buildBiomeSpawns() {
        addSpawn(0, SRPEntities.PRI_LONGARMS, 1, 1, SRPConfigMobs.shycoSpawnRate, SRPConfigMobs.shycoEnabled);
        addSpawn(0, SRPEntities.ADA_LONGARMS, 1, 1, SRPConfigMobs.shycoASpawnRate, SRPConfigMobs.shycoEnabled);
        addSpawn(0, SRPEntities.PRI_YELLOWEYE, 2, 3, SRPConfigMobs.emanaSpawnRate, SRPConfigMobs.emanaEnabled);
        addSpawn(0, SRPEntities.ADA_YELLOWEYE, 2, 3, SRPConfigMobs.emanaASpawnRate, SRPConfigMobs.emanaEnabled);
        addSpawn(0, SRPEntities.PRI_MANDUCATER, 4, 6, SRPConfigMobs.hullSpawnRate, SRPConfigMobs.hullEnabled);
        addSpawn(0, SRPEntities.ADA_MANDUCATER, 4, 6, SRPConfigMobs.hullASpawnRate, SRPConfigMobs.hullEnabled);
        addSpawn(0, SRPEntities.PRI_SUMMONER, 1, 1, SRPConfigMobs.canraSpawnRate, SRPConfigMobs.canraEnabled);
        addSpawn(0, SRPEntities.ADA_SUMMONER, 1, 1, SRPConfigMobs.canraASpawnRate, SRPConfigMobs.canraEnabled);
        addSpawn(0, SRPEntities.PRI_REEKER, 1, 2, SRPConfigMobs.noglaSpawnRate, SRPConfigMobs.noglaEnabled);
        addSpawn(0, SRPEntities.ADA_REEKER, 1, 2, SRPConfigMobs.noglaASpawnRate, SRPConfigMobs.noglaEnabled);
        addSpawn(0, SRPEntities.PRI_BOLSTER, 1, 1, SRPConfigMobs.zetmoSpawnRate, SRPConfigMobs.zetmoEnabled);
        addSpawn(0, SRPEntities.ADA_BOLSTER, 1, 1, SRPConfigMobs.zetmoASpawnRate, SRPConfigMobs.zetmoEnabled);
        addSpawn(0, SRPEntities.PRI_ARACHNIDA, 1, 1, SRPConfigMobs.arachnidaSpawnRate, SRPConfigMobs.arachnidaEnabled);
        addSpawn(0, SRPEntities.ADA_ARACHNIDA, 1, 1, SRPConfigMobs.arachnidaASpawnRate, SRPConfigMobs.arachnidaEnabled);
        addSpawn(0, SRPEntities.PRI_TOZOON, 1, 1, SRPConfigMobs.wymoSpawnRate, SRPConfigMobs.wymoEnabled);
        addSpawn(0, SRPEntities.PRI_VERMIN, 1, 1, SRPConfigMobs.ikiSpawnRate, SRPConfigMobs.ikiEnabled);
        addSpawn(0, SRPEntities.CARRIER_HEAVY, 2, 2, SRPConfigMobs.ratholSpawnRate, SRPConfigMobs.ratholEnabled);
        addSpawn(0, SRPEntities.CARRIER_FLYING, 1, 2, SRPConfigMobs.butholSpawnRate, SRPConfigMobs.butholEnabled);
        addSpawn(0, SRPEntities.RUPTER, 3, 6, SRPConfigMobs.mudoSpawnRate, SRPConfigMobs.mudoEnabled);
        addSpawn(0, SRPEntities.BUGLIN, 2, 5, SRPConfigMobs.lodoSpawnRate, SRPConfigMobs.lodoEnabled);
        addSpawn(0, SRPEntities.WORKER, 1, 1, SRPConfigMobs.kolSpawnRate, SRPConfigMobs.kolEnabled);
        addSpawn(0, SRPEntities.MANGLER, 1, 1, SRPConfigMobs.nuuhSpawnRate, SRPConfigMobs.nuuhEnabled);
        addSpawn(0, SRPEntities.GNAT, 1, 1, SRPConfigMobs.ataSpawnRate, SRPConfigMobs.ataEnabled);
        addSpawn(0, SRPEntities.SIM_BEAR, 1, 1, SRPConfigMobs.infbearSpawnRate, SRPConfigMobs.infbearEnabled);
        addSpawn(0, SRPEntities.SIM_BIGSPIDER, 1, 1, SRPConfigMobs.dorpaSpawnRate, SRPConfigMobs.dorpaEnabled);
        addSpawn(0, SRPEntities.SIM_ENDERMAN, 1, 1, SRPConfigMobs.infendermanSpawnRate, SRPConfigMobs.infendermanEnabled);
        addSpawn(0, SRPEntities.SIM_HUMAN, 3, 5, SRPConfigMobs.infhumanSpawnRate, SRPConfigMobs.infhumanEnabled);
        addSpawn(0, SRPEntities.SIM_COW, 1, 3, SRPConfigMobs.infcowSpawnRate, SRPConfigMobs.infcowEnabled);
        addSpawn(0, SRPEntities.SIM_SHEEP, 1, 3, SRPConfigMobs.infsheepSpawnRate, SRPConfigMobs.infsheepEnabled);
        addSpawn(0, SRPEntities.SIM_WOLF, 3, 6, SRPConfigMobs.infwolfSpawnRate, SRPConfigMobs.infwolfEnabled);
        addSpawn(0, SRPEntities.SIM_PIG, 3, 6, SRPConfigMobs.infpigSpawnRate, SRPConfigMobs.infpigEnabled);
        addSpawn(0, SRPEntities.SIM_VILLAGER, 3, 6, SRPConfigMobs.infvillagerSpawnRate, SRPConfigMobs.infvillagerEnabled);
        addSpawn(0, SRPEntities.SIM_HORSE, 3, 6, SRPConfigMobs.infhorseSpawnRate, SRPConfigMobs.infhorseEnabled);
        addSpawn(0, SRPEntities.SIM_ADVENTURER, 3, 6, SRPConfigMobs.infadventurerSpawnRate, SRPConfigMobs.infadventurerEnabled);
        addSpawn(0, SRPEntities.SIM_DRAGONE, 3, 6, SRPConfigMobs.infdragoneSpawnRate, SRPConfigMobs.infdragoneEnabled);
        addSpawn(0, SRPEntities.FER_ENDERMAN, 1, 1, SRPConfigMobs.ferendermanSpawnRate, SRPConfigMobs.ferendermanEnabled);
        addSpawn(0, SRPEntities.FER_HUMAN, 3, 5, SRPConfigMobs.ferhumanSpawnRate, SRPConfigMobs.ferhumanEnabled);
        addSpawn(0, SRPEntities.FER_COW, 1, 3, SRPConfigMobs.fercowSpawnRate, SRPConfigMobs.fercowEnabled);
        addSpawn(0, SRPEntities.FER_SHEEP, 1, 3, SRPConfigMobs.fersheepSpawnRate, SRPConfigMobs.fersheepEnabled);
        addSpawn(0, SRPEntities.FER_WOLF, 3, 6, SRPConfigMobs.ferwolfSpawnRate, SRPConfigMobs.ferwolfEnabled);
        addSpawn(0, SRPEntities.FER_PIG, 3, 6, SRPConfigMobs.ferpigSpawnRate, SRPConfigMobs.ferpigEnabled);
        addSpawn(0, SRPEntities.FER_VILLAGER, 3, 6, SRPConfigMobs.fervillagerSpawnRate, SRPConfigMobs.fervillagerEnabled);
        addSpawn(0, SRPEntities.FER_HORSE, 3, 6, SRPConfigMobs.ferhorseSpawnRate, SRPConfigMobs.ferhorseEnabled);
        addSpawn(0, SRPEntities.HI_GOLEM, 1, 1, SRPConfigMobs.higolemSpawnRate, SRPConfigMobs.higolemEnabled);
        addSpawn(0, SRPEntities.HOST, 1, 1, SRPConfigMobs.hostSpawnRate, SRPConfigMobs.hostEnabled);
        addSpawn(0, SRPEntities.HOSTII, 1, 1, SRPConfigMobs.herdSpawnRate, SRPConfigMobs.herdEnabled);
        addSpawn(0, SRPEntities.HEED, 1, 1, SRPConfigMobs.heedSpawnRate, SRPConfigMobs.heedEnabled);
        addSpawn(0, SRPEntities.CRUX, 1, 1, SRPConfigMobs.cruxaSpawnRate, SRPConfigMobs.cruxaEnabled);
        addSpawn(0, SRPEntities.INCOMPLETEFORM_SMALL, 2, 5, SRPConfigMobs.inhooSSpawnRate, SRPConfigMobs.inhooSEnabled);
        addSpawn(0, SRPEntities.INCOMPLETEFORM_MEDIUM, 2, 5, SRPConfigMobs.inhooMSpawnRate, SRPConfigMobs.inhooMEnabled);
        addSpawn(0, SRPEntities.OVERSEER, 1, 1, SRPConfigMobs.alafhaSpawnRate, SRPConfigMobs.alafhaEnabled);
        addSpawn(0, SRPEntities.WARDEN, 1, 1, SRPConfigMobs.ganroSpawnRate, SRPConfigMobs.ganroEnabled);
        addSpawn(0, SRPEntities.VIGILANTE, 2, 2, SRPConfigMobs.angedSpawnRate, SRPConfigMobs.angedEnabled);
        addSpawn(0, SRPEntities.MARAUDER, 1, 1, SRPConfigMobs.esorSpawnRate, SRPConfigMobs.esorEnabled);
        addSpawn(0, SRPEntities.BOMBER_LIGHT, 2, 2, SRPConfigMobs.ombooSpawnRate, SRPConfigMobs.ombooEnabled);
        addSpawn(0, SRPEntities.GRUNT, 3, 6, SRPConfigMobs.flogSpawnRate, SRPConfigMobs.flogEnabled);
        addSpawn(0, SRPEntities.BOMBER_HEAVY, 3, 6, SRPConfigMobs.jinjoSpawnRate, SRPConfigMobs.jinjoEnabled);
        addSpawn(0, SRPEntities.WRAITH, 3, 6, SRPConfigMobs.elviaSpawnRate, SRPConfigMobs.elviaEnabled);
        addSpawn(0, SRPEntities.BOGLE, 3, 6, SRPConfigMobs.lenciaSpawnRate, SRPConfigMobs.lenciaEnabled);
        addSpawn(0, SRPEntities.HAUNTER, 3, 6, SRPConfigMobs.pheonSpawnRate, SRPConfigMobs.pheonEnabled);
        addSpawn(0, SRPEntities.CARRIER_COLONY, 3, 6, SRPConfigMobs.vestaSpawnRate, SRPConfigMobs.vestaEnabled);
        addSpawn(0, SRPEntities.DRACONITE, 1, 1, SRPConfigMobs.hebluSpawnRate, SRPConfigMobs.hebluEnabled);
        addSpawn(0, SRPEntities.ANC_DREADNAUT, 1, 1, SRPConfigMobs.oroncoSpawnRate, SRPConfigMobs.oroncoEnabled);
        addSpawn(0, SRPEntities.ANC_OVERLORD, 1, 1, SRPConfigMobs.terlaSpawnRate, SRPConfigMobs.terlaEnabled);
        addSpawn(1, SRPEntities.BECKON_SI, 1, 1, SRPConfigMobs.venkrolSpawnrate, SRPConfigSystems.rsEnabled);
    }

    public static void init() {
        if (!SRPConfig.allowMobs) {
            return;
        }
        if (SRPConfigSystems.useEvolution && SRPConfigSystems.phaseCustomSpawner) {
            PHASEMINUSONE = new ArrayList<SpawnEntry>();
            PHASEZERO = new ArrayList<SpawnEntry>();
            PHASEONE = new ArrayList<SpawnEntry>();
            PHASETWO = new ArrayList<SpawnEntry>();
            PHASETHREE = new ArrayList<SpawnEntry>();
            PHASEFOUR = new ArrayList<SpawnEntry>();
            PHASEFIVE = new ArrayList<SpawnEntry>();
            PHASESIX = new ArrayList<SpawnEntry>();
            PHASESEVEN = new ArrayList<SpawnEntry>();
            PHASEEIGHT = new ArrayList<SpawnEntry>();
            PHASENINE = new ArrayList<SpawnEntry>();
            PHASETEN = new ArrayList<SpawnEntry>();
            listInit(SRPConfigSystems.phaseSpawnEntryMinusne, PHASEMINUSONE);
            listInit(SRPConfigSystems.phaseSpawnEntryZero, PHASEZERO);
            listInit(SRPConfigSystems.phaseSpawnEntryOne, PHASEONE);
            listInit(SRPConfigSystems.phaseSpawnEntryTwo, PHASETWO);
            listInit(SRPConfigSystems.phaseSpawnEntryThree, PHASETHREE);
            listInit(SRPConfigSystems.phaseSpawnEntryFour, PHASEFOUR);
            listInit(SRPConfigSystems.phaseSpawnEntryFive, PHASEFIVE);
            listInit(SRPConfigSystems.phaseSpawnEntrySix, PHASESIX);
            listInit(SRPConfigSystems.phaseSpawnEntrySeven, PHASESEVEN);
            listInit(SRPConfigSystems.phaseSpawnEntryEight, PHASEEIGHT);
            listInit(SRPConfigSystems.phaseSpawnEntryNine, PHASENINE);
            listInit(SRPConfigSystems.phaseSpawnEntryTen, PHASETEN);
            LEVELONE = new ArrayList<SpawnEntry>();
            LEVELTWO = new ArrayList<SpawnEntry>();
            LEVELTHREE = new ArrayList<SpawnEntry>();
            LEVELFOUR = new ArrayList<SpawnEntry>();
            listInit(SRPConfigSystems.deveSpawnEntryUDOne, LEVELONE);
            listInit(SRPConfigSystems.deveSpawnEntryUDTwo, LEVELTWO);
            listInit(SRPConfigSystems.deveSpawnEntryUDThree, LEVELTHREE);
            listInit(SRPConfigSystems.deveSpawnEntryUDFour, LEVELFOUR);
        }
    }

    private static void listInit(String[] list, List<SpawnEntry> in) {
        for (int i = 0; i < list.length; ++i) {
            if (list[i] == null) continue;
            String[] here = list[i].split(";");
            int min = Integer.parseInt(here[1]);
            int max = Integer.parseInt(here[2]);
            int weight = Integer.parseInt(here[3]);
            ResourceLocation id = ResourceLocation.parse(here[0]);
            Optional<EntityType<?>> type = BuiltInRegistries.ENTITY_TYPE.getOptional(id);
            if (type.isEmpty()) {
                SRP_LOG.warn("[Spawner] unknown entity '{}' in a spawn entry", here[0]);
                continue;
            }
            in.add(new SpawnEntry(type.get(), weight, min, max));
        }
    }

    public static List<SpawnEntry> getSpawns(Level world, String id, int phase, SRPSaveData data) {
        if (phase == -2) {
            return null;
        }
        if (data != null) {
            int[] vaal;
            if (world.random.nextDouble() < SRPConfigSystems.deveMobChance && data.getDeveLevel() > 0) {
                List<SpawnEntry> spawnList = LEVELONE;
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


    @EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
    public static class DimensionHandler {
        private static int mobClearCooldown = 0;

        @SubscribeEvent
        public static void onSpawn(MobSpawnEvent.PositionCheck event) {
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
                    event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                    if (count > (SRPConfig.worldSpawningMobCap + players) * 6 && mobClearCooldown <= 0 && SRPConfig.mobCleanerEnabled) {
                        SRP_LOG.warn("SOO MANY PARASITES!!! If you see this logging message, it means an unusual amount of parasites have spawned in the world. This is likely due to a bug or an issue with the mod or an addon you have installed. Excess parasites will be removed as a result, THIS IS INTENDED.");
                        mobClearCooldown = 50;
                        ArrayList<EntityParasiteBase> list = new ArrayList<EntityParasiteBase>();
                        for (Entity entity : serverList) {
                            if (!(entity instanceof EntityParasiteBase)) continue;
                            list.add((EntityParasiteBase)entity);
                        }
                        list.sort(Comparator.<EntityParasiteBase>comparingDouble(e -> e.getBbWidth() + e.getBbHeight()).thenComparingInt(e -> e.tickCount));
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
                    event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                    return;
                }
                if (event.getEntity() instanceof EntityKol && worker > 10) {
                    event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                    return;
                }
                if (waterParasites > SRPConfig.worldWaterCap && event.getEntity() instanceof EntityCanSwim) {
                    event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                    return;
                }
                if (airParasites > SRPConfig.worldAirCap && event.getEntity() instanceof EntityCanFly) {
                    event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                    return;
                }
                if (!SRPConfigSystems.useEvolution) {
                    boolean inv = false;
                    boolean flagI = false;
                    if (SRPConfig.blackListedDimensionsWhite) {
                        inv = true;
                    }
                    for (String i : SRPConfig.blackListedDimensions) {
                        if (SRPConfig.blackListedDimensionsWhite) {
                            if (!DimKeys.normalize(i).equals(DimKeys.of(event.getLevel().getLevel()))) continue;
                            flagI = true;
                            break;
                        }
                        if (!DimKeys.normalize(i).equals(DimKeys.of(event.getLevel().getLevel()))) continue;
                        event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                        return;
                    }
                    if (inv && !flagI) {
                        event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                        return;
                    }
                }
                if (SRPConfigSystems.useEvolution || SRPConfigWorld.coloniesActivated) {
                    EntityCanSpawn parasiteSus;
                    EntityParasiteBase parasite = (EntityParasiteBase)event.getEntity();
                    if (!parasite.canSpawnSpawn) {
                        event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                        return;
                    }
                    SRPWorldData data = SRPWorldData.get(parasite.level());
                    SRPSaveData sopa = SRPSaveData.get(parasite.level());
                    parasite.setCreatedPhase(sopa.getEvolutionPhase(DimKeys.of(parasite.level())), sopa.getDeveLevel());
                    if (SRPConfigSystems.useEvolution && !SRPConfigSystems.phaseCustomSpawner) {
                        if (sopa.getEvolutionPhase(DimKeys.of(parasite.level())) <= -1) {
                            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                            return;
                        }
                        if (!DimensionHandler.canSpawninPhase(sopa.getEvolutionPhase(DimKeys.of(parasite.level())), sopa.getDeveLevel(), parasite)) {
                            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                            return;
                        }
                    }
                    if (DimensionHandler.checkEvoLock(parasite.getParasiteIDRegister(), sopa) || DimensionHandler.checkColoLock(parasite.getParasiteIDRegister(), data, parasite)) {
                        event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                        return;
                    }
                    if (parasite instanceof EntityCanSpawn && sopa.getNumberIDDataSpawn((parasiteSus = (EntityCanSpawn)(parasite)).getIDSpawn()) < parasiteSus.canSpawnByIDData()) {
                        event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                        return;
                    }
                }
            } else if (event.getEntity() instanceof LivingEntity) {
                if (SRPConfigSystems.useEvolution && SRPSaveData.get(event.getLevel().getLevel()).getEvolutionPhase(DimKeys.of(event.getLevel().getLevel())) >= SRPConfigSystems.evolutionNoParasiteSpawnDenied) {
                    event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                }
                if (SRPConfigWorld.coloniesActivated && SRPWorldData.get(event.getLevel().getLevel()).nearestColonyPosition(event.getEntity().blockPosition(), false) != null) {
                    event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
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
                if (SRPBlockLinks.isParasiteBiome(parasite.level(), parasite.blockPosition())) {
                    return SRPConfigWorld.preeValuesBiome;
                }
                req = Integer.parseInt(here[1]);
                return points < req;
            }
            return false;
        }
    }
}

