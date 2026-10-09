package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvadeDash;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileWebball;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.PathNavigateClimberStatus;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;

public class EntityOrch
extends EntityPPure
implements EntityCutomAttack,
EntityCanShoot {
    private float attackTimer;
    private boolean up;
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityOrch.class, EntityDataSerializers.BYTE);

    public EntityOrch(EntityType<? extends EntityOrch> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.noCulling = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 84;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EntityAISkill(this, 40, 100, 10, true, 14));
        this.setskillLeapValues(0.5f, 3.5, 4);
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.12));
        this.goalSelector.addGoal(6, new EntityAIAttackProjectile(this, 40, 15, 4));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 7));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatusAOE(this, 1.3, false, 8.0, 2.0));
        this.goalSelector.addGoal(2, new EntityAIEvadeDash(this, 17, 2, 5, 3.5, 15));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPure.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.ORCH_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.ORCH_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.2775);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.ORCH_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.ORCH_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.pureFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.up) {
            this.attackTimer = (float)((double)this.attackTimer + 0.2);
            if (this.attackTimer > 1.0f) {
                this.up = false;
            }
        } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.1);
        }
        if (!this.level().isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
        }
    }

    @Override
    protected boolean spawnT(LivingEntity in, int range2, int mini2, int check, int type) {
        if (this.hasLineOfSight((Entity)in)) {
            return false;
        }
        if (this.getRandom().nextInt(4) == 0) {
            return super.spawnT(in, range2, mini2, check, type);
        }
        return super.spawnT(in, range2, mini2, check, 2);
    }

    public void setInWeb() {
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLIMBING, (byte) (0));
    }

    public boolean isBesideClimbableBlock() {
        return ((Byte)this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = (Byte)this.entityData.get(CLIMBING);
        b0 = climbing ? (byte)(b0 | 1) : (byte)(b0 & 0xFFFFFFFE);
        this.entityData.set(CLIMBING, (byte) (b0));
    }

    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
    }

    @Override
    public void setAttackTarget(LivingEntity entitylivingbaseIn) {
        super.setTarget(entitylivingbaseIn);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new PathNavigateClimberStatus((Mob)this, worldIn);
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            Mot.addY(entityIn, 0.5000000059604645);
        }
        return flag;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 3.5f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ORCH_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ORCH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.ORCH_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.orchOrbEffects, mobs);
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.HEAVY_STEPS_MULTIPLE.get(), 0.15f, 1.0f);
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        if (this.borderOrb != 0) {
            return false;
        }
        this.up = true;
        this.attackTimer = 0.0f;
        this.level().broadcastEntityEvent((Entity)this, (byte)12);
        boolean flag = false;
        AABB axisalignedbb = new AABB(entityIn.getX(), entityIn.getY(), entityIn.getZ(), entityIn.getX() + 1.0, entityIn.getY() + 1.0, entityIn.getZ() + 1.0).inflate(2.0);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            if (mob instanceof EntityParasiteBase) {
                if (this.getTarget() != mob) continue;
                this.setTarget(null);
                return false;
            }
            if (mob == this || !this.hasLineOfSight((Entity)mob) || !this.doHurtTarget((Entity)mob)) continue;
            flag = true;
        }
        return flag;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            switch (this.getRandom().nextInt(2)) {
                case 0: {
                    this.setSkin(1);
                    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(SRPAttributes.ORCH_HEALTH * 0.5);
                    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.ORCH_ATTACK_DAMAGE * 1.5);
                    this.setHealth((float)this.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
                    break;
                }
                case 1: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }

    public float getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    protected void skillLeap() {
        if (this.leapMotionY == 0.0f) {
            return;
        }
        if (this.getTarget() != null && this.shouldWorkTask() && !this.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && this.getParasiteStatus() <= 2) {
            LivingEntity entitylivingbase = this.getTarget();
            if (this.attacking == 0) {
                ++this.attacking;
                this.targetX = entitylivingbase.getX();
                this.targetZ = entitylivingbase.getZ();
            }
        }
        if (this.attacking >= 1) {
            ++this.attacking;
            this.skillBreakBlocks();
            if (this.attacking % 5 == 0 && this.attacking < 40 && SRPConfigMobs.lodoEnabled) {
                EntityLodo b = new EntityLodo(SRPEntities.BUGLIN.get(), this.level());
                b.copyPosition((Entity)this);
                this.level().addFreshEntity((Entity)b);
            }
            if (this.attacking == 2 && this.onGround()) {
                this.setParasiteStatus(10);
                this.getNavigation().stop();
                double d0 = this.targetX - this.getX();
                double d1 = this.targetZ - this.getZ();
                double f = (float)Math.sqrt((double)(d0 * d0 + d1 * d1));
                Mot.setY(this, this.leapMotionY);
                Mot.addX(this, d0 / f * this.jumpSpeed * 0.9 + this.getDeltaMovement().x * 0.3);
                Mot.addZ(this, d1 / f * this.jumpSpeed * 0.9 + this.getDeltaMovement().z * 0.3);
            }
            if (this.attacking > 2 && this.onGround()) {
                if (this.jumpR != 0) {
                    float damage = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                    AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate((double)this.jumpR, 2.0, (double)this.jumpR);
                    List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                    for (LivingEntity mob : moblist) {
                        if (mob == this || mob instanceof EntityParasiteBase) continue;
                        mob.knockback(2.5f, this.getX() - mob.getX(), this.getZ() - mob.getZ());
                        this.doHurtTarget((Entity)mob);
                    }
                }
                this.setParasiteStatus(0);
                this.attacking = 0;
                if (this.type >= 31 && this.getBbHeight() > 2.0f) {
                    this.playSound(SRPSounds.HITGROUND.get(), 15.0f, 1.0f);
                }
                this.SkillLeapFlag = true;
            }
        }
    }

    @Override
    public void skillBreakBlocks() {
        LivingEntity target;
        if (this.getBlockH() == 0.0f) {
            return;
        }
        int blocksbroke = 0;
        int i1 = Mth.floor((double)(this.getY() + 0.1));
        double l1 = this.getX();
        double i2 = this.getZ();
        boolean flag = false;
        int Brangeatm = this.BGrange;
        int offsetT = 0;
        if (this.getTarget() != null && (target = this.getTarget()).distanceToSqr(this.getX(), target.getY(), this.getZ()) < 9.0) {
            if (target.getY() - this.getY() < -1.0) {
                offsetT -= 2;
            } else if (target.getY() - this.getY() > 2.0) {
                ++offsetT;
                this.BGrange = 0;
            }
        }
        for (int k2 = -1 * this.BGrange; k2 <= 1 * this.BGrange; ++k2) {
            for (int l2 = -1 * this.BGrange; l2 <= 1 * this.BGrange; ++l2) {
                for (int j = 1 + offsetT; j <= this.BGheight + offsetT; ++j) {
                    String name;
                    double i3 = l1 + (double)k2;
                    double k = i1 + j;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = BlockPos.containing(i3, k, l);
                    BlockState iblockstate = this.level().getBlockState(blockpos);
                    Block block = iblockstate.getBlock();
                    float bHard = iblockstate.getDestroySpeed(this.level(), blockpos);
                    if (!(bHard <= this.getBlockH()) || !(bHard >= 0.0f) || block instanceof IMetaName && block != SRPBlocks.ParasiteCanister.get() || block == SRPBlocks.BiomeHeart.get() || block == SRPBlocks.ColonyHeart.get() || block == SRPBlocks.ParasiteRubbleDense.get() || block == SRPBlocks.ParasiteCanisterActive.get() || this.blockException(name = block.builtInRegistryHolder().key().location().toString()) || block == Blocks.AIR || !iblockstate.canEntityDestroy(this.level(), blockpos, this) || !EventHooks.onEntityDestroyBlock((LivingEntity)this, (BlockPos)blockpos, (BlockState)iblockstate)) continue;
                    if (SRPConfig.cystActive) {
                        boolean bl = flag = this.destroyBlockPos(blockpos, false) || flag;
                        if (SRPConfig.doTileDrops) {
                            this.addToBlockInv(BlockIds.stateString(iblockstate));
                        }
                    } else {
                        this.destroyBlockPos(blockpos, SRPConfig.doTileDrops);
                    }
                    ++blocksbroke;
                }
            }
        }
        this.BGrange = Brangeatm;
        this.SkillBGflag = true;
    }

    public Fireball getProj(double accelX, double accelY, double accelZ) {
        this.playSound(SRPSounds.DORPA_RANGE.get(), 2.0f, 1.0f);
        LivingEntity entitylivingbase = this.getTarget();
        accelY = entitylivingbase.getBoundingBox().minY + (double)(entitylivingbase.getBbHeight() / 3.0f) - (1.0 + this.getY() + (double)(this.getBbHeight() / 2.0f));
        return new EntityProjectileWebball(SRPEntities.WEBBALL.get(), this.level(), (LivingEntity)this, accelX, accelY, accelZ, 2);
    }

    @Override
    public void playProjSound() {
    }
}

