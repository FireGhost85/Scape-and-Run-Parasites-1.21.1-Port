package com.dhanantry.scapeandrunparasites.entity.monster.derived;

import com.dhanantry.scapeandrunparasites.client.SRPClientParticles;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.EntityOrbVoid;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIKirinBlink;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDerived;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileKirinSlash;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class EntityKirin
extends EntityPDerived {
    private int voidCool;
    private int voidCoolTotal;
    private int floatBob;
    private final ArrayList<KirinPendingSlash> pendingJudgementCuts = new ArrayList();
    private static final int FLOAT_GROUND_SCAN = 24;
    private static final double FLOAT_HOVER_HEIGHT = 0.35;
    private static final double FLOAT_BOB_AMPLITUDE = 0.06;
    private int noGroundTicks = 0;
    private static final double FLOAT_UP_MAX = 0.16;
    private static final double FLOAT_DOWN_MAX = -0.16;
    private static final EntityDataAccessor<BlockPos> BLINK_POS = SynchedEntityData.defineId(EntityKirin.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> BLINK_TICKS = SynchedEntityData.defineId(EntityKirin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> JUDGEMENT_CUT_CHARGE_TICKS = SynchedEntityData.defineId(EntityKirin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> JUDGEMENT_CUT_AURA_END_TICKS = SynchedEntityData.defineId(EntityKirin.class, EntityDataSerializers.INT);
    private int limit;
    private int border;
    private boolean skillSummon;
    private boolean skillJudgementCut;
    private int judgementCutSkillTicks;
    private boolean judgementCutQueued;

    public EntityKirin(EntityType<? extends EntityKirin> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.goalSelector.removeGoal(this.folow);
        this.voidCool = this.voidCoolTotal = 5;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BLINK_POS, BlockPos.ZERO);
        builder.define(BLINK_TICKS, 0);
        builder.define(JUDGEMENT_CUT_CHARGE_TICKS, 0);
        builder.define(JUDGEMENT_CUT_AURA_END_TICKS, 0);
    }

    public void setBlinkCharge(BlockPos pos, int ticks) {
        if (pos == null) {
            pos = BlockPos.ZERO;
        }
        this.entityData.set(BLINK_POS, pos);
        this.entityData.set(BLINK_TICKS, Math.max(0, ticks));
    }

    public void clearBlinkCharge() {
        this.entityData.set(BLINK_POS, BlockPos.ZERO);
        this.entityData.set(BLINK_TICKS, 0);
    }

    public void setJudgementCutChargeTicks(int ticks) {
        this.entityData.set(JUDGEMENT_CUT_CHARGE_TICKS, Math.max(0, ticks));
    }

    public int getJudgementCutChargeTicks() {
        return (Integer)this.entityData.get(JUDGEMENT_CUT_CHARGE_TICKS);
    }

    public void setJudgementCutAuraEndTicks(int ticks) {
        this.entityData.set(JUDGEMENT_CUT_AURA_END_TICKS, Math.max(0, ticks));
    }

    public int getJudgementCutAuraEndTicks() {
        return (Integer)this.entityData.get(JUDGEMENT_CUT_AURA_END_TICKS);
    }

    public boolean isChargingJudgementCut() {
        return this.getJudgementCutChargeTicks() > 0 || this.getJudgementCutAuraEndTicks() > 0;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.KIRIN_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.KIRIN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.KIRIN_DEATH.get();
    }

    public void tick() {
        BlockPos p;
        int ticks;
        super.tick();
        if (this.level().isClientSide && (ticks = ((Integer)this.entityData.get(BLINK_TICKS)).intValue()) > 0 && (p = (BlockPos)this.entityData.get(BLINK_POS)) != null && !Objects.equals(p, BlockPos.ZERO)) {
            float t = 1.0f - (float)ticks / 60.0f;
            double y = (double)p.getY() + 1.5 + (double)t;
            double cx = (double)p.getX() + 0.5;
            double cz = (double)p.getZ() + 0.5;
            float sizeBig = 6.0f;
            float sizeSmall = 5.5f;
            double base = 0.35;
            double accel = 1.25;
            double spd = base + (double)t * accel;
            float angSmall = (float)((double)this.tickCount * spd % (Math.PI * 2));
            float angBig = (float)((double)this.tickCount * -spd % (Math.PI * 2));
            SRPClientParticles.spawnKirinWarning(this.level(), cx, y, cz, sizeSmall, angSmall, 1);
            SRPClientParticles.spawnKirinWarning(this.level(), cx, y, cz, sizeBig, angBig, 1);
        }
    }

    @Override
    public int getParasiteIDRegister() {
        return 67;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(2, new EntityAIKirinBlink(this));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.0, true, 0.0));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 80, 35, true, 1));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 80, 35, true, 2));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPDerived.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.KIRIN_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.KIRIN_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.24);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.KIRIN_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.KIRIN_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.derivedFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            int auraEndTicks;
            this.updateFloating();
            this.updatePendingJudgementCuts();
            int chargeTicks = this.getJudgementCutChargeTicks();
            if (chargeTicks > 0) {
                this.setJudgementCutChargeTicks(chargeTicks - 1);
                if (chargeTicks - 1 <= 0) {
                    this.setJudgementCutAuraEndTicks(24);
                }
            }
            if ((auraEndTicks = this.getJudgementCutAuraEndTicks()) > 0) {
                this.setJudgementCutAuraEndTicks(auraEndTicks - 1);
            }
        }
        if (this.level().isClientSide) {
            for (int i = 0; i < 4; ++i) {
                this.level().addParticle(ParticleTypes.PORTAL, this.getX() + (this.getRandom().nextDouble() - 0.5) * ((double)this.getBbWidth() * 3.0), this.getY() + this.getRandom().nextDouble() * (double)this.getBbHeight() - 0.25, this.getZ() + (this.getRandom().nextDouble() - 0.5) * ((double)this.getBbWidth() * 3.0), (this.getRandom().nextDouble() - 0.5) * 2.0, -this.getRandom().nextDouble(), (this.getRandom().nextDouble() - 0.5) * 2.0);
            }
            if (this.isChargingJudgementCut()) {
                this.spawnJudgementCutChargeParticles();
            }
        }
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
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 5.7f;
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    protected float getSoundVolume() {
        return 5.0f;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    protected EntityPCosmical getThis() {
        return new EntityKirin(SRPEntities.KIRIN.get(), this.level());
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillSummon;
            }
            case 2: {
                return this.skillJudgementCut;
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
            case 2: {
                this.skillJudgementCut = in;
                if (!in) {
                    this.judgementCutSkillTicks = 0;
                    this.judgementCutQueued = false;
                }
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
            case 2: {
                this.summonJudgementCutsOnly();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void summonJudgementCutsOnly() {
        Mot.setY(this, 0.0);
        Mot.setPosY(this, this.yo);
        this.setParasiteStatus(30);
        this.getNavigation().stop();
        LivingEntity target = this.getTarget();
        if (this.getCloneC() || target == null || !target.isAlive()) {
            this.skillJudgementCut = true;
            this.setParasiteStatus(0);
            this.judgementCutSkillTicks = 0;
            this.judgementCutQueued = false;
            return;
        }
        if (!this.judgementCutQueued) {
            this.spawnJudgementCuts(target);
            this.judgementCutQueued = true;
        }
        ++this.judgementCutSkillTicks;
        if (this.judgementCutSkillTicks > 80) {
            this.skillJudgementCut = true;
            this.setParasiteStatus(0);
            this.judgementCutSkillTicks = 0;
            this.judgementCutQueued = false;
        }
    }

    public void spawnJudgementCuts(LivingEntity target) {
        if (this.level().isClientSide || target == null || target.isRemoved()) {
            return;
        }
        this.level().playSound(null, this.getX(), this.getY() + (double)this.getBbHeight() * 0.55, this.getZ(), SRPSounds.KIRIN_PROJECTILE_CHARGE.get(), SoundSource.HOSTILE, 4.0f, 0.95f + this.getRandom().nextFloat() * 0.08f);
        int chargeDelayTicks = 60;
        this.setJudgementCutChargeTicks(chargeDelayTicks);
        this.setJudgementCutAuraEndTicks(0);
        boolean targetIsPlayer = target instanceof Player;
        int count = 42;
        float damage = targetIsPlayer ? 8.0f : 10.0f;
        for (int i = 0; i < count; ++i) {
            double verticalOffset;
            float heightRoll;
            double sideOffset;
            float closeRoll;
            double passAngle = this.getRandom().nextDouble() * Math.PI * 2.0;
            double dirX = Math.cos(passAngle);
            double dirZ = Math.sin(passAngle);
            double beforeTarget = 22.0 + this.getRandom().nextDouble() * 16.0;
            if (targetIsPlayer) {
                closeRoll = this.getRandom().nextFloat();
                sideOffset = closeRoll < 0.55f ? this.getRandomSignedRange(2.4, 5.2) : (closeRoll < 0.88f ? this.getRandomSignedRange(5.5, 10.0) : this.getRandomSignedRange(10.0, 22.0));
                heightRoll = this.getRandom().nextFloat();
                verticalOffset = heightRoll < 0.62f ? (this.getRandom().nextDouble() - 0.5) * 2.2 : (heightRoll < 0.9f ? (this.getRandom().nextDouble() - 0.5) * 5.0 : (this.getRandom().nextDouble() - 0.5) * 9.0);
            } else {
                closeRoll = this.getRandom().nextFloat();
                sideOffset = closeRoll < 0.7f ? (this.getRandom().nextDouble() - 0.5) * 1.2 : (closeRoll < 0.92f ? (this.getRandom().nextDouble() - 0.5) * 3.0 : this.getRandomSignedRange(4.0, 8.0));
                heightRoll = this.getRandom().nextFloat();
                verticalOffset = heightRoll < 0.75f ? (this.getRandom().nextDouble() - 0.5) * Math.max(1.0, (double)target.getBbHeight() * 0.45) : (heightRoll < 0.94f ? (this.getRandom().nextDouble() - 0.5) * Math.max(2.0, (double)target.getBbHeight() * 0.8) : (this.getRandom().nextDouble() - 0.5) * Math.max(3.0, (double)target.getBbHeight() * 1.2));
            }
            float yaw = (float)(Math.atan2(dirX, dirZ) * 180.0 / Math.PI);
            float pitch = targetIsPlayer ? (this.getRandom().nextFloat() < 0.1f ? -60.0f + this.getRandom().nextFloat() * 120.0f : -14.0f + this.getRandom().nextFloat() * 28.0f) : (this.getRandom().nextFloat() < 0.08f ? -35.0f + this.getRandom().nextFloat() * 70.0f : -8.0f + this.getRandom().nextFloat() * 16.0f);
            float roll = (float)(this.getRandom().nextDouble() * 360.0);
            float length = 110.0f + this.getRandom().nextFloat() * 75.0f;
            int delayTicks = chargeDelayTicks + i + this.getRandom().nextInt(5);
            int growTicks = 4 + this.getRandom().nextInt(8);
            int lifeTicks = 55 + this.getRandom().nextInt(18);
            this.pendingJudgementCuts.add(new KirinPendingSlash(target.getId(), delayTicks, dirX, dirZ, beforeTarget, sideOffset, verticalOffset, yaw, pitch, roll, length, damage, growTicks, lifeTicks));
        }
    }

    private void updatePendingJudgementCuts() {
        if (this.level().isClientSide || this.pendingJudgementCuts.isEmpty()) {
            return;
        }
        Iterator<KirinPendingSlash> iterator = this.pendingJudgementCuts.iterator();
        while (iterator.hasNext()) {
            KirinPendingSlash slash = iterator.next();
            --slash.delay;
            if (slash.delay > 0) continue;
            Entity entity = this.level().getEntity(slash.targetId);
            if (!(entity instanceof LivingEntity) || entity.isRemoved()) {
                iterator.remove();
                continue;
            }
            LivingEntity target = (LivingEntity)entity;
            double cx = target.getX();
            double cy = target.getBoundingBox().minY + (double)target.getBbHeight() * 0.55;
            double cz = target.getZ();
            double sideX = -slash.dirZ;
            double sideZ = slash.dirX;
            double x = cx - slash.dirX * slash.beforeTarget + sideX * slash.sideOffset;
            double y = cy + slash.verticalOffset;
            double z = cz - slash.dirZ * slash.beforeTarget + sideZ * slash.sideOffset;
            EntityProjectileKirinSlash entitySlash = new EntityProjectileKirinSlash(SRPEntities.KIRIN_SLASH.get(), this.level(), (LivingEntity)this, x, y, z, slash.yaw, slash.pitch, slash.roll, slash.length, slash.damage, 0, slash.growTicks, slash.lifeTicks);
            this.level().addFreshEntity((Entity)entitySlash);
            this.level().playSound(null, x, y, z, SRPSounds.KIRIN_PROJECTILE_SUMMON.get(), SoundSource.HOSTILE, 0.85f, 0.9f + this.getRandom().nextFloat() * 0.25f);
            iterator.remove();
        }
    }

    private void spawnJudgementCutChargeParticles() {
        int chargeTicks = this.getJudgementCutChargeTicks();
        float progress = 1.0f - Math.max(0.0f, Math.min((float)chargeTicks / 60.0f, 1.0f));
        int count = 4 + (int)(progress * 6.0f);
        double targetX = this.getX();
        double targetY = this.getY() + (double)this.getBbHeight() * 0.58;
        double targetZ = this.getZ();
        for (int i = 0; i < count; ++i) {
            double angle = this.getRandom().nextDouble() * Math.PI * 2.0;
            double outerRadius = 12.0 + this.getRandom().nextDouble() * 8.0;
            double innerRadius = 2.0 + this.getRandom().nextDouble() * 2.0;
            double radius = outerRadius + (innerRadius - outerRadius) * (double)progress;
            double spawnX = this.getX() + Math.cos(angle) * radius;
            double spawnY = this.getY() + 0.5 + this.getRandom().nextDouble() * ((double)this.getBbHeight() + 3.0);
            double spawnZ = this.getZ() + Math.sin(angle) * radius;
            this.level().addParticle(new DustParticleOptions(new Vector3f(1.0f, 1.0f, 1.0f), 1.0f), spawnX, spawnY, spawnZ, 0.0, 0.0, 0.0);
        }
        if (this.getRandom().nextInt(2) == 0) {
            this.level().addParticle(new DustParticleOptions(new Vector3f(1.0f, 1.0f, 1.0f), 1.0f), targetX + (this.getRandom().nextDouble() - 0.5) * ((double)this.getBbWidth() * 1.6), targetY + (this.getRandom().nextDouble() - 0.5) * 1.4, targetZ + (this.getRandom().nextDouble() - 0.5) * ((double)this.getBbWidth() * 1.6), 0.0, 0.0, 0.0);
        }
    }

    private void summon() {
        Mot.setY(this, 0.0);
        Mot.setPosY(this, this.yo);
        this.setParasiteStatus(30);
        this.getNavigation().stop();
        if (this.tickCount % 20 != 0) {
            return;
        }
        ++this.border;
        this.level().broadcastEntityEvent((Entity)this, (byte)100);
        if (this.getCloneC() || this.getTarget() == null) {
            this.skillSummon = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.limit = 0;
        } else if (!this.getTarget().isAlive()) {
            ++this.border;
        }
        if (this.border == 3) {
            EntityOrbVoid orb = new EntityOrbVoid(SRPEntities.ORBVOID.get(), this.level(), this, 8, 80, true);
            orb.copyPosition((Entity)this);
            double off = 10.0;
            Mot.setPosY(orb, orb.getY() + ((double)this.getBbHeight() + off));
            orb.offsetOrb = off;
            this.level().addFreshEntity((Entity)orb);
            this.playSound(SRPSounds.KIRIN_SHOOT.get(), this.getSoundVolume() * 2.0f, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2f + 1.0f);
            this.spawnJudgementCuts(this.getTarget());
        }
        if (this.border > 12) {
            this.skillSummon = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.limit = 0;
        }
    }

    private double getRandomSignedRange(double min, double max) {
        double value = min + this.getRandom().nextDouble() * (max - min);
        return this.getRandom().nextBoolean() ? value : -value;
    }

    private void updateFloating() {
        this.fallDistance = 0.0f;
        ++this.floatBob;
        double bob = Math.sin((double)(this.tickCount + this.floatBob) * 0.12) * 0.06;
        BlockPos base = BlockPos.containing(this.getX(), this.getY() + 0.1, this.getZ());
        BlockPos ground = null;
        for (int i = 0; i <= 24; ++i) {
            BlockPos p = base.below(i);
            if (!this.level().getBlockState(p).isCollisionShapeFullBlock(this.level(), p)) continue;
            ground = p;
            break;
        }
        if (ground == null) {
            Mot.setY(this, 0.0);
            ++this.noGroundTicks;
            if (!this.level().isClientSide && this.noGroundTicks >= 40 && EntityAIKirinBlink.tryBlinkToNearbyLand(this, 48, 20)) {
                this.noGroundTicks = 0;
            }
            return;
        }
        this.noGroundTicks = 0;
        double targetY = (double)ground.getY() + 1.0 + 0.35 + bob;
        double dy = targetY - this.getY();
        double accel = dy > 0.0 ? dy * 0.12 : dy * 0.06;
        Mot.addY(this, accel);
        if (this.getDeltaMovement().y > 0.16) {
            Mot.setY(this, 0.16);
        }
        if (this.getDeltaMovement().y < -0.16) {
            Mot.setY(this, -0.16);
        }
    }

    private static class KirinPendingSlash {
        int targetId;
        int delay;
        double dirX;
        double dirZ;
        double beforeTarget;
        double sideOffset;
        double verticalOffset;
        float yaw;
        float pitch;
        float roll;
        float length;
        float damage;
        int growTicks;
        int lifeTicks;

        KirinPendingSlash(int targetId, int delay, double dirX, double dirZ, double beforeTarget, double sideOffset, double verticalOffset, float yaw, float pitch, float roll, float length, float damage, int growTicks, int lifeTicks) {
            this.targetId = targetId;
            this.delay = delay;
            this.dirX = dirX;
            this.dirZ = dirZ;
            this.beforeTarget = beforeTarget;
            this.sideOffset = sideOffset;
            this.verticalOffset = verticalOffset;
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
            this.length = length;
            this.damage = damage;
            this.growTicks = growTicks;
            this.lifeTicks = lifeTicks;
        }
    }
}

