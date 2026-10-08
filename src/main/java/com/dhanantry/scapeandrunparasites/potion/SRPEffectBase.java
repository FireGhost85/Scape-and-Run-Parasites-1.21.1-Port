package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.network.ParticlePayload;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;

/** Base of every SRP mob effect (SRPEffectBase of 1.10.9). The icon is the standard sprite textures/mob_effect/&lt;name&gt;.png. */
public class SRPEffectBase extends MobEffect {
    private final String name;

    public SRPEffectBase(String name, boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn ? MobEffectCategory.HARMFUL : MobEffectCategory.BENEFICIAL, liquidColorIn);
        this.name = name;
    }

    public String getEffectName() {
        return this.name;
    }

    protected final boolean is(DeferredHolder<MobEffect, MobEffect> holder) {
        return holder.isBound() && holder.get() == this;
    }

    /** getCurativeItems() returned an empty list: nothing cures the effect except the totem of undying (1.12 clearActivePotions). */
    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        cures.add(EffectCures.PROTECTED_BY_TOTEM);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        if (this.is(SRPPotions.BLEED_E) || this.is(SRPPotions.EPEL_E) || this.is(SRPPotions.DLER_E) || this.is(SRPPotions.CORRO_E) || this.is(SRPPotions.FOSTER_E) || this.is(SRPPotions.CONTA_E)) {
            int j = 25 >> amplifier;
            if (j > 0) {
                return duration % j == 0;
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            if (this.is(SRPPotions.RAGE_E)) {
                for (int i = 0; i <= 3; ++i) {
                    if (!(entity.level().random.nextFloat() < 0.1f)) continue;
                    this.sendParticle(entity, (byte) 12, true);
                }
                if (entity.level().random.nextFloat() < 0.8f) {
                    this.sendParticle(entity, (byte) 2, true);
                }
            }
            if (this.is(SRPPotions.BLEED_E)) {
                for (int i = 0; i <= 3; ++i) {
                    if (!(entity.level().random.nextFloat() < 0.8f)) continue;
                    this.sendParticle(entity, (byte) 13, true);
                }
            }
            if (this.is(SRPPotions.INDEAF_E)) {
                Vec3 motion = entity.getDeltaMovement();
                entity.setDeltaMovement(0.0, motion.y, 0.0);
                if (entity instanceof Player) {
                    entity.zza = 0.0f;
                    entity.xxa = 0.0f;
                }
            } else if (this.is(SRPPotions.EFFECTPOS_E)) {
                this.effectEffectPos(entity, amplifier);
            } else if (this.is(SRPPotions.EFFECTNEG_E)) {
                this.effectEffectNeg(entity, amplifier);
            }
        }
        return true;
    }

    private void sendParticle(LivingEntity entity, byte type, boolean showForSelf) {
        ParticlePayload packet = ParticlePayload.at(entity, type);
        if (showForSelf) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, packet);
        } else {
            PacketDistributor.sendToPlayersTrackingEntity(entity, packet);
        }
    }

    protected void InfectNearby(LivingEntity entity, int range) {
        if (SRPConfigSystems.cothAura <= 0) {
            return;
        }
        if (entity.hasEffect(SRPPotions.EPEL_E)) {
            return;
        }
        if (entity instanceof Player player) {
            if (player.getAbilities().instabuild) {
                return;
            }
        }
        AABB aabb = new AABB(entity.getX(), entity.getY(), entity.getZ(), entity.getX() + 1.0, entity.getY() + 1.0, entity.getZ() + 1.0).inflate(range);
        List<LivingEntity> moblist = entity.level().getEntitiesOfClass(LivingEntity.class, aabb);
        for (LivingEntity mob : moblist) {
            if (!entity.hasLineOfSight(mob) || mob == entity || mob.hasEffect(SRPPotions.COTH_E) || mob.hasEffect(SRPPotions.EPEL_E)) continue;
            mob.addEffect(SRPPotions.effect(SRPPotions.COTH_E, 4800, 0));
        }
    }

    protected BlockPos getRandomPosInCircle(RandomSource rand, BlockPos center, double radius) {
        double theta = rand.nextDouble() * 2.0 * Math.PI;
        double r = Math.sqrt(rand.nextDouble()) * radius;
        int dx = (int) Math.floor(Math.cos(theta) * r);
        int dz = (int) Math.floor(Math.sin(theta) * r);
        return center.offset(dx, 0, dz);
    }

    protected void effectEffectPos(LivingEntity entity, int amplifier) {
        if (entity.tickCount % 20 != 0) {
            return;
        }
        for (MobEffectInstance pe : new ArrayList<>(entity.getActiveEffects())) {
            MobEffect p = pe.getEffect().value();
            if (p.getCategory() == MobEffectCategory.HARMFUL) continue;
            entity.hurt(entity.level().damageSources().wither(), 0.5f * (float) (pe.getAmplifier() + 1));
        }
    }

    protected void effectEffectNeg(LivingEntity entity, int amplifier) {
        if (entity.tickCount % 20 != 0) {
            return;
        }
        for (MobEffectInstance pe : new ArrayList<>(entity.getActiveEffects())) {
            Holder<MobEffect> p = pe.getEffect();
            if (p.value().getCategory() != MobEffectCategory.HARMFUL) continue;
            SRPPotions.applyStackPotion(p, entity, 20, amplifier);
        }
    }
}
