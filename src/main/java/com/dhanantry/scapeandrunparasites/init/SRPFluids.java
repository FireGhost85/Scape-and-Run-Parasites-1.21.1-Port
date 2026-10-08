package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Dead blood. The 1.12 {@code DeadBloodFluid}: density 3000, viscosity max(1500, 500), temperature 310, luminosity 0, not gaseous.
 * The Forge {@code BlockFluidClassic} derived the tick rate from the viscosity (1500 / 200) and could not create sources.
 * The original never enabled the universal bucket, so there is no bucket item.
 */
public final class SRPFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ScapeAndRunParasites.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, ScapeAndRunParasites.MODID);

    public static final DeferredHolder<FluidType, FluidType> DEADBLOOD_TYPE = FLUID_TYPES.register("deadblood", () -> new FluidType(
            FluidType.Properties.create().descriptionId("block.srparasites.deadblood").density(3000).viscosity(1500).temperature(310).lightLevel(0)
                    .canPushEntity(true).canSwim(true).canDrown(true).canExtinguish(true).canConvertToSource(false).supportsBoating(false)
                    .fallDistanceModifier(0.0f)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> DEADBLOOD_FLUID = FLUIDS.register("deadblood",
            () -> new BaseFlowingFluid.Source(properties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> DEADBLOOD_FLOWING = FLUIDS.register("deadblood_flowing",
            () -> new BaseFlowingFluid.Flowing(properties()));

    private SRPFluids() {
    }

    private static BaseFlowingFluid.Properties properties() {
        return new BaseFlowingFluid.Properties(DEADBLOOD_TYPE, DEADBLOOD_FLUID, DEADBLOOD_FLOWING)
                .block(SRPBlocks.DeadBlood).tickRate(7).slopeFindDistance(4).levelDecreasePerBlock(1).explosionResistance(100.0f);
    }

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
    }
}
