package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFollowBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;

public class EntityWymoAdapted
extends EntityPAdapted
implements EntityCanHaveBodies,
EntityCutomAttack {
    private UUID following;
    private boolean canF;
    private BlockPos digTarget;
    public float diggingModel;
    public final float heightTwo = 2.4f;
    private int bodiesT;
    private int bodiesTOut;
    private float attackTimer;
    private boolean up;
    private static final EntityDataAccessor<Byte> PART = SynchedEntityData.defineId(EntityWymoAdapted.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> TAIL = SynchedEntityData.defineId(EntityWymoAdapted.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DIG = SynchedEntityData.defineId(EntityWymoAdapted.class, EntityDataSerializers.BOOLEAN);

    public EntityWymoAdapted(EntityType<? extends EntityWymoAdapted> type, Level worldIn) {
        super(type, worldIn);
        this.noCulling = true;
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.canModRender = 1;
        this.borderOrb = -1;
        this.goalSelector.removeGoal(this.folow);
        this.setPathfindingMalus(PathType.WATER, -1.0f);
        this.diggingModel = 0.0f;
        this.canF = true;
        this.bodiesT = 0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 57;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PART, (byte) (0));
        builder.define(TAIL, false);
        builder.define(DIG, false);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new EntityAIFollowBodies(this, 1.9f, 0.32, 5, 10000.0, 140));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatusAOE(this, 1.3, false, 0.0, 4.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAdapted.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.WYMO_HEALTH + SRPAttributes.WYMO_A_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.WYMO_ARMOR + SRPAttributes.WYMO_A_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.32);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.WYMO_ATTACK_DAMAGE + SRPAttributes.WYMO_A_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.adaptedFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.handleDigging();
        if (this.up) {
            this.attackTimer = (float)((double)this.attackTimer + 0.3);
            if ((double)this.attackTimer > 1.5) {
                this.up = false;
            }
        } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.2);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return super.removeWhenFarAway(0.0) && this.following == null;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        if (this.getDigging()) {
            return false;
        }
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        this.up = true;
        this.attackTimer = 0.0f;
        this.level().broadcastEntityEvent((Entity)this, (byte)12);
        boolean flag = false;
        this.playSound(SRPSounds.SWIPE.get(), 2.0f, 1.0f);
        AABB axisalignedbb = new AABB(entityIn.getX(), entityIn.getY(), entityIn.getZ(), entityIn.getX() + 1.0, entityIn.getY() + 1.0, entityIn.getZ() + 1.0).inflate(1.5);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            if (mob instanceof EntityParasiteBase || !this.hasLineOfSight((Entity)mob) || !this.doHurtTarget((Entity)mob)) continue;
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        this.madeRng = 0;
        if (source.is(DamageTypes.IN_WALL)) {
            return false;
        }
        if (this.bodiesT - 1 >= this.getBodyNumber()) {
            return false;
        }
        boolean flag = super.hurt(source, amount);
        if (flag && this.following != null) {
            Entity follo = this.getFather(this.following);
            if (follo == null) {
                this.following = null;
                return flag;
            }
            if (!follo.isAlive()) {
                this.following = null;
                return flag;
            }
            follo.hurt(source, amount * 0.5f);
        }
        return flag;
    }

    @Override
    protected void attackEntityFromCap(int go) {
        if (this.getBodyNumber() != 0) {
            return;
        }
        super.attackEntityFromCap(go);
    }

    public void push(Entity entityIn) {
        if (this.getDigging()) {
            return;
        }
        super.push(entityIn);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.0f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.AWYMO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.AWYMO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.AWYMO_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.MOBSILENCE.get(), 0.15f, 1.0f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        return floo;
    }

    @Override
    public void spawnEffectsGore() {
        for (int i = 0; i <= 60; ++i) {
            if (i % 4 == 0) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 150, 0, 0);
            }
            if (i % 5 != 0) continue;
            this.spawnParticles(SRPEnumParticle.GSPLASH, 2, -1, -1);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.following != null) {
            compound.putUUID("wormhead", this.following);
        }
        compound.putBoolean("wormcanf", this.canF);
        compound.putByte("wormpart", this.getBodyNumber());
        compound.putBoolean("wormtail", this.getBodyTail());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("wormhead")) {
            this.following = compound.getUUID("wormhead");
        }
        if (compound.contains("wormcanf", 99)) {
            this.canF = compound.getBoolean("wormcanf");
        }
        if (compound.contains("wormpart", 99)) {
            this.setBodyNumber(compound.getByte("wormpart"));
        }
        if (compound.contains("wormtail", 99)) {
            this.setBodyTail(compound.getBoolean("wormtail"));
        }
    }

    @Override
    public void skillBreakBlocks() {
        if (this.getBodyNumber() == 0) {
            super.skillBreakBlocks();
        }
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        if (this.getBodyNumber() == 0) {
            super.onKillEntity(entityLivingIn);
            return;
        }
        EntityParasiteBase head = this.getHead();
        if (head != null) {
            head.setKillC(1.0);
        }
    }

    public float getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 12) {
            this.up = true;
            this.attackTimer = 0.0f;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void setAttackTarget(LivingEntity entitylivingbaseIn) {
        if (this.getBodyNumber() == 0) {
            super.setTarget(entitylivingbaseIn);
        }
    }

    @Override
    public void setTargetPos(BlockPos in) {
        this.digTarget = in;
    }

    @Override
    public BlockPos getTargetPos() {
        return this.digTarget;
    }

    @Override
    public boolean getDigging() {
        return (Boolean)this.entityData.get(DIG);
    }

    @Override
    public void setDigging(boolean in) {
        if (in) {
            this.playSound(SRPSounds.AWYMO_DIG.get(), 2.0f, this.getVoicePitch());
        }
        this.entityData.set(DIG, in);
    }

    @Override
    public float getDigModel() {
        return this.diggingModel;
    }

    @Override
    public int getBodiesT() {
        return this.bodiesT;
    }

    @Override
    public void setBodiesT(int in) {
        this.bodiesT = in;
    }

    @Override
    public byte getBodyNumber() {
        return (Byte)this.entityData.get(PART);
    }

    @Override
    public void setBodyNumber(int texture) {
        this.entityData.set(PART, (byte) (((byte)texture)));
    }

    @Override
    public boolean getBodyTail() {
        return (Boolean)this.entityData.get(TAIL);
    }

    @Override
    public void setBodyTail(boolean in) {
        this.entityData.set(TAIL, in);
    }

    @Override
    public void setFollowing(EntityCanHaveBodies in) {
        if (in == null) {
            return;
        }
        this.following = in.getEntity().getUUID();
    }

    @Override
    public UUID getFollowing() {
        return this.following;
    }

    @Override
    public int getBodyLength() {
        return 4;
    }

    @Override
    public EntityCanHaveBodies getAnotherBody(Level in) {
        return new EntityWymoAdapted(SRPEntities.ADA_TOZOON.get(), in);
    }

    @Override
    public void copyCopy(EntityCanHaveBodies in) {
        this.copyPosition(in.getEntity());
        Mot.setPosY(this, this.getY() + (0.5));
    }

    @Override
    public Entity getEntity() {
        return this;
    }

    @Override
    public void onSpawn(DifficultyInstance difficulty, SpawnGroupData livingdata) {
        if (this.level() instanceof ServerLevelAccessor accessor) {
            this.finalizeSpawn(accessor, difficulty, MobSpawnType.NATURAL, livingdata);
        }
    }

    @Override
    public void setCanF(boolean in) {
        this.canF = in;
    }

    @Override
    public boolean getCanF() {
        return this.canF;
    }

    @Override
    public void bodyPartEffect() {
        if (this.getDigging()) {
            return;
        }
        if (this.getBodyNumber() >= 1 && this.getBodyNumber() <= 3) {
            AABB axisalignedbb = new AABB(this.blockPosition()).inflate(5.0);
            List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity mob : moblist) {
                Player player;
                if (mob instanceof EntityParasiteBase || mob instanceof Player && (player = (Player)mob).isCreative() || !this.attackEntityAsMobAOE((Entity)mob)) continue;
                return;
            }
        }
    }

    private void spawnGroundParticles() {
        BlockState state = this.level().getBlockState(this.blockPosition().below());
        if (state.getBlock() != Blocks.AIR) {
            BlockState id = state;
            for (int i = 0; i < 15; ++i) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, id), this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 3.0f) - (double)this.getBbWidth(), this.getY(), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 3.0f) - (double)this.getBbWidth(), this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() * 0.02);
            }
        }
    }

    @Override
    public double getKillPoints() {
        return this.killcount;
    }

    @Override
    public EntityParasiteBase getEvolution(Level in) {
        return null;
    }

    @Override
    public EntityParasiteBase getHead() {
        if (this.following != null) {
            EntityCanHaveBodies follo = (EntityCanHaveBodies)this.getFather(this.following);
            if (follo == null) {
                return null;
            }
            if (!follo.getEntity().isAlive()) {
                return null;
            }
            return follo.getHead();
        }
        return this;
    }

    @Override
    public Entity getFather(UUID uuid) {
        List serverList = SRPEntityUtil.allEntities(this.level());
        for (int x = 0; x < serverList.size(); ++x) {
            if (!uuid.equals(((Entity)serverList.get(x)).getUUID())) continue;
            return (Entity)serverList.get(x);
        }
        return null;
    }

    @Override
    public void handleDigging() {
        if (this.getDigging()) {
            this.bodiesTOut = 0;
            if (this.getBodyNumber() == 0) {
                this.spawnGroundParticles();
                if (this.getBbHeight() > 0.25f) {
                    this.setSize(this.getBbWidth(), this.getBbHeight() - 0.15f);
                }
                if (this.diggingModel < 2.4f) {
                    this.diggingModel += 0.08f;
                }
            } else {
                if (this.srpTicks == 10) {
                    ++this.bodiesT;
                }
                if (this.bodiesT >= this.getBodyNumber()) {
                    this.spawnGroundParticles();
                    if (this.getBbHeight() > 0.25f) {
                        this.setSize(this.getBbWidth(), this.getBbHeight() - 0.15f);
                    }
                    if (this.diggingModel < 2.4f) {
                        this.diggingModel += 0.08f;
                    }
                }
            }
        } else {
            this.bodiesT = 0;
            if (this.getBodyNumber() == 0) {
                if (this.getBbHeight() < 1.2f) {
                    this.setSize(this.getBbWidth(), this.getBbHeight() + 0.13f);
                    this.spawnGroundParticles();
                }
                if (this.diggingModel >= 0.0f) {
                    this.diggingModel -= 0.08f;
                }
            } else if (this.getX() != this.xo && this.getZ() != this.zo) {
                if (this.getBbHeight() < 1.2f) {
                    this.setSize(this.getBbWidth(), this.getBbHeight() + 0.13f);
                    this.spawnGroundParticles();
                }
                if (this.diggingModel >= 0.0f) {
                    this.diggingModel -= 0.08f;
                }
            }
        }
    }

    private net.minecraft.world.entity.EntityDimensions srpSize;

    /** The 1.12 setSize(width, height): the entity dimensions are replaced and the bounding box refreshed. */
    protected void setSize(float width, float height) {
        this.srpSize = net.minecraft.world.entity.EntityDimensions.scalable(width, height);
        this.refreshDimensions();
    }

    @Override
    protected net.minecraft.world.entity.EntityDimensions getDefaultDimensions(net.minecraft.world.entity.Pose pose) {
        return this.srpSize != null ? this.srpSize : super.getDefaultDimensions(pose);
    }
}
