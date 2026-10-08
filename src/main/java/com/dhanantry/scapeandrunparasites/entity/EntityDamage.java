package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class EntityDamage
extends Entity {
    private int lifeTicks = 10;
    private LivingEntity caster;
    private UUID casterUuid;
    private float mobDamage;
    private boolean pulling;
    private float str;
    private boolean follow;
    private Entity ffollow;

    public EntityDamage(EntityType<? extends EntityDamage> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityDamage(Level worldIn, double x, double y, double z, float rotation, LivingEntity casterIn, float damage, boolean pull, float strenght) {
        this(SRPEntities.DAMAGE.get(), worldIn);
        this.setCaster(casterIn);
        this.setYRot(rotation * 57.295776f);
        this.setPos(x, y, z);
        this.mobDamage = damage;
        this.pulling = pull;
        this.str = strenght;
    }

    public EntityDamage(Level worldIn, double x, double y, double z, float rotation, LivingEntity casterIn, float damage, boolean pull, float strenght, float w, float h) {
        this(worldIn, x, y, z, rotation, casterIn, damage, pull, strenght);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    public void setCaster(@Nullable LivingEntity in) {
        this.caster = in;
        this.casterUuid = in == null ? null : in.getUUID();
    }

    public void setFollower(Entity in) {
        this.follow = true;
        this.ffollow = in;
    }

    @Nullable
    public LivingEntity getCaster() {
        Entity entity;
        if (this.caster == null && this.casterUuid != null && this.level() instanceof ServerLevel && (entity = ((ServerLevel)this.level()).getEntity(this.casterUuid)) instanceof LivingEntity) {
            this.caster = (LivingEntity)entity;
        }
        return this.caster;
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        this.casterUuid = compound.getUUID("OwnerUUID");
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.casterUuid != null) {
            compound.putUUID("OwnerUUID", this.casterUuid);
        }
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.follow) {
                if (this.ffollow == null) {
                    if (--this.lifeTicks < 0) {
                        this.discard();
                    }
                } else {
                    AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(1.5);
                    List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                    for (LivingEntity entityIn : moblist) {
                        if (SRPEntityUtil.rayTraceBlocks(this.level(), new Vec3(this.getX(), this.getY() + (double)this.getEyeHeight(), this.getZ()), new Vec3(entityIn.getX(), entityIn.getY() + (double)entityIn.getEyeHeight(), entityIn.getZ())) != null) continue;
                        this.damage(entityIn);
                    }
                    int i1 = Mth.floor((double)(this.getY() + 0.1));
                    double l1 = this.getX();
                    double i2 = this.getZ();
                    boolean flag = false;
                    int offsetT = 0;
                    int BGrange = 2;
                    int BGheight = 2;
                    for (int k2 = -1 * BGrange; k2 <= BGrange; ++k2) {
                        for (int l2 = -1 * BGrange; l2 <= BGrange; ++l2) {
                            for (int j = 1 + offsetT; j <= BGheight + offsetT; ++j) {
                                double i3 = l1 + (double)k2;
                                double k = i1 + j;
                                double l = i2 + (double)l2;
                                BlockPos blockpos = BlockPos.containing(i3, k, l);
                                BlockState iblockstate = this.level().getBlockState(blockpos);
                                Block block = iblockstate.getBlock();
                                float bHard = iblockstate.getDestroySpeed(this.level(), blockpos);
                                if (!((double)bHard <= 0.3 && bHard >= 0.0f) && !(block instanceof IMetaName) || block == SRPBlocks.BiomeHeart.get() || block == SRPBlocks.ColonyHeart.get() || block == SRPBlocks.ParasiteRubbleDense.get() || block == SRPBlocks.ParasiteCanisterActive.get() || block == Blocks.AIR || !iblockstate.canEntityDestroy(this.level(), blockpos, this) || !EventHooks.onEntityDestroyBlock((LivingEntity)this.getCaster(), (BlockPos)blockpos, (BlockState)iblockstate)) continue;
                                flag = this.level().destroyBlock(blockpos, true) || flag;
                            }
                        }
                    }
                    Mot.setPosX(this, this.ffollow.getX());
                    Mot.setPosY(this, this.ffollow.getY());
                    Mot.setPosZ(this, this.ffollow.getZ());
                    if (!this.ffollow.isAlive()) {
                        this.ffollow = null;
                    }
                }
            } else {
                for (LivingEntity entitylivingbase : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().expandTowards(0.3, 0.0, 0.2))) {
                    this.damage(entitylivingbase);
                }
                if (--this.lifeTicks < 0) {
                    this.discard();
                }
            }
        }
    }

    private void damage(LivingEntity in) {
        if (in instanceof EntityParasiteBase) {
            return;
        }
        LivingEntity entitylivingbase = this.getCaster();
        if (in.isAlive() && !in.isInvulnerable() && in != entitylivingbase) {
            if (entitylivingbase == null) {
                in.hurt(this.damageSources().magic(), this.mobDamage);
            } else {
                if (entitylivingbase.isAlliedTo((Entity)in)) {
                    return;
                }
                if (this.pulling) {
                    if (in instanceof Player) {
                        this.knockBack2((Entity)in, this.str, in.getX() - this.caster.getX(), in.getZ() - this.caster.getZ());
                    } else {
                        this.knockBack2((Entity)in, this.str / 3.0f, in.getX() - this.caster.getX(), in.getZ() - this.caster.getZ());
                    }
                } else if (in instanceof Player) {
                    this.knockBack2((Entity)in, this.str, this.caster.getX() - in.getX(), this.caster.getZ() - in.getZ());
                } else {
                    this.knockBack2((Entity)in, this.str, this.caster.getX() - in.getX(), this.caster.getZ() - in.getZ());
                }
                entitylivingbase.doHurtTarget((Entity)in);
            }
        }
    }

    private void knockBack2(Entity entityIn, float strength, double xRatio, double zRatio) {
        float f = (float)Math.sqrt((double)(xRatio * xRatio + zRatio * zRatio));
        Mot.mulX(entityIn, 1.0 / (2.0));
        Mot.mulZ(entityIn, 1.0 / (2.0));
        Mot.addX(entityIn, -(xRatio / (double)f * (double)strength));
        Mot.addZ(entityIn, -(zRatio / (double)f * (double)strength));
        if (entityIn.onGround()) {
            Mot.mulY(entityIn, 1.0 / (2.0));
            Mot.addY(entityIn, (double)strength);
            if (entityIn.getDeltaMovement().y > (double)0.4f) {
                Mot.setY(entityIn, 0.4f);
            }
        }
    }
}

