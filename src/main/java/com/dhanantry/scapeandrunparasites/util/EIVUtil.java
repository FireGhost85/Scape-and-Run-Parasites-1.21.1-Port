package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EIVUtil {
    public static void createRandomOrigin(Level worldIn, int min, int max) {
        if (!SRPConfigWorld.originActivated) {
            return;
        }
        if (SRPWorldEntitySpawner.triggerSPAWNING) {
            return;
        }
        SRPWorldData data = SRPWorldData.get(worldIn);
        int number = data.getorigins("x").size();
        if (number == 0 ? worldIn.random.nextInt(SRPConfigWorld.originCreatingRandZero) != 0 : worldIn.random.nextInt(SRPConfigWorld.originCreatingRand) != 0) {
            return;
        }
        if (worldIn.players().isEmpty()) {
            return;
        }
        Player player = (Player)worldIn.players().get(worldIn.random.nextInt(worldIn.players().size()));
        double distance = max <= min ? (double)min : (double)min + worldIn.random.nextDouble() * (double)(max - min);
        double angle = worldIn.random.nextDouble() * Math.PI * 2.0;
        int dx = (int)Math.round(Math.cos(angle) * distance);
        int dz = (int)Math.round(Math.sin(angle) * distance);
        BlockPos pos = player.blockPosition().offset(dx, 64, dz);
        int key = data.setOrigin(worldIn, pos.getX(), pos.getY(), pos.getZ(), SRPConfigWorld.originHealth, SRPConfigWorld.originRadius);
        if (key == 1 && !SRPConfigWorld.originNewMess.isEmpty()) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigWorld.originNewMess, 400);
        }
        if (key == 2 && !SRPConfigWorld.originNewOutbreakMess.isEmpty()) {
            ParasiteEventEntity.alertAllPlayerSer(worldIn, SRPConfigWorld.originNewOutbreakMess, 401);
        }
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double trueDistance = Math.sqrt(player.distanceToSqr((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()));
        ScapeAndRunParasites.LOGGER.debug("[EIV DEBUG] RANDOM_ORIGIN result. Player={} playerPos={} originPos={} dx={} dz={} horizontalDistance={} trueDistance={} min={} max={} health={} radius={} resultKey={} totalOrigins={}", new Object[]{player.getName().getString(), player.blockPosition(), pos, dx, dz, String.format("%.2f", horizontalDistance), String.format("%.2f", trueDistance), min, max, SRPConfigWorld.originHealth, SRPConfigWorld.originRadius, key, data.getorigins("x").size()});
    }
}

