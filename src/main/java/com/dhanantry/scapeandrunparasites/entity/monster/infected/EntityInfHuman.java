package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityAICircleGroup;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfHumanHead;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityInfHuman
extends EntityPInfected
implements EntityCanMelt {
    private static final EntityDataAccessor<Float> HEIGH = SynchedEntityData.defineId(EntityInfHuman.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> MELTING = SynchedEntityData.defineId(EntityInfHuman.class, EntityDataSerializers.BOOLEAN);
    private float aSize;
    private int sound;
    private int host;
    private BlockPos barrierGoal = null;
    private int barrierRetryTicks = 0;
    private double _lastX = 0.0;
    private double _lastZ = 0.0;
    private int _stuckTicks = 0;
    private int _shoveTicks = 0;
    private BlockPos lastHeardSoundPos;
    private int soundMemoryTicks;
    private static final UUID SHOVE_SPEED_UUID = UUID.fromString("8f5b3f68-7aa5-4c2c-8d9e-2e4a5f5583c3");
    private static final AttributeModifier SHOVE_SPEED_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "gate_shove_boost"), 0.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    public EntityInfHuman(EntityType<? extends EntityInfHuman> type, Level worldIn) {
        super(type, worldIn);
        this.aSize = 1.0f;
        this.sound = 0;
        this.canModRender = 1;
        this.type = (byte)11;
        this.thisMelting = true;
        if (this.getNavigation() instanceof GroundPathNavigation) {
            GroundPathNavigation nav = (GroundPathNavigation)this.getNavigation();
            nav.setCanPassDoors(true);
            if (nav.getNodeEvaluator() instanceof WalkNodeEvaluator) {
                WalkNodeEvaluator proc = (WalkNodeEvaluator)nav.getNodeEvaluator();
                proc.setCanOpenDoors(true);
            }
        }
        this.setPathfindingMalus(PathType.DOOR_WOOD_CLOSED, 0.0f);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 0.0f);
        this.setPathfindingMalus(PathType.DOOR_IRON_CLOSED, -1.0f);
    }

    @Override
    public int getParasiteIDRegister() {
        return 6;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infhumanCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(1, new OpenDoorGoal((Mob)this, true));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.goalSelector.addGoal(4, new EntityAICircleGroup((PathfinderMob)this, 1.15, 8, 4.0, 10.0, 16, (Predicate<? super Entity>)((Predicate)e -> e instanceof EntityInfHuman)));
        this.goalSelector.addGoal(5, new EntityAIGetFollowers(this, 1, 16));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal((Mob)this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPInfected.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.INFHUMAN_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.INFHUMAN_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.230000004172325);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.INFHUMAN_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.INFHUMAN_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.infectedFollow);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HEIGH, (float) (Float.valueOf(0.0f)));
        builder.define(MELTING, Boolean.FALSE);
    }

    @Override
    public void aiStep() {
        double dz;
        double dx;
        super.aiStep();
        this.melting();
        if (this.getNavigation() instanceof GroundPathNavigation) {
            ((GroundPathNavigation)this.getNavigation()).setCanPassDoors(true);
        }
        if (this.getSkin() == 111) {
            this.tickSoundMemory();
            if (this.getHeardSoundPos() == null) {
                AABB scanBox = this.getBoundingBox().inflate(16.0, 4.0, 16.0);
                List<? extends Player> nearby = this.level().getEntitiesOfClass(Player.class, scanBox, p -> !p.isSpectator() && !p.getAbilities().instabuild && p.isAlive());
                Player loudest = null;
                double loudestSpeedSq = 0.0;
                for (Player p2 : nearby) {
                    boolean moving;
                    float walkedThisTick = p2.walkDist - p2.walkDistO;
                    boolean bl = moving = walkedThisTick > 0.01f || p2.isSprinting();
                    if (!moving) continue;
                    double loudness = walkedThisTick;
                    if (p2.isSprinting()) {
                        loudness *= 3.0;
                    }
                    if (!(loudness > loudestSpeedSq)) continue;
                    loudestSpeedSq = loudness;
                    loudest = p2;
                }
                if (loudest != null) {
                    BlockPos soundPos = BlockPos.containing(loudest.getX(), loudest.getY(), loudest.getZ());
                    this.notifyHeardSound(soundPos, 60);
                    this.setTarget((LivingEntity)loudest);
                }
            }
            if (this.getHeardSoundPos() == null && this.getLastHurtByMob() == null && this.getTarget() != null) {
                this.setTarget(null);
            }
        } else {
            this.clearHeardSound();
        }
        this._stuckTicks = (dx = this.getX() - this._lastX) * dx + (dz = this.getZ() - this._lastZ) * dz < 0.0025 ? ++this._stuckTicks : 0;
        this._lastX = this.getX();
        this._lastZ = this.getZ();
        if (this.getTarget() != null) {
            double d2;
            if (this.getNavigation().isDone() || this._stuckTicks > 20) {
                if (this.barrierRetryTicks <= 0 && this.retargetToNearestEntrance(12)) {
                    this.barrierRetryTicks = 40;
                }
            } else if (this.barrierRetryTicks > 0) {
                --this.barrierRetryTicks;
            }
            if (this.barrierGoal != null && (d2 = this.distanceToSqr(Vec3.atCenterOf(this.barrierGoal))) <= 4.0) {
                BlockState st = this.level().getBlockState(this.barrierGoal);
                this.openBarrierAt(this.barrierGoal, st);
                this.barrierGoal = null;
                this.getNavigation().stop();
                this.getNavigation().moveTo((Entity)this.getTarget(), 1.5);
            }
            if (!this.getNavigation().isDone()) {
                this.tryOpenNextPathBarrier();
            }
            this.tryOpenNearbyBarriers();
            if (this._shoveTicks == 0 && this._stuckTicks >= 15 && this.nearOpenBarrier(1.5)) {
                this._shoveTicks = 8;
                this._stuckTicks = 0;
            }
            if (this._shoveTicks > 0) {
                AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
                if (!speedAttr.hasModifier(SHOVE_SPEED_MOD.id())) {
                    speedAttr.addTransientModifier(SHOVE_SPEED_MOD);
                }
                Vec3 look = this.getLookAngle();
                this.push(look.x * 0.15, 0.0, look.z * 0.15);
                if (this.onGround() && this._shoveTicks == 8) {
                    Mot.addY(this, 0.05);
                }
                List<? extends LivingEntity> crowd = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.6, 0.2, 0.6), e -> e != this);
                for (LivingEntity e2 : crowd) {
                    double ex = e2.getX() - this.getX();
                    double ez = e2.getZ() - this.getZ();
                    double dSq = ex * ex + ez * ez + 1.0E-4;
                    e2.push(ex / dSq * 0.08, 0.0, ez / dSq * 0.08);
                }
                --this._shoveTicks;
                if (this._shoveTicks == 0 && speedAttr.hasModifier(SHOVE_SPEED_MOD.id())) {
                    speedAttr.removeModifier(SHOVE_SPEED_MOD.id());
                }
            }
        }
    }

    @Override
    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        Player player;
        if (entitylivingbaseIn instanceof Player && ((player = (Player)entitylivingbaseIn).isSpectator() || player.getAbilities().instabuild)) {
            super.setTarget(null);
            return;
        }
        super.setTarget(entitylivingbaseIn);
    }

    private boolean nearOpenBarrier(double radius) {
        BlockPos base = this.blockPosition();
        int r = (int)Math.ceil(radius);
        for (int y = 0; y <= 1; ++y) {
            for (int dx = -r; dx <= r; ++dx) {
                for (int dz = -r; dz <= r; ++dz) {
                    DoorBlock d;
                    BlockPos p = base.offset(dx, y, dz);
                    BlockState st = this.level().getBlockState(p);
                    if (!(st.getBlock() instanceof DoorBlock ? st.is(BlockTags.WOODEN_DOORS) && (st.getValue((Property)DoorBlock.HALF) != DoubleBlockHalf.UPPER || (st = this.level().getBlockState(p = p.below())).getBlock() instanceof DoorBlock) && (Boolean)st.getValue((Property)DoorBlock.OPEN) != false : st.getBlock() instanceof FenceGateBlock && st.is(BlockTags.FENCE_GATES) && !st.is(Blocks.CRIMSON_FENCE_GATE) && !st.is(Blocks.WARPED_FENCE_GATE) && (Boolean)st.getValue((Property)FenceGateBlock.OPEN) != false)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean retargetToNearestEntrance(int radius) {
        BlockPos me = this.blockPosition();
        BlockPos bestBarrier = null;
        double bestDist2 = Double.MAX_VALUE;
        for (int dy = 0; dy <= 1; ++dy) {
            for (int dx = -radius; dx <= radius; ++dx) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    double d2;
                    BlockPos p = me.offset(dx, dy, dz);
                    BlockState st = this.level().getBlockState(p);
                    if (!EntityInfHuman.isWoodDoor(st) && !EntityInfHuman.isWoodGate(st)) continue;
                    if (EntityInfHuman.isWoodDoor(st)) {
                        p = this.normalizeDoorPos(p, st);
                    }
                    if (!((d2 = p.distSqr((Vec3i)me)) < bestDist2)) continue;
                    bestDist2 = d2;
                    bestBarrier = p;
                }
            }
        }
        if (bestBarrier == null) {
            return false;
        }
        List<BlockPos> around = Arrays.asList(bestBarrier.north(), bestBarrier.south(), bestBarrier.east(), bestBarrier.west());
        around.sort(Comparator.comparingDouble(pos -> pos.distToLowCornerSqr(this.getX(), this.getY(), this.getZ())));
        for (BlockPos ap : around) {
            boolean ok;
            if (!this.level().getBlockState(ap).canBeReplaced() || !(ok = this.getNavigation().moveTo((double)ap.getX() + 0.5, (double)ap.getY() + 0.0, (double)ap.getZ() + 0.5, 1.5))) continue;
            this.barrierGoal = bestBarrier;
            return true;
        }
        return false;
    }

    private static boolean isWoodDoor(BlockState s) {
        if (!(s.getBlock() instanceof DoorBlock)) {
            return false;
        }
        return s.is(BlockTags.WOODEN_DOORS);
    }

    private static boolean isWoodGate(BlockState s) {
        if (!(s.getBlock() instanceof FenceGateBlock)) {
            return false;
        }
        return !s.is(Blocks.CRIMSON_FENCE_GATE) && !s.is(Blocks.WARPED_FENCE_GATE);
    }

    private BlockPos normalizeDoorPos(BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof DoorBlock && state.getValue((Property)DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            return pos.below();
        }
        return pos;
    }

    private void openBarrierAt(BlockPos pos, BlockState state) {
        if (EntityInfHuman.isWoodDoor(state)) {
            if (((Boolean)(state = this.level().getBlockState(pos = this.normalizeDoorPos(pos, state))).getValue((Property)DoorBlock.OPEN)).booleanValue()) {
                return;
            }
            ((DoorBlock)state.getBlock()).setOpen(this, this.level(), state, pos, true);
            return;
        }
        if (EntityInfHuman.isWoodGate(state)) {
            if (((Boolean)state.getValue((Property)FenceGateBlock.OPEN)).booleanValue()) {
                return;
            }
            this.level().setBlock(pos, state.setValue((Property)FenceGateBlock.OPEN, Boolean.valueOf(true)), 10);
        }
    }

    private void tryOpenNextPathBarrier() {
        Path path = this.getNavigation().getPath();
        if (path == null) {
            return;
        }
        int i = path.getNextNodeIndex();
        if (i >= path.getNodeCount()) {
            return;
        }
        Node next = path.getNode(i);
        if (next == null) {
            return;
        }
        BlockPos pos = BlockPos.containing(next.x, next.y, next.z);
        this.openBarrierAt(pos, this.level().getBlockState(pos));
    }

    private void tryOpenNearbyBarriers() {
        BlockPos[] spots;
        BlockPos base = this.blockPosition();
        Direction face = this.getDirection();
        for (BlockPos p : spots = new BlockPos[]{base, base.above(), base.relative(face), base.relative(face).above(), base.relative(face.getClockWise()), base.relative(face.getClockWise()).above(), base.relative(face.getCounterClockWise()), base.relative(face.getCounterClockWise()).above()}) {
            BlockState st = this.level().getBlockState(p);
            if (!EntityInfHuman.isWoodDoor(st) && !EntityInfHuman.isWoodGate(st)) continue;
            this.openBarrierAt(p, st);
        }
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (this.getTHeigh() < 1.57f && !this.level().isClientSide) {
            this.setTHeigh(0.13f);
        }
        if (this.deathTime == 20 && !this.level().isClientSide && this.getRandom().nextDouble() <= SRPAttributes.INFHUMAN_HEADCHANCE) {
            EntityInfHumanHead out = new EntityInfHumanHead(SRPEntities.SIM_HUMANHEAD.get(), this.level());
            out.copyPosition((Entity)this);
            out.finalizeSpawn((ServerLevel) out.level(), this.level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
            out.setSkin(this.getSkin());
            out.cannotDespawn(false);
            this.level().addFreshEntity((Entity)out);
        }
    }

    public void notifyHeardSound(BlockPos pos, int lifeTicks) {
        if (this.getSkin() != 111) {
            return;
        }
        this.lastHeardSoundPos = pos;
        this.soundMemoryTicks = lifeTicks;
    }

    public BlockPos getHeardSoundPos() {
        return this.lastHeardSoundPos;
    }

    public void tickSoundMemory() {
        if (this.soundMemoryTicks > 0) {
            --this.soundMemoryTicks;
            if (this.soundMemoryTicks <= 0) {
                this.lastHeardSoundPos = null;
            }
        }
    }

    public void clearHeardSound() {
        this.lastHeardSoundPos = null;
        this.soundMemoryTicks = 0;
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        if (!this.level().isClientSide) {
            if (SRPConfigMobs.hostEnabled && entityLivingIn instanceof AbstractSkeleton) {
                ++this.host;
                if (this.host >= SRPConfigMobs.hostSkele) {
                    this.particleStatus((byte)7);
                    EntityHost host = new EntityHost(SRPEntities.HOST.get(), this.level());
                    host.copyPosition((Entity)this);
                    host.finalizeSpawn((ServerLevel) host.level(), this.level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                    this.level().addFreshEntity((Entity)host);
                    host.particleStatus((byte)7);
                    this.discard();
                }
            } else {
                super.onKillEntity(entityLivingIn);
            }
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && this.getRandom().nextDouble() < (double)SRPConfig.infectedBleedingChance && entityIn instanceof LivingEntity) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 100, 0);
        }
        return flag;
    }

    @Override
    public void melt() {
        this.setWait(1000);
        this.entityData.set(HEIGH, (float) (Float.valueOf(1.95f)));
        this.entityData.set(MELTING, Boolean.TRUE);
    }

    @Override
    public void melting() {
        if (this.isMelting()) {
            if (this.sound % 20 == 0) {
                this.playSound(SRPSounds.INFECTED_MELT.get(), 1.0f, 1.0f);
            }
            ++this.sound;
            if ((double)this.getTHeigh() > 0.7) {
                this.setaSize(-0.005f);
                this.setTHeigh(-0.01f);
                this.setSize(this.getBbWidth(), this.getTHeigh());
            }
            if (!this.level().isClientSide) {
                if ((double)this.getTHeigh() <= 0.7 || this.sound >= 127) {
                    EntityLesh out = new EntityLesh(SRPEntities.MOVINGFLESH.get(), this.level());
                    out.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                    if (SRPEntityUtil.getCustomNameTag(this) != null) {
                        SRPEntityUtil.setCustomNameTag(out, SRPEntityUtil.getCustomNameTag(this));
                    }
                    this.discard();
                    this.level().addFreshEntity((Entity)out);
                    out.setLegs(SRPAttributes.INFHUMAN_V, false);
                }
            } else {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 106, 0);
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
            }
        }
    }

    public boolean canEntityBeSeen(Entity entityIn) {
        if (this.getSkin() == 111) {
            double dSq = this.distanceToSqr(entityIn);
            return dSq <= 4.0;
        }
        return super.hasLineOfSight(entityIn);
    }

    @Override
    public boolean isMelting() {
        return (Boolean)this.entityData.get(MELTING);
    }

    @Override
    public float getTHeigh() {
        return ((Float)this.entityData.get(HEIGH)).floatValue();
    }

    @Override
    public void setTHeigh(float in) {
        this.entityData.set(HEIGH, (float) (Float.valueOf(in += this.getTHeigh())));
    }

    @Override
    public float getaSize() {
        return this.aSize;
    }

    @Override
    public void setaSize(float in) {
        this.aSize += in;
    }

    @Override
    public float getSelfeFlashIntensity2() {
        return this.aSize;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.73f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INFECTEDHUMAN_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDHUMAN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDHUMAN_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    @Override
    public EntityPFeral getFeral(Level in) {
        return new EntityFerHuman(SRPEntities.FER_HUMAN.get(), in);
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("parasitehost", this.host);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("parasitehost", 99)) {
            this.host = compound.getInt("parasitehost");
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData data = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            this.setSkin(this.level().random.nextInt(3) + 1);
            if (this.getRandom().nextDouble() < (double)0.01f) {
                AttributeInstance moveAttr;
                this.setSkin(111);
                if (this.getAttribute(Attributes.FOLLOW_RANGE) != null) {
                    this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(12.0);
                }
                if ((moveAttr = this.getAttribute(Attributes.MOVEMENT_SPEED)) != null) {
                    moveAttr.setBaseValue(0.32);
                }
            }
        } else {
            this.setSkin(0);
        }
        return data;
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
