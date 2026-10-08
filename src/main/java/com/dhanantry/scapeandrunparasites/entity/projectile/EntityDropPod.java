package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;

public class EntityDropPod
extends EntityParasiteBase {
    private byte owner;

    public EntityDropPod(EntityType<? extends EntityDropPod> type, Level worldIn) {
        super(type, worldIn);
        this.goalSelector.removeGoal(this.aiWander);
        this.goalSelector.removeGoal(this.folow);
        this.fuseTime = 80;
        this.type = (byte)61;
        this.setParasiteStatus(1);
        this.killcount = -10.0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 34;
    }

    public EntityDropPod(EntityType<? extends EntityDropPod> type, Level worldIn, int points) {
        this(type, worldIn);
    }

    public void push(Entity entityIn) {
    }

    @Override
    protected void doPush(Entity entityIn) {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, (SRPAttributes.ORONCO_HEALTH + SRPAttributes.TERLA_HEALTH) * 0.1);
        builder.add(Attributes.ARMOR, 5.0);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 2.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.onGround()) {
            this.setParasiteStatus(0);
            this.setSelfeState(1);
            this.dyingBurst(false, 2);
        } else {
            this.particleStatus((byte)12);
        }
    }

    @Override
    protected void selfExplode() {
        boolean flag = EventHooks.canEntityGrief((Level)this.level(), (Entity)this) && SRPConfigMobs.ratholGriefing;
        ParasiteEventEntity.createExplosion(this.level(), (Entity)this, this.getX(), this.getY(), this.getZ(), 4.0f, flag);
        if (!this.level().isClientSide) {
            this.playSound(SRPSounds.RATHOL_BOOM.get(), 1.0f, 1.0f);
            this.dead = true;
            String[] here = new String[3];
            AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(7.0);
            List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (String i : SRPConfigMobs.pod1Effects) {
                here = i.split(";");
                Holder<MobEffect> potionE = SRPEntityUtil.effect(here[2]);
                if (potionE == null) continue;
                int duration = Integer.parseInt(here[0]) * 20;
                int amp = Integer.parseInt(here[1]);
                for (LivingEntity mob : moblist) {
                    if (mob == this || mob instanceof EntityParasiteBase) continue;
                    mob.addEffect(new MobEffectInstance(potionE, duration, amp, false, false));
                }
            }
            this.discard();
            this.spawnLingeringCloud();
            switch (this.owner) {
                case 62: {
                    int limit = 0;
                    int cap = 0;
                    while (limit < SRPConfigMobs.oroncoMaxMobPod && cap < 5) {
                        if (ParasiteSummon.SummonM(this, SRPConfigMobs.oroncoMobList, 1, this.getX(), this.getY(), this.getZ(), this.getTarget(), false)) {
                            ++limit;
                            continue;
                        }
                        ++cap;
                    }
                    break;
                }
                case 63: {
                    ParasiteSummon.spawnM(this, SRPConfigMobs.oroncoMobList, 0, false, SRPEntityUtil.getCustomNameTag(this));
                    break;
                }
            }
        }
    }

    private void spawnLingeringCloud() {
        AreaEffectCloud entityareaeffectcloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
        entityareaeffectcloud.setRadius(this.getBbWidth() * 2.0f);
        entityareaeffectcloud.setWaitTime(5);
        entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration());
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 0));
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
        this.level().addFreshEntity((Entity)entityareaeffectcloud);
    }

    public void setOwner(byte in) {
        this.owner = in;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 2.1f;
    }
}

