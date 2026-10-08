package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class PotionContamination extends SRPEffectBase {
    public PotionContamination(String name, boolean isBadEffectIn, int liquidColorIn) {
        super(name, isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            return true;
        }
        if (entity.tickCount % 40 == 0) {
            if (entity.getHealth() > 1.0f) {
                entity.hurt(entity.level().damageSources().magic(), 1.0f);
            }
            int dur = entity.getEffect(SRPPotions.CONTA_E).getDuration();
            AABB aabb = new AABB(entity.getX(), entity.getY(), entity.getZ(), entity.getX() + 1.0, entity.getY() + 1.0, entity.getZ() + 1.0).inflate(4.0, 3.0, 4.0);
            List<LivingEntity> moblist = entity.level().getEntitiesOfClass(LivingEntity.class, aabb);
            for (LivingEntity mob : moblist) {
                SRPPotions.applyStackPotion(SRPPotions.CONTA_E, mob, dur, amplifier);
            }
        }
        return true;
    }
}
