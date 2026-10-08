package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackSwell;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHorse;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class EntityInfHorse
extends EntityPInfected
implements EntityCanMelt {
    private static final EntityDataAccessor<Float> HEIGH = SynchedEntityData.defineId(EntityInfHorse.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> MELTING = SynchedEntityData.defineId(EntityInfHorse.class, EntityDataSerializers.BOOLEAN);
    private float aSize;
    private int sound;

    public EntityInfHorse(EntityType<? extends EntityInfHorse> type, Level worldIn) {
        super(type, worldIn);
        this.aSize = 1.0f;
        this.canModRender = 1;
        this.type = (byte)11;
        this.fuseTime = 70;
        this.thisMelting = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 44;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infhorseCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 1, 16));
        this.goalSelector.addGoal(2, new EntityAIAttackSwell(this, 5.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPInfected.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.INFHORSE_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.INFHORSE_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.26999999701976773);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.INFHORSE_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.INFHORSE_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.infectedFollow);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HEIGH, (float) (Float.valueOf(0.0f)));
        builder.define(MELTING, false);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.melting();
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (this.getTHeigh() < 1.57f && !this.level().isClientSide) {
            this.setTHeigh(0.17f);
        }
        if (this.deathTime == 20 && !this.level().isClientSide && this.getRandom().nextDouble() <= SRPAttributes.INFHORSE_HEADCHANCE) {
            ParasiteSummon.spawnM(this, new String[]{"srparasites:sim_horsehead;1;1"}, 0, false, SRPEntityUtil.getCustomNameTag(this));
        }
    }

    @Override
    public void melt() {
        this.setWait(1000);
        this.entityData.set(HEIGH, (float) (Float.valueOf(1.6f)));
        this.entityData.set(MELTING, true);
    }

    @Override
    public void melting() {
        if (this.isMelting()) {
            if (this.sound % 20 == 0) {
                this.playSound(SRPSounds.INFECTED_MELT.get(), 1.0f, 1.0f);
            }
            ++this.sound;
            if ((double)this.getTHeigh() > 0.7) {
                this.setaSize(-0.005f);
                this.setTHeigh(-0.01f);
                this.setSize(this.getBbWidth(), this.getTHeigh());
            }
            if (!this.level().isClientSide) {
                if ((double)this.getTHeigh() <= 0.7 || this.sound >= 73) {
                    EntityLesh out = new EntityLesh(SRPEntities.MOVINGFLESH.get(), this.level());
                    out.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                    if (SRPEntityUtil.getCustomNameTag(this) != null) {
                        SRPEntityUtil.setCustomNameTag(out, SRPEntityUtil.getCustomNameTag(this));
                    }
                    this.discard();
                    this.level().addFreshEntity((Entity)out);
                    out.setLegs(SRPAttributes.INFHORSE_V, false);
                }
            } else {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 106, 0);
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
            }
        }
    }

    @Override
    public boolean isMelting() {
        return (Boolean)this.entityData.get(MELTING);
    }

    @Override
    public float getTHeigh() {
        return ((Float)this.entityData.get(HEIGH)).floatValue();
    }

    @Override
    public void setTHeigh(float in) {
        this.entityData.set(HEIGH, (float) (Float.valueOf(in += this.getTHeigh())));
    }

    @Override
    public float getaSize() {
        return this.aSize;
    }

    @Override
    public void setaSize(float in) {
        this.aSize += in;
    }

    @Override
    public float getSelfeFlashIntensity2() {
        return this.aSize;
    }

    @Override
    public void setSelfeState(int state) {
        if ((double)this.getHealth() <= (double)this.getMaxHealth() * 0.5) {
            super.setSelfeState(state);
        }
    }

    public void tick() {
        if (this.isAlive()) {
            this.dyingBurst(false, 1);
        }
        super.tick();
    }

    @Override
    protected void selfExplode() {
        if (!this.level().isClientSide) {
            AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(3.5);
            List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity mob : moblist) {
                if (mob instanceof EntityParasiteBase) continue;
                mob.hurt(this.damageSources().generic(), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * SRPConfigMobs.infhorseExplotionMult);
            }
            this.playSound(SRPSounds.INFECTEDHORSE_SA2.get(), 2.0f, 1.0f);
            this.dead = true;
            this.discard();
            this.spawnLingeringCloud();
            this.spawnGore();
        } else {
            this.spawnParticles(ParticleTypes.EXPLOSION);
            this.spawnEffectsGore();
        }
    }

    private void spawnLingeringCloud() {
        EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY(), this.getZ());
        entityareaeffectcloud.setRadius(this.getBbWidth() * 1.5f, 0.5f);
        entityareaeffectcloud.setWaitTime(10);
        entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() * 2);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 0));
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
        this.level().addFreshEntity((Entity)entityareaeffectcloud);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.3f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INFECTEDHORSE_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDHORSE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDHORSE_DEATH.get();
    }

    @Override
    public EntityPFeral getFeral(Level in) {
        return new EntityFerHorse(SRPEntities.FER_HORSE.get(), in);
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.COW_STEP, 0.15f, 1.0f);
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
