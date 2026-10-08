package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntitySource
extends Entity {
    private final ServerBossEvent bossInfo = (ServerBossEvent)new ServerBossEvent(Component.literal("The Source"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false);
    private byte type;
    private float total;
    private float charging;

    public EntitySource(EntityType<? extends EntitySource> type, Level parent) {
        super(type, parent);
        this.total = 100.0f;
    }

    public void tick() {
        super.tick();
        this.bossInfo.setProgress(this.charging / this.total);
        if (this.tickCount % 20 == 0 && !this.level().isClientSide) {
            this.charging += 1.0f;
            if (this.charging > this.total) {
                this.attack();
            }
        }
    }

    private void attack() {
        if (this.charging > 200.0f) {
            this.discard();
        }
    }

    public void setCustomNameTag(String name) {
        SRPEntityUtil.setCustomNameTag(this, name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public Component getDisplayName() {
        return Component.literal("The Source");
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    public boolean isEntityEqual(Entity entityIn) {
        return this == entityIn;
    }

    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
    }
}

