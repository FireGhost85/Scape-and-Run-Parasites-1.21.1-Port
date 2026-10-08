package com.dhanantry.scapeandrunparasites.util.spawn;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityDropPod;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityMeteor;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.network.QlipShakePayload;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.network.PacketDistributor;

public class ParasiteSummon {
    public static void spawnM(EntityParasiteBase entityin, String[] out, int particle, boolean cannotDespawn, String name) {
        if (!entityin.level().isClientSide) {
            Random rand = new Random();
            double x = entityin.getX();
            double y = entityin.getY();
            double z = entityin.getZ();
            for (String s : out) {
                if (s == null) continue;
                String[] entityC = s.split(";");
                int max = Integer.parseInt(entityC[1]);
                int min = Integer.parseInt(entityC[2]);
                int total = min == max ? min : rand.nextInt(max - min + 1) + min;
                ResourceLocation entityR = ResourceLocation.parse(entityC[0]);
                for (int count = 0; count < total; ++count) {
                    Mob entityout = (Mob)SRPEntityUtil.create(entityR, entityin.level());
                    if (entityout == null) {
                        return;
                    }
                    entityout.moveTo(x, y + (double)(entityin.getBbHeight() / 2.0f) + 0.5, z, entityin.getYRot(), entityin.getXRot());
                    entityout.finalizeSpawn((ServerLevel) entityout.level(), entityin.level().getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                    if (entityout instanceof EntityParasiteBase && cannotDespawn) {
                        EntityParasiteBase parasite = (EntityParasiteBase)entityout;
                        parasite.cannotDespawn(SRPConfig.convertedDespawn);
                        entityin.level().addFreshEntity((Entity)parasite);
                        continue;
                    }
                    entityin.level().addFreshEntity((Entity)entityout);
                }
            }
        }
    }

    public static boolean SummonM(LivingEntity entityin, String[] out, int minRange, int maxRange, @Nullable LivingEntity target) {
        if (!entityin.level().isClientSide) {
            Random rand = new Random();
            double x = entityin.getX();
            double y = entityin.getY();
            double z = entityin.getZ();
            double randomx = rand.nextInt(maxRange - minRange + 1) + minRange;
            double randomz = rand.nextInt(maxRange - minRange + 1) + minRange;
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
                        randomx = rand.nextInt(maxRange - minRange + 1) + minRange;
                        randomz = rand.nextInt(maxRange - minRange + 1) + minRange;
                        if (rand.nextBoolean()) {
                            randomx *= -1.0;
                        }
                        if (rand.nextBoolean()) {
                            randomz *= -1.0;
                        }
                        if (entityin.level().getBlockState(BlockPos.containing(x + randomx, y, z + randomz)).getBlock() == Blocks.AIR && entityin.level().getBlockState(BlockPos.containing(x + randomx, y - 1.0, z + randomz)).getBlock() != Blocks.AIR) {
                            Mob entityout = (Mob)SRPEntityUtil.create(ResourceLocation.parse(entityC[0]), entityin.level());
                            if (entityout == null) {
                                return false;
                            }
                            entityout.moveTo(x + randomx, y, z + randomz, entityin.getYRot(), entityin.getXRot());
                            if (!entityin.level().noCollision((Entity)entityout, entityout.getBoundingBox())) {
                                entityout.discard();
                                ++index;
                                continue;
                            }
                            if (!entityin.level().getEntities((Entity)entityout, entityout.getBoundingBox().inflate(0.25), (e -> e.isAlive())).isEmpty()) {
                                entityout.discard();
                                ++index;
                                continue;
                            }
                            entityout.finalizeSpawn((ServerLevel) entityout.level(), entityin.level().getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                            if (entityin instanceof EntityCanSummon) {
                                EntityCanSummon father = (EntityCanSummon)entityin;
                                int points = Integer.parseInt(entityC[2]);
                                if (father.getTotalParasites() - father.getActualParasites() < points) {
                                    entityout.discard();
                                    ++index;
                                    continue;
                                }
                                father.setActualParasites(points);
                                father.addID(entityout.getId(), points);
                            }
                            if (entityout instanceof EntityPStationaryArchitect) {
                                if (SRPConfigSystems.rsSky && !entityout.level().canSeeSky(BlockPos.containing(entityout.getX(), entityout.getY() + (double)entityout.getEyeHeight(), entityout.getZ()))) {
                                    entityout.discard();
                                    ++index;
                                    continue;
                                }
                                switch (((EntityPStationaryArchitect)entityout).getParasiteIDRegister()) {
                                    case 16: {
                                        EntityVenkrol vOut = (EntityVenkrol)entityout;
                                        entityin.level().addFreshEntity((Entity)vOut);
                                        if (target != null) {
                                            vOut.setTarget(target);
                                        }
                                        entityin.level().broadcastEntityEvent((Entity)vOut, (byte)50);
                                        break;
                                    }
                                    case 18: {
                                        EntityVenkrolSII vOutii = (EntityVenkrolSII)entityout;
                                        entityin.level().addFreshEntity((Entity)vOutii);
                                        if (target != null) {
                                            vOutii.setTarget(target);
                                        }
                                        entityin.level().broadcastEntityEvent((Entity)vOutii, (byte)50);
                                        break;
                                    }
                                    case 19: {
                                        EntityVenkrolSIII vOutiii = (EntityVenkrolSIII)entityout;
                                        entityin.level().addFreshEntity((Entity)vOutiii);
                                        if (target != null) {
                                            vOutiii.setTarget(target);
                                        }
                                        entityin.level().broadcastEntityEvent((Entity)vOutiii, (byte)50);
                                        break;
                                    }
                                    default: {
                                        entityin.level().addFreshEntity((Entity)entityout);
                                        if (target == null) break;
                                        entityout.setTarget(target);
                                    }
                                }
                                return true;
                            }
                            entityin.level().addFreshEntity((Entity)entityout);
                            if (target != null) {
                                entityout.setTarget(target);
                            }
                            flag = false;
                            return true;
                        }
                    }
                }
                ++index;
            }
        }
        return false;
    }

    public static boolean SummonM(EntityParasiteBase entityin, String[] out, int range, double tx, double ty, double tz, @Nullable LivingEntity target, boolean pod) {
        if (!entityin.level().isClientSide) {
            Random rand = new Random();
            double randomx = rand.nextInt(range);
            double randomz = rand.nextInt(range);
            if (rand.nextBoolean()) {
                randomx *= -1.0;
            }
            if (rand.nextBoolean()) {
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
                        if (entityin.level().getBlockState(BlockPos.containing(tx + randomx, ty, tz + randomz)).getBlock() == Blocks.AIR && entityin.level().getBlockState(BlockPos.containing(tx + randomx, ty - 1.0, tz + randomz)).getBlock() != Blocks.AIR || pod) {
                            if (pod) {
                                EntityDropPod entityout2 = new EntityDropPod(SRPEntities.ANC_POD.get(), entityin.level());
                                entityout2.moveTo(tx + randomx, ty, tz + randomz, entityin.getYRot(), entityin.getXRot());
                                entityout2.setOwner(entityin.getParasiteType());
                                entityin.level().addFreshEntity((Entity)entityout2);
                                flag = false;
                                return true;
                            }
                            Mob entityout = (Mob)SRPEntityUtil.create(ResourceLocation.parse(entityC[0]), entityin.level());
                            if (entityout == null) {
                                return false;
                            }
                            entityout.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16.0);
                            entityout.moveTo(tx + randomx, ty, tz + randomz, entityin.getYRot(), entityin.getXRot());
                            entityout.finalizeSpawn((ServerLevel) entityout.level(), entityin.level().getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                            if (entityin instanceof EntityCanSummon) {
                                EntityCanSummon father = (EntityCanSummon)(entityin);
                                int points = Integer.parseInt(entityC[2]);
                                if (father.getTotalParasites() - father.getActualParasites() < points) {
                                    ++index;
                                    continue;
                                }
                                father.setActualParasites(points);
                                father.addID(entityout.getId(), points);
                            }
                            if (entityin.getParasiteIDRegister() == 41) {
                                if (entityout instanceof EntityPBeckon) {
                                    EntityPBeckon out2 = (EntityPBeckon)entityout;
                                    out2.setCanGrowTo(false);
                                    out2.setLifeB(300);
                                    out2.level().broadcastEntityEvent((Entity)out2, (byte)50);
                                }
                            } else if (entityout instanceof EntityVenkrol) {
                                Block looking = entityin.level().getBlockState(entityout.blockPosition().below()).getBlock();
                                if (looking == SRPBlocks.InfestedStain.get()) {
                                    return false;
                                }
                                entityout.level().broadcastEntityEvent((Entity)entityout, (byte)50);
                            }
                            entityin.level().addFreshEntity((Entity)entityout);
                            if (target != null) {
                                entityout.setTarget(target);
                            }
                            flag = false;
                            if (entityout instanceof EntityParasiteBase) {
                                EntityParasiteBase eOut = (EntityParasiteBase)entityout;
                                eOut.setParasiteToFollow(entityin);
                            }
                            return true;
                        }
                        randomx = rand.nextInt(range);
                        randomz = rand.nextInt(range);
                        if (rand.nextBoolean()) {
                            randomx *= -1.0;
                        }
                        if (rand.nextBoolean()) {
                            randomz *= -1.0;
                        }
                    }
                }
                ++index;
            }
        }
        return false;
    }

    public static void spawnMeteor(BlockPos pos, int rad, int minRad, Level world) {
        if (!SRPConfigWorld.meteorActive) {
            return;
        }
        if (world == null || world.isClientSide) {
            return;
        }
        ParasiteSummon.spawnMeteor(pos.getX(), pos.getY(), pos.getZ(), rad, minRad, world);
    }

    public static void spawnMeteor(int oX, int oY, int oZ, int rad, int minRad, Level world) {
        if (rad > SRPConfigWorld.meteorRadius) {
            rad = SRPConfigWorld.meteorRadius;
        }
        if (minRad < SRPConfigWorld.meteorMinRadius) {
            minRad = SRPConfigWorld.meteorMinRadius;
        }
        if (rad < minRad) {
            rad = minRad + 1;
        }
        int min = minRad;
        int max = rad;
        int origenX = world.random.nextInt(max - min + 1) + min;
        if (world.random.nextBoolean()) {
            origenX *= -1;
        }
        int origenY = world.getMaxBuildHeight();
        int origenZ = world.random.nextInt(max - min + 1) + min;
        if (world.random.nextBoolean()) {
            origenZ *= -1;
        }
        int targetX = world.random.nextInt(max - min + 1) + min;
        if (world.random.nextBoolean()) {
            targetX *= -1;
        }
        int targetZ = world.random.nextInt(max - min + 1) + min;
        if (world.random.nextBoolean()) {
            targetZ *= -1;
        }
        ParasiteSummon.spawnMeteor(oX + origenX, origenY, oZ + origenZ, oX + targetX, oY, oZ + targetZ, world);
    }

    public static void spawnMeteor(int oX, int oY, int oZ, int tX, int tY, int tZ, Level world) {
        double tarX = tX;
        double tarY = tY;
        double tarZ = tZ;
        double dx = tarX - (double)oX;
        double dy = tarY - (double)oY;
        double dz = tarZ - (double)oZ;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double speed = 10.5;
        double motionX = (dx /= len) * speed;
        double motionY = (dy /= len) * speed;
        double motionZ = (dz /= len) * speed;
        EntityMeteor meteor = new EntityMeteor(SRPEntities.METEOR.get(), world, oX, oY, oZ, motionX, motionY, motionZ);
        world.addFreshEntity((Entity)meteor);
        for (Player mob : world.players()) {
            PacketDistributor.sendToPlayer((ServerPlayer)mob, new QlipShakePayload(0, 0, true, false, 0.0f));
        }
    }
}

