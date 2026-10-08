package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.entity.EntityOrbScary;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;

public abstract class EntityPCosmical
extends EntityPMalleable {
    private static final EntityDataAccessor<Boolean> SHADOW = SynchedEntityData.defineId(EntityPCosmical.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CLONE = SynchedEntityData.defineId(EntityPCosmical.class, EntityDataSerializers.BOOLEAN);
    protected int cloneLife;
    protected float hackHeal;
    protected String[] hackEffects;
    private ArrayList<EntityDataAccessor<Integer>> victims = new ArrayList();
    private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(EntityPCosmical.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ENTITY1 = SynchedEntityData.defineId(EntityPCosmical.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ENTITY2 = SynchedEntityData.defineId(EntityPCosmical.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ENTITY3 = SynchedEntityData.defineId(EntityPCosmical.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ENTITY4 = SynchedEntityData.defineId(EntityPCosmical.class, EntityDataSerializers.INT);
    public int limitClones;
    protected int limitClonesLife;
    protected int limitClonesCooldown;
    protected float shadowDamage;
    protected int shadowDamageTick;
    public float shadowDamageR;
    public int shadowDamageRCooldown;
    protected int shakeee;
    protected int showC;
    private boolean skillHack;
    protected int borderHack;

    public EntityPCosmical(EntityType<? extends EntityPCosmical> type, Level worldIn) {
        super(type, worldIn);
        this.victims.add(TARGET_ENTITY);
        this.victims.add(TARGET_ENTITY1);
        this.victims.add(TARGET_ENTITY2);
        this.victims.add(TARGET_ENTITY3);
        this.victims.add(TARGET_ENTITY4);
        this.hackHeal = 0.01f;
        this.cothSpread = 1.0f;
        this.goalSelector.addGoal(2, new EntityAISkill(this, 160, 100, 10, false, 32, true));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 240, 100, 0, false, 33, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHADOW, true);
        builder.define(CLONE, false);
        builder.define(TARGET_ENTITY, 0);
        builder.define(TARGET_ENTITY1, 0);
        builder.define(TARGET_ENTITY2, 0);
        builder.define(TARGET_ENTITY3, 0);
        builder.define(TARGET_ENTITY4, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("SRPShadow", this.getShadowStatus());
        compound.putBoolean("SRPClone", this.getCloneC());
        compound.putInt("SRPLimitClones", this.limitClones);
        compound.putInt("SRPLimitClonesLife", this.limitClonesLife);
        compound.putInt("SRPLimitClonesCooldown", this.limitClonesCooldown);
        compound.putFloat("SRPShadowDamage", this.shadowDamage);
        compound.putInt("SRPShadowDamageTick", this.shadowDamageTick);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SRPShadow")) {
            this.entityData.set(SHADOW, compound.getBoolean("SRPShadow"));
        }
        if (compound.contains("SRPClone")) {
            this.entityData.set(CLONE, compound.getBoolean("SRPClone"));
        }
        if (compound.contains("SRPLimitClones")) {
            this.limitClones = compound.getInt("SRPLimitClones");
        }
        if (compound.contains("SRPLimitClonesLife")) {
            this.limitClonesLife = compound.getInt("SRPLimitClonesLife");
        }
        if (compound.contains("SRPLimitClonesCooldown")) {
            this.limitClonesCooldown = compound.getInt("SRPLimitClonesCooldown");
        }
        if (compound.contains("SRPShadowDamage")) {
            this.shadowDamage = compound.getFloat("SRPShadowDamage");
        }
        if (compound.contains("SRPShadowDamageTick")) {
            this.shadowDamageTick = compound.getInt("SRPShadowDamageTick");
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.shakeee > 0) {
            --this.shakeee;
        }
        if ((float)this.shadowDamageRCooldown > 0.0f) {
            --this.shadowDamageRCooldown;
        }
        if (this.shadowDamageR > 0.0f && this.shadowDamageRCooldown == 0) {
            this.shadowDamageR -= 0.01f;
        }
        if (this.showC > -10) {
            --this.showC;
        }
        if (this.getRandom().nextInt(10) == 0 && this.showC <= -10) {
            this.showC = this.getRandom().nextInt(1) * 60;
        }
        if (this.srpTicks == 10 && this.getRandom().nextInt(10) == 0) {
            this.shadowDamageR = 0.6f;
            this.shadowDamageRCooldown = 40;
            this.level().broadcastEntityEvent((Entity)this, (byte)42);
        }
        if (!this.level().isClientSide) {
            if (this.srpTicks == 5 || this.srpTicks == 15) {
                this.setEffectsTargets(true);
            }
            --this.shadowDamageTick;
            if (this.shadowDamageTick <= 0) {
                this.shadowDamage = 0.0f;
            }
            if (this.srpTicks == 10) {
                this.checkShadow(100);
            }
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        if (this.level().isClientSide) {
            return false;
        }
        if (source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.IN_WALL)) {
            return super.hurt(source, amount);
        }
        if (this.getShadowStatus() || this.getCloneC()) {
            if (SRPEntityUtil.lightBrightness(this.level(), this.blockPosition()) <= 0.46666667f && this.level().getBrightness(LightLayer.BLOCK, this.blockPosition()) <= 7) {
                this.shakeee = 15;
                this.shadowDamageR = 0.6f;
                this.shadowDamageRCooldown = 40;
                this.showC = 15;
                this.level().broadcastEntityEvent((Entity)this, (byte)41);
                boolean flag = super.hurt(source, 0.0f);
                if (flag) {
                    this.entityData.set(HIT, (byte) 3);
                }
                return flag;
            }
            this.shadowDamage += amount;
            this.shadowDamageTick = 100;
            if (this.shadowDamage >= this.getMaxHealth() * 0.1f && !this.getCloneC()) {
                this.setShadowStatus(false);
                EntityPCosmical lol = this.getThis();
                if (lol != null) {
                    this.spawnCloneCosmical(lol);
                }
                this.shadowDamageTick = 200;
                this.shadowDamage = 0.0f;
            }
            this.shakeee = 15;
            this.shadowDamageR = 0.6f;
            this.shadowDamageRCooldown = 40;
            this.showC = 15;
            this.level().broadcastEntityEvent((Entity)this, (byte)41);
            this.entityData.set(HIT, (byte) 3);
            boolean flag = super.hurt(source, 0.0f);
            if (flag) {
                this.entityData.set(HIT, (byte) 3);
            }
            return flag;
        }
        boolean flag = super.hurt(source, amount);
        if (flag && source.getEntity() instanceof LivingEntity) {
            ((LivingEntity)source.getEntity()).hurt(this.damageSources().mobAttack(this), this.damageAmountReduced / 2.0f);
            this.attackEntityAsMobMinimum((LivingEntity)source.getEntity(), this.damageAmountReduced / 2.0f);
        }
        return flag;
    }

    @Override
    public void setAttackTarget(LivingEntity entitylivingbaseIn) {
        super.setTarget(entitylivingbaseIn);
    }

    @Override
    public void die(DamageSource cause) {
        Entity flag;
        super.die(cause);
        if (!this.level().isClientSide && (flag = this.level().getEntity(this.limitClones)) != null && !this.getCloneC()) {
            flag.hurt(this.damageSources().fellOutOfWorld(), 100000.0f);
        }
    }

    protected abstract EntityPCosmical getThis();

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected void checkShadow(int chance) {
        if (this.getParasiteStatus() >= 3) {
            return;
        }
        if (this.getCloneC()) {
            ++this.limitClonesLife;
            if (this.limitClonesLife > 22) {
                this.particleStatus((byte)7);
                Entity flag = this.level().getEntity(this.limitClones);
                if (flag instanceof EntityPCosmical) {
                    EntityPCosmical original = (EntityPCosmical)flag;
                    original.particleStatus((byte)7);
                    original.limitClones = 0;
                    original.setShadowStatus(true);
                    original.limitClonesCooldown = 10;
                }
                this.discard();
                return;
            }
            return;
        }
        if (this.getShadowStatus()) {
            EntityPCosmical lol;
            --this.limitClonesCooldown;
            if (this.getRandom().nextInt(chance) == 0 && this.limitClonesCooldown <= 0 && (lol = this.getThis()) != null) {
                this.spawnCloneCosmical(lol);
            }
        } else {
            Entity flag = this.level().getEntity(this.limitClones);
            if (flag == null) {
                if (this.shadowDamageTick <= 0) {
                    this.particleStatus((byte)7);
                    this.limitClones = 0;
                    this.setShadowStatus(true);
                    this.limitClonesCooldown = 10;
                }
            } else if (this.getTarget() != null) {
                ((EntityPCosmical)flag).setTarget(this.getTarget());
            }
        }
    }

    protected void spawnCloneCosmical(EntityPCosmical entityout) {
        entityout.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        entityout.finalizeSpawn((ServerLevel) entityout.level(), this.level().getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        if (this.hasCustomName()) {
            SRPEntityUtil.setCustomNameTag(entityout, "--" + SRPEntityUtil.getCustomNameTag(this) + "--");
            entityout.setCustomNameVisible(this.isCustomNameVisible());
        }
        this.level().addFreshEntity((Entity)entityout);
        entityout.particleStatus((byte)7);
        this.limitClones = entityout.getId();
        entityout.limitClones = this.getId();
        this.setShadowStatus(false);
        entityout.setCloneC();
        entityout.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(entityout.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() * 1.33);
        entityout.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(entityout.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * 0.5);
    }

    public int shakingC() {
        return this.shakeee;
    }

    public int showC() {
        return this.showC;
    }

    public boolean getShadowStatus() {
        return (Boolean)this.entityData.get(SHADOW);
    }

    public void setShadowStatus(boolean in) {
        this.entityData.set(SHADOW, in);
        if (in) {
            this.shadowDamageR = 0.6f;
            this.shadowDamageRCooldown = 40;
            this.level().broadcastEntityEvent((Entity)this, (byte)41);
        }
    }

    public boolean getCloneC() {
        return (Boolean)this.entityData.get(CLONE);
    }

    public void setCloneC() {
        this.entityData.set(CLONE, true);
    }

    private void setTargetedEntity(int entityId) {
        for (EntityDataAccessor<Integer> mob : this.victims) {
            if ((Integer)this.entityData.get(mob) != entityId) continue;
            return;
        }
        for (EntityDataAccessor<Integer> mob : this.victims) {
            if ((Integer)this.entityData.get(mob) != 0) continue;
            this.entityData.set(mob, entityId);
            return;
        }
    }

    private void updateTargets() {
        for (EntityDataAccessor<Integer> mob : this.victims) {
            if ((Integer)this.entityData.get(mob) == 0) continue;
            Entity entity = this.level().getEntity(((Integer)this.entityData.get(mob)).intValue());
            if (entity == null) {
                this.entityData.set(mob, 0);
                continue;
            }
            if (!((LivingEntity)entity).isAlive()) {
                this.entityData.set(mob, 0);
                return;
            }
            if (!(((LivingEntity)entity).distanceToSqr((Entity)this) > 576.0)) continue;
            this.entityData.set(mob, 0);
            return;
        }
    }

    private boolean hasTargetedEntity() {
        this.updateTargets();
        for (EntityDataAccessor<Integer> mob : this.victims) {
            if ((Integer)this.entityData.get(mob) == 0) continue;
            return true;
        }
        return false;
    }

    private boolean fullTargets() {
        this.updateTargets();
        int size = this.victims.size();
        int count = 0;
        for (EntityDataAccessor<Integer> mob : this.victims) {
            if ((Integer)this.entityData.get(mob) == 0) continue;
            ++count;
        }
        return size == count;
    }

    private void resetTargets() {
        for (EntityDataAccessor<Integer> mob : this.victims) {
            this.entityData.set(mob, 0);
        }
    }

    private void setEffectsTargets(boolean effects) {
        for (EntityDataAccessor<Integer> mob : this.victims) {
            String rando;
            String[] stringArray;
            Holder<MobEffect> potion;
            if ((Integer)this.entityData.get(mob) == 0) continue;
            Entity entity = this.level().getEntity(((Integer)this.entityData.get(mob)).intValue());
            if (entity == null) {
                this.entityData.set(mob, 0);
                continue;
            }
            LivingEntity mobis = (LivingEntity)entity;
            this.shadowDamageR = 0.6f;
            this.shadowDamageRCooldown = 40;
            this.level().broadcastEntityEvent((Entity)this, (byte)41);
            if (!mobis.isAlive()) {
                this.entityData.set(mob, 0);
                continue;
            }
            Collection<MobEffectInstance> potionsTarget = mobis.getActiveEffects();
            ArrayList<Holder<MobEffect>> potionsToRemove = new ArrayList<>();
            for (MobEffectInstance potionEffect : potionsTarget) {
                if (potionEffect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) continue;
                float amp = potionEffect.getAmplifier() + 1;
                potionsToRemove.add(potionEffect.getEffect());
                this.heal(this.getMaxHealth() * (this.hackHeal * amp));
            }
            for (Holder<MobEffect> potion2 : potionsToRemove) {
                mobis.removeEffect(potion2);
            }
            if (!effects || this.hackEffects.length <= 0 || (potion = SRPEntityUtil.effect((stringArray = (rando = this.hackEffects[this.getRandom().nextInt(this.hackEffects.length)]).split(";"))[1])) == null) continue;
            SRPPotions.applyStackPotion(potion, mobis, Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[2]));
        }
    }

    public ArrayList<LivingEntity> getTargetedEntityVictims() {
        if (!this.hasTargetedEntity()) {
            return new ArrayList<LivingEntity>();
        }
        ArrayList<LivingEntity> mobs = new ArrayList<LivingEntity>();
        for (EntityDataAccessor<Integer> mob : this.victims) {
            Entity entity;
            if ((Integer)this.entityData.get(mob) == 0 || (entity = this.level().getEntity(((Integer)this.entityData.get(mob)).intValue())) == null) continue;
            mobs.add((LivingEntity)entity);
        }
        return mobs;
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case 41: {
                this.shakeee = 15;
                this.shadowDamageR = 0.6f;
                this.shadowDamageRCooldown = 40;
                this.showC = 15;
                break;
            }
            case 42: {
                this.shadowDamageR = 0.6f;
                this.shadowDamageRCooldown = 40;
                break;
            }
            default: {
                super.handleEntityEvent(id);
            }
        }
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 32: {
                return this.skillOrb;
            }
            case 33: {
                return this.skillHack;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 32: {
                this.skillOrb = in;
                return;
            }
            case 33: {
                this.skillHack = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 32: {
                this.cosmicOrb();
                return;
            }
            case 33: {
                this.cosmicHacking();
            }
        }
        super.doSpecialSkill(id);
    }

    private void cosmicOrb() {
        if (this.getTarget() == null) {
            this.skillOrb = true;
            this.setParasiteStatus(0);
            this.borderOrb = 0;
            this.limitOrb = 0;
            return;
        }
        if (this.borderOrb == -1 || this.orbVersionCooldown > 0 && this.borderOrb == 0 || !this.getTarget().isAlive() || this.getCloneC()) {
            this.skillOrb = true;
            this.setParasiteStatus(0);
            this.borderOrb = 0;
            this.limitOrb = 0;
            return;
        }
        if (this.borderOrb == 0) {
            AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate((double)(this.fuseOrb / 2 + 1));
            List<? extends EntityPMalleable> moblist = this.level().getEntitiesOfClass(EntityPMalleable.class, axisalignedbb);
            for (EntityPMalleable mob : moblist) {
                if (mob == this || mob.getParasiteType() > this.getParasiteType()) continue;
                mob.setOrbVersionCooldown(4);
            }
        }
        this.setParasiteStatus(19);
        this.getNavigation().stop();
        if (this.tickCount % 20 != 0) {
            return;
        }
        ++this.borderOrb;
        if (this.borderOrb < 6) {
            if (this.borderOrb % 2 == 0) {
                return;
            }
            EntityOrbScary ttt = new EntityOrbScary(SRPEntities.ORBSCARY.get(), this.level(), this, this.fuseOrb, this.orbStartTimer, false);
            ttt.copyPosition((Entity)this.getTarget());
            Mot.setPosY(ttt, ttt.getY() - ((double)this.getTarget().getEyeHeight()));
            this.level().addFreshEntity((Entity)ttt);
            this.playSound(SRPSounds.ORB_S.get(), 1.0f, 1.0f);
            return;
        }
        if (this.borderOrb > 10) {
            this.skillOrb = true;
            this.setParasiteStatus(0);
            this.borderOrb = 0;
            this.limitOrb = 0;
        }
    }

    private void cosmicHacking() {
        if (this.getTarget() == null || this.getCloneC() || !this.onGround() || !this.getShadowStatus()) {
            this.resetTargets();
            this.skillHack = true;
            this.setParasiteStatus(0);
            this.borderHack = 0;
            this.setGlowingTag(false);
            return;
        }
        this.setParasiteStatus(20);
        this.getNavigation().stop();
        if (this.tickCount % 20 != 0) {
            return;
        }
        ++this.borderHack;
        if (this.borderHack >= 3) {
            if (!this.fullTargets()) {
                AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(24.0);
                List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                for (LivingEntity mob : moblist) {
                    if (mob == this || mob instanceof EntityParasiteBase || !mob.isAlive()) continue;
                    if (mob instanceof Player) {
                        if (((Player)mob).isCreative()) continue;
                        this.setGlowingTag(true);
                    }
                    this.setTargetedEntity(mob.getId());
                }
            }
        } else {
            this.shadowDamageR = 0.6f;
            this.shadowDamageRCooldown = 40;
            this.level().broadcastEntityEvent((Entity)this, (byte)41);
            this.particleStatus((byte)7);
        }
        if (this.borderHack >= 7) {
            this.resetTargets();
            this.skillHack = true;
            this.setParasiteStatus(0);
            this.setGlowingTag(false);
            this.borderHack = 0;
        }
    }
}

