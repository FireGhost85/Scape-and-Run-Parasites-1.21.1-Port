package com.dhanantry.scapeandrunparasites.entity.monster;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class EntityBiomass
extends EntityParasiteBase {
    private int fuse;
    private EntityParasiteBase entityin;
    private LivingEntity target;
    private String parasite;
    private int point;
    private static final EntityDataAccessor<Float> STAGE = SynchedEntityData.defineId(EntityBiomass.class, EntityDataSerializers.FLOAT);
    private boolean payfather;
    private float currentWidth;
    private float currentHeight;
    private float capWidth;
    private float capHeight;
    private float growWidth;
    private float growHeight;

    public EntityBiomass(EntityType<? extends EntityBiomass> type, Level worldIn) {
        super(type, worldIn);
        this.goalSelector.removeGoal(this.folow);
        this.goalSelector.removeGoal(this.aiWander);
        this.fuse = 777;
        this.type = (byte)100;
        this.currentWidth = 0.98f;
        this.currentHeight = 0.98f;
        this.capWidth = 0.98f;
        this.capHeight = 0.98f;
    }

    public EntityBiomass(EntityType<? extends EntityBiomass> type, Level worldIn, double x, double y, double z) {
        this(type, worldIn);
        this.setPos(x, y, z);
        float f = (float)(Math.random() * (Math.PI * 2));
        Mot.setX(this, -((float)Math.sin(f)) * 0.02f);
        Mot.setY(this, 0.2f);
        Mot.setZ(this, -((float)Math.cos(f)) * 0.02f);
        this.setFuse(80);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    public EntityBiomass(EntityType<? extends EntityBiomass> type, Level worldIn, EntityParasiteBase fatherEntity, float stage, LivingEntity target, boolean payfather) {
        this(type, worldIn);
        this.entityin = fatherEntity;
        this.setStage(stage);
        this.target = target;
        this.payfather = payfather;
    }

    public EntityBiomass(EntityType<? extends EntityBiomass> type, Level worldIn, EntityParasiteBase entityin, LivingEntity target) {
        this(type, worldIn);
        this.entityin = entityin;
        this.target = target;
        this.setStage(1.0f);
        this.payfather = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 205;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance >= 18.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, 15.0);
        builder.add(Attributes.ARMOR, 0.0);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.ATTACK_DAMAGE, 0.0);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, 0.0);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STAGE, (float) (Float.valueOf(0.0f)));
    }

    @Override
    public void aiStep() {
        block34: {
            block33: {
                super.aiStep();
                if (!this.level().isClientSide) {
                    if (!this.isRemoved() && this.getHealth() > 0.0f) {
                        if (this.getHealth() < this.getMaxHealth()) {
                            this.setHealth(this.getHealth() + 0.1f);
                        }
                    } else {
                        return;
                    }
                }
                if (!this.level().isClientSide) break block33;
                switch ((int)this.getStage()) {
                    case 1: {
                        if (this.tickCount % 10 != 0) break;
                        for (int i = 0; i <= 1; ++i) {
                            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                        }
                        break block34;
                    }
                    case 2: {
                        if (this.tickCount % 5 != 0) break;
                        for (int i = 0; i <= 1; ++i) {
                            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                        }
                        break block34;
                    }
                    case 3: {
                        if (this.tickCount % 3 != 0) break;
                        for (int i = 0; i <= 2; ++i) {
                            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                        }
                        break block34;
                    }
                    case 4: {
                        if (this.tickCount % 10 != 0) break;
                        for (int i = 0; i <= 1; ++i) {
                            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                        }
                        break block34;
                    }
                    case 5: {
                        if (this.tickCount % 10 != 0) break;
                        for (int i = 0; i <= 1; ++i) {
                            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                        }
                        break block34;
                    }
                    case 6: {
                        if (this.tickCount % 5 != 0) break;
                        for (int i = 0; i <= 1; ++i) {
                            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                        }
                        break;
                    }
                }
                break block34;
            }
            if (this.tickCount % 10 == 0) {
                this.applyCOTH(2);
            }
        }
        if (this.onGround()) {
            --this.fuse;
        }
        if (this.tickCount >= 200) {
            --this.fuse;
        }
        if (this.fuse <= 0) {
            this.particleStatus((byte)7);
            this.discard();
            if (!this.level().isClientSide) {
                this.explode();
                this.level().broadcastEntityEvent((Entity)this, (byte)18);
            }
        } else if (this.onGround()) {
            switch ((int)this.getStage()) {
                case 1: {
                    this.growWidth += 0.005f;
                    this.growHeight += 0.006f;
                    this.level().broadcastEntityEvent((Entity)this, (byte)11);
                    break;
                }
                case 2: {
                    this.growWidth += 0.007f;
                    this.growHeight += 0.009f;
                    this.level().broadcastEntityEvent((Entity)this, (byte)12);
                    break;
                }
                case 3: {
                    this.growWidth += 0.015f;
                    this.growHeight += 0.02f;
                    this.level().broadcastEntityEvent((Entity)this, (byte)13);
                    break;
                }
                case 4: {
                    this.growWidth += 0.007f;
                    this.growHeight += 0.009f;
                    this.level().broadcastEntityEvent((Entity)this, (byte)15);
                    break;
                }
                case 5: {
                    this.growWidth += 0.005f;
                    this.growHeight += 0.006f;
                    this.level().broadcastEntityEvent((Entity)this, (byte)16);
                    break;
                }
                case 6: {
                    this.growWidth += 0.007f;
                    this.growHeight += 0.009f;
                    this.level().broadcastEntityEvent((Entity)this, (byte)17);
                }
            }
        }
    }

    public void applyCOTH(int r) {
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate((double)r);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            if (mob == this || mob instanceof EntityParasiteBase) continue;
            mob.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 200, 1, false, false));
        }
    }

    public float getGrowW() {
        return this.growWidth;
    }

    public float getGrowHeight() {
        return this.growHeight;
    }

    private boolean explode() {
        this.applyCOTH(2);
        this.playSound(SRPSounds.FLESH_PRIMITIVE.get(), 1.0f, 1.0f);
        if (this.fuse == 777) {
            return false;
        }
        if (this.entityin == null) {
            return this.explode2();
        }
        Mob entityout = (Mob)SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(this.parasite), (Level)this.level());
        if (entityout == null) {
            return false;
        }
        entityout.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue((double)(16.0f + (this.getStage() - 1.0f) * 8.0f));
        entityout.moveTo(this.getX(), this.getY(), this.getZ(), this.entityin.getYRot(), this.entityin.getXRot());
        entityout.finalizeSpawn((ServerLevel) entityout.level(), this.entityin.level().getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null);
        if (SRPConfigSystems.rageEnable) {
            entityout.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 1200, 1, false, false));
        }
        entityout.addEffect(new MobEffectInstance(SRPPotions.DEBAR_E, 120000, 1, false, false));
        if (this.isOnFire()) {
            entityout.setHealth(entityout.getMaxHealth() * 0.5f);
            entityout.igniteForSeconds(8);
        }
        this.entityin.level().addFreshEntity((Entity)entityout);
        if (this.target != null) {
            entityout.setTarget(this.target);
        }
        if (this.payfather && this.entityin instanceof EntityCanSummon) {
            EntityCanSummon father = (EntityCanSummon)(this.entityin);
            father.setActualParasites(this.point);
            father.addID(entityout.getId(), this.point);
        }
        return true;
    }

    private boolean explode2() {
        if (this.fuse == 777 || this.parasite == null) {
            return false;
        }
        Mob entityout = (Mob)SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(this.parasite), (Level)this.level());
        if (entityout == null) {
            return false;
        }
        entityout.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue((double)(16.0f + (this.getStage() - 1.0f) * 8.0f));
        entityout.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        entityout.finalizeSpawn((ServerLevel) entityout.level(), this.level().getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null);
        if (SRPConfigSystems.rageEnable) {
            entityout.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 1200, 1, false, false));
        }
        entityout.addEffect(new MobEffectInstance(SRPPotions.DEBAR_E, 120000, 1, false, false));
        if (this.isOnFire()) {
            entityout.setHealth(entityout.getMaxHealth() * 0.5f);
            entityout.igniteForSeconds(8);
        }
        this.level().addFreshEntity((Entity)entityout);
        if (this.target != null) {
            entityout.setTarget(this.target);
        }
        return true;
    }

    private void explotionParticles() {
        double b = 0.0;
        switch ((int)this.getStage()) {
            case 2: {
                b = 0.3;
                break;
            }
            case 3: {
                b = 0.6;
                break;
            }
            case 4: {
                b = 0.3;
                break;
            }
            case 6: {
                b = 0.3;
            }
        }
        for (int i = 0; i <= 10; ++i) {
            double d0 = (float)this.getX() + this.getRandom().nextFloat();
            double d1 = (double)((float)this.getY() + this.getRandom().nextFloat()) + 1.0;
            double d2 = (float)this.getZ() + this.getRandom().nextFloat();
            double d3 = d0 - this.getX();
            double d4 = d1 - this.getY();
            double d5 = d2 - this.getZ();
            double d6 = (float)Math.sqrt((double)(d3 * d3 + d4 * d4 + d5 * d5));
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.5 / (d6 / 1.0 + 0.1);
            d3 = d3 * (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.7f)) * b;
            d4 = d4 * d7 * (b * 0.5);
            d5 = d5 * d7 * b;
            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0, d0, d1, d2, d3, d4, d5);
        }
    }

    public void setMotion(double xSpeedIn, double ySpeedIn, double zSpeedIn, double capX, double capY) {
        xSpeedIn = Math.min(xSpeedIn, capX);
        ySpeedIn = Math.min(ySpeedIn, capY);
        zSpeedIn = Math.min(zSpeedIn, capX);
        Mot.setX(this, xSpeedIn * (Math.random() * 2.0 - 1.0));
        Mot.setY(this, ySpeedIn);
        Mot.setZ(this, zSpeedIn * (Math.random() * 2.0 - 1.0));
    }

    public void setSizeGrow(float width, float height, float capWidht, float capHeight) {
        this.currentWidth = width;
        this.currentHeight = height;
        this.capWidth = capWidht;
        this.capHeight = capHeight;
    }

    public void setFuse(int fuseIn) {
        this.fuse = fuseIn;
    }

    public void setParasite(String in, int points) {
        this.parasite = in;
        this.point = points;
    }

    public int getFuse() {
        return this.fuse;
    }

    @Override
    public int getSkin() {
        return super.getSkin();
    }

    @Override
    public void setSkin(int texture) {
        super.setSkin(texture);
    }

    public float getStage() {
        return ((Float)this.entityData.get(STAGE)).floatValue();
    }

    public void setStage(float in) {
        this.entityData.set(STAGE, (float) (Float.valueOf(in)));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putShort("Fuse", (short)this.getFuse());
        if (this.entityin != null) {
            compound.putInt("parasitefather", this.entityin.getId());
        }
        if (this.target != null) {
            compound.putInt("parasitetarget", this.target.getId());
        }
        if (this.parasite != null) {
            compound.putString("parasiteparasite", this.parasite);
        }
        compound.putInt("parasitepoint", this.point);
        compound.putBoolean("parasitepayfather", this.payfather);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFuse(compound.getShort("Fuse"));
        if (compound.contains("parasitefather", 99) && this.level().getEntity(compound.getInt("parasitefather")) instanceof EntityParasiteBase) {
            this.entityin = (EntityParasiteBase)this.level().getEntity(compound.getInt("parasitefather"));
        }
        if (compound.contains("parasitetarget", 99)) {
            this.target = (LivingEntity)this.level().getEntity(compound.getInt("parasitetarget"));
        }
        if (compound.contains("parasiteparasite", 8)) {
            this.parasite = compound.getString("parasiteparasite").toString();
        }
        if (compound.contains("parasitepoint", 99)) {
            this.point = compound.getInt("parasitepoint");
        }
        if (compound.contains("parasitepayfather", 99)) {
            this.payfather = compound.getBoolean("parasitepayfather");
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 11) {
            this.growWidth += 0.005f;
            this.growHeight += 0.006f;
        } else if (id == 12) {
            this.growWidth += 0.01f;
            this.growHeight += 0.012f;
        } else if (id == 13) {
            this.growWidth += 0.001f;
            this.growHeight += 0.001f;
        } else if (id == 15) {
            this.growWidth += 0.01f;
            this.growHeight += 0.012f;
        } else if (id == 16) {
            this.growWidth += 0.005f;
            this.growHeight += 0.006f;
        } else if (id == 17) {
            this.growWidth += 0.01f;
            this.growHeight += 0.012f;
        } else if (id == 18) {
            this.explotionParticles();
        } else {
            super.handleEntityEvent(id);
        }
    }
}

