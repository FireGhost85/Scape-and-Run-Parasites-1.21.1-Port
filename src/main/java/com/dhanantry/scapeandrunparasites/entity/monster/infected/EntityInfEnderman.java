package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

public class EntityInfEnderman
extends EntityPInfected {
        private static final AttributeModifier ATTACKING_SPEED_BOOST = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "attacking_speed_boost"), (double)0.15f, AttributeModifier.Operation.ADD_VALUE);
    private static final EntityDataAccessor<Boolean> SCREAMING = SynchedEntityData.defineId(EntityInfEnderman.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CRAWLING = SynchedEntityData.defineId(EntityInfEnderman.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT = SynchedEntityData.defineId(EntityInfEnderman.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ARIRAL = SynchedEntityData.defineId(EntityInfEnderman.class, EntityDataSerializers.BOOLEAN);
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
    private EntityHitbox head;
    private int waterHurtCooldown = 0;

    public EntityInfEnderman(EntityType<? extends EntityInfEnderman> type, Level worldIn) {
        super(type, worldIn);
        this.canModRender = 0;
        this.type = (byte)14;
        this.goalSelector.removeGoal(this.folow);
        this.killcount = -10.0;
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.ally = 0;
        this.tpPlayerName = null;
        this.head = new EntityHitbox((Mob)this, 0.0f, 0.0f, 2.3f, 0.6f, 0.6f, 1.25f);
        this.hitboxes = new EntityHitbox[]{this.head};
    }

    @Override
    public int getParasiteIDRegister() {
        return 59;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infendermanCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.2, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPInfected.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.INFENDERMAN_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.INFENDERMAN_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, (double)0.3f);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.INFENDERMAN_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.INFENDERMAN_ATTACK_DAMAGE);
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
                this.spotCool = SRPConfigMobs.infendermansaw;
                if (entitylivingbaseIn instanceof Player) {
                    if (this.tpPlayerName != null) {
                        if (!this.tpPlayerName.equals(entitylivingbaseIn.getName().getString())) {
                            this.tpPlayerName = entitylivingbaseIn.getName().getString();
                            this.level().playSound(null, entitylivingbaseIn.xo, entitylivingbaseIn.yo, entitylivingbaseIn.zo, SRPSounds.INFECTEDENDERMAN_PORTAL.get(), this.getSoundSource(), 0.3f, 1.0f);
                        }
                    } else {
                        this.tpPlayerName = entitylivingbaseIn.getName().getString();
                        this.level().playSound(null, entitylivingbaseIn.xo, entitylivingbaseIn.yo, entitylivingbaseIn.zo, SRPSounds.INFECTEDENDERMAN_PORTAL.get(), this.getSoundSource(), 0.3f, 1.0f);
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
        builder.define(TEXTURE_VARIANT, 0);
        builder.define(SCREAMING, false);
        builder.define(CRAWLING, false);
        builder.define(ARIRAL, false);
    }

    public int getTextureVariant() {
        return (Integer)this.entityData.get(TEXTURE_VARIANT);
    }

    public void setTextureVariant(int variant) {
        this.entityData.set(TEXTURE_VARIANT, variant);
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
        if (this.isCrawling()) {
            this.setSize(0.95f, 1.25f);
        }
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(ParticleTypes.PORTAL, this.getX() + (this.getRandom().nextDouble() - 0.5) * (double)this.getBbWidth(), this.getY() + this.getRandom().nextDouble() * (double)this.getBbHeight() - 0.25, this.getZ() + (this.getRandom().nextDouble() - 0.5) * (double)this.getBbWidth(), (this.getRandom().nextDouble() - 0.5) * 2.0, -this.getRandom().nextDouble(), (this.getRandom().nextDouble() - 0.5) * 2.0);
            }
        } else {
            if (SRPConfigMobs.infendermanWaterDamageEnabled && this.isInWater()) {
                if (this.waterHurtCooldown-- <= 0) {
                    this.hurt(this.damageSources().drown(), 2.0f);
                    this.waterHurtCooldown = 20;
                    this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 1, false, true));
                }
            } else {
                this.waterHurtCooldown = 0;
            }
            if (this.getTarget() != null && this.tickCount % 20 == 0 && this.distanceToSqr((Entity)this.getTarget()) > Math.pow(SRPAttributes.INFENDERMAN_TP_DIST / 2.0, 2.0) && this.getRandom().nextInt(SRPConfigMobs.infendermantelefreq) == 0 && !this.teleportAllies()) {
                this.teleportRandomly();
            }
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

    @Override
    public void updateHitboxes() {
        super.updateHitboxes();
        if (this.head != null) {
            boolean isCrawling = this.isCrawling();
            this.head.setRadius(isCrawling ? 0.9f : 0.0f);
            this.head.setAngle(isCrawling ? 1.6f : 0.0f);
            this.head.teleportTo(this.head.getX(), this.head.getY() - (double)(isCrawling ? 1.625f : 0.0f), this.head.getZ());
        }
    }

    protected boolean teleportAllies() {
        if (this.ally > 0 || !SRPConfigMobs.infendermanteleally || this.toTeleCool > 0 || this.spotCool > 0) {
            return false;
        }
        LivingEntity target = this.getTarget();
        if (target == null) {
            return false;
        }
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(64.0);
        List<? extends EntityParasiteBase> moblist = this.level().getEntitiesOfClass(EntityPInfected.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            if (mob == this || mob.getTarget() != null || mob.getParasiteType() > 15 || mob instanceof EntityCanMelt && ((EntityCanMelt)(mob)).isMelting() || mob.getHealth() - SRPConfigMobs.infendermanTeleDamage < 2.0f || !this.teleportToEntity((Entity)mob, 1.0)) continue;
            this.setCoordTarget(target.getX(), target.getY(), target.getZ());
            this.toTele = mob;
            this.setWorkTask(false);
            this.ally = 1;
            return true;
        }
        for (EntityParasiteBase mob : moblist) {
            if (!(mob instanceof EntityMudo) || mob.getTarget() != null || mob.getHealth() - SRPConfigMobs.infendermanTeleDamage < 2.0f || !this.teleportToEntity((Entity)mob, 1.0)) continue;
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
                    this.toTele.hurt(this.damageSources().fall(), SRPConfigMobs.infendermanTeleDamage);
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
        this.toTeleCool = SRPConfigMobs.infendermanallyCool;
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
    protected void tickDeath() {
        super.tickDeath();
        if (this.deathTime == 20 && !this.level().isClientSide && this.getRandom().nextDouble() <= SRPAttributes.INFENDERMAN_HEADCHANCE) {
            ParasiteSummon.spawnM(this, new String[]{"srparasites:sim_endermanhead;1;1"}, 0, false, SRPEntityUtil.getCustomNameTag(this));
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        this.spotCool = 0;
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source instanceof DamageSource) {
            for (int i = 0; i < 64; ++i) {
                if (!this.teleportRandomly()) continue;
                return true;
            }
            return false;
        }
        boolean flag = super.hurt(source, amount);
        if (source.is(DamageTypeTags.BYPASSES_ARMOR) && this.getRandom().nextInt(SRPConfigMobs.infendermantelefreq) == 0 && !this.teleportAllies()) {
            this.teleportRandomly();
        }
        return flag;
    }

    @Override
    public void cycleManualVariant() {
        if (this.isAriral()) {
            this.setAriral(false);
            this.setTextureVariant(0);
            return;
        }
        if (this.getTextureVariant() == 0) {
            this.setTextureVariant(1);
            this.setAriral(false);
            return;
        }
        this.setTextureVariant(0);
        this.setAriral(true);
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            if (this.getRandom().nextDouble() < (double)SRPConfig.infectedBleedingChance && entityIn instanceof LivingEntity) {
                SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 100, 0);
            }
            if (this.getRandom().nextInt(SRPConfigMobs.infendermantelefreq) == 0 && !this.teleportAllies()) {
                this.teleportRandomly();
            }
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

    public void setCrawling(boolean in) {
        this.entityData.set(CRAWLING, in);
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

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    @Override
    public EntityPFeral getFeral(Level in) {
        return new EntityFerEnderman(SRPEntities.FER_ENDERMAN.get(), in);
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SRPTextureVariant", this.getTextureVariant());
        compound.putBoolean("thiscancrawl", this.isCrawling());
        compound.putBoolean("SRPAriral", this.isAriral());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SRPAriral")) {
            this.setAriral(compound.getBoolean("SRPAriral"));
        }
        if (compound.contains("SRPTextureVariant")) {
            this.setTextureVariant(compound.getInt("SRPTextureVariant"));
        }
        if (compound.contains("thiscancrawl", 99)) {
            this.setCrawling(compound.getBoolean("thiscancrawl"));
            if (this.isCrawling()) {
                // empty if block
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        livingdata = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        this.setTextureVariant(this.getRandom().nextBoolean() ? 1 : 0);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            this.setCrawling(true);
        }
        return livingdata;
    }

    public boolean isAriral() {
        return (Boolean)this.entityData.get(ARIRAL);
    }

    public void setAriral(boolean value) {
        this.entityData.set(ARIRAL, value);
    }

    private net.minecraft.world.entity.EntityDimensions srpSize;

    /** The 1.12 setSize(width, height): the entity dimensions are replaced and the bounding box refreshed. */
    protected void setSize(float width, float height) {
        this.srpSize = net.minecraft.world.entity.EntityDimensions.scalable(width, height);
        this.refreshDimensions();
    }

    @Override
    protected net.minecraft.world.entity.EntityDimensions getDefaultDimensions(net.minecraft.world.entity.Pose pose) {
        return this.srpSize != null ? this.srpSize : super.getDefaultDimensions(pose);
    }
}
