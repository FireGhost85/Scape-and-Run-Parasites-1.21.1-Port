package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.celestial.CelestialNightData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

public class ShrimpDropHandler {
    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        if (event.getEntity().level() == null || event.getEntity().level().isClientSide) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (!this.isSRPMob(entity)) {
            return;
        }
        if (!this.isArrowCelestialActive(entity)) {
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

    private boolean isSRPMob(LivingEntity entity) {
        return entity instanceof EntityParasiteBase;
    }

    private boolean isArrowCelestialActive(LivingEntity entity) {
        if (entity == null || entity.level() == null || entity.level().isClientSide) {
            return false;
        }
        if (!entity.level().dimensionType().isSurfaceWorld()) {
            return false;
        }
        String dim = DimKeys.of(entity.level());
        CelestialNightData nightData = CelestialNightData.get(entity.level());
        if (nightData == null) {
            return false;
        }
        CelestialNightData.DimState state = nightData.getOrCreate(dim);
        return state.active.contains("arrow") || state.forced.contains("arrow");
    }
}

