package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.MovingSoundPayload;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityParasiticScent
extends Entity {
    private final ServerBossEvent bossInfo = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false);
    private boolean phaseI = true;
    private byte scentState;
    private int lifeTicks = 600;
    private int currentL;
    private int dangerToUs = 100;
    private byte active;
    private int delay;
    private int timerTick;
    private byte scentLevel;
    private byte scentReaction;
    private int minwave;
    private int maxwave;
    private int minmob;
    private int maxmob;
    private boolean followTargetScent = false;
    private boolean dieAfterKilling;
    private boolean hasCheckedForOthers;
    private byte loopLife = (byte)103;
    private byte failing;
    private static final String SCENT_HOST_TAG = "SRPScentBuffed";
    private double originalHostMaxHealth = -1.0;
    private boolean hostBuffApplied = false;
    private LivingEntity targetScent;
    private LivingEntity hostLiving;

    public void setCustomNameTag(String name) {
        SRPEntityUtil.setCustomNameTag(this, name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
        List<? extends Player> playerEntityList = this.level().players();
        for (Player entityPlayer : playerEntityList) {
            com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayersInDimension((ServerLevel) this.level(), new MovingSoundPayload(102));
        }
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
        List<? extends Player> playerEntityList = this.level().players();
        for (Player entityPlayer : playerEntityList) {
            com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayersInDimension((ServerLevel) this.level(), new MovingSoundPayload(103));
        }
    }

    public EntityParasiticScent(EntityType<? extends EntityParasiticScent> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityParasiticScent(EntityType<? extends EntityParasiticScent> type, Level worldIn, int status) {
        this(type, worldIn);
        this.scentState = (byte)status;
    }

    public EntityParasiticScent(EntityType<? extends EntityParasiticScent> type, Level worldIn, int status, LivingEntity tar) {
        this(type, worldIn, status);
        this.setTargetToKill(tar, false);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    public void tick() {
        Player pa;
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (this.phaseI) {
            if (this.currentL != 600) {
                ++this.currentL;
            }
            if (this.hostLiving == null) {
                AABB axisalignedbb;
                List<? extends EntityParasiteBase> moblist;
                if (this.currentL % 20 == 0 && this.currentL >= 300 && !(moblist = this.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(80.0))).isEmpty()) {
                    EntityParasiteBase pickedHost = (EntityParasiteBase)moblist.get(this.level().random.nextInt(moblist.size()));
                    if (pickedHost.getPersistentData().getBoolean(SCENT_HOST_TAG)) {
                        return;
                    }
                    this.hostLiving = pickedHost;
                    this.hostLiving.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.currentL, 3, false, true));
                    if (this.hostLiving.getAttribute(Attributes.MAX_HEALTH) != null) {
                        this.originalHostMaxHealth = this.hostLiving.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
                        double newMax = this.originalHostMaxHealth * 10.0;
                        this.hostLiving.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newMax);
                        this.hostLiving.setHealth((float)newMax);
                        this.hostLiving.getPersistentData().putBoolean(SCENT_HOST_TAG, true);
                        this.hostBuffApplied = true;
                    }
                }
            } else if (!this.hostLiving.isAlive()) {
                this.discard();
                return;
            }
            this.bossInfo.setProgress(((float)this.currentL + 1.0f) / (float)this.lifeTicks);
            if (this.currentL == 600) {
                --this.dangerToUs;
                if (this.dangerToUs == 0) {
                    this.phaseI = false;
                }
            }
            return;
        }
        float hea = ((float)this.currentL + 1.0f) / (float)this.lifeTicks;
        this.bossInfo.setProgress(hea);
        if (this.scentState >= 5) {
            this.currentL -= 20;
        }
        if (this.currentL <= 0 || this.level().getDifficulty() == Difficulty.PEACEFUL || this.loopLife < 0 || this.hostLiving == null) {
            this.discard();
            return;
        }
        if (!this.hostLiving.isAlive()) {
            this.discard();
            return;
        }
        if (this.getTargetToKill() == null) {
            if (this.scentState > 1) {
                this.scentState = 1;
                return;
            }
        } else if (this.getTargetToKill().distanceToSqr((Entity)this) > 4096.0) {
            if (this.dieAfterKilling) {
                this.discard();
                return;
            }
            this.setTargetToKill(null, false);
            this.scentState = 1;
            return;
        }
        if (this.delay > 0) {
            --this.delay;
            return;
        }
        this.scentFollower();
        if (this.getTargetToKill() instanceof Player && ((pa = (Player)this.getTargetToKill()).isSpectator() || pa.isCreative())) {
            this.setTargetToKill(null, false);
            return;
        }
        switch (this.scentState) {
            case 0: {
                if (this.getRandom().nextInt(3) == 0) {
                    this.scentObserver();
                }
                this.scentListener();
                break;
            }
            case 1: {
                this.scentObserver();
                this.scentListener();
                break;
            }
            case 4: {
                this.scentTactical();
                break;
            }
            case 5: {
                this.scentAttacker();
                break;
            }
            case 6: {
                this.scentBuilder();
            }
        }
    }

    private void scentListener() {
        if (this.active >= this.scentReaction && this.getTargetToKill() != null) {
            this.scentState = (byte)4;
            this.active = (byte)2;
            this.warnPlayers(Component.translatable("srp.msg.scent.active"));
        }
    }

    private void scentObserver() {
        if (this.tickCount % 20 != 0) {
            return;
        }
        if (this.followTargetScent) {
            return;
        }
        if (this.getTargetToKill() != null) {
            this.active = (byte)(this.active + 1);
            AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(80.0);
            List<? extends EntityParasiteBase> moblist = this.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
            for (EntityParasiteBase mob : moblist) {
                double ra;
                if (!mob.isAlive()) continue;
                if (mob.getTarget() == null) {
                    ra = mob.getAttribute(Attributes.FOLLOW_RANGE).getValue();
                    if (!(mob.distanceToSqr((Entity)this.getTargetToKill()) < ra * ra)) continue;
                    mob.setTarget(this.getTargetToKill());
                    continue;
                }
                if (mob.getTarget().isAlive()) continue;
                ra = mob.getAttribute(Attributes.FOLLOW_RANGE).getValue();
                if (!(mob.distanceToSqr((Entity)this.getTargetToKill()) < ra * ra)) continue;
                mob.setTarget(this.getTargetToKill());
            }
            return;
        }
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(80.0);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        int parasites = moblist.size();
        if (parasites < (moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb)).size()) {
            LivingEntity target = null;
            double dis = 40000.0;
            for (LivingEntity mob : moblist) {
                Player qqq;
                double atm;
                if (mob instanceof EntityParasiteBase || mob instanceof WaterAnimal || mob instanceof Creeper || (atm = mob.distanceToSqr((Entity)this)) > 4096.0 || !this.checkAttri(mob) || mob instanceof Player && ((qqq = (Player)mob).isCreative() || qqq.isSpectator()) || !(atm < dis)) continue;
                dis = atm;
                target = mob;
            }
            this.setTargetToKill(target, false);
        }
    }

    private void scentTactical() {
        if (this.active >= SRPConfigSystems.scentSpawnWaves) {
            this.scentState = (byte)5;
            this.active = (byte)(this.active - 5);
            return;
        }
        if (this.getTargetToKill() != null) {
            this.active = (byte)(this.active + 1);
        } else if (this.tickCount % 80 == 0) {
            this.active = (byte)(this.active - 1);
            if (this.active <= 0) {
                this.scentState = 1;
                return;
            }
        }
    }

    private void scentAttacker() {
        if (this.checkNearby() <= 6) {
            this.scentState = (byte)6;
        } else {
            this.delay = 100;
            this.scentState = (byte)4;
        }
    }

    private void scentBuilder() {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SRPSounds.SCENTWAVE.get(), this.getSoundSource(), 10.0f, 1.0f);
        this.level().playSound(null, this.getTargetToKill().getX(), this.getTargetToKill().getY(), this.getTargetToKill().getZ(), SRPSounds.SCENTWAVE.get(), this.getTargetToKill().getSoundSource(), 10.0f, 1.0f);
        this.loopLife = (byte)(this.loopLife - 1);
        int a = 0;
        int horde = this.maxMinInt(this.minwave, this.maxwave);
        for (int limit = 0; limit < 10; ++limit) {
            if ((a += this.placeWaves(SRPConfigSystems.scentMiniDis, SRPConfigSystems.scentMaxDis)) < horde) continue;
            this.delay = 100 + a * 20;
            this.scentState = (byte)4;
            return;
        }
        this.delay = 200;
        if (a <= 0) {
            this.delay = 100;
        }
    }

    private void scentFollower() {
        if (this.followTargetScent) {
            if (this.getTargetToKill() != null) {
                if (this.getTargetToKill().distanceToSqr((Entity)this) > 144.0 && this.getTargetToKill().hasEffect(SRPPotions.PREY_E)) {
                    this.copyPosition((Entity)this.getTargetToKill());
                }
            } else {
                this.setTargetToKill(null, false);
                this.followTargetScent = false;
                return;
            }
        }
    }

    public boolean getCanFollow() {
        return this.followTargetScent;
    }

    public void setCanFollow(boolean in) {
        this.followTargetScent = in;
    }

    public boolean getDieToE() {
        return this.dieAfterKilling;
    }

    public void setDieToE(boolean in) {
        this.dieAfterKilling = in;
    }

    public void setScentState(int in) {
        this.scentState = (byte)in;
    }

    public byte getScentState() {
        return this.scentState;
    }

    public void setScentLife(int in) {
        this.lifeTicks = in;
    }

    public int getScentLife() {
        return this.lifeTicks;
    }

    public boolean setTargetToKill(LivingEntity in, boolean checkATT) {
        if (this.followTargetScent) {
            return false;
        }
        if (in == null) {
            return false;
        }
        if (!in.isAlive()) {
            return false;
        }
        if (checkATT) {
            if (this.checkAttri(in)) {
                this.targetScent = in;
                return true;
            }
            return false;
        }
        this.targetScent = in;
        return true;
    }

    private boolean checkAttri(LivingEntity in) {
        if (in == null) {
            return false;
        }
        int cond = 0;
        if (in.getAttribute(Attributes.MAX_HEALTH) != null && in.getAttribute(Attributes.MAX_HEALTH).getValue() >= SRPConfigSystems.minAttriHealth) {
            ++cond;
        }
        if (in.getAttribute(Attributes.ARMOR) != null && in.getAttribute(Attributes.ARMOR).getValue() >= SRPConfigSystems.minAttriArmor) {
            ++cond;
        }
        if (in.getAttribute(Attributes.ATTACK_DAMAGE) != null && in.getAttribute(Attributes.ATTACK_DAMAGE).getValue() >= SRPConfigSystems.minAttriDamage) {
            ++cond;
        }
        return cond >= SRPConfigSystems.minAttriFailCount || cond == 0;
    }

    public LivingEntity getTargetToKill() {
        if (this.targetScent == null) {
            return null;
        }
        if (!this.targetScent.isAlive()) {
            this.targetScent = null;
            return null;
        }
        return this.targetScent;
    }

    public void increaseDanger(int in, boolean plus) {
        this.dangerToUs = plus ? (this.dangerToUs += in) : in;
        this.updateScentOLevel();
    }

    private void updateScentOLevel() {
        if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsEight) {
            this.scentLevel = (byte)8;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveEight;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveEight;
            this.maxwave = SRPConfigSystems.scentWaveMaximumEight;
            this.minwave = SRPConfigSystems.scentWaveMinimumEight;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsSeven) {
            this.scentLevel = (byte)7;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveSeven;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveSeven;
            this.maxwave = SRPConfigSystems.scentWaveMaximumSeven;
            this.minwave = SRPConfigSystems.scentWaveMinimumSeven;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsSix) {
            this.scentLevel = (byte)6;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveSix;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveSix;
            this.maxwave = SRPConfigSystems.scentWaveMaximumSix;
            this.minwave = SRPConfigSystems.scentWaveMinimumSix;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsFive) {
            this.scentLevel = (byte)5;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveFive;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveFive;
            this.maxwave = SRPConfigSystems.scentWaveMaximumFive;
            this.minwave = SRPConfigSystems.scentWaveMinimumFive;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsFour) {
            this.scentLevel = (byte)4;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveFour;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveFour;
            this.maxwave = SRPConfigSystems.scentWaveMaximumFour;
            this.minwave = SRPConfigSystems.scentWaveMinimumFour;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsThree) {
            this.scentLevel = (byte)3;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveThree;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveThree;
            this.maxwave = SRPConfigSystems.scentWaveMaximumThree;
            this.minwave = SRPConfigSystems.scentWaveMinimumThree;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsTwo) {
            this.scentLevel = (byte)2;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveTwo;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveTwo;
            this.maxwave = SRPConfigSystems.scentWaveMaximumTwo;
            this.minwave = SRPConfigSystems.scentWaveMinimumTwo;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsOne) {
            this.scentLevel = 1;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveOne;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveOne;
            this.maxwave = SRPConfigSystems.scentWaveMaximumOne;
            this.minwave = SRPConfigSystems.scentWaveMinimumOne;
        } else {
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveZero;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveZero;
            this.maxwave = SRPConfigSystems.scentWaveMaximumZero;
            this.minwave = SRPConfigSystems.scentWaveMinimumZero;
        }
    }

    public int getDanger() {
        return this.dangerToUs;
    }

    public void increaseActivity(int in, boolean plus) {
        if (in > 100) {
            return;
        }
        this.active = plus ? (byte)(this.active + (byte)in) : (byte)in;
    }

    private int checkNearby() {
        int i = 0;
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(80.0);
        List<? extends EntityParasiteBase> moblist = this.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            if (mob.getDeltaMovement().x == 0.0 || mob.getDeltaMovement().y == 0.0 || mob.getDeltaMovement().z == 0.0 || mob.getTarget() != null || !mob.isAlive() || this.level().getBrightness(LightLayer.BLOCK, this.blockPosition()) >= 5 || mob instanceof EntityPStationary || mob instanceof EntityBiomass || mob.hurtTime > 0 || !this.moveMobToLoc(mob)) continue;
            mob.setTarget(this.getTargetToKill());
            ++i;
        }
        List<? extends Entity> serverList = SRPEntityUtil.allEntities(this.level());
        int count = 0;
        for (Entity entity : serverList) {
            if (!(entity instanceof EntityParasiteBase)) continue;
            ++count;
        }
        int players = this.level().players().size();
        if (count > SRPConfig.worldMobCap + (players *= SRPConfig.worldMobCapPlusPlayer)) {
            return 20;
        }
        return i;
    }

    private boolean moveMobToLoc(EntityParasiteBase in) {
        int minDist = SRPConfigSystems.scentMiniDis;
        int maxDist = SRPConfigSystems.scentMaxDis;
        int loop = 0;
        if (loop < 7) {
            ++loop;
            int range = maxDist * 2;
            int tryX = (int)Math.floor(this.getTargetToKill().getX() - (double)range / 2.0 + (double)this.getRandom().nextInt(range));
            int tryY = (int)this.getTargetToKill().getY();
            int tryZ = (int)Math.floor(this.getTargetToKill().getZ() - (double)range / 2.0 + (double)this.getRandom().nextInt(range));
            BlockPos poss = BlockPos.containing(tryX, tryY, tryZ);
            if ((poss = ParasiteEventEntity.getFloor(this.level(), poss, 10)) == null) {
                return false;
            }
            if (this.level().getBrightness(LightLayer.BLOCK, poss) > 4) {
                return false;
            }
            AABB axisalignedbb = new AABB((double)tryX, (double)tryY, (double)tryZ, (double)(tryX + 1), (double)(tryY + 1), (double)(tryZ + 1)).expandTowards((double)minDist, 5.0, (double)minDist);
            List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity mob : moblist) {
                if (mob instanceof EntityParasiteBase) continue;
                return false;
            }
            if (Math.sqrt(this.distanceToSqr(poss.getX(), poss.getY(), poss.getZ())) < (double)minDist || Math.sqrt(this.distanceToSqr(poss.getX(), poss.getY(), poss.getZ())) > (double)maxDist) {
                return false;
            }
            in.setPos((double)poss.getX() + 0.5, poss.getY(), (double)poss.getZ() + 0.5);
            return true;
        }
        return false;
    }

    public int placeWaves(int minDist, int maxDist) {
        if (this.getTargetToKill() == null) {
            return 0;
        }
        if (!SRPConfigSystems.useScent) {
            this.discard();
            return 0;
        }
        int range = maxDist * 2;
        int tryX = (int)Math.floor(this.getTargetToKill().getX() - (double)range / 2.0 + (double)this.getRandom().nextInt(range));
        int tryY = (int)this.getTargetToKill().getY();
        int tryZ = (int)Math.floor(this.getTargetToKill().getZ() - (double)range / 2.0 + (double)this.getRandom().nextInt(range));
        BlockPos poss = BlockPos.containing(tryX, tryY, tryZ);
        if ((poss = ParasiteEventEntity.getFloor(this.level(), poss, 10)) == null) {
            return 0;
        }
        if (Math.sqrt(this.distanceToSqr(poss.getX(), poss.getY(), poss.getZ())) < (double)minDist || Math.sqrt(this.distanceToSqr(poss.getX(), poss.getY(), poss.getZ())) > (double)SRPConfigSystems.oneMinRangeCap) {
            return 0;
        }
        AABB axisalignedbb = new AABB((double)poss.getX(), (double)poss.getY(), (double)poss.getZ(), (double)(poss.getX() + 1), (double)(poss.getY() + 1), (double)(poss.getZ() + 1)).expandTowards((double)maxDist, 16.0, (double)maxDist);
        List moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        int living = moblist.size();
        moblist = this.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        List<? extends Entity> serverList = SRPEntityUtil.allEntities(this.level());
        int count = 0;
        for (Entity entity : serverList) {
            if (!(entity instanceof EntityParasiteBase)) continue;
            ++count;
        }
        int players = this.level().players().size();
        if (count > SRPConfig.worldMobCap + (players *= SRPConfig.worldMobCapPlusPlayer)) {
            return 0;
        }
        if (living == moblist.size()) {
            return 0;
        }
        axisalignedbb = new AABB((double)poss.getX(), (double)poss.getY(), (double)poss.getZ(), (double)(poss.getX() + 1), (double)(poss.getY() + 1), (double)(poss.getZ() + 1)).expandTowards((double)minDist, 5.0, (double)minDist);
        moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        this.updateScentOLevel();
        ParasiteEventEntity.spawnUnitFromRof(this.level(), this.getTargetToKill(), poss, this.getMob(), this.minmob, this.maxmob);
        return 1;
    }

    private String[] getMob() {
        String[] mob = new String[]{"minecraft:zombie"};
        switch (this.getareaValue()) {
            case 0: {
                mob = SRPConfigSystems.scentLevelZero;
                break;
            }
            case 1: {
                mob = SRPConfigSystems.scentLevelOne;
                break;
            }
            case 2: {
                mob = SRPConfigSystems.scentLevelTwo;
                break;
            }
            case 3: {
                mob = SRPConfigSystems.scentLevelThree;
                break;
            }
            case 4: {
                mob = SRPConfigSystems.scentLevelFour;
                break;
            }
            case 5: {
                mob = SRPConfigSystems.scentLevelFive;
                break;
            }
            case 6: {
                mob = SRPConfigSystems.scentLevelSix;
                break;
            }
            case 7: {
                mob = SRPConfigSystems.scentLevelSeven;
                break;
            }
            case 8: {
                mob = SRPConfigSystems.scentLevelEight;
            }
        }
        return mob;
    }

    private byte getareaValue() {
        byte i = this.scentLevel;
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(80.0);
        List<? extends EntityParasiticScent> moblist1 = this.level().getEntitiesOfClass(EntityParasiticScent.class, axisalignedbb);
        for (EntityParasiticScent mob : moblist1) {
            if (mob.getScentLevel() <= i) continue;
            i = mob.getScentLevel();
        }
        return i;
    }

    public void setScentReaction(byte in, boolean override) {
        if (override) {
            this.scentReaction = in;
        } else if (in > this.scentReaction) {
            this.scentReaction = in;
        }
    }

    public byte getScentLevel() {
        return this.scentLevel;
    }

    public void setScentLevel(int in) {
        this.scentLevel = (byte)in;
    }

    public void warnPlayers(Component in) {
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(80.0);
        List<? extends Player> moblist1 = this.level().getEntitiesOfClass(Player.class, axisalignedbb);
        for (Player mob : moblist1) {
            mob.displayClientMessage(in, true);
        }
    }

    private int maxMinInt(int min, int max) {
        int atm = max - min + 1;
        return this.getRandom().nextInt(atm) + min;
    }

    public boolean checkIfScentAlone(EntityParasiticScent scent) {
        ArrayList<BlockPos> scentsLoadedPositions = new ArrayList<BlockPos>();
        List<? extends Entity> serverList = SRPEntityUtil.allEntities(this.level());
        for (Entity entity : serverList) {
            if (!(entity instanceof EntityParasiticScent) || entity == scent) continue;
            scentsLoadedPositions.add(entity.blockPosition());
        }
        for (BlockPos s : scentsLoadedPositions) {
            if (!(Math.sqrt(scent.distanceToSqr(s.getX(), s.getY(), s.getZ())) <= (double)SRPConfigSystems.scentSpacing)) continue;
            return false;
        }
        return true;
    }

    public void push(Entity entityIn) {
        super.push(entityIn);
    }

    public void playerTouch(Player entityIn) {
        if (this.tickCount % 20 == 0 && !entityIn.level().isClientSide) {
            System.out.println("\n scent state " + this.scentState + "\n active " + this.active + "\n reaction " + this.scentReaction + "\n danger " + this.dangerToUs + "\n delay " + this.delay + "\n total life " + this.lifeTicks + "\n current life " + this.currentL + "\n targetScent is null " + (this.getTargetToKill() == null) + "\n level " + this.scentLevel + "\n targetScent name " + (this.getTargetToKill() != null ? this.getTargetToKill().getClass().toString() : " b ") + "\n min max mob " + this.minmob + " " + this.maxmob + "\n loop " + this.loopLife + "\n fail " + this.failing);
        }
        super.playerTouch(entityIn);
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("parasitehostbuffapplied", 1)) {
            this.hostBuffApplied = compound.getBoolean("parasitehostbuffapplied");
        }
        if (compound.contains("parasitehostoriginalmax", 6)) {
            this.originalHostMaxHealth = compound.getDouble("parasitehostoriginalmax");
        }
        if (compound.contains("parasitescenttype", 99)) {
            this.scentState = compound.getByte("parasitescenttype");
        }
        if (compound.contains("parasitescentactive", 99)) {
            this.active = compound.getByte("parasitescentactive");
        }
        if (compound.contains("parasitescentlevel", 99)) {
            this.scentLevel = compound.getByte("parasitescentlevel");
        }
        if (compound.contains("parasitelifespan", 99)) {
            this.lifeTicks = compound.getInt("parasitelifespan");
        }
        if (compound.contains("parasitelifecurrent", 99)) {
            this.currentL = compound.getInt("parasitelifecurrent");
        }
        if (compound.contains("parasitedangerous", 99)) {
            this.dangerToUs = compound.getInt("parasitedangerous");
        }
        if (compound.contains("parasitedelay", 99)) {
            this.delay = compound.getInt("parasitedelay");
        }
        if (compound.contains("parasitescentreaction", 99)) {
            this.scentReaction = compound.getByte("parasitescentreaction");
        }
        if (compound.contains("parasitescentloopf", 99)) {
            this.loopLife = compound.getByte("parasitescentloopf");
        }
        if (compound.contains("parasitescentfailing", 99)) {
            this.failing = compound.getByte("parasitescentfailing");
        }
        if (compound.contains("parasitescentdyeing", 99)) {
            this.dieAfterKilling = compound.getBoolean("parasitescentdyeing");
        }
        if (compound.contains("parasitescentfollowing", 99)) {
            this.followTargetScent = compound.getBoolean("parasitescentfollowing");
        }
        if (compound.contains("parasitescentphase", 99)) {
            this.phaseI = compound.getBoolean("parasitescentphase");
        }
        if (compound.contains("parasitehost", 99)) {
            this.hostLiving = (LivingEntity)this.level().getEntity(compound.getInt("parasitehost"));
        }
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("parasitehostbuffapplied", this.hostBuffApplied);
        compound.putDouble("parasitehostoriginalmax", this.originalHostMaxHealth);
        compound.putByte("parasitescenttype", this.scentState);
        compound.putByte("parasitescentactive", this.active);
        compound.putByte("parasitescentlevel", this.scentLevel);
        compound.putInt("parasitelifespan", this.lifeTicks);
        compound.putInt("parasitelifecurrent", this.currentL);
        compound.putInt("parasitedangerous", this.dangerToUs);
        compound.putInt("parasitedelay", this.delay);
        compound.putByte("parasitescentreaction", this.scentReaction);
        compound.putByte("parasitescentloopf", this.loopLife);
        compound.putByte("parasitescentfailing", this.failing);
        compound.putBoolean("parasitescentdyeing", this.dieAfterKilling);
        compound.putBoolean("parasitescentfollowing", this.followTargetScent);
        compound.putBoolean("parasitescentphase", this.phaseI);
        if (this.hostLiving != null) {
            compound.putInt("parasitehost", this.hostLiving.getId());
        }
    }

    private void cleanupHostBuff() {
        if (!this.hostBuffApplied || this.hostLiving == null) {
            return;
        }
        if (this.hostLiving.getAttribute(Attributes.MAX_HEALTH) != null && this.hostLiving.isAlive()) {
            double restore = this.originalHostMaxHealth > 0.0 ? this.originalHostMaxHealth : this.hostLiving.getAttribute(Attributes.MAX_HEALTH).getBaseValue() / 10.0;
            this.hostLiving.getAttribute(Attributes.MAX_HEALTH).setBaseValue(restore);
            if ((double)this.hostLiving.getHealth() > restore) {
                this.hostLiving.setHealth((float)restore);
            }
        }
        this.hostLiving.getPersistentData().remove(SCENT_HOST_TAG);
        this.hostBuffApplied = false;
        this.originalHostMaxHealth = -1.0;
    }

    public void setDead() {
        if (!this.level().isClientSide) {
            this.cleanupHostBuff();
        }
        super.discard();
    }
}

