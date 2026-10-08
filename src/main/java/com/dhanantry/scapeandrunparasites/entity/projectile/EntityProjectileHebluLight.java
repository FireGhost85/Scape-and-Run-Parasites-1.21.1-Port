package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.init.SRPDamageTypes;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EntityProjectileHebluLight
extends Entity {
    private LivingEntity owner;
    private LivingEntity target;
    private static long lastSpeedSoundTick = -100L;
    private static long lastImpactSoundTick = -100L;
    public static boolean DEBUG_IGNORE_CREATIVE_AND_SPECTATOR_PLAYERS = true;
    private static final int PARRY_IMMUNITY_TICKS = 25;
    private static final Map<UUID, Long> PLAYER_PARRY_IMMUNITY = new HashMap<UUID, Long>();
    private static final EntityDataAccessor<Boolean> SYNC_PARRIED = SynchedEntityData.defineId(EntityProjectileHebluLight.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> SYNC_DANGER = SynchedEntityData.defineId(EntityProjectileHebluLight.class, EntityDataSerializers.FLOAT);
    private int age;
    private int parryAge;
    private boolean parried;
    private double arcX;
    private double arcY;
    private double arcZ;
    private double stopHomingDistance;

    public EntityProjectileHebluLight(EntityType<? extends EntityProjectileHebluLight> type, Level worldIn) {
        super(type, worldIn);
        this.noPhysics = false;
        this.noCulling = true;
    }

    public EntityProjectileHebluLight(EntityType<? extends EntityProjectileHebluLight> type, Level worldIn, LivingEntity ownerIn, LivingEntity targetIn, double x, double y, double z) {
        this(type, worldIn, ownerIn, targetIn, x, y, z, false);
    }

    public EntityProjectileHebluLight(EntityType<? extends EntityProjectileHebluLight> type, Level worldIn, LivingEntity ownerIn, LivingEntity targetIn, double x, double y, double z, boolean forceTarget) {
        super(type, worldIn);
        Vec3 toTarget;
        this.owner = ownerIn;
        this.target = targetIn;
        if (!forceTarget && this.target instanceof Player && EntityProjectileHebluLight.shouldIgnorePlayer((Player)this.target)) {
            this.target = null;
        }
        if (this.target == null) {
            this.target = this.findFallbackPlayerTarget(x, y, z);
        }
        this.setPos(x, y, z);
        if (this.target != null) {
            toTarget = new Vec3(this.target.getX() - x, this.target.getY() + (double)this.target.getEyeHeight() * 0.5 - y, this.target.getZ() - z);
            if (toTarget.length() < 1.0E-4) {
                toTarget = new Vec3(0.0, 0.0, 1.0);
            }
            toTarget = toTarget.normalize();
        } else {
            toTarget = new Vec3(this.getRandom().nextDouble() - 0.5, this.getRandom().nextDouble() - 0.2, this.getRandom().nextDouble() - 0.5).normalize();
        }
        Vec3 side = toTarget.cross(new Vec3(0.0, 1.0, 0.0));
        if (side.length() < 1.0E-4) {
            side = new Vec3(1.0, 0.0, 0.0);
        }
        side = side.normalize();
        Vec3 up = side.cross(toTarget).normalize();
        double angle = this.getRandom().nextDouble() * Math.PI * 2.0;
        double sideAmount = Math.cos(angle);
        double upAmount = Math.sin(angle);
        Vec3 outward = side.scale(sideAmount).add(up.scale(upAmount)).add(toTarget.scale(-0.25));
        if (outward.length() < 1.0E-4) {
            outward = side;
        }
        outward = outward.normalize();
        this.arcX = outward.x;
        this.arcY = outward.y;
        this.arcZ = outward.z;
        double startSpeed = 1.65 + this.getRandom().nextDouble() * 1.15;
        Mot.setX(this, outward.x * startSpeed);
        Mot.setY(this, outward.y * startSpeed);
        Mot.setZ(this, outward.z * startSpeed);
        this.stopHomingDistance = 5.0 + this.getRandom().nextDouble() * 5.0;
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SYNC_PARRIED, false);
        builder.define(SYNC_DANGER, (float) (Float.valueOf(0.0f)));
    }

    public void tick() {
        super.tick();
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if (!this.level().isClientSide && (this.owner == null || this.owner.isRemoved())) {
            this.discard();
            return;
        }
        ++this.age;
        this.updateSyncedRenderState();
        if (this.age > 95) {
            this.pop();
            return;
        }
        if (this.parried) {
            this.updateParriedMotion();
        } else {
            this.updateLightMotion();
        }
        Vec3 from = new Vec3(this.getX(), this.getY(), this.getZ());
        Vec3 to = new Vec3(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z);
        HitResult blockHit = SRPEntityUtil.rayTraceBlocks(this.level(), from, to);
        if (blockHit != null) {
            if (!this.level().isClientSide) {
                this.pop();
            }
            return;
        }
        this.move(MoverType.SELF, new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z));
        if (!this.level().isClientSide) {
            this.checkEntityImpact();
        }
        this.spawnClientParticles();
    }

    private void updateLightMotion() {
        if (this.age < 5) {
            Mot.mulX(this, 0.995);
            Mot.mulY(this, 0.995);
            Mot.mulZ(this, 0.995);
            return;
        }
        if (this.age < 10) {
            Mot.mulX(this, 0.88);
            Mot.mulY(this, 0.88);
            Mot.mulZ(this, 0.88);
            return;
        }
        if (this.age == 10 && !this.level().isClientSide) {
            this.level().broadcastEntityEvent((Entity)this, (byte)4);
        }
        if (this.target == null || this.target.isRemoved()) {
            Mot.mulX(this, 0.99);
            Mot.mulY(this, 0.99);
            Mot.mulZ(this, 0.99);
            return;
        }
        Vec3 targetPos = new Vec3(this.target.getX(), this.target.getBoundingBox().minY + (double)this.target.getBbHeight() * 0.55, this.target.getZ());
        Vec3 toTarget = targetPos.subtract(this.getX(), this.getY(), this.getZ());
        double dist = toTarget.length();
        if (dist <= this.stopHomingDistance) {
            this.target = null;
            Mot.mulX(this, 1.12);
            Mot.mulY(this, 1.12);
            Mot.mulZ(this, 1.12);
            this.limitSpeed(2.65);
            return;
        }
        int homingEnd = 54;
        if (this.age >= homingEnd) {
            this.target = null;
            Mot.mulX(this, 1.04);
            Mot.mulY(this, 1.04);
            Mot.mulZ(this, 1.04);
            this.limitSpeed(2.45);
            return;
        }
        if (dist < 1.0E-4) {
            return;
        }
        double progress = Mth.clamp((double)((double)(this.age - 10) / 44.0), (double)0.0, (double)1.0);
        Vec3 desired = toTarget.normalize();
        double arcStrength = (1.0 - progress) * (1.0 - progress) * 1.05;
        desired = desired.add(this.arcX * arcStrength, this.arcY * arcStrength, this.arcZ * arcStrength).normalize();
        double targetSpeed = 0.95 + progress * 2.15;
        double turnStrength = 0.25 + progress * 0.17;
        Mot.addX(this, (desired.x * targetSpeed - this.getDeltaMovement().x) * turnStrength);
        Mot.addY(this, (desired.y * targetSpeed - this.getDeltaMovement().y) * turnStrength);
        Mot.addZ(this, (desired.z * targetSpeed - this.getDeltaMovement().z) * turnStrength);
        this.limitSpeed(targetSpeed + 0.55);
    }

    private void updateParriedMotion() {
        ++this.parryAge;
        if (this.target == null || this.target.isRemoved()) {
            Mot.mulX(this, 0.985);
            Mot.mulY(this, 0.985);
            Mot.mulZ(this, 0.985);
            if (this.parryAge > 35) {
                this.pop();
            }
            return;
        }
        if (this.parryAge < 13) {
            Mot.mulX(this, 0.975);
            Mot.mulY(this, 0.975);
            Mot.mulZ(this, 0.975);
            return;
        }
        Vec3 targetPos = new Vec3(this.target.getX(), this.target.getBoundingBox().minY + (double)this.target.getBbHeight() * 0.55, this.target.getZ());
        Vec3 toTarget = targetPos.subtract(this.getX(), this.getY(), this.getZ());
        if (toTarget.length() < 1.0E-4) {
            return;
        }
        Vec3 desired = toTarget.normalize();
        double targetSpeed = 2.25;
        double turnStrength = 0.28;
        Mot.addX(this, (desired.x * targetSpeed - this.getDeltaMovement().x) * turnStrength);
        Mot.addY(this, (desired.y * targetSpeed - this.getDeltaMovement().y) * turnStrength);
        Mot.addZ(this, (desired.z * targetSpeed - this.getDeltaMovement().z) * turnStrength);
        this.limitSpeed(2.65);
    }

    private void limitSpeed(double max) {
        double speed = Math.sqrt(this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().y * this.getDeltaMovement().y + this.getDeltaMovement().z * this.getDeltaMovement().z);
        if (speed > max && speed > 1.0E-4) {
            double scale = max / speed;
            Mot.mulX(this, scale);
            Mot.mulY(this, scale);
            Mot.mulZ(this, scale);
        }
    }

    private static void giveParryImmunity(LivingEntity entity, Level world) {
        if (!(entity instanceof Player)) {
            return;
        }
        PLAYER_PARRY_IMMUNITY.put(entity.getUUID(), world.getGameTime() + 25L);
    }

    private static boolean hasParryImmunity(LivingEntity entity, Level world) {
        if (!(entity instanceof Player)) {
            return false;
        }
        Long endTime = PLAYER_PARRY_IMMUNITY.get(entity.getUUID());
        if (endTime == null) {
            return false;
        }
        if (world.getGameTime() > endTime) {
            PLAYER_PARRY_IMMUNITY.remove(entity.getUUID());
            return false;
        }
        return true;
    }

    private void checkEntityImpact() {
        AABB box = this.getBoundingBox().inflate(0.45);
        List<? extends Entity> list = this.level().getEntities((Entity)this, box);
        for (Entity entity : list) {
            if (entity == this.owner && !this.parried || entity instanceof EntityProjectileHebluLight || !entity.canBeCollidedWith()) continue;
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity)entity;
                if (!this.parried && living == this.owner) continue;
                if (!this.parried && living instanceof Player) {
                    Player player = (Player)living;
                    if (EntityProjectileHebluLight.shouldIgnorePlayer(player) || EntityProjectileHebluLight.hasParryImmunity(living, this.level())) continue;
                    living.hurt(SRPDamageTypes.source(this.level(), SRPDamageTypes.HEBLU_LIGHT, this, this.owner), 10.0f);
                    living.addEffect(new MobEffectInstance(SRPPotions.DISTORTED_ENLIGHTENMENT_E, 400, 0, false, true));
                    this.pop();
                    return;
                }
                if (this.parried) {
                    if (living != this.target) continue;
                    living.hurt(SRPDamageTypes.source(this.level(), SRPDamageTypes.HEBLU_LIGHT_NEUTRAL, null, null), 24.0f);
                    this.pop();
                    return;
                }
                living.hurt(SRPDamageTypes.source(this.level(), SRPDamageTypes.HEBLU_LIGHT_NEUTRAL, null, null), 7.0f);
                this.pop();
                return;
            }
            this.pop();
            return;
        }
    }

    private LivingEntity findParryTarget(Entity attacker) {
        AABB box = this.getBoundingBox().expandTowards(32.0, 18.0, 32.0);
        List<? extends LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, box);
        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (LivingEntity living : list) {
            double dist;
            String className;
            if (living == null || living.isRemoved() || living == this.owner || living == attacker || living instanceof Player || !(className = living.getClass().getName()).startsWith("com.dhanantry.scapeandrunparasites.entity.monster.") || !((dist = living.distanceToSqr((Entity)this)) < bestDist)) continue;
            bestDist = dist;
            best = living;
        }
        return best;
    }

    private LivingEntity getThrower() {
        return this.owner;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide || this.isRemoved()) {
            return true;
        }
        Entity trueSource = source.getEntity();
        if (trueSource instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity)trueSource;
            if (!EntityProjectileHebluLight.isSentientParryWeapon(attacker)) {
                return false;
            }
            this.parryNearbyLights(attacker, this);
            return true;
        }
        this.pop();
        return true;
    }

    private void parryNearbyLights(LivingEntity attacker, EntityProjectileHebluLight mainLight) {
        double radiusXZ = 6.0;
        double radiusY = 4.0;
        AABB box = mainLight.getBoundingBox().expandTowards(radiusXZ, radiusY, radiusXZ);
        List<? extends EntityProjectileHebluLight> nearby = this.level().getEntitiesOfClass(EntityProjectileHebluLight.class, box);
        boolean playedEffects = false;
        for (EntityProjectileHebluLight light : nearby) {
            if (light == null || light.isRemoved() || light.parried) continue;
            light.parryFrom(attacker, !playedEffects);
            playedEffects = true;
        }
        if (!(playedEffects || mainLight == null || mainLight.isRemoved() || mainLight.parried)) {
            mainLight.parryFrom(attacker, true);
        }
    }

    private void parryFrom(LivingEntity attacker, boolean playEffects) {
        Vec3 randomSpread;
        Vec3 look = attacker.getLookAngle();
        if (look == null || look.length() < 1.0E-4) {
            look = new Vec3(this.getX() - attacker.getX(), this.getY() - attacker.getY(), this.getZ() - attacker.getZ()).normalize();
        }
        if ((randomSpread = new Vec3(this.getRandom().nextDouble() - 0.5, this.getRandom().nextDouble() * 0.75 - 0.15, this.getRandom().nextDouble() - 0.5)).length() < 1.0E-4) {
            randomSpread = new Vec3(0.0, 0.3, 1.0);
        }
        randomSpread = randomSpread.normalize();
        Vec3 bounce = look.scale(0.75).add(randomSpread.scale(1.25));
        if (bounce.length() < 1.0E-4) {
            bounce = look;
        }
        bounce = bounce.normalize();
        this.parried = true;
        this.entityData.set(SYNC_PARRIED, true);
        this.entityData.set(SYNC_DANGER, (float) (Float.valueOf(0.0f)));
        this.parryAge = 0;
        this.age = 20;
        this.target = this.findParryTarget((Entity)attacker);
        Mot.setX(this, bounce.x * (1.85 + this.getRandom().nextDouble() * 0.65));
        Mot.setY(this, bounce.y * (1.85 + this.getRandom().nextDouble() * 0.65) + 0.12);
        Mot.setZ(this, bounce.z * (1.85 + this.getRandom().nextDouble() * 0.65));
        EntityProjectileHebluLight.giveParryImmunity(attacker, this.level());
        if (playEffects) {
            this.playSound(SRPSounds.HEBLU_PARRY.get(), 1.2f, 0.95f + this.getRandom().nextFloat() * 0.12f);
            if (this.level() instanceof ServerLevel) {
                ((ServerLevel)this.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, attacker.getX() + look.x * 1.2, attacker.getY() + (double)attacker.getEyeHeight() * 0.65, attacker.getZ() + look.z * 1.2, 1, 0.15, 0.15, 0.15, 0.0);
                ((ServerLevel)this.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, attacker.getX() - look.z * 0.75, attacker.getY() + (double)attacker.getEyeHeight() * 0.7, attacker.getZ() + look.x * 0.75, 1, 0.1, 0.1, 0.1, 0.0);
                for (int i = 0; i < 12; ++i) {
                    ((ServerLevel)this.level()).sendParticles(ParticleTypes.FIREWORK, attacker.getX() + look.x * 1.1, attacker.getY() + (double)attacker.getEyeHeight() * 0.65, attacker.getZ() + look.z * 1.1, 1, 0.45, 0.28, 0.45, 0.09);
                }
            }
        }
    }

    private void pop() {
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent((Entity)this, (byte)3);
            this.discard();
        }
    }

    public void handleEntityEvent(byte id) {
        if (id == 3) {
            long time = this.level().getGameTime();
            if (time - lastImpactSoundTick > 2L) {
                lastImpactSoundTick = time;
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SRPSounds.HEBLU_LIGHT_IMPACT.get(), this.getSoundSource(), 0.48f, 0.92f + this.getRandom().nextFloat() * 0.16f, false);
            }
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY(), this.getZ(), (this.getRandom().nextDouble() - 0.5) * 0.22, (this.getRandom().nextDouble() - 0.5) * 0.22, (this.getRandom().nextDouble() - 0.5) * 0.22);
            }
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_BLAST, this.getSoundSource(), 0.22f, 1.8f + this.getRandom().nextFloat() * 0.25f, false);
        } else if (id == 4) {
            int i;
            for (i = 0; i < 10; ++i) {
                this.level().addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY(), this.getZ(), (this.getRandom().nextDouble() - 0.5) * 0.35, (this.getRandom().nextDouble() - 0.5) * 0.35, (this.getRandom().nextDouble() - 0.5) * 0.35);
            }
            for (i = 0; i < 4; ++i) {
                this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), -this.getDeltaMovement().x * 0.12 + (this.getRandom().nextDouble() - 0.5) * 0.08, -this.getDeltaMovement().y * 0.12 + (this.getRandom().nextDouble() - 0.5) * 0.08, -this.getDeltaMovement().z * 0.12 + (this.getRandom().nextDouble() - 0.5) * 0.08);
            }
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_BLAST, this.getSoundSource(), 0.18f, 2.0f + this.getRandom().nextFloat() * 0.2f, false);
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void spawnClientParticles() {
        if (!this.level().isClientSide) {
            return;
        }
        if (this.tickCount % 3 != 0) {
            return;
        }
        this.level().addParticle(ParticleTypes.END_ROD, this.getX() - this.getDeltaMovement().x * 0.25, this.getY() - this.getDeltaMovement().y * 0.25, this.getZ() - this.getDeltaMovement().z * 0.25, -this.getDeltaMovement().x * 0.015, -this.getDeltaMovement().y * 0.015, -this.getDeltaMovement().z * 0.015);
    }

    public boolean isParriedLight() {
        return (Boolean)this.entityData.get(SYNC_PARRIED);
    }

    public float getPlayerDangerColorAmount() {
        return ((Float)this.entityData.get(SYNC_DANGER)).floatValue();
    }

    private static boolean shouldIgnorePlayer(Player player) {
        if (!DEBUG_IGNORE_CREATIVE_AND_SPECTATOR_PLAYERS) {
            return false;
        }
        return player.isCreative() || player.isSpectator();
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        this.age = compound.getInt("Age");
        this.parryAge = compound.getInt("ParryAge");
        this.parried = compound.getBoolean("Parried");
        this.arcX = compound.getDouble("ArcX");
        this.arcY = compound.getDouble("ArcY");
        this.arcZ = compound.getDouble("ArcZ");
        this.stopHomingDistance = compound.getDouble("StopHomingDistance");
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Age", this.age);
        compound.putInt("ParryAge", this.parryAge);
        compound.putBoolean("Parried", this.parried);
        compound.putDouble("ArcX", this.arcX);
        compound.putDouble("ArcY", this.arcY);
        compound.putDouble("ArcZ", this.arcZ);
        compound.putDouble("StopHomingDistance", this.stopHomingDistance);
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean canAttackWithItem() {
        return true;
    }

    public float getPickRadius() {
        return 1.15f;
    }

    private static boolean isSentientParryWeapon(LivingEntity attacker) {
        if (!(attacker instanceof Player)) {
            return false;
        }
        Player player = (Player)attacker;
        return EntityProjectileHebluLight.isSentientParryWeaponStack(player.getMainHandItem()) || EntityProjectileHebluLight.isSentientParryWeaponStack(player.getOffhandItem());
    }

    private static boolean isSentientParryWeaponStack(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getItem() == null) {
            return false;
        }
        ResourceLocation id = stack.getItem().builtInRegistryHolder().key().location();
        if (id == null || !"srparasites".equals(id.getNamespace())) {
            return false;
        }
        String path = id.getPath();
        return "weapon_scythe_sentient".equals(path) || "weapon_axe_sentient".equals(path) || "weapon_sword_sentient".equals(path) || "weapon_cleaver_sentient".equals(path) || "weapon_maul_sentient".equals(path) || "weapon_lance_sentient".equals(path);
    }

    private void updateSyncedRenderState() {
        if (this.level().isClientSide) {
            return;
        }
        this.entityData.set(SYNC_PARRIED, this.parried);
        if (this.parried || this.target == null || this.target.isRemoved() || !(this.target instanceof Player)) {
            this.entityData.set(SYNC_DANGER, (float) (Float.valueOf(0.0f)));
            return;
        }
        double dist = this.distanceTo((Entity)this.target);
        float danger = dist >= 18.0 ? 0.0f : (dist <= 5.0 ? 1.0f : (float)((18.0 - dist) / 13.0));
        this.entityData.set(SYNC_DANGER, (float) (Float.valueOf(danger)));
    }

    private LivingEntity findFallbackPlayerTarget(double x, double y, double z) {
        Player best = null;
        double bestDist = Double.MAX_VALUE;
        for (Player player : this.level().players()) {
            double dist;
            if (player == null || player.isRemoved() || player.isSpectator() || EntityProjectileHebluLight.shouldIgnorePlayer(player) || !((dist = player.distanceToSqr(x, y, z)) < bestDist)) continue;
            bestDist = dist;
            best = player;
        }
        return best;
    }
}

