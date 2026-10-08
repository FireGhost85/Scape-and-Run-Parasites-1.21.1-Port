package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.TileEntityDermoidCyst;
import com.dhanantry.scapeandrunparasites.block.TileEntityFogNullifier;
import com.dhanantry.scapeandrunparasites.block.TileEntityInfestationPurifier;
import com.dhanantry.scapeandrunparasites.block.TileEntityParasiteBarrier;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityCanister;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityDod;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityTrophy;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityInfestedFurnace;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityInfuserFurnace;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityNodeRelay;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityParasiteLoot;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Block entity types. Registry names are the 1.12 tile entity ids ({@code GameRegistry.registerTileEntity}). The inventories
 * expose the item handler capability the way {@code TileEntityLockable} did in 1.12 (the unsided {@code InvWrapper}); the
 * infested furnace had none.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class SRPBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ScapeAndRunParasites.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityNodeRelay>> NODE_RELAY = BLOCK_ENTITIES.register("node_relay",
            () -> BlockEntityType.Builder.of(TileEntityNodeRelay::new, SRPBlocks.NODE_RELAY.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityTrophy>> TROPHY = BLOCK_ENTITIES.register("trophy_te",
            () -> BlockEntityType.Builder.of(TileEntityTrophy::new, SRPBlocks.TrophyVoidOrb.get(), SRPBlocks.TrophyBoomOrb.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityDermoidCyst>> DERMOID_CYST = BLOCK_ENTITIES.register("dermoid_cyst",
            () -> BlockEntityType.Builder.of(TileEntityDermoidCyst::new, SRPBlocks.DermoidCyst.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityInfestedFurnace>> INFESTED_FURNACE = BLOCK_ENTITIES.register("infested_furnace",
            () -> BlockEntityType.Builder.of(TileEntityInfestedFurnace::new, SRPBlocks.INFESTED_FURNACE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityCanister>> CANISTER = BLOCK_ENTITIES.register("tileentitycanister",
            () -> BlockEntityType.Builder.of(TileEntityCanister::new, SRPBlocks.ParasiteCanisterActive.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityDod>> DOD = BLOCK_ENTITIES.register("tileentitydod",
            () -> BlockEntityType.Builder.of(TileEntityDod::new, SRPBlocks.dodN.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityParasiteLoot>> PARASITE_LOOT = BLOCK_ENTITIES.register("parasite_loot",
            () -> BlockEntityType.Builder.of(TileEntityParasiteLoot::new, SRPBlocks.ParasiteLoot.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityRelayController>> RELAY_CONTROLLER = BLOCK_ENTITIES.register("relaycontroller",
            () -> BlockEntityType.Builder.of(TileEntityRelayController::new, SRPBlocks.RELAY_CONTROLLER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityInfestationPurifier>> INFESTATION_PURIFIER = BLOCK_ENTITIES.register("infestation_purifier_te",
            () -> BlockEntityType.Builder.of(TileEntityInfestationPurifier::new, SRPBlocks.InfestPurify.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityParasiteBarrier>> PARASITE_BARRIER = BLOCK_ENTITIES.register("parasite_barrier_te",
            () -> BlockEntityType.Builder.of(TileEntityParasiteBarrier::new, SRPBlocks.PARASITE_BARRIER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityFogNullifier>> FOG_NULLIFIER = BLOCK_ENTITIES.register("fog_nullifier_te",
            () -> BlockEntityType.Builder.of(TileEntityFogNullifier::new, SRPBlocks.FogNullifier.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityInfuserFurnace>> INFUSER_FURNACE = BLOCK_ENTITIES.register("infuser_furnace",
            () -> BlockEntityType.Builder.of(TileEntityInfuserFurnace::new, SRPBlocks.InfuserFurnace.get()).build(null));

    private SRPBlockEntities() {
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }

    @SubscribeEvent
    static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DERMOID_CYST.get(), (be, side) -> new InvWrapper(be));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CANISTER.get(), (be, side) -> new InvWrapper(be));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PARASITE_LOOT.get(), (be, side) -> new InvWrapper(be));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, INFUSER_FURNACE.get(), (be, side) -> new InvWrapper(be));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, RELAY_CONTROLLER.get(), (be, side) -> be.getHandler());
    }
}
