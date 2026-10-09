package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityAta;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileNade;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.ParticlePayload;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityIki
extends EntityPPrimitive
implements EntityCanShoot,
EntityCanFly {
    private EntityHitbox back;
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityIki.class, EntityDataSerializers.BYTE);
    private int count;

    public EntityIki(EntityType<? extends EntityIki> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.setNoGravity(true);
        this.goalSelector.removeGoal(this.folow);
        this.borderOrb = -1;
        this.noCulling = true;
        if (SRPConfigMobs.emanaMaxY != 256) {
            this.goalSelector.addGoal(3, new EntityAIFlightLimits(this, SRPConfigMobs.emanaMaxY, true));
        }
        this.back = new EntityHitbox((Mob)this, -1.6f, 1.2f, -0.2f, 1.6f, 1.6f, 0.5f);
        this.hitboxes = new EntityHitbox[]{this.back};
    }

    @Override
    public int getParasiteIDRegister() {
        return 92;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.IKI_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.IKI_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.IKI_DEATH.get();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, SRPConfig.primitiveFollow));
        this.goalSelector.addGoal(4, new AIChargeAttack());
        this.goalSelector.addGoal(5, new AIBomb(this));
        this.goalSelector.addGoal(7, new AIMoveRandom());
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPrimitive.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.IKI_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.IKI_A_ARMOR);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.IKI_A_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.IKI_A_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.primitiveFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        block4: {
            block3: {
                super.aiStep();
                if (this.level().isClientSide) break block3;
                if (this.srpTicks != 10) break block4;
                if (this.onGround() && !this.level().isClientSide) {
                    this.moveControl.setWantedPosition(this.getX(), this.getY() + 5.0, this.getZ(), 0.5);
                }
                if (this.level().getBlockState(this.blockPosition().below(1)).getBlock() == Blocks.AIR && this.level().getBlockState(this.blockPosition().below(2)).getBlock() == Blocks.AIR || this.getTarget() == null) break block4;
                Mot.setY(this, 0.5);
                break block4;
            }
            if (this.getRandom().nextInt(25) == 0) {
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
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.7f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityLesh(SRPEntities.MOVINGFLESH.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
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

    public Fireball getProj(double accelX, double accelY, double accelZ) {
        float pit = 1.0f;
        if (this.count == 4) {
            pit = 2.0f;
        }
        this.playSound(SRPSounds.EMANA_SHOOTING.get(), 2.0f, pit);
        if (this.count >= 4) {
            this.count = 0;
            return new EntityProjectileNade(SRPEntities.NADEBALL.get(), this.level(), (LivingEntity)this, accelX, accelY, accelZ, 3, 60);
        }
        EntityProjectileSpineball ball = new EntityProjectileSpineball(SRPEntities.SPINEBALL.get(), this.level(), (LivingEntity)this, accelX, accelY, accelZ, SRPAttributes.EMANA_RANGED_DAMAGE);
        ball.setDurationAmplifier(SRPConfigMobs.emanaPoisonDuration, SRPConfigMobs.emanaPoisonAmplifier);
        ball.setGearDamage(SRPConfigMobs.emanaGearD);
        return ball;
    }

    @Override
    public void playProjSound() {
        ++this.count;
        if (this.count == 4) {
            this.level().broadcastEntityEvent((Entity)this, (byte)100);
            float v = (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.4f + 2.0f;
            this.playSound(this.getHurtSound(this.damageSources().generic()), 4.0f, v);
            return;
        }
        this.playSound(SRPSounds.EMANA_SHOOTINGPOST.get(), 2.0f, 1.0f);
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    class AIBomb
    extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

        private final EntityParasiteBase parent;
        private int ccc;

        public AIBomb(EntityParasiteBase parentIn) {
            this.parent = parentIn;
            this.ccc = 0;
        }

        public boolean canUse() {
            ++this.ccc;
            if (this.ccc >= 20) {
                this.ccc = 0;
                if (this.parent.getTarget() != null) {
                    LivingEntity target = this.parent.getTarget();
                    if (!target.onGround()) {
                        this.ccc = 10;
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
            List serverList = SRPEntityUtil.allEntities(this.parent.level());
            int count = 0;
            for (int x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityAta)) continue;
                ++count;
            }
            if (count < SRPConfig.worldGnatCap) {
                EntityAta out = new EntityAta(SRPEntities.GNAT.get(), this.parent.level());
                if (this.parent.getTarget() != null) {
                    out.copyPosition((Entity)this.parent);
                    this.parent.level().addFreshEntity((Entity)out);
                    com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new ParticlePayload(this.parent.getX(), this.parent.getY(), this.parent.getZ(), 0.5f, 0.5f, 10));
                }
            } else {
                EntityBomb bomb = new EntityBomb(SRPEntities.BOMB.get(), this.parent.level(), this.parent, false);
                if (this.parent.getTarget() != null) {
                    if (this.parent.getTarget().getY() > this.parent.getY()) {
                        return;
                    }
                    bomb.copyPosition((Entity)this.parent);
                    bomb.setFuse(60);
                    bomb.setStren(0.0f);
                    bomb.setSkin(1);
                    bomb.setDamage((float)this.parent.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue(), 2);
                    bomb.setXRot(bomb.getXRot() - (-20.0f));
                    this.parent.level().addFreshEntity((Entity)bomb);
                    bomb.updateSTR();
                    com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new ParticlePayload(this.parent.getX(), this.parent.getY(), this.parent.getZ(), 0.5f, 0.5f, 10));
                }
            }
        }
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
            if (EntityIki.this.getTarget() != null && EntityIki.this.getRandom().nextInt(5) == 0) {
                return EntityIki.this.distanceToSqr((Entity)EntityIki.this.getTarget()) > 4.0;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return EntityIki.this.getMoveControl().hasWanted() && EntityIki.this.isCharging() && EntityIki.this.getTarget() != null && EntityIki.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity entitylivingbase = EntityIki.this.getTarget();
            Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
            EntityIki.this.moveControl.setWantedPosition(vec3d.x, entitylivingbase.getY() + 7.0, vec3d.z, 0.2);
            EntityIki.this.setCharging(true);
        }

        public void stop() {
            EntityIki.this.setCharging(false);
        }

        public void tick() {
            LivingEntity entitylivingbase = EntityIki.this.getTarget();
            if (entitylivingbase != null && entitylivingbase.isAlive()) {
                if (EntityIki.this.getBoundingBox().intersects(entitylivingbase.getBoundingBox())) {
                    EntityIki.this.doHurtTarget((Entity)entitylivingbase);
                    EntityIki.this.setCharging(false);
                } else {
                    double d0 = EntityIki.this.distanceToSqr((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                        EntityIki.this.moveControl.setWantedPosition(vec3d.x, entitylivingbase.getY() + 7.0, vec3d.z, 1.0);
                    } else {
                        Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                        EntityIki.this.moveControl.setWantedPosition(vec3d.x, entitylivingbase.getY() + 7.0, vec3d.z, 1.1);
                    }
                }
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
            return !EntityIki.this.getMoveControl().hasWanted() && EntityIki.this.getRandom().nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityIki.this.blockPosition();
            int flag = 1;
            double speed = 0.5;
            if (EntityIki.this.getTarget() != null) {
                if (EntityIki.this.distanceToSqr((Entity)EntityIki.this.getTarget()) > 100.0) {
                    blockpos = EntityIki.this.getTarget().blockPosition();
                    flag = 2;
                    speed += 0.25;
                } else if (EntityIki.this.distanceToSqr((Entity)EntityIki.this.getTarget()) < 36.0) {
                    blockpos = EntityIki.this.getTarget().blockPosition();
                    flag = 3;
                    speed += 0.25;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityIki.this.getRandom().nextInt(15) - 7, EntityIki.this.getRandom().nextInt(11) - 5, EntityIki.this.getRandom().nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.offset(EntityIki.this.getRandom().nextInt(6) - 2, EntityIki.this.getRandom().nextInt(7) - 2, EntityIki.this.getRandom().nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.offset(EntityIki.this.getRandom().nextInt(4) + 3, EntityIki.this.getRandom().nextInt(5) + 4, EntityIki.this.getRandom().nextInt(4) + 3);
                }
                if (!EntityIki.this.level().isEmptyBlock(blockpos1)) continue;
                EntityIki.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityIki.this.getTarget() != null) break;
                EntityIki.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityIki vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityIki.this.getX();
                double d1 = this.getWantedY() - EntityIki.this.getY();
                double d2 = this.getWantedZ() - EntityIki.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityIki.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityIki.this, 0.5);
                    Mot.mulY(EntityIki.this, 0.5);
                    Mot.mulZ(EntityIki.this, 0.5);
                } else {
                    Mot.addX(EntityIki.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityIki.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityIki.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityIki.this.getTarget() == null) {
                        EntityIki.this.setYRot(-((float)Mth.atan2((double)EntityIki.this.getDeltaMovement().x, (double)EntityIki.this.getDeltaMovement().z)) * 57.295776f);
        EntityIki.this.yBodyRot = EntityIki.this.getYRot();
                    } else {
                        double d4 = EntityIki.this.getTarget().getX() - EntityIki.this.getX();
                        double d5 = EntityIki.this.getTarget().getZ() - EntityIki.this.getZ();
                        EntityIki.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityIki.this.yBodyRot = EntityIki.this.getYRot();
                    }
                }
            }
        }
    }
}

