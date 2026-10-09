package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.init.SRPDamageTypes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityProjectileKirinSlash
extends Entity {
    private LivingEntity owner;
    private static final EntityDataAccessor<Float> SYNC_YAW = SynchedEntityData.defineId(EntityProjectileKirinSlash.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SYNC_PITCH = SynchedEntityData.defineId(EntityProjectileKirinSlash.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SYNC_ROLL = SynchedEntityData.defineId(EntityProjectileKirinSlash.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SYNC_LENGTH = SynchedEntityData.defineId(EntityProjectileKirinSlash.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> SYNC_DELAY = SynchedEntityData.defineId(EntityProjectileKirinSlash.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SYNC_GROW_TICKS = SynchedEntityData.defineId(EntityProjectileKirinSlash.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SYNC_LIFE = SynchedEntityData.defineId(EntityProjectileKirinSlash.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SYNC_FADING = SynchedEntityData.defineId(EntityProjectileKirinSlash.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SYNC_HIT_POP = SynchedEntityData.defineId(EntityProjectileKirinSlash.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SYNC_HIT_POP_AGE = SynchedEntityData.defineId(EntityProjectileKirinSlash.class, EntityDataSerializers.INT);
    private int age;
    private int delayTicks = 0;
    private int growTicks = 5;
    private int life = 60;
    private float damage = 12.0f;
    private float maxLength = 80.0f;
    private int targetEntityId = -1;
    private double dirX;
    private double dirZ;
    private double beforeTarget;
    private double sideOffset;
    private double verticalOffset;
    private boolean startPositionFixed = false;
    private double startX;
    private double startY;
    private double startZ;
    private boolean fading = false;
    private int fadeAge = 0;
    private int fadeTicks = 10;
    private boolean hitPop = false;
    private int hitPopAge = 0;
    private int hitPopTicks = 6;
    private final Set<Integer> hitEntities = new HashSet<Integer>();

    public EntityProjectileKirinSlash(EntityType<? extends EntityProjectileKirinSlash> type, Level worldIn) {
        super(type, worldIn);
        this.noPhysics = true;
        this.noCulling = true;
    }

    public EntityProjectileKirinSlash(EntityType<? extends EntityProjectileKirinSlash> type, Level worldIn, LivingEntity ownerIn, double x, double y, double z, float yaw, float pitch, float roll, float length, float damageIn) {
        this(type, worldIn, ownerIn, x, y, z, yaw, pitch, roll, length, damageIn, 0, 5, 60);
    }

    public EntityProjectileKirinSlash(EntityType<? extends EntityProjectileKirinSlash> type, Level worldIn, LivingEntity ownerIn, double x, double y, double z, float yaw, float pitch, float roll, float length, float damageIn, int delayTicksIn, int growTicksIn, int lifeIn) {
        this(type, worldIn);
        this.owner = ownerIn;
        this.damage = damageIn;
        this.maxLength = length;
        this.delayTicks = Math.max(0, delayTicksIn);
        this.growTicks = Math.max(1, growTicksIn);
        this.life = Math.max(10, lifeIn);
        this.startX = x;
        this.startY = y;
        this.startZ = z;
        this.startPositionFixed = true;
        this.setPos(x, y, z);
        this.entityData.set(SYNC_YAW, (float) (Float.valueOf(yaw)));
        this.entityData.set(SYNC_PITCH, (float) (Float.valueOf(pitch)));
        this.entityData.set(SYNC_ROLL, (float) (Float.valueOf(roll)));
        this.entityData.set(SYNC_LENGTH, (float) (Float.valueOf(length)));
        this.entityData.set(SYNC_DELAY, this.delayTicks);
        this.entityData.set(SYNC_GROW_TICKS, this.growTicks);
        this.entityData.set(SYNC_LIFE, this.life);
        this.entityData.set(SYNC_FADING, false);
        this.entityData.set(SYNC_HIT_POP, false);
        this.entityData.set(SYNC_HIT_POP_AGE, 0);
    }

    public EntityProjectileKirinSlash(EntityType<? extends EntityProjectileKirinSlash> type, Level worldIn, LivingEntity ownerIn, LivingEntity targetIn, double dirXIn, double dirZIn, double beforeTargetIn, double sideOffsetIn, double verticalOffsetIn, float yaw, float pitch, float roll, float length, float damageIn, int delayTicksIn, int growTicksIn, int lifeIn) {
        this(type, worldIn);
        this.owner = ownerIn;
        if (targetIn != null) {
            this.targetEntityId = targetIn.getId();
        }
        this.dirX = dirXIn;
        this.dirZ = dirZIn;
        this.beforeTarget = beforeTargetIn;
        this.sideOffset = sideOffsetIn;
        this.verticalOffset = verticalOffsetIn;
        this.damage = damageIn;
        this.maxLength = length;
        this.delayTicks = Math.max(0, delayTicksIn);
        this.growTicks = Math.max(1, growTicksIn);
        this.life = Math.max(10, lifeIn);
        this.updateStartFromTarget(true);
        this.entityData.set(SYNC_YAW, (float) (Float.valueOf(yaw)));
        this.entityData.set(SYNC_PITCH, (float) (Float.valueOf(pitch)));
        this.entityData.set(SYNC_ROLL, (float) (Float.valueOf(roll)));
        this.entityData.set(SYNC_LENGTH, (float) (Float.valueOf(length)));
        this.entityData.set(SYNC_DELAY, this.delayTicks);
        this.entityData.set(SYNC_GROW_TICKS, this.growTicks);
        this.entityData.set(SYNC_LIFE, this.life);
        this.entityData.set(SYNC_FADING, false);
        this.entityData.set(SYNC_HIT_POP, false);
        this.entityData.set(SYNC_HIT_POP_AGE, 0);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SYNC_YAW, (float) (Float.valueOf(0.0f)));
        builder.define(SYNC_PITCH, (float) (Float.valueOf(0.0f)));
        builder.define(SYNC_ROLL, (float) (Float.valueOf(0.0f)));
        builder.define(SYNC_LENGTH, (float) (Float.valueOf(80.0f)));
        builder.define(SYNC_DELAY, 0);
        builder.define(SYNC_GROW_TICKS, 5);
        builder.define(SYNC_LIFE, 60);
        builder.define(SYNC_FADING, false);
        builder.define(SYNC_HIT_POP, false);
        builder.define(SYNC_HIT_POP_AGE, 0);
    }

    public void tick() {
        super.tick();
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        ++this.age;
        if (this.getVisibleAge() < 0 && this.targetEntityId != -1) {
            this.updateStartFromTarget(false);
        } else if (!this.startPositionFixed) {
            this.startX = this.getX();
            this.startY = this.getY();
            this.startZ = this.getZ();
            this.startPositionFixed = true;
        }
        this.setPos(this.startX, this.startY, this.startZ);
        if (this.hitPop) {
            ++this.hitPopAge;
            if (!this.level().isClientSide) {
                this.entityData.set(SYNC_HIT_POP, true);
                this.entityData.set(SYNC_HIT_POP_AGE, this.hitPopAge);
            }
            if (this.hitPopAge >= this.hitPopTicks) {
                this.discard();
                return;
            }
        }
        if (this.fading) {
            ++this.fadeAge;
            if (!this.level().isClientSide) {
                this.entityData.set(SYNC_FADING, true);
            }
            if (this.fadeAge >= this.fadeTicks) {
                this.discard();
                return;
            }
        }
        if (!this.level().isClientSide) {
            if (this.owner == null || this.owner.isRemoved()) {
                this.discard();
                return;
            }
            if (!this.fading && !this.hitPop && this.getVisibleAge() > this.life) {
                this.discard();
                return;
            }
            if (!this.fading && !this.hitPop && this.getVisibleAge() >= 0) {
                this.checkEntityImpact();
            }
        }
    }

    private void updateStartFromTarget(boolean forcePosition) {
        Entity entity = this.level().getEntity(this.targetEntityId);
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity target = (LivingEntity)entity;
        double cx = target.getX();
        double cy = target.getBoundingBox().minY + (double)target.getBbHeight() * 0.55;
        double cz = target.getZ();
        double sideX = -this.dirZ;
        double sideZ = this.dirX;
        this.startX = cx - this.dirX * this.beforeTarget + sideX * this.sideOffset;
        this.startY = cy + this.verticalOffset;
        this.startZ = cz - this.dirZ * this.beforeTarget + sideZ * this.sideOffset;
        this.startPositionFixed = true;
        if (forcePosition) {
            this.setPos(this.startX, this.startY, this.startZ);
        }
    }

    private int getVisibleAge() {
        return this.age - this.delayTicks;
    }

    public float getGrowth(float partialTicks) {
        float visibleAge = (float)this.tickCount + partialTicks - (float)this.getDelayTicks();
        if (visibleAge <= 0.0f) {
            return 0.0f;
        }
        float raw = Mth.clamp((float)(visibleAge / (float)this.getGrowTicks()), (float)0.0f, (float)1.0f);
        return 1.0f - (1.0f - raw) * (1.0f - raw);
    }

    private float getServerGrowth() {
        int visibleAge = this.getVisibleAge();
        if (visibleAge <= 0) {
            return 0.0f;
        }
        float raw = Mth.clamp((float)((float)visibleAge / (float)this.growTicks), (float)0.0f, (float)1.0f);
        return 1.0f - (1.0f - raw) * (1.0f - raw);
    }

    private void checkEntityImpact() {
        float growth = this.getServerGrowth();
        if (growth <= 0.0f) {
            return;
        }
        float currentLength = Math.max(1.0f, this.maxLength * growth);
        AABB searchBox = this.getBoundingBox().inflate((double)currentLength, Math.max(4.0, (double)currentLength * 0.25), (double)currentLength);
        List<? extends LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, searchBox);
        Vec3 start = new Vec3(this.startX, this.startY, this.startZ);
        Vec3 dir = this.getSlashDirection();
        Vec3 a = start;
        Vec3 b = start.add(dir.scale((double)currentLength));
        for (LivingEntity living : list) {
            AABB hitBox;
            Player player;
            if (living == null || living.isRemoved() || living == this.owner || this.hitEntities.contains(living.getId()) || EntityProjectileKirinSlash.isSRParasitesMob(living) || living instanceof Player && (player = (Player)living).isSpectator() || (hitBox = living.getBoundingBox().inflate(0.35, 0.25, 0.35)).clip(a, b) == null && !hitBox.contains(a) && !hitBox.contains(b)) continue;
            this.onSliceTouched(living);
            return;
        }
    }

    private void onSliceTouched(LivingEntity living) {
        Player player;
        if (living == null || living.isRemoved()) {
            return;
        }
        if (this.hitEntities.contains(living.getId())) {
            return;
        }
        if (living instanceof Player && (player = (Player)living).isSpectator()) {
            return;
        }
        this.hitEntities.add(living.getId());
        this.spawnContactSmoke(living);
        this.level().playSound(null, living.getX(), living.getY() + (double)living.getBbHeight() * 0.5, living.getZ(), SRPSounds.KIRIN_PROJECTILE_IMPACT.get(), SoundSource.HOSTILE, 0.45f, 0.92f + this.getRandom().nextFloat() * 0.16f);
        if (living instanceof Player) {
            player = (Player)living;
            if (!player.isCreative()) {
                this.forceExactHealthDamage((LivingEntity)player, 2.0f);
            }
        } else {
            living.hurt(SRPDamageTypes.source(this.level(), SRPDamageTypes.KIRIN_SLASH, this, this.owner), this.damage);
        }
        this.beginHitPop();
    }

    private void forceExactHealthDamage(LivingEntity living, float amount) {
        if (living == null || living.isRemoved() || amount <= 0.0f) {
            return;
        }
        float newHealth = Math.max(0.0f, living.getHealth() - amount);
        living.setHealth(newHealth);
        if (newHealth <= 0.0f) {
            living.die(SRPDamageTypes.source(this.level(), SRPDamageTypes.KIRIN_SLASH, this, this.owner));
        }
    }

    private void spawnContactSmoke(LivingEntity living) {
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel) || living == null) {
            return;
        }
        ServerLevel worldServer = (ServerLevel)this.level();
        double x = living.getX();
        double y = living.getBoundingBox().minY + 0.05;
        double z = living.getZ();
        worldServer.sendParticles(ParticleTypes.SMOKE, x, y, z, 4, 0.25, 0.05, 0.25, 0.012);
        worldServer.sendParticles(ParticleTypes.CLOUD, x, y, z, 2, 0.18, 0.03, 0.18, 0.006);
    }

    private void beginHitPop() {
        if (this.hitPop) {
            return;
        }
        this.hitPop = true;
        this.hitPopAge = 0;
        this.fading = false;
        if (!this.level().isClientSide) {
            this.entityData.set(SYNC_HIT_POP, true);
            this.entityData.set(SYNC_HIT_POP_AGE, 0);
            this.entityData.set(SYNC_FADING, false);
        }
    }

    private Vec3 getSlashDirection() {
        double z;
        double y;
        float yawRad = this.getSlashYaw() * ((float)Math.PI / 180);
        float pitchRad = this.getSlashPitch() * ((float)Math.PI / 180);
        double x = Mth.sin((float)yawRad) * Mth.cos((float)pitchRad);
        Vec3 dir = new Vec3(x, y = (double)(-Mth.sin((float)pitchRad)), z = (double)(Mth.cos((float)yawRad) * Mth.cos((float)pitchRad)));
        if (dir.length() < 1.0E-4) {
            return new Vec3(0.0, 0.0, 1.0);
        }
        return dir.normalize();
    }

    private static double distancePointToSegmentSq(Vec3 p, Vec3 a, Vec3 b) {
        Vec3 ab = b.subtract(a);
        double abLenSq = ab.dot(ab);
        if (abLenSq <= 1.0E-4) {
            return p.distanceToSqr(a);
        }
        double t = p.subtract(a).dot(ab) / abLenSq;
        t = Mth.clamp((double)t, (double)0.0, (double)1.0);
        Vec3 closest = a.add(ab.scale(t));
        return p.distanceToSqr(closest);
    }

    private static boolean isSRParasitesMob(LivingEntity living) {
        String className = living.getClass().getName();
        return className.startsWith("com.dhanantry.scapeandrunparasites.entity.monster.");
    }

    public float getSlashYaw() {
        return ((Float)this.entityData.get(SYNC_YAW)).floatValue();
    }

    public float getSlashPitch() {
        return ((Float)this.entityData.get(SYNC_PITCH)).floatValue();
    }

    public float getSlashRoll() {
        return ((Float)this.entityData.get(SYNC_ROLL)).floatValue();
    }

    public float getSlashLength() {
        return ((Float)this.entityData.get(SYNC_LENGTH)).floatValue();
    }

    public int getDelayTicks() {
        return (Integer)this.entityData.get(SYNC_DELAY);
    }

    public int getGrowTicks() {
        return Math.max(1, (Integer)this.entityData.get(SYNC_GROW_TICKS));
    }

    public int getLife() {
        return Math.max(1, (Integer)this.entityData.get(SYNC_LIFE));
    }

    public boolean isFading() {
        return (Boolean)this.entityData.get(SYNC_FADING);
    }

    public boolean isHitPopping() {
        return (Boolean)this.entityData.get(SYNC_HIT_POP);
    }

    public int getHitPopAge() {
        return (Integer)this.entityData.get(SYNC_HIT_POP_AGE);
    }

    public int getHitPopTicks() {
        return this.hitPopTicks;
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        this.age = compound.getInt("Age");
        this.life = compound.getInt("Life");
        this.damage = compound.getFloat("Damage");
        this.maxLength = compound.getFloat("SlashLength");
        this.growTicks = compound.getInt("GrowTicks");
        this.delayTicks = compound.getInt("DelayTicks");
        this.startX = compound.getDouble("StartX");
        this.startY = compound.getDouble("StartY");
        this.startZ = compound.getDouble("StartZ");
        this.startPositionFixed = true;
        this.targetEntityId = compound.getInt("TargetEntityId");
        this.dirX = compound.getDouble("DirX");
        this.dirZ = compound.getDouble("DirZ");
        this.beforeTarget = compound.getDouble("BeforeTarget");
        this.sideOffset = compound.getDouble("SideOffset");
        this.verticalOffset = compound.getDouble("VerticalOffset");
        this.fading = compound.getBoolean("Fading");
        this.fadeAge = compound.getInt("FadeAge");
        this.hitPop = compound.getBoolean("HitPop");
        this.hitPopAge = compound.getInt("HitPopAge");
        if (this.life <= 0) {
            this.life = 60;
        }
        if (this.growTicks <= 0) {
            this.growTicks = 5;
        }
        if (this.maxLength <= 0.0f) {
            this.maxLength = 80.0f;
        }
        this.entityData.set(SYNC_YAW, (float) (Float.valueOf(compound.getFloat("SlashYaw"))));
        this.entityData.set(SYNC_PITCH, (float) (Float.valueOf(compound.getFloat("SlashPitch"))));
        this.entityData.set(SYNC_ROLL, (float) (Float.valueOf(compound.getFloat("SlashRoll"))));
        this.entityData.set(SYNC_LENGTH, (float) (Float.valueOf(this.maxLength)));
        this.entityData.set(SYNC_DELAY, this.delayTicks);
        this.entityData.set(SYNC_GROW_TICKS, this.growTicks);
        this.entityData.set(SYNC_LIFE, this.life);
        this.entityData.set(SYNC_FADING, this.fading);
        this.entityData.set(SYNC_HIT_POP, this.hitPop);
        this.entityData.set(SYNC_HIT_POP_AGE, this.hitPopAge);
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Age", this.age);
        compound.putInt("Life", this.life);
        compound.putFloat("Damage", this.damage);
        compound.putFloat("SlashLength", this.maxLength);
        compound.putInt("GrowTicks", this.growTicks);
        compound.putInt("DelayTicks", this.delayTicks);
        compound.putDouble("StartX", this.startX);
        compound.putDouble("StartY", this.startY);
        compound.putDouble("StartZ", this.startZ);
        compound.putInt("TargetEntityId", this.targetEntityId);
        compound.putDouble("DirX", this.dirX);
        compound.putDouble("DirZ", this.dirZ);
        compound.putDouble("BeforeTarget", this.beforeTarget);
        compound.putDouble("SideOffset", this.sideOffset);
        compound.putDouble("VerticalOffset", this.verticalOffset);
        compound.putBoolean("Fading", this.fading);
        compound.putInt("FadeAge", this.fadeAge);
        compound.putBoolean("HitPop", this.hitPop);
        compound.putInt("HitPopAge", this.hitPopAge);
        compound.putFloat("SlashYaw", this.getSlashYaw());
        compound.putFloat("SlashPitch", this.getSlashPitch());
        compound.putFloat("SlashRoll", this.getSlashRoll());
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean canAttackWithItem() {
        return false;
    }
}

