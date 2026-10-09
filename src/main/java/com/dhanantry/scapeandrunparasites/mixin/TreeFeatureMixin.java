package com.dhanantry.scapeandrunparasites.mixin;

import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.dhanantry.scapeandrunparasites.world.star.SRPColdStarTreeHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Cold star world: the trees of the world generation are replaced by deadhead trees (DecorateBiomeEvent TREE of 1.12). Only the
 * world generation is touched, saplings that grow keep the vanilla trees.
 */
@Mixin(TreeFeature.class)
public abstract class TreeFeatureMixin {
    @Inject(method = "place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z", at = @At("HEAD"), cancellable = true)
    private void srp$coldStarTree(FeaturePlaceContext<TreeConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        if (SRPWorldEntitySpawner.starType != 1 || !(context.level() instanceof WorldGenRegion region)) {
            return;
        }
        ServerLevel level = region.getLevel();
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }
        SRPColdStarTreeHandler.onVanillaTree(level, context.origin(), context.random());
        cir.setReturnValue(false);
    }
}
