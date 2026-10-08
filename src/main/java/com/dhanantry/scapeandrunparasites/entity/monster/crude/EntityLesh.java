package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIParasiteFollow;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class EntityLesh
extends EntityParasiteBase {
    private int noLegs;
    private int noTimes;
    private float tSize;
    private float aSize;
    private int delay;
    private boolean st1;
    private float tSpeed;
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(EntityLesh.class, EntityDataSerializers.FLOAT);

    public EntityLesh(EntityType<? extends EntityLesh> type, Level worldIn) {
        super(type, worldIn);
        this.goalSelector.removeGoal(this.folow);
        this.noLegs = (1 + this.getRandom().nextInt(2)) * 2;
        this.noTimes = 1;
        this.st1 = false;
        this.tSize = 1.0f;
        this.aSize = 1.0f;
        this.fuseTime = 70;
        this.killcount = -10.0;
        this.type = (byte)100;
    }

    @Override
    public int getParasiteIDRegister() {
        return 23;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal((PathfinderMob)this, 1.0, false));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIParasiteFollow(this, 1.3, 10.0, 2.0, false));
        this.goalSelector.addGoal(1, new EntityAILeshCombine(this, 1.1));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal((PathfinderMob)this, EntityLesh.class, false, false));
        this.goalSelector.addGoal(3, new AvoidEntityGoal((PathfinderMob)this, LivingEntity.class, (Predicate)new Predicate<LivingEntity>(){

            public boolean test(@Nullable LivingEntity entity) {
                return !(entity instanceof WaterAnimal) && !(entity instanceof Creeper) && !(entity instanceof EntityParasiteBase) && !(entity instanceof Animal);
            }
        }, 8.0f, 1.0, 1.0, (Predicate<LivingEntity>)(e -> true)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SIZE, (float) (Float.valueOf(this.tSize)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, 15.0);
        builder.add(Attributes.ARMOR, 0.0);
        builder.add(Attributes.MOVEMENT_SPEED, 0.23);
        builder.add(Attributes.ATTACK_DAMAGE, 0.0);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.0);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.adaptedFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getTarget() != null && this.tickCount % 20 == 0) {
            if (!this.getTarget().isAlive()) {
                this.setTarget(null);
            }
            if (this.getTarget() instanceof EntityLesh) {
                EntityLesh meh = (EntityLesh)this.getTarget();
                if (this.getTimes() + meh.getTimes() > 4) {
                    this.setTarget(null);
                }
            }
        }
        if (this.gettSize() > this.getaSize()) {
            this.setaSize(0.01f);
            this.setSize(0.7f + (this.getaSize() - 1.0f), 0.5f + (this.getaSize() - 1.0f));
        }
        if (this.getTimes() >= 4 && this.srpTicks == 10 || this.tickCount > 800 && this.getTimes() > 1) {
            ++this.delay;
            this.st1 = true;
        }
        if (this.delay >= 4 && this.st1) {
            this.setSelfeState(1);
        }
        if (!this.level().isClientSide) {
            if (SRPConfigSystems.disloGiveBodies && this.isAlive() && this.srpTicks == 10 && SRPSaveData.get(this.level()).getCurrentCode(DimKeys.of(this.level()), 20) >= 1) {
                ParasiteEventEntity.spawnNext(this, ParasiteEventEntity.getRandomPrimitive(this.level()), true, false);
                return;
            }
            if (!this.isRemoved() && this.getHealth() > 0.0f && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + 0.007f);
            }
        }
    }

    public void tick() {
        if (this.isAlive()) {
            this.lastActiveTime = this.timeSinceIgnited;
            this.dyingBurst(false, 2);
        }
        super.tick();
    }

    @Override
    protected void dyingBurst(boolean fromDeath, int value) {
        int i = this.getSelfeState();
        if (i > 0 && this.timeSinceIgnited == 0) {
            this.playSound(SRPSounds.FLESH_GROW.get(), 10.0f, 1.0f);
        }
        this.timeSinceIgnited += i * value;
        if (this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
        }
        if (this.timeSinceIgnited >= this.fuseTime) {
            this.timeSinceIgnited = this.fuseTime;
            this.selfExplode();
        }
    }

    @Override
    protected void selfExplode() {
        if (!this.level().isClientSide) {
            this.dead = true;
            this.discard();
            this.spawnPrimitive(this, this.noLegs);
            this.playSound(SRPSounds.FLESH_PRIMITIVE.get(), 1.0f, 1.0f);
        } else {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
        }
    }

    public int getLegs() {
        return this.noLegs;
    }

    public void setLegs(int i, boolean plus) {
        if (plus) {
            this.noLegs += i;
            return;
        }
        this.noLegs = i;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        if (!(entityIn instanceof EntityLesh)) {
            return true;
        }
        ++this.delay;
        if (this.delay >= 3) {
            EntityLesh meh = (EntityLesh)entityIn;
            if (this.getaSize() >= meh.getaSize()) {
                if (SRPEntityUtil.getCustomNameTag(this) == null && SRPEntityUtil.getCustomNameTag(meh) != null) {
                    SRPEntityUtil.setCustomNameTag(this, SRPEntityUtil.getCustomNameTag(meh));
                }
                this.setLegs(meh.getLegs(), true);
                this.setTimes(meh.getTimes());
                meh.particleStatus((byte)6);
                meh.discard();
                this.playSound(SRPSounds.FLESH_EAT.get(), 1.0f, 1.0f);
                this.settSize(0.3f);
                this.resetDelay();
                this.settSpeed((float)(this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() - 0.01));
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)this.tSpeed);
                this.particleStatus((byte)5);
                this.playSound(SRPSounds.FLESH_GROW.get(), 10.0f, 1.0f);
            }
        }
        return true;
    }

    public int getTimes() {
        return this.noTimes;
    }

    public void setTimes(int in) {
        this.noTimes += in;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.3f;
    }

    public float gettSize() {
        return ((Float)this.entityData.get(SIZE)).floatValue();
    }

    public void settSize(float in) {
        this.tSize += in;
        this.entityData.set(SIZE, (float) (Float.valueOf(this.tSize)));
    }

    public void settSpeed(float in) {
        this.tSpeed = in;
    }

    public float getaSize() {
        return this.aSize;
    }

    public void setaSize(float in) {
        this.aSize += in;
    }

    public void resetDelay() {
        this.delay = 0;
    }

    protected SoundEvent getAmbientSound() {
        return SRPSounds.FLESH_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.FLESH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.FLESH_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.LITE_FLESH_SLIDE.get(), 0.3f, 1.0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("leshtotalsize", this.tSize);
        compound.putFloat("leshactualsize", this.aSize);
        compound.putInt("leshlegs", this.noLegs);
        compound.putInt("leshtimes", this.noTimes);
        compound.putFloat("leshspeed", this.tSpeed);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("leshtotalsize", 99)) {
            this.tSize = compound.getFloat("leshtotalsize");
            this.entityData.set(SIZE, (float) (Float.valueOf(compound.getFloat("leshtotalsize"))));
        }
        if (compound.contains("leshlegs", 99)) {
            this.noLegs = compound.getInt("leshlegs");
        }
        if (compound.contains("leshtimes", 99)) {
            this.noTimes = compound.getInt("leshtimes");
        }
        if (compound.contains("leshspeed", 99)) {
            this.tSpeed = compound.getFloat("leshspeed");
        }
    }

    public float getSelfeFlashIntensityS() {
        return this.aSize;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.tSize += 0.3f;
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void spawnPrimitive(EntityLesh host, int code) {
        host.discard();
        this.merge(this, code, SRPEntityUtil.getCustomNameTag(this));
    }

    private boolean merge(EntityParasiteBase entityin, int code, String name) {
        if (SRPConfigSystems.mergeRandom) {
            entityin.particleStatus((byte)7);
            RandomSource rand = RandomSource.create();
            int index = rand.nextInt(SRPConfigSystems.mergeMobTable.length);
            boolean flag = true;
            int limit = 0;
            while (flag) {
                if (index >= SRPConfigSystems.mergeMobTable.length) {
                    index = 0;
                    ++limit;
                }
                if (limit == 2) {
                    return false;
                }
                if (SRPConfigSystems.mergeMobTable[index] != null) {
                    String[] entityC = SRPConfigSystems.mergeMobTable[index].split(";");
                    ParasiteSummon.spawnM(entityin, new String[]{entityC[0] + ";1;1"}, 7, true, name);
                    flag = false;
                    if (SRPConfigSystems.useEvolution) {
                        SRPWorldData data = SRPWorldData.get(entityin.level());
                        SRPSaveData.get(entityin.level()).setTotalKills(DimKeys.of(entityin.level()), SRPConfigSystems.valueMerge, true, entityin.level(), true, 40);
                    }
                    return true;
                }
                ++index;
            }
        } else {
            String[] entityC;
            for (int i = 0; i < SRPConfigSystems.mergeMobTable.length; ++i) {
                int points;
                if (SRPConfigSystems.mergeMobTable[i] == null || (points = Integer.parseInt((entityC = SRPConfigSystems.mergeMobTable[i].split(";"))[1])) != code) continue;
                ParasiteSummon.spawnM(entityin, new String[]{entityC[0] + ";1;1"}, 7, true, name);
                if (SRPConfigSystems.useEvolution) {
                    SRPSaveData.get(entityin.level()).setTotalKills(DimKeys.of(entityin.level()), SRPConfigSystems.valueMerge, true, entityin.level(), true, 41);
                }
                return true;
            }
            RandomSource rand = RandomSource.create();
            int index = rand.nextInt(SRPConfigSystems.mergeMobTable.length);
            boolean flag = true;
            int limit = 0;
            while (flag) {
                if (index >= SRPConfigSystems.mergeMobTable.length) {
                    index = 0;
                    ++limit;
                }
                if (limit == 2) {
                    return false;
                }
                if (SRPConfigSystems.mergeMobTable[index] != null) {
                    entityC = SRPConfigSystems.mergeMobTable[index].split(";");
                    ParasiteSummon.spawnM(entityin, new String[]{entityC[0] + ";1;1"}, 7, true, name);
                    flag = false;
                    if (SRPConfigSystems.useEvolution) {
                        SRPSaveData.get(entityin.level()).setTotalKills(DimKeys.of(entityin.level()), SRPConfigSystems.valueMerge, true, entityin.level(), true, 42);
                    }
                    return true;
                }
                ++index;
            }
        }
        return false;
    }

    static class EntityAILeshCombine
    extends Goal {
        private final EntityLesh parent;

        public EntityAILeshCombine(EntityLesh animal, double speedIn) {
            this.parent = animal;
        }

        public boolean canUse() {
            LivingEntity target = this.parent.getTarget();
            if (target == null) {
                return true;
            }
            return !target.isAlive();
        }

        public void stop() {
        }

        public void tick() {
            if (this.parent.tickCount % 20 != 0) {
                return;
            }
            AABB axisalignedbb = new AABB(this.parent.getX(), this.parent.getY(), this.parent.getZ(), this.parent.getX() + 1.0, this.parent.getY() + 1.0, this.parent.getZ() + 1.0).inflate(this.parent.getAttribute(Attributes.FOLLOW_RANGE).getValue());
            List<? extends EntityLesh> moblist = this.parent.level().getEntitiesOfClass(EntityLesh.class, axisalignedbb);
            for (EntityLesh mob : moblist) {
                if (mob == this.parent || !this.parent.hasLineOfSight((Entity)mob) || !mob.isAlive() || mob.getLastHurtByMob() != null || mob.getTimes() + this.parent.getTimes() > 4) continue;
                this.parent.setTarget((LivingEntity)mob);
                mob.setTarget((LivingEntity)this.parent);
                return;
            }
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
