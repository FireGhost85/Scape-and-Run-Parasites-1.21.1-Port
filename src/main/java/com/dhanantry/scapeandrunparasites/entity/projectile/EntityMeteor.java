package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityOrbBoom;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.network.QlipShakePayload;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteMeteorCrash;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityMeteor
extends Entity {
    private int ticksAlive;
    private int ticksInAir;
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;
    public float prevRenderYawOffset;
    public float renderYawOffset;
    public float prevRotationYawHead;
    public float rotationYawHead;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    public int hurtTime;
    public int deathTime;
    public static final EntityDataAccessor<Boolean> FATHER = SynchedEntityData.defineId(EntityMeteor.class, EntityDataSerializers.BOOLEAN);

    public EntityMeteor(EntityType<? extends EntityMeteor> type, Level worldIn) {
        super(type, worldIn);
        this.noCulling = true;
        this.setRoot(true);
    }

    public EntityMeteor(EntityType<? extends EntityMeteor> type, Level worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        this(type, worldIn);
        this.moveTo(x, y, z, this.getYRot(), this.getXRot());
        this.setPos(x, y, z);
        double d0 = (float)Math.sqrt((double)(accelX * accelX + accelY * accelY + accelZ * accelZ));
        this.accelerationX = accelX / d0 * 0.1;
        this.accelerationY = accelY / d0 * 0.1;
        this.accelerationZ = accelZ / d0 * 0.1;
    }

    public void setDead() {
        super.discard();
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FATHER, false);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 65536.0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.getRoot() && this.tickCount % 20 == 0) {
            List<? extends Player> players = this.level().players();
            for (Player mob : players) {
                float str = EntityMeteor.getDistancePack(this.blockPosition(), mob.blockPosition(), 150);
                if (!(str > 0.0f)) continue;
                com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayer((ServerPlayer)mob, new QlipShakePayload(20, 0, false, true, str * 2.0f));
            }
            if (this.getRandom().nextInt(2) == 0) {
                double spread = 0.9;
                double vx = this.getDeltaMovement().x + (this.level().random.nextDouble() - 0.5) * spread;
                double vy = this.getDeltaMovement().y + (this.level().random.nextDouble() - 0.5) * spread;
                double vz = this.getDeltaMovement().z + (this.level().random.nextDouble() - 0.5) * spread;
                EntityMeteor fragment = new EntityMeteor(SRPEntities.METEOR.get(), this.level(), this.getX(), this.getY(), this.getZ(), vx, vy, vz);
                fragment.setRoot(false);
                this.level().addFreshEntity((Entity)fragment);
            }
        }
        ++this.ticksInAir;
        HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, e -> e != this && !e.isSpectator() && e.isAlive() && e.isPickable(), ClipContext.Block.COLLIDER);
        if (raytraceresult.getType() != HitResult.Type.MISS) {
            this.onHit();
        }
        Mot.setPosX(this, this.getX() + (this.getDeltaMovement().x));
        Mot.setPosY(this, this.getY() + (this.getDeltaMovement().y));
        Mot.setPosZ(this, this.getZ() + (this.getDeltaMovement().z));
        ProjectileUtil.rotateTowardsMovement((Entity)this, (float)0.2f);
        float f = this.getMotionFactor();
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f1 = 0.25f;
                this.level().addParticle(ParticleTypes.BUBBLE, this.getX() - this.getDeltaMovement().x * 0.25, this.getY() - this.getDeltaMovement().y * 0.25, this.getZ() - this.getDeltaMovement().z * 0.25, this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
            }
            f = 0.8f;
        }
        Mot.addX(this, this.accelerationX);
        Mot.addY(this, this.accelerationY);
        Mot.addZ(this, this.accelerationZ);
        Mot.mulX(this, (double)f);
        Mot.mulY(this, (double)f);
        Mot.mulZ(this, (double)f);
        this.level().addParticle(this.getTrailParticle(), this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
        this.setPos(this.getX(), this.getY(), this.getZ());
        if (this.ticksInAir > 1200 || this.getY() <= 0.0) {
            this.onHit();
        }
        if (this.level().isClientSide) {
            for (int i = 0; i < 5; ++i) {
                double offsetX = (this.level().random.nextDouble() - 0.5) * (double)this.getBbWidth();
                double offsetY = (this.level().random.nextDouble() - 0.5) * (double)this.getBbHeight() * 4.0;
                double offsetZ = (this.level().random.nextDouble() - 0.5) * (double)this.getBbWidth();
                double particleX = this.getX() + offsetX;
                double particleY = this.getY() + offsetY + (double)this.getBbHeight() * 0.5 * 2.0;
                double particleZ = this.getZ() + offsetZ;
                double particleVX = -this.getDeltaMovement().x + this.level().random.nextGaussian() * 0.05;
                double particleVY = -this.getDeltaMovement().y + this.level().random.nextGaussian() * 0.05;
                double particleVZ = -this.getDeltaMovement().z + this.level().random.nextGaussian() * 0.05;
                this.level().addParticle(ParticleTypes.FLAME, particleX, particleY, particleZ, particleVX, particleVY, particleVZ);
                if (!this.getRoot()) {
                    this.level().addParticle(ParticleTypes.EXPLOSION, particleX, particleY, particleZ, particleVX, particleVY, particleVZ);
                    continue;
                }
                this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, particleX, particleY, particleZ, particleVX, particleVY, particleVZ);
            }
        }
    }


    protected void onHit() {
        if (!this.level().isClientSide) {
            if (this.getRoot()) {
                List<Entity> mobs = new ArrayList<>(SRPEntityUtil.allEntities(this.level()));
                if (mobs.size() != 0) {
                    for (Entity mob : mobs) {
                        float str;
                        if (!(mob instanceof LivingEntity)) continue;
                        if (mob instanceof Player && (str = EntityMeteor.getDistancePack(this.blockPosition(), mob.blockPosition(), 400)) > 0.0f) {
                            com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayer((ServerPlayer)mob, new QlipShakePayload(150, 0, false, true, str * 8.0f));
                        }
                        if ((str = EntityMeteor.getDistancePack(this.blockPosition(), mob.blockPosition(), SRPConfigWorld.meteorDamage)) > 0.0f) {
                            mob.hurt(this.damageSources().fallingBlock(this), str * 450.0f);
                        }
                        if (!((double)(str = EntityMeteor.getDistancePack(this.blockPosition(), mob.blockPosition(), 800)) > 0.5)) continue;
                        ((LivingEntity)mob).addEffect(new MobEffectInstance(SRPPotions.COTH_E, 1200, 0, false, false));
                    }
                }
            }
            int rad = 40;
            if (!this.getRoot()) {
                rad = 8;
            }
            EntityOrbBoom orb = new EntityOrbBoom(SRPEntities.ORBBOOM.get(), this.level(), null, rad, 1);
            orb.copyPosition(this);
            this.level().addFreshEntity((Entity)orb);
            WorldGenParasiteMeteorCrash ccc = new WorldGenParasiteMeteorCrash(false, this.getRoot() ? 5 : 1);
            ccc.generate(this.level(), RandomSource.create(), this.blockPosition());
            if (this.getRoot() && SRPConfigWorld.originActivated) {
                ParasiteEventWorld.placeOriginInWorld(this.level(), BlockPos.containing(this.getX(), this.getY(), this.getZ()), SRPConfigWorld.originHealth, SRPConfigWorld.originRadius);
            }
            this.discard();
        }
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    protected float getMotionFactor() {
        return 0.95f;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        compound.put("direction", (Tag)this.newDoubleList(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z));
        compound.put("power", (Tag)this.newDoubleList(this.accelerationX, this.accelerationY, this.accelerationZ));
        compound.putInt("life", this.ticksAlive);
        compound.putBoolean("bigmet", this.getRoot());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        ListTag nbttaglist;
        if (compound.contains("bigmet")) {
            this.setRoot(compound.getBoolean("bigmet"));
        }
        if (compound.contains("power", 9) && (nbttaglist = compound.getList("power", 6)).size() == 3) {
            this.accelerationX = nbttaglist.getDouble(0);
            this.accelerationY = nbttaglist.getDouble(1);
            this.accelerationZ = nbttaglist.getDouble(2);
        }
        this.ticksAlive = compound.getInt("life");
        if (compound.contains("direction", 9) && compound.getList("direction", 6).size() == 3) {
            ListTag nbttaglist1 = compound.getList("direction", 6);
            Mot.setX(this, nbttaglist1.getDouble(0));
            Mot.setY(this, nbttaglist1.getDouble(1));
            Mot.setZ(this, nbttaglist1.getDouble(2));
        }
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public float getPickRadius() {
        return 1.0f;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.markHurt();
        return true;
    }

    public float getBrightness() {
        return 1.0f;
    }

    public void lookAt(Entity in) {
        this.lookAt(in.getX(), in.getY(), in.getZ());
    }

    public void lookAt(double x, double y, double z) {
        double dx = x - this.getX();
        double dy = y - (this.getY() + (double)this.getEyeHeight());
        double dz = z - this.getZ();
        double yaw = Math.atan2(dz, dx) * 57.29577951308232 - 90.0;
        double distance = Math.sqrt(dx * dx + dz * dz);
        double pitch = -Math.atan2(dy, distance) * 57.29577951308232;
        this.setYRot((float)yaw);
        this.setXRot((float)pitch);
    }

    public int getBrightnessForRender() {
        return 0xF000F0;
    }

    public void setRoot(boolean in) {
        this.entityData.set(FATHER, in);
    }

    public boolean getRoot() {
        return (Boolean)this.entityData.get(FATHER);
    }

    public static float getDistancePack(BlockPos pos1, BlockPos pos2, int maxDistance) {
        if (maxDistance <= 0) {
            return 0.0f;
        }
        double maxDistSq = maxDistance * maxDistance;
        double distSq = pos1.distSqr((Vec3i)pos2);
        double value = 1.0 - distSq / maxDistSq;
        return (float)Math.max(0.0, Math.min(1.0, value));
    }
}

