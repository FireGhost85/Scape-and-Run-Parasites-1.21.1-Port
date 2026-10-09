package com.dhanantry.scapeandrunparasites.entity.monster.infected.head;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public class EntityInfEndermanHead
extends EntityPInfected {
    private static final EntityDataAccessor<Boolean> SCREAMING = SynchedEntityData.defineId(EntityInfEndermanHead.class, EntityDataSerializers.BOOLEAN);
        private static final AttributeModifier ATTACKING_SPEED_BOOST = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "attacking_speed_boost"), (double)0.15f, AttributeModifier.Operation.ADD_VALUE);
    private int lastCreepySound;
    private int targetChangeTime;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int ally;
    private EntityParasiteBase toTele;

    public EntityInfEndermanHead(EntityType<? extends EntityInfEndermanHead> type, Level worldIn) {
        super(type, worldIn);
        this.killcount = -10.0;
        this.attackSpeedT = 15;
    }

    @Override
    public int getParasiteIDRegister() {
        return 69;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infendermanCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EntityAISkill(this, 40, 100, 3, true, 14));
        this.setskillLeapValues(0.7f, 2.5, 0);
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal((Mob)this, 0.4f));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, -1.0));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal((PathfinderMob)this, EntityInhooM.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPInfected.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.INFENDERMAN_HEADHEALTH);
        builder.add(Attributes.MOVEMENT_SPEED, 0.4);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.INFENDERMAN_HEADDAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, 32.0);
        return builder;
    }

    @Override
    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        super.setTarget(entitylivingbaseIn);
        AttributeInstance iattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (entitylivingbaseIn == null) {
            this.targetChangeTime = 0;
            this.entityData.set(SCREAMING, false);
            iattributeinstance.removeModifier(ATTACKING_SPEED_BOOST.id());
        } else {
            this.targetChangeTime = this.tickCount;
            this.entityData.set(SCREAMING, true);
            if (!iattributeinstance.hasModifier(ATTACKING_SPEED_BOOST.id())) {
                iattributeinstance.addTransientModifier(ATTACKING_SPEED_BOOST);
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SCREAMING, false);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(ParticleTypes.PORTAL, this.getX() + (this.getRandom().nextDouble() - 0.5) * (double)this.getBbWidth(), this.getY() + this.getRandom().nextDouble() * (double)this.getBbHeight() - 0.25, this.getZ() + (this.getRandom().nextDouble() - 0.5) * (double)this.getBbWidth(), (this.getRandom().nextDouble() - 0.5) * 2.0, -this.getRandom().nextDouble(), (this.getRandom().nextDouble() - 0.5) * 2.0);
            }
        } else {
            if (this.getTarget() != null && this.tickCount % 20 == 0 && this.distanceToSqr((Entity)this.getTarget()) > 4.0 && this.getRandom().nextInt(SRPConfigMobs.infendermantelefreq * 2) == 0 && !this.teleportAllies()) {
                this.teleportRandomly();
            }
            if (SRPConfigSystems.disloGiveBodies && this.isAlive() && this.srpTicks == 10 && SRPSaveData.get(this.level()).getCurrentCode(DimKeys.of(this.level()), 20) >= 1) {
                ParasiteEventEntity.spawnNext(this, new EntityInfEnderman(SRPEntities.SIM_ENDERMAN.get(), this.level()), true, false);
                return;
            }
        }
        if (this.ally > 0 && !this.level().isClientSide) {
            ++this.ally;
            this.teleportAlly();
        }
        this.jumping = false;
    }

    protected boolean teleportAllies() {
        if (this.ally > 0 || !SRPConfigMobs.infendermanteleally) {
            return false;
        }
        LivingEntity target = this.getTarget();
        if (target == null) {
            return false;
        }
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(64.0);
        List<? extends EntityParasiteBase> moblist = this.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            if (!(mob instanceof EntityMudo) || mob.getTarget() != null || !this.teleportToEntity((Entity)mob, 1.0)) continue;
            this.setCoordTarget(target.getX(), target.getY(), target.getZ());
            this.toTele = mob;
            this.setWorkTask(false);
            this.ally = 1;
            return true;
        }
        for (EntityParasiteBase mob : moblist) {
            if (!(mob instanceof EntityPInfected) || mob.getBbWidth() > 0.7f || mob.getBbHeight() > 0.9f || mob == this || mob.getTarget() != null || mob.getParasiteType() > 15 || mob instanceof EntityCanMelt && ((EntityCanMelt)(mob)).isMelting() || !this.teleportToEntity((Entity)mob, 1.0)) continue;
            this.setCoordTarget(target.getX(), target.getY(), target.getZ());
            this.toTele = mob;
            this.setWorkTask(false);
            this.ally = 1;
            return true;
        }
        return false;
    }

    private void teleportAlly() {
        block8: {
            block7: {
                LivingEntity target;
                if (this.ally < 8) {
                    return;
                }
                if (this.toTele == null) break block7;
                if (!this.toTele.isAlive()) {
                    this.toTele = null;
                    this.ally = 0;
                    this.setWorkTask(true);
                    boolean flag2 = false;
                    for (int lag2 = 10; !flag2 && lag2 > 0; --lag2) {
                        flag2 = this.teleportToPos(this.targetX, this.targetY, this.targetZ, 8.0);
                    }
                    return;
                }
                boolean flag1 = false;
                for (int lag1 = 10; !flag1 && lag1 > 0 && !(flag1 = this.teleportToPos(this.targetX, this.targetY, this.targetZ, 8.0)); --lag1) {
                }
                if (!flag1) break block8;
                boolean flag2 = false;
                int lag2 = 12;
                this.toTele.copyPosition((Entity)this);
                if (this.toTele.getParasiteIDRegister() != 59 && this.toTele.getParasiteIDRegister() != 69) {
                    this.toTele.hurt(this.damageSources().fall(), SRPConfigMobs.infendermanTeleDamage);
                }
                if (this.isOnFire() && this.getRandom().nextBoolean()) {
                    this.toTele.igniteForSeconds(8);
                }
                this.level().playSound((Player)null, this.toTele.xo, this.toTele.yo, this.toTele.zo, SRPSounds.INFECTEDENDERMAN_PORTAL.get(), this.getSoundSource(), 1.0f, 1.0f);
                flag2 = true;
                if (!flag2 || (target = this.getTarget()) == null || !target.isAlive()) break block8;
                this.toTele.setTarget(target);
                break block8;
            }
            boolean flag2 = false;
            for (int lag2 = 10; !flag2 && lag2 > 0; --lag2) {
                flag2 = this.teleportToPos(this.targetX, this.targetY, this.targetZ, 8.0);
            }
        }
        this.setWorkTask(true);
        this.ally = 0;
        this.toTele = null;
    }

    protected boolean teleportRandomly() {
        double d0 = this.getX() + (this.getRandom().nextDouble() - 0.5) * 64.0;
        double d1 = this.getY() + (double)(this.getRandom().nextInt(64) - 32);
        double d2 = this.getZ() + (this.getRandom().nextDouble() - 0.5) * 64.0;
        return this.teleportToPos(d0, d1, d2);
    }

    protected boolean teleportToEntity(Entity in, double dis) {
        double d1 = in.getX() + (this.getRandom().nextDouble() - 0.5) * dis;
        double d2 = in.getY() + (double)(this.getRandom().nextInt(16) - 8) * dis;
        double d3 = in.getZ() + (this.getRandom().nextDouble() - 0.5) * dis;
        return this.teleportToPos(d1, d2, d3);
    }

    protected boolean teleportToPos(double x, double y, double z, double dis) {
        double d1 = x + (this.getRandom().nextDouble() - 0.5) * dis;
        double d2 = y + (double)(this.getRandom().nextInt(16) - 8) * dis;
        double d3 = z + (this.getRandom().nextDouble() - 0.5) * dis;
        return this.teleportToPos(d1, d2, d3);
    }

    protected boolean teleportEntityTo(EntityParasiteBase in, double x, double y, double z, double dis) {
        double d1 = x + (this.getRandom().nextDouble() - 0.5) * dis;
        double d2 = y + (double)(this.getRandom().nextInt(16) - 8) * dis;
        double d3 = z + (this.getRandom().nextDouble() - 0.5) * dis;
        return this.teleportTo(in, d1, d2, d3);
    }

    private boolean teleportToPos(double x, double y, double z) {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity((LivingEntity)this, x, y, z);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return false;
        }
        boolean flag = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
        if (flag) {
            this.level().playSound((Player)null, this.xo, this.yo, this.zo, SRPSounds.INFECTEDENDERMAN_PORTAL.get(), this.getSoundSource(), 1.0f, 1.0f);
            this.playSound(SRPSounds.INFECTEDENDERMAN_PORTAL.get(), 1.0f, 1.0f);
        }
        return flag;
    }


    private boolean teleportTo(EntityParasiteBase in, double x, double y, double z) {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity((LivingEntity)this, x, y, z);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return false;
        }
        boolean flag = in.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
        if (flag) {
            in.level().playSound((Player)null, in.xo, in.yo, in.zo, SoundEvents.ENDERMAN_TELEPORT, in.getSoundSource(), 1.0f, 1.0f);
            in.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
        return flag;
    }

    private void setCoordTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source.getDirectEntity() != null && source.getDirectEntity() != source.getEntity()) { // EntityDamageSourceIndirect: arrows, fireballs, thrown potions
            for (int i = 0; i < 64; ++i) {
                if (!this.teleportRandomly()) continue;
                return true;
            }
            return false;
        }
        boolean flag = super.hurt(source, amount);
        if (source.is(DamageTypeTags.BYPASSES_ARMOR) && this.getRandom().nextInt(SRPConfigMobs.infendermantelefreq * 2) == 0 && !this.teleportAllies()) {
            this.teleportRandomly();
        }
        return flag;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        if (entityIn instanceof EntityInhooM && entityIn.isAlive() && this.isAlive()) {
            ParasiteEventEntity.spawnNext(this, new EntityInfEnderman(SRPEntities.SIM_ENDERMAN.get(), this.level()), true, false);
            ((EntityParasiteBase)entityIn).particleStatus((byte)7);
            entityIn.discard();
            return true;
        }
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            if (this.getRandom().nextDouble() < (double)SRPConfig.infectedBleedingChance && entityIn instanceof LivingEntity) {
                SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 100, 0);
            }
            if (this.getRandom().nextInt(SRPConfigMobs.infendermantelefreq * 2) == 0 && !this.teleportAllies()) {
                this.teleportRandomly();
            }
        }
        return flag;
    }

    public void setInWeb() {
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.8f;
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        if (type == EntityType.PLAYER) {
            return true;
        }
        String name = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        if (name.contains("srparasites") && type != SRPEntities.INCOMPLETEFORM_MEDIUM.get()) {
            return false;
        }
        return !SRPConfig.mobAttackingFull || !ParasiteEventEntity.checkName(name, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
    }

    public boolean isScreaming() {
        return (Boolean)this.entityData.get(SCREAMING);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        super.causeFallDamage(distance, damageMultiplier * 0.3f, source);
        return false;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), this.getVoicePitch());
    }

    protected SoundEvent getStepSound() {
        return SRPSounds.SMALL_STEPS.get();
    }
}

