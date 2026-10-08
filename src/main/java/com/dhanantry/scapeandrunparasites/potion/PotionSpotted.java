package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

public class PotionSpotted extends SRPEffectBase {
    public PotionSpotted(String name, boolean isBadEffectIn, int liquidColorIn) {
        super(name, isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            return true;
        }
        this.effectSpotted(entity, amplifier);
        return true;
    }

    private void effectSpotted(LivingEntity entity, int amplifier) {
        if (entity.tickCount % 200 == 0) {
            if (entity instanceof Player pa && (pa.isSpectator() || pa.isCreative())) {
                return;
            }
            ServerLevel level = (ServerLevel) entity.level();
            if (entity instanceof EntityParasiteBase || SRPWorldData.get(level).nearestInfectionValue(entity.blockPosition(), true) < SRPConfigWorld.originSpotted) {
                entity.removeEffect(SRPPotions.SPOT_E);
                return;
            }
            int test = 32;
            AABB aabb = new AABB(entity.blockPosition()).inflate((double) test, 16.0, (double) test);
            List<EntityParasiteBase> moblist = level.getEntitiesOfClass(EntityParasiteBase.class, aabb);
            EntityParasiteBase pin = moblist.isEmpty() ? null : moblist.get(0);
            if (pin == null) {
                return;
            }
            int parasiteCount = level.getEntities(EntityTypeTest.forClass(EntityParasiteBase.class), value -> true).size();
            if (parasiteCount >= SRPConfig.worldMobCap) {
                return;
            }
            String[] mobs = new String[]{""};
            int min = 2;
            int max = 4;
            if (entity instanceof EntityPInfected) {
                mobs = new String[]{"srparasites:sim_bigspider", "srparasites:sim_human", "srparasites:sim_cow", "srparasites:sim_sheep", "srparasites:sim_wolf", "srparasites:sim_pig", "srparasites:sim_villager", "srparasites:sim_horse", "srparasites:sim_bear", "srparasites:sim_enderman"};
                min = 2;
                max = 4;
            } else if (entity instanceof EntityPPrimitive) {
                mobs = new String[]{"srparasites:sim_bigspider", "srparasites:sim_human", "srparasites:sim_cow", "srparasites:sim_sheep", "srparasites:sim_wolf", "srparasites:sim_pig", "srparasites:sim_villager", "srparasites:sim_horse", "srparasites:sim_bear", "srparasites:sim_enderman", "srparasites:pri_longarms", "srparasites:pri_manducater", "srparasites:pri_reeker", "srparasites:pri_yelloweye", "srparasites:pri_summoner", "srparasites:pri_bolster", "srparasites:pri_arachnida", "srparasites:pri_vermin", "srparasites:pri_viscera"};
                min = 2;
                max = 3;
            } else if (entity instanceof EntityPAdapted) {
                mobs = new String[]{"srparasites:pri_longarms", "srparasites:pri_manducater", "srparasites:pri_reeker", "srparasites:pri_yelloweye", "srparasites:pri_summoner", "srparasites:pri_bolster", "srparasites:pri_arachnida", "srparasites:pri_vermin", "srparasites:pri_viscera", "srparasites:ada_longarms", "srparasites:ada_manducater", "srparasites:ada_reeker", "srparasites:ada_yelloweye", "srparasites:ada_summoner", "srparasites:ada_bolster", "srparasites:ada_arachnida", "srparasites:ada_vermin", "srparasites:ada_viscera"};
                min = 1;
                max = 3;
            } else if (entity instanceof EntityPPure) {
                mobs = new String[]{"srparasites:ada_longarms", "srparasites:ada_manducater", "srparasites:ada_reeker", "srparasites:ada_yelloweye", "srparasites:ada_summoner", "srparasites:ada_bolster", "srparasites:ada_arachnida", "srparasites:ada_vermin", "srparasites:ada_viscera", "srparasites:overseer", "srparasites:vigilante", "srparasites:warden", "srparasites:bomber_light", "srparasites:marauder", "srparasites:monarch", "srparasites:grunt"};
                min = 1;
                max = 2;
            }
            if (mobs[0].equals("")) {
                return;
            }
            ParasiteEventEntity.spawnUnitFromRof(level, entity, this.getRandomPosInCircle(level.random, entity.blockPosition(), 5.0), mobs, min, max);
        }
    }
}
