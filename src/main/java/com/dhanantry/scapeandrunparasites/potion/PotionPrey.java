package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityParasiticScent;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

public class PotionPrey extends SRPEffectBase {
    public PotionPrey(String name, boolean isBadEffectIn, int liquidColorIn) {
        super(name, isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return true;
        }
        if (entity instanceof Player pa && (pa.isSpectator() || pa.isCreative())) {
            return true;
        }
        if (entity.tickCount % 80 == 0) {
            int count = level.getEntities(EntityTypeTest.forClass(EntityParasiticScent.class), scent -> true).size();
            if (count > SRPConfigSystems.scentCap) {
                return true;
            }
            AABB aabb = new AABB(entity.getX(), entity.getY(), entity.getZ(), entity.getX() + 1.0, entity.getY() + 1.0, entity.getZ() + 1.0).inflate(64.0);
            List<EntityParasiticScent> moblist = level.getEntitiesOfClass(EntityParasiticScent.class, aabb);
            for (EntityParasiticScent mob : moblist) {
                if (mob.getTargetToKill() != entity || !mob.getCanFollow()) continue;
                return true;
            }
            byte phase = SRPSaveData.get(level).getEvolutionPhase(DimKeys.of(level));
            EntityParasiticScent nut = new EntityParasiticScent(level, 1, entity);
            nut.copyPosition(entity);
            nut.setScentLife(SRPConfigSystems.scentLifeObserver * 20);
            nut.increaseDanger(ParasiteEventEntity.getScentBonus(phase), true);
            nut.setScentReaction(ParasiteEventEntity.getScentReactionBonus(phase), false);
            nut.setCanFollow(true);
            level.addFreshEntity(nut);
        }
        return true;
    }
}
