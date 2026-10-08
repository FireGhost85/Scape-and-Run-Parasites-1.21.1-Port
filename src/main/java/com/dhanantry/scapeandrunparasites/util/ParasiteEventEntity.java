package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityParasiticScent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCrude;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPHijacked;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityBanoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityCanraAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityGimAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityHullAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityNoglaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityRanracAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityWymoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityZaaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityRof;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityTonro;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerBear;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerCow;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHorse;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerPig;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfBear;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfCow;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHorse;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPig;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeCow;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityBano;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityCanra;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityGim;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityIki;
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
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityTenn;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileBiomass;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.MovingSoundPayload;
import com.dhanantry.scapeandrunparasites.network.ParticlePayload;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import com.dhanantry.scapeandrunparasites.world.SRPExplosion;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;

public class ParasiteEventEntity {
    public static boolean canSpawnNext = true;

    public static int entityChunkCount(Level world, BlockPos pos, Class<? extends LivingEntity> mobC) {
        ChunkPos chunk = new ChunkPos(pos);
        AABB area = new AABB(chunk.getMinBlockX(), world.getMinBuildHeight(), chunk.getMinBlockZ(), chunk.getMaxBlockX() + 1, world.getMaxBuildHeight(), chunk.getMaxBlockZ() + 1);
        return world.getEntitiesOfClass(mobC, area).size();
    }

    public static boolean checkEntity(LivingEntity entity, String[] list, boolean inverted) {
        ResourceLocation enti = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (enti != null) {
            return ParasiteEventEntity.checkName(enti.toString(), list, inverted);
        }
        return false;
    }

    /**
     * Blacklist test of 1.10.9: true when the element contains any entry of the list (blacklist), inverted for a whitelist.
     * Entries are substrings, not exact names.
     */
    public static boolean checkName(@Nullable String potentialElement, String[] blacklist, boolean isWhitelist) {
        if (potentialElement == null) {
            return false;
        }
        return Arrays.stream(blacklist).anyMatch(potentialElement::contains) != isWhitelist;
    }

    public static void orbApplyEffects(LivingEntity target, EntityParasiteBase in, String[] effects, int mobs) {
        for (String i : effects) {
            String[] here = i.split(";");
            try {
                if (here[5] == null) {
                    return;
                }
            }
            catch (Exception e) {
                return;
            }
            Holder<MobEffect> potionE = SRPEntityUtil.effect(here[3]);
            if (potionE == null) continue;
            int self = Integer.parseInt(here[0]);
            int duration = Integer.parseInt(here[1]) * 20;
            int amp = Integer.parseInt(here[2]);
            int enemiesA = Integer.parseInt(here[4]);
            int enemiesD = Integer.parseInt(here[5]);
            if (enemiesA != 0) {
                amp += mobs / enemiesA;
            }
            if (enemiesD != 0) {
                duration += mobs / enemiesD * 20;
            }
            if (self == 1) {
                in.addEffect(new MobEffectInstance(potionE, duration, amp, false, false));
                continue;
            }
            if (self == 2) {
                if (!(target instanceof EntityParasiteBase)) continue;
                SRPPotions.applyStackPotion(potionE, target, duration, amp);
                continue;
            }
            if (target instanceof EntityParasiteBase) continue;
            SRPPotions.applyStackPotion(potionE, target, duration, amp);
        }
    }

