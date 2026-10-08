package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIInfectedSearch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSpawn;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.network.ParticlePayload;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class EntityPInfected
extends EntityParasiteBase
implements EntityCanSpawn {
    private String host;
    private int check;
    private int timer;
    protected boolean thisMelting;

    public EntityPInfected(EntityType<? extends EntityPInfected> type, Level worldIn) {
        super(type, worldIn);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, true, false, null, SRPConfig.infectedSneakPen, SRPConfig.infectedInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, true, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.infectedSneakPen, SRPConfig.infectedInviPen));
        }
        this.xpReward = SRPAttributes.XP_INFECTED;
        this.damageCap = SRPConfig.infectedCap;
        this.canD = SRPConfig.infecteddespawn;
        this.MiniDamage = SRPConfig.infectedMinDamage;
        this.oneMindDeathValue = SRPConfig.infectedOneMindDeathV;
        this.foodSteal = 0.1f;
        this.cothSpread = SRPConfigSystems.cothInfected;
        this.check = 0;
        this.timer = 0;
        this.host = "";
        this.valueEvDeath = SRPConfig.infectedLoosingEPValue;
        this.thisMelting = false;
        this.setScentHPMultiplier(2.5f);
    }

    @Override
    public int getIDSpawn() {
        return this.getParasiteIDRegister();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.srpTicks == 5 && !this.level().isClientSide) {
            if (ParasiteEventEntity.canSpawnNext) {
                EntityPFeral out;
                if (this.killcount > SRPConfig.primitiveKills && this.getLevelCreated() >= SRPConfigSystems.deveMergeUse && this.thisMelting) {
                    this.goalSelector.addGoal(3, new EntityAIInfectedSearch(this, 1.1));
                    this.killcount = 0.0;
                }
                if (this.killcount > SRPConfig.feralKills && (out = this.getFeral(this.level())) != null) {
                    ParasiteEventEntity.spawnNext(this, out, true, false);
                    return;
                }
            }
            if (this.host.length() != 0) {
                if (this.check == 0) {
                    LivingEntity entityout = (LivingEntity)SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(this.host), (Level)this.level());
                    if (entityout != null && !(entityout instanceof Monster) && SRPConfig.infectedAssimilation) {
                        this.check = 2;
                        this.level().broadcastEntityEvent((Entity)this, (byte)14);
                    } else {
                        this.check = 1;
                        this.level().broadcastEntityEvent((Entity)this, (byte)15);
                    }
                }
                ++this.timer;
                if (this.check >= 2 && this.timer > 5) {
                    if (SRPConfigSystems.useEvolution) {
                        if (this.phaseCreated < SRPConfigSystems.evolutionAssimilatedDehiding) {
                            this.transform();
                        }
                    } else {
                        this.transform();
                    }
                }
            }
            this.InfectNearby((LivingEntity)this, SRPConfigSystems.cothAura);
        }
        if (this.check >= 3 && this.level().isClientSide) {
            this.spawnParticles(SRPEnumParticle.GSPLASH, 0, 0, 0);
        }
    }

    @Override
    public void setAttackTarget(LivingEntity entitylivingbaseIn) {
        super.setTarget(entitylivingbaseIn);
        this.check = 0;
        this.level().broadcastEntityEvent((Entity)this, (byte)17);
    }

    @Override
    protected void fearPlayer(LivingEntity player) {
    }

    protected void fearPlayer(LivingEntity player, float damageDealt) {
        if (player == null || player.level().isClientSide) {
            return;
        }
        if (damageDealt <= 8.0f) {
            return;
        }
        int level = 1 + Math.max(0, (int)Math.floor((damageDealt - 8.0f) / 4.0f));
        int cap = 3;
        level = Math.min(level, cap);
        int duration = 300 + 40 * (level - 1);
        duration = Mth.clamp((int)duration, (int)200, (int)500);
        int amplifier = level - 1;
        player.addEffect(new MobEffectInstance(SRPPotions.FEAR_E, duration, amplifier, false, true));
    }

    private void transform() {
        if (this.getTarget() != null || this.hasEffect(SRPPotions.EPEL_E)) {
            this.timer = -1;
            return;
        }
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).expandTowards(10.0, 5.0, 10.0);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        boolean flag = true;
        for (LivingEntity mob : moblist) {
            CompoundTag tags;
            Player player;
            if (mob == this || mob instanceof EntityParasiteBase || mob instanceof Player && (player = (Player)mob).isCreative() || (tags = mob.getPersistentData()).contains("srpcothimmunity") && tags.getInt("srpcothimmunity") != 0) continue;
            flag = false;
            this.check = 2;
            this.timer = 0;
            this.level().broadcastEntityEvent((Entity)this, (byte)14);
            break;
        }
        if (flag) {
            ++this.check;
            this.level().broadcastEntityEvent((Entity)this, (byte)16);
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3, false, false));
            PacketDistributor.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), this.getBbWidth(), this.getBbHeight(), 1));
            if (this.timer < 8) {
                return;
            }
            LivingEntity entityout = (LivingEntity)SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(this.host), (Level)this.level());
            entityout.copyPosition((Entity)this);
            this.particleStatus((byte)7);
            this.discard();
            if (this.hasCustomName()) {
                SRPEntityUtil.setCustomNameTag(entityout, SRPEntityUtil.getCustomNameTag(this));
                entityout.setCustomNameVisible(this.isCustomNameVisible());
            }
            entityout.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 2, false, false));
            this.level().addFreshEntity((Entity)entityout);
            this.level().levelEvent((Player)null, 1026, entityout.blockPosition(), 0);
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            float dealt;
            LivingEntity target = (LivingEntity)entityIn;
            float before = target.getHealth();
            boolean hit = super.doHurtTarget(entityIn);
            if (hit && !this.level().isClientSide && (dealt = Math.max(0.0f, before - target.getHealth())) > 0.0f) {
                this.fearPlayer(target, dealt);
            }
            return hit;
        }
        return super.doHurtTarget(entityIn);
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        this.timer = -200;
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
                this.level().setBlockAndUpdate(blockpos, SRPBlocks.goreSim.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.FLAT)));
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
            bomb.setType((byte)1);
            bomb.copyPosition((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)), d4, d5 *= d7, 0.2, 0.8);
            this.level().addFreshEntity((Entity)bomb);
        }
    }

    @Override
    protected void spawnGore() {
        this.attackEntityFromEffects(2, 100);
        if (this.level().getBlockState(this.blockPosition().below()).isCollisionShapeFullBlock(this.level(), this.blockPosition().below()) && (this.level().getBlockState(this.blockPosition()).getBlock() instanceof BushBlock || this.level().getBlockState(this.blockPosition()).getBlock() == Blocks.AIR)) {
            this.level().setBlockAndUpdate(this.blockPosition(), SRPBlocks.goreSim.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(SRPEntities.REMAIN.get(), this.level());
            nnn.moveTo((double)this.blockPosition().getX() + 0.5, this.blockPosition().getY(), (double)this.blockPosition().getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.infectedRemainValue);
            this.level().addFreshEntity((Entity)nnn);
        }
        this.attackEntityFromCap(3);
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
        if (!this.level().isClientSide && this.killcount > SRPConfig.primitiveKills && this.getLevelCreated() >= SRPConfigSystems.deveMergeUse && this.thisMelting) {
            this.goalSelector.addGoal(3, new EntityAIInfectedSearch(this, 1.1));
            this.killcount = 0.0;
        }
    }

    private void InfectNearby(LivingEntity entity, int range) {
        if (SRPConfigSystems.cothAura == 0) {
            return;
        }
        AABB axisalignedbb = new AABB(entity.getX(), entity.getY(), entity.getZ(), entity.getX() + 1.0, entity.getY() + 1.0, entity.getZ() + 1.0).inflate((double)range);
        List<? extends LivingEntity> moblist = entity.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            if (mob == entity || mob.hasEffect(SRPPotions.COTH_E) || mob.hasEffect(SRPPotions.EPEL_E)) continue;
            mob.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 4800, 0, false, false));
        }
    }

    public void setHost(String mobname) {
        this.host = mobname;
    }

    public String getHost() {
        return this.host;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        Item wea = player.getItemBySlot(EquipmentSlot.MAINHAND).getItem();
        if (wea == SRPItems.itemAssimilate.get() && this.host.length() != 0 && hand == InteractionHand.MAIN_HAND) {
            this.level().broadcastEntityEvent((Entity)this, (byte)16);
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3, false, false));
            PacketDistributor.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), this.getBbWidth(), this.getBbHeight(), 1));
            LivingEntity entityout = (LivingEntity)SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(this.host), (Level)this.level());
            entityout.copyPosition((Entity)this);
            this.particleStatus((byte)7);
            this.discard();
            if (this.hasCustomName()) {
                SRPEntityUtil.setCustomNameTag(entityout, SRPEntityUtil.getCustomNameTag(this));
                entityout.setCustomNameVisible(this.isCustomNameVisible());
            }
            entityout.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 2, false, false));
            this.level().addFreshEntity((Entity)entityout);
            this.level().levelEvent((Player)null, 1026, entityout.blockPosition(), 0);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public EntityPFeral getFeral(Level in) {
        return null;
    }

    @Override
    public int canSpawnByIDData() {
        return 0;
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        if (!SRPConfigSystems.disloDeathHighVerions) {
            return false;
        }
        int goo = SRPSaveData.get(this.level()).getCurrentCode(DimKeys.of(this.level()), 10);
        if (goo != 0 && this.level().random.nextDouble() < SRPConfigSystems.disloDeathHighVerionsChance) {
            EntityParasiteBase halo = ParasiteEventEntity.getRandomPrimitive(this.level());
            if (goo >= SRPConfigSystems.disloDeathHighVerionsValue1) {
                halo = ParasiteEventEntity.getRandomAdapted(this.level());
            }
            if (goo >= SRPConfigSystems.disloDeathHighVerionsValue2) {
                halo = ParasiteEventEntity.getRandomPure(this.level());
            }
            halo.copyPosition((Entity)this);
            halo.finalizeSpawn((ServerLevel) halo.level(), this.level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
            this.level().addFreshEntity((Entity)halo);
            this.level().levelEvent(null, 1026, halo.blockPosition(), 0);
            halo.particleStatus((byte)7);
            halo.addEffect(new MobEffectInstance(SRPPotions.EPEL_E, 600, 0, false, false));
        }
        return super.onDeathDislo(cause);
    }

    @Override
    public void spawnEffectsGore() {
        int i;
        for (i = 0; i <= 100; ++i) {
            if (i % 5 != 0) continue;
            this.spawnParticlesGore(SRPEnumParticle.GSPLASH, 0, -1, -1);
        }
        for (i = 0; i <= 20; ++i) {
            if (i % 5 == 0) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
            }
            if (i % 5 != 0) continue;
            this.spawnParticles(SRPEnumParticle.GSPLASH, 0, -1, -1);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("parasitehost", this.host);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("parasitehost", 8)) {
            this.setHost(compound.getString("parasitehost").toString());
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 14) {
            this.check = 2;
        } else if (id == 15) {
            this.check = 1;
        } else if (id == 16) {
            ++this.check;
        } else if (id == 17) {
            this.check = 0;
        } else {
            super.handleEntityEvent(id);
        }
    }
}

