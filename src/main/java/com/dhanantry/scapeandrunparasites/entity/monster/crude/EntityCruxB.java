package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidEntityStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidOrAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCruxA;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EntityCruxB
extends EntityParasiteBase {
    private float tSize;
    private float aSize;
    private boolean st1;
    private float tSpeed;
    private int growing;
    private int currentgrow;
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(EntityCruxB.class, EntityDataSerializers.FLOAT);

    public EntityCruxB(EntityType<? extends EntityCruxB> type, Level worldIn) {
        super(type, worldIn);
        int atm;
        this.goalSelector.removeGoal(this.folow);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, false, false, null, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, false, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        }
        if ((atm = SRPConfigMobs.cruxMaxGrowTime - SRPConfigMobs.cruxMinGrowTime + 1) <= 0 || SRPConfigMobs.cruxMaxGrowTime <= SRPConfigMobs.cruxMinGrowTime) {
            atm = SRPConfigMobs.cruxMinGrowTime;
        }
        this.growing = this.getRandom().nextInt(atm) + SRPConfigMobs.cruxMinGrowTime;
        this.st1 = false;
        this.tSize = 1.0f;
        this.aSize = 1.0f;
        this.fuseTime = 70;
        this.killcount = -10.0;
        this.type = (byte)5;
    }

    @Override
    public int getParasiteIDRegister() {
        return 320;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.0, false, -1.0));
        this.goalSelector.addGoal(4, new EntityAIAvoidOrAttack(this, 0.0f, 10, 2));
        this.goalSelector.addGoal(5, new EntityAIAvoidEntityStatus<Mob>(this, Mob.class, new Predicate<Mob>(){

            public boolean test(@Nullable Mob entity) {
                return !(entity instanceof WaterAnimal) && !(entity instanceof EntityParasiteBase) && !(entity instanceof Animal) && !(entity instanceof Villager);
            }
        }, 8.0f, 1.3));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SIZE, (float) (Float.valueOf(this.tSize)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.CRUXA_HEALTH * 0.3);
        builder.add(Attributes.ARMOR, SRPAttributes.CRUXA_ARMOR * 0.3);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.CRUXA_KD_RESISTANCE * 0.3);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.CRUXA_ATTACK_DAMAGE * 0.3);
        builder.add(Attributes.MOVEMENT_SPEED, 0.28);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.primitiveFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.gettSize() > this.getaSize()) {
            this.setaSize(0.01f);
            this.setSize(0.7f + (this.getaSize() - 1.0f), 0.5f + (this.getaSize() - 1.0f));
        }
        ++this.currentgrow;
        if (this.currentgrow > this.growing * 20) {
            this.setSelfeState(1);
        }
        if (!this.level().isClientSide && !this.isRemoved() && this.getHealth() > 0.0f && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + 0.007f);
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
            ParasiteEventEntity.spawnNext(this, new EntityCruxA(SRPEntities.CRUX.get(), this.level()), true, false);
            this.playSound(SRPSounds.FLESH_PRIMITIVE.get(), 1.0f, 1.0f);
        } else {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
        }
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

    protected SoundEvent getAmbientSound() {
        return SRPSounds.FLESH_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.FLESH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.FLESH_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("leshtotalsize", this.tSize);
        compound.putFloat("leshactualsize", this.aSize);
        compound.putFloat("leshspeed", this.tSpeed);
        compound.putInt("growing", this.growing);
        compound.putInt("currentg", this.currentgrow);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("leshtotalsize", 99)) {
            this.tSize = compound.getFloat("leshtotalsize");
            this.entityData.set(SIZE, (float) (Float.valueOf(compound.getFloat("leshtotalsize"))));
        }
        if (compound.contains("leshspeed", 99)) {
            this.tSpeed = compound.getFloat("leshspeed");
        }
        if (compound.contains("growing", 99)) {
            this.growing = compound.getInt("growing");
        }
        if (compound.contains("currentg", 99)) {
            this.currentgrow = compound.getInt("currentg");
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