    public static void spawnNext(EntityParasiteBase entityin, EntityParasiteBase entityout, boolean effects, boolean thunder) {
        if (entityin.isRemoved()) {
            return;
        }
        if (entityout == null) {
            return;
        }
        boolean flag = entityin.isOnFire();
        entityin.discard();
        entityout.moveTo(entityin.getX(), entityin.getY(), entityin.getZ(), entityin.getYRot(), entityin.getXRot());
        entityout.finalizeSpawn((ServerLevel) entityout.level(), entityin.level().getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        entityout.cannotDespawn(entityin.removeWhenFarAway(0.0));
        if (entityin.hasCustomName()) {
            SRPEntityUtil.setCustomNameTag(entityout, SRPEntityUtil.getCustomNameTag(entityin));
            entityout.setCustomNameVisible(entityin.isCustomNameVisible());
        }
        entityin.level().addFreshEntity((Entity)entityout);
        if (entityin instanceof EntityPMalleable && entityout instanceof EntityPMalleable) {
            ((EntityPMalleable)entityout).copyResistancesFrom((EntityPMalleable)entityin);
        }
        if (effects) {
            entityout.particleStatus((byte)7);
        }
        if (thunder && SRPConfig.thunderEnable) {
            SRPEntityUtil.lightning(entityout.level(), entityout.getX(), entityout.getY(), entityout.getZ(), true);
        }
        if (flag) {
            entityout.setHealth(entityout.getMaxHealth() * 0.5f);
            entityout.igniteForSeconds(8);
        }
    }

    public static void spawnFromList(Entity entityin, String[] out, @Nullable LivingEntity target) {
        Mob entityout = (Mob)SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(out[entityin.level().random.nextInt(out.length)]), (Level)entityin.level());
        if (entityout == null) {
            return;
        }
        entityout.copyPosition(entityin);
        entityout.finalizeSpawn((ServerLevel) entityout.level(), entityin.level().getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        entityin.level().addFreshEntity((Entity)entityout);
        if (target != null) {
            entityout.setTarget(target);
        }
    }

    public static boolean spawnBiomassFromProjectile(EntityParasiteBase entityin, String[] out, @Nullable LivingEntity target) {
        if (!entityin.level().isClientSide) {
            RandomSource rand = RandomSource.create();
            int index = rand.nextInt(out.length);
            int limit = 0;
            boolean flag = true;
            while (flag) {
                if (index >= out.length) {
                    index = 0;
                    ++limit;
                }
                if (limit == 2) {
                    return false;
                }
                if (out[index] != null) {
                    String[] entityC = out[index].split(";");
                    double chance = Double.parseDouble(entityC[1]);
                    if (rand.nextDouble() <= chance) {
                        EntityCanSummon father = (EntityCanSummon)(entityin);
                        int points = Integer.parseInt(entityC[2]);
                        if (father.getTotalParasites() - father.getActualParasites() < points) {
                            ++index;
                            continue;
                        }
                        if (target == null) {
                            return false;
                        }
                        Vec3 vec3d = entityin.getViewVector(1.0f);
                        double d2 = target.getX() - (entityin.getX() + vec3d.x);
                        double d3 = target.getBoundingBox().minY + (double)(target.getBbHeight() / 2.0f) - (0.5 + entityin.getY() + (double)(entityin.getBbHeight() / 2.0f));
                        double d4 = target.getZ() - (entityin.getZ() + vec3d.z);
                        EntityProjectileBiomass entityout = new EntityProjectileBiomass(SRPEntities.BIOMASSBALL.get(), entityin.level(), (LivingEntity)entityin, d2, d3, d4);
                        Mot.setPosX(entityout, entityin.getX() + vec3d.x);
                        Mot.setPosY(entityout, entityin.getY() + (double)entityin.getEyeHeight() - 0.2);
                        Mot.setPosZ(entityout, entityin.getZ() + vec3d.z);
                        entityout.setParasite(entityC[0], points, 4);
                        father.setActualParasites(points);
                        father.addID(entityout.getId(), points);
                        entityin.level().addFreshEntity((Entity)entityout);
                        flag = false;
                        return true;
                    }
                }
                ++index;
            }
        }
        return false;
    }

    public static boolean spawnBiomassFromVomit(EntityParasiteBase entityin, String[] out, @Nullable LivingEntity target) {
        if (!entityin.level().isClientSide) {
            RandomSource rand = RandomSource.create();
            int index = rand.nextInt(out.length);
            int limit = 0;
            boolean flag = true;
            while (flag) {
                if (index >= out.length) {
                    index = 0;
                    ++limit;
                }
                if (limit == 2) {
                    return false;
                }
                if (out[index] != null) {
                    String[] entityC = out[index].split(";");
                    double chance = Double.parseDouble(entityC[1]);
                    if (rand.nextDouble() <= chance) {
                        EntityCanSummon father = (EntityCanSummon)(entityin);
                        int points = Integer.parseInt(entityC[2]);
                        if (father.getTotalParasites() - father.getActualParasites() < points) {
                            ++index;
                            continue;
                        }
                        EntityBiomass entityout = new EntityBiomass(SRPEntities.BIOMASS.get(), entityin.level(), entityin, target);
                        entityout.moveTo(entityin.getX(), entityin.getY(), entityin.getZ(), entityin.getYRot(), entityin.getXRot());
                        float f19 = Mth.sin((float)(entityin.getYRot() * ((float)Math.PI / 180) - entityin.rotA * 0.01f));
                        float f14 = 0.17453292f;
                        float f16 = Mth.cos((float)f14);
                        float f4 = Mth.cos((float)(entityin.getYRot() * ((float)Math.PI / 180) - entityin.rotA * 0.01f));
                        entityout.setYRot(entityin.getYRot());
                        if (entityout.level().getBlockState(BlockPos.containing(entityin.getX() + -1.0 * (double)(f19 * 3.0f * f16), entityin.getY() + (double)entityin.getEyeHeight(), entityin.getZ() - -1.0 * (double)(f4 * 3.0f * f16))).getBlock() != Blocks.AIR) {
                            entityout.discard();
                            return false;
                        }
                        entityout.setPos(entityin.getX() + -1.0 * (double)(f19 * 3.0f * f16), entityin.getY() + (double)entityin.getEyeHeight(), entityin.getZ() - -1.0 * (double)(f4 * 3.0f * f16));
                        entityout.setFuse(80);
                        entityout.setParasite(entityC[0], points);
                        if (entityin instanceof EntityCanra) {
                            entityout.setSkin(5);
                        } else {
                            entityout.setSkin(6);
                        }
                        father.setActualParasites(points);
                        father.addID(entityout.getId(), points);
                        if (entityin.isOnFire()) {
                            entityout.igniteForSeconds(8);
                        }
                        entityin.level().addFreshEntity((Entity)entityout);
                        flag = false;
                        return true;
                    }
                }
                ++index;
            }
        }
        return false;
    }

    private static boolean getWorldBeckonSpawnLimit(EntityParasiteBase entityin) {
        int count = 0;
        List<? extends Entity> entities = SRPEntityUtil.allEntities(entityin.level());
        for (Entity entity : entities) {
            if (!(entity instanceof EntityParasiteBase)) continue;
            ++count;
        }
        int players = entityin.level().players().size();
        return count < SRPConfig.worldMobCap + players * SRPConfig.worldMobCapPlusPlayer + SRPConfig.worldBeckonSpawnsCap;
    }

    public static boolean spawnBiomassFromBeckon(EntityParasiteBase entityin, int stage, LivingEntity target, boolean payfather, String[] ground, String[] air) {
        if (!entityin.level().isClientSide) {
            String[] mobListG = ground;
            if (entityin.getY() + 3.0 <= target.getY()) {
                if (stage == 1) {
                    return false;
                }
                mobListG = air;
            }
            RandomSource rand = RandomSource.create();
            int index = rand.nextInt(mobListG.length);
            int limit = 0;
            boolean flag = true;
            while (flag) {
                if (index >= mobListG.length) {
                    index = 0;
                    ++limit;
                }
                if (limit == 2) {
                    return false;
                }
                if (mobListG[index] != null) {
                    double k;
                    double d7;
                    double d6;
                    double d5;
                    double d4;
                    double d3;
                    double d2;
                    double d1;
                    double d0;
                    String[] entityC = mobListG[index].split(";");
                    if (entityC.length != 3) {
                        ScapeAndRunParasites.LOGGER.error("Malformed string: " + mobListG[index].toString() + " in the beckon spawn pool configuration, safely exiting loop. Did you forget a semicolon?");
                        return false;
                    }
                    double chance = Double.parseDouble(entityC[1]);
                    EntityCanSummon father = (EntityCanSummon)(entityin);
                    int points = Integer.parseInt(entityC[2]);
                    if (father.getTotalParasites() - father.getActualParasites() < points && payfather) {
                        ++index;
                    }
                    double b = 0.0;
                    if (stage == 3) {
                        b = 0.5;
                    }
                    if (!ParasiteEventEntity.getWorldBeckonSpawnLimit(entityin)) {
                        EntityBomb entityAlt = new EntityBomb(SRPEntities.BOMB.get(), entityin.level(), entityin, false);
                        if (entityin.level().random.nextInt() < 40) {
                            return false;
                        }
                        if (entityin.getTarget() != null) {
                            entityAlt.moveTo(entityin.getX(), entityin.getY() + ((double)entityin.getEyeHeight() + b), entityin.getZ(), entityin.getYRot(), entityin.getXRot());
                            d0 = (float)entityin.getX() + entityin.level().random.nextFloat();
                            d1 = (float)entityin.getY() + entityin.getEyeHeight() + entityin.level().random.nextFloat();
                            d2 = (float)entityin.getZ() + entityin.level().random.nextFloat();
                            d3 = d0 - entityin.getX();
                            d4 = d1 - entityin.getY();
                            d5 = d2 - entityin.getZ();
                            d6 = (float)Math.sqrt((double)(d3 * d3 + d4 * d4 + d5 * d5));
                            d3 /= d6;
                            d4 /= d6;
                            d5 /= d6;
                            d7 = 0.5 / (d6 / 4.0 + 0.1);
                            d7 *= (double)(entityin.level().random.nextFloat() * entityin.level().random.nextFloat() + 1.7f);
                            k = 3.0;
                            if (stage == 3) {
                                k = 5.0;
                            }
                            d5 = d7 * k;
                            entityAlt.setMotion(d3 *= d7 * k, d4 *= d7 * 2.0, d5, 0.4, 0.5);
                            entityAlt.setFuse(60);
                            entityAlt.setStren(0.0f);
                            entityAlt.setSkin(1);
                            entityAlt.setDamage((float)entityin.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue(), 2);
                            entityAlt.setXRot(entityAlt.getXRot() - (-20.0f));
                            entityin.level().addFreshEntity((Entity)entityAlt);
                            entityAlt.updateSTR();
                            PacketDistributor.sendToAllPlayers(new ParticlePayload(entityin.getX(), entityin.getY(), entityin.getZ(), 0.5f, 0.5f, 10));
                        }
                    } else {
                        EntityBiomass entityout = new EntityBiomass(SRPEntities.BIOMASS.get(), entityin.level(), entityin, stage, target, payfather);
                        entityout.moveTo(entityin.getX(), entityin.getY() + ((double)entityin.getEyeHeight() + b), entityin.getZ(), entityin.getYRot(), entityin.getXRot());
                        d0 = (float)entityin.getX() + entityin.level().random.nextFloat();
                        d1 = (float)entityin.getY() + entityin.getEyeHeight() + entityin.level().random.nextFloat();
                        d2 = (float)entityin.getZ() + entityin.level().random.nextFloat();
                        d3 = d0 - entityin.getX();
                        d4 = d1 - entityin.getY();
                        d5 = d2 - entityin.getZ();
                        d6 = (float)Math.sqrt((double)(d3 * d3 + d4 * d4 + d5 * d5));
                        d3 /= d6;
                        d4 /= d6;
                        d5 /= d6;
                        d7 = 0.5 / (d6 / 4.0 + 0.1);
                        d7 *= (double)(entityin.level().random.nextFloat() * entityin.level().random.nextFloat() + 1.7f);
                        k = 3.0;
                        if (stage == 3) {
                            k = 5.0;
                        }
                        d5 = d7 * k;
                        entityout.setMotion(d3 *= d7 * k, d4 *= d7 * 2.0, d5, 0.4, 0.5);
                        entityout.setFuse(80);
                        entityout.setParasite(entityC[0], points);
                        entityout.setSkin(stage);
                        if (entityin.isOnFire()) {
                            entityout.igniteForSeconds(8);
                        }
                        entityin.level().addFreshEntity((Entity)entityout);
                        if (payfather) {
                            father.setActualParasites(points);
                            father.addID(entityout.getId(), points);
                        }
                        flag = false;
                        return true;
                    }
                }
                ++index;
            }
        }
        return false;
    }

    public static void convertEntity(LivingEntity entityin, CompoundTag tags, boolean ignoreKey, String[] list) {
        if (entityin == null) {
            return;
        }
        Level world = entityin.level();
        if (world.isClientSide) {
            return;
        }
        if (SRPSaveData.get(entityin.level()).getEvolutionPhase(DimKeys.of(entityin.level())) >= SRPConfigSystems.evolutionFeralNoSim && ParasiteEventEntity.convertEntityFeral(entityin, tags, true, list)) {
            return;
        }
        if (tags.contains("srpcothimmunity")) {
            String mobname;
            int goo;
            int key = tags.getInt("srpcothimmunity");
            if (key == 0 && !ignoreKey) {
                entityin.removeEffect(SRPPotions.COTH_E);
                return;
            }
            entityin.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3, false, false));
            PacketDistributor.sendToAllPlayers(new ParticlePayload(entityin.getX(), entityin.getY(), entityin.getZ(), entityin.getBbWidth(), entityin.getBbHeight(), 1));
            tags.putInt("srpcothimmunity", ++key);
            if (key < 3 && !ignoreKey) {
                return;
            }
            SRPSaveData dataLol = SRPSaveData.get(entityin.level());
            int n = goo = SRPConfigSystems.disloCOTHTiers ? dataLol.getCurrentCode(DimKeys.of(entityin.level()), 1) : 0;
            if (goo != 0) {
                EntityParasiteBase halo = ParasiteEventEntity.getRandomFeral(entityin.level());
                if (goo >= SRPConfigSystems.disloCOTHTiersValue1) {
                    halo = ParasiteEventEntity.getRandomPrimitive(entityin.level());
                }
                if (goo >= SRPConfigSystems.disloCOTHTiersValue2) {
                    halo = ParasiteEventEntity.getRandomAdapted(entityin.level());
                }
                if (goo >= SRPConfigSystems.disloCOTHTiersValue3) {
                    halo = ParasiteEventEntity.getRandomPure(entityin.level());
                }
                halo.copyPosition((Entity)entityin);
                entityin.discard();
                halo.finalizeSpawn((ServerLevel) halo.level(), world.getCurrentDifficultyAt(halo.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                if (entityin.hasCustomName()) {
                    SRPEntityUtil.setCustomNameTag(halo, SRPEntityUtil.getCustomNameTag(entityin));
                    halo.setCustomNameVisible(entityin.isCustomNameVisible());
                }
                world.addFreshEntity((Entity)halo);
                world.levelEvent(null, 1026, halo.blockPosition(), 0);
                halo.particleStatus((byte)7);
                halo.cannotDespawn(SRPConfig.convertedDespawn);
                if (key >= 10) {
                    halo.addEffect(new MobEffectInstance(SRPPotions.EPEL_E, 600, 0, false, false));
                }
                return;
            }
            try {
                mobname = BuiltInRegistries.ENTITY_TYPE.getKey(entityin.getType()).toString();
            }
            catch (Exception e) {
                ScapeAndRunParasites.LOGGER.error("Problem while converting entity", e);
                ParasiteEventEntity.spawnInsider(entityin, world, tags);
                return;
            }
            boolean flag = true;
            for (String s : list) {
                EntityPInfected entityout;
                String[] here = s.split(";");
                try {
                    if (here[0] == null || here[1] == null) {
                        ParasiteEventEntity.spawnInsider(entityin, world, tags);
                        return;
                    }
                }
                catch (Exception e) {
                    ScapeAndRunParasites.LOGGER.error("Problem while converting entity", e);
                    ParasiteEventEntity.spawnInsider(entityin, world, tags);
                    return;
                }
                if (!here[0].equals(mobname)) continue;
                Entity outOne = SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(here[1]), (Level)world);
                if (outOne == null) {
                    ParasiteEventEntity.spawnInsider(entityin, world, tags);
                    return;
                }
                if (outOne instanceof EntityPInfected) {
                    entityout = (EntityPInfected)outOne;
                    SRPSaveData.get(world).addNumberIDDataSpawn(entityout.getParasiteIDRegister());
                    entityout.copyPosition((Entity)entityin);
                    entityin.discard();
                    entityout.setHost(mobname);
                    entityout.finalizeSpawn((ServerLevel) entityout.level(), world.getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                    if (entityin.hasCustomName()) {
                        SRPEntityUtil.setCustomNameTag(entityout, SRPEntityUtil.getCustomNameTag(entityin));
                        entityout.setCustomNameVisible(entityin.isCustomNameVisible());
                    }
                    world.addFreshEntity((Entity)entityout);
                    world.levelEvent(null, 1026, entityout.blockPosition(), 0);
                    if (SRPConfigSystems.generationUse) {
                        entityout.setHealth(entityout.getHealth() * ParasiteEventEntity.getSimCOTHMod(dataLol, world));
                    }
                    entityout.particleStatus((byte)7);
                    entityout.cannotDespawn(SRPConfig.convertedDespawn);
                    if (key >= 10) {
                        entityout.addEffect(new MobEffectInstance(SRPPotions.EPEL_E, 600, 0, false, false));
                    }
                    AABB axisalignedbb = new AABB(entityout.blockPosition()).inflate(14.0);
                    List<? extends LivingEntity> moblist = entityout.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                    for (LivingEntity mob : moblist) {
                        if (!mob.hasEffect(SRPPotions.COTH_E) || !(tags = mob.getPersistentData()).contains("srpcothimmunity") || (key = tags.getInt("srpcothimmunity")) != 1 || mob.getEffect(SRPPotions.COTH_E).getAmplifier() <= 1) continue;
                        tags.putInt("srpcothimmunity", ++key);
                    }
                } else if (outOne instanceof Mob) {
                    Mob mobOut = (Mob)outOne;
                    mobOut.copyPosition((Entity)entityin);
                    entityin.discard();
                    mobOut.finalizeSpawn((ServerLevel) mobOut.level(), world.getCurrentDifficultyAt(mobOut.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                    if (entityin.hasCustomName()) {
                        SRPEntityUtil.setCustomNameTag(mobOut, SRPEntityUtil.getCustomNameTag(entityin));
                        mobOut.setCustomNameVisible(entityin.isCustomNameVisible());
                    }
                    world.addFreshEntity((Entity)mobOut);
                    world.levelEvent(null, 1026, mobOut.blockPosition(), 0);
                }
                flag = false;
            }
            if (flag && !ignoreKey) {
                ParasiteEventEntity.spawnInsider(entityin, world, tags);
            }
        }
    }

    public static void spawnInsider(LivingEntity entity, Level world, CompoundTag tags) {
        if (!SRPConfigMobs.inhooSEnabled || !SRPConfigMobs.inhooMEnabled) {
            return;
        }
        List<? extends Entity> serverList = SRPEntityUtil.allEntities(world);
        int count = 0;
        for (Entity value : serverList) {
            if (!(value instanceof EntityInhooM) && !(value instanceof EntityInhooS)) continue;
            ++count;
        }
        if (count > SRPConfig.incompleteCap) {
            entity.discard();
            return;
        }
        if (tags.contains("srpcothimmunity")) {
            int key = tags.getInt("srpcothimmunity");
            if (key == 0) {
                entity.removeEffect(SRPPotions.COTH_E);
                return;
            }
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3, false, false));
            PacketDistributor.sendToAllPlayers(new ParticlePayload(entity.getX(), entity.getY(), entity.getZ(), entity.getBbWidth(), entity.getBbHeight(), 1));
            tags.putInt("srpcothimmunity", ++key);
            if (key < 3) {
                return;
            }
            EntityPCrude out = new EntityInhooS(SRPEntities.INCOMPLETEFORM_SMALL.get(), world);
            float mass = ParasiteEventEntity.getEntityArea(entity);
            if ((double)mass > 0.517) {
                out = new EntityInhooM(SRPEntities.INCOMPLETEFORM_MEDIUM.get(), world);
            }
            out.copyPosition((Entity)entity);
            entity.discard();
            world.addFreshEntity((Entity)out);
            world.levelEvent(null, 1026, out.blockPosition(), 0);
            out.particleStatus((byte)7);
            out.cannotDespawn(SRPConfig.convertedDespawn);
            if (SRPConfigSystems.generationUse) {
                out.setHealth(out.getHealth() * ParasiteEventEntity.getSimCOTHMod(SRPSaveData.get(world), world));
            }
            int range = 1;
            double i1 = Mth.floor((double)(out.getY() + 0.1));
            double l1 = out.getX();
            double i2 = out.getZ();
            int counttt = 0;
            count = 2;
            for (int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
                for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                    double i3 = l1 + (double)k2;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = BlockPos.containing(i3, i1, l);
                    Block block = out.level().getBlockState(blockpos).getBlock();
                    Block blockDown = out.level().getBlockState(blockpos.below()).getBlock();
                    if (block != Blocks.AIR || blockDown == Blocks.AIR || !world.getBlockState(blockpos.below()).isCollisionShapeFullBlock(world, blockpos.below()) || blockDown == SRPBlocks.InfestedStain.get() || out.level().random.nextInt(4) != 0) continue;
                    out.level().setBlockAndUpdate(blockpos, SRPBlocks.goreSim.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.FLAT)));
                    if (++counttt < count) continue;
                    return;
                }
            }
        }
    }

    private static float getEntityArea(LivingEntity entity) {
        return entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight();
    }

    private static float getSimCOTHMod(SRPSaveData data, Level world) {
        switch (data.getGeneration(DimKeys.of(world))) {
            case 0: {
                return SRPConfigSystems.generationCOTH0;
            }
            case 1: {
                return SRPConfigSystems.generationCOTH1;
            }
            case 2: {
                return SRPConfigSystems.generationCOTH2;
            }
            case 3: {
                return SRPConfigSystems.generationCOTH3;
            }
            case 4: {
                return SRPConfigSystems.generationCOTH4;
            }
            case 5: {
                return SRPConfigSystems.generationCOTH5;
            }
        }
        return 1.0f;
    }

    public static boolean convertEntityFeral(LivingEntity entityin, CompoundTag tags, boolean ignoreKey, String[] list) {
        Level world = entityin.level();
        if (world.isClientSide) {
            return false;
        }
        if (entityin == null) {
            return false;
        }
        if (tags.contains("srpcothimmunity")) {
            String mobname;
            int key = tags.getInt("srpcothimmunity");
            if (key == 0 && !ignoreKey) {
                entityin.removeEffect(SRPPotions.COTH_E);
                return false;
            }
            entityin.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3, false, false));
            PacketDistributor.sendToAllPlayers(new ParticlePayload(entityin.getX(), entityin.getY(), entityin.getZ(), entityin.getBbWidth(), entityin.getBbHeight(), 1));
            tags.putInt("srpcothimmunity", ++key);
            if (key < 3 && !ignoreKey) {
                return false;
            }
            try {
                mobname = BuiltInRegistries.ENTITY_TYPE.getKey(entityin.getType()).toString();
            }
            catch (Exception e) {
                ScapeAndRunParasites.LOGGER.error("Problem while converting entity", e);
                ParasiteEventEntity.spawnInsider(entityin, world, tags);
                return false;
            }
            boolean flag = true;
            for (String s : list) {
                String[] here = s.split(";");
                try {
                    if (here[0] == null || here[1] == null) {
                        ParasiteEventEntity.spawnInsider(entityin, world, tags);
                        return false;
                    }
                }
                catch (Exception e) {
                    ScapeAndRunParasites.LOGGER.error("Problem while converting entity", e);
                    ParasiteEventEntity.spawnInsider(entityin, world, tags);
                    return false;
                }
                if (!here[0].equals(mobname)) continue;
                Entity outOne = SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(here[1]), (Level)world);
                if (outOne == null) {
                    ParasiteEventEntity.spawnInsider(entityin, world, tags);
                    return false;
                }
                if (outOne instanceof EntityPInfected) {
                    EntityPFeral gaa = ((EntityPInfected)outOne).getFeral(world);
                    if (gaa != null) {
                        outOne.discard();
                        gaa.copyPosition((Entity)entityin);
                        entityin.discard();
                        gaa.finalizeSpawn((ServerLevel) gaa.level(), world.getCurrentDifficultyAt(gaa.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                        if (entityin.hasCustomName()) {
                            SRPEntityUtil.setCustomNameTag(gaa, SRPEntityUtil.getCustomNameTag(entityin));
                            gaa.setCustomNameVisible(entityin.isCustomNameVisible());
                        }
                        world.addFreshEntity((Entity)gaa);
                        world.levelEvent(null, 1026, gaa.blockPosition(), 0);
                        gaa.particleStatus((byte)7);
                        gaa.cannotDespawn(SRPConfig.convertedDespawn);
                    } else {
                        EntityPInfected entityout = (EntityPInfected)outOne;
                        SRPSaveData.get(world).addNumberIDDataSpawn(entityout.getParasiteIDRegister());
                        entityout.copyPosition((Entity)entityin);
                        entityin.discard();
                        entityout.setHost(mobname);
                        entityout.finalizeSpawn((ServerLevel) entityout.level(), world.getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                        if (entityin.hasCustomName()) {
                            SRPEntityUtil.setCustomNameTag(entityout, SRPEntityUtil.getCustomNameTag(entityin));
                            entityout.setCustomNameVisible(entityin.isCustomNameVisible());
                        }
                        world.addFreshEntity((Entity)entityout);
                        world.levelEvent(null, 1026, entityout.blockPosition(), 0);
                        entityout.particleStatus((byte)7);
                        entityout.cannotDespawn(SRPConfig.convertedDespawn);
                        if (key >= 10) {
                            entityout.addEffect(new MobEffectInstance(SRPPotions.EPEL_E, 600, 0, false, false));
                        }
                        AABB axisalignedbb = new AABB(entityout.blockPosition()).inflate(14.0);
                        List<? extends LivingEntity> moblist = entityout.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                        for (LivingEntity mob : moblist) {
                            if (!mob.hasEffect(SRPPotions.COTH_E) || !(tags = mob.getPersistentData()).contains("srpcothimmunity") || (key = tags.getInt("srpcothimmunity")) != 1 || mob.getEffect(SRPPotions.COTH_E).getAmplifier() <= 1) continue;
                            tags.putInt("srpcothimmunity", ++key);
                        }
                    }
                    return true;
                }
                if (outOne instanceof Mob) {
                    Mob entityout = (Mob)outOne;
                    entityout.copyPosition((Entity)entityin);
                    entityin.discard();
                    entityout.finalizeSpawn((ServerLevel) entityout.level(), world.getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                    if (entityin.hasCustomName()) {
                        SRPEntityUtil.setCustomNameTag(entityout, SRPEntityUtil.getCustomNameTag(entityin));
                        entityout.setCustomNameVisible(entityin.isCustomNameVisible());
                    }
                    world.addFreshEntity((Entity)entityout);
                    world.levelEvent(null, 1026, entityout.blockPosition(), 0);
                    return true;
                }
                flag = false;
            }
            if (flag && !ignoreKey) {
                ParasiteEventEntity.spawnInsider(entityin, world, tags);
            }
        }
        return false;
    }

    public static boolean hijackEntity(LivingEntity entityin, String[] list) {
        String mobname;
        if (entityin == null) {
            return false;
        }
        Level world = entityin.level();
        if (world.isClientSide) {
            return false;
        }
        try {
            mobname = BuiltInRegistries.ENTITY_TYPE.getKey(entityin.getType()).toString();
        }
        catch (Exception e) {
            ScapeAndRunParasites.LOGGER.error("Problem while converting entity", e);
            return false;
        }
        boolean flag = true;
        for (String s : list) {
            EntityPHijacked entityout;
            String[] here = s.split(";");
            try {
                if (here[0] == null || here[1] == null) {
                    return false;
                }
            }
            catch (Exception e) {
                ScapeAndRunParasites.LOGGER.error("Problem while converting entity", e);
                return false;
            }
            if (!here[0].equals(mobname)) continue;
            Entity outOne = SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(here[1]), (Level)world);
            if (outOne == null) {
                return false;
            }
            if (outOne instanceof EntityPHijacked) {
                entityout = (EntityPHijacked)outOne;
                SRPSaveData.get(world).addNumberIDDataSpawn(entityout.getParasiteIDRegister());
                entityout.copyPosition((Entity)entityin);
                entityin.discard();
                entityout.finalizeSpawn((ServerLevel) entityout.level(), world.getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                if (entityin.hasCustomName()) {
                    SRPEntityUtil.setCustomNameTag(entityout, SRPEntityUtil.getCustomNameTag(entityin));
                    entityout.setCustomNameVisible(entityin.isCustomNameVisible());
                }
                world.addFreshEntity((Entity)entityout);
                world.levelEvent(null, 1026, entityout.blockPosition(), 0);
                entityout.particleStatus((byte)7);
                entityout.cannotDespawn(SRPConfig.convertedDespawn);
            } else if (outOne instanceof Mob) {
                Mob mobOut = (Mob)outOne;
                mobOut.copyPosition((Entity)entityin);
                entityin.discard();
                mobOut.finalizeSpawn((ServerLevel) mobOut.level(), world.getCurrentDifficultyAt(mobOut.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                if (entityin.hasCustomName()) {
                    SRPEntityUtil.setCustomNameTag(mobOut, SRPEntityUtil.getCustomNameTag(entityin));
                    mobOut.setCustomNameVisible(entityin.isCustomNameVisible());
                }
                world.addFreshEntity((Entity)mobOut);
                world.levelEvent(null, 1026, mobOut.blockPosition(), 0);
            }
            flag = false;
        }
        return false;
    }

    public static BlockPos getFloor(Level worldIn, BlockPos pos, int loop) {
        if (loop <= 0) {
            return null;
        }
        --loop;
        if (worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
            if (worldIn.getBlockState(pos.below()).getBlock() != Blocks.AIR) {
                return pos;
            }
            return ParasiteEventEntity.getFloor(worldIn, pos.below(), loop);
        }
        return ParasiteEventEntity.getFloor(worldIn, pos.above(), loop);
    }

    public static BlockPos getFloorBuilding(Level worldIn, BlockPos pos, int loop) {
        if (loop <= 0) {
            return null;
        }
        --loop;
        if (!worldIn.getBlockState(pos).isCollisionShapeFullBlock(worldIn, pos)) {
            if (worldIn.getBlockState(pos.below()).isCollisionShapeFullBlock(worldIn, pos.below()) && !(worldIn.getBlockState(pos.below()).getBlock() instanceof BushBlock) && !(worldIn.getBlockState(pos.below()).is(BlockTags.LEAVES)) && !(worldIn.getBlockState(pos.below()).is(BlockTags.LOGS))) {
                return pos;
            }
            return ParasiteEventEntity.getFloorBuilding(worldIn, pos.below(), loop);
        }
        if (worldIn.getBlockState(pos).getBlock() instanceof BushBlock || worldIn.getBlockState(pos).is(BlockTags.LEAVES) || worldIn.getBlockState(pos).is(BlockTags.LOGS)) {
            return ParasiteEventEntity.getFloorBuilding(worldIn, pos.below(1), loop);
        }
        return ParasiteEventEntity.getFloorBuilding(worldIn, pos.above(), loop);
    }

    public static boolean spawnTurrets(LivingEntity entityin, int range, byte type, int stage) {
        if (stage <= 2) {
            return false;
        }
        if (entityin.level().getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }
        RandomSource rand = RandomSource.create();
        Level world = entityin.level();
        double randomx = rand.nextInt(range);
        double randomz = rand.nextInt(range);
        double negative = rand.nextInt(2);
        if (negative == 0.0) {
            randomx *= -1.0;
        }
        if ((negative = (double)rand.nextInt(2)) == 0.0) {
            randomz *= -1.0;
        }
        int index = 5;
        int limit = 0;
        boolean flag = true;
        while (flag) {
            if (limit >= 5) {
                return false;
            }
            BlockPos floor = ParasiteEventEntity.getFloor(world, BlockPos.containing(entityin.getX() + randomx, entityin.getY(), entityin.getZ() + randomz), 5);
            if (floor != null && world.getBlockState(floor.below()).getBlock() == SRPBlocks.InfestedStain.get()) {
                int flag2 = 0;
                AABB axisalignedbb = new AABB((double)floor.getX(), (double)floor.getY(), (double)floor.getZ(), (double)(floor.getX() + 1), (double)(floor.getY() + 1), (double)(floor.getZ() + 1)).expandTowards(42.0, 5.0, 42.0);
                List<? extends EntityParasiteBase> moblist = world.getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
                for (EntityParasiteBase mob : moblist) {
                    if (!mob.isAlive() || mob.getParasiteType() != 40) continue;
                    ++flag2;
                }
                if (flag2 < 3) {
                    EntityPStationary out;
                    if (type == 1) {
                        if (!SRPConfigMobs.unvoEnabled) {
                            return false;
                        }
                        out = new EntityUnvo(SRPEntities.SENTRY.get(), world);
                        out.moveTo(floor.getX(), floor.getY(), floor.getZ(), 0.0f, 0.0f);
                        world.addFreshEntity((Entity)out);
                        out.setTarget(entityin);
                        return true;
                    }
                    if (type == 2) {
                        if (!SRPConfigMobs.tonroEnabled) {
                            return false;
                        }
                        out = new EntityTonro(SRPEntities.KYPHOSIS.get(), world);
                        out.moveTo(floor.getX(), floor.getY(), floor.getZ(), 0.0f, 0.0f);
                        world.addFreshEntity((Entity)out);
                        out.setTarget(entityin);
                        return true;
                    }
                } else {
                    return false;
                }
            }
            randomx = rand.nextInt(range);
            randomz = rand.nextInt(range);
            negative = rand.nextInt(2);
            if (negative == 0.0) {
                randomx *= -1.0;
            }
            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                randomz *= -1.0;
            }
            ++limit;
        }
        return false;
    }

    public static void alertAllPlayerDim(Level worldIn, String message, int warning) {
        if (worldIn == null) {
            return;
        }
        List<? extends Player> playerEntityList = worldIn.players();
        PacketDistributor.sendToPlayersInDimension((ServerLevel) worldIn, new MovingSoundPayload(warning));
        if (!message.equals("")) {
            for (Player entityPlayer : playerEntityList) {
                entityPlayer.sendSystemMessage(Component.literal(message));
            }
        }
        if (warning == -7 && message.equals("Phase decreased")) {
            List<? extends Entity> serverList = SRPEntityUtil.allEntities(worldIn);
            for (Entity entity : serverList) {
                if (!(entity instanceof EntityParasiteBase)) continue;
                ((EntityParasiteBase)entity).addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 2400, 1, false, false));
            }
        }
    }

    public static void alertAllPlayerSer(Level w, String message) {
        if (w == null) {
            return;
        }
        List<? extends ServerPlayer> playerEntityList = w.getServer().getPlayerList().getPlayers();
        for (ServerPlayer entityPlayerMP : playerEntityList) {
            entityPlayerMP.sendSystemMessage(Component.literal(message));
        }
    }

    public static void alertAllPlayerSer(Level w, String message, int warning) {
        if (w == null) {
            return;
        }
        List<? extends ServerPlayer> playerEntityList = w.getServer().getPlayerList().getPlayers();
        // 1.12 sent this packet to the server (a no-op for the cue); kept out on purpose, see PORTING_NOTES.md
        for (ServerPlayer entityPlayerMP : playerEntityList) {
            entityPlayerMP.sendSystemMessage(Component.literal(message));
        }
    }

    public static boolean spawnFromBlock(Level world, String[] out, int range, BlockPos pos) {
        if (!world.isClientSide) {
            List<? extends Entity> serverList = SRPEntityUtil.allEntities(world);
            int count = 0;
            int tenn = 0;
            for (int x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityParasiteBase)) continue;
                ++count;
                if (serverList.get(x) instanceof EntityTenn) {
                    ++tenn;
                }
                if (count > SRPConfig.worldMobCap) {
                    return false;
                }
                if (tenn <= 5) continue;
                return false;
            }
            RandomSource rand = RandomSource.create();
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            double randomx = rand.nextInt(range);
            double randomz = rand.nextInt(range);
            double negative = rand.nextInt(2);
            if (negative == 0.0) {
                randomx *= -1.0;
            }
            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                randomz *= -1.0;
            }
            int index = rand.nextInt(out.length);
            int limit = 0;
            boolean flag = true;
            while (flag) {
                if (index >= out.length) {
                    index = 0;
                    ++limit;
                }
                if (limit == 2) {
                    return false;
                }
                if (out[index] != null) {
                    String[] entityC = out[index].split(";");
                    double chance = Double.parseDouble(entityC[1]);
                    if (rand.nextDouble() <= chance) {
                        BlockPos helper = ParasiteEventEntity.getFloor(world, BlockPos.containing(x + randomx, y, z + randomz), 3);
                        if (helper != null) {
                            Mob entityout = (Mob)SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(entityC[0]), (Level)world);
                            if (entityout == null) {
                                return false;
                            }
                            entityout.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16.0);
                            entityout.moveTo((double)helper.getX(), (double)helper.getY(), (double)helper.getZ(), 0.0f, 0.0f);
                            entityout.finalizeSpawn((ServerLevel) entityout.level(), world.getCurrentDifficultyAt(helper), MobSpawnType.MOB_SUMMONED, null);
                            if (entityout instanceof EntityKol) {
                                EntityKol kol = (EntityKol)entityout;
                                SRPWorldData data = SRPWorldData.get(world);
                                BlockPos origin = data.nearestColonyPosition(helper, false);
                                if (origin != null) {
                                    kol.setTask(origin, data.getColonyDistanceSpreadByPosition(origin, false));
                                } else {
                                    return false;
                                }
                            }
                            world.addFreshEntity((Entity)entityout);
                            flag = false;
                            return true;
                        }
                        randomx = rand.nextInt(range);
                        randomz = rand.nextInt(range);
                        negative = rand.nextInt(2);
                        if (negative == 0.0) {
                            randomx *= -1.0;
                        }
                        if ((negative = (double)rand.nextInt(2)) == 0.0) {
                            randomz *= -1.0;
                        }
                    }
                }
                ++index;
            }
        }
        return false;
    }

    public static void spawnBeckon(Level world, DamageSource cause, EntityParasiteBase in) {
        if (in.getBbWidth() <= 1.0f && in.getBbHeight() <= 1.0f) {
            return;
        }
        if (SRPConfigSystems.rsEnabled) {
            List<? extends Entity> serverList = SRPEntityUtil.allEntities(world);
            int count = 0;
            for (Entity entity : serverList) {
                if (!(entity instanceof EntityPBeckon) || ++count <= SRPConfig.nexusVenkrolCap && !(in.distanceToSqr(entity) < (double)(SRPConfig.nexusVenkrolDis * SRPConfig.nexusVenkrolDis))) continue;
                return;
            }
            SRPWorldData data = SRPWorldData.get(world);
            if (SRPConfigSystems.rsPlayer) {
                if (cause.getEntity() instanceof Player) {
                    if (SRPConfigSystems.useEvolution) {
                        ParasiteEventEntity.spawnBeckonE(data, world, in);
                    } else {
                        ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.rschance, world, in);
                    }
                }
            } else if (SRPConfigSystems.useEvolution) {
                ParasiteEventEntity.spawnBeckonE(data, world, in);
            } else {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.rschance, world, in);
            }
        }
    }

    public static void spawnBeckonNE(SRPWorldData data, double chance, Level world, EntityParasiteBase in) {
        long worldT = world.getGameTime();
        long seconds = (worldT - SRPAttributes.lastTimeD1) / 20L;
        RandomSource rand = RandomSource.create();
        if (rand.nextDouble() < chance && (long)SRPConfigSystems.rsCooldown < Math.abs(seconds)) {
            if (SRPConfigWorld.originActivated && data.nearestInfectionValue(in.blockPosition(), false) == -1) {
                return;
            }
            if (ParasiteSummon.SummonM((LivingEntity)in, new String[]{ParasiteEventEntity.getRSColony(data)}, 5, 10, in.getTarget())) {
                if (SRPConfigSystems.rsSounds) {
                    if (SRPConfigSystems.disloGrowlNoise) {
                        if (SRPSaveData.get(world).getCurrentCode(DimKeys.of(world), 15) == 0) {
                            in.playSound(SRPSounds.VENKROLSI.get(), 4.0f, 1.0f);
                        }
                    } else {
                        in.playSound(SRPSounds.VENKROLSI.get(), 4.0f, 1.0f);
                    }
                }
                SRPAttributes.lastTimeD1 = worldT;
            }
        }
    }

    public static String getRSColony(SRPWorldData data) {
        if (SRPConfigWorld.coloniesActivated) {
            int totalColonyPoints = data.totalColonyPoints(0);
            double bonus = (float)totalColonyPoints / SRPConfigWorld.colonyExtraRSChancePoint * SRPConfigWorld.colonyExtraRSChanceValue;
            if (bonus > 2.0) {
                return "srparasites:beckon_siii;1;1";
            }
            if (bonus > 1.0) {
                return "srparasites:beckon_sii;1;1";
            }
        }
        return "srparasites:beckon_si;1;1";
    }

    public static void spawnBeckonE(SRPWorldData data, Level world, EntityParasiteBase in) {
        switch (SRPSaveData.get(world).getEvolutionPhase(DimKeys.of(world))) {
            case 1: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceOne, world, in);
                break;
            }
            case 2: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceTwo, world, in);
                break;
            }
            case 3: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceThree, world, in);
                break;
            }
            case 4: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceFour, world, in);
                break;
            }
            case 5: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceFive, world, in);
                break;
            }
            case 6: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceSix, world, in);
                break;
            }
            case 7: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceSeven, world, in);
                break;
            }
            case 8: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceEight, world, in);
                break;
            }
            case 9: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceNine, world, in);
                break;
            }
            case 10: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceTen, world, in);
            }
        }
    }

    public static double getRSchance(Level world) {
        switch (SRPSaveData.get(world).getEvolutionPhase(DimKeys.of(world))) {
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

    public static boolean alertOthers(EntityParasiteBase pin, LivingEntity target, Level world, int loop) {
        return false;
    }

    public static void leaveScent(Level world, DamageSource cause, EntityParasiteBase in) {
        if (!SRPConfigSystems.useScent) {
            return;
        }
        if (SRPConfigSystems.scentPlayer ? !(cause.getEntity() instanceof Player) : !(cause.getEntity() instanceof LivingEntity)) {
            return;
        }
        if (world.random.nextDouble() < SRPConfigSystems.scentDeathSpawning) {
            return;
        }
        if (SRPConfigSystems.useEvolution && in.getPhaseCreated() < SRPConfigSystems.evolutionOneMind && in.getLevelCreated() < SRPConfigSystems.deveOnemindUse) {
            return;
        }
        List<? extends Entity> serverList = SRPEntityUtil.allEntities(world);
        int count = 0;
        for (Entity entity : serverList) {
            if (!(entity instanceof EntityParasiticScent)) continue;
            ++count;
        }
        if (count > SRPConfigSystems.scentCap) {
            return;
        }
        AABB axisalignedbb = new AABB(in.getX(), in.getY(), in.getZ(), in.getX() + 1.0, in.getY() + 1.0, in.getZ() + 1.0).inflate(64.0);
        List<? extends EntityParasiticScent> moblist1 = world.getEntitiesOfClass(EntityParasiticScent.class, axisalignedbb);
        for (EntityParasiticScent mob : moblist1) {
            Entity source;
            if (!in.hasLineOfSight(mob)) continue;
            mob.increaseDanger(in.getCCDeathValue(), true);
            mob.increaseActivity(1, true);
            mob.setScentLife(mob.getScentLife() + 20 * SRPConfigSystems.scentLifeDeath);
            mob.setScentReaction(ParasiteEventEntity.getScentReactionBonus(in.getPhaseCreated()), false);
            mob.setTargetToKill((LivingEntity)cause.getEntity(), true);
            if (cause.getEntity() instanceof Player && (source = cause.getEntity()) instanceof Player) {
                ((Player)source).displayClientMessage(Component.translatable("srp.msg.scent.closest_notified", new Object[0]), true);
            }
            return;
        }
        axisalignedbb = new AABB(in.getX(), in.getY(), in.getZ(), in.getX() + 1.0, in.getY() + 1.0, in.getZ() + 1.0).inflate(64.0);
        List moblist2 = world.getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        int dangerValue = in.getCCDeathValue() + ParasiteEventEntity.getScentBonus(in.getPhaseCreated());
        if (moblist2.size() <= 3 && in.getPhaseCreated() >= 0 && in.getCCDeathValue() > 2) {
            EntityParasiticScent nut = new EntityParasiticScent(SRPEntities.SCENT.get(), world, 0, (LivingEntity)cause.getEntity());
            nut.copyPosition(cause.getEntity());
            nut.setScentLife(SRPConfigSystems.scentLifeObserver * 20);
            nut.increaseDanger(dangerValue, true);
            nut.setScentReaction(ParasiteEventEntity.getScentReactionBonus(in.getPhaseCreated()), false);
            world.addFreshEntity((Entity)nut);
            nut.warnPlayers(Component.translatable("srp.msg.scent.deployed_area"));
        }
    }

    public static int getScentBonus(byte in) {
        int q = 1;
        if (SRPConfigSystems.useEvolution) {
            switch (in) {
                case 0: {
                    q = SRPConfigSystems.phaseScentBonusZero;
                    break;
                }
                case 1: {
                    q = SRPConfigSystems.phaseScentBonusOne;
                    break;
                }
                case 2: {
                    q = SRPConfigSystems.phaseScentBonusTwo;
                    break;
                }
                case 3: {
                    q = SRPConfigSystems.phaseScentBonusThree;
                    break;
                }
                case 4: {
                    q = SRPConfigSystems.phaseScentBonusFour;
                    break;
                }
                case 5: {
                    q = SRPConfigSystems.phaseScentBonusFive;
                    break;
                }
                case 6: {
                    q = SRPConfigSystems.phaseScentBonusSix;
                    break;
                }
                case 7: {
                    q = SRPConfigSystems.phaseScentBonusSeven;
                    break;
                }
                case 8: {
                    q = SRPConfigSystems.phaseScentBonusEight;
                }
            }
        }
        return q;
    }

    public static byte getScentReactionBonus(byte in) {
        byte q = SRPConfigSystems.scentGoActive;
        if (SRPConfigSystems.useEvolution) {
            switch (in) {
                case 0: {
                    q = SRPConfigSystems.phaseScentReactionZero;
                    break;
                }
                case 1: {
                    q = SRPConfigSystems.phaseScentReactionOne;
                    break;
                }
                case 2: {
                    q = SRPConfigSystems.phaseScentReactionTwo;
                    break;
                }
                case 3: {
                    q = SRPConfigSystems.phaseScentReactionThree;
                    break;
                }
                case 4: {
                    q = SRPConfigSystems.phaseScentReactionFour;
                    break;
                }
                case 5: {
                    q = SRPConfigSystems.phaseScentReactionFive;
                    break;
                }
                case 6: {
                    q = SRPConfigSystems.phaseScentReactionSix;
                    break;
                }
                case 7: {
                    q = SRPConfigSystems.phaseScentReactionSeven;
                    break;
                }
                case 8: {
                    q = SRPConfigSystems.phaseScentReactionEight;
                }
            }
        }
        return q;
    }

    public static void checkColony(Level world, DamageSource cause, EntityPMalleable in) {
        if (in.isOnFire() || !SRPConfigWorld.coloniesActivated) {
            return;
        }
        if (ParasiteEventWorld.numberofColonies(world) <= 0) {
            return;
        }
        double chance = 0.0;
        if (in.hasEffect(SRPPotions.LINK_E)) {
            chance = (double)(in.getEffect(SRPPotions.LINK_E).getAmplifier() + 1) * SRPConfigSystems.adapsChance;
        }
        if (ParasiteEventWorld.rangeOfColony(world, in.blockPosition(), true) != null || world.random.nextDouble() < chance) {
            String da;
            SRPWorldData data = SRPWorldData.get(world);
            if (in.colonySpawned) {
                in.removeCommonDamage(data.getMostCommonDamageS(), data.getMostCommonDamageI());
            }
            if ((da = in.getMostCommonDamage()) == null) {
                return;
            }
            data.addGlobalResistance(da);
            PacketDistributor.sendToAllPlayers(new ParticlePayload(in.getX(), in.getY(), in.getZ(), in.getBbWidth(), in.getBbHeight(), 4));
        }
    }

    public static SRPExplosion createExplosion(Level worldIn, @Nullable Entity entityIn, double x, double y, double z, float strength, boolean isSmoking) {
        SRPExplosion explosion = new SRPExplosion(worldIn, entityIn, x, y, z, strength, false, isSmoking);
        if (EventHooks.onExplosionStart((Level)worldIn, (Explosion)explosion)) {
            return explosion;
        }
        explosion.doExplosionA();
        explosion.doExplosionB(false);
        return explosion;
    }

    public static boolean teleportDigging(EntityParasiteBase in, float maxHardness, BlockPos posIn, int range, int mini) {
        if (!in.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            in.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 4, false, false));
            return false;
        }
        RandomSource rand = RandomSource.create();
        double x = posIn.getX();
        double y = posIn.getY();
        double z = posIn.getZ();
        double randomx = rand.nextInt(range) + mini;
        double randomz = rand.nextInt(range) + mini;
        double negative = rand.nextInt(2);
        if (negative == 0.0) {
            randomx *= -1.0;
        }
        if ((negative = (double)rand.nextInt(2)) == 0.0) {
            randomz *= -1.0;
        }
        int limit = 0;
        boolean flag = true;
        while (flag) {
            if (limit >= 5) {
                return false;
            }
            BlockPos pos = BlockPos.containing(x + randomx, y, z + randomz);
            if ((pos = ParasiteEventEntity.getFloor(in.level(), pos, 5)) != null && in.level().getBlockState(pos.below()).isCollisionShapeFullBlock(in.level(), pos.below())) {
                float bHard = 0.0f;
                for (int i = 1; i < 4; ++i) {
                    BlockState state = in.level().getBlockState(pos.below(i));
                    float atm = state.getDestroySpeed(in.level(), pos.below(i));
                    if (atm <= 0.0f) {
                        return false;
                    }
                    bHard += atm;
                }
                if (bHard >= maxHardness) {
                    return false;
                }
                AABB axisalignedbb = new AABB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)).inflate(1.0);
                List moblist = in.level().getEntitiesOfClass(EntityNak.class, axisalignedbb);
                if (moblist.isEmpty()) {
                    in.moveTo((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, in.getYRot(), in.getXRot());
                    flag = false;
                    return true;
                }
            }
            randomx = rand.nextInt(range) + mini;
            randomz = rand.nextInt(range) + mini;
            negative = rand.nextInt(2);
            if (negative == 0.0) {
                randomx *= -1.0;
            }
            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                randomz *= -1.0;
            }
            ++limit;
        }
        return false;
    }

    public static EntityParasiteBase getRandomAssimilated(Level world) {
        switch (world.random.nextInt(9)) {
            case 0: {
                if (!SRPConfigMobs.infbearEnabled) break;
                return new EntityInfBear(SRPEntities.SIM_BEAR.get(), world);
            }
            case 1: {
                if (!SRPConfigMobs.infcowEnabled) break;
                return new EntityInfCow(SRPEntities.SIM_COW.get(), world);
            }
            case 2: {
                if (!SRPConfigMobs.infendermanEnabled) break;
                return new EntityInfEnderman(SRPEntities.SIM_ENDERMAN.get(), world);
            }
            case 3: {
                if (!SRPConfigMobs.infhorseEnabled) break;
                return new EntityInfHorse(SRPEntities.SIM_HORSE.get(), world);
            }
            case 4: {
                if (!SRPConfigMobs.infhumanEnabled) break;
                return new EntityInfHuman(SRPEntities.SIM_HUMAN.get(), world);
            }
            case 5: {
                if (!SRPConfigMobs.infpigEnabled) break;
                return new EntityInfPig(SRPEntities.SIM_PIG.get(), world);
            }
            case 6: {
                if (!SRPConfigMobs.infsheepEnabled) break;
                return new EntityInfSheep(SRPEntities.SIM_SHEEP.get(), world);
            }
            case 7: {
                if (!SRPConfigMobs.infvillagerEnabled) break;
                return new EntityInfVillager(SRPEntities.SIM_VILLAGER.get(), world);
            }
            case 8: {
                if (!SRPConfigMobs.infwolfEnabled) break;
                return new EntityInfWolf(SRPEntities.SIM_WOLF.get(), world);
            }
        }
        return null;
    }

    public static EntityParasiteBase getRandomFeral(Level world) {
        switch (world.random.nextInt(9)) {
            case 0: {
                if (!SRPConfigMobs.ferbearEnabled) break;
                return new EntityFerBear(SRPEntities.FER_BEAR.get(), world);
            }
            case 1: {
                if (!SRPConfigMobs.fercowEnabled) break;
                return new EntityFerCow(SRPEntities.FER_COW.get(), world);
            }
            case 2: {
                if (!SRPConfigMobs.ferendermanEnabled) break;
                return new EntityFerEnderman(SRPEntities.FER_ENDERMAN.get(), world);
            }
            case 3: {
                if (!SRPConfigMobs.ferhorseEnabled) break;
                return new EntityFerHorse(SRPEntities.FER_HORSE.get(), world);
            }
            case 4: {
                if (!SRPConfigMobs.ferhumanEnabled) break;
                return new EntityFerHuman(SRPEntities.FER_HUMAN.get(), world);
            }
            case 5: {
                if (!SRPConfigMobs.ferpigEnabled) break;
                return new EntityFerPig(SRPEntities.FER_PIG.get(), world);
            }
            case 6: {
                if (!SRPConfigMobs.fersheepEnabled) break;
                return new EntityFerSheep(SRPEntities.FER_SHEEP.get(), world);
            }
            case 7: {
                if (!SRPConfigMobs.fervillagerEnabled) break;
                return new EntityFerVillager(SRPEntities.FER_VILLAGER.get(), world);
            }
            case 8: {
                if (!SRPConfigMobs.ferwolfEnabled) break;
                return new EntityFerWolf(SRPEntities.FER_WOLF.get(), world);
            }
        }
        return null;
    }

    public static EntityParasiteBase getRandomAssimara(Level world) {
        switch (world.random.nextInt(9)) {
            case 1: {
                if (!SRPConfigMobs.fercowEnabled) break;
                return new EntitySpeCow(SRPEntities.MAR_COW.get(), world);
            }
            case 2: {
                if (!SRPConfigMobs.ferendermanEnabled) break;
                return new EntitySpeEnderman(SRPEntities.MAR_ENDERMAN.get(), world);
            }
            case 4: {
                if (!SRPConfigMobs.ferhumanEnabled) break;
                return new EntitySpeHuman(SRPEntities.MAR_HUMAN.get(), world);
            }
            case 7: {
                if (!SRPConfigMobs.fervillagerEnabled) break;
                return new EntitySpeVillager(SRPEntities.MAR_VILLAGER.get(), world);
            }
        }
        return null;
    }

    public static EntityParasiteBase getRandomPrimitive(Level world) {
        switch (world.random.nextInt(11)) {
            case 0: {
                if (!SRPConfigMobs.emanaEnabled) break;
                return new EntityEmana(SRPEntities.PRI_YELLOWEYE.get(), world);
            }
            case 1: {
                if (!SRPConfigMobs.canraEnabled) break;
                return new EntityCanra(SRPEntities.PRI_SUMMONER.get(), world);
            }
            case 2: {
                if (!SRPConfigMobs.zetmoEnabled) break;
                return new EntityBano(SRPEntities.PRI_BOLSTER.get(), world);
            }
            case 3: {
                if (!SRPConfigMobs.shycoEnabled) break;
                return new EntityShyco(SRPEntities.PRI_LONGARMS.get(), world);
            }
            case 4: {
                if (!SRPConfigMobs.arachnidaEnabled) break;
                return new EntityRanrac(SRPEntities.PRI_ARACHNIDA.get(), world);
            }
            case 5: {
                if (!SRPConfigMobs.noglaEnabled) break;
                return new EntityNogla(SRPEntities.PRI_REEKER.get(), world);
            }
            case 6: {
                if (!SRPConfigMobs.hullEnabled) break;
                return new EntityHull(SRPEntities.PRI_MANDUCATER.get(), world);
            }
            case 7: {
                if (!SRPConfigMobs.ikiEnabled) break;
                return new EntityIki(SRPEntities.PRI_VERMIN.get(), world);
            }
            case 8: {
                if (!SRPConfigMobs.wymoEnabled) break;
                return new EntityWymo(SRPEntities.PRI_TOZOON.get(), world);
            }
            case 9: {
                if (!SRPConfigMobs.zaaEnabled) break;
                return new EntityZaa(SRPEntities.PRI_BURROWER.get(), world);
            }
            case 10: {
                if (!SRPConfigMobs.gimEnabled) break;
                return new EntityGim(SRPEntities.PRI_VISCERA.get(), world);
            }
        }
        return null;
    }

    public static EntityParasiteBase getRandomAdapted(Level world) {
        switch (world.random.nextInt(10)) {
            case 0: {
                if (!SRPConfigMobs.emanaEnabled) break;
                return new EntityEmanaAdapted(SRPEntities.ADA_YELLOWEYE.get(), world);
            }
            case 1: {
                if (!SRPConfigMobs.canraEnabled) break;
                return new EntityCanraAdapted(SRPEntities.ADA_SUMMONER.get(), world);
            }
            case 2: {
                if (!SRPConfigMobs.zetmoEnabled) break;
                return new EntityBanoAdapted(SRPEntities.ADA_BOLSTER.get(), world);
            }
            case 3: {
                if (!SRPConfigMobs.shycoEnabled) break;
                return new EntityShycoAdapted(SRPEntities.ADA_LONGARMS.get(), world);
            }
            case 4: {
                if (!SRPConfigMobs.arachnidaEnabled) break;
                return new EntityRanracAdapted(SRPEntities.ADA_ARACHNIDA.get(), world);
            }
            case 5: {
                if (!SRPConfigMobs.noglaEnabled) break;
                return new EntityNoglaAdapted(SRPEntities.ADA_REEKER.get(), world);
            }
            case 6: {
                if (!SRPConfigMobs.hullEnabled) break;
                return new EntityHullAdapted(SRPEntities.ADA_MANDUCATER.get(), world);
            }
            case 7: {
                if (!SRPConfigMobs.wymoEnabled) break;
                return new EntityWymoAdapted(SRPEntities.ADA_TOZOON.get(), world);
            }
            case 8: {
                if (!SRPConfigMobs.zaaEnabled) break;
                return new EntityZaaAdapted(SRPEntities.ADA_BURROWER.get(), world);
            }
            case 9: {
                if (!SRPConfigMobs.gimEnabled) break;
                return new EntityGimAdapted(SRPEntities.ADA_VISCERA.get(), world);
            }
        }
        return null;
    }

    public static EntityParasiteBase getRandomPure(Level world) {
        switch (world.random.nextInt(7)) {
            case 0: {
                if (!SRPConfigMobs.alafhaEnabled) break;
                return new EntityAlafha(SRPEntities.OVERSEER.get(), world);
            }
            case 1: {
                if (!SRPConfigMobs.angedEnabled) break;
                return new EntityAnged(SRPEntities.VIGILANTE.get(), world);
            }
            case 2: {
                if (!SRPConfigMobs.esorEnabled) break;
                return new EntityEsor(SRPEntities.MARAUDER.get(), world);
            }
            case 3: {
                if (!SRPConfigMobs.flogEnabled) break;
                return new EntityFlog(SRPEntities.GRUNT.get(), world);
            }
            case 4: {
                if (!SRPConfigMobs.ganroEnabled) break;
                return new EntityGanro(SRPEntities.WARDEN.get(), world);
            }
            case 5: {
                if (!SRPConfigMobs.ombooEnabled) break;
                return new EntityOmboo(SRPEntities.BOMBER_LIGHT.get(), world);
            }
            case 6: {
                if (!SRPConfigMobs.orchEnabled) break;
                return new EntityOrch(SRPEntities.MONARCH.get(), world);
            }
        }
        return null;
    }

    public static boolean spawnUnitFromRof(Level world, LivingEntity target, BlockPos poss, String[] out, int min, int max) {
        if ((poss = ParasiteEventEntity.getFloor(world, poss, 10)) == null) {
            return false;
        }
        EntityRof samuel = new EntityRof(SRPEntities.WORM.get(), world);
        samuel.setPos((double)poss.getX() + 0.5, poss.getY(), (double)poss.getZ() + 0.5);
        if (!samuel.level().noCollision(samuel, samuel.getBoundingBox().expandTowards(1.0, 7.0, 1.0))) {
            samuel.discard();
            return false;
        }
        samuel.setMob(out);
        samuel.maxmob = max;
        samuel.minmob = min;
        samuel.setPeek(true);
        samuel.setBuried();
        samuel.setTarget(target);
        world.addFreshEntity((Entity)samuel);
        world.playSound(null, samuel.blockPosition(), SRPSounds.ROF_EMERGE.get(), SoundSource.HOSTILE, 2.0f, 1.0f);
        world.broadcastEntityEvent((Entity)samuel, (byte)50);
        samuel.targetScent = target;
        if (target == null) {
            samuel.targetScent = samuel;
        }
        return true;
    }

    public static boolean disloNumber2(Level world) {
        List serverList = SRPEntityUtil.allEntities(world);
        int count = 0;
        LivingEntity in = null;
        for (int i = 0; i < serverList.size(); ++i) {
            int atm;
            if (!(serverList.get(i) instanceof LivingEntity) || !((LivingEntity)serverList.get(i)).hasEffect(SRPPotions.JUGG_E) || count > (atm = ((LivingEntity)serverList.get(i)).getEffect(SRPPotions.JUGG_E).getAmplifier())) continue;
            count = atm;
            in = (LivingEntity)serverList.get(i);
        }
        if (in != null && count >= SRPConfigSystems.disloSummonByDeathKilling) {
            RandomSource rand = RandomSource.create();
            int count2 = SRPSaveData.get(world).getCurrentCode(DimKeys.of(world), 2);
            for (int loop = 0; loop < 10; ++loop) {
                double randomx = rand.nextInt(4);
                double randomz = rand.nextInt(4);
                if (rand.nextBoolean()) {
                    randomx *= -1.0;
                }
                if (rand.nextBoolean()) {
                    randomz *= -1.0;
                }
                if (!ParasiteEventEntity.spawnUnitFromRof(world, in, BlockPos.containing(in.getX() + randomx, in.getY(), in.getZ() + randomz), new String[]{ParasiteEventEntity.disloNumber2A(count2, world.random)}, 0, 0)) continue;
                SRPSaveData.get(world).setCurrentCode(DimKeys.of(world), 2, 0, 0, world, false, 0);
                return true;
            }
        }
        return false;
    }

    private static String disloNumber2A(int dama, RandomSource rand) {
        int i;
        String[] here;
        int cc = 0;
        for (int i2 = 0; i2 < SRPConfigSystems.disloSummonByDeathMobs.length; ++i2) {
            int min;
            if (SRPConfigSystems.disloSummonByDeathMobs[i2] == null || (min = Integer.parseInt((here = SRPConfigSystems.disloSummonByDeathMobs[i2].split(";"))[0])) >= dama || min <= cc) continue;
            cc = min;
        }
        ArrayList<String> mobList = new ArrayList<String>();
        for (i = 0; i < SRPConfigSystems.disloSummonByDeathMobs.length; ++i) {
            if (SRPConfigSystems.disloSummonByDeathMobs[i] == null || cc != Integer.parseInt((here = SRPConfigSystems.disloSummonByDeathMobs[i].split(";"))[0])) continue;
            mobList.add(here[1]);
        }
        if (mobList.isEmpty()) {
            for (i = 0; i < SRPConfigSystems.disloSummonByDeathMobs.length; ++i) {
                if (SRPConfigSystems.disloSummonByDeathMobs[i] == null || (here = SRPConfigSystems.disloSummonByDeathMobs[i].split(";")).length <= 1) continue;
                return here[1];
            }
            return "srparasites:warden";
        }
        return (String)mobList.get(rand.nextInt(mobList.size()));
    }

    public static boolean disloNumber5(LivingEntity target, Level world) {
        return false;
    }
}

