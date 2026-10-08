package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerBear;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntityInfBear
extends EntityPInfected
implements EntityCanMelt {
    private static final EntityDataAccessor<Float> HEIGH = SynchedEntityData.defineId(EntityInfBear.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> MELTING = SynchedEntityData.defineId(EntityInfBear.class, EntityDataSerializers.BOOLEAN);
    private float aSize;
    private int sound;

    public EntityInfBear(EntityType<? extends EntityInfBear> type, Level worldIn) {
        super(type, worldIn);
        this.aSize = 1.0f;
        this.canModRender = 1;
        this.type = (byte)11;
        this.fuseTime = 40;
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.thisMelting = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 49;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infbearCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 1, 16));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPInfected.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.INFBEAR_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.INFBEAR_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.INFBEAR_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.INFBEAR_ATTACK_DAMAGE);
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
                    out.setLegs(SRPAttributes.INFBEAR_V, false);
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
    public EntityPFeral getFeral(Level world) {
        return new EntityFerBear(SRPEntities.FER_BEAR.get(), world);
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
    protected void selfExplode() {
        super.selfExplode();
        ParasiteSummon.spawnM(this, new String[]{SRPConfigMobs.infcowmob}, 0, false, SRPEntityUtil.getCustomNameTag(this));
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.3f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INFECTEDBEAR_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDBEAR_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDBEAR_DEATH.get();
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
