package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

public class EntityProjectileHomming
extends Mob {
    private float damage;
    private LivingEntity owner;
    private Entity target;
    @Nullable
    private UUID ownerUniqueId;
    private BlockPos ownerBlockPos;
    @Nullable
    private UUID targetUniqueId;
    private BlockPos targetBlockPos;

    public EntityProjectileHomming(EntityType<? extends EntityProjectileHomming> type, Level worldIn) {
        super(type, worldIn);
        this.noPhysics = true;
        this.moveControl = new AIMoveControl(this);
        this.setNoGravity(true);
    }

    public SoundSource getSoundCategory() {
        return SoundSource.HOSTILE;
    }

    public EntityProjectileHomming(EntityType<? extends EntityProjectileHomming> type, Level worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn) {
        this(type, worldIn);
        this.moveTo(x, y, z, this.getYRot(), this.getXRot());
        Mot.setX(this, motionXIn);
        Mot.setY(this, motionYIn);
        Mot.setZ(this, motionZIn);
    }

    public EntityProjectileHomming(EntityType<? extends EntityProjectileHomming> type, Level worldIn, LivingEntity ownerIn, Entity targetIn, float damage) {
        this(type, worldIn);
        this.owner = ownerIn;
        BlockPos blockpos = ownerIn.blockPosition();
        double d0 = (double)blockpos.getX() + 0.5;
        double d1 = (double)blockpos.getY() + 0.5;
        double d2 = (double)blockpos.getZ() + 0.5;
        this.moveTo(d0, d1, d2, this.getYRot(), this.getXRot());
        this.target = targetIn;
        this.damage = damage;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.owner != null) {
            BlockPos blockpos = this.owner.blockPosition();
            CompoundTag nbttagcompound = new CompoundTag();
            nbttagcompound.putUUID("Id", this.owner.getUUID());
            nbttagcompound.putInt("X", blockpos.getX());
            nbttagcompound.putInt("Y", blockpos.getY());
            nbttagcompound.putInt("Z", blockpos.getZ());
            compound.put("Owner", (Tag)nbttagcompound);
        }
        if (this.target != null) {
            BlockPos blockpos1 = this.target.blockPosition();
            CompoundTag nbttagcompound1 = new CompoundTag();
            nbttagcompound1.putUUID("Id", this.target.getUUID());
            nbttagcompound1.putInt("X", blockpos1.getX());
            nbttagcompound1.putInt("Y", blockpos1.getY());
            nbttagcompound1.putInt("Z", blockpos1.getZ());
            compound.put("Target", (Tag)nbttagcompound1);
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Owner", 10)) {
            CompoundTag nbttagcompound = compound.getCompound("Owner");
            this.ownerUniqueId = nbttagcompound.getUUID("Id");
            this.ownerBlockPos = BlockPos.containing(nbttagcompound.getInt("X"), nbttagcompound.getInt("Y"), nbttagcompound.getInt("Z"));
        }
        if (compound.contains("Target", 10)) {
            CompoundTag nbttagcompound1 = compound.getCompound("Target");
            this.targetUniqueId = nbttagcompound1.getUUID("Id");
            this.targetBlockPos = BlockPos.containing(nbttagcompound1.getInt("X"), nbttagcompound1.getInt("Y"), nbttagcompound1.getInt("Z"));
        }
    }

    public void tick() {
        if (!this.level().isClientSide && this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        } else {
            super.tick();
            if (!this.level().isClientSide) {
                if (this.target == null && this.targetUniqueId != null) {
                    this.discard();
                }
                if (this.owner == null && this.ownerUniqueId != null) {
                    this.discard();
                }
                if (this.target != null && !this.target.isRemoved()) {
                    this.moveControl.setWantedPosition(this.target.getX(), this.target.getY() - (double)this.target.getBbHeight() * 1.5, this.target.getZ(), 1.5);
                }
                AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(2.0);
                List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                boolean flag = false;
                for (LivingEntity mob : moblist) {
                    if (mob == null || mob instanceof EntityParasiteBase || mob instanceof EntityProjectileHomming) continue;
                    DamageSource damagesource = this.owner == null ? this.damageSources().thrown((Entity)this, (Entity)this) : this.damageSources().thrown((Entity)this, (Entity)this.owner);
                    if (flag) continue;
                    flag = mob.hurt(damagesource, this.damage);
                }
                if (flag) {
                    this.discard();
                }
                if (this.tickCount > 200) {
                    this.discard();
                }
            }
        }
    }

    public boolean isBurning() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 16384.0;
    }

    protected void bulletHit(HitResult result) {
        if (SRPEntityUtil.hitEntity(result) == null) {
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 7, 0.2, 0.2, 0.2, 0.0);
        } else {
            DamageSource damagesource;
            boolean flag;
            if (SRPEntityUtil.hitEntity(result) instanceof EntityParasiteBase || !(flag = SRPEntityUtil.hitEntity(result).hurt(damagesource = this.owner == null ? this.damageSources().thrown((Entity)this, (Entity)this) : this.damageSources().thrown((Entity)this, (Entity)this.owner), this.damage)) || SRPEntityUtil.hitEntity(result) instanceof LivingEntity) {
                // empty if block
            }
            this.discard();
        }
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0f, 1.0f);
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
            this.discard();
        }
        return true;
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityProjectileHomming vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityProjectileHomming.this.getX();
                double d1 = this.getWantedY() - EntityProjectileHomming.this.getY();
                double d2 = this.getWantedZ() - EntityProjectileHomming.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityProjectileHomming.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityProjectileHomming.this, 0.5);
                    Mot.mulY(EntityProjectileHomming.this, 0.5);
                    Mot.mulZ(EntityProjectileHomming.this, 0.5);
                } else {
                    Mot.addX(EntityProjectileHomming.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityProjectileHomming.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityProjectileHomming.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityProjectileHomming.this.target == null) {
                        EntityProjectileHomming.this.setYRot(-((float)Mth.atan2((double)EntityProjectileHomming.this.getDeltaMovement().x, (double)EntityProjectileHomming.this.getDeltaMovement().z)) * 57.295776f);
                    } else {
                        double d4 = ((EntityProjectileHomming)EntityProjectileHomming.this).target.getX() - EntityProjectileHomming.this.getX();
                        double d5 = ((EntityProjectileHomming)EntityProjectileHomming.this).target.getZ() - EntityProjectileHomming.this.getZ();
                        EntityProjectileHomming.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
                    }
                }
            }
        }
    }
}

