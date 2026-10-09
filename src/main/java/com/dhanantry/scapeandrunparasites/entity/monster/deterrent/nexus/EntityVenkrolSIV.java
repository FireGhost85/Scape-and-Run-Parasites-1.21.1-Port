package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIVenkrolSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.logic.VenkrolTornadoLogic;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.AABB;

public class EntityVenkrolSIV
extends EntityPBeckon
implements EntityBodyParts {
    public EntityAIVenkrolSummon summonV = new EntityAIVenkrolSummon(this, SRPConfigMobs.venkrolsivlimit, 20 * SRPConfigMobs.venkrolsivCooldown, 4, SRPConfigMobs.venkrolsivCAMinimumV, SRPConfigMobs.venkrolsivCAExtraM);
    private int count;
    private int tCount;
    private int ticksss;
    private EntityBody head;
    private final ServerBossEvent bossInfo = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false);

    public EntityVenkrolSIV(EntityType<? extends EntityVenkrolSIV> type, Level worldIn) {
        super(type, worldIn);
        this.noCulling = true;
        this.head = new EntityBody(this, 1.9f, 1.9f, 1.0f, 0.0f, 7.0f, -1, 1, false, 0.2f);
        this.totalP = SRPConfigMobs.venkrolsivTotalActiveMobs;
        this.mobID = new int[this.totalP + SRPConfigMobs.venkrolsivlimit];
        this.mobPT = new int[this.totalP + SRPConfigMobs.venkrolsivlimit];
        this.stage = (byte)4;
        this.xpReward = SRPAttributes.XP_ADAPTED * 4;
        Arrays.fill(this.mobID, -777);
        this.setBODY(1.0f);
        this.goalSelector.addGoal(2, this.summonV);
        this.damageCap = SRPConfig.nexussivCap;
        this.count = 200;
        this.tCount = 300;
        this.ticksss = 0;
        this.pointCap = SRPConfig.nexussivPointCap;
        this.pointReduction = SRPConfig.nexussivPointRed;
        this.chanceLearn = SRPConfig.nexussivChanceLe;
        this.chanceLearnFire = SRPConfig.nexussivChanceLeFire;
        this.DamageTypeCap = SRPConfig.nexussivPointDamCap;
        this.valueEvDeath = SRPConfig.nexussivLoosingEPValue;
    }

    @Override
    public void setSkillBreakBlocksValues(float hardness, int heightIn, int rangeIn) {
        this.blockH = hardness;
        this.BGheight = heightIn + 2;
        this.BGrange = rangeIn;
    }

    @Override
    public int getParasiteIDRegister() {
        return 41;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPBeckon.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.VENKROLSIV_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.VENKROLSIV_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.VENKROLSIV_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.nexussivFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.head.tick();
        --this.count;
        --this.tCount;
        if (this.getParasiteStatus() == 0) {
            this.setBODY(0.04f);
        } else {
            this.setBODY(-0.04f);
        }
        if (!this.level().isClientSide) {
            BlockPos pos = this.blockPosition();
            if (SRPConfigWorld.venkrolTornadoEnabled && this.level().isRaining() && this.level().isThundering() && this.level().canSeeSky(pos.above())) {
                VenkrolTornadoLogic.tickTornadoEffects((LivingEntity)this);
            }
        }
        this.spawnThunder();
        this.placeBiome();
    }

    private void spawnThunder() {
        if (!this.level().isClientSide && this.getRandom().nextDouble() < 0.015) {
            BlockPos ray;
            if (this.tCount > 0) {
                return;
            }
            if (this.count < 0 && this.upgradeParasites()) {
                this.tCount = 300;
                this.count = 300;
                return;
            }
            int range = 64;
            double randomx = this.getRandom().nextInt(range);
            double randomz = this.getRandom().nextInt(range);
            double negative = this.getRandom().nextInt(2);
            if (negative == 0.0) {
                randomx *= -1.0;
            }
            if ((negative = (double)this.getRandom().nextInt(2)) == 0.0) {
                randomz *= -1.0;
            }
            if ((ray = ParasiteEventEntity.getFloor(this.level(), BlockPos.containing(randomx + this.getX(), this.getY(), randomz + this.getZ()), 5)) != null) {
                if (SRPConfig.thunderEnable) {
                    SRPEntityUtil.lightning(this.level(), ray.getX(), ray.getY(), ray.getZ(), true);
                }
                this.tCount = 200;
            }
        }
    }

    private boolean upgradeParasites() {
        int count = 3;
        int current = 0;
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(48.0, 34.0, 48.0);
        List<? extends EntityParasiteBase> moblist = this.level().getEntitiesOfClass(EntityPInfected.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            if (!(mob instanceof EntityPStationaryArchitect) || mob.getParasiteIDRegister() != 16 && mob.getParasiteIDRegister() != 18 && mob.getParasiteIDRegister() != 19) continue;
            ((EntityPStationaryArchitect)mob).setActualT(0);
        }
        if (moblist.size() > SRPConfig.nexussivCapUpgrade) {
            return true;
        }
        for (EntityParasiteBase mob : moblist) {
            if (mob.getParasiteIDRegister() == 64) continue;
            this.thunderParasite(mob, 1);
            if (++current < count) continue;
            return true;
        }
        if (current >= count) {
            return true;
        }
        moblist = this.level().getEntitiesOfClass(EntityPPrimitive.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            this.thunderParasite(mob, 2);
            if (++current < count) continue;
            return true;
        }
        return false;
    }

    private void thunderParasite(EntityParasiteBase in, int type) {
        if (!ParasiteEventEntity.canSpawnNext) {
            return;
        }
        switch (type) {
            case 1: {
                EntityParasiteBase out = ParasiteEventEntity.getRandomPrimitive(in.level());
                if (SRPConfig.thunderEnable) {
                    SRPEntityUtil.lightning(in.level(), in.getX(), in.getY(), in.getZ(), true);
                }
                ParasiteEventEntity.spawnNext(in, out, true, true);
                break;
            }
            case 2: {
                if (SRPConfig.thunderEnable) {
                    SRPEntityUtil.lightning(in.level(), in.getX(), in.getY(), in.getZ(), true);
                }
                in.setKillC(in.getKillC() + 1000.0);
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public void setCustomNameTag(String name) {
        SRPEntityUtil.setCustomNameTag(this, name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void addTrackingPlayer(ServerPlayer player) {
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag && source.getEntity() != null && source.getEntity() instanceof ServerPlayer) {
            this.bossInfo.addPlayer((ServerPlayer)source.getEntity());
        }
        return flag;
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.getRandom().nextBoolean()) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)this, 80, 0);
        }
        return this.hurt(source, amount * 3.0f);
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.head.discard();
        }
        super.discard();
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 8.1f;
    }

    public void setBODY(float in) {
        if ((in += this.getBODY()) > 0.5f) {
            in = 0.5f;
        }
        if (in < 0.0f) {
            in = 0.0f;
        }
        this.body = in;
    }

    private void placeBiome() {
        if (this.tickCount < 1200) {
            return;
        }
        if (this.level().isClientSide) {
            return;
        }
        ++this.ticksss;
        if (this.ticksss < 20) {
            return;
        }
        this.ticksss = 0;
        if (ParasiteEventWorld.canBiomeStillExist(this.level(), this.blockPosition(), true) >= 1) {
            this.ticksss = -1000;
            return;
        }
        int range = 7;
        int attemp = 3;
        int mini = 5;
        while (attemp > 0) {
            --attemp;
            double randomx = this.getRandom().nextInt(range);
            double randomz = this.getRandom().nextInt(range);
            double negative = this.getRandom().nextInt(2);
            randomx = negative == 0.0 ? randomx * -1.0 - (double)mini : (randomx += (double)mini);
            negative = this.getRandom().nextInt(2);
            randomz = negative == 0.0 ? randomz * -1.0 - (double)mini : (randomz += (double)mini);
            BlockPos pos = BlockPos.containing(this.getX() + randomx, this.getY(), this.getZ() + randomz);
            if ((pos = ParasiteEventEntity.getFloor(this.level(), pos, 5)) == null) continue;
            int typeB = 1;
            Holder<Biome> biome = this.level().getBiome(pos);
            if (biome.is(Biomes.WINDSWEPT_HILLS) || biome.is(Biomes.NETHER_WASTES) || biome.is(Biomes.SNOWY_PLAINS) || biome.is(Biomes.SNOWY_SLOPES) || biome.is(Biomes.FROZEN_PEAKS) || biome.is(Biomes.STONY_SHORE) || biome.is(Biomes.THE_VOID)) {
                typeB = 3;
            }
            if (ParasiteEventWorld.placeHeartInWorld(this.level(), pos, typeB) != 1) continue;
            this.ticksss = -1000;
            return;
        }
        this.ticksss = -100;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return SRPConfig.rsDespawn;
    }

    @Override
    public void setBodyPartDead(int id) {
    }

    @Override
    public float getBombDamage() {
        return (float)SRPAttributes.VENKROLSIV_ATTACK_DAMAGE;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityVenkrolSIII(SRPEntities.BECKON_SIII.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        ParasiteEventWorld.setDisloWorldPhase(this.level(), SRPAttributes.EVENTPARANEXUSIVD, SRPConfigSystems.chanceEventParaNexusIVD, 0, null);
        return super.onDeathDislo(cause);
    }
}

