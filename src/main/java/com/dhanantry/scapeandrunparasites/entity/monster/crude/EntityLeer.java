package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanPullMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCrude;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectilePullball;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityLeer
extends EntityParasiteBase
implements EntityCanPullMobs {
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityLeer.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(EntityLeer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ENTITY1 = SynchedEntityData.defineId(EntityLeer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ENTITY2 = SynchedEntityData.defineId(EntityLeer.class, EntityDataSerializers.INT);
    private LivingEntity targetedEntity;
    private ArrayList<EntityDataAccessor<Integer>> tracking = new ArrayList();
    private int pulling;
    private EntityBody head;
    private int border;
    private boolean skillpulling;

    public EntityLeer(EntityType<? extends EntityLeer> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.setNoGravity(true);
        this.tracking.add(TARGET_ENTITY);
        this.tracking.add(TARGET_ENTITY1);
        this.tracking.add(TARGET_ENTITY2);
        if (SRPConfigMobs.ombooMaxY != 256) {
            this.goalSelector.addGoal(3, new EntityAIFlightLimits(this, SRPConfigMobs.ombooMaxY, true));
        }
        this.head = new EntityBody(this, 2.6f, 2.5f, 1.0f, 0.0f, 7.5f, -1, 1, false, 0.2f);
        this.noCulling = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 328;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, SRPConfig.pureFollow));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 40, 300, 7, true, 1, true));
        this.goalSelector.addGoal(6, new AIMoveRandom());
        this.goalSelector.addGoal(4, new EntityAIFlightLimits(this, 5, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.LEER_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.LEER_ARMOR);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.LEER_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.LEER_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.preeminentFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.head.tick();
        if (!this.level().isClientSide) {
            if (this.onGround()) {
                this.moveControl.setWantedPosition(this.getX(), this.getY() + 5.0, this.getZ(), 0.5);
            }
            if (this.srpTicks == 10 && (this.level().getBlockState(this.blockPosition().below(1)).getBlock() != Blocks.AIR || this.level().getBlockState(this.blockPosition().below(2)).getBlock() != Blocks.AIR) && this.getTarget() != null) {
                Mot.setY(this, 0.5);
            }
            if (this.hasTargetedEntity()) {
                Mot.setY(this, -0.01);
                this.noActionTime = 0;
                for (LivingEntity entitylivingbase : this.getTargetedEntityVictims()) {
                    double dis = this.distanceToSqr((Entity)entitylivingbase);
                    if (this.hasLineOfSight((Entity)entitylivingbase) && dis > 0.0) {
                        entitylivingbase.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 3, false, false));
                        entitylivingbase.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 3, false, false));
                        this.setParasiteStatus(3);
                        ++this.pulling;
                        if (this.pulling > 600) {
                            this.setParasiteStatus(2);
                            this.setTargetedEntity(0, entitylivingbase);
                        }
                        if (!(dis < 4.0)) continue;
                        this.doHurtTarget((Entity)entitylivingbase);
                        continue;
                    }
                    this.setParasiteStatus(2);
                    this.setTargetedEntity(0, entitylivingbase);
                }
            } else {
                this.setParasiteStatus(0);
            }
        } else if (this.getRandom().nextInt(30) == 0) {
            for (int i = 0; i <= 20; ++i) {
                if (i % 5 != 0) continue;
                this.spawnParticlesGoreMouth(SRPEnumParticle.GSPLASH, 0, -1, -1, 0.2, 0.0);
            }
        }
        for (LivingEntity entitylivingbase : this.getTargetedEntityVictims()) {
            if (!(this.distanceToSqr((Entity)entitylivingbase) > 0.0)) continue;
            entitylivingbase.stopRiding();
            double str = 0.4;
            double deltaX = this.getX() - entitylivingbase.getX();
            double deltaY = this.getY() - entitylivingbase.getY();
            double deltaZ = this.getZ() - entitylivingbase.getZ();
            str = 0.1;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance == 0.0) {
                return;
            }
            Mot.addX(entitylivingbase, (deltaX /= distance) * str);
            Mot.addY(entitylivingbase, (deltaY /= distance) * str);
            Mot.addZ(entitylivingbase, (deltaZ /= distance) * str);
        }
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);
    }

    @Override
    public void setTargetedEntity(int entityId) {
        this.pulling = 0;
        for (EntityDataAccessor<Integer> mob : this.tracking) {
            if ((Integer)this.entityData.get(mob) != entityId) continue;
            return;
        }
        for (EntityDataAccessor<Integer> mob : this.tracking) {
            if ((Integer)this.entityData.get(mob) != 0) continue;
            this.entityData.set(mob, entityId);
            return;
        }
    }

    public void setTargetedEntity(int entityId, LivingEntity sameEntity) {
        this.pulling = 0;
        for (EntityDataAccessor<Integer> mob : this.tracking) {
            if (((Integer)this.entityData.get(mob)).intValue() != sameEntity.getId()) continue;
            this.entityData.set(mob, entityId);
        }
    }

    public void updateTargets() {
        for (EntityDataAccessor<Integer> mob : this.tracking) {
            if ((Integer)this.entityData.get(mob) == 0) continue;
            Entity entity = this.level().getEntity(((Integer)this.entityData.get(mob)).intValue());
            if (entity == null) {
                this.entityData.set(mob, 0);
                continue;
            }
            if (((LivingEntity)entity).isAlive()) continue;
            this.entityData.set(mob, 0);
        }
    }

    @Override
    public boolean hasTargetedEntity() {
        this.updateTargets();
        for (EntityDataAccessor<Integer> mob : this.tracking) {
            if ((Integer)this.entityData.get(mob) == 0) continue;
            return true;
        }
        return false;
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        if (this.level().isClientSide) {
            return;
        }
        if (this.killcount >= 0.0) {
            if (this.getSkin() != 120) {
                this.killcount += 1.0;
            } else if (this.getRandom().nextInt(3) == 0) {
                this.killcount += 1.0;
            }
        }
        if (SRPConfigSystems.useEvolution) {
            if (this.hasEffect(SRPPotions.PIVOT_E)) {
                int amp = this.getEffect(SRPPotions.PIVOT_E).getAmplifier();
                SRPSaveData.get(this.level()).setTotalKills(DimKeys.of(this.level()), SRPConfigSystems.valueKill * (SRPConfigSystems.pivotPointMultiplier * (amp + 1)), true, this.level(), true, 38);
            } else {
                SRPSaveData.get(this.level()).setTotalKills(DimKeys.of(this.level()), SRPConfigSystems.valueKill, true, this.level(), true, 39);
            }
        }
        if (SRPConfigWorld.originActivated) {
            ParasiteEventWorld.setOriginInHealth(this.level(), this.blockPosition(), (int)((double)entityLivingIn.getMaxHealth() * SRPConfigWorld.originKillMultiplier), true);
            if (SRPConfigWorld.originTriggerKill > this.level().random.nextDouble()) {
                ParasiteEventWorld.placeOriginInWorld(this.level(), BlockPos.containing(this.getX(), this.getY(), this.getZ()), SRPConfigWorld.originHealth, SRPConfigWorld.originRadius);
            }
        }
        if (this.hasEffect(SRPPotions.PARATE_E)) {
            int bonuss = this.getEffect(SRPPotions.PARATE_E).getAmplifier() + 1;
            if (entityLivingIn.getAttribute(Attributes.MAX_HEALTH) != null) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + entityLivingIn.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * (SRPConfigSystems.parateMuch * (double)bonuss));
            }
            if (entityLivingIn.getAttribute(Attributes.ARMOR) != null) {
                this.getAttribute(Attributes.ARMOR).setBaseValue(this.getAttribute(Attributes.ARMOR).getBaseValue() + entityLivingIn.getAttribute(Attributes.ARMOR).getBaseValue() * (SRPConfigSystems.parateMuch * (double)bonuss));
            }
            if (entityLivingIn.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() + entityLivingIn.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * (SRPConfigSystems.parateMuch * (double)bonuss));
            }
        }
        this.heal(entityLivingIn.getHealth() * this.geneMobHealing);
        this.setWait(10);
        List<? extends Entity> serverList = SRPEntityUtil.allEntities(this.level());
        int parasiteCount = 0;
        for (Entity value : serverList) {
            if (!(value instanceof EntityParasiteBase)) continue;
            ++parasiteCount;
        }
        if (parasiteCount >= SRPConfig.worldMobCap) {
            return;
        }
        entityLivingIn.discard();
        EntityPCrude mob = new EntityInhooM(SRPEntities.INCOMPLETEFORM_MEDIUM.get(), this.level());
        if (this.getRandom().nextBoolean()) {
            mob = new EntityInhooS(SRPEntities.INCOMPLETEFORM_SMALL.get(), this.level());
        }
        mob.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        mob.finalizeSpawn((ServerLevel) mob.level(), this.level().getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        mob.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 60, 5));
        this.level().addFreshEntity((Entity)mob);
        mob.particleStatus((byte)7);
    }

    public ArrayList<LivingEntity> getTargetedEntityVictims() {
        if (!this.hasTargetedEntity()) {
            return new ArrayList<LivingEntity>();
        }
        ArrayList<LivingEntity> mobs = new ArrayList<LivingEntity>();
        for (EntityDataAccessor<Integer> mob : this.tracking) {
            Entity entity;
            if ((Integer)this.entityData.get(mob) == 0 || (entity = this.level().getEntity(((Integer)this.entityData.get(mob)).intValue())) == null) continue;
            mobs.add((LivingEntity)entity);
        }
        return mobs;
    }

    @Override
    public LivingEntity getTargetedEntity() {
        if (!this.hasTargetedEntity()) {
            return null;
        }
        if (this.level().isClientSide) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            }
            Entity entity = this.level().getEntity(((Integer)this.entityData.get(TARGET_ENTITY)).intValue());
            if (entity instanceof LivingEntity) {
                this.targetedEntity = (LivingEntity)entity;
                return this.targetedEntity;
            }
            return null;
        }
        return this.getTarget();
    }

    @Override
    public void setPStatus(int in) {
        this.setParasiteStatus(in);
    }

    @Override
    public void setPullingMobEffects(LivingEntity mob) {
        mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 3, false, false));
    }

    @Override
    public int getAcceleration() {
        return 2;
    }

    @Override
    public boolean checkAttackTarget(LivingEntity check) {
        if (check instanceof EntityParasiteBase) {
            return false;
        }
        return !(check instanceof Player) || !((Player)check).isCreative();
    }

    public void push(Entity entityIn) {
        for (LivingEntity entitylivingbase : this.getTargetedEntityVictims()) {
            if (entitylivingbase != entityIn) continue;
            return;
        }
        super.push(entityIn);
    }

    public void notifyDataManagerChange(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ENTITY.equals(key)) {
            this.targetedEntity = null;
        }
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.head.discard();
        }
        super.discard();
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.5f;
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        super.move(type, movement);
        this.checkInsideBlocks();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VEX_FLAGS, (byte) (0));
        builder.define(TARGET_ENTITY, 0);
        builder.define(TARGET_ENTITY1, 0);
        builder.define(TARGET_ENTITY2, 0);
    }

    private boolean getVexFlag(int mask) {
        byte i = (Byte)this.entityData.get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = ((Byte)this.entityData.get(VEX_FLAGS)).byteValue();
        i = value ? (i |= mask) : (i &= ~mask);
        this.entityData.set(VEX_FLAGS, (byte) (((byte)(i & 0xFF))));
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillpulling;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillpulling = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.pullingE();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void pullingE() {
        if (this.hasTargetedEntity()) {
            this.skillpulling = true;
            this.border = 0;
            return;
        }
        this.setParasiteStatus(11);
        this.getNavigation().stop();
        if (this.border == 0) {
            // empty if block
        }
        if (this.border <= 2) {
            // empty if block
        }
        if (this.tickCount % 20 != 0) {
            return;
        }
        ++this.border;
        if (this.getTarget() == null) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        if (!this.hasLineOfSight((Entity)this.getTarget())) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        this.shootEntityRed(this.getTarget());
        int count = 2;
        AABB axisalignedbb = new AABB(this.getTarget().blockPosition().below(2)).expandTowards(18.0, 8.0, 18.0);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            if (count < 0 || mob == this || !mob.isAlive() || mob instanceof EntityParasiteBase) continue;
            this.shootEntityRed(mob);
            --count;
        }
        if (this.border > 5) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
        }
    }

    public void shootEntityRed(LivingEntity entitylivingbase) {
        Vec3 vec3d = this.getViewVector(1.0f);
        double d2 = entitylivingbase.getX() - (this.getX() + vec3d.x);
        double d3 = entitylivingbase.getBoundingBox().maxY + (double)(entitylivingbase.getBbHeight() + 0.8f) - (0.5 + this.getY() + (double)(this.getBbHeight() / 1.0f));
        double d4 = entitylivingbase.getZ() - (this.getZ() + vec3d.z);
        EntityProjectilePullball entitylargefireball = new EntityProjectilePullball(SRPEntities.PULLINGBALL.get(), this.level(), this, d2, d3, d4);
        Mot.setPosX(entitylargefireball, this.getX() + vec3d.x);
        Mot.setPosY(entitylargefireball, this.getY() + (double)this.getEyeHeight() - 0.2);
        Mot.setPosZ(entitylargefireball, this.getZ() + vec3d.z);
        this.level().addFreshEntity((Entity)entitylargefireball);
    }

    @Override
    public void resetPullSkill() {
        this.skillpulling = true;
        this.border = 0;
    }

    class AIChargeAttack
    extends Goal {
        public AIChargeAttack() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (EntityLeer.this.getTarget() != null && EntityLeer.this.getRandom().nextInt(5) == 0) {
                return EntityLeer.this.distanceToSqr((Entity)EntityLeer.this.getTarget()) > 4.0;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return EntityLeer.this.getMoveControl().hasWanted() && EntityLeer.this.isCharging() && EntityLeer.this.getTarget() != null && EntityLeer.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity entitylivingbase = EntityLeer.this.getTarget();
            Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
            EntityLeer.this.moveControl.setWantedPosition(vec3d.x, entitylivingbase.getY() + 20.0, vec3d.z, 0.2);
            EntityLeer.this.setCharging(true);
        }

        public void stop() {
            EntityLeer.this.setCharging(false);
        }

        public void tick() {
            LivingEntity entitylivingbase = EntityLeer.this.getTarget();
            if (entitylivingbase != null && entitylivingbase.isAlive()) {
                if (EntityLeer.this.getBoundingBox().intersects(entitylivingbase.getBoundingBox())) {
                    EntityLeer.this.doHurtTarget((Entity)entitylivingbase);
                    EntityLeer.this.setCharging(false);
                } else {
                    double d0 = EntityLeer.this.distanceToSqr((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                        EntityLeer.this.moveControl.setWantedPosition(vec3d.x, entitylivingbase.getY() + 20.0, vec3d.z, 1.0);
                    } else {
                        Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                        EntityLeer.this.moveControl.setWantedPosition(vec3d.x, entitylivingbase.getY() + 20.0, vec3d.z, 1.1);
                    }
                }
            }
        }
    }

    class AIMoveRandom
    extends Goal {
        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !EntityLeer.this.getMoveControl().hasWanted() && EntityLeer.this.getRandom().nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityLeer.this.blockPosition();
            int flag = 1;
            double speed = 0.1;
            if (EntityLeer.this.getTarget() != null) {
                if (EntityLeer.this.distanceToSqr((Entity)EntityLeer.this.getTarget()) > 100.0) {
                    blockpos = EntityLeer.this.getTarget().blockPosition();
                    flag = 2;
                    speed += 0.05;
                } else if (EntityLeer.this.distanceToSqr((Entity)EntityLeer.this.getTarget()) < 36.0) {
                    blockpos = EntityLeer.this.getTarget().blockPosition();
                    flag = 3;
                    speed += 0.05;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityLeer.this.getRandom().nextInt(15) - 7, EntityLeer.this.getRandom().nextInt(11) - 5, EntityLeer.this.getRandom().nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.offset(EntityLeer.this.getRandom().nextInt(6) - 2, EntityLeer.this.getRandom().nextInt(7) - 2, EntityLeer.this.getRandom().nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.offset(EntityLeer.this.getRandom().nextInt(4) + 3, EntityLeer.this.getRandom().nextInt(5) + 4, EntityLeer.this.getRandom().nextInt(4) + 3);
                }
                if (!EntityLeer.this.level().isEmptyBlock(blockpos1)) continue;
                EntityLeer.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityLeer.this.getTarget() != null) break;
                EntityLeer.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityLeer vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityLeer.this.getX();
                double d1 = this.getWantedY() - EntityLeer.this.getY();
                double d2 = this.getWantedZ() - EntityLeer.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityLeer.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityLeer.this, 0.5);
                    Mot.mulY(EntityLeer.this, 0.5);
                    Mot.mulZ(EntityLeer.this, 0.5);
                } else {
                    Mot.addX(EntityLeer.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityLeer.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityLeer.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityLeer.this.getTarget() == null) {
                        EntityLeer.this.setYRot(-((float)Mth.atan2((double)EntityLeer.this.getDeltaMovement().x, (double)EntityLeer.this.getDeltaMovement().z)) * 57.295776f);
        EntityLeer.this.yBodyRot = EntityLeer.this.getYRot();
                    } else {
                        double d4 = EntityLeer.this.getTarget().getX() - EntityLeer.this.getX();
                        double d5 = EntityLeer.this.getTarget().getZ() - EntityLeer.this.getZ();
                        EntityLeer.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityLeer.this.yBodyRot = EntityLeer.this.getYRot();
                    }
                }
            }
        }
    }
}

