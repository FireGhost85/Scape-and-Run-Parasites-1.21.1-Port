package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.entity.PartEntity;

/**
 * Body part (tendril, head) of a parasite. 1.12: an unspawned entity that the client could hit through the EntityBodyHit packet;
 * 1.21: a {@link PartEntity} of the parent, so vanilla and NeoForge route the attack to the server by themselves.
 */
public class EntityBody
extends PartEntity<EntityParasiteBase> {
    protected EntityParasiteBase parent;
    protected float damageMultiplier;
    private EntityDimensions size;
    private float eye;
    private float offx;
    private float offy;
    private float offz;
    private int inverted;
    private int partId;
    private boolean logicSide;

    public EntityBody(EntityParasiteBase parent, float width, float height, float damageMultiplier, float offset, float offheight, int inv, int id, boolean side) {
        super(parent);
        this.parent = parent;
        this.damageMultiplier = damageMultiplier;
        this.eye = height * 0.8f;
        this.size = EntityDimensions.scalable(width, height).withEyeHeight(this.eye);
        this.refreshDimensions();
        this.offx = offset;
        this.offy = offheight;
        this.offz = offset;
        this.inverted = inv;
        this.partId = id;
        this.logicSide = side;
        parent.registerPart(this);
    }

    public EntityBody(EntityParasiteBase parent, float width, float height, float damageMultiplier, float offset, float offheight, int inv, int id, boolean side, float eyes) {
        this(parent, width, height, damageMultiplier, offset, offheight, inv, id, side);
        this.eye = eyes;
        this.size = this.size.withEyeHeight(eyes);
        this.refreshDimensions();
    }

    /** Called by the parent every tick; parts are not ticked by the level. */
    @Override
    public void tick() {
        if (this.parent == null || this.parent.isRemoved()) {
            this.discard();
            return;
        }
        if (this.logicSide) {
            this.updatePositionWithParentSides();
        } else {
            this.updatePositionWithParentFront();
        }
    }

    private void updatePositionWithParentSides() {
        float f17 = this.parent.getYRot() * ((float)Math.PI / 180);
        float f3 = Mth.sin((float)f17);
        float f18 = Mth.cos((float)f17);
        this.setYRot(this.parent.getYRot());
        this.setPos(this.parent.getX() + (double)this.inverted * (double)(f18 * this.offx), this.parent.getY() + (double)this.offy, this.parent.getZ() + (double)this.inverted * (double)(f3 * this.offz));
    }

    private void updatePositionWithParentFront() {
        float f19 = Mth.sin((float)(this.parent.getYRot() * ((float)Math.PI / 180) - this.parent.rotA * 0.01f));
        float f14 = 0.17453292f;
        float f16 = Mth.cos((float)f14);
        float f4 = Mth.cos((float)(this.parent.getYRot() * ((float)Math.PI / 180) - this.parent.rotA * 0.01f));
        this.setYRot(this.parent.getYRot());
        this.setPos(this.parent.getX() + (double)this.inverted * (double)(f19 * this.offx * f16), this.parent.getY() + (double)this.offy, this.parent.getZ() - (double)this.inverted * (double)(f4 * this.offx * f16));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isRemoved() || this.level().isClientSide) {
            return false;
        }
        if (this.parent instanceof EntityBodyParts rTarget) {
            return rTarget.attackEntityBodyFrom(source, amount, this.partId, false);
        }
        return this.parent.hurt(source, amount);
    }

    public void collideWithNearbyEntities() {
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(1.0, 1.0, 1.0));
        double d0 = (this.getBoundingBox().minX + this.getBoundingBox().maxX) / 2.0;
        double d1 = (this.getBoundingBox().minZ + this.getBoundingBox().maxZ) / 2.0;
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity) || entity == this.parent || entity instanceof EntityParasiteBase) continue;
            double d2 = entity.getX() - d0;
            double d3 = entity.getZ() - d1;
            double d4 = d2 * d2 + d3 * d3;
            entity.push(d2 / d4, (double)0.2f, d3 / d4);
            entity.hurt(this.parent.damageSources().mobAttack(this.parent), 5.0f);
        }
    }

    /** The id of the part within its parent (1.12 overrode getEntityId for this; here it is separate from the entity id). */
    public int getPartId() {
        return this.partId;
    }

    public EntityParasiteBase getFather() {
        return this.parent;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isRemoved();
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return this.parent.getPickResult();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    public void setBodySize(float w, float h) {
        this.size = EntityDimensions.scalable(w, h).withEyeHeight(this.eye);
        this.refreshDimensions();
    }

    @Override
    public boolean is(Entity entityIn) {
        return this == entityIn || this.parent == entityIn;
    }
}
