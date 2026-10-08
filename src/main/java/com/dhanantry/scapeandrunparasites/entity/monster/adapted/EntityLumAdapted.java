package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeNotGround;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSwim;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class EntityLumAdapted
extends EntityPAdapted
implements EntityCutomAttack,
EntityCanSwim {
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityLumAdapted.class, EntityDataSerializers.BYTE);

    public EntityLumAdapted(EntityType<? extends EntityLumAdapted> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.borderOrb = -1;
        this.goalSelector.removeGoal(this.folow);
        this.goalSelector.removeGoal(this.aiWander);
        this.noCulling = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 81;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new EntityAIAttackMeleeNotGround(this, 7.0, SRPConfig.adaptedFollow, 0.08, false, 0, 4));
        this.goalSelector.addGoal(6, new AIMoveRandom());
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAdapted.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.LUM_HEALTH + SRPAttributes.LUM_A_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.LUM_ARMOR + SRPAttributes.LUM_A_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.LUM_ATTACK_DAMAGE + SRPAttributes.LUM_A_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.LUM_KD_RESISTANCE + SRPAttributes.LUM_A_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.adaptedFollow);
        return builder;
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WaterBoundPathNavigation((Mob)this, worldIn);
    }

    @Override
    public void aiStep() {
        if (this.isNoAi()) {
            return;
        }
        this.liquidLeap = 0;
        super.aiStep();
        if (this.isInWater()) {
            this.setNoGravity(true);
        } else {
            this.setNoGravity(false);
        }
        this.liquidLeap = 0;
        if (!this.level().isClientSide) {
            if (this.getTarget() != null) {
                this.setParasiteStatus(1);
            }
            if (this.tickCount % 20 == 0) {
                // empty if block
            }
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            Mot.addY(entityIn, -0.9645);
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        return this.doHurtTarget(entityIn);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.0f;
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.LUM_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.LUM_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.LUM_DEATH.get();
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.level().getDifficulty() != Difficulty.PEACEFUL && SRPConfig.spawnDays <= (int)this.level().getGameTime();
    }

    public boolean checkSpawnObstruction() {
        return this.level().isUnobstructed((Entity)this);
    }

    public int getAmbientSoundInterval() {
        return 0;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return true;
    }

    protected int getExperiencePoints(Player player) {
        return 1 + this.level().random.nextInt(3);
    }

    public void baseTick() {
        int i = this.getAirSupply();
        super.baseTick();
        if (this.isNoAi()) {
            return;
        }
        if (this.isAlive() && !this.isInWater()) {
            this.setAirSupply(--i);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().drown(), 2.0f);
            }
        } else {
            this.setAirSupply(300);
        }
    }

    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.getRandom().nextInt(1)) {
                case 0: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }

    @Override
    public void skillBreakBlocks() {
        LivingEntity target;
        if (this.isRemoved()) {
            return;
        }
        if (this.getBlockH() == 0.0f) {
            return;
        }
        int blocksbroke = 0;
        if (!EventHooks.canEntityGrief((Level)this.level(), (Entity)this)) {
            return;
        }
        int i1 = Mth.floor((double)(this.getY() + 0.1));
        double l1 = this.getX();
        double i2 = this.getZ();
        boolean flag = false;
        int Brangeatm = this.BGrange;
        int offsetT = -2;
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
                for (int j = 1 + offsetT; j <= this.BGheight; ++j) {
                    String name;
                    double i3 = l1 + (double)k2;
                    double k = i1 + j;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = BlockPos.containing(i3, k, l);
                    BlockState iblockstate = this.level().getBlockState(blockpos);
                    Block block = iblockstate.getBlock();
                    float bHard = iblockstate.getDestroySpeed(this.level(), blockpos);
                    if (!(bHard <= this.getBlockH()) || !(bHard >= 0.0f) || block instanceof IMetaName && block != SRPBlocks.ParasiteCanister.get() || block == SRPBlocks.BiomeHeart.get() || block == SRPBlocks.ColonyHeart.get() || block == SRPBlocks.ParasiteRubbleDense.get() || block == SRPBlocks.ParasiteCanisterActive.get() || block == SRPBlocks.dodN.get() || block instanceof LiquidBlock || block instanceof NetherPortalBlock || block instanceof EndGatewayBlock || block instanceof EndPortalFrameBlock || iblockstate.getBlock() instanceof EndPortalBlock || this.blockException(name = block.builtInRegistryHolder().key().location().toString()) || block == Blocks.AIR || !iblockstate.canEntityDestroy(this.level(), blockpos, this) || !EventHooks.onEntityDestroyBlock((LivingEntity)this, (BlockPos)blockpos, (BlockState)iblockstate)) continue;
                    if (SRPConfig.cystActive) {
                        boolean bl = flag = this.destroyBlockPos(blockpos, false) || flag;
                        if (SRPConfig.doTileDrops) {
                            this.addToBlockInv(BlockIds.stateString(iblockstate));
                        }
                    } else {
                        this.destroyBlockPos(blockpos, SRPConfig.doTileDrops);
                    }
                    if (SRPConfigMobs.lumWaterPlacement) {
                        this.level().setBlock(blockpos, Blocks.WATER.defaultBlockState(), 3);
                    }
                    ++blocksbroke;
                }
            }
        }
        this.BGrange = Brangeatm;
        this.SkillBGflag = true;
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

    class AIMoveRandom
    extends Goal {
        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return EntityLumAdapted.this.getRandom().nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityLumAdapted.this.blockPosition();
            int flag = 1;
            double speed = 0.24;
            if (EntityLumAdapted.this.getTarget() != null) {
                if (EntityLumAdapted.this.distanceToSqr((Entity)EntityLumAdapted.this.getTarget()) > 100.0) {
                    blockpos = EntityLumAdapted.this.getTarget().blockPosition();
                    flag = 2;
                } else if (EntityLumAdapted.this.distanceToSqr((Entity)EntityLumAdapted.this.getTarget()) < 36.0) {
                    blockpos = EntityLumAdapted.this.getTarget().blockPosition();
                    flag = 3;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityLumAdapted.this.getRandom().nextInt(15) - 7, EntityLumAdapted.this.getRandom().nextInt(11) - 5, EntityLumAdapted.this.getRandom().nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.offset(EntityLumAdapted.this.getRandom().nextInt(6) - 2, EntityLumAdapted.this.getRandom().nextInt(7) - 2, EntityLumAdapted.this.getRandom().nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.offset(EntityLumAdapted.this.getRandom().nextInt(4) + 3, EntityLumAdapted.this.getRandom().nextInt(5) + 4, EntityLumAdapted.this.getRandom().nextInt(4) + 3);
                }
                if (EntityLumAdapted.this.level().getBlockState(blockpos1).getFluidState().is(FluidTags.WATER) == false) continue;
                EntityLumAdapted.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityLumAdapted.this.getTarget() != null) break;
                EntityLumAdapted.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityLumAdapted vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityLumAdapted.this.getX();
                double d1 = this.getWantedY() - EntityLumAdapted.this.getY();
                double d2 = this.getWantedZ() - EntityLumAdapted.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityLumAdapted.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityLumAdapted.this, 0.5);
                    Mot.mulY(EntityLumAdapted.this, 0.5);
                    Mot.mulZ(EntityLumAdapted.this, 0.5);
                } else {
                    Mot.addX(EntityLumAdapted.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityLumAdapted.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityLumAdapted.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityLumAdapted.this.getTarget() == null) {
                        EntityLumAdapted.this.setYRot(-((float)Mth.atan2((double)EntityLumAdapted.this.getDeltaMovement().x, (double)EntityLumAdapted.this.getDeltaMovement().z)) * 57.295776f);
        EntityLumAdapted.this.yBodyRot = EntityLumAdapted.this.getYRot();
                    } else {
                        double d4 = EntityLumAdapted.this.getTarget().getX() - EntityLumAdapted.this.getX();
                        double d5 = EntityLumAdapted.this.getTarget().getZ() - EntityLumAdapted.this.getZ();
                        EntityLumAdapted.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityLumAdapted.this.yBodyRot = EntityLumAdapted.this.getYRot();
                    }
                }
            }
        }
    }
}

