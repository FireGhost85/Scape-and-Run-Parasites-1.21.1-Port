package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class EntityRemain
extends Entity {
    private int plus;
    private int count;
    private int goal;
    private boolean active;
    private String parasite;
    private float health;
    private byte skin;

    public EntityRemain(EntityType<? extends EntityRemain> type, Level in) {
        super(type, in);
        this.active = false;
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.tickCount % 20 == 0) {
                if (!(this.level().getBlockState(this.blockPosition()).getBlock() instanceof BlockGore) || this.parasite == null) {
                    this.discard();
                }
                if (!this.getActive() && SRPConfigSystems.disloGiveBodies && SRPSaveData.get(this.level()).getCurrentCode(DimKeys.of(this.level()), 20) >= 1) {
                    this.setPlus(SRPConfigMobs.canraadaptedremainplus);
                    this.setHealth(SRPConfigMobs.canraadaptedremainhealth);
                }
            }
            if (this.active) {
                this.count += this.plus;
                if (this.count > this.goal) {
                    List serverList = SRPEntityUtil.allEntities(this.level());
                    int count = 0;
                    for (int x = 0; x < serverList.size(); ++x) {
                        if (serverList.get(x) instanceof EntityParasiteBase) {
                            ++count;
                        }
                        if (count <= SRPConfig.worldMobCap) continue;
                        this.count = 0;
                        return;
                    }
                    EntityParasiteBase out = (EntityParasiteBase)SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(this.parasite), (Level)this.level());
                    if (out == null) {
                        return;
                    }
                    if (!out.level().noCollision(out, out.getBoundingBox())) {
                        out.discard();
                        return;
                    }
                    out.copyPosition(this);
                    out.finalizeSpawn((ServerLevel) out.level(), this.level().getCurrentDifficultyAt(out.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                    out.setSkin(this.skin);
                    out.setHealth(out.getMaxHealth() * this.health);
                    this.level().playSound(null, this.blockPosition(), SRPSounds.RESURRECT.get(), SoundSource.HOSTILE, 1.0f, 1.0f);
                    this.level().addFreshEntity((Entity)out);
                    out.particleStatus((byte)7);
                    out.addEffect(new MobEffectInstance(SRPPotions.DEBAR_E, 400, 0, false, false));
                    this.level().setBlockAndUpdate(this.blockPosition(), Blocks.AIR.defaultBlockState());
                    this.discard();
                }
                if (this.count % 10 == 0) {
                    this.level().broadcastEntityEvent((Entity)this, (byte)18);
                }
            }
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return this.getBbHeight();
    }

    /** canBeCollidedWith of 1.12 (the target of clicks and rays); in 1.21 that is isPickable, while canBeCollidedWith makes the entity solid to stand on. */
    @Override
    public boolean isPickable() {
        return true;
    }

    public void setParasite(String in) {
        this.parasite = in;
    }

    public void setGoal(int g) {
        this.goal = g;
    }

    public void setPlus(int p) {
        if (this.plus >= p) {
            return;
        }
        this.plus = p;
        this.active = true;
    }

    public void setSkin(byte in) {
        this.skin = in;
    }

    public void setHealth(float in) {
        if (this.health >= in) {
            return;
        }
        this.health = in;
    }

    public boolean getActive() {
        return this.active;
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("parasiteparasite", 8)) {
            this.parasite = compound.getString("parasiteparasite");
        }
        if (compound.contains("parasiteactive", 99)) {
            this.active = compound.getBoolean("parasiteactive");
        }
        if (compound.contains("parasitepoint", 99)) {
            this.count = compound.getInt("parasitepoint");
        }
        if (compound.contains("parasiteplus", 99)) {
            this.plus = compound.getInt("parasiteplus");
        }
        if (compound.contains("parasitegoal", 99)) {
            this.goal = compound.getInt("parasitegoal");
        }
        if (compound.contains("parasiteskin", 99)) {
            this.skin = compound.getByte("parasiteskin");
        }
        if (compound.contains("parasitehealth", 99)) {
            this.health = compound.getFloat("parasitehealth");
        }
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.parasite != null) {
            compound.putString("parasiteparasite", this.parasite);
        }
        compound.putBoolean("parasiteactive", this.active);
        compound.putInt("parasitepoint", this.count);
        compound.putInt("parasiteplus", this.plus);
        compound.putInt("parasitegoal", this.goal);
        compound.putByte("parasiteskin", this.skin);
        compound.putFloat("parasitehealth", this.health);
    }

    public void handleEntityEvent(byte id) {
        if (id == 18) {
            for (int i = 0; i <= 1; ++i) {
                this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b) {
        double d0 = this.getRandom().nextGaussian() * 0.02;
        double d1 = this.getRandom().nextGaussian() * 0.02;
        double d2 = this.getRandom().nextGaussian() * 0.02;
        ParticleSpawner.spawnParticle(particleType, this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY() + 0.5 + (double)this.getRandom().nextFloat() * ((double)this.getBbHeight() + 0.5), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), d0, d1, d2, r, g, b);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }
}

