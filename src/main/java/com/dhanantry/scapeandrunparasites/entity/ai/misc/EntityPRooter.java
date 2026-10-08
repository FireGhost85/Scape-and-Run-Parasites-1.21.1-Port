package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityLeemB;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNexusProtection3;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class EntityPRooter
extends EntityPStationaryArchitect {
    protected int leemRange;
    protected int leemRangeEffect;
    protected int leemBalls;
    protected int leemCooldown;
    protected int leemCooldownReset;
    private int blockR;
    private static final EntityDataAccessor<Boolean> RTTS = SynchedEntityData.defineId(EntityPRooter.class, EntityDataSerializers.BOOLEAN);
    protected int rangeB = 0;

    public EntityPRooter(EntityType<? extends EntityPRooter> type, Level worldIn) {
        super(type, worldIn);
        this.setScentHPMultiplier(1.0f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RTTS, false);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.srpTicks == 5) {
                --this.leemCooldown;
                ++this.blockR;
                if (this.blockR >= 7) {
                    this.checkRTTS();
                    this.blockR = 0;
                }
            }
            if (this.srpTicks == 10 && this.leemCooldown <= 0) {
                this.leemCooldown = this.leemCooldownReset;
                AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).expandTowards((double)this.leemRangeEffect, (double)this.leemRangeEffect, (double)this.leemRangeEffect);
                List<? extends EntityParasiteBase> moblist = this.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
                for (EntityParasiteBase mob : moblist) {
                    if (mob == this || !mob.isAlive() || mob instanceof EntityPRooter || mob.getParasiteIDRegister() == 314) continue;
                    mob.addEffect(new MobEffectInstance(SRPPotions.PIVOT_E, 300, this.stage - 1, false, false));
                    mob.addEffect(new MobEffectInstance(SRPPotions.PARATE_E, 300, this.stage - 1, false, false));
                    mob.SetRooter(this);
                    mob.applyGene(new boolean[]{true, true, true, true, true, true, true, true, true, true}, new float[]{2.5f, 3.0f, 0.5f});
                }
            }
        }
    }

    @Override
    public void addPotionEffect(MobEffectInstance potioneffectIn) {
        if (potioneffectIn.getEffect() == SRPPotions.PIVOT_E) {
            return;
        }
        super.addEffect(potioneffectIn);
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        if (this.level().isClientSide) {
            return false;
        }
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).expandTowards((double)(this.leemRange + 1), (double)this.leemRange, (double)(this.leemRange + 1));
        List<? extends EntityLeemB> moblist = this.level().getEntitiesOfClass(EntityLeemB.class, axisalignedbb);
        if (moblist.size() > 0) {
            float part = amount / (float)moblist.size();
            for (EntityLeemB mob : moblist) {
                mob.hurt(source, part);
            }
            return super.hurt(source, 0.0f);
        }
        this.spawnLeemB(this.leemBalls);
        return super.hurt(source, amount);
    }

    protected void spawnLeemB(int in) {
        int cap = this.getRandom().nextInt(this.leemBalls) + 1;
        for (int i = 0; i < cap; ++i) {
            ParasiteSummon.SummonM((LivingEntity)this, new String[]{"srparasites:rooterball;1;1"}, 2, 5, null);
        }
    }

    public void checkRTTS() {
        if (this.rangeB == 0) {
            return;
        }
        double l1 = this.getX();
        double i2 = this.getZ();
        for (int k2 = -1 * this.rangeB; k2 <= 1 * this.rangeB; ++k2) {
            for (int l2 = -1 * this.rangeB; l2 <= 1 * this.rangeB; ++l2) {
                double i3 = l1 + (double)k2;
                double l = i2 + (double)l2;
                BlockState iblockstate = this.level().getBlockState(BlockPos.containing(i3, this.getY() - 0.5, l));
                Block block = iblockstate.getBlock();
                if (block != Blocks.AIR) continue;
                this.setRTTS(false);
                return;
            }
        }
        this.setRTTS(true);
    }

    @Override
    public void generateStructure() {
        if (SRPConfig.nexusStructures) {
            WorldGenParasiteNexusProtection3 p1 = new WorldGenParasiteNexusProtection3(false, 1);
            p1.generate(this.level(), RandomSource.create(), this.blockPosition());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    public boolean getRTTS() {
        return (Boolean)this.entityData.get(RTTS);
    }

    public void setRTTS(boolean in) {
        this.entityData.set(RTTS, in);
    }
}

