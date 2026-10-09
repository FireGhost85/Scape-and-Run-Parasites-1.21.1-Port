package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import com.dhanantry.scapeandrunparasites.util.Mot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * The 1.12 explosion of the parasites (SRPExplosion): the vanilla algorithm with the config switches of the mod; the blocks a
 * parasite blows up are collected in its block inventory instead of being dropped. {@link #doExplosionA} computes the
 * affected blocks and hurts the entities, {@link #doExplosionB} destroys the blocks.
 */
public class SRPExplosion
extends Explosion {
    private final boolean causesFire;
    private final boolean damagesTerrain;
    private final Level world;
    private final double x;
    private final double y;
    private final double z;
    private final Entity exploder;
    private final float size;
    private final List<BlockPos> affectedBlockPositions = new ArrayList<>();
    EntityParasiteBase shooterTwo;

    public SRPExplosion(Level worldIn, Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
        this(worldIn, entityIn, x, y, z, size, false, true, affectedPositions);
    }

    public SRPExplosion(Level worldIn, Entity entityIn, double x, double y, double z, float size, boolean causesFire, boolean damagesTerrain, List<BlockPos> affectedPositions) {
        this(worldIn, entityIn, x, y, z, size, causesFire, damagesTerrain);
        this.affectedBlockPositions.addAll(affectedPositions);
    }

    public SRPExplosion(Level worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain) {
        super(worldIn, entityIn, x, y, z, size, flaming, damagesTerrain ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP);
        this.world = worldIn;
        this.exploder = entityIn;
        this.size = size;
        this.x = x;
        this.y = y;
        this.z = z;
        this.causesFire = flaming;
        this.damagesTerrain = damagesTerrain;
        if (entityIn instanceof EntityParasiteBase) {
            this.shooterTwo = (EntityParasiteBase)entityIn;
        }
    }

    public void doExplosionA() {
        if (this.world.isClientSide) {
            return;
        }
        Set<BlockPos> set = new HashSet<>();
        int i = 16;
        for (int j = 0; j < i; ++j) {
            for (int k = 0; k < i; ++k) {
                for (int l = 0; l < i; ++l) {
                    if (j != 0 && j != 15 && k != 0 && k != 15 && l != 0 && l != 15) continue;
                    double d0 = (float)j / 15.0f * 2.0f - 1.0f;
                    double d1 = (float)k / 15.0f * 2.0f - 1.0f;
                    double d2 = (float)l / 15.0f * 2.0f - 1.0f;
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    d0 /= d3;
                    d1 /= d3;
                    d2 /= d3;
                    double d4 = this.x;
                    double d6 = this.y;
                    double d8 = this.z;
                    for (float f = this.size * (0.7f + this.world.random.nextFloat() * 0.6f); f > 0.0f; f -= 0.22500001f) {
                        BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                        BlockState state = this.world.getBlockState(blockpos);
                        if (LegacyMaterial.of(state) != LegacyMaterial.air) {
                            float f2 = Math.max(state.getExplosionResistance(this.world, blockpos, this), this.world.getFluidState(blockpos).getExplosionResistance(this.world, blockpos, this));
                            if (this.exploder != null) {
                                f2 = this.exploder.getBlockExplosionResistance(this, this.world, blockpos, state, this.world.getFluidState(blockpos), f2);
                            }
                            f -= (f2 + 0.3f) * 0.3f;
                        }
                        if (f > 0.0f && (this.exploder == null || this.exploder.shouldBlockExplode(this, this.world, blockpos, state, f))) {
                            set.add(blockpos);
                        }
                        d4 += d0 * (double)0.3f;
                        d6 += d1 * (double)0.3f;
                        d8 += d2 * (double)0.3f;
                    }
                }
            }
        }
        this.affectedBlockPositions.addAll(set);
        float f3 = this.size * 2.0f;
        double df1 = (double)f3 - 1.0;
        double df2 = (double)f3 + 1.0;
        int k1 = Mth.floor(this.x - df1);
        int l1 = Mth.floor(this.x + df2);
        int i2 = Mth.floor(this.y - df1);
        int i1 = Mth.floor(this.y + df2);
        int j2 = Mth.floor(this.z - df1);
        int j1 = Mth.floor(this.z + df2);
        List<Entity> list = this.world.getEntities(this.exploder, new AABB(k1, i2, j2, l1, i1, j1));
        net.neoforged.neoforge.event.EventHooks.onExplosionDetonate(this.world, this, list, f3);
        Vec3 center = new Vec3(this.x, this.y, this.z);
        for (Entity entity : list) {
            double d12;
            if (entity instanceof EntityParasiteBase && SRPConfig.explotionDamPara || entity.ignoreExplosion(this) || !((d12 = Math.sqrt(entity.distanceToSqr(this.x, this.y, this.z)) / (double)f3) <= 1.0)) continue;
            double d5 = entity.getX() - this.x;
            double d7 = entity.getEyeY() - this.y;
            double d9 = entity.getZ() - this.z;
            double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
            if (d13 == 0.0) continue;
            d5 /= d13;
            d7 /= d13;
            d9 /= d13;
            double d14 = Explosion.getSeenPercent(center, entity);
            double d10 = (1.0 - d12) * d14;
            entity.hurt(this.world.damageSources().explosion(this), (float)((int)((d10 * d10 + d10) / 2.0 * 7.0 * (double)f3 + 1.0)));
            double d11 = d10;
            if (entity instanceof LivingEntity living) {
                d11 = d10 * (1.0 - living.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));
            }
            Mot.addX(entity, d5 * d11);
            Mot.addY(entity, d7 * d11);
            Mot.addZ(entity, d9 * d11);
            if (!(entity instanceof Player player) || player.isSpectator() || player.isCreative() && player.getAbilities().flying) continue;
            this.getHitPlayers().put(player, new Vec3(d5 * d10, d7 * d10, d9 * d10));
        }
    }

    public void doExplosionB(boolean spawnParticles) {
        this.world.playSound(null, this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f);
        if (this.size >= 2.0f && this.damagesTerrain) {
            this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
        } else {
            this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
        }
        if (this.damagesTerrain) {
            for (BlockPos blockpos : this.affectedBlockPositions) {
                BlockState state = this.world.getBlockState(blockpos);
                Block block = state.getBlock();
                if (spawnParticles) {
                    double d0 = (float)blockpos.getX() + this.world.random.nextFloat();
                    double d1 = (float)blockpos.getY() + this.world.random.nextFloat();
                    double d2 = (float)blockpos.getZ() + this.world.random.nextFloat();
                    double d3 = d0 - this.x;
                    double d4 = d1 - this.y;
                    double d5 = d2 - this.z;
                    double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                    d3 /= d6;
                    d4 /= d6;
                    d5 /= d6;
                    double d7 = 0.5 / (d6 / (double)this.size + 0.1);
                    d7 *= (double)(this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3f);
                    this.world.addParticle(ParticleTypes.POOF, (d0 + this.x) / 2.0, (d1 + this.y) / 2.0, (d2 + this.z) / 2.0, d3 * d7, d4 * d7, d5 * d7);
                    this.world.addParticle(ParticleTypes.SMOKE, d0, d1, d2, d3, d4, d5);
                }
                if (LegacyMaterial.of(state) == LegacyMaterial.air) continue;
                if (this.shooterTwo != null) {
                    this.shooterTwo.addToBlockInv(BlockIds.stateString(state));
                }
                state.onBlockExploded(this.world, blockpos, this);
            }
        }
        if (this.causesFire) {
            for (BlockPos blockpos1 : this.affectedBlockPositions) {
                if (LegacyMaterial.of(this.world.getBlockState(blockpos1)) != LegacyMaterial.air || !this.world.getBlockState(blockpos1.below()).isCollisionShapeFullBlock(this.world, blockpos1.below()) || this.world.random.nextInt(3) != 0) continue;
                this.world.setBlockAndUpdate(blockpos1, Blocks.FIRE.defaultBlockState());
            }
        }
    }
}
