package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityParasiticScent;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCrude;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class EntityHeed
extends EntityPCrude
implements EntityBodyParts {
    private EntityBody head;
    private int scentCool;
    private int border;
    private boolean skillThrow;

    public EntityHeed(EntityType<? extends EntityHeed> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.noCulling = true;
        this.type = (byte)31;
        this.skillThrow = false;
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, !SRPConfigSystems.useOneMind, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        }
        this.head = new EntityBody(this, 1.8f, 1.8f, 1.0f, 2.1f, 0.3f, 1, 1, false, 0.2f);
        this.scentCool = 1000;
        this.attackSpeedT = 10;
    }

    @Override
    public int getParasiteIDRegister() {
        return 63;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.095));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 200, 20, false, 1));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 2, 16));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPCrude.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.HEED_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.HEED_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.32);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.HEED_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.HEED_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.primitiveFollow);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        --this.scentCool;
        if (this.scentCool < 0 && this.srpTicks == 10 && SRPConfigSystems.useScent && this.getLevelCreated() >= SRPConfigSystems.deveScentUse) {
            List serverList = SRPEntityUtil.allEntities(this.level());
            int count = 0;
            for (int x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityParasiticScent)) continue;
                ++count;
            }
            if (count > SRPConfigSystems.scentCap) {
                return;
            }
            if (this.getTarget() != null) {
                EntityParasiticScent sss = new EntityParasiticScent(SRPEntities.SCENT.get(), this.level());
                sss.copyPosition((Entity)this.getTarget());
                sss.setTargetToKill(this.getTarget(), false);
                sss.setDieToE(true);
                sss.setCanFollow(true);
                this.level().addFreshEntity((Entity)sss);
                this.scentCool = 1000;
            }
        }
        this.head.tick();
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.getRandom().nextBoolean()) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)this, 80, 0);
        }
        return this.hurt(source, amount * 3.0f);
    }

    @Override
    public void setBodyPartDead(int id) {
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.head.discard();
        }
        super.discard();
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.5f;
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillThrow;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillThrow = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.throwBlock();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void throwBlock() {
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(1.5);
        List<? extends EntityParasiteBase> moblist = this.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        for (EntityParasiteBase entityIn : moblist) {
            if (entityIn == this || !SRPConfigSystems.rageEnable) continue;
            entityIn.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 1200, 0, false, false));
        }
        this.skillThrow = true;
        this.setParasiteStatus(0);
        this.border = 0;
    }
}

