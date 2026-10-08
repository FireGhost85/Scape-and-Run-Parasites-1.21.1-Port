package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PotionCorrosion extends SRPEffectBase {
    public PotionCorrosion(String name, boolean isBadEffectIn, int liquidColorIn) {
        super(name, isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            return true;
        }
        this.effectCorrosive(entity, amplifier);
        return true;
    }

    /** 1.12 getEquipmentAndArmor(): hands and armor; damageItem became hurtAndBreak, which needs the slot. */
    private void effectCorrosive(LivingEntity entity, int amplifier) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot == EquipmentSlot.BODY) continue;
            ItemStack part = entity.getItemBySlot(slot);
            if (part.isEmpty() || !part.isDamageableItem()) continue;
            if (!((double) part.getMaxDamage() * SRPConfigSystems.corrNot < (double) (part.getMaxDamage() - part.getDamageValue()))) continue;
            part.hurtAndBreak(SRPConfigSystems.corroValue, entity, slot);
        }
    }
}
