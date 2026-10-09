package com.dhanantry.scapeandrunparasites.init;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.EntityOrbBoom;
import com.dhanantry.scapeandrunparasites.entity.EntityOrbScary;
import com.dhanantry.scapeandrunparasites.entity.EntityOrbVoid;
import com.dhanantry.scapeandrunparasites.entity.EntityParasiticScent;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.EntitySource;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWave;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWaveShock;
import com.dhanantry.scapeandrunparasites.entity.monster.abomination.EntityAboBodies;
import com.dhanantry.scapeandrunparasites.entity.monster.abomination.EntityAboHead;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityBanoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityCanraAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityGimAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityHullAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityIkiAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityLumAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityNoglaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityRanracAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityWymoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityZaaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOroncoTen;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityTerla;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCruxA;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCruxB;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityDone;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHeed;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHostII;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLeer;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityMes;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityQuac;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityKirin;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityDodT;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityLeemB;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityRof;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityTonro;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeem;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeemSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeemSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeemSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerBear;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerCow;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHorse;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerPig;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiBlaze;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiGolem;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiSkeleton;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityAta;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityButhol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityGothol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityRathol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityViin;
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
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSquid;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfCowHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfDragonEHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfEndermanHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfHorseHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfHumanHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfPigHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfPlayerHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfSheepHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfVillagerHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfWolfHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeBear;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeCow;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityBano;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityCanra;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityGim;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityIki;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityLum;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityRanrac;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityShyco;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityWymo;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityZaa;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAlafha;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAnged;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityEsor;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityFlog;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityGanro;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOmboo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOrch;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntitySoo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityElvia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityFlam;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityJinjo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityLencia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityPheon;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityTenn;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityVesta;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityDropPod;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityMeteor;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityNade;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAlafhaBall;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAncientball;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAngedball;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileBiomass;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileDragonE;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileEffects;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileElviaBall;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileHebluLight;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileHomming;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileKirinSlash;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileLenciaBall;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileNade;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectilePullball;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileWebball;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityThrowableAntiInfestedBlock;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Entity registry, generated by porting/tools/gen_entities.py from the 1.12.2 registration table
 * (same registry names). Sizes come from the original constructors. {@link #ENABLED} holds the
 * original per-mob config switch (1.12 skipped registration of disabled mobs, see PORTING_NOTES.md).
 */
public final class SRPEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, ScapeAndRunParasites.MODID);

    /** registry name -> original "active" switch */
    public static final Map<String, BooleanSupplier> ENABLED = new LinkedHashMap<>();
    /** registry name -> {primary, secondary} egg colours of the mobs that get a spawn egg */
    public static final Map<String, int[]> EGG_COLORS = new LinkedHashMap<>();
    /** attribute registration callbacks */
    private static final List<Runnable> NOOP = new ArrayList<>();

    private SRPEntities() {}

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> mob(String name, EntityType.EntityFactory<T> factory, float w, float h,
            boolean fireImmune, int c1, int c2, BooleanSupplier enabled) {
        ENABLED.put(name, enabled);
        EGG_COLORS.put(name, new int[]{c1, c2});
        return ENTITIES.register(name, () -> {
            EntityType.Builder<T> b = EntityType.Builder.of(factory, MobCategory.MONSTER).sized(w, h).clientTrackingRange(4).updateInterval(3);
            if (fireImmune) {
                b.fireImmune();
            }
            return b.build(ScapeAndRunParasites.MODID + ":" + name);
        });
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> proj(String name, EntityType.EntityFactory<T> factory, float w, float h,
            boolean fireImmune, int track, int interval) {
        return ENTITIES.register(name, () -> {
            EntityType.Builder<T> b = EntityType.Builder.of(factory, MobCategory.MISC).sized(w, h).clientTrackingRange(Math.max(4, track / 16)).updateInterval(interval);
            if (fireImmune) {
                b.fireImmune();
            }
            return b.build(ScapeAndRunParasites.MODID + ":" + name);
        });
    }

    public static final DeferredHolder<EntityType<?>, EntityType<EntityDorpa>> SIM_BIGSPIDER = mob("sim_bigspider", EntityDorpa::new, 1.9f, 2.1f, false, 8611072, 16711900, () -> SRPConfigMobs.dorpaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfSquid>> SIM_SQUID = mob("sim_squid", EntityInfSquid::new, 0.9f, 0.9f, false, 8611072, 16711900, () -> SRPConfigMobs.infsquidEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfHuman>> SIM_HUMAN = mob("sim_human", EntityInfHuman::new, 0.6f, 1.95f, false, 8611072, 16711900, () -> SRPConfigMobs.infhumanEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfCow>> SIM_COW = mob("sim_cow", EntityInfCow::new, 0.9f, 1.4f, false, 8611072, 16711900, () -> SRPConfigMobs.infcowEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfSheep>> SIM_SHEEP = mob("sim_sheep", EntityInfSheep::new, 0.9f, 1.3f, false, 8611072, 16711900, () -> SRPConfigMobs.infsheepEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfWolf>> SIM_WOLF = mob("sim_wolf", EntityInfWolf::new, 0.6f, 0.85f, false, 8611072, 16711900, () -> SRPConfigMobs.infwolfEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfPig>> SIM_PIG = mob("sim_pig", EntityInfPig::new, 0.9f, 0.9f, false, 8611072, 16711900, () -> SRPConfigMobs.infpigEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfVillager>> SIM_VILLAGER = mob("sim_villager", EntityInfVillager::new, 0.6f, 1.95f, false, 8611072, 16711900, () -> SRPConfigMobs.infvillagerEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfPlayer>> SIM_ADVENTURER = mob("sim_adventurer", EntityInfPlayer::new, 0.6f, 1.95f, false, 8611072, 16711900, () -> SRPConfigMobs.infadventurerEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfHorse>> SIM_HORSE = mob("sim_horse", EntityInfHorse::new, 1.3964844f, 1.6f, false, 8611072, 16711900, () -> SRPConfigMobs.infhorseEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfBear>> SIM_BEAR = mob("sim_bear", EntityInfBear::new, 1.3f, 1.4f, false, 8611072, 16711900, () -> SRPConfigMobs.infbearEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfEnderman>> SIM_ENDERMAN = mob("sim_enderman", EntityInfEnderman::new, 0.6f, 2.3f, false, 8611072, 16711900, () -> SRPConfigMobs.infendermanEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfDragonE>> SIM_DRAGONE = mob("sim_dragone", EntityInfDragonE::new, 1.9f, 3.8f, false, 8611072, 16711900, () -> SRPConfigMobs.infdragoneEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfSheepHead>> SIM_SHEEPHEAD = mob("sim_sheephead", EntityInfSheepHead::new, 0.7f, 0.7f, false, 8611072, 16711900, () -> SRPConfigMobs.infsheepEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfWolfHead>> SIM_WOLFHEAD = mob("sim_wolfhead", EntityInfWolfHead::new, 0.7f, 0.6f, false, 8611072, 16711900, () -> SRPConfigMobs.infwolfEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfCowHead>> SIM_COWHEAD = mob("sim_cowhead", EntityInfCowHead::new, 0.7f, 0.9f, false, 8611072, 16711900, () -> SRPConfigMobs.infcowEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfPigHead>> SIM_PIGHEAD = mob("sim_pighead", EntityInfPigHead::new, 0.7f, 0.9f, false, 8611072, 16711900, () -> SRPConfigMobs.infpigEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfVillagerHead>> SIM_VILLAGERHEAD = mob("sim_villagerhead", EntityInfVillagerHead::new, 0.7f, 0.8f, false, 8611072, 16711900, () -> SRPConfigMobs.infvillagerEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfHorseHead>> SIM_HORSEHEAD = mob("sim_horsehead", EntityInfHorseHead::new, 0.7f, 0.9f, false, 8611072, 16711900, () -> SRPConfigMobs.infhorseEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfHumanHead>> SIM_HUMANHEAD = mob("sim_humanhead", EntityInfHumanHead::new, 0.7f, 0.8f, false, 8611072, 16711900, () -> SRPConfigMobs.infhumanEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfEndermanHead>> SIM_ENDERMANHEAD = mob("sim_endermanhead", EntityInfEndermanHead::new, 0.7f, 0.9f, false, 8611072, 16711900, () -> SRPConfigMobs.infendermanEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfDragonEHead>> SIM_DRAGONEHEAD = mob("sim_dragonehead", EntityInfDragonEHead::new, 1.75f, 1.95f, false, 8611072, 16711900, () -> SRPConfigMobs.infdragoneEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInfPlayerHead>> SIM_ADVENTURERHEAD = mob("sim_adventurerhead", EntityInfPlayerHead::new, 0.7f, 0.9f, false, 8611072, 16711900, () -> SRPConfigMobs.infadventurerEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySpeEnderman>> MAR_ENDERMAN = mob("mar_enderman", EntitySpeEnderman::new, 0.6f, 2.9f, false, 8611072, 16711900, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySpeCow>> MAR_COW = mob("mar_cow", EntitySpeCow::new, 0.9f, 1.4f, false, 8611072, 16711900, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySpeVillager>> MAR_VILLAGER = mob("mar_villager", EntitySpeVillager::new, 0.6f, 2.75f, false, 8611072, 16711900, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySpeHuman>> MAR_HUMAN = mob("mar_human", EntitySpeHuman::new, 0.6f, 1.95f, false, 8611072, 16711900, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySpeSheep>> MAR_SHEEP = mob("mar_sheep", EntitySpeSheep::new, 0.7566f, 2.85f, false, 8611072, 16711900, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySpeBear>> MAR_BEAR = mob("mar_bear", EntitySpeBear::new, 1.3f, 1.4f, false, 8611072, 16711900, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFerBear>> FER_BEAR = mob("fer_bear", EntityFerBear::new, 1.3f, 1.4f, false, 8611072, 16711900, () -> SRPConfigMobs.ferbearEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFerCow>> FER_COW = mob("fer_cow", EntityFerCow::new, 0.9f, 1.4f, false, 8611072, 16711900, () -> SRPConfigMobs.fercowEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFerEnderman>> FER_ENDERMAN = mob("fer_enderman", EntityFerEnderman::new, 0.6f, 2.9f, false, 8611072, 16711900, () -> SRPConfigMobs.ferendermanEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFerHorse>> FER_HORSE = mob("fer_horse", EntityFerHorse::new, 1.3964844f, 1.75f, false, 8611072, 16711900, () -> SRPConfigMobs.ferhorseEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFerHuman>> FER_HUMAN = mob("fer_human", EntityFerHuman::new, 0.6f, 1.95f, false, 8611072, 16711900, () -> SRPConfigMobs.ferhumanEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFerPig>> FER_PIG = mob("fer_pig", EntityFerPig::new, 0.9f, 0.9f, false, 8611072, 16711900, () -> SRPConfigMobs.ferpigEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFerSheep>> FER_SHEEP = mob("fer_sheep", EntityFerSheep::new, 0.9f, 1.3f, false, 8611072, 16711900, () -> SRPConfigMobs.fersheepEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFerVillager>> FER_VILLAGER = mob("fer_villager", EntityFerVillager::new, 0.6f, 1.95f, false, 8611072, 16711900, () -> SRPConfigMobs.fervillagerEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFerWolf>> FER_WOLF = mob("fer_wolf", EntityFerWolf::new, 0.6f, 1.95f, false, 8611072, 16711900, () -> SRPConfigMobs.ferwolfEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityAboBodies>> ABO_BODIES = mob("abo_bodies", EntityAboBodies::new, 1.95154f, 2.95f, false, 8611072, 16711900, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityAboHead>> ABO_HEAD = mob("abo_head", EntityAboHead::new, 1.954f, 2.73f, false, 8611072, 16711900, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHiBlaze>> HI_BLAZE = mob("hi_blaze", EntityHiBlaze::new, 0.6f, 0.95f, false, 8611072, 16711900, () -> SRPConfigMobs.hiblazeEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHiGolem>> HI_GOLEM = mob("hi_golem", EntityHiGolem::new, 1.1f, 2.7f, false, 8611072, 16711900, () -> SRPConfigMobs.higolemEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHiSkeleton>> HI_SKELETON = mob("hi_skeleton", EntityHiSkeleton::new, 0.6f, 1.95f, false, 8611072, 16711900, () -> SRPConfigMobs.hiskeletonEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityRathol>> CARRIER_HEAVY = mob("carrier_heavy", EntityRathol::new, 1.3f, 3.1f, false, 3224855, 3224855, () -> SRPConfigMobs.ratholEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGothol>> CARRIER_LIGHT = mob("carrier_light", EntityGothol::new, 0.85f, 2.3f, false, 3224855, 3224855, () -> SRPConfigMobs.gotholEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLodo>> BUGLIN = mob("buglin", EntityLodo::new, 0.5f, 0.3f, false, 3224855, 3224855, () -> SRPConfigMobs.lodoEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityButhol>> CARRIER_FLYING = mob("carrier_flying", EntityButhol::new, 1.4f, 2.4f, false, 3224855, 3224855, () -> SRPConfigMobs.butholEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMudo>> RUPTER = mob("rupter", EntityMudo::new, 0.85f, 1.0f, false, 3224855, 3224855, () -> SRPConfigMobs.mudoEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLesh>> MOVINGFLESH = mob("movingflesh", EntityLesh::new, 0.7f, 0.5f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityKol>> WORKER = mob("worker", EntityKol::new, 0.65f, 0.65f, false, 3224855, 3224855, () -> SRPConfigMobs.kolEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityNuuh>> MANGLER = mob("mangler", EntityNuuh::new, 1.0f, 1.0f, false, 3224855, 3224855, () -> SRPConfigMobs.nuuhEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityAta>> GNAT = mob("gnat", EntityAta::new, 0.85f, 1.0f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityViin>> LICE = mob("lice", EntityViin::new, 0.85f, 1.0f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityVenkrol>> BECKON_SI = mob("beckon_si", EntityVenkrol::new, 0.5f, 1.5f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityVenkrolSII>> BECKON_SII = mob("beckon_sii", EntityVenkrolSII::new, 0.6f, 2.8f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityVenkrolSIII>> BECKON_SIII = mob("beckon_siii", EntityVenkrolSIII::new, 0.7f, 5.1f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityVenkrolSIV>> BECKON_SIV = mob("beckon_siv", EntityVenkrolSIV::new, 0.8f, 6.9f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDodT>> DISPATCHERTEN = mob("dispatcherten", EntityDodT::new, 0.7f, 2.5f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDod>> DISPATCHER_SI = mob("dispatcher_si", EntityDod::new, 2.7f, 2.5f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDodSII>> DISPATCHER_SII = mob("dispatcher_sii", EntityDodSII::new, 3.2f, 3.6f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDodSIII>> DISPATCHER_SIII = mob("dispatcher_siii", EntityDodSIII::new, 3.9f, 4.8f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDodSIV>> DISPATCHER_SIV = mob("dispatcher_siv", EntityDodSIV::new, 4.7f, 5.5f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLeemB>> ROOTERBALL = mob("rooterball", EntityLeemB::new, 1.4f, 1.4f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLeem>> ROOTER_SI = mob("rooter_si", EntityLeem::new, 1.2f, 2.8f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLeemSII>> ROOTER_SII = mob("rooter_sii", EntityLeemSII::new, 1.2f, 5.2f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLeemSIII>> ROOTER_SIII = mob("rooter_siii", EntityLeemSIII::new, 1.5f, 5.2f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLeemSIV>> ROOTER_SIV = mob("rooter_siv", EntityLeemSIV::new, 1.7f, 5.4f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTonro>> KYPHOSIS = mob("kyphosis", EntityTonro::new, 0.7f, 4.5f, false, 3224855, 3224855, () -> SRPConfigMobs.tonroEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityUnvo>> SENTRY = mob("sentry", EntityUnvo::new, 0.7f, 4.1f, false, 3224855, 3224855, () -> SRPConfigMobs.unvoEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityNak>> SEIZER = mob("seizer", EntityNak::new, 0.7f, 2.5f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityRof>> WORM = mob("worm", EntityRof::new, 1.5f, 4.6f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInhooS>> INCOMPLETEFORM_SMALL = mob("incompleteform_small", EntityInhooS::new, 0.6f, 0.85f, false, 8611072, 16711900, () -> SRPConfigMobs.inhooSEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityInhooM>> INCOMPLETEFORM_MEDIUM = mob("incompleteform_medium", EntityInhooM::new, 0.6f, 1.95f, false, 8611072, 16711900, () -> SRPConfigMobs.inhooMEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHost>> HOST = mob("host", EntityHost::new, 0.9f, 0.25f, false, 8611072, 16711900, () -> SRPConfigMobs.hostEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHostII>> HOSTII = mob("hostii", EntityHostII::new, 1.5f, 0.25f, false, 8611072, 16711900, () -> SRPConfigMobs.herdEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHeed>> HEED = mob("heed", EntityHeed::new, 0.9f, 1.9f, false, 8350208, 0x404040, () -> SRPConfigMobs.heedEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCruxA>> CRUX = mob("crux", EntityCruxA::new, 1.13333f, 3.3f, false, 8339200, 11992832, () -> SRPConfigMobs.cruxaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCruxB>> CRUX_INCOMPLETE = mob("crux_incomplete", EntityCruxB::new, 1.31f, 1.1f, false, 8339200, 11992832, () -> SRPConfigMobs.cruxaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMes>> THRALL = mob("thrall", EntityMes::new, 0.8f, 3.05f, false, 8339200, 11992832, () -> SRPConfigMobs.thrallEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDone>> DREDGE = mob("dredge", EntityDone::new, 0.8f, 3.4f, false, 8339200, 11992832, () -> SRPConfigMobs.doneEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLeer>> AIRSCREW = mob("airscrew", EntityLeer::new, 2.1f, 7.1f, false, 8339200, 11992832, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityQuac>> CARRIER_WORM = mob("carrier_worm", EntityQuac::new, 1.321f, 1.2f, false, 8339200, 11992832, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityShyco>> PRI_LONGARMS = mob("pri_longarms", EntityShyco::new, 0.6f, 3.2f, false, 8350208, 0x404040, () -> SRPConfigMobs.shycoEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHull>> PRI_MANDUCATER = mob("pri_manducater", EntityHull::new, 1.3f, 1.7f, false, 8350208, 0x404040, () -> SRPConfigMobs.hullEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityNogla>> PRI_REEKER = mob("pri_reeker", EntityNogla::new, 0.9f, 2.6f, false, 8350208, 0x404040, () -> SRPConfigMobs.noglaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityEmana>> PRI_YELLOWEYE = mob("pri_yelloweye", EntityEmana::new, 0.4f, 1.5f, false, 8350208, 0x404040, () -> SRPConfigMobs.emanaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCanra>> PRI_SUMMONER = mob("pri_summoner", EntityCanra::new, 1.2f, 1.8f, false, 8350208, 0x404040, () -> SRPConfigMobs.canraEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBano>> PRI_BOLSTER = mob("pri_bolster", EntityBano::new, 0.9f, 2.9f, false, 8350208, 0x404040, () -> SRPConfigMobs.zetmoEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityWymo>> PRI_TOZOON = mob("pri_tozoon", EntityWymo::new, 0.978f, 1.2f, false, 8350208, 0x404040, () -> SRPConfigMobs.wymoEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityRanrac>> PRI_ARACHNIDA = mob("pri_arachnida", EntityRanrac::new, 0.9f, 2.7f, false, 8350208, 0x404040, () -> SRPConfigMobs.arachnidaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLum>> PRI_DEVOURER = mob("pri_devourer", EntityLum::new, 1.3f, 1.8f, false, 8350208, 0x404040, () -> SRPConfigMobs.lumEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityIki>> PRI_VERMIN = mob("pri_vermin", EntityIki::new, 1.1f, 1.4f, false, 8350208, 0x404040, () -> SRPConfigMobs.ikiEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGim>> PRI_VISCERA = mob("pri_viscera", EntityGim::new, 1.211f, 2.351f, false, 8350208, 0x404040, () -> SRPConfigMobs.gimEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityZaa>> PRI_BURROWER = mob("pri_burrower", EntityZaa::new, 1.0f, 0.25f, false, 8350208, 0x404040, () -> SRPConfigMobs.zaaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityShycoAdapted>> ADA_LONGARMS = mob("ada_longarms", EntityShycoAdapted::new, 0.901f, 3.5f, false, 8339200, 11992832, () -> SRPConfigMobs.shycoEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHullAdapted>> ADA_MANDUCATER = mob("ada_manducater", EntityHullAdapted::new, 1.4f, 2.7f, false, 8339200, 11992832, () -> SRPConfigMobs.hullEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityNoglaAdapted>> ADA_REEKER = mob("ada_reeker", EntityNoglaAdapted::new, 1.3f, 3.3f, false, 8339200, 11992832, () -> SRPConfigMobs.noglaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityEmanaAdapted>> ADA_YELLOWEYE = mob("ada_yelloweye", EntityEmanaAdapted::new, 1.3f, 2.9f, false, 8339200, 11992832, () -> SRPConfigMobs.emanaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCanraAdapted>> ADA_SUMMONER = mob("ada_summoner", EntityCanraAdapted::new, 1.3f, 3.3f, false, 8339200, 11992832, () -> SRPConfigMobs.canraEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBanoAdapted>> ADA_BOLSTER = mob("ada_bolster", EntityBanoAdapted::new, 1.3f, 3.8f, false, 8339200, 11992832, () -> SRPConfigMobs.zetmoEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityWymoAdapted>> ADA_TOZOON = mob("ada_tozoon", EntityWymoAdapted::new, 1.321f, 1.2f, false, 8339200, 11992832, () -> SRPConfigMobs.wymoEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityRanracAdapted>> ADA_ARACHNIDA = mob("ada_arachnida", EntityRanracAdapted::new, 1.901f, 2.85f, false, 8339200, 11992832, () -> SRPConfigMobs.arachnidaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLumAdapted>> ADA_DEVOURER = mob("ada_devourer", EntityLumAdapted::new, 0.901f, 3.5f, false, 8350208, 0x404040, () -> SRPConfigMobs.lumEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityIkiAdapted>> ADA_VERMIN = mob("ada_vermin", EntityIkiAdapted::new, 1.1f, 1.4f, false, 8350208, 0x404040, () -> SRPConfigMobs.ikiEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGimAdapted>> ADA_VISCERA = mob("ada_viscera", EntityGimAdapted::new, 1.511f, 3.655f, false, 8350208, 0x404040, () -> SRPConfigMobs.gimEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityZaaAdapted>> ADA_BURROWER = mob("ada_burrower", EntityZaaAdapted::new, 1.321f, 1.2f, false, 8350208, 0x404040, () -> SRPConfigMobs.zaaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityAlafha>> OVERSEER = mob("overseer", EntityAlafha::new, 1.9f, 2.6f, false, 8611072, 16711900, () -> SRPConfigMobs.alafhaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityAnged>> VIGILANTE = mob("vigilante", EntityAnged::new, 1.6f, 3.1f, false, 3224855, 3224855, () -> SRPConfigMobs.angedEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGanro>> WARDEN = mob("warden", EntityGanro::new, 0.901f, 4.2f, false, 3224855, 3224855, () -> SRPConfigMobs.ganroEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityOmboo>> BOMBER_LIGHT = mob("bomber_light", EntityOmboo::new, 1.7f, 2.4f, false, 3224855, 3224855, () -> SRPConfigMobs.ombooEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityEsor>> MARAUDER = mob("marauder", EntityEsor::new, 0.901f, 4.2f, false, 3224855, 3224855, () -> SRPConfigMobs.esorEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityOrch>> MONARCH = mob("monarch", EntityOrch::new, 1.901f, 4.1f, false, 3224855, 3224855, () -> SRPConfigMobs.orchEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFlog>> GRUNT = mob("grunt", EntityFlog::new, 0.7666f, 1.95f, false, 3224855, 3224855, () -> SRPConfigMobs.flogEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityJinjo>> BOMBER_HEAVY = mob("bomber_heavy", EntityJinjo::new, 3.7f, 4.4f, false, 3224855, 3224855, () -> SRPConfigMobs.jinjoEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityElvia>> WRAITH = mob("wraith", EntityElvia::new, 4.0f, 4.0f, false, 3224855, 3224855, () -> SRPConfigMobs.elviaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLencia>> BOGLE = mob("bogle", EntityLencia::new, 4.0f, 4.0f, false, 3224855, 3224855, () -> SRPConfigMobs.lenciaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityPheon>> HAUNTER = mob("haunter", EntityPheon::new, 2.0f, 3.6f, false, 3224855, 3224855, () -> SRPConfigMobs.pheonEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityVesta>> CARRIER_COLONY = mob("carrier_colony", EntityVesta::new, 1.75f, 3.6f, false, 3224855, 3224855, () -> SRPConfigMobs.vestaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFlam>> SUCCOR = mob("succor", EntityFlam::new, 1.2f, 1.2f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySoo>> SEEKER = mob("seeker", EntitySoo::new, 1.9f, 2.6f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTenn>> ARCHITECT = mob("architect", EntityTenn::new, 1.9f, 2.6f, false, 3224855, 3224855, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityOronco>> ANC_DREADNAUT = mob("anc_dreadnaut", EntityOronco::new, 4.0f, 4.0f, false, 4272252, 4272252, () -> SRPConfigMobs.oroncoEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTerla>> ANC_OVERLORD = mob("anc_overlord", EntityTerla::new, 2.4f, 2.9f, false, 4272252, 4272252, () -> SRPConfigMobs.terlaEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDropPod>> ANC_POD = mob("anc_pod", EntityDropPod::new, 1.0f, 2.0f, false, 4272252, 4272252, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityOroncoTen>> ANC_DREADNAUT_TEN = mob("anc_dreadnaut_ten", EntityOroncoTen::new, 1.0f, 0.7f, false, 4272252, 4272252, () -> SRPConfigMobs.oroncoEnabled);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectilePullball>> PULLINGBALL = proj("pullingball", EntityProjectilePullball::new, 0.3f, 0.3f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileWebball>> WEBBALL = proj("webball", EntityProjectileWebball::new, 0.3f, 0.3f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileSpineball>> SPINEBALL = proj("spineball", EntityProjectileSpineball::new, 0.3f, 0.3f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileNade>> NADEBALL = proj("nadeball", EntityProjectileNade::new, 0.3f, 0.3f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileAlafhaBall>> SALIVABALL = proj("salivaball", EntityProjectileAlafhaBall::new, 0.3f, 0.3f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileAngedball>> BALLBALL = proj("ballball", EntityProjectileAngedball::new, 0.3f, 0.3f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileAncientball>> ANCIENTBALL = proj("ancientball", EntityProjectileAncientball::new, 0.3f, 0.3f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileHomming>> HOMMING = proj("homming", EntityProjectileHomming::new, 0.3125f, 0.3125f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityThrowableAntiInfestedBlock>> ANTIINFESTEDBLOCK = proj("antiinfestedblock", EntityThrowableAntiInfestedBlock::new, 0.6f, 1.8f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileBiomass>> BIOMASSBALL = proj("biomassball", EntityProjectileBiomass::new, 0.3f, 0.3f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileDragonE>> MISSILE = proj("missile", EntityProjectileDragonE::new, 0.3f, 0.3f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileElviaBall>> BALLTALL = proj("balltall", EntityProjectileElviaBall::new, 0.3f, 0.3f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileLenciaBall>> BALLMALL = proj("ballmall", EntityProjectileLenciaBall::new, 0.3f, 0.3f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileEffects>> SALIVAEFF = proj("salivaeff", EntityProjectileEffects::new, 0.3f, 0.3f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileHebluLight>> HEBLU_LIGHT = proj("heblu_light", EntityProjectileHebluLight::new, 0.65f, 0.65f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileKirinSlash>> KIRIN_SLASH = proj("kirin_slash", EntityProjectileKirinSlash::new, 0.25f, 0.25f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityKirin>> KIRIN = mob("kirin", EntityKirin::new, 2.1271334f, 8.85f, false, 4272252, 4272252, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHeblu>> DRACONITE = mob("draconite", EntityHeblu::new, 2.4f, 3.8f, false, 4272252, 4272252, () -> true);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityOrbScary>> ORBSCARY = proj("orbscary", EntityOrbScary::new, 0.5f, 0.5f, true, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityOrbVoid>> ORBVOID = proj("orbvoid", EntityOrbVoid::new, 0.5f, 0.5f, true, 256, 1);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityOrbBoom>> ORBBOOM = proj("orbboom", EntityOrbBoom::new, 0.5f, 0.5f, true, 256, 1);
    /** [CHG] not registered in 1.12 (spawned unregistered); 1.21 entities need a type. */
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDamage>> DAMAGE = proj("damage", EntityDamage::new, 1.2f, 0.9f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySource>> SOURCE = proj("source", EntitySource::new, 0.5f, 0.5f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityRemain>> REMAIN = proj("remain", EntityRemain::new, 0.5f, 0.5f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBomb>> BOMB = proj("bomb", EntityBomb::new, 0.68f, 0.68f, true, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityToxicCloud>> CLOUDTOXIC = proj("cloudtoxic", EntityToxicCloud::new, 0.6f, 1.8f, true, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBiomass>> BIOMASS = proj("biomass", EntityBiomass::new, 0.98f, 0.98f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGore>> GORE = proj("gore", EntityGore::new, 0.4f, 0.4f, true, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTendril>> TENDRIL = proj("tendril", EntityTendril::new, 1.0f, 1.0f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityParasiticScent>> SCENT = proj("scent", EntityParasiticScent::new, 0.6f, 1.8f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityWave>> WAVE = proj("wave", EntityWave::new, 1.5f, 0.2f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityWaveShock>> WAVESHOCK = proj("waveshock", EntityWaveShock::new, 3.1f, 0.2f, false, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityNade>> NADE = proj("nade", EntityNade::new, 0.5f, 0.5f, true, 64, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMeteor>> METEOR = proj("meteor", EntityMeteor::new, 4.5f, 4.5f, false, 256, 1);

    private static final java.util.Map<EntityType<? extends net.minecraft.world.entity.LivingEntity>, java.util.function.Supplier<AttributeSupplier.Builder>> ATTRIBUTE_BUILDERS = new java.util.LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    private static void put(EntityAttributeCreationEvent event, DeferredHolder<EntityType<?>, ? extends EntityType<?>> type, java.util.function.Supplier<AttributeSupplier.Builder> builder) {
        EntityType<? extends net.minecraft.world.entity.LivingEntity> t = (EntityType<? extends net.minecraft.world.entity.LivingEntity>) type.get();
        ATTRIBUTE_BUILDERS.put(t, builder);
        event.put(t, builder.get().build());
    }

    /**
     * The attribute suppliers are built in {@link EntityAttributeCreationEvent}, which is posted before the configs are loaded and
     * {@code SRPAttributes.init()} has applied them (adapted bonuses were 0, multipliers 1). When the configs are baked the base
     * values of the live suppliers are set again from freshly built ones.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void refreshAttributes() {
        for (var entry : ATTRIBUTE_BUILDERS.entrySet()) {
            net.minecraft.world.entity.ai.attributes.AttributeSupplier live = net.minecraft.world.entity.ai.attributes.DefaultAttributes.getSupplier((EntityType)entry.getKey());
            if (live == null) {
                continue;
            }
            net.minecraft.world.entity.ai.attributes.AttributeSupplier fresh = entry.getValue().get().build();
            for (var attr : fresh.instances.entrySet()) {
                var target = live.instances.get(attr.getKey());
                if (target != null) {
                    target.baseValue = attr.getValue().getBaseValue();
                }
            }
        }
    }

    /** Registers the attribute suppliers of all living parasite entities. */
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        put(event, BIOMASS, EntityBiomass::createAttributes);
        put(event, TENDRIL, EntityParasiteBase::createAttributes);
        put(event, WAVE, EntityWave::createAttributes);
        put(event, WAVESHOCK, EntityWaveShock::createAttributes);
        put(event, HOMMING, net.minecraft.world.entity.monster.Vex::createAttributes);
        put(event, SIM_BIGSPIDER, EntityDorpa::createAttributes);
        put(event, SIM_SQUID, EntityInfSquid::createAttributes);
        put(event, SIM_HUMAN, EntityInfHuman::createAttributes);
        put(event, SIM_COW, EntityInfCow::createAttributes);
        put(event, SIM_SHEEP, EntityInfSheep::createAttributes);
        put(event, SIM_WOLF, EntityInfWolf::createAttributes);
        put(event, SIM_PIG, EntityInfPig::createAttributes);
        put(event, SIM_VILLAGER, EntityInfVillager::createAttributes);
        put(event, SIM_ADVENTURER, EntityInfPlayer::createAttributes);
        put(event, SIM_HORSE, EntityInfHorse::createAttributes);
        put(event, SIM_BEAR, EntityInfBear::createAttributes);
        put(event, SIM_ENDERMAN, EntityInfEnderman::createAttributes);
        put(event, SIM_DRAGONE, EntityInfDragonE::createAttributes);
        put(event, SIM_SHEEPHEAD, EntityInfSheepHead::createAttributes);
        put(event, SIM_WOLFHEAD, EntityInfWolfHead::createAttributes);
        put(event, SIM_COWHEAD, EntityInfCowHead::createAttributes);
        put(event, SIM_PIGHEAD, EntityInfPigHead::createAttributes);
        put(event, SIM_VILLAGERHEAD, EntityInfVillagerHead::createAttributes);
        put(event, SIM_HORSEHEAD, EntityInfHorseHead::createAttributes);
        put(event, SIM_HUMANHEAD, EntityInfHumanHead::createAttributes);
        put(event, SIM_ENDERMANHEAD, EntityInfEndermanHead::createAttributes);
        put(event, SIM_DRAGONEHEAD, EntityInfDragonEHead::createAttributes);
        put(event, SIM_ADVENTURERHEAD, EntityInfPlayerHead::createAttributes);
        put(event, MAR_ENDERMAN, EntitySpeEnderman::createAttributes);
        put(event, MAR_COW, EntitySpeCow::createAttributes);
        put(event, MAR_VILLAGER, EntitySpeVillager::createAttributes);
        put(event, MAR_HUMAN, EntitySpeHuman::createAttributes);
        put(event, MAR_SHEEP, EntitySpeSheep::createAttributes);
        put(event, MAR_BEAR, EntitySpeBear::createAttributes);
        put(event, FER_BEAR, EntityFerBear::createAttributes);
        put(event, FER_COW, EntityFerCow::createAttributes);
        put(event, FER_ENDERMAN, EntityFerEnderman::createAttributes);
        put(event, FER_HORSE, EntityFerHorse::createAttributes);
        put(event, FER_HUMAN, EntityFerHuman::createAttributes);
        put(event, FER_PIG, EntityFerPig::createAttributes);
        put(event, FER_SHEEP, EntityFerSheep::createAttributes);
        put(event, FER_VILLAGER, EntityFerVillager::createAttributes);
        put(event, FER_WOLF, EntityFerWolf::createAttributes);
        put(event, ABO_BODIES, EntityAboBodies::createAttributes);
        put(event, ABO_HEAD, EntityAboHead::createAttributes);
        put(event, HI_BLAZE, EntityHiBlaze::createAttributes);
        put(event, HI_GOLEM, EntityHiGolem::createAttributes);
        put(event, HI_SKELETON, EntityHiSkeleton::createAttributes);
        put(event, CARRIER_HEAVY, EntityRathol::createAttributes);
        put(event, CARRIER_LIGHT, EntityGothol::createAttributes);
        put(event, BUGLIN, EntityLodo::createAttributes);
        put(event, CARRIER_FLYING, EntityButhol::createAttributes);
        put(event, RUPTER, EntityMudo::createAttributes);
        put(event, MOVINGFLESH, EntityLesh::createAttributes);
        put(event, WORKER, EntityKol::createAttributes);
        put(event, MANGLER, EntityNuuh::createAttributes);
        put(event, GNAT, EntityAta::createAttributes);
        put(event, LICE, EntityViin::createAttributes);
        put(event, BECKON_SI, EntityVenkrol::createAttributes);
        put(event, BECKON_SII, EntityVenkrolSII::createAttributes);
        put(event, BECKON_SIII, EntityVenkrolSIII::createAttributes);
        put(event, BECKON_SIV, EntityVenkrolSIV::createAttributes);
        put(event, DISPATCHERTEN, EntityDodT::createAttributes);
        put(event, DISPATCHER_SI, EntityDod::createAttributes);
        put(event, DISPATCHER_SII, EntityDodSII::createAttributes);
        put(event, DISPATCHER_SIII, EntityDodSIII::createAttributes);
        put(event, DISPATCHER_SIV, EntityDodSIV::createAttributes);
        put(event, ROOTERBALL, EntityLeemB::createAttributes);
        put(event, ROOTER_SI, EntityLeem::createAttributes);
        put(event, ROOTER_SII, EntityLeemSII::createAttributes);
        put(event, ROOTER_SIII, EntityLeemSIII::createAttributes);
        put(event, ROOTER_SIV, EntityLeemSIV::createAttributes);
        put(event, KYPHOSIS, EntityTonro::createAttributes);
        put(event, SENTRY, EntityUnvo::createAttributes);
        put(event, SEIZER, EntityNak::createAttributes);
        put(event, WORM, EntityRof::createAttributes);
        put(event, INCOMPLETEFORM_SMALL, EntityInhooS::createAttributes);
        put(event, INCOMPLETEFORM_MEDIUM, EntityInhooM::createAttributes);
        put(event, HOST, EntityHost::createAttributes);
        put(event, HOSTII, EntityHostII::createAttributes);
        put(event, HEED, EntityHeed::createAttributes);
        put(event, CRUX, EntityCruxA::createAttributes);
        put(event, CRUX_INCOMPLETE, EntityCruxB::createAttributes);
        put(event, THRALL, EntityMes::createAttributes);
        put(event, DREDGE, EntityDone::createAttributes);
        put(event, AIRSCREW, EntityLeer::createAttributes);
        put(event, CARRIER_WORM, EntityQuac::createAttributes);
        put(event, PRI_LONGARMS, EntityShyco::createAttributes);
        put(event, PRI_MANDUCATER, EntityHull::createAttributes);
        put(event, PRI_REEKER, EntityNogla::createAttributes);
        put(event, PRI_YELLOWEYE, EntityEmana::createAttributes);
        put(event, PRI_SUMMONER, EntityCanra::createAttributes);
        put(event, PRI_BOLSTER, EntityBano::createAttributes);
        put(event, PRI_TOZOON, EntityWymo::createAttributes);
        put(event, PRI_ARACHNIDA, EntityRanrac::createAttributes);
        put(event, PRI_DEVOURER, EntityLum::createAttributes);
        put(event, PRI_VERMIN, EntityIki::createAttributes);
        put(event, PRI_VISCERA, EntityGim::createAttributes);
        put(event, PRI_BURROWER, EntityZaa::createAttributes);
        put(event, ADA_LONGARMS, EntityShycoAdapted::createAttributes);
        put(event, ADA_MANDUCATER, EntityHullAdapted::createAttributes);
        put(event, ADA_REEKER, EntityNoglaAdapted::createAttributes);
        put(event, ADA_YELLOWEYE, EntityEmanaAdapted::createAttributes);
        put(event, ADA_SUMMONER, EntityCanraAdapted::createAttributes);
        put(event, ADA_BOLSTER, EntityBanoAdapted::createAttributes);
        put(event, ADA_TOZOON, EntityWymoAdapted::createAttributes);
        put(event, ADA_ARACHNIDA, EntityRanracAdapted::createAttributes);
        put(event, ADA_DEVOURER, EntityLumAdapted::createAttributes);
        put(event, ADA_VERMIN, EntityIkiAdapted::createAttributes);
        put(event, ADA_VISCERA, EntityGimAdapted::createAttributes);
        put(event, ADA_BURROWER, EntityZaaAdapted::createAttributes);
        put(event, OVERSEER, EntityAlafha::createAttributes);
        put(event, VIGILANTE, EntityAnged::createAttributes);
        put(event, WARDEN, EntityGanro::createAttributes);
        put(event, BOMBER_LIGHT, EntityOmboo::createAttributes);
        put(event, MARAUDER, EntityEsor::createAttributes);
        put(event, MONARCH, EntityOrch::createAttributes);
        put(event, GRUNT, EntityFlog::createAttributes);
        put(event, BOMBER_HEAVY, EntityJinjo::createAttributes);
        put(event, WRAITH, EntityElvia::createAttributes);
        put(event, BOGLE, EntityLencia::createAttributes);
        put(event, HAUNTER, EntityPheon::createAttributes);
        put(event, CARRIER_COLONY, EntityVesta::createAttributes);
        put(event, SUCCOR, EntityFlam::createAttributes);
        put(event, SEEKER, EntitySoo::createAttributes);
        put(event, ARCHITECT, EntityTenn::createAttributes);
        put(event, ANC_DREADNAUT, EntityOronco::createAttributes);
        put(event, ANC_OVERLORD, EntityTerla::createAttributes);
        put(event, ANC_POD, EntityDropPod::createAttributes);
        put(event, ANC_DREADNAUT_TEN, EntityOroncoTen::createAttributes);
        put(event, KIRIN, EntityKirin::createAttributes);
        put(event, DRACONITE, EntityHeblu::createAttributes);
    }
}
