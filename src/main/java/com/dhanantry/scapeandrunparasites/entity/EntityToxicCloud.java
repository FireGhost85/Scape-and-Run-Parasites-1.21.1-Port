package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;

/** The poison/vomit cloud of the carriers and projectiles (1.12: a copy of EntityAreaEffectCloud with extra damage). */
public class EntityToxicCloud
extends Entity {
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(EntityToxicCloud.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIG = SynchedEntityData.defineId(EntityToxicCloud.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Byte> PART = SynchedEntityData.defineId(EntityToxicCloud.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> IGNORE_RADIUS = SynchedEntityData.defineId(EntityToxicCloud.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ParticleOptions> PARTICLE = SynchedEntityData.defineId(EntityToxicCloud.class, EntityDataSerializers.PARTICLE);
    private PotionContents potionContents = PotionContents.EMPTY;
    private final List<MobEffectInstance> effects = new ArrayList<>();
    private final Map<Entity, Integer> reapplicationDelayMap = new HashMap<>();
    private int duration = 600;
    private int waitTime = 20;
    private int reapplicationDelay = 20;
    private float radiusOnUse;
    private float radiusPerTick;
    private EntityParasiteBase owner;
    private UUID ownerUniqueId;
    public int effectParticles;

    public EntityToxicCloud(EntityType<? extends EntityToxicCloud> type, Level worldIn) {
        super(type, worldIn);
        this.noPhysics = true;
        this.setRadius(3.0f, 0.5f);
    }

    public EntityToxicCloud(EntityType<? extends EntityToxicCloud> type, Level worldIn, double x, double y, double z) {
        this(type, worldIn);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PART, (byte) (0));
        builder.define(RADIUS, 0.5f);
        builder.define(HEIG, 0.5f);
        builder.define(IGNORE_RADIUS, Boolean.FALSE);
        builder.define(PARTICLE, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, -1));
    }

    /** The 1.12 setSize(radius * 2, height): the dimensions are read from the synched radius and height. */
    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0f, this.getHeight());
    }

    public void setRadius(float radiusIn, float heightIn) {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        if (!this.level().isClientSide) {
            this.entityData.set(RADIUS, radiusIn);
            this.entityData.set(HEIG, heightIn);
        }
        this.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public float getRadius() {
        return this.entityData.get(RADIUS);
    }

    public float getHeight() {
        return this.entityData.get(HEIG);
    }

    public void setPotionContents(PotionContents contents) {
        this.potionContents = contents;
    }

    public void addEffect(MobEffectInstance effect) {
        this.effects.add(effect);
    }

    public byte getColor() {
        return this.entityData.get(PART);
    }

    public void setColor(int colorIn) {
        this.entityData.set(PART, (byte) colorIn);
    }

    public ParticleOptions getParticle() {
        return this.entityData.get(PARTICLE);
    }

    public void setParticle(ParticleOptions particleIn) {
        this.entityData.set(PARTICLE, particleIn);
    }

    protected void setIgnoreRadius(boolean ignoreRadius) {
        this.entityData.set(IGNORE_RADIUS, ignoreRadius);
    }

    public boolean shouldIgnoreRadius() {
        return this.entityData.get(IGNORE_RADIUS);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int durationIn) {
        this.duration = durationIn;
    }

    @Override
    public void tick() {
        super.tick();
        boolean ignoreRadius = this.shouldIgnoreRadius();
        float f = this.getRadius();
        if (this.level().isClientSide) {
            this.tickClientParticles(ignoreRadius, f);
            return;
        }
        if (this.tickCount >= this.waitTime + this.duration) {
            this.discard();
            return;
        }
        boolean waiting = this.tickCount < this.waitTime;
        if (ignoreRadius != waiting) {
            this.setIgnoreRadius(waiting);
        }
        if (waiting) {
            return;
        }
        if (this.radiusPerTick != 0.0f) {
            f += this.radiusPerTick;
            if (f < 0.5f) {
                this.discard();
                return;
            }
            this.setRadius(f, this.getHeight());
        }
        if (this.tickCount % 5 != 0) {
            return;
        }
        this.reapplicationDelayMap.entrySet().removeIf(entry -> this.tickCount >= entry.getValue());
        List<MobEffectInstance> potions = new ArrayList<>();
        for (MobEffectInstance potioneffect1 : this.potionContents.getAllEffects()) {
            potions.add(new MobEffectInstance(potioneffect1.getEffect(), potioneffect1.getDuration() / 4, potioneffect1.getAmplifier(), potioneffect1.isAmbient(), potioneffect1.isVisible()));
        }
        potions.addAll(this.effects);
        if (potions.isEmpty()) {
            this.reapplicationDelayMap.clear();
            return;
        }
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
        for (LivingEntity entitylivingbase : list) {
            boolean pb = entitylivingbase instanceof EntityParasiteBase;
            if (this.owner != null) {
                if (!pb) {
                    this.owner.attackEntityAsMobMinimum(entitylivingbase, this.owner.getMiniDamage() / 2.0f);
                }
                if (!(entitylivingbase instanceof EntityPCosmical)) {
                    entitylivingbase.hurt(this.damageSources().dragonBreath(), 10.0f);
                }
            }
            if (pb || this.reapplicationDelayMap.containsKey(entitylivingbase) || !entitylivingbase.isAffectedByPotions()) continue;
            double d0 = entitylivingbase.getX() - this.getX();
            double d1 = entitylivingbase.getZ() - this.getZ();
            if (!(d0 * d0 + d1 * d1 <= (double)(f * f))) continue;
            this.reapplicationDelayMap.put(entitylivingbase, this.tickCount + this.reapplicationDelay);
            for (MobEffectInstance potioneffect : potions) {
                if (potioneffect.getEffect().value().isInstantenous()) {
                    potioneffect.getEffect().value().applyInstantenousEffect(this, this.getOwner(), entitylivingbase, potioneffect.getAmplifier(), 0.5);
                    continue;
                }
                SRPPotions.applyStackPotion(potioneffect.getEffect(), entitylivingbase, potioneffect.getDuration(), potioneffect.getAmplifier());
            }
            if (this.radiusOnUse == 0.0f) continue;
            f += this.radiusOnUse;
            if (f < 0.5f) {
                this.discard();
                return;
            }
            this.setRadius(f, this.getHeight());
        }
    }

    private void tickClientParticles(boolean ignoreRadius, float f) {
        ParticleOptions particle = this.getParticle();
        boolean effect = particle.getType() == ParticleTypes.ENTITY_EFFECT;
        if (ignoreRadius) {
            if (this.getRandom().nextInt(3) == 0) {
                return;
            }
            for (int i = 0; i < 2; ++i) {
                float f1 = this.getRandom().nextFloat() * ((float)Math.PI * 2);
                float f2 = Mth.sqrt(this.getRandom().nextFloat()) * 0.2f;
                float f3 = Mth.cos(f1) * f2;
                float f4 = Mth.sin(f1) * f2;
                double x = this.getX() + (double)f3;
                double y = this.getY() + (double)(this.getRandom().nextFloat() * this.getBbHeight());
                double z = this.getZ() + (double)f4;
                if (effect) {
                    int j = this.getRandom().nextBoolean() ? 0xFFFFFF : (int)this.getColor();
                    int k = j >> 16 & 0xFF;
                    int l = j >> 8 & 0xFF;
                    int i1 = j & 0xFF;
                    this.level().addAlwaysVisibleParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, (float)k / 255.0f, (float)l / 255.0f, (float)i1 / 255.0f), x, y, z, 0.0, 0.0, 0.0);
                    continue;
                }
                this.level().addAlwaysVisibleParticle(particle, x, y, z, 0.0, 0.0, 0.0);
            }
            return;
        }
        float f5 = (float)Math.PI * f * f;
        for (int k1 = 0; (float)k1 < f5; ++k1) {
            if (this.getRandom().nextInt(3) != 0) continue;
            float f6 = this.getRandom().nextFloat() * ((float)Math.PI * 2);
            float f7 = Mth.sqrt(this.getRandom().nextFloat()) * f;
            float f8 = Mth.cos(f6) * f7;
            float f9 = Mth.sin(f6) * f7;
            if (effect) {
                byte l1 = this.getColor();
                int i2 = l1 >> 16 & 0xFF;
                int j2 = l1 >> 8 & 0xFF;
                int j1 = l1 & 0xFF;
                double d11 = this.getX() + (double)f8 * 1.0;
                double d22 = this.getY() + (double)(this.getRandom().nextFloat() * this.getBbHeight());
                double d33 = this.getZ() + (double)f9 * 1.0;
                double d44 = (float)i2 / 255.0f;
                double d55 = (float)j2 / 255.0f;
                double d66 = (float)j1 / 255.0f;
                if (this.effectParticles == 1) {
                    this.level().addAlwaysVisibleParticle(ParticleTypes.FLAME, d11, d22, d33, d44, d55, d66);
                    this.spawnParticles(SRPEnumParticle.GCLOUD, 0, 0, 0, d11, d22 + 0.5, d33, d44, d55, d66);
                } else {
                    this.level().addAlwaysVisibleParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, (float)d44, (float)d55, (float)d66), d11, d22, d33, 0.0, 0.0, 0.0);
                }
            } else {
                this.level().addAlwaysVisibleParticle(particle, this.getX() + (double)f8, this.getY() + (double)(this.getRandom().nextFloat() * this.getBbHeight()), this.getZ() + (double)f9, (0.5 - this.getRandom().nextDouble()) * 0.15, (double)0.01f, (0.5 - this.getRandom().nextDouble()) * 0.15);
            }
        }
    }

    public void setRadiusOnUse(float radiusOnUseIn) {
        this.radiusOnUse = radiusOnUseIn;
    }

    public void setRadiusPerTick(float radiusPerTickIn) {
        this.radiusPerTick = radiusPerTickIn;
    }

    public void setWaitTime(int waitTimeIn) {
        this.waitTime = waitTimeIn;
    }

    public void setOwner(@Nullable EntityParasiteBase ownerIn) {
        this.owner = ownerIn;
        this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUUID();
    }

    @Nullable
    public EntityParasiteBase getOwner() {
        Entity entity;
        if (this.owner == null && this.ownerUniqueId != null && this.level() instanceof ServerLevel && (entity = ((ServerLevel)this.level()).getEntity(this.ownerUniqueId)) instanceof EntityParasiteBase) {
            this.owner = (EntityParasiteBase)entity;
        }
        return this.owner;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 77) {
            this.effectParticles = 1;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.tickCount = compound.getInt("Age");
        this.duration = compound.getInt("Duration");
        this.waitTime = compound.getInt("WaitTime");
        this.reapplicationDelay = compound.getInt("ReapplicationDelay");
        this.radiusOnUse = compound.getFloat("RadiusOnUse");
        this.radiusPerTick = compound.getFloat("RadiusPerTick");
        this.setRadius(compound.getFloat("Radius"), compound.getFloat("heig"));
        if (compound.hasUUID("OwnerUUID")) {
            this.ownerUniqueId = compound.getUUID("OwnerUUID");
        }
        RegistryOps<Tag> ops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        if (compound.contains("Particle", 10)) {
            ParticleTypes.CODEC.parse(ops, compound.get("Particle"))
                    .resultOrPartial(err -> ScapeAndRunParasites.LOGGER.warn("Failed to parse toxic cloud particle: '{}'", err))
                    .ifPresent(this::setParticle);
        }
        if (compound.contains("Color", 99)) {
            this.setColor(compound.getInt("Color"));
        }
        if (compound.contains("potion_contents")) {
            PotionContents.CODEC.parse(ops, compound.get("potion_contents"))
                    .resultOrPartial(err -> ScapeAndRunParasites.LOGGER.warn("Failed to parse toxic cloud potion: '{}'", err))
                    .ifPresent(this::setPotionContents);
        }
        if (compound.contains("Effects", 9)) {
            ListTag nbttaglist = compound.getList("Effects", 10);
            this.effects.clear();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                MobEffectInstance.CODEC.parse(ops, nbttaglist.getCompound(i))
                        .resultOrPartial(err -> ScapeAndRunParasites.LOGGER.warn("Failed to parse toxic cloud effect: '{}'", err))
                        .ifPresent(this::addEffect);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Age", this.tickCount);
        compound.putInt("Duration", this.duration);
        compound.putInt("WaitTime", this.waitTime);
        compound.putInt("ReapplicationDelay", this.reapplicationDelay);
        compound.putFloat("RadiusOnUse", this.radiusOnUse);
        compound.putFloat("RadiusPerTick", this.radiusPerTick);
        compound.putFloat("Radius", this.getRadius());
        compound.putFloat("heig", this.getHeight());
        compound.putInt("Color", this.getColor());
        RegistryOps<Tag> ops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        compound.put("Particle", ParticleTypes.CODEC.encodeStart(ops, this.getParticle()).getOrThrow());
        if (this.ownerUniqueId != null) {
            compound.putUUID("OwnerUUID", this.ownerUniqueId);
        }
        if (!this.potionContents.equals(PotionContents.EMPTY)) {
            compound.put("potion_contents", PotionContents.CODEC.encodeStart(ops, this.potionContents).getOrThrow());
        }
        if (!this.effects.isEmpty()) {
            ListTag nbttaglist = new ListTag();
            for (MobEffectInstance potioneffect : this.effects) {
                nbttaglist.add(MobEffectInstance.CODEC.encodeStart(ops, potioneffect).getOrThrow());
            }
            compound.put("Effects", nbttaglist);
        }
    }

    public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b, double d0, double d1, double d2, double d3, double d4, double d5) {
        ParticleSpawner.spawnParticle(particleType, d0, d1, d2, d3, d4, d5, r, g, b);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (RADIUS.equals(key) || HEIG.equals(key)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }
}
