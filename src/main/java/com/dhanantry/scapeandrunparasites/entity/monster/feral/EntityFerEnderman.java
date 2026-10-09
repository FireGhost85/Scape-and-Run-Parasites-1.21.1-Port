package com.dhanantry.scapeandrunparasites.entity.monster.feral;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public class EntityFerEnderman
extends EntityPFeral {
        private static final AttributeModifier ATTACKING_SPEED_BOOST = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "attacking_speed_boost"), (double)0.15f, AttributeModifier.Operation.ADD_VALUE);
    private static final EntityDataAccessor<Boolean> SCREAMING = SynchedEntityData.defineId(EntityFerEnderman.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CRAWLING = SynchedEntityData.defineId(EntityFerEnderman.class, EntityDataSerializers.BOOLEAN);
    private int lastCreepySound;
    private int targetChangeTime;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int ally;
    private EntityParasiteBase toTele;
    private int toTeleCool;
    private int spotCool;
    private String tpPlayerName;

    public EntityFerEnderman(EntityType<? extends EntityFerEnderman> type, Level worldIn) {
        super(type, worldIn);
        this.canModRender = 1;
        this.type = (byte)11;
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.ally = 0;
        this.tpPlayerName = null;
    }

    @Override
    public int getIDSpawn() {
        return 59;
    }

    @Override
    public int getParasiteIDRegister() {
        return 94;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infendermanCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 1, 16));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPFeral.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.FERENDERMAN_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.FERENDERMAN_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.330000011920929);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.FERENDERMAN_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.FERENDERMAN_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, 64.0);
        return builder;
    }

    @Override
    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        boolean flag = this.getTarget() == null;
        super.setTarget(entitylivingbaseIn);
        AttributeInstance iattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (entitylivingbaseIn == null) {
            this.targetChangeTime = 0;
            this.entityData.set(SCREAMING, false);
            iattributeinstance.removeModifier(ATTACKING_SPEED_BOOST.id());
        } else {
            this.targetChangeTime = this.tickCount;
            this.entityData.set(SCREAMING, true);
            if (flag) {
                this.spotCool = SRPConfigMobs.feralendermansaw;
                if (entitylivingbaseIn instanceof Player) {
                    if (this.tpPlayerName != null) {
                        if (!this.tpPlayerName.equals(((Player)entitylivingbaseIn).getName().getString())) {
                            this.tpPlayerName = ((Player)entitylivingbaseIn).getName().getString();
                            this.level().playSound((Player)null, entitylivingbaseIn.xo, entitylivingbaseIn.yo, entitylivingbaseIn.zo, SRPSounds.INFECTEDENDERMAN_PORTAL.get(), this.getSoundSource(), 0.3f, 1.0f);
                        }
                    } else {
                        this.tpPlayerName = ((Player)entitylivingbaseIn).getName().getString();
                        this.level().playSound((Player)null, entitylivingbaseIn.xo, entitylivingbaseIn.yo, entitylivingbaseIn.zo, SRPSounds.INFECTEDENDERMAN_PORTAL.get(), this.getSoundSource(), 0.3f, 1.0f);
                    }
                } else {
                    this.tpPlayerName = null;
                }
            }
            if (!iattributeinstance.hasModifier(ATTACKING_SPEED_BOOST.id())) {
                iattributeinstance.addTransientModifier(ATTACKING_SPEED_BOOST);
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SCREAMING, false);
        builder.define(CRAWLING, false);
    }

    public void playEndermanSound() {
        if (this.tickCount >= this.lastCreepySound + 400) {
            this.lastCreepySound = this.tickCount;
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getY() + (double)this.getEyeHeight(), this.getZ(), SoundEvents.ENDERMAN_STARE, this.getSoundSource(), 2.5f, 1.0f, false);
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(ParticleTypes.PORTAL, this.getX() + (this.getRandom().nextDouble() - 0.5) * (double)this.getBbWidth(), this.getY() + this.getRandom().nextDouble() * (double)this.getBbHeight() - 0.25, this.getZ() + (this.getRandom().nextDouble() - 0.5) * (double)this.getBbWidth(), (this.getRandom().nextDouble() - 0.5) * 2.0, -this.getRandom().nextDouble(), (this.getRandom().nextDouble() - 0.5) * 2.0);
            }
        } else if (this.getTarget() != null && this.tickCount % 20 == 0 && this.distanceToSqr((Entity)this.getTarget()) > Math.pow(SRPAttributes.FERENDERMAN_TP_DIST / 2.0, 2.0) && this.getRandom().nextInt(SRPConfigMobs.feralendermantelefreq) == 0 && !this.teleportAllies()) {
            this.teleportRandomly();
        }
        if (!this.level().isClientSide) {
            if (this.ally > 0) {
                ++this.ally;
                this.teleportAlly();
            }
            if (this.spotCool >= 0) {
                --this.spotCool;
            }
            if (this.toTeleCool >= 0) {
                --this.toTeleCool;
            }
            if (this.srpTicks == 10 && this.hasEffect(SRPPotions.RAGE_E)) {
                this.spotCool = 0;
                this.toTeleCool = 0;
            }
        }
        this.jumping = false;
    }

    protected boolean teleportAllies() {
        if (this.ally > 0 || !SRPConfigMobs.feralendermanteleally || this.toTeleCool > 0 || this.spotCool > 0) {
            return false;
        }
        LivingEntity target = this.getTarget();
        if (target == null) {
            return false;
        }
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(64.0);
        if (this.hasEffect(SRPPotions.RAGE_E)) {
            List<? extends EntityPAdapted> moblist5 = this.level().getEntitiesOfClass(EntityPAdapted.class, axisalignedbb);
            for (EntityPAdapted mob : moblist5) {
                if (mob.getTarget() != null || mob.getHealth() - SRPConfigMobs.feralendermanTeleDamage < 2.0f || !this.teleportToEntity((Entity)mob, 1.0)) continue;
                this.setCoordTarget(target.getX(), target.getY(), target.getZ());
                this.toTele = mob;
                this.setWorkTask(false);
                this.ally = 1;
                return true;
            }
        }
        List<? extends EntityPPrimitive> moblist1 = this.level().getEntitiesOfClass(EntityPPrimitive.class, axisalignedbb);
        for (EntityPPrimitive mob : moblist1) {
            if (mob.getTarget() != null || mob.getHealth() - SRPConfigMobs.feralendermanTeleDamage < 2.0f || !this.teleportToEntity((Entity)mob, 1.0)) continue;
            this.setCoordTarget(target.getX(), target.getY(), target.getZ());
            this.toTele = mob;
            this.setWorkTask(false);
            this.ally = 1;
            return true;
        }
        List<? extends EntityPFeral> moblist7 = this.level().getEntitiesOfClass(EntityPFeral.class, axisalignedbb);
        for (EntityPFeral mob : moblist7) {
            if (mob.getTarget() != null || mob.getHealth() - SRPConfigMobs.feralendermanTeleDamage < 2.0f || !this.teleportToEntity((Entity)mob, 1.0)) continue;
            this.setCoordTarget(target.getX(), target.getY(), target.getZ());
            this.toTele = mob;
            this.setWorkTask(false);
            this.ally = 1;
            return true;
        }
        List<? extends EntityPInfected> moblist2 = this.level().getEntitiesOfClass(EntityPInfected.class, axisalignedbb);
        for (EntityPInfected mob : moblist2) {
            if (mob.getTarget() != null || mob.getParasiteType() > 15 || mob instanceof EntityCanMelt && ((EntityCanMelt)(mob)).isMelting() || mob.getHealth() - SRPConfigMobs.feralendermanTeleDamage < 2.0f || !this.teleportToEntity((Entity)mob, 1.0)) continue;
            this.setCoordTarget(target.getX(), target.getY(), target.getZ());
            this.toTele = mob;
            this.setWorkTask(false);
            this.ally = 1;
            return true;
        }
        List<? extends EntityParasiteBase> moblist3 = this.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist3) {
            if (!(mob instanceof EntityMudo) || mob.getTarget() != null || mob.getHealth() - SRPConfigMobs.feralendermanTeleDamage < 2.0f || !this.teleportToEntity((Entity)mob, 1.0)) continue;
            this.setCoordTarget(target.getX(), target.getY(), target.getZ());
            this.toTele = mob;
            this.setWorkTask(false);
            this.ally = 1;
            return true;
        }
        return false;
    }

    private void teleportAlly() {
        block8: {
            block7: {
                LivingEntity target;
                if (this.ally < 8) {
                    return;
                }
                if (this.toTele == null) break block7;
                if (!this.toTele.isAlive()) {
                    this.toTele = null;
                    this.ally = 0;
                    this.setWorkTask(true);
                    boolean flag2 = false;
                    for (int lag2 = 10; !flag2 && lag2 > 0; --lag2) {
                        flag2 = this.teleportToPos(this.targetX, this.targetY, this.targetZ, 8.0);
                    }
                    return;
                }
                boolean flag1 = false;
                for (int lag1 = 10; !flag1 && lag1 > 0 && !(flag1 = this.teleportToPos(this.targetX, this.targetY, this.targetZ, 8.0)); --lag1) {
                }
                if (!flag1) break block8;
                boolean flag2 = false;
                int lag2 = 12;
                this.toTele.copyPosition((Entity)this);
                if (this.toTele.getParasiteIDRegister() != 59 && this.toTele.getParasiteIDRegister() != 69) {
                    this.toTele.hurt(this.damageSources().fall(), SRPConfigMobs.feralendermanTeleDamage);
                }
                if (this.isOnFire() && this.getRandom().nextInt(4) != 0) {
                    this.toTele.igniteForSeconds(8);
                }
                this.level().playSound((Player)null, this.toTele.xo, this.toTele.yo, this.toTele.zo, SRPSounds.INFECTEDENDERMAN_PORTAL.get(), this.getSoundSource(), 1.0f, 1.0f);
                flag2 = true;
                if (!flag2 || (target = this.getTarget()) == null || !target.isAlive()) break block8;
                this.toTele.setTarget(target);
                break block8;
            }
            boolean flag2 = false;
            for (int lag2 = 10; !flag2 && lag2 > 0; --lag2) {
                flag2 = this.teleportToPos(this.targetX, this.targetY, this.targetZ, 8.0);
            }
        }
        this.setWorkTask(true);
        this.ally = 0;
        this.toTele = null;
        this.toTeleCool = SRPConfigMobs.feralendermanallyCool;
    }

    protected boolean teleportRandomly() {
        if (this.spotCool > 0) {
            return false;
        }
        double d0 = this.getX() + (this.getRandom().nextDouble() - 0.5) * 64.0;
        double d1 = this.getY() + (double)(this.getRandom().nextInt(64) - 32);
        double d2 = this.getZ() + (this.getRandom().nextDouble() - 0.5) * 64.0;
        if (this.getTarget() != null && Math.sqrt(this.getTarget().distanceToSqr(d0, d1, d2)) < Math.pow(SRPAttributes.INFENDERMAN_TP_DIST, 2.0)) {
            return false;
        }
        return this.teleportToPos(d0, d1, d2);
    }

    protected boolean teleportToEntity(Entity in, double dis) {
        double d1 = in.getX() + (this.getRandom().nextDouble() - 0.5) * dis;
        double d2 = in.getY() + (double)(this.getRandom().nextInt(16) - 8) * dis;
        double d3 = in.getZ() + (this.getRandom().nextDouble() - 0.5) * dis;
        return this.teleportToPos(d1, d2, d3);
    }

    protected boolean teleportToPos(double x, double y, double z, double dis) {
        double d1 = x + (this.getRandom().nextDouble() - 0.5) * dis;
        double d2 = y + (double)(this.getRandom().nextInt(16) - 8) * dis;
        double d3 = z + (this.getRandom().nextDouble() - 0.5) * dis;
        if (this.getTarget() != null && Math.sqrt(this.getTarget().distanceToSqr(d1, d2, d3)) < 10.0) {
            return false;
        }
        return this.teleportToPos(d1, d2, d3);
    }

    protected boolean teleportEntityTo(EntityParasiteBase in, double x, double y, double z, double dis) {
        double d1 = x + (this.getRandom().nextDouble() - 0.5) * dis;
        double d2 = y + (double)(this.getRandom().nextInt(16) - 8) * dis;
        double d3 = z + (this.getRandom().nextDouble() - 0.5) * dis;
        return this.teleportTo(in, d1, d2, d3);
    }

    private boolean teleportToPos(double x, double y, double z) {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity((LivingEntity)this, x, y, z);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return false;
        }
        boolean flag = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
        if (flag) {
            this.level().playSound((Player)null, this.xo, this.yo, this.zo, SRPSounds.INFECTEDENDERMAN_PORTAL.get(), this.getSoundSource(), 1.0f, 1.0f);
            this.playSound(SRPSounds.INFECTEDENDERMAN_PORTAL.get(), 1.0f, 1.0f);
        }
        return flag;
    }


    private boolean teleportTo(EntityParasiteBase in, double x, double y, double z) {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity((LivingEntity)this, x, y, z);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return false;
        }
        boolean flag = in.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
        if (flag) {
            in.level().playSound((Player)null, in.xo, in.yo, in.zo, SoundEvents.ENDERMAN_TELEPORT, in.getSoundSource(), 1.0f, 1.0f);
            in.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
        return flag;
    }

    private void setCoordTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        this.spotCool = 0;
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source.getDirectEntity() != null && source.getDirectEntity() != source.getEntity()) { // EntityDamageSourceIndirect: arrows, fireballs, thrown potions
            for (int i = 0; i < 64; ++i) {
                if (!this.teleportRandomly()) continue;
                return true;
            }
            return false;
        }
        boolean flag = super.hurt(source, amount);
        if (source.is(DamageTypeTags.BYPASSES_ARMOR) && this.getRandom().nextInt(SRPConfigMobs.feralendermantelefreq) == 0 && !this.teleportAllies()) {
            this.teleportRandomly();
        }
        return flag;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && this.getRandom().nextInt(SRPConfigMobs.feralendermantelefreq) == 0 && !this.teleportAllies()) {
            this.teleportRandomly();
        }
        return flag;
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.tpPlayerName = null;
    }

    public boolean isScreaming() {
        return (Boolean)this.entityData.get(SCREAMING);
    }

    public boolean isCrawling() {
        return (Boolean)this.entityData.get(CRAWLING);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return this.getBbHeight() * 0.88f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INFECTEDENDERMAN_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDENDERMAN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDENDERMAN_DEATH.get();
    }

    public float getVoicePitch() {
        return (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2f + 0.5f;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        return floo;
    }
}

