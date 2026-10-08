package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class EntityOrbBoom
extends Entity {
    public float prevRenderYawOffset;
    public float renderYawOffset;
    public float prevRotationYawHead;
    public float rotationYawHead;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    public int hurtTime;
    public int deathTime;
    protected int lastActiveTime;
    protected int timeSinceIgnited;
    protected int timerDDD;
    EntityPMalleable father;
    private double str;
    public double offsetOrb;
    private double poosX;
    private double poosY;
    private double poosZ;
    private static final EntityDataAccessor<Integer> SELFE = SynchedEntityData.defineId(EntityOrbBoom.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(EntityOrbBoom.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WAITSTART = SynchedEntityData.defineId(EntityOrbBoom.class, EntityDataSerializers.INT);
    public float alpha;

    public EntityOrbBoom(EntityType<? extends EntityOrbBoom> type, Level worldIn) {
        super(type, worldIn);
        this.noCulling = true;
        this.lastActiveTime = 0;
        this.timeSinceIgnited = 0;
        this.setFuseState(7);
        this.setStartState(40);
        this.setNoGravity(false);
        this.alpha = 1.0f;
        this.str = 0.2;
    }

    public EntityOrbBoom(EntityType<? extends EntityOrbBoom> type, Level worldIn, EntityPMalleable in, int fuse, int waitStart) {
        this(type, worldIn);
        if (in != null) {
            this.father = in;
            this.prevRenderYawOffset = in.yBodyRotO;
            this.renderYawOffset = in.yBodyRot;
            this.prevRotationYawHead = in.yHeadRotO;
            this.rotationYawHead = in.yHeadRot;
            this.prevLimbSwingAmount = in.walkAnimation.speed(0.0f);
            this.limbSwingAmount = in.walkAnimation.speed();
            this.limbSwing = in.walkAnimation.position();
        }
        this.setFuseState(fuse);
        this.setStartState(waitStart);
    }

    public EntityOrbBoom(EntityType<? extends EntityOrbBoom> type, Level worldIn, EntityPMalleable in, int fuse, int waitStart, boolean stayPY) {
        this(type, worldIn, in, fuse, waitStart);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SELFE, -1);
        builder.define(FUSE, -1);
        builder.define(WAITSTART, -1);
    }

    public void tick() {
        super.tick();
        if (this.tickCount > this.getStartState()) {
            this.orbDoing();
            this.setSelfeState(1);
            this.dyingBurst(true, 1);
            if (this.level().isClientSide) {
                if (this.father != null) {
                    this.prevRenderYawOffset = this.father.yBodyRotO;
                    this.renderYawOffset = this.father.yBodyRot;
                    this.prevRotationYawHead = this.father.yHeadRotO;
                    this.rotationYawHead = this.father.yHeadRot;
                }
                this.spawnOrbEffects(4);
                return;
            }
            Mot.setPosX(this, this.poosX);
            Mot.setPosY(this, this.poosY - this.getRandom().nextDouble() * 0.1);
            Mot.setPosZ(this, this.poosZ);
        } else {
            if (this.level().isClientSide) {
                this.spawnOrbEffects(4);
                return;
            }
            this.poosX = this.getX();
            this.poosY = this.getY();
            this.poosZ = this.getZ();
        }
    }

    public int getStartState() {
        return (Integer)this.entityData.get(WAITSTART);
    }

    public void setStartState(int state) {
        this.entityData.set(WAITSTART, state);
    }

    public int getFuseState() {
        return (Integer)this.entityData.get(FUSE);
    }

    public void setFuseState(int state) {
        this.entityData.set(FUSE, state);
    }

    public int getSelfeState() {
        return (Integer)this.entityData.get(SELFE);
    }

    public void setSelfeState(int state) {
        this.entityData.set(SELFE, state);
    }

    protected void dyingBurst(boolean fromDeath, int value) {
        int i = this.getSelfeState();
        this.timeSinceIgnited += i * value;
        if (this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
        }
        if (this.timeSinceIgnited >= this.getFuseState()) {
            this.timeSinceIgnited = this.getFuseState();
            this.selfExplode();
        } else {
            this.setSize(this.getBbWidth() + 1.0f, this.getBbHeight() + 0.4f);
        }
    }

    protected void selfExplode() {
        this.setSelfeState(2);
        if (this.getSelfeState() == 2) {
            ++this.timerDDD;
            if (this.timerDDD > 1) {
                this.alpha = Math.max(this.alpha - 0.2f, 0.0f);
                if (!this.level().isClientSide) {
                    if (this.father != null) {
                        float f = this.getBbWidth() / 2.0f;
                        float f1 = this.getBbHeight();
                        AABB axisalignedbb = new AABB(this.getX() - (double)f, this.getY() - (double)f1, this.getZ() - (double)f, this.getX() + (double)f, this.getY() + (double)f1, this.getZ() + (double)f);
                        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                        for (LivingEntity mob : moblist) {
                            if (mob instanceof EntityParasiteBase) continue;
                            this.father.attackEntityAsMobMinimum(mob, this.father.getMiniDamage() * 5.0f);
                        }
                    }
                } else {
                    int par = this.getFuseState();
                    par += par / 2;
                    double offsetX = (this.level().random.nextDouble() - 0.5) * (double)this.getBbWidth();
                    double offsetY = (this.level().random.nextDouble() - 0.5) * (double)this.getBbHeight();
                    double offsetZ = (this.level().random.nextDouble() - 0.5) * (double)this.getBbWidth();
                    double particleX = this.getX() + offsetX;
                    double particleY = this.getY() + offsetY;
                    double particleZ = this.getZ() + offsetZ;
                    double particleVX = -this.getDeltaMovement().x + this.level().random.nextGaussian() * 0.05;
                    double particleVY = -this.getDeltaMovement().y + this.level().random.nextGaussian() * 0.05;
                    double particleVZ = -this.getDeltaMovement().z + this.level().random.nextGaussian() * 0.05;
                    this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, particleX, particleY, particleZ, particleVX, particleVY, particleVZ);
                }
                this.playSound(SRPSounds.ORB_E.get(), 1.0f, 1.0f);
                if (this.timerDDD > 5) {
                    this.discard();
                }
            }
        }
    }

    private void orbDoing() {
        if (this.level().isClientSide) {
            return;
        }
        if (this.tickCount % 10 != 0) {
            return;
        }
        float f = this.getBbWidth() / 2.0f;
        float f1 = this.getBbHeight();
        AABB axisalignedbb = new AABB(this.getX() - (double)f, this.getY() - (double)f1, this.getZ() - (double)f, this.getX() + (double)f, this.getY() + (double)f1, this.getZ() + (double)f);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        if (this.father == null) {
            for (LivingEntity mob : moblist) {
                mob.hurt(this.damageSources().magic(), 10.0f);
            }
            return;
        }
        for (LivingEntity mob : moblist) {
            if (mob instanceof EntityPCosmical) continue;
            this.father.doHurtTarget((Entity)mob);
        }
    }

    public void push(Entity entityIn) {
    }

    public AABB getCollisionBoundingBox() {
        return new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    public void spawnParticles(ParticleOptions particleType) {
        double d0 = this.getRandom().nextGaussian() * 0.02;
        double d1 = this.getRandom().nextGaussian() * 0.02;
        double d2 = this.getRandom().nextGaussian() * 0.02;
        this.level().addParticle(particleType, this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY() + 0.5 + (double)(this.getRandom().nextFloat() * this.getBbHeight()), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), d0, d1, d2);
    }

    public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b) {
        double d0 = this.getRandom().nextGaussian() * 0.02;
        double d1 = this.getRandom().nextGaussian() * 0.02;
        double d2 = this.getRandom().nextGaussian() * 0.02;
        ParticleSpawner.spawnParticle(particleType, this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY() + 0.5 + (double)(this.getRandom().nextFloat() * this.getBbHeight()), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), d0, d1, d2, r, g, b);
    }

    public void spawnOrbEffects(int cap1) {
        for (int i = -cap1; i <= cap1; ++i) {
            for (int j = -cap1; j <= cap1; ++j) {
                if (i <= -2 || i >= 2 || j != -1) continue;
                j = 2;
            }
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 65536.0;
    }

    public float getSelfeFlashIntensity(float p_70831_1_) {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_ * 5.0f) / (float)(this.getFuseState() - 2);
    }

    private net.minecraft.world.entity.EntityDimensions srpSize;

    /** The 1.12 setSize(width, height): the entity dimensions are replaced and the bounding box refreshed. */
    protected void setSize(float width, float height) {
        this.srpSize = net.minecraft.world.entity.EntityDimensions.scalable(width, height);
        this.refreshDimensions();
    }

    @Override
    public net.minecraft.world.entity.EntityDimensions getDimensions(net.minecraft.world.entity.Pose pose) {
        return this.srpSize != null ? this.srpSize : super.getDimensions(pose);
    }
}
