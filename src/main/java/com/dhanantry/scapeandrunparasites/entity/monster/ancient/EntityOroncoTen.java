package com.dhanantry.scapeandrunparasites.entity.monster.ancient;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class EntityOroncoTen
extends EntityParasiteBase {
    private int ticksGround;
    private int maxMobs;

    public EntityOroncoTen(EntityType<? extends EntityOroncoTen> type, Level worldIn) {
        super(type, worldIn);
        this.goalSelector.removeGoal(this.aiWander);
        this.goalSelector.removeGoal(this.folow);
        this.noCulling = true;
        this.canD = SRPConfig.ancientdespawn;
        this.killcount = -10.0;
        this.type = (byte)62;
    }

    @Override
    public int getParasiteIDRegister() {
        return 35;
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.ORONCO_HEALTH * 0.25);
        builder.add(Attributes.ARMOR, SRPAttributes.ORONCO_ARMOR * 0.25);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 2.0);
        builder.add(Attributes.FOLLOW_RANGE, 64.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.onGround()) {
            ++this.ticksGround;
            if (this.ticksGround > 200 && !this.level().isClientSide) {
                if (this.ticksGround % 20 == 0 && this.nearbydangerous() && this.maxMobs < 10) {
                    if (ParasiteSummon.SummonM((LivingEntity)this, new String[]{"srparasites:lodo;1;1"}, 2, 3, this.getTarget())) {
                        ++this.maxMobs;
                    } else if (this.maxMobs >= 10) {
                        this.discard();
                    }
                }
            } else if (this.level().isClientSide && this.ticksGround > 200) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 91, 81, 75);
                this.spawnParticles(SRPEnumParticle.GCLOUD, 164, 174, 180);
                this.spawnParticles(SRPEnumParticle.GCLOUD, 91, 81, 75);
                this.spawnParticles(SRPEnumParticle.GCLOUD, 164, 174, 180);
            }
        }
    }

    private boolean nearbydangerous() {
        int k = 0;
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(16.0);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            if (mob != this && mob instanceof EntityParasiteBase && mob.isAlive()) {
                --k;
                continue;
            }
            if (!mob.isAlive()) continue;
            ++k;
        }
        return k > 0;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.5f;
    }
}

