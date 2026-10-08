package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.block.BlockInfestedRubble;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.ticks.TickPriority;

public class EntityThrowableAntiInfestedBlock
extends ThrowableProjectile {
    public EntityThrowableAntiInfestedBlock(EntityType<? extends EntityThrowableAntiInfestedBlock> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityThrowableAntiInfestedBlock(EntityType<? extends EntityThrowableAntiInfestedBlock> type, Level worldIn, LivingEntity throwerIn) {
        super(type, throwerIn, worldIn);
    }

    public EntityThrowableAntiInfestedBlock(EntityType<? extends EntityThrowableAntiInfestedBlock> type, Level worldIn, double x, double y, double z) {
        super(type, x, y, z, worldIn);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void onHit(HitResult result) {
        if (SRPEntityUtil.hitEntity(result) != null) {
            SRPEntityUtil.hitEntity(result).hurt(this.damageSources().thrown((Entity)this, (Entity)this.getOwner()), 0.0f);
        }
        if (!this.level().isClientSide) {
            int i1 = Mth.floor((double)(this.getY() + 0.1));
            double l1 = this.getX();
            double i2 = this.getZ();
            boolean flag = false;
            int offsetT = 5;
            int range = 7;
            for (int k2 = -1 * range; k2 <= 1 * range; ++k2) {
                for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                    for (int j = -1 * offsetT; j <= 1 * offsetT; ++j) {
                        double i3 = l1 + (double)k2;
                        double k = i1 + j;
                        double l = i2 + (double)l2;
                        BlockPos blockpos = BlockPos.containing(i3, k, l);
                        BlockState iblockstate = this.level().getBlockState(blockpos);
                        Block block = iblockstate.getBlock();
                        if (block == SRPBlocks.InfestedStain.get()) {
                            this.level().setBlockAndUpdate(blockpos, SRPBlocks.InfestedStain.get().defaultBlockState().setValue((Property)BlockInfestedStain.STAGE, Integer.valueOf(5)));
                            this.level().scheduleTick(blockpos, SRPBlocks.InfestedStain.get(), 40, TickPriority.byValue(5));
                        }
                        if (block != SRPBlocks.InfestedRubble.get()) continue;
                        this.level().setBlockAndUpdate(blockpos, SRPBlocks.InfestedRubble.get().defaultBlockState().setValue((Property)BlockInfestedRubble.STAGE, Integer.valueOf(5)));
                        this.level().scheduleTick(blockpos, SRPBlocks.InfestedRubble.get(), 40, TickPriority.byValue(5));
                    }
                }
            }
            this.discard();
        } else {
            double d0 = this.getRandom().nextGaussian() * 0.02;
            double d1 = this.getRandom().nextGaussian() * 0.02;
            double d2 = this.getRandom().nextGaussian() * 0.02;
            this.level().addParticle(ParticleTypes.POOF, this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY() + 0.5 + (double)(this.getRandom().nextFloat() * this.getBbHeight()), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), d0, d1, d2);
        }
    }
}

