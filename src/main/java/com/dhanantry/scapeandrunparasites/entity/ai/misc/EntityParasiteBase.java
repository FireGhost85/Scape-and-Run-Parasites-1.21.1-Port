package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.dislodgment.IDislodgmentTarget;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.IHitboxedEntity;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIParasiteFollow;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWanderStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolMeleeBase;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolRangeBase;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.MobDespawnEvent;

public abstract class EntityParasiteBase
extends Monster
implements IHitboxedEntity, IDislodgmentTarget {
    /** set while a capped hit is applied: the damage then ignores armor (1.12 DamageSource.setDamageBypassesArmor) */
    private boolean bypassArmorHit;
    protected int attackSpeedT;
    protected double killcount = 0.0;
    protected boolean canD = true;
    private int waitInt;
    private boolean canWorkTask;
    private EntityParasiteBase owner;
    private EntityParasiteBase notOwner;
    public boolean canChangeVariant;
    protected float scentHPMultiplier = 2.5f;
    protected int foodRootNumber;
    protected double foodRott;
    public boolean disloNumberTwo = false;
    public boolean disloNumberThree = false;
    public boolean disloNumberFour = false;
    public boolean disloNumberSix = false;
    public boolean disloNumberSeven = false;
    public boolean disloNumberEight = false;
    public boolean disloNumberNine = false;
    public int disloNumberEleven = 0;
    public int disloNumberFifteen = 0;
    public static final EntityDataAccessor<Boolean> DISLO15 = SynchedEntityData.defineId(EntityParasiteBase.class, EntityDataSerializers.BOOLEAN);
    public int disloNumberSixteen = 0;
    public float distanceWalkedOnStepModifiedDislo;
    public int disloNumberSeventeen = 0;
    public boolean disloNumberEighteen = false;
    public int disloNumberNineteen = 0;
    public double disloNumberNineteenValue = 0.0;
    public boolean disloNumberTwentyone = false;
    public boolean disloNumberTwentytwo = false;
    protected boolean geneMindam = true;
    protected boolean geneDamcap = true;
    protected boolean geneLookwall = true;
    protected boolean geneSprinting = true;
    protected boolean geneWaterleap = true;
    protected boolean geneSpecialmove = true;
    protected float genePoisonHealing = 2.5f;
    protected float geneMobHealing = 3.0f;
    protected float geneAttackSpeed = 0.5f;
    private static final EntityDataAccessor<Byte> SPECIAL = SynchedEntityData.defineId(EntityParasiteBase.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> SELFE = SynchedEntityData.defineId(EntityParasiteBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> SKIN = SynchedEntityData.defineId(EntityParasiteBase.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> COLD_L = SynchedEntityData.defineId(EntityParasiteBase.class, EntityDataSerializers.BOOLEAN);
    public int srpTicks;
    protected int lastActiveTime;
    protected int timeSinceIgnited;
    protected int fuseTime = 40;
    protected byte canModRender = 0;
    protected int attackCooldown;
    protected byte madeRng = (byte)-1;
    protected byte paraGeneration = 0;
    public boolean spawnedByColo;
    protected byte type = 0;
    public int damageCap = 1;
    protected float MiniDamage = 0.0f;
    protected float miniCap;
    protected boolean miniCapA = false;
    protected byte phaseCreated;
    protected byte levelCreated;
    protected EntityAIJumping jumpT = new EntityAIJumping((Mob)this);
    protected EntityAIWait wait = new EntityAIWait();
    public EntityAIParasiteFollow folow = new EntityAIParasiteFollow(this, 1.3, 16.0, 6.0, true);
    public EntityAIWanderStatus aiWander = new EntityAIWanderStatus(this, 1.0, 120, 0.001f, true);
    protected int oneMindDeathValue;
    protected float foodSteal;
    private float aniticks;
    private boolean still;
    private int stillTicks;
    private int canBeStored;
    private int dodFatherID = 0;
    public boolean canSpawnSpawn;
    protected int valueEvDeath;
    protected int liquidLeap;
    protected float cothSpread;
    private LivingEntity last;
    public int targetNewCool;
    protected float blockH;
    protected int BGheight;
    protected int BGrange;
    protected boolean SkillBGflag;
    private ArrayList<String> inbBlockName = new ArrayList();
    private ArrayList<Integer> inbBlockNumber = new ArrayList();
    protected int attacking;
    protected double targetX;
    protected double targetZ;
    protected float leapMotionY;
    protected double jumpSpeed;
    protected int jumpR;
    protected boolean SkillLeapFlag;
    public EntityHitbox[] hitboxes;
    public float prevHitboxScale;

    public EntityParasiteBase(EntityType<? extends EntityParasiteBase> type, Level worldIn) {
        super(type, worldIn);
        this.goalSelector.addGoal(0, this.wait);
        this.goalSelector.addGoal(5, this.aiWander);
        this.goalSelector.addGoal(6, this.folow);
        this.goalSelector.addGoal(5, this.jumpT);
        this.canBeStored = 0;
        this.attackCooldown = 0;
        this.canWorkTask = true;
        this.srpTicks = 0;
        this.madeRng = (byte)-1;
        this.foodSteal = 0.001f;
        this.spawnedByColo = false;
        this.SkillBGflag = false;
        this.SkillLeapFlag = false;
        this.canChangeVariant = false;
        this.oneMindDeathValue = 1;
        this.foodRootNumber = 0;
        this.foodRott = 0.0;
        this.attackSpeedT = 20;
        this.canSpawnSpawn = true;
        this.still = false;
        this.miniCap = SRPConfig.miniminiCap;
        this.setScentHPMultiplier(2.5f);
    }

    /** 1.12 defaults of EntityLivingBase / EntityLiving / EntityMob (max health 20, speed 0.7, follow range 16, attack damage 2). */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.699999988079071);
    }

    /** The dislodgment flags of 1.10.9 (the public disloNumberX fields written by SRPSaveData); see {@link IDislodgmentTarget}. */
    @Override
    public void setDislodgment(int position, int value) {
        boolean on = value != 0;
        switch (position) {
            case 2 -> this.disloNumberTwo = on;
            case 3 -> this.disloNumberThree = on;
            case 4 -> this.disloNumberFour = on;
            case 6 -> this.disloNumberSix = on;
            case 7 -> this.disloNumberSeven = on;
            case 8 -> this.disloNumberEight = on;
            case 9 -> this.disloNumberNine = on;
            case 11 -> this.disloNumberEleven = value;
            case 15 -> {
                this.disloNumberFifteen = value;
                this.setDislo15(on);
            }
            case 16 -> {
                this.disloNumberSixteen = value;
                if (on) {
                    this.distanceWalkedOnStepModifiedDislo = this.moveDist;
                }
            }
            case 17 -> this.disloNumberSeventeen = value;
            case 18 -> this.disloNumberEighteen = on;
            case 19 -> this.disloNumberNineteen = value;
            case 21 -> this.disloNumberTwentyone = on;
            case 22 -> this.disloNumberTwentytwo = on;
            default -> { }
        }
    }

    public void setCreatedPhase(int in, int ud) {
        this.phaseCreated = (byte)in;
        this.levelCreated = (byte)ud;
    }

    public void setScentHPMultiplier(float scentHPMultiplier) {
        this.scentHPMultiplier = scentHPMultiplier;
    }

    public float getScentHPMultiplier() {
        return this.scentHPMultiplier;
    }

    public double scentHostHPCalculation() {
        return this.getAttribute(Attributes.MAX_HEALTH).getValue() * (double)this.getScentHPMultiplier();
    }

    public abstract int getParasiteIDRegister();

    public int getAttackSpeed() {
        return (int)((float)this.attackSpeedT * this.geneAttackSpeed);
    }

    public void applyBonuses(SRPSaveData sabe, Level world) {
        this.setCreatedPhase(sabe.getEvolutionPhase(DimKeys.of(world)), sabe.getDeveLevel());
        String id = DimKeys.of(world);
        this.applyGene(sabe.getGeneModi(id), sabe.getGeneModi2(id));
        if (!(SRPConfigWorld.nodesActivated || SRPConfigWorld.coloniesActivated || SRPConfigSystems.useEvolution)) {
            return;
        }
        this.phaseCreated = sabe.getEvolutionPhase(DimKeys.of(world));
        if (this.phaseCreated >= SRPConfigSystems.evolutionAssimilatedDehiding) {
            this.targetSelector.addGoal(5, new EntityAINearestAttackableTargetStatus<Villager>(this, Villager.class, true));
        }
        if (this.phaseCreated >= SRPConfigSystems.evolutionTotalKill) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<LivingEntity>(this, LivingEntity.class, 0, false, false, entity -> !(entity instanceof Player) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite), SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        }
    }

    public void applyGene(boolean[] kool, float[] goon) {
        if (!SRPConfigSystems.generationUse) {
            return;
        }
        this.geneMindam = kool[0];
        this.geneDamcap = kool[1];
        this.geneLookwall = kool[2];
        this.geneSprinting = kool[3];
        this.geneWaterleap = kool[4];
        this.geneSpecialmove = kool[5];
        this.genePoisonHealing = goon[0];
        this.geneMobHealing = goon[1];
        this.geneAttackSpeed = goon[2];
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPECIAL, (byte) (0));
        builder.define(SELFE, -1);
        builder.define(SKIN, (byte) (0));
        builder.define(COLD_L, false);
        builder.define(DISLO15, false);
    }

    public void cannotDespawn(boolean in) {
        this.canD = in;
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return this.canD;
    }

    public void setApplyColonyB(boolean in) {
        this.spawnedByColo = in;
    }

    public boolean getApplyColonyB() {
        return this.spawnedByColo;
    }

    public void aiStep() {
        ++this.srpTicks;
        if (this.isNoAi()) {
            return;
        }
        if (this.getWait() > 0) {
            int j1 = this.getWait() - 1;
            if (j1 == 0) {
                this.setParasiteStatus(0);
            }
            this.setWait(j1);
        } else {
            super.aiStep();
        }
        if (this.hitboxes != null && this.hitboxes.length > 0) {
            this.updateHitboxes();
            this.resetHitboxes(this.getScale());
        }
        if (this.level().isClientSide) {
            if (this.hasEffect(SRPPotions.PARATE_E)) {
                if (this.level().random.nextInt(3) != 0) {
                    return;
                }
                for (int i = 0; i <= 1; ++i) {
                    this.spawnParticles(SRPEnumParticle.RHAPPY, 0, 0, 0);
                }
            }
            if (this.srpTicks > 20) {
                this.srpTicks = 0;
            }
        } else {
            LivingEntity tgt;
            if (this.targetNewCool >= 0) {
                --this.targetNewCool;
            }
            if ((tgt = this.getTarget()) instanceof Player && tgt.hasEffect(SRPPotions.THE_SIGN_E)) {
                super.setTarget(null);
                this.setLastHurtByMob(null);
                this.setLastHurtMob(null);
                this.getNavigation().stop();
            }
            if (this.disloNumberEleven >= 0) {
                --this.disloNumberEleven;
            }
            if (this.disloNumberFifteen >= 0) {
                --this.disloNumberFifteen;
                if (this.disloNumberFifteen <= 0) {
                    this.entityData.set(DISLO15, false);
                }
            }
            if (this.disloNumberSixteen >= 0) {
                this.moveDist = -10.0f;
                --this.disloNumberSixteen;
                if (this.disloNumberSixteen <= 0) {
                    this.moveDist = this.distanceWalkedOnStepModifiedDislo + 500.0f;
                }
            }
            if (this.disloNumberSeventeen >= 0) {
                --this.disloNumberSeventeen;
            }
            if (this.disloNumberNineteen >= 0) {
                --this.disloNumberNineteen;
            }
            this.handleParasiteStatus();
            if (this.srpTicks == 1) {
                this.handleWater(true);
                if (this.disloNumberNineteen > 0) {
                    this.killcount += this.disloNumberNineteenValue;
                }
            }
            if (this.srpTicks > 20) {
                this.srpTicks = 0;
                if (!this.inbBlockName.isEmpty() && this.getRandom().nextInt(50) == 0) {
                    this.spawnCyst();
                }
                if (this.getTarget() instanceof Player) {
                    this.fearPlayer(this.getTarget());
                }
                if (SRPConfigSystems.useEvolution && this.killcount >= 0.0) {
                    switch (this.phaseCreated) {
                        case 1: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusOne;
                            break;
                        }
                        case 2: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusTwo;
                            break;
                        }
                        case 3: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusThree;
                            break;
                        }
                        case 4: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusFour;
                            break;
                        }
                        case 5: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusFive;
                            break;
                        }
                        case 6: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusSix;
                            break;
                        }
                        case 7: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusSeven;
                            break;
                        }
                        case 8: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusEight;
                            break;
                        }
                        case 9: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusNine;
                            break;
                        }
                        case 10: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusTen;
                        }
                    }
                } else if (this.level().getDifficulty() == Difficulty.HARD && this.killcount >= 0.0) {
                    this.killcount += SRPConfig.killcountplus;
                }
                this.placeNidus();
            }
            this.handleWater(false);
        }
        if (this.getX() == this.xo && this.getZ() == this.zo) {
            ++this.stillTicks;
        } else {
            this.canBeStored = 55;
            this.stillTicks = 0;
            this.still = false;
        }
        if (this.stillTicks > 25) {
            this.still = true;
        }
        this.aniticks = this.still ? (this.aniticks += 0.0012f) : 0.0f;
        if (this.canBeStored > 0) {
            --this.canBeStored;
        }
    }

    protected void handleWater(boolean check) {
        if (check && this.level().getBlockState(this.blockPosition()).getBlock() instanceof LiquidBlock && this.getTarget() != null) {
            ++this.liquidLeap;
            if (this.liquidLeap > 4) {
                this.liquidLeap = 4;
            }
        }
        if (this.liquidLeap >= 1) {
            if (this.geneWaterleap) {
                double h = 0.1;
                double str = 0.5;
                this.getNavigation().stop();
                LivingEntity entitylivingbase = this.getTarget();
                if (entitylivingbase != null) {
                    if (this.level().getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
                        h = 0.3;
                        str = 1.0;
                    }
                    --this.liquidLeap;
                    double dd0 = entitylivingbase.getX() - this.getX();
                    double dd1 = entitylivingbase.getZ() - this.getZ();
                    float f = (float)Math.sqrt((double)(dd0 * dd0 + dd1 * dd1));
                    Mot.addX(this, dd0 / (double)f * str * (double)0.8f + this.getDeltaMovement().x * (double)0.2f);
                    Mot.addZ(this, dd1 / (double)f * str * (double)0.8f + this.getDeltaMovement().z * (double)0.2f);
                    Mot.setY(this, h);
                    this.lookAt((Entity)entitylivingbase);
                }
            } else {
                --this.liquidLeap;
            }
        }
    }

    protected void handleParasiteStatus() {
        byte k = this.getParasiteStatus();
        if (this.getAttackCooldownAni() != 0 || k == 1 || k == 2) {
            if (this.getAttackCooldownAni() != 0) {
                int i = this.getAttackCooldownAni() - 1;
                this.setAttackCooldownAni(i);
            }
            if (k == 1 || k == 2) {
                if (this.getTarget() != null) {
                    if (!this.getTarget().isAlive()) {
                        this.setTarget(null);
                        this.setParasiteStatus(0);
                    } else if (this.srpTicks == 10 && this.getRandom().nextInt(10) == 0) {
                        double follo = this.getAttribute(Attributes.FOLLOW_RANGE).getValue();
                        if (this.distanceToSqr((Entity)this.getTarget()) > follo * follo) {
                            this.setTarget(null);
                            this.setParasiteStatus(0);
                        }
                    }
                } else {
                    this.setParasiteStatus(0);
                    this.setTarget(null);
                }
            }
        }
    }

    protected void fearPlayer(LivingEntity player) {
    }

    public boolean checkPositionWander(double x, double y, double z) {
        return true;
    }

    protected void placeNidus() {
        if (SRPConfigSystems.evolutionNests < this.getPhaseCreated() && SRPConfigSystems.deveNestsUse < this.getLevelCreated()) {
            return;
        }
        if (SRPConfigSystems.useEvolution && this.phaseCreated <= 0) {
            return;
        }
        if (this.level().random.nextInt(350) != 0 || this.type < 11) {
            return;
        }
        if (this.killcount < 10.0) {
            return;
        }
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(SRPConfig.nexussivFollow);
        List<? extends EntityParasiteBase> moblist = this.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        int collec = 0;
        for (EntityParasiteBase mob : moblist) {
            int idP = mob.getParasiteIDRegister();
            if (idP == 73 || idP == 77 || idP == 78 || idP == 79) {
                return;
            }
            collec += (int)mob.getKillC();
        }
        if (collec < SRPConfig.reinforcerNidusStart) {
            return;
        }
        double d0 = (float)this.getX() + this.level().random.nextFloat();
        double d1 = (float)this.getY() + this.level().random.nextFloat();
        double d2 = (float)this.getZ() + this.level().random.nextFloat();
        double d3 = d0 - this.getX();
        double d4 = d1 - this.getY();
        double d5 = d2 - this.getZ();
        double d6 = (float)Math.sqrt((double)(d3 * d3 + d4 * d4 + d5 * d5));
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d4 = d4 * d7 * 2.0;
        EntityGore bomb = new EntityGore(SRPEntities.GORE.get(), this.level());
        bomb.setType((byte)11);
        bomb.copyPosition((Entity)this);
        bomb.setMotion(d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)), d4, d5 *= d7, 0.1, 0.5);
        this.level().addFreshEntity((Entity)bomb);
    }

    public void setDodFatherID(int in) {
        this.dodFatherID = in;
    }

    public boolean getGeneMod(int id) {
        switch (id) {
            case 0: {
                return this.geneMindam;
            }
            case 1: {
                return this.geneDamcap;
            }
            case 2: {
                return this.geneLookwall;
            }
            case 3: {
                return this.geneSprinting;
            }
            case 4: {
                return this.geneWaterleap;
            }
            case 5: {
                return this.geneSpecialmove;
            }
        }
        return false;
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.getWait() > 0) {
            int j1 = this.getWait() - 1;
            if (j1 == 0) {
                this.setParasiteStatus(0);
            }
            this.setWait(j1);
        }
    }

    public boolean hurt(@Nonnull DamageSource source, float amount) {
        if (this.level().isClientSide) {
            return false;
        }
        this.canBeStored = 55;
        if (this.madeRng == -1) {
            this.madeRng = (byte)this.getRandom().nextInt(2);
            if (this.madeRng == 0) {
                this.level().broadcastEntityEvent((Entity)this, (byte)40);
            }
        }
        if (source.is(DamageTypes.MAGIC) && this.hasEffect(MobEffects.POISON) && amount == 1.0f) {
            this.heal(1.0f * this.genePoisonHealing);
            return false;
        }
        if (this.disloNumberTwentyone && !this.isOnFire() && !source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return super.hurt(source, 0.0f);
        }
        if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return super.hurt(source, amount);
        }
        if (source.is(DamageTypes.IN_WALL)) {
            this.skillBreakBlocks();
        }
        if (this.disloNumberSeven && this.getRandom().nextInt(5) == 0) {
            this.particleStatus((byte)8);
        }
        if (this.disloNumberEight && this.getRandom().nextInt(5) == 0) {
            this.particleStatus((byte)11);
        }
        if (this.disloNumberNine && this.getRandom().nextInt(5) == 0) {
            this.particleStatus((byte)13);
        }
        if (this.getRandom().nextDouble() < 0.1 && this.getHealth() > 0.0f) {
            this.attackEntityFromEffects(1, 1);
        }
        if (source.getDirectEntity() != null && source.getDirectEntity() instanceof Player) {
            Player playerIn = (Player)source.getDirectEntity();
            if (this.disloNumberSix) {
                SRPSaveData data = SRPSaveData.get(this.level());
                int count = data.getCurrentCode(DimKeys.of(this.level()), 6);
                if (count >= 1) {
                    playerIn.getItemBySlot(EquipmentSlot.MAINHAND).hurtAndBreak(count, playerIn, EquipmentSlot.MAINHAND);
                    playerIn.getItemBySlot(EquipmentSlot.OFFHAND).hurtAndBreak(count, playerIn, EquipmentSlot.OFFHAND);
                } else {
                    this.disloNumberSix = false;
                }
            }
            if (playerIn.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof WeaponToolMeleeBase) {
                return super.hurt(source, amount);
            }
        }
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE)) {
            if (!this.fireImmune()) {
                if (this.getRandom().nextInt(5) == 0 && !this.hasEffect(SRPPotions.RAGE_E)) {
                    this.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 200, 1, false, false));
                }
                return super.hurt(source, this.getSkin() == 120 ? amount * (SRPConfig.firemultyplier - SRPConfig.firemultyplier / 2.0f) : amount * SRPConfig.firemultyplier);
            }
            return false;
        }
        if (this.notOwner != null) {
            if (this.notOwner.isAlive()) {
                MobEffectInstance pivot = this.getEffect(SRPPotions.PIVOT_E);
                if (pivot != null) {
                    float redirected = (float)(pivot.getAmplifier() + 1) * SRPConfigSystems.pivotDamageRHost;
                    redirected = Math.min(redirected, 0.95f);
                    float remaining = 1.0f - redirected;
                    this.notOwner.hurt(source, amount * redirected);
                    return super.hurt(source, amount * remaining);
                }
                this.removeEffect(SRPPotions.PIVOT_E);
                this.notOwner = null;
            } else {
                this.notOwner = null;
            }
        }
        boolean flagWeak = SRPConfig.parasiteWeakToMobs.length > 0 || SRPConfig.parasiteWeakToItems.length > 0 || SRPConfig.parasiteWeakToElse.length > 0;
        boolean flagCap = this.damageCap > 1 && this.geneDamcap;
        String damageName = "";
        int naniDesu = 0;
        if (flagWeak || flagCap) {
            if (source.getDirectEntity() instanceof Player) {
                damageName = BuiltInRegistries.ITEM.getKey(((Player)Objects.requireNonNull(source.getEntity())).getItemBySlot(EquipmentSlot.MAINHAND).getItem()).toString();
                naniDesu = 1;
            } else if (source.getDirectEntity() instanceof LivingEntity) {
                damageName = SRPEntityUtil.entityId(source.getDirectEntity());
                naniDesu = 2;
            } else {
                damageName = source.getMsgId();
                naniDesu = 3;
            }
        }
        if (flagWeak) {
            switch (naniDesu) {
                case 1: {
                    int i;
                    for (i = 0; i < SRPConfig.parasiteWeakToItems.length; ++i) {
                        String[] here;
                        if (SRPConfig.parasiteWeakToItems[i] == null || !(here = SRPConfig.parasiteWeakToItems[i].split(";"))[0].equals(damageName)) continue;
                        amount *= Float.parseFloat(here[1]);
                    }
                    break;
                }
                case 2: {
                    int i;
                    for (i = 0; i < SRPConfig.parasiteWeakToMobs.length; ++i) {
                        String[] here;
                        if (SRPConfig.parasiteWeakToMobs[i] == null || !(here = SRPConfig.parasiteWeakToMobs[i].split(";"))[0].equals(damageName)) continue;
                        amount *= Float.parseFloat(here[1]);
                    }
                    break;
                }
                case 3: {
                    int i;
                    for (i = 0; i < SRPConfig.parasiteWeakToElse.length; ++i) {
                        String[] here;
                        if (SRPConfig.parasiteWeakToElse[i] == null || !(here = SRPConfig.parasiteWeakToElse[i].split(";"))[0].equals(damageName)) continue;
                        amount *= Float.parseFloat(here[1]);
                    }
                    break;
                }
            }
        }
        if (flagCap) {
            switch (naniDesu) {
                case 1: {
                    if (!ParasiteEventEntity.checkName(damageName, SRPConfig.damageCapBlackListItem, SRPConfig.damageCapBlackListWhite)) break;
                    return super.hurt(source, amount);
                }
                case 2: {
                    if (!ParasiteEventEntity.checkName(damageName, SRPConfig.damageCapBlackListMob, SRPConfig.damageCapBlackListWhite)) break;
                    return super.hurt(source, amount);
                }
                case 3: {
                    if (!ParasiteEventEntity.checkName(damageName, SRPConfig.damageCapBlackListElse, SRPConfig.damageCapBlackListWhite)) break;
                    return super.hurt(source, amount);
                }
            }
            float damage = this.getMaxHealth() / (float)this.damageCap + this.getMaxHealth() % (float)this.damageCap * 0.5f;
            if (amount >= damage) {
                this.bypassArmorHit = true;
                if (!this.hasEffect(SRPPotions.RAGE_E)) {
                    this.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 200, 1, false, false));
                }
                if (this.getRandom().nextDouble() < 0.3 && this.getHealth() > 0.0f) {
                    this.attackEntityFromEffects(1, 1);
                    this.attackEntityFromCap(1);
                }
            }
            try {
                return super.hurt(source, Math.min(amount, damage));
            } finally {
                this.bypassArmorHit = false;
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    protected float getDamageAfterArmorAbsorb(DamageSource source, float damage) {
        return this.bypassArmorHit ? damage : super.getDamageAfterArmorAbsorb(source, damage);
    }

    protected void attackEntityFromEffects(int range, int count) {
    }

    protected void attackEntityFromCap(int go) {
    }

    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag;
        if (this.level().isClientSide) {
            return false;
        }
        this.canBeStored = 55;
        boolean green = entityIn instanceof LivingEntity;
        if (green) {
            this.attackEntityAsMobMinimum((LivingEntity)entityIn, this.MiniDamage);
        }
        if ((flag = super.doHurtTarget(entityIn)) && green) {
            LivingEntity target;
            if (this.getSkin() == 120) {
                SRPPotions.applyStackPotion(MobEffects.MOVEMENT_SLOWDOWN, (LivingEntity)entityIn, 100, 1);
            }
            this.setAttackCooldownAni(100);
            if (this.disloNumberSix && entityIn instanceof Player) {
                Player playerIn = (Player)entityIn;
                SRPSaveData data = SRPSaveData.get(this.level());
                int count = data.getCurrentCode(DimKeys.of(this.level()), 6);
                if (count >= 1) {
                    for (EquipmentSlot armorSlot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                        playerIn.getItemBySlot(armorSlot).hurtAndBreak(count, playerIn, armorSlot);
                    }
                } else {
                    this.disloNumberSix = false;
                }
            }
            if (this.getRandom().nextDouble() < (double)this.cothSpread && !(target = (LivingEntity)entityIn).hasEffect(SRPPotions.COTH_E) && !target.hasEffect(SRPPotions.EPEL_E)) {
                target.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
            }
            this.attackEntityAsMobFood(entityIn, true, this.foodRootNumber, this.foodRott);
        }
        return flag;
    }

    public boolean attackEntityAsMobMinimum(LivingEntity target, float MinimumDamage) {
        if (MinimumDamage <= 0.0f) {
            return false;
        }
        if (!this.geneMindam) {
            return false;
        }
        float f1 = target.getHealth();
        if (f1 <= 0.0f || f1 <= this.miniCap && this.miniCapA) {
            return false;
        }
        if (target instanceof Player) {
            ItemStack itemstack1;
            Player entityplayer = (Player)target;
            if (entityplayer.getAbilities().instabuild) {
                return false;
            }
            entityplayer.causeFoodExhaustion(this.foodSteal);
            ItemStack itemStack = itemstack1 = entityplayer.isUsingItem() ? entityplayer.getUseItem() : ItemStack.EMPTY;
            if (itemstack1.getItem() instanceof ShieldItem && entityplayer.isBlocking() && !entityplayer.getCooldowns().isOnCooldown(itemstack1.getItem())) {
                float f12 = 0.25f;
                if (this.getRandom().nextFloat() < f12 && this.type >= 31 || this.disloNumberSeventeen > 0) {
                    entityplayer.getCooldowns().addCooldown(itemstack1.getItem(), 100);
                    this.level().broadcastEntityEvent((Entity)entityplayer, (byte)30);
                }
                return true;
            }
        }
        DamageSource s = this.damageSources().mobAttack(this);
        float damage = 0.0f;
        if (target.hasEffect(SRPPotions.VIRA_E)) {
            damage = MinimumDamage * (float)(target.getEffect(SRPPotions.VIRA_E).getAmplifier() + 1);
        }
        damage += MinimumDamage;
        try {
            if (target.getCombatTracker() != null && s != null) {
                target.getCombatTracker().recordDamage(s, damage);
            }
        }
        catch (Exception f12) {
            // empty catch block
        }
        if (target.getAbsorptionAmount() > 0.0f) {
            target.setHealth(f1 - damage / 2.0f);
            target.setAbsorptionAmount(target.getAbsorptionAmount() - damage / 2.0f);
        } else {
            target.setHealth(f1 - damage);
        }
        this.level().broadcastEntityEvent((Entity)target, (byte)2);
        if (target.getHealth() <= 0.0f) {
            ItemStack itemstack = null;
            for (InteractionHand enumhand : InteractionHand.values()) {
                ItemStack itemstack1 = target.getItemInHand(enumhand);
                if (itemstack1.getItem() != Items.TOTEM_OF_UNDYING) continue;
                itemstack = itemstack1.copy();
                itemstack1.shrink(1);
                break;
            }
            if (itemstack != null) {
                if (target instanceof ServerPlayer) {
                    ServerPlayer entityplayermp = (ServerPlayer)target;
                    entityplayermp.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                    CriteriaTriggers.USED_TOTEM.trigger(entityplayermp, itemstack);
                }
                target.setHealth(1.0f);
                target.removeAllEffects();
                target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                target.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                target.level().broadcastEntityEvent((Entity)target, (byte)35);
            } else {
                target.die(s);
                this.killcount += 1.0;
            }
        }
        return true;
    }

    public void setLastHurtByMob(@Nullable LivingEntity livingBase) {
        if (livingBase instanceof Player && livingBase.hasEffect(SRPPotions.THE_SIGN_E)) {
            super.setLastHurtByMob(null);
            return;
        }
        super.setLastHurtByMob(livingBase);
    }

    public boolean attackEntityAsMobFood(Entity target, boolean hit, int foodnumber, double foodchance) {
        if (this.disloNumberSeventeen > 0) {
            foodnumber = 1;
            foodchance = 1.0;
        }
        if (this.getRandom().nextDouble() >= foodchance && hit) {
            return false;
        }
        if (!(target instanceof Player)) {
            return false;
        }
        if (foodnumber <= 0) {
            return false;
        }
        Player player = (Player)target;
        if (player.getAbilities().instabuild) {
            return false;
        }
        for (int i = 0; i < player.getInventory().items.size(); ++i) {
            if (!(((ItemStack)player.getInventory().items.get(i)).getItem() instanceof Item)) continue;
            int amm = this.getRandom().nextInt(foodnumber) + 1;
            ((ItemStack)player.getInventory().items.get(i)).shrink(amm);
            ItemStack stack = new ItemStack(SRPItems.infected_drop.get(), amm);
            ItemEntity entityitem = new ItemEntity(this.level(), target.getX(), target.getY(), target.getZ(), stack);
            entityitem.setDefaultPickUpDelay();
            this.level().addFreshEntity((Entity)entityitem);
            return true;
        }
        if (player.getItemBySlot(EquipmentSlot.OFFHAND).getItem() instanceof Item) {
            int amm = this.getRandom().nextInt(foodnumber) + 1;
            player.getItemBySlot(EquipmentSlot.OFFHAND).shrink(amm);
            ItemStack stack = new ItemStack(SRPItems.infected_drop.get(), amm);
            ItemEntity entityitem = new ItemEntity(this.level(), target.getX(), target.getY(), target.getZ(), stack);
            entityitem.setDefaultPickUpDelay();
            this.level().addFreshEntity((Entity)entityitem);
            return true;
        }
        return false;
    }

    @Override
    public boolean killedEntity(ServerLevel serverLevel, LivingEntity entity) {
        boolean result = super.killedEntity(serverLevel, entity);
        this.onKillEntity(entity);
        return result;
    }

    /** 1.12 onKillEntity hook (called from killedEntity). */
    public void onKillEntity(LivingEntity entityLivingIn) {
        if (this.level().isClientSide) {
            return;
        }
        if (this.killcount >= 0.0) {
            if (this.getSkin() != 120) {
                this.killcount += 1.0;
            } else if (this.getRandom().nextInt(3) == 0) {
                this.killcount += 1.0;
            }
        }
        if (SRPConfigSystems.useEvolution) {
            if (this.hasEffect(SRPPotions.PIVOT_E)) {
                int amp = this.getEffect(SRPPotions.PIVOT_E).getAmplifier();
                SRPSaveData.get(this.level()).setTotalKills(DimKeys.of(this.level()), SRPConfigSystems.valueKill * (SRPConfigSystems.pivotPointMultiplier * (amp + 1)), true, this.level(), true, 17);
            } else {
                SRPSaveData.get(this.level()).setTotalKills(DimKeys.of(this.level()), SRPConfigSystems.valueKill, true, this.level(), true, 16);
            }
        }
        if (SRPConfigWorld.originActivated) {
            ParasiteEventWorld.setOriginInHealth(this.level(), this.blockPosition(), (int)((double)entityLivingIn.getMaxHealth() * SRPConfigWorld.originKillMultiplier), true);
            if (SRPConfigWorld.originTriggerKill > this.level().random.nextDouble()) {
                ParasiteEventWorld.placeOriginInWorld(this.level(), BlockPos.containing(this.getX(), this.getY(), this.getZ()), SRPConfigWorld.originHealth, SRPConfigWorld.originRadius);
            }
        }
        if (entityLivingIn.hasEffect(SRPPotions.COTH_E) && this.getRandom().nextDouble() < (double)SRPConfigSystems.cothConvert) {
            ParasiteEventEntity.convertEntity(entityLivingIn, entityLivingIn.getPersistentData(), true, SRPConfigSystems.COTHVictimParasite);
        }
        if (this.hasEffect(SRPPotions.PARATE_E)) {
            int bonuss = this.getEffect(SRPPotions.PARATE_E).getAmplifier() + 1;
            if (entityLivingIn.getAttribute(Attributes.MAX_HEALTH) != null) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + entityLivingIn.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * (SRPConfigSystems.parateMuch * (double)bonuss));
            }
            if (entityLivingIn.getAttribute(Attributes.ARMOR) != null) {
                this.getAttribute(Attributes.ARMOR).setBaseValue(this.getAttribute(Attributes.ARMOR).getBaseValue() + entityLivingIn.getAttribute(Attributes.ARMOR).getBaseValue() * (SRPConfigSystems.parateMuch * (double)bonuss));
            }
            if (entityLivingIn.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() + entityLivingIn.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * (SRPConfigSystems.parateMuch * (double)bonuss));
            }
        }
        this.heal(entityLivingIn.getHealth() * this.geneMobHealing);
        this.setWait(10);
    }

    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        if (this.disloNumberEleven > 0 && potioneffectIn.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
            return false;
        }
        MobEffect potion = potioneffectIn.getEffect().value();
        if (potion == SRPPotions.COTH_E.get() || potion == SRPPotions.VIRA_E.get() || potion == SRPPotions.CORRO_E.get() || potion == SRPPotions.DLER_E.get()) {
            return false;
        }
        return super.canBeAffected(potioneffectIn);
    }

    @Override
    protected boolean canRide(Entity entityIn) {
        if (entityIn instanceof Boat || entityIn instanceof AbstractMinecart) {
            return false;
        }
        return super.canRide(entityIn);
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        if (type == EntityType.PLAYER) {
            return true;
        }
        String name = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        if (name.contains("srparasites")) {
            return false;
        }
        return !SRPConfig.mobAttackingFull || !ParasiteEventEntity.checkName(name, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || this.level().isClientSide) {
            return super.mobInteract(player, hand);
        }
        Item wea = player.getItemBySlot(EquipmentSlot.MAINHAND).getItem();
        if (wea == SRPItems.itemEvolve.get()) {
            this.killcount += 1.0E9;
            this.killcount += 1.0E9;
            this.killcount += 1.0E9;
        } else if (wea == SRPItems.itemDevolve.get()) {
            this.canChangeVariant = true;
            this.die(this.damageSources().generic());
            this.discard();
        } else if (wea == SRPItems.itemVariant.get()) {
            this.cycleManualVariant();
        }
        return super.mobInteract(player, hand);
    }

    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (entitylivingbaseIn instanceof Player && entitylivingbaseIn.hasEffect(SRPPotions.THE_SIGN_E)) {
            super.setTarget(null);
            return;
        }
        if (this.targetNewCool > 0 && entitylivingbaseIn != null) {
            return;
        }
        super.setTarget(entitylivingbaseIn);
        if (entitylivingbaseIn != null && entitylivingbaseIn != this.last) {
            this.doLast(entitylivingbaseIn);
            this.last = entitylivingbaseIn;
        }
    }

    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn, int negative) {
        this.setTarget(entitylivingbaseIn);
        this.targetNewCool = negative;
    }

    protected void doLast(LivingEntity entitylivingbaseIn) {
        if (entitylivingbaseIn == null) {
            return;
        }
        SRPWorldData data = SRPWorldData.get(this.level());
        if (data == null) {
            return;
        }
        if (data.nearestInfectionPosition(false, this.blockPosition()) == null) {
            return;
        }
        entitylivingbaseIn.addEffect(new MobEffectInstance(SRPPotions.SPOT_E, 1200, 0, false, false));
        if (SRPConfigSystems.useOneMind) {
            ParasiteEventEntity.alertOthers(this, entitylivingbaseIn, this.level(), 7);
        }
    }

    public int canBeStored() {
        return this.canBeStored;
    }

    public int getSelfeState() {
        return (Integer)this.entityData.get(SELFE);
    }

    public void setSelfeState(int state) {
        this.entityData.set(SELFE, state);
    }

    public float getMiniDamage() {
        return this.MiniDamage;
    }

    public int getLLeap() {
        return this.liquidLeap;
    }

    public void setWorkTask(boolean in) {
        this.canWorkTask = in;
    }

    public boolean shouldWorkTask() {
        return this.canWorkTask;
    }

    public void setParasiteToFollow(@Nullable EntityParasiteBase in) {
        this.owner = in;
    }

    public EntityParasiteBase getParasiteFollowing() {
        if (this.owner != null) {
            if (this.owner.isAlive()) {
                return this.owner;
            }
            this.setParasiteToFollow(null);
        }
        return null;
    }

    public void SetRooter(@Nullable EntityParasiteBase in) {
        this.notOwner = in;
    }

    public byte getParasiteStatus() {
        return (Byte)this.entityData.get(SPECIAL);
    }

    public void setParasiteStatus(int state) {
        this.entityData.set(SPECIAL, (byte) (((byte)state)));
    }

    public void setWait(int in) {
        this.waitInt = in;
    }

    public int getWait() {
        return this.waitInt;
    }

    public void resetIdleTime() {
        this.noActionTime = 0;
    }

    public byte getPhaseCreated() {
        return this.phaseCreated;
    }

    public byte getLevelCreated() {
        return this.levelCreated;
    }

    public int getSkin() {
        return ((Byte)this.entityData.get(SKIN)).intValue();
    }

    public int getMaxManualVariants() {
        return 10;
    }

    public void cycleManualVariant() {
        this.canChangeVariant = true;
        int next = this.getSkin() + 1;
        if (next >= this.getMaxManualVariants()) {
            next = 0;
        }
        this.setSkin(next);
        this.canChangeVariant = false;
    }

    public boolean getColdL() {
        return (Boolean)this.entityData.get(COLD_L);
    }

    public boolean getDislo15() {
        return (Boolean)this.entityData.get(DISLO15);
    }

    public void setDislo15(boolean in) {
        this.entityData.set(DISLO15, in);
    }

    public void setSkin(int texture) {
        if (this.getSkin() == 120 && !this.canChangeVariant) {
            return;
        }
        this.entityData.set(SKIN, (byte) (((byte)texture)));
    }

    public int getCCDeathValue() {
        return this.oneMindDeathValue;
    }

    public void setAttackCooldownAni(int i) {
        this.attackCooldown = i;
    }

    public int getAttackCooldownAni() {
        return this.attackCooldown;
    }

    public void setKillC(double i) {
        this.killcount = i;
    }

    public double getKillC() {
        return this.killcount;
    }

    public byte getParasiteType() {
        return this.type;
    }

    public void playAmbientSound() {
        if (((Boolean)this.entityData.get(DISLO15)).booleanValue()) {
            return;
        }
        super.playAmbientSound();
    }

    public void die(DamageSource cause) {
        super.die(cause);
        if (!this.level().isClientSide) {
            if (this.onDeathDislo(cause)) {
                return;
            }
            this.spawnCyst();
            ParasiteEventEntity.spawnBeckon(this.level(), cause, this);
            ParasiteEventEntity.leaveScent(this.level(), cause, this);
            ParasiteEventWorld.setOriginInHealth(this.level(), this.blockPosition(), (int)(-((double)this.getMaxHealth() * SRPConfigWorld.originParasiteDeath)), true);
            if (SRPConfigSystems.useEvolution && !this.hasEffect(SRPPotions.DEBAR_E)) {
                SRPSaveData.get(this.level()).setTotalKills(DimKeys.of(this.level()), -this.valueEvDeath, true, this.level(), false, 18);
            }
            if (cause.getEntity() != null && cause.getEntity() instanceof Player) {
                this.checkWeapon((Player)cause.getEntity());
            }
        }
    }

    protected boolean onDeathDislo(DamageSource cause) {
        int count;
        ParasiteEventWorld.setDisloWorldPhase(this.level(), SRPAttributes.EVENTPARADEATH, SRPConfigSystems.chanceEventParaDeath, 0, null);
        SRPSaveData data = SRPSaveData.get(this.level());
        if (this.disloNumberTwo && (count = data.getCurrentCode(DimKeys.of(this.level()), 2)) >= 1 && cause.getEntity() instanceof LivingEntity) {
            count = (int)((float)count + this.getMaxHealth());
            int furaa = data.getCurrentCodeDuration(DimKeys.of(this.level()), 2);
            SRPPotions.applyStackPotion(SRPPotions.JUGG_E, (LivingEntity)cause.getEntity(), furaa * 20 + 50, 1);
            data.setCurrentCode(DimKeys.of(this.level()), 2, count, furaa, null, false, 0);
        }
        if (this.disloNumberSeven && (count = data.getCurrentCode(DimKeys.of(this.level()), 7)) >= 1) {
            AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(7.0);
            for (EntityParasiteBase mob : this.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb)) {
                mob.heal(count);
            }
            this.playSound(SRPSounds.RATHOL_BOOM.get(), 0.2f, 0.7f);
            this.particleStatus((byte)7);
        }
        if (this.disloNumberEight) {
            count = data.getCurrentCode(DimKeys.of(this.level()), 8);
            if (count >= 1) {
                AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(7.0);
                for (LivingEntity mob : this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb)) {
                    if (mob instanceof EntityParasiteBase) continue;
                    mob.hurt(this.damageSources().mobAttack(this), count);
                }
            }
            this.playSound(SRPSounds.RATHOL_BOOM.get(), 0.2f, 0.7f);
            this.particleStatus((byte)7);
        }
        if (this.disloNumberNine) {
            count = data.getCurrentCode(DimKeys.of(this.level()), 9);
            if (count >= 1) {
                AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(7.0);
                for (Player mob : this.level().getEntitiesOfClass(Player.class, axisalignedbb)) {
                    mob.causeFoodExhaustion(count);
                }
            }
            this.playSound(SRPSounds.RATHOL_BOOM.get(), 0.2f, 0.7f);
            this.particleStatus((byte)7);
        }
        if (this.disloNumberEighteen && data.getCurrentCode(DimKeys.of(this.level()), 18) >= 1) {
            this.xpReward = 0;
        }
        return false;
    }

    private void checkWeapon(Player playerIn) {
        if (!SRPConfigSystems.useScent) {
            return;
        }
        if (this.getLevelCreated() < SRPConfigSystems.deveScentUse) {
            return;
        }
        Item ga = playerIn.getItemBySlot(EquipmentSlot.MAINHAND).getItem();
        if ((ga instanceof WeaponToolMeleeBase || ga instanceof WeaponToolRangeBase) && this.getRandom().nextInt(10) == 0) {
            playerIn.addEffect(new MobEffectInstance(SRPPotions.PREY_E, 600, 0, false, false));
        }
    }

    protected void tickDeath() {
        if (this.canModRender == 1) {
            if (this.madeRng == 0) {
                this.setSelfeState(1);
                this.setParasiteStatus(6);
                this.dyingBurst(true, 1);
            } else {
                this.onDeathUpdateOG();
            }
        } else if (this.canModRender == 2) {
            if (this.madeRng == 0) {
                this.selfExplode();
                this.OnDeathHelper();
            } else {
                this.onDeathUpdateOG();
            }
        } else {
            this.onDeathUpdateOG();
        }
    }

    protected void onDeathUpdateOG() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            if (!this.level().isClientSide && (this.lastHurtByPlayerTime > 0 && this.shouldDropLoot() && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) && !this.hasEffect(SRPPotions.DEBAR_E)) {
                if (SRPConfigSystems.useEvolution) {
                    if (SRPSaveData.get(this.level()).getEvolutionPhase(DimKeys.of(this.level())) < SRPConfigSystems.evolutionPArasitesWithoutXP) {
                        this.dropExperienceOrbs();
                    }
                } else {
                    this.dropExperienceOrbs();
                }
            }
            this.discard();
            if (this.level().isClientSide) {
                for (int k = 0; k < 20; ++k) {
                    this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                }
            }
        }
    }

    protected void dyingBurst(boolean fromDeath, int value) {
        int i = this.getSelfeState();
        if (i <= 0 || this.timeSinceIgnited == 0) {
            // empty if block
        }
        this.timeSinceIgnited += i * value;
        if (this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
        }
        if (this.timeSinceIgnited >= this.fuseTime) {
            this.timeSinceIgnited = this.fuseTime;
            this.selfExplode();
            if (fromDeath) {
                this.OnDeathHelper();
            }
        }
    }

    /** 1.12 loop: split the (event adjusted) experience into orbs at the mob's position. */
    private void dropExperienceOrbs() {
        int i = EventHooks.getExperienceDrop(this, this.lastHurtByPlayer, this.getBaseExperienceReward());
        if (this.level() instanceof ServerLevel serverLevel) {
            ExperienceOrb.award(serverLevel, this.position(), i);
        }
    }

    public void OnDeathHelper() {
        if (!this.level().isClientSide && (this.lastHurtByPlayerTime > 0 && this.shouldDropLoot() && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
            this.dropExperienceOrbs();
        }
    }

    protected void selfExplode() {
        if (this.level().isClientSide) {
            this.spawnEffectsGore();
        }
        if (!this.level().isClientSide) {
            this.spawnGore();
            this.playSound(SRPSounds.MOBEXPLOTION.get(), 1.0f, 1.0f);
            this.dead = true;
            this.discard();
            EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY(), this.getZ());
            entityareaeffectcloud.setRadius(this.getBbWidth() * 1.5f, 0.5f);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 0));
            entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
            this.level().addFreshEntity((Entity)entityareaeffectcloud);
        }
    }

    protected void spawnGore() {
    }

    protected void doPush(Entity entityIn) {
        super.doPush(entityIn);
    }

    /** 1.12 getCanSpawnHere: the mod's own spawner calls this (see SRPWorldEntitySpawner). */
    public boolean getCanSpawnHere() {
        boolean lightIgnoring;
        if (SRPConfigSystems.useEvolution) {
            lightIgnoring = this.phaseCreated >= SRPConfigSystems.evolutionSpawningIgnoreSunlight || this.phaseCreated == -1 && SRPConfigSystems.phaseLightlessMinusOne;
        } else {
            lightIgnoring = SRPConfig.ignoreL;
        }
        BlockState iblockstate = this.level().getBlockState(this.blockPosition().below());
        return iblockstate.isValidSpawn(this.level(), this.blockPosition().below(), this.getType()) && this.level().getDifficulty() != Difficulty.PEACEFUL
                && (lightIgnoring ? this.isValidLightLevelTwo() : this.isValidLightLevelOne()) && SRPConfig.spawnDays <= (int)this.level().getGameTime();
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnType) {
        return this.getCanSpawnHere();
    }

    @Override
    public void checkDespawn() {
        if (this.isPersistenceRequired()) {
            this.noActionTime = 0;
            return;
        }
        if ((this.noActionTime & 0x1F) == 31) {
            MobDespawnEvent event = new MobDespawnEvent(this, (ServerLevel) this.level());
            NeoForge.EVENT_BUS.post(event);
            if (event.getResult() == MobDespawnEvent.Result.DENY) {
                this.noActionTime = 0;
                return;
            }
            if (event.getResult() == MobDespawnEvent.Result.ALLOW) {
                this.spawnCyst();
                this.storeBefDes();
                this.discard();
                return;
            }
        }
        Player entity = this.level().getNearestPlayer(this, -1.0);
        if (entity != null) {
            double d3 = entity.distanceToSqr(this);
            if (this.removeWhenFarAway(0.0) && d3 > 16384.0) {
                this.spawnCyst();
                this.storeBefDes();
                this.discard();
            }
            if (this.noActionTime > 600 && this.getRandom().nextInt(800) == 0 && d3 > 1024.0 && this.removeWhenFarAway(0.0)) {
                this.spawnCyst();
                this.storeBefDes();
                this.discard();
            } else if (d3 < 1024.0) {
                this.noActionTime = 0;
            }
        }
    }

    public void reduceIdleChance(int chance, int amount) {
        if (this.getRandom().nextInt(chance) == 0) {
            this.noActionTime -= amount;
        }
    }

    protected void storeBefDes() {
        Entity dod;
        if (this.dodFatherID != 0 && (dod = this.level().getEntity(this.dodFatherID)) != null && dod.isAlive() && dod instanceof EntityPDispatcher) {
            ((EntityPDispatcher)dod).storeParasite(this);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected boolean isValidLightLevelOne() {
        if (this.level().getBiome(this.blockPosition()).value() instanceof BiomeParasiteBase) {
            return this.isValidLightLevelTwo();
        }
        BlockPos blockpos = BlockPos.containing(this.getX(), this.getBoundingBox().minY, this.getZ());
        if (this.level().getBrightness(LightLayer.SKY, blockpos) > this.getRandom().nextInt(32)) {
            return false;
        }
        int i = this.level().getMaxLocalRawBrightness(blockpos);
        if (this.level().isThundering()) {
            i = this.level().getMaxLocalRawBrightness(blockpos, 10);
        }
        if (i > this.getRandom().nextInt(8)) return false;
        BlockPos blockPos = BlockPos.containing(this.getX(), this.getBoundingBox().minY, this.getZ());
        if (!(this.getWalkTargetValue(blockPos) >= 0.0f)) return false;
        return true;
    }

    protected boolean isValidLightLevelTwo() {
        BlockPos blockpos = BlockPos.containing(this.getX(), this.getBoundingBox().minY, this.getZ());
        int light = this.level().getBrightness(LightLayer.BLOCK, blockpos);
        if (light > this.getRandom().nextInt(1000) || light > 7) {
            return false;
        }
        return this.getRandom().nextInt(8) == 0;
    }

    public void lookAt(Entity in) {
        this.lookAt(in.getX(), in.getY(), in.getZ());
    }

    public void lookAt(double x, double y, double z) {
        double dx = x - this.getX();
        double dy = y - (this.getY() + (double)this.getEyeHeight());
        double dz = z - this.getZ();
        double yaw = Math.atan2(dz, dx) * 57.29577951308232 - 90.0;
        double distance = Math.sqrt(dx * dx + dz * dz);
        double pitch = -Math.atan2(dy, distance) * 57.29577951308232;
        this.setYRot((float)yaw);
        this.setXRot((float)pitch);
    }

    private boolean isInParasiteBiome() {
        return this.getRandom().nextInt(8) == 0 && this.level().getBiome(this.blockPosition()).value() instanceof BiomeParasiteBase;
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (SRPConfigSystems.useEvolution && !this.level().isClientSide) {
            this.phaseCreated = SRPSaveData.get(this.level()).getEvolutionPhase(DimKeys.of(this.level()));
            if (this.phaseCreated >= SRPConfigSystems.evolutionParasiteStatIncrease) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * (double)(1.0f + SRPConfigSystems.evolutionParasiteStatIncreaseValue));
                this.getAttribute(Attributes.ARMOR).setBaseValue(this.getAttribute(Attributes.ARMOR).getBaseValue() * (double)(1.0f + SRPConfigSystems.evolutionParasiteStatIncreaseValue));
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * (double)(1.0f + SRPConfigSystems.evolutionParasiteStatIncreaseValue));
            }
        }
        return floo;
    }

    protected boolean applyColdBiome() {
        Holder<Biome> holder = this.level().getBiome(this.blockPosition());
        Biome c = holder.value();
        if (c == null) {
            return false;
        }
        if (this.isBiomeBlacklistedForFrozen(holder)) {
            return false;
        }
        if (c.getBaseTemperature() <= (float)SRPConfigWorld.frozenVariantTempThreshold || c.coldEnoughToSnow(this.blockPosition()) || this.canChangeVariant) {
            this.setSkin(120);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() * 0.6);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * 1.4);
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() * 1.3);
            return true;
        }
        return false;
    }

    private boolean isBiomeBlacklistedForFrozen(Holder<Biome> biome) {
        if (biome == null || biome.unwrapKey().isEmpty()) {
            return false;
        }
        String biomeKey = biome.unwrapKey().get().location().toString();
        for (String raw : SRPConfigWorld.frozenVariantBiomeBlacklist) {
            if (raw == null || raw.isEmpty() || !biomeKey.equalsIgnoreCase(raw.trim())) continue;
            return true;
        }
        return false;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("dsltwo", this.disloNumberTwo);
        compound.putBoolean("dslthree", this.disloNumberThree);
        compound.putBoolean("dslfour", this.disloNumberFour);
        compound.putBoolean("dslsix", this.disloNumberSix);
        compound.putBoolean("dslseven", this.disloNumberSeven);
        compound.putBoolean("dsleight", this.disloNumberEight);
        compound.putBoolean("dslnine", this.disloNumberNine);
        compound.putInt("dsleleven", this.disloNumberEleven);
        compound.putInt("dslfifteen", this.disloNumberFifteen);
        compound.putBoolean("dslfifteenb", ((Boolean)this.entityData.get(DISLO15)).booleanValue());
        compound.putInt("dslsixteen", this.disloNumberSixteen);
        compound.putInt("dslseventeen", this.disloNumberSeventeen);
        compound.putBoolean("dsleighteen", this.disloNumberEighteen);
        compound.putInt("dslnineteen", this.disloNumberNineteen);
        compound.putDouble("dslnineteenv", this.disloNumberNineteenValue);
        compound.putBoolean("dsltyone", this.disloNumberTwentyone);
        compound.putBoolean("dsltytwo", this.disloNumberTwentytwo);
        compound.putInt("parasitetype", this.getSkin());
        compound.putBoolean("parasitedespawn", this.removeWhenFarAway(0.0));
        compound.putFloat("parasitekills", (float)this.getKillC());
        compound.putBoolean("parasitecolob", this.getApplyColonyB());
        compound.putBoolean("parasitecoldl", ((Boolean)this.entityData.get(COLD_L)).booleanValue());
        compound.putByte("phasecreat", this.phaseCreated);
        compound.putByte("levelcreat", this.levelCreated);
        compound.putByte("paragener", this.paraGeneration);
        ListTag allResS = new ListTag();
        ListTag allResI = new ListTag();
        if (this.inbBlockName.size() != this.inbBlockNumber.size()) {
            return;
        }
        for (int i = 0; i < this.inbBlockName.size(); ++i) {
            String res = this.inbBlockName.get(i);
            CompoundTag resT = new CompoundTag();
            resT.putString("blocks" + i, res);
            allResS.add((Tag)resT);
            int resi = this.inbBlockNumber.get(i);
            CompoundTag resU = new CompoundTag();
            resU.putInt("blocki" + i, resi);
            allResI.add((Tag)resU);
        }
        compound.put("srpinvblocksname", (Tag)allResS);
        compound.put("srpinvblocksnumber", (Tag)allResI);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("dsltwo", 99)) {
            this.disloNumberTwo = compound.getBoolean("dsltwo");
        }
        if (compound.contains("dslthree", 99)) {
            this.disloNumberThree = compound.getBoolean("dslthree");
        }
        if (compound.contains("dslfour", 99)) {
            this.disloNumberFour = compound.getBoolean("dslfour");
        }
        if (compound.contains("dslsix", 99)) {
            this.disloNumberSix = compound.getBoolean("dslsix");
        }
        if (compound.contains("dslseven", 99)) {
            this.disloNumberSeven = compound.getBoolean("dslseven");
        }
        if (compound.contains("dsleight", 99)) {
            this.disloNumberEight = compound.getBoolean("dsleight");
        }
        if (compound.contains("dslnine", 99)) {
            this.disloNumberNine = compound.getBoolean("dslnine");
        }
        if (compound.contains("dsleleven", 99)) {
            this.disloNumberEleven = compound.getInt("dsleleven");
        }
        if (compound.contains("dslfifteen", 99)) {
            this.disloNumberFifteen = compound.getInt("dslfifteen");
        }
        if (compound.contains("dslfifteenb", 99)) {
            this.entityData.set(DISLO15, compound.getBoolean("dslfifteenb"));
        }
        if (compound.contains("dslsixteen", 99)) {
            this.disloNumberSixteen = compound.getInt("dslsixteen");
        }
        if (compound.contains("dslseventeen", 99)) {
            this.disloNumberSeventeen = compound.getInt("dslseventeen");
        }
        if (compound.contains("dsleighteen", 99)) {
            this.disloNumberEighteen = compound.getBoolean("dsleighteen");
        }
        if (compound.contains("dslnineteen", 99)) {
            this.disloNumberNineteen = compound.getInt("dslnineteen");
        }
        if (compound.contains("dslnineteenv", 99)) {
            this.disloNumberNineteenValue = compound.getDouble("dslnineteenv");
        }
        if (compound.contains("dsltyone", 99)) {
            this.disloNumberTwentyone = compound.getBoolean("dsltyone");
        }
        if (compound.contains("dsltytwo", 99)) {
            this.disloNumberTwentytwo = compound.getBoolean("dsltytwo");
        }
        if (compound.contains("parasitetype", 99)) {
            this.setSkin(compound.getInt("parasitetype"));
        }
        if (compound.contains("parasitedespawn", 99)) {
            this.cannotDespawn(compound.getBoolean("parasitedespawn"));
        }
        if (compound.contains("parasitekills", 99)) {
            this.setKillC(compound.getFloat("parasitekills"));
        }
        if (compound.contains("parasitecolob", 99)) {
            this.setApplyColonyB(compound.getBoolean("parasitecolob"));
        }
        if (compound.contains("parasitecoldl", 99)) {
            this.entityData.set(COLD_L, compound.getBoolean("parasitecoldl"));
        }
        if (compound.contains("phasecreat", 99)) {
            this.phaseCreated = compound.getByte("phasecreat");
        }
        if (compound.contains("levelcreat", 99)) {
            this.levelCreated = compound.getByte("levelcreat");
        }
        if (compound.contains("paragener", 99)) {
            this.paraGeneration = compound.getByte("paragener");
        }
        if (compound.contains("srpinvblocksname")) {
            ListTag allResS = compound.getList("srpinvblocksname", 10);
            ListTag allResI = compound.getList("srpinvblocksnumber", 10);
            if (allResS.size() != allResI.size()) {
                return;
            }
            for (int i = 0; i < allResS.size(); ++i) {
                CompoundTag resT = allResS.getCompound(i);
                String res = resT.getString("blocks" + i);
                this.inbBlockName.add(i, res);
                CompoundTag resU = allResI.getCompound(i);
                int resi = resU.getInt("blocki" + i);
                this.inbBlockNumber.add(i, resi);
            }
        }
    }

    public void particleStatus(byte id) {
        this.level().broadcastEntityEvent((Entity)this, id);
    }

    protected void spawnEffectsGore() {
    }

    public void handleEntityEvent(byte id) {
        switch (id) {
            case 5: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(SRPEnumParticle.RHAPPY, 0, 0, 0);
                }
                break;
            }
            case 6: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                }
                break;
            }
            case 7: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(ParticleTypes.EXPLOSION);
                }
                break;
            }
            case 8: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                }
                break;
            }
            case 10: {
                for (int i = 0; i <= 40; ++i) {
                    this.spawnParticles(ParticleTypes.ENCHANTED_HIT);
                }
                break;
            }
            case 11: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(SRPEnumParticle.GCLOUD, 0, 0, 0);
                }
                break;
            }
            case 12: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(SRPEnumParticle.GCLOUD, 0, 0, 0);
                }
                break;
            }
            case 13: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(SRPEnumParticle.GSPLASH, 4, -1, -1);
                }
                break;
            }
            case 40: {
                this.madeRng = 0;
                break;
            }
            case 43: {
                BlockState iddd = this.level().getBlockState(BlockPos.containing(this.getX(), this.getY(), this.getZ()).below());
                for (int i = 0; i <= 10; ++i) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, iddd), this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY(), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() * 0.02);
                }
                break;
            }
            case 51: {
                for (int i = 0; i <= 60; ++i) {
                    if (i % 4 == 0) {
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 150, 0, 0);
                    }
                    if (i % 5 != 0) continue;
                    this.spawnParticles(SRPEnumParticle.GSPLASH, 2, -1, -1);
                }
                break;
            }
            case 52: {
                for (int i = 0; i <= 80; ++i) {
                    if (i % 3 == 0) {
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 200, 200, 0);
                    }
                    if (i % 5 != 0) continue;
                    this.spawnParticles(SRPEnumParticle.GSPLASH, 3, -1, -1);
                }
                break;
            }
            default: {
                super.handleEntityEvent(id);
            }
        }
    }

    public void spawnParticles(ParticleOptions particleType) {
        double d0 = this.getRandom().nextGaussian() * 0.02;
        double d1 = this.getRandom().nextGaussian() * 0.02;
        double d2 = this.getRandom().nextGaussian() * 0.02;
        this.level().addParticle(particleType, this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY() + 0.5 + (double)(this.getRandom().nextFloat() * this.getBbHeight()), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), d0, d1, d2);
    }

    public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b) {
        double d0 = this.getRandom().nextGaussian() * 0.02;
        double d1 = this.getRandom().nextGaussian() * 0.02;
        double d2 = this.getRandom().nextGaussian() * 0.02;
        ParticleSpawner.spawnParticle(particleType, this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY() + 0.5 + (double)(this.getRandom().nextFloat() * this.getBbHeight()), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), d0, d1, d2, r, g, b);
    }

    public void spawnParticlesGore(SRPEnumParticle particleType, int r, int g, int b) {
        this.spawnParticlesGore(particleType, r, g, b, 1.0, 4.0);
    }

    public void spawnParticlesGore(SRPEnumParticle particleType, int r, int g, int b, double xF, double yF) {
        double d0 = (float)this.getX() + this.getRandom().nextFloat();
        double d1 = (float)this.getY() + this.getRandom().nextFloat();
        double d2 = (float)this.getZ() + this.getRandom().nextFloat();
        double d3 = d0 - this.getX();
        double d4 = d1 - this.getY();
        double d5 = d2 - this.getZ();
        double d6 = (float)Math.sqrt((double)(d3 * d3 + d4 * d4 + d5 * d5));
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d3 = d3 * (d7 *= (double)(this.getRandom().nextFloat() * this.getRandom().nextFloat() + 0.3f)) * xF;
        d4 = d4 * d7 * yF;
        d5 = d5 * d7 * xF;
        d3 = Math.min(d3, 0.2) * (Math.random() * 2.0 - 1.0);
        d4 = Math.min(d4, 0.6) * (Math.random() * 2.0 - 1.0);
        d5 = Math.min(d5, 0.2) * (Math.random() * 2.0 - 1.0);
        ParticleSpawner.spawnParticle(particleType, this.getX(), this.getY() + 0.2 + (double)this.getBbHeight(), this.getZ(), d3, d4, d5, r, g, b);
    }

    public void spawnParticlesGoreMouth(SRPEnumParticle particleType, int r, int g, int b, double xF, double yF) {
        double d0 = (float)this.getX() + this.getRandom().nextFloat();
        double d1 = (float)this.getY() + this.getRandom().nextFloat();
        double d2 = (float)this.getZ() + this.getRandom().nextFloat();
        double d3 = d0 - this.getX();
        double d4 = d1 - this.getY();
        double d5 = d2 - this.getZ();
        double d6 = (float)Math.sqrt((double)(d3 * d3 + d4 * d4 + d5 * d5));
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d3 = d3 * (d7 *= (double)(this.getRandom().nextFloat() * this.getRandom().nextFloat() + 0.3f)) * xF;
        d4 = d4 * d7 * yF;
        d5 = d5 * d7 * xF;
        d3 = Math.min(d3, 0.2) * (Math.random() * 2.0 - 1.0);
        d4 = Math.min(d4, 0.6) * (Math.random() * 2.0 - 1.0);
        d5 = Math.min(d5, 0.2) * (Math.random() * 2.0 - 1.0);
        ParticleSpawner.spawnParticle(particleType, this.getX(), this.getY(), this.getZ(), d3, d4, d5, r, g, b);
    }

    public void spawnParticlesGoreBox(SRPEnumParticle particleType, int r, int g, int b, double xF, double yF) {
        double d0 = (float)this.getX() + this.getRandom().nextFloat();
        double d1 = (float)this.getY() + this.getRandom().nextFloat();
        double d2 = (float)this.getZ() + this.getRandom().nextFloat();
        double d3 = d0 - this.getX();
        double d4 = d1 - this.getY();
        double d5 = d2 - this.getZ();
        double d6 = (float)Math.sqrt((double)(d3 * d3 + d4 * d4 + d5 * d5));
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d3 = d3 * (d7 *= (double)(this.getRandom().nextFloat() * this.getRandom().nextFloat() + 0.3f)) * xF;
        d4 = d4 * d7 * yF;
        d5 = d5 * d7 * xF;
        d3 = Math.min(d3, 0.2) * (Math.random() * 2.0 - 1.0);
        d4 = Math.min(d4, 0.6) * (Math.random() * 2.0 - 1.0);
        d5 = Math.min(d5, 0.2) * (Math.random() * 2.0 - 1.0);
        ParticleSpawner.spawnParticle(particleType, this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY() + 0.5 + (double)(this.getRandom().nextFloat() * this.getBbHeight()), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), d3, d4, d5, r, g, b);
    }

    public void spawnParticles(ParticleOptions particleType, double d0, double d1, double d2, double d3, double d4, double d5) {
        this.level().addParticle(particleType, d0, d1, d2, d3, d4, d5);
    }

    public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b, double d0, double d1, double d2, double d3, double d4, double d5) {
        ParticleSpawner.spawnParticle(particleType, d0, d1, d2, d3, d4, d5, r, g, b);
    }

    public float getSelfeFlashIntensity(float p_70831_1_) {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_) / (float)(this.fuseTime - 2);
    }

    public boolean isSwingingArms() {
        return false;
    }

    public float getscale(float p_70831_1_) {
        return 0.0f;
    }

    public boolean getStillAni() {
        return this.still;
    }

    public float getAniTick() {
        return this.aniticks;
    }

    public void doSpecialSkill(byte id) {
        switch (id) {
            case 13: {
                this.skillBreakBlocks();
                return;
            }
            case 14: {
                this.skillLeap();
                return;
            }
        }
    }

    public boolean getFinished(byte attID) {
        switch (attID) {
            case 13: {
                return this.SkillBGflag;
            }
            case 14: {
                return this.SkillLeapFlag;
            }
        }
        return false;
    }

    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 13: {
                this.SkillBGflag = in;
            }
            case 14: {
                this.SkillLeapFlag = in;
            }
        }
    }

    public void setSkillBreakBlocksValues(float hardness, int heightIn, int rangeIn) {
        this.blockH = hardness;
        this.BGheight = heightIn;
        this.BGrange = rangeIn;
    }

    public float getBlockH() {
        if (this.getSkin() == 120) {
            return this.blockH * 1.5f;
        }
        if (this.getSkin() == 7) {
            return this.blockH * 2.0f;
        }
        return this.blockH;
    }

    public void skillBreakBlocks() {
        LivingEntity target;
        if (this.isRemoved()) {
            return;
        }
        if (this.getBlockH() == 0.0f) {
            return;
        }
        int blocksbroke = 0;
        if (!EventHooks.canEntityGrief((Level)this.level(), (Entity)this)) {
            return;
        }
        int i1 = Mth.floor((double)(this.getY() + 0.1));
        double l1 = this.getX();
        double i2 = this.getZ();
        boolean flag = false;
        int Brangeatm = this.BGrange;
        int offsetT = 0;
        if (this.getTarget() != null && (target = this.getTarget()).distanceToSqr(this.getX(), target.getY(), this.getZ()) < 9.0) {
            if (target.getY() - this.getY() < -1.0) {
                offsetT -= 2;
                if (!this.onGround()) {
                    --offsetT;
                }
            } else if (target.getY() - this.getY() > 2.0) {
                ++offsetT;
                this.BGrange = 0;
            }
        }
        for (int k2 = -1 * this.BGrange; k2 <= this.BGrange; ++k2) {
            for (int l2 = -1 * this.BGrange; l2 <= this.BGrange; ++l2) {
                for (int j = 1 + offsetT; j <= this.BGheight + offsetT; ++j) {
                    String name;
                    double i3 = l1 + (double)k2;
                    double k = i1 + j;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = BlockPos.containing(i3, k, l);
                    BlockState iblockstate = this.level().getBlockState(blockpos);
                    Block block = iblockstate.getBlock();
                    float bHard = iblockstate.getDestroySpeed(this.level(), blockpos);
                    if (!(bHard <= this.getBlockH()) || !(bHard >= 0.0f) || block instanceof IMetaName && block != SRPBlocks.ParasiteCanister.get() || block == SRPBlocks.BiomeHeart.get() || block == SRPBlocks.ColonyHeart.get() || block == SRPBlocks.ParasiteRubbleDense.get() || block == SRPBlocks.ParasiteCanisterActive.get() || block == SRPBlocks.dodN.get() || block instanceof LiquidBlock || block instanceof NetherPortalBlock || block instanceof EndGatewayBlock || block instanceof EndPortalFrameBlock || block instanceof EndPortalBlock || this.blockException(name = BuiltInRegistries.BLOCK.getKey(block).toString()) || block == Blocks.AIR || !iblockstate.canEntityDestroy(this.level(), blockpos, this) || !EventHooks.onEntityDestroyBlock((LivingEntity)this, (BlockPos)blockpos, (BlockState)iblockstate)) continue;
                    if (SRPConfig.cystActive) {
                        boolean bl = flag = this.destroyBlockPos(blockpos, false) || flag;
                        if (SRPConfig.doTileDrops) {
                            this.addToBlockInv(BlockIds.stateString(iblockstate));
                        }
                    } else {
                        this.destroyBlockPos(blockpos, SRPConfig.doTileDrops);
                    }
                    ++blocksbroke;
                }
            }
        }
        this.BGrange = Brangeatm;
        this.SkillBGflag = true;
    }

    protected boolean destroyBlockPos(BlockPos pos, boolean dropBlock) {
        BlockState iblockstate = this.level().getBlockState(pos);
        Block block = iblockstate.getBlock();
        if (iblockstate.isAir()) {
            return false;
        }
        if (this.getRandom().nextDouble() < SRPConfig.blockParticleChance) {
            this.level().levelEvent(2001, pos, Block.getId((BlockState)iblockstate));
        }
        if (this.getRandom().nextDouble() < SRPConfig.blockSoundChance) {
            SoundType soundtype = iblockstate.getSoundType();
            this.level().playSound((Player)null, pos, soundtype.getBreakSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f);
        }
        if (dropBlock && this.getRandom().nextDouble() < SRPConfig.blockDropChance) {
            Block.dropResources(iblockstate, this.level(), pos);
        }
        return this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    protected boolean blockException(String name) {
        return ParasiteEventEntity.checkName(name, SRPConfig.parasiteGriefingBlackList, SRPConfig.parasiteGriefingWhite);
    }

    public void addToBlockInv(String name) {
        boolean flag = true;
        for (int i = 0; i < this.inbBlockName.size(); ++i) {
            if (!this.inbBlockName.get(i).equals(name) || this.inbBlockNumber.get(i) > 64) continue;
            int iiii = this.inbBlockNumber.get(i) + 1;
            this.inbBlockNumber.set(i, iiii);
            flag = false;
            break;
        }
        if (flag) {
            if (this.inbBlockName.size() >= 22) {
                this.spawnCyst();
            }
            this.inbBlockName.add(name);
            this.inbBlockNumber.add(1);
        }
    }

    protected void spawnCyst() {
        if (this.inbBlockName.isEmpty()) {
            return;
        }
        double d0 = (float)this.getX() + this.level().random.nextFloat();
        double d1 = (float)this.getY() + this.level().random.nextFloat();
        double d2 = (float)this.getZ() + this.level().random.nextFloat();
        double d3 = d0 - this.getX();
        double d4 = d1 - this.getY();
        double d5 = d2 - this.getZ();
        double d6 = (float)Math.sqrt((double)(d3 * d3 + d4 * d4 + d5 * d5));
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d4 = d4 * d7 * 2.0;
        EntityGore bomb = new EntityGore(SRPEntities.GORE.get(), this.level());
        bomb.setType((byte)10);
        bomb.copyPosition((Entity)this);
        bomb.setMotion(d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)), d4, d5 *= d7, 0.25, 0.75);
        this.level().addFreshEntity((Entity)bomb);
        bomb.inbBlockName = new ArrayList<String>(this.inbBlockName);
        bomb.inbBlockNumber = new ArrayList<Integer>(this.inbBlockNumber);
        this.inbBlockName = new ArrayList();
        this.inbBlockNumber = new ArrayList();
    }

    public void setskillLeapValues(float leapY, double leapSpeed, int jumpRad) {
        this.leapMotionY = leapY;
        this.jumpSpeed = leapSpeed;
        this.jumpR = jumpRad;
    }

    protected void skillLeap() {
        if (this.leapMotionY == 0.0f) {
            return;
        }
        if (this.getTarget() != null && this.shouldWorkTask() && !this.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && this.getParasiteStatus() <= 2) {
            LivingEntity entitylivingbase = this.getTarget();
            if (this.attacking == 0) {
                ++this.attacking;
                this.targetX = entitylivingbase.getX();
                this.targetZ = entitylivingbase.getZ();
            }
        }
        if (this.attacking >= 1) {
            ++this.attacking;
            this.skillBreakBlocks();
            if (this.attacking == 2 && this.onGround()) {
                this.setParasiteStatus(10);
                this.getNavigation().stop();
                double d0 = this.targetX - this.getX();
                double d1 = this.targetZ - this.getZ();
                double f = (float)Math.sqrt((double)(d0 * d0 + d1 * d1));
                Mot.setY(this, this.leapMotionY);
                Mot.addX(this, d0 / f * this.jumpSpeed * 0.9 + this.getDeltaMovement().x * 0.3);
                Mot.addZ(this, d1 / f * this.jumpSpeed * 0.9 + this.getDeltaMovement().z * 0.3);
            }
            if (this.attacking > 2 && this.onGround()) {
                if (this.jumpR != 0) {
                    float damage = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                    AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).expandTowards((double)this.jumpR, 2.0, (double)this.jumpR);
                    List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                    for (LivingEntity mob : moblist) {
                        if (mob == this || mob instanceof EntityParasiteBase) continue;
                        mob.knockback(2.5f, this.getX() - mob.getX(), this.getZ() - mob.getZ());
                        this.doHurtTarget((Entity)mob);
                    }
                }
                this.setParasiteStatus(0);
                this.attacking = 0;
                if (this.type >= 31 && this.getBbHeight() > 2.0f) {
                    this.playSound(SRPSounds.HITGROUND.get(), 15.0f, 1.0f);
                }
                this.SkillLeapFlag = true;
            }
        }
    }

    @Override
    public EntityParasiteBase getParent() {
        return this;
    }

    private final java.util.List<net.neoforged.neoforge.entity.PartEntity<?>> partList = new java.util.ArrayList<>();
    private net.neoforged.neoforge.entity.PartEntity<?>[] partArray;

    /** Registers a hit box or body part; the order is the creation order (NeoForge numbers the parts after the parent id). */
    public void registerPart(net.neoforged.neoforge.entity.PartEntity<?> part) {
        this.partList.add(part);
        this.partArray = this.partList.toArray(new net.neoforged.neoforge.entity.PartEntity<?>[0]);
    }

    @Override
    public boolean isMultipartEntity() {
        return this.partArray != null && this.partArray.length > 0;
    }

    @Override
    public net.neoforged.neoforge.entity.PartEntity<?>[] getParts() {
        return this.partArray;
    }

    public void updateHitboxes() {
        for (EntityHitbox hb : this.hitboxes) {
            if (hb == null) continue;
            hb.tick();
        }
    }

    public void resetHitboxes(float scale) {
        if (scale > this.prevHitboxScale) {
            this.prevHitboxScale = scale;
            for (EntityHitbox hb : this.hitboxes) {
                if (hb == null) continue;
                hb.resize(scale);
            }
        }
    }

    private void clearHitboxes() {
        for (EntityHitbox hb : this.hitboxes) {
            if (hb == null) continue;
            hb.discard();
            hb.discard();
        }
    }

    public void setDead() {
        if (this.hitboxes != null) {
            this.clearHitboxes();
        }
        super.discard();
    }

    @Nonnull
    public Level getWorld() {
        return this.level();
    }

    public static class EntityAIJumping
    extends Goal {
        private final Mob parent;
        private int secs;

        public EntityAIJumping(Mob parentIn) {
            this.parent = parentIn;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP));
            if (parentIn.getNavigation() instanceof GroundPathNavigation) {
                ((GroundPathNavigation)parentIn.getNavigation()).setCanFloat(true);
            } else if (parentIn.getNavigation() instanceof FlyingPathNavigation) {
                ((FlyingPathNavigation)parentIn.getNavigation()).setCanFloat(true);
            }
        }

        public boolean canUse() {
            ++this.secs;
            if (this.secs < 10) {
                return false;
            }
            this.secs = 0;
            LivingEntity target = this.parent.getTarget();
            if (target == null) {
                return false;
            }
            if (target.distanceToSqr(this.parent.getX(), target.getY(), this.parent.getZ()) < 4.0 && target.getY() - (this.parent.getY() + (double)this.parent.getEyeHeight()) > 1.0 && this.parent.onGround()) {
                this.parent.getNavigation().stop();
                double dd0 = target.getX() - this.parent.getX();
                double dd1 = target.getZ() - this.parent.getZ();
                float f = (float)Math.sqrt((double)(dd0 * dd0 + dd1 * dd1));
                Mot.addX(this.parent, dd0 / (double)f * 0.5 * (double)0.8f + this.parent.getDeltaMovement().x * (double)0.2f);
                Mot.addZ(this.parent, dd1 / (double)f * 0.5 * (double)0.8f + this.parent.getDeltaMovement().z * (double)0.2f);
                Mot.setY(this.parent, 0.2 + (double)this.parent.getBbHeight() * 0.15);
            }
            return false;
        }

        public void tick() {
            if (this.parent.getRandom().nextFloat() < 0.8f) {
                this.parent.getJumpControl().jump();
            }
        }
    }

    class EntityAIWait
    extends Goal {
        public EntityAIWait() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        public boolean canUse() {
            return EntityParasiteBase.this.getWait() > 0;
        }

        public void tick() {
        }
    }
}

