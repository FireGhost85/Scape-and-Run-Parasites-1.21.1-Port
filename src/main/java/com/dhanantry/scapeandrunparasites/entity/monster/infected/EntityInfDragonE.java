package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfDragonEHead;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileDragonE;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.EntityBodyDeadPayload;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityInfDragonE
extends EntityPInfected
implements EntityCutomAttack,
EntityBodyParts {
    protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityInfDragonE.class, EntityDataSerializers.BOOLEAN);
    private int flying;
    private float aaa;
    private float sss;
    private final ServerBossEvent bossInfo = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false);
    private EntityBody leftTendril;
    private EntityBody rightTendril;
    private EntityBody head;
    private float leftTendrilHealth;
    private float rightTendrilHealth;
    private float headlHealth;
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityInfDragonE.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(EntityInfDragonE.class, EntityDataSerializers.BOOLEAN);
    private int limit;
    private boolean skillFlame;
    private double tttX;
    private double tttY;
    private double tttZ;
    private double tttH;
    private double tttHH;

    public EntityInfDragonE(EntityType<? extends EntityInfDragonE> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.canModRender = 0;
        this.type = (byte)14;
        this.killcount = -10.0;
        this.goalSelector.removeGoal(this.folow);
        this.flying = 0;
        this.skillFlame = false;
        this.leftTendril = new EntityBody(this, 2.6f, 2.5f, 1.0f, 3.1f, 2.8f, 1, 1, true);
        this.rightTendril = new EntityBody(this, 2.6f, 2.5f, 1.0f, 3.1f, 2.8f, -1, 2, true);
        this.head = new EntityBody(this, 2.2f, 2.2f, 1.0f, 4.0f, 2.0f, -1, 3, false, 0.2f);
        this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.headlHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.noCulling = true;
        this.moveControl = new AIMoveControl(this);
    }

    @Override
    public int getParasiteIDRegister() {
        return 64;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infdragoneCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatusAOE(this, 1.9, false, 8.0, 6.0));
        this.goalSelector.addGoal(6, new AIMoveRandom());
        this.goalSelector.addGoal(6, new AIFireballAttack(this));
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, 64.0));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 60, 32, 7, true, 1));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPInfected.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.INFDRAGONE_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.INFDRAGONE_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.27);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.INFDRAGONE_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.INFDRAGONE_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, 64.0);
        return builder;
    }

    @Override
    public void aiStep() {
        if (this.isNoAi()) {
            return;
        }
        super.aiStep();
        this.killcount = -10.0;
        if (this.headlHealth > 0.0f) {
            this.head.tick();
        }
        if (this.leftTendrilHealth > 0.0f) {
            this.leftTendril.tick();
        }
        if (this.rightTendrilHealth > 0.0f) {
            this.rightTendril.tick();
        }
        if (!this.level().isClientSide && this.srpTicks == 10) {
            if ((this.level().getBlockState(this.blockPosition().below(1)).getBlock() != Blocks.AIR || this.level().getBlockState(this.blockPosition().below(2)).getBlock() != Blocks.AIR) && this.getFlyingState() && this.getRandom().nextInt(3) == 0) {
                Mot.setY(this, 0.5);
            }
            if (this.getRandom().nextInt(9) == 0 && !this.getFlyingState()) {
                this.changeStateTo(true);
                return;
            }
        }
        if (this.flying >= 1) {
            ++this.flying;
        }
        if (this.getFlyingState()) {
            this.aaa += 0.08f;
            this.sss += 0.782f;
            if (this.sss >= 24.0f) {
                this.playSound(SoundEvents.ENDER_DRAGON_FLAP, 5.0f, 0.8f + this.getRandom().nextFloat() * 0.3f);
                this.sss = 0.0f;
            }
            if (this.onGround() && !this.level().isClientSide && this.flying > 40) {
                this.changeStateTo(false);
            }
        } else {
            this.aaa = 0.08f;
            this.sss = 0.0f;
        }
    }

    public void tick() {
        super.tick();
        if (this.getFlyingState()) {
            this.setNoGravity(true);
        } else {
            this.setNoGravity(false);
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

    public float getaaa() {
        return this.aaa;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag) {
            if (source.getEntity() != null && source.getEntity() instanceof ServerPlayer) {
                this.bossInfo.addPlayer((ServerPlayer)source.getEntity());
            }
            if (this.getRandom().nextInt(12) == 0 && !this.getFlyingState()) {
                this.changeStateTo(true);
            }
        }
        return flag;
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.level().isClientSide) {
            return false;
        }
        boolean flag = this.hurt(source, amount);
        if (!flag) {
            return false;
        }
        if (this.leftTendril.getPartId() == id) {
            this.leftTendrilHealth -= amount;
            if (this.leftTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(SRPEntities.TENDRIL.get(), this.level());
                tendril.setSkin(7);
                tendril.copyPosition(this.leftTendril);
                this.level().addFreshEntity((Entity)tendril);
                this.leftTendril.discard();
                this.level().broadcastEntityEvent((Entity)this, (byte)11);
                PacketDistributor.sendToAllPlayers(new EntityBodyDeadPayload(this.getId(), id));
                this.changeStateTo(false);
            }
        } else if (this.rightTendril.getPartId() == id) {
            this.rightTendrilHealth -= amount;
            if (this.rightTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(SRPEntities.TENDRIL.get(), this.level());
                tendril.setSkin(8);
                tendril.copyPosition(this.rightTendril);
                this.level().addFreshEntity((Entity)tendril);
                this.rightTendril.discard();
                this.level().broadcastEntityEvent((Entity)this, (byte)22);
                PacketDistributor.sendToAllPlayers(new EntityBodyDeadPayload(this.getId(), id));
                this.changeStateTo(false);
            }
        } else if (this.head.getPartId() == id) {
            this.headlHealth -= amount;
            if (this.headlHealth <= 0.0f) {
                EntityInfDragonEHead tendril = new EntityInfDragonEHead(SRPEntities.SIM_DRAGONEHEAD.get(), this.level());
                tendril.setSkin(5);
                tendril.copyPosition(this.head);
                this.level().addFreshEntity((Entity)tendril);
                this.head.discard();
                this.level().broadcastEntityEvent((Entity)this, (byte)33);
                PacketDistributor.sendToAllPlayers(new EntityBodyDeadPayload(this.getId(), id));
            }
        }
        return flag;
    }

    @Override
    public void setBodyPartDead(int id) {
        if (this.leftTendril.getPartId() == id) {
            this.leftTendril.discard();
        } else if (this.rightTendril.getPartId() == id) {
            this.rightTendril.discard();
        } else if (this.head.getPartId() == id) {
            this.head.discard();
        }
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.head.discard();
        }
        if (this.leftTendril != null) {
            this.leftTendril.discard();
        }
        if (this.rightTendril != null) {
            this.rightTendril.discard();
        }
        super.discard();
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public void changeStateTo(boolean fly) {
        if (this.limit >= 1) {
            return;
        }
        if (fly) {
            if (!this.getFlyingState()) {
                if (this.leftTendrilHealth <= 0.0f || this.rightTendrilHealth <= 0.0f) {
                    return;
                }
                this.moveControl = new AIMoveControl(this);
                this.setParasiteStatus(3);
                this.entityData.set(FLYING, true);
                Mot.setY(this, 0.5);
                this.aaa += 0.08f;
                this.flying = 1;
                this.sss = 19.85f;
            }
        } else if (this.getFlyingState()) {
            this.moveControl = new MoveControl((Mob)this);
            this.setParasiteStatus(0);
            this.entityData.set(FLYING, false);
            this.flying = 0;
            this.aaa = 0.0f;
            this.sss = 0.0f;
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.75f;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        return super.doHurtTarget(entityIn);
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        return this.doHurtTarget(entityIn);
    }

    @Override
    protected void selfExplode() {
    }

    @Override
    protected void spawnGore() {
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENDER_DRAGON_HURT;
    }

    protected float getSoundVolume() {
        return 5.0f;
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.SPIDER_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        return floo;
    }

    public boolean getFlyingState() {
        return (Boolean)this.entityData.get(FLYING);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("parasiteleftTendril", this.leftTendrilHealth);
        compound.putFloat("parasiterightTendril", this.rightTendrilHealth);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("parasiteleftTendril", 99)) {
            this.leftTendrilHealth = compound.getFloat("parasiteleftTendril");
            if (this.leftTendrilHealth <= 0.0f) {
                this.level().broadcastEntityEvent((Entity)this, (byte)11);
            }
        }
        if (compound.contains("parasiterightTendril", 99)) {
            this.rightTendrilHealth = compound.getFloat("parasiterightTendril");
            if (this.rightTendrilHealth <= 0.0f) {
                this.level().broadcastEntityEvent((Entity)this, (byte)22);
            }
        }
    }

    public float getLeft() {
        return this.leftTendrilHealth;
    }

    public float getRight() {
        return this.rightTendrilHealth;
    }

    public float getHead() {
        return this.headlHealth;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 11) {
            this.leftTendrilHealth = 0.0f;
        } else if (id == 22) {
            this.rightTendrilHealth = 0.0f;
        } else if (id == 33) {
            this.headlHealth = 0.0f;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        super.move(type, movement);
        this.checkInsideBlocks();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VEX_FLAGS, (byte) (0));
        builder.define(FLYING, true);
        builder.define(ATTACKING, false);
    }

    private boolean getVexFlag(int mask) {
        byte i = (Byte)this.entityData.get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = ((Byte)this.entityData.get(VEX_FLAGS)).byteValue();
        i = value ? (i |= mask) : (i &= ~mask);
        this.entityData.set(VEX_FLAGS, (byte) (((byte)(i & 0xFF))));
    }

    public boolean isAttacking() {
        return (Boolean)this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillFlame;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillFlame = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.flame();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void flame() {
        if (this.getFlyingState() || this.headlHealth <= 0.0f) {
            this.skillFlame = true;
            this.limit = 0;
            return;
        }
        if (this.limit == 0) {
            LivingEntity entitylivingbase = this.getTarget();
            if (entitylivingbase == null) {
                this.skillFlame = true;
                this.limit = 0;
                return;
            }
            this.tttX = entitylivingbase.getX();
            this.tttY = entitylivingbase.getY();
            this.tttZ = entitylivingbase.getZ();
            this.tttH = entitylivingbase.getBoundingBox().minY;
            this.tttHH = entitylivingbase.getBbHeight();
        }
        ++this.limit;
        this.setParasiteStatus(10);
        this.getNavigation().moveTo(this.tttX, this.tttY, this.tttZ, 0.0);
        this.resetIdleTime();
        if (this.tickCount % 10 != 0) {
            return;
        }
        double d1 = 4.0;
        Vec3 vec3d = this.getViewVector(1.0f);
        double d2 = this.tttX - (this.getX() + vec3d.x * 4.0);
        double d3 = this.tttH + this.tttHH / 4.0 - (0.5 + this.getY() + (double)(this.getBbHeight() / 4.0f));
        double d4 = this.tttZ - (this.getZ() + vec3d.z * 4.0);
        this.level().levelEvent((Player)null, 1016, this.blockPosition(), 0);
        EntityProjectileDragonE entitylargefireball = new EntityProjectileDragonE(SRPEntities.MISSILE.get(), this.level(), (LivingEntity)this, d2, d3, d4);
        Mot.setPosX(entitylargefireball, this.getX() + vec3d.x * 4.0);
        Mot.setPosY(entitylargefireball, this.getY() + (double)(this.getBbHeight() / 2.0f) + 0.5);
        Mot.setPosZ(entitylargefireball, this.getZ() + vec3d.z * 4.0);
        this.level().addFreshEntity((Entity)entitylargefireball);
        if (this.limit >= 60) {
            this.skillFlame = true;
            this.setParasiteStatus(0);
            this.limit = 0;
        }
    }

    static class AIFireballAttack
    extends Goal {
        private final EntityInfDragonE parentEntity;
        public int attackTimer;

        public AIFireballAttack(EntityInfDragonE ghast) {
            this.parentEntity = ghast;
        }

        public boolean canUse() {
            return this.parentEntity.getTarget() != null && this.parentEntity.getFlyingState() && this.parentEntity.headlHealth > 0.0f;
        }

        public void start() {
            this.attackTimer = 0;
        }

        public void stop() {
            this.parentEntity.setAttacking(false);
        }

        public void tick() {
            LivingEntity entitylivingbase = this.parentEntity.getTarget();
            double d0 = 64.0;
            if (entitylivingbase == null) {
                return;
            }
            if (entitylivingbase.distanceToSqr((Entity)this.parentEntity) < 4096.0 && this.parentEntity.hasLineOfSight((Entity)entitylivingbase)) {
                Level world = this.parentEntity.level();
                ++this.attackTimer;
                if (this.parentEntity.hasEffect(SRPPotions.RAGE_E)) {
                    ++this.attackTimer;
                }
                this.parentEntity.resetIdleTime();
                if (this.attackTimer == 10) {
                    // empty if block
                }
                if (this.attackTimer == 20) {
                    double d1 = 4.0;
                    Vec3 vec3d = this.parentEntity.getViewVector(1.0f);
                    double d2 = entitylivingbase.getX() - (this.parentEntity.getX() + vec3d.x * 4.0);
                    double d3 = entitylivingbase.getBoundingBox().minY + (double)(entitylivingbase.getBbHeight() / 2.0f) - (0.5 + this.parentEntity.getY() + (double)(this.parentEntity.getBbHeight() / 2.0f));
                    double d4 = entitylivingbase.getZ() - (this.parentEntity.getZ() + vec3d.z * 4.0);
                    world.levelEvent((Player)null, 1016, this.parentEntity.blockPosition(), 0);
                    EntityProjectileDragonE entitylargefireball = new EntityProjectileDragonE(SRPEntities.MISSILE.get(), world, (LivingEntity)this.parentEntity, d2, d3, d4);
                    Mot.setPosX(entitylargefireball, this.parentEntity.getX() + vec3d.x * 4.0);
                    Mot.setPosY(entitylargefireball, this.parentEntity.getY() + (double)(this.parentEntity.getBbHeight() / 2.0f) + 0.5);
                    Mot.setPosZ(entitylargefireball, this.parentEntity.getZ() + vec3d.z * 4.0);
                    world.addFreshEntity((Entity)entitylargefireball);
                    this.parentEntity.noActionTime = 0;
                    this.attackTimer = -5;
                    for (int i = 0; i <= 2; ++i) {
                        this.parentEntity.level().addParticle(ParticleTypes.FLAME, this.parentEntity.getX() + vec3d.x * 4.0, this.parentEntity.getY() + (double)(this.parentEntity.getBbHeight() / 2.0f) + 0.5, this.parentEntity.getZ() + vec3d.z * 4.0, 0.0, -1.0, 0.0);
                    }
                }
            } else if (this.attackTimer > 0) {
                --this.attackTimer;
            }
            this.parentEntity.setAttacking(this.attackTimer > 10);
        }
    }

    class AIMoveRandom
    extends Goal {
        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return EntityInfDragonE.this.getRandom().nextInt(7) == 0 && EntityInfDragonE.this.getFlyingState();
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityInfDragonE.this.blockPosition();
            int flag = 1;
            double speed = 0.5;
            if (EntityInfDragonE.this.getTarget() != null) {
                if (EntityInfDragonE.this.distanceToSqr((Entity)EntityInfDragonE.this.getTarget()) > 100.0) {
                    blockpos = EntityInfDragonE.this.getTarget().blockPosition();
                    flag = 2;
                } else if (EntityInfDragonE.this.distanceToSqr((Entity)EntityInfDragonE.this.getTarget()) < 36.0) {
                    blockpos = EntityInfDragonE.this.getTarget().blockPosition();
                    flag = 3;
                    speed += 0.25;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityInfDragonE.this.getRandom().nextInt(15) - 7, EntityInfDragonE.this.getRandom().nextInt(11) - 5, EntityInfDragonE.this.getRandom().nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.offset(EntityInfDragonE.this.getRandom().nextInt(6) - 2, EntityInfDragonE.this.getRandom().nextInt(7) - 2, EntityInfDragonE.this.getRandom().nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.offset(EntityInfDragonE.this.getRandom().nextInt(4) + 3, EntityInfDragonE.this.getRandom().nextInt(5) + 4, EntityInfDragonE.this.getRandom().nextInt(4) + 3);
                }
                if (!EntityInfDragonE.this.level().isEmptyBlock(blockpos1)) continue;
                EntityInfDragonE.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityInfDragonE.this.getTarget() != null) break;
                EntityInfDragonE.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityInfDragonE vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityInfDragonE.this.getX();
                double d1 = this.getWantedY() - EntityInfDragonE.this.getY();
                double d2 = this.getWantedZ() - EntityInfDragonE.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityInfDragonE.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityInfDragonE.this, 0.5);
                    Mot.mulY(EntityInfDragonE.this, 0.5);
                    Mot.mulZ(EntityInfDragonE.this, 0.5);
                } else {
                    Mot.addX(EntityInfDragonE.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityInfDragonE.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityInfDragonE.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityInfDragonE.this.getTarget() == null) {
                        EntityInfDragonE.this.setYRot(-((float)Mth.atan2((double)EntityInfDragonE.this.getDeltaMovement().x, (double)EntityInfDragonE.this.getDeltaMovement().z)) * 57.295776f);
        EntityInfDragonE.this.yBodyRot = EntityInfDragonE.this.getYRot();
                    } else {
                        double d4 = EntityInfDragonE.this.getTarget().getX() - EntityInfDragonE.this.getX();
                        double d5 = EntityInfDragonE.this.getTarget().getZ() - EntityInfDragonE.this.getZ();
                        EntityInfDragonE.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityInfDragonE.this.yBodyRot = EntityInfDragonE.this.getYRot();
                    }
                }
            }
        }
    }
}

