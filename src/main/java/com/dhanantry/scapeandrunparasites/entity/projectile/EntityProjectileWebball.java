package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.block.BlockWebBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.EventHooks;

public class EntityProjectileWebball
extends EntitySRPProjectile {
    private static final float WEB_BLIND_CHANCE = 0.3f;
    private static final int WEB_BLIND_TICKS = 60;
    private static final int WEB_BLIND_AMPLIFIER = 0;
    private byte type;

    public EntityProjectileWebball(EntityType<? extends EntityProjectileWebball> type, Level worldIn) {
        super(type, worldIn);
        this.type = 1;
    }

    public EntityProjectileWebball(EntityType<? extends EntityProjectileWebball> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, worldIn, shooter, accelX, accelY, accelZ);
        this.type = 1;
    }

    public EntityProjectileWebball(EntityType<? extends EntityProjectileWebball> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ, int t) {
        super(type, worldIn, shooter, accelX, accelY, accelZ);
        this.type = (byte)t;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.POOF;
    }

    protected void onHit(HitResult result) {
        if (this.level().isClientSide) {
            return;
        }
        boolean griefing = EventHooks.canEntityGrief((Level)this.level(), (Entity)this);
        Consumer<BlockPos> tryPlaceWeb = pos -> {
            if (griefing && this.level().isEmptyBlock(pos)) {
                this.level().setBlock(pos, Blocks.COBWEB.defaultBlockState(), 3);
            }
        };
        if (SRPEntityUtil.hitEntity(result) instanceof LivingEntity) {
            LivingEntity target = (LivingEntity)SRPEntityUtil.hitEntity(result);
            if (target instanceof Player && !target.hasEffect(MobEffects.BLINDNESS) && this.getRandom().nextFloat() < 0.3f) {
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, true));
            }
            BlockPos feet = BlockPos.containing(target.getX(), Math.floor(target.getBoundingBox().minY), target.getZ());
            tryPlaceWeb.accept(feet);
        } else if (result instanceof BlockHitResult blockHit) {
            BlockPos placePos = blockHit.getBlockPos().relative(blockHit.getDirection());
            tryPlaceWeb.accept(placePos);
        }
        this.level().broadcastEntityEvent((Entity)this, (byte)3);
        this.discard();
    }

    public void tick() {
        super.tick();
        if (this.tickCount > 60) {
            this.setWebsAround();
            this.discard();
        }
    }

    private void setWebsAround() {
        int totalWebs = this.getRandom().nextInt(3) + 1;
        int[] positionss = new int[]{-1, 0, 1};
        block5: for (int i = 1; i <= totalWebs; ++i) {
            int poz;
            int poy;
            int pox = positionss[this.getRandom().nextInt(3)];
            if (this.level().getBlockState(BlockPos.containing(this.getX() + (double)pox, this.getY() + (double)(poy = positionss[this.getRandom().nextInt(3)]), this.getZ() + (double)(poz = positionss[this.getRandom().nextInt(3)]))).getBlock() != Blocks.AIR) continue;
            switch (this.type) {
                case 1: {
                    this.level().setBlockAndUpdate(BlockPos.containing(this.getX() + (double)pox, this.getY() + (double)poy, this.getZ() + (double)poz), SRPBlocks.SRPWeb.get().defaultBlockState());
                    continue block5;
                }
                case 2: {
                    this.level().setBlockAndUpdate(BlockPos.containing(this.getX() + (double)pox, this.getY() + (double)poy, this.getZ() + (double)poz), SRPBlocks.SRPWeb.get().defaultBlockState().setValue(BlockWebBase.VARIANT, (BlockWebBase.EnumType.TWO)));
                    continue block5;
                }
                case 3: {
                    this.level().setBlockAndUpdate(BlockPos.containing(this.getX() + (double)pox, this.getY() + (double)poy, this.getZ() + (double)poz), SRPBlocks.SRPWeb.get().defaultBlockState().setValue(BlockWebBase.VARIANT, (BlockWebBase.EnumType.THREE)));
                }
            }
        }
    }
}

