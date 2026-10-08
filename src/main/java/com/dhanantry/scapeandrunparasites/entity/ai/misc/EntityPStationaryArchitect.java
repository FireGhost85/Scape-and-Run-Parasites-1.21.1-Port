package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.BlockInfestedStain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteFog;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.ticks.TickPriority;

public abstract class EntityPStationaryArchitect
extends EntityPStationary
implements EntityCanSummon {
    protected int totalP;
    protected int actualP;
    protected boolean convert = true;
    protected int[] mobID;
    protected int[] mobPT;
    protected byte stage;
    protected float body;
    public int neededTime;
    protected int actualTime;
    protected int topParticles;
    private boolean canGrowTo;
    private int bombC;

    public EntityPStationaryArchitect(EntityType<? extends EntityPStationaryArchitect> type, Level worldIn) {
        super(type, worldIn);
        this.killcount = -10.0;
        this.canD = SRPConfig.rsDespawn;
        this.canGrowTo = true;
        this.borderOrb = -1;
    }

    public void setCanGrowTo(boolean in) {
        this.canGrowTo = in;
    }

    public void setConvert(boolean in) {
        this.convert = in;
    }

    public int setGT(int minGrowTime, int maxGrowTime) {
        int atm = maxGrowTime - minGrowTime + 1;
        if (atm <= 0 || maxGrowTime <= minGrowTime) {
            return 1;
        }
        return this.getRandom().nextInt(atm) + minGrowTime;
    }

    public void setActualT(int in) {
        if (this.canGrowTo) {
            this.actualTime = in;
        }
    }

    public int getActualT() {
        return this.actualTime;
    }

    public int getNeededTime() {
        return this.neededTime;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.topParticles > 0) {
            --this.topParticles;
        }
        if (this.bombC > 0) {
            --this.bombC;
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || this.level().isClientSide) {
            return super.mobInteract(player, hand);
        }
        Item wea = player.getItemBySlot(EquipmentSlot.MAINHAND).getItem();
        if (wea == SRPItems.itemEvolve.get()) {
            this.canChangeVariant = true;
            this.setActualT(100000000);
            this.setActualT(100000000);
            return InteractionResult.SUCCESS;
        }
        if (wea == SRPItems.itembase.get()) {
            this.generateStructure();
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!this.level().isClientSide) {
            int meta;
            BlockPos floor;
            Block bbb;
            int i1 = this.blockPosition().getY();
            double l1 = this.blockPosition().getX();
            double i2 = this.blockPosition().getZ();
            int BGrange = 2;
            for (int k2 = -1 * BGrange; k2 <= 1 * BGrange; ++k2) {
                for (int l2 = -1 * BGrange; l2 <= 1 * BGrange; ++l2) {
                    for (int j = -1 * BGrange; j <= 1 * BGrange; ++j) {
                        double i3 = l1 + (double)k2;
                        double k = i1 + j;
                        double l = i2 + (double)l2;
                        BlockPos blockpos = BlockPos.containing(i3, k, l);
                        BlockState iblockstate = this.level().getBlockState(blockpos);
                        Block block = iblockstate.getBlock();
                        if (block != SRPBlocks.ParasiteFog.get()) continue;
                        this.level().setBlockAndUpdate(blockpos, SRPBlocks.ParasiteFog.get().defaultBlockState().setValue((Property)BlockParasiteFog.STAGE, Integer.valueOf(2)));
                    }
                }
            }
            if (SRPConfigWorld.originActivated) {
                return;
            }
            if ((this.convert || ParasiteEventEntity.getRSchance(this.level()) == 0.0 || !SRPConfigSystems.useEvolution && (double)SRPConfigSystems.rschance == 0.0) && ((bbb = this.level().getBlockState(floor = this.blockPosition().below()).getBlock()) == SRPBlocks.InfestedStain.get() || bbb == SRPBlocks.InfestedRubble.get()) && ((meta = this.level().getBlockState(floor).getValue(BlockInfestedStain.STAGE)) <= SRPConfigSystems.rsBlockRevertStage && meta <= this.stage || ParasiteEventEntity.getRSchance(this.level()) == 0.0)) {
                this.level().setBlockAndUpdate(floor, SRPBlocks.InfestedStain.get().defaultBlockState().setValue((Property)BlockInfestedStain.STAGE, Integer.valueOf(5)));
                this.level().scheduleTick(floor, SRPBlocks.InfestedStain.get(), 40, TickPriority.byValue(5));
            }
        }
    }

    public float getBODY() {
        return this.body;
    }

    public byte getStageV() {
        return this.stage;
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
        for (int j : this.mobID) {
            if (j != -777) continue;
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

    public abstract float getBombDamage();

    public abstract void generateStructure();

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        if (this.level().getBiome(this.blockPosition()).value() instanceof BiomeParasiteBase) {
            return true;
        }
        return super.removeWhenFarAway(0.0);
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        if (source.is(DamageTypes.MAGIC) && this.hasEffect(MobEffects.POISON) && amount == 1.0f) {
            this.heal(1.0f * this.genePoisonHealing);
            return false;
        }
        float red = this.getParasiteStatus() == 0 ? 0.4f : 1.0f;
        boolean flag = super.hurt(source, amount * red);
        if (flag && this.bombC == 0 && !source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            if (source.getEntity() instanceof LivingEntity) {
                if (source.getEntity().distanceToSqr((Entity)this) > 36.0) {
                    return flag;
                }
            } else {
                return flag;
            }
            for (int i = 0; i < this.stage * 2; ++i) {
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
                d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.65f));
                d4 = d4 * d7 * (double)this.stage * 3.0;
                d5 *= d7;
                EntityBomb bomb = new EntityBomb(SRPEntities.BOMB.get(), this.level(), this, false);
                bomb.setFuse(60);
                bomb.setStren(0.0f);
                bomb.setSkin(1);
                bomb.setDamage(this.getBombDamage(), this.stage);
                bomb.setXRot(bomb.getXRot() - (-20.0f));
                bomb.copyPosition((Entity)this);
                bomb.setMotion(d3, d4, d5, 0.35, (float)this.stage / 10.0f * 2.0f);
                this.level().addFreshEntity((Entity)bomb);
                bomb.updateSTR();
            }
            this.bombC = 80;
        }
        return flag;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("tickse", this.actualTime);
        compound.putInt("neededtime", this.neededTime);
        compound.putBoolean("cangrowto", this.canGrowTo);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("tickse", 99)) {
            this.actualTime = compound.getInt("tickse");
        }
        if (compound.contains("neededtime", 99)) {
            this.neededTime = compound.getInt("neededtime");
        }
        if (compound.contains("cangrowto", 99)) {
            this.canGrowTo = compound.getBoolean("cangrowto");
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 12) {
            this.topParticles = 20;
        } else {
            super.handleEntityEvent(id);
        }
    }

    public void spawnParticlesTop(SRPEnumParticle particleType, int r, int g, int b) {
        double d0 = this.getRandom().nextGaussian() * 0.02;
        double d1 = this.getRandom().nextGaussian() * 0.02;
        double d2 = this.getRandom().nextGaussian() * 0.02;
        ParticleSpawner.spawnParticle(particleType, this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY() + (double)this.getBbHeight() + (double)this.getRandom().nextFloat() * ((double)(this.getBbHeight() - this.getEyeHeight()) + 0.5), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), d0, d1, d2, r, g, b);
    }
}

