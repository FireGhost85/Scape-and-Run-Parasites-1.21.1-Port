package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityNade
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
    private EntityParasiteBase father;
    private double poosX;
    private double poosY;
    private double poosZ;
    private static final EntityDataAccessor<Integer> SELFE = SynchedEntityData.defineId(EntityNade.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(EntityNade.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WAITSTART = SynchedEntityData.defineId(EntityNade.class, EntityDataSerializers.INT);

    public EntityNade(EntityType<? extends EntityNade> type, Level worldIn) {
        super(type, worldIn);
        this.noCulling = true;
        this.lastActiveTime = 0;
        this.timeSinceIgnited = 0;
        this.setFuseState(3);
        this.setStartState(10);
    }

    public EntityNade(EntityType<? extends EntityNade> type, Level worldIn, int fuse, int waitStart) {
        this(type, worldIn);
        this.setFuseState(fuse);
        this.setStartState(waitStart);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SELFE, -1);
        builder.define(FUSE, -1);
        builder.define(WAITSTART, -1);
    }

    public void tick() {
        super.tick();
        if (this.tickCount > 3) {
            this.setSelfeState(1);
            this.dyingBurst(true, 1);
            if (this.level().isClientSide) {
                int i;
                for (i = 0; i < 5; ++i) {
                    this.spawnParticles(ParticleTypes.SMOKE);
                }
                for (i = 0; i < 2; ++i) {
                    this.spawnParticles(ParticleTypes.LARGE_SMOKE);
                }
                return;
            }
            Mot.setPosX(this, this.poosX);
            if (this.onGround()) {
                Mot.setPosY(this, this.getY() + (this.getRandom().nextDouble() * 0.01));
            }
            Mot.setPosZ(this, this.poosZ);
        } else {
            if (this.level().isClientSide) {
                int i;
                for (i = 0; i < 5; ++i) {
                    this.spawnParticles(ParticleTypes.SMOKE);
                }
                for (i = 0; i < 2; ++i) {
                    this.spawnParticles(ParticleTypes.LARGE_SMOKE);
                }
                return;
            }
            this.poosX = this.getX();
            this.poosZ = this.getZ();
            if (this.tickCount == 2) {
                this.playSound(SRPSounds.NADE_S.get(), 1.0f, 1.0f);
            }
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
        if (i <= 0 || this.timeSinceIgnited == 0) {
            // empty if block
        }
        this.timeSinceIgnited += i * value;
        if (this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
        }
        if (this.timeSinceIgnited >= this.getFuseState()) {
            this.timeSinceIgnited = this.getFuseState();
            this.selfExplode();
            this.xo = this.getX();
            if (this.onGround()) {
                this.yo = this.getY();
            }
            this.zo = this.getZ();
            if (!this.isNoGravity()) {
                Mot.addY(this, -((double)0.04f));
            }
            this.move(MoverType.SELF, new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z));
            Mot.mulX(this, (double)0.98f);
            Mot.mulY(this, (double)0.98f);
            Mot.mulZ(this, (double)0.98f);
            if (this.onGround()) {
                Mot.mulX(this, (double)0.7f);
                Mot.mulZ(this, (double)0.7f);
                Mot.mulY(this, -0.5);
            }
        } else {
            this.setSize(this.getBbWidth() + 0.8f, this.getBbHeight() + 0.32f);
        }
    }

    protected void selfExplode() {
        this.setSelfeState(2);
        if (this.getSelfeState() == 2) {
            ++this.timerDDD;
            if (!this.level().isClientSide) {
                if (this.father != null && this.father.isAlive()) {
                    float f = this.getBbWidth() / 2.0f;
                    float f1 = this.getBbHeight();
                    AABB axisalignedbb = new AABB(this.getX() - (double)f, this.getY(), this.getZ() - (double)f, this.getX() + (double)f, this.getY() + (double)f1, this.getZ() + (double)f);
                    List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                    for (LivingEntity mob : moblist) {
                        if (mob instanceof EntityParasiteBase) continue;
                        mob.hurt(this.damageSources().generic(), (float)this.father.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                        this.father.attackEntityAsMobMinimum(mob, this.father.getMiniDamage());
                    }
                }
            } else {
                int par = this.getFuseState();
                int n = par + par / 2;
            }
            if (this.timerDDD > this.getStartState()) {
                this.discard();
            }
        }
    }

    public void push(Entity entityIn) {
        if (entityIn instanceof Player && ((Player)entityIn).getAbilities().instabuild) {
            return;
        }
    }

    public AABB getCollisionBoundingBox() {
        return new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public void setFatherS(EntityParasiteBase in) {
        this.father = in;
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
                if (i > -2 && i < 2 && j == -1) {
                    j = 2;
                }
                if (this.getRandom().nextInt(16) != 0) continue;
                for (int k = 0; k <= 5; ++k) {
                    ParticleSpawner.spawnParticle(SRPEnumParticle.EEN, this.getX() + (this.getRandom().nextDouble() - 0.5) * ((double)this.getBbWidth() * 2.0), this.getY() + this.getRandom().nextDouble() * 2.0 * (double)this.getBbHeight(), this.getZ() + (this.getRandom().nextDouble() - 0.5) * ((double)this.getBbWidth() * 2.0), (double)((float)i + this.getRandom().nextFloat()) - 0.5, (float)k - this.getRandom().nextFloat() - 1.0f, (double)((float)j + this.getRandom().nextFloat()) - 0.5, 0, 0, 0);
                }
            }
        }
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
