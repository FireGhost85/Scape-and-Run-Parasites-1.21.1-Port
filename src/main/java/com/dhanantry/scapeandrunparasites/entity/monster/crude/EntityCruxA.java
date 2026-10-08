package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCrude;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityCruxA
extends EntityPCrude
implements EntityCutomAttack {
    private float attackTimerM;
    private boolean upM;
    private float attackTimerR;
    private boolean upR;
    private int extraDamageCap;
    private int currentDamageTimes;
    private double baseDamage;
    private byte cooldownBack;
    private boolean canBack;
    private int limit;
    private int border;
    private double targetX;
    private double targetZ;
    private double mottY;
    private boolean skillThrow;

    public EntityCruxA(EntityType<? extends EntityCruxA> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.goalSelector.removeGoal(this.folow);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, false, false, null, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, false, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        }
        this.type = (byte)41;
        this.skillThrow = false;
        this.currentDamageTimes = 0;
        this.extraDamageCap = SRPConfigMobs.cruxaDamageCap;
        this.baseDamage = -1.0;
        this.cooldownBack = 0;
        this.canBack = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 62;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.11));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 60, 20, 12, true, 1));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatusAOE(this, 1.3, true, 0.0, 3.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPCrude.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.CRUXA_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.CRUXA_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.24);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.CRUXA_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.CRUXA_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, 64.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.upM) {
            this.attackTimerM = (float)((double)this.attackTimerM + 0.2);
            if ((double)this.attackTimerM > 0.9) {
                this.upM = false;
            }
        } else {
            this.attackTimerM = (float)((double)this.attackTimerM - 0.1);
        }
        if (this.upR) {
            this.attackTimerR = (float)((double)this.attackTimerR + 0.4);
            if ((double)this.attackTimerR > 2.2) {
                this.upR = false;
            }
        } else {
            this.attackTimerR = (float)((double)this.attackTimerR - 0.2);
        }
    }

    private void popBack() {
        if (this.canBack) {
            if (this.srpTicks == 10) {
                this.cooldownBack = (byte)(this.cooldownBack + 1);
            }
            if (this.cooldownBack < 20) {
                return;
            }
            this.canBack = false;
            this.playSound(SRPSounds.MOBEXPLOTION.get(), 3.0f, 1.0f);
            this.cooldownBack = 0;
            if (this.level().isClientSide) {
                int i;
                for (i = 0; i <= 180; ++i) {
                    if (i % 5 != 0) continue;
                    this.spawnParticlesGore(SRPEnumParticle.GSPLASH, 0, -1, -1, 5.0, 9.0);
                }
                for (i = 0; i <= 80; ++i) {
                    if (i % 5 == 0) {
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                    }
                    if (i % 5 != 0) continue;
                    this.spawnParticles(SRPEnumParticle.GSPLASH, 0, -1, -1);
                }
                return;
            }
            int range = 4;
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
                    if (block != Blocks.AIR || blockDown == Blocks.AIR || !this.level().getBlockState(blockpos.below()).isCollisionShapeFullBlock(this.level(), blockpos.below()) || blockDown == SRPBlocks.InfestedStain.get() || this.level().random.nextInt(4) != 0) continue;
                    this.level().setBlockAndUpdate(blockpos, SRPBlocks.goreFer.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.FLAT)));
                }
            }
            for (int i = 0; i < 7 && SRPConfig.paraGore; ++i) {
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
                d3 = d3 * (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)) * 7.0;
                d4 = d4 * d7 * 12.0;
                d5 = d5 * d7 * 7.0;
                EntityGore bomb = new EntityGore(SRPEntities.GORE.get(), this.level());
                bomb.setType((byte)1);
                bomb.copyPosition((Entity)this);
                bomb.setMotion(d3, d4, d5, 0.28, 0.55);
                Mot.setPosY(bomb, bomb.getY() + (3.5));
                this.level().addFreshEntity((Entity)bomb);
            }
        } else {
            if (this.srpTicks == 10) {
                this.cooldownBack = (byte)(this.cooldownBack + 1);
            }
            if (this.cooldownBack > 15) {
                this.cooldownBack = 0;
                this.canBack = true;
            }
        }
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        if (this.extraDamageCap > this.currentDamageTimes) {
            return;
        }
        ++this.currentDamageTimes;
        if (this.baseDamage == -1.0) {
            this.baseDamage = this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * SRPConfigMobs.cruxaDamageGain;
        }
        double getD = this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getD + this.baseDamage);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 3.3f;
    }

    public boolean getBack() {
        return this.canBack;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.CRUX_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.CRUX_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.CRUX_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.MONSTER_STEP.get(), 0.15f, 1.0f);
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        this.upM = true;
        this.attackTimerM = 0.0f;
        this.level().broadcastEntityEvent((Entity)this, (byte)22);
        boolean flag = false;
        this.playSound(SRPSounds.SWIPE.get(), 2.0f, 1.0f);
        AABB axisalignedbb = new AABB(entityIn.getX(), entityIn.getY(), entityIn.getZ(), entityIn.getX() + 1.0, entityIn.getY() + 1.0, entityIn.getZ() + 1.0).inflate(1.0);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            if (mob instanceof EntityParasiteBase) {
                if (this.getTarget() != mob) continue;
                this.setTarget(null);
                return false;
            }
            if (mob == this || !this.hasLineOfSight((Entity)mob) || !this.doHurtTarget((Entity)mob)) continue;
            flag = true;
        }
        return flag;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (SRPConfigSystems.rageEnable) {
            this.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 1333320, 0, false, false));
        }
        return floo;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putDouble("parasitecruxdamage", this.baseDamage);
        compound.putInt("parasitecruxtimes", this.currentDamageTimes);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("parasitecruxdamage", 99)) {
            this.baseDamage = compound.getDouble("parasitecruxdamage");
        }
        if (compound.contains("parasitecruxtimes", 99)) {
            this.currentDamageTimes = compound.getInt("parasitecruxtimes");
        }
    }

    public float getAttackTimerM() {
        return this.attackTimerM;
    }

    public float getAttackTimerR() {
        return this.attackTimerR;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 22) {
            this.upM = true;
            this.attackTimerM = 0.0f;
        } else if (id == 23) {
            this.upR = true;
            this.attackTimerR = 0.0f;
        } else if (id == 24) {
            this.canBack = true;
        } else if (id == 25) {
            this.canBack = false;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillThrow;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillThrow = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.throwBlock();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void throwBlock() {
        LivingEntity entitylivingbase = this.getTarget();
        if (entitylivingbase != null) {
            this.targetX = entitylivingbase.getX();
            this.targetZ = entitylivingbase.getZ();
            if (this.distanceToSqr((Entity)entitylivingbase) < 144.0 || entitylivingbase.getY() < this.getY() || entitylivingbase.getY() > this.getY() + 3.0) {
                this.skillThrow = true;
                this.setParasiteStatus(0);
                this.border = 0;
                this.limit = 0;
                return;
            }
        } else {
            this.skillThrow = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.limit = 0;
            return;
        }
        this.mottY = this.distanceToSqr((Entity)entitylivingbase);
        if (this.tickCount % 20 != 0) {
            return;
        }
        ++this.border;
        BlockState b = this.blockNotAvailable();
        if (b == null) {
            ++this.limit;
            if (this.limit >= 5) {
                this.skillThrow = true;
                this.setParasiteStatus(0);
                this.border = 0;
                this.limit = 0;
            }
            return;
        }
        this.skillBreakBlocks();
        this.setParasiteStatus(10);
        this.getNavigation().moveTo(this.targetX, entitylivingbase.getY(), this.targetZ, 0.0);
        this.level().broadcastEntityEvent((Entity)this, (byte)23);
        BlockPos pos = this.blockPosition();
        Vec3 vec3d = this.getViewVector(1.0f);
        FallingBlockEntity entityfallingblock = FallingBlockEntity.fall(this.level(), BlockPos.containing(this.getX() + vec3d.x, this.getY() + (double)this.getEyeHeight() - 0.7, this.getZ() + vec3d.z), b);
        entityfallingblock.setPos(this.getX() + vec3d.x, this.getY() + (double)this.getEyeHeight() - 0.7, this.getZ() + vec3d.z);
        entityfallingblock.time = -100;
        this.targetX += Math.random() * 2.0 - 1.0;
        this.targetZ += Math.random() * 2.0 - 1.0;
        double d0 = this.targetX - this.getX();
        double d1 = this.targetZ - this.getZ();
        double f = (float)Math.sqrt((double)(d0 * d0 + d1 * d1));
        Mot.setY(entityfallingblock, this.mottY * 0.0);
        if (entitylivingbase.getY() >= this.getY() + 2.0) {
            Mot.addY(entityfallingblock, 0.5);
        }
        if (entitylivingbase.getY() <= this.getY() - 2.0) {
            Mot.addY(entityfallingblock, -0.5);
        }
        double mmx = d0 / f * (this.mottY * 0.009) * 0.9 + entityfallingblock.getDeltaMovement().x * 0.3;
        double mmz = d1 / f * (this.mottY * 0.009) * 0.9 + entityfallingblock.getDeltaMovement().z * 0.3;
        Mot.addX(entityfallingblock, mmx);
        Mot.addZ(entityfallingblock, mmz);
        EntityDamage damage = new EntityDamage(this.level(), entityfallingblock.getX(), entityfallingblock.getY(), entityfallingblock.getZ(), 0.0f, (LivingEntity)this, 10.0f + b.getDestroySpeed(this.level(), this.blockPosition()), false, 0.0f);
        damage.setFollower((Entity)entityfallingblock);
        this.level().addFreshEntity((Entity)entityfallingblock);
        this.level().addFreshEntity((Entity)damage);
        if (this.limit >= 5 || this.border > 8) {
            this.skillThrow = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.limit = 0;
        }
    }

    private BlockState blockNotAvailable() {
        BlockPos pos;
        BlockState b = null;
        int range = 2;
        int newX = this.getRandom().nextInt(range) + 1;
        int newZ = this.getRandom().nextInt(range) + 1;
        if (this.getRandom().nextBoolean()) {
            newX *= -1;
        }
        if (this.getRandom().nextBoolean()) {
            newZ *= -1;
        }
        if ((pos = ParasiteEventEntity.getFloor(this.level(), BlockPos.containing(this.getX() + (double)newX, this.getY(), this.getZ() + (double)newZ), 4)) != null) {
            if (this.checkBlock(this.level().getBlockState(pos = pos.below()).getBlock(), this.level().getBlockState(pos), pos)) {
                return null;
            }
            b = this.level().getBlockState(pos);
            this.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
        return b;
    }

    private boolean checkBlock(Block in, BlockState state, BlockPos pos) {
        return in == Blocks.AIR || in instanceof LiquidBlock || !state.isCollisionShapeFullBlock(this.level(), pos) || state.getDestroySpeed(this.level(), pos) <= 0.0f || this.blockException(in.builtInRegistryHolder().key().location().toString());
    }
}

