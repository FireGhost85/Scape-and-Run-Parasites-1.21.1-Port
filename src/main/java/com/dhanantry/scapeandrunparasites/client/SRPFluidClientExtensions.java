package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPFluids;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/** Textures of the dead blood fluid ({@code srparasites:blocks/deadblood_still} and {@code _flowing} in 1.12). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class SRPFluidClientExtensions {
    private static final ResourceLocation STILL = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "block/deadblood_still");
    private static final ResourceLocation FLOWING = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "block/deadblood_flowing");

    private SRPFluidClientExtensions() {
    }

    @SubscribeEvent
    static void register(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING;
            }
        }, SRPFluids.DEADBLOOD_TYPE.get());
    }
}
