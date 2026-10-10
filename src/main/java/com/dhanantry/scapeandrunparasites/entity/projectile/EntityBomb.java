package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class EntityBomb
extends Entity {
    private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(EntityBomb.class, EntityDataSerializers.INT);
    @Nullable
    private EntityParasiteBase tntPlacedBy;
    private int fuse = 80;
    private float str = 4.0f;
    private float damage;
    private boolean grief;
    private int rangeRad;
    private static final EntityDataAccessor<Byte> SKIN = SynchedEntityData.defineId(EntityBomb.class, EntityDataSerializers.BYTE);

    public EntityBomb(EntityType<? extends EntityBomb> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityBomb(EntityType<? extends EntityBomb> type, Level worldIn, EntityParasiteBase igniter, boolean canGrief) {
        this(type, worldIn);
        this.tntPlacedBy = igniter;
        this.setPos(igniter.getX(), igniter.getY() + (double)igniter.getEyeHeight() - (double)0.1f, igniter.getZ());
        this.grief = canGrief;
    }

    public EntityBomb(EntityType<? extends EntityBomb> type, Level worldIn, double x, double y, double z, EntityParasiteBase igniter, float stren) {
        this(type, worldIn);
        this.setPos(x, y, z);
        float f = (float)(Math.random() * (Math.PI * 2));
        Mot.setX(this, -((float)Math.sin(f)) * 0.02f);
        Mot.setY(this, 0.2f);
        Mot.setZ(this, -((float)Math.cos(f)) * 0.02f);
        this.setFuse(80);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.tntPlacedBy = igniter;
        this.str = stren;
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FUSE, 80);
        builder.define(SKIN, (byte) (0));
    }

    public void setInWeb() {
    }

    protected boolean isMovementNoisy() {
        return false;
    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    public void tick() {
        this.xo = this.getX();
        this.yo = this.getY();
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
        --this.fuse;
        if (this.fuse <= 0) {
            this.explode();
        } else {
            this.updateInWaterStateAndDoFluidPushing();
        }
        this.aiStep();
    }

    public void aiStep() {
        this.collideWithNearbyEntities();
    }

    protected void collideWithNearbyEntities() {
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            int i = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
            if (i > 0 && list.size() > i - 1 && this.getRandom().nextInt(4) == 0) {
                int j = 0;
                for (int k = 0; k < list.size(); ++k) {
                    if (list.get(k).isPassenger()) continue;
                    ++j;
                }
                if (j > i - 1) {
                    this.hurt(this.damageSources().cramming(), 6.0f);
                }
            }
            for (int l = 0; l < list.size(); ++l) {
                Entity entity = (Entity)list.get(l);
                this.doPush(entity);
            }
        }
    }

    protected void doPush(Entity entityIn) {
        entityIn.push((Entity)this);
    }

    public void baseTick() {
        super.baseTick();
    }

    private void explode() {
        if (this.str > 0.0f) {
            boolean flag = EventHooks.canEntityGrief((Level)this.level(), (Entity)this) && this.grief;
            ParasiteEventEntity.createExplosion(this.level(), this, this.getX(), this.getY(), this.getZ(), this.str, flag);
        }
        if (this.level().isClientSide) {
            return;
        }
        this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 0.5f, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
        float f = 4.0f;
        if (this.tntPlacedBy != null) {
            AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate((double)this.rangeRad);
            List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity mob : moblist) {
                if (mob instanceof EntityParasiteBase || !mob.hasLineOfSight((Entity)this)) continue;
                mob.hurt(this.damageSources().thrown((Entity)this, (Entity)this.tntPlacedBy), this.damage);
                SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 300, 0);
                if (!this.tntPlacedBy.isAlive()) continue;
                this.tntPlacedBy.attackEntityAsMobMinimum(mob, this.tntPlacedBy.getMiniDamage() * 3.0f);
            }
        }
        if (this.getSkin() == 2) {
            ParasiteEventEntity.spawnFromList(this, SRPConfigMobs.jinjoMobs, null);
        }
        EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY(), this.getZ());
        entityareaeffectcloud.setRadius(this.rangeRad, 0.5f);
        entityareaeffectcloud.setWaitTime(5);
        entityareaeffectcloud.setDuration(60);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 0));
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.VIRA_E, 3600, 0, false, false));
        this.level().addFreshEntity((Entity)entityareaeffectcloud);
        this.discard();
    }

    public void setMotion(double xSpeedIn, double ySpeedIn, double zSpeedIn, double capX, double capY) {
        xSpeedIn = Math.min(xSpeedIn, capX);
        ySpeedIn = Math.min(ySpeedIn, capY);
        zSpeedIn = Math.min(zSpeedIn, capX);
        Mot.setX(this, xSpeedIn * (Math.random() * 2.0 - 1.0));
        Mot.setY(this, ySpeedIn);
        Mot.setZ(this, zSpeedIn * (Math.random() * 2.0 - 1.0));
    }

    public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
        float f = -Mth.sin((float)(rotationYawIn * ((float)Math.PI / 180))) * Mth.cos((float)(rotationPitchIn * ((float)Math.PI / 180)));
        float f1 = -Mth.sin((float)((rotationPitchIn + pitchOffset) * ((float)Math.PI / 180)));
        float f2 = Mth.cos((float)(rotationYawIn * ((float)Math.PI / 180))) * Mth.cos((float)(rotationPitchIn * ((float)Math.PI / 180)));
        this.shootTwo(f, f1, f2, velocity, inaccuracy);
        Mot.addX(this, entityThrower.getDeltaMovement().x);
        Mot.addZ(this, entityThrower.getDeltaMovement().z);
        if (!entityThrower.onGround()) {
            Mot.addY(this, entityThrower.getDeltaMovement().y);
        }
    }

    public void shootTwo(double x, double y, double z, float velocity, float inaccuracy) {
        float f = (float)Math.sqrt((double)(x * x + y * y + z * z));
        x /= (double)f;
        y /= (double)f;
        z /= (double)f;
        x += this.getRandom().nextGaussian() * (double)0.0075f * (double)inaccuracy;
        y += this.getRandom().nextGaussian() * (double)0.0075f * (double)inaccuracy;
        z += this.getRandom().nextGaussian() * (double)0.0075f * (double)inaccuracy;
        Mot.setX(this, x *= (double)velocity);
        Mot.setY(this, y *= (double)velocity);
        Mot.setZ(this, z *= (double)velocity);
        float f1 = (float)Math.sqrt((double)(x * x + z * z));
        this.setYRot((float)(Mth.atan2((double)x, (double)z) * 57.29577951308232));
        this.setXRot((float)(Mth.atan2((double)y, (double)f1) * 57.29577951308232));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putShort("Fuse", (short)this.getFuse());
        compound.putInt("parasitetype", (int)this.getSkin());
        compound.putFloat("stren", this.str);
        compound.putBoolean("cangrief", this.grief);
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setFuse(compound.getShort("Fuse"));
        if (compound.contains("parasitetype", 99)) {
            this.setSkin(compound.getInt("parasitetype"));
        }
        if (compound.contains("stren", 99)) {
            this.str = compound.getFloat("stren");
        }
        if (compound.contains("cangrief", 99)) {
            this.grief = compound.getBoolean("cangrief");
        }
    }

    @Nullable
    public LivingEntity getTntPlacedBy() {
        return this.tntPlacedBy;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.0f;
    }

    public void setFuse(int fuseIn) {
        this.entityData.set(FUSE, fuseIn);
        this.fuse = fuseIn;
    }

    public void setStren(float in) {
        this.str = in;
    }

    public void updateSTR() {
        this.level().broadcastEntityEvent((Entity)this, (byte)this.str);
    }

    public void notifyDataManagerChange(EntityDataAccessor<?> key) {
        if (FUSE.equals(key)) {
            this.fuse = this.getFuseDataManager();
        }
    }

    public int getFuseDataManager() {
        return (Integer)this.entityData.get(FUSE);
    }

    public int getFuse() {
        return this.fuse;
    }

    public byte getSkin() {
        return (Byte)this.entityData.get(SKIN);
    }

    public void setSkin(int texture) {
        this.entityData.set(SKIN, (byte) (((byte)texture)));
    }

    public void setDamage(float in, int radius) {
        this.damage = in;
        this.rangeRad = radius;
    }

    public void handleEntityEvent(byte id) {
        this.str = id;
        if (id >= 2) {
            this.grief = true;
        }
    }
}

