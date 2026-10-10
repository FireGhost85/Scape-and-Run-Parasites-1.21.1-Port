package com.dhanantry.scapeandrunparasites.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.entity.PartEntity;

/**
 * Extra hit box of a parasite (1.12: EntityDragonPart). NeoForge routes attacks and interactions on a {@link PartEntity} to the
 * server by itself, so the HitboxHit packet of the original is not needed.
 */
public class EntityHitbox
extends PartEntity<Mob> {
    private float radius;
    private float angle;
    private float yPos;
    private float dmgVuln;
    private EntityDimensions size;

    public EntityHitbox(Mob parent, float angle, float radius, float yOffset, float width, float height, float damageVulnerability) {
        super(parent);
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.yPos = yOffset;
        this.radius = radius;
        this.angle = angle;
        this.dmgVuln = damageVulnerability;
        this.noCulling = true;
        if (parent instanceof com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase base) {
            base.registerPart(this);
        }
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getAngle() {
        return this.angle;
    }

    public float getRadius() {
        return this.radius;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    @Override
    public boolean isPickable() {
        return this.getParent().isAlive();
    }


    @Nullable
    @Override
    public ItemStack getPickResult() {
        return this.getParent().getPickResult();
    }

    @Override
    public boolean is(Entity other) {
        return this == other || this.getParent() == other;
    }

    public void resize(float scale) {
        this.size = EntityDimensions.scalable(this.size.width() * scale, this.size.height() * scale);
        this.refreshDimensions();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        return this.getParent().hurt(source, amount * this.dmgVuln);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        return this.getParent().interact(player, hand);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return this.isInvulnerable() && !source.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    private boolean positioned;

    /** Called by the parent every tick; parts are not ticked by the level. */
    @Override
    public void tick() {
        Mob parent = this.getParent();
        double yaw = (double)parent.yBodyRot * (Math.PI / 180) + (double)this.angle;
        double nx = parent.getX() + (double)this.radius * Math.cos(yaw);
        double ny = parent.getY() + (double)this.yPos;
        double nz = parent.getZ() + (double)this.radius * Math.sin(yaw);
        // the previous position drives the interpolation of the hitbox rendering (F3+B); without it the box slid in from the origin every frame
        if (this.positioned) {
            this.setOldPosAndRot();
            this.setPos(nx, ny, nz);
        } else {
            this.setPos(nx, ny, nz);
            this.setOldPosAndRot();
            this.positioned = true;
        }
        if (!this.level().isClientSide && this.size.width() >= parent.getBbWidth()) {
            this.collideWithNearbyEntities();
        }
        if (!parent.isAlive() || this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    public void collideWithNearbyEntities() {
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards((double)0.2f, 0.0, (double)0.2f));
        entities.stream().filter(entity -> entity != this.getParent() && !(entity instanceof EntityHitbox) && entity.isPushable()).forEach(entity -> entity.push(this.getParent()));
    }
}
