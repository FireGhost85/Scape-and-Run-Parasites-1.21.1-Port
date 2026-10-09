package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.EntityOrbScary;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class EntityFlam
extends EntityPPreeminent {
    private EntityPPreeminent father;
    private BlockPos targetP;
    private int activationF;
    private boolean moveM;
    private byte typeDead;
    private byte steadyD;
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityFlam.class, EntityDataSerializers.BYTE);

    public EntityFlam(EntityType<? extends EntityFlam> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.setNoGravity(true);
        this.goalSelector.removeGoal(this.folow);
        this.borderOrb = -1;
        this.canModRender = 1;
        this.xpReward = SRPAttributes.XP_LiTTLE;
        if (SRPConfigMobs.emanaMaxY != 256) {
            this.goalSelector.addGoal(3, new EntityAIFlightLimits(this, SRPConfigMobs.emanaMaxY, true));
        }
    }

    @Override
    public int getParasiteIDRegister() {
        return 89;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new AIMoveRandom());
        this.goalSelector.addGoal(4, new AIChargeAttack());
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPreeminent.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.FLAM_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.FLAM_ARMOR);
        builder.add(Attributes.ATTACK_DAMAGE, 1.0);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.FLAM_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.preeminentFollow);
        return builder;
    }

    public void setDamageATT(EntityParasiteBase in) {
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(in.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2.0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isNoAi()) {
            return;
        }
        if (!this.level().isClientSide) {
            if (this.moveM) {
                ++this.activationF;
            }
            if (this.getX() == this.xo && this.getZ() == this.zo) {
                this.steadyD = (byte)(this.steadyD + 1);
            }
            if (this.steadyD > 20) {
                this.DeadDead();
                return;
            }
            if (this.targetP != null && this.father != null) {
                if (this.tickCount % 20 == 0 && this.getBlockH() != 0.0f) {
                    this.skillBreakBlocks();
                }
                if (this.distanceToSqr(Vec3.atCenterOf(this.targetP)) < 4.0 || this.moveM) {
                    this.DeadDead();
                    return;
                }
            } else {
                this.activationF += 5000;
                this.DeadDead();
                return;
            }
            if (this.tickCount > 400) {
                this.activationF += 5000;
                this.DeadDead();
                return;
            }
        }
    }

    @Override
    protected void selfExplode() {
        if (this.level().isClientSide) {
            this.spawnEffectsGore();
        }
        if (!this.level().isClientSide) {
            this.spawnGore();
            this.playSound(SRPSounds.MOBEXPLOTION.get(), 1.0f, 1.0f);
            this.dead = true;
            this.discard();
            AABB axisalignedbb = new AABB(this.getX(), this.getY() - 2.0, this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(4.0);
            List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity mob : moblist) {
                if (mob instanceof EntityParasiteBase) continue;
                this.doHurtTarget((Entity)mob);
            }
            EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY() - 2.0, this.getZ());
            entityareaeffectcloud.setRadius(this.getBbWidth() * 3.5f, 0.5f);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 2));
            entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.WITHER, 300, 2));
            entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 2, false, false));
            this.level().addFreshEntity((Entity)entityareaeffectcloud);
        }
    }

    public void DeadDead() {
        ++this.activationF;
        this.moveControl.setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.0);
        this.moveM = true;
        if (this.activationF < 20) {
            return;
        }
        block0 : switch (this.typeDead) {
            case 1: {
                this.selfExplode();
                break;
            }
            case 2: {
                EntityOrbScary ttt = new EntityOrbScary(SRPEntities.ORBSCARY.get(), this.level(), this.father, this.fuseOrb, this.orbStartTimer, false);
                ttt.copyPosition((Entity)this);
                Mot.setPosY(ttt, ttt.getY() - (3.0));
                this.level().addFreshEntity((Entity)ttt);
                this.playSound(SRPSounds.ORB_S.get(), 1.0f, 1.0f);
                break;
            }
            case 3: {
                if (this.targetP != null) {
                    if (this.distanceToSqr(Vec3.atCenterOf(this.targetP)) < 16.0) {
                        this.father.copyPosition((Entity)this);
                        this.father.setTeleFlam(false);
                        break;
                    }
                    switch (this.getRandom().nextInt(2)) {
                        case 0: {
                            this.selfExplode();
                            break;
                        }
                        case 1: {
                            EntityOrbScary ttt1 = new EntityOrbScary(SRPEntities.ORBSCARY.get(), this.level(), this.father, this.fuseOrb, this.orbStartTimer, false);
                            ttt1.copyPosition((Entity)this);
                            Mot.setPosY(ttt1, ttt1.getY() - (3.0));
                            this.level().addFreshEntity((Entity)ttt1);
                            this.playSound(SRPSounds.ORB_S.get(), 1.0f, 1.0f);
                        }
                    }
                    break;
                }
                switch (this.getRandom().nextInt(2)) {
                    case 0: {
                        this.selfExplode();
                        break block0;
                    }
                    case 1: {
                        EntityOrbScary ttt1 = new EntityOrbScary(SRPEntities.ORBSCARY.get(), this.level(), this.father, this.fuseOrb, this.orbStartTimer, false);
                        ttt1.copyPosition((Entity)this);
                        Mot.setPosY(ttt1, ttt1.getY() - (2.0));
                        this.level().addFreshEntity((Entity)ttt1);
                        this.playSound(SRPSounds.ORB_S.get(), 1.0f, 1.0f);
                    }
                }
            }
        }
        this.spawnCyst();
        this.discard();
    }

    public void setFatherTo(EntityPPreeminent in, int typeIn) {
        this.father = in;
        this.typeDead = (byte)typeIn;
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);
    }

    @Override
    public void setAttackTarget(LivingEntity entitylivingbaseIn) {
        super.setTarget(entitylivingbaseIn);
        if (entitylivingbaseIn != null) {
            this.targetP = entitylivingbaseIn.blockPosition();
        }
    }

    @Override
    protected boolean summonFlam(LivingEntity in) {
        return false;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.5f;
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        if (!this.moveM) {
            super.move(type, movement);
        }
        this.checkInsideBlocks();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VEX_FLAGS, (byte) (0));
    }

    @Override
    protected void spawnGore() {
        double i1 = Mth.floor((double)(this.getY() + 0.1));
        double l1 = this.getX();
        double i2 = this.getZ();
        for (int i = 0; i < 10 && SRPConfig.paraGore; ++i) {
            double d0 = (float)this.getX() + this.level().random.nextFloat();
            double d1 = (float)this.getY() + this.level().random.nextFloat();
            double d2 = (float)this.getZ() + this.level().random.nextFloat();
            double d3 = d0 - this.getX();
            double d4 = d1 - this.getY();
            double d5 = d2 - this.getZ();
            double d6 = (float)Math.sqrt((double)(d3 * d3 + d4 * d4 + d5 * d5));
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.5 / (d6 / 4.0 + 0.1);
            d4 = d4 * d7 * 2.0;
            EntityGore bomb = new EntityGore(SRPEntities.GORE.get(), this.level());
            bomb.setType((byte)12);
            bomb.copyPosition((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)), d4, d5 *= d7, 0.25, 0.65);
            this.level().addFreshEntity((Entity)bomb);
        }
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

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 100) {
            for (int i = 0; i <= 1; ++i) {
                this.spawnParticles(ParticleTypes.FLAME);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void skillBreakBlocks() {
        if (this.isRemoved()) {
            return;
        }
        if (this.getBlockH() == 0.0f) {
            return;
        }
        int blocksbroke = 0;
        int i1 = Mth.floor((double)(this.getY() + 0.1));
        double l1 = this.getX();
        double i2 = this.getZ();
        boolean flag = false;
        int Brangeatm = this.BGrange;
        boolean offsetT = false;
        for (int k2 = -1 * this.BGrange; k2 <= 1 * this.BGrange; ++k2) {
            for (int l2 = -1 * this.BGrange; l2 <= 1 * this.BGrange; ++l2) {
                for (int j = -1; j <= this.BGheight + 1; ++j) {
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

    class AIChargeAttack
    extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

        public AIChargeAttack() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (EntityFlam.this.targetP != null && !EntityFlam.this.getMoveControl().hasWanted() && EntityFlam.this.getRandom().nextInt(7) == 0 && !EntityFlam.this.moveM) {
                return EntityFlam.this.distanceToSqr(Vec3.atCenterOf(EntityFlam.this.targetP)) > 4.0;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return EntityFlam.this.isCharging() && EntityFlam.this.targetP != null && !EntityFlam.this.moveM;
        }

        public void start() {
            if (EntityFlam.this.getTarget() != null && EntityFlam.this.getTarget().isAlive()) {
                EntityFlam.this.targetP = EntityFlam.this.getTarget().blockPosition();
            }
            EntityFlam.this.moveControl.setWantedPosition((double)EntityFlam.this.targetP.getX() + 0.5, (double)(EntityFlam.this.targetP.getY() + 2), (double)EntityFlam.this.targetP.getZ() + 0.5, 0.8);
            EntityFlam.this.setCharging(true);
        }

        public void stop() {
            EntityFlam.this.setCharging(false);
        }

        public void tick() {
            double d0;
            if (EntityFlam.this.getTarget() != null && EntityFlam.this.getTarget().isAlive()) {
                EntityFlam.this.targetP = EntityFlam.this.getTarget().blockPosition();
            }
            if (EntityFlam.this.targetP != null && (d0 = EntityFlam.this.distanceToSqr(Vec3.atCenterOf(EntityFlam.this.targetP))) < 9.0) {
                EntityFlam.this.moveControl.setWantedPosition((double)EntityFlam.this.targetP.getX() + 0.5, (double)(EntityFlam.this.targetP.getY() + 2), (double)EntityFlam.this.targetP.getZ() + 0.5, 0.8);
            }
        }
    }

    class AIMoveRandom
    extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !EntityFlam.this.getMoveControl().hasWanted() && EntityFlam.this.getRandom().nextInt(7) == 0 && !EntityFlam.this.moveM;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityFlam.this.blockPosition();
            int flag = 1;
            double speed = 1.0;
            if (EntityFlam.this.getTarget() != null && EntityFlam.this.getTarget().isAlive()) {
                EntityFlam.this.targetP = EntityFlam.this.getTarget().blockPosition();
            }
            if (EntityFlam.this.targetP != null) {
                blockpos = EntityFlam.this.targetP;
                flag = 2;
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityFlam.this.getRandom().nextInt(15) - 7, EntityFlam.this.getRandom().nextInt(11) - 5, EntityFlam.this.getRandom().nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.offset(EntityFlam.this.getRandom().nextInt(6) - 2, EntityFlam.this.getRandom().nextInt(7) - 2, EntityFlam.this.getRandom().nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.offset(EntityFlam.this.getRandom().nextInt(4) + 3, EntityFlam.this.getRandom().nextInt(5) + 4, EntityFlam.this.getRandom().nextInt(4) + 3);
                }
                if (!EntityFlam.this.level().isEmptyBlock(blockpos1)) continue;
                EntityFlam.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityFlam.this.getTarget() != null) break;
                EntityFlam.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityFlam vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO && !EntityFlam.this.moveM) {
                double d0 = this.getWantedX() - EntityFlam.this.getX();
                double d1 = this.getWantedY() - EntityFlam.this.getY();
                double d2 = this.getWantedZ() - EntityFlam.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityFlam.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityFlam.this, 0.5);
                    Mot.mulY(EntityFlam.this, 0.5);
                    Mot.mulZ(EntityFlam.this, 0.5);
                } else {
                    Mot.addX(EntityFlam.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityFlam.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityFlam.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityFlam.this.getTarget() == null) {
                        EntityFlam.this.setYRot(-((float)Mth.atan2((double)EntityFlam.this.getDeltaMovement().x, (double)EntityFlam.this.getDeltaMovement().z)) * 57.295776f);
        EntityFlam.this.yBodyRot = EntityFlam.this.getYRot();
                    } else {
                        double d4 = EntityFlam.this.getTarget().getX() - EntityFlam.this.getX();
                        double d5 = EntityFlam.this.getTarget().getZ() - EntityFlam.this.getZ();
                        EntityFlam.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityFlam.this.yBodyRot = EntityFlam.this.getYRot();
                    }
                }
            }
        }
    }
}

