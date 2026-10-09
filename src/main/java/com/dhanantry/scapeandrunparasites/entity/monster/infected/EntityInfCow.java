package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerCow;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
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

public class EntityInfCow
extends EntityPInfected
implements EntityCanMelt {
    private static final EntityDataAccessor<Float> HEIGH = SynchedEntityData.defineId(EntityInfCow.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> MELTING = SynchedEntityData.defineId(EntityInfCow.class, EntityDataSerializers.BOOLEAN);
    private float aSize;
    private int sound;
    private int attacking;
    private double targetX;
    private double targetY;
    private double targetZ;
    private boolean skillCharge;

    public EntityInfCow(EntityType<? extends EntityInfCow> type, Level worldIn) {
        super(type, worldIn);
        this.aSize = 1.0f;
        this.canModRender = 1;
        this.type = (byte)11;
        this.fuseTime = 40;
        this.skillCharge = false;
        this.thisMelting = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 13;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infcowCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 1, 16));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 60, 32, 8, true, 1));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPInfected.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.INFCOW_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.INFCOW_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, (double)0.2f);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.INFCOW_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.INFCOW_ATTACK_DAMAGE);
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
        if (this.deathTime == 20 && !this.level().isClientSide && this.getRandom().nextDouble() <= SRPAttributes.INFCOW_HEADCHANCE) {
            ParasiteSummon.spawnM(this, new String[]{"srparasites:sim_cowhead;1;1"}, 0, false, SRPEntityUtil.getCustomNameTag(this));
        }
    }

    @Override
    public void melt() {
        this.setWait(1000);
        this.entityData.set(HEIGH, (float) (Float.valueOf(1.4f)));
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
                    out.setLegs(SRPAttributes.INFCOW_V, false);
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
        return SRPSounds.INFECTEDCOW_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDCOW_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDCOW_DEATH.get();
    }

    @Override
    public EntityPFeral getFeral(Level in) {
        return new EntityFerCow(SRPEntities.FER_COW.get(), in);
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.COW_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillCharge;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillCharge = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.charge();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void charge() {
        ++this.attacking;
        this.MiniDamage = 0.0f;
        if (this.attacking < 40) {
            LivingEntity entitylivingbase = this.getTarget();
            if (entitylivingbase == null || !this.onGround() || this.isInWater() || entitylivingbase.getY() > this.getY()) {
                this.skillCharge = true;
                this.attacking = 0;
                this.setParasiteStatus(0);
                this.MiniDamage = SRPConfig.infectedMinDamage;
                return;
            }
            if (!entitylivingbase.isAlive()) {
                this.skillCharge = true;
                this.attacking = 0;
                this.setParasiteStatus(0);
                this.MiniDamage = SRPConfig.infectedMinDamage;
                return;
            }
            if (this.attacking <= 39) {
                double dis = this.distanceTo((Entity)entitylivingbase);
                this.setParasiteStatus(3);
                this.getNavigation().stop();
                this.targetX = this.getX() + 15.0 * (entitylivingbase.getX() - this.getX()) / dis;
                this.targetY = this.getY() + 15.0 * (entitylivingbase.getY() - this.getY()) / dis;
                this.targetZ = this.getZ() + 15.0 * (entitylivingbase.getZ() - this.getZ()) / dis;
            }
        }
        if (this.attacking == 40) {
            this.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, 2.0);
        }
        if (this.attacking >= 40) {
            for (LivingEntity mob : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0, 0.0, 1.0))) {
                if (mob == this || mob instanceof EntityParasiteBase) continue;
                float f = (float)Mth.atan2((double)(mob.getZ() - this.getZ()), (double)(mob.getX() - this.getX()));
                EntityDamage damage = new EntityDamage(this.level(), mob.getX(), mob.getY(), mob.getZ(), f, (LivingEntity)this, 1.0f, false, 0.5f);
                this.level().addFreshEntity((Entity)damage);
            }
        }
        this.skillBreakBlocks();
        if (!this.onGround()) {
            Mot.mulX(this, 0.7);
            Mot.mulZ(this, 0.7);
        }
        if (this.attacking >= 80 && this.getX() == this.xo && this.getZ() == this.zo) {
            this.attacking = 0;
            this.skillCharge = true;
            this.setParasiteStatus(2);
            this.MiniDamage = SRPConfig.infectedMinDamage;
        }
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
