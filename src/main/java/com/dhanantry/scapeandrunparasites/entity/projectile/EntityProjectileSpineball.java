package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.ArrayList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class EntityProjectileSpineball
extends EntitySRPProjectile {
    private float damage;
    private int duration;
    private int amp;
    private double item;

    public EntityProjectileSpineball(EntityType<? extends EntityProjectileSpineball> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityProjectileSpineball(EntityType<? extends EntityProjectileSpineball> type, Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ, float projDamage) {
        super(type, worldIn, shooter, accelX, accelY, accelZ);
        this.damage = projDamage;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.ITEM_SLIME;
    }

    public void setDurationAmplifier(int duration, int amplifier) {
        this.duration = duration * 20;
        this.amp = amplifier - 1;
    }

    public void setGearDamage(double in) {
        this.item = in;
    }

    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            if (SRPEntityUtil.hitEntity(result) != null && SRPEntityUtil.hitEntity(result) instanceof LivingEntity) {
                LivingEntity target = (LivingEntity)SRPEntityUtil.hitEntity(result);
                if (target instanceof EntityParasiteBase && !(target instanceof EntityNak)) {
                    this.discard();
                    return;
                }
                boolean primitive = false;
                EntityParasiteBase shooter = (EntityParasiteBase)this.getOwner();
                if (shooter instanceof EntityEmana) {
                    primitive = true;
                }
                DamageSource damagesource = this.getOwner() == null ? this.damageSources().thrown((Entity)this, (Entity)this) : this.damageSources().thrown((Entity)this, (Entity)this.getOwner());
                target.hurt(damagesource, this.damage);
                target.addEffect(new MobEffectInstance(MobEffects.POISON, this.duration, this.amp));
                this.attackEntityAsMobMinimum((Entity)target, (EntityParasiteBase)this.getOwner());
                this.damageArmor(target, this.item);
                if (target.getHealth() <= 0.0f && primitive && shooter.isAlive()) {
                    double k = shooter.getKillC();
                    shooter.setKillC(k += 1.0);
                    shooter.particleStatus((byte)5);
                    if (k > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
                        ParasiteEventEntity.spawnNext(shooter, new EntityEmanaAdapted(SRPEntities.ADA_YELLOWEYE.get(), this.level()), true, true);
                    }
                }
            }
            this.discard();
        }
    }

    private void damageArmor(LivingEntity target, double percen) {
        ArrayList<ItemStack> off = new ArrayList<ItemStack>();
        ArrayList<EquipmentSlot> slots = new ArrayList<EquipmentSlot>();
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
            ItemStack part = target.getItemBySlot(slot);
            if (part.isEmpty() || !part.isDamageableItem()) continue;
            off.add(part);
            slots.add(slot);
        }
        for (int i = 0; i < off.size(); ++i) {
            ItemStack part = off.get(i);
            if (!((double)part.getMaxDamage() * SRPConfigSystems.corrNot < (double)(part.getMaxDamage() - part.getDamageValue()))) continue;
            part.hurtAndBreak((int)((double)part.getMaxDamage() * percen), target, slots.get(i));
        }
    }
}

