package com.dhanantry.scapeandrunparasites.entity.monster.ancient;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAncient;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileHomming;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityDharma
extends EntityPAncient
implements EntityBodyParts,
RangedAttackMob,
EntityCutomAttack {
    private EntityBody head;
    private EntityBody middle;
    private final ServerBossEvent bossInfo = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false);

    public EntityDharma(EntityType<? extends EntityDharma> type, Level worldIn) {
        super(type, worldIn);
        this.goalSelector.removeGoal(this.folow);
        this.noCulling = true;
        this.type = (byte)63;
        this.borderOrb = -1;
        this.head = new EntityBody(this, 2.4f, 7.5f, 1.0f, -3.0f, 0.0f, -1, 1, false, 0.2f);
        this.middle = new EntityBody(this, 2.4f, 4.5f, 1.0f, 0.0f, 3.0f, 1, 2, false, 0.2f);
    }

    @Override
    public int getParasiteIDRegister() {
        return 0;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIAttackMeleeRangeSwitch(this, 10.0f));
        this.goalSelector.addGoal(2, new EntityAIAttackMeleeStatusAOE(this, 1.0, false, 100.0, 5.0));
        this.goalSelector.addGoal(4, new EntityAIAttackRangedStatus(this, 1.0, 80, 40.0f, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAncient.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.TERLA_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.TERLA_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, (double)0.23f);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.TERLA_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.ancientFollow);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 2.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.head.tick();
        this.middle.tick();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public void setCustomNameTag(String name) {
        SRPEntityUtil.setCustomNameTag(this, name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void addTrackingPlayer(ServerPlayer player) {
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        Vec3 vec3d = this.getViewVector(1.0f);
        double d2 = target.getX() - (this.getX() + vec3d.x);
        double d3 = target.getBoundingBox().minY + (double)(target.getBbHeight() / 2.0f) - (0.5 + this.getY() + (double)(this.getBbHeight() / 2.0f));
        double d4 = target.getZ() - (this.getZ() + vec3d.z);
        this.playSound(SRPSounds.EMANA_SHOOTING.get(), 2.0f, 1.0f);
        d3 = target.getBoundingBox().minY + (double)(target.getBbHeight() / 4.0f) - (1.0 + this.getY() + (double)(this.getBbHeight() / 2.0f));
        EntityProjectileHomming entitylargefireball = new EntityProjectileHomming(SRPEntities.HOMMING.get(), this.level(), (LivingEntity)this, (Entity)target, SRPAttributes.ORONCO_ATTACK_DAMAGE);
        Mot.setPosX(entitylargefireball, this.getX() + vec3d.x);
        Mot.setPosY(entitylargefireball, this.getY() + (double)this.getEyeHeight() - 0.2);
        Mot.setPosZ(entitylargefireball, this.getZ() + vec3d.z);
        this.level().addFreshEntity((Entity)entitylargefireball);
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        AABB axisalignedbb = new AABB(entityIn.getX(), entityIn.getY(), entityIn.getZ(), entityIn.getX() + 1.0, entityIn.getY() + 1.0, entityIn.getZ() + 1.0).inflate(5.0, 2.0, 5.0);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        if (moblist.size() > 4) {
            axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(5.0, 3.0, 5.0);
            moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            float luck = (float)(SRPAttributes.TERLA_ATTACK_DAMAGE * 2.0);
            for (LivingEntity mob : moblist) {
                if (mob == null || mob instanceof EntityParasiteBase || mob == this) continue;
                EntityDamage damage = new EntityDamage(this.level(), mob.getX(), mob.getY(), mob.getZ(), 0.0f, (LivingEntity)this, luck, false, 3.0f);
                this.level().addFreshEntity((Entity)damage);
            }
            return true;
        }
        for (LivingEntity mob : moblist) {
            if (mob == null || mob instanceof EntityParasiteBase) continue;
            this.doHurtTarget((Entity)mob);
        }
        return !moblist.isEmpty();
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag && source.getEntity() instanceof ServerPlayer) {
            this.bossInfo.addPlayer((ServerPlayer)source.getEntity());
        }
        return flag;
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        boolean flag = this.hurt(source, amount);
        return flag;
    }

    @Override
    public void setBodyPartDead(int id) {
    }

    public int getHorizontalFaceSpeed() {
        return 3;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return potioneffectIn.getEffect() == MobEffects.POISON ? false : super.canBeAffected(potioneffectIn);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 4.7f;
    }

    protected SoundEvent getAmbientSound() {
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.MOBSILENCE.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    protected float getSoundVolume() {
        return 5.0f;
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.head.discard();
        }
        if (this.middle != null) {
            this.middle.discard();
        }
        super.discard();
    }

    public void setAggressive(boolean swingingArms) {
    }
}

