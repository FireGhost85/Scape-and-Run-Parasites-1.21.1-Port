package com.dhanantry.scapeandrunparasites.mixin;

import com.dhanantry.scapeandrunparasites.world.star.StarBiomeMapper;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Star worlds (cold / warm): replaces the biome the overworld noise source returns (the GenLayer of 1.12). */
@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSourceMixin {
    @Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at = @At("RETURN"), cancellable = true)
    private void srp$starBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        Holder<Biome> mapped = StarBiomeMapper.map(cir.getReturnValue());
        if (mapped != cir.getReturnValue()) {
            cir.setReturnValue(mapped);
        }
    }
}
