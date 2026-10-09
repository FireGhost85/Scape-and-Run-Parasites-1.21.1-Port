package com.dhanantry.scapeandrunparasites.mixin;

import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.dhanantry.scapeandrunparasites.world.star.SRPFracturedTerrainHandler;
import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldTweaks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fractured terrain of the cold star world: the terrain of a chunk is reshaped just before its features are placed (PopulateChunkEvent.Pre of 1.12). */
@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
    @Inject(method = "applyBiomeDecoration(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/StructureManager;)V", at = @At("HEAD"))
    private void srp$fracture(WorldGenLevel level, ChunkAccess chunk, StructureManager structures, CallbackInfo ci) {
        if (SRPWorldEntitySpawner.starType == 1 && SRPWorldEntitySpawner.fracturedTerrain && level.getLevel().dimension() == Level.OVERWORLD) {
            SRPFracturedTerrainHandler.apply(level, chunk);
        }
    }

    /** The star world tweaks (snowy grass of the cold star, no surface water of the warm star) on the chunk that was just decorated. */
    @Inject(method = "applyBiomeDecoration(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/StructureManager;)V", at = @At("RETURN"))
    private void srp$starTweaks(WorldGenLevel level, ChunkAccess chunk, StructureManager structures, CallbackInfo ci) {
        int starType = SRPWorldEntitySpawner.starType;
        if ((starType == 1 || starType == 2) && level.getLevel().dimension() == Level.OVERWORLD) {
            SRPStarWorldTweaks.apply(level, chunk, starType);
        }
    }
}
