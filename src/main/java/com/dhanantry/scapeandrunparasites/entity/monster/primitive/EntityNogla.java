package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIDiveBomb;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityNoglaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class EntityNogla
extends EntityPPrimitive {
    private EntityHitbox head;
    private static final byte STATUS_RICARDO_BURST = 77;
    private boolean ricardoBaseCaptured = false;
    private double ricardoBaseMaxHealth;
    private double ricardoBaseArmor;
    private double ricardoBaseToughness;
    private static final EntityDataAccessor<Boolean> RICARDO_BALD = SynchedEntityData.defineId(EntityNogla.class, EntityDataSerializers.BOOLEAN);
    private int attacking;
    private double targetX;
    private double targetY;
    private double targetZ;
    private boolean skillCharge;

    public EntityNogla(EntityType<? extends EntityNogla> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.skillCharge = false;
        this.head = new EntityHitbox((Mob)this, 1.6f, 0.9f, 2.2f, 0.7f, 0.8f, 1.25f);
        this.hitboxes = new EntityHitbox[]{this.head};
    }

    @Override
    public int getParasiteIDRegister() {
        return 10;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.095));
        this.goalSelector.addGoal(1, new EntityAIDiveBomb((Mob)this, 1200, 60, 3.8, 3.0f));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 40, 32, 8, true, 1));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 2, 16));
        this.goalSelector.addGoal(2, new EntityAIEvade(this, 55, 10, 4.0));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RICARDO_BALD, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPrimitive.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.NOGLA_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.NOGLA_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.31234);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.NOGLA_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.NOGLA_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.primitiveFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.tickCount % 20 == 0 && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityNoglaAdapted(SRPEntities.ADA_REEKER.get(), this.level()), true, true);
        }
        if (!this.level().isClientSide && this.isRicardoBald() && this.tickCount % 20 == 0) {
            this.applyPermanentRicardoRage();
        }
        if (this.level().isClientSide && this.isRicardoVariant() && (this.tickCount & 3) == 0) {
            for (int i = 0; i < 3; ++i) {
                double x = this.getX() + (this.getRandom().nextDouble() - 0.5) * (double)this.getBbWidth() * 1.6;
                double y = this.getY() + this.getRandom().nextDouble() * ((double)this.getBbHeight() * 0.9);
                double z = this.getZ() + (this.getRandom().nextDouble() - 0.5) * (double)this.getBbWidth() * 1.6;
                float r = 1.0f;
                float g = 0.25f;
                float b = 0.75f;
                this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, r, g, b), x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("RicardoBald", ((Boolean)this.entityData.get(RICARDO_BALD)).booleanValue());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(RICARDO_BALD, compound.getBoolean("RicardoBald"));
        if (!this.level().isClientSide && this.isRicardoBald()) {
            this.applyPermanentRicardoRage();
        }
    }

    @Override
    protected void doPush(Entity entityIn) {
        super.doPush(entityIn);
        if (this.level().isClientSide) {
            return;
        }
        if (entityIn instanceof LivingEntity && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 40, 0);
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 2.4f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (this.isRicardoVariant()) {
                this.spawnAtLocation(new ItemStack(SRPItems.bookofvengeance.get(), 1), 0.0f);
            }
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityLesh(SRPEntities.MOVINGFLESH.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            if (entityIn instanceof LivingEntity) {
                ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, 100));
                switch (this.getSkin()) {
                    case 5: {
                        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 40, 0);
                        break;
                    }
                    case 6: {
                        SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 40, 0);
                    }
                }
            }
            if (!this.level().isClientSide && entityIn instanceof Slime && this.getRandom().nextFloat() < 0.1f) {
                double dx = entityIn.getX() - this.getX();
                double dz = entityIn.getZ() - this.getZ();
                double len = Math.sqrt(dx * dx + dz * dz);
                if (len < 1.0E-4) {
                    dx = this.getRandom().nextDouble() - 0.5;
                    dz = this.getRandom().nextDouble() - 0.5;
                    len = Math.sqrt(dx * dx + dz * dz);
                }
                double horizontal = 4.75;
                double yBoost = 1.2;
                entityIn.push((dx /= len) * horizontal, yBoost, (dz /= len) * horizontal);
                entityIn.hurtMarked = true;
            }
            if (this.isRicardoVariant() && !this.level().isClientSide) {
                this.level().explode((Entity)this, entityIn.getX(), entityIn.getY(), entityIn.getZ(), 1.8f, false, false ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
            }
        }
        return flag;
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
        if (!this.level().isClientSide && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityNoglaAdapted(SRPEntities.ADA_REEKER.get(), this.level()), true, true);
        }
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.NOGLA_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.NOGLA_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.NOGLA_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.noglaOrbEffects, mobs);
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.MONSTER_STEP.get(), 0.15f, 1.0f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.getRandom().nextInt(4)) {
                case 0: {
                    this.setSkin(5);
                    break;
                }
                case 1: {
                    this.setSkin(6);
                    break;
                }
                case 2: {
                    this.setSkin(1);
                    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(SRPAttributes.NOGLA_HEALTH * 0.5);
                    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.NOGLA_ATTACK_DAMAGE * 1.5);
                    this.setHealth((float)this.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
                    break;
                }
                case 3: {
                    this.setSkin(7);
                }
            }
        }
        if (!this.level().isClientSide) {
            this.updateRicardoAttributes();
        }
        return floo;
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case 100: {
                for (int i = 0; i <= 1; ++i) {
                    this.spawnParticles(ParticleTypes.FLAME);
                }
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

    public boolean isRicardoVariant() {
        if (!SRPConfigMobs.noglaRicardoVariantEnabled) {
            return false;
        }
        if (!this.hasCustomName()) {
            return false;
        }
        String raw = ChatFormatting.stripFormatting(SRPEntityUtil.getCustomNameTag(this));
        if (raw == null) {
            return false;
        }
        return raw.trim().toLowerCase(Locale.ROOT).equals("ricardo");
    }

    public boolean isRicardoBald() {
        return this.isRicardoVariant() && (Boolean)this.entityData.get(RICARDO_BALD) != false;
    }

    private void setRicardoBald(boolean bald) {
        this.entityData.set(RICARDO_BALD, bald);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.level().isClientSide && this.isRicardoVariant() && !this.isRicardoBald() && !stack.isEmpty() && stack.getItem() == Items.SHEARS) {
            this.setRicardoBald(true);
            this.applyPermanentRicardoRage();
            this.grantRicardoShearAdvancement(player);
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SRPSounds.NOGLA_HURT.get(), SoundSource.HOSTILE, 2.0f, 0.65f);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SRPSounds.NOGLA_GROWL.get(), SoundSource.HOSTILE, 2.5f, 0.75f);
            ((ServerLevel)this.level()).sendParticles(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, -1), this.getX(), this.getY() + 1.2, this.getZ(), 40, 0.6, 0.8, 0.6, 0.05);
            this.level().broadcastEntityEvent((Entity)this, (byte)77);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    private void grantRicardoShearAdvancement(Player player) {
        if (this.level().isClientSide || !(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer playerMP = (ServerPlayer)player;
        AdvancementHolder advancement = playerMP.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath("srparasites", "tricked_me_did_you"));
        if (advancement == null) {
            return;
        }
        AdvancementProgress progress = playerMP.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) {
            for (String criterion : progress.getRemainingCriteria()) {
                playerMP.getAdvancements().award(advancement, criterion);
            }
        }
    }

    private void applyPermanentRicardoRage() {
        if (!this.isRicardoBald()) {
            return;
        }
        this.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 1200, 0, false, true));
    }

    private void captureRicardoBaselines() {
        if (this.ricardoBaseCaptured) {
            return;
        }
        this.ricardoBaseMaxHealth = this.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
        this.ricardoBaseArmor = this.getAttribute(Attributes.ARMOR).getBaseValue();
        this.ricardoBaseToughness = this.getAttribute(Attributes.ARMOR_TOUGHNESS).getBaseValue();
        this.ricardoBaseCaptured = true;
    }

    private void updateRicardoAttributes() {
        this.captureRicardoBaselines();
        double baseSpeed = this.isRicardoVariant() ? 0.45 : 0.3;
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(baseSpeed);
        if (this.isRicardoVariant()) {
            double MAX_HP = 3763.0;
            double ARMOR_NORMAL = 32.0;
            double ARMOR_ENRAGE = 40.0;
            double SPEED_ENRAGE = 0.58;
            double ENRAGE_PCT = 0.25;
            double BERSERK_PCT = 0.1;
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(3763.0);
            double cur = this.getHealth();
            double max = this.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
            if (max <= 0.0) {
                max = 3763.0;
            }
            double pct = max > 0.0 ? cur / max : 1.0;
            boolean enrage = pct <= 0.25;
            boolean berserk = pct <= 0.1;
            double armorNow = 32.0;
            double speedNow = baseSpeed;
            if (enrage) {
                armorNow = 40.0;
                speedNow = 0.58;
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1, false, true));
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0, false, true));
                if (!this.level().isClientSide && this.tickCount % 20 == 0) {
                    ((ServerLevel)this.level()).sendParticles(ParticleTypes.ENCHANTED_HIT, this.getX(), this.getY() + 1.2, this.getZ(), 12, 0.35, 0.6, 0.35, 0.02);
                }
            }
            if (berserk) {
                armorNow = Math.max(armorNow, 44.0);
                speedNow = Math.max(speedNow, 0.62);
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 1, false, true));
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 2, false, true));
            }
            this.getAttribute(Attributes.ARMOR).setBaseValue(armorNow);
            this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(this.ricardoBaseToughness);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speedNow);
            if (this.getHealth() > (float)max) {
                this.setHealth((float)max);
            } else if (this.getHealth() <= 0.0f) {
                this.setHealth((float)max);
            }
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.ricardoBaseMaxHealth);
            this.getAttribute(Attributes.ARMOR).setBaseValue(this.ricardoBaseArmor);
            this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(this.ricardoBaseToughness);
            if (this.getHealth() > (float)this.ricardoBaseMaxHealth) {
                this.setHealth((float)this.ricardoBaseMaxHealth);
            }
        }
    }

    public void setCustomNameTag(String name) {
        boolean wasRicardo = this.isRicardoVariant();
        SRPEntityUtil.setCustomNameTag(this, name);
        System.out.println("[SRP] noglaRicardoVariantEnabled=" + SRPConfigMobs.noglaRicardoVariantEnabled + " name=" + SRPEntityUtil.getCustomNameTag(this));
        if (!this.level().isClientSide) {
            if (!SRPConfigMobs.noglaRicardoVariantEnabled) {
                this.updateRicardoAttributes();
                return;
            }
            boolean isNowRicardo = this.isRicardoVariant();
            this.updateRicardoAttributes();
            if (isNowRicardo && !wasRicardo) {
                this.setHealth((float)this.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 8.0f, 1.0f);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 4.0f, 1.0f);
                ((ServerLevel)this.level()).sendParticles(ParticleTypes.ENCHANTED_HIT, this.getX(), this.getY() + 1.2, this.getZ(), 40, 0.6, 0.8, 0.6, 0.1);
                this.level().broadcastEntityEvent((Entity)this, (byte)77);
            }
        }
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
        this.miniCapA = true;
        if (this.attacking < 20) {
            LivingEntity entitylivingbase;
            this.level().broadcastEntityEvent((Entity)this, (byte)100);
            if (this.attacking == 2) {
                float v = (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.4f + 2.0f;
                this.playSound(this.getHurtSound(this.damageSources().generic()), 4.0f, v);
            }
            if ((entitylivingbase = this.getTarget()) == null || !this.onGround() || this.isInWater() || entitylivingbase.getY() > this.getY() && entitylivingbase.onGround()) {
                this.skillCharge = true;
                this.attacking = 0;
                this.miniCapA = false;
                this.setParasiteStatus(0);
                return;
            }
            if (!entitylivingbase.isAlive()) {
                this.skillCharge = true;
                this.attacking = 0;
                this.miniCapA = false;
                this.setParasiteStatus(0);
                return;
            }
            if (this.attacking <= 19) {
                double dis = this.distanceTo((Entity)entitylivingbase);
                this.setParasiteStatus(3);
                this.getNavigation().stop();
                this.targetX = this.getX() + 15.0 * (entitylivingbase.getX() - this.getX()) / dis;
                this.targetY = this.getY() + 15.0 * (entitylivingbase.getY() - this.getY()) / dis;
                this.targetZ = this.getZ() + 15.0 * (entitylivingbase.getZ() - this.getZ()) / dis;
            }
        }
        if (this.attacking == 20) {
            this.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, 2.5);
        }
        if (this.attacking >= 20) {
            for (LivingEntity mob : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().expandTowards(2.0, 0.0, 2.0))) {
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
        if (this.attacking >= 60 && this.getX() == this.xo && this.getZ() == this.zo) {
            this.attacking = 0;
            this.miniCapA = false;
            this.skillCharge = true;
            this.setParasiteStatus(2);
        }
    }
}

