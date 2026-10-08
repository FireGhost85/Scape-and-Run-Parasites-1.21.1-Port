package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPFluids;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * The dead blood block (1.12 {@code BlockFluid extends BlockFluidClassic}). Parasites heal in it, everything else is slowed
 * and takes the minimum wither damage; a head under the surface gets corrosion and viral infection.
 */
public class BlockFluid extends LiquidBlock {
    private final boolean pushesEntity;

    public BlockFluid(FlowingFluid fluid, boolean pushEntities) {
        super(fluid, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).replaceable().noCollission().strength(100.0f)
                .pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY));
        this.pushesEntity = pushEntities;
    }

    public boolean getPushesEntity() {
        return this.pushesEntity;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        super.animateTick(state, level, pos, rand);
        if (this.isCalmDeadBloodSurface(level, pos, state) && rand.nextInt(90) == 0) {
            double x = pos.getX() + 0.25 + rand.nextDouble() * 0.5;
            double y = pos.getY() + 1.02;
            double z = pos.getZ() + 0.25 + rand.nextDouble() * 0.5;
            double dx = (rand.nextDouble() - 0.5) * 0.01;
            double dy = 0.02 + rand.nextDouble() * 0.01;
            double dz = (rand.nextDouble() - 0.5) * 0.01;
            level.addParticle(ParticleTypes.CLOUD, x, y, z, dx, dy, dz);
            if (rand.nextInt(3) == 0) {
                level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.12f, 0.28f, 0.1f), x, y + 0.02, z, 0.0, 0.0, 0.0);
            }
        }
    }

    private boolean isCalmDeadBloodSurface(Level level, BlockPos pos, BlockState state) {
        if (!state.getFluidState().isSource()) {
            return false;
        }
        int same = 0;
        for (Direction f : Direction.Plane.HORIZONTAL) {
            if (!level.getFluidState(pos.relative(f)).getType().isSame(SRPFluids.DEADBLOOD_FLUID.get()) || ++same < 3) {
                continue;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (entity instanceof EntityParasiteBase) {
            ((LivingEntity) entity).heal(1.0f);
            return;
        }
        Entity e = entity;
        double my = e.getDeltaMovement().y;
        e.setDeltaMovement(e.getDeltaMovement().x * 0.85, my < 0.0 ? my * 0.92 : my, e.getDeltaMovement().z * 0.85);
        e.fallDistance = 0.0f;
        if (!level.isClientSide && entity instanceof LivingEntity mob) {
            this.attackEntityAsMobMinimum(mob, 0.1f);
            if (!this.isHeadInDeadBlood(level, entity)) {
                return;
            }
            MobEffectInstance curCorro = mob.getEffect(SRPPotions.CORRO_E);
            if (curCorro == null || curCorro.getDuration() < 20) {
                mob.addEffect(new MobEffectInstance(SRPPotions.CORRO_E, 100, 0, false, false));
            }
            MobEffectInstance curVira = mob.getEffect(SRPPotions.VIRA_E);
            if (curVira == null || curVira.getDuration() < 20) {
                mob.addEffect(new MobEffectInstance(SRPPotions.VIRA_E, 200, 1, false, false));
            }
        }
    }

    private boolean isHeadInDeadBlood(Level level, Entity entity) {
        BlockPos headPos = BlockPos.containing(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
        return level.getFluidState(headPos).getType().isSame(SRPFluids.DEADBLOOD_FLUID.get());
    }

    /**
     * Wither-source damage that ignores invulnerability frames, armour and events: {@code MinimumDamage} plus
     * {@code MinimumDamage} times (viral amplifier + 1), taken from the absorption first, with the totem handling of the original.
     */
    public boolean attackEntityAsMobMinimum(LivingEntity target, float minimumDamage) {
        if (minimumDamage <= 0.0f) {
            return false;
        }
        float f1 = target.getHealth();
        if (f1 <= 0.0f) {
            return false;
        }
        if (target instanceof Player player && player.getAbilities().instabuild) {
            return false;
        }
        DamageSource source = target.level().damageSources().wither();
        float damage = 0.0f;
        MobEffectInstance viral = target.getEffect(SRPPotions.VIRA_E);
        if (viral != null) {
            damage = minimumDamage * (float) (viral.getAmplifier() + 1);
        }
        damage += minimumDamage;
        try {
            target.getCombatTracker().recordDamage(source, damage);
        } catch (Exception ignored) {
        }
        if (target.getAbsorptionAmount() > 0.0f) {
            target.setHealth(f1 - damage / 2.0f);
            target.setAbsorptionAmount(target.getAbsorptionAmount() - damage / 2.0f);
        } else {
            target.setHealth(f1 - damage);
        }
        target.level().broadcastEntityEvent(target, (byte) 2);
        if (target.getHealth() <= 0.0f) {
            ItemStack totem = null;
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack held = target.getItemInHand(hand);
                if (!held.is(Items.TOTEM_OF_UNDYING)) {
                    continue;
                }
                totem = held.copy();
                held.shrink(1);
                break;
            }
            if (totem != null) {
                if (target instanceof ServerPlayer serverPlayer) {
                    serverPlayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                    CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, totem);
                }
                target.setHealth(1.0f);
                target.removeAllEffects();
                target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                target.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                target.level().broadcastEntityEvent(target, (byte) 35);
            } else {
                target.die(source);
            }
        }
        return true;
    }
}
