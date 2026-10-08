package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityJinjo
extends EntityPPreeminent
implements EntityCanFly {
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityJinjo.class, EntityDataSerializers.BYTE);

    public EntityJinjo(EntityType<? extends EntityJinjo> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.setNoGravity(true);
        if (SRPConfigMobs.ombooMaxY != 256) {
            this.goalSelector.addGoal(3, new EntityAIFlightLimits(this, SRPConfigMobs.ombooMaxY, true));
        }
        this.canTeleportToo = true;
        this.adaptationCap = 0.95f;
        this.noCulling = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 65;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, SRPConfig.pureFollow));
        this.goalSelector.addGoal(4, new AIChargeAttack());
        this.goalSelector.addGoal(6, new AIMoveRandom());
        this.goalSelector.addGoal(5, new AIBomb(this));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(4, new EntityAIFlightLimits(this, 10, false));
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.JINJO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.JINJO_HURT.get();
    }

    protected float getSoundVolume() {
        return 6.0f;
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.JINJO_DEATH.get();
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPreeminent.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.JINJO_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.JINJO_ARMOR);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.JINJO_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.JINJO_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.preeminentFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        block7: {
            block6: {
                super.aiStep();
                if (this.level().isClientSide) break block6;
                if (this.onGround()) {
                    this.moveControl.setWantedPosition(this.getX(), this.getY() + 5.0, this.getZ(), 0.5);
                }
                if (this.srpTicks == 10 && (this.level().getBlockState(this.blockPosition().below(1)).getBlock() != Blocks.AIR || this.level().getBlockState(this.blockPosition().below(2)).getBlock() != Blocks.AIR) && this.getTarget() != null) {
                    Mot.setY(this, 0.5);
                }
                if (this.srpTicks != 10 || this.getRandom().nextInt(7) != 0) break block7;
                if (!SRPConfigWorld.coloniesActivated) {
                    return;
                }
                AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(16.0);
                List moblist = this.level().getEntitiesOfClass(EntityKol.class, axisalignedbb);
                if (moblist.size() > 3) {
                    return;
                }
                SRPWorldData data = SRPWorldData.get(this.level());
                BlockPos origin = data.nearestColonyPosition(this.blockPosition(), false);
                if (origin == null) break block7;
                EntityKol aaa = new EntityKol(SRPEntities.WORKER.get(), this.level());
                aaa.copyPosition((Entity)this);
                aaa.setTask(origin, data.getColonyDistanceSpreadByPosition(origin, false));
                this.level().addFreshEntity((Entity)aaa);
                break block7;
            }
            if (this.getRandom().nextInt(30) == 0) {
                for (int i = 0; i <= 20; ++i) {
                    if (i % 5 != 0) continue;
                    this.spawnParticlesGoreMouth(SRPEnumParticle.GSPLASH, 0, -1, -1, 0.2, 0.0);
                }
            }
        }
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.jinjoOrbEffects, mobs);
        }
        return flag;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 2.4f;
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

    class AIBomb
    extends Goal {
        private final EntityParasiteBase parent;
        private int ccc;

        public AIBomb(EntityParasiteBase parentIn) {
            this.parent = parentIn;
            this.ccc = 0;
        }

        public boolean canUse() {
            ++this.ccc;
            if (this.ccc >= 100) {
                this.ccc = 0;
                if (this.parent.getTarget() != null) {
                    LivingEntity target = this.parent.getTarget();
                    if (!target.onGround()) {
                        this.ccc = 70;
                        return false;
                    }
                    if (target.distanceToSqr(this.parent.getX(), target.getY(), this.parent.getZ()) < 256.0) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void tick() {
            EntityBomb out = new EntityBomb(SRPEntities.BOMB.get(), this.parent.level(), this.parent, SRPConfigMobs.jinjoGriefing);
            if (this.parent.getTarget() != null) {
                boolean flag = this.parent.hasLineOfSight((Entity)this.parent.getTarget()) || this.parent.level().random.nextInt(3) == 0;
                out.copyPosition((Entity)this.parent);
                out.setFuse(80);
                out.setStren(flag ? 4.0f : 8.0f);
                out.setSkin(flag ? 2 : 3);
                out.setDamage((float)this.parent.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * SRPConfigMobs.jinjoExplotionMult, 7);
                this.parent.level().addFreshEntity((Entity)out);
                out.updateSTR();
            }
        }
    }

    class AIChargeAttack
    extends Goal {
        public AIChargeAttack() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (EntityJinjo.this.getTarget() != null && EntityJinjo.this.getRandom().nextInt(5) == 0) {
                return EntityJinjo.this.distanceToSqr((Entity)EntityJinjo.this.getTarget()) > 4.0;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return EntityJinjo.this.getMoveControl().hasWanted() && EntityJinjo.this.isCharging() && EntityJinjo.this.getTarget() != null && EntityJinjo.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity entitylivingbase = EntityJinjo.this.getTarget();
            Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
            EntityJinjo.this.moveControl.setWantedPosition(vec3d.x, entitylivingbase.getY() + 20.0, vec3d.z, 0.2);
            EntityJinjo.this.setCharging(true);
        }

        public void stop() {
            EntityJinjo.this.setCharging(false);
        }

        public void tick() {
            LivingEntity entitylivingbase = EntityJinjo.this.getTarget();
            if (entitylivingbase != null && entitylivingbase.isAlive()) {
                if (EntityJinjo.this.getBoundingBox().intersects(entitylivingbase.getBoundingBox())) {
                    EntityJinjo.this.doHurtTarget((Entity)entitylivingbase);
                    EntityJinjo.this.setCharging(false);
                } else {
                    double d0 = EntityJinjo.this.distanceToSqr((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                        EntityJinjo.this.moveControl.setWantedPosition(vec3d.x, entitylivingbase.getY() + 20.0, vec3d.z, 1.0);
                    } else {
                        Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                        EntityJinjo.this.moveControl.setWantedPosition(vec3d.x, entitylivingbase.getY() + 20.0, vec3d.z, 1.1);
                    }
                }
            }
        }
    }

    class AIMoveRandom
    extends Goal {
        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !EntityJinjo.this.getMoveControl().hasWanted() && EntityJinjo.this.getRandom().nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityJinjo.this.blockPosition();
            int flag = 1;
            double speed = 0.1;
            if (EntityJinjo.this.getTarget() != null) {
                if (EntityJinjo.this.distanceToSqr((Entity)EntityJinjo.this.getTarget()) > 100.0) {
                    blockpos = EntityJinjo.this.getTarget().blockPosition();
                    flag = 2;
                    speed += 0.05;
                } else if (EntityJinjo.this.distanceToSqr((Entity)EntityJinjo.this.getTarget()) < 36.0) {
                    blockpos = EntityJinjo.this.getTarget().blockPosition();
                    flag = 3;
                    speed += 0.05;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityJinjo.this.getRandom().nextInt(15) - 7, EntityJinjo.this.getRandom().nextInt(11) - 5, EntityJinjo.this.getRandom().nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.offset(EntityJinjo.this.getRandom().nextInt(6) - 2, EntityJinjo.this.getRandom().nextInt(7) - 2, EntityJinjo.this.getRandom().nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.offset(EntityJinjo.this.getRandom().nextInt(4) + 3, EntityJinjo.this.getRandom().nextInt(5) + 4, EntityJinjo.this.getRandom().nextInt(4) + 3);
                }
                if (!EntityJinjo.this.level().isEmptyBlock(blockpos1)) continue;
                EntityJinjo.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityJinjo.this.getTarget() != null) break;
                EntityJinjo.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityJinjo vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityJinjo.this.getX();
                double d1 = this.getWantedY() - EntityJinjo.this.getY();
                double d2 = this.getWantedZ() - EntityJinjo.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityJinjo.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityJinjo.this, 0.5);
                    Mot.mulY(EntityJinjo.this, 0.5);
                    Mot.mulZ(EntityJinjo.this, 0.5);
                } else {
                    Mot.addX(EntityJinjo.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityJinjo.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityJinjo.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityJinjo.this.getTarget() == null) {
                        EntityJinjo.this.setYRot(-((float)Mth.atan2((double)EntityJinjo.this.getDeltaMovement().x, (double)EntityJinjo.this.getDeltaMovement().z)) * 57.295776f);
        EntityJinjo.this.yBodyRot = EntityJinjo.this.getYRot();
                    } else {
                        double d4 = EntityJinjo.this.getTarget().getX() - EntityJinjo.this.getX();
                        double d5 = EntityJinjo.this.getTarget().getZ() - EntityJinjo.this.getZ();
                        EntityJinjo.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityJinjo.this.yBodyRot = EntityJinjo.this.getYRot();
                    }
                }
            }
        }
    }
}

