package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.block.BlockColonyStructure;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanColony;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class EntityKol
extends EntityParasiteBase
implements EntityCanColony {
    EntityAIFindingSpotBlock build;

    public EntityKol(EntityType<? extends EntityKol> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = SRPAttributes.XP_LiTTLE;
        this.goalSelector.removeGoal(this.folow);
        this.type = (byte)7;
        this.killcount = -10.0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 36;
    }

    public EntityKol(EntityType<? extends EntityKol> type, Level worldIn, BlockPos origin, int distanceBuilding) {
        this(type, worldIn);
        this.setTask(origin, distanceBuilding);
    }

    public void setTask(BlockPos origin, int distanceBuilding) {
        this.build = new EntityAIFindingSpotBlock(this, distanceBuilding);
        this.goalSelector.addGoal(3, this.build);
        this.setOrigin(origin);
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.LODO_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.LODO_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.LODO_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.LODO_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, 16.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.srpTicks == 10 && this.getRandom().nextInt(7) == 0) {
            if (!SRPConfigWorld.coloniesActivated) {
                return;
            }
            SRPWorldData data = SRPWorldData.get(this.level());
            if (data == null) {
                return;
            }
            BlockPos origin = data.nearestColonyPosition(this.blockPosition(), false);
            if (origin != null) {
                this.setTask(origin, data.getColonyDistanceSpreadByPosition(origin, false));
            }
        }
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance > 50.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.5f;
    }

    public void setOrigin(BlockPos pos) {
        this.build.setOrigin(pos.getX(), pos.getY(), pos.getZ());
    }

    public void setOrigin(int x, int y, int z) {
        this.build.setOrigin(x, y, z);
    }

    @Override
    public boolean getCanSpawnHere() {
        BlockState iblockstate = this.level().getBlockState(this.blockPosition().below());
        return true && this.level().getDifficulty() != Difficulty.PEACEFUL && this.isValidLightLevelTwo() && SRPConfig.spawnDays <= (int)this.level().getGameTime();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.build != null) {
            compound.putInt("parasiteoriginx", this.build.getOrigin(1));
            compound.putInt("parasiteoriginy", this.build.getOrigin(2));
            compound.putInt("parasiteoriginz", this.build.getOrigin(3));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        int x = 0;
        int y = 0;
        int z = 0;
        if (compound.contains("parasiteoriginx", 99)) {
            x = compound.getInt("parasiteoriginx");
        }
        if (compound.contains("parasiteoriginy", 99)) {
            y = compound.getInt("parasiteoriginy");
        }
        if (compound.contains("parasiteoriginz", 99)) {
            z = compound.getInt("parasiteoriginz");
        }
        this.build = new EntityAIFindingSpotBlock(this, 10);
        this.goalSelector.addGoal(3, this.build);
        this.setOrigin(x, y, z);
    }

    @Override
    public boolean onlySpawnInside() {
        return true;
    }

    public class EntityAIFindingSpotBlock
    extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

        protected final EntityParasiteBase entity;
        private int building;
        private int defence;
        private int tiick;
        private int originX;
        private int originY;
        private int originZ;
        private int maxDistance;

        public EntityAIFindingSpotBlock(EntityKol creatureIn, int maxDistance) {
            this.entity = creatureIn;
            this.building = 26;
            this.defence = this.building / 2;
            this.tiick = 0;
            this.maxDistance = maxDistance * maxDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            ++this.tiick;
            return this.tiick >= 200 && this.entity.getParasiteStatus() == 0;
        }

        public boolean canContinueToUse() {
            return this.entity.getNavigation().isDone() && this.entity.getParasiteStatus() == 0 && this.tiick != 0;
        }

        public void stop() {
            this.tiick = 0;
        }

        public void tick() {
            BlockPos flag = this.entity.blockPosition();
            int range = 25;
            for (int x = flag.getX() - range; x <= flag.getX() + range; ++x) {
                for (int z = flag.getZ() - range; z <= flag.getZ() + range; ++z) {
                    BlockPos spot;
                    if (this.checkPosition(x, z) == this.defence) {
                        spot = this.checkBlock(EntityKol.this.level(), BlockPos.containing(x, flag.getY(), z));
                        if (spot == null) continue;
                        this.entity.level().setBlockAndUpdate(spot.below(), SRPBlocks.ParasiteStructure.get().defaultBlockState().setValue((Property)BlockColonyStructure.ACTIVE, Integer.valueOf(2)));
                        this.stop();
                        return;
                    }
                    if (this.checkPosition(x, z) != this.building || (spot = this.checkBlock(EntityKol.this.level(), BlockPos.containing(x, flag.getY(), z))) == null) continue;
                    this.entity.level().setBlockAndUpdate(spot.below(), SRPBlocks.ParasiteStructure.get().defaultBlockState().setValue((Property)BlockColonyStructure.ACTIVE, Integer.valueOf(1)));
                    this.stop();
                    return;
                }
            }
            this.stop();
        }

        private BlockPos checkBlock(Level world, BlockPos pos) {
            if (pos.getY() <= 2) {
                return null;
            }
            if (this.getDistanceSqFromOrigin(pos)) {
                return null;
            }
            if ((pos = ParasiteEventEntity.getFloor(world, pos, 5)) != null) {
                if (this.checkArea(world, pos)) {
                    return null;
                }
                return pos;
            }
            return null;
        }

        private boolean checkArea(Level world, BlockPos pos) {
            int a = 30;
            int yy = pos.getY();
            for (int i = yy - a; i <= yy + a; ++i) {
                Block block = world.getBlockState(BlockPos.containing(pos.getX(), i, pos.getZ())).getBlock();
                if (block != SRPBlocks.ParasiteStructure.get() && block != SRPBlocks.ColonyHeart.get() && block != SRPBlocks.BiomeHeart.get()) continue;
                return true;
            }
            return false;
        }

        private int checkPosition(int posX, int posZ) {
            if (posX % this.defence == 0 && posX % this.building != 0 && posZ % this.defence == 0 && posZ % this.building != 0) {
                return this.defence;
            }
            if (posX % this.building == 0 && posZ % this.building == 0) {
                return this.building;
            }
            return 0;
        }

        private boolean getDistanceSqFromOrigin(BlockPos pos) {
            double d2;
            double d1;
            double d0 = this.originX - pos.getX();
            return d0 * d0 + (d1 = (double)(this.originY - pos.getY())) * d1 + (d2 = (double)(this.originZ - pos.getZ())) * d2 > (double)this.maxDistance;
        }

        public void setOrigin(int x, int y, int z) {
            this.originX = x;
            this.originY = y;
            this.originZ = z;
        }

        public int getOrigin(int in) {
            switch (in) {
                case 1: {
                    return this.originX;
                }
                case 2: {
                    return this.originY;
                }
                case 3: {
                    return this.originZ;
                }
            }
            return 0;
        }
    }
}

