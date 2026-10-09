package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

/**
 * Parasites drop 1 to 5 shrimp (25 percent) while the "arrow" celestial event is active on the surface dimension.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class ShrimpDropHandler {
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        if (!(entity instanceof EntityParasiteBase)) {
            return;
        }
        if (!isArrowCelestialActive(entity)) {
            return;
        }
        RandomSource rand = entity.getRandom();
        if (rand.nextFloat() >= 0.25f) {
            return;
        }
        int count = 1 + rand.nextInt(5);
        ItemStack stack = new ItemStack(SRPItems.shrimp.get(), count);
        BlockPos pos = entity.blockPosition();
        ItemEntity drop = new ItemEntity(entity.level(), (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, stack);
        event.getDrops().add(drop);
    }

    private static boolean isArrowCelestialActive(LivingEntity entity) {
        net.minecraft.world.level.Level level = entity.level();
        if (level == null || level.isClientSide || !com.dhanantry.scapeandrunparasites.world.celestial.CelestialEventManager.isSurface(level)) {
            return false;
        }
        com.dhanantry.scapeandrunparasites.world.celestial.CelestialNightData.DimState state = com.dhanantry.scapeandrunparasites.world.celestial.CelestialNightData.get(level).getOrCreate(com.dhanantry.scapeandrunparasites.phase.DimKeys.of(level));
        return state.active.contains("arrow") || state.forced.contains("arrow");
    }
}
