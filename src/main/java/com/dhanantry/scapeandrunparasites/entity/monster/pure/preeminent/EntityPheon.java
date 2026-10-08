package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvadeDash;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanColony;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileHomming;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class EntityPheon
extends EntityPPreeminent
implements EntityCanColony,
EntityBodyParts,
RangedAttackMob,
EntityCutomAttack,
EntityCanShoot {
    private EntityBody head;
    private EntityBody middle;

    public EntityPheon(EntityType<? extends EntityPheon> type, Level worldIn) {
        super(type, worldIn);
        this.noCulling = true;
        this.type = (byte)63;
        this.borderOrb = -1;
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.head = new EntityBody(this, 2.0f, 7.3f, 1.0f, -2.5f, 0.0f, -1, 1, false, 0.2f);
        this.middle = new EntityBody(this, 1.9f, 3.5f, 1.0f, 0.0f, 3.7f, 1, 2, false, 0.2f);
    }

    @Override
    public int getParasiteIDRegister() {
        return 87;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.15));
        this.goalSelector.addGoal(6, new EntityAIAttackMeleeRangeSwitch(this, 10.0f));
        this.goalSelector.addGoal(2, new EntityAIAttackMeleeStatusAOE(this, 1.0, false, 100.0, 5.0));
        this.goalSelector.addGoal(4, new EntityAIAttackRangedStatus(this, 1.0, 40, 40.0f, false));
        this.goalSelector.addGoal(6, new EntityAIAttackProjectile(this, 60, 10, 3));
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, SRPConfig.preeminentFollow, true, 3));
        this.goalSelector.addGoal(2, new EntityAIEvadeDash(this, 40, 2, 4, 5.0, 100));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPreeminent.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.PHEON_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.PHEON_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.283);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.PHEON_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.preeminentFollow);
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
    }

    public void setCustomNameTag(String name) {
        SRPEntityUtil.setCustomNameTag(this, name);
    }

    public void addTrackingPlayer(ServerPlayer player) {
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
    }

    public void performRangedAttack(LivingEntity target, float distanceFactor) {
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        AABB axisalignedbb = new AABB(entityIn.getX(), entityIn.getY(), entityIn.getZ(), entityIn.getX() + 1.0, entityIn.getY() + 1.0, entityIn.getZ() + 1.0).expandTowards(5.0, 2.0, 5.0);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        if (moblist.size() > 4) {
            axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).expandTowards(5.0, 3.0, 5.0);
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
        if (flag) {
            // empty if block
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
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.MOBSILENCE.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.pheonOrbEffects, mobs);
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

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            switch (this.getRandom().nextInt(1)) {
                case 0: {
                    this.setSkin(1);
                    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(SRPAttributes.PHEON_HEALTH * 0.5);
                    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.PHEON_ATTACK_DAMAGE * 1.5);
                    this.setHealth((float)this.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
                }
            }
        }
        return floo;
    }

    @Override
    public Entity getProj(double accelX, double accelY, double accelZ) {
        this.playSound(SRPSounds.DORPA_RANGE.get(), 2.0f, 1.0f);
        Vec3 vec3d = this.getViewVector(1.0f);
        EntityProjectileHomming entitylargefireball = new EntityProjectileHomming(SRPEntities.HOMMING.get(), this.level(), (LivingEntity)this, (Entity)this.getTarget(), SRPAttributes.ORONCO_ATTACK_DAMAGE);
        Mot.setPosX(entitylargefireball, this.getX() + vec3d.x);
        Mot.setPosY(entitylargefireball, this.getY() + (double)this.getEyeHeight() - 0.2);
        Mot.setPosZ(entitylargefireball, this.getZ() + vec3d.z);
        return entitylargefireball;
    }

    @Override
    public void playProjSound() {
    }

    @Override
    public void skillBreakBlocks() {
        LivingEntity target;
        if (this.getBlockH() == 0.0f) {
            return;
        }
        int blocksbroke = 0;
        int i1 = Mth.floor((double)(this.getY() + 0.1));
        double l1 = this.getX();
        double i2 = this.getZ();
        boolean flag = false;
        int Brangeatm = this.BGrange;
        int offsetT = 0;
        if (this.getTarget() != null && (target = this.getTarget()).distanceToSqr(this.getX(), target.getY(), this.getZ()) < 9.0) {
            if (target.getY() - this.getY() < -1.0) {
                offsetT -= 2;
                if (!this.onGround()) {
                    --offsetT;
                }
            } else if (target.getY() - this.getY() > 2.0) {
                ++offsetT;
                this.BGrange = 0;
            }
        }
        for (int k2 = -1 * this.BGrange; k2 <= 1 * this.BGrange; ++k2) {
            for (int l2 = -1 * this.BGrange; l2 <= 1 * this.BGrange; ++l2) {
                for (int j = 1 + offsetT; j <= 8 + offsetT; ++j) {
                    String name;
                    double i3 = l1 + (double)k2;
                    double k = i1 + j;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = BlockPos.containing(i3, k, l);
                    BlockState iblockstate = this.level().getBlockState(blockpos);
                    Block block = iblockstate.getBlock();
                    float bHard = iblockstate.getDestroySpeed(this.level(), blockpos);
                    if (!(bHard <= this.getBlockH()) || !(bHard >= 0.0f) || block instanceof IMetaName && block != SRPBlocks.ParasiteCanister.get() || block == SRPBlocks.BiomeHeart.get() || block == SRPBlocks.ColonyHeart.get() || block == SRPBlocks.ParasiteRubbleDense.get() || block == SRPBlocks.ParasiteCanisterActive.get() || this.blockException(name = block.builtInRegistryHolder().key().location().toString()) || block == Blocks.AIR || !iblockstate.canEntityDestroy(this.level(), blockpos, this) || !EventHooks.onEntityDestroyBlock((LivingEntity)this, (BlockPos)blockpos, (BlockState)iblockstate)) continue;
                    if (SRPConfig.cystActive) {
                        boolean bl = flag = this.destroyBlockPos(blockpos, false) || flag;
                        if (SRPConfig.doTileDrops) {
                            this.addToBlockInv(BlockIds.stateString(iblockstate));
                        }
                    } else {
                        this.destroyBlockPos(blockpos, SRPConfig.doTileDrops);
                    }
                    ++blocksbroke;
                }
            }
        }
        this.BGrange = Brangeatm;
        this.SkillBGflag = true;
    }

    @Override
    public boolean onlySpawnInside() {
        return false;
    }
}

