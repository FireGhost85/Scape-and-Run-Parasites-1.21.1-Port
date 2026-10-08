package com.dhanantry.scapeandrunparasites.entity.monster;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;

public class EntityWaveShock
extends EntityParasiteBase {
    private int raaa;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int duration;
    private boolean canTarget;
    EntityParasiteBase caster;

    public EntityWaveShock(EntityType<? extends EntityWaveShock> type, Level worldIn) {
        super(type, worldIn);
        this.goalSelector.removeGoal(this.aiWander);
        this.goalSelector.removeGoal(this.folow);
        this.killcount = -10.0;
        this.MiniDamage = 0.1f;
        this.canTarget = true;
        this.duration = 1;
    }

    public EntityWaveShock(EntityType<? extends EntityWaveShock> type, Level worldIn, EntityParasiteBase father) {
        this(type, worldIn);
        this.caster = father;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(3, new MeleeAttackGoal((PathfinderMob)this, 1.0, false));
    }

    @Override
    public void applyBonuses(SRPSaveData saveData, Level world) {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, 1.0);
        builder.add(Attributes.MOVEMENT_SPEED, 0.5);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, 20.0);
        return builder;
    }

    public void setDamages(double baseDamage, float min, int range, int durationF) {
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(baseDamage);
        this.MiniDamage = min;
        this.raaa = range;
        this.duration = durationF;
    }

    @Override
    public int getParasiteIDRegister() {
        return 213;
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            BlockState state = this.level().getBlockState(this.blockPosition().below());
            if (state.getBlock() != Blocks.AIR) {
                BlockState id = state;
                for (int i = 0; i < 35; ++i) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, id), this.getX() + (double)this.getRandom().nextFloat() * ((double)this.getBbWidth() * 1.2) * 2.0 - (double)this.getBbWidth() * 1.2, this.getY(), this.getZ() + (double)this.getRandom().nextFloat() * ((double)this.getBbWidth() * 1.2) * 2.0 - (double)this.getBbWidth() * 1.2, this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() + 140.0, this.getRandom().nextGaussian() * 0.02);
                }
            }
        } else {
            if (this.targetX == 0.0 || this.caster == null) {
                this.discard();
                return;
            }
            if (this.level().getBlockState(this.blockPosition()).getBlock() instanceof LiquidBlock) {
                this.discard();
                return;
            }
            if (this.tickCount > 20) {
                if (this.getX() == this.xo || this.getZ() == this.zo) {
                    this.discard();
                }
                if (this.tickCount > 20 * this.duration) {
                    this.discard();
                    return;
                }
            }
            this.skillBreakBlocks();
            float f = this.getBbWidth() / 2.0f;
            float f1 = this.getBbHeight();
            AABB axisalignedbb = new AABB(this.getX() - (double)f, this.getY(), this.getZ() - (double)f, this.getX() + (double)f, this.getY() + (double)f1, this.getZ() + (double)f).expandTowards(1.5, 0.2, 1.5);
            List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity mob : moblist) {
                if (mob instanceof EntityParasiteBase) continue;
                this.doHurtTarget((Entity)mob);
            }
            if (this.distanceToSqr(this.targetX, this.targetY, this.targetZ) > 2.0) {
                this.getMoveControl().setWantedPosition(this.targetX, this.targetY, this.targetZ, 0.6);
            }
            if (this.getX() == this.targetX && this.getZ() == this.targetZ) {
                this.discard();
            }
        }
    }

    protected void jump() {
        this.discard();
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return false;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        if (this.caster == null) {
            return false;
        }
        boolean flag = this.caster.doHurtTarget(entityIn);
        if (flag) {
            Mot.addY(entityIn, 0.64645);
        }
        return flag;
    }

    @Override
    protected void doPush(Entity entityIn) {
    }

    protected void collideWithNearbyEntities() {
    }

    public AABB getCollisionBoundingBox() {
        return new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public void setAttackTarget(LivingEntity entitylivingbaseIn) {
        if (this.tickCount > 40 || this.caster == null || entitylivingbaseIn == null) {
            this.discard();
            return;
        }
        if (this.canTarget) {
            this.targetX = entitylivingbaseIn.getX();
            this.targetY = this.caster.getY();
            this.targetZ = entitylivingbaseIn.getZ();
            this.blockH = this.caster.getBlockH();
            this.BGrange = 2;
            this.BGheight = 3;
            this.canTarget = false;
        }
    }

    @Override
    public void skillBreakBlocks() {
        if (this.isRemoved() || this.caster == null) {
            return;
        }
        int blocksbroke = 0;
        if (!EventHooks.canEntityGrief((Level)this.level(), (Entity)this)) {
            return;
        }
        int i1 = Mth.floor((double)this.getY());
        double l1 = this.getX();
        double i2 = this.getZ();
        boolean flag = false;
        int Brangeatm = this.BGrange;
        int offsetT = 0;
        for (int k2 = -1 * this.BGrange; k2 <= 1 * this.BGrange; ++k2) {
            for (int l2 = -1 * this.BGrange; l2 <= 1 * this.BGrange; ++l2) {
                for (int j = offsetT; j <= this.BGheight + offsetT; ++j) {
                    String name;
                    double i3 = l1 + (double)k2;
                    double k = i1 + j;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = BlockPos.containing(i3, k, l);
                    BlockState iblockstate = this.level().getBlockState(blockpos);
                    Block block = iblockstate.getBlock();
                    float bHard = iblockstate.getDestroySpeed(this.level(), blockpos);
                    if (!(bHard <= this.blockH) || !(bHard >= 0.0f) || block instanceof IMetaName && block != SRPBlocks.ParasiteCanister.get() || block == SRPBlocks.BiomeHeart.get() || block == SRPBlocks.ColonyHeart.get() || block == SRPBlocks.ParasiteRubbleDense.get() || block == SRPBlocks.ParasiteCanisterActive.get() || block == SRPBlocks.dodN.get() || block instanceof LiquidBlock || block instanceof NetherPortalBlock || block instanceof EndGatewayBlock || block instanceof EndPortalFrameBlock || iblockstate.getBlock() instanceof EndPortalBlock || this.blockException(name = block.builtInRegistryHolder().key().location().toString()) || block == Blocks.AIR || !iblockstate.canEntityDestroy(this.level(), blockpos, this) || !EventHooks.onEntityDestroyBlock((LivingEntity)this, (BlockPos)blockpos, (BlockState)iblockstate)) continue;
                    if (SRPConfig.cystActive) {
                        boolean bl = flag = this.destroyBlockPos(blockpos, false) || flag;
                        if (SRPConfig.doTileDrops) {
                            this.caster.addToBlockInv(BlockIds.stateString(iblockstate));
                        }
                    } else {
                        this.destroyBlockPos(blockpos, SRPConfig.doTileDrops);
                    }
                    ++blocksbroke;
                }
            }
        }
        this.BGrange = Brangeatm;
        this.SkillBGflag = true;
    }
}

