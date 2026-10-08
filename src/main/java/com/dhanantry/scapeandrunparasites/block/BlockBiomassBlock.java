package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPDamageTypes;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;

/** Biomass block: damages and poisons any non-parasite creature standing in it. */
public class BlockBiomassBlock extends Block {
    private static final float DAMAGE_PER_HIT = 1.0f;
    private static final int APPLY_COOLDOWN_TICKS = 20;
    private static final String NBT_KEY_NEXT_APPLY = "srp_biomass_next_apply";
    public static final MapCodec<BlockBiomassBlock> CODEC = MapCodec.unit(BlockBiomassBlock::new);

    public BlockBiomassBlock() {
        super(SRPMaterial.CLAY.props(0.6f).sound(SoundType.SLIME_BLOCK).friction(0.8f).lightLevel(s -> 6));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        this.tryAffect(level, pos, entity);
    }

    private void tryAffect(Level world, BlockPos pos, Entity entity) {
        if (world.isClientSide) {
            return;
        }
        if (!(entity instanceof LivingEntity mob)) {
            return;
        }
        if (entity instanceof EntityParasiteBase) {
            return;
        }
        if (entity instanceof Player && ((Player) entity).getAbilities().instabuild) {
            return;
        }
        long now = world.getGameTime();
        CompoundTag tag = mob.getPersistentData();
        long nextAllowed = tag.getLong(NBT_KEY_NEXT_APPLY);
        if (now < nextAllowed) {
            return;
        }
        tag.putLong(NBT_KEY_NEXT_APPLY, now + 20L);
        mob.hurt(new DamageSource(world.registryAccess().holderOrThrow(SRPDamageTypes.BIOMASS)), 1.0f);
        mob.removeEffect(SRPPotions.CORRO_E);
        mob.addEffect(new MobEffectInstance(SRPPotions.CORRO_E, 100, 0, false, true));
        mob.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 1000, 3, false, true));
        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 200, 1);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if (rand.nextFloat() < 0.25f) {
            double x = pos.getX() + 0.2 + rand.nextDouble() * 0.6;
            double y = pos.getY() + 0.05 + rand.nextDouble() * 0.9;
            double z = pos.getZ() + 0.2 + rand.nextDouble() * 0.6;
            double r = 0.1;
            double g = 0.85;
            double b = 0.2;
            world.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, (float) r, (float) g, (float) b), x, y, z, 0.0, 0.0, 0.0);
            BlockPos above = pos.above();
            boolean openAbove = world.isEmptyBlock(above) || !world.getBlockState(above).isFaceSturdy(world, above, Direction.DOWN);
            if (openAbove) {
                BlockClientHooks.particle(SRPEnumParticle.DOT, x, y, z, 0.0, 0.0, 0.0, 165, 255, 0);
            }
        }
        BlockPos below = pos.below();
        boolean openBelow = world.isEmptyBlock(below) || !world.getBlockState(below).isFaceSturdy(world, below, Direction.UP);
        if (openBelow && rand.nextFloat() < 0.1f) {
            double x = pos.getX() + 0.25 + rand.nextDouble() * 0.5;
            double y = pos.getY() + 0.01;
            double z = pos.getZ() + 0.25 + rand.nextDouble() * 0.5;
            double vx = (rand.nextDouble() - 0.5) * 0.02;
            double vy = -0.07 - rand.nextDouble() * 0.03;
            double vz = (rand.nextDouble() - 0.5) * 0.02;
            world.addParticle(ParticleTypes.ITEM_SLIME, x, y, z, vx, vy, vz);
        }
    }
}
