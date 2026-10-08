package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFollowBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;

public class EntityQuac
extends EntityParasiteBase
implements EntityCanHaveBodies {
    private UUID following;
    private boolean canF;
    private BlockPos digTarget;
    public float diggingModel;
    public final float heightTwo = 2.4f;
    private int bodiesT;
    private int bodiesTOut;
    private static final EntityDataAccessor<Byte> PART = SynchedEntityData.defineId(EntityQuac.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> TAIL = SynchedEntityData.defineId(EntityQuac.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DIG = SynchedEntityData.defineId(EntityQuac.class, EntityDataSerializers.BOOLEAN);

    public EntityQuac(EntityType<? extends EntityQuac> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.canModRender = 1;
        this.goalSelector.removeGoal(this.folow);
        this.setPathfindingMalus(PathType.WATER, -1.0f);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, SRPConfig.primitiveWalls, false, null, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, SRPConfig.primitiveWalls, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        }
        this.xpReward = SRPAttributes.XP_PRIMITIVE;
        this.damageCap = SRPConfig.primitiveCap;
        this.canD = SRPConfig.primitivedespawn;
        this.type = (byte)31;
        this.foodSteal = SRPConfig.primitiveFoodSteal;
        this.MiniDamage = SRPConfig.primitiveMinDamage;
        this.oneMindDeathValue = SRPConfig.primitiveOneMindDeathV;
        this.valueEvDeath = SRPConfig.primitiveLoosingEPValue;
        this.diggingModel = 0.0f;
        this.canF = true;
        this.bodiesT = 0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 327;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PART, (byte) (0));
        builder.define(TAIL, false);
        builder.define(DIG, false);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new EntityAIFollowBodies(this, 1.9f, 0.33, 7, 10000.0, 120));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, 0.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.QUAC_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.QUAC_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.33);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.QUAC_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.primitiveFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.handleDigging();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return super.removeWhenFarAway(0.0) && this.following == null;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        if (this.getDigging()) {
            return false;
        }
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        this.madeRng = 0;
        if (source.is(DamageTypes.IN_WALL)) {
            return false;
        }
        if (this.bodiesT - 1 >= this.getBodyNumber()) {
            return false;
        }
        boolean flag = super.hurt(source, amount);
        if (flag && this.following != null) {
            Entity follo = this.getFather(this.following);
            if (follo == null) {
                this.following = null;
                return flag;
            }
            if (!follo.isAlive()) {
                this.following = null;
                return flag;
            }
            follo.hurt(source, amount * 0.5f);
        }
        return flag;
    }

    @Override
    protected void attackEntityFromCap(int go) {
        if (this.getBodyNumber() != 0) {
            return;
        }
        super.attackEntityFromCap(go);
    }

    public void push(Entity entityIn) {
        if (this.getDigging()) {
            return;
        }
        super.push(entityIn);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.0f;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    @Override
    protected void spawnGore() {
        int range = 2;
        double i1 = Mth.floor((double)(this.getY() + 0.1));
        double l1 = this.getX();
        double i2 = this.getZ();
        for (int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
            for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                double i3 = l1 + (double)k2;
                double l = i2 + (double)l2;
                BlockPos blockpos = BlockPos.containing(i3, i1, l);
                Block block = this.level().getBlockState(blockpos).getBlock();
                Block blockDown = this.level().getBlockState(blockpos.below()).getBlock();
                if (block != Blocks.AIR || blockDown == Blocks.AIR || !this.level().getBlockState(blockpos.below()).isCollisionShapeFullBlock(this.level(), blockpos.below()) || blockDown == SRPBlocks.InfestedStain.get() || this.level().random.nextInt(3) != 0) continue;
                this.level().setBlockAndUpdate(blockpos, SRPBlocks.goreFer.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.FLAT)));
            }
        }
        if (this.level().getBlockState(this.blockPosition().below()).isCollisionShapeFullBlock(this.level(), this.blockPosition().below()) && (this.level().getBlockState(this.blockPosition()).getBlock() instanceof BushBlock || this.level().getBlockState(this.blockPosition()).getBlock() == Blocks.AIR)) {
            this.level().setBlockAndUpdate(this.blockPosition(), SRPBlocks.goreFer.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(SRPEntities.REMAIN.get(), this.level());
            nnn.moveTo((double)this.blockPosition().getX() + 0.5, this.blockPosition().getY(), (double)this.blockPosition().getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.primitiveRemainValue);
            this.level().addFreshEntity((Entity)nnn);
        }
        for (int i = 0; i < 4 && SRPConfig.paraGore; ++i) {
            double d0 = (float)this.getX() + this.level().random.nextFloat();
            double d1 = (float)this.getY() + this.level().random.nextFloat();
            double d2 = (float)this.getZ() + this.level().random.nextFloat();
            double d3 = d0 - this.getX();
            double d4 = d1 - this.getY();
            double d5 = d2 - this.getZ();
            double d6 = (float)Math.sqrt((double)(d3 * d3 + d4 * d4 + d5 * d5));
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.5 / (d6 / 4.0 + 0.1);
            d4 = d4 * d7 * 2.0;
            EntityGore bomb = new EntityGore(SRPEntities.GORE.get(), this.level());
            bomb.setType((byte)2);
            bomb.copyPosition((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)), d4, d5 *= d7, 0.15, 0.55);
            this.level().addFreshEntity((Entity)bomb);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        return floo;
    }

    @Override
    public void spawnEffectsGore() {
        for (int i = 0; i <= 60; ++i) {
            if (i % 4 == 0) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 150, 0, 0);
            }
            if (i % 5 != 0) continue;
            this.spawnParticles(SRPEnumParticle.GSPLASH, 2, -1, -1);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.following != null) {
            compound.putUUID("wormhead", this.following);
        }
        compound.putBoolean("wormcanf", this.canF);
        compound.putByte("wormpart", this.getBodyNumber());
        compound.putBoolean("wormtail", this.getBodyTail());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("wormhead")) {
            this.following = compound.getUUID("wormhead");
        }
        if (compound.contains("wormcanf", 99)) {
            this.canF = compound.getBoolean("wormcanf");
        }
        if (compound.contains("wormpart", 99)) {
            this.setBodyNumber(compound.getByte("wormpart"));
        }
        if (compound.contains("wormtail", 99)) {
            this.setBodyTail(compound.getBoolean("wormtail"));
        }
    }

    @Override
    public void skillBreakBlocks() {
        if (this.getBodyNumber() == 0) {
            super.skillBreakBlocks();
        }
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        if (this.getBodyNumber() == 0) {
            super.onKillEntity(entityLivingIn);
            return;
        }
        EntityParasiteBase head = this.getHead();
        if (head != null) {
            head.setKillC(1.0);
        }
    }

    @Override
    public void setAttackTarget(LivingEntity entitylivingbaseIn) {
        if (this.getBodyNumber() == 0) {
            super.setTarget(entitylivingbaseIn);
        }
    }

    @Override
    public void setTargetPos(BlockPos in) {
        this.digTarget = in;
    }

    @Override
    public BlockPos getTargetPos() {
        return this.digTarget;
    }

    @Override
    public boolean getDigging() {
        return (Boolean)this.entityData.get(DIG);
    }

    @Override
    public void setDigging(boolean in) {
        if (in) {
            this.playSound(SRPSounds.QUAC_DIG.get(), 2.0f, this.getVoicePitch());
        }
        this.entityData.set(DIG, in);
    }

    @Override
    public float getDigModel() {
        return this.diggingModel;
    }

    @Override
    public int getBodiesT() {
        return this.bodiesT;
    }

    @Override
    public void setBodiesT(int in) {
        this.bodiesT = in;
    }

    @Override
    public byte getBodyNumber() {
        return (Byte)this.entityData.get(PART);
    }

    @Override
    public void setBodyNumber(int texture) {
        this.entityData.set(PART, (byte) (((byte)texture)));
    }

    @Override
    public boolean getBodyTail() {
        return (Boolean)this.entityData.get(TAIL);
    }

    @Override
    public void setBodyTail(boolean in) {
        this.entityData.set(TAIL, in);
    }

    @Override
    public void setFollowing(EntityCanHaveBodies in) {
        if (in == null) {
            return;
        }
        this.following = in.getEntity().getUUID();
    }

    @Override
    public UUID getFollowing() {
        return this.following;
    }

    @Override
    public int getBodyLength() {
        return 4;
    }

    @Override
    public EntityCanHaveBodies getAnotherBody(Level in) {
        return new EntityQuac(SRPEntities.CARRIER_WORM.get(), in);
    }

    @Override
    public void copyCopy(EntityCanHaveBodies in) {
        this.copyPosition(in.getEntity());
    }

    @Override
    public Entity getEntity() {
        return this;
    }

    @Override
    public void onSpawn(DifficultyInstance difficulty, SpawnGroupData livingdata) {
        if (this.level() instanceof ServerLevelAccessor accessor) {
            this.finalizeSpawn(accessor, difficulty, MobSpawnType.NATURAL, livingdata);
        }
    }

    @Override
    public void setCanF(boolean in) {
        this.canF = in;
    }

    @Override
    public boolean getCanF() {
        return this.canF;
    }

    @Override
    public void bodyPartEffect() {
    }

    private void spawnGroundParticles() {
        BlockState state = this.level().getBlockState(this.blockPosition().below());
        if (state.getBlock() != Blocks.AIR) {
            BlockState id = state;
            for (int i = 0; i < 15; ++i) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, id), this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 3.0f) - (double)this.getBbWidth(), this.getY(), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 3.0f) - (double)this.getBbWidth(), this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() * 0.02);
            }
        }
    }

    @Override
    public double getKillPoints() {
        return this.killcount;
    }

    @Override
    public EntityParasiteBase getEvolution(Level in) {
        return null;
    }

    @Override
    public EntityParasiteBase getHead() {
        if (this.following != null) {
            EntityCanHaveBodies follo = (EntityCanHaveBodies)this.getFather(this.following);
            if (follo == null) {
                return null;
            }
            if (!follo.getEntity().isAlive()) {
                return null;
            }
            return follo.getHead();
        }
        return this;
    }

    @Override
    public Entity getFather(UUID uuid) {
        List serverList = SRPEntityUtil.allEntities(this.level());
        for (int x = 0; x < serverList.size(); ++x) {
            if (!uuid.equals(((Entity)serverList.get(x)).getUUID())) continue;
            return (Entity)serverList.get(x);
        }
        return null;
    }

    @Override
    public void handleDigging() {
        if (this.getDigging()) {
            this.bodiesTOut = 0;
            if (this.getBodyNumber() == 0) {
                this.spawnGroundParticles();
                if (this.getBbHeight() > 0.25f) {
                    this.setSize(this.getBbWidth(), this.getBbHeight() - 0.15f);
                }
                if (this.diggingModel < 2.4f) {
                    this.diggingModel += 0.08f;
                }
            } else {
                if (this.srpTicks == 10) {
                    ++this.bodiesT;
                }
                if (this.bodiesT >= this.getBodyNumber()) {
                    this.spawnGroundParticles();
                    if (this.getBbHeight() > 0.25f) {
                        this.setSize(this.getBbWidth(), this.getBbHeight() - 0.15f);
                    }
                    if (this.diggingModel < 2.4f) {
                        this.diggingModel += 0.08f;
                    }
                }
            }
        } else {
            this.bodiesT = 0;
            if (this.getBodyNumber() == 0) {
                if (this.getBbHeight() < 1.2f) {
                    this.setSize(this.getBbWidth(), this.getBbHeight() + 0.13f);
                    this.spawnGroundParticles();
                }
                if (this.diggingModel >= 0.0f) {
                    this.diggingModel -= 0.08f;
                }
            } else if (this.getX() != this.xo && this.getZ() != this.zo) {
                if (this.getBbHeight() < 1.2f) {
                    this.setSize(this.getBbWidth(), this.getBbHeight() + 0.13f);
                    this.spawnGroundParticles();
                }
                if (this.diggingModel >= 0.0f) {
                    this.diggingModel -= 0.08f;
                }
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
