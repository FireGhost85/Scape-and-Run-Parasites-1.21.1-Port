package com.dhanantry.scapeandrunparasites.entity.monster;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class EntityWave
extends EntityParasiteBase {
    private int raaa;
    private LivingEntity target;
    private int duration;

    public EntityWave(EntityType<? extends EntityWave> type, Level worldIn) {
        super(type, worldIn);
        this.goalSelector.removeGoal(this.aiWander);
        this.goalSelector.removeGoal(this.folow);
        this.killcount = -10.0;
        this.MiniDamage = 0.1f;
        this.duration = 1;
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
        builder.add(Attributes.MOVEMENT_SPEED, 0.45);
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
        return 211;
    }

    public void tick() {
        block8: {
            block7: {
                super.tick();
                if (!this.level().isClientSide) break block7;
                BlockState state = this.level().getBlockState(this.blockPosition().below());
                if (state.getBlock() == Blocks.AIR) break block8;
                BlockState id = state;
                for (int i = 0; i < 15; ++i) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, id), this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY(), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() + 20.0, this.getRandom().nextGaussian() * 0.02);
                }
                break block8;
            }
            if (this.target != null && !this.target.isAlive()) {
                this.discard();
                return;
            }
            if (this.tickCount > 40) {
                if (this.getX() == this.xo || this.getZ() == this.zo) {
                    this.discard();
                }
                if (this.tickCount > 20 * this.duration) {
                    this.discard();
                    return;
                }
            }
            if (this.level().getBlockState(this.blockPosition()).getBlock() instanceof LiquidBlock) {
                this.discard();
                return;
            }
            float f = this.getBbWidth() / 2.0f;
            float f1 = this.getBbHeight();
            AABB axisalignedbb = new AABB(this.getX() - (double)f, this.getY(), this.getZ() - (double)f, this.getX() + (double)f, this.getY() + (double)f1, this.getZ() + (double)f).inflate(0.4, 0.2, 0.4);
            List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity mob : moblist) {
                if (mob instanceof EntityParasiteBase) continue;
                this.attackEntityAsMobMinimum(mob, this.MiniDamage);
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
        return false;
    }

    public AABB getCollisionBoundingBox() {
        return new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    protected void doPush(Entity entityIn) {
    }

    protected void collideWithNearbyEntities() {
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        if (this.level().isClientSide) {
            return;
        }
        if (this.target == entityLivingIn) {
            this.discard();
        }
    }

    @Override
    public void setAttackTarget(LivingEntity entitylivingbaseIn) {
        if (this.tickCount > 40) {
            this.discard();
        }
        super.setTarget(entitylivingbaseIn);
        this.target = entitylivingbaseIn;
    }
}

