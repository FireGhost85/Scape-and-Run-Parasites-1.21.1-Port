package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerSheep;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class EntityInfSheep
extends EntityPInfected
implements EntityCanMelt {
    private static final EntityDataAccessor<Float> HEIGH = SynchedEntityData.defineId(EntityInfSheep.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> MELTING = SynchedEntityData.defineId(EntityInfSheep.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TEX_VARIANT = SynchedEntityData.defineId(EntityInfSheep.class, EntityDataSerializers.INT);
    private float aSize;
    private int sound;

    public EntityInfSheep(EntityType<? extends EntityInfSheep> type, Level worldIn) {
        super(type, worldIn);
        this.aSize = 1.0f;
        this.canModRender = 1;
        this.type = (byte)12;
        this.fuseTime = 40;
        this.thisMelting = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 14;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infsheepCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 1, 16));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPInfected.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.INFSHEEP_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.INFSHEEP_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, (double)0.23f);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.INFSHEEP_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.INFSHEEP_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.infectedFollow);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HEIGH, (float) (Float.valueOf(0.0f)));
        builder.define(MELTING, false);
        builder.define(TEX_VARIANT, 0);
    }

    public int getTextureVariant() {
        return (Integer)this.entityData.get(TEX_VARIANT);
    }

    public void setTextureVariant(int v) {
        if (v < 0) {
            v = 0;
        }
        if (v > 2) {
            v = 2;
        }
        this.entityData.set(TEX_VARIANT, v);
    }

    private void rollTextureVariantWeighted() {
        float r = this.getRandom().nextFloat();
        if (r < 0.1f) {
            this.setTextureVariant(2);
        } else if (r < 0.4f) {
            this.setTextureVariant(1);
        } else {
            this.setTextureVariant(0);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.melting();
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        livingdata = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (!this.level().isClientSide) {
            this.rollTextureVariantWeighted();
        }
        return livingdata;
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (this.getTHeigh() < 1.57f && !this.level().isClientSide) {
            this.setTHeigh(0.17f);
        }
        if (this.deathTime == 20 && !this.level().isClientSide && this.getRandom().nextDouble() <= SRPAttributes.INFSHEEP_HEADCHANCE) {
            ParasiteSummon.spawnM(this, new String[]{"srparasites:sim_sheephead;1;1"}, 0, false, SRPEntityUtil.getCustomNameTag(this));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TextureVariant", this.getTextureVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("TextureVariant", 3)) {
            this.setTextureVariant(compound.getInt("TextureVariant"));
        }
    }

    @Override
    public void melt() {
        this.setWait(1000);
        this.entityData.set(HEIGH, (float) (Float.valueOf(1.3f)));
        this.entityData.set(MELTING, true);
    }

    @Override
    public void melting() {
        if (this.isMelting()) {
            if (this.sound % 20 == 0) {
                this.playSound(SRPSounds.INFECTED_MELT.get(), 1.0f, 1.0f);
            }
            ++this.sound;
            if ((double)this.getTHeigh() > 0.7) {
                this.setaSize(-0.005f);
                this.setTHeigh(-0.01f);
                this.setSize(this.getBbWidth(), this.getTHeigh());
            }
            if (!this.level().isClientSide) {
                if ((double)this.getTHeigh() <= 0.7 || this.sound >= 63) {
                    EntityLesh out = new EntityLesh(SRPEntities.MOVINGFLESH.get(), this.level());
                    out.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                    if (SRPEntityUtil.getCustomNameTag(this) != null) {
                        SRPEntityUtil.setCustomNameTag(out, SRPEntityUtil.getCustomNameTag(this));
                    }
                    this.discard();
                    this.level().addFreshEntity((Entity)out);
                    out.setLegs(SRPAttributes.INFSHEEP_V, false);
                }
            } else {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 106, 0);
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
            }
        }
    }

    @Override
    public boolean isMelting() {
        return (Boolean)this.entityData.get(MELTING);
    }

    @Override
    public float getTHeigh() {
        return ((Float)this.entityData.get(HEIGH)).floatValue();
    }

    @Override
    public void setTHeigh(float in) {
        this.entityData.set(HEIGH, (float) (Float.valueOf(in += this.getTHeigh())));
    }

    @Override
    public float getaSize() {
        return this.aSize;
    }

    @Override
    public void setaSize(float in) {
        this.aSize += in;
    }

    @Override
    public float getSelfeFlashIntensity2() {
        return this.aSize;
    }

    @Override
    protected void selfExplode() {
        super.selfExplode();
        ParasiteSummon.spawnM(this, new String[]{SRPConfigMobs.infsheepmob}, 0, false, SRPEntityUtil.getCustomNameTag(this));
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.95f * this.getBbHeight();
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INFECTEDSHEEP_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDSHEEP_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDSHEEP_DEATH.get();
    }

    @Override
    public EntityPFeral getFeral(Level in) {
        return new EntityFerSheep(SRPEntities.FER_SHEEP.get(), in);
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SHEEP_STEP, 0.15f, 1.0f);
    }

    private void cycleTextureVariant() {
        int v = this.getTextureVariant();
        v = (v + 1) % 3;
        this.setTextureVariant(v);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        int before;
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult base = super.mobInteract(player, hand);
        if (base.consumesAction()) {
            return base;
        }
        if (stack.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!(stack.getItem() instanceof DyeItem dye)) {
            return InteractionResult.PASS;
        }
        int desired = this.variantFromDyeMeta(dye.getDyeColor().getId());
        if (desired < 0) {
            return InteractionResult.PASS;
        }
        if (!this.level().isClientSide && (before = this.getTextureVariant()) != desired) {
            this.setTextureVariant(desired);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private int variantFromDyeMeta(int meta) {
        if (meta == DyeColor.WHITE.getId()) {
            return 0;
        }
        if (meta == DyeColor.GRAY.getId()) {
            return 1;
        }
        if (meta == DyeColor.LIGHT_GRAY.getId()) {
            return 1;
        }
        if (meta == DyeColor.BLACK.getId()) {
            return 2;
        }
        return -1;
    }

    private net.minecraft.world.entity.EntityDimensions srpSize;

    /** The 1.12 setSize(width, height): the entity dimensions are replaced and the bounding box refreshed. */
    protected void setSize(float width, float height) {
        this.srpSize = net.minecraft.world.entity.EntityDimensions.scalable(width, height);
        this.refreshDimensions();
    }

    @Override
    protected net.minecraft.world.entity.EntityDimensions getDefaultDimensions(net.minecraft.world.entity.Pose pose) {
        return this.srpSize != null ? this.srpSize : super.getDefaultDimensions(pose);
    }
}
