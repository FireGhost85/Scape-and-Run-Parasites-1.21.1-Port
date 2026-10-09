package com.dhanantry.scapeandrunparasites;

import com.dhanantry.scapeandrunparasites.init.SRPArmorMaterials;
import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPFluids;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPMenus;
import com.dhanantry.scapeandrunparasites.init.SRPParticles;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/** Scape and Run: Parasites - port of SRP 1.10.9 (1.12.2) to NeoForge 1.21.1. */
@Mod(ScapeAndRunParasites.MODID)
public class ScapeAndRunParasites {
    public static final String MODID = "srparasites";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ScapeAndRunParasites(IEventBus modEventBus, ModContainer modContainer) {
        Config.register(modContainer);
        if (net.neoforged.fml.loading.FMLEnvironment.dist == net.neoforged.api.distmarker.Dist.CLIENT) {
            com.dhanantry.scapeandrunparasites.client.ClientModInit.registerConfigScreen(modContainer);
        }
        SRPSounds.SOUNDS.register(modEventBus);
        modEventBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent e) -> e.enqueueWork(com.dhanantry.scapeandrunparasites.bestiary.SRPBestiaryRegistry::registerDefaults));
        com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability.ATTACHMENTS.register(modEventBus);
        SRPPotions.register(modEventBus);
        SRPParticles.register(modEventBus);
        SRPFluids.register(modEventBus);
        SRPBlocks.register(modEventBus);
        SRPBlockEntities.register(modEventBus);
        SRPMenus.register(modEventBus);
        SRPArmorMaterials.register(modEventBus);
        SRPItems.register(modEventBus);
        SRPEntities.ENTITIES.register(modEventBus);
        com.dhanantry.scapeandrunparasites.init.SRPSpawnEggs.register(modEventBus);
        com.dhanantry.scapeandrunparasites.util.SRPDebugRules.init();
        com.dhanantry.scapeandrunparasites.util.SRPCreativeTabs.register(modEventBus);
        com.dhanantry.scapeandrunparasites.world.spawner.SRPSpawnBiomeModifier.SERIALIZERS.register(modEventBus);
        modEventBus.addListener(SRPEntities::registerAttributes);
    }
}
