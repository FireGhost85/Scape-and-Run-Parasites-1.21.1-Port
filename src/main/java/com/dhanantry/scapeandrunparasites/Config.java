package com.dhanantry.scapeandrunparasites;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

/** Registers the four SRP config files (same split as 1.12.2: SRParasites / Mobs / Systems / World) and bakes them into static fields. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class Config {
    public static void register(ModContainer c) {
        c.registerConfig(ModConfig.Type.COMMON, SRPConfig.SPEC, "srparasites/SRParasites.toml");
        c.registerConfig(ModConfig.Type.COMMON, SRPConfigMobs.SPEC, "srparasites/SRParasitesMobs.toml");
        c.registerConfig(ModConfig.Type.COMMON, SRPConfigSystems.SPEC, "srparasites/SRParasitesSystems.toml");
        c.registerConfig(ModConfig.Type.COMMON, SRPConfigWorld.SPEC, "srparasites/SRParasitesWorld.toml");
    }

    private static void bake(IConfigSpec spec) {
        if (spec == SRPConfig.SPEC) SRPConfig.bake();
        else if (spec == SRPConfigMobs.SPEC) SRPConfigMobs.bake();
        else if (spec == SRPConfigSystems.SPEC) SRPConfigSystems.bake();
        else if (spec == SRPConfigWorld.SPEC) SRPConfigWorld.bake();
        else return;
        // the 1.12 CommonProxy.init() derived SRPAttributes from the configs after they were loaded
        SRPAttributes.init();
        com.dhanantry.scapeandrunparasites.init.SRPEntities.refreshAttributes();
        com.dhanantry.scapeandrunparasites.init.SRPBlocks.init();
        com.dhanantry.scapeandrunparasites.init.SRPSpawning.init();
        ScapeAndRunParasites.LOGGER.info("[SRP] config {} (re)loaded", spec == SRPConfig.SPEC ? "SRParasites" : spec == SRPConfigMobs.SPEC ? "SRParasitesMobs" : spec == SRPConfigSystems.SPEC ? "SRParasitesSystems" : "SRParasitesWorld");
    }

    /** {@code /srparasites readconfigurationfile}: bakes the four configs again and rebuilds what is derived from them. */
    public static void rebakeAll() {
        SRPConfig.bake();
        SRPConfigMobs.bake();
        SRPConfigSystems.bake();
        SRPConfigWorld.bake();
        SRPAttributes.reset();
        SRPAttributes.init();
        com.dhanantry.scapeandrunparasites.init.SRPEntities.refreshAttributes();
        com.dhanantry.scapeandrunparasites.init.SRPBlocks.init();
        com.dhanantry.scapeandrunparasites.init.SRPSpawning.init();
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent.Loading event) {
        bake(event.getConfig().getSpec());
    }

    @SubscribeEvent
    static void onReload(ModConfigEvent.Reloading event) {
        // the file watcher fires on its own thread: derived state (attributes, spawning) is rebuilt on the server thread
        IConfigSpec spec = event.getConfig().getSpec();
        net.minecraft.server.MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null && !server.isSameThread()) {
            server.execute(() -> bake(spec));
        } else {
            bake(spec);
        }
    }
}
