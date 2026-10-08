package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanPullMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLeer;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

public class EntityProjectilePullball
extends EntitySRPProjectile {
    private EntityCanPullMobs spider;

    public EntityProjectilePullball(EntityType<? extends EntityProjectilePullball> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityProjectilePullball(EntityType<? extends EntityProjectilePullball> type, Level worldIn, EntityParasiteBase shooter, double accelX, double accelY, double accelZ) {
        super(type, worldIn, (LivingEntity)shooter, accelX, accelY, accelZ);
        this.spider = (EntityCanPullMobs)(shooter);
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.POOF;
    }

    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            if (SRPEntityUtil.hitEntity(result) != null) {
                if (SRPEntityUtil.hitEntity(result) instanceof LivingEntity) {
                    // empty if block
                }
            } else {
                this.setWebsAround();
            }
            this.discard();
        }
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.tickCount == 5) {
                this.accelerationPower *= (double)this.spider.getAcceleration();
            }
            if (this.getOwner() == null) {
                this.discard();
                return;
            }
            if (this.spider.hasTargetedEntity() && !(this.getOwner() instanceof EntityLeer)) {
                this.discard();
                return;
            }
            AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(2.0);
            List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity mob : moblist) {
                if (mob instanceof EntityParasiteBase || !((LivingEntity)this.getOwner()).hasLineOfSight((Entity)mob) || !this.spider.checkAttackTarget(mob) || !mob.isAlive()) continue;
                this.spider.setPStatus(3);
                this.spider.setPullingMobEffects(mob);
                this.spider.setTargetedEntity(mob.getId());
                this.spider.resetPullSkill();
                this.discard();
            }
        }
    }

    private void setWebsAround() {
        int totalWebs = this.getRandom().nextInt(3) + 1;
        int[] positionss = new int[]{-1, 0, 1};
        for (int i = 1; i <= totalWebs; ++i) {
            int poz;
            int poy;
            int pox = positionss[this.getRandom().nextInt(3)];
            if (this.level().getBlockState(BlockPos.containing(this.getX() + (double)pox, this.getY() + (double)(poy = positionss[this.getRandom().nextInt(3)]), this.getZ() + (double)(poz = positionss[this.getRandom().nextInt(3)]))).getBlock() != Blocks.AIR) continue;
            this.level().setBlockAndUpdate(BlockPos.containing(this.getX() + (double)pox, this.getY() + (double)poy, this.getZ() + (double)poz), SRPBlocks.SRPWeb.get().defaultBlockState());
        }
    }
}

