package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityCanra;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.EntityBodyDeadPayload;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityCanraAdapted
extends EntityPAdapted
implements EntityCanSummon,
EntityBodyParts,
RangedAttackMob {
    private EntityBody leftTendril;
    private EntityBody rightTendril;
    private float leftTendrilHealth;
    private float rightTendrilHealth;
    private int totalP = SRPConfigMobs.canraadaptedtotalactivemobs;
    private int actualP = 0;
    private int[] mobID = new int[this.totalP + SRPConfigMobs.canraadaptedlimit];
    private int[] mobPT = new int[this.totalP + SRPConfigMobs.canraadaptedlimit];
    int vomit;
    private int limit;
    private int border;
    private boolean skillSummon;

    public EntityCanraAdapted(EntityType<? extends EntityCanraAdapted> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        for (int i = 0; i < this.mobID.length; ++i) {
            this.mobID[i] = -777;
        }
        this.leftTendril = new EntityBody(this, 0.6f, 1.7f, 1.0f, 0.9f, 1.8f, 1, 1, true);
        this.rightTendril = new EntityBody(this, 0.6f, 1.7f, 1.0f, 0.9f, 1.8f, -1, 2, true);
        this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.skillSummon = false;
    }

    @Override
    public int getParasiteIDRegister() {
        return 53;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.11));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 20 * SRPConfigMobs.canraadaptedsummoningcooldown, 16, true, 1));
        this.goalSelector.addGoal(6, new EntityAIAttackMeleeRangeSwitch(this, 5.0f));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        this.goalSelector.addGoal(4, new EntityAIAttackRangedStatus(this, 1.0, 140, 9.0f, false));
        if (SRPConfig.parasiteGenResidue) {
            this.goalSelector.addGoal(9, new EntityAIBlockResidue(this, 2));
        }
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 3, 32));
        this.goalSelector.addGoal(2, new EntityAIEvade(this, 25, 10, 4.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAdapted.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.CANRA_HEALTH + SRPAttributes.CANRA_A_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.CANRA_ARMOR + SRPAttributes.CANRA_A_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.29);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.CANRA_KD_RESISTANCE + SRPAttributes.CANRA_A_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.CANRA_ATTACK_DAMAGE + SRPAttributes.CANRA_A_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.adaptedFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.leftTendrilHealth > 0.0f) {
            this.leftTendril.tick();
        }
        if (this.rightTendrilHealth > 0.0f) {
            this.rightTendril.tick();
        }
        if (!this.level().isClientSide && this.getRandom().nextInt(2) == 0 && this.tickCount % 60 == 0) {
            boolean flag = false;
            AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(16.0);
            List<? extends EntityRemain> moblist = this.level().getEntitiesOfClass(EntityRemain.class, axisalignedbb);
            for (EntityRemain mob : moblist) {
                if (mob.getActive()) continue;
                mob.setPlus(SRPConfigMobs.canraadaptedremainplus);
                mob.setHealth(SRPConfigMobs.canraadaptedremainhealth);
                flag = true;
            }
            if (flag) {
                this.playSound(SRPSounds.ACANRA_SPECIAL.get(), 3.0f, 1.0f);
                this.particleStatus((byte)8);
            }
        }
        if (this.level().isClientSide && this.vomit > 0) {
            --this.vomit;
            for (int i = 0; i < 6; ++i) {
                Vec3 vec3d = this.getViewVector(1.0f);
                double bon = 1.2;
                double offsetX = this.getX() + vec3d.x * bon;
                double offsetY = this.getY() + (double)this.getEyeHeight() - 0.2;
                double offsetZ = this.getZ() + vec3d.z * bon;
                double motionX = (double)(-Mth.sin((float)(this.getYRot() * (float)Math.PI / 180.0f))) * 0.2;
                double motionZ = (double)Mth.cos((float)(this.getYRot() * (float)Math.PI / 180.0f)) * 0.2;
                double motionY = 0.01 + this.getRandom().nextDouble() * 0.1;
                double spreadFactor = 0.25;
                this.spawnParticles(SRPEnumParticle.GCLOUD, 123, 0, 196, offsetX, offsetY, offsetZ, motionX += (this.getRandom().nextDouble() - 0.5) * spreadFactor, motionY, motionZ += (this.getRandom().nextDouble() - 0.5) * spreadFactor);
            }
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && entityIn instanceof LivingEntity) {
            switch (this.getSkin()) {
                case 5: {
                    SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 100, 0);
                    break;
                }
                case 6: {
                    SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 100, 0);
                }
            }
        }
        return flag;
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.level().isClientSide) {
            return false;
        }
        boolean flag = this.hurt(source, amount);
        if (!flag) {
            return false;
        }
        if (this.leftTendril.getPartId() == id) {
            this.leftTendrilHealth -= amount;
            if (this.leftTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(SRPEntities.TENDRIL.get(), this.level());
                tendril.setSkin(3);
                tendril.copyPosition(this.leftTendril);
                this.level().addFreshEntity((Entity)tendril);
                this.leftTendril.discard();
                this.level().broadcastEntityEvent((Entity)this, (byte)11);
                this.cutResistances(SRPConfig.adaptedPointDamCap / 2);
                PacketDistributor.sendToAllPlayers(new EntityBodyDeadPayload(this.getId(), id));
            }
        } else if (this.rightTendril.getPartId() == id) {
            this.rightTendrilHealth -= amount;
            if (this.rightTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(SRPEntities.TENDRIL.get(), this.level());
                tendril.setSkin(3);
                tendril.copyPosition(this.rightTendril);
                this.level().addFreshEntity((Entity)tendril);
                this.rightTendril.discard();
                this.level().broadcastEntityEvent((Entity)this, (byte)22);
                this.cutResistances(SRPConfig.adaptedPointDamCap / 2);
                PacketDistributor.sendToAllPlayers(new EntityBodyDeadPayload(this.getId(), id));
            }
        }
        return flag;
    }

    @Override
    public void setBodyPartDead(int id) {
        if (this.leftTendril.getPartId() == id) {
            this.leftTendril.discard();
        } else if (this.rightTendril.getPartId() == id) {
            this.rightTendril.discard();
        }
    }

    @Override
    protected void doPush(Entity entityIn) {
        super.doPush(entityIn);
        if (this.level().isClientSide) {
            return;
        }
        if (entityIn instanceof LivingEntity && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 100, 0);
        }
    }

    @Override
    public int getTotalParasites() {
        return this.totalP;
    }

    @Override
    public int getActualParasites() {
        return this.actualP;
    }

    @Override
    public void setActualParasites(int i) {
        this.actualP += i;
    }

    @Override
    public void addID(int id, int points) {
        for (int i = 0; i < this.mobID.length; ++i) {
            if (this.mobID[i] != -777) continue;
            this.mobID[i] = id;
            this.mobPT[i] = points;
            return;
        }
    }

    @Override
    public int IDable() {
        int flag = 0;
        for (int i = 0; i < this.mobID.length; ++i) {
            if (this.mobID[i] != -777) continue;
            ++flag;
        }
        if (flag > this.totalP) {
            flag = this.totalP;
        }
        return flag;
    }

    @Override
    public void checkID() {
        for (int i = 0; i < this.mobID.length; ++i) {
            Entity flag;
            if (this.mobID[i] <= 0 || (flag = this.level().getEntity(this.mobID[i])) != null) continue;
            this.mobID[i] = -777;
            int negative = this.mobPT[i] * -1;
            this.setActualParasites(negative);
        }
    }

    @Override
    public int[] getIDList() {
        return null;
    }

    @Override
    public int[] getPointList() {
        return null;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.8f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityCanra(SRPEntities.PRI_SUMMONER.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ACANRA_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ACANRA_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.ACANRA_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.canraadaptedOrbEffects, mobs);
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.HEAVY_STEPS_TWO.get(), 0.15f, 1.0f);
    }

    @Override
    public void setDead() {
        if (this.leftTendril != null) {
            this.leftTendril.discard();
        }
        if (this.rightTendril != null) {
            this.rightTendril.discard();
        }
        super.discard();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.getRandom().nextInt(3)) {
                case 0: {
                    this.setSkin(5);
                    break;
                }
                case 1: {
                    this.setSkin(6);
                    break;
                }
                case 2: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("parasiteleftTendril", this.leftTendrilHealth);
        compound.putFloat("parasiterightTendril", this.rightTendrilHealth);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("parasiteleftTendril", 99)) {
            this.leftTendrilHealth = compound.getFloat("parasiteleftTendril");
            if (this.leftTendrilHealth <= 0.0f) {
                this.level().broadcastEntityEvent((Entity)this, (byte)11);
            }
        }
        if (compound.contains("parasiterightTendril", 99)) {
            this.rightTendrilHealth = compound.getFloat("parasiterightTendril");
            if (this.rightTendrilHealth <= 0.0f) {
                this.level().broadcastEntityEvent((Entity)this, (byte)22);
            }
        }
    }

    public float getLeft() {
        return this.leftTendrilHealth;
    }

    public float getRight() {
        return this.rightTendrilHealth;
    }

    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.level().broadcastEntityEvent((Entity)this, (byte)100);
        Vec3 vec3d = this.getViewVector(1.0f);
        double bon = 6.5;
        AreaEffectCloud entityareaeffectcloud = new AreaEffectCloud(this.level(), this.getX() + vec3d.x * bon, this.getY(), this.getZ() + vec3d.z * bon);
        entityareaeffectcloud.setRadius(5.0f);
        entityareaeffectcloud.setDuration(100);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.VOMIT_E, 300, 0, false, true));
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.VIRA_E, 300, 40, false, true));
        entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 40, false, true));
        entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 40, false, true));
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.CORRO_E, 300, 40, false, true));
        this.level().addFreshEntity((Entity)entityareaeffectcloud);
        this.lookAt((Entity)target);
        this.setWait(60);
    }

    public void setAggressive(boolean swingingArms) {
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 11) {
            this.leftTendrilHealth = 0.0f;
        } else if (id == 22) {
            this.rightTendrilHealth = 0.0f;
        } else if (id == 100) {
            this.vomit = 40;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillSummon;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillSummon = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.summon();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void summon() {
        this.setParasiteStatus(10);
        this.getNavigation().stop();
        if (this.tickCount % 20 != 0) {
            return;
        }
        ++this.border;
        if (this.level().getBlockState(this.blockPosition()).getBlock() instanceof LiquidBlock) {
            this.skillSummon = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.limit = 0;
            return;
        }
        this.checkID();
        if (this.getActualParasites() < this.getTotalParasites() && this.limit < SRPConfigMobs.canraadaptedlimit) {
            this.playSound(SRPSounds.ACANRA_SPECIAL.get(), 3.0f, 1.0f);
            if (ParasiteEventEntity.spawnBiomassFromVomit(this, SRPConfigMobs.canraadaptedmoblist, this.getTarget())) {
                ++this.limit;
                --this.border;
                this.particleStatus((byte)8);
            }
        } else {
            ++this.border;
        }
        if (this.limit >= SRPConfigMobs.canraadaptedlimit || this.border > 4) {
            this.skillSummon = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.limit = 0;
        }
    }
}

