package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class EntityOrbScary
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
    private boolean followF;
    private double poosX;
    private double poosY;
    private double poosZ;
    private static final EntityDataAccessor<Integer> SELFE = SynchedEntityData.defineId(EntityOrbScary.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(EntityOrbScary.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WAITSTART = SynchedEntityData.defineId(EntityOrbScary.class, EntityDataSerializers.INT);

    public EntityOrbScary(EntityType<? extends EntityOrbScary> type, Level worldIn) {
        super(type, worldIn);
        this.noCulling = true;
        this.lastActiveTime = 0;
        this.timeSinceIgnited = 0;
        this.followF = false;
        this.setFuseState(7);
        this.setStartState(40);
        this.setNoGravity(false);
    }

    public EntityOrbScary(EntityType<? extends EntityOrbScary> type, Level worldIn, EntityPMalleable in, int fuse, int waitStart) {
        this(type, worldIn);
        this.father = in;
        this.followF = true;
        this.prevRenderYawOffset = in.yBodyRotO;
        this.renderYawOffset = in.yBodyRot;
        this.prevRotationYawHead = in.yHeadRotO;
        this.rotationYawHead = in.yHeadRot;
        this.prevLimbSwingAmount = in.walkAnimation.speed(0.0f);
        this.limbSwingAmount = in.walkAnimation.speed();
        this.limbSwing = in.walkAnimation.position();
        this.setFuseState(fuse);
        this.setStartState(waitStart);
    }

    public EntityOrbScary(EntityType<? extends EntityOrbScary> type, Level worldIn, EntityPMalleable in, int fuse, int waitStart, boolean stayPY) {
        this(type, worldIn, in, fuse, waitStart);
        this.followF = stayPY;
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SELFE, -1);
        builder.define(FUSE, -1);
        builder.define(WAITSTART, -1);
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount == 1) {
            this.playSound(SRPSounds.ORB_SUMMON.get(), 1.0f, 1.0f);
            this.playSound(SRPSounds.ORB_IDLE.get(), 1.0f, 1.0f);
        }
        if (this.tickCount > this.getStartState()) {
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
            if (this.father != null) {
                if (this.father.isAlive() && this.followF) {
                    Mot.setPosX(this, this.father.getX());
                    Mot.setPosY(this, this.father.getY() - this.getRandom().nextDouble() * 0.1);
                    Mot.setPosZ(this, this.father.getZ());
                } else {
                    Mot.setPosX(this, this.poosX);
                    Mot.setPosY(this, this.poosY - this.getRandom().nextDouble() * 0.1);
                    Mot.setPosZ(this, this.poosZ);
                    if (this.followF) {
                        this.discard();
                    }
                }
            } else {
                Mot.setPosX(this, this.poosX);
                Mot.setPosY(this, this.poosY - this.getRandom().nextDouble() * 0.1);
                Mot.setPosZ(this, this.poosZ);
            }
        } else {
            if (this.level().isClientSide) {
                this.spawnOrbEffects(4);
                return;
            }
            if (this.father != null) {
                if (this.father.isAlive() && this.followF) {
                    this.poosX = this.father.getX();
                    this.poosY = this.father.getY();
                    this.poosZ = this.father.getZ();
                    Mot.setPosX(this, this.father.getX());
                    Mot.setPosY(this, this.father.getY() - this.getRandom().nextDouble() * 0.1);
                    Mot.setPosZ(this, this.father.getZ());
                } else {
                    if (this.followF) {
                        this.discard();
                    }
                    this.poosX = this.getX();
                    this.poosY = this.getY();
                    this.poosZ = this.getZ();
                }
            } else {
                this.poosX = this.getX();
                this.poosY = this.getY();
                this.poosZ = this.getZ();
            }
        }
        this.orbDoing();
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
            this.setSize(this.getBbWidth() + 0.8f, this.getBbHeight() + 0.482f);
        }
    }

    protected void selfExplode() {
        this.setSelfeState(2);
        if (this.getSelfeState() == 2) {
            ++this.timerDDD;
            if (this.timerDDD > 35) {
                this.setSize(Math.max(0.1f, this.getBbWidth() - 0.8f), Math.max(0.1f, this.getBbHeight() - 0.32f));
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
                    this.spawnParticles(ParticleTypes.EXPLOSION);
                }
                this.playSound(SRPSounds.ORB_E.get(), 1.0f, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2f + 1.0f);
                if (this.timerDDD > 45) {
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
        if (this.father == null) {
            List<? extends Player> moblist = this.level().getEntitiesOfClass(Player.class, axisalignedbb);
            for (Player player : moblist) {
                if (player.getAbilities().instabuild) continue;
                int ccc = 100;
                ItemCooldowns track = player.getCooldowns();
                for (int i = 0; i < player.getInventory().items.size(); ++i) {
                    if (track.getCooldownPercent(((ItemStack)player.getInventory().items.get(i)).getItem(), 1.0f) != 0.0f) continue;
                    track.addCooldown(((ItemStack)player.getInventory().items.get(i)).getItem(), ccc);
                }
                if (track.getCooldownPercent(player.getItemBySlot(EquipmentSlot.OFFHAND).getItem(), 1.0f) != 0.0f) continue;
                track.addCooldown(player.getItemBySlot(EquipmentSlot.OFFHAND).getItem(), ccc);
            }
            return;
        }
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            this.father.scaryOrbEffect(mob, moblist.size());
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
                if (i > -2 && i < 2 && j == -1) {
                    j = 2;
                }
                if (this.getRandom().nextInt(16) != 0) continue;
                for (int k = 0; k <= 5; ++k) {
                    ParticleSpawner.spawnParticle(SRPEnumParticle.EEN, this.getX() + (this.getRandom().nextDouble() - 0.5) * ((double)this.getBbWidth() * 2.0), this.getY() + this.getRandom().nextDouble() * 2.0 * (double)this.getBbHeight(), this.getZ() + (this.getRandom().nextDouble() - 0.5) * ((double)this.getBbWidth() * 2.0), (double)((float)i + this.getRandom().nextFloat()) - 0.5, (float)k - this.getRandom().nextFloat() - 1.0f, (double)((float)j + this.getRandom().nextFloat()) - 0.5, 255, 0, 0);
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
