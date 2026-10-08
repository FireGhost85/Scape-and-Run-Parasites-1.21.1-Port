package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

public class EntityBodyModel
extends EntityBody {
    private static final EntityDataAccessor<Byte> SKIN = SynchedEntityData.defineId(EntityBodyModel.class, EntityDataSerializers.BYTE);
    public float prevRenderYawOffset;
    public float renderYawOffset;
    public float prevRotationYawHead;
    public float rotationYawHead;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    public int hurtTime;
    public int deathTime;

    public EntityBodyModel(EntityParasiteBase parent, float width, float height, float damageMultiplier, float offset, float offheight, int inv, int id, boolean side) {
        super(parent, width, height, damageMultiplier, offset, offheight, inv, id, side);
    }

    public EntityBodyModel(EntityParasiteBase parent, float width, float height, float damageMultiplier, float offset, float offheight, int inv, int id, boolean side, float eyes) {
        super(parent, width, height, damageMultiplier, offset, offheight, inv, id, side, eyes);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SKIN, (byte) (0));
    }

    @Override
    public void tick() {
        super.tick();
        this.prevRenderYawOffset = this.parent.yBodyRotO;
        this.renderYawOffset = this.parent.yBodyRot;
        this.prevRotationYawHead = this.parent.yHeadRotO;
        this.rotationYawHead = this.parent.yHeadRot;
        this.prevLimbSwingAmount = this.parent.walkAnimation.speed(0.0f);
        this.limbSwingAmount = this.parent.walkAnimation.speed();
        this.limbSwing = this.parent.walkAnimation.position();
        this.hurtTime = this.parent.hurtTime;
        this.deathTime = this.parent.deathTime;
        this.tickCount = this.parent.tickCount;
    }

    public byte getSkin() {
        return (Byte)this.entityData.get(SKIN);
    }

    public void setSkin(int texture) {
        this.entityData.set(SKIN, (byte) (((byte)texture)));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("entityboduskin", (int)this.getSkin());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("entityboduskin", 99)) {
            this.setSkin(compound.getInt("beckonlifeleft"));
        }
    }
}

