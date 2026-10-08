package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityDodT;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class EntityPPure
extends EntityPMalleable
implements EntityCanSummon {
    protected int totalP;
    protected int actualP;
    protected int[] mobID;
    protected int[] mobPT;
    private int ggg;

    public EntityPPure(EntityType<? extends EntityPPure> type, Level worldIn) {
        super(type, worldIn);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, SRPConfig.pureWalls, false, null, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        if (SRPConfig.canOrbAttack) {
            this.goalSelector.addGoal(2, new EntityAISkill(this, 60, 5, false, 21));
        }
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, SRPConfig.pureWalls, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        }
        this.goalSelector.removeGoal(this.folow);
        this.xpReward = SRPAttributes.XP_ADAPTED;
        this.canD = SRPConfig.puredespawn;
        this.damageCap = SRPConfig.pureCap;
        this.canModRender = 1;
        this.fuseTime = 70;
        this.type = (byte)51;
        this.killcount = 10.0;
        this.fuseOrb = 23;
        this.orbStartTimer = 5;
        this.foodSteal = SRPConfig.pureFoodSteal;
        this.orbItemCool = SRPConfig.pureItemOrbCooldown * 20;
        this.pointCap = SRPConfig.purePointCap;
        this.pointReduction = SRPConfig.purePointRed;
        this.chanceLearn = SRPConfig.pureChanceLe;
        this.chanceLearnFire = SRPConfig.pureChanceLeFire;
        this.DamageTypeCap = SRPConfig.purePointDamCap;
        this.MiniDamage = SRPConfig.pureMinDamage;
        this.regen = SRPConfig.pureRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.pureOneMindDeathV;
        this.regenEff = 15;
        this.foodRott = SRPConfig.pureFoodChance;
        this.foodRootNumber = SRPConfig.pureFoodAmount;
        this.cothSpread = SRPConfigSystems.cothPure;
        this.totalP = SRPConfig.pureSeiZ;
        this.mobID = new int[this.totalP];
        this.mobPT = new int[this.totalP];
        for (int i = 0; i < this.mobID.length; ++i) {
            this.mobID[i] = -777;
        }
        this.valueEvDeath = SRPConfig.pureLoosingEPValue;
        this.setScentHPMultiplier(1.0f);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        ++this.ggg;
        if (this.ggg > 60) {
            this.ggg = 0;
        }
        if (this.getTarget() != null) {
            this.summonTentacles(this.getTarget());
        }
    }

    @Override
    protected void fearPlayer(LivingEntity player) {
        try {
            if (player == null) {
                return;
            }
            if (!this.hasLineOfSight((Entity)player)) {
                return;
            }
            if (!player.hasEffect(SRPPotions.FEAR_E)) {
                player.addEffect(new MobEffectInstance(SRPPotions.FEAR_E, 300, 2, false, false));
            } else if (player.getEffect(SRPPotions.FEAR_E).getAmplifier() < 2) {
                player.addEffect(new MobEffectInstance(SRPPotions.FEAR_E, 300, 2, false, false));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        LivingEntity attacker;
        if (this.level().isClientSide) {
            return super.hurt(source, amount);
        }
        if (source.getEntity() instanceof LivingEntity && (attacker = (LivingEntity)source.getEntity()).hasEffect(SRPPotions.KILLPUR_E)) {
            int amp = attacker.getEffect(SRPPotions.KILLPUR_E).getAmplifier();
            float totalRed = Mth.clamp((float)(SRPConfigSystems.parasiteKillingReduction * ((float)amp + 1.0f)), (float)0.0f, (float)0.95f);
            float reduced = amount * (1.0f - totalRed);
            boolean flag = super.hurt(source, Math.max(0.0f, reduced));
            if (flag) {
                this.summonTentacles((LivingEntity)source.getEntity());
            }
            return flag;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void attackEntityFromEffects(int range, int count) {
        this.particleStatus((byte)51);
        double i1 = Mth.floor((double)(this.getY() + 0.1));
        double l1 = this.getX();
        double i2 = this.getZ();
        int counttt = 0;
        for (int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
            for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                double i3 = l1 + (double)k2;
                double l = i2 + (double)l2;
                BlockPos blockpos = BlockPos.containing(i3, i1, l);
                Block block = this.level().getBlockState(blockpos).getBlock();
                Block blockDown = this.level().getBlockState(blockpos.below()).getBlock();
                if (block != Blocks.AIR || blockDown == Blocks.AIR || !this.level().getBlockState(blockpos.below()).isCollisionShapeFullBlock(this.level(), blockpos.below()) || blockDown == SRPBlocks.InfestedStain.get() || this.level().random.nextInt(4) != 0) continue;
                this.level().setBlockAndUpdate(blockpos, SRPBlocks.gorePur.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.FLAT)));
                if (++counttt < count) continue;
                return;
            }
        }
    }

    @Override
    protected void attackEntityFromCap(int go) {
        for (int i = 0; i < go && SRPConfig.paraGore; ++i) {
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
            double d7 = 0.8 / (d6 / 4.0 + 0.1);
            d4 = d4 * d7 * 2.0;
            EntityGore bomb = new EntityGore(SRPEntities.GORE.get(), this.level());
            bomb.setType((byte)4);
            bomb.copyPosition((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)), d4, d5 *= d7, 0.2, 0.8);
            this.level().addFreshEntity((Entity)bomb);
        }
    }

    @Override
    protected void spawnGore() {
        this.attackEntityFromEffects(3, 100);
        if (this.level().getBlockState(this.blockPosition().below()).isCollisionShapeFullBlock(this.level(), this.blockPosition().below()) && (this.level().getBlockState(this.blockPosition()).getBlock() instanceof BushBlock || this.level().getBlockState(this.blockPosition()).getBlock() == Blocks.AIR)) {
            this.level().setBlockAndUpdate(this.blockPosition(), SRPBlocks.gorePur.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(SRPEntities.REMAIN.get(), this.level());
            nnn.moveTo((double)this.blockPosition().getX() + 0.5, this.blockPosition().getY(), (double)this.blockPosition().getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.pureRemainValue);
            this.level().addFreshEntity((Entity)nnn);
        }
        this.attackEntityFromCap(5);
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        if (!SRPConfigSystems.disloSameVersionDyeing || !this.disloNumberTwentytwo) {
            return super.onDeathDislo(cause);
        }
        boolean flag = super.onDeathDislo(cause);
        if (!flag && cause.getEntity() instanceof LivingEntity) {
            int count = SRPSaveData.get(this.level()).getCurrentCode(DimKeys.of(this.level()), 22);
            LivingEntity target = (LivingEntity)cause.getEntity();
            if (count >= 1) {
                target.removeEffect(SRPPotions.KILLPRI_E);
                target.removeEffect(SRPPotions.KILLADA_E);
                target.removeEffect(SRPPotions.KILLFER_E);
                target.removeEffect(SRPPotions.KILLCRU_E);
                target.removeEffect(SRPPotions.KILLNEX_E);
                SRPPotions.applyStackPotion(SRPPotions.KILLPUR_E, target, 1200, count);
            }
        }
        return flag;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance >= 80.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }

    protected void summonTentacles(LivingEntity in) {
        boolean flag;
        if (this.ggg != 20) {
            return;
        }
        if (SRPConfigSystems.useEvolution && ParasiteEventEntity.getRSchance(this.level()) == 0.0) {
            return;
        }
        double dis = this.distanceToSqr((Entity)in);
        boolean bl = flag = !this.hasLineOfSight((Entity)in);
        if (dis > 64.0 && flag && this.getRandom().nextInt(3) == 0 || this.getRandom().nextInt(10) == 0) {
            this.checkID();
            if (this.getActualParasites() < this.getTotalParasites() && this.spawnT(in, 5, 3, 2, 2)) {
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 150, 120, false, false));
            }
        } else {
            if (in.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && in.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() == 2) {
                return;
            }
            this.checkID();
            if (this.getActualParasites() >= this.getTotalParasites() || this.spawnT(in, 3, 1, 16, 1)) {
                // empty if block
            }
        }
    }

    protected boolean spawnT(LivingEntity in, int range2, int mini2, int check, int type) {
        int range = range2;
        int mini = mini2;
        RandomSource rand = RandomSource.create();
        double x = in.getX();
        double y = in.getY();
        double z = in.getZ();
        double randomx = rand.nextInt(range) + mini;
        double randomz = rand.nextInt(range) + mini;
        double negative = rand.nextInt(2);
        if (negative == 0.0) {
            randomx *= -1.0;
        }
        if ((negative = (double)rand.nextInt(2)) == 0.0) {
            randomz *= -1.0;
        }
        int limit = 0;
        boolean flag = true;
        EntityPStationary entityout = new EntityDodT(SRPEntities.DISPATCHERTEN.get(), this.level());
        ((EntityDodT)entityout).setTele(this);
        if (type == 1) {
            List serverList = SRPEntityUtil.allEntities(this.level());
            int count = 0;
            for (int k = 0; k < serverList.size(); ++k) {
                if (!(serverList.get(k) instanceof EntityNak)) continue;
                ++count;
            }
            if (count >= 7) {
                entityout.discard();
                return false;
            }
            entityout = new EntityNak(SRPEntities.SEIZER.get(), this.level());
        }
        while (flag) {
            if (limit >= 5) {
                entityout.discard();
                return false;
            }
            BlockPos pos = BlockPos.containing(x + randomx, y, z + randomz);
            if ((pos = ParasiteEventEntity.getFloor(this.level(), pos, 5)) != null) {
                BlockState state = this.level().getBlockState(pos);
                if (this.level().getBlockState(pos.below()).isCollisionShapeFullBlock(this.level(), pos.below())) {
                    AABB axisalignedbb = new AABB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)).inflate((double)check);
                    List moblist = this.level().getEntitiesOfClass(EntityNak.class, axisalignedbb);
                    if (moblist.size() > 10) {
                        return false;
                    }
                    axisalignedbb = new AABB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)).inflate(2.0);
                    moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                    if (moblist.isEmpty()) {
                        entityout.moveTo(x + randomx, y, z + randomz, this.getYRot(), this.getXRot());
                        if (!this.level().noCollision(entityout, entityout.getBoundingBox())) {
                            randomx = rand.nextInt(range) + mini;
                            randomz = rand.nextInt(range) + mini;
                            negative = rand.nextInt(2);
                            if (negative == 0.0) {
                                randomx *= -1.0;
                            }
                            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                                randomz *= -1.0;
                            }
                            ++limit;
                            continue;
                        }
                        entityout.finalizeSpawn((ServerLevel) entityout.level(), this.level().getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                        entityout.setTarget(in);
                        this.setActualParasites(1);
                        this.addID(entityout.getId(), 1);
                        entityout.setBuried();
                        this.level().addFreshEntity((Entity)entityout);
                        this.level().broadcastEntityEvent((Entity)entityout, (byte)50);
                        flag = false;
                        return true;
                    }
                }
            }
            randomx = rand.nextInt(range) + mini;
            randomz = rand.nextInt(range) + mini;
            negative = rand.nextInt(2);
            if (negative == 0.0) {
                randomx *= -1.0;
            }
            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                randomz *= -1.0;
            }
            ++limit;
        }
        return false;
    }

    private EntityDodT getTentacle() {
        for (int i = 0; i < this.mobID.length; ++i) {
            if (this.mobID[i] <= 0 || !(this.level().getEntity(this.mobID[i]) instanceof EntityDodT)) continue;
            EntityDodT flag = (EntityDodT)this.level().getEntity(this.mobID[i]);
            if (flag == null) {
                this.mobID[i] = -777;
                int negative = this.mobPT[i] * -1;
                this.setActualParasites(negative);
                continue;
            }
            if (!flag.isAlive() || flag.getTele() != null || flag.buried()) continue;
            return flag;
        }
        return null;
    }

    @Override
    public int getTotalParasites() {
        return this.totalP;
    }

    @Override
    public int getActualParasites() {
        return this.actualP;
    }

    @Override
    public void setActualParasites(int i) {
        this.actualP += i;
    }

    @Override
    public void addID(int id, int points) {
        for (int i = 0; i < this.mobID.length; ++i) {
            if (this.mobID[i] != -777) continue;
            this.mobID[i] = id;
            this.mobPT[i] = points;
            return;
        }
    }

    @Override
    public int IDable() {
        int flag = 0;
        for (int i = 0; i < this.mobID.length; ++i) {
            if (this.mobID[i] != -777) continue;
            ++flag;
        }
        if (flag > this.totalP) {
            flag = this.totalP;
        }
        return flag;
    }

    @Override
    public void checkID() {
        for (int i = 0; i < this.mobID.length; ++i) {
            Entity flag;
            if (this.mobID[i] <= 0 || (flag = this.level().getEntity(this.mobID[i])) != null) continue;
            this.mobID[i] = -777;
            int negative = this.mobPT[i] * -1;
            this.setActualParasites(negative);
        }
    }

    @Override
    public int[] getIDList() {
        return this.mobID;
    }

    @Override
    public int[] getPointList() {
        return this.mobPT;
    }

    @Override
    public void spawnEffectsGore() {
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag && in instanceof Player) {
            Player playerIn = (Player)in;
            if (playerIn.experienceLevel > 0) {
                playerIn.giveExperiencePoints(-SRPConfig.pureExpSteal);
                if (playerIn.experienceProgress < 0.0f) {
                    --playerIn.experienceLevel;
                    playerIn.experienceProgress = 1.0f;
                }
            } else {
                playerIn.giveExperiencePoints(-SRPConfig.pureExpSteal);
                if (playerIn.experienceProgress < 0.0f) {
                    playerIn.experienceProgress = 0.0f;
                }
            }
        }
        return flag;
    }
}

