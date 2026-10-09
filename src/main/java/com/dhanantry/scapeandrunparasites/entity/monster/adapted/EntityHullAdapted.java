package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.EntityBodyDeadPayload;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EntityHullAdapted
extends EntityPAdapted
implements EntityBodyParts {
    private static final double HULL_SEARCH_RADIUS = 48.0;
    private static final int HULL_FAKE_COOLDOWN_MIN_T = 400;
    private static final int HULL_FAKE_COOLDOWN_MAX_T = 600;
    private static final boolean HULL_FAKE_DEBUG = false;
    private static final boolean HULL_FAKE_FORCE_ENABLE = true;
    private static final double HULL_FAKE_MIN_DIST = 22.0;
    private static final double HULL_FAKE_MAX_DIST = 46.0;
    private static final float HULL_FAKE_VOLUME = 1.0f;
    private static final float HULL_FAKE_PITCH_JIT = 0.1f;
    private static final SoundEvent[] HULL_FAKE_SOUNDS = new SoundEvent[]{SoundEvents.ENDERMAN_SCREAM, SoundEvents.ANVIL_LAND, SoundEvents.SPIDER_AMBIENT, SoundEvents.GRASS_STEP};
    private int hullFakeCooldown = 0;
    private EntityBody leftTendril;
    private EntityBody rightTendril;
    private float leftTendrilHealth;
    private float rightTendrilHealth;
    private int timer = 0;
    private static final EntityDataAccessor<Boolean> CAM = SynchedEntityData.defineId(EntityHullAdapted.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(EntityHullAdapted.class, EntityDataSerializers.INT);
    private LivingEntity targetedEntity;
    private int pulling;
    private boolean canPull;
    private static final int CLOAK_WARMUP_TICKS = 40;
    private static final int DECLOAK_WARMUP_TICKS = 40;
    private static final float VIB_FREQ_MIN_HZ = 12.0f;
    private static final float VIB_FREQ_MAX_HZ = 108.0f;
    private static final float VIB_MAX_AMP_BLOCKS = 0.09f;
    private static final float VIB_JITTER_FRAC = 0.35f;
    private static final float VIB_MAX_RADIUS = 0.12f;
    private static final float VIB_AXIS_DRIFT_DEG = 3000.0f;
    private static final float VIB_PHASE_JUMP_PROB = 3000.12f;
    private static final float VIB_BURST_PROB = 3000.12f;
    private static final float VIB_STUTTER_PROB = 3000.1f;
    private CloakPhase cloakPhase = CloakPhase.IDLE;
    private int cloakPhaseTicks = 0;
    private double vibAxisX = 0.0;
    private double vibAxisZ = 0.0;
    private double vibLastX = 0.0;
    private double vibLastZ = 0.0;
    private double vibPhase = 0.0;
    private int vibStutterTicks = 0;
    private double vibHoldX = 0.0;
    private double vibHoldZ = 0.0;
    private static final boolean HULL_SOUND_DEBUG = false;
    private boolean hullDumpedSounds = false;

    public EntityHullAdapted(EntityType<? extends EntityHullAdapted> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.adaptationCap = 0.95f;
        this.attackSpeedT = 4;
        this.leftTendril = new EntityBody(this, 0.6f, 1.1f, 1.0f, 0.9f, 2.1f, 1, 1, true);
        this.rightTendril = new EntityBody(this, 0.6f, 1.1f, 1.0f, 0.9f, 2.1f, -1, 2, true);
        this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
    }

    @Override
    public int getParasiteIDRegister() {
        return 52;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CAM, false);
        builder.define(TARGET_ENTITY, 0);
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.11));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(2, new EntityAIEvade(this, 25, 10, 4.0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        if (SRPConfig.parasiteGenResidue) {
            this.goalSelector.addGoal(9, new EntityAIBlockResidue(this, 2));
        }
    }

    private void hullBroadcast(String msg) {
    }

    public void setInvisible(boolean invisible) {
        if (!this.level().isClientSide) {
            boolean currentlyInvisible = super.isInvisible();
            if (invisible && !currentlyInvisible) {
                if (this.cloakPhase != CloakPhase.PRE_CLOAK && this.cloakPhase != CloakPhase.CLOAKED) {
                    this.startPreCloak();
                    return;
                }
            } else if (!invisible && currentlyInvisible && this.cloakPhase != CloakPhase.PRE_DECLOAK && this.cloakPhase != CloakPhase.IDLE) {
                this.startPreDecloak();
                return;
            }
        }
        super.setInvisible(invisible);
    }

    private void startPreCloak() {
        this.cloakPhase = CloakPhase.PRE_CLOAK;
        this.cloakPhaseTicks = 0;
        this.pickVibeAxis();
        this.vibLastZ = 0.0;
        this.vibLastX = 0.0;
        this.vibPhase = 0.0;
    }

    private void startPreDecloak() {
        this.cloakPhase = CloakPhase.PRE_DECLOAK;
        this.cloakPhaseTicks = 0;
        this.pickVibeAxis();
        this.vibLastZ = 0.0;
        this.vibLastX = 0.0;
        this.vibPhase = 0.0;
    }

    private void pickVibeAxis() {
        double ang = this.getRandom().nextDouble() * Math.PI * 2.0;
        this.vibAxisX = Math.cos(ang);
        this.vibAxisZ = Math.sin(ang);
    }

    private void tickCloakVibration() {
        double maxR;
        double r2;
        double nz;
        double sin;
        double dAng;
        double cos;
        double nx;
        double nLen;
        if (this.cloakPhase != CloakPhase.PRE_CLOAK && this.cloakPhase != CloakPhase.PRE_DECLOAK) {
            this.cloakPhase = super.isInvisible() ? CloakPhase.CLOAKED : CloakPhase.IDLE;
            return;
        }
        if (this.vibLastX != 0.0 || this.vibLastZ != 0.0) {
            this.setPos(this.getX() - this.vibLastX, this.getY(), this.getZ() - this.vibLastZ);
            this.vibLastZ = 0.0;
            this.vibLastX = 0.0;
        }
        int duration = this.cloakPhase == CloakPhase.PRE_CLOAK ? 40 : 40;
        float tRaw = (float)this.cloakPhaseTicks / (float)duration;
        float ramp = tRaw * tRaw * (3.0f - 2.0f * tRaw);
        float fRamp = 1.0f - (float)Math.pow(1.0f - ramp, 3.0);
        float freqHz = 12.0f + fRamp * 96.0f;
        double dPhase = Math.PI * 2 * (double)freqHz / 20.0;
        this.vibPhase += dPhase;
        if (this.vibStutterTicks > 0) {
            --this.vibStutterTicks;
            this.vibLastX = this.vibHoldX;
            this.vibLastZ = this.vibHoldZ;
            this.setPos(this.getX() + this.vibLastX, this.getY(), this.getZ() + this.vibLastZ);
            ++this.cloakPhaseTicks;
            if (this.cloakPhaseTicks >= duration) {
                this.setPos(this.getX() - this.vibLastX, this.getY(), this.getZ() - this.vibLastZ);
                this.vibLastZ = 0.0;
                this.vibLastX = 0.0;
                if (this.cloakPhase == CloakPhase.PRE_CLOAK) {
                    super.setInvisible(true);
                    this.cloakPhase = CloakPhase.CLOAKED;
                } else {
                    super.setInvisible(false);
                    this.cloakPhase = CloakPhase.IDLE;
                }
            }
            return;
        }
        if (this.getRandom().nextFloat() < 3000.12f * ramp) {
            double jump = Math.PI * (0.7 + this.getRandom().nextDouble() * 0.6);
            if (this.getRandom().nextBoolean()) {
                jump = -jump;
            }
            this.vibPhase += jump;
        }
        if (ramp > 0.0f && (nLen = Math.sqrt((nx = this.vibAxisX * (cos = Math.cos(dAng = Math.toRadians(3000.0) * (double)ramp * (this.getRandom().nextDouble() * 2.0 - 1.0))) - this.vibAxisZ * (sin = Math.sin(dAng))) * nx + (nz = this.vibAxisX * sin + this.vibAxisZ * cos) * nz)) > 1.0E-6) {
            this.vibAxisX = nx / nLen;
            this.vibAxisZ = nz / nLen;
        }
        double baseMag = (double)(0.09f * ramp) * Math.sin(this.vibPhase);
        double jitterAmp = 0.0315f * ramp;
        double j1 = (this.getRandom().nextDouble() - 0.5) * 2.0 * jitterAmp;
        double j2 = (this.getRandom().nextDouble() - 0.5) * 2.0 * jitterAmp;
        double axX = this.vibAxisX;
        double axZ = this.vibAxisZ;
        double pxX = -this.vibAxisZ;
        double pxZ = this.vibAxisX;
        double offX = axX * (baseMag + j1) + pxX * j2;
        double offZ = axZ * (baseMag + j1) + pxZ * j2;
        if (this.getRandom().nextFloat() < 3000.12f * ramp) {
            double mult = 1.4 + this.getRandom().nextDouble() * 0.8;
            offX *= mult;
            offZ *= mult;
        }
        if ((r2 = offX * offX + offZ * offZ) > (maxR = (double)(0.12f * ramp)) * maxR && r2 > 0.0) {
            double s = maxR / Math.sqrt(r2);
            offX *= s;
            offZ *= s;
        }
        this.vibLastX = offX;
        this.vibLastZ = offZ;
        this.setPos(this.getX() + this.vibLastX, this.getY(), this.getZ() + this.vibLastZ);
        if (this.getRandom().nextFloat() < 3000.1f * ramp) {
            this.vibStutterTicks = 1;
            this.vibHoldX = this.vibLastX;
            this.vibHoldZ = this.vibLastZ;
        }
        ++this.cloakPhaseTicks;
        if (this.cloakPhaseTicks >= duration) {
            if (this.vibLastX != 0.0 || this.vibLastZ != 0.0) {
                this.setPos(this.getX() - this.vibLastX, this.getY(), this.getZ() - this.vibLastZ);
                this.vibLastZ = 0.0;
                this.vibLastX = 0.0;
            }
            if (this.cloakPhase == CloakPhase.PRE_CLOAK) {
                super.setInvisible(true);
                this.cloakPhase = CloakPhase.CLOAKED;
            } else {
                super.setInvisible(false);
                this.cloakPhase = CloakPhase.IDLE;
            }
        }
    }

    private void hullDebugTo(Player player, String msg) {
    }

    private boolean hullPlayFakeSoundNearPlayer(Player player) {
        if (player == null) {
            return false;
        }
        double angle = this.getRandom().nextDouble() * Math.PI * 2.0;
        double dist = 22.0 + this.getRandom().nextDouble() * 24.0;
        double fx = player.getX() + Math.cos(angle) * dist;
        double fz = player.getZ() + Math.sin(angle) * dist;
        double fy = player.getY() + (double)(this.getRandom().nextInt(3) - 1);
        BlockPos pos = BlockPos.containing(fx, fy, fz);
        if (!this.level().hasChunkAt(pos) && !this.level().hasChunkAt(pos = BlockPos.containing(fx = (fx + player.getX()) * 0.5, fy, fz = (fz + player.getZ()) * 0.5))) {
            return false;
        }
        SoundEvent chosen = HULL_FAKE_SOUNDS[this.getRandom().nextInt(HULL_FAKE_SOUNDS.length)];
        float pitch = 1.0f + (this.getRandom().nextFloat() - 0.5f) * 0.2f;
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level()).playSound(null, fx, fy, fz, chosen, SoundSource.HOSTILE, 1.0f, pitch);
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.LARGE_SMOKE, fx, fy + 0.2, fz, 10, 0.1, 0.1, 0.1, 0.01);
        }
        if (player instanceof ServerPlayer) {
            ((ServerPlayer)player).connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(chosen), SoundSource.HOSTILE, fx, fy, fz, 1.0f, pitch, this.getRandom().nextLong()));
        }
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAdapted.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.HULL_HEALTH + SRPAttributes.HULL_A_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.HULL_ARMOR + SRPAttributes.HULL_A_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.35);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.HULL_ATTACK_DAMAGE + SRPAttributes.HULL_A_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.HULL_KD_RESISTANCE + SRPAttributes.HULL_A_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.adaptedFollow);
        return builder;
    }

    private void hullDbg(String msg) {
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.leftTendrilHealth > 0.0f) {
            this.leftTendril.tick();
        }
        if (this.rightTendrilHealth > 0.0f) {
            this.rightTendril.tick();
        }
        if (!this.level().isClientSide) {
            if (this.srpTicks == 10) {
                float currentH = this.getHealth() / this.getMaxHealth();
                if (this.getSSS()) {
                    this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 25, 1, false, false));
                    this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 2, false, false));
                    if (this.tickCount % 2 == 0) {
                        this.playSound(SRPSounds.HULL_C.get(), 0.2f, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2f + 1.0f);
                    }
                    if ((double)currentH < SRPConfigMobs.hulladaptedneededhealth) {
                        this.setSSS(false);
                    }
                } else if ((double)currentH >= SRPConfigMobs.hulladaptedneededhealth) {
                    ++this.timer;
                    if ((float)this.timer > SRPConfigMobs.hulladaptedneededtime) {
                        this.setSSS(true);
                        this.particleStatus((byte)6);
                        this.timer = 0;
                    }
                }
            }
            if (!this.canPull) {
                --this.pulling;
                if (this.pulling == 0) {
                    this.canPull = true;
                }
            }
            if (this.getTarget() != null) {
                if (!this.getTarget().isAlive()) {
                    this.setTarget(null);
                    this.setTargetedEntity(0);
                } else if (this.hasLineOfSight((Entity)this.getTarget()) && this.distanceToSqr((Entity)this.getTarget()) > 0.0 && this.canPull && this.getTargetedEntity() != null) {
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, false));
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 1, false, false));
                    this.lookAt((Entity)this.getTargetedEntity());
                    this.attackEntityAsMobMinimum(this.getTarget(), 0.02f);
                    this.setParasiteStatus(3);
                    ++this.pulling;
                    if (this.pulling > 200 || this.distanceToSqr((Entity)this.getTarget()) > 9.0) {
                        this.setTargetedEntity(0);
                        this.canPull = false;
                    }
                } else {
                    this.setTargetedEntity(0);
                }
            } else {
                this.setTargetedEntity(0);
            }
        }
        if (this.getTargetedEntity() != null && this.distanceToSqr((Entity)this.getTargetedEntity()) > 0.0) {
            LivingEntity target = this.getTargetedEntity();
            target.stopRiding();
            double str = 0.3;
            double deltaX = this.getX() - target.getX();
            double deltaY = this.getY() - target.getY();
            double deltaZ = this.getZ() - target.getZ();
            str = 0.13;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance == 0.0) {
                return;
            }
            Mot.addX(target, (deltaX /= distance) * str);
            Mot.addY(target, (deltaY /= distance) * str);
            Mot.addZ(target, (deltaZ /= distance) * str);
        }
        if (!this.level().isClientSide) {
            this.tickCloakVibration();
        }
        if (!this.level().isClientSide) {
            Player target = this.level().getNearestPlayer((Entity)this, 48.0);
            boolean invisibleEnough = true;
            if (this.hullFakeCooldown > 0) {
                --this.hullFakeCooldown;
            } else if (target != null && invisibleEnough) {
                IllusionType type = this.pickIllusionType();
                boolean ok = this.hullPlayTypedIllusion_STRONG(target, type);
                this.hullDbg("Attempt type=" + (type) + " -> " + (ok ? "OK" : "FAILED"));
                this.hullFakeCooldown = 400 + this.getRandom().nextInt(201);
            } else {
                this.hullFakeCooldown = 400 + this.getRandom().nextInt(201);
            }
        }
    }

    private boolean hullPlayTypedIllusion_STRONG(Player player, IllusionType type) {
        List<SoundEvent> pool;
        if (player == null) {
            return false;
        }
        IllusionProfile prof = this.profileFor(type);
        Vec3 pos = this.pickOffset(player, prof.minDist, prof.maxDist);
        BlockPos bp = BlockPos.containing(pos.x, pos.y, pos.z);
        if (!this.level().hasChunkAt(bp)) {
            pos = new Vec3((pos.x + player.getX()) * 0.5, pos.y, (pos.z + player.getZ()) * 0.5);
            bp = BlockPos.containing(pos.x, pos.y, pos.z);
            if (!this.level().hasChunkAt(bp)) {
                this.hullDbg("Chunk not loaded even after nudge; abort.");
                return false;
            }
        }
        if ((pool = this.hullBuildSoundPool(type)).isEmpty()) {
            SoundEvent amb = this.getAmbientSound();
            pool = Collections.singletonList(amb != null ? amb : SoundEvents.ENDERMAN_SCREAM);
            this.hullDbg("SRPSounds pool empty for " + (type) + " \u2014 using fallback.");
        }
        SoundEvent chosen = pool.get(this.getRandom().nextInt(pool.size()));
        float pitch = prof.basePitch + (this.getRandom().nextFloat() - 0.5f) * (prof.pitchJitter * 2.0f);
        IllusionLoudness loud = this.loudnessFor(type);
        float volume = EntityHullAdapted.clampf(loud.base + (this.getRandom().nextFloat() - 0.5f) * (loud.jitter * 2.0f), 0.01f, 1.0f);
        if (this.level() instanceof ServerLevel) {
            this.level().playSound(null, pos.x, pos.y, pos.z, chosen, SoundSource.HOSTILE, volume, pitch);
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.LARGE_SMOKE, pos.x, pos.y + 0.2, pos.z, 8, 0.08, 0.08, 0.08, 0.01);
        }
        if (player instanceof ServerPlayer) {
            ((ServerPlayer)player).connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(chosen), SoundSource.HOSTILE, pos.x, pos.y, pos.z, volume, pitch, this.getRandom().nextLong()));
        }
        return true;
    }

    @Override
    protected void handleParasiteStatus() {
        byte k = this.getParasiteStatus();
        if (this.getAttackCooldownAni() != 0 || k == 1 || k == 2 || k == 3) {
            if (this.getAttackCooldownAni() != 0) {
                int i = this.getAttackCooldownAni() - 1;
                this.setAttackCooldownAni(i);
            }
            if (k == 1 || k == 2 || k == 3) {
                if (this.getTarget() != null) {
                    if (!this.getTarget().isAlive()) {
                        this.setTarget(null);
                        this.setParasiteStatus(0);
                    } else if (!this.canPull) {
                        this.setParasiteStatus(Math.min(k, 2));
                    }
                } else {
                    this.setParasiteStatus(0);
                    this.setTarget(null);
                }
            }
        }
    }

    public void setTargetedEntity(int entityId) {
        if (!this.canPull && entityId != 0) {
            return;
        }
        this.pulling = 0;
        this.canPull = true;
        this.entityData.set(TARGET_ENTITY, entityId);
    }

    public boolean hasTargetedEntity() {
        if (!this.canPull) {
            return false;
        }
        return (Integer)this.entityData.get(TARGET_ENTITY) != 0;
    }

    public LivingEntity getTargetedEntity() {
        if (!this.hasTargetedEntity()) {
            return null;
        }
        if (this.level().isClientSide) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            }
            Entity entity = this.level().getEntity(((Integer)this.entityData.get(TARGET_ENTITY)).intValue());
            if (entity instanceof LivingEntity) {
                this.targetedEntity = (LivingEntity)entity;
                return this.targetedEntity;
            }
            return null;
        }
        return this.getTarget();
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        this.setSSS(false);
        this.timer = 0;
        return super.hurt(source, amount);
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.level().isClientSide) {
            return false;
        }
        boolean flag = this.hurt(source, amount);
        if (!flag) {
            return false;
        }
        if (this.leftTendril.getPartId() == id) {
            this.leftTendrilHealth -= amount;
            if (this.leftTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(SRPEntities.TENDRIL.get(), this.level());
                tendril.setSkin(3);
                tendril.copyPosition(this.leftTendril);
                this.level().addFreshEntity((Entity)tendril);
                this.leftTendril.discard();
                this.level().broadcastEntityEvent((Entity)this, (byte)11);
                this.cutResistances(SRPConfig.adaptedPointDamCap / 2);
                com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new EntityBodyDeadPayload(this.getId(), id));
            }
        } else if (this.rightTendril.getPartId() == id) {
            this.rightTendrilHealth -= amount;
            if (this.rightTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(SRPEntities.TENDRIL.get(), this.level());
                tendril.setSkin(3);
                tendril.copyPosition(this.rightTendril);
                this.level().addFreshEntity((Entity)tendril);
                this.rightTendril.discard();
                this.level().broadcastEntityEvent((Entity)this, (byte)22);
                this.cutResistances(SRPConfig.adaptedPointDamCap / 2);
                com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new EntityBodyDeadPayload(this.getId(), id));
            }
        }
        return flag;
    }

    private IllusionType pickIllusionType() {
        float r = this.getRandom().nextFloat();
        return r < 0.55f ? IllusionType.FOOTSTEP : (r < 0.9f ? IllusionType.AMBIENT : IllusionType.GROWL);
    }

    private SoundEvent srpSound(String fieldName) {
        try {
            Field f = SRPSounds.class.getField(fieldName);
            Object v = f.get(null);
            if (v instanceof SoundEvent) {
                return (SoundEvent)v;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private List<SoundEvent> srpFindMatchingSounds(String[] whoTokens, String[] whatTokens) {
        ArrayList<SoundEvent> out = new ArrayList<SoundEvent>();
        try {
            for (Field f : SRPSounds.class.getDeclaredFields()) {
                if (!SoundEvent.class.isAssignableFrom(f.getType())) continue;
                f.setAccessible(true);
                Object v = f.get(null);
                if (!(v instanceof SoundEvent)) continue;
                String name = f.getName().toUpperCase();
                boolean whoOk = false;
                boolean whatOk = false;
                for (String w : whoTokens) {
                    if (!name.contains(w)) continue;
                    whoOk = true;
                    break;
                }
                for (String w : whatTokens) {
                    if (!name.contains(w)) continue;
                    whatOk = true;
                    break;
                }
                if (!whoOk || !whatOk) continue;
                out.add((SoundEvent)v);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (out.isEmpty()) {
            SoundEvent amb = this.getAmbientSound();
            if (amb != null) {
                out.add(amb);
            } else {
                out.add(SoundEvents.ENDERMAN_SCREAM);
            }
        }
        return out;
    }

    private Vec3 pickOffset(Player p, double minD, double maxD) {
        double dist = minD + this.getRandom().nextDouble() * (maxD - minD);
        Vec3 look = p.getLookAngle().normalize();
        Vec3 side = look.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        Vec3 dir = this.getRandom().nextBoolean() ? look.scale(-1.0).add(side.scale((this.getRandom().nextDouble() - 0.5) * 0.6)).normalize() : side.scale(this.getRandom().nextBoolean() ? 1.0 : -1.0);
        return new Vec3(p.getX() + dir.x * dist, p.getY() + (double)(this.getRandom().nextInt(3) - 1), p.getZ() + dir.z * dist);
    }

    private boolean hullPlayTypedIllusion(Player player, IllusionType type) {
        List<SoundEvent> pool;
        if (player == null) {
            return false;
        }
        IllusionProfile prof = this.profileFor(type);
        Vec3 pos = this.pickOffset(player, prof.minDist, prof.maxDist);
        BlockPos bp = BlockPos.containing(pos.x, pos.y, pos.z);
        if (!this.level().hasChunkAt(bp)) {
            pos = new Vec3((pos.x + player.getX()) * 0.5, pos.y, (pos.z + player.getZ()) * 0.5);
            bp = BlockPos.containing(pos.x, pos.y, pos.z);
            if (!this.level().hasChunkAt(bp)) {
                return false;
            }
        }
        if ((pool = this.hullBuildSoundPool(type)).isEmpty()) {
            return false;
        }
        SoundEvent chosen = pool.get(this.getRandom().nextInt(pool.size()));
        float pitch = prof.basePitch + (this.getRandom().nextFloat() - 0.5f) * (prof.pitchJitter * 2.0f);
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level()).playSound(null, pos.x, pos.y, pos.z, chosen, SoundSource.HOSTILE, 1.0f, pitch);
        }
        if (player instanceof ServerPlayer) {
            ((ServerPlayer)player).connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(chosen), SoundSource.HOSTILE, pos.x, pos.y, pos.z, 1.0f, pitch, this.getRandom().nextLong()));
        }
        return true;
    }

    private IllusionProfile profileFor(IllusionType t) {
        switch (t) {
            case FOOTSTEP: {
                return new IllusionProfile(8.0, 14.0, 1.0f, 0.1f);
            }
            case AMBIENT: {
                return new IllusionProfile(12.0, 22.0, 1.0f, 0.12f);
            }
            case GROWL: {
                return new IllusionProfile(16.0, 28.0, 0.98f, 0.14f);
            }
        }
        return new IllusionProfile(12.0, 22.0, 1.0f, 0.12f);
    }

    private IllusionLoudness loudnessFor(IllusionType t) {
        switch (t) {
            case FOOTSTEP: {
                return new IllusionLoudness(0.35f, 0.08f);
            }
            case AMBIENT: {
                return new IllusionLoudness(0.4f, 0.1f);
            }
            case GROWL: {
                return new IllusionLoudness(0.5f, 0.12f);
            }
        }
        return new IllusionLoudness(0.4f, 0.1f);
    }

    private static float clampf(float v, float lo, float hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }

    @Override
    public void setBodyPartDead(int id) {
        if (this.leftTendril.getPartId() == id) {
            this.leftTendril.discard();
        } else if (this.rightTendril.getPartId() == id) {
            this.rightTendril.discard();
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            if (this.getSSS()) {
                float f = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * SRPConfigMobs.hulladaptedstealthdamage;
                if (entityIn instanceof LivingEntity) {
                    f = EnchantmentHelper.modifyDamage((ServerLevel)this.level(), this.getMainHandItem(), entityIn, this.damageSources().mobAttack(this), f);
                }
                entityIn.hurt(this.damageSources().mobAttack(this), f);
                this.setSSS(false);
                this.timer = 0;
            }
            if (!this.hasTargetedEntity()) {
                this.setTargetedEntity(entityIn.getId());
                ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 3, false, false));
            }
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMobMinimum(LivingEntity entityIn, float damage) {
        boolean flag = super.attackEntityAsMobMinimum(entityIn, damage);
        if (flag && this.getSSS()) {
            this.setSSS(false);
            this.timer = 0;
        }
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 2.0f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityHull(SRPEntities.PRI_MANDUCATER.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    @Override
    public void setDead() {
        if (this.leftTendril != null) {
            this.leftTendril.discard();
        }
        if (this.rightTendril != null) {
            this.rightTendril.discard();
        }
        super.discard();
    }

    public boolean getSSS() {
        return (Boolean)this.entityData.get(CAM);
    }

    public void setSSS(boolean in) {
        this.entityData.set(CAM, in);
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0 || this.hasEffect(MobEffects.INVISIBILITY)) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.AHULL_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.AHULL_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.AHULL_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.hulladaptedOrbEffects, mobs);
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.HEAVY_STEPS_TWO.get(), 0.15f, 1.0f);
    }

    public void notifyDataManagerChange(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ENTITY.equals(key)) {
            this.targetedEntity = null;
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.getRandom().nextInt(1)) {
                case 0: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("parasiteleftTendril", this.leftTendrilHealth);
        compound.putFloat("parasiterightTendril", this.rightTendrilHealth);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("parasiteleftTendril", 99)) {
            this.leftTendrilHealth = compound.getFloat("parasiteleftTendril");
            if (this.leftTendrilHealth <= 0.0f) {
                this.level().broadcastEntityEvent((Entity)this, (byte)11);
            }
        }
        if (compound.contains("parasiterightTendril", 99)) {
            this.rightTendrilHealth = compound.getFloat("parasiterightTendril");
            if (this.rightTendrilHealth <= 0.0f) {
                this.level().broadcastEntityEvent((Entity)this, (byte)22);
            }
        }
    }

    public float getLeft() {
        return this.leftTendrilHealth;
    }

    public float getRight() {
        return this.rightTendrilHealth;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 11) {
            this.leftTendrilHealth = 0.0f;
        } else if (id == 22) {
            this.rightTendrilHealth = 0.0f;
        } else {
            super.handleEntityEvent(id);
        }
    }

    private static boolean anyContains(String hay, String ... needles) {
        for (String n : needles) {
            if (!hay.contains(n)) continue;
            return true;
        }
        return false;
    }

    private List<SoundEvent> hullBuildSoundPool(IllusionType type) {
        String[] what;
        String[] who = new String[]{"HULL", "MANDUCATOR"};
        switch (type) {
            case FOOTSTEP: {
                what = new String[]{"STEP", "FOOT", "WALK", "MOVE"};
                break;
            }
            case AMBIENT: {
                what = new String[]{"AMBIENT", "IDLE", "BREATH"};
                break;
            }
            case GROWL: {
                what = new String[]{"GROWL", "ROAR", "ALERT", "SNARL", "SCREAM"};
                break;
            }
            default: {
                what = new String[]{"AMBIENT"};
            }
        }
        ArrayList<SoundEvent> out = new ArrayList<SoundEvent>();
        try {
            for (Field f : SRPSounds.class.getDeclaredFields()) {
                boolean whatOk;
                if (!DeferredHolder.class.isAssignableFrom(f.getType())) continue;
                f.setAccessible(true);
                Object v = f.get(null);
                if (!(v instanceof DeferredHolder<?, ?> holder) || !(holder.get() instanceof SoundEvent)) continue;
                SoundEvent s = (SoundEvent)holder.get();
                String fieldName = f.getName().toUpperCase();
                String regName = String.valueOf(s.getLocation().getPath()).toUpperCase();
                if (regName.contains("SILENCE") || fieldName.contains("SILENCE")) continue;
                boolean whoOk = EntityHullAdapted.anyContains(fieldName, who) || EntityHullAdapted.anyContains(regName, who);
                boolean bl = whatOk = EntityHullAdapted.anyContains(fieldName, what) || EntityHullAdapted.anyContains(regName, what);
                if (!whoOk || !whatOk) continue;
                out.add(s);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return out;
    }

    private void dumpSRPSoundsOnce() {
        if (this.hullDumpedSounds || this.level().isClientSide) {
            return;
        }
        this.hullDumpedSounds = true;
        StringBuilder sb = new StringBuilder("[Hull] SRPSounds detected:");
        try {
            for (Field f : SRPSounds.class.getDeclaredFields()) {
                if (!DeferredHolder.class.isAssignableFrom(f.getType())) continue;
                f.setAccessible(true);
                Object v = f.get(null);
                if (!(v instanceof DeferredHolder<?, ?> holder) || !(holder.get() instanceof SoundEvent)) continue;
                SoundEvent s = (SoundEvent)holder.get();
                sb.append("\n - ").append(f.getName()).append(" -> ").append(s.getLocation());
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        System.out.println(sb.toString());
    }

    private static class IllusionLoudness {
        final float base;
        final float jitter;

        IllusionLoudness(float base, float jitter) {
            this.base = base;
            this.jitter = jitter;
        }
    }

    private static class IllusionProfile {
        final double minDist;
        final double maxDist;
        final float basePitch;
        final float pitchJitter;

        IllusionProfile(double minDist, double maxDist, float basePitch, float pitchJitter) {
            this.minDist = minDist;
            this.maxDist = maxDist;
            this.basePitch = basePitch;
            this.pitchJitter = pitchJitter;
        }
    }

    private static enum IllusionType {
        FOOTSTEP,
        AMBIENT,
        GROWL;

    }

    private static enum CloakPhase {
        IDLE,
        PRE_CLOAK,
        CLOAKED,
        PRE_DECLOAK;

    }
}

