package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackSwell;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import java.util.List;
import java.util.function.Predicate;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;

public class EntityRathol
extends EntityParasiteBase {
    public EntityRathol(EntityType<? extends EntityRathol> type, Level worldIn) {
        super(type, worldIn);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, true, false, null, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, true, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        }
        this.xpReward = SRPAttributes.XP_PRIMITIVE;
        this.fuseTime = 70;
        this.type = (byte)41;
        this.killcount = -10.0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 3;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(1, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(2, new EntityAIAttackSwell(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal((PathfinderMob)this, 1.1, false));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal((Mob)this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.RATHOL_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.RATHOL_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.RATHOL_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.RATHOL_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, 32.0);
        return builder;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.CARRIER_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.CARRIER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.CARRIER_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putShort("Fuse", (short)this.fuseTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Fuse", 99)) {
            this.fuseTime = compound.getShort("Fuse");
        }
    }

    public void tick() {
        if (this.isAlive()) {
            this.lastActiveTime = this.timeSinceIgnited;
            if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.05) {
                this.setSelfeState(1);
            }
            this.dyingBurst(false, 2);
        }
        super.tick();
    }

    @Override
    protected void tickDeath() {
        if (this.isOnFire()) {
            super.tickDeath();
        } else {
            this.setSelfeState(1);
            this.dyingBurst(true, 2);
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        return true;
    }

    @Override
    protected void selfExplode() {
        if (!this.level().isClientSide) {
            double i1 = Mth.floor((double)(this.getY() + 0.1));
            double l1 = this.getX();
            double i2 = this.getZ();
            Level world = this.level();
            int range = 6;
            for (int k2 = -1 * range; k2 <= 1 * range; ++k2) {
                for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                    double i3 = l1 + (double)k2;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = BlockPos.containing(i3, i1, l);
                    if ((blockpos = ParasiteEventEntity.getFloor(this.level(), blockpos, 3)) == null || this.level().random.nextInt(2) != 0) continue;
                    this.level().setBlockAndUpdate(blockpos, BlockIds.legacyState(SRPBlocks.InfestRemain.get(), 1));
                }
            }
        }
        switch (this.getSkin()) {
            case 1: {
                boolean flag = EventHooks.canEntityGrief((Level)this.level(), (Entity)this) && SRPConfigMobs.ratholGriefing;
                ParasiteEventEntity.createExplosion(this.level(), (Entity)this, this.getX(), this.getY(), this.getZ(), 4.0f, flag);
                if (!this.level().isClientSide) {
                    AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(11.0);
                    List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                    for (LivingEntity mob : moblist) {
                        if (mob instanceof EntityParasiteBase) continue;
                        mob.hurt(this.damageSources().wither(), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 400, 4);
                        mob.addEffect(new MobEffectInstance(SRPPotions.VOMIT_E, 600, 0, false, true));
                    }
                    this.playSound(SRPSounds.RATHOL_BOOM.get(), 2.0f, 1.0f);
                    this.dead = true;
                    this.discard();
                    this.spawnLingeringCloud();
                }
                return;
            }
        }
        boolean flag = EventHooks.canEntityGrief((Level)this.level(), (Entity)this) && SRPConfigMobs.ratholGriefing;
        ParasiteEventEntity.createExplosion(this.level(), (Entity)this, this.getX(), this.getY(), this.getZ(), 4.0f, flag);
        if (!this.level().isClientSide) {
            AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(7.0);
            List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity mob : moblist) {
                if (mob instanceof EntityParasiteBase) continue;
                SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 400, 2);
                mob.addEffect(new MobEffectInstance(SRPPotions.VOMIT_E, 600, 0, false, true));
            }
            this.playSound(SRPSounds.RATHOL_BOOM.get(), 2.0f, 1.0f);
            this.dead = true;
            this.discard();
            this.spawnLingeringCloud();
            ParasiteSummon.spawnM(this, SRPConfigMobs.ratholMobs, 0, false, SRPEntityUtil.getCustomNameTag(this));
        }
    }

    private void spawnLingeringCloud() {
        switch (this.getSkin()) {
            case 1: {
                EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY(), this.getZ());
                entityareaeffectcloud.setRadius(this.getBbWidth() * 3.5f, 0.5f);
                entityareaeffectcloud.setWaitTime(10);
                entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() * 2);
                entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
                entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 2));
                entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 2, false, false));
                entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.VIRA_E, 3600, 2, false, false));
                this.level().addFreshEntity((Entity)entityareaeffectcloud);
                return;
            }
        }
        EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY(), this.getZ());
        entityareaeffectcloud.setRadius(this.getBbWidth() * 3.5f, 0.5f);
        entityareaeffectcloud.setWaitTime(10);
        entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() * 2);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 0));
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.VIRA_E, 3600, 0, false, false));
        this.level().addFreshEntity((Entity)entityareaeffectcloud);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            switch (this.getRandom().nextInt(1)) {
                case 0: {
                    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3);
                    this.setSkin(1);
                }
            }
        }
        return floo;
    }
}

